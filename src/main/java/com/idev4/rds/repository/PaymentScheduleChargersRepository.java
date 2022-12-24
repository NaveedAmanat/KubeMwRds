package com.idev4.rds.repository;

import com.idev4.rds.domain.PaymentScheduleChargers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentScheduleChargersRepository extends JpaRepository<PaymentScheduleChargers, Long> {

    @Query(value = "select tot_chrg_due amt,'SERVICE CHARGES' typ_str,17 CHRG_TYPS_SEQ\r\n" + "from mw_pymt_sched_dtl psd \r\n"
            + "where psd.pymt_sched_dtl_seq=:paySchedDtlSeq and psd.crnt_rec_flg=1\r\n" + "union\r\n"
            + "select psc.amt amt,typ_str,psc.CHRG_TYPS_SEQ\r\n" + "from mw_pymt_sched_chrg psc \r\n"
            + "left outer join mw_typs t on psc.chrg_typs_seq=t.typ_seq and t.del_flg=0 and t.crnt_rec_flg=1\r\n"
            + "where psc.pymt_sched_dtl_seq=:paySchedDtlSeq and psc.del_flg=0 and psc.crnt_rec_flg=1", nativeQuery = true)
    List<Object[]> getPaymentScheduleChargersBy(@Param("paySchedDtlSeq") long paySchedDtlSeq);

    List<PaymentScheduleChargers> findAllByPaySchedDtlSeqAndCrntRecFlg(long paySchedDtlSeq, boolean flg);

    @Query(value = "select psc.*\r\n" + "from mw_pymt_sched_chrg psc   \r\n"
            + "join mw_pymt_sched_dtl psd on psd.PYMT_SCHED_DTL_SEQ=psc.PYMT_SCHED_DTL_SEQ and psd.CRNT_REC_FLG=1  \r\n"
            + "join mw_pymt_sched_hdr psh on psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ and psh.CRNT_REC_FLG = 1 \r\n"
            + "join mw_ref_cd_val val on val.REF_CD_SEQ = psd.PYMT_STS_KEY and val.CRNT_REC_FLG = 1 and val.REF_CD='0945'  \r\n"
            + "join mw_typs t on t.TYP_SEQ=psc.CHRG_TYPS_SEQ and t.CRNT_REC_FLG =1 and typ_id='0005'\r\n"
            + "where psc.CRNT_REC_FLG=1  and psh.LOAN_APP_SEQ=:loanAppSeq", nativeQuery = true)
    List<PaymentScheduleChargers> findAllkashCareChargesByLoanAppSeq(@Param("loanAppSeq") long loanAppSeq);

    @Query(value = "select psc.* \r\n" + "from mw_pymt_sched_chrg psc  \r\n"
            + "join mw_pymt_sched_dtl psd on psd.PYMT_SCHED_DTL_SEQ=psc.PYMT_SCHED_DTL_SEQ and psd.CRNT_REC_FLG=1 \r\n"
            + "join mw_pymt_sched_hdr psh on psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ and psh.CRNT_REC_FLG = 1\r\n"
            + "join mw_ref_cd_val val on val.REF_CD_SEQ = psd.PYMT_STS_KEY and val.CRNT_REC_FLG = 1 and val.REF_CD='0945' \r\n"
            + "where psc.CHRG_TYPS_SEQ=-2 and psc.CRNT_REC_FLG=0 and psc.DEL_FLG = 1\r\n"
            + "and psh.LOAN_APP_SEQ=:loanAppSeq ", nativeQuery = true)
    List<PaymentScheduleChargers> findAllDeletedkashCareChargesByLoanAppSeq(@Param("loanAppSeq") long loanAppSeq);

    List<PaymentScheduleChargers> findAllByPaySchedDtlSeqInAndCrntRecFlg(List<Long> paySchedDtlSeq, boolean flg);
}
