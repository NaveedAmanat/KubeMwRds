package com.idev4.rds.repository;

import com.idev4.rds.domain.MwMfcibOthOutsdLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the MwMfcibOthOutsdLoan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwMfcibOthOutsdLoanRepository extends JpaRepository<MwMfcibOthOutsdLoan, Long> {

    public List<MwMfcibOthOutsdLoan> findAllByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean recFlag);

    public MwMfcibOthOutsdLoan findOneByOthOutsdLoanSeqAndCrntRecFlg(long seq, boolean recFlag);
}
