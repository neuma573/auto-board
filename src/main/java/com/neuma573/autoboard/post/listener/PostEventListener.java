package com.neuma573.autoboard.post.listener;

import com.neuma573.autoboard.post.event.PostEvent;

import com.neuma573.autoboard.post.service.PostHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostEventListener {

    private final PostHistoryService postHistoryService;

    @EventListener
    public void handlePostEvent(PostEvent event) {
        postHistoryService.savePostHistory(event);
    }
}