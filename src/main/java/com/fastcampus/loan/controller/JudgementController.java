package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.JudgementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/judgements")
public class JudgementController extends AbstractController{

    private final JudgementService judgementService;

    @PostMapping
    public ResponseDTO<JudgementDTO.Response> create(@RequestBody JudgementDTO.Request request){
        return ok(judgementService.create(request));
    }

    @GetMapping("/{judgementId}")
    public ResponseDTO<JudgementDTO.Response> get(@PathVariable Long judgementId){
        return ok(judgementService.get(judgementId));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseDTO<JudgementDTO.Response> getJudgementOfApplication(@PathVariable Long applicationId){
        return ok(judgementService.getJudgementOfApplication(applicationId));
    }

    @PutMapping("/{judgementId}")
    public ResponseDTO<JudgementDTO.Response> update(@PathVariable Long judgementId, @RequestBody JudgementDTO.Request request){
        return ok(judgementService.update(judgementId, request));
    }

}