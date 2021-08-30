package com.fileupload.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResult {

    private List<String> path = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private int statusCode;

}
