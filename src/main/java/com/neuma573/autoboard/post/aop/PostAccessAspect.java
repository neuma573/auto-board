package com.neuma573.autoboard.post.aop;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@RequiredArgsConstructor
@Component
public class PostAccessAspect {

    private final PostService postService;

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
                postId.set(jwtProvider.extractLongFromParamMap(paramMap, "postId")
                        .orElse(-1L));
                boardId.set(jwtProvider.extractLongFromParamMap(paramMap, "boardId")
                        .orElse(postService.findBoardIdByPostId(postId.get())));
            }
            default -> Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> arg instanceof PostRequest)
                    .findFirst()
                    .ifPresent(arg -> {
                        PostRequest postRequest = (PostRequest) arg;
                        boardId.set(postRequest.getBoardId());
                        if (postRequest instanceof PostModifyRequest) {
                            postId.set(((PostModifyRequest) postRequest).getPostId());
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
