package com.idev4.rds.repository;

import com.idev4.rds.domain.PaymentScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PaymentScheduleDetailRepository extends JpaRepository<PaymentScheduleDetail, Long> {

    PaymentScheduleDetail findOneByPaySchedDtlSeqAndCrntRecFlg(Long paySchedDtlSeq, boolean flg);

    @Query(value = "SELECT PSD.INST_NUM,PSD.DUE_DT,PSD.PPAL_AMT_DUE,PSD.TOT_CHRG_DUE,\r\n" + "(SELECT AMT \r\n"
            + "FROM MW_PYMT_SCHED_CHRG PSC\r\n"
            + "WHERE PSC.PYMT_SCHED_DTL_SEQ=PSD.PYMT_SCHED_DTL_SEQ AND PSC.CHRG_TYPS_SEQ=1 AND PSC.CRNT_REC_FLG =1) DOCUMENTS,\r\n"
            + "(SELECT AMT \r\n" + "FROM MW_PYMT_SCHED_CHRG PSC\r\n"
            + "WHERE PSC.PYMT_SCHED_DTL_SEQ=PSD.PYMT_SCHED_DTL_SEQ AND PSC.CHRG_TYPS_SEQ!=1 AND PSC.CRNT_REC_FLG =1) OTHER_CHRGS\r\n"
            + "FROM MW_PYMT_SCHED_HDR PSH, MW_PYMT_SCHED_DTL PSD\r\n" + "WHERE PSH.PYMT_SCHED_HDR_SEQ=PSD.PYMT_SCHED_HDR_SEQ\r\n"
            + "AND PSH.LOAN_APP_SEQ=?\r\n" + "ORDER BY INST_NUM", nativeQuery = true)
    List<Map> getLoanAppDataSet(long loanAppSeq);

    List<PaymentScheduleDetail> findAllByPaySchedHdrSeqAndCrntRecFlgOrderByInstNumAsc(long paySchedHdrSeq, boolean flg);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg   \r\n"
            + "from mw_pymt_sched_dtl psd \r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd='0945' \r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179' \r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.CRNT_REC_FLG = 1 \r\n"
            + "where psh.loan_app_seq=? \r\n" + "and psd.CRNT_REC_FLG=1 \r\n"
            + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getNextDueInst(long loanAppSeq);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd='0945'\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179'\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1\r\n"
            + "where app.clnt_seq=? and psd.crnt_rec_flg=1 \r\n" + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getDuaInstallmentsByClientSeq(long clntSeq);

    // Modified by Yousaf : KMWK anmal adjusment handling.
    @Query(value = "SELECT DISTINCT psd.pymt_sched_dtl_seq,\n" +
            "               psd.eff_start_dt,\n" +
            "               psd.pymt_sched_hdr_seq,\n" +
            "               psd.inst_num,\n" +
            "               psd.due_dt,\n" +
            "               psd.ppal_amt_due,\n" +
            "               psd.tot_chrg_due,\n" +
            "               psd.crtd_by,\n" +
            "               psd.crtd_dt,\n" +
            "               psd.last_upd_by,\n" +
            "               psd.last_upd_dt,\n" +
            "               psd.del_flg,\n" +
            "               psd.eff_end_dt,\n" +
            "               psd.crnt_rec_flg,\n" +
            "               psd.pymt_sts_key,\n" +
            "               psd.sync_flg\n" +
            "          FROM MW_pymt_sched_dtl  psd\n" +
            "               JOIN MW_pymt_sched_hdr psh\n" +
            "                   ON     psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ\n" +
            "                      AND psh.CRNT_REC_FLG = 1\n" +
            "               JOIN mw_loan_app la\n" +
            "                   ON la.LOAN_APP_SEQ = psh.LOAN_APP_SEQ AND la.CRNT_REC_FLG = 1\n" +
            "               JOIN MW_RCVRY_TRX RT ON TO_NUMBER(RT.PYMT_REF) = LA.CLNT_SEQ AND RT.CRNT_REC_FLG = 1\n" +
            "               JOIN MW_RCVRY_DTL RD ON RD.RCVRY_TRX_SEQ = RT.RCVRY_TRX_SEQ AND RD.PYMT_SCHED_DTL_SEQ = PSD.PYMT_SCHED_DTL_SEQ\n" +
            "                   AND RD.CRNT_REC_FLG = RT.CRNT_REC_FLG AND RCVRY_TYP_SEQ not in (454) \n" +
            "         WHERE la.CLNT_SEQ = :clntSeq and psd.pymt_sts_key not in (945,1500)\n" +
            "               AND la.prnt_loan_app_seq = :prntLoanAppSeq\n" +
            "               AND TO_DATE (psd.DUE_DT) >= TO_DATE ( :date, 'dd-mm-yyyy')\n" +
            "               order by 4", nativeQuery = true)
    List<PaymentScheduleDetail> getAdvancedPaid(@Param("clntSeq") long clntSeq, @Param("prntLoanAppSeq") long prntLoanAppSeq, @Param("date") String date);

    @Query(value = "select distinct psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due, psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_rcvry_trx mrt\r\n" + "join  mw_rcvry_dtl mrd on mrd.rcvry_trx_seq = mrt.rcvry_trx_seq\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = mrd.pymt_sched_dtl_seq\r\n"
            + "where mrt.rcvry_trx_seq =:rcvryTrxSeq order by 1", nativeQuery = true)
    List<PaymentScheduleDetail> getPaidInstallmentByTrxSeq(@Param("rcvryTrxSeq") long rcvryTrxSeq);

    List<PaymentScheduleDetail> findOneByPaySchedDtlSeqInAndCrntRecFlg(List<Long> paySchedDtlSeqs, boolean flg);

    @Query(value = "select psd.PYMT_SCHED_DTL_SEQ, psd.EFF_START_DT, psd.PYMT_SCHED_HDR_SEQ, psd.INST_NUM, psd.DUE_DT, psd.PPAL_AMT_DUE,  psd.TOT_CHRG_DUE+NVL(sum(psc.AMT),0) TOT_CHRG_DUE, psd.CRTD_BY, psd.CRTD_DT, psd.LAST_UPD_BY, psd.LAST_UPD_DT, psd.DEL_FLG, psd.EFF_END_DT, psd.CRNT_REC_FLG, psd.PYMT_STS_KEY, psd.SYNC_FLG\r\n"
            + "from mw_pymt_sched_dtl psd \r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1 \r\n"
            + "left outer join mw_pymt_sched_chrg psc on psc.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ and psc.CRNT_REC_FLG = 1\r\n"
            + "where psh.LOAN_APP_SEQ=? and psd.crnt_rec_flg=1\r\n"
            + "group by psd.PYMT_SCHED_DTL_SEQ, psd.EFF_START_DT, psd.PYMT_SCHED_HDR_SEQ, psd.INST_NUM, psd.DUE_DT, psd.PPAL_AMT_DUE, psd.TOT_CHRG_DUE, psd.CRTD_BY, psd.CRTD_DT, psd.LAST_UPD_BY, psd.LAST_UPD_DT, psd.DEL_FLG, psd.EFF_END_DT, psd.CRNT_REC_FLG, psd.PYMT_STS_KEY, psd.SYNC_FLG \r\n"
            + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getAllInstallmentsWithTotalDueAmount(long loanAppSeq);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd='0945'\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179'\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1\r\n"
            + "where app.loan_app_seq=? and psd.crnt_rec_flg=1 \r\n" + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getDuaInstallmentsByLoanAppSeq(long loanAppSeq);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd in ('0945', '1145')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179'\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1\r\n"
            + "where app.loan_app_seq=? and psd.crnt_rec_flg=1 \r\n" + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getDuaAndPartialInstallmentsByLoanAppSeq(long loanAppSeq);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd in ('1145')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179'\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1\r\n"
            + "where app.loan_app_seq=? and psd.crnt_rec_flg=1 \r\n" + "order by psd.inst_num", nativeQuery = true)
    List<PaymentScheduleDetail> getPartialInstallmentsByLoanAppSeq(long loanAppSeq);

    @Query(value = "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1\r\n"
            + "join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1\r\n"
            + "where app.loan_app_seq=:loanAppSeq and psd.crnt_rec_flg=1 and  psd.inst_num = :instNum", nativeQuery = true)
    PaymentScheduleDetail getInstByLoanAppSeqAndInstNum(@Param("loanAppSeq") long loanAppSeq, @Param("instNum") long instNum);

    @Query(value = "select dtl.* from mw_pymt_sched_dtl dtl \r\n"
            + "join mw_pymt_sched_hdr hdr on hdr.pymt_sched_hdr_seq = dtl.pymt_sched_hdr_seq and hdr.crnt_rec_flg=1\r\n"
            + "where dtl.crnt_rec_flg=1 and dtl.due_dt between to_date('01-04-2020', 'DD-MM-YYYY') and to_date('30-04-2020', 'DD-MM-YYYY')  and hdr.loan_app_seq=:loanAppSeq", nativeQuery = true)
    PaymentScheduleDetail getDtlWithInstDueInApril(@Param("loanAppSeq") long loanAppSeq);

    @Query(value = "select psd.* from mw_pymt_sched_dtl psd\r\n"
            + "join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
            + "where psd.crnt_rec_flg=1 and psh.loan_app_seq=:loanAppSeq and trunc(psd.due_dt)>=to_date(:dateStr)", nativeQuery = true)
    List<PaymentScheduleDetail> getDtlWithInstDueInDate(@Param("loanAppSeq") long loanAppSeq, @Param("dateStr") String dateStr);

    @Query(value = "SELECT *\r\n" + "  FROM (SELECT psd.*,\r\n" + "               row_number() OVER (ORDER BY psd.inst_num) rn\r\n"
            + "          FROM mw_pymt_sched_dtl psd \r\n"
            + "          join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1                    \r\n"
            + "                                join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1                    \r\n"
            + "                                where app.loan_app_seq=:loanAppSeq and psd.crnt_rec_flg=1\r\n" + "          )\r\n"
            + " WHERE rn = :instNum", nativeQuery = true)
    PaymentScheduleDetail getInstByLoanAppSeqAndInstNumUsingRowIndex(@Param("loanAppSeq") long loanAppSeq,
                                                                     @Param("instNum") long instNum);

    @Query(value = "select pymt_sched_dtl_seq, eff_start_dt, pymt_sched_hdr_seq, inst_num, due_dt, ppal_amt_due, tot_chrg_due, crtd_by, crtd_dt, last_upd_by, last_upd_dt, del_flg, eff_end_dt, crnt_rec_flg, pymt_sts_key, sync_flg \r\n"
            + "from (\r\n"
            + "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg \r\n"
            + ", (nvl(psd.ppal_amt_due,0) + nvl(psd.tot_chrg_due,0) + nvl(sum(psc.amt),0))  - nvl((select sum(pymt_amt)                      \r\n"
            + "                                from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and crnt_rec_flg=1),0) as remaingAmount\r\n"
            + "                                from mw_pymt_sched_dtl psd                    \r\n"
            + "                                join mw_ref_cd_val rcv on rcv.ref_cd_seq=psd.pymt_sts_key and rcv.crnt_rec_flg=1 and ref_cd in ( '1145')                    \r\n"
            + "                                join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0179'                    \r\n"
            + "                                join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1                    \r\n"
            + "                                join mw_loan_app app on app.loan_app_seq = psh.loan_app_seq and app.crnt_rec_flg = 1    \r\n"
            + "                                left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
            + "                                where app.loan_app_seq=:loanAppSeq and psd.crnt_rec_flg=1 \r\n"
            + "                                group by psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg\r\n"
            + "                                order by psd.inst_num\r\n" + "                                )\r\n"
            + "                                where remaingAmount = 0", nativeQuery = true)
    List<PaymentScheduleDetail> getInstallmentsWithPartialAndZeroOutStandingAmt(@Param("loanAppSeq") long loanAppSeq);

    @Query(value = "select pymt_sched_dtl_seq, eff_start_dt, pymt_sched_hdr_seq, inst_num, due_dt, ppal_amt_due, tot_chrg_due, crtd_by, crtd_dt, last_upd_by, last_upd_dt, del_flg, eff_end_dt, crnt_rec_flg, pymt_sts_key, sync_flg                     \r\n"
            + "from (                    \r\n"
            + "select psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg                     \r\n"
            + ", (nvl(psd.ppal_amt_due,0) + nvl(psd.tot_chrg_due,0) + nvl(sum(psc.amt),0))  - nvl((select sum(pymt_amt)                                          \r\n"
            + " from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and crnt_rec_flg=1),0) as remaingAmount          \r\n"
            + " ,(nvl(psd.ppal_amt_due,0) + nvl(psd.tot_chrg_due,0) + nvl(sum(psc.amt),0)) as amt\r\n"
            + " from mw_pymt_sched_dtl psd                        \r\n"
            + " left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1                     \r\n"
            + " where psd.crnt_rec_flg=1 and psd.pymt_sched_dtl_seq=:pymtSchedDtlSeq           \r\n"
            + " group by psd.pymt_sched_dtl_seq, psd.eff_start_dt, psd.pymt_sched_hdr_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,psd.crtd_by, psd.crtd_dt, psd.last_upd_by, psd.last_upd_dt, psd.del_flg, psd.eff_end_dt, psd.crnt_rec_flg, psd.pymt_sts_key, psd.sync_flg                    \r\n"
            + " order by psd.inst_num          )                   \r\n" + " where remaingAmount = amt", nativeQuery = true)
    PaymentScheduleDetail getDtlIfComopletelyUnpaid(@Param("pymtSchedDtlSeq") long pymtSchedDtlSeq);

    // Added by Zohaib Asim - Dated 27/01/2021
    // CR: IN CASE PAYMENT DATE IS BEFORE DEATH DATE
    // Modified by Areeba - SCR - Disability Recoveries
    // Modified by Yousaf - KMWK advance recovery handling
    @Query(value = "SELECT DISTINCT PSD.*\n" +
            "        FROM MW_RCVRY_TRX RT \n" +
            "        JOIN MW_RCVRY_DTL RD ON RD.RCVRY_TRX_SEQ = RT.RCVRY_TRX_SEQ\n" +
            "            AND RD.CRNT_REC_FLG = RT.CRNT_REC_FLG AND RCVRY_TYP_SEQ not in (454)\n" +
            "        JOIN MW_PYMT_SCHED_DTL PSD ON PSD.PYMT_SCHED_DTL_SEQ = RD.PYMT_SCHED_DTL_SEQ\n" +
            "            AND PSD.CRNT_REC_FLG = RD.CRNT_REC_FLG\n" +
            "        WHERE RT.CRNT_REC_FLG = 1 AND RT.PYMT_REF = :clntSeq\n" +
            "            AND PSD.PYMT_SCHED_DTL_SEQ = :pymtSchedDtlSeq\n" +
            "            AND RT.PYMT_DT > TO_DATE(:incdntDate, 'dd-MM-yyyy')", nativeQuery = true)
    List<PaymentScheduleDetail> getAdvncPymntsAftrIncdnt(@Param("clntSeq") long clntSeq,
                                                         @Param("pymtSchedDtlSeq") long pymtSchedDtlSeq,
                                                         @Param("incdntDate") String date);

    // Added by Zohaib Asim - Dated 19/02/2021
    // CR: IN CASE PARITIAL PAYMENT HAS BEEN MADE FOR SPECIFIC RECOVERY
    @Query(value = "SELECT DISTINCT PSD.*\n" +
            "  FROM MW_RCVRY_TRX  RT\n" +
            "       JOIN MW_RCVRY_DTL RD\n" +
            "           ON     RD.RCVRY_TRX_SEQ = RT.RCVRY_TRX_SEQ\n" +
            "              AND RD.CRNT_REC_FLG = RT.CRNT_REC_FLG\n" +
            "       JOIN MW_PYMT_SCHED_DTL PSD\n" +
            "           ON     PSD.PYMT_SCHED_DTL_SEQ = RD.PYMT_SCHED_DTL_SEQ\n" +
            "              AND PSD.CRNT_REC_FLG = RD.CRNT_REC_FLG\n" +
            " WHERE     RT.CRNT_REC_FLG = 1\n" +
            "       AND RT.PYMT_REF = :clntSeq\n" +
            "       AND PSD.PYMT_SCHED_DTL_SEQ = :pymtSchedDtlSeq\n" +
            "       AND RT.PYMT_DT <= TO_DATE ( :dthDate, 'dd-MM-yyyy')", nativeQuery = true)
    List<PaymentScheduleDetail> getPartialPymntsBfDth(@Param("clntSeq") long clntSeq,
                                                      @Param("pymtSchedDtlSeq") long pymtSchedDtlSeq,
                                                      @Param("dthDate") String date);

    // Added by Zohaib Asim - Dated 16-03-2021
    // Production Issue: Report(Clickable) error while Loan is in Submitted Status
    @Query(value = "SELECT COUNT(1)\n" +
            "FROM MW_PYMT_SCHED_HDR PSH\n" +
            "JOIN MW_PYMT_SCHED_DTL PSD ON PSD.PYMT_SCHED_HDR_SEQ = PSH.PYMT_SCHED_HDR_SEQ\n" +
            "    AND PSD.CRNT_REC_FLG = 1\n" +
            "WHERE PSH.LOAN_APP_SEQ = :loanAppSeq\n" +
            "    AND PSH.CRNT_REC_FLG = 1", nativeQuery = true)
    List getPymtSchedDtlByLoanAppSeq(@Param("loanAppSeq") long loanAppSeq);
    // End by Zohaib Asim
}
