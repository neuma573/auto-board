package com.neuma573.autoboard.file.controller;

import com.neuma573.autoboard.file.model.dto.CkEditorResponse;
import com.neuma573.autoboard.file.service.FileService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Objects;

@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
@RestController
public class FileController {

    private final FileService fileService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<CkEditorResponse> ckeditorUpload(
            MultipartHttpServletRequest multipartHttpServletRequest,
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "tempId") String tempId) throws IOException {
        MultipartFile file = multipartHttpServletRequest.getFile("upload");
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.ok().body(fileService.uploadFile(Objects.requireNonNull(file), userId, tempId));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource fileResource = fileService.loadFileAsResource(fileName);
        if (fileResource != null) {
            MediaType mediaType = fileService.getMediaTypeForFileName(fileName);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(fileResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
