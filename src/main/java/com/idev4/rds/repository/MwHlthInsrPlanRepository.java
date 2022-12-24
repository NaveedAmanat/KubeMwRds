package com.idev4.rds.repository;

import com.idev4.rds.domain.MwHlthInsrPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MwHlthInsrPlanRepository extends JpaRepository<MwHlthInsrPlan, Long> {

    @Query(value = "select pln.* from mw_hlth_insr_plan pln\r\n"
            + "join mw_clnt_hlth_insr ins on ins.hlth_insr_plan_seq = pln.hlth_insr_plan_seq and ins.crnt_rec_flg=1\r\n"
            + "where pln.crnt_rec_flg=1 and ins.loan_app_seq=:loanAppSeq", nativeQuery = true)
    public MwHlthInsrPlan findByLoanAppSeqAndCrntRecFlg(@Param("loanAppSeq") long loanAppSeq);
}
