package com.fileupload;

import com.fileupload.response.UploadResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadTest {

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("파일 단건 업로드 테스트")
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
    }

    @Test
    @DisplayName("여러건 업로드 테스트")
    void 다건_업로드() {
        HttpEntity request = getHttpEntity( new ClassPathResource("test1.txt"), new ClassPathResource("test2.txt"));

        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/api/uploads")
                .build()
                .toUri();

        TestRestTemplate template = new TestRestTemplate();
        ResponseEntity<UploadResult> response = template.postForEntity(uri, request, UploadResult.class);

        printLog(response.getBody());

        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo(200);
    }

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
