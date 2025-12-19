package com.app.recychool.service;

import com.app.recychool.domain.dto.PaymentCompleteRequestDTO;
import com.app.recychool.domain.dto.PaymentCompleteResponseDTO;
import com.app.recychool.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentCompleteResponseDTO completePayment(PaymentCompleteRequestDTO requestDTO) {
        return null;
    }
}
