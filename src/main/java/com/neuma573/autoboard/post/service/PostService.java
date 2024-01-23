package com.neuma573.autoboard.post.service;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
import com.neuma573.autoboard.global.exception.BoardNotFoundException;
import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.repository.PostRepository;
import com.neuma573.autoboard.security.service.AuthService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
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

    private final static String VIEW_COUNT_KEY_PREFIX = "view:count:";
    private final static long VIEW_COUNT_EXPIRATION = 24 * 60 * 60; // 24시간

    public List<PostResponse> getPostList(Long boardId, Pageable pageable, Long userId) {
        Page<Post> posts = postRepository.findAllByBoardId(boardId, pageable);

        List<PostResponse> postResponseList = posts.map(this::convertToPostResponse).getContent();

        User user = userRepository.findById(userId).orElse(null);
        if(user == null || !user.isAdmin()){
            postResponseList = subtractDeleted(postResponseList);
        }

        return postResponseList;
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

    private PostResponse convertToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isDeleted(post.isDeleted())
                .userResponse(post.getCreatedBy().toResponse())
                .views(post.getViews())
                .createdAt(post.getFormattedCreatedAt())
                .build();
    }

    private List<PostResponse> subtractDeleted(List<PostResponse> postResponseList) {
        return postResponseList.stream()
                .filter(postResponse -> !postResponse.isDeleted())
                .collect(Collectors.toList());
    }

    public void checkAccessibleAndThrow(Long postId, Long userId) {

        if (userId != -1L && !checkAccessible(postId, userId)) {
            throw new PostNotAccessibleException("접근할 수 없는 게시글입니다.");
        }

        if (!checkAccessible(postId, userId)) {
            throw new PostNotAccessibleException("접근할 수 없는 게시글입니다.");
        }
    }

    public boolean checkAccessible(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotAccessibleException("접근할 수 없는 게시글입니다."));

        if (userId == -1L) {
            return post.getBoard().isPublic() && !post.isDeleted();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

        return post.getBoard().isAccessible(user) && !post.isDeleted();
    }

    public PostResponse getPost(HttpServletRequest httpServletRequest, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotAccessibleException("접근할 수 없는 게시글입니다."));
        increaseViewCount(httpServletRequest, postId);
        return PostResponse.of(post);
    }

    @Async
    public void increaseViewCount(HttpServletRequest httpServletRequest, Long postId) {
        String ipAddress = authService.getClientIpAddress(httpServletRequest);
        String cacheKey = VIEW_COUNT_KEY_PREFIX + postId + ":" + ipAddress;
        Boolean alreadyViewed = stringRedisTemplate.opsForValue().setIfAbsent(cacheKey, "1", VIEW_COUNT_EXPIRATION, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(alreadyViewed)) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotAccessibleException("Post not found with id " + postId));
            post.addViews();
            postRepository.save(post);
        }
    }
}
