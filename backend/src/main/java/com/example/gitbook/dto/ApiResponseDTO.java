package com.example.gitbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 状态码
     */
    private int code;
    
    /**
     * 创建成功响应
     * @param data 数据
     * @return ApiResponseDTO
     */
    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message("操作成功")
                .data(data)
                .code(200)
                .build();
    }
    
    /**
     * 创建失败响应
     * @param message 错误消息
     * @return ApiResponseDTO
     */
    public static <T> ApiResponseDTO<T> fail(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .code(500)
                .build();
    }
    
    /**
     * 创建失败响应
     * @param message 错误消息
     * @param code 状态码
     * @return ApiResponseDTO
     */
    public static <T> ApiResponseDTO<T> fail(String message, int code) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .code(code)
                .build();
    }
}