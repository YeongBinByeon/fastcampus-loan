package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Judgement;
import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.JudgementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JudgementServiceImpl implements JudgementService {

    private final JudgementRepository judgementRepository;

    private final ApplicationRepository applicationRepository;

    private final ModelMapper modelMapper;

    @Override
    public JudgementDTO.Response create(JudgementDTO.Request request) {
        // 신청 정보 검증
        Long applicationId = request.getApplicationId();
        if (!isPresentApplication(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        // request dto -> entity -> save
        Judgement judgement = modelMapper.map(request, Judgement.class);

        Judgement saved = judgementRepository.save(judgement);

        // save -> response dto
        return modelMapper.map(saved, JudgementDTO.Response.class);
    }

    @Override
    public JudgementDTO.Response get(Long judgementId) {
        Judgement judgement = judgementRepository.findById(judgementId).orElseThrow(() -> {
           throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        return modelMapper.map(judgement, JudgementDTO.Response.class);
    }

    @Override
    public JudgementDTO.Response getJudgementOfApplication(Long applicationId) {
        if(!isPresentApplication(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        Judgement judgement = judgementRepository.findByApplicationId(applicationId).orElseThrow(()->{
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        return modelMapper.map(judgement, JudgementDTO.Response.class);
    }

    private boolean isPresentApplication(Long applicationId){
        return applicationRepository.findById(applicationId).isPresent();
    }
}
