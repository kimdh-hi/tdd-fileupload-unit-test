package com.fileupload.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@Setter
public class
FileService {

    private Path uploadDir;

    public FileService() {
        this.uploadDir = Path.of("uploads");
        try {
            initUploadDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initUploadDirectory() throws IOException {
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
    }

    public String upload(MultipartFile file) throws IOException {
        file.transferTo(uploadDir.resolve(file.getOriginalFilename()));

        return uploadDir.resolve(file.getOriginalFilename()).toString();
    }

    public Path getDownloadPath(String file) {
        return uploadDir.resolve(file);
    }
}
