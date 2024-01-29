package com.neuma573.autoboard.comment.model.annotation;

import com.neuma573.autoboard.comment.model.enums.CommentAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCommentAccess {
    CommentAction action();
}
