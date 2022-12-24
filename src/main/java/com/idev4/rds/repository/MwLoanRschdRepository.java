package com.idev4.rds.repository;

import com.idev4.rds.domain.MwLoanRschd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MwLoanRschdRepository extends JpaRepository<MwLoanRschd, Long> {

    public MwLoanRschd findOneByLoanAppSeqAndCrntRecFlgAndRvslInstIsNull(long seq, boolean flg);

    public MwLoanRschd findOneByLoanAppSeqAndCrntRecFlgAndRvslInstIsNotNull(long seq, boolean flg);
}
