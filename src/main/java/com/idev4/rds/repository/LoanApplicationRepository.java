package com.idev4.rds.repository;

import com.idev4.rds.domain.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    @Query(value = "SELECT l.LOAN_APP_SEQ, l.LOAN_ID, l.RQSTD_LOAN_AMT, l.APRVD_LOAN_AMT, l.CLNT_SEQ , p.PRD_NM, l.RCMND_LOAN_AMT, (select ref_cd_dscr from mw_ref_cd_val where ref_cd_seq=l.loan_app_sts) as status, c.NATR_OF_DIS_KEY, c.CLNT_STS_KEY, c.RES_TYP_KEY, c.DIS_FLG, c.SLF_PDC_FLG, c.CRNT_ADDR_PERM_FLG, c.PH_NUM, c.FRST_NM, c.LAST_NM "
            + "FROM MW_loan_App l, " + "MW_clnt c, " + "MW_PRD p WHERE " + "l.CLNT_SEQ = c.CLNT_SEQ " + "AND c.crnt_rec_flg=1 "
            + "AND l.crnt_rec_flg = 1 " + "AND l.PRD_SEQ =p.PRD_SEQ " + "AND l.LOAN_APP_STS=455", nativeQuery = true)
    List<Map<String, ?>> getAllApplications();

    @Query(value = "SELECT LOAN_APP_SEQ,LOAN_ID,RQSTD_LOAN_AMT, APRVD_LOAN_AMT ,PR.PRD_ID,PO.PORT_NM, RCMND_LOAN_AMT,LOAN_APP_ID,C.CLNT_ID,C.FRST_NM,LAST_NM,B.BRNCH_NM,PR.PRD_NM "
            + "FROM MW_LOAN_APP L, MW_CLNT C ,MW_PORT PO,MW_BRNCH B,MW_PRD PR " + "WHERE L.CLNT_SEQ=C.CLNT_SEQ AND L.LOAN_APP_STS=455 "
            + "AND L.PORT_SEQ=PO.PORT_SEQ AND PO.BRNCH_SEQ=B.BRNCH_SEQ " + "AND L.PRD_SEQ=PR.PRD_SEQ "
            + "AND LOAN_APP_SEQ=?", nativeQuery = true)
    public Map<String, ?> findOneByLoanAppSeq(long loanAppSeq);

    public LoanApplication findByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flag);

    public List<LoanApplication> findAllByClntSeqAndLoanAppStsAndCrntRecFlg(long clntSeq, long loanAppSts, boolean flag);

    public List<LoanApplication> findAllByLoanAppSeqInAndLoanAppStsInAndCrntRecFlg(List<Long> loanSeq, List<Long> sts, boolean flg);

    public LoanApplication findOneByClntSeq(long seq);

    // Modified by Zohaib Asim - Dated 22-02-2021
    // Rejected, Discarded, Deferred will not be included
    @Query(value = "select *\r\n"
            + " from mw_loan_app where loan_app_sts not in (765, 1107, 1285) "
            + " and loan_app_seq=prnt_loan_app_seq and  clnt_seq = ? "
            + " and crnt_rec_flg = ? and loan_cycl_num = ? ", nativeQuery = true)
    public LoanApplication findOneByClntSeqAndCrntRecFlgAndLoanCyclNum(long seq, boolean flag, long loanCyclNum);

    @Query(value = "select distinct mla.*\r\n" + "from mw_rcvry_trx mrt\r\n"
            + "join  mw_rcvry_dtl mrd on mrd.rcvry_trx_seq = mrt.rcvry_trx_seq\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = mrd.pymt_sched_dtl_seq\r\n"
            + "join MW_PYMT_SCHED_HDR mpsh on mpsh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ\r\n"
            + "join mw_loan_app mla on mla.LOAN_APP_SEQ = mpsh.LOAN_APP_SEQ\r\n"
            + "where mrt.rcvry_trx_seq = :rcvryTrxSeq and mla.LOAN_APP_STS=:loanAppSts and mla.crnt_rec_flg=1 and mrd.crnt_rec_flg=1 and  psd.crnt_rec_flg=1 and  mpsh.crnt_rec_flg=1", nativeQuery = true)
    public List<LoanApplication> findAllPaidLoanAppByTrx(@Param("rcvryTrxSeq") long rcvryTrxSeq,
                                                         @Param("loanAppSts") long loanAppSts);

    public LoanApplication findTop1ByLoanAppSeqAndCrntRecFlgOrderByLastUpdDtDesc(long loanAppSeq, boolean flag);

    @Query(value = "select * from mw_loan_app ap\r\n" + "where ap.crnt_rec_flg=1\r\n" + "and ap.clnt_seq=:clntSeq\r\n"
            + "and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where clnt_seq=ap.clnt_seq and crnt_rec_flg=1)", nativeQuery = true)
    public List<LoanApplication> findMaxLoanCyclLoansForClient(@Param("clntSeq") long clntSeq);

    @Query(value = "select * from mw_loan_app ap\r\n" + "where ap.crnt_rec_flg=1\r\n" + "and ap.clnt_seq=:clntSeq\r\n"
            + "and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where clnt_seq=ap.clnt_seq and crnt_rec_flg=1) and ap.loan_app_seq!=ap.prnt_loan_app_seq", nativeQuery = true)
    public List<LoanApplication> findMaxLoanCyclAssocLoansForClient(@Param("clntSeq") long clntSeq);

    @Query(value = "select * from mw_loan_app ap\r\n" + "where ap.crnt_rec_flg=1\r\n" + "and ap.clnt_seq=:clntSeq\r\n"
            + "and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where clnt_seq=ap.clnt_seq and crnt_rec_flg=1) and ap.loan_app_seq!=ap.prnt_loan_app_seq and ap.loan_app_sts_dt>=to_date(:dthDt,'dd-mm-yyyy'", nativeQuery = true)
    public List<LoanApplication> findMaxLoanCyclAssocLoansForClientCompletedAfterDeathDt(@Param("clntSeq") long clntSeq,
                                                                                         @Param("dthDt") String dthDt);

    @Query(value = "select app.*\r\n" + "            from hist_mw_loan_app app\r\n"
            + "            where app.loan_app_seq=:loanAppSeq and app.loan_app_sts=:loanAppSts ", nativeQuery = true)
    public List<LoanApplication> findAllByLoanAppSeqAndLoanAppSts(@Param("loanAppSeq") long loanAppSeq, @Param("loanAppSts") long loanAppSts);

    @Query(value = "select app.*\r\n" + "            from mw_loan_app app\r\n"
            + "            join mw_ref_cd_val rcv on rcv.ref_cd_seq=app.loan_app_sts and rcv.crnt_rec_flg=1 and ref_cd='0005'\r\n"
            + "            join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "            where app.clnt_seq=:clntSeq and app.crnt_rec_flg=1 ", nativeQuery = true)
    public List<LoanApplication> findAllActiveLoansForClnt(@Param("clntSeq") long clntSeq);

    @Query(value = "select app.*\r\n" + "            from mw_loan_app app\r\n"
            + "            join mw_ref_cd_val rcv on rcv.ref_cd_seq=app.loan_app_sts and rcv.crnt_rec_flg=1 and ref_cd='0005'\r\n"
            + "            join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "            where app.clnt_seq=:clntSeq and app.crnt_rec_flg=1 and LOAN_APP_SEQ=PRNT_LOAN_APP_SEQ", nativeQuery = true)
    public LoanApplication findAllActiveLoanForClntBasicPrd(@Param("clntSeq") long clntSeq);

    @Query(value = "select app.*\r\n" + "            from mw_loan_app app\r\n"
            + "            join mw_ref_cd_val rcv on rcv.ref_cd_seq=app.loan_app_sts and rcv.crnt_rec_flg=1 and ref_cd='0005'\r\n"
            + "            join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = rcv.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "            where app.clnt_seq=:clntSeq and app.crnt_rec_flg=1 and LOAN_APP_SEQ<>PRNT_LOAN_APP_SEQ", nativeQuery = true)
    public LoanApplication findAllActiveLoanForClntAccosPrd(@Param("clntSeq") long clntSeq);

    public List<LoanApplication> findAllByPrntLoanAppSeqAndCrntRecFlg(long loanSeq, boolean flg);

    @Query(value = "select distinct mla.*\r\n" + "from mw_rcvry_trx mrt\r\n"
            + "join  mw_rcvry_dtl mrd on mrd.rcvry_trx_seq = mrt.rcvry_trx_seq\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = mrd.pymt_sched_dtl_seq\r\n"
            + "join MW_PYMT_SCHED_HDR mpsh on mpsh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ\r\n"
            + "join mw_loan_app mla on mla.LOAN_APP_SEQ = mpsh.LOAN_APP_SEQ\r\n"
            + "where mrt.rcvry_trx_seq = :rcvryTrxSeq and mla.crnt_rec_flg=1 and mrd.crnt_rec_flg=1 and  psd.crnt_rec_flg=1 and  mpsh.crnt_rec_flg=1", nativeQuery = true)
    public List<LoanApplication> findAllPaidLoanAppByTrx(@Param("rcvryTrxSeq") long rcvryTrxSeq);

    @Query(value = "select * from mw_loan_app where crnt_rec_flg=1 and loan_app_seq= (select prnt_loan_app_seq from mw_loan_app where loan_app_seq=:loanAppSeq and crnt_rec_flg=1)", nativeQuery = true)
    public LoanApplication findParentLoan(@Param("loanAppSeq") long loanAppSeq);


    /**
     * @Added, Naveed
     * @Date, 14-06-2022
     * @Description, find ParentLoanApplicationSeq against max loanCycle
     */

    @Query(value = "SELECT *\n" +
            "  FROM MW_LOAN_APP APP\n" +
            " WHERE     APP.CLNT_SEQ = :P_CLNT_SEQ\n" +
            "       AND APP.LOAN_APP_STS IN (703, 1245)\n" +
            "       AND APP.CRNT_REC_FLG = 1\n" +
            "       AND APP.LOAN_CYCL_NUM =\n" +
            "           (SELECT MAX (APP.LOAN_CYCL_NUM)\n" +
            "              FROM MW_LOAN_APP AP\n" +
            "             WHERE     AP.CLNT_SEQ = APP.CLNT_SEQ\n" +
            "                   AND AP.LOAN_APP_STS IN (703, 1245)\n" +
            "                   AND AP.CRNT_REC_FLG = 1)", nativeQuery = true)
    public List<LoanApplication> findMaxLoanCyclLoansForClientAndLoanAppSts(@Param("P_CLNT_SEQ") long clntSeq);
}
