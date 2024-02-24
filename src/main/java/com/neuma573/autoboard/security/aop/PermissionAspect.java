package com.neuma573.autoboard.security.aop;

import com.neuma573.autoboard.global.exception.AccessDeniedException;
import com.neuma573.autoboard.security.model.annotation.CheckPermission;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@RequiredArgsConstructor
@Component
public class PermissionAspect {

    private final JwtProvider jwtProvider;

    private final UserService userService;

    @Around("@annotation(checkPermission)")
    public Object checkUserPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        Role requiredRole = checkPermission.role();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        User user = userService.getUserById(userId);

        if (requiredRole == Role.ADMIN && !userService.isAdmin(user)) {
            throw new AccessDeniedException("관리자만 접근가능합니다.");
        }
        return joinPoint.proceed();
    }
}
