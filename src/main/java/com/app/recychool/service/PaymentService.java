package com.app.recychool.service;

import com.app.recychool.domain.dto.PaymentCompleteRequestDTO;
import com.app.recychool.domain.dto.PaymentCompleteResponseDTO;
import com.app.recychool.domain.entity.Payment;

import java.util.List;

public interface PaymentService {

    PaymentCompleteResponseDTO completePayment(PaymentCompleteRequestDTO requestDTO);

}
