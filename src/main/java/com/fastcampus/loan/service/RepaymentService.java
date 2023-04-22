package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.RepaymentDTO;

public interface RepaymentService {

    RepaymentDTO.Response create(Long applicationId, RepaymentDTO.Request request);
}
