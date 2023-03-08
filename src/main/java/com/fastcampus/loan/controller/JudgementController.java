package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.JudgementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/judgements")
public class JudgementController extends AbstractController{

    private final JudgementService judgementService;

    @PostMapping
    public ResponseDTO<JudgementDTO.Response> create(@RequestBody JudgementDTO.Request request){
        return ok(judgementService.create(request));
    }
}
