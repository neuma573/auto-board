package com.neuma573.autoboard.post.service;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
import com.neuma573.autoboard.global.exception.BoardNotFoundException;
import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostPermissionResponse;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.repository.PostRepository;
import com.neuma573.autoboard.security.service.AuthService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final AuthService authService;

    private final UserService userService;

    private final static String VIEW_COUNT_KEY_PREFIX = "view:count:";
    private final static long VIEW_COUNT_EXPIRATION = 24 * 60 * 60; // 24시간

    @Transactional
    public List<PostResponse> getPostList(Long boardId, Pageable pageable, Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        Page<Post> posts = (user != null && user.isAdmin())
                ? postRepository.findAllByBoardId(boardId, pageable)
                : postRepository.findAllByBoardIdAndIsDeletedFalse(boardId, pageable);

        return posts.map(PostResponse::of).getContent();
    }

    @Transactional
    public PostResponse savePost(Long id, PostRequest postRequest) {
        Board destination = boardRepository.findById(postRequest.getBoardId()).orElseThrow(() -> new BoardNotFoundException(""));
        User writer = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(""));
        if (!destination.isAccessible(writer)) {
            throw new BoardNotAccessibleException("이용할 수 없는 게시판입니다.");
        }
        Post post = postRepository.save(postRequest.of(
                destination,
                writer)
        );
        return PostResponse.of(post);
    }

    private List<PostResponse> subtractDeleted(List<PostResponse> postResponseList) {
        return postResponseList.stream()
                .filter(postResponse -> !postResponse.isDeleted())
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAccessibleAndThrow(Long postId, Long userId) {

        if (userId != -1L && !checkAccessible(postId, userId)) {
            throw new PostNotAccessibleException("접근할 수 없는 게시글입니다.");
        }

        if (!checkAccessible(postId, userId)) {
            throw new PostNotAccessibleException("접근할 수 없는 게시글입니다.");
        }
    }

    @Transactional
    public boolean checkAccessible(Long postId, Long userId) {
        Post post = getPostById(postId);

        if (userId == -1L) {
            return post.getBoard().isPublic() && !post.isDeleted();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

        return user.isAdmin() || (post.getBoard().isAccessible(user) && !post.isDeleted());
    }

    @Transactional
    public PostResponse getPost(HttpServletRequest httpServletRequest, Long postId) {
        Post post = getPostById(postId);
        increaseViewCount(httpServletRequest, postId);
        return PostResponse.of(post);
    }

    @Async
    public void increaseViewCount(HttpServletRequest httpServletRequest, Long postId) {
        String ipAddress = authService.getClientIpAddress(httpServletRequest);
        String cacheKey = VIEW_COUNT_KEY_PREFIX + postId + ":" + ipAddress;
        Boolean alreadyViewed = stringRedisTemplate.opsForValue().setIfAbsent(cacheKey, "1", VIEW_COUNT_EXPIRATION, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(alreadyViewed)) {
            Post post = getPostById(postId);
            post.addViews();
            postRepository.save(post);
        }
    }

    @Transactional
    public void modifyPost(PostModifyRequest postModifyRequest, Long userId) {
        Post post = getPostById(postModifyRequest.getPostId());
        if (isAbleToModify(post, userService.getUserById(userId))) {
            modify(post, postModifyRequest);
        } else {
            throw new PostNotAccessibleException("수정할 수 있는 권한이 없습니다");
        }
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        if (isAbleToDelete(post, userService.getUserById(userId))) {
            delete(post);
        } else {
            throw new PostNotAccessibleException("삭제할 수 있는 권한이 없습니다");
        }
    }

    public boolean isAbleToModify(Post post, User user) {
        log.info(post.getCreatedBy().getEmail());
        return post.getCreatedBy().getId().equals(user.getId());
    }

    public boolean isAbleToDelete(Post post, User user) {
        return user.isAdmin() || isAbleToModify(post, user);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostNotAccessibleException("접근할 수 없는 게시글입니다."));
    }

    public void modify(Post post, PostModifyRequest postModifyRequest) {
        post.setTitle(postModifyRequest.getTitle());
        post.setContent(postModifyRequest.getContent());
    }

    public void delete(Post post) {
        post.delete();
    }

    @Transactional
    public PostPermissionResponse getPermissionFrom(Long postId, Long userId) {
        if (userId == -1L) {
            return PostPermissionResponse.builder()
                    .isAbleToDelete(false)
                    .isAbleToModify(false)
                    .build();
        }
        User user = userService.getUserById(userId);
        Post post = getPostById(postId);

        return PostPermissionResponse.builder()
                .isAbleToDelete(!post.isDeleted() && isAbleToDelete(post, user))
                .isAbleToModify(isAbleToModify(post, user))
                .build();

    }

}
