package com.fileupload.controller;

import com.fileupload.response.UploadResult;
import com.fileupload.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public UploadResult upload(@RequestParam("file") MultipartFile file) throws IOException {

        String path = fileService.upload(file);
        log.info("/upload path : {}", path);

        return UploadResult.builder()
                .name(List.of(file.getOriginalFilename()))
                .path(List.of(path))
                .statusCode(200)
                .build();
    }

    @PostMapping("/uploads")
    public UploadResult uploads(@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {

        List<String> name = new ArrayList<>();
        List<String> path = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String uploadPath = fileService.upload(file);
            path.add(uploadPath);
            name.add(file.getOriginalFilename());
        }

        return UploadResult.builder()
                .name(name)
                .path(path)
                .statusCode(200)
                .build();
    }

    @GetMapping("/download")
    public void download(
            @RequestParam(value="file") String file, HttpServletResponse response
    ) throws IOException {
        // 다운로드 할 파일의 경로 get
        Path downloadPath = fileService.getDownloadPath(file);
        // 해당 경로가 존재하지 않는 경우 예외
        if (!Files.exists(downloadPath)) throw new FileNotFoundException("파일을 찾을 수 없습니다.");

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(downloadPath));
    }
}
