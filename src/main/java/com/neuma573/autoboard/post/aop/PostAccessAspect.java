package com.neuma573.autoboard.post.aop;

import com.neuma573.autoboard.post.model.annotation.CheckPostAccess;
import com.neuma573.autoboard.post.model.enums.PostAction;
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
public class PostAccessAspect {
    @Around("@annotation(checkPostAccess)")
    public Object checkUserPostAccess(ProceedingJoinPoint joinPoint, CheckPostAccess checkPostAccess) throws Throwable {
        PostAction action = checkPostAccess.action();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        switch (action) {
            case READ -> {

            }
            case DELETE -> {

            }
            case CREATE -> {

            }
            case UPDATE -> {

            }
        }


        return joinPoint.proceed();
    }
}
