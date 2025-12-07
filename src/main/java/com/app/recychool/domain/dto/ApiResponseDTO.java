package com.app.recychool.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApiResponseDTO<T> {
    private String message;
    private T data;

    ApiResponseDTO(String message){
        this.message = message;
    }

    public static <T> ApiResponseDTO<T> of(String message) {
        return new ApiResponseDTO<>(message);
    }

    public static <T> ApiResponseDTO<T> of(String message, T data) {
        return new ApiResponseDTO<>(message, data);
    }

}

