package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.RepaymentDTO;

import java.util.List;

public interface RepaymentService {

    RepaymentDTO.Response create(Long applicationId, RepaymentDTO.Request request);

    List<RepaymentDTO.ListResponse> get(Long applicationId);
}
