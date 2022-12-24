package com.idev4.rds.repository;

import com.idev4.rds.domain.MwRcvryDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MwRcvryDtlRepository extends JpaRepository<MwRcvryDtl, Long> {

    /* @Query ( value = "select psd.pymt_sched_dtl_seq,c.clnt_seq,c.frst_nm,c.last_nm,la.loan_id,psd.inst_num,psd.ppal_amt_due,sum(psc.amt) amt,rt.instr_num,rt.pymt_dt,rcvr.typ_str rcvr,sts.typ_str sts,\r\n"
            + "r.pymt_amt,b.brnch_nm\r\n" + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\r\n"
            + "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1\r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
            + "left outer join mw_rcvry_dtl r on r.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and r.crnt_rec_flg=1\r\n"
            + "left outer join mw_rcvry_trx rt on rt.rcvry_trx_seq=r.rcvry_trx_seq\r\n"
            + "left outer join mw_typs  rcvr on rt.rcvry_typ_seq=rcvr.typ_seq\r\n"
            + "left outer join mw_typs  sts on rt.pymt_sts_key=sts.typ_seq\r\n"
            + "join mw_port p on la.port_seq=p.port_seq and p.crnt_rec_flg=1\r\n"
            + "join mw_brnch b on b.brnch_seq=p.brnch_seq and b.crnt_rec_flg=1\r\n" + "where psd.due_dt<=last_day(sysdate)\r\n"
            + "group by psd.pymt_sched_dtl_seq,c.clnt_seq,c.frst_nm,c.last_nm,la.loan_id,psd.inst_num,psd.ppal_amt_due,rt.instr_num,rt.pymt_dt,rcvr.typ_str,sts.typ_str,r.pymt_amt,brnch_nm\r\n"
            + "order by inst_num", nativeQuery = true )*/
    @Query(value = "select pymt_sched_dtl_seq,clnt_seq,frst_nm,last_nm,loan_id,inst_num,ppal_amt_due,amt,instrnum,pymtdt,rcvr,sts,\r\n"
            + "sum (pymtamt) pymtamt,brnch_nm \r\n" + "from (\r\n"
            + "select psd.pymt_sched_dtl_seq,c.clnt_seq,c.frst_nm,c.last_nm,la.loan_id,psd.inst_num,psd.ppal_amt_due,sum(psc.amt) amt,rt.instr_num as instrnum,rt.pymt_dt pymtdt,rcvr.typ_str rcvr,sts.typ_str sts,\r\n"
            + "r.pymt_amt as pymtamt,b.brnch_nm\r\n" + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\r\n"
            + "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1\r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
            + "left outer join mw_rcvry_dtl r on r.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and r.crnt_rec_flg=1\r\n"
            + "left outer join mw_rcvry_trx rt on rt.rcvry_trx_seq=r.rcvry_trx_seq\r\n"
            + "left outer join mw_typs  rcvr on rt.rcvry_typ_seq=rcvr.typ_seq\r\n"
            + "left outer join mw_typs  sts on rt.pymt_sts_key=sts.typ_seq\r\n"
            + "join mw_port p on la.port_seq=p.port_seq and p.crnt_rec_flg=1\r\n"
            + "join mw_brnch b on b.brnch_seq=p.brnch_seq and b.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val val on val.REF_CD_SEQ=la.LOAN_APP_STS and val.CRNT_REC_FLG=1 and val.ref_cd = '5'\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.CRNT_REC_FLG=1 and grp.ref_cd_grp = '0106'\r\n"
            + "where psd.due_dt<=last_day(sysdate)\r\n"
            + "group by psd.pymt_sched_dtl_seq,c.clnt_seq,c.frst_nm,c.last_nm,la.loan_id,psd.inst_num,psd.ppal_amt_due,rt.instr_num,rt.pymt_dt,rcvr.typ_str,sts.typ_str,r.pymt_amt,brnch_nm\r\n"
            + "order by inst_num\r\n" + ")a  \r\n"
            + "group by pymt_sched_dtl_seq,clnt_seq,frst_nm,last_nm,loan_id,inst_num,ppal_amt_due,amt,instrnum,pymtdt,rcvr,sts,brnch_nm", nativeQuery = true)
    List<Map<String, ?>> getAllRecovery();

    @Query(value = "select la.prd_seq,psd.pymt_sched_hdr_seq,la.loan_app_seq,psd.pymt_sched_dtl_seq,psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) as amt,  \r\n"
            + "(select sum(pymt_amt)  \r\n"
            + "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and crnt_rec_flg=1) as reco,psd.DUE_DT, \r\n"
            + "max(inst_num) over (partition by la.loan_app_seq) last_inst,t.TYP_SEQ  \r\n" + "from mw_loan_app la  \r\n"
            + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1  \r\n"
            + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1  \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and (val.ref_cd = '0005' or val.ref_cd ='1245')  \r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'  \r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
            + "left outer join  mw_loan_app_chrg_stngs stng  on la.LOAN_APP_SEQ = stng.LOAN_APP_SEQ \r\n"
            + "join mw_typs t on t.TYP_SEQ=stng.TYP_SEQ and t.CRNT_REC_FLG=1 and t.TYP_ID='0017'\r\n"
            + "where la.clnt_seq=? and la.crnt_rec_flg=1  \r\n"
            + "group by la.prd_seq,psd.pymt_sched_hdr_seq,la.loan_app_seq,psd.pymt_sched_dtl_seq,psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,psc.pymt_sched_dtl_seq,psd.DUE_DT,t.TYP_SEQ  \r\n"
            + "order by psd.due_dt,la.prd_seq", nativeQuery = true)
    List<Object[]> getDuaInstallment(Long clntSeq);

    @Query(value = "SELECT LA.CLNT_SEQ\r\n" + "FROM MW_LOAN_APP LA,MW_PYMT_SCHED_HDR PSH, MW_PYMT_SCHED_DTL PSD\r\n"
            + "WHERE LA.LOAN_APP_SEQ=PSH.LOAN_APP_SEQ\r\n" + "AND PSH.PYMT_SCHED_HDR_SEQ=PSD.PYMT_SCHED_HDR_SEQ \r\n"
            + "AND PSD.PYMT_SCHED_DTL_SEQ=?", nativeQuery = true)
    Map<String, ?> getClntSeqByPaySchedDtlSeq(Long paySchedDtlSeq);

    List<MwRcvryDtl> findAllByPaySchedDtlSeqAndCrntRecFlg(Long paySchedDtlSeq, boolean crntRecFlg);

    List<MwRcvryDtl> findAllByRcvryTrxSeqAndCrntRecFlg(long rcvryTrxSeq, boolean crntRecFlg);

    @Query(value = "select inst_num,sum(ppal_amt_due) ppal_amt_due,sum(tot_chrg_due) tot_chrg_due,sum(amt),sum(reco) reco,due_dt\r\n"
            + " from(\r\n"
            + "select psd.inst_num,sum(psd.ppal_amt_due) ppal_amt_due,sum(psd.tot_chrg_due) tot_chrg_due ,sum(psc.amt) as amt, \r\n"
            + "(select sum(pymt_amt) \r\n"
            + "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and crnt_rec_flg=1) as reco,psd.due_dt  \r\n"
            + "from mw_loan_app la \r\n" + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and val.ref_cd ='0005'  \r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'  \r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
            + "where la.clnt_seq=? and la.crnt_rec_flg=1 \r\n" + "group by inst_num,psd.pymt_sched_dtl_seq,psd.due_dt  \r\n"
            + "order by inst_num)\r\n" + "group by inst_num,due_dt  \r\n" + "order by inst_num", nativeQuery = true)
    List<Object[]> getDueInstForAdc(Long clntSeq);

    @Query(value = "select loan_app_seq,sum(amt),sum(reco),prd_seq \r\n" + "from (\r\n"
            + "select la.prd_seq,psd.pymt_sched_hdr_seq,la.loan_app_seq,psd.pymt_sched_dtl_seq,psd.inst_num,psd.ppal_amt_due+psd.tot_chrg_due+NVL(sum(psc.amt),0) as amt,  \r\n"
            + "(select sum(pymt_amt)  \r\n"
            + "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and crnt_rec_flg=1) as reco,psd.DUE_DT   \r\n"
            + "from mw_loan_app la  \r\n" + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1  \r\n"
            + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1  \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and (val.ref_cd = '0005' or val.ref_cd ='1245')    \r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'   \r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1  \r\n"
            + "where la.clnt_seq=? and la.crnt_rec_flg=1  \r\n"
            + "group by prd_seq,psd.pymt_sched_hdr_seq,la.loan_app_seq,psd.pymt_sched_dtl_seq,psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,psc.pymt_sched_dtl_seq,psd.DUE_DT   \r\n"
            + "order by inst_num\r\n" + ")group by loan_app_seq,prd_seq", nativeQuery = true)
    public List<Object[]> getDuaAndPymtTotalAmts(Long clntSeq);

    @Query(value = "SELECT DTL.*\r\n" + "FROM MW_RCVRY_DTL DTL\r\n"
            + "join MW_RCVRY_TRX TRX ON TRX.RCVRY_TRX_SEQ = DTL.RCVRY_TRX_SEQ AND TRX.CRNT_REC_FLG = 1 \r\n"
            + "where TRX.PYMT_REF=? AND DTL.CRTD_DT>=(select MAX(DTH.CRTD_DT) from MW_DTH_RPT DTH where DTH.CLNT_SEQ=TRX.PYMT_REF AND DTH.CRNT_REC_FLG = 1 AND DTH.DEL_FLG = 0 ) \r\n"
            + "AND DTL.PYMT_SCHED_DTL_SEQ= -1 AND DTL.CRNT_REC_FLG = 1 order by dtl.rcvry_trx_seq", nativeQuery = true)
    public List<MwRcvryDtl> getExcessRecoveryToReverse(String clntSeq);

    // Added by Zohaib Asim - Dated 18-02-2021
    // Function Overloaded
    // In case of Excess Recoveries Reversal and Client Death has been marked
    @Query(value = "SELECT DTL.* FROM MW_RCVRY_DTL DTL\n" +
            "JOIN MW_RCVRY_TRX TRX ON TRX.RCVRY_TRX_SEQ = DTL.RCVRY_TRX_SEQ AND TRX.CRNT_REC_FLG = 1 \n" +
            "WHERE TRX.PYMT_REF=:CLNTSEQ AND \n" +
            "DTL.CRTD_DT>=(SELECT MAX(DTH.CRTD_DT) FROM MW_DTH_RPT DTH WHERE DTH.CLNT_SEQ=TRX.PYMT_REF \n" +
            " AND DTH.CRNT_REC_FLG = 0 AND DTH.DEL_FLG = 1 ) \n" +
            "AND TO_DATE(SYSDATE) = (SELECT TO_DATE(MAX (DTH.LAST_UPD_DT)) FROM MW_DTH_RPT DTH WHERE DTH.CLNT_SEQ=TRX.PYMT_REF \n" +
            " AND DTH.CRNT_REC_FLG = 0 AND DTH.DEL_FLG = 1 ) \n" +
            "AND DTL.PYMT_SCHED_DTL_SEQ= -1 AND DTL.CRNT_REC_FLG = 1 ORDER BY DTL.RCVRY_TRX_SEQ", nativeQuery = true)
    public List<MwRcvryDtl> getXSRcvryToRvrse(@Param("CLNTSEQ") String clntSeq);

    // For Reversal: Find all transaction took place after client death
    // Modified by Areeba - SCR - Disability Recoveries
    // Modified by Yousaf - SCR - KMWK loan Adjustment
    @Query(value = "SELECT DISTINCT RD.* \n" +
            "FROM MW_RCVRY_TRX RT\n" +
            "JOIN MW_RCVRY_DTL RD ON RD.RCVRY_TRX_SEQ = RT.RCVRY_TRX_SEQ\n" +
            "    AND RD.CRNT_REC_FLG = RT.CRNT_REC_FLG\n" +
            "JOIN MW_PYMT_SCHED_DTL PSD\n" +
            "           ON     PSD.PYMT_SCHED_DTL_SEQ = RD.PYMT_SCHED_DTL_SEQ\n" +
            "              AND PSD.CRNT_REC_FLG = RD.CRNT_REC_FLG\n" +
            "WHERE RT.CRNT_REC_FLG = 1 AND RT.PYMT_REF =:pymtRef AND RCVRY_TYP_SEQ not in (454)\n" +
            "    AND PSD.DUE_DT >= TO_DATE(:incdntDate, 'dd-MM-yyyy')\n" +
            "    AND RT.PYMT_DT >= TO_DATE(:incdntDate, 'dd-MM-yyyy')", nativeQuery = true)
    public List<MwRcvryDtl> findMwRcvryTrxByPymtRefAndPymtDtAfter(@Param("pymtRef") String pymtRef,
                                                                  @Param("incdntDate") String pymtDt);
    // End by Zohaib Asim

    // Added by Areeba
    // In case of Excess Recoveries Reversal and Client Disability has been marked
    @Query(value = "SELECT DTL.* FROM MW_RCVRY_DTL DTL\n" +
            "JOIN MW_RCVRY_TRX TRX ON TRX.RCVRY_TRX_SEQ = DTL.RCVRY_TRX_SEQ AND TRX.CRNT_REC_FLG = 1 \n" +
            "WHERE TRX.PYMT_REF=:CLNTSEQ AND \n" +
            "DTL.CRTD_DT>=(SELECT MAX(DSB.CRTD_DT) FROM MW_DSBLTY_RPT DSB WHERE DSB.CLNT_SEQ=TRX.PYMT_REF \n" +
            " AND DSB.CRNT_REC_FLG = 0 AND DSB.DEL_FLG = 1 ) \n" +
            "AND TO_DATE(SYSDATE) = (SELECT TO_DATE(MAX (DSB.LAST_UPD_DT)) FROM MW_DSBLTY_RPT DSB WHERE DSB.CLNT_SEQ=TRX.PYMT_REF \n" +
            " AND DSB.CRNT_REC_FLG = 0 AND DSB.DEL_FLG = 1 ) \n" +
            "AND DTL.PYMT_SCHED_DTL_SEQ= -1 AND DTL.CRNT_REC_FLG = 1 ORDER BY DTL.RCVRY_TRX_SEQ", nativeQuery = true)
    public List<MwRcvryDtl> getXSRcvryToRvrseForDsblty(@Param("CLNTSEQ") String clntSeq);
    //Ended by Areeba
}
