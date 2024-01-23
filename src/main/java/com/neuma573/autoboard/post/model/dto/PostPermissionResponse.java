package com.neuma573.autoboard.post.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostPermissionResponse {

    private boolean isAbleToDelete;

    private boolean isAbleToModify;
}
