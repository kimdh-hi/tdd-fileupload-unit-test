package com.fileupload.controller;

import com.fileupload.response.UploadResult;
import com.fileupload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public UploadResult upload(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        log.info("/upload file.name : {}", file.getOriginalFilename());
        String path = fileUploadService.upload(file);
        log.info("/upload path : {}", path);

        return UploadResult.builder()
                .name(List.of(file.getOriginalFilename()))
                .path(List.of(path))
                .statusCode(200)
                .build();
    }

    @PostMapping("/uploads")
    public UploadResult uploads(
            @RequestParam("files") MultipartFile[] multipartFiles
    ) throws IOException {
        List<String> name = new ArrayList<>();
        List<String> path = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            String uploadPath = fileUploadService.upload(file);
            path.add(uploadPath);
            name.add(file.getOriginalFilename());
        }
        return UploadResult.builder()
                .name(name)
                .path(path)
                .statusCode(200)
                .build();
    }
}
