package com.neuma573.autoboard.file.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CkEditorResponse {

    private boolean uploaded;
    private String url;

}
