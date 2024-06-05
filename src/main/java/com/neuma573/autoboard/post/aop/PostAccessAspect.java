package com.neuma573.autoboard.post.aop;

import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.service.CommentService;
import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
import com.neuma573.autoboard.post.model.annotation.CheckPostAccess;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.enums.PostAction;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.spi.ToolProvider.findFirst;

@Aspect
@RequiredArgsConstructor
@Component
public class PostAccessAspect {

    private final PostService postService;

    private final CommentService commentService;

    private final JwtProvider jwtProvider;



    @Around("@annotation(checkPostAccess)")
    public Object checkUserPostAccess(ProceedingJoinPoint joinPoint, CheckPostAccess checkPostAccess) throws Throwable {
        PostAction action = checkPostAccess.action();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String httpMethod = httpServletRequest.getMethod();


        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);

        Map<String, String[]> paramMap = jwtProvider.getParameterMap(httpServletRequest);

        AtomicReference<Long> postId = new AtomicReference<>(-1L);

        AtomicReference<Long> boardId = new AtomicReference<>(-1L);


        switch (httpMethod) {
            case "GET", "DELETE" -> {
                Optional<Long> commentIdOptional = jwtProvider.extractLongFromParamMap(paramMap, "commentId");
                if (commentIdOptional.isPresent()) {
                    Long commentId = commentIdOptional.get();
                    Long associatedPostId = commentService.findPostIdByCommentId(commentId);
                    if (associatedPostId != null) {
                        postId.set(associatedPostId);
                    }
                }
                else {
                    postId.set(jwtProvider.extractLongFromParamMap(paramMap, "postId")
                            .orElse(-1L));
                    boardId.set(jwtProvider.extractLongFromParamMap(paramMap, "boardId")
                            .orElse(postService.findBoardIdByPostId(postId.get())));
                }
            }
            default -> Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> arg instanceof PostRequest || arg instanceof CommentRequest)
                    .findFirst()
                    .ifPresent(arg -> {
                        if (arg instanceof PostRequest postRequest) {
                            boardId.set(postRequest.getBoardId());
                            if (postRequest instanceof PostModifyRequest) {
                                postId.set(((PostModifyRequest) postRequest).getPostId());
                                boardId.set(postService.findBoardIdByPostId(postId.get()));
                            }
                        } else if (arg instanceof CommentRequest commentRequest) {
                            postId.set(commentRequest.getPostId());
                            boardId.set(postService.findBoardIdByPostId(postId.get()));
                        }
                    });
        }
        if (postId.get() == -1L) {
            Parameter[] parameters = joinPoint.getSignature().getDeclaringType().getDeclaredMethods()[0].getParameters();
            Arrays.stream(parameters)
                    .flatMap(parameter -> Arrays.stream(parameter.getAnnotations())
                            .filter(annotation -> annotation instanceof PathVariable)
                            .map(annotation -> parameter))
                    .forEach(parameter -> {
                        int index = Arrays.asList(parameters).indexOf(parameter);
                        if (joinPoint.getArgs()[index] instanceof Long) {
                            postId.set((Long) joinPoint.getArgs()[index]);
                            boardId.set(postService.findBoardIdByPostId(postId.get()));
                        }
                    });
        }

        switch (action) {
            case READ -> {
                if (!postService.isPostAccessible(userId, postId.get(), action)) {
                    throw new PostNotAccessibleException("게시글에 접근할 수 없습니다.");
                }
            }
            case DELETE -> {
                if (!postService.isPostAccessible(userId, postId.get(), action)) {
                    throw new PostNotAccessibleException("게시글을 삭제할 수 없습니다.");
                }
            }
            case CREATE -> {
                if (!postService.isCreatable(userId, boardId.get())) {
                    throw new PostNotAccessibleException("게시글을 작성할 수 없습니다.");
                }
            }
            case UPDATE -> {
                if (!postService.isPostAccessible(userId, postId.get(), action)) {
                    throw new PostNotAccessibleException("게시글을 수정할 수 없습니다.");
                }
            }
        }


        return joinPoint.proceed();
    }
}
