package com.idev4.rds.repository;

import com.idev4.rds.domain.MwClntHlthInsrCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MwClntHlthInsrCardRepository extends JpaRepository<MwClntHlthInsrCard, Long> {

    public MwClntHlthInsrCard findOneByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flag);

}
