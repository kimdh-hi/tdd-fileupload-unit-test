package com.fileupload.controller;

import com.fileupload.response.UploadResult;
import com.fileupload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(value = "file") MultipartFile file) throws IOException {
        log.info("/upload file : {}", file.getOriginalFilename());
        fileUploadService.upload(file);

        return UploadResult.builder()
                .path(List.of("/files/"+file.getOriginalFilename()))
                .userName(principal.getUsername())
                .statusCode(200)
                .build();
    }

    @PostMapping("/uploads")
    public UploadResult uploads(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("files") MultipartFile[] multipartFiles) throws IOException {

        List<String> path = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            fileUploadService.upload(file);
            path.add("/files/"+file.getOriginalFilename());
        }

        return UploadResult.builder()
                .userName(principal.getUsername())
                .path(path)
                .statusCode(200)
                .build();
    }
}
