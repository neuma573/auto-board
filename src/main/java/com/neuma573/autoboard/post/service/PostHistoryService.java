package com.neuma573.autoboard.post.service;

import com.neuma573.autoboard.post.event.PostEvent;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.model.entity.PostHistory;
import com.neuma573.autoboard.post.repository.PostHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostHistoryService {

    private final PostHistoryRepository postHistoryRepository;

    @Transactional
    public void savePostHistory(PostEvent event) {
        Post post = event.getPost();


        PostHistory postHistory = PostHistory.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .postId(post.getId())
                .board(post.getBoard())
                .isDeleted(post.isDeleted())
                .createdBy(post.getCreatedBy())
                .views(post.getViews())
                .changedBy(post.getCurrentUser())
                .historyType(event.getPostAction())
                .build();
        postHistoryRepository.save(postHistory);
    }

}
