package com.idev4.rds.service;

import com.idev4.rds.domain.*;
import com.idev4.rds.dto.Charges;
import com.idev4.rds.dto.RecoveryJvHostory;
import com.idev4.rds.repository.*;
import com.idev4.rds.util.SequenceFinder;
import com.idev4.rds.util.Sequences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class VchrPostingService {

    private static final String CREDIT = "Credit";
    private static final String DEBIT = "Debit";
    // debit flag
    private static final Long CREDIT_FLG = 0L;
    // credit flag
    private static final Long DEBIT_FLG = 1L;
    private static final String DISBURSEMENT = "Disbursement";
    private static final String Rescheduling = "Rescheduling";
    private static final String AccessRecovery = "Access Recovery";
    private static final String ISLAMIC_TYPE = "0002";
    private static final String APROVED_STATUS = "0004";
    private static final String ADVANCE_STATUS = "1305";
    private final Logger log = LoggerFactory.getLogger(VchrPostingService.class);
    @Autowired
    MwRcvryTrxRepository mwRcvryTrxRepository;
    @Autowired
    VchrPostingComponent vchrPostingComponent;
    @Autowired
    MwPrdAcctSetRepository mwPrdAcctSetRepository;
    @Autowired
    MwPrdRepository mwPrdRepository;
    @Autowired
    MwJvHdrRepository mwJvHdrRepository;
    @Autowired
    MwJvDtlRepository mwJvDtlRepository;
    @Autowired
    MwLoanAppChrgStngsRepository mwLoanAppChrgStngsRepository;
    @Autowired
    EntityManager em;
    @Autowired
    TypesRepository typesRepository;
    @Autowired
    Charges charges;
    @Autowired
    MwBrnchRepository mwBrnchRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    DisbursementVoucherHeaderRepository disbursementVoucherHeaderRepository;
    @Autowired
    MwExpRepository mwExpRepository;
    @Autowired
    MwHlthInsrPlanRepository mwHlthInsrPlanRepository;
    @Autowired
    MwPrdGrpRepository mwPrdGrpRepository;

    @Transactional
    public MwJvHdr jvPosting(long rcvryTrxSeq, String entyTyp, String desc, String currUser, List<Object[]> chrgOrder,
                             List<MwRcvryDtl> rcvryDtls, long rcvryTypsSeq, Date date, long brnch) {
        Types typ = typesRepository.findOneByTypSeqAndCrntRecFlg(rcvryTypsSeq, true);
        desc = desc.concat((typ == null) ? "" : typ.getTypStr());
        log.debug("JV Posting : {}", typ.getGlAcctNum());
        return genrateRecoveryJv(rcvryTrxSeq, entyTyp, desc, typ.getGlAcctNum(), currUser, chrgOrder, rcvryDtls, date, brnch);

    }

    @Transactional
    public MwJvHdr genrateRecoveryJv(Long rcvryTrxSeq, String entyTyp, String desc, String glAcctNum, String currUser,
                                     List<Object[]> chrgOrder, List<MwRcvryDtl> rcvryDtls, Date date, long brnch) {
        log.debug("Genrate Recovery : {}", entyTyp);
        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
        MwJvHdr mwJvHdr = new MwJvHdr();
        mwJvHdr.setJvHdrSeq(seq);
        mwJvHdr.setRcvryTrxSeq(rcvryTrxSeq);
        // mwJvHdr.setPrntVchrRef( prntVchrRef );
        mwJvHdr.setJvId(Long.toString(seq));
        mwJvHdr.setJvDt(date.toInstant());
        mwJvHdr.setJvDscr(desc != null ? desc : entyTyp);
        // mwJvHdr.setJvTypKey( jvTypKey );
        mwJvHdr.setEntySeq(rcvryTrxSeq);
        mwJvHdr.setEntyTyp(entyTyp);
        mwJvHdr.setCrtdBy(currUser);
        mwJvHdr.setPostFlg(0L);
        mwJvHdr.setBrnchSeq(brnch);
        long counter = 1L;
        List<MwJvDtl> mwJvDtls = new ArrayList();

        for (MwRcvryDtl dtl : rcvryDtls) {
            if (dtl.getPaySchedDtlSeq() == null) {
                continue;
            }
            String generalLedgerAcct = "";
            if (dtl.getChrgTypKey().longValue() == -1L) {
                MwPrdAcctSet mwPrdAcctSet = mwPrdAcctSetRepository.findOneByrefCdAndPymentDtlSeq("0001",
                        dtl.getPaySchedDtlSeq().longValue());
                generalLedgerAcct = mwPrdAcctSet.getGlAcctNum();
            } else if (dtl.getChrgTypKey().longValue() == -2L) {
                Query helthQry = em
                        .createNativeQuery("select hip.gl_acct_num gl_acct_num\r\n" + "from mw_clnt_hlth_insr chi\r\n"
                                + "join mw_hlth_insr_plan hip on hip.hlth_insr_plan_seq = chi.hlth_insr_plan_seq and hip.crnt_rec_flg=1\r\n"
                                + "join mw_pymt_sched_hdr hdr on hdr.loan_app_seq=chi.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
                                + "join mw_pymt_sched_dtl dtl on dtl.pymt_sched_hdr_seq=hdr.pymt_sched_hdr_seq and dtl.crnt_rec_flg=1\r\n"
                                + "where chi.crnt_rec_flg=1 and hip.PLAN_ID!='1223' and  dtl.pymt_sched_dtl_seq=:seq")
                        .setParameter("seq", dtl.getPaySchedDtlSeq().longValue());
                Object helthObj = new Object();
                try {
                    helthObj = helthQry.getSingleResult();
                    generalLedgerAcct = helthObj.toString().equals("0") ? "" : helthObj.toString();
                } catch (Exception e) {
                    log.warn("Health Insurance Plan Not Founded");
                }

            } else {
                generalLedgerAcct = typesRepository.findOneByTypSeqAndCrntRecFlg(dtl.getChrgTypKey().longValue(), true).getGlAcctNum();
            }

            long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
            // Credit Entry
            MwJvDtl mwJvDtlCrdt = new MwJvDtl();
            mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
            mwJvDtlCrdt.setJvHdrSeq(seq);
            mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
            mwJvDtlCrdt.setAmt(dtl.getPymtAmt());
            mwJvDtlCrdt.setGeneralLedgerAcct(generalLedgerAcct);
            mwJvDtlCrdt.setDscr(CREDIT);
            mwJvDtlCrdt.setLineItemNum(counter);
            mwJvDtls.add(mwJvDtlCrdt);
            // Debit entry
            long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
            MwJvDtl mwJvDtlDbt = new MwJvDtl();
            mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
            mwJvDtlDbt.setJvHdrSeq(seq);
            mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
            mwJvDtlDbt.setAmt(dtl.getPymtAmt());
            mwJvDtlDbt.setGeneralLedgerAcct(glAcctNum);
            mwJvDtlDbt.setDscr(DEBIT);
            mwJvDtlDbt.setLineItemNum(counter);

            mwJvDtls.add(mwJvDtlDbt);
            counter++;
        }
        vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);
        return mwJvHdr;

    }

    @Transactional
    public void reverseAdvanceRecovery(List<MwRcvryDtl> rcvryDtlList, String entyTyp, String currUser) {
        Instant currIns = Instant.now();
        List<MwJvHdr> mwJvHdrs = new ArrayList();
        List<MwJvDtl> mwJvDtls = new ArrayList();
        // Commented by Zohaib Asim - Dated 28-09-2021
        // Partial Recovery JV Issue
        /*advance.forEach( psd -> {
            Query q = em.createNativeQuery(
                    "select la.PRD_SEQ,la.LOAN_APP_SEQ,hdr.RCVRY_TRX_SEQ,dtl.RCVRY_CHRG_SEQ,CHRG_TYP_KEY,dtl.PYMT_AMT,RCVRY_TYP_SEQ,POST_FLG,t.GL_ACCT_NUM tgl,rec.GL_ACCT_NUM recgl\r\n"
                            + "  ,mc.frst_nm ||' '|| mc.last_nm as clntNm " + "from mw_rcvry_dtl dtl\r\n"
                            + "join mw_rcvry_trx hdr on hdr.RCVRY_TRX_SEQ = dtl.RCVRY_TRX_SEQ and hdr.CRNT_REC_FLG = 0 and hdr.del_flg=1\r\n"
                            + "join MW_pymt_sched_dtl psd on psd.PYMT_SCHED_DTL_SEQ=dtl.PYMT_SCHED_DTL_SEQ and psd.CRNT_REC_FLG=1 \r\n"
                            + "join MW_pymt_sched_hdr psh on psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ and psh.CRNT_REC_FLG = 1\r\n"
                            + "join mw_loan_app la on la.LOAN_APP_SEQ = psh.LOAN_APP_SEQ and la.CRNT_REC_FLG = 1\r\n"
                            + "join mw_clnt mc on mc.clnt_seq=la.clnt_seq and mc.crnt_rec_flg=1 "
                            + "left outer join mw_typs t on t.TYP_SEQ=dtl.CHRG_TYP_KEY and t.CRNT_REC_FLG = 1\r\n"
                            + "join mw_typs rec on rec.TYP_SEQ=hdr.RCVRY_TYP_SEQ and rec.CRNT_REC_FLG = 1\r\n"
                            + "where dtl.pymt_sched_dtl_seq=:psdSeq AND DTL.RCVRY_TRX_SEQ = (SELECT MAX(RD.RCVRY_TRX_SEQ) FROM MW_RCVRY_DTL RD WHERE RD.PYMT_SCHED_DTL_SEQ = :psdSeq AND RD.CRNT_REC_FLG = 0 AND RD.DEL_FLG = 1) "
                            + "and dtl.crnt_rec_flg=0 and dtl.DEL_FLG = 1" )
                    .setParameter( "psdSeq", psd.getPaySchedDtlSeq() );*/

        // Modified by Zohaib Asim - Dated 28-09-2021 - Partial Recovery JV Issue
        List rcvryTrxList = new ArrayList();
        rcvryDtlList.forEach(rtl -> {
            if (!rcvryTrxList.contains(rtl.getRcvryTrxSeq())) {
                Query q = em.createNativeQuery(
                                "select la.PRD_SEQ,la.LOAN_APP_SEQ,hdr.RCVRY_TRX_SEQ,dtl.RCVRY_CHRG_SEQ,CHRG_TYP_KEY,dtl.PYMT_AMT,RCVRY_TYP_SEQ,POST_FLG,t.GL_ACCT_NUM tgl,rec.GL_ACCT_NUM recgl\r\n"
                                        + "  ,mc.frst_nm ||' '|| mc.last_nm as clntNm " + "from mw_rcvry_dtl dtl\r\n"
                                        + "join mw_rcvry_trx hdr on hdr.RCVRY_TRX_SEQ = dtl.RCVRY_TRX_SEQ and hdr.CRNT_REC_FLG = 0 and hdr.del_flg=1\r\n"
                                        + "join MW_pymt_sched_dtl psd on psd.PYMT_SCHED_DTL_SEQ=dtl.PYMT_SCHED_DTL_SEQ and psd.CRNT_REC_FLG=1 \r\n"
                                        + "join MW_pymt_sched_hdr psh on psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ and psh.CRNT_REC_FLG = 1\r\n"
                                        + "join mw_loan_app la on la.LOAN_APP_SEQ = psh.LOAN_APP_SEQ and la.CRNT_REC_FLG = 1\r\n"
                                        + "join mw_clnt mc on mc.clnt_seq=la.clnt_seq and mc.crnt_rec_flg=1 "
                                        + "left outer join mw_typs t on t.TYP_SEQ=dtl.CHRG_TYP_KEY and t.CRNT_REC_FLG = 1\r\n"
                                        + "join mw_typs rec on rec.TYP_SEQ=hdr.RCVRY_TYP_SEQ and rec.CRNT_REC_FLG = 1\r\n"
                                        + "where hdr.RCVRY_TRX_SEQ = :trxSeq "
                                        + "and dtl.crnt_rec_flg=0 and dtl.DEL_FLG = 1")
                        .setParameter("trxSeq", rtl.getRcvryTrxSeq());

                // Add Recovery Transaction to List
                rcvryTrxList.add(rtl.getRcvryTrxSeq());

                List<Object[]> overdues = q.getResultList();

                long rec = 0;
                for (Object[] pObj : overdues) {

                    if (new BigDecimal(pObj[2].toString()).longValue() != rec) {
                        //Edited by Areeba
                        MwJvHdr oldHdr = mwJvHdrRepository
                                .findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDescAlt(new BigDecimal(pObj[2].toString()).longValue(), entyTyp);
                        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
                        MwJvHdr mwJvHdr = new MwJvHdr();
                        mwJvHdr.setJvHdrSeq(seq);
                        mwJvHdr.setRcvryTrxSeq(oldHdr.getRcvryTrxSeq());
                        mwJvHdr.setJvId(Long.toString(seq));
                        mwJvHdr.setJvDt(currIns);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(Date.from(oldHdr.getJvDt()));
                        mwJvHdr.setJvDscr("Recovery reversed dated " + dateString + " due to client death " + oldHdr.getEntySeq());
                        // ( pObj[ 10 ] == null ? "" : pObj[ 10 ].toString() )
                        mwJvHdr.setEntySeq(oldHdr.getEntySeq());
                        mwJvHdr.setEntyTyp(oldHdr.getEntyTyp());
                        mwJvHdr.setCrtdBy(currUser);
                        mwJvHdr.setPostFlg(0L);
                        mwJvHdr.setBrnchSeq(oldHdr.getBrnchSeq());
                        mwJvHdr.setPrntVchrRef(oldHdr.getJvHdrSeq());
                        mwJvHdrs.add(mwJvHdr);

                        long counter = 1L;
                        for (Object[] obj : overdues) {
                            if (new BigDecimal(pObj[2].toString()).longValue() == new BigDecimal(obj[2].toString()).longValue()) {

                                String generalLedgerAcct;
                                if (new BigDecimal(obj[4].toString()).longValue() == -1L) {
                                    MwPrdAcctSet acc = mwPrdAcctSetRepository.findOneByPrdSeqAndAcctCtgryKeyAndCrntRecFlg(
                                            new BigDecimal(obj[0].toString()).longValue(), 255, true);
                                    generalLedgerAcct = acc.getGlAcctNum();
                                } else if (new BigDecimal(obj[4].toString()).longValue() == -2L) {
                                    Query helthQry = em.createNativeQuery("select hip.gl_acct_num gl_acct_num\r\n"
                                                    + "from mw_clnt_hlth_insr chi\r\n"
                                                    + "join mw_hlth_insr_plan hip on hip.hlth_insr_plan_seq = chi.hlth_insr_plan_seq and hip.crnt_rec_flg=1\r\n"
                                                    + "where loan_app_seq=:loanAppSeq and chi.crnt_rec_flg=1\r\n" + "")
                                            .setParameter("loanAppSeq", new BigDecimal(obj[1].toString()).longValue());
                                    Object helthAcc = new Object();
                                    try {
                                        helthAcc = helthQry.getSingleResult();
                                    } catch (Exception e) {

                                    }
                                    generalLedgerAcct = helthAcc.toString().equals("0") ? "" : helthAcc.toString();
                                } else {
                                    generalLedgerAcct = obj[8].toString();
                                }

                                long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                                // Credit Entry
                                MwJvDtl mwJvDtlCrdt = new MwJvDtl();
                                mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
                                mwJvDtlCrdt.setJvHdrSeq(seq);
                                mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
                                mwJvDtlCrdt.setAmt(new BigDecimal(obj[5].toString()).longValue());
                                mwJvDtlCrdt.setGeneralLedgerAcct(obj[9].toString());
                                mwJvDtlCrdt.setDscr(CREDIT);
                                mwJvDtlCrdt.setLineItemNum(counter);
                                mwJvDtls.add(mwJvDtlCrdt);
                                // Debit entry
                                long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                                MwJvDtl mwJvDtlDbt = new MwJvDtl();
                                mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
                                mwJvDtlDbt.setJvHdrSeq(seq);
                                mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
                                mwJvDtlDbt.setAmt(new BigDecimal(obj[5].toString()).longValue());
                                mwJvDtlDbt.setGeneralLedgerAcct(generalLedgerAcct);
                                mwJvDtlDbt.setDscr(DEBIT);
                                mwJvDtlDbt.setLineItemNum(counter);

                                mwJvDtls.add(mwJvDtlDbt);
                                counter++;

                            }
                        }

                    }

                    rec = new BigDecimal(pObj[2].toString()).longValue();

                }
            }

        });
        if (mwJvHdrs.size() > 0 && mwJvDtls.size() > 0) {
            vchrPostingComponent.saveVchrPosting(mwJvHdrs, mwJvDtls);
        }

    }

    @Transactional
    public void reverseVchrPosting(Long entitySeq, String entyTyp, String user, String desc) {

        //Edited by Areeba
        MwJvHdr oldmwJvHdr = mwJvHdrRepository.findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDescAlt(entitySeq, entyTyp);

        if (oldmwJvHdr.getPrntVchrRef() == null) {
            desc = desc.concat(" ").concat("" + entitySeq);
            long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
            Instant currIns = Instant.now();
            MwJvHdr mwJvHdr = new MwJvHdr();
            mwJvHdr.setJvHdrSeq(seq);
            mwJvHdr.setRcvryTrxSeq(oldmwJvHdr.getRcvryTrxSeq());
            mwJvHdr.setJvId(Long.toString(seq));
            mwJvHdr.setJvDt(currIns);
            mwJvHdr.setJvDscr(desc);
            mwJvHdr.setEntySeq(oldmwJvHdr.getEntySeq());
            mwJvHdr.setEntyTyp(oldmwJvHdr.getEntyTyp());
            mwJvHdr.setCrtdBy(user);
            mwJvHdr.setPostFlg(0L);
            mwJvHdr.setBrnchSeq(oldmwJvHdr.getBrnchSeq());
            mwJvHdr.setPrntVchrRef(oldmwJvHdr.getJvHdrSeq());

            if (oldmwJvHdr.getJvHdrSeq() != null) {
                List<MwJvDtl> list = mwJvDtlRepository.findAllByJvHdrSeq(oldmwJvHdr.getJvHdrSeq());
                List<MwJvDtl> mwJvDtls = new ArrayList();
                list.forEach(jv -> {
                    long seqDtl = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                    MwJvDtl revJv = new MwJvDtl();
                    revJv.setAmt(jv.getAmt());
                    revJv.setGeneralLedgerAcct(jv.getGeneralLedgerAcct());
                    revJv.setJvDtlSeq(seqDtl);
                    revJv.setJvHdrSeq(seq);
                    revJv.setLineItemNum(jv.getLineItemNum());
                    if (jv.getCrdtDbtFlg().longValue() == 1L) {
                        revJv.setDscr(CREDIT);
                        revJv.setCrdtDbtFlg(CREDIT_FLG);
                    } else {
                        revJv.setDscr(DEBIT);
                        revJv.setCrdtDbtFlg(DEBIT_FLG);
                    }
                    mwJvDtls.add(revJv);

                });
                vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);
            }
        }
    }

    @Async
    public void bulkPosting(String user, String currUser, List<Object[]> unpostedTrx) {

        Query q = em.createNativeQuery(
                        "select la.loan_app_seq,la.prd_seq,trx.rcvry_trx_seq,dtl.chrg_typ_key,dtl.pymt_amt,dtl.pymt_sched_dtl_seq\r\n"
                                + "from mw_rcvry_dtl dtl \r\n"
                                + "join mw_rcvry_trx trx on trx.rcvry_trx_seq = dtl.rcvry_trx_seq and nvl(trx.post_flg,0)!=1 and to_date(trx.crtd_dt)=to_date(sysdate)\r\n"
                                + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq=dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg=1\r\n"
                                + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
                                + "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\r\n"
                                + "join mw_acl acl on acl.port_seq = la.port_seq and acl.user_id =:userId \r\n" + "where dtl.crnt_rec_flg =1 ")
                .setParameter("userId", user);
        List<Object[]> transections = q.getResultList();

        unpostedTrx.forEach(ut -> {
            long trxId = new BigDecimal(ut[2].toString()).longValue();
            long clntSeq = new BigDecimal(ut[3].toString()).longValue();
            List<MwRcvryDtl> rcvryDtls = new ArrayList();
            transections.forEach(t -> {
                long tId = new BigDecimal(t[2].toString()).longValue();
                if (trxId == tId) {
                    MwRcvryDtl mwRcvry = new MwRcvryDtl();
                    mwRcvry.setChrgTypKey(new BigDecimal(t[3].toString()).longValue());
                    mwRcvry.setPymtAmt(new BigDecimal(t[4].toString()).longValue());
                    rcvryDtls.add(mwRcvry);
                }
            });
            Query cq = em.createNativeQuery(com.idev4.rds.util.Queries.CHARG_ORDER).setParameter("clntSeq", clntSeq);
            /*Query cq = em
                    .createNativeQuery(
                            "select adj_ordr,prcao.prd_chrg_seq,typ_seq,typ_str,t.gl_acct_num\r\n" + "from mw_prd_chrg_adj_ordr prcao\r\n"
                                    + "left outer join MW_PRD_CHRG pc  on prcao.prd_chrg_seq=pc.PRD_CHRG_SEQ and pc.crnt_rec_flg =1\r\n"
                                    + "left outer join mw_typs t on pc.CHRG_TYP_SEQ=t.typ_seq and t.crnt_rec_flg =1\r\n"
                                    + "where prcao.crnt_rec_flg =1\r\n" + "and prcao.prd_seq=:prdSeq \r\n" + "order by adj_ordr" )
                    .setParameter( "prdSeq", prdSeq );*/
            List<Object[]> chrgOrder = cq.getResultList();

            Map m = mwRcvryTrxRepository.getGlAcctNumByRcvryTrxSeq(trxId);
            String glAcctNum = m.get("gl_acct_num").toString();
            MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(trxId, true);
            MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(clntSeq);
            MwJvHdr d = genrateRecoveryJv(trxId, "Recovery", "Bulk Posting", glAcctNum, currUser, chrgOrder, rcvryDtls, trx.getPymtDt(),
                    (brnch != null) ? brnch.getBrnchSeq() : 0L);
            if (d.getJvHdrSeq() != null) {
                trx.setPostFlg(1L);
                mwRcvryTrxRepository.save(trx);
            }
        });

    }

    @Transactional
    public String genrateDisbVchr(LoanApplication app, String prdNm, String currUser, String sts, String typ, int vchrTyp) {
        MwBrnch branch = mwBrnchRepository.findOneByClntSeq(app.getClntSeq());
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(app.getClntSeq(), true);
        Types advTyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0501", 1, 0, true);
        int type = 0;
        MwPrdAcctSet mwPrdAcctSet = mwPrdAcctSetRepository.findOneByrefCdAndPrdSeq("0001", app.getPrdSeq());
        if (typ.equals(ISLAMIC_TYPE)) {
            type = 1;
            if (vchrTyp == 0) {
                mwPrdAcctSet = mwPrdAcctSetRepository.findOneByrefCdAndPrdSeq("0001", app.getPrdSeq());
            } else if (vchrTyp == 1) {
                mwPrdAcctSet = new MwPrdAcctSet();
                mwPrdAcctSet.setGlAcctNum(advTyp.getGlAcctNum());
            }
        }

        MwPrdGrp grp = mwPrdGrpRepository.findOneByLoanAppSeq(app.getLoanAppSeq());

        List<Object[]> vouchers = em
                .createNativeQuery(
                        "select dvd.amt,t.typ_str,t.gl_acct_num,t.typ_id,dvh.DSBMT_DT,dvh.dsbmt_hdr_seq, t.typ_seq,dvd.INSTR_NUM  from \r\n"
                                + "        mw_dsbmt_vchr_dtl dvd\r\n"
                                + "        join mw_typs t on t.typ_seq=dvd.pymt_typs_seq and t.crnt_rec_flg=1\r\n"
                                + "        join mw_dsbmt_vchr_hdr dvh on dvh.dsbmt_hdr_seq=dvd.dsbmt_hdr_seq and dvh.crnt_rec_flg=1 \r\n"
                                + "        where dvd.crnt_rec_flg=1 and dvh.loan_app_seq=:loanAppSeq and dvh.dsbmt_vchr_typ=:vchrTyp")
                .setParameter("loanAppSeq", app.getLoanAppSeq()).setParameter("vchrTyp", vchrTyp).getResultList();

        long vHdrSeq = vouchers.get(0)[5] == null ? 0 : new BigDecimal(vouchers.get(0)[5].toString()).longValue();

        // MwJvHdr oldmwJvHdr = mwJvHdrRepository.findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDesc( vHdrSeq, DISBURSEMENT );

        MwJvHdr mwJvHdr = new MwJvHdr();
        // if ( oldmwJvHdr != null && oldmwJvHdr.getPrntVchrRef() == null ) {
        // mwJvHdrRepository.delete( oldmwJvHdr );
        // List< MwJvDtl > exdtls = mwJvDtlRepository.findAllByJvHdrSeq( oldmwJvHdr.getJvHdrSeq() );
        // mwJvDtlRepository.delete( exdtls );
        // }
        // Jv Header
        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
        Instant currIns = Instant.now();
        mwJvHdr.setJvHdrSeq(seq);
        // mwJvHdr.setPrntVchrRef( prntVchrRef );
        mwJvHdr.setJvId(Long.toString(seq));
        mwJvHdr.setJvDt(currIns);
        // mwJvHdr.setJvTypKey( jvTypKey );
        mwJvHdr.setEntyTyp(DISBURSEMENT);
        mwJvHdr.setCrtdBy(currUser);
        mwJvHdr.setPostFlg(0L);
        mwJvHdr.setBrnchSeq(branch.getBrnchSeq());

        mwJvHdr.setRcvryTrxSeq(vHdrSeq);
        mwJvHdr.setEntySeq(vHdrSeq);
        String remit = new String("");

        // detail genration
        long counter = 1L;
        List<MwJvDtl> mwJvDtls = new ArrayList();
        long loanAmt = 0;
        for (Object[] v : vouchers) {
            remit = remit.concat(v[1].toString() + ",");
            long amt = v[0] == null ? 0 : new BigDecimal(v[0].toString()).longValue();

            String account = v[2] == null ? "" : v[2].toString();

            if (v[3].toString().equals("0009")) {
                Types advCashAcc = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0001", 3, 0, true);
                account = advCashAcc.getGlAcctNum();
            }
            if (v[3].toString().equals("0011")) {
                Types advBankAcc = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0008", 3, branch.getBrnchSeq(),
                        true);
                account = advBankAcc.getGlAcctNum();
            }
            if (v[3].toString().equals("0010")) {
                Types advBankAcc = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0010", 3, 0, true);
                account = advBankAcc.getGlAcctNum();
            }
            if (!v[3].toString().equals("0009") && !v[3].toString().equals("0011")) {
                loanAmt = loanAmt + amt;
            }
            if (type == 1 && vchrTyp == 0) {
                account = advTyp.getGlAcctNum();
            }
            // KM SALE 1
            if (type == 1 && vchrTyp == 1) {
                long expSeq = SequenceFinder.findNextVal(Sequences.EXP_SEQ);
                MwExp exp = new MwExp();
                exp.setBrnchSeq(branch.getBrnchSeq());
                exp.setCrntRecFlg(true);
                exp.setCrtdBy(currUser);
                exp.setCrtdDt(currIns);
                exp.setDelFlg(false);
                exp.setEffStartDt(currIns);
                exp.setExpnsAmt(amt);
                exp.setExpnsDscr(advTyp.getTypStr());
                exp.setExpnsId(String.format("%04d", expSeq));
                exp.setExpnsTypSeq(advTyp.getTypSeq());
                exp.setExpRef("" + app.getLoanAppSeq());
                exp.setExpSeq(expSeq);
                exp.setInstrNum(v[7] == null ? "" : v[7].toString());
                exp.setLastUpdBy(currUser);
                exp.setLastUpdDt(currIns);
                exp.setPostFlg(1L);
                exp.setPymtRctFlg(1);
                exp.setPymtTypSeq(v[6] == null ? 0L : Long.valueOf(v[6].toString()));
                exp.setExpnsStsKey(-1L);
                mwJvHdr.setRcvryTrxSeq(expSeq);
                mwJvHdr.setEntySeq(expSeq);
                mwJvHdr.setEntyTyp("Expense");
                mwExpRepository.save(exp);
            }

            // Debit entry
            long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
            MwJvDtl mwJvDtlDbt = new MwJvDtl();
            mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
            mwJvDtlDbt.setJvHdrSeq(seq);
            mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
            mwJvDtlDbt.setAmt(amt);
            mwJvDtlDbt.setGeneralLedgerAcct(mwPrdAcctSet.getGlAcctNum());
            mwJvDtlDbt.setDscr(DEBIT);
            mwJvDtlDbt.setLineItemNum(counter);

            mwJvDtls.add(mwJvDtlDbt);

            if (grp.getPrdGrpId().equals("5765")) {
                if (counter <= 1) {
                    // String loanOstQuery = "select get_KRK_dsb_amt( " + app.getLoanAppSeq() + ") from dual";
                    // amt = new BigDecimal( em.createNativeQuery( loanOstQuery ).getSingleResult().toString() ).longValue();

                    StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("get_KRK_dsb_amt_");
                    // storedProcedure.s
                    storedProcedure.registerStoredProcedureParameter("p_loan_app_seq", Long.class, ParameterMode.IN);
                    storedProcedure.registerStoredProcedureParameter("v_dsb_amt", Long.class, ParameterMode.OUT);
                    storedProcedure.registerStoredProcedureParameter("P_PRD_GRP_SEQ", Long.class, ParameterMode.OUT);
                    storedProcedure.registerStoredProcedureParameter("v_msg", String.class, ParameterMode.OUT);

                    storedProcedure.setParameter("p_loan_app_seq", app.getLoanAppSeq());
                    // execute SP
                    storedProcedure.execute();
                    // get result
                    Long vDsbAmt = (Long) storedProcedure.getOutputParameterValue("v_dsb_amt");
                    Long prdGrpSeq = (Long) storedProcedure.getOutputParameterValue("P_PRD_GRP_SEQ");
                    String p_msg = (String) storedProcedure.getOutputParameterValue("v_msg");

                    log.debug("vDsbAmt: ", vDsbAmt);
                    log.debug("prdGrpSeq: ", prdGrpSeq);
                    log.debug("p_msg: ", p_msg);
                    if (p_msg != null && p_msg.contains("Error Code")) {
                        return p_msg;
                    }
                    Types adjTyp = typesRepository.findOneByDfrdAcctNumAndTypIdInAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("" + prdGrpSeq,
                            Arrays.asList("16188", "16190", "16191", "16192", "16193"), 1, 0, true);
                    if (amt - vDsbAmt > 0) {
                        long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                        MwJvDtl mwJvDtlCrdt = new MwJvDtl();
                        mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
                        mwJvDtlCrdt.setJvHdrSeq(seq);
                        mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
                        mwJvDtlCrdt.setAmt(amt - vDsbAmt);
                        mwJvDtlCrdt.setGeneralLedgerAcct(adjTyp.getGlAcctNum());
                        mwJvDtlCrdt.setDscr(CREDIT);
                        mwJvDtlCrdt.setLineItemNum(counter);
                        mwJvDtls.add(mwJvDtlCrdt);

                    }
                    amt = vDsbAmt;
                } else
                    continue;
            }

            // Credit Entry
            long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
            MwJvDtl mwJvDtlCrdt = new MwJvDtl();
            mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
            mwJvDtlCrdt.setJvHdrSeq(seq);
            mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
            mwJvDtlCrdt.setAmt(amt);
            mwJvDtlCrdt.setGeneralLedgerAcct(account);
            mwJvDtlCrdt.setDscr(CREDIT);
            mwJvDtlCrdt.setLineItemNum(counter);
            mwJvDtls.add(mwJvDtlCrdt);

            counter++;

        }
        mwJvHdr.setJvDscr(prdNm + " Disbursed to Client " + clnt.getFrstNm() + " "
                + ((clnt.getLastNm() == null) ? "" : clnt.getLastNm()) + " through " + remit);
        // KM SALE 1
        if (type == 1 && vchrTyp == 1) {
            mwJvHdr.setJvDscr(prdNm + " Sale 1 to Client " + clnt.getFrstNm() + " "
                    + ((clnt.getLastNm() == null) ? "" : clnt.getLastNm()) + " through " + remit);
        }

        if ((type == 0) || (type == 1 && vchrTyp == 1)) {
            List<Object[]> chargesObj = mwLoanAppChrgStngsRepository.getLoanAppChrgStngsByRddSeqAndLoanAppSeq(app.getPrdSeq(),
                    app.getLoanAppSeq());
            List<Charges> chargesList = charges.getListofCharge(loanAmt, chargesObj);

            for (Charges obj : chargesList) {
                if (!obj.typeId.equals("0017")) {
                    // Credit Entry
                    long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                    MwJvDtl mwJvDtlCrdt = new MwJvDtl();
                    mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
                    mwJvDtlCrdt.setJvHdrSeq(seq);
                    mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
                    mwJvDtlCrdt.setAmt((long) obj.val);
                    mwJvDtlCrdt.setGeneralLedgerAcct(obj.defAcct);
                    mwJvDtlCrdt.setDscr(CREDIT);
                    mwJvDtlCrdt.setLineItemNum(counter);
                    mwJvDtls.add(mwJvDtlCrdt);
                    // Debit entry
                    long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                    MwJvDtl mwJvDtlDbt = new MwJvDtl();
                    mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
                    mwJvDtlDbt.setJvHdrSeq(seq);
                    mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
                    mwJvDtlDbt.setAmt((long) obj.val);
                    mwJvDtlDbt.setGeneralLedgerAcct(obj.acct);
                    mwJvDtlDbt.setDscr(DEBIT);
                    mwJvDtlDbt.setLineItemNum(counter);

                    mwJvDtls.add(mwJvDtlDbt);
                    counter++;
                }
            }
        }

        vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);
        return "";
    }

    @Transactional
    public void resheduleChargesJV(List<PaymentScheduleChargers> chrges, Long vHdrSeq, String currUser, long brnchSeq,
                                   long loanAppSeq) {

        // Jv Header
        MwJvHdr mwJvHdr = new MwJvHdr();
        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
        Instant currIns = Instant.now();
        mwJvHdr.setJvHdrSeq(seq);
        // mwJvHdr.setPrntVchrRef( prntVchrRef );
        mwJvHdr.setJvId(Long.toString(seq));
        mwJvHdr.setJvDt(currIns);
        // mwJvHdr.setJvTypKey( jvTypKey );
        mwJvHdr.setEntyTyp(Rescheduling);
        mwJvHdr.setCrtdBy(currUser);
        mwJvHdr.setPostFlg(0L);
        mwJvHdr.setBrnchSeq(brnchSeq);
        mwJvHdr.setRcvryTrxSeq(vHdrSeq);
        mwJvHdr.setEntySeq(vHdrSeq);
        mwJvHdr.setJvDscr("Reschedule Charges JV");
        String remit = new String("");

        List<MwJvDtl> mwJvDtls = new ArrayList();
        // detail genration
        long counter = 1L;
        for (PaymentScheduleChargers obj : chrges) {
            String dfrAcc = "";
            String acc = "";
            if (obj.getChrgTypsSeq() == -2L) {
                MwHlthInsrPlan plan = mwHlthInsrPlanRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq);
                if (plan != null) {
                    acc = plan.getGlAcctNum();
                    dfrAcc = plan.getDfrdAcctNum();
                }
                // Credit Entry
                long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                MwJvDtl mwJvDtlCrdt = new MwJvDtl();
                mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
                mwJvDtlCrdt.setJvHdrSeq(seq);
                mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
                mwJvDtlCrdt.setAmt(obj.getAmt());
                mwJvDtlCrdt.setGeneralLedgerAcct(dfrAcc);
                mwJvDtlCrdt.setDscr(CREDIT);
                mwJvDtlCrdt.setLineItemNum(counter);
                mwJvDtls.add(mwJvDtlCrdt);
                // Debit entry
                long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                MwJvDtl mwJvDtlDbt = new MwJvDtl();
                mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
                mwJvDtlDbt.setJvHdrSeq(seq);
                mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
                mwJvDtlDbt.setAmt(obj.getAmt());
                mwJvDtlDbt.setGeneralLedgerAcct(acc);
                mwJvDtlDbt.setDscr(DEBIT);
                mwJvDtlDbt.setLineItemNum(counter);

                mwJvDtls.add(mwJvDtlDbt);
                counter++;
            } else {

                Types chrgTyp = typesRepository.findOneByTypSeqAndCrntRecFlg(obj.getChrgTypsSeq(), true);
                if (!chrgTyp.getTypId().equals("0017")) {
                    // Credit Entry
                    long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                    MwJvDtl mwJvDtlCrdt = new MwJvDtl();
                    mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
                    mwJvDtlCrdt.setJvHdrSeq(seq);
                    mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
                    mwJvDtlCrdt.setAmt(obj.getAmt());
                    mwJvDtlCrdt.setGeneralLedgerAcct(chrgTyp.getDfrdAcctNum());
                    mwJvDtlCrdt.setDscr(CREDIT);
                    mwJvDtlCrdt.setLineItemNum(counter);
                    mwJvDtls.add(mwJvDtlCrdt);
                    // Debit entry
                    long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
                    MwJvDtl mwJvDtlDbt = new MwJvDtl();
                    mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
                    mwJvDtlDbt.setJvHdrSeq(seq);
                    mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
                    mwJvDtlDbt.setAmt(obj.getAmt());
                    mwJvDtlDbt.setGeneralLedgerAcct(chrgTyp.getGlAcctNum());
                    mwJvDtlDbt.setDscr(DEBIT);
                    mwJvDtlDbt.setLineItemNum(counter);
                    mwJvDtls.add(mwJvDtlDbt);
                    counter++;

                }
            }
        }

        vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);

    }

    @Transactional
    public Long genericJV(long trxSeq, String enty, String desc, String crdtAcc, String dbtAcc, long amt, String currUser, long branch,
                          Instant pymtDate) {
        Instant currIns = Instant.now();
        MwJvHdr mwJvHdr = new MwJvHdr();
        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
        mwJvHdr.setJvHdrSeq(seq);
        mwJvHdr.setRcvryTrxSeq(trxSeq);
        // mwJvHdr.setPrntVchrRef( prntVchrRef );
        mwJvHdr.setJvId(Long.toString(seq));
        mwJvHdr.setJvDt(pymtDate);
        mwJvHdr.setJvDscr(desc);
        // mwJvHdr.setJvTypKey( jvTypKey );
        mwJvHdr.setEntySeq(trxSeq);
        mwJvHdr.setEntyTyp(enty);
        mwJvHdr.setCrtdBy(currUser);
        mwJvHdr.setPostFlg(0L);
        mwJvHdr.setBrnchSeq(branch);
        List<MwJvDtl> mwJvDtls = new ArrayList();

        // Credit Entry
        long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
        MwJvDtl mwJvDtlCrdt = new MwJvDtl();
        mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
        mwJvDtlCrdt.setJvHdrSeq(seq);
        mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
        mwJvDtlCrdt.setAmt(amt);
        mwJvDtlCrdt.setGeneralLedgerAcct(crdtAcc);
        mwJvDtlCrdt.setDscr(CREDIT);
        mwJvDtlCrdt.setLineItemNum(1L);
        mwJvDtls.add(mwJvDtlCrdt);
        // Debit entry
        long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
        MwJvDtl mwJvDtlDbt = new MwJvDtl();
        mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
        mwJvDtlDbt.setJvHdrSeq(seq);
        mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
        mwJvDtlDbt.setAmt(amt);
        mwJvDtlDbt.setGeneralLedgerAcct(dbtAcc);
        mwJvDtlDbt.setDscr(DEBIT);
        mwJvDtlDbt.setLineItemNum(1L);

        mwJvDtls.add(mwJvDtlDbt);

        return vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);

    }

    @Transactional
    public void funralJV(MwExp exp, String clnt, Types fund, List<Object[]> chrgs, String currUser) {
        Types type = typesRepository.findOneByTypSeqAndCrntRecFlg(exp.getPymtTypSeq(), true);
        MwBrnch branch = mwBrnchRepository.findOneByClntSeq(Long.parseLong(exp.getExpRef()));
        Instant currIns = Instant.now();
        MwJvHdr mwJvHdr = new MwJvHdr();
        long seq = SequenceFinder.findNextVal(Sequences.JV_HDR_SEQ);
        mwJvHdr.setJvHdrSeq(seq);
        mwJvHdr.setRcvryTrxSeq(exp.getExpSeq());
        // mwJvHdr.setPrntVchrRef( prntVchrRef );
        mwJvHdr.setJvId(Long.toString(seq));
        mwJvHdr.setJvDt(currIns);
        mwJvHdr.setJvDscr("Funeral Charges is paid to Client " + clnt + "  through " + type.getTypStr());
        // mwJvHdr.setJvTypKey( jvTypKey );
        mwJvHdr.setEntySeq(exp.getExpSeq());
        mwJvHdr.setEntyTyp("Expense");
        mwJvHdr.setCrtdBy(currUser);
        mwJvHdr.setPostFlg(0L);
        mwJvHdr.setBrnchSeq(branch.getBrnchSeq());
        List<MwJvDtl> mwJvDtls = new ArrayList();

        // Credit Entry
        /*
        chrgs.forEach( c -> {
            long dtlSeqCrdt = SequenceFinder.findNextVal( Sequences.JV_DTL_SEQ );
            MwJvDtl mwJvDtlCrdt = new MwJvDtl();
            mwJvDtlCrdt.setJvDtlSeq( dtlSeqCrdt );
            mwJvDtlCrdt.setJvHdrSeq( seq );
            mwJvDtlCrdt.setCrdtDbtFlg( CREDIT_FLG );
            mwJvDtlCrdt.setAmt( new BigDecimal( c[ 3 ].toString() ).longValue() );
            mwJvDtlCrdt.setGeneralLedgerAcct( c[ 2 ].toString() );
            mwJvDtlCrdt.setDscr( CREDIT );
            mwJvDtlCrdt.setLineItemNum( 1L );
            mwJvDtls.add( mwJvDtlCrdt );
            // Debit entry
            long dtlSeqDbt = SequenceFinder.findNextVal( Sequences.JV_DTL_SEQ );
            MwJvDtl mwJvDtlDbt = new MwJvDtl();
            mwJvDtlDbt.setJvDtlSeq( dtlSeqDbt );
            mwJvDtlDbt.setJvHdrSeq( seq );
            mwJvDtlDbt.setCrdtDbtFlg( DEBIT_FLG );
            mwJvDtlDbt.setAmt( new BigDecimal( c[ 3 ].toString() ).longValue() );
            mwJvDtlDbt.setGeneralLedgerAcct( fund.getGlAcctNum() );
            mwJvDtlDbt.setDscr( DEBIT );
            mwJvDtlDbt.setLineItemNum( 1L );

            mwJvDtls.add( mwJvDtlDbt );

        } );
        */
        long dtlSeqCrdt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
        MwJvDtl mwJvDtlCrdt = new MwJvDtl();
        mwJvDtlCrdt.setJvDtlSeq(dtlSeqCrdt);
        mwJvDtlCrdt.setJvHdrSeq(seq);
        mwJvDtlCrdt.setCrdtDbtFlg(CREDIT_FLG);
        mwJvDtlCrdt.setAmt(exp.getExpnsAmt());
        mwJvDtlCrdt.setGeneralLedgerAcct(type.getGlAcctNum());
        mwJvDtlCrdt.setDscr(CREDIT);
        mwJvDtlCrdt.setLineItemNum(1L);
        mwJvDtls.add(mwJvDtlCrdt);
        // Debit entry
        long dtlSeqDbt = SequenceFinder.findNextVal(Sequences.JV_DTL_SEQ);
        MwJvDtl mwJvDtlDbt = new MwJvDtl();
        mwJvDtlDbt.setJvDtlSeq(dtlSeqDbt);
        mwJvDtlDbt.setJvHdrSeq(seq);
        mwJvDtlDbt.setCrdtDbtFlg(DEBIT_FLG);
        mwJvDtlDbt.setAmt(exp.getExpnsAmt());
        mwJvDtlDbt.setGeneralLedgerAcct(fund.getGlAcctNum());
        mwJvDtlDbt.setDscr(DEBIT);
        mwJvDtlDbt.setLineItemNum(1L);

        mwJvDtls.add(mwJvDtlDbt);

        vchrPostingComponent.saveVchrPosting(mwJvHdr, mwJvDtls);

    }

    public Page<MwJvHdr> getAllJvHdrs(int page, int size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "jvDt");
        Page<MwJvHdr> list = mwJvHdrRepository.findAll(pageable);

        return list;
    }

    public Map<String, Object> getAllJvHdrs(Long brnch_seq, Integer pageIndex, Integer pageSize, String filter, Boolean isCount) {

        String jvScript = "select hdr.* from mw_jv_hdr hdr where hdr.brnch_seq = :brnch_seq";
        String jvCountScript = "select count(*) from mw_jv_hdr hdr where hdr.brnch_seq = :brnch_seq";

        if (filter != null && filter.length() > 0) {

            String search = " and ((lower(hdr.jv_id) like '%?%')  OR (lower(hdr.enty_seq) like '%?%'))".replace("?",
                    filter.toLowerCase());

            jvScript += search;
            jvCountScript += search;
        }

        List<Object[]> result = em.createNativeQuery(jvScript + "\r\norder by 1 desc").setParameter("brnch_seq", brnch_seq)
                .setFirstResult((pageIndex) * pageSize).setMaxResults(pageSize).getResultList();
        List<VhcrHdrDto> jvdtoList = new ArrayList<>();
        for (Object[] res : result) {
            VhcrHdrDto jv = new VhcrHdrDto();
            jv.jvHdrSeq = (res[0] == null) ? "" : res[0].toString();
            jv.jvId = (res[2] == null) ? "" : res[2].toString();
            jv.jvDt = (res[3] == null) ? "" : res[3].toString();
            jv.jvDscr = (res[4] == null) ? "" : res[4].toString();
            jv.entySeq = (res[6] == null) ? "" : res[6].toString();
            jv.entyTyp = (res[7] == null) ? "" : res[7].toString();
            jv.rcvryTrxSeq = (res[10] == null) ? "" : res[10].toString();
            jvdtoList.add(jv);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("jvHdr", jvdtoList);

        Long totalCountResult = 0L;
        if (isCount.booleanValue()) {
            totalCountResult = new BigDecimal(
                    em.createNativeQuery(jvCountScript).setParameter("brnch_seq", brnch_seq).getSingleResult().toString()).longValue();
        }

        resp.put("count", totalCountResult);
        return resp;
    }

    public Page<VhcrHdrDto> getAllJvHdrs(int page, int size, String filterString) {
        String countHdr = "select count(*) from mw_jv_hdr hdr ";
        String query = "select hdr.* from mw_jv_hdr hdr ";

        String filter = "";
        if (filterString != null && filterString.length() > 0) {
            String[] splited = filterString.split("\\s+");
            for (int i = 0; i < splited.length; i++) {
                filter = (i == 0) ? " where " : filter + " and ";
                filter = filter
                        + " (  hdr.JV_ID Like '%%' or TO_CHAR(hdr.JV_DT, 'dd-mm-yyyy') Like '%%' or LOWER( hdr.JV_DSCR ) Like '%%' or  hdr.ENTY_SEQ  Like '%%' or  hdr.RCVRY_TRX_SEQ Like '%%' ) ";
                filter = filter.replaceAll("%%", "%" + splited[i] + "%");
            }

        }
        query = query + filter + " order by hdr.jv_dt desc";
        Query q = em.createNativeQuery(query);
        Long totalCountResult = new BigDecimal(em.createNativeQuery(countHdr + filter).getSingleResult().toString()).longValue();
        Query qm = em.createNativeQuery(query);
        qm.setFirstResult((page) * size);
        qm.setMaxResults(size);
        List<Object[]> result = qm.getResultList();
        List<VhcrHdrDto> dtoList = new ArrayList<>();
        for (Object[] res : result) {
            VhcrHdrDto jv = new VhcrHdrDto();
            jv.jvHdrSeq = (res[0] == null) ? "" : res[0].toString();
            jv.jvId = (res[2] == null) ? "" : res[2].toString();
            jv.jvDt = (res[3] == null) ? "" : res[3].toString();
            jv.jvDscr = (res[4] == null) ? "" : res[4].toString();
            jv.entySeq = (res[6] == null) ? "" : res[6].toString();
            jv.entyTyp = (res[7] == null) ? "" : res[7].toString();
            jv.rcvryTrxSeq = (res[10] == null) ? "" : res[10].toString();
            dtoList.add(jv);
        }
        return new PageImpl<>(dtoList, new PageRequest(page, size), totalCountResult);
    }

    public List<MwJvDtl> getJVDetailByHdrSeq(long jvHdrSeq) {
        return mwJvDtlRepository.findAllByJvHdrSeq(jvHdrSeq);
    }

    public List<VhcrDtlDto> getJVDTLbyHdrSeq(long jvHdrSeq) {
        String query = "select dtl.*, (select max(CUST_ACC_DESC) from  ledger_heads where CUST_SEGMENTS = dtl.gl_acct_num) as acc from mw_jv_dtl dtl where jv_hdr_seq="
                + jvHdrSeq + " order by 7";
        Query q = em.createNativeQuery(query);
        List<Object[]> result = q.getResultList();
        List<VhcrDtlDto> dtls = new ArrayList<>();
        for (Object[] res : result) {
            VhcrDtlDto dtl = new VhcrDtlDto();
            dtl.jvDtlSeq = (res[0] == null) ? "" : res[0].toString();
            dtl.jvHdrSeq = (res[1] == null) ? "" : res[1].toString();
            dtl.crdtDbtFlg = (res[2] == null) ? "" : res[2].toString();
            dtl.amt = (res[3] == null) ? "" : res[3].toString();
            dtl.generalLedgerAcct = (res[4] == null) ? "" : res[4].toString();
            dtl.dscr = (res[5] == null) ? "" : res[5].toString();
            dtl.lineItemNum = (res[6] == null) ? "" : res[6].toString();
            dtl.ledgerAcctDesc = (res[7] == null) ? "" : res[7].toString();
            dtls.add(dtl);
        }
        return dtls;
    }

    public List<RecoveryJvHostory> getRecoveryJvHistory(long clntSeq) {
        Query q = em.createNativeQuery("select jh.jv_hdr_seq,jh.jv_id,to_char(jh.jv_dt,'dd-mm-yyyy'),jv_dscr,\r\n"
                        + "     lh.cust_acc_desc,\r\n" + "     sum(case when crdt_dbt_flg=1 then amt else 0 end) crdt_amt,\r\n"
                        + "     sum(case when crdt_dbt_flg=0 then amt else 0 end) dbt_amt\r\n" + "from mw_rcvry_trx trx\r\n"
                        + "join mw_loan_app app on app.clnt_seq=trx.pymt_ref and app.crnt_rec_flg=1 and app.loan_app_sts=703\r\n"
                        + "join mw_jv_hdr jh on jh.enty_seq=trx.rcvry_trx_seq and jh.enty_typ='Recovery'\r\n"
                        + "join mw_jv_dtl jd on jd.jv_hdr_seq=jh.jv_hdr_seq\r\n" + "join ledger_heads lh on lh.cust_segments=jd.gl_acct_num\r\n"
                        + "where trx.crnt_rec_flg=1 and app.CLNT_SEQ=:clntSeq\r\n"
                        + "group by jh.jv_hdr_seq,jh.jv_id,jv_dt,jv_dscr,lh.cust_acc_desc order by jh.jv_dt desc")
                .setParameter("clntSeq", clntSeq);
        List<Object[]> result = q.getResultList();
        List<RecoveryJvHostory> jvs = new ArrayList();
        result.forEach(j -> {
            RecoveryJvHostory jv = new RecoveryJvHostory();
            jv.hdrSeq = new BigDecimal(j[0].toString()).longValue();

            jv.hdrId = j[1].toString();

            jv.jvDt = j[2].toString();

            jv.Desc = j[3].toString();
            jv.accDesc = j[4].toString();

            jv.debit = j[5] != null ? new BigDecimal(j[5].toString()).longValue() : 0;

            jv.credit = j[6] != null ? new BigDecimal(j[6].toString()).longValue() : 0;
            jvs.add(jv);
        });

        return jvs;
    }

    public class VhcrHdrDto {

        public String jvHdrSeq;

        public String rcvryTrxSeq;

        public String prntVchrRef;

        public String jvId;

        public String jvDt;

        public String jvDscr;

        public String jvTypKey;

        public String entySeq;

        public String entyTyp;

        public String postFlg;

        public String brnchSeq;

    }

    public class VhcrDtlDto {

        public String jvDtlSeq;

        public String jvHdrSeq;

        public String crdtDbtFlg;

        public String amt;

        public String generalLedgerAcct;

        public String dscr;

        public String lineItemNum;

        public String ledgerAcctDesc;
    }

}
