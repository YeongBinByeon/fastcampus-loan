package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.JudgementDTO;

public interface JudgementService {

    JudgementDTO.Response create(JudgementDTO.Request request);

    JudgementDTO.Response get(Long judgementId);

    JudgementDTO.Response getJudgementOfApplication(Long applicationId);

    JudgementDTO.Response update(Long judgementId, JudgementDTO.Request request);

    void delete(Long judgementId);
}
