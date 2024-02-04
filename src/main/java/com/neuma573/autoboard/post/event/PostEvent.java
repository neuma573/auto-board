package com.neuma573.autoboard.post.event;

import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.model.enums.PostAction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PostEvent extends ApplicationEvent {

    @Getter
    private final Post post;

    @Getter
    private final PostAction postAction;

    public PostEvent(Object source, Post post, PostAction postAction) {
        super(source);
        this.post = post;
        this.postAction = postAction;
    }


}
