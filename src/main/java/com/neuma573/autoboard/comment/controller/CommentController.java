package com.neuma573.autoboard.comment.controller;

import com.neuma573.autoboard.comment.model.dto.CommentModifyRequest;
import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.dto.CommentResponse;
import com.neuma573.autoboard.comment.service.CommentService;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    @PostMapping("")
    public ResponseEntity<Response<CommentResponse>> saveComment(
            @RequestBody @Valid CommentRequest commentRequest,
            HttpServletRequest httpServletRequest
            ) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);

        return ResponseEntity.created(URI.create("/main")).body(responseUtils.success(commentService.saveComment(commentRequest, userId)));
    }

    @GetMapping("/list")
    public ResponseEntity<Response<Page<CommentResponse>>> getCommentList(
            @RequestParam(name = "postId") Long postId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);
        Sort.Direction direction = Sort.Direction.ASC;
        page = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(page, 20, Sort.by(direction, "createdAt"));
        return ResponseEntity.ok().body(responseUtils.success(commentService.getCommentList(postId, pageable, userId)));
    }

    @PutMapping("")
    public ResponseEntity<Void> modifyComment(
            @RequestBody @Valid CommentModifyRequest commentModifyRequest
    ) {
        commentService.modifyComment(commentModifyRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteComment(
            @RequestParam(name = "commentId") Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
