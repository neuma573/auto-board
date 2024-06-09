package com.neuma573.autoboard.comment.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RepliesResponse {

    List<CommentResponse> replies;

    boolean hasMore;

}
