package com.neuma573.autoboard.file.repository;

import com.neuma573.autoboard.file.model.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    Optional<UploadedFile> findByFileName(String fileName);
}
