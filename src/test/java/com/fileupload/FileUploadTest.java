package com.fileupload;

import com.fileupload.response.UploadResult;
import com.fileupload.service.FileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadTest {

    @LocalServerPort
    private int port;
    private Path rootPath;

    @Autowired
    FileService fileService;


    @BeforeEach
    void before() throws IOException {
        // 업로드 파일 root path: build/resouces/test/files
        ClassPathResource testDirPath = new ClassPathResource("files");
        rootPath = testDirPath.getFile().toPath();
        // 업로드 디렉토리를 테스트마다 달리하기 위함
        // 똑같은 폴더에 같은 이름의 파일이 계속 덮어지면 제대로 테스트가 되는지 확인할 수 없음
        // 테스트마다 랜덤한 이름의 폴더 생성
        rootPath = rootPath.resolve(""+Math.abs(new Random().nextInt()));
        fileService.setUploadDir(rootPath);
        fileService.initUploadDirectory();
    }

    @AfterEach
    void after() throws IOException {
        if (Files.exists(rootPath)) FileSystemUtils.deleteRecursively(rootPath);
    }


    @DisplayName("1. 파일 단건 업로드 테스트")
    @Test
    void 단건_업로드() {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/api/upload")
                .build()
                .toUri();

        HttpEntity request = getHttpEntity(new ClassPathResource("test1.txt"));
        System.out.println("request = " + request);
        TestRestTemplate template = new TestRestTemplate();
        ResponseEntity<UploadResult> response = template.postForEntity(uri, request, UploadResult.class);

        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getBody().getName().get(0)).isEqualTo("test1.txt");
    }

    @DisplayName("2. 여러 건 업로드 테스트")
    @Test
    void 다건_업로드() {
        HttpEntity request = getHttpEntity( new ClassPathResource("test1.txt"), new ClassPathResource("test2.txt"));

        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/api/uploads")
                .build()
                .toUri();

        TestRestTemplate template = new TestRestTemplate();
        ResponseEntity<UploadResult> response = template.postForEntity(uri, request, UploadResult.class);

        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getBody().getName().size()).isEqualTo(2);
    }

    /**
     * 요청 객체 구성
     * MultiValueMap을 form처럼 사용
     */
    private HttpEntity getHttpEntity(Resource... resources) {
        MultiValueMap form = new LinkedMultiValueMap();

        if (resources.length > 1) {
            Arrays.stream(resources).forEach(
                    r -> form.add("files", r)
            );
        } else {
            form.add("file", resources[0]);
        }
        HttpEntity request = new HttpEntity(form, null);
        return request;
    }

    private void printLog(UploadResult result) {
        System.out.println(">>> UploadResult.path : " + result.getPath());
        System.out.println(">>> UploadResult.name : " + result.getName());
        System.out.println(">>> UploadResult.status : " + result.getStatusCode());
    }


}
