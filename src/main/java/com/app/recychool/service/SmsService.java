package com.app.recychool.service;


import com.app.recychool.domain.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface SmsService {
    public ApiResponseDTO sendAuthentificationCodeBySms(String phoneNumber, HttpSession session);
}
