package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.enums.ReserveType;

public interface ReserveService {

    public ReserveCreateResponseDTO createReserve(
            Long userId,
            Long schoolId,
            ReserveType reserveType,
            ReserveCreateRequestDTO requestDTO
    );

    public void cancelReserve(Long reserveId, Long userId);





}
