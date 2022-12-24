package com.idev4.rds.repository;

import com.idev4.rds.domain.MwClntHlthInsr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MwClntHlthInsr entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwClntHlthInsrRepository extends JpaRepository<MwClntHlthInsr, Long> {

    public MwClntHlthInsr findOneByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flag);

}
