package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Judgement;
import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.JudgementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JudgementServiceTest {

    @InjectMocks
    private JudgementServiceImpl judgementService;

    @Mock
    private JudgementRepository judgementRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Spy
    private ModelMapper modelMapper;

    @Test
    void Should_ReturnResponseOfNewJudgementEntity_When_RequestNewJudgement(){

        Judgement judgement = Judgement.builder()
                .applicationId(1L)
                .name("Member Kim")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        JudgementDTO.Request request = JudgementDTO.Request.builder()
                .applicationId(1L)
                .name("Member Kim")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        //application find
        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(Application.builder().build()));
        //judgement save
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgement);

        JudgementDTO.Response actual = judgementService.create(request);

        Assertions.assertThat(actual.getName()).isSameAs(judgement.getName());
        Assertions.assertThat(actual.getApplicationId()).isSameAs(judgement.getApplicationId());
        Assertions.assertThat(actual.getApprovalAmount()).isSameAs(judgement.getApprovalAmount());

    }

}