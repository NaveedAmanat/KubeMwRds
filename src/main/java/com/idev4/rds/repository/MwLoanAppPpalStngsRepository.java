package com.idev4.rds.repository;

import com.idev4.rds.domain.MwLoanAppPpalStngs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MwLoanAppPpalStngsRepository extends JpaRepository<MwLoanAppPpalStngs, Long> {

    public MwLoanAppPpalStngs findOneByPrdSeqAndLoanAppSeq(long prdSeq, long loanAppSeq);

}
