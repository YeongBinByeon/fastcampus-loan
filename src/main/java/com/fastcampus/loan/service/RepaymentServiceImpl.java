package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Entry;
import com.fastcampus.loan.domain.Repayment;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.dto.RepaymentDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.EntryRepository;
import com.fastcampus.loan.repository.RepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {

    private final RepaymentRepository repaymentRepository;

    private final ApplicationRepository applicationRepository;

    private final EntryRepository entryRepository;

    private final BalanceService balanceService;

    private final ModelMapper modelMapper;

    @Override
    public RepaymentDTO.Response create(Long applicationId, RepaymentDTO.Request request) {

        // validation
        // 1. 계약을 완료한 신청 정보
        // 2. 집행이 되어있어야 함
        if(!isRepayableApplication(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        Repayment repayment = modelMapper.map(request, Repayment.class);
        repayment.setApplicationId(applicationId);

        repaymentRepository.save(repayment);

        // 잔고
        // balance : 500 - 100 = 400
        BalanceDTO.Response updatedBalance = balanceService.repaymentUpdate(applicationId,
                BalanceDTO.RepaymentRequest.builder()
                        .repaymentAmount(request.getRepaymentAmount())
                        .type(BalanceDTO.RepaymentRequest.RepaymentType.REMOVE)
                        .build());

        RepaymentDTO.Response response = modelMapper.map(repayment, RepaymentDTO.Response.class);
        response.setBalance(updatedBalance.getBalance());

        return response;
    }

    @Override
    public List<RepaymentDTO.ListResponse> get(Long applicationId) {
        List<Repayment> repayments = repaymentRepository.findAllByApplicationId(applicationId);

        return repayments.stream().map( r-> modelMapper.map(r, RepaymentDTO.ListResponse.class)).collect(Collectors.toList());
    }

    @Override
    public RepaymentDTO.UpdateResponse update(Long repaymentId, RepaymentDTO.Request request) {
        Repayment repayment = repaymentRepository.findById(repaymentId).orElseThrow(()->{
           throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        // 전체 대출 잔고, 상환금액, 결과 대출 잔고
        // 500 - 100 = 400
        // 400 + 100 = 500
        // 500 - 200 = 300

        // 500 - 100 = 400
        // 이전에 잘못 입력한 상환 금액(100) 가져오기
        Long applicationId = repayment.getApplicationId();
        BigDecimal beforeRepaymentAmount = repayment.getRepaymentAmount();

        // 전체 대출 잔액에서 잘못 입력한 상환 금액 되돌리기 (+100)
        balanceService.repaymentUpdate(applicationId,
                BalanceDTO.RepaymentRequest.builder()
                        .repaymentAmount(beforeRepaymentAmount)
                        .type(BalanceDTO.RepaymentRequest.RepaymentType.ADD)
                        .build()
        );

        repayment.setRepaymentAmount(request.getRepaymentAmount());
        repaymentRepository.save(repayment);

        // 전체 대줄 잔액에서 상환 요청한 금액만큼 상환하기 (-200)
        BalanceDTO.Response updatedBalance = balanceService.repaymentUpdate(applicationId,
                BalanceDTO.RepaymentRequest.builder()
                        .repaymentAmount(request.getRepaymentAmount())
                        .type(BalanceDTO.RepaymentRequest.RepaymentType.REMOVE)
                        .build());

        return RepaymentDTO.UpdateResponse.builder()
                .applicationId(applicationId)
                .beforeRepaymentAmount(beforeRepaymentAmount)
                .afterRepaymentAmount(request.getRepaymentAmount())
                .balance(updatedBalance.getBalance())
                .createdAt(repayment.getCreatedAt())
                .updatedAt(repayment.getUpdatedAt())
                .build();
    }

    @Override
    public void delete(Long repaymentId) {
        Repayment repayment = repaymentRepository.findById(repaymentId).orElseThrow(()->{
           throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        Long applicationId = repayment.getApplicationId();
        BigDecimal removeRepaymentAmount = repayment.getRepaymentAmount();

        balanceService.repaymentUpdate(applicationId
                , BalanceDTO.RepaymentRequest.builder()
                                .repaymentAmount(removeRepaymentAmount)
                                .type(BalanceDTO.RepaymentRequest.RepaymentType.ADD)
                        .build()
        );

        repayment.setIsDeleted(true);
        repaymentRepository.save(repayment);
    }

    private boolean isRepayableApplication(Long applicationId){
        Optional<Application> existedApplication = applicationRepository.findById(applicationId);
        if(existedApplication.isEmpty()){
            return false;
        }

        if(existedApplication.get().getContractedAt() == null){
            return false;
        }

        Optional<Entry> existedEntry = entryRepository.findByApplicationId(applicationId);
        return existedEntry.isPresent();

    }
}
