package com.neuma573.autoboard.post.service;

import com.neuma573.autoboard.ai.model.dto.OpenAiResponse;
import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.file.model.dto.UploadFileRequest;
import com.neuma573.autoboard.file.service.FileService;
import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
import com.neuma573.autoboard.global.exception.UserBlockedException;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.global.service.OptionService;
import com.neuma573.autoboard.global.utils.ContentSanitizer;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostPermissionResponse;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.model.enums.PostAction;
import com.neuma573.autoboard.post.repository.PostRepository;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final UserService userService;

    private final BoardService boardService;

    private final OptionService optionService;

    private final FileService fileService;

    private final RedisTemplate<String, List<UploadFileRequest>> tempFileRedisTemplate;

    private final static String VIEW_COUNT_KEY_PREFIX = "view:count:";
    private final static long VIEW_COUNT_EXPIRATION = 24 * 60 * 60; // 24시간

    @Transactional
    public Page<PostResponse> getPostList(Long boardId, Pageable pageable, Long userId) {
        User user = userService.getUserByIdSafely(userId);

        Page<Post> posts = (user != null && userService.isAdmin(user))
                ? postRepository.findAllByBoardId(boardId, pageable)
                : postRepository.findAllByBoardIdAndIsDeletedFalse(boardId, pageable);

        return posts.map(PostResponse::of);
    }

    @Transactional
    public PostResponse generatePost(Long userId, PostRequest postRequest) {
        Board destination = boardService.getBoardById(postRequest.getBoardId());
        User writer = userService.getUserById(userId);
        Post post = savePost(
                postRequest,
                destination,
                writer
        );
        handlingTempFileToEntity(postRequest, post);
        return PostResponse.of(post);
    }

    public Post savePost(PostRequest postRequest, Board destination, User writer) {
        return postRepository.save(postRequest.toEntity(
                destination,
                writer)
        );
    }

    public void saveAiPost(OpenAiResponse openAiResponse) {
        String userId = optionService.findByKey("aiUserId");
        String scheduledBoardId = optionService.findByKey("aiBoardId");

        Board destination = boardService.getBoardById(Long.valueOf(scheduledBoardId));
        User aiUser = userService.getUserById(Long.valueOf(userId));

        postRepository.save(Post.builder()
                .board(destination)
                .title(openAiResponse.getTitle())
                .content(openAiResponse.getContent())
                .createdBy(aiUser)
                .isDeleted(false)
                .views(0L)
                .build()
        );
    }

    @Transactional
    public boolean isCreatable(Long userId, Long boardId) {
        User user = userService.getUserByIdSafely(userId);
        if (user != null && user.getStatus().equals(Status.BANNED.getStatus())) {
            throw new UserBlockedException(userId);
        }
        return userService.isAdmin(
                userService.getUserById(userId)
        ) || boardService.getBoardById(boardId).isPublic()
                || boardService.isContainedUser(
                        userService.getUserById(userId),
                        boardId
        );
    }

    @Transactional
    public boolean isPostAccessible(Long userId, Long postId, PostAction action) {
        Post post = getPostById(postId);
        User user = userService.getUserByIdSafely(userId);
        if (user != null && user.getStatus().equals(Status.BANNED.getStatus())) {
            throw new UserBlockedException(userId);
        }

        if (post.isDeleted()) {
            return action == PostAction.READ && (user != null && userService.isAdmin(user));
        }

        return switch (action) {
            case READ -> true;
            case UPDATE -> isCreatedBy(userId, post);
            case DELETE -> (user != null && userService.isAdmin(user)) || isCreatedBy(userId, post);
            default -> false;
        };
    }

    @Transactional
    public PostResponse getPost(String ipAddress, Long postId) {
        Post post = getPostById(postId);
        increaseViewCount(ipAddress, postId);
        return filterXSSCodes(
                PostResponse.of(post)
        );
    }

    private PostResponse filterXSSCodes(PostResponse postResponse) {
        postResponse.setContent(ContentSanitizer.filterHtmlSource(postResponse.getContent()));
        return postResponse;
    }

    @Async
    public void increaseViewCount(String ipAddress, Long postId) {
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
        User user = userService.getUserById(userId);
        Post post = getPostById(postModifyRequest.getPostId());
        modify(post, postModifyRequest);
        handlingTempFileToEntity(postModifyRequest, post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        User user = userService.getUserById(userId);
        Post post = getPostById(postId);
        delete(post);
    }

    public boolean isAbleToModify(Post post, User user) {
        return post.getCreatedBy().getId().equals(user.getId());
    }

    public boolean isAbleToDelete(Post post, User user) {
        return userService.isAdmin(user) || isAbleToModify(post, user);
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

    @Transactional
    public Long findBoardIdByPostId(Long postId) {
        return postRepository.findById(postId)
                .map(Post::getBoard)
                .map(Board::getId)
                .orElse(null);
    }

    @Transactional
    public boolean isCreatedBy(Long userId, Post post) {
        return Objects.equals(userId, post.getCreatedBy().getId());
    }

    private void handlingTempFileToEntity(PostRequest postRequest, Post post) {
        List<UploadFileRequest> fileList = tempFileRedisTemplate.opsForValue().get(postRequest.getTempId());

        if (fileList != null) {
            fileList.forEach(file -> {
                try {
                    fileService.saveFile(file, post);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장에 실패했습니다.", e);
                }
            });
        }
        tempFileRedisTemplate.delete(postRequest.getTempId());
    }
}
