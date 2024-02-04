package com.neuma573.autoboard.comment.event;

import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.comment.model.enums.CommentAction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class CommentEvent extends ApplicationEvent {

    @Getter
    private final Comment comment;

    @Getter
    private final CommentAction commentAction;

    public CommentEvent(Object source, Comment comment, CommentAction commentAction) {
        super(source);
        this.comment = comment;
        this.commentAction = commentAction;
    }
}
