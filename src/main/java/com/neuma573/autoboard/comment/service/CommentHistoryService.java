package com.neuma573.autoboard.comment.service;

import com.neuma573.autoboard.comment.event.CommentEvent;
import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.comment.model.entity.CommentHistory;
import com.neuma573.autoboard.comment.repository.CommentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentHistoryService {

    private final CommentHistoryRepository commentHistoryRepository;

    @Transactional
    public void saveCommentHistory(CommentEvent event) {
        Comment comment = event.getComment();
        CommentHistory commentHistory = CommentHistory
                .builder()
                .commentId(comment.getId())
                .createdBy(comment.getCreatedBy())
                .changedBy(comment.getCurrentUser())
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .post(comment.getPost())
                .historyType(event.getCommentAction())
                .build();
        commentHistoryRepository.save(commentHistory);
    }

}
