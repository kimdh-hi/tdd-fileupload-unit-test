package com.fileupload;

import com.fileupload.service.FileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FileDownloadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileService fileService;

    @Test
    @DisplayName("텍스트 파일 다운로드 테스트")
    void textFileDownloadTest() throws Exception {

        // get downloadPath method
        Mockito.when(fileService.getDownloadPath(anyString()))
                .thenReturn(new ClassPathResource("test1.txt").getFile().toPath());

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/download?file=test1.txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(">>> test1.txt : " + response);

        Assertions.assertThat(response).isEqualTo("test1");
    }
}
