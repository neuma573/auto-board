package com.neuma573.autoboard.file.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UploadFileRequest {

    private String fileName;

    private String originalFileName;

    private Long size;

    private String filePath;

    private Long createdBy;

    private LocalDateTime createdAt;

}
