package com.neuma573.autoboard.global.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@Builder
@Getter
public class CustomErrorResponse implements ErrorResponse {

    private final HttpStatusCode statusCode;
    private final ProblemDetail problemDetail;

    public CustomErrorResponse(HttpStatusCode statusCode, ProblemDetail problemDetail) {
        this.statusCode = statusCode;
        this.problemDetail = problemDetail;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }

    public static CustomErrorResponse of(ProblemDetail problemDetail, HttpStatusCode httpStatusCode) {
        return CustomErrorResponse.builder()
                .problemDetail(problemDetail)
                .statusCode(httpStatusCode)
                .build();
    }
}
