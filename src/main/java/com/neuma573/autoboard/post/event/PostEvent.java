package com.neuma573.autoboard.post.event;

import com.neuma573.autoboard.post.model.entity.Post;
import org.springframework.context.ApplicationEvent;

public class PostEvent extends ApplicationEvent {

    private final Post post;

    public PostEvent(Object source, Post post) {
        super(source);
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

}
