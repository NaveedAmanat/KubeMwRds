package com.idev4.rds.repository;

import com.idev4.rds.domain.DisbursementVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DisbursementVoucherHeaderRepository extends JpaRepository<DisbursementVoucherHeader, Long> {

    @Query(value = "SELECT DVH.DSBMT_HDR_SEQ, DVH.DSBMT_ID, DVH.DSBMT_DT, DVH.LOAN_APP_SEQ, DVH.DSBMT_STS_KEY,DVD.DSBMT_DTL_KEY, DVD.INSTR_NUM, DVD.AMT, DVD.PYMT_TYPS_SEQ\r\n"
            + ",TYP_SEQ, TYP_ID, TYP_STR, GL_ACCT_NUM, TYP_STS_KEY, TYP_CTGRY_KEY\r\n"
            + "FROM MW_DSBMT_VCHR_HDR DVH,MW_DSBMT_VCHR_DTL DVD,MW_TYPS T\r\n"
            + "WHERE DVH.DSBMT_HDR_SEQ=DVD.DSBMT_HDR_SEQ(+) AND DVD.PYMT_TYPS_SEQ=T.TYP_SEQ\r\n"
            + "AND DVH.LOAN_APP_SEQ=1 AND DVH.DSBMT_VCHR_TYP=2", nativeQuery = true)
    List<Map<String, ?>> getDisbursementVoucherByLoanAppSeq(long paySchedChrgSeq);

    @Query(value = "SELECT DVH.DSBMT_HDR_SEQ, DVH.DSBMT_ID, DVH.DSBMT_DT, DVH.LOAN_APP_SEQ, DVH.DSBMT_STS_KEY,DVD.DSBMT_DTL_KEY, DVD.INSTR_NUM, DVD.AMT, DVD.PYMT_TYPS_SEQ\r\n"
            + ",TYP_SEQ, TYP_ID, TYP_STR, GL_ACCT_NUM, TYP_STS_KEY, TYP_CTGRY_KEY\r\n"
            + "FROM MW_DSBMT_VCHR_HDR DVH,MW_DSBMT_VCHR_DTL DVD,MW_TYPS T\r\n"
            + "WHERE DVH.DSBMT_HDR_SEQ=DVD.DSBMT_HDR_SEQ(+) AND DVD.PYMT_TYPS_SEQ=T.TYP_SEQ\r\n"
            + "AND DVH.LOAN_APP_SEQ=1 AND DVH.DSBMT_VCHR_TYP=1", nativeQuery = true)
    List<Map<String, ?>> getAgencyByLoanAppSeq(long paySchedChrgSeq);

    public DisbursementVoucherHeader findOneByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flag);

    public DisbursementVoucherHeader findOneByDsbmtHdrSeqAndCrntRecFlg(long dsbmtHdrSeq, boolean flag);

    public List<DisbursementVoucherHeader> findAllByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flag);

    public DisbursementVoucherHeader findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(long loanAppSeq, boolean flag, int type);

    public List<DisbursementVoucherHeader> findAllByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTypOrderByCrtdDtDesc(long loanAppSeq, boolean flag, int type);

    @Query(value = "select dvd.amt,t.typ_str,t.typ_id,t.gl_acct_num,t.typ_seq from  mw_dsbmt_vchr_dtl dvd\r\n"
            + "join mw_typs t on t.typ_seq=dvd.pymt_typs_seq and t.crnt_rec_flg=1\r\n"
            + "join mw_dsbmt_vchr_hdr dvh on dvh.dsbmt_hdr_seq=dvd.dsbmt_hdr_seq and dvh.crnt_rec_flg=1\r\n"
            + "where dvd.crnt_rec_flg=1 and dvh.loan_app_seq=:loanAppSeq and dvh.dsbmt_vchr_typ=:type", nativeQuery = true)
    List<Map<String, ?>> getVouchersbyLoanAppAndType(@Param("loanAppSeq") long loanAppSeq, @Param("type") long type);

    @Query(value = "select hdr.*,app.loan_app_sts,app.LOAN_APP_SEQ \r\n" + "from mw_dsbmt_vchr_hdr hdr \r\n"
            + "join mw_loan_app app on app.loan_app_seq = hdr.loan_app_seq and app.crnt_rec_flg=1 and app.loan_app_seq=app.prnt_loan_app_seq\r\n"
            + "join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1\r\n"
            + "join mw_prd p on p.prd_seq=app.prd_seq and p.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val val on val.REF_CD_SEQ=p.PRD_TYP_KEY and val.CRNT_REC_FLG=1 and val.ref_cd !='1165'\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.CRNT_REC_FLG=1 and grp.ref_cd_grp = '0161'   \r\n"
            + "where clnt.clnt_seq=:clientId and hdr.crnt_rec_flg=1 and hdr.DSBMT_VCHR_TYP=0 \r\n"
            + "and app.LOAN_CYCL_NUM=(select max(LOAN_CYCL_NUM) from mw_loan_app where crnt_rec_flg=1 and clnt_seq=:clientId)", nativeQuery = true)
    public DisbursementVoucherHeader getDisbursementVoucherHeaderForClientsActiveLoan(@Param("clientId") long clientId);

    @Query(value = "select dhdr.* from mw_dsbmt_vchr_hdr dhdr \r\n"
            + "where dhdr.crnt_rec_flg=1 and dhdr.dsbmt_dt<to_date('01-04-2020', 'DD-MM-YYYY') and dhdr.loan_app_seq=:loanAppSeq", nativeQuery = true)
    public DisbursementVoucherHeader getHdrBeforeAprilForResheduleCriteria(@Param("loanAppSeq") long loanAppSeq);


    // Added by Zohaib Asim - Dated 06-05-2022 - MCB Disbursement
    @Query(value = "SELECT CASE\n" +
            "           WHEN RCV_DSBMT_STS.REF_CD_DSCR = 'PROCESSED'\n" +
            "           THEN\n" +
            "               CASE\n" +
            "                   WHEN RCV_ADC_STS.REF_CD_DSCR = 'PAID' THEN 'Y'\n" +
            "                   ELSE 'N'\n" +
            "               END\n" +
            "           WHEN RCV_DSBMT_STS.REF_CD_DSCR = 'IN PROCESS'\n" +
            "           THEN\n" +
            "               'Y'\n" +
            "           ELSE\n" +
            "               'N'\n" +
            "       END\n" +
            "           CAN_PROCEED\n" +
            "  FROM MW_DSBMT_VCHR_HDR  DVH\n" +
            "       JOIN MW_DSBMT_VCHR_DTL DVD\n" +
            "           ON DVD.DSBMT_HDR_SEQ = DVH.DSBMT_HDR_SEQ AND DVD.CRNT_REC_FLG = 1\n" +
            "       JOIN MW_ADC_DSBMT_QUE ADQ\n" +
            "           ON     ADQ.DSBMT_HDR_SEQ = DVH.DSBMT_HDR_SEQ\n" +
            "              AND ADQ.DSBMT_DTL_KEY = DVD.DSBMT_DTL_KEY\n" +
            "              AND ADQ.CRNT_REC_FLG = 1\n" +
            "       -- DISBURSEMENT STATUS\n" +
            "       JOIN MW_REF_CD_VAL RCV_DSBMT_STS\n" +
            "           ON     RCV_DSBMT_STS.REF_CD_SEQ = ADQ.DSBMT_STS_SEQ\n" +
            "              AND RCV_DSBMT_STS.CRNT_REC_FLG = 1\n" +
            "       JOIN MW_REF_CD_GRP RCG_DSBMT_STS\n" +
            "           ON     RCG_DSBMT_STS.REF_CD_GRP_SEQ = RCV_DSBMT_STS.REF_CD_GRP_KEY\n" +
            "              AND RCG_DSBMT_STS.CRNT_REC_FLG = 1\n" +
            "              AND RCG_DSBMT_STS.REF_CD_GRP = '0040'\n" +
            "       -- ADC STATUS\n" +
            "       JOIN MW_REF_CD_VAL RCV_ADC_STS\n" +
            "           ON     RCV_ADC_STS.REF_CD_SEQ = ADQ.ADC_STS_SEQ\n" +
            "              AND RCV_ADC_STS.CRNT_REC_FLG = 1\n" +
            "       JOIN MW_REF_CD_GRP RCG_DSBMT_STS\n" +
            "           ON     RCG_DSBMT_STS.REF_CD_GRP_SEQ = RCV_ADC_STS.REF_CD_GRP_KEY\n" +
            "              AND RCG_DSBMT_STS.CRNT_REC_FLG = 1\n" +
            "              AND RCG_DSBMT_STS.REF_CD_GRP = '0040'\n" +
            " WHERE DVH.CRNT_REC_FLG = 1 AND DVH.LOAN_APP_SEQ = :loanAppSeq", nativeQuery = true)
    public String getADCProceedingSts(@Param("loanAppSeq") long loanAppSeq);
    // End

}
