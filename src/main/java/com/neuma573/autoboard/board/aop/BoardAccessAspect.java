package com.neuma573.autoboard.board.aop;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostRequest;
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
public class BoardAccessAspect {

    private final JwtProvider jwtProvider;

    private final BoardService boardService;

    private final PostService postService;

    @Around("@annotation(checkBoardAccess)")
    public Object checkUserBoardAccess(ProceedingJoinPoint joinPoint, CheckBoardAccess checkBoardAccess) throws Throwable {
        BoardAction action = checkBoardAccess.action();
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
                if (!boardService.isBoardAccessible(userId, boardId.get(), action)) {
                    throw new BoardNotAccessibleException("게시판 읽기 권한이 없습니다.");
                }
            }
            case DELETE -> {
                if (!boardService.isBoardAccessible(userId, boardId.get(), action)) {
                    throw new BoardNotAccessibleException("게시판 삭제 권한이 없습니다.");
                }
            }
            case CREATE, UPDATE -> {
                if (!boardService.isCreatable(userId)) {
                    throw new BoardNotAccessibleException("게시판 생성/수정 권한이 없습니다.");
                }
            }
            case UNAUTHORIZED_ACTION -> {

            }
        }

        return joinPoint.proceed();
    }
}
