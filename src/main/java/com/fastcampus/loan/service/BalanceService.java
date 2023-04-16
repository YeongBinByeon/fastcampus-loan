package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.BalanceDTO;

public interface BalanceService {

    BalanceDTO.Response create(Long applicationId, BalanceDTO.Request request);
}
