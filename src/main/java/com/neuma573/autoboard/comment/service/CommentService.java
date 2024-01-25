package com.neuma573.autoboard.comment.service;

import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.dto.CommentResponse;
import com.neuma573.autoboard.comment.repository.CommentRepository;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final PostService postService;

    @Transactional
    public CommentResponse savePost(CommentRequest commentRequest, Long userId) {

        Post parentPost = postService.getPostById(commentRequest.getPostId());
        User createdBy = userService.getUserById(userId);

        return CommentResponse.of(commentRepository.save(commentRequest.toEntity(parentPost, createdBy)));
    }

}
