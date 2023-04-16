package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Entry;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.dto.EntryDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService {

    private final BalanceService balanceService;

    private final EntryRepository entryRepository;

    private final ApplicationRepository applicationRepository;

    private final ModelMapper modelMapper;

    @Override
    public EntryDTO.Response create(Long applicationId, EntryDTO.Request request) {
        // 계약 체결 여부 검증
        if(!isContractedApplication(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        Entry entry = modelMapper.map(request, Entry.class);
        entry.setApplicationId(applicationId);

        entryRepository.save(entry);

        // 대출 잔고 관리
        balanceService.create(applicationId,
                BalanceDTO.Request.builder()
                        .entryAmount(request.getEntryAmount())
                        .build());

        return modelMapper.map(entry, EntryDTO.Response.class);
    }

    private boolean isContractedApplication(Long applicationId){
        Optional<Application> existed = applicationRepository.findById(applicationId);
        if(existed.isEmpty()){
            return false;
        }

        return existed.get().getContractedAt() != null;
    }
}
