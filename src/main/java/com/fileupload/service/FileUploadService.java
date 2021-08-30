package com.fileupload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class FileUploadService {

    private Path path;

    public FileUploadService() {
        this.path = Path.of("uploads");
        try {
            initUploadDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUploadDirectory() throws IOException {
        if (!Files.exists(path)) Files.createDirectories(path);
    }

    public String upload(MultipartFile file) throws IOException {
        file.transferTo(path.resolve(file.getOriginalFilename()));

        return path.resolve(file.getOriginalFilename()).toString();
    }
}
