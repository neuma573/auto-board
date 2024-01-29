package com.neuma573.autoboard.post.controller;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.post.model.annotation.CheckPostAccess;
import com.neuma573.autoboard.post.model.dto.PostModifyRequest;
import com.neuma573.autoboard.post.model.dto.PostPermissionResponse;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.enums.PostAction;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    private final PostService postService;

    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/list")
    public ResponseEntity<Response<Page<PostResponse>>> getPostList(
            @RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);
        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        page = Math.max(page - 1, 0);
        size = Math.min(Math.max(size, 1), 20);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
        return ResponseEntity.ok().body(responseUtils.success(postService.getPostList(boardId, pageable, userId)));
    }

    @CheckPostAccess(action = PostAction.CREATE)
    @CheckBoardAccess(action = BoardAction.READ)
    @PostMapping("")
    public ResponseEntity<Response<PostResponse>> savePost(
            @Valid @RequestBody PostRequest postRequest,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(postService.savePost(userId, postRequest)));
    }

    @CheckPostAccess(action = PostAction.READ)
    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("")
    public ResponseEntity<Response<PostResponse>> getPost(
            @RequestParam(name = "postId") Long postId,
            HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(responseUtils.success(postService.getPost(httpServletRequest, postId)));
    }

    @CheckPostAccess(action = PostAction.UPDATE)
    @CheckBoardAccess(action = BoardAction.READ)
    @PutMapping("")
    public ResponseEntity<Void> modifyPost(
            @Valid @RequestBody PostModifyRequest postModifyRequest) {
        postService.modifyPost(postModifyRequest);
        return ResponseEntity.noContent().build();
    }

    @CheckPostAccess(action = PostAction.DELETE)
    @CheckBoardAccess(action = BoardAction.READ)
    @DeleteMapping("")
    public ResponseEntity<Void> deletePost(
            @RequestParam(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @CheckPostAccess(action = PostAction.READ)
    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/permission")
    public ResponseEntity<Response<PostPermissionResponse>> checkPermission(
            @RequestParam(name = "postId") Long postId,
            HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);
        return ResponseEntity.ok().body(responseUtils.success(postService.getPermissionFrom(postId, userId)));
    }
}
