package com.neuma573.autoboard.comment.aop;

import com.neuma573.autoboard.comment.model.annotation.CheckCommentAccess;
import com.neuma573.autoboard.comment.model.dto.CommentModifyRequest;
import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.enums.CommentAction;
import com.neuma573.autoboard.comment.service.CommentService;
import com.neuma573.autoboard.global.exception.CommentNotAccessibleException;
import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@RequiredArgsConstructor
@Component
public class CommentAccessAspect {

    private final JwtProvider jwtProvider;

    private final CommentService commentService;

    @Around("@annotation(checkCommentAccess)")
    public Object checkUserCommentAccess(ProceedingJoinPoint joinPoint, CheckCommentAccess checkCommentAccess) throws Throwable {

        CommentAction action = checkCommentAccess.action();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String httpMethod = httpServletRequest.getMethod();
        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);

        Map<String, String[]> paramMap = jwtProvider.getParameterMap(httpServletRequest);

        AtomicReference<Long> commentId = new AtomicReference<>(-1L);
        AtomicReference<Long> postId = new AtomicReference<>(-1L);

        switch (httpMethod) {
            case "PUT" -> Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> arg instanceof CommentRequest)
                    .findFirst()
                    .ifPresent(arg -> {
                        CommentRequest commentRequest = (CommentRequest) arg;
                        postId.set(commentRequest.getPostId());
                        if (commentRequest instanceof CommentModifyRequest) {
                            commentId.set(((CommentModifyRequest) commentRequest).getCommentId());
                        }
                    });
            case "DELETE" -> {
                Optional<Long> commentIdOptional = jwtProvider.extractLongFromParamMap(paramMap, "commentId");
                commentIdOptional.ifPresent(commentId::set);
            }
        }


        switch (action) {
            case DELETE -> {
                if (!commentService.isCommentAccessible(userId, commentId.get(), action)) {
                    throw new CommentNotAccessibleException("댓글을 삭제할 수 없습니다.");
                }
            }
            case UPDATE -> {
                if (!commentService.isCommentAccessible(userId, commentId.get(), action)) {
                    throw new PostNotAccessibleException("댓글을 수정할 수 없습니다.");
                }
            }
        }


        return joinPoint.proceed();
    }

}
