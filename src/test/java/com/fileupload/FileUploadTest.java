package com.fileupload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileupload.response.UploadResult;
import com.fileupload.service.FileUploadService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FileUploadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileUploadService fileUploadService;
    private ObjectMapper mapper = new ObjectMapper();


    private Path rootPath;

//    @BeforeEach
//    void before() throws IOException {
//
//        // 업로드 파일 root path: build/resouces/test/files
//        ClassPathResource testDirPath = new ClassPathResource("files");
//        rootPath = testDirPath.getFile().toPath();
//        // 업로드 디렉토리를 테스트마다 달리하기 위함
//        // 똑같은 폴더에 같은 이름의 파일이 계속 덮어지면 제대로 테스트가 되는지 확인할 수 없음
//        // 테스트마다 랜덤한 이름의 폴더 생성
//        rootPath = rootPath.resolve(""+Math.abs(new Random().nextInt()));
//        fileUploadService.setUploadDir(rootPath);
//        fileUploadService.initUploadDirectory();
//    }
//
//    @AfterEach
//    void after() throws IOException {
//        if (Files.exists(rootPath)) FileSystemUtils.deleteRecursively(rootPath);
//    }


    @DisplayName("1. 인증된 user가 파일 단건 업로드 테스트")
    @Test
    @WithMockUser(username = "testUser")
    void 단건_업로드() throws Exception {

        String responseJson = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart("/api/upload")
                        .file(
                                new MockMultipartFile(
                                        "file",
                                        "test1.txt",
                                        MediaType.TEXT_PLAIN_VALUE,
                                        new ClassPathResource("test1.txt").getInputStream()))
                        .with(csrf())
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UploadResult result = mapper.readValue(responseJson, UploadResult.class);

        Assertions.assertThat(result.getUserName()).isEqualTo("testUser");
        Assertions.assertThat(result.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(result.getPath().size()).isEqualTo(1);
    }

    @DisplayName("2.인증된 user가 2개 파일 업로드 테스트")
    @Test
    @WithMockUser(username = "testUser")
    void uploads() throws Exception {
        String responseJson = mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart("/api/uploads")
                        .file(new MockMultipartFile("files", "test1.txt", MediaType.TEXT_PLAIN_VALUE, new ClassPathResource("test1.txt").getInputStream()))
                        .file(new MockMultipartFile("files", "test2.txt", MediaType.TEXT_PLAIN_VALUE, new ClassPathResource("test2.txt").getInputStream()))
                        .with(csrf())
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        UploadResult result = mapper.readValue(responseJson, UploadResult.class);

        Assertions.assertThat(result.getUserName()).isEqualTo("testUser");
        Assertions.assertThat(result.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(result.getPath().size()).isEqualTo(2);
    }
}
