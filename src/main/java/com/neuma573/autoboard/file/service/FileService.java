package com.neuma573.autoboard.file.service;

import com.neuma573.autoboard.file.model.dto.CkEditorResponse;
import com.neuma573.autoboard.file.model.dto.UploadFileRequest;
import com.neuma573.autoboard.file.model.entity.UploadedFile;
import com.neuma573.autoboard.file.repository.UploadedFileRepository;
import com.neuma573.autoboard.global.exception.InvalidFileTypeException;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${app.file.upload-path}")
    private String uploadPath;

    @Value("${app.domain}")
    private String domain;

    private final UploadedFileRepository uploadedFileRepository;

    private final UserService userService;

    private final RedisTemplate<String, List<UploadFileRequest>> tempFileRedisTemplate;

    public CkEditorResponse uploadFile(MultipartFile multipartFile, Long userId, String tempId) throws IOException {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        validateImageExtension(extension);

        String newFileName = System.currentTimeMillis() + "." + extension;
        Path targetPath = Paths.get(uploadPath + "/temp/").resolve(newFileName);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Thumbnails.of(inputStream)
                    .scale(1.0)
                    .outputFormat(extension)
                    .toFile(targetPath.toFile());
        }

        UploadFileRequest uploadFileRequest = UploadFileRequest
                .builder()
                .fileName(newFileName)
                .originalFileName(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .createdBy(userId)
                .filePath(targetPath.toAbsolutePath().toString())
                .build();

        List<UploadFileRequest> fileList = tempFileRedisTemplate.opsForValue().get(tempId);
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        fileList.add(uploadFileRequest);

        tempFileRedisTemplate.opsForValue().set(tempId, fileList, 24, TimeUnit.HOURS);

        return CkEditorResponse.builder()
                .uploaded(true)
                .url(domain + "/api/v1/file/" + newFileName)
                .build();
    }

    private void validateImageExtension(String extension) throws InvalidFileTypeException {
        if (isNotImageExtension(extension)) {
            throw new InvalidFileTypeException("Unsupported format.");
        }
    }

    private boolean isNotImageExtension(String extension) {
        return !"png".equalsIgnoreCase(extension) && !"jpg".equalsIgnoreCase(extension) && !"jpeg".equalsIgnoreCase(extension) && !"gif".equalsIgnoreCase(extension);
    }

    public UploadedFile saveFile(UploadFileRequest uploadFileRequest, Post post) throws IOException {

        Path sourceLocation = Paths.get(uploadFileRequest.getFilePath());
        Path targetLocation = Paths.get(uploadPath).resolve(uploadFileRequest.getFileName());
        Files.move(sourceLocation, targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return saveFileAsEntity(uploadFileRequest, targetLocation, post);
    }

    public UploadedFile saveFileAsEntity(UploadFileRequest uploadFileRequest,
                                 Path targetLocation,
                                 Post post) {
        return uploadedFileRepository.save(
                UploadedFile.builder()
                        .fileName(uploadFileRequest.getFileName())
                        .originalFileName(uploadFileRequest.getOriginalFileName())
                        .filePath(targetLocation.toAbsolutePath().toString())
                        .contentType("image")
                        .size(uploadFileRequest.getSize())
                        .createdBy(userService.getUserById(uploadFileRequest.getCreatedBy()))
                        .isDeleted(false)
                        .post(post)
                        .build()
        );
    }

    public Resource loadFileAsResource(String fileName) {
        Path tempFilePath = Paths.get(uploadPath + "/temp/").resolve(fileName).normalize();

        Resource tempFileResource = getResourceIfExists(tempFilePath);
        if (tempFileResource != null) {
            return tempFileResource;
        }
        UploadedFile file = getFileByName(fileName);
        Path mainFilePath = Paths.get(file.getFilePath());
        return getResourceIfExists(mainFilePath);
    }

    private Resource getResourceIfExists(Path filePath) {
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException ignore) {

        }
        return null;
    }

    public MediaType getMediaTypeForFileName(String fileName) {
        String extension = Objects.requireNonNull(StringUtils.getFilenameExtension(fileName)).toLowerCase();
        return switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public UploadedFile getFileByName(String name) {
        UploadedFile file = uploadedFileRepository.findByFileName(name).orElseThrow(
                ()-> new EntityNotFoundException("파일이 존재하지 않습니다."));
        return file;
    }
}
