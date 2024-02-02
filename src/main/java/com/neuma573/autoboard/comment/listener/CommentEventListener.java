package com.neuma573.autoboard.comment.listener;

import com.neuma573.autoboard.comment.event.CommentEvent;
import com.neuma573.autoboard.comment.service.CommentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentEventListener {

    private final CommentHistoryService commentHistoryService;

    @EventListener
    public void handleCommentEvent(CommentEvent event) {
        commentHistoryService.saveCommentHistory(event);
    }
 }
