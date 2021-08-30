package com.fileupload.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResult {

    private List<String> path;
    private List<String> name;
    private int statusCode;

}
