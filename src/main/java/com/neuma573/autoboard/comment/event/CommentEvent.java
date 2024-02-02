package com.neuma573.autoboard.comment.event;

import com.neuma573.autoboard.comment.model.entity.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class CommentEvent extends ApplicationEvent {

    @Getter
    private final Comment comment;

    public CommentEvent(Object source, Comment comment) {
        super(source);
        this.comment = comment;
    }
}
