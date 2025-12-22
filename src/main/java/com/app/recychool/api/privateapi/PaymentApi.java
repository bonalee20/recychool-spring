package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.PaymentCompleteRequestDTO;
import com.app.recychool.domain.dto.PaymentCompleteResponseDTO;
import com.app.recychool.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/payment")
@RequiredArgsConstructor
public class PaymentApi {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<ApiResponseDTO> complete(@RequestBody PaymentCompleteRequestDTO requestDTO) {
        PaymentCompleteResponseDTO result = paymentService.completePayment(requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.of("결제 완료 처리 성공", result));
    }
}
