package com.neuma573.autoboard.global.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private String status;
    private String message;
    private T data;

    public static <T> Response<T> success(String message, T data){
        return new Response<>("Success", message, data);
    }

    public static Response<Void> error(String status, String message){
        return new Response<>(status, message, null);
    }
}
