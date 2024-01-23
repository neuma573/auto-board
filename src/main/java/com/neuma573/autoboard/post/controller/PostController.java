package com.neuma573.autoboard.post.controller;

import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    private final PostService postService;

    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity<Response<List<PostResponse>>> getPostList(
            @RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            HttpServletRequest httpServletRequest) {

        Long userId = jwtProvider.parseIdFrom(httpServletRequest);
        boardService.checkAccessibleAndThrow(boardId, userId);

        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        page = Math.max(page - 1, 0);

        size = Math.min(Math.max(size, 1), 20);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        return ResponseEntity.ok().body(responseUtils.success(postService.getPostList(boardId, pageable, userId)));
    }

    @PostMapping("")
    public ResponseEntity<Response<PostResponse>> savePost(@Valid @RequestBody PostRequest postRequest, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.getUserId(httpServletRequest);
        boardService.checkAccessibleAndThrow(postRequest.getBoardId(), userId);

        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(postService.savePost(userId, postRequest)));
    }

    @GetMapping("")
    public ResponseEntity<Response<PostResponse>> getPost(
            @RequestParam(name = "postId") Long postId,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseIdFrom(httpServletRequest);
        postService.checkAccessibleAndThrow(postId, userId);

        return ResponseEntity.ok().body(responseUtils.success(postService.getPost(httpServletRequest, postId)));
    }

    @PutMapping("")
    public ResponseEntity<Void> modifyPost(
            @Valid @RequestBody PostModifyRequest postModifyRequest,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseIdFrom(httpServletRequest);
        postService.checkAccessibleAndThrow(postModifyRequest.getPostId(), userId);

        postService.modifyPost(postModifyRequest, userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deletePost(
            @RequestParam(name = "postId") Long postId,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseIdFrom(httpServletRequest);
        postService.checkAccessibleAndThrow(postId, userId);

        postService.deletePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}
