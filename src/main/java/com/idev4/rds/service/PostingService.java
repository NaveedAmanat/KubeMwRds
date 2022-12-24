package com.idev4.rds.service;

import com.fasterxml.uuid.Logger;
import com.idev4.rds.domain.*;
import com.idev4.rds.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostingService {

    @Autowired
    VchrPostingService vchrPostingService;

    @Autowired
    TypesRepository typesRepository;

    @Autowired
    MwExpRepository mwExpRepository;

    @Autowired
    RecoveryService recoveryService;

    @Autowired
    EntityManager em;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MwRcvryDtlRepository mwRcvryRepository;

    @Autowired
    MwRcvryTrxRepository mwRcvryTrxRepository;

    public List allAccessRecoveries(String user) {
        List allAccessRecvry = new ArrayList();
        return allAccessRecvry;
    }

    public boolean isExpensePosted(long expSeq) {
        MwExp exp = mwExpRepository.findOneByExpSeqAndCrntRecFlg(expSeq, true);
        return (exp.getPostFlg() == null) ? false : (exp.getPostFlg().longValue() > 0) ? true : false;
    }

    @Transactional(rollbackFor = Exception.class)
    public long postExpense(long expSeq, String currUser) throws Exception {
        boolean posted = isExpensePosted(expSeq);
        if (posted) {
            return 0;
        }

        // Added by Zohaib Asim - Dated 14-01-2021
        // Business Functionality moved to Procedure
        String parmOutputProcedure = "";
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRC_POST_EXPENSE");
        storedProcedure.registerStoredProcedureParameter("P_EXPNS_SEQ", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_LOGIN_USER", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_RTN_MSG", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("P_EXPNS_SEQ", expSeq);
        storedProcedure.setParameter("P_LOGIN_USER", currUser);
        //storedProcedure.setParameter( "P_RTN_MSG", parmOutputProcedure);
        storedProcedure.execute();
        System.out.println(storedProcedure.getOutputParameterValue("P_RTN_MSG"));
        parmOutputProcedure = storedProcedure.getOutputParameterValue("P_RTN_MSG").toString();

        // ZOHAIB ASIM - DATED 3-11-2021 - SANCTION LIST PHASE 2
        // CONDITIONS MODIFIED
        if (parmOutputProcedure.equals("SUCCESS")) {
            Logger.logInfo("PRC_POST_EXPENSE: Successfully Executed.");
        } else if (parmOutputProcedure.contains("ERR_CODE:0001")) {
            Logger.logError("PRC_POST_EXPENSE:" + "AML BLOCKED.");
            expSeq = -1;
        } else {
            Logger.logError("PRC_POST_EXPENSE:" + parmOutputProcedure);
            expSeq = 0;
        }
        return expSeq;
    }

    @Async
    public void postMultiExpense(String exps, String currUser) {
        Pattern pattern = Pattern.compile(",");
        List<Long> list = pattern.splitAsStream(exps).map(Long::valueOf).collect(Collectors.toList());

        list.forEach(e -> {
            try {
                postExpense(e, currUser);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

    }

    private long postFunralCharges(MwExp exp, Types fund, String currUser) {

        Query query = em.createNativeQuery("select t.TYP_SEQ,t.TYP_STR,t.GL_ACCT_NUM ,nvl(sum(psc.amt),0)-nvl(sum(rd.pymt_amt),0) amt \r\n"
                + "from mw_loan_app la \r\n" + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
                + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
                + "join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
                + "left outer join mw_rcvry_dtl rd on rd.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.CHRG_TYP_KEY=psc.CHRG_TYPS_SEQ and rd.crnt_rec_flg=1\r\n"
                + "join mw_typs t on t.TYP_SEQ=psc.CHRG_TYPS_SEQ and t.DEL_FLG = 0 and t.CRNT_REC_FLG = 1 \r\n"
                + "where la.clnt_seq=:clntSeq and la.crnt_rec_flg=1 and la.LOAN_APP_STS=703\r\n"
                + "group by t.TYP_SEQ,t.TYP_STR,t.GL_ACCT_NUM  \r\n" + "union \r\n"
                + "select hip.HLTH_INSR_PLAN_SEQ,hip.PLAN_NM,hip.GL_ACCT_NUM,nvl(sum(psc.amt),0)-nvl(sum(rd.pymt_amt),0)  amt\r\n"
                + "from mw_loan_app la \r\n" + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
                + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
                + "join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
                + "left outer join mw_rcvry_dtl rd on rd.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.CHRG_TYP_KEY=psc.CHRG_TYPS_SEQ and rd.crnt_rec_flg=1\r\n"
                + "join MW_CLNT_HLTH_INSR chi on chi.LOAN_APP_SEQ=la.LOAN_APP_SEQ and chi.CRNT_REC_FLG=1   \r\n"
                + "join MW_HLTH_INSR_PLAN hip on hip.HLTH_INSR_PLAN_SEQ = chi.HLTH_INSR_PLAN_SEQ and hip.CRNT_REC_FLG=1   \r\n"
                + "where la.clnt_seq=:clntSeq and la.crnt_rec_flg=1 and psc.CHRG_TYPS_SEQ=-2 and la.LOAN_APP_STS=703\r\n"
                + "group by hip.HLTH_INSR_PLAN_SEQ,hip.PLAN_NM,hip.GL_ACCT_NUM").setParameter("clntSeq", exp.getExpRef());
        List<Object[]> chrgs = query.getResultList();

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(exp.getExpRef()), true);

        vchrPostingService.funralJV(exp, clnt.getFrstNm() + " " + ((clnt.getLastNm() == null) ? "" : clnt.getLastNm()), fund, chrgs,
                currUser);

        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("adjust_claim_pymt_schdl");
        storedProcedure.registerStoredProcedureParameter("p_amt", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_clnt", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN);
        storedProcedure.setParameter("p_amt", 5000L - exp.getExpnsAmt());
        storedProcedure.setParameter("p_clnt", Long.parseLong(exp.getExpRef()));
        storedProcedure.setParameter("p_user", currUser);
        storedProcedure.execute();

        exp.setPostFlg(1L);
        mwExpRepository.save(exp);
        return exp.getExpSeq();
    }

    public boolean reverseExpense(long expSeq, String currUser, String reason) {
        MwExp exp = mwExpRepository.findOneByExpSeqAndCrntRecFlg(expSeq, true);
        Types funChrg = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0424", 2, 0, true);
        if (funChrg.getTypSeq().longValue() == exp.getExpnsTypSeq().longValue()) {
            // Modified by Zohaib Asim - Dated 20/01/2021 - Expense Seq added in parameter
            recoveryService.reverseCashFunralCharges(Long.parseLong(exp.getExpRef()), expSeq, currUser);
        }

        //Added by Areeba
        Types dsbltyClaim = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0423", 2, 0, true);
        if (dsbltyClaim.getTypSeq().longValue() == exp.getExpnsTypSeq().longValue()) {
            recoveryService.reverseDisabilityReceivable(Long.parseLong(exp.getExpRef()), expSeq, currUser);
        }
        //Ended by Areeba

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = format.format(Date.from(exp.getCrtdDt()));
        String desc = new String("Expense Reversed - ").concat(exp.getExpnsDscr()).concat(" dated ").concat(dateString)
                .concat(" due to ").concat(reason);
        vchrPostingService.reverseVchrPosting(expSeq, "Expense", currUser, desc);
        return true;
    }

    @Transactional
    public boolean reverseExcessRecovery(long trxSeq, String currUser, String reason, String token) {
        Instant currIns = Instant.now();

        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true);
        if (exesstyp == null)
            return false;

        MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(trxSeq, true);
        if (trx == null)
            return false;
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(trx.getPymtRef(), true);
        List<MwRcvryDtl> excessRecv = mwRcvryRepository.findAllByRcvryTrxSeqAndCrntRecFlg(trx.getRcvryTrxSeq(), true);
        if (excessRecv.size() > 0) {
            List<MwRcvryTrx> trxs = new ArrayList();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            excessRecv.forEach(r -> {
                r.setCrntRecFlg(false);
                r.setLastUpdBy(currUser);
                r.setDelFlg(true);
                r.setLastUpdDt(currIns);
                mwRcvryRepository.save(r);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = format.format(new Date());
                if (r.getPaySchedDtlSeq() == null) {
                    String desc = new String("Reversal of Excess Recovery dated ").concat(dateString).concat(clnt.getFrstNm())
                            .concat(" ").concat((clnt.getLastNm() == null) ? "" : clnt.getLastNm());
                    vchrPostingService.reverseVchrPosting(r.getRcvryTrxSeq(), exesstyp.getTypStr(), currUser, desc);
                } else {
                    recoveryService.reversePaymentWithoutExcess(trx, clnt, currUser, token);
                }
            });
        }
        trx.setCrntRecFlg(false);
        trx.setLastUpdBy(currUser);
        trx.setDelFlg(true);
        trx.setLastUpdDt(currIns);
        mwRcvryRepository.save(excessRecv);
        mwRcvryTrxRepository.save(trx);
        return true;
    }

    /* public long postAccessRecovry( long expSeq, String currUser ) {
        MwExp exp = mwExpRepository.findOneByExpSeqAndCrntRecFlg( expSeq, true );
        vchrPostingService.genericJV( expSeq, "Expense", getTypByTypSeq( exp.getPymtTypSeq() ).getGlAcctNum(),
                getTypIdAndTyp( exp.getExpnsDscr() ).getGlAcctNum(), exp.getExpnsAmt(), currUser );
        exp.setPostFlg( 1L );
        mwExpRepository.save( exp );
        return exp.getExpSeq();
    }

    public long postHealthInsurance( long expSeq, String currUser ) {
        MwExp exp = mwExpRepository.findOneByExpSeqAndCrntRecFlg( expSeq, true );
        vchrPostingService.genericJV( expSeq, "Expense", getTypByTypSeq( exp.getPymtTypSeq() ).getGlAcctNum(),
                getTypIdAndTyp( exp.getExpnsDscr() ).getGlAcctNum(), exp.getExpnsAmt(), currUser );
        exp.setPostFlg( 1L );
        mwExpRepository.save( exp );
        return exp.getExpSeq();

    }*/

    private Types getTypByTypSeq(long seq) {
        return typesRepository.findOneByTypSeqAndCrntRecFlg(seq, true);
    }

    private Types getTypIdAndTyp(String typStr) {
        return typesRepository.findOneByTypStrAndCrntRecFlg(typStr, true);
    }

}
