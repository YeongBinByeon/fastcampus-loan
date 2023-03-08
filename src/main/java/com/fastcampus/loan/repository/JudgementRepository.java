package com.fastcampus.loan.repository;

import com.fastcampus.loan.domain.Judgement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JudgementRepository extends JpaRepository<Judgement, Long> {

}