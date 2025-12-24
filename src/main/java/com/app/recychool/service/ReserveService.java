package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.enums.ReserveType;

import java.util.List;
import java.util.Map;

public interface ReserveService {

    public ReserveCreateResponseDTO createReserve(
            Long userId,
            Long schoolId,
            ReserveType reserveType,
            ReserveCreateRequestDTO requestDTO
    );

    public void cancelReserve(Long reserveId, Long userId);

    public List<Map<String, String>> getMyPlaceReserves(Long userId);
    public Long getMyParkingReserve(Long userId);


}
