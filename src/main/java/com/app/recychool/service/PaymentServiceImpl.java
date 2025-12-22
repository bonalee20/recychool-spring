package com.app.recychool.service;

import com.app.recychool.domain.dto.PaymentCompleteRequestDTO;
import com.app.recychool.domain.dto.PaymentCompleteResponseDTO;
import com.app.recychool.domain.entity.Payment;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.type.PaymentStatus;
import com.app.recychool.repository.PaymentRepository;
import com.app.recychool.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReserveRepository reserveRepository;

    @Override
    public PaymentCompleteResponseDTO completePayment(PaymentCompleteRequestDTO requestDTO) {

        // 1) 예약 조회
        Reserve reserve = reserveRepository.findById(requestDTO.getReserveId())
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다. reserveId=" + requestDTO.getReserveId()));

        // 2) 예약 1건당 결제 1건 (이미 결제됐으면 차단)
        if (paymentRepository.existsByReserve_Id(reserve.getId())) {
            throw new IllegalStateException("이미 결제가 완료된 예약입니다. reserveId=" + reserve.getId());
        }

        // 3) impUid 중복 차단
        if (paymentRepository.existsByImpUid(requestDTO.getImpUid())) {
            throw new IllegalStateException("이미 처리된 결제입니다. impUid=" + requestDTO.getImpUid());
        }

        // 4) 결제 성공 시점에만 저장
        Payment payment = Payment.builder()
                .reserve(reserve)
                .paymentPrice(reserve.getReservePrice())
                .impUid(requestDTO.getImpUid())
                .merchantUid(requestDTO.getMerchantUid())
                .paymentStatus(PaymentStatus.PAID)
                .paymentType(requestDTO.getPaymentType())
                .build();

        Payment saved = paymentRepository.save(payment);

        // 5) 예약 상태 업데이트: 결제 성공 → CONFIRMED
        reserve.setReserveStatus(ReserveStatus.CONFIRMED);

        return new PaymentCompleteResponseDTO(
                saved.getId(),
                reserve.getId(),
                saved.getPaymentStatus(),
                saved.getPaymentPrice()
        );
    }
}
