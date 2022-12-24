package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPrd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the MwPrd entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwPrdRepository extends JpaRepository<MwPrd, Long> {

    public MwPrd findOneByPrdSeqAndCrntRecFlg(Long prdSeq, boolean flag);

    @Query(value = "select prd.PRD_SEQ, prd.EFF_START_DT, prd.PRD_GRP_SEQ, prd.PRD_ID, prd.PRD_NM, prd.PRD_CMNT, prd.PRD_STS_KEY, prd.PRD_TYP_KEY, prd.IRR_FLG, prd.RNDNG_SCL, prd.RNDNG_ADJ, prd.DAILY_ACCURAL_FLG, prd.FND_BY_KEY, prd.CRNCY_CD_KEY,prd.MLT_LOAN_FLG, prd.CRTD_BY, prd.CRTD_DT,prd.LAST_UPD_BY,prd.LAST_UPD_DT,prd.DEL_FLG,prd.EFF_END_DT, prd.CRNT_REC_FLG, prd.IRR_VAL, prd.PDC_NUM\r\n"
            + "from mw_prd prd\r\n" + "join mw_loan_app app on app.PRD_SEQ=prd.PRD_SEQ and app.crnt_rec_flg = 1\r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq = app.loan_app_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psd.crnt_rec_flg = 1  \r\n"
            + "where prd.crnt_rec_flg = 1  and psd.PYMT_SCHED_DTL_SEQ=?", nativeQuery = true)
    public MwPrd findOneByPymtSchedDtlSeq(long pymtSchedDtlSeq);

    public List<MwPrd> findAllByPrdSeqInAndCrntRecFlg(List<Long> prdSeqs, boolean flag);
}
