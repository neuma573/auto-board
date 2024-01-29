package com.neuma573.autoboard.board.model.annotation;

import com.neuma573.autoboard.board.model.enums.BoardAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBoardAccess {
    BoardAction action();
}
