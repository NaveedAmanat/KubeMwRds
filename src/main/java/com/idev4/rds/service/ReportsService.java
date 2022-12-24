package com.idev4.rds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idev4.rds.domain.*;
import com.idev4.rds.dto.AccountLedger;
import com.idev4.rds.dto.BookDetailsDTO;
import com.idev4.rds.repository.*;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private static final String FDARTA = "FDARTA.jrxml";
    private static final String TOPSHEET = "TOPSHEET.jrxml";
    private static final String PDCDETAIL = "PDCDETAIL.jrxml";
    private static final String RETENTIONRATE = "RETENTIONRATE.jrxml";
    private static final String PROJECTED_CLIENTS_LOANS_COM = "PROJECTED_CLIENTS_LOANS_COM.jrxml";
    private static final String PAR_BRANCHWISE = "PAR_BRANCHWISE.jrxml";
    private static final String ADC_MONTH_WISE_REPORT = "ADC_MONTH_WISE_REPORT.jrxml";
    private static final String ANML_INSURANCECLAIM = "ANML_INSURANCECLAIM.jrxml";
    private static final String PORTFOLIO_CONCENTRATION = "PORTFOLIO_CONCENTRATION.jrxml";
    private static final String PENDINGCLIENTS = "PENDINGCLIENTS.jrxml";
    private static final String TAGGEDCLIENTSCLAIM = "TAGGEDCLIENTSCLAIM.jrxml";
    private static final String PRODUCTWISEREPORTADDITION = "PRODUCTWISEREPORTADDITION.jrxml";
    private static final String AGENCIES_TRAGET_TRACKING = "AGENCIES_TRAGET_TRACKING.jrxml";
    private static final String TRANSFERRED_CLIENTS = "TRANSFERRED_CLIENTS.jrxml";
    private static final String ACTIVE_STATUS = "0005";
    private static final String DISBIRST_STATUS = "0009";
    private static final String DISGARD_STATUS = "0008";
    private static final String DEFERRED_STATUS = "1285";
    private static final String ADVANCE_STATUS = "1305";
    private static final String DISBURSEMENT = "Disbursement";
    private static final String ISLAMIC_TYPE = "0002";
    private static final String APPLICATIONS_STATUS = "0106";
    private static final String APROVED_STATUS = "0004";
    private static final String SUBMITTED_STATUS = "0002";
    private static final String TURNAROUNDTIME = "TURNAROUNDTIME.jrxml";
    private static final String FEMALEPARTICIPATION = "FEMALEPARTICIPATION.jrxml";
    private static final String DUES = "DUES.jrxml";
    private static final String PORTFOLIO_SEG = "PORTFOLIO_SEG.jrxml";
    private static final String PORTFOLIO_AT_RISK = "PORTFOLIO_AT_RISK.jrxml";
    //Ended by Areeba
    private static final String RISKFLAG = "RISKFLAG.jrxml";
    private static final String PORTFOLIO_STAT_REP = "PORTFOLIO_STAT_REP.jrxml";
    private static final String PORTFOLIO_STAT_FROM = "PORTFOLIO_STAT_FROM.jrxml";
    //Ended by Areeba
    private static final String CONSOLIDATED_LOAN = "CONSOLIDATED_LOAN.jrxml";
    private static final String ROR = "ROR.jrxml";
    private static final String LOAN_UTI_FORM = "LOAN_UTI_FORM.jrxml";
    private static final String MONTHLY_STATUS = "MONTHLY_STATUS.jrxml";
    private static final String PORTFOLIO_RISK = "PORTFOLIO_RISK.jrxml";
    private static final String FEMALE_PARTICIPATION_RATIO = "FEMALE_PARTICIPATION_RATIO.jrxml";
    private static final String BRANCH_TARGET_MANG = "BRANCH_TARGET_MANG.jrxml";
    private static final String MCB_CHEQUE = "MCB_CHECQUE.jrxml";
    private static final String MLABAF = "MLABAF.jrxml";
    private static final String BOP_INFO = "BOP_CHECQUE.jrxml";
    private static final String REM_REPORT = "REM_RATIO_REP.jrxml";
    private static final String FUND_TRANSFER_REPORT = "FUND_REPORT.jrxml";
    private static final String DISBURSEMENT_REPORT = "DISBURSEMENT_REPORT.jrxml";
    private static final String RECOVERY_REPORT = "RECOVERY_REPORT.jrxml";
    private static final String REM_RATIO_REP = "REM_RATIO_REP.jrxml";
    private static final String ORG_TAGGING_REPORT = "ORG_TAGGING_REPORT.jrxml";
    private static final String PRODUCT_WISE_DISBURSEMENT = "PRODUCT_WISE_DISBURSEMENT.jrxml";
    private static final String ORG_TAG_TIME_REPORT = "ORG_TAGGING_TIME_REPORT.jrxml";
    private static final String ORG_TAGGING_TIME_REPORT = "ORG_TAGGING_TIME_REPORT.jrxml";
    private static final String REVRSAL_ENTERIES_REPORT = "REVRSAL_ENTERIES.jrxml";
    private static final String TRAIL_BALANCE_REPORT = "Trail_balance_report.jrxml";
    private static final String BRANCH_RANKING = "BRANCH_RANKING.jrxml";
    private static final String CPC_REPORT = "CPC_REPORT.jrxml";
    private static final String AICG_TRAINING = "AICG-TRAINING.jrxml";
    private static final String MONTHLY_RANKING = "MONTHLY_RANKING.jrxml";
    private static final String GESA = "GESA.jrxml";
    private static final String PAR_REPORT = "PAR_REPORT.jrxml";
    private static final String ADVANCE_RECOVERY_REPORT = "ADV_REC_REPORT.jrxml";
    private static final String ADVANCE_CLIENT_REPORT = "ADV_CLNT_REPORT.jrxml";
    private static final String ADVANCE_MATURITY_REPORT = "ADV_MATURITY_REPORT.jrxml";
    private static final String WEEKLY_TARGET_REPORT = "WEEKLY_TRGT_REPORT.jrxml";
    private static final String AREA_DISBURSEMENT_REPORT = "AREA_DIS_REPORT.jrxml";
    private static final String LEAVE_AND_ATTENDANCE_MONITORING_REPORT = "LEAVE_AND_ATTENDANCE_MONITORING_REPORT.jrxml";
    private static final String CLIENT_LOAN_MATRITY = "Clients_Loans_Maturity_Report.jrxml";
    private static final String MOBILE_WALLET_DUE = "Mobile-Wallet_Due.jrxml";
    private static final String MOBILE_WALLET = "Mobile-Wallet.jrxml";
    private static final String CLIENT_LOAN_MATRITY_EXCEL = "Clients_Loans_Maturity_Excel.jrxml";
    private static final String MOBILE_WALLET_DUE_EXCEL = "Mobile-Wallet_Due_Excel.jrxml";
    private static final String MOBILE_WALLET_EXCEL = "Mobile_Wallet_Excel.jrxml";
    private static final String Mfcib_Report = "Mfcib_Report.jrxml";
    private final Integer MaxResultSet = 10000;
    private final String UDERTAKING_REPORT = "UDERTAKING_REPORT.jrxml";
    private final String HEALTHCARD = "HEALTHCARD.jrxml";
    private final String LABAF = "LABAF.jrxml";
    private final String SLABAF = "SLABAF.jrxml";
    private final String LAF = "LAF.jrxml";
    private final String MABAF = "MABAF.jrxml";
    private final String REPAYMENT = "REPAYMENT.jrxml";
    private final String REPAYMENT_KTK = "REPAYMENT_KTK.jrxml";
    private final String OVERDUELOANS = "OVERDUELOANS.jrxml";
    private final String MONITORING = "MONITORING.jrxml";
    private final String POSTING = "POSTING.jrxml";
    private final String FUNDSTATEMENT = "FUNDSTATEMENT.jrxml";
    private final String INSURANCECLAIM = "INSURANCECLAIM.jrxml";
    //Added by Areeba
    private final String INSURANCECLAIM_DSBLTY = "INSURANCECLAIM_DSBLTY.jrxml";
    private final String ACCOUNTSLEDGER = "ACCOUNTSLEDGER.jrxml";
    private final String KCR = "KCR.jrxml";
    //Added by Areeba
    private final String KCR_DSBLTY = "KCR_DSBLTY.jrxml";
    private final String BOOKDETAILS = "BOOKDETAILS.jrxml";
    private final String DUERECOVERY = "DUERECOVERY.jrxml";
    private final String KSK_LPD = "KSK_LPD.jrxml";
    private final String WOMENPARTICIPATION = "WOMENPARTICIPATION.jrxml";
    private final String CLIENT_HEALTH_BENEFICIARY = "CLIENT_HEALTH_BENEFICIARY.jrxml";
    private final String INSURANCE_CLAIM = "INSURANCE_CLAIM_REPORT.jrxml";
    private final String BTAP = "BTAP.jrxml";
    private final String BM_BDO_RECOVERY = "BDO_Recovery_report.jrxml";
    private final String SALE_2_PENDING = "SALE_2_PENDING.jrxml";
    private final String MONITORING_NEW = "MONITORING_NEW.jrxml";
    private final String MCB_FUNDS = "MCB_Remittance_Disbursement_funds.jrxml";
    private final String MCB_FUNDS_EXCEL = "MCB_Remittance_Disbursement_funds_Excel.jrxml";
    private final String MCB_LOADER = "MCBRemittanceDisbursmentfundsLoader.jrxml";
    private final String MCB_LOADER_EXCEL = "MCBRemittanceDisbursmentfundsLoaderExcel.jrxml";
    private final String MCB_LETTER = "MCBRemittanceDisbursmentLetter.jrxml";
    private final String MCB_LETTER_EXCEL = "MCBRemittanceDisbursmentLetterExcel.jrxml";
    private final String CHEQUE_DISBURSMENT = "Cheques_Disbursment_Funds.jrxml";
    private final String CHEQUE_DISBURSMENT_EXCEL = "Cheques_Disbursment_Funds_Excel.jrxml";
    private final String EASYPAISA_FUNDS = "EasyPaisa_Mobile_Wallet_funds.jrxml";
    private final String EASYPAISA_FUNDS_EXCEL = "EasyPaisa_Mobile_Wallet_funds_Excel.jrxml";
    private final String EASYPAISA_LOADER = "EasyPaisa_Mobile_Wallet_funds_Loader.jrxml";
    private final String EASYPAISA_LOADER_EXCEL = "EasyPaisa_funds_Loader_Excel.jrxml";
    private final String EASYPAISA_LETTER = "EasyPaisa_Mobile_Wallet_funds_Letter.jrxml";
    private final String EASYPAISA_LETTER_EXCEL = "EasyPaisa_Mobile_Wallet_Letter_Excel.jrxml";
    private final String JAZZ_CASH_INFO_DUES = "Jazz_Cash-info_dues.jrxml";
    private final String JAZZ_CASH_INFO_DUES_EXCEL = "Jazz_Cash-info_dues_Excel.jrxml";
    private final String JAZZ_CASH_UPLOAD_DUES = "Jazz_Cash_dues_upload.jrxml";
    private final String JAZZ_CASH_UPLOAD_DUES_EXCEL = "Jazz_Cash_dues_upload_Excel.jrxml";
    private final String EASY_PAISA_DUES = "EasyPaisaDues.jrxml";
    private final String EASY_PAISA_DUES_EXCEL = "EasyPaisaDues_Excel.jrxml";
    private final String UBL_OMNI_DUES = "UBLOmniDues.jrxml";
    private final String UBL_OMNI_DUES_EXCEL = "UBLOmniDues_Excel.jrxml";
    private final String HBL_CONNECT_DUES = "HBLConnectDues.jrxml";
    private final String HBL_CONNECT_DUES_EXCEL = "HBLConnectDues_Excel.jrxml";
    private final String ABL_DUES = "ABLDues.jrxml";
    private final String ABL_DUES_EXCEL = "ABLDues_Excel.jrxml";
    private final String MCB_COLLECT_DUES = "MCBCollectDues.jrxml";
    private final String MCB_COLLECT_DUES_EXCEL = "MCBCollectDues_Excel.jrxml";
    private final String NADRA_DUES = "NadraDues.jrxml";
    private final String NADRA_DUES_EXCEL = "NadraDues_Excel.jrxml";
    private final String ONE_LINK_DEPOSIT_SLIP = "ONE_LINK_DEPOSIT_SLIP.jrxml";
    private final String REPAYMENT_KSWK_INFO = "REPAYMENT_KSWK_INFO.jrxml";
    private final String SPACE = " ";
    private final File file = null;
    private final String AGENCY_INFO = "AGENCY_INFO.jrxml";
    @Autowired
    PaymentScheduleService paymentScheduleService;
    @Autowired
    MwHlthInsrMembRepository mwHlthInsrMembRepository;
    @Autowired
    ReportComponent reportComponent;
    @Autowired
    MwMfcibOthOutsdLoanRepository mwMfcibOthOutsdLoanRepository;
    @Autowired
    MwClntHlthInsrCardRepository mwClntHlthInsrCardRepository;
    @Autowired
    MwClntRelRepository mwClntRelRepository;
    @Autowired
    LoanApplicationRepository loanApplicationRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    MwBizAprslRepository mwBizAprslRepository;
    @Autowired
    EntityManager em;
    @Autowired
    ServletContext context;
    @Autowired
    MwRefCdValRepository mwRefCdValRepository;
    @Autowired
    MwLedgerHeadsRepository mwLedgerHeadsRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static Date getFirstDateOfMonth(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getLastDateOfMonth(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    // Ended by Areeba

    public byte[] getRepaymentReport(long loanAppSeq, String currUser) throws IOException {
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);

        int vchrTyp = 0;
        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
            vchrTyp = 1;
        }

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

        Query qt = em.createNativeQuery(com.idev4.rds.util.Queries.TOTAL_RECEIVEABLE_AMOUNT).setParameter("loanAppSeq",
                loanAppSeq);
        Object[] qtObj = (Object[]) qt.getSingleResult();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Map<String, Object> params = new HashMap<>();
        params.put("TOTAL_AMT",
                ((qtObj[0] == null) ? 0L : new BigDecimal(qtObj[0].toString()).longValue())
                        + ((qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue())
                        + ((qtObj[2] == null) ? 0L : new BigDecimal(qtObj[2].toString()).longValue()));
        params.put("TOT_CHRG_DUE", qtObj[1] == null ? 0L : new BigDecimal(qtObj[1].toString()).longValue());
        params.put("other_chrgs", qtObj[2] == null ? 0L : new BigDecimal(qtObj[2].toString()).longValue());

        String ql;

        ql = readFile(Charset.defaultCharset(), "RepaymentReport.txt");
        Query rs = em.createNativeQuery(ql).setParameter("loanAppSeq", loanAppSeq).setParameter("vtyp", vchrTyp);

        /*
         * ql = em.createNativeQuery(
         * "select brnch.brnch_nm,emp.emp_nm bdo,prd.prd_cmnt, app.loan_id,clnt.clnt_id,clnt.frst_nm||' '||clnt.last_nm clnt_nm,clnt.spz_frst_nm||clnt.fthr_frst_nm ||'/'||clnt.spz_last_nm||clnt.fthr_last_nm fath, clnt.cnic_num, \r\n"
         * + "app.aprvd_loan_amt,pc1.chrg_val doc,slb.val \r\n" +
         * ",app.loan_cycl_num,to_char(nvl(vh.dsbmt_dt,sysdate),'dd-MM-yyyy') dt,clnt.clnt_seq,prd.irr_val   from mw_loan_app app   \r\n"
         * +
         * "join mw_dsbmt_vchr_hdr vh on vh.LOAN_APP_SEQ=app.LOAN_APP_SEQ and vh.CRNT_REC_FLG = 1  \r\n"
         * +
         * "join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1   \r\n"
         * +
         * "join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1  \r\n"
         * +
         * "join mw_port_emp_rel poer on poer.port_seq=port.port_seq and poer.crnt_rec_flg=1  \r\n"
         * + "join mw_emp emp on emp.emp_seq=poer.emp_seq   \r\n" +
         * "join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1   \r\n"
         * +
         * "join mw_prd prd on prd.prd_seq = app.prd_seq and prd.crnt_rec_flg=1   \r\n"
         * +
         * "join mw_prd_chrg pc on prd.prd_seq=pc.prd_seq and pc.crnt_rec_flg = 1   \r\n"
         * +
         * "join mw_typs pct on pct.TYP_SEQ=pc.CHRG_TYP_SEQ and pct.CRNT_REC_FLG=1 AND pct.TYP_ID='0017'  \r\n"
         * +
         * "join mw_prd_chrg_slb  slb on slb.prd_chrg_seq=pc.prd_chrg_seq and slb.crnt_rec_flg=1   \r\n"
         * +
         * "left outer join mw_prd_chrg pc1 on prd.prd_seq=pc1.prd_seq and pc1.crnt_rec_flg = 1 and pc1.chrg_typ_seq=1  \r\n"
         * +
         * "where app.crnt_rec_flg=1 and app.loan_app_seq =:loanAppSeq and vh.DSBMT_VCHR_TYP=:vtyp"
         * ) .setParameter( "loanAppSeq", loanAppSeq ).setParameter( "vtyp", vchrTyp );
         */

        /**
         * @Date: 28-11-2022
         * @Update: Naveed
         * @Description: Production Issue - Repayment Scheduled Report thrown error when not details found against loanAppSeq
         * @Fixation: return byte[0] then FrontEnd shown "Required data not available" message
         * */

        List<Object[]> resultList = rs.getResultList();

        if (resultList.isEmpty()) {
            return new byte[0];
        }

        Object[] obj = resultList.get(0);
        params.put("PRD_TYP", type.getRefCd());
        params.put("BRNCH_NM", obj[0].toString());
        params.put("bdo", obj[1] == null ? "" : obj[1].toString());
        params.put("PRD_NM", obj[2].toString());
        params.put("CLNT_SEQ", (obj[4] == null) ? "" : obj[4].toString());
        params.put("CLNT_NM", obj[5] == null ? "" : obj[5].toString());
        params.put("FTHR_NM", obj[6] == null ? "" : obj[6].toString());

        params.put("CNIC_NUM", obj[7].toString());

        params.put("APRVD_LOAN_AMT", (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue());

        params.put("TOT_DOC", obj[9] == null ? 0L : new BigDecimal(obj[9].toString()).longValue());

        params.put("CHRG_VAL", (double) (new BigDecimal(qtObj[1].toString()).doubleValue()
                / new BigDecimal(qtObj[0].toString()).doubleValue()) * 100);

        params.put("LOAN_CYCL_NUM", (obj[11] == null) ? 0 : new BigDecimal(obj[11].toString()).longValue());

        params.put("disbu_dt", obj[12].toString());
        loanAppSeq = app.getPrntLoanAppSeq();
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.REPAYMENT).setParameter("loanAppSeq", loanAppSeq)
                .setParameter("clntseq", obj[13].toString());
        List<Object[]> payments = q.getResultList();

        List<Map<String, ?>> paymentsList = new ArrayList();
        Long kszb = 0L;
        long inst = 0;
        String oldDate = "";
        for (Object[] r : payments) {
            if (!r[0].toString().equals(oldDate)) {
                inst++;
            }
            Map<String, Object> map = new HashMap();
            map.put("inst_num", inst);

            try {
                map.put("due_dt", new SimpleDateFormat("dd-MM-yyyy").format(inputFormat.parse(r[0].toString())));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            map.put("ppal_amt_due", r[1] == null ? 0 : new BigDecimal(r[1].toString()).longValue());
            map.put("tot_chrg_due", r[2] == null ? 0 : new BigDecimal(r[2].toString()).longValue());
            if (r[3] != null) {
                map.put("type", r[3] == null ? "" : r[3].toString());
                map.put("amt", r[4] == null ? 0 : getLongValue(r[4].toString()));
            }
            map.put("out_std", r[5] == null ? 0 : getLongValue(r[5].toString()));

            oldDate = r[0].toString();
            paymentsList.add(map);

        }

        params.put("irr_val", obj[14] == null ? 0d : (new BigDecimal(obj[14].toString()).doubleValue() * 12) * 100);
        params.put("INSURD_AMT", obj[15] == null ? 0 : new BigDecimal(obj[15].toString()).longValue());

        params.put("kszb", kszb);
        params.put("dataset", getJRDataSource(paymentsList));
        params.put("dataset1", getJRDataSource(paymentsList));
        params.put("curr_user", currUser);
        return reportComponent.generateReport(REPAYMENT, params, null);
    }

    //ADDED BY YOUSAF DATED: 14-OCT-2022
    public byte[] getRepaymentReportKtk(long loanAppSeq, String currUser) throws IOException {
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);

        int vchrTyp = 0;
        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
            vchrTyp = 1;
        }

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

        Query qt = em.createNativeQuery(com.idev4.rds.util.Queries.TOTAL_RECEIVEABLE_AMOUNT_KTK).setParameter("loanAppSeq",
                loanAppSeq);
        Object[] qtObj = (Object[]) qt.getSingleResult();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Map<String, Object> params = new HashMap<>();
        params.put("TOTAL_AMT",
                ((qtObj[0] == null) ? 0L : new BigDecimal(qtObj[0].toString()).longValue())
                        + ((qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue())
                        + ((qtObj[2] == null) ? 0L : new BigDecimal(qtObj[2].toString()).longValue()));
        params.put("TOT_CHRG_DUE", qtObj[1] == null ? 0L : new BigDecimal(qtObj[1].toString()).longValue());
        params.put("other_chrgs", qtObj[2] == null ? 0L : new BigDecimal(qtObj[2].toString()).longValue());

        String ql;

        ql = readFile(Charset.defaultCharset(), "RepaymentReportKTK.txt");
        Query rs = em.createNativeQuery(ql).setParameter("loanAppSeq", loanAppSeq).setParameter("vtyp", vchrTyp);

        Object[] obj = (Object[]) rs.getSingleResult();
        params.put("PRD_TYP", type.getRefCd());
        params.put("BRNCH_NM", obj[0].toString());
        params.put("bdo", obj[1] == null ? "" : obj[1].toString());
        params.put("PRD_NM", obj[2].toString());
        params.put("CLNT_SEQ", (obj[4] == null) ? "" : obj[4].toString());
        params.put("CLNT_NM", obj[5] == null ? "" : obj[5].toString());
        params.put("FTHR_NM", obj[6] == null ? "" : obj[6].toString());

        params.put("CNIC_NUM", obj[7].toString());

        params.put("APRVD_LOAN_AMT", (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue());

        params.put("TOT_DOC", obj[9] == null ? 0L : new BigDecimal(obj[9].toString()).longValue());

        params.put("CHRG_VAL", (double) (new BigDecimal(qtObj[1].toString()).doubleValue()
                / new BigDecimal(qtObj[0].toString()).doubleValue()) * 100);

        params.put("LOAN_CYCL_NUM", (obj[11] == null) ? 0 : new BigDecimal(obj[11].toString()).longValue());

        params.put("disbu_dt", obj[12].toString());
        loanAppSeq = app.getPrntLoanAppSeq();
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.REPAYMENT_KTK).setParameter("clntseq", obj[13].toString());
        List<Object[]> payments = q.getResultList();

        List<Map<String, ?>> paymentsList = new ArrayList();
        Long kszb = 0L;
        long inst = 0;
        String oldDate = "";
        for (Object[] r : payments) {
            if (!r[0].toString().equals(oldDate)) {
                inst++;
            }
            Map<String, Object> map = new HashMap();
            map.put("INST_NUM", inst);
            map.put("DUE_DT", r[0]);

            map.put("PPAL_AMT_DUE", r[1] == null ? 0 : new BigDecimal(r[1].toString()).longValue());
            map.put("TOT_CHRG_DUE", r[2] == null ? 0 : new BigDecimal(r[2].toString()).longValue());
            if (r[3] != null) {
                map.put("TYP_STR", r[3] == null ? "" : r[3].toString());
                map.put("AMT", r[4] == null ? 0 : getLongValue(r[4].toString()));
            }
            map.put("OST_PPL_KKK", r[5] == null ? 0 : getLongValue(r[5].toString()));
            map.put("OST_PPL_KTK", r[6] == null ? 0 : getLongValue(r[6].toString()));
            if (r[7] != null) {
                map.put("PRD_CMNT", r[7] == null ? "" : r[7].toString());
            }

            oldDate = r[0].toString();
            paymentsList.add(map);
        }

        // ADDED BY YOUSAF DATE: 14-OCT-2022
        //params.put("irr_val", obj[14] == null ? 0d : (new BigDecimal(obj[14].toString()).doubleValue() * 12) * 100);
        params.put("irr_val", obj[14] == null ? "" : obj[14].toString());
        params.put("INSURD_AMT", obj[15] == null ? 0 : new BigDecimal(obj[15].toString()).longValue());

        params.put("kszb", kszb);
        params.put("dataset", getJRDataSource(paymentsList));
        params.put("dataset1", getJRDataSource(paymentsList));
        params.put("curr_user", currUser);
        return reportComponent.generateReport(REPAYMENT_KTK, params, null);
    }

    // Added by Areeba
    public byte[] getRepaymentInfoKSWKReport(String currUser) {

        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", currUser);

        return reportComponent.generateReport(REPAYMENT_KSWK_INFO, params, null);
    }

    public byte[] getUndertakingReport(long loanAppSeq, String currUser) throws IOException {

        String q;

        q = readFile(Charset.defaultCharset(), "UNDERTAKING_INFO_QUERY.txt");
        Query rs = em.createNativeQuery(q).setParameter("loanAppSeq", loanAppSeq);

        // Query q = em.createNativeQuery(
        // com.idev4.rds.util.Queries.UNDERTAKING_INFO_QUERY ).setParameter(
        // "loanAppSeq", loanAppSeq );
        Object[] obj = (Object[]) rs.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", currUser);
        params.put("CLNT_NM",
                (obj[1] != null ? obj[1].toString() : "") + SPACE + (obj[2] != null ? obj[2].toString() : ""));
        params.put("FTHR_NM",
                (obj[3] != null ? obj[3].toString() : "") + SPACE + (obj[4] != null ? obj[4].toString() : ""));
        params.put("CLNT_ID", obj[5].toString());
        params.put("CNIC_NUM", obj[6]);
        params.put("BRNCH_NM", obj[8]);
        params.put("AREA_NM", obj[9]);
        params.put("REG_NM", obj[10]);

        String qt;

        qt = readFile(Charset.defaultCharset(), "TOTAL_RECEIVEABLE_AMOUNT.txt");
        Query rset = em.createNativeQuery(qt).setParameter("loanAppSeq", loanAppSeq);

        // Query qt = em.createNativeQuery(
        // com.idev4.rds.util.Queries.TOTAL_RECEIVEABLE_AMOUNT ).setParameter(
        // "loanAppSeq", loanAppSeq );
        Object[] qtObj = (Object[]) rset.getSingleResult();

        MwMfcibOthOutsdLoan loan = new MwMfcibOthOutsdLoan();
        loan.setInstnNm("Kashf Foundation");
        loan.setLoanPrps(
                (obj[12] == null ? "" : obj[12].toString()) + " " + (obj[13] == null ? "" : obj[13].toString()));
        if (new BigDecimal(obj[11].toString()).longValue() == 5153L
                || new BigDecimal(obj[11].toString()).longValue() == 4513L) {
            loan.setLoanPrps("SCHOOL");
        }
        loan.crntOutsdAmt(((qtObj[0] == null) ? 0L : new BigDecimal(qtObj[0].toString()).longValue())
                + ((qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue())
                + ((qtObj[2] == null) ? 0L : new BigDecimal(qtObj[2].toString()).longValue()));

        List<MwMfcibOthOutsdLoan> mfcib = new ArrayList();
        mfcib.add(0, loan);
        mfcib.addAll(mwMfcibOthOutsdLoanRepository.findAllByLoanAppSeqAndCrntRecFlg(loanAppSeq, true));

        return reportComponent.generateReport(UDERTAKING_REPORT, params, getJRDataSource(mfcib));
    }

    public byte[] getHealthCardReport(long loanAppSeq, String currUser) throws IOException {
        String q;

        q = readFile(Charset.defaultCharset(), "HLTH_INSR_CARD_QUERY.txt");
        Query rs = em.createNativeQuery(q).setParameter("loanAppSeq", loanAppSeq);

        // Query q = em.createNativeQuery(
        // com.idev4.rds.util.Queries.HLTH_INSR_CARD_QUERY ).setParameter( "loanAppSeq",
        // loanAppSeq );
        Object[] obj = (Object[]) rs.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("CARD_NO", obj[0] == null ? "" : obj[0].toString());
        params.put("CERT_NUM",
                obj[0] == null ? "" : "00" + obj[0].toString().substring(obj[0].toString().length() - 5));
        params.put("PLCY_NUM", obj[0] == null ? "" : obj[0].toString().substring(2, obj[0].toString().length() - 5));
        params.put("CARD_EXPIRY_DT", obj[14]);
        params.put("CUST_NO", obj[2].toString());
        params.put("CLNT_NM",
                (obj[3] == null ? "" : obj[3].toString()) + SPACE + (obj[4] == null ? "" : obj[4].toString()));
        params.put("DOB", obj[5]);
        params.put("CNIC_NUM", obj[9]);
        params.put("BRNCH_NM", obj[11]);
        params.put("MEMB_OF", "KASHF FOUNDATION");
        params.put("MAX_PLCY_AMT", obj[12]);

        // Added by Zohaib Asim - Dated 24-12-2020
        // KSZB Claims
        params.put("ROOM_LIMT", obj[17]);

        /*
         * Commented by Zohaib Asim - Dated 24-12-2020 Query will display the
         * description
         *
         * if ( new BigDecimal( obj[ 15 ].toString() ).longValue() <= 2019 ) { if ( obj[
         * 13 ].toString().equals( "0001" ) || obj[ 13 ].toString().equals( "1323" ) ||
         * obj[ 13 ].toString().equals( "1324" ) ) { params.put( "ROOM_LIMT",
         * "General Ward C-Section Limit : 20,000" ); } else { params.put( "ROOM_LIMT",
         * "General Ward" ); } } else { if ( obj[ 13 ].toString().equals( "0001" ) ||
         * obj[ 13 ].toString().equals( "1323" ) || obj[ 13 ].toString().equals( "1324"
         * ) ) { params.put( "ROOM_LIMT", "General Ward C-Section Limit : 25,000" ); }
         * else { params.put( "ROOM_LIMT", "General Ward" ); } } End by Zohaib Asim
         */

        // if ( new BigDecimal( obj[ 15 ].toString() ).longValue() <= 2019 ) {
        // params.put( "ROOM_LIMT", "General Ward" + ( obj[ 13 ].toString().equals(
        // "0001" ) ? "C-Section Limit : 20,000" : "" ) );
        // } else {
        // params.put( "ROOM_LIMT", "General Ward" + ( obj[ 13 ].toString().equals(
        // "0001" ) ? "C-Section Limit : 25,000" : "" ) );
        // }

        // params.put( "ROOM_LIMT", "General Ward" + ( obj[ 13 ].toString().equals(
        // "0001" ) ? "C-Section Limit : 25,000" : "" ) );

        // Added by Areeba - Dated 28-1-2022 - Age Limit Issue
        /*List<MwHlthInsrMemb> mem = mwHlthInsrMembRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq);
		params.put("DATASET", getJRDataSource(mem));*/
        String kszbInsuranceMembCard;
        kszbInsuranceMembCard = readFile(Charset.defaultCharset(), "KSZB_INSURANCE_MEMB_CARD.txt");
        Query rs2 = em.createNativeQuery(kszbInsuranceMembCard).setParameter("loanAppSeq", loanAppSeq);

        List<Object[]> kszbInsuranceMembCardLists = rs2.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        kszbInsuranceMembCardLists.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("hlth_insr_memb_seq", l[0] == null ? 0 : new BigDecimal(l[0].toString()).longValue());
            map.put("eff_start_dt", l[1]);
            map.put("member_cnic_num", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("memberNm", l[3] == null ? "" : l[3].toString());
            map.put("dob", l[4] == null ? "" : l[4].toString());
            map.put("gndr_key", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("rel_key", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("mrtl_sts_key", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("crtd_by", l[8] == null ? "" : l[8].toString());
            map.put("crtd_dt", l[9]);
            map.put("last_upd_by", l[10] == null ? "" : l[10].toString());
            map.put("last_upd_dt", l[11]);
            map.put("del_flg", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("eff_end_dt", l[13]);
            map.put("crnt_rec_flg", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            map.put("loan_app_seq", l[15] == null ? 0 : new BigDecimal(l[15].toString()).longValue());
            map.put("member_id", l[16] == null ? "" : l[16].toString());
            map.put("sync_flg", l[17] == null ? 0 : new BigDecimal(l[17].toString()).longValue());

            reportParams.add(map);

        });
        params.put("DATASET", getJRDataSource(reportParams));
        //Ended by Areeba - Dated 28-1-2022 - Age Limit Issue


        return reportComponent.generateReport(HEALTHCARD, params, null);
    }

    public byte[] getClientInfoReport(long loanAppSeq, String currUser) throws IOException {
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);

        int vchrTyp = 0;
        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
            vchrTyp = 1;
        }

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        loanAppSeq = app.getPrntLoanAppSeq();

        File file = null;
        String REPORTS_BASEPATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator
                + "reports" + file.separator;
        // String REPORTS_BASEPATH = "D:\\Report\\";
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        String q;
        q = readFile(Charset.defaultCharset(), "ClientInfoReport.txt");
        Query rs = em.createNativeQuery(q).setParameter("loanAppSeq", loanAppSeq).setParameter("vchrTyp", vchrTyp);

        /*
         * Query q = em.createNativeQuery(
         * "select app.loan_id,prd.prd_id,prd.prd_cmnt,app.loan_cycl_num,app.rqstd_loan_amt,app.aprvd_loan_amt,mrtl.ref_cd_dscr mrtl,occ.ref_cd_dscr occ,res.ref_cd_dscr res,clnt.yrs_res,clnt.hse_hld_memb,clnt.num_of_chldrn,clnt.num_of_erng_memb,clnt.frst_nm, clnt.last_nm, clnt.clnt_id, clnt.cnic_num,port.port_nm,brnch.brnch_cd,brnch.brnch_nm, area.area_nm, reg.reg_nm,e.emp_nm,to_char(app.crtd_dt,'dd-MM-yyyy') crtd_dt,be.emp_nm bm_name,clnt.clnt_seq,clnt.MNTHS_RES,app.TBL_SCRN_FLG,clnt.biz_dtl\r\n"
         * +
         * ",to_char(nvl(vh.dsbmt_dt,sysdate),'dd-MM-yyyy') dt ,cs.crdt_rsk_ctgry,(case when brnch.CS_FLG=1 and prd.CS_FLG=1 then 1 else 0 end) cs_flg \r\n"
         * + " from mw_loan_app app \r\n" +
         * " join mw_dsbmt_vchr_hdr vh on vh.LOAN_APP_SEQ=app.LOAN_APP_SEQ and vh.CRNT_REC_FLG = 1\r\n"
         * +
         * " join mw_prd prd on prd.prd_seq=app.prd_seq and prd.crnt_rec_flg=1 and prd.del_flg=0 \r\n"
         * +
         * " join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
         * +
         * " join mw_ref_cd_val mrtl on clnt.mrtl_sts_key=mrtl.ref_cd_seq and mrtl.crnt_rec_flg=1 and mrtl.del_flg=0 \r\n"
         * +
         * " join mw_ref_cd_val occ on clnt.occ_key=occ.ref_cd_seq and occ.crnt_rec_flg=1 and occ.del_flg=0 \r\n"
         * +
         * " join mw_ref_cd_val res on clnt.res_typ_key=res.ref_cd_seq and res.crnt_rec_flg=1 and res.del_flg=0  \r\n"
         * +
         * " join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1   \r\n"
         * +
         * " join mw_port_emp_rel per on per.port_seq=port.port_seq and per.crnt_rec_flg=1  \r\n"
         * + " join MW_EMP e on e.EMP_SEQ=per.EMP_SEQ  \r\n" +
         * " join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1 \r\n"
         * +
         * " join mw_brnch_emp_rel ber on brnch.brnch_seq=ber.brnch_seq and ber.crnt_rec_flg=1  and ber.del_flg=0\r\n"
         * + " join MW_EMP be on be.EMP_SEQ=ber.EMP_SEQ  \r\n" +
         * " join mw_area area on area.area_seq=brnch.area_seq and area.crnt_rec_flg=1    \r\n"
         * + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1  \r\n"
         * +
         * "left outer join mw_loan_app_crdt_scr cs on  cs.LOAN_APP_SEQ = app.LOAN_APP_SEQ and cs.CRNT_REC_FLG = 1 \r\n"
         * +
         * " where app.crnt_rec_flg=1 and app.loan_app_seq =:loanAppSeq and vh.DSBMT_VCHR_TYP=:vchrTyp"
         * )
         */

        /**
         * @Date: 28-11-2022
         * @Update: Naveed
         * @Description: Production Issue - LABAF Reports thrown error when not details found against loanAppSeq
         * @Fixation: return byte[0] then FrontEnd shown "Required data not available" message
         * */

        List<Object[]> results = rs.getResultList();

        Map<String, Object> params = new HashMap<String, Object>();

        if (results.isEmpty()) {
            return new byte[0];
        }

        params.put("loan_id", results.get(0)[0].toString());
        params.put("prd_seq", results.get(0)[1].toString());
        params.put("prd_nm", results.get(0)[2].toString());
        params.put("loan_cycl_num", results.get(0)[3].toString());
        params.put("rqstd_loan_amt", new BigDecimal(results.get(0)[4].toString()).longValue());
        params.put("aprvd_loan_amt",
                results.get(0)[5] == null ? 0L : new BigDecimal(results.get(0)[5].toString()).longValue());
        params.put("mrtl", results.get(0)[6].toString());
        params.put("occ", results.get(0)[7].toString());
        params.put("res", results.get(0)[8].toString());
        params.put("yrs_res", results.get(0)[9].toString());
        params.put("num_of_dpnd", new BigDecimal(results.get(0)[10].toString()).longValue());
        params.put("num_of_chldrn", new BigDecimal(results.get(0)[11].toString()).longValue());
        params.put("num_of_erng_memb", new BigDecimal(results.get(0)[12].toString()).longValue());
        params.put("frst_nm", results.get(0)[13] == null ? "" : results.get(0)[13].toString());
        params.put("last_nm", results.get(0)[14] == null ? "" : results.get(0)[14].toString());
        params.put("clnt_id", results.get(0)[15] == null ? "" : results.get(0)[15].toString());
        params.put("cnic_num", results.get(0)[16].toString());
        params.put("port_nm", results.get(0)[24].toString());
        params.put("brnch_cd", results.get(0)[18].toString());
        params.put("brnch_nm", results.get(0)[19].toString());
        params.put("area_nm", results.get(0)[20].toString());
        params.put("reg_nm", results.get(0)[21].toString());
        params.put("bdo", results.get(0)[22].toString());
        params.put("mnth_res", results.get(0)[26] == null ? "0" : results.get(0)[26].toString());
        params.put("scrn_flg",
                results.get(0)[27] != null && new BigDecimal(results.get(0)[27].toString()).longValue() == 1L ? "Field"
                        : "Table");
        params.put("biz_dtl", results.get(0)[28] == null ? "" : results.get(0)[28].toString());
        params.put("apprv_dt", results.get(0)[23].toString());
        params.put("disbu_dt", results.get(0)[29].toString());
        params.put("crdt_rsk_ctgry", results.get(0)[30] == null ? "" : results.get(0)[30].toString());
        params.put("cs_flg",
                results.get(0)[31] == null ? 0 : new BigDecimal(results.get(0)[31].toString()).longValue());
        params.put("prev_loan_amt", 0L);
        if (new BigDecimal(results.get(0)[3].toString()).longValue() >= 2) {
            LoanApplication preapp = loanApplicationRepository.findOneByClntSeqAndCrntRecFlgAndLoanCyclNum(
                    new BigDecimal(results.get(0)[25].toString()).longValue(), true,
                    new BigDecimal(results.get(0)[3].toString()).longValue() - 1);
            params.put("prev_loan_amt",
                    (preapp == null) ? 0 : ((preapp.getAprvdLoanAmt() == null) ? 0 : preapp.getAprvdLoanAmt()));
        }
        params.put("fthr_frst_nm", results.get(0)[32] == null ? "" : results.get(0)[32].toString());
        params.put("fthr_last_nm", results.get(0)[33] == null ? "" : results.get(0)[33].toString());

        String qt;
        qt = readFile(Charset.defaultCharset(), "TOTAL_RECEIVEABLE_AMOUNT.txt");
        Query rs1 = em.createNativeQuery(qt).setParameter("loanAppSeq", loanAppSeq);

        // Query qt = em.createNativeQuery(
        // com.idev4.rds.util.Queries.TOTAL_RECEIVEABLE_AMOUNT ).setParameter(
        // "loanAppSeq", loanAppSeq );
        Object[] qtObj = (Object[]) rs1.getSingleResult();

        if (qtObj != null) {
            params.put("li_ttl",
                    ((qtObj[0] == null) ? 0L : new BigDecimal(qtObj[0].toString()).longValue())
                            + ((qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue())
                            + ((qtObj[2] == null) ? 0L : new BigDecimal(qtObj[2].toString()).longValue()));
            params.put("li_servc", (qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue());
        }

        String instQry;
        instQry = readFile(Charset.defaultCharset(), "clientInfoInstQuery.txt");
        Query rs2 = em.createNativeQuery(instQry).setParameter("loanAppSeq", loanAppSeq);

        /*
         * Query instQry = em.createNativeQuery(
         * "select psd.due_dt, psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) amt\r\n" +
         * "from mw_pymt_sched_hdr psh\r\n" +
         * "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 and psd.del_flg=0\r\n"
         * +
         * "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 and psc.del_flg=0\r\n"
         * +
         * "where psh.loan_app_seq=:loanAppSeq and psh.crnt_rec_flg=1 and psh.del_flg=0\r\n"
         * +
         * "group by psd.inst_num,psd.due_dt,psd.ppal_amt_due,psd.tot_chrg_due order by psd.inst_num"
         * ) .setParameter( "loanAppSeq", loanAppSeq );
         */

        try {
            List<Object[]> isntList = rs2.getResultList();
            if (isntList != null && isntList.size() > 0) {
                params.put("li_inst_no", isntList.size());
                params.put("li_inst_amt", new BigDecimal(isntList.get(1)[1].toString()).longValue()
                        + new BigDecimal(isntList.get(1)[2].toString()).longValue()
                        + (isntList.get(1)[3] == null ? 0 : new BigDecimal(isntList.get(1)[3].toString()).longValue()));
                try {
                    params.put("li_com_dt", new SimpleDateFormat("dd-MM-yyyy")
                            .format(inputFormat.parse(isntList.get(isntList.size() - 1)[0].toString())));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String kszbQry;
        kszbQry = readFile(Charset.defaultCharset(), "clientInfoKSZBQuery.txt");
        Query rs3 = em.createNativeQuery(kszbQry).setParameter("loanAppSeq", loanAppSeq);

        /*
         * Query kszbQry = em.createNativeQuery(
         * "select hip.ANL_PREM_AMT chrg_val,hip.PLAN_NM typ_str\r\n" +
         * "from mw_clnt_hlth_insr chi\r\n" +
         * "join mw_hlth_insr_plan hip on hip.hlth_insr_plan_seq = chi.hlth_insr_plan_seq and hip.crnt_rec_flg=1 and hip.del_flg=0\r\n"
         * + " where LOAN_APP_SEQ=:loanAppSeq and chi.CRNT_REC_FLG=1 and chi.del_flg=0"
         * ).setParameter( "loanAppSeq", loanAppSeq );
         */
        Object[] kszb = null;
        try {
            List<Object[]> kszbResults = rs3.getResultList();
            if (kszbResults != null && kszbResults.size() > 0) {
                kszb = kszbResults.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("li_kszb", kszb == null ? "" : kszb[1].toString());

        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 10L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 11L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 12L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 39L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 40L)) {

            String takfulQry;
            takfulQry = readFile(Charset.defaultCharset(), "ClientInfoTkafulQuery.txt");
            Query rs4 = em.createNativeQuery(takfulQry).setParameter("loanAppSeq", loanAppSeq);

            /*
             * Query takfulQry = em.createNativeQuery( "select sum(psc.amt) amt\r\n" +
             * "from mw_pymt_sched_hdr psh\r\n" +
             * "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
             * +
             * "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
             * +
             * "left outer join mw_typs t on t.TYP_SEQ= psc.CHRG_TYPS_SEQ and t.TYP_ID='0018' \r\n"
             * + "where psh.loan_app_seq=:loanAppSeq and psh.crnt_rec_flg=1" ).setParameter(
             * "loanAppSeq", loanAppSeq );
             */
            Object takfulObj = rs4.getSingleResult();
            params.put("takaful", (takfulObj == null) ? 0L : new BigDecimal(takfulObj.toString()).longValue());
        }

        String bizApprQury;
        bizApprQury = readFile(Charset.defaultCharset(), "ClientInfobizApprQury.txt");
        Query rs4 = em.createNativeQuery(bizApprQury).setParameter("loanAppSeq", loanAppSeq);

        /*
         * Query bizApprQury = em.createNativeQuery(
         * " select ba.biz_aprsl_seq,ba.yrs_in_biz,ba.mnth_in_biz,biz.ref_cd_dscr biz,prsn.ref_cd_dscr prsn,sect.biz_sect_nm sect,acty.biz_acty_nm acty, \r\n"
         * +
         * "(case when ba.BIZ_ADDR_SAME_AS_HOME_FLG=1 then 'Same as Client Address' else 'H No '||ad.HSE_NUM ||' ,St No '|| ad.STRT||' ,'|| ad.oth_dtl ||' ,'||city.CITY_NM||','||dist.dist_nm||','||cntry.cntry_nm end) addr\r\n"
         * + "from mw_biz_aprsl  ba\r\n" +
         * "join mw_ref_cd_val biz on ba.biz_own=biz.ref_cd_seq and biz.crnt_rec_flg=1 and biz.del_flg=0\r\n"
         * +
         * "join mw_ref_cd_val prsn on ba.prsn_run_the_biz=prsn.ref_cd_seq and prsn.crnt_rec_flg=1 and prsn.del_flg=0\r\n"
         * +
         * "join mw_biz_acty acty on ba.acty_key=acty.biz_acty_seq and acty.crnt_rec_flg=1 and acty.del_flg=0\r\n"
         * +
         * "join mw_biz_sect sect on sect.BIZ_SECT_SEQ=acty.BIZ_SECT_SEQ and sect.crnt_rec_flg=1 and sect.del_flg=0\r\n"
         * +
         * "left outer join mw_addr_rel addrRel on addrrel.enty_key = ba.biz_aprsl_seq and addrRel.enty_typ='Business' and addrRel.crnt_rec_flg=1 and addrRel.del_flg=0\r\n"
         * +
         * "left outer join mw_addr ad on ad.addr_seq = addrRel.addr_seq and ad.crnt_rec_flg=1 and ad.del_flg=0\r\n"
         * +
         * "left outer join mw_city_uc_rel rel on rel.city_uc_rel_seq = ad.city_seq and rel.crnt_rec_flg=1 and rel.del_flg = 0\r\n"
         * +
         * "left outer join mw_uc uc on rel.uc_SEQ =uc.UC_SEQ  and uc.del_flg = 0 and uc.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_thsl thsl on uc.thsl_SEQ =thsl.thsl_SEQ and thsl.del_flg = 0 and thsl.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_dist dist on thsl.dist_SEQ =dist.dist_SEQ and dist.del_flg = 0 and dist.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_st st on dist.st_SEQ =st.st_SEQ and st.del_flg = 0  and st.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_cntry cntry on st.cntry_SEQ =cntry.cntry_SEQ and cntry.crnt_rec_flg = 1 and cntry.del_flg=0\r\n"
         * +
         * "left outer join mw_city city on city.city_seq = rel.city_seq  and city.del_flg = 0  and city.crnt_rec_flg = 1\r\n"
         * + "where ba.loan_app_seq=:loanAppSeq and ba.crnt_rec_flg=1" ) .setParameter(
         * "loanAppSeq", loanAppSeq );
         */

        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 5153L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 4513L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 37L)) {
            // Added By Naveed 7-10-2021
            // 37L for KSS 24M prd

            // String bizApprQury;w
            bizApprQury = readFile(Charset.defaultCharset(), "ClientInfobizApprQury2.txt");
            rs4 = em.createNativeQuery(bizApprQury).setParameter("loanAppSeq", loanAppSeq);

            /*
             * bizApprQury = em.createNativeQuery(
             * " select ba.sch_aprsl_seq,ba.sch_yr,'' mnth_in_biz,biz.ref_cd_dscr biz,prsn.ref_cd_dscr prsn,'SCHOOL' sect,'School' acty \r\n"
             * +
             * " ,'H No '||ad.HSE_NUM ||' ,St No '|| ad.STRT||' ,'|| ad.oth_dtl ||' ,'||city.CITY_NM||','||dist.dist_nm||','||cntry.cntry_nm addr\r\n"
             * + "from mw_sch_aprsl  ba\r\n" +
             * "join mw_ref_cd_val biz on ba.sch_own_typ_key=biz.ref_cd_seq and biz.crnt_rec_flg=1 and biz.del_flg=0\r\n"
             * +
             * "join mw_ref_cd_val prsn on ba.rel_wth_own_key=prsn.ref_cd_seq and prsn.crnt_rec_flg=1 and prsn.del_flg=0\r\n"
             * +
             * "join mw_addr_rel addrRel on addrrel.enty_key = ba.sch_aprsl_seq and addrRel.enty_typ='SchoolAppraisal' and addrRel.crnt_rec_flg=1 and addrRel.del_flg=0\r\n"
             * +
             * "join mw_addr ad on ad.addr_seq = addrRel.addr_seq and ad.crnt_rec_flg=1 and ad.del_flg=0\r\n"
             * +
             * "join mw_city_uc_rel rel on rel.city_uc_rel_seq = ad.city_seq and rel.crnt_rec_flg=1 and rel.del_flg = 0\r\n"
             * +
             * "join mw_uc uc on rel.uc_SEQ =uc.UC_SEQ  and uc.del_flg = 0 and uc.crnt_rec_flg = 1\r\n"
             * +
             * "join mw_thsl thsl on uc.thsl_SEQ =thsl.thsl_SEQ and thsl.del_flg = 0 and thsl.crnt_rec_flg = 1\r\n"
             * +
             * "join mw_dist dist on thsl.dist_SEQ =dist.dist_SEQ and dist.del_flg = 0 and dist.crnt_rec_flg = 1\r\n"
             * +
             * "join mw_st st on dist.st_SEQ =st.st_SEQ and st.del_flg = 0  and st.crnt_rec_flg = 1\r\n"
             * +
             * "join mw_cntry cntry on st.cntry_SEQ =cntry.cntry_SEQ and cntry.crnt_rec_flg = 1 and cntry.del_flg=0\r\n"
             * +
             * "join mw_city city on city.city_seq = rel.city_seq  and city.del_flg = 0  and city.crnt_rec_flg = 1\r\n"
             * + "where ba.loan_app_seq=:loanAppSeq and ba.crnt_rec_flg=1\r\n" + "" )
             * .setParameter( "loanAppSeq", loanAppSeq );
             */
        }

        List<Object[]> bizApprList = rs4.getResultList();
        if (bizApprList.size() > 0) {
            params.put("biz_aprsl_seq", bizApprList.get(0)[0].toString());
            params.put("yrs_in_biz", bizApprList.get(0)[1].toString());
            params.put("mnth_in_biz", bizApprList.get(0)[2] == null ? "" : bizApprList.get(0)[2].toString());
            params.put("biz", bizApprList.get(0)[3].toString());
            params.put("prsn", bizApprList.get(0)[4].toString());
            params.put("sect", bizApprList.get(0)[5] == null ? "" : bizApprList.get(0)[5].toString());
            params.put("acty", bizApprList.get(0)[6] == null ? "" : bizApprList.get(0)[6].toString());
            params.put("biz_addr", bizApprList.get(0)[7].toString());
        }

        String bizApprSummry;
        bizApprSummry = readFile(Charset.defaultCharset(), "ClientInfobizAppSummaryQuery.txt");
        rs4 = em.createNativeQuery(bizApprSummry).setParameter("loanAppSeq", loanAppSeq);
        /*
         * Query bizApprSummry = em .createNativeQuery(
         * " select biz_inc,biz_exp,prm_incm hfs_incm,sec_incm,hsld_exp,ndi from vw_loan_app where loan_app_seq=:loanAppSeq"
         * ) .setParameter( "loanAppSeq", loanAppSeq );
         */
        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 5153L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 4513L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 37L)) {
            // Added By Naveed 7-10-2021
            // 37L for KSS 24M prd
            /*
             * bizApprSummry = em.createNativeQuery(
             * "select tot_fee,sch_biz_exp,sch_prm_incm,sch_sec_incm,sch_hsld_exp,sch_profit+sch_prm_incm+sch_sec_incm - sch_hsld_exp ndi\r\n"
             * + "from vw_loan_app where loan_app_seq =:loanAppSeq" ) .setParameter(
             * "loanAppSeq", loanAppSeq );
             */
            bizApprSummry = readFile(Charset.defaultCharset(), "ClientInfobizAppSummaryQuery2.txt");
            rs4 = em.createNativeQuery(bizApprSummry).setParameter("loanAppSeq", loanAppSeq);
        }

        List<Object[]> bizApprSummryList = rs4.getResultList();
        if (bizApprSummryList != null && bizApprSummryList.get(0)[0] != null) {
            long prfit = new BigDecimal(bizApprSummryList.get(0)[0].toString()).longValue()
                    - new BigDecimal(bizApprSummryList.get(0)[1].toString()).longValue();

            params.put("busi_incm", new BigDecimal(bizApprSummryList.get(0)[0].toString()).longValue());
            params.put("busi_exp", new BigDecimal(bizApprSummryList.get(0)[1].toString()).longValue());
            params.put("busi_prft", prfit);
            params.put("hfs_incm", new BigDecimal(bizApprSummryList.get(0)[2].toString()).longValue());
            params.put("ttl_p_incm", new BigDecimal(bizApprSummryList.get(0)[2].toString()).longValue() + prfit);
            params.put("sec_incm", new BigDecimal(bizApprSummryList.get(0)[3].toString()).longValue());
            params.put("ttl_incm", new BigDecimal(bizApprSummryList.get(0)[3].toString()).longValue()
                    + new BigDecimal(bizApprSummryList.get(0)[2].toString()).longValue() + prfit);
            params.put("hsld_exp", new BigDecimal(bizApprSummryList.get(0)[4].toString()).longValue());
            params.put("ndi", new BigDecimal(bizApprSummryList.get(0)[5].toString()).longValue());
        }

        String bizBreakupQury;
        bizBreakupQury = readFile(Charset.defaultCharset(), "ClientInfobizBreakupQury.txt");
        rs4 = em.createNativeQuery(bizBreakupQury).setParameter("loanAppSeq", loanAppSeq);

        /*
         * Query bizBreakupQury = em.createNativeQuery(
         * "select case when dtl.incm_ctgry_key=1 then 'Primary Income' else 'Secondary Income' end incm_ctgry_key,val.ref_cd_dscr ,dtl.incm_amt\r\n"
         * + "from mw_biz_aprsl_incm_dtl dtl\r\n" +
         * "join mw_biz_aprsl_incm_hdr hdr on hdr.incm_hdr_seq=dtl.incm_hdr_seq and hdr.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_biz_aprsl ba on ba.biz_aprsl_seq = hdr.biz_aprsl_seq and ba.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq=incm_typ_key and val.crnt_rec_flg = 1\r\n"
         * + "where dtl.crnt_rec_flg=1 and ba.loan_app_seq=:loanAppSeq\r\n" +
         * "union\r\n" +
         * "select case when dtl.incm_ctgry_key=1 then 'Primary Income' else 'Secondary Income' end incm_ctgry_key\r\n"
         * + ",val.ref_cd_dscr \r\n" + ",dtl.incm_amt\r\n" +
         * "from mw_biz_aprsl_incm_dtl dtl\r\n" +
         * "join mw_sch_aprsl ba on ba.sch_aprsl_seq = dtl.incm_hdr_seq and ba.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq=incm_typ_key and val.crnt_rec_flg = 1\r\n"
         * + "where dtl.crnt_rec_flg=1 and ba.loan_app_seq=:loanAppSeq" ) .setParameter(
         * "loanAppSeq", loanAppSeq );
         */

        List<Object[]> bizBreakupList = rs4.getResultList();

        List<Map<String, ?>> bizDtlList = new ArrayList();
        bizBreakupList.forEach(d -> {
            Map<String, Object> map = new HashMap();
            map.put("incm_ctgry_key", d[0] == null ? "" : d[0].toString());
            map.put("ref_cd_dscr", d[1] == null ? "" : d[1].toString());
            map.put("incm_amt", d[2] == null ? 0 : new BigDecimal(d[2].toString()).longValue());
            bizDtlList.add(map);
        });
        params.put("bizdtlbreakup", getJRDataSource(bizDtlList));

        params.put("curr_user", currUser);

        // relations

        String clntRelsQury;
        clntRelsQury = readFile(Charset.defaultCharset(), "ClientInfoclntRelsQury.txt");
        rs4 = em.createNativeQuery(clntRelsQury).setParameter("loanAppSeq", loanAppSeq);
        /*
         * Query clntRelsQury = em.createNativeQuery(
         * "select c.clnt_seq seq,cnic_num, cnic_expry_dt, frst_nm, last_nm, ph_num,dob,gndr.ref_cd_dscr gndr,'Client' rel_typ,0 rel_typ_flg,'SELF' rel,\r\n"
         * +
         * "(case when hse_num is not null then 'H No '||hse_num||', ' else null end)|| (case when strt is not null then 'St No '||strt||', ' else null end) || (case when oth_dtl is not null then oth_dtl||', ' else null end)  || (case when vlg is not null then vlg||', ' else null end) ||  city_nm addr,'' FTHR_NM,c.co_bwr_san_flg  co_bwr_san_flg,c.slf_pdc_flg slf_pdc_flg,0 CO_BWR_ADDR_AS_CLNT_FLG   \r\n"
         * + "from mw_clnt c \r\n" +
         * "join mw_loan_app la on la.clnt_seq=c.clnt_seq and c.crnt_rec_flg=1\r\n" +
         * "join mw_ref_cd_val gndr on c.gndr_key=gndr.ref_cd_seq and gndr.crnt_rec_flg=1 and gndr.del_flg=0\r\n"
         * +
         * "join mw_addr_rel addrrel on addrrel.enty_key = c.clnt_seq and addrrel.crnt_rec_flg = 1  and addrrel.del_flg = 0 and addrrel.enty_typ = 'Client'\r\n"
         * +
         * "JOIN mw_addr addr ON addr.addr_seq = addrrel.addr_seq AND addr.crnt_rec_flg = 1 AND addr.del_flg = 0\r\n"
         * + "join mw_city_uc_rel rel on rel.city_uc_rel_seq = addr.city_seq\r\n" +
         * "JOIN mw_city city ON rel.city_seq = city.city_seq AND city.crnt_rec_flg = 1\r\n"
         * + "join mw_uc uc on uc.uc_seq = rel.uc_seq and uc.crnt_rec_flg = 1\r\n" +
         * "JOIN mw_thsl thsl ON thsl.thsl_seq = uc.thsl_seq AND thsl.crnt_rec_flg = 1\r\n"
         * +
         * "JOIN mw_dist dist ON dist.dist_seq = thsl.dist_seq AND dist.crnt_rec_flg = 1\r\n"
         * + "join mw_st st on st.st_seq = dist.st_seq AND st.crnt_rec_flg = 1\r\n" +
         * "JOIN mw_cntry cntry ON cntry.cntry_seq = st.st_seq AND cntry.crnt_rec_flg = 1\r\n"
         * + "where la.loan_app_seq=:loanAppSeq and la.crnt_rec_flg=1\r\n" + "union\r\n"
         * +
         * "select cr.clnt_rel_seq seq,cnic_num, cnic_expry_dt, frst_nm, last_nm, ph_num,dob,gndr.ref_cd_dscr gndr,decode(cr.rel_typ_flg,'1','Nominee','3','Co-Borrower','2','Next of Kin') rel_typ,cr.rel_typ_flg,rel.ref_cd_dscr rel,\r\n"
         * +
         * "(case when hse_num is not null then 'H No '||hse_num||', ' else null end)|| (case when strt is not null then 'St No '||strt||', ' else null end) || (case when oth_dtl is not null then oth_dtl||', ' else null end)  || (case when vlg is not null then vlg||', ' else null end) ||  city_nm  addr,cr.FTHR_FRST_NM||' '||cr.FTHR_LAST_NM FTHR_NM,0 co_bwr_san_flg,0 slf_pdc_flg,app.CO_BWR_ADDR_AS_CLNT_FLG   \r\n"
         * + "from mw_clnt_rel cr\r\n" +
         * " join mw_ref_cd_val gndr on cr.gndr_key=gndr.ref_cd_seq and gndr.crnt_rec_flg=1 and gndr.del_flg=0\r\n"
         * +
         * " join mw_ref_cd_val rel on  cr.rel_wth_clnt_key=rel.ref_cd_seq and rel.crnt_rec_flg=1 and rel.del_flg=0\r\n"
         * +
         * " left outer join mw_addr_rel addrrel on addrrel.enty_key = cr.clnt_rel_seq and enty_typ=(case when cr.rel_typ_flg=1 then 'Nominee' when cr.rel_typ_flg=3 then 'CoBorrower' else '' end ) and addrrel.crnt_rec_flg = 1  and addrrel.del_flg = 0 \r\n"
         * +
         * " left outer JOIN mw_addr addr ON addr.addr_seq = addrrel.addr_seq AND addr.crnt_rec_flg = 1 AND addr.del_flg = 0\r\n"
         * +
         * "  left outer join mw_city_uc_rel rel on rel.city_uc_rel_seq = addr.city_seq and rel.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_city city on rel.city_seq = city.city_seq and city.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_uc uc on uc.uc_seq = rel.uc_seq and uc.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_thsl thsl on thsl.thsl_seq = uc.thsl_seq and thsl.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_dist dist on dist.dist_seq = thsl.dist_seq and dist.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_st st on st.st_seq = dist.st_seq and st.crnt_rec_flg = 1\r\n"
         * +
         * "  left outer join mw_cntry cntry on cntry.cntry_seq = st.st_seq and cntry.crnt_rec_flg = 1\r\n"
         * +
         * " join mw_loan_app app on app.loan_app_seq=cr.loan_app_seq and app.crnt_rec_flg=1\r\n"
         * +
         * " where cr.loan_app_seq=:loanAppSeq and cr.crnt_rec_flg=1  and (cr.rel_typ_flg=1 or cr.rel_typ_flg=2 or cr.rel_typ_flg=3)\r\n"
         * + "order by rel_typ_flg" ) .setParameter( "loanAppSeq", loanAppSeq );
         */
        List<Object[]> clntRelsList = rs4.getResultList();

        List<Map<String, ?>> clntRels = new ArrayList();
        boolean san = false;
        boolean spf = false;
        for (Object[] r : clntRelsList) {
            Map<String, String> map = new HashMap();
            map.put("seq", r[0] == null ? "" : r[0].toString());
            map.put("cnic_num", r[1] == null ? "" : r[1].toString());
            try {
                map.put("cnic_expry_dt", new SimpleDateFormat("dd-MM-yyyy").format(inputFormat.parse(r[2].toString())));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            map.put("frst_nm", r[3] == null ? "" : r[3].toString());
            map.put("last_nm", r[4] == null ? "" : r[4].toString());
            map.put("ph_num", r[5] == null ? "" : r[5].toString());
            try {
                map.put("dob", new SimpleDateFormat("dd-MM-yyyy").format(inputFormat.parse(r[6].toString())));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            map.put("gndr", r[7] == null ? "" : r[7].toString());
            map.put("rel_typ", r[8] == null ? "" : r[8].toString());
            map.put("rel_typ_flg", r[9] == null ? "" : r[9].toString());
            map.put("rel", r[10] == null ? "" : r[10].toString());
            map.put("addr", r[11] == null ? "" : r[11].toString());
            map.put("rel_fathr", r[12] == null ? "" : r[12].toString());
            boolean cobAddrSAC = r[15] != null && new BigDecimal(r[15].toString()).longValue() == 1L ? true : false;
            if (!san && (params.containsKey("prd_seq") && new BigDecimal(params.get("prd_seq").toString()).longValue() != 2657L)) {
                san = r[13] != null && new BigDecimal(r[13].toString()).longValue() == 1L ? true : false;
            }
            if (!spf && (params.containsKey("prd_seq") && new BigDecimal(params.get("prd_seq").toString()).longValue() != 2657L)) {
                spf = r[14] != null && new BigDecimal(r[14].toString()).longValue() == 1L ? true : false;
            }
            if (cobAddrSAC || (new BigDecimal(r[9].toString()).longValue() == 1L)) {
                map.put("addr", "Same As Client");
            }
            if (r[9] != null && r[9].toString().equals("1")) {
                params.put("isNm", new BigDecimal(r[9].toString()).longValue());
            } else {
                params.put("isNm", null);
            }
            clntRels.add(map);
        }
        if (san) {
            Map<String, String> map = new HashMap();
            for (Map r : clntRels) {
                // Condition modified by Zohaib Asim - Dated 22-02-2021
                // Object should be greater than 0, It will minimize NULL exception
                if (r.size() > 0 && r.get("rel_typ").equals("Nominee")) {
                    map.put("seq", r.get("seq").toString());
                    map.put("cnic_num", r.get("cnic_num").toString());
                    map.put("cnic_expry_dt", r.get("cnic_expry_dt").toString());
                    map.put("frst_nm", r.get("frst_nm").toString());
                    map.put("last_nm", r.get("last_nm").toString());
                    map.put("ph_num", r.get("ph_num").toString());
                    map.put("dob", r.get("dob").toString());
                    map.put("gndr", r.get("gndr").toString());
                    map.put("rel_typ_flg", "3");
                    map.put("rel", r.get("rel").toString());
                    map.put("addr", "Same as Client");
                    map.put("rel_fathr", r.get("rel_fathr").toString());
                    map.put("rel_typ", "Co-Borrower");
                }
            }
            clntRels.add(map);
        }
        if (spf) {
            Map<String, String> map = new HashMap();
            for (Map r : clntRels) {
                // Condition modified by Zohaib Asim - Dated 22-02-2021
                // Object should be greater than 0, It will minimize NULL exception
                if (r.size() > 0 && r.get("rel_typ").equals("Client")) {
                    map.put("seq", r.get("seq").toString());
                    map.put("cnic_num", r.get("cnic_num").toString());
                    map.put("cnic_expry_dt", r.get("cnic_expry_dt").toString());
                    map.put("frst_nm", r.get("frst_nm").toString());
                    map.put("last_nm", r.get("last_nm").toString());
                    map.put("ph_num", r.get("ph_num").toString());
                    map.put("dob", r.get("dob").toString());
                    map.put("gndr", r.get("gndr").toString());
                    map.put("rel_typ_flg", "0");
                    map.put("rel", r.get("rel").toString());
                    map.put("addr", "Same as Client");
                    map.put("rel_fathr", r.get("rel_fathr").toString());
                    map.put("rel_typ", "Co-Borrower");
                }
            }
            clntRels.add(map);
        }

        String picQry;
        picQry = readFile(Charset.defaultCharset(), "ClientInfopicQry.txt");
        rs4 = em.createNativeQuery(picQry).setParameter("loanAppSeq", loanAppSeq);

        /*
         * Query picQry = em.createNativeQuery(
         * "select lad.doc_seq, doc_img,d.doc_nm \r\n" + "from mw_loan_app_doc lad\r\n"
         * +
         * "join mw_doc d on d.doc_seq=lad.doc_seq and d.crnt_rec_flg = 1 and (doc_nm='CLIENTS PICTURE' or doc_nm='NOMINEE PICTURE')\r\n"
         * +
         * "where loan_app_seq=:loanAppSeq and lad.crnt_rec_flg = 1 order by lad.doc_seq"
         * ).setParameter( "loanAppSeq", loanAppSeq );
         */

        Clob clob = null;

        Clob clob2 = null;
        try {
            List<Object[]> picsObj = rs4.getResultList();
            if (picsObj != null && picsObj.size() > 0) {
                clob = (Clob) (picsObj.size() >= 0 ? picsObj.get(0)[1] : null);
                clob2 = (Clob) (picsObj.size() == 2 ? picsObj.get(1)[1] : null);
            }

        } catch (

                Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        params.put("nominee_img",

                getStringClob(clob2));
        params.put("client_img", getStringClob(clob));

        /**
         * @Added, Naveed
         * @Date, 02-09-2022
         * @Description, SCR - LABAF and Undertaking single command
         */
        String clientUnderTakingScript = readFile(Charset.defaultCharset(), "TOTAL_RECEIVEABLE_AMOUNT.txt");
        Query clientUnderTakingQuery = em.createNativeQuery(clientUnderTakingScript).setParameter("loanAppSeq", loanAppSeq);

        Object[] clientUnderTakingResult = (Object[]) clientUnderTakingQuery.getSingleResult();

        MwMfcibOthOutsdLoan loan = new MwMfcibOthOutsdLoan();
        loan.setInstnNm("Kashf Foundation");
        if (params.containsKey("sect")) {
            loan.setLoanPrps(params.containsKey("sect") ? params.get("sect").toString() + " " : " ");
        }
        if (params.containsKey("acty")) {
            loan.setLoanPrps(loan.getLoanPrps() + (params.containsKey("acty") ? params.get("acty").toString() : " "));
        }
        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 5153L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 4513L)) {
            loan.setLoanPrps("SCHOOL");
        }
        loan.crntOutsdAmt(((clientUnderTakingResult[0] == null) ? 0L : new BigDecimal(clientUnderTakingResult[0].toString()).longValue())
                + ((clientUnderTakingResult[1] == null) ? 0L : new BigDecimal(clientUnderTakingResult[1].toString()).longValue())
                + ((clientUnderTakingResult[2] == null) ? 0L : new BigDecimal(clientUnderTakingResult[2].toString()).longValue()));

        List<MwMfcibOthOutsdLoan> mfcib = new ArrayList();
        mfcib.add(0, loan);
        mfcib.addAll(mwMfcibOthOutsdLoanRepository.findAllByLoanAppSeqAndCrntRecFlg(loanAppSeq, true));
        params.put("UNDER_TAKING", getJRDataSource(mfcib));

        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 2625L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 5765L)) {
            params.put("charter_img", REPORTS_BASEPATH + "labaf_1.jpg");
            params.put("signature_img", REPORTS_BASEPATH + "labaf_2.jpg");
            return reportComponent.generateReport(LABAF, params, getJRDataSource(clntRels));
        }
        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 10L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 11L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 12L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 39L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 40L)) {
            params.put("charter_img", REPORTS_BASEPATH + "mabaf_1.jpg");
            params.put("signature_img", REPORTS_BASEPATH + "mabaf_2.jpg");

            return reportComponent.generateReport(MABAF, params, getJRDataSource(clntRels));
        }

        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 5153L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 4513L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 37L)) {
            // Added By Naveed 7-10-2021
            // 37L for KSS 24M prd
            params.put("charter_img", REPORTS_BASEPATH + "slabaf_1.jpg");
            params.put("signature_img", REPORTS_BASEPATH + "slabaf_2.jpg");
            return reportComponent.generateReport(SLABAF, params, getJRDataSource(clntRels));
        }

        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 2657L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 41L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 42L
                || new BigDecimal(params.get("prd_seq").toString()).longValue() == 43L)) {
            params.put("charter_img", REPORTS_BASEPATH + "laf_1.jpg");
            params.put("signature_img", REPORTS_BASEPATH + "laf_2.jpg");
            return reportComponent.generateReport(LAF, params, getJRDataSource(clntRels));
        }
        if (params.containsKey("prd_seq") && (new BigDecimal(params.get("prd_seq").toString()).longValue() == 16L)) {
            params.put("charter_img", REPORTS_BASEPATH + "labaf_1.jpg");
            params.put("signature_img", REPORTS_BASEPATH + "labaf_2.jpg");
            return reportComponent.generateReport(MLABAF, params, getJRDataSource(clntRels));
        }

        // if ( new BigDecimal( params.get( "prd_seq" ).toString() ).longValue() ==
        // 2625L
        // || new BigDecimal( params.get( "prd_seq" ).toString() ).longValue() == 5765L
        // ) {
        params.put("charter_img", REPORTS_BASEPATH + "labaf_1.jpg");
        params.put("signature_img", REPORTS_BASEPATH + "labaf_2.jpg");
        return reportComponent.generateReport(LABAF, params, getJRDataSource(clntRels));
        // }

        // return null;

    }

    private String getStringClob(Clob clob) {
        byte[] imgByte = null;
        try {
            imgByte = clob == null ? null : IOUtils.toByteArray(clob.getAsciiStream());
        } catch (IOException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String noImg = "/9j/4AAQSkZJRgABAgAAZABkAAD/2wBDAAYEBAQFBAYFBQYJBgUGCQsIBgYICwwKCgsKCgwQDAwMDAwMEAwODxAPDgwTExQUExMcGxsbHB8fHx8fHx8fHx//2wBDAQcHBw0MDRgQEBgaFREVGh8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx//wgARCADIAMgDAREAAhEBAxEB/8QAFwABAQEBAAAAAAAAAAAAAAAAAAECB//EABUBAQEAAAAAAAAAAAAAAAAAAAAB/9oADAMBAAIQAxAAAAHqgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABCgAAAAhQCFAAAAAAAAAIUAAAQFAAAABCgAAAAAAAAAAAAEKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEKCFAAAAAAAAAAAAAAMmgAAAAAQoAAAAAAAIUAAAAAAAAAAAAAAAAhQAAADBsAAAAAAAAAAAEKAAACFAAIUAAAAAAAAAAAAQoQoAAIUAAAEIaBCgAAAAAAAAEKACFBItAAAAAAAAQoAAAAAAAAAAAAAAAAAAAAAAEKAAAAAhQAAQFAAAAAAAAAABCgAAQoQoAAAABItAACFAAAIUAH/8QAGRABAAMBAQAAAAAAAAAAAAAAARFgcAAw/9oACAEBAAEFArxOBjJYHyWqvA5r/8QAFBEBAAAAAAAAAAAAAAAAAAAAkP/aAAgBAwEBPwEcf//EABQRAQAAAAAAAAAAAAAAAAAAAJD/2gAIAQIBAT8BHH//xAAYEAACAwAAAAAAAAAAAAAAAAABESFwgP/aAAgBAQAGPwLXkl1r/8QAJBAAAgAGAQMFAAAAAAAAAAAAAREQICEwQEExAFBhUWBxgcH/2gAIAQEAAT8hkL7lvAahvIAXGQUK9eLZd4gHnHeA4iucOxAARweOwpG7v5t6jpxeCUKmQ3WVLCglaZVckgFMNVFkkDnc1X+3axq/F0Tmy2uhByI7xKrzKWognfsJwrZPTmJAHX3j+JzDWIDd/9oADAMBAAIAAwAAABASQCQSSQSSSCSSSSSSSSSCSQSSSQSSCSSQSQQSSSCCSQSSCSSSSSQSCQCQCCQSSCTKCSSSRSSCSSSSSSSSSSRSSSCSSSQCCSSSSSSQSSSSCSACQSSCSSSSACSSSSSQSSSSSSSSSSSSSQSSSSSCSSCSSSSSQCCSSQSSSSSACSCQQAASSKSQQCQCQAASSAASCSSCSQBSSAQQSSQACQCCSSBSSCSTAQCSKSQSQSSQCQSQQSCAaSCSJSSSSSSAACSSQCSSSQSSCSSCQCCQBACCCASCQASSSQCQQACSSQQCSRAQSCCQCYASCSQASSSf/8QAFBEBAAAAAAAAAAAAAAAAAAAAkP/aAAgBAwEBPxAcf//EABgRAAMBAQAAAAAAAAAAAAAAAAERYBCA/9oACAECAQE/EOZnZDRKOC//xAAoEAACAQQBBAICAwEBAAAAAAABEQAQITFBUSBhcYGhwTCRsdHx4fD/2gAIAQEAAT8QRZPMc0oH6nB78UzLRcVLVsy9DqaqYSAYf8FO34Lum6gu/QhMS7FvdDL+qK//ALcJAl5/HTe524q7qgLUL176sjiphAGcRQkoES98ddqO63N/dFCV42ZrOYpe/wAQEqaiaepqgoCwLFk+aCgdhnk0+qGCd4MmpANlaLDusVLVsxnGDR3xUITBYElorU30EA56L0JLC/HSCxxXETz0oEaLHYiAbpjqvqrt34guI+k0YmRAxI4gLFXUdaFE+5dvXFCAMFg4I61TdMUVGTjpMVHHaBK0ccN7OF51uYVG6OqL7UBuQkundLv+IQ/6gAAWhFiEOhDH4iUCTYDcJGxMu0tQFhi4OPFczUYdC9VY/BtdCHrpIhsLUCADOST5vDW8AA6kW3biAK0JAAb+lN/gOIOXuAS0eSyDXiKHtGCe4KgsOt1+CxYeIja+4TCId63gE2mlRfNEkscTMKgWtw9FlmWMlWd4+bVGKK75oQNiGO94X6gnkgQgSTJOB5oORgwD4xQwMC5vMk9v1Br5g3LG+o6HFNlZnb9zeIn4mLUWuKCER+ARXRgjYhf9y9TFzeZEUMDAEvZhosVBtEMKBiEBfcHxD2zC9ZhKl91JUYt+47oZy9QlCsy6vNUF70u74109p8TNDiAgt0z0WUZW4gwCkTVWUYBWzXzLKzeTDYOiS0kDqhMZgXi3NWniKDE3L1L1HV0d+9HbEOjwcTR90LlyO8HBnmAgMe3BGEHgiGeJqB7gofmWxuDEHfqzQl+Dmi3F+poKihBVs6hPzT5ivQUAC+JYQFhwGrvO9MQD/Ohx7obOmqHFXehDExEP3QkAM24lxfUJs0YAIcGBDgAu74oAiZuNk9pgOdxGd4oJICJCLKVx7iMjJGqq7hAmo8E5sPMIJIWGe/VuBPvQkMTvS0OlASry66iQr4oELMC0IBTDgH/YaK7oBItmEGJAOPqXwDLq2YOY4v3QvVBiZ+oXbzc0ZiFz8TeYBzdbhamrQi2YwnkTdLxx3o4QyOBQm6r/AP/Z";

        return imgByte == null ? noImg : new String(imgByte);
    }

    public byte[] getPostedReport(String date, String userId, long branch) throws IOException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());

        String openBalQury;
        openBalQury = readFile(Charset.defaultCharset(), "PostedReportopenBalQury.txt");
        Query rs = em.createNativeQuery(openBalQury).setParameter("date", date).setParameter("branch",
                obj[4].toString());

        /*
         * Query openBalQury = em.createNativeQuery(
         * "select op_blnc(to_date(:date,'MM-dd-yyyy'),:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0001' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=0 )) cash, op_blnc(to_date(:date,'MM-dd-yyyy'),:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0008' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=:branch)) bank from dual"
         * ) .setParameter( "date", date ).setParameter( "branch", obj[ 4 ].toString()
         * );
         */
        Object[] openBal = (Object[]) rs.getSingleResult();

        Long ttlOpnBacnk = new BigDecimal(openBal[1].toString()).longValue();

        Long totlOpnCash = new BigDecimal(openBal[0].toString()).longValue();

        String closeBalQury;
        closeBalQury = readFile(Charset.defaultCharset(), "PostedReportcloseBalQury.txt");
        Query rs1 = em.createNativeQuery(closeBalQury).setParameter("date", date).setParameter("branch",
                obj[4].toString());

        /*
         * Query closeBalQury = em.createNativeQuery(
         * "select op_blnc(to_date(:date,'MM-dd-yyyy')+1,:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0001' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=0 )) cash, op_blnc(to_date(:date,'MM-dd-yyyy')+1,:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0008' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=:branch)) bank from dual"
         * ) .setParameter( "date", date ).setParameter( "branch", obj[ 4 ].toString()
         * );
         */
        Object[] closeBal = (Object[]) rs1.getSingleResult();

        Long ttlClsBank = new BigDecimal(closeBal[1].toString()).longValue();

        Long ttlClsCash = new BigDecimal(closeBal[0].toString()).longValue();

        params.put("curr_user", userId);
        try {
            Date date1 = new SimpleDateFormat("MM-dd-yyyy").parse(date);
            params.put("date", new SimpleDateFormat("dd-MM-yyyy").format(date1));
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        params.put("open_bank", ttlOpnBacnk);
        params.put("open_cash", totlOpnCash);
        params.put("close_bank", ttlClsBank);
        params.put("close_cash", ttlClsCash);

        String qt;
        qt = readFile(Charset.defaultCharset(), "PostedReportqt.txt");
        Query rs2 = em.createNativeQuery(qt).setParameter("userId", userId).setParameter("reportdate", date)
                .setParameter("branch", branch);

        /*
         * Query qt = em.createNativeQuery( "select \r\n" +
         * "distinct trx.rcvry_trx_seq,\r\n" + "clnt.clnt_id, \r\n" +
         * "clnt.frst_nm|| ' '|| clnt.last_nm name, instr_num, sum(dtl.pymt_amt) pymt_amt, rcvry.typ_str, p.prd_cmnt, \r\n"
         * +
         * "case when tg.loan_app_seq is null then 'RECOVERIES' else 'TAGGED CLIENT RECOVERIES' end rcvry_flg,emp.emp_nm, to_char(trx.last_upd_dt,'HH12:MI:SSAM') upd_dt, psd.due_dt \r\n"
         * +
         * "from mw_rcvry_trx trx join mw_typs rcvry on rcvry.typ_seq = rcvry_typ_seq and rcvry.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_rcvry_dtl dtl on dtl.rcvry_trx_seq = trx.rcvry_trx_seq and dtl.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_loan_app la on la.loan_app_seq = psh.loan_app_seq and la.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq = la.loan_app_sts and val.crnt_rec_flg = 1 and val.del_flg = 0 \r\n"
         * +
         * "join mw_acl acl on acl.port_seq = la.port_seq and acl.user_id =:userId \r\n"
         * + "join mw_prd p on p.prd_seq = la.prd_seq and p.crnt_rec_flg = 1 \r\n" +
         * "join mw_clnt clnt on clnt.clnt_id = trx.pymt_ref and clnt.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_port_emp_rel prt on prt.port_seq = clnt.port_key and prt.crnt_rec_flg = 1 \r\n"
         * + "join mw_emp emp on emp.emp_seq = prt.emp_seq \r\n" +
         * "left outer join mw_clnt_tag_list tg on tg.loan_app_seq=la.loan_app_seq\r\n"
         * + "where trx.post_flg = 1 and trx.crnt_rec_flg = 1 \r\n" +
         * "and rcvry.typ_id !='0020' \r\n" +
         * "and to_date(trx.pymt_dt) = to_date(:reportdate,'MM-dd-yyyy') \r\n" +
         * "group by trx.rcvry_trx_seq,\r\n" +
         * "clnt.clnt_id,clnt.frst_nm|| ' '|| clnt.last_nm, instr_num, \r\n" +
         * "rcvry.typ_str,p.prd_cmnt,case when tg.loan_app_seq is null then 'RECOVERIES' else 'TAGGED CLIENT RECOVERIES' end,emp.emp_nm,trx.last_upd_dt,psd.due_dt \r\n"
         * + "order by rcvry_flg,rcvry.typ_str, clnt.clnt_id" ).setParameter( "userId",
         * userId ).setParameter( "reportdate", date );
         */
        List<Object[]> recvrys = rs2.getResultList();
        List<Map<String, ?>> recvrysList = new ArrayList();
        recvrys.forEach(r -> {
            Map<String, Object> map = new HashMap();
            map.put("rcvry_trx_seq", r[0] == null ? 0 : new BigDecimal(r[0].toString()).longValue());
            map.put("clnt_id", r[1].toString());
            map.put("name", r[2].toString());
            map.put("instr_num", r[3] == null ? "" : r[3].toString());
            map.put("pymt_amt", r[4] == null ? 0 : new BigDecimal(r[4].toString()).longValue());
            map.put("typ_str", r[5].toString());
            map.put("prd_nm", r[6].toString());
            map.put("ref_cd", r[7].toString());
            map.put("emp_nm", r[8] == null ? "" : r[8].toString());
            map.put("upd_dt", r[9].toString());
            map.put("due_dt", r[10] == null ? "" : r[10].toString());
            recvrysList.add(map);

        });

        // params.put( "dataset", getJRDataSource( recvrysList ) );

        String des;
        des = readFile(Charset.defaultCharset(), "PostedReportdes.txt");
        Query rs3 = em.createNativeQuery(des).setParameter("brnch", obj[3].toString()).setParameter("reportdate", date);
        /*
         * Query des = em.createNativeQuery(
         * "select p.prd_cmnt,c.clnt_id,c.frst_nm||' '|| c.last_nm as name,c.spz_frst_nm,c.spz_last_nm as spz, sum(dtl.amt),  \r\n"
         * +
         * "emp.emp_nm,dsmode.typ_str,dvh.dsbmt_hdr_seq,to_char(dvh.dsbmt_dt,'HH12:MI:SSAM') dsbmt_dt,  \r\n"
         * +
         * "listagg( dtl.instr_num,',') within group (order by dsmode.typ_str) instr_num\r\n"
         * + "from mw_loan_app app  \r\n" +
         * "join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=app.loan_app_seq and dvh.crnt_rec_flg=1  \r\n"
         * +
         * "join mw_dsbmt_vchr_dtl dtl on dtl.dsbmt_hdr_seq=dvh.dsbmt_hdr_seq and dtl.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_jv_hdr jh on jh.enty_seq=dvh.dsbmt_hdr_seq and jh.enty_typ='Disbursement' and PRNT_VCHR_REF is null\r\n"
         * +
         * "join mw_typs dsmode on dsmode.typ_seq=dtl.pymt_typs_seq and dsmode.crnt_rec_flg=1  \r\n"
         * + "join mw_prd p on p.prd_seq=app.prd_seq and p.crnt_rec_flg=1  \r\n" +
         * "join mw_clnt c on c.clnt_seq=app.clnt_seq and c.crnt_rec_flg=1  \r\n" +
         * "join mw_port prt on prt.port_seq = app.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:brnch  \r\n"
         * +
         * "join mw_port_emp_rel prt on prt.port_seq = c.port_key and prt.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_emp emp on emp.emp_seq = prt.emp_seq where app.crnt_rec_flg=1  \r\n"
         * +
         * "and to_date(jh.jv_dt) = to_date(:reportdate,'MM-dd-yyyy') and dvh.dsbmt_dt is not null\r\n"
         * +
         * "group by p.prd_cmnt,c.clnt_id,c.frst_nm,c.last_nm,c.spz_frst_nm,c.spz_last_nm,  \r\n"
         * +
         * "emp.emp_nm, app.loan_app_sts_dt,dsmode.typ_str,dvh.dsbmt_hdr_seq,dvh.dsbmt_dt  union \r\n"
         * +
         * "select nvl(chrg.typ_str,'KSZB'), c.clnt_id,c.frst_nm||' '|| c.last_nm as name, \r\n"
         * + "c.spz_frst_nm,c.spz_last_nm as spz, sum(chrg.amt), emp.emp_nm, \r\n" +
         * "'JV', app.LOAN_APP_SEQ, to_char(dvh.DSBMT_DT,'HH12:MI:SSAM') dsbmt_dt, \r\n"
         * + "null instr_num from mw_loan_app app \r\n" +
         * "join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=app.loan_app_seq and dvh.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_dsbmt_vchr_dtl dtl on dtl.dsbmt_hdr_seq=dvh.dsbmt_hdr_seq and dtl.crnt_rec_flg=1\r\n"
         * +
         * "join mw_jv_hdr jh on jh.enty_seq=dvh.loan_app_seq and jh.enty_typ='Disbursement'\r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psh.loan_app_seq=app.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_pymt_sched_chrg chrg on chrg.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and chrg.crnt_rec_flg=1 \r\n"
         * +
         * "left outer join mw_typs chrg on chrg.typ_seq=chrg.chrg_typs_seq and chrg.crnt_rec_flg=1 \r\n"
         * + "join mw_prd p on p.prd_seq=app.prd_seq and p.crnt_rec_flg=1 \r\n" +
         * "join mw_clnt c on c.clnt_seq=app.clnt_seq and c.crnt_rec_flg=1 \r\n" +
         * "join mw_port prt on prt.PORT_SEQ = app.PORT_SEQ and prt.CRNT_REC_FLG=1 and prt.BRNCH_SEQ=:brnch \r\n"
         * +
         * "join mw_port_emp_rel prt on prt.port_seq = c.port_key and prt.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_emp emp on emp.emp_seq = prt.emp_seq where app.crnt_rec_flg=1 \r\n"
         * +
         * "and to_date(jh.jv_dt) = to_date(:reportdate,'MM-dd-yyyy') and dvh.dsbmt_dt is not null\r\n"
         * +
         * "group by nvl(chrg.typ_str,'KSZB'),c.clnt_id,c.frst_nm,c.last_nm,c.spz_frst_nm,c.spz_last_nm, \r\n"
         * +
         * "emp.emp_nm, app.loan_app_sts_dt,'JV',app.LOAN_APP_SEQ,dvh.DSBMT_DT,null order by 8"
         * ) .setParameter( "brnch", obj[ 3 ].toString() ).setParameter( "reportdate",
         * date );
         */
        List<Object[]> disbursments = rs3.getResultList();
        List<Map<String, ?>> disbList = new ArrayList();
        disbursments.forEach(d -> {
            Map<String, Object> map = new HashMap();
            map.put("prd_nm", d[0].toString());
            map.put("clnt_id", d[1].toString());
            map.put("name", d[2].toString());
            map.put("spz", (d[3] == null ? "" : d[3].toString()) + " " + (d[4] == null ? "" : d[4].toString()));
            map.put("aprvd_loan_amt", d[5] == null ? 0 : new BigDecimal(d[5].toString()).longValue());
            map.put("emp_nm", d[6].toString());
            map.put("typ_str", d[7].toString());
            map.put("ref_cd", d[8].toString());
            map.put("dsbmt_dt", d[9].toString());
            map.put("instr_num", d[10] == null ? "" : d[10].toString());
            disbList.add(map);

        });

        params.put("datasetdis", getJRDataSource(disbList));

        String expQry;
        expQry = readFile(Charset.defaultCharset(), "PostedReportexpQry.txt");
        Query rs4 = em.createNativeQuery(expQry).setParameter("branch", branch).setParameter("reportdate", date);

        /*
         * Query expQry = em.createNativeQuery( "select ptyp.typ_str pymt_mod,\r\n" +
         * "(case when etyp.typ_id='0005' and etyp.BRNCH_SEQ=0 then (select distinct tr.PYMT_REF from  mw_rcvry_trx tr where tr.RCVRY_TRX_SEQ = exp.EXP_REF) else exp.EXP_REF end) clnt_id,\r\n"
         * + " etyp.typ_str lgr_acty,  \r\n" +
         * "    exp.expns_dscr, exp.instr_num,       case when exp.pymt_rct_flg=1 then expns_amt else 0 end pymts,  \r\n"
         * +
         * "    case when exp.pymt_rct_flg=2 then expns_amt else 0 end rcpts,exp.EXP_SEQ EXP_SEQ   \r\n"
         * + "    from mw_exp exp  \r\n" +
         * "join mw_jv_hdr jh on jh.enty_seq=exp.exp_seq and jh.enty_typ='Expense' \r\n"
         * +
         * "join mw_typs ptyp on ptyp.typ_seq=exp.pymt_typ_seq and ptyp.crnt_rec_flg=1  \r\n"
         * +
         * "join mw_typs etyp on etyp.typ_seq=exp.expns_typ_seq and etyp.crnt_rec_flg=1  \r\n"
         * +
         * "join MW_BRNCH_EMP_REL ber on ber.BRNCH_SEQ = exp.BRNCH_SEQ and ber.CRNT_REC_FLG = 1   and ber.del_flg=0\r\n"
         * + "join mw_emp e on e.EMP_SEQ = ber.EMP_SEQ  \r\n" +
         * "where exp.crnt_rec_flg=1 and exp.post_flg=1 and e.EMP_LAN_ID=:userId  \r\n"
         * +
         * "and (ptyp.typ_id='0001' or ptyp.typ_id='0008' or ptyp.typ_id='0420') and ptyp.typ_ctgry_key=6  \r\n"
         * + "and to_date(jh.jv_dt) = to_date(:reportdate,'MM-dd-yyyy')\r\n" +
         * "union  \r\n" +
         * "select pmod.typ_str pymt_mod,trx.PYMT_REF clnt_id, rtyp.typ_str lgr_acty, \r\n"
         * +
         * "rtyp.typ_str expns_dscr, trx.instr_num, sum(0) pymts, sum(rdtl.pymt_amt) rcpts,trx.RCVRY_TRX_SEQ EXP_SEQ \r\n"
         * + "from mw_rcvry_dtl rdtl \r\n" +
         * "join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * +
         * "join mw_jv_hdr jh on jh.enty_seq=trx.rcvry_trx_seq and enty_typ='EXCESS RECOVERY'\r\n"
         * +
         * "join mw_typs pmod on pmod.typ_seq=trx.rcvry_typ_seq and pmod.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_clnt clnt on clnt.clnt_seq=trx.pymt_ref and clnt.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_port prt on prt.port_seq=clnt.port_key and prt.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_brnch_emp_rel brnch on brnch.brnch_seq=prt.brnch_seq and brnch.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_emp emp on emp.emp_seq=brnch.emp_seq where rdtl.crnt_rec_flg=1 \r\n"
         * + "and rdtl.pymt_sched_dtl_seq is null and emp.emp_lan_id=:userId \r\n" +
         * "and to_date(jh.jv_dt) = to_date(:reportdate,'MM-dd-yyyy') \r\n" +
         * "group by pmod.typ_str,trx.PYMT_REF,rtyp.typ_str,rtyp.typ_str,trx.instr_num,trx.RCVRY_TRX_SEQ order by 1"
         * ) .setParameter( "userId", userId ).setParameter( "reportdate", date );
         */
        List<Object[]> expenses = new ArrayList();
        try {
            expenses = rs4.getResultList();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        List<Map<String, ?>> expList = new ArrayList();
        expenses.forEach(d -> {
            Map<String, Object> map = new HashMap();
            map.put("PYMT_MOD", d[0].toString());
            map.put("CLNT_ID", d[1] == null ? "" : d[1].toString());
            map.put("LGR_ACTY", d[2] == null ? "" : d[2].toString());
            map.put("EXPNS_DSCR", d[3] == null ? "" : d[3].toString());
            map.put("INSTR_NUM", d[4] == null ? "" : d[4].toString());
            map.put("PYMTS", d[5] == null ? 0 : new BigDecimal(d[5].toString()).longValue());
            map.put("RCPTS", d[6] == null ? 0 : new BigDecimal(d[6].toString()).longValue());
            map.put("EXP_SEQ", d[7] == null ? 0 : new BigDecimal(d[7].toString()).longValue());
            expList.add(map);
        });
        params.put("expdataset", getJRDataSource(expList));

        String adjQry;
        adjQry = readFile(Charset.defaultCharset(), "PostedReportadjQry.txt");
        Query rs5 = em.createNativeQuery(adjQry).setParameter("userId", userId).setParameter("reportdate", date)
                .setParameter("branch", branch);

        /*
         * Query adjQry = em.createNativeQuery(
         * "select   distinct trx.rcvry_trx_seq,  clnt.clnt_id,  \r\n" +
         * "clnt.frst_nm|| ' '|| clnt.last_nm name, instr_num,sum(dtl.PYMT_AMT) pymt_amt, rcvry.typ_str, p.prd_cmnt,emp.emp_nm, to_char(trx.last_upd_dt,'HH12:MI:SSAM') upd_dt,jh.JV_HDR_SEQ  \r\n"
         * + "from mw_rcvry_trx trx \r\n" +
         * "join mw_typs rcvry on rcvry.typ_seq = rcvry_typ_seq and rcvry.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_jv_hdr jh on jh.enty_seq=trx.rcvry_trx_seq and enty_typ='Recovery'\r\n"
         * +
         * "join mw_rcvry_dtl dtl on dtl.rcvry_trx_seq = trx.rcvry_trx_seq and dtl.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_loan_app la on la.loan_app_seq = psh.loan_app_seq and la.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq = la.loan_app_sts and val.crnt_rec_flg = 1 and val.del_flg = 0  \r\n"
         * +
         * "join mw_acl acl on acl.port_seq = la.port_seq and acl.user_id =:userId\r\n"
         * + "join mw_prd p on p.prd_seq = la.prd_seq and p.crnt_rec_flg = 1  \r\n" +
         * "join mw_clnt clnt on clnt.clnt_id = trx.pymt_ref and clnt.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_port_emp_rel prt on prt.port_seq = clnt.port_key and prt.crnt_rec_flg = 1  \r\n"
         * + "join mw_emp emp on emp.emp_seq = prt.emp_seq  \r\n" +
         * "where trx.post_flg = 1 and trx.crnt_rec_flg = 1   and rcvry.typ_id ='0020'  \r\n"
         * + "and to_date(jh.jv_dt) = to_date(:reportdate,'MM-dd-yyyy')   \r\n" +
         * "group by trx.rcvry_trx_seq, \r\n" +
         * "clnt.clnt_id,clnt.frst_nm|| ' '|| clnt.last_nm, instr_num,trx.PYMT_AMT  ,\r\n"
         * + "rcvry.typ_str,p.prd_cmnt,emp.emp_nm,trx.last_upd_dt,jh.JV_HDR_SEQ\r\n" +
         * "order by rcvry.typ_str, clnt.clnt_id" ) .setParameter( "userId", userId
         * ).setParameter( "reportdate", date );
         */
        List<Object[]> adjRec = new ArrayList();
        try {
            adjRec = rs5.getResultList();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        List<Map<String, ?>> adjList = new ArrayList();
        adjRec.forEach(r -> {
            Map<String, Object> map = new HashMap();
            map.put("RCVRY_TRX_SEQ", r[0] == null ? 0 : new BigDecimal(r[0].toString()).longValue());
            map.put("CLNT_ID", r[1].toString());
            map.put("NAME", r[2].toString());
            map.put("INSTR_NUM", r[3] == null ? "" : r[3].toString());
            map.put("PYMT_AMT", r[4] == null ? 0 : new BigDecimal(r[4].toString()).longValue());
            map.put("TYP_STR", r[5].toString());
            map.put("PRD_CMNT", r[6].toString());
            map.put("EMP_NM", r[7].toString());
            map.put("UPD_DT", r[8].toString());
            map.put("JV_HDR_SEQ", r[9].toString());
            adjList.add(map);
        });
        params.put("adjdataset", getJRDataSource(adjList));

        File file = null;
        String REPORTS_BASEPATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator
                + "reports" + file.separator;
        params.put("POSTING_SUB", REPORTS_BASEPATH + "POSTING_SUB.jasper");
        return reportComponent.generateReport(POSTING, params, getJRDataSource(recvrysList));
    }

    /**
     * @Update: Naveed
     * @SCR: Rescheduled Branch Reports (Operation)
     * @Date: 02-11-2022
     */
    @Transactional
    public byte[] getOverdueLoansReport(long prdSeq, String asDt, String userId, long branch, String type) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("type", type);
        Query prdQ = em
                .createNativeQuery(
                        "select PRD_GRP_NM from mw_prd_grp grp where grp.PRD_GRP_SEQ=:prdSeq and grp.crnt_rec_flg=1")
                .setParameter("prdSeq", prdSeq);
        String prd = (String) prdQ.getSingleResult();

        params.put("prd", prd);
        params.put("date", asDt);

        String q;
        q = readFile(Charset.defaultCharset(), "OVER_DUE_LOANS_INSRT.txt");
        Query rs5 = em.createNativeQuery(q).setParameter("brnchSeq", obj[4].toString()).setParameter("prdSeq", prdSeq)
                .setParameter("aDt", asDt).setParameter("P_TYPE", type);

        // Query q = em.createNativeQuery( com.idev4.rds.util.Queries.OVER_DUE_LOANS
        // ).setParameter( "brnch", obj[ 4 ].toString() )
        // .setParameter( "prdSeq", prdSeq ).setParameter( "aDt", asDt );
        int insertStatement = rs5.executeUpdate();

        String selectQueryStr = readFile(Charset.defaultCharset(), "OVER_DUE_LOANS_SLCT.txt");
        Query selectQuery = em.createNativeQuery(selectQueryStr).setParameter("P_TYPE", type);
        List<Object[]> overdues = selectQuery.getResultList();

        List<Map<String, ?>> paymentsList = new ArrayList();
        for (Object[] r : overdues) {
            Map<String, Object> map = new HashMap();
            map.put("PORT_NM", r[1].toString());
            map.put("CLNT_ID", r[2].toString());
            map.put("FRST_NM", r[3].toString());
            map.put("FS_FRST_NM", r[4] == null ? "" : r[4].toString());
            map.put("PH_NUM", r[5] == null ? "" : r[5].toString());
            map.put("HSE_NUM", r[6] == null ? "" : r[6].toString());
            map.put("DSBMT_DT", r[7] == null ? "" : getFormaedDate(r[7].toString()));
            map.put("APRVD_LOAN_AMT", r[8] == null ? 0 : new BigDecimal(r[8].toString()).longValue());
            map.put("OST_INST", r[9] == null ? 0 : new BigDecimal(r[9].toString()).longValue());
            map.put("OST_AMT", r[10] == null ? 0 : new BigDecimal(r[10].toString()).longValue());
            map.put("OST_CHRG_AMT", r[11] == null ? 0 : new BigDecimal(r[11].toString()).longValue());
            map.put("OD_INST", r[12] == null ? 0 : new BigDecimal(r[12].toString()).longValue());
            map.put("OD_AMT", r[13] == null ? 0 : new BigDecimal(r[13].toString()).longValue());
            map.put("OD_DAYS", r[14] == null ? 0 : new BigDecimal(r[14].toString()).longValue());
            map.put("LST_REC_DT", r[15] == null ? "" : getFormaedDate(r[15].toString()));
            map.put("RSN", r[16] == null ? "" : r[16].toString());
            map.put("CMNT", r[17] == null ? "" : r[17].toString());
            map.put("PRD_NM", r[18] == null ? "" : r[18].toString());
            paymentsList.add(map);
        }

        return reportComponent.generateReport(OVERDUELOANS, params, getJRDataSource(paymentsList));
    }

    public byte[] getPortfolioMonitoringNewReport(String fromDt, String toDt, String userId, long branch)
            throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", fromDt);
        params.put("to_dt", toDt);

        Date frmDt = null;
        Date tooDt = null;
        try {
            frmDt = new SimpleDateFormat("dd-MM-yyyy").parse(fromDt);
            tooDt = new SimpleDateFormat("dd-MM-yyyy").parse(toDt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fromDt = new SimpleDateFormat("dd-MMM-yyyy").format(frmDt);
        toDt = new SimpleDateFormat("dd-MMM-yyyy").format(tooDt);
        String q;
        q = readFile(Charset.defaultCharset(), "PORTFOLIO_MONITORING_NEW.txt");
        Query rs5 = em.createNativeQuery(q).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("branch", new BigDecimal(obj[3].toString()).longValue());
        List<Object[]> overdues = rs5.getResultList();

        List<Map<String, ?>> paymentsList = new ArrayList();
        for (Object[] r : overdues) {
            Map<String, Object> map = new HashMap();
            if (r[4] != null && new BigDecimal(r[4].toString()).longValue() > 0) {
                map.put("PRD_NM", r[0].toString());
                map.put("EMP_NM", r[1].toString());
                map.put("EFF_START_DT", getFormaedDate(r[2].toString()));
                map.put("OST_CLNT", r[3] == null ? 0 : new BigDecimal(r[3].toString()).longValue());
                map.put("OST_AMT", r[4] == null ? 0 : new BigDecimal(r[4].toString()).longValue());
                map.put("DUE_PERD_CLNT", r[5] == null ? 0 : new BigDecimal(r[5].toString()).longValue());
                map.put("DUE_PERD_AMT", r[6] == null ? 0 : new BigDecimal(r[6].toString()).longValue());
                map.put("RCVRD_CLNT", r[7] == null ? 0 : new BigDecimal(r[7].toString()).longValue());
                map.put("RCVRD_AMT", r[8] == null ? 0 : new BigDecimal(r[8].toString()).longValue());
                map.put("TRGT_CLNT", r[9] == null ? 0 : new BigDecimal(r[9].toString()).longValue());
                map.put("ACHVD_IN_PERD", r[10] == null ? 0 : new BigDecimal(r[10].toString()).longValue());
                map.put("TRGT_AMT", r[11] == null ? 0 : new BigDecimal(r[11].toString()).longValue());
                map.put("ACHVD_IN_PERD_AMT", r[12] == null ? 0 : new BigDecimal(r[12].toString()).longValue());
                map.put("OD_CLNT", r[13] == null ? 0 : new BigDecimal(r[13].toString()).longValue());
                map.put("OD_AMT", r[14] == null ? 0 : new BigDecimal(r[14].toString()).longValue());
                map.put("PAR_1_DY_CNT", r[15] == null ? 0 : new BigDecimal(r[15].toString()).longValue());
                map.put("PAR_1_DY_AMT", r[16] == null ? 0 : new BigDecimal(r[16].toString()).longValue());
                map.put("PAR_30_DAY_CNT", r[17] == null ? 0 : new BigDecimal(r[17].toString()).longValue());
                map.put("PAR_30_DAY_AMT", r[18] == null ? 0 : new BigDecimal(r[18].toString()).longValue());
                map.put("CMPLTD_LOANS", r[19] == null ? 0 : new BigDecimal(r[19].toString()).longValue());
                map.put("OD_BP_CLNT", r[20] == null ? 0 : new BigDecimal(r[20].toString()).longValue());
                map.put("OD_BP_AMT0", r[21] == null ? 0 : new BigDecimal(r[21].toString()).longValue());

                paymentsList.add(map);
            }
        }

        return reportComponent.generateReport(MONITORING_NEW, params, getJRDataSource(paymentsList));
    }

    /**
     * @Update: Naveed
     * @SCR: Rescheduled Branch Reports (Operation)
     * @Date: 02-11-2022
     */
    public byte[] getPortfolioReport(String fromDt, String toDt, String userId, long branch, String type) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", fromDt);
        params.put("to_dt", toDt);
        params.put("type", type);

        Date frmDt = null;
        Date tooDt = null;
        try {
            frmDt = new SimpleDateFormat("dd-MM-yyyy").parse(fromDt);
            tooDt = new SimpleDateFormat("dd-MM-yyyy").parse(toDt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fromDt = new SimpleDateFormat("dd-MMM-yyyy").format(frmDt);
        toDt = new SimpleDateFormat("dd-MMM-yyyy").format(tooDt);
        String q;
        q = readFile(Charset.defaultCharset(), "PORTFOLIO_MONITORING.txt");
        Query rs5 = em.createNativeQuery(q).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("branch", new BigDecimal(obj[3].toString()).longValue()).setParameter("p_type", type);
        List<Object[]> overdues = rs5.getResultList();

        // Query q = em.createNativeQuery(
        // com.idev4.rds.util.Queries.PORTFOLIO_MONITORING ).setParameter( "frmdt",
        // fromDt )
        // .setParameter( "todt", toDt ).setParameter( "branch", new BigDecimal( obj[ 3
        // ].toString() ).longValue() );
        // List< Object[] > overdues = rs5.getResultList();

        List<Map<String, ?>> paymentsList = new ArrayList();
        for (Object[] r : overdues) {
            Map<String, Object> map = new HashMap();
            if (r[4] != null && new BigDecimal(r[4].toString()).longValue() > 0) {
                map.put("PRD_NM", r[0].toString());
                map.put("PORT_NM", r[1].toString());
                map.put("PORT_HANDEL_DT", getFormaedDate(r[2].toString()));
                map.put("OST_CLNTS", r[3] == null ? 0 : new BigDecimal(r[3].toString()).longValue());
                map.put("OST_AMT", r[4] == null ? 0 : new BigDecimal(r[4].toString()).longValue());
                map.put("PERIOD_DUE_CLNTS", r[5] == null ? 0 : new BigDecimal(r[5].toString()).longValue());
                map.put("PERIOD_DUE_AMT", r[6] == null ? 0 : new BigDecimal(r[6].toString()).longValue());
                map.put("PERIOD_PAID_CLNTS", r[7] == null ? 0 : new BigDecimal(r[7].toString()).longValue());
                map.put("PERIOD_PAID_AMT", r[8] == null ? 0 : new BigDecimal(r[8].toString()).longValue());
                map.put("TRGT_CLNTS", r[9] == null ? 0 : new BigDecimal(r[9].toString()).longValue());
                map.put("ACHIV_CLNTS", r[10] == null ? 0 : new BigDecimal(r[10].toString()).longValue());
                map.put("TRGT_AMT", r[11] == null ? 0 : new BigDecimal(r[11].toString()).longValue());
                map.put("ACHIV_AMT", r[12] == null ? 0 : new BigDecimal(r[12].toString()).longValue());
                map.put("OD_LOANS", r[13] == null ? 0 : new BigDecimal(r[13].toString()).longValue());
                map.put("OD_AMT", r[14] == null ? 0 : new BigDecimal(r[14].toString()).longValue());
                map.put("DAY_1_OD_CLNTS", r[15] == null ? 0 : new BigDecimal(r[15].toString()).longValue());
                map.put("DAY_1_OD_AMT", r[16] == null ? 0 : new BigDecimal(r[16].toString()).longValue());
                map.put("DAY_30_OD_CLNTS", r[17] == null ? 0 : new BigDecimal(r[17].toString()).longValue());
                map.put("DAY_30_OD_AMT", r[18] == null ? 0 : new BigDecimal(r[18].toString()).longValue());
                map.put("CMPLTD_CLNTS", r[19] == null ? 0 : new BigDecimal(r[19].toString()).longValue());
                map.put("BP_OD_CLNTS", r[20] == null ? 0 : new BigDecimal(r[20].toString()).longValue());
                map.put("BP_OD_AMT", r[21] == null ? 0 : new BigDecimal(r[21].toString()).longValue());

                paymentsList.add(map);
            }
        }

        String bdoScript;
        bdoScript = readFile(Charset.defaultCharset(), "PORTFOLIO_MONITORING_BDO.txt");
        Query bdoResultQuery = em.createNativeQuery(bdoScript).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("branch", new BigDecimal(obj[3].toString()).longValue()).setParameter("p_type", type);
        List<Object[]> bdoResultSet = bdoResultQuery.getResultList();


        List<Map<String, ?>> bdoList = new ArrayList();
        for (Object[] r : bdoResultSet) {
            Map<String, Object> map = new HashMap();
            map.put("PORT_NM", r[0] == null ? "" : r[0].toString());
            map.put("PORT_HANDEL_DT", r[1] == null ? "" : getFormaedDate(r[1].toString()));
            map.put("TRGT_CLNTS", r[2] == null ? 0 : new BigDecimal(r[2].toString()).longValue());
            map.put("ACHIV_CLNTS", r[3] == null ? 0 : new BigDecimal(r[3].toString()).longValue());
            map.put("TRGT_AMT", r[4] == null ? 0 : new BigDecimal(r[4].toString()).longValue());
            map.put("ACHIV_AMT", r[5] == null ? 0 : new BigDecimal(r[5].toString()).longValue());

            bdoList.add(map);
        }
        params.put("bdoDataset", getJRDataSource(bdoList));

        return reportComponent.generateReport(MONITORING, params, getJRDataSource(paymentsList));
    }

    public byte[] getFundStatmentReport(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String loanProtfolio;
        loanProtfolio = readFile(Charset.defaultCharset(), "FUND_STATMENT_HEADER.txt");
        Query rs5 = em.createNativeQuery(loanProtfolio).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("branch", obj[4].toString());
        // Query loanProtfolio = em.createNativeQuery(
        // com.idev4.rds.util.Queries.FUND_STATMENT_HEADER ).setParameter( "frmdt",
        // fromDt )
        // .setParameter( "todt", toDt ).setParameter( "branch", obj[ 4 ].toString() );
        List<Object[]> loanPrts = rs5.getResultList();
        List<Map<String, ?>> loanPrtss = new ArrayList();
        loanPrts.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRD_GRP_NM", l[0] == null ? "" : l[0].toString());
            map.put("DSB_LOANS", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("DSB_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("REC", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("DIP", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("IP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("DDOC", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("DOC", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("DTF", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("TF", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("DKSZB", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("KSZB", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("FC", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("LA", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            loanPrtss.add(map);
        });
        params.put("dataset", getJRDataSource(loanPrtss));
        try {
            params.put("from_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(fromDt)));
            params.put("to_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(toDt)));

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String fundStatHead1;
        fundStatHead1 = readFile(Charset.defaultCharset(), "FUND_STATMENT_HEADER1.txt");
        Query resultset = em.createNativeQuery(fundStatHead1).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("branch", obj[4].toString());
        // Query loanProtfolio = em.createNativeQuery(
        // com.idev4.rds.util.Queries.FUND_STATMENT_HEADER ).setParameter( "frmdt",
        // fromDt )
        // .setParameter( "todt", toDt ).setParameter( "branch", obj[ 4 ].toString() );
        List<Object[]> fundStat = resultset.getResultList();
        List<Map<String, ?>> fundStats = new ArrayList();
        fundStat.forEach(l -> {
            Map<String, Object> mapp = new HashMap();
            mapp.put("PRD_GRP_NM", l[0] == null ? "" : l[0].toString());
            mapp.put("DSB_LOANS", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            mapp.put("DSB_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            mapp.put("REC", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            mapp.put("DIP", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            mapp.put("IP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            mapp.put("DDOC", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            mapp.put("DOC", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            mapp.put("DTF", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            mapp.put("TF", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            mapp.put("DKSZB", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            mapp.put("KSZB", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            mapp.put("FC", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            mapp.put("LA", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            fundStats.add(mapp);
        });
        params.put("datasett", getJRDataSource(fundStats));
        try {
            params.put("from_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(fromDt)));
            params.put("to_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(toDt)));

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String expnsQuery;
        expnsQuery = readFile(Charset.defaultCharset(), "FUND_STATMENT_REPORT.txt");
        Query rs = em.createNativeQuery(expnsQuery).setParameter("frmDt", fromDt).setParameter("toDt", toDt)
                .setParameter("brnch", obj[4].toString());

        // Query expnsQuery = em.createNativeQuery(
        // com.idev4.rds.util.Queries.FUND_STATMENT_REPORT ).setParameter( "frmDt",
        // fromDt )
        // .setParameter( "toDt", toDt ).setParameter( "brnch", obj[ 4 ].toString() );
        List<Object[]> expnsObj = rs.getResultList();
        List<Map<String, ?>> expns = new ArrayList();
        expnsObj.forEach(e -> {
            Map<String, Object> map = new HashMap();
            map.put("GRP", e[0].toString());
            map.put("PERD_KEY", e[1] == null ? 0 : new BigDecimal(e[1].toString()).longValue());
            map.put("PERD_STRT_DT", e[2].toString());
            map.put("PERD_END_DT", e[3].toString());
            map.put("TYP_STR", e[5].toString());
            map.put("BDGT_AMT", e[6] == null ? 0 : new BigDecimal(e[6].toString()).longValue());
            map.put("CASH_DB_AMT", e[7] == null ? 0 : new BigDecimal(e[7].toString()).longValue());
            map.put("CASH_CR_AMT", e[8] == null ? 0 : new BigDecimal(e[8].toString()).longValue());
            map.put("BNK_DB_AMT", e[9] == null ? 0 : new BigDecimal(e[9].toString()).longValue());
            map.put("BNK_CR_AMT", e[10] == null ? 0 : new BigDecimal(e[10].toString()).longValue());
            expns.add(map);
        });
        return reportComponent.generateReport(FUNDSTATEMENT, params, getJRDataSource(expns));
    }

    public byte[] getInsuClmFrm(long clntSeq, String userId) throws IOException {

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(clntSeq, true);

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.USER_BRANCH_INFO).setParameter("portKey",
                clnt.getPortKey());
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String clntinfoQry;
        clntinfoQry = readFile(Charset.defaultCharset(), "InsuClmFrmclntinfoQry.txt");
        Query rs = em.createNativeQuery(clntinfoQry).setParameter("clntSeq", clntSeq);

        /*
         * Query clntinfoQry = em.createNativeQuery(
         * "select c.clnt_id,c.frst_nm||' '||c.last_nm clnt_nm,c.cnic_num,c.dob clnt_dob,dr.dt_of_dth,dr.cause_of_dth,dr.clnt_nom_flg,cr.frst_nm||' '||cr.last_nm rel_nm,\r\n"
         * +
         * "cr.cnic_num re_cnic,cr.dob rel_dob,e.instr_num,e.crtd_dt,app.prnt_loan_app_seq\r\n"
         * + "from mw_clnt c\r\n" +
         * "join mw_loan_app app on app.clnt_seq = c.clnt_seq and app.crnt_rec_flg=1 and app.prnt_loan_app_seq=app.loan_app_seq\r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq=app.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and val.ref_cd ='0005'\r\n"
         * +
         * "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
         * +
         * "join mw_dth_rpt dr on c.clnt_seq = dr.clnt_seq and dr.crnt_rec_flg= 1 and dr.crtd_dt>app.crtd_dt\r\n"
         * +
         * "join mw_exp e on e.exp_ref=c.clnt_seq and e.crnt_rec_flg=1 and e.crtd_dt>app.crtd_dt\r\n"
         * +
         * "join mw_typs t on t.typ_seq = e.EXPNS_TYP_SEQ and t.crnt_rec_flg=1 and t.TYP_CTGRY_KEY=2 and t.TYP_ID='0424'\r\n"
         * +
         * "left outer join mw_clnt_rel cr on cr.loan_app_seq = app.prnt_loan_app_seq and cr.rel_typ_flg=1 and cr.crnt_rec_flg = 1\r\n"
         * + "where c.clnt_seq =:clntSeq and c.crnt_rec_flg = 1" ) .setParameter(
         * "clntSeq", clntSeq );
         */
        Object[] clntObj = (Object[]) rs.getSingleResult();

        params.put("clnt_id", clntObj[0].toString());
        params.put("clnt_nm", clntObj[1].toString());
        params.put("rel_nm", clntObj[7] == null ? "" : clntObj[7].toString());
        params.put("dt_of_dth", getFormaedDate(clntObj[4].toString()));
        params.put("cause_of_dth", clntObj[5].toString());
        int flg = new BigDecimal(clntObj[6].toString()).intValue();
        params.put("des_nm", flg == 0 ? clntObj[1].toString() : clntObj[7].toString());
        String desAg = flg == 0 ? (clntObj[3] == null ? "" : clntObj[3].toString())
                : (clntObj[9] == null ? "" : clntObj[9].toString());
        params.put("des_ag", calculateAge(getLocalDate(desAg), LocalDate.now()));
        params.put("des_cnic", flg == 0 ? (clntObj[2] == null ? "" : clntObj[2].toString())
                : clntObj[8] == null ? "" : clntObj[8].toString());
        params.put("inst_no", clntObj[10] == null ? "" : clntObj[10].toString());
        params.put("pay_dt", getFormaedDate(clntObj[11].toString()));
        params.put("form_title_main", clntObj[13].toString().equals("0010") ? "" : "Credit for Life");
        params.put("form_title", clntObj[13].toString().equals("0010") ? "Takaful Claim Form" : "Insurance Claim Form");
/*        params.put("form_title_main", "Credit for Life");
        params.put("form_title", "Claim Form");*/

        String loanInfoQry;
        loanInfoQry = readFile(Charset.defaultCharset(), "InsuClmFrmloanInfoQry.txt");
        Query rs1 = em.createNativeQuery(loanInfoQry).setParameter("clntSeq", clntSeq);

        /*
         * Query loanInfoQry = em.createNativeQuery( "SELECT dsbmt.prd_nm,\r\n" +
         * "       dsbmt.dsbmt_dt,\r\n" + "       dsbmt.dsbmt_amt,\r\n" +
         * "       ag.ag_dt,\r\n" + "       ag.ag_amt,\r\n" + "       rc.svc_chg,\r\n" +
         * "       rc.paid_amt,dsbmt.prd_id\r\n" +
         * "  FROM (  SELECT app.loan_app_seq,\r\n" + "                 p.prd_nm,\r\n" +
         * "                 app.loan_app_sts_dt     dsbmt_dt,\r\n" +
         * "                 SUM (amt)               dsbmt_amt,p.prd_id\r\n" +
         * "            FROM mw_loan_app app\r\n" +
         * "                 JOIN mw_ref_cd_val sts\r\n" +
         * "                     ON     sts.ref_cd_seq = app.loan_app_sts\r\n" +
         * "                        AND sts.crnt_rec_flg = 1\r\n" +
         * "                 JOIN mw_prd p\r\n" +
         * "                     ON p.PRD_SEQ = app.PRD_SEQ AND p.CRNT_REC_FLG = 1\r\n"
         * + "                 JOIN MW_DSBMT_VCHR_HDR dvh\r\n" +
         * "                     ON     dvh.LOAN_APP_SEQ = app.LOAN_APP_SEQ\r\n" +
         * "                        AND dvh.CRNT_REC_FLG = 1\r\n" +
         * "                 JOIN MW_DSBMT_VCHR_DTL dvd\r\n" +
         * "                     ON     dvd.DSBMT_HDR_SEQ = dvh.DSBMT_HDR_SEQ\r\n" +
         * "                        AND dvd.CRNT_REC_FLG = 1\r\n" +
         * "           WHERE     app.crnt_rec_flg = 1\r\n" +
         * "                 AND dvh.dsbmt_vchr_typ = 0\r\n" +
         * "                 AND sts.ref_cd = '0005'\r\n" +
         * "                 AND app.clnt_seq = :clntSeq\r\n" +
         * "        GROUP BY app.loan_app_seq, p.prd_nm, app.loan_app_sts_dt ,p.prd_id) dsbmt\r\n"
         * + "       LEFT OUTER JOIN\r\n" +
         * "       (  SELECT app.loan_app_seq, app.loan_app_sts_dt ag_dt, SUM (amt) ag_amt\r\n"
         * + "            FROM mw_loan_app app\r\n" +
         * "                 JOIN mw_ref_cd_val sts\r\n" +
         * "                     ON     sts.ref_cd_seq = app.loan_app_sts\r\n" +
         * "                        AND sts.crnt_rec_flg = 1\r\n" +
         * "                 JOIN mw_prd p\r\n" +
         * "                     ON p.PRD_SEQ = app.PRD_SEQ AND p.CRNT_REC_FLG = 1\r\n"
         * + "                 JOIN MW_DSBMT_VCHR_HDR dvh\r\n" +
         * "                     ON     dvh.LOAN_APP_SEQ = app.LOAN_APP_SEQ\r\n" +
         * "                        AND dvh.CRNT_REC_FLG = 1\r\n" +
         * "                 JOIN MW_DSBMT_VCHR_DTL dvd\r\n" +
         * "                     ON     dvd.DSBMT_HDR_SEQ = dvh.DSBMT_HDR_SEQ\r\n" +
         * "                        AND dvd.CRNT_REC_FLG = 1\r\n" +
         * "           WHERE     app.crnt_rec_flg = 1\r\n" +
         * "                 AND dvh.dsbmt_vchr_typ = 1\r\n" +
         * "                 AND sts.ref_cd = '0005'\r\n" +
         * "                 AND app.clnt_seq = :clntSeq \r\n" +
         * "        GROUP BY app.loan_app_seq, p.prd_nm, app.loan_app_sts_dt) ag\r\n" +
         * "           ON ag.loan_app_seq = dsbmt.loan_app_seq\r\n" + "       JOIN\r\n"
         * + "       (  SELECT app.loan_app_seq,\r\n" +
         * "                 SUM (psd.TOT_CHRG_DUE)     svc_chg,\r\n" +
         * "                 SUM (ramt)                 paid_amt\r\n" +
         * "            FROM mw_loan_app app\r\n" +
         * "                 JOIN mw_ref_cd_val sts\r\n" +
         * "                     ON     sts.ref_cd_seq = app.loan_app_sts\r\n" +
         * "                        AND sts.crnt_rec_flg = 1\r\n" +
         * "                 JOIN MW_PYMT_SCHED_HDR psh\r\n" +
         * "                     ON     psh.LOAN_APP_SEQ = app.LOAN_APP_SEQ\r\n" +
         * "                        AND psh.CRNT_REC_FLG = 1\r\n" +
         * "                 JOIN MW_PYMT_SCHED_DTL psd\r\n" +
         * "                     ON     psd.PYMT_SCHED_HDR_SEQ = psh.PYMT_SCHED_HDR_SEQ\r\n"
         * + "                        AND psd.CRNT_REC_FLG = 1\r\n" +
         * "                 LEFT OUTER JOIN\r\n" +
         * "                 (   SELECT PYMT_SCHED_DTL_SEQ, SUM (r.PYMT_AMT) ramt \r\n"
         * + "                      FROM mw_rcvry_dtl r\r\n" +
         * "                      WHERE     (r.CHRG_TYP_KEY IN (select typ_seq from mw_typs where typ_id='0017' and TYP_CTGRY_KEY=1 and CRNT_REC_FLG=1) OR r.CHRG_TYP_KEY = -1) \r\n"
         * + "                      AND r.crnt_rec_flg = 1\r\n" +
         * "                      GROUP BY PYMT_SCHED_DTL_SEQ) rc\r\n" +
         * "                     ON rc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
         * + "           WHERE     app.crnt_rec_flg = 1\r\n" +
         * "                 AND sts.ref_cd = '0005'\r\n" +
         * "                 AND app.clnt_seq =:clntSeq \r\n" +
         * "        GROUP BY app.loan_app_seq, app.loan_app_sts_dt) rc\r\n" +
         * "           ON rc.loan_app_seq = dsbmt.loan_app_seq order by dsbmt.PRD_NM"
         * ).setParameter( "clntSeq", clntSeq );
         */

        List<Object[]> loanInfo = rs1.getResultList();
        List<Map> loanInfoList = new ArrayList();
        loanInfo.forEach(l -> {
            Map loan = new HashMap();
            loan.put("prd_name", l[0].toString());
            loan.put("dis_dt", getFormaedDate(l[1].toString()));
            loan.put("dis_amt", getLongValue(l[2].toString()));
            loan.put("ag_dt", l[3] == null ? "" : getFormaedDate(l[3].toString()));
            loan.put("ad_amt", l[4] == null ? 0 : getLongValue(l[4].toString()));
            loan.put("rec_amt",
                    (l[2] == null ? 0 : getLongValue(l[2].toString()))
                            + (l[5] == null ? 0 : getLongValue(l[5].toString()))
                            - (l[6] == null ? 0 : getLongValue(l[6].toString())));
            loanInfoList.add(loan);
        });
        params.put("dataset", getJRDataSource(loanInfoList));

        /*
         * params.put( "prd_name", loanInfo.get( 0 )[ 0 ].toString() ); params.put(
         * "dis_dt", getFormaedDate( loanInfo.get( 0 )[ 1 ].toString() ) ); params.put(
         * "dis_amt", getLongValue( loanInfo.get( 0 )[ 2 ].toString() ) );
         */
        String paymentsQry;
        paymentsQry = readFile(Charset.defaultCharset(), "InsuClmFrmpaymentsQry.txt");
        Query rs2 = em.createNativeQuery(paymentsQry).setParameter("clntSeq", clntSeq);

        /*
         * Query paymentsQry = em.createNativeQuery(
         * "select DUE_DT,sum(ppal_amt_due),sum(tot_chrg_due),sum(amt),sum(PR_REC),sum(SC_REC), pymt_dt  from(   \r\n"
         * +
         * "select psd.inst_num,psd.DUE_DT DUE_DT,psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) as amt,psd.pymt_sched_dtl_seq,   \r\n"
         * + "(select sum(pymt_amt)   \r\n" +
         * "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and CHRG_TYP_KEY=-1 and crnt_rec_flg=1) as PR_REC,   \r\n"
         * + "(select sum(pymt_amt)   \r\n" + "from mw_rcvry_dtl dtl\r\n" +
         * "join mw_typs t on t.typ_seq=dtl.CHRG_TYP_KEY and t.CRNT_REC_FLG=1\r\n" +
         * "where typ_id='0017' and TYP_CTGRY_KEY=1 and dtl.CRNT_REC_FLG=1 and pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq ) as SC_REC,   \r\n"
         * + "(select PYMT_DT  from mw_rcvry_dtl rd   \r\n" +
         * "join mw_rcvry_trx rt on rd.RCVRY_TRX_SEQ = rt.RCVRY_TRX_SEQ and rt.CRNT_REC_FLG = 1    \r\n"
         * +
         * "where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.crnt_rec_flg=1  and CHRG_TYP_KEY=-1 and rownum=1) as pymt_dt   \r\n"
         * + "from mw_loan_app la   \r\n" +
         * "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and val.ref_cd ='0005'    \r\n"
         * +
         * "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'    \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1   \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1   \r\n"
         * +
         * "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1   \r\n"
         * + "where la.clnt_seq=:clntSeq and la.crnt_rec_flg=1    \r\n" +
         * "group by psd.inst_num,psd.DUE_DT,psd.ppal_amt_due,psd.tot_chrg_due,psd.pymt_sched_dtl_seq   \r\n"
         * + "order by inst_num)group by inst_num,DUE_DT,pymt_dt  order by DUE_DT" )
         * .setParameter( "clntSeq", clntSeq );
         */
        List<Object[]> pymtsObj = rs2.getResultList();
        List<Map<String, ?>> pymts = new ArrayList();
        Long outSts = 0L;
        long inst = 0;
        String oldDate = "";
        for (Object[] l : pymtsObj) {
            Map<String, Object> map = new HashMap();
            if (!l[0].toString().equals(oldDate)) {
                inst++;
            }
            map.put("INST_NUM", inst);
            map.put("DUE_DT", getFormaedDate(l[0].toString()));
            map.put("PPAL_AMT_DUE", l[1] == null ? 0 : getLongValue(l[1].toString()));
            map.put("TOT_CHRG_DUE", l[2] == null ? 0 : getLongValue(l[2].toString()));
            map.put("AMT", l[3] == null ? 0 : getLongValue(l[3].toString()));
            map.put("PR_REC", l[4] == null ? 0 : getLongValue(l[4].toString()));
            map.put("SC_REC", l[5] == null ? 0 : getLongValue(l[5].toString()));
            map.put("PYMT_DT", l[6] == null ? "" : getFormaedDate(l[6].toString()));

            oldDate = l[0].toString();
            pymts.add(map);

        }

        return reportComponent.generateReport(INSURANCECLAIM, params, getJRDataSource(pymts));
    }

    public byte[] getAccountLedger(AccountLedger accountLedger, String userId) throws IOException {

        Map<String, Object> params = new HashMap<>();
        if (accountLedger.branch != null) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                    accountLedger.branch);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        }
        MwLedgerHeads account = mwLedgerHeadsRepository.findOneByCustSegments(accountLedger.account);

        params.put("acct_det", account.getCustAccDesc());
        params.put("curr_user", userId);
        params.put("from_dt", new SimpleDateFormat("dd-MM-yyyy").format(accountLedger.frmDt));
        params.put("to_dt", new SimpleDateFormat("dd-MM-yyyy").format(accountLedger.toDt));

        Query q = em.createNativeQuery("select op_blnc_ho(to_date(:FROM_DATE,'MM/dd/yyyy'),:GL_ACC_NUM) bal from dual")
                .setParameter("FROM_DATE", formateTimeZoneDate(accountLedger.frmDt.toString()))
                .setParameter("GL_ACC_NUM", accountLedger.account);

        if (accountLedger.branch != null) {
            q = em.createNativeQuery(
                            "select op_blnc(to_date(:FROM_DATE,'MM/dd/yyyy'),:BRNCH_SEQ,:GL_ACC_NUM) bal from dual")
                    .setParameter("BRNCH_SEQ", accountLedger.branch)
                    .setParameter("FROM_DATE", formateTimeZoneDate(accountLedger.frmDt.toString()))
                    .setParameter("GL_ACC_NUM", accountLedger.account);
        }
        BigDecimal openBal = null;
        try {
            openBal = (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
        }
        long balnce = getLongValue(openBal == null ? "0" : openBal.toString());

        params.put("open_bal", balnce);

        String detailQry;
        detailQry = readFile(Charset.defaultCharset(), "AccountLedgerdetailQry.txt");
        Query rs2 = em.createNativeQuery(detailQry)
                .setParameter("FROM_DATE", formateTimeZoneDate(accountLedger.frmDt.toString()))
                .setParameter("TO_DATE", formateTimeZoneDate(accountLedger.toDt.toString()))
                .setParameter("GL_ACC_NUM", accountLedger.account);

        /*
         * Query detailQry = em.createNativeQuery( "SELECT TO_DATE (MJH.JV_DT)\r\n" +
         * "             VOUCHER_DATE,\r\n" +
         * "             get_clnt_seq (MJH.ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)  CLNT_INFO_seq,\r\n"
         * +
         * "         get_clnt_name (MJH.ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)  CLNT_INFO_name,\r\n"
         * + "         INITCAP (MJH.ENTY_TYP)\r\n" + "             VOUCHER_TYPE,\r\n" +
         * "         MJH.JV_HDR_SEQ , MJH.ENTY_SEQ,\r\n" +
         * "         get_instr_num(MJH.ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) INSTR_NO,get_narration(MJH.ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) NAR_NO, \r\n"
         * + "         MJH.JV_DSCR,\r\n" +
         * "         NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN MJD.AMT END), 0)\r\n" +
         * "             DEBIT,\r\n" +
         * "         NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN MJD.AMT END), 0)\r\n" +
         * "             CREDIT                                                   --DFGFGG\r\n"
         * + "    FROM MW_JV_HDR MJH, MW_JV_DTL MJD, MW_BRNCH MB\r\n" +
         * "WHERE     MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ\r\n" +
         * "         AND MB.CRNT_REC_FLG = 1\r\n" + "         AND MJD.AMT > 0\r\n" +
         * "         AND TO_DATE (MJH.JV_DT) BETWEEN TO_DATE (:FROM_DATE, 'MM/dd/yyyy')\r\n"
         * +
         * "                                     AND TO_DATE (:TO_DATE, 'MM/dd/yyyy')\r\n"
         * + "         AND MJD.GL_ACCT_NUM =:GL_ACC_NUM\r\n" +
         * "ORDER BY MJH.JV_DT, MJH.JV_HDR_SEQ || '/' || MJH.ENTY_SEQ  DESC" )
         * .setParameter( "FROM_DATE", formateTimeZoneDate(
         * accountLedger.frmDt.toString() ) ) .setParameter( "TO_DATE",
         * formateTimeZoneDate( accountLedger.toDt.toString() ) ) .setParameter(
         * "GL_ACC_NUM", accountLedger.account );
         */

        if (accountLedger.branch != null) {

            /*
             * detailQry = em.createNativeQuery(
             * "select   to_date (mjh.jv_dt) voucher_date, \r\n" +
             * "                              get_clnt_seq (mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq)  clnt_info_seq, \r\n"
             * +
             * "                              get_clnt_name (mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq)  clnt_info_name, \r\n"
             * +
             * "                              (case when initcap (mjh.enty_typ) = 'Expense' then \r\n"
             * +
             * "                                    (select max(mt.TYP_STR) from mw_typs mt where mt.gl_acct_num = mjd.gl_acct_num and mt.TYP_CTGRY_KEY = 2) \r\n"
             * +
             * "                               else initcap (mjh.enty_typ) end ) voucher_type, \r\n"
             * + "                              mjh.jv_hdr_seq ,mjh.enty_seq, \r\n" +
             * "                              get_instr_num(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq) instr_no, \r\n"
             * +
             * "                              get_narration(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq) nar_no, \r\n"
             * +
             * "                              get_voucher_type(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq) jv_dscr,  \r\n"
             * +
             * "                     nvl(sum(nvl((case when mjd.crdt_dbt_flg = 1 then nvl(mjd.amt,0) end),0)),0) debit, \r\n"
             * +
             * "                     nvl(sum(nvl((case when mjd.crdt_dbt_flg = 0 then nvl(mjd.amt,0) end),0)),0) credit \r\n"
             * +
             * "                     from mw_jv_hdr mjh, mw_jv_dtl mjd  where mjh.jv_hdr_seq = mjd.jv_hdr_seq \r\n"
             * +
             * "                     and brnch_seq = :brnchseq  and mjd.gl_acct_num =:gl_acc_num \r\n"
             * +
             * "                     and to_date(mjh.jv_dt)  between to_date(:frmdt,'MM/dd/yyyy') and to_date(:todt,'MM/dd/yyyy') \r\n"
             * +
             * "                     group by to_date (mjh.jv_dt),            get_clnt_seq (mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq), \r\n"
             * +
             * "                              get_clnt_name (mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq), \r\n"
             * +
             * "                              initcap (mjh.enty_typ) , mjh.jv_hdr_seq ,mjh.enty_seq, \r\n"
             * +
             * "                              get_voucher_type(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq),\r\n"
             * +
             * "                              get_instr_num(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq) , mjd.gl_acct_num,\r\n"
             * +
             * "                              get_narration(mjh.enty_seq, mjh.enty_typ, mjh.brnch_seq) ,mjh.jv_dscr \r\n"
             * +
             * "                     order by to_date (mjh.jv_dt), mjh.jv_hdr_seq,mjh.enty_seq  desc"
             * ) .setParameter( "brnchseq", accountLedger.branch ) .setParameter( "frmdt",
             * formateTimeZoneDate( accountLedger.frmDt.toString() ) ) .setParameter(
             * "todt", formateTimeZoneDate( accountLedger.toDt.toString() ) ) .setParameter(
             * "gl_acc_num", accountLedger.account );
             */

            detailQry = readFile(Charset.defaultCharset(), "AccountLedgerdetailQry_2.txt");
            rs2 = em.createNativeQuery(detailQry).setParameter("brnchseq", accountLedger.branch)
                    .setParameter("frmdt", formateTimeZoneDate(accountLedger.frmDt.toString()))
                    .setParameter("todt", formateTimeZoneDate(accountLedger.toDt.toString()))
                    .setParameter("gl_acc_num", accountLedger.account);

        }

        List<Object[]> dtlObj = rs2.getResultList();

        List<Map<String, ?>> pymts = new ArrayList();

        dtlObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("VOUCHER_DATE", getFormaedDateShortYear(l[0].toString()));
            map.put("VOUCHER_TYPE", l[1] == null ? "" : l[1].toString());
            map.put("JV_HDR_SEQ", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("ENTY_SEQ", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("DSCR", l[4] == null ? "" : l[4].toString());
            map.put("INSTR_NO", l[5] == null ? "" : l[5].toString());
            map.put("CLNT_INFO_SEQ", l[6] == null ? "" : l[6].toString());
            map.put("CLNT_INFO_NAME", l[7] == null ? "" : l[7].toString());
            // Modified by Naveed - Date - 01-03-2022
            // change loanValue to Double to display float point report
            map.put("DEBIT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).doubleValue());
            map.put("CREDIT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).doubleValue());
            map.put("CLS_BLNC", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            pymts.add(map);
        });

        String headerQuery;
        headerQuery = readFile(Charset.defaultCharset(), "AccountLedgerHeaderQry.txt");
        Query header = em.createNativeQuery(headerQuery).setParameter("brnchseq", accountLedger.branch)
                .setParameter("frmdt", formateTimeZoneDate(accountLedger.frmDt.toString()))
                .setParameter("todt", formateTimeZoneDate(accountLedger.toDt.toString()))
                .setParameter("gl_acc_num", accountLedger.account);

        List<Object[]> hdrObj = header.getResultList();

        List<Map<String, ?>> hdrRes = new ArrayList();

        hdrObj.forEach(l -> {
            Map<String, Object> hrdmap = new HashMap();
            hrdmap.put("LEDGER_TYPE", l[0] == null ? "" : l[0].toString());
            // Modified by Naveed - Date - 01-03-2022
            // change loanValue to Double to display float point report
            hrdmap.put("DEBIT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).doubleValue());
            hrdmap.put("CREDIT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).doubleValue());
            hdrRes.add(hrdmap);
        });
        params.put("dataset", getJRDataSource(hdrRes));

        return reportComponent.generateReport(ACCOUNTSLEDGER, params,

                getJRDataSource(pymts));
    }

    public byte[] getKcrReport(long trxSeq, String userId, int type) throws IOException {

        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("brnch_nm", obj[2].toString());
        params.put("curr_user", userId);

        String qury;
        qury = readFile(Charset.defaultCharset(), "KcrReportqury.txt");
        // Query rs2 = em.createNativeQuery( qury );
        /*
         * String qury = "SELECT MJH.JV_HDR_SEQ,\r\n" + "         MJH.ENTY_TYP,\r\n" +
         * "         MJH.JV_DSCR,\r\n" + "         MJH.JV_DT,\r\n" +
         * "         MRT.INSTR_NUM,\r\n" + "         MC.CLNT_SEQ,\r\n" +
         * "         MC.FRST_NM || ' ' || MC.LAST_NM     Name,\r\n" +
         * "         SUM (NVL (MJD.AMT, 0))              amt,T.TYP_ID   FROM MW_RCVRY_TRX MRT,\r\n"
         * + "         MW_JV_HDR   MJH,\r\n" + "         MW_JV_DTL   MJD,\r\n" +
         * "         MW_CLNT     MC,\r\n" + "         MW_TYPS T\r\n" +
         * "   WHERE     MRT.CRNT_REC_FLG = 1\r\n" + "         AND MRT.POST_FLG = 1\r\n"
         * + "         AND MRT.RCVRY_TRX_SEQ = :TRX_SEQ\r\n" +
         * "         AND MRT.RCVRY_TRX_SEQ = MJH.ENTY_SEQ\r\n" +
         * "         AND MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ\r\n" +
         * "         AND MRT.PYMT_REF = MC.CLNT_SEQ\r\n" +
         * "         AND MC.CRNT_REC_FLG = 1\r\n" +
         * "         AND MJD.CRDT_DBT_FLG = 0\r\n" +
         * "         AND T.TYP_SEQ=MRT.RCVRY_TYP_SEQ AND T.TYP_ID='0001'\r\n" +
         * "         AND UPPER (MJH.ENTY_TYP) IN ('RECOVERY', 'EXCESS RECOVERY')\r\n" +
         * "GROUP BY MJH.ENTY_TYP,\r\n" + "         MJH.JV_DSCR,\r\n" +
         * "         MJH.JV_DT,\r\n" + "         MRT.INSTR_NUM,\r\n" +
         * "         MC.CLNT_SEQ,\r\n" + "         MC.FRST_NM || ' ' || MC.LAST_NM,\r\n"
         * + "         MJH.JV_HDR_SEQ,T.TYP_ID";
         */

        if (type == 1) {

            qury = readFile(Charset.defaultCharset(), "KcrReportqury_2.txt");
            /*
             * qury = " select mjh.jv_hdr_seq,\r\n" + "         mjh.enty_typ,\r\n" +
             * "         mjh.jv_dscr,\r\n" + "         mjh.jv_dt,\r\n" +
             * "         me.instr_num,\r\n" + "         mc.clnt_seq,\r\n" +
             * "         mc.frst_nm || ' ' || mc.last_nm     name ,me.expns_amt amt,t.typ_id\r\n"
             * + "    from mw_exp me\r\n" + "         join mw_jv_hdr mjh\r\n" +
             * "             on me.exp_seq = mjh.enty_seq and mjh.enty_typ = 'Expense'\r\n"
             * + "         left outer join mw_clnt mc\r\n" +
             * "             on me.exp_ref = mc.clnt_seq and mc.crnt_rec_flg = 1\r\n" +
             * "         join mw_typs t on t.typ_seq = me.expns_typ_seq \r\n" +
             * "   where me.exp_seq = :TRX_SEQ and me.crnt_rec_flg = 1";
             */
        } else if (type == 2) {
            qury = readFile(Charset.defaultCharset(), "KcrReportqury_3.txt");
        }

        Query q = em.createNativeQuery(qury).setParameter("TRX_SEQ", trxSeq);

        try {
            List<Object[]> kcrObj = q.getResultList();
            if (kcrObj.size() == 0) {
                return null;
            }
            long amt = 0;
            params.put("JV_HDR_SEQ", kcrObj.get(0)[0].toString());
            params.put("ENTY_TYP", kcrObj.get(0)[1].toString());
            params.put("JV_DSCR", kcrObj.get(0)[2].toString());
            params.put("JV_DT", getFormaedDate(kcrObj.get(0)[3].toString()));
            params.put("INSTR_NUM", kcrObj.get(0)[4] == null ? "" : kcrObj.get(0)[4].toString());
            params.put("CLNT_SEQ", kcrObj.get(0)[5] == null ? "" : kcrObj.get(0)[5].toString());
            params.put("NAME", kcrObj.get(0)[6] == null ? "" : kcrObj.get(0)[6].toString());
            amt = kcrObj.get(0)[7] == null ? 0 : new BigDecimal(kcrObj.get(0)[7].toString()).longValue();
            if (kcrObj.get(0)[8].toString().equals("0424")) {
                amt = 5000 - new BigDecimal(kcrObj.get(0)[7].toString()).longValue();
                params.put("ENTY_TYP", "RECOVERY");
                params.put("JV_DSCR", "RECOVERY");
            }

            params.put("AMT", amt);

            if (type == 4) {
                return reportComponent.generateReport(KCR_DSBLTY, params, null);
            } else {
                return reportComponent.generateReport(KCR, params, null);
            }

        } catch (Exception e) {

        }
        return null;

    }

    public byte[] getDueRecovery(BookDetailsDTO bookDetailsDTO, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (bookDetailsDTO.branch != null) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                    bookDetailsDTO.branch);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        }
        params.put("curr_user", userId);
        params.put("from_dt", new SimpleDateFormat("dd-MM-yyyy").format(bookDetailsDTO.frmDt));
        params.put("to_dt", new SimpleDateFormat("dd-MM-yyyy").format(bookDetailsDTO.toDt));

        String detailQry;
        detailQry = readFile(Charset.defaultCharset(), "DueRecoverydetailQry.txt");
        Query rs = em.createNativeQuery(detailQry).setParameter("BRNCH_SEQ", bookDetailsDTO.branch)
                .setParameter("BRNCH_SEQ", bookDetailsDTO.branch)
                .setParameter("frmdt", formateTimeZoneDate(bookDetailsDTO.frmDt.toString()))
                .setParameter("todt", formateTimeZoneDate(bookDetailsDTO.toDt.toString()));

        /*
         * Query detailQry = em.createNativeQuery( "select emp_seq,\r\n" + "emp_nm,\r\n"
         * + "clnt_id,\r\n" + "name,\r\n" + "fs_nm,\r\n" + "ph_num,\r\n" + "addr,\r\n" +
         * "DUE_DT,\r\n" + "prd_cmnt,\r\n" + "inst_amt, \r\n" + "due_amt,\r\n" +
         * "get_od_info(LOAN_APP_SEQ, (case when to_date(DUE_DT) < to_date(sysdate-1) then to_date(DUE_DT-1) else to_date(sysdate-1) end),'psc') od_amt,\r\n"
         * + "to_number(to_char(DUE_DT,'yyyyMMdd')) srt\r\n" + "from \r\n" + "(\r\n" +
         * "select la.port_seq emp_seq, get_port_bdo(la.port_Seq) emp_nm, mc.clnt_id,\r\n"
         * + "mc.frst_nm ||' '||mc.last_nm name,\r\n" +
         * "mc.fthr_frst_nm||case when mc.fthr_last_nm is not null then ' '||mc.fthr_last_nm end ||(case when mc.fthr_frst_nm is not null and mc.spz_frst_nm is not null then '/' end )||mc.spz_frst_nm||' '||mc.spz_last_nm fs_nm,\r\n"
         * +
         * "mc.ph_num, 'St. '||ad.strt||' H. '||ad.hse_num||' '||ad.oth_dtl||', '||city.city_nm addr,\r\n"
         * + "psd.DUE_DT,\r\n" + "prd.prd_cmnt,\r\n" +
         * "psd.inst_num inst_amt, (nvl(psd.PPAL_AMT_DUE,0) + nvl(psd.TOT_CHRG_DUE,0) + sum(nvl(psc.amt,0))) -\r\n"
         * + "nvl(max((\r\n" + "select sum(nvl(rdtl.PYMT_AMT,0) )rec\r\n" +
         * "from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n" +
         * "where rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ and rdtl.CRNT_REC_FLG=1\r\n"
         * + "and rdtl.RCVRY_TRX_SEQ = trx.RCVRY_TRX_SEQ and trx.CRNT_REC_FLG = 1\r\n" +
         * "and trx.PYMT_REF = la.clnt_seq \r\n" + ")),0) due_amt,\r\n" +
         * "la.prd_seq,  la.LOAN_APP_SEQ\r\n" +
         * "from mw_loan_app la, mw_pymt_sched_hdr psh, mw_pymt_sched_dtl psd, mw_pymt_sched_chrg psc, mw_clnt mc,\r\n"
         * +
         * "mw_addr_rel ar, mw_addr ad, mw_city_uc_rel cur,mw_city city,mw_prd prd, mw_port mp\r\n"
         * + "where la.LOAN_APP_SEQ = psh.LOAN_APP_SEQ and la.CRNT_REC_FLG = 1\r\n" +
         * "and psh.loan_app_seq=la.loan_app_seq and psh.crnt_rec_flg=1\r\n" +
         * "and psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * + "and psd.PYMT_SCHED_DTL_SEQ = psc.PYMT_SCHED_DTL_SEQ(+)\r\n" +
         * "and la.LOAN_APP_STS = 703\r\n" +
         * "and mc.CLNT_SEQ = la.CLNT_SEQ and mc.CRNT_REC_FLG = 1\r\n" +
         * "and ar.enty_key=mc.clnt_seq and ar.crnt_rec_flg = 1 and ar.enty_typ='Client'\r\n"
         * + "and ad.addr_seq = ar.addr_seq and ad.crnt_rec_flg = 1\r\n" +
         * "and cur.city_uc_rel_seq = ad.city_seq\r\n" +
         * "and city.city_seq = cur.city_seq and city.crnt_rec_flg = 1\r\n" +
         * "and prd.prd_seq=la.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "and la.PORT_SEQ = mp.PORT_SEQ and mp.CRNT_REC_FLG =1\r\n" +
         * "and mp.BRNCH_SEQ = :BRNCH_SEQ\r\n" +
         * "and psd.DUE_DT between to_date(:frmdt,'MM/dd/yyyy') and to_date(:todt,'MM/dd/yyyy')\r\n"
         * +
         * "group by  get_port_bdo(la.port_Seq),mc.clnt_id, la.prd_seq, la.LOAN_APP_SEQ, psd.DUE_DT,psd.inst_num, psd.PPAL_AMT_DUE, psd.TOT_CHRG_DUE,\r\n"
         * +
         * "mc.spz_last_nm, mc.spz_frst_nm, mc.fthr_last_nm, mc.fthr_frst_nm,mc.last_nm,mc.frst_nm,la.port_seq,mc.ph_num,\r\n"
         * + "ad.strt,ad.hse_num,ad.oth_dtl,city.city_nm,prd.prd_cmnt\r\n" + ")\r\n" +
         * "where due_amt > 0\r\n" + "order by 2,13" ) .setParameter( "BRNCH_SEQ",
         * bookDetailsDTO.branch ) .setParameter( "frmdt", formateTimeZoneDate(
         * bookDetailsDTO.frmDt.toString() ) ) .setParameter( "todt",
         * formateTimeZoneDate( bookDetailsDTO.toDt.toString() ) );
         */

        List<Object[]> dtlObj = rs.getResultList();

        List<Map<String, ?>> pymts = new ArrayList();
        dtlObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_SEQ", l[0] == null ? 0 : new BigDecimal(l[0].toString()).longValue());
            map.put("EMP_NM", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_ID", l[2] == null ? "" : l[2].toString());
            map.put("NAME", l[3] == null ? "" : l[3].toString());
            map.put("SPZ", l[4] == null ? "" : l[4].toString());
            map.put("PH_NUM", l[5] == null ? "" : l[5].toString());
            map.put("ADDR", l[6] == null ? "" : l[6].toString());
            map.put("DUE_DT", getFormaedDate(l[7].toString()));
            map.put("PRD_NM", l[8] == null ? "" : l[8].toString());
            map.put("INST_NUM", l[9] == null ? "" : l[9].toString());
            map.put("AMT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("OD", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            pymts.add(map);
        });
        return reportComponent.generateReport(DUERECOVERY, params,

                getJRDataSource(pymts));
    }

    public byte[] getBookDetails(BookDetailsDTO bookDetailsDTO, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                bookDetailsDTO.branch);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());

        /*
         * Query openQury = em.createNativeQuery(
         * "select opn_dt,bal.opn_bank_bal, bal.opn_cash_bal\r\n" +
         * "from mw_brnch_bal bal\r\n" +
         * "join mw_brnch_emp_rel br on br.brnch_seq=bal.brnch_seq and br.CRNT_REC_FLG=1  and br.del_flg=0\r\n"
         * +
         * "join mw_emp emp on emp.emp_seq = br.emp_seq and emp.emp_lan_id = :userId\r\n"
         * +
         * "where bal.opn_dt = (select max(opn_dt) from mw_brnch_bal a where a.opn_dt <= to_date(:date,'MM/dd/yyyy') and br.brnch_seq=a.brnch_seq) \r\n"
         * + "and bal.actv_flg = 1\r\n" + "and bal.crnt_rec_flg = 1" ).setParameter(
         * "userId", userId ) .setParameter( "date", formateTimeZoneDate(
         * bookDetailsDTO.frmDt.toString() ) ); Object[] open = null; try { open = (
         * Object[] ) openQury.getSingleResult(); } catch ( Exception e ) { // TODO
         * Auto-generated catch block e.printStackTrace(); } DateFormat inputFormat =
         * new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" ); String opnDt = ""; try {
         * opnDt = new SimpleDateFormat( "MM/dd/yyyy" ).format( inputFormat.parse( open[
         * 0 ].toString() ) ); } catch ( ParseException e ) { // TODO Auto-generated
         * catch block e.printStackTrace(); }
         */
        /*
         * DateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
         * String opnDt = ""; try { opnDt = new SimpleDateFormat( "MM/dd/yyyy" ).format(
         * inputFormat.parse( bookDetailsDTO.frmDt.toString() ) ); } catch (
         * ParseException e ) { // TODO Auto-generated catch block e.printStackTrace();
         * }
         */

        String openBalQury;
        openBalQury = readFile(Charset.defaultCharset(), "BookDetailsopenBalQury.txt");
        Query rs = em.createNativeQuery(openBalQury)
                .setParameter("date", formateTimeZoneDate(bookDetailsDTO.frmDt.toString()))
                .setParameter("branch", obj[4].toString());
        ;

        /*
         * Query openBalQury = em.createNativeQuery(
         * "select op_blnc(to_date( :date,'MM/dd/yyyy'),:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0001' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=0 )) cash, op_blnc(to_date( :date,'MM/dd/yyyy'),:branch,(select t.GL_ACCT_NUM\r\n"
         * +
         * "from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID ='0008' and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=:branch)) bank from dual"
         * ) .setParameter( "date", formateTimeZoneDate( bookDetailsDTO.frmDt.toString()
         * ) ) .setParameter( "branch", obj[ 4 ].toString() );
         */
        Object[] openBal = (Object[]) rs.getSingleResult();

        Long opnBnk = new BigDecimal(openBal[1].toString()).longValue();

        Long opnCsh = new BigDecimal(openBal[0].toString()).longValue();

        params.put("type", bookDetailsDTO.type == 1 ? "Bank" : "Cash");
        params.put("curr_user", userId);
        params.put("from_dt", new SimpleDateFormat("dd-MM-yyyy").format(bookDetailsDTO.frmDt));
        params.put("to_dt", new SimpleDateFormat("dd-MM-yyyy").format(bookDetailsDTO.toDt));
        long branchAcc = 0;
        // List< Long > ids = Arrays.asList( 3L, 4L );
        String typId = "0001";
        if (bookDetailsDTO.type == 1) {
            // ids = Arrays.asList( 3L, 4L, 5L );
            typId = "0008";
            branchAcc = bookDetailsDTO.branch;
        }

        /*
         * Query q = em.createNativeQuery(
         * "select sum(nvl(RECIEPT,0)) - SUM(NVL(PAYMENT,0)) OPEN  from  ( \r\n" +
         * "SELECT NULL RECIEPT, SUM(NVL(MDVD.AMT,0)) PAYMENT \r\n" +
         * "  FROM MW_DSBMT_VCHR_HDR MDVH, MW_DSBMT_VCHR_DTL MDVD, MW_LOAN_APP MLA, MW_CLNT MC, \r\n"
         * +
         * "  MW_PORT MP, MW_TYPS MT   WHERE     MDVH.DSBMT_HDR_SEQ = MDVD.DSBMT_HDR_SEQ \r\n"
         * +
         * "       AND MDVD.CRNT_REC_FLG = 1         AND MDVH.LOAN_APP_SEQ = MLA.LOAN_APP_SEQ \r\n"
         * +
         * "       AND MLA.CRNT_REC_FLG = 1         AND MLA.CLNT_SEQ = MC.CLNT_SEQ \r\n"
         * +
         * "       AND MC.PORT_KEY = MP.PORT_SEQ         AND MP.CRNT_REC_FLG = 1       \r\n"
         * +
         * "       AND MDVD.PYMT_TYPS_SEQ IN :IDS   AND MDVD.PYMT_TYPS_SEQ = MT.TYP_SEQ \r\n"
         * + "       AND MT.CRNT_REC_FLG = 1   \r\n" +
         * "       AND TO_DATE (MDVH.CRTD_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (MDVH.CRTD_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')  \r\n"
         * +
         * "      GROUP BY MDVH.CRTD_DT, MC.CLNT_SEQ,  MC.FRST_NM||' '||MC.LAST_NM, MDVD.INSTR_NUM  UNION ALL \r\n"
         * +
         * "SELECT SUM(NVL(MRT.PYMT_AMT,0)) RECIEPT, NULL PAYMENT  FROM MW_RCVRY_TRX MRT, MW_CLNT MC, \r\n"
         * +
         * "MW_PORT MP, MW_TYPS MT  WHERE MRT.CRNT_REC_FLG = 1  AND MRT.PYMT_REF = MC.CLNT_SEQ \r\n"
         * +
         * "AND MC.PORT_KEY = MP.PORT_SEQ  AND MP.CRNT_REC_FLG = 1  AND MRT.RCVRY_TYP_SEQ = MT.TYP_SEQ \r\n"
         * +
         * "AND MT.TYP_SEQ IN :IDS  AND MT.CRNT_REC_FLG = 1  AND MRT.POST_FLG = 1 \r\n"
         * +
         * "and TO_DATE (MRT.PYMT_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (MRT.PYMT_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')\r\n"
         * +
         * "GROUP BY MRT.CRTD_DT, MC.CLNT_SEQ,  MC.FRST_NM||' '||MC.LAST_NM, MRT.INSTR_NUM  UNION ALL \r\n"
         * +
         * "SELECT   (CASE WHEN ME.PYMT_RCT_FLG IS NULL THEN (NVL(ME.EXPNS_AMT,0)) END) RECIEPT, \r\n"
         * +
         * "(CASE WHEN ME.PYMT_RCT_FLG IS NOT NULL THEN (NVL(ME.EXPNS_AMT,0)) END) PAYMENT \r\n"
         * + "FROM MW_EXP ME, MW_TYPS MT1, MW_TYPS MT2 \r\n" +
         * "WHERE ME.CRNT_REC_FLG = 1 and mt1.crnt_rec_flg = 1 and mt2.crnt_rec_flg = 1 and me.POST_FLG=1 \r\n"
         * +
         * "AND ME.EXPNS_TYP_SEQ = MT1.TYP_SEQ  AND ME.PYMT_TYP_SEQ = MT2.TYP_SEQ   AND MT2.TYP_SEQ IN :IDS\r\n"
         * +
         * "AND TO_DATE (ME.CRTD_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (ME.CRTD_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')  )"
         * ) .setParameter( "IDS", ids ).setParameter( "FROM_DATE", formateTimeZoneDate(
         * bookDetailsDTO.frmDt.toString() ) ) .setParameter( "OPEN_DATE", opnDt );
         *
         * if ( bookDetailsDTO.branch != null ) { q = em.createNativeQuery(
         * "select sum(nvl(RECIEPT,0)) - SUM(NVL(PAYMENT,0)) OPEN  from  ( \r\n" +
         * "SELECT NULL RECIEPT, SUM(NVL(MDVD.AMT,0)) PAYMENT \r\n" +
         * "  FROM MW_DSBMT_VCHR_HDR MDVH, MW_DSBMT_VCHR_DTL MDVD, MW_LOAN_APP MLA, MW_CLNT MC, \r\n"
         * +
         * "  MW_PORT MP, MW_TYPS MT   WHERE     MDVH.DSBMT_HDR_SEQ = MDVD.DSBMT_HDR_SEQ \r\n"
         * +
         * "       AND MDVD.CRNT_REC_FLG = 1         AND MDVH.LOAN_APP_SEQ = MLA.LOAN_APP_SEQ \r\n"
         * +
         * "       AND MLA.CRNT_REC_FLG = 1         AND MLA.CLNT_SEQ = MC.CLNT_SEQ \r\n"
         * +
         * "       AND MC.PORT_KEY = MP.PORT_SEQ         AND MP.CRNT_REC_FLG = 1       \r\n"
         * +
         * "       AND MDVD.PYMT_TYPS_SEQ IN :IDS   AND MDVD.PYMT_TYPS_SEQ = MT.TYP_SEQ \r\n"
         * + "       AND MT.CRNT_REC_FLG = 1         AND Mp.BRNCH_SEQ =:BRNCH_SEQ  \r\n"
         * +
         * "       AND TO_DATE (MDVH.CRTD_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (MDVH.CRTD_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')  \r\n"
         * +
         * "      GROUP BY MDVH.CRTD_DT, MC.CLNT_SEQ,  MC.FRST_NM||' '||MC.LAST_NM, MDVD.INSTR_NUM  UNION ALL \r\n"
         * +
         * "SELECT SUM(NVL(MRT.PYMT_AMT,0)) RECIEPT, NULL PAYMENT  FROM MW_RCVRY_TRX MRT, MW_CLNT MC, \r\n"
         * +
         * "MW_PORT MP, MW_TYPS MT  WHERE MRT.CRNT_REC_FLG = 1  AND MRT.PYMT_REF = MC.CLNT_SEQ \r\n"
         * +
         * "AND MC.PORT_KEY = MP.PORT_SEQ  AND MP.CRNT_REC_FLG = 1  AND MRT.RCVRY_TYP_SEQ = MT.TYP_SEQ \r\n"
         * +
         * "AND MT.TYP_SEQ IN :IDS  AND MT.CRNT_REC_FLG = 1  AND MRT.POST_FLG = 1 \r\n"
         * +
         * "AND MP.BRNCH_SEQ =:BRNCH_SEQ and TO_DATE (MRT.PYMT_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (MRT.PYMT_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')\r\n"
         * +
         * "GROUP BY MRT.CRTD_DT, MC.CLNT_SEQ,  MC.FRST_NM||' '||MC.LAST_NM, MRT.INSTR_NUM  UNION ALL \r\n"
         * +
         * "SELECT   (CASE WHEN ME.PYMT_RCT_FLG IS NULL THEN (NVL(ME.EXPNS_AMT,0)) END) RECIEPT, \r\n"
         * +
         * "(CASE WHEN ME.PYMT_RCT_FLG IS NOT NULL THEN (NVL(ME.EXPNS_AMT,0)) END) PAYMENT \r\n"
         * + "FROM MW_EXP ME, MW_TYPS MT1, MW_TYPS MT2 \r\n" +
         * "WHERE ME.CRNT_REC_FLG = 1 and mt1.crnt_rec_flg = 1 and mt2.crnt_rec_flg = 1 and me.POST_FLG=1 \r\n"
         * +
         * "AND ME.EXPNS_TYP_SEQ = MT1.TYP_SEQ  AND ME.PYMT_TYP_SEQ = MT2.TYP_SEQ   AND MT2.TYP_SEQ IN :IDS\r\n"
         * +
         * "AND ME.BRNCH_SEQ =:BRNCH_SEQ   AND TO_DATE (ME.CRTD_DT) >  TO_DATE(:OPEN_DATE,'MM/dd/yyyy') and TO_DATE (ME.CRTD_DT)< TO_DATE(:FROM_DATE,'MM/dd/yyyy')  )"
         * ) .setParameter( "IDS", ids ).setParameter( "BRNCH_SEQ",
         * bookDetailsDTO.branch ).setParameter( "OPEN_DATE", opnDt ) .setParameter(
         * "FROM_DATE", formateTimeZoneDate( bookDetailsDTO.frmDt.toString() ) );
         *
         * }
         */

        // BigDecimal openBal = ( BigDecimal ) q.getSingleResult();

        // long balnce = getLongValue( openBal.toString() );
        params.put("open_bal", opnCsh);
        if (bookDetailsDTO.type == 1) {
            params.put("open_bal", opnBnk);
        }

        String detailQry;
        detailQry = readFile(Charset.defaultCharset(), "BookDetailsdetailQry.txt");
        Query rs1 = em.createNativeQuery(detailQry).setParameter("typId", typId)
                .setParameter("FROM_DATE", formateTimeZoneDate(bookDetailsDTO.frmDt.toString()))
                .setParameter("TO_DATE", formateTimeZoneDate(bookDetailsDTO.toDt.toString()));

        /*
         * Query detailQry = em.createNativeQuery(
         * "select prd.prd_cmnt  trans_type,mdvh.crtd_dt crtd_dt, mc.clnt_seq,upper(mc.frst_nm||' '||mc.last_nm) name, mdvd.instr_num,null reciept, sum(nvl(mdvd.amt,0)) payment, '' dscr \r\n"
         * + "from mw_dsbmt_vchr_hdr mdvh  \r\n" +
         * "join mw_dsbmt_vchr_dtl mdvd on mdvd.dsbmt_hdr_seq=mdvh.dsbmt_hdr_seq and mdvd.crnt_rec_flg=1   \r\n"
         * +
         * "join mw_loan_app mla on mla.loan_app_seq = mdvh.loan_app_seq and mla.crnt_rec_flg = 1  \r\n"
         * + "join mw_prd prd on prd.prd_seq=mla.prd_seq and prd.crnt_rec_flg=1 \r\n" +
         * "join mw_clnt mc on mc.clnt_seq = mla.clnt_seq and mc.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_port mp on mc.port_key = mp.port_seq and mp.crnt_rec_flg = 1  \r\n"
         * +
         * "join mw_typs mt on mdvd.pymt_typs_seq = mt.typ_seq and mt.typ_id=:typId and mt.typ_ctgry_key in :IDS and mt.crnt_rec_flg = 1 \r\n"
         * +
         * "where to_date (mdvh.crtd_dt) between to_date(:FROM_DATE,'MM/dd/yyyy') and to_date(:TO_DATE,'MM/dd/yyyy') \r\n"
         * +
         * "      group by prd.prd_cmnt,mdvh.crtd_dt, mc.clnt_seq, mc.frst_nm||' '||mc.last_nm, mdvd.instr_num     \r\n"
         * + "union all \r\n" +
         * "select ' RECOVERY'  trans_type, mrt.crtd_dt crtd_dt, mc.clnt_seq, \r\n" +
         * "upper(mc.frst_nm||' '||mc.last_nm) name, mrt.instr_num, sum(nvl(mrt.pymt_amt,0)) reciept, null payment,'' dscr \r\n"
         * +
         * "from mw_rcvry_trx mrt, mw_clnt mc, mw_port mp, mw_typs mt where mrt.crnt_rec_flg = 1 \r\n"
         * +
         * "and mrt.pymt_ref = mc.clnt_seq and mc.port_key = mp.port_seq and mp.crnt_rec_flg = 1 \r\n"
         * +
         * "and mrt.rcvry_typ_seq = mt.typ_seq and mt.typ_id=:typId and mt.typ_ctgry_key in :IDS and mt.crnt_rec_flg = 1 \r\n"
         * + "and mrt.post_flg = 1  \r\n" +
         * "and to_date (mrt.crtd_dt) between to_date(:FROM_DATE,'MM/dd/yyyy') and to_date(:TO_DATE,'MM/dd/yyyy') \r\n"
         * +
         * "group by mrt.crtd_dt, mc.clnt_seq, mc.frst_nm||' '||mc.last_nm, mrt.instr_num  \r\n"
         * + "union all \r\n" +
         * "select upper(' '||mt1.typ_str) trans_type, me.crtd_dt, null clnt_seq, null name, me.instr_num, \r\n"
         * +
         * "case when nvl(me.pymt_rct_flg,1)=2 then expns_amt else 0 end reciept, \r\n"
         * +
         * "case when nvl(me.pymt_rct_flg,1)=1 then expns_amt else 0 end payment,me.expns_dscr dscr \r\n"
         * + "from mw_exp me, mw_typs mt1, \r\n" +
         * "mw_typs mt2 where me.crnt_rec_flg = 1 and mt1.crnt_rec_flg = 1 and mt2.crnt_rec_flg = 1 and me.POST_FLG=1 \r\n"
         * +
         * "and me.expns_typ_seq = mt1.typ_seq and me.pymt_typ_seq = mt2.typ_seq  and mt2.typ_id=:typId and mt2.TYP_CTGRY_KEY in :IDS  \r\n"
         * +
         * "and to_date (me.crtd_dt) between to_date(:FROM_DATE,'MM/dd/yyyy') and to_date(:TO_DATE,'MM/dd/yyyy') \r\n"
         * + "order by crtd_dt" ) .setParameter( "typId", typId ).setParameter(
         * "FROM_DATE", formateTimeZoneDate( bookDetailsDTO.frmDt.toString() ) )
         * .setParameter( "TO_DATE", formateTimeZoneDate( bookDetailsDTO.toDt.toString()
         * ) );
         */
        if (bookDetailsDTO.branch != null) {
            /*
             * detailQry = em.createNativeQuery(
             * "select get_narration_bb_cb(MJH.ENTY_SEQ , MJH.ENTY_TYP, MJH.BRNCH_SEQ) dscr,TO_DATE (MJH.JV_DT) crtd_dt,  \r\n"
             * +
             * "                                        get_clnt_seq (MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ)  clnt_seq,   \r\n"
             * +
             * "                                        get_clnt_name (MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ)  name,MJH.ENTY_SEQ,   \r\n"
             * +
             * "                                        get_instr_num(MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ) instr_num,  \r\n"
             * +
             * "                               nvl(sum(NVL((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN NVL(MJD.AMT,0) END),0)),0) reciept,  \r\n"
             * +
             * "                               nvl(sum(NVL((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN NVL(MJD.AMT,0) END),0)),0) payment,  \r\n"
             * +
             * "                               get_narration(MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ) NAR_NO  from mw_jv_hdr mjh, mw_jv_dtl mjd  \r\n"
             * +
             * "                               where mjh.JV_HDR_SEQ = mjd.JV_HDR_SEQ  and BRNCH_SEQ =:BRNCH_SEQ  \r\n"
             * +
             * "                               AND MJD.GL_ACCT_NUM = (select t.GL_ACCT_NUM from mw_typs t where t.CRNT_REC_FLG =  1 and t.TYP_ID =:typId and t.TYP_CTGRY_KEY=3 and t.BRNCH_SEQ=:branchAcc )   \r\n"
             * +
             * "                               and to_date(mjh.JV_DT) BETWEEN to_date(:frmdt,'MM/dd/yyyy') AND to_date(:todt,'MM/dd/yyyy')\r\n"
             * +
             * "                               group by get_narration_bb_cb(MJH.ENTY_SEQ , MJH.ENTY_TYP, MJH.BRNCH_SEQ) ,TO_DATE (MJH.JV_DT) ,  \r\n"
             * +
             * "                                        get_clnt_seq (MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ)  ,   \r\n"
             * +
             * "                                        get_clnt_name (MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ)  ,MJH.ENTY_SEQ,  \r\n"
             * +
             * "                                        get_instr_num(MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ),\r\n"
             * +
             * "                                        get_narration(MJH.ENTY_SEQ, MJH.ENTY_TYP, MJH.BRNCH_SEQ)                                        \r\n"
             * + "                               ORDER BY 2 ,3" ) .setParameter( "typId",
             * typId ).setParameter( "branchAcc", branchAcc ) .setParameter( "BRNCH_SEQ",
             * bookDetailsDTO.branch ) .setParameter( "frmdt", formateTimeZoneDate(
             * bookDetailsDTO.frmDt.toString() ) ) .setParameter( "todt",
             * formateTimeZoneDate( bookDetailsDTO.toDt.toString() ) );
             */

            detailQry = readFile(Charset.defaultCharset(), "BookDetailsdetailQry_2.txt");
            rs1 = em.createNativeQuery(detailQry).setParameter("typId", typId).setParameter("typId", typId)
                    .setParameter("branchAcc", branchAcc).setParameter("BRNCH_SEQ", bookDetailsDTO.branch)
                    .setParameter("frmdt", formateTimeZoneDate(bookDetailsDTO.frmDt.toString()))
                    .setParameter("todt", formateTimeZoneDate(bookDetailsDTO.toDt.toString()));
        }

        List<Object[]> dtlObj = rs1.getResultList();

        List<Map<String, ?>> pymts = new ArrayList();
        dtlObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("CRTD_DT", getFormaedDate(l[0].toString()));
            map.put("SR_NO", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("CLNT_SEQ", l[2] == null ? "" : l[2].toString());
            map.put("REFERENCE_COL", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("LEDGER_HEAD", l[4] == null ? "" : l[4].toString());
            map.put("INSTR_NUM", l[5] == null ? "" : l[5].toString());
            map.put("NAR_NO", l[6] == null ? "" : l[6].toString());
            map.put("RECIEPT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("PAYMENT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());

            pymts.add(map);
        });

        return reportComponent.generateReport(BOOKDETAILS, params,

                getJRDataSource(pymts));
    }

    public byte[] getKSKLPD(long loanAppSeq, String currUser) throws IOException {

        String ql;
        ql = readFile(Charset.defaultCharset(), "KSKLPDql.txt");
        Query rs = em.createNativeQuery(ql).setParameter("loanAppSeq", loanAppSeq);
        /*
         * Query ql = em.createNativeQuery(
         * "select clnt.clnt_id,clnt.frst_nm, clnt.last_nm,clnt.spz_frst_nm||clnt.fthr_frst_nm,clnt.spz_last_nm||clnt.fthr_last_nm, \r\n"
         * +
         * "port.port_cd,port.port_nm,brnch.brnch_cd,brnch.brnch_nm, area.area_nm, reg.reg_nm , prd.prd_nm, prd.prd_id,app.rqstd_loan_amt,app.aprvd_loan_amt,emp.emp_nm bdo \r\n"
         * + "from mw_loan_app app \r\n" +
         * "join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1\r\n"
         * +
         * "join mw_port_emp_rel poer on poer.port_seq=port.port_seq and poer.crnt_rec_flg=1\r\n"
         * + "join mw_emp emp on emp.emp_seq=poer.emp_seq \r\n" +
         * "join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_area area on area.area_seq=brnch.area_seq and area.crnt_rec_flg=1 \r\n"
         * + "join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1 \r\n" +
         * "join mw_prd prd on prd.prd_seq = app.prd_seq and prd.crnt_rec_flg=1 \r\n" +
         * "where app.crnt_rec_flg=1 and app.loan_app_seq =:loanAppSeq" ) .setParameter(
         * "loanAppSeq", loanAppSeq );
         */
        Object[] obj = (Object[]) rs.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("clnt_id", obj[0] == null ? "" : obj[0].toString());
        params.put("clnt_nm",
                ((obj[1] == null) ? "" : obj[1].toString()) + SPACE + ((obj[2] == null) ? "" : obj[2].toString()));
        params.put("spz_nm",
                (obj[3] == null ? "" : obj[3].toString()) + SPACE + (obj[4] == null ? "" : obj[4].toString()));
        params.put("req_amt", obj[13] == null ? 0 : getLongValue(obj[13].toString()));
        params.put("aprv_amt", obj[14] == null ? 0 : getLongValue(obj[14].toString()));
        params.put("area", obj[9] == null ? "" : obj[9].toString());
        params.put("branch", obj[8] == null ? "" : obj[8].toString());
        params.put("bdo", obj[15] == null ? "" : obj[15].toString());
        params.put("curr_user", currUser);
        params.put("title", obj[1].toString().equals("0010") ? "MPD" : "LPD");
        return reportComponent.generateReport(KSK_LPD, params, null);
    }

    public byte[] getWomenParticipation(String date, long branch, String currUser) throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());

        String ql;
        ql = readFile(Charset.defaultCharset(), "WomenParticipationql.txt");
        Query rs = em.createNativeQuery(ql);

        /*
         * Query ql = em.createNativeQuery( "select bo.ref_cd_dscr,\r\n" +
         * "bo.ref_cd,\r\n" +
         * "count(distinct case when loan_app_sts_dt between to_date('01-'||to_char(to_date('09-apr-19'),'MON-YY')) and to_date('9-apr-19') then ap.clnt_seq else null end) crnt_clnt_cnt,\r\n"
         * +
         * "count(distinct case when (((bo.ref_cd='0191' and gndr.ref_cd='0019') or (bo.ref_cd in ('0190','1055','1057','1056','1058','1059'))) \r\n"
         * +
         * "and (loan_app_sts_dt between to_date('01-apr-19') and to_date('9-apr-19'))) then ap.clnt_seq else null end) crnt_wmn_cnt,\r\n"
         * + "--prev \r\n" +
         * "count(distinct case when to_char(loan_app_sts_dt,'YYYYMM') = to_char(add_months(to_date('09-apr-19'),-1),'YYYYMM') then ap.clnt_seq else null end) prv_clnt_cnt,\r\n"
         * +
         * "count(distinct case when (((bo.ref_cd='0191' and gndr.ref_cd='0019') or (bo.ref_cd in ('0190','1055','1057','1056','1058','1059'))) \r\n"
         * +
         * "and (to_char(loan_app_sts_dt,'YYYY') = to_char(loan_app_sts_dt,'YYYYMM') )) then ap.clnt_seq else null end) prv_wmn_cnt,\r\n"
         * + "-- port\r\n" + "count(distinct ap.clnt_seq) prt_clnt_cnt,\r\n" +
         * "count(distinct case when (((bo.ref_cd='0191' and gndr.ref_cd='0019') or (bo.ref_cd in ('0190','1055','1057','1056','1058','1059'))) ) then ap.clnt_seq else null end) prt_wmn_cnt\r\n"
         * +
         * "--count(distinct case when (bo.ref_cd='0193' ) then ap.clnt_seq else null end) prt_jnt_cnt\r\n"
         * + "from mw_loan_app ap\r\n" +
         * "join mw_clnt clnt on clnt.clnt_seq=ap.clnt_seq and clnt.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val gndr on gndr.ref_cd_seq=clnt.gndr_key and gndr.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_biz_aprsl biz on biz.loan_app_seq=ap.loan_app_seq and biz.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val bo on bo.ref_cd_seq=biz.prsn_run_the_biz and bo.crnt_rec_flg=1 \r\n"
         * + "where ap.crnt_rec_flg=1\r\n" +
         * "and (lsts.ref_cd='0005' or (lsts.ref_cd='0006' and ap.loan_app_sts_dt >= '9-apr-19'))\r\n"
         * + "and port_seq in (9)\r\n" + "group by bo.ref_cd,bo.ref_cd_dscr" );
         */
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REF_CD_DSCR", w[0].toString());
            parm.put("REF_CD", w[1].toString());
            parm.put("CRNT_CLNT_CNT", getLongValue(w[2].toString()));
            parm.put("CRNT_WMN_CNT", getLongValue(w[3].toString()));
            parm.put("PRV_CLNT_CNT", getLongValue(w[4].toString()));
            parm.put("PRV_WMN_CNT", getLongValue(w[5].toString()));
            parm.put("PRT_CLNT_CNT", getLongValue(w[6].toString()));
            parm.put("PRT_WMN_CNT", getLongValue(w[7].toString()));
            expList.add(parm);
        });

        params.put("curr_user", currUser);
        return reportComponent.generateReport(WOMENPARTICIPATION, params, getJRDataSource(expList));
    }

    private JRDataSource getJRDataSource(List<?> list) {
        return new JRBeanCollectionDataSource(list);
    }

    private String getFormaedDate(String input) {
        String date = "";
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").format(inputFormat.parse(input));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private String getFormaedDateShortYear(String input) {
        String date = "";
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            date = new SimpleDateFormat("dd-MM-yy").format(inputFormat.parse(input));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private String formateTimeZoneDate(String input) {
        String date = "";
        DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            date = new SimpleDateFormat("MM/dd/yyyy").format(inputFormat.parse(input));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private LocalDate getLocalDate(String date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDate.parse(date, formatter);
    }

    public Long calculateAge(LocalDate birthDate, LocalDate currentDate) {
        // validate inputs ...
        return (long) Period.between(birthDate, currentDate).getYears();
    }
    ////////// Health Beneficiary report

    private Long getLongValue(String value) {
        return new BigDecimal(value).longValue();
    }

    /// Insurance Claim Report

    public byte[] getClientHealthBeneficiaryReport(String fromDt, String toDt, long brnchSeq, String userId)
            throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String clientBeneficiary;
        clientBeneficiary = readFile(Charset.defaultCharset(), "CLT_HEALTH_BENEFICIARY.txt");
        Query rs = em.createNativeQuery(clientBeneficiary).setParameter("from_dt", fromDt).setParameter("to_dt", toDt)
                .setParameter("brnch_cd", obj[4].toString());

        /*
         * Query clientBeneficiary = em.createNativeQuery(
         * com.idev4.rds.util.Queries.CLT_HEALTH_BENEFICIARY ) .setParameter( "from_dt",
         * fromDt ).setParameter( "to_dt", toDt ).setParameter( "brnch_cd", obj[ 4
         * ].toString() );
         */
        List<Object[]> clientBeneficiaryList = rs.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        clientBeneficiaryList.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("CLNT_SEQ", l[0] == null ? "" : l[0].toString());
            map.put("CLIENT_NAME", l[1] == null ? "" : l[1].toString());
            map.put("HUSBAND_NAME", l[2] == null ? "" : l[2].toString());
            map.put("FATHER_NAME", l[3] == null ? "" : l[3].toString());
            map.put("CNIC_NUM", l[4] == null ? "" : l[4].toString());
            map.put("PLAN", l[5] == null ? "" : l[5].toString());
            map.put("ANL_PREM_AMT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("HLTH_INSR_MEMB_SEQ", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("MEMBER_NM", l[8] == null ? "" : l[8].toString());
            map.put("AGE_AT_INSURACE_TIME", l[9] == null ? "" : l[9].toString());
            map.put("MARITAL_STATUS", l[10] == null ? "" : l[10].toString());
            map.put("RELATION", l[11] == null ? "" : l[11].toString());
            map.put("GENDER", l[12] == null ? "" : l[12].toString());
            map.put("ENROLLMENT_DATE", l[13] == null ? "" : getFormaedDate(l[13].toString()));
            map.put("INSURANCE_DATE", l[14] == null ? "" : getFormaedDate(l[14].toString()));
            map.put("MATURITY_DATE", l[15] == null ? "" : getFormaedDate(l[15].toString()));
            map.put("CARD_NUM", l[16] == null ? "" : l[16].toString());
            map.put("DEATH_DATE", l[17] == null ? "" : getFormaedDate(l[17].toString()));
            reportParams.add(map);
        });
        params.put("dataset", getJRDataSource(reportParams));

        params.put("from_dt", fromDt);

        params.put("to_dt", toDt);
        return reportComponent.generateReport(CLIENT_HEALTH_BENEFICIARY, params, getJRDataSource(reportParams));
    }

    /// PAR Branch Wise Report

    public byte[] getInsuranceClaimReport(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String clientBeneficiary;
        clientBeneficiary = readFile(Charset.defaultCharset(), "INSURANCE_CLAIM.txt");
        Query rs = em.createNativeQuery(clientBeneficiary).setParameter("from_dt", fromDt).setParameter("to_dt", toDt)
                .setParameter("brnch_cd", brnchSeq);

        // Query clientBeneficiary = em.createNativeQuery(
        // com.idev4.rds.util.Queries.INSURANCE_CLAIM ).setParameter( "from_dt", fromDt
        // )
        // .setParameter( "to_dt", toDt ).setParameter( "brnch_cd", obj[ 4 ].toString()
        // );
        List<Object[]> clientBeneficiaryList = rs.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        clientBeneficiaryList.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PORT_NM", l[0] == null ? "" : l[0].toString());
            map.put("CLNT_ID", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_NM", l[2] == null ? "" : l[2].toString());
            map.put("NOM_NM", l[3] == null ? "" : l[3].toString());
            map.put("DT_OF_DTH", l[4] == null ? "" : getFormaedDate(l[4].toString()));
            map.put("DECEASED_PERSON", l[5] == null ? "" : l[5].toString());
            map.put("ADJ_DT", l[6] == null ? "" : getFormaedDate(l[6].toString()));
            map.put("PRD_CMNT", l[7] == null ? "" : l[7].toString());
            map.put("DSBMT_DT", l[8] == null ? "" : getFormaedDate(l[8].toString()));
            map.put("DSBMT_AMT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("ADJ_PRNCPL", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("ADJ_SRVC_CHRG", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("INS_CLM_DT", l[12] == null ? "" : getFormaedDate(l[12].toString()));
            map.put("FNRL_CHRG", l[13] == null ? "" : new BigDecimal(l[13].toString()).longValue());
            reportParams.add(map);
        });
        params.put("dataset", getJRDataSource(reportParams));

        try {
            params.put("from_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(fromDt)));
            params.put("to_dt",
                    new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(toDt)));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return reportComponent.generateReport(INSURANCE_CLAIM, params, getJRDataSource(reportParams));

    }

    /// Branch Performance Report

    public byte[] getParBranchWiseReport(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", fromDt);
        params.put("to_dt", toDt);

        String clientBeneficiary;
        clientBeneficiary = readFile(Charset.defaultCharset(), "PAR_BRNACH_WISE.txt");
        Query rs = em.createNativeQuery(clientBeneficiary).setParameter("fromDt", fromDt).setParameter("toDt", toDt)
                .setParameter("brnch", obj[4].toString());
        // Query clientBeneficiary = em.createNativeQuery(
        // com.idev4.rds.util.Queries.PAR_BRNACH_WISE ).setParameter( "fromDt", fromDt )
        // .setParameter( "toDt", toDt ).setParameter( "brnch", obj[ 4 ].toString() );
        List<Object[]> clientBeneficiaryList = rs.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        clientBeneficiaryList.forEach(l -> {

            Map<String, Object> map = new HashMap();
            map.put("PRD_NM", l[0] == null ? "" : l[0].toString());
            map.put("EMP_NM", l[1] == null ? "" : l[1].toString());
            map.put("PORT_SEQ", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("LOAN_APP_SEQ", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("LOAN_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("OST_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("OD_AMT_OP", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("ADDITION", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("RECOVERED", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("OD_AMT_CL", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("OD_LOANS_1", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("OST_AMT_1", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("OD_LOANS_30", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("OST_AMT_30", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            map.put("OD_LOANS_1_5", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            map.put("OST_AMT_1_5", l[15] == null ? 0 : new BigDecimal(l[15].toString()).longValue());
            map.put("OD_LOANS_6_10", l[16] == null ? 0 : new BigDecimal(l[16].toString()).longValue());
            map.put("OST_AMT_6_10", l[17] == null ? 0 : new BigDecimal(l[17].toString()).longValue());
            map.put("OD_LOANS_11_15", l[18] == null ? 0 : new BigDecimal(l[18].toString()).longValue());
            map.put("OST_AMT_11_15", l[19] == null ? 0 : new BigDecimal(l[19].toString()).longValue());
            map.put("OD_LOANS_16_30", l[20] == null ? 0 : new BigDecimal(l[20].toString()).longValue());
            map.put("OST_AMT_16_30", l[21] == null ? 0 : new BigDecimal(l[21].toString()).longValue());
            map.put("OD_LOANS_31_90", l[22] == null ? 0 : new BigDecimal(l[22].toString()).longValue());
            map.put("OST_AMT_31_90", l[23] == null ? 0 : new BigDecimal(l[23].toString()).longValue());
            map.put("OD_LOANS_90_180", l[24] == null ? 0 : new BigDecimal(l[24].toString()).longValue());
            map.put("OST_AMT_91_180", l[25] == null ? 0 : new BigDecimal(l[25].toString()).longValue());
            map.put("OD_LOANS_181_365", l[26] == null ? 0 : new BigDecimal(l[26].toString()).longValue());
            map.put("OST_AMT_181_365", l[27] == null ? 0 : new BigDecimal(l[27].toString()).longValue());
            map.put("OD_LOANS_365", l[28] == null ? 0 : new BigDecimal(l[28].toString()).longValue());
            map.put("OST_AMT_365", l[29] == null ? 0 : new BigDecimal(l[29].toString()).longValue());
            map.put("OD_DYS", l[30] == null ? 0 : new BigDecimal(l[30].toString()).longValue());
            reportParams.add(map);
        });
        params.put("dataset", getJRDataSource(reportParams));

        return reportComponent.generateReport(PAR_BRANCHWISE, params, getJRDataSource(reportParams));
    }

    public byte[] getBranchPerformanceReport(String fromDt, String toDt, long brnchSeq, String userId) {

        // Query bi = em.createNativeQuery(
        // com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH ).setParameter( "brnchSeq",
        // brnchSeq );
        // Object[] obj = ( Object[] ) bi.getSingleResult();
        //
        // Map< String, Object > params = new HashMap<>();
        // params.put( "reg_nm", obj[ 0 ].toString() );
        // params.put( "area_nm", obj[ 1 ].toString() );
        // params.put( "brnch_nm", obj[ 2 ].toString() );
        // params.put( "brnch_cd", obj[ 3 ].toString() );
        // params.put( "curr_user", userId );
        //
        // Query clientBeneficiary = em.createNativeQuery(
        // com.idev4.rds.util.Queries.INSURANCE_CLAIM ).setParameter( "from_dt", fromDt
        // )
        // .setParameter( "to_dt", toDt ).setParameter( "brnch_cd", obj[ 4 ].toString()
        // );
        // List< Object[] > clientBeneficiaryList = clientBeneficiary.getResultList();
        // List< Map< String, ? > > reportParams = new ArrayList();
        // clientBeneficiaryList.forEach( l -> {
        // Map< String, Object > map = new HashMap();
        // map.put( "CLNT_SEQ", l[ 0 ] == null ? "" : l[ 0 ].toString() );
        // map.put( "CLIENT_NAME", l[ 1 ] == null ? "" : l[ 1 ].toString() );
        // map.put( "HUSBAND_NAME", l[ 2 ] == null ? "" : l[ 2 ].toString() );
        // map.put( "FATHER_NAME", l[ 3 ] == null ? "" : l[ 3 ].toString() );
        // map.put( "CNIC_NUM", l[ 4 ] == null ? "" : l[ 4 ].toString() );
        // map.put( "PLAN", l[ 5 ] == null ? "" : l[ 5 ].toString() );
        // map.put( "ANL_PREM_AMT", l[ 6 ] == null ? 0 : new BigDecimal( l[ 6
        // ].toString() ).longValue() );
        // map.put( "HLTH_INSR_MEMB_SEQ", l[ 7 ] == null ? 0 : new BigDecimal( l[ 7
        // ].toString() ).longValue() );
        // map.put( "MEMBER_NM", l[ 8 ] == null ? "" : l[ 8 ].toString() );
        // map.put( "AGE_AT_INSURACE_TIME", l[ 9 ] == null ? "" : l[ 9 ].toString() );
        // map.put( "MARITAL_STATUS", l[ 10 ] == null ? "" : l[ 10 ].toString() );
        // map.put( "RELATION", l[ 11 ] == null ? "" : l[ 11 ].toString() );
        // map.put( "GENDER", l[ 12 ] == null ? "" : l[ 12 ].toString() );
        // map.put( "ENROLLMENT_DATE", l[ 13 ] == null ? "" : getFormaedDate( l[ 13
        // ].toString() ) );
        // map.put( "INSURANCE_DATE", l[ 14 ] == null ? "" : getFormaedDate( l[ 14
        // ].toString() ) );
        // map.put( "MATURITY_DATE", l[ 15 ] == null ? "" : getFormaedDate( l[ 15
        // ].toString() ) );
        // map.put( "CARD_NUM", l[ 16 ] == null ? "" : l[ 16 ].toString() );
        // map.put( "DEATH_DATE", l[ 17 ] == null ? "" : getFormaedDate( l[ 17
        // ].toString() ) );
        // reportParams.add( map );
        // } );
        // params.put( "dataset", getJRDataSource( reportParams ) );
        //
        // try {
        // params.put( "from_dt", new SimpleDateFormat( "MM-yyyy" ).format( new
        // SimpleDateFormat( "dd-MMM-yyyy" ).parse( fromDt ) ) );
        //
        // params.put( "to_dt", new SimpleDateFormat( "MM-yyyy" ).format( new
        // SimpleDateFormat( "dd-MMM-yyyy" ).parse( toDt ) ) );
        // } catch ( ParseException e ) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // return reportComponent.generateReport( INSURANCE_CLAIM, params,
        // getJRDataSource( reportParams ) );
        return null;
    }

    public byte[] getBranchTurnoverAnalysisAndPlaningReport(String date, long branch, String currUser)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("date", date);

        Date frstDt = getFirstDateOfMonth(date);
        Date lastDt = getLastDateOfMonth(date);
        String frstDt1 = new SimpleDateFormat("dd-MMM-yyyy").format(frstDt);
        String lastDT1 = new SimpleDateFormat("dd-MMM-yyyy").format(lastDt);

        String turnObverQury;
        turnObverQury = readFile(Charset.defaultCharset(), "turnObverQury.txt");
        Query rs = em.createNativeQuery(turnObverQury).setParameter("asDt", date).setParameter("branch", branch);

        /*
         * Query turnObverQury = em.createNativeQuery(
         * "SELECT bdo_name,         prd,\r\n" +
         * "         SUM (CASE WHEN seq1 > 0 THEN seq1 ELSE 0 END)     SameDay,\r\n" +
         * "         SUM (CASE WHEN seq2 > 0 THEN seq2 ELSE 0 END)     days_1_4,\r\n" +
         * "         SUM (CASE WHEN seq3 > 0 THEN seq3 ELSE 0 END)     days_5_15,\r\n" +
         * "         SUM (CASE WHEN seq4 > 0 THEN seq4 ELSE 0 END)     days_16_30,\r\n"
         * + "         SUM (CASE WHEN seq5 > 0 THEN seq5 ELSE 0 END)     abv_30    \r\n"
         * + "     FROM \r\n" + "        (\r\n" +
         * "        SELECT get_port_bdo (la.port_seq)bdo_name,mpg.PRD_GRP_NM prd,\r\n" +
         * "              count(CASE WHEN  TO_DATE (dsbh.DSBMT_DT) - NVL (lst_loan_cmpltn_dt (la.loan_app_seq), TO_DATE ( :asDt, 'dd-MM-yyyy') - 365) = 0 then 1 end) seq1,\r\n"
         * +
         * "              count(CASE WHEN  TO_DATE (dsbh.DSBMT_DT) - NVL (lst_loan_cmpltn_dt (la.loan_app_seq), TO_DATE ( :asDt, 'dd-MM-yyyy') - 365) between 1 and 4 then 2 end) seq2,\r\n"
         * +
         * "              count(CASE WHEN  TO_DATE (dsbh.DSBMT_DT) - NVL (lst_loan_cmpltn_dt (la.loan_app_seq), TO_DATE ( :asDt, 'dd-MM-yyyy') - 365) between 5 and 15 then 3 end) seq3,\r\n"
         * +
         * "              count(CASE WHEN  TO_DATE (dsbh.DSBMT_DT) - NVL (lst_loan_cmpltn_dt (la.loan_app_seq), TO_DATE ( :asDt, 'dd-MM-yyyy') - 365) between 16 and 30 then 4 end) seq4,\r\n"
         * +
         * "              count(CASE WHEN  TO_DATE (dsbh.DSBMT_DT) - NVL (lst_loan_cmpltn_dt (la.loan_app_seq), TO_DATE ( :asDt, 'dd-MM-yyyy') - 365) > 30 then 5 end) seq5                        \r\n"
         * +
         * "              FROM MW_DSBMT_VCHR_HDR dsbh,MW_DSBMT_VCHR_DTL dsbd,mw_loan_app la,MW_PORT MP,mw_prd mprd,mw_prd_grp mpg\r\n"
         * +
         * "             WHERE dsbh.DSBMT_HDR_SEQ = dsbd.DSBMT_HDR_SEQ                   \r\n"
         * + "                   AND dsbh.CRNT_REC_FLG = 1\r\n" +
         * "                   AND dsbd.CRNT_REC_FLG = 1                   \r\n" +
         * "                   AND la.LOAN_APP_SEQ = dsbh.LOAN_APP_SEQ\r\n" +
         * "                   AND LA.PORT_SEQ = MP.PORT_SEQ                   \r\n" +
         * "                   AND MP.CRNT_REC_FLG = 1\r\n" +
         * "                   AND MP.BRNCH_SEQ = :branch                   \r\n" +
         * "                   AND la.CRNT_REC_FLG = 1\r\n" +
         * "                   AND la.LOAN_CYCL_NUM > 1                   \r\n" +
         * "                   AND la.PRD_SEQ = mprd.PRD_SEQ\r\n" +
         * "                   AND mprd.CRNT_REC_FLG = 1                   \r\n" +
         * "                   AND mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n" +
         * "                   AND mpg.CRNT_REC_FLG = 1        \r\n" +
         * "                   AND la.prd_seq not in (2,3,5,13,14,29)\r\n" +
         * "                   AND TO_DATE (dsbh.DSBMT_DT) BETWEEN TRUNC (TO_DATE ( TO_DATE (:asDt,'dd-MM-yyyy')),'month') AND TO_DATE ( TO_DATE ( :asDt,'dd-MM-yyyy'))\r\n"
         * + "            GROUP BY mpg.PRD_GRP_NM,get_port_bdo (la.port_seq)\r\n" +
         * "        )\r\n" + "                    GROUP BY bdo_name, prd\r\n" +
         * "                    ORDER BY prd,bdo_name" ) .setParameter( "asDt", date
         * ).setParameter( "branch", branch );
         */
        List<Object[]> turnObverObject = rs.getResultList();

        List<Map<String, ?>> turnObverList = new ArrayList();
        turnObverObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0].toString());
            parm.put("PRD_NM", w[1].toString());
            parm.put("SAMEDAY", getLongValue(w[2].toString()));
            parm.put("DAYS_1_4", getLongValue(w[3].toString()));
            parm.put("DAYS_5_15", getLongValue(w[4].toString()));
            parm.put("DAYS_16_30", getLongValue(w[5].toString()));
            parm.put("ABV30", getLongValue(w[6].toString()));
            turnObverList.add(parm);
        });

        String pndgSummryQury;
        pndgSummryQury = readFile(Charset.defaultCharset(), "pndgSummryQury.txt");
        Query rs1 = em.createNativeQuery(pndgSummryQury).setParameter("asDt", date).setParameter("branch", branch);

        /*
         * Query pndgSummryQury = em.createNativeQuery(
         * "select       bdo_name,prd,  \r\n" +
         * "    sum(case when dys between 1 and 7 then 1 else 0 end) days_1_7,  \r\n" +
         * "    sum(case when dys between 8 and 15 then 1 else 0 end) days_8_15,  \r\n"
         * +
         * "    sum(case when dys between 16 and 30 then 1 else 0 end) days_16_30,  \r\n"
         * +
         * "    sum(case when dys between 31 and 60 then 1 else 0 end) days_31_60        from (       \r\n"
         * +
         * "    select get_port_bdo(la.port_seq) bdo_name,                       mpg.prd_grp_nm prd, \r\n"
         * +
         * "           to_date(:asDt,'dd-MM-yyyy') - to_date(Loan_completion_date(la.CLNT_SEQ,la.LOAN_APP_SEQ,to_date(:asDt,'dd-MM-yyyy'))) dys        \r\n"
         * +
         * "      from mw_loan_app la         join MW_PORT MP on mp.port_seq=la.port_seq and mp.crnt_rec_flg=1  \r\n"
         * +
         * "      join mw_prd prd on prd.prd_seq=la.prd_seq and prd.crnt_rec_flg=1  \r\n"
         * +
         * "      join mw_prd_grp mpg on mpg.prd_grp_seq=prd.prd_grp_seq and mpg.crnt_rec_flg=1  \r\n"
         * +
         * "     WHERE MP.CRNT_REC_FLG = 1               AND BRNCH_SEQ = :branch          and not exists ( \r\n"
         * +
         * "                       SELECT *                            FROM MW_DSBMT_VCHR_HDR dvh, mw_loan_app ap \r\n"
         * + "                         WHERE ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ \r\n" +
         * "                               AND ap.CLNT_SEQ = la.CLNT_SEQ \r\n" +
         * "                               AND ap.CRNT_REC_FLG = 1 \r\n" +
         * "                               AND dvh.crnt_rec_flg = 1 \r\n" +
         * "                               AND TO_DATE (dvh.DSBMT_DT) > TO_DATE ( :asDt,'dd-MM-yyyy') - 60 \r\n"
         * +
         * "                       )          and mpg.CRNT_REC_FLG = 1                and la.LOAN_APP_STS = 704  \r\n"
         * +
         * "       AND TO_DATE(Loan_completion_date(la.CLNT_SEQ,la.LOAN_APP_SEQ,to_date(:asDt,'dd-MM-yyyy'))) BETWEEN TO_DATE(:asDt,'dd-MM-yyyy')-60 AND TO_DATE (to_date(:asDt,'dd-MM-yyyy'))  \r\n"
         * + "    )        group by  bdo_name,prd        order by prd,bdo_name"
         * ).setParameter( "asDt", date ) .setParameter( "branch", branch );
         */
        List<Object[]> pndgSummryObject = rs1.getResultList();

        List<Map<String, ?>> pndgSummryList = new ArrayList();
        pndgSummryObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0].toString());
            parm.put("PRD_CMNT", w[1].toString());
            parm.put("DAYS1_7", getLongValue(w[2].toString()));
            parm.put("DAYS8_15", getLongValue(w[3].toString()));
            parm.put("DAYS16_30", getLongValue(w[4].toString()));
            parm.put("DAYS31_60", getLongValue(w[5].toString()));
            pndgSummryList.add(parm);
        });

        String clntLoanMatuProjQury;
        clntLoanMatuProjQury = readFile(Charset.defaultCharset(), "clntLoanMatuProjQury.txt");
        Query rs2 = em.createNativeQuery(clntLoanMatuProjQury).setParameter("asDt", date).setParameter("branch",
                branch);

        /*
         * Query clntLoanMatuProjQury = em .createNativeQuery(
         * "select  bdo_name,PRD_GRP_NM,\r\n" +
         * "    sum(case when seq=1 then loans else 0 end) day_1_7, \r\n" +
         * "    sum(case when seq=2 then loans else 0 end) day_8_15, \r\n" +
         * "    sum(case when seq=3 then loans else 0 end) day_16_30, \r\n" +
         * "    sum(case when seq=4 then loans else 0 end) day_31_60\r\n" + "from (\r\n"
         * + "select PRD_GRP_NM,\r\n" + "      bdo_name,\r\n" +
         * "      count(loans) loans,\r\n" +
         * "      (case when mat_dys <= 7 then 'between 1-7'\r\n" +
         * "               when mat_dys between 8 and 15 then 'between 8-15'\r\n" +
         * "               when mat_dys between 16 and 30 then 'between 16-30'\r\n" +
         * "               when mat_dys > 30 then 'between 31-60'\r\n" +
         * "         end) maturity,\r\n" + "         (case when mat_dys <= 7 then 1\r\n"
         * + "               when mat_dys between 8 and 15 then 2\r\n" +
         * "               when mat_dys between 16 and 30 then 3\r\n" +
         * "               when mat_dys > 30 then 4\r\n" +
         * "         end) seq                \r\n" + "      from\r\n" + "         (\r\n"
         * + "        select mpg.PRD_GRP_NM,\r\n" +
         * "         get_port_bdo(la.port_seq) bdo_name,\r\n" +
         * "         la.LOAN_APP_SEQ    loans,\r\n" +
         * "         max(psd.DUE_DT) - to_date(:toDt,'dd-MM-yyyy') mat_dys         \r\n"
         * + "         from mw_loan_app      la,\r\n" +
         * "         MW_PORT          MP,\r\n" + "         mw_prd  mprd,\r\n" +
         * "         mw_prd_grp mpg,\r\n" + "         MW_PYMT_SCHED_HDR psh,\r\n" +
         * "         MW_PYMT_SCHED_DTL psd     \r\n" +
         * "   WHERE     LA.PORT_SEQ = MP.PORT_SEQ\r\n" +
         * "         AND MP.CRNT_REC_FLG = 1\r\n" +
         * "         AND MP.BRNCH_SEQ = :branch\r\n" +
         * "         AND la.CRNT_REC_FLG = 1         \r\n" +
         * "         and la.PRD_SEQ = mprd.PRD_SEQ\r\n" +
         * "         and mprd.CRNT_REC_FLG = 1\r\n" +
         * "         and mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n" +
         * "         and mpg.CRNT_REC_FLG = 1 \r\n" +
         * "        and la.LOAN_APP_STS = 703     \r\n" +
         * "        and la.LOAN_APP_SEQ = psh.LOAN_APP_SEQ \r\n" +
         * "        and psh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ\r\n" +
         * "        and psh.CRNT_REC_FLG = 1\r\n" +
         * "        and psd.CRNT_REC_FLG = 1  \r\n" +
         * "         group by mpg.PRD_GRP_NM,\r\n" +
         * "         get_port_bdo(la.port_seq) ,\r\n" + "         la.LOAN_APP_SEQ  \r\n"
         * +
         * " having TO_DATE (max(psd.DUE_DT)) BETWEEN TO_DATE(:toDt,'dd-MM-yyyy') and TO_DATE (:toDt,'dd-MM-yyyy') + 60 \r\n"
         * + " )\r\n" +
         * " group by PRD_GRP_NM, bdo_name, (case when mat_dys <= 7 then 'between 1-7'\r\n"
         * + "               when mat_dys between 8 and 15 then 'between 8-15'\r\n" +
         * "               when mat_dys between 16 and 30 then 'between 16-30'\r\n" +
         * "               when mat_dys > 30 then 'between 31-60'\r\n" +
         * "         end),\r\n" + "         (case when mat_dys <= 7 then 1\r\n" +
         * "               when mat_dys between 8 and 15 then 2\r\n" +
         * "               when mat_dys between 16 and 30 then 3\r\n" +
         * "               when mat_dys > 30 then 4\r\n" + "         end)\r\n" + ")\r\n"
         * + "group by PRD_GRP_NM,bdo_name\r\n" + "order by 2,1" ) .setParameter(
         * "toDt", date ).setParameter( "branch", branch );
         */
        List<Object[]> clntLoanMatuProjObject = rs2.getResultList();

        List<Map<String, ?>> clntLoanMatuProjList = new ArrayList();
        clntLoanMatuProjObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0].toString());
            parm.put("PRD_CMNT", w[1].toString());
            parm.put("DAYS1_7", getLongValue(w[2].toString()));
            parm.put("DAYS8_15", getLongValue(w[3].toString()));
            parm.put("DAYS16_30", getLongValue(w[4].toString()));
            parm.put("DAYS31_60", getLongValue(w[5].toString()));
            clntLoanMatuProjList.add(parm);
        });
        params.put("pndgSummryList", getJRDataSource(pndgSummryList));
        params.put("clntLoanMatuProjList", getJRDataSource(clntLoanMatuProjList));
        params.put("curr_user", currUser);
        return reportComponent.generateReport(BTAP, params, getJRDataSource(turnObverList));
    }

    public byte[] getFiveDaysAdvanceRecoveryTrends(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq", brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("fromDt", fromDt);
        params.put("toDt", toDt);
        params.put("curr_user", userId);

        String trendQury;
        trendQury = readFile(Charset.defaultCharset(), "FIVE_DAYS_ADVANCE_RECOVERY_TRENDS.txt");
        Query rs = em.createNativeQuery(trendQury).setParameter("fromDt", fromDt).setParameter("toDt", toDt).setParameter("brnchSeq",
                brnchSeq);

        // Query trendQury = em.createNativeQuery( com.idev4.rds.util.Queries.FIVE_DAYS_ADVANCE_RECOVERY_TRENDS )
        // .setParameter( "fromDt", fromDt ).setParameter( "toDt", toDt ).setParameter( "brnchSeq", brnchSeq );
        List<Object[]> trendObject = rs.getResultList();

        List<Map<String, ?>> trendsList = new ArrayList();
        trendObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0].toString());
            parm.put("CLNT_ID", w[1].toString());
            parm.put("CLNT_NM", w[2].toString());
            parm.put("DUE_DT", getFormaedDate(w[3].toString()));
            parm.put("PYMT_DT", getFormaedDate(w[4].toString()));
            parm.put("ADV_DAYS", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("PH_NUM", w[6] == null ? "" : w[6].toString());
            trendsList.add(parm);
        });

        return reportComponent.generateReport(FDARTA, params, getJRDataSource(trendsList));

    }

    public byte[] getTopSheet(String fromDt, String toDt, long brnchSeq, long prd, int flg, String userId)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("fromDt", fromDt);
        params.put("toDt", toDt);
        params.put("curr_user", userId);
        params.put("title", flg == 0 ? "Top Sheet Report" : "Tag Top Sheet Report");

        String trendQury;
        trendQury = (flg == 0 ? readFile(Charset.defaultCharset(), "TOP_SHEET.txt")
                : readFile(Charset.defaultCharset(), "TAG_TOPSHEET.txt"));

        Query rs = em.createNativeQuery(trendQury).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("prdseq", prd).setParameter("brnchseq", brnchSeq);

        // Query trendQury = em.createNativeQuery( flg == 0 ?
        // com.idev4.rds.util.Queries.TOP_SHEET :
        // com.idev4.rds.util.Queries.TAG_TOPSHEET
        // )
        // .setParameter( "frmdt", fromDt ).setParameter( "todt", toDt ).setParameter(
        // "prdseq", prd )
        // .setParameter( "brnchseq", brnchSeq );
        List<Object[]> trendObject = rs.getResultList();
        List<Map<String, ?>> trendsList = new ArrayList();
        trendObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("PRD_GRP_NM", w[0] == null ? "" : w[0].toString());
            parm.put("EMP_NM", w[1] == null ? "" : w[1].toString());
            parm.put("OPN_CLNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("OPN_PRN_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("OPN_SVC_AMT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("DSBMT_CNT", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("DSBMT_PRN_AMT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("DSBMT_SVC_AMT", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("RCVRD_CLNT", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("RCVRD_PRN_AMT", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("RCVRD_SVC_AMT", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("ADJ_CLNT", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("ADJ_PRN_AMT", w[12] == null ? 0 : new BigDecimal(w[12].toString()).longValue());
            parm.put("ADJ_SVC_AMT", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("CLSNG_CLNT", w[14] == null ? 0 : new BigDecimal(w[14].toString()).longValue());
            parm.put("CLSNG_PRN_AMT", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("CLSNG_SVC_AMT", w[16] == null ? 0 : new BigDecimal(w[16].toString()).longValue());
            parm.put("CMPLTD_LOANS", w[17] == null ? 0 : new BigDecimal(w[17].toString()).longValue());
            parm.put("TG_CLSNG_CLNT", w[18] == null ? 0 : new BigDecimal(w[18].toString()).longValue());
            parm.put("TG_CLSNG_PRN_AMT", w[19] == null ? 0 : new BigDecimal(w[19].toString()).longValue());
            parm.put("TG_CLSNG_SVC_AMT", w[20] == null ? 0 : new BigDecimal(w[20].toString()).longValue());

            trendsList.add(parm);
        });

        String reversalQuery;

        reversalQuery = (flg == 0 ? readFile(Charset.defaultCharset(), "TopSheetReversal.txt")
                : readFile(Charset.defaultCharset(), "Tag_TopsheetReversal.txt"));
        // reversalQuery = readFile( Charset.defaultCharset(), "TopSheetReversal.txt" );
        Query header = em.createNativeQuery(reversalQuery).setParameter("frmdt", fromDt).setParameter("todt", toDt)
                .setParameter("prdseq", prd).setParameter("brnchseq", brnchSeq);

        List<Object[]> reversalObj = header.getResultList();

        List<Map<String, ?>> rev = new ArrayList();

        reversalObj.forEach(l -> {
            Map<String, Object> hrdmap = new HashMap();
            hrdmap.put("EMP_NM", l[1] == null ? "" : l[1].toString());
            hrdmap.put("DSBMT_CNT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            hrdmap.put("DSBMT_PRN_AMT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            hrdmap.put("DSBMT_SVC_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            hrdmap.put("RCVRD_CLNT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            hrdmap.put("RCVRD_PRN_AMT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            hrdmap.put("RCVRD_SVC_AMT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            hrdmap.put("ADJ_CLNT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            hrdmap.put("ADJ_PRN_AMT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            hrdmap.put("ADJ_SVC_AMT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            rev.add(hrdmap);
        });
        params.put("dataset", getJRDataSource(rev));

        return reportComponent.generateReport(TOPSHEET, params, getJRDataSource(trendsList));

    }

    public byte[] getPDCDetailReport(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("fromDt", fromDt);
        params.put("toDt", toDt);
        params.put("curr_user", userId);
        String trendQury;
        trendQury = readFile(Charset.defaultCharset(), "PDC_DETAILS.txt");

        Query rs = em.createNativeQuery(trendQury).setParameter("frmDt", fromDt).setParameter("toDt", toDt)
                .setParameter("brnchSeq", brnchSeq);

        // Query trendQury = em.createNativeQuery(
        // com.idev4.rds.util.Queries.PDC_DETAILS ).setParameter( "frmDt", fromDt )
        // .setParameter( "toDt", toDt ).setParameter( "brnchSeq", brnchSeq );
        List<Object[]> trendObject = rs.getResultList();
        List<Map<String, ?>> trendsList = new ArrayList();
        trendObject.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("CLNT_ID", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_NM", w[1] == null ? "" : w[1].toString());
            parm.put("PDC_PRVDR_NM", w[2] == null ? "" : w[2].toString());
            parm.put("REF_CD_DSCR", w[3] == null ? "" : w[3].toString());
            parm.put("ACCT_NUM", w[4] == null ? "" : w[4].toString());
            parm.put("PDC_HDR_SEQ", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("SR_NO", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("CHQ_NUM", w[7] == null ? "" : w[7].toString());

            trendsList.add(parm);
        });
        params.put("dataset", getJRDataSource(trendsList));

        return reportComponent.generateReport(PDCDETAIL, params, null);

    }

    public byte[] getRateOfRetentionReport(long brnchSeq, String userId) throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("curr_user", userId);

        String rateOfRetObjQury;
        rateOfRetObjQury = readFile(Charset.defaultCharset(), "AgencyTrackingReportrateOfRetObjQury.txt");

        Query rs = em.createNativeQuery(rateOfRetObjQury).setParameter("brnchSeq", brnchSeq);

        /*
         * Query rateOfRetObjQury = em.createNativeQuery( "select mnth cmpltd_mnth,\r\n"
         * + "mnth_ordr,\r\n" +
         * "cmpltd_loans,month01,month02,month03,month04,month05,month06,month07,month08,month09,month10,month11,month12\r\n"
         * + "from\r\n" + "(\r\n" +
         * "        select to_char(trunc(cap.loan_app_sts_dt),'Mon-YYYY') mnth, to_char(trunc(cap.loan_app_sts_dt),'YYYYMM') mnth_ordr,\r\n"
         * + "        count(distinct cap.loan_app_seq) cmpltd_loans\r\n" +
         * "        from mw_loan_app cap\r\n" +
         * "        join mw_port prt on prt.port_seq= cap.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:brnchSeq\r\n"
         * +
         * "        join mw_ref_cd_val lsts on lsts.ref_cd_seq=cap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * + "        where cap.crnt_rec_flg=1\r\n" +
         * "        and lsts.ref_cd='0006' \r\n" +
         * "        and to_number(to_char(to_date(cap.loan_app_sts_dt),'YYYYMM')) between to_number(to_char(add_months(sysdate,-12),'YYYYMM')) and to_number(to_char(add_months(sysdate,-1),'YYYYMM'))\r\n"
         * +
         * "        group by to_char(trunc(cap.loan_app_sts_dt),'Mon-YYYY'), to_char(trunc(cap.loan_app_sts_dt),'YYYYMM')\r\n"
         * + ") cmp\r\n" + "left outer join \r\n" + "(\r\n" +
         * "        select  cclnt.c_mnth,\r\n" +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-12),'YYYYMM') then ap.loan_app_seq else null end) month01,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-11),'YYYYMM') then ap.loan_app_seq else null end) month02,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-10),'YYYYMM') then ap.loan_app_seq else null end) month03,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-09),'YYYYMM') then ap.loan_app_seq else null end) month04,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-08),'YYYYMM') then ap.loan_app_seq else null end) month05,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-07),'YYYYMM') then ap.loan_app_seq else null end) month06,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-06),'YYYYMM') then ap.loan_app_seq else null end) month07,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-05),'YYYYMM') then ap.loan_app_seq else null end) month08,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-04),'YYYYMM') then ap.loan_app_seq else null end) month09,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-03),'YYYYMM') then ap.loan_app_seq else null end) month10,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-02),'YYYYMM') then ap.loan_app_seq else null end) month11,\r\n"
         * +
         * "                count(distinct case when to_char(dvh.dsbmt_dt,'YYYYMM')=to_char(add_months(sysdate,-01),'YYYYMM') then ap.loan_app_seq else null end) month12\r\n"
         * + "        from mw_loan_app ap\r\n" +
         * "        join mw_port prt on prt.port_seq= ap.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:brnchSeq\r\n"
         * +
         * "        join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * + "        join \r\n" +
         * "        ( select to_char(cmp.loan_app_sts_dt,'YYYYMM') c_mnth,cmp.clnt_seq\r\n"
         * + "                from mw_loan_app cmp\r\n" +
         * "                join mw_port prt on prt.port_seq= cmp.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:brnchSeq\r\n"
         * +
         * "                join mw_ref_cd_val lsts on lsts.ref_cd_seq=cmp.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * + "                where cmp.crnt_rec_flg=1\r\n" +
         * "                --and cmp.clnt_seq=ap.clnt_seq\r\n" +
         * "--                and to_char(cmp.loan_app_sts_dt,'YYYYMM')=201805\r\n" +
         * "                and lsts.ref_cd='0006' \r\n" +
         * "                and to_number(to_char(to_date(cmp.loan_app_sts_dt),'YYYYMM')) between to_number(to_char(add_months(sysdate,-12),'YYYYMM')) and to_number(to_char(add_months(sysdate,-1),'YYYYMM'))\r\n"
         * + "        ) cclnt on cclnt.clnt_seq=ap.clnt_seq\r\n" +
         * "        where ap.crnt_rec_flg=1\r\n" + "        and ap.loan_app_sts=703\r\n"
         * + "--        and to_char(dvh.dsbmt_dt,'YYYYMM')=201805\r\n" +
         * "        and to_number(to_char(to_date(ap.loan_app_sts_dt),'YYYYMM')) between to_number(to_char(add_months(sysdate,-12),'YYYYMM')) and to_number(to_char(add_months(sysdate,-1),'YYYYMM'))\r\n"
         * + "    group by cclnt.c_mnth\r\n" + ") rtnd on rtnd.c_mnth=mnth_ordr\r\n" +
         * "order by 2" ) .setParameter( "brnchSeq", brnchSeq );
         */
        List<Object[]> rateOfRetObj = rs.getResultList();
        List<Map<String, ?>> rateOfRetObjList = new ArrayList();
        int c = 1;
        long comRepttd = 0L;
        long comComltd = 0L;
        DecimalFormat df = new DecimalFormat("#.##");
        for (Object[] w : rateOfRetObj) {
            if (c == 1) {
                params.put("from_dt", w[0].toString());
            }
            params.put("to_dt", w[0].toString());
            params.put("MONTH" + (c < 10 ? "0" : "") + c, w[0].toString());
            Map<String, Object> parm = new HashMap<>();
            parm.put("CMPLTD_MNTH", w[0].toString());
            parm.put("MNTH_ORDR", w[1].toString());
            parm.put("CMPLTD_LOANS", new BigDecimal(w[2].toString()).longValue());
            parm.put("MONTH01", new BigDecimal(w[3].toString()).longValue());
            parm.put("MONTH02", new BigDecimal(w[4].toString()).longValue());
            parm.put("MONTH03", new BigDecimal(w[5].toString()).longValue());
            parm.put("MONTH04", new BigDecimal(w[6].toString()).longValue());
            parm.put("MONTH05", new BigDecimal(w[7].toString()).longValue());
            parm.put("MONTH06", new BigDecimal(w[8].toString()).longValue());
            parm.put("MONTH07", new BigDecimal(w[9].toString()).longValue());
            parm.put("MONTH08", new BigDecimal(w[10].toString()).longValue());
            parm.put("MONTH09", new BigDecimal(w[11].toString()).longValue());
            parm.put("MONTH10", new BigDecimal(w[12].toString()).longValue());
            parm.put("MONTH11", new BigDecimal(w[13].toString()).longValue());
            parm.put("MONTH12", new BigDecimal(w[14].toString()).longValue());
            long rptTtl = new BigDecimal(w[3].toString()).longValue() + new BigDecimal(w[4].toString()).longValue()
                    + new BigDecimal(w[5].toString()).longValue() + new BigDecimal(w[6].toString()).longValue()
                    + new BigDecimal(w[7].toString()).longValue() + new BigDecimal(w[8].toString()).longValue()
                    + new BigDecimal(w[9].toString()).longValue() + new BigDecimal(w[10].toString()).longValue()
                    + new BigDecimal(w[11].toString()).longValue() + new BigDecimal(w[12].toString()).longValue()
                    + new BigDecimal(w[13].toString()).longValue() + new BigDecimal(w[14].toString()).longValue();
            comRepttd = comRepttd + rptTtl;
            comComltd = comComltd + new BigDecimal(w[2].toString()).longValue();
            double comRetnRat = ((double) comRepttd / comComltd) * 100;
            parm.put("REPTED_TTL", rptTtl);
            parm.put("COM_REPTD", comRepttd);
            parm.put("COM_COMLTD", comComltd);
            parm.put("COM_RENTNRAT", df.format(comRetnRat));
            rateOfRetObjList.add(parm);
            c++;
        }

        return reportComponent.generateReport(RETENTIONRATE, params, getJRDataSource(rateOfRetObjList));
    }

    public byte[] getProjectedClientLoanCompletion(String fromDt, String toDt, long brnchSeq, String userId)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("curr_user", userId);
        params.put("from_dt", fromDt);
        params.put("to_dt", toDt);

        String projClntLoanQuery;
        projClntLoanQuery = readFile(Charset.defaultCharset(), "PROJECTED_CLIENTS_LOANS_COMPLE.txt");

        Query rs = em.createNativeQuery(projClntLoanQuery).setParameter("frmDt", fromDt).setParameter("toDt", toDt)
                .setParameter("brnch", brnchSeq);

        // Query projClntLoanQuery = em.createNativeQuery(
        // com.idev4.rds.util.Queries.PROJECTED_CLIENTS_LOANS_COMPLE )
        // .setParameter( "frmDt", fromDt ).setParameter( "toDt", toDt ).setParameter(
        // "brnch", brnchSeq );
        List<Object[]> projClntObj = rs.getResultList();
        List<Map<String, ?>> projClntList = new ArrayList();
        projClntObj.forEach(c -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("BDO_NM", c[0] == null ? "" : c[0].toString());
            parm.put("CLNT_ID", c[1] == null ? "" : c[1].toString());
            parm.put("CLNT_NM", c[2] == null ? "" : c[2].toString());
            parm.put("FTHR_SPZ_NM", c[3] == null ? "" : c[3].toString());
            parm.put("CNTCT_NUM", c[4] == null ? "" : c[4].toString());
            parm.put("ADDR", c[5] == null ? "" : c[5].toString());
            parm.put("LOAN_USR", c[6] == null ? "" : c[6].toString());
            parm.put("LOAN_CYCL_NUM", c[7] == null ? "" : c[7].toString());
            parm.put("APRVD_LOAN_AMT", c[8] == null ? 0L : new BigDecimal(c[8].toString()).longValue());
            parm.put("CMP_DT", c[9] == null ? "" : getFormaedDate(c[9].toString()));
            parm.put("PRD", c[10] == null ? "" : c[10].toString());
            projClntList.add(parm);
        });

        return reportComponent.generateReport(PROJECTED_CLIENTS_LOANS_COM, params, getJRDataSource(projClntList));
    }

    public byte[] getRegionMonthWiseADCReport(String fromDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("fromDt", fromDt);
        params.put("toDt", toDt);
        params.put("curr_user", userId);

        String query;
        query = readFile(Charset.defaultCharset(), "RegionMonthWiseADCReport.txt");

        Query rs = em.createNativeQuery(query).setParameter("frmDt", fromDt).setParameter("toDt", toDt);

        /*
         * Query query = em.createNativeQuery( "SELECT \r\n" + "mr.REG_NM, \r\n" +
         * "ma.area_nm, \r\n" + "mb.brnch_nm, \r\n" + "mt.typ_str Channel, \r\n" +
         * "COUNT (DISTINCT trx.RCVRY_TRX_SEQ) trx_cnt, \r\n" +
         * "SUM (trx.pymt_amt) rcvrd_amt,\r\n" + "max(tot_adc_trx) tot_adc_trx,\r\n" +
         * "max(tot_adc_amt) tot_adc_amt,\r\n" +
         * "max(lst_act_clnts) actv_clnts_last_mnth, \r\n" +
         * "max(due_amt) due_last_mnth,\r\n" +
         * "max(tot_adc_trx)/max(tot_trx)*100 adc_trx_per,\r\n" +
         * "max(tot_adc_amt)/max(tot_amt)*100 adc_amt_per,\r\n" + "max(tot_amt),\r\n" +
         * "max(csh_trx)csh_trx,\r\n" + "max(csh_amt)csh_amt,\r\n" +
         * "max(csh_trx)/max(tot_trx)*100 csh_trx_per,\r\n" +
         * "max(csh_amt)/max(tot_amt)*100 csh_amt_per\r\n" + "FROM mw_rcvry_trx trx\r\n"
         * +
         * "join mw_loan_app ap ON ap.clnt_seq=trx.pymt_ref and ap.crnt_rec_flg = 1 and (loan_app_sts=703 \r\n"
         * +
         * "     or (ap.loan_app_sts=704 and ap.loan_app_sts_dt between to_date(:frmDt,'dd-MM-yyyy') AND to_date(:toDt,'dd-MM-yyyy')))\r\n"
         * + "     and ap.prnt_loan_app_seq=ap.loan_app_seq\r\n" +
         * "join mw_typs mt on mt.TYP_SEQ=trx.RCVRY_TYP_SEQ and mt.CRNT_REC_FLG = 1 \r\n"
         * + "join mw_port mp on ap.PORT_SEQ = mp.PORT_SEQ AND mp.CRNT_REC_FLG = 1 \r\n"
         * +
         * "join mw_brnch mb on mp.BRNCH_SEQ = mb.BRNCH_SEQ AND mb.CRNT_REC_FLG = 1 \r\n"
         * + "join mw_area ma on ma.area_seq=mb.area_seq and ma.crnt_rec_flg=1 \r\n" +
         * "join mw_reg mr on mr.reg_seq=ma.reg_seq and mr.crnt_rec_flg=1 \r\n" +
         * "left outer join (\r\n" + "    select brnch_seq,\r\n" +
         * "    count(distinct case when trx.rcvry_typ_seq=161 then trx.rcvry_trx_seq else null end) csh_trx,\r\n"
         * +
         * "    sum(distinct case when trx.rcvry_typ_seq=161 then pymt_amt else 0 end) csh_amt,   \r\n"
         * +
         * "    count(distinct case when trx.rcvry_typ_seq<>161 then trx.rcvry_trx_seq else null end) tot_adc_trx,\r\n"
         * +
         * "    sum(case when trx.rcvry_typ_seq<>161 then pymt_amt else 0 end) tot_adc_amt, \r\n"
         * + "    count(trx.rcvry_trx_seq) tot_trx,\r\n" +
         * "    sum(pymt_amt) tot_amt\r\n" + "    FROM mw_rcvry_trx trx\r\n" +
         * "    join mw_loan_app ap ON ap.clnt_seq=trx.pymt_ref and ap.crnt_rec_flg = 1 and (loan_app_sts=703 \r\n"
         * +
         * "         or (ap.loan_app_sts=704 and ap.loan_app_sts_dt between to_date(:frmDt,'dd-MM-yyyy') AND to_date(:toDt,'dd-MM-yyyy')))\r\n"
         * + "         and ap.prnt_loan_app_seq=ap.loan_app_seq\r\n" +
         * "    join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * + "    where trx.crnt_rec_flg=1     \r\n" +
         * "      AND trx.pymt_dt BETWEEN to_date(:frmDt,'dd-MM-yyyy') AND to_date(:toDt,'dd-MM-yyyy') \r\n"
         * + "    group by prt.brnch_seq \r\n" +
         * ") csh on csh.brnch_seq=mb.brnch_seq\r\n" + "join (\r\n" +
         * "    select  mp.BRNCH_SEQ,count(ap.loan_app_seq) lst_act_clnts,\r\n" +
         * "    sum(loan_app_ost( ap.loan_app_seq,to_date(:frmDt,'dd-MM-yyyy')-1,'ps')) due_amt\r\n"
         * + "    from mw_loan_app ap\r\n" +
         * "    join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1,\r\n"
         * + "    mw_port mp, MW_DSBMT_VCHR_HDR dvh\r\n" +
         * "    where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:frmDt,'dd-MM-yyyy')-1 and ap.crnt_rec_flg=1) --toDate\r\n"
         * +
         * "    or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:frmDt,'dd-MM-yyyy')-1 and to_date(dvh.DSBMT_DT) <= to_date(:frmDt,'dd-MM-yyyy')-1) \r\n"
         * +
         * "    or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:frmDt,'dd-MM-yyyy')-1)) \r\n"
         * +
         * "    and loan_app_ost(ap.loan_app_seq,to_date(:frmDt,'dd-MM-yyyy')-1) > 0\r\n"
         * + "    and ap.port_seq = mp.port_seq\r\n" +
         * "    and dvh.LOAN_APP_SEQ = ap.LOAN_APP_SEQ \r\n" +
         * "    and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.loan_app_seq = ap.LOAN_APP_SEQ and ctl.EFF_START_DT <= to_date(:frmDt,'dd-MM-yyyy')-1) \r\n"
         * + "    and dvh.CRNT_REC_FLG = 1\r\n" + "    and ap.CRNT_REC_FLG = 1\r\n" +
         * "    and mp.CRNT_REC_FLG = 1\r\n" + "    group by mp.brnch_seq\r\n" +
         * ") pmnth on pmnth.brnch_seq=mt.brnch_seq\r\n" +
         * "WHERE trx.crnt_rec_flg = 1 \r\n" +
         * "AND trx.pymt_dt BETWEEN to_date(:frmDt,'dd-MM-yyyy') AND to_date(:toDt,'dd-MM-yyyy') \r\n"
         * + "and trx.rcvry_typ_seq<>161\r\n" +
         * "GROUP BY RCVRY_TYP_SEQ, mr.REG_NM, ma.area_nm, mb.brnch_nm, mt.typ_str\r\n"
         * + "order by 1,2,3" ).setParameter( "frmDt", fromDt ).setParameter( "toDt",
         * toDt );
         */
        List<Object[]> queryResult = rs.getResultList();
        List<Map<String, ?>> regionAdcList = new ArrayList();
        queryResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REG_NM", w[0] == null ? "" : w[0].toString());
            parm.put("AREA_NM", w[1] == null ? "" : w[1].toString());
            parm.put("BRNCH_NM", w[2] == null ? "" : w[2].toString());
            parm.put("CHANNEL", w[3] == null ? "" : w[3].toString());
            parm.put("TRX_CNT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("RCVRD_AMT", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("ACTV_CLNTS_LAST_MNTH", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("DUE_LAST_MNTH", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("ADC_TRX_PER", w[10] == null ? 0.0 : new BigDecimal(w[10].toString()).floatValue());
            parm.put("ADC_AMT_PER", w[11] == null ? 0.0 : new BigDecimal(w[11].toString()).floatValue());
            parm.put("ADC_AMT", w[12] == null ? 0 : new BigDecimal(w[12].toString()).longValue());
            parm.put("CASH_TRX", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("CASH_AMT", w[14] == null ? 0 : new BigDecimal(w[14].toString()).longValue());
            parm.put("CASH_PER", w[15] == null ? 0.0 : new BigDecimal(w[15].toString()).floatValue());
            parm.put("CASH_AMT_PER", w[16] == null ? 0.0 : new BigDecimal(w[16].toString()).floatValue());

            regionAdcList.add(parm);
        });
        params.put("data_set", getJRDataSource(regionAdcList));
        return reportComponent.generateReport(ADC_MONTH_WISE_REPORT, params, null);
    }

    public byte[] getAnmlInsuClmFrm(long anmlRgstrSeq, String userId) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String clntinfoQry;
        clntinfoQry = readFile(Charset.defaultCharset(), "AnmlInsuClmFrmclntinfoQry.txt");

        Query rs = em.createNativeQuery(clntinfoQry).setParameter("anmlRgstrSeq", anmlRgstrSeq);
        /*
         * Query clntinfoQry = em.createNativeQuery(
         * "select c.clnt_id,c.frst_nm||' '||c.last_nm clnt_nm,c.cnic_num,c.PH_NUM,  \r\n"
         * +
         * "dr.dt_of_dth,dr.cause_of_dth,dr.clnt_nom_flg,app.APRVD_LOAN_AMT,app.LOAN_APP_STS_DT,  \r\n"
         * +
         * "cr.frst_nm||' '||cr.last_nm rel_nm,app.loan_app_seq,mar.ANML_TYP,anm_typ.REF_CD_DSCR,  \r\n"
         * + "mar.TAG_NUM,mar.PRCH_AMT,mar.AGE_MNTH,mar.AGE_YR,  \r\n" +
         * "( 'H No '||ad.HSE_NUM ||' ,St No '|| ad.STRT||' ,'|| ad.oth_dtl ||' ,'||city.CITY_NM||','||dist.dist_nm||','||cntry.cntry_nm ) addr,mar.ANML_PIC,mar.TAG_PIC,c.clnt_seq  \r\n"
         * + "from mw_clnt c   \r\n" +
         * "join mw_loan_app app on app.clnt_seq = c.clnt_seq and app.crnt_rec_flg=1 and app.prnt_loan_app_seq=app.loan_app_seq   \r\n"
         * +
         * "join mw_ref_cd_val val on val.ref_cd_seq=app.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and val.ref_cd ='0005'   \r\n"
         * +
         * "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'  \r\n"
         * +
         * "join mw_anml_rgstr mar on mar.LOAN_APP_SEQ = app.LOAN_APP_SEQ and mar.CRNT_REC_FLG = 1  \r\n"
         * +
         * "join mw_ref_cd_val anm_typ on anm_typ.ref_cd_seq=mar.ANML_TYP and anm_typ.crnt_rec_flg=1 and anm_typ.del_flg=0    \r\n"
         * +
         * "join mw_dth_rpt dr on dr.clnt_seq=mar.ANML_RGSTR_SEQ and dr.crnt_rec_flg= 1 and dr.crtd_dt>app.crtd_dt and (dr.CLNT_NOM_FLG=3 OR dr.CLNT_NOM_FLG=4)   \r\n"
         * +
         * "join mw_addr_rel addrRel on addrrel.enty_key = c.clnt_seq and addrRel.enty_typ='Client' and addrRel.crnt_rec_flg=1 and addrRel.del_flg=0  \r\n"
         * +
         * "join mw_addr ad on ad.addr_seq = addrRel.addr_seq and ad.crnt_rec_flg=1 and ad.del_flg=0   \r\n"
         * +
         * "join mw_city_uc_rel rel on rel.city_uc_rel_seq = ad.city_seq and rel.crnt_rec_flg=1 and rel.del_flg = 0   \r\n"
         * +
         * "join mw_uc uc on rel.uc_SEQ =uc.UC_SEQ  and uc.del_flg = 0 and uc.crnt_rec_flg = 1   \r\n"
         * +
         * "join mw_thsl thsl on uc.thsl_SEQ =thsl.thsl_SEQ and thsl.del_flg = 0 and thsl.crnt_rec_flg = 1   \r\n"
         * +
         * "join mw_dist dist on thsl.dist_SEQ =dist.dist_SEQ and dist.del_flg = 0 and dist.crnt_rec_flg = 1   \r\n"
         * +
         * "join mw_st st on dist.st_SEQ =st.st_SEQ and st.del_flg = 0  and st.crnt_rec_flg = 1   \r\n"
         * +
         * "join mw_cntry cntry on st.cntry_SEQ =cntry.cntry_SEQ and cntry.crnt_rec_flg = 1 and cntry.del_flg=0   \r\n"
         * +
         * "join mw_city city on city.city_seq = rel.city_seq  and city.del_flg = 0  and city.crnt_rec_flg = 1   \r\n"
         * +
         * "left outer join mw_clnt_rel cr on cr.loan_app_seq = app.prnt_loan_app_seq and cr.rel_typ_flg=1 and cr.crnt_rec_flg = 1   \r\n"
         * + "where mar.ANML_RGSTR_SEQ=:anmlRgstrSeq and c.crnt_rec_flg = 1"
         * ).setParameter( "anmlRgstrSeq", anmlRgstrSeq );
         */
        Object[] clntObj = (Object[]) rs.getSingleResult();
        params.put("CLNT_ID", clntObj[0].toString());
        params.put("CLNT_NM", clntObj[1].toString());
        params.put("CNIC_NUM", clntObj[2] == null ? "" : clntObj[2].toString());
        params.put("PH_NUM", clntObj[3] == null ? "" : clntObj[3].toString());
        params.put("DT_OF_DTH", getFormaedDate(clntObj[4].toString()));
        params.put("CAUSE_OF_DTH", clntObj[5] == null ? "" : clntObj[5].toString());
        params.put("CLNT_NOM_FLG", clntObj[6] == null ? "" : clntObj[6].toString());
        params.put("APRVD_LOAN_AMT", clntObj[7] == null ? 0 : new BigDecimal(clntObj[7].toString()).longValue());
        params.put("LOAN_APP_STS_DT", getFormaedDate(clntObj[8].toString()));
        params.put("REL_NM", clntObj[9] == null ? "" : clntObj[9].toString());
        params.put("LOAN_APP_SEQ", clntObj[10] == null ? 0 : new BigDecimal(clntObj[10].toString()).longValue());
        params.put("ANML_TYP", clntObj[12] == null ? "" : clntObj[12].toString());
        params.put("TAG_NUM", clntObj[13] == null ? "" : clntObj[13].toString());
        params.put("PRCH_AMT", clntObj[14] == null ? "" : clntObj[14].toString());
        params.put("AGE_MNTH", clntObj[15] == null ? "" : clntObj[15].toString());
        params.put("AGE_YR", clntObj[16] == null ? "" : clntObj[16].toString());
        params.put("ADDR", clntObj[17] == null ? "" : clntObj[17].toString());
        Clob anml = null;
        Clob tag = null;

        try {
            anml = (Clob) (clntObj[18] == null ? null : clntObj[18]);

            tag = (Clob) (clntObj[19] == null ? null : clntObj[19]);
        } catch (Exception e) {

        }

        params.put("ANML_IMG", getStringClob(anml));
        params.put("TAG_IMG", getStringClob(tag));

        Clients clnt = clientRepository
                .findOneByClntSeqAndCrntRecFlg(new BigDecimal(clntObj[20].toString()).longValue(), true);

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.USER_BRANCH_INFO).setParameter("portKey",
                clnt.getPortKey());
        Object[] obj = (Object[]) bi.getSingleResult();

        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String bizApprQury;
        bizApprQury = readFile(Charset.defaultCharset(), "AnmlInsuClmFrmbizApprQury.txt");

        Query rset = em.createNativeQuery(bizApprQury).setParameter("loanAppSeq", clntObj[10].toString());

        /*
         * Query bizApprQury = em.createNativeQuery(
         * " select ba.biz_aprsl_seq,ba.yrs_in_biz,ba.mnth_in_biz,biz.ref_cd_dscr biz,prsn.ref_cd_dscr prsn,sect.biz_sect_nm sect,acty.biz_acty_nm acty, \r\n"
         * +
         * "(case when ba.BIZ_ADDR_SAME_AS_HOME_FLG=1 then 'Same as Client Address' else 'H No '||ad.HSE_NUM ||' ,St No '|| ad.STRT||' ,'|| ad.oth_dtl ||' ,'||city.CITY_NM||','||dist.dist_nm||','||cntry.cntry_nm end) addr\r\n"
         * + "from mw_biz_aprsl  ba\r\n" +
         * "join mw_ref_cd_val biz on ba.biz_own=biz.ref_cd_seq and biz.crnt_rec_flg=1 and biz.del_flg=0\r\n"
         * +
         * "join mw_ref_cd_val prsn on ba.prsn_run_the_biz=prsn.ref_cd_seq and prsn.crnt_rec_flg=1 and prsn.del_flg=0\r\n"
         * +
         * "join mw_biz_acty acty on ba.acty_key=acty.biz_acty_seq and acty.crnt_rec_flg=1 and acty.del_flg=0\r\n"
         * +
         * "join mw_biz_sect sect on sect.BIZ_SECT_SEQ=acty.BIZ_SECT_SEQ and sect.crnt_rec_flg=1 and sect.del_flg=0\r\n"
         * +
         * "left outer join mw_addr_rel addrRel on addrrel.enty_key = ba.biz_aprsl_seq and addrRel.enty_typ='Business' and addrRel.crnt_rec_flg=1 and addrRel.del_flg=0\r\n"
         * +
         * "left outer join mw_addr ad on ad.addr_seq = addrRel.addr_seq and ad.crnt_rec_flg=1 and ad.del_flg=0\r\n"
         * +
         * "left outer join mw_city_uc_rel rel on rel.city_uc_rel_seq = ad.city_seq and rel.crnt_rec_flg=1 and rel.del_flg = 0\r\n"
         * +
         * "left outer join mw_uc uc on rel.uc_SEQ =uc.UC_SEQ  and uc.del_flg = 0 and uc.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_thsl thsl on uc.thsl_SEQ =thsl.thsl_SEQ and thsl.del_flg = 0 and thsl.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_dist dist on thsl.dist_SEQ =dist.dist_SEQ and dist.del_flg = 0 and dist.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_st st on dist.st_SEQ =st.st_SEQ and st.del_flg = 0  and st.crnt_rec_flg = 1\r\n"
         * +
         * "left outer join mw_cntry cntry on st.cntry_SEQ =cntry.cntry_SEQ and cntry.crnt_rec_flg = 1 and cntry.del_flg=0\r\n"
         * +
         * "left outer join mw_city city on city.city_seq = rel.city_seq  and city.del_flg = 0  and city.crnt_rec_flg = 1\r\n"
         * + "where ba.loan_app_seq=:loanAppSeq and ba.crnt_rec_flg=1" ) .setParameter(
         * "loanAppSeq", clntObj[ 10 ].toString() );
         */

        List<Object[]> bizApprList = rset.getResultList();
        if (bizApprList.size() > 0) {
            params.put("BIZ_ADDR", bizApprList.get(0)[7].toString());
        }

        String paymentsQry;
        paymentsQry = readFile(Charset.defaultCharset(), "AnmlInsuClmFrmpaymentsQry.txt");

        Query resset = em.createNativeQuery(paymentsQry).setParameter("loanAppSeq", clntObj[10].toString());

        /*
         * Query paymentsQry = em.createNativeQuery(
         * "select inst_num,DUE_DT,sum(ppal_amt_due),sum(tot_chrg_due),sum(amt),sum(PR_REC),sum(SC_REC), pymt_dt\r\n"
         * + "from( \r\n" +
         * "select psd.inst_num,psd.DUE_DT DUE_DT,psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) as amt,psd.pymt_sched_dtl_seq, \r\n"
         * + "(select sum(pymt_amt) \r\n" +
         * "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and CHRG_TYP_KEY=-1 and crnt_rec_flg=1) as PR_REC, \r\n"
         * + "(select sum(pymt_amt) \r\n" +
         * "from mw_rcvry_dtl where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and CHRG_TYP_KEY=17 and crnt_rec_flg=1) as SC_REC, \r\n"
         * + "(select PYMT_DT \" + \"from mw_rcvry_dtl rd \r\n" +
         * "join mw_rcvry_trx rt on rd.RCVRY_TRX_SEQ = rt.RCVRY_TRX_SEQ and rt.CRNT_REC_FLG = 1  \r\n"
         * +
         * "where pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.crnt_rec_flg=1  and CHRG_TYP_KEY=-1 and rownum=1) as pymt_dt \r\n"
         * + "from mw_loan_app la \r\n" +
         * "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and val.ref_cd ='0005'  \r\n"
         * +
         * "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'  \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
         * +
         * "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1 \r\n"
         * + "where la.loan_app_seq=:loanAppSeq and la.crnt_rec_flg=1  \r\n" +
         * "group by psd.inst_num,psd.DUE_DT,psd.ppal_amt_due,psd.tot_chrg_due,psd.pymt_sched_dtl_seq \r\n"
         * + "order by inst_num)group by inst_num,DUE_DT,pymt_dt\r\n" +
         * "order by inst_num" ) .setParameter( "loanAppSeq", clntObj[ 10 ].toString()
         * );
         */
        List<Object[]> pymtsObj = resset.getResultList();
        List<Map<String, ?>> pymts = new ArrayList();
        Long outSts = 0L;
        pymtsObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("INST_NUM", l[0].toString());
            map.put("DUE_DT", getFormaedDate(l[1].toString()));
            map.put("PPAL_AMT_DUE", l[2] == null ? 0 : getLongValue(l[2].toString()));
            map.put("TOT_CHRG_DUE", l[3] == null ? 0 : getLongValue(l[3].toString()));
            map.put("AMT", l[4] == null ? 0 : getLongValue(l[4].toString()));
            map.put("PR_REC", l[5] == null ? 0 : getLongValue(l[5].toString()));
            map.put("SC_REC", l[6] == null ? 0 : getLongValue(l[6].toString()));
            map.put("PYMT_DT", l[7] == null ? "" : getFormaedDate(l[7].toString()));
            pymts.add(map);
        });

        return reportComponent.generateReport(ANML_INSURANCECLAIM, params, getJRDataSource(pymts));
    }

    public byte[] getPortfolioConcentrationReport(long prdSeq, long brnch, String asDt, String userId)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("asDt", asDt);

        Query prdQ = em
                .createNativeQuery("select prd_cmnt \r\n" + "from mw_prd p\r\n"
                        + "join mw_prd_grp pg on pg.prd_grp_seq = p.prd_grp_seq and pg.crnt_rec_flg = 1 \r\n"
                        + "where pg.prd_grp_seq=:prdSeq and p.crnt_rec_flg=1\r\n" + "group by prd_cmnt")
                .setParameter("prdSeq", prdSeq);
        String prd = (String) prdQ.getSingleResult();

        params.put("prd", prd);

        String bussQry;
        bussQry = readFile(Charset.defaultCharset(), "PortfolioConcentrationbussQry.txt");

        Query resset = em.createNativeQuery(bussQry).setParameter("prdSeq", prdSeq).setParameter("brnch", brnch)
                .setParameter("asdt", asDt);

        /*
         * Query bussQry = em.createNativeQuery( "select bs.biz_sect_nm,\r\n" +
         * "count(la.loan_app_seq) actv_lns,\r\n" +
         * "sum(nvl (la.aprvd_loan_amt, 0)) bsbmt_amt,\r\n" +
         * "sum(nvl (loan_app_ost (la.loan_app_seq, to_date(:asdt,'dd-MM-yyyy')), 0)) ost_amt,\r\n"
         * +
         * "sum(nvl ((nvl (od_cl.pr_od, 0) + nvl (od_cl.sc_od, 0)), 0)) od_amt_cl,\r\n"
         * + "count(nvl(od_cl.loan_app_seq,0)) od_loans,\r\n" +
         * "sum(nvl((case when od_cl.loan_app_seq > 0 then nvl (loan_app_ost (od_cl.loan_app_seq, to_date(:asdt,'dd-MM-yyyy')), 0) end),0)) ost_par_amt,\r\n"
         * +
         * "sum(nvl((case when nvl (od_cl.od_dys, 0) >= 30 then od_cl.loan_app_seq end),0)) od_loans_30,\r\n"
         * +
         * "sum(nvl((case when nvl (od_cl.od_dys, 0) >= 30 then nvl (loan_app_ost (od_cl.loan_app_seq, to_date(:asdt,'dd-MM-yyyy')), 0) end),0)) ost_par_amt_30,\r\n"
         * + "sum(nvl (od_cl.od_dys, 0)) od_dys\r\n" + "from mw_loan_app la,\r\n" +
         * "mw_biz_aprsl ba,\r\n" + "mw_prd prd,\r\n" + "mw_biz_acty bac,\r\n" +
         * "mw_biz_sect bs,\r\n" + "mw_port mp,\r\n" + "mw_ref_cd_val asts,\r\n" +
         * "(   \r\n" + "select shld_rec.loan_app_seq,\r\n" +
         * "pr_due - pr_rec pr_od,\r\n" + "sc_due - sc_rec sc_od,\r\n" +
         * "chrg_due - chrg_rec chrg_od,\r\n" +
         * "nvl (to_date(:asdt,'dd-MM-yyyy') - od_dt, 0) od_dys\r\n" +
         * "from ( select ap.loan_app_seq,\r\n" + "nvl (sum (psd.ppal_amt_due), 0)\r\n"
         * + "pr_due,\r\n" + "nvl (sum (tot_chrg_due), 0)\r\n" + "sc_due,\r\n" +
         * "nvl (sum ((select sum (amt) from mw_pymt_sched_chrg psc where psc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and crnt_rec_flg = 1)), 0) chrg_due,\r\n"
         * + "max (inst_num) inst_num, \r\n" + "min (psd.due_dt) od_dt\r\n" +
         * "from mw_loan_app ap\r\n" +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq = ap.loan_app_sts and lsts.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psh.loan_app_seq = ap.loan_app_seq and psh.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psd.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_ref_cd_val vl on vl.ref_cd_seq = psd.pymt_sts_key and vl.crnt_rec_flg = 1 and psd.due_dt <= to_date(:asdt,'dd-MM-yyyy')\r\n"
         * +
         * "and ( psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945', '1145') and ref_cd_grp_key = 179 and val.crnt_rec_flg = 1) \r\n"
         * +
         * "or ( psd.pymt_sts_key = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and ref_cd_grp_key = 179 and val.crnt_rec_flg = 1)\r\n"
         * +
         * "and (select max (trx.pymt_dt) from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq = rdtl.rcvry_trx_seq and trx.crnt_rec_flg = 1 \r\n"
         * +
         * "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > to_date(:asdt,'dd-MM-yyyy')))\r\n"
         * + "and ap.crnt_rec_flg = 1\r\n" +
         * "and ap.loan_app_sts not in(select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and ref_cd_grp_key = 106 and val.crnt_rec_flg = 1)\r\n"
         * + "group by ap.loan_app_seq) shld_rec,\r\n" + "( select ap.loan_app_seq,\r\n"
         * + "sum (nvl (pr_rec, 0)) pr_rec,\r\n" + "sum (nvl (sc_rec, 0)) sc_rec,\r\n" +
         * "sum (nvl (chrg_rec, 0)) chrg_rec\r\n" + "from mw_loan_app ap\r\n" +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq = ap.loan_app_sts and lsts.crnt_rec_flg = 1 \r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psh.loan_app_seq = ap.loan_app_seq and psh.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psd.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_ref_cd_val vl on vl.ref_cd_seq = psd.pymt_sts_key and vl.crnt_rec_flg = 1 \r\n"
         * + "left outer join ( select rdtl.pymt_sched_dtl_seq,\r\n" + "pymt_dt,\r\n" +
         * "(case when chrg_typ_key = -1 then nvl (sum (nvl (rdtl.pymt_amt, 0)), 0) end) pr_rec,\r\n"
         * +
         * "(case when chrg_typ_key in (416,413,418,419,383,414,17,415,417,412,410,411) then nvl (sum (nvl (rdtl.pymt_amt, 0)), 0) end) sc_rec,\r\n"
         * +
         * "(case when chrg_typ_key not in (-1,416,413,418,419,383,414,17,415,417,412,410,411) then nvl (sum (nvl (rdtl.pymt_amt, 0)), 0) end) chrg_rec\r\n"
         * + "from mw_pymt_sched_dtl psd\r\n" +
         * "join mw_rcvry_dtl rdtl on rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg = 1\r\n"
         * +
         * "join mw_rcvry_trx trx on trx.rcvry_trx_seq = rdtl.rcvry_trx_seq and trx.crnt_rec_flg = 1 \r\n"
         * + "where psd.crnt_rec_flg = 1\r\n" +
         * "and trx.pymt_dt <= to_date(:asdt,'dd-MM-yyyy')\r\n" +
         * "group by rdtl.pymt_sched_dtl_seq,pymt_dt,chrg_typ_key) pdt on pdt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
         * + "where ap.crnt_rec_flg = 1\r\n" +
         * "and psd.due_dt <= to_date(:asdt,'dd-MM-yyyy')\r\n" +
         * "and ( psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945', '1145') and ref_cd_grp_key = 179 and val.crnt_rec_flg = 1) \r\n"
         * +
         * "or ( psd.pymt_sts_key = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and ref_cd_grp_key = 179 and val.crnt_rec_flg = 1)\r\n"
         * +
         * "and (select max (trx.pymt_dt) from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq = rdtl.rcvry_trx_seq and trx.crnt_rec_flg = 1 and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > to_date(:asdt,'dd-MM-yyyy')))\r\n"
         * + "and ap.crnt_rec_flg = 1\r\n" +
         * "and ap.loan_app_sts not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and ref_cd_grp_key = 106 and val.crnt_rec_flg = 1)\r\n"
         * + "group by ap.loan_app_seq) actl_rec\r\n" +
         * "where shld_rec.loan_app_seq = actl_rec.loan_app_seq(+)\r\n" +
         * "and ( (pr_due - pr_rec) > 0\r\n" + "or (sc_due - sc_rec) > 0\r\n" +
         * "or (chrg_due - chrg_rec) > 0)\r\n" + ") od_cl\r\n" +
         * "where asts.ref_cd_seq = la.loan_app_sts\r\n" +
         * "and asts.crnt_rec_flg = 1\r\n" + "and la.loan_app_seq = ba.loan_app_seq\r\n"
         * + "and ba.acty_key = bac.biz_acty_seq\r\n" +
         * "and bac.biz_sect_seq = bs.biz_sect_seq\r\n" + "and ba.crnt_rec_flg = 1\r\n"
         * + "and bs.crnt_rec_flg = 1\r\n" + "and bac.crnt_rec_flg = 1\r\n" +
         * "and la.port_seq = mp.port_seq\r\n" + "and prd.prd_grp_seq=:prdSeq\r\n" +
         * "and prd.prd_seq=la.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "and mp.brnch_seq = :brnch\r\n" +
         * "and la.loan_app_seq = od_cl.loan_app_seq(+)\r\n" +
         * "and ( ( asts.ref_cd = '0005' and to_date (la.loan_app_sts_dt) <= to_date(:asdt,'dd-MM-yyyy')\r\n"
         * + "and la.crnt_rec_flg = 1) \r\n" +
         * "or ( asts.ref_cd = '0006' and to_date (la.loan_app_sts_dt) > to_date(:asdt,'dd-MM-yyyy')) \r\n"
         * +
         * "or ( asts.ref_cd = '1245' and to_date (la.loan_app_sts_dt) > to_date(:asdt,'dd-MM-yyyy')))\r\n"
         * + "and la.crnt_rec_flg = 1\r\n" +
         * "and loan_app_ost (la.loan_app_seq, to_date(:asdt,'dd-MM-yyyy')) > 0\r\n" +
         * "group by bs.biz_sect_nm" ).setParameter( "prdSeq", prdSeq ).setParameter(
         * "brnch", brnch ).setParameter( "asdt", asDt );
         */
        List<Object[]> bussResult = resset.getResultList();
        List<Map<String, ?>> bussList = new ArrayList();
        bussResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("BIZ_SECT_NM", w[0] == null ? "" : w[0].toString());
            parm.put("ACTV_LNS", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("BSBMT_AMT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("OST_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("OD_AMT_CL", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("OD_LOANS", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("OST_PAR_AMT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("OD_LOANS_30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("OST_PAR_AMT_30", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("OD_DYS", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            bussList.add(parm);
        });
        params.put("buss_dataset", getJRDataSource(bussList));

        String rangQry;
        bussQry = readFile(Charset.defaultCharset(), "PortfolioConcentrationbussQry.txt");

        Query rest = em.createNativeQuery(bussQry).setParameter("prdSeq", prdSeq).setParameter("brnch", brnch)
                .setParameter("asdt", asDt);

        /*
         * Query rangQry = em.createNativeQuery(
         * "select  case when nvl(la.APRVD_LOAN_AMT,0) <= 20000 then '10,001 - 20,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 20001 and 30000 then '20,001 - 30,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 30001 and 40000 then '30,001 - 40,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 40001 and 50000 then '40,001 - 50,000'\r\n"
         * + "         else 'More than 50,000' \r\n" + "         end loan_size,\r\n" +
         * "        count(distinct la.loan_app_seq) actv_loans ,\r\n" +
         * "        sum(case when nvl(la.APRVD_LOAN_AMT,0) <= 20000 then nvl(la.APRVD_LOAN_AMT,0)\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 20001 and 30000 then nvl(la.APRVD_LOAN_AMT,0)\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 30001 and 40000 then nvl(la.APRVD_LOAN_AMT,0) \r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 40001 and 50000 then nvl(la.APRVD_LOAN_AMT,0)\r\n"
         * + "         else nvl(la.APRVD_LOAN_AMT,0) \r\n" +
         * "        end) loan_amt,\r\n" +
         * "        sum(nvl(loan_app_ost(la.loan_app_seq,to_date(:asDt,'dd-MM-yyyy')),0)) ost_amt,\r\n"
         * +
         * "        sum(nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0)) od_amt_cl,\r\n"
         * + "        count(distinct od_cl.loan_app_seq) od_loans,\r\n" +
         * "        sum(nvl((case when od_cl.loan_app_seq > 0 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:asDt,'dd-MM-yyyy')),0) end),0)) ost_par_amt,\r\n"
         * +
         * "        sum(nvl((case when nvl(od_cl.OD_DYS,0) >= 30 then od_cl.loan_app_seq end),0)) od_loans_30,\r\n"
         * +
         * "        sum(nvl((case when nvl(od_cl.OD_DYS,0) >= 30 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:asDt,'dd-MM-yyyy')),0) end),0)) ost_par_amt_30,\r\n"
         * + "        sum(nvl(od_cl.OD_DYS,0)) od_dys                       \r\n" +
         * "    from mw_loan_app la, \r\n" + "         mw_port mp,\r\n" +
         * "         mw_prd prd,\r\n" + "         mw_ref_cd_val asts,            \r\n" +
         * "            (\r\n" + "                select shld_rec.LOAN_APP_SEQ,\r\n" +
         * "                pr_due - pr_rec pr_od,\r\n" +
         * "                sc_due - sc_rec sc_od,\r\n" +
         * "                chrg_due - chrg_rec chrg_od,\r\n" +
         * "               nvl(to_date(:asDt,'dd-MM-yyyy') - od_dt,0) od_dys\r\n" +
         * "                from\r\n" + "                (\r\n" +
         * "                select ap.LOAN_APP_SEQ, nvl(sum(psd.ppal_amt_due),0) pr_due, nvl(sum(tot_chrg_due),0) sc_due,\r\n"
         * +
         * "                nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ and crnt_rec_flg=1)),0) chrg_due,\r\n"
         * + "                max(INST_NUM) inst_num, min(psd.due_dt) od_dt\r\n" +
         * "                from mw_loan_app ap\r\n" +
         * "                join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
         * + "                and psd.due_dt <= to_date(:asDt,'dd-MM-yyyy')\r\n" +
         * "                and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
         * +
         * "                    or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
         * + "                    and (\r\n" +
         * "                    select max(trx.pymt_dt)\r\n" +
         * "                    from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * +
         * "                    and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:asDt,'dd-MM-yyyy')\r\n"
         * + "                    )\r\n" + "                    )\r\n" +
         * "                and ap.crnt_rec_flg =1\r\n" +
         * "                and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
         * + "                group by ap.LOAN_APP_SEQ\r\n" +
         * "                )shld_rec,\r\n" + "                (\r\n" +
         * "                select ap.loan_app_seq,\r\n" +
         * "                       sum(nvl(pr_rec,0)) pr_rec,\r\n" +
         * "                       sum(nvl(sc_rec,0)) sc_rec,\r\n" +
         * "                       sum(nvl(chrg_rec,0)) chrg_rec\r\n" +
         * "                from mw_loan_app ap\r\n" +
         * "                join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "                join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
         * + "                left outer join (\r\n" +
         * "                    select rdtl.pymt_sched_dtl_seq , pymt_dt ,\r\n" +
         * "                    (case when CHRG_TYP_KEY = -1 then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) pr_rec,\r\n"
         * +
         * "                    (case when CHRG_TYP_KEY in (416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) sc_rec,\r\n"
         * +
         * "                    (case when CHRG_TYP_KEY not in (-1,416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) chrg_rec\r\n"
         * + "                     from mw_pymt_sched_dtl psd\r\n" +
         * "                     join mw_rcvry_dtl rdtl on rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
         * +
         * "                     join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * + "                     where psd.crnt_rec_flg=1\r\n" +
         * "                     and trx.PYMT_DT <= to_date(:asDt,'dd-MM-yyyy')\r\n" +
         * "                     group by rdtl.pymt_sched_dtl_seq,pymt_dt,CHRG_TYP_KEY) pDt on pdt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
         * + "                where ap.crnt_rec_flg=1\r\n" +
         * "                and psd.due_dt <= to_date(:asDt,'dd-MM-yyyy')\r\n" +
         * "                and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
         * +
         * "                    or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
         * + "                    and (\r\n" +
         * "                    select max(trx.pymt_dt)\r\n" +
         * "                    from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * +
         * "                    and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:asDt,'dd-MM-yyyy')\r\n"
         * + "                    )\r\n" + "                    )\r\n" +
         * "                  and ap.crnt_rec_flg =1\r\n" +
         * "                  and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
         * + "                group by ap.loan_app_seq\r\n" +
         * "                ) actl_rec\r\n" +
         * "                where shld_rec.loan_app_seq = actl_rec.loan_app_seq(+)\r\n"
         * +
         * "                and ((pr_due - pr_rec) > 0 or (sc_due - sc_rec) > 0 or (chrg_due - chrg_rec) > 0)\r\n"
         * + "            ) od_cl\r\n" +
         * "        where asts.ref_cd_seq=la.loan_app_sts\r\n" +
         * "        and asts.crnt_rec_flg=1\r\n" +
         * "        and la.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "        and prd.prd_seq=la.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "        and prd.prd_grp_seq = :prdSeq\r\n" +
         * "        and mp.brnch_seq =:brnch\r\n" + "        and mp.crnt_rec_flg=1\r\n"
         * + "        and la.LOAN_APP_SEQ = od_cl.LOAN_APP_SEQ (+)\r\n" +
         * "        and ((asts.ref_cd='0005' and to_date(la.loan_app_sts_dt) <= to_date(:asDt,'dd-MM-yyyy') and la.crnt_rec_flg=1)\r\n"
         * +
         * "        or (asts.ref_cd='0006' and to_date(la.loan_app_sts_dt) > to_date(:asDt,'dd-MM-yyyy'))\r\n"
         * +
         * "        or (asts.ref_cd='1245' and to_date(la.loan_app_sts_dt) > to_date(:asDt,'dd-MM-yyyy')))\r\n"
         * + "        and la.crnt_rec_flg =1\r\n" +
         * "        and loan_app_ost(la.loan_app_seq,to_date(:asDt,'dd-MM-yyyy')) > 0\r\n"
         * + "group by \r\n" +
         * "        case when nvl(la.APRVD_LOAN_AMT,0) <= 20000 then '10,001 - 20,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 20001 and 30000 then '20,001 - 30,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 30001 and 40000 then '30,001 - 40,000'\r\n"
         * +
         * "             when nvl(la.APRVD_LOAN_AMT,0) between 40001 and 50000 then '40,001 - 50,000'\r\n"
         * + "         else 'More than 50,000' \r\n" + "         end" ).setParameter(
         * "prdSeq", prdSeq ).setParameter( "brnch", brnch ) .setParameter( "asDt", asDt
         * );
         */
        List<Object[]> rangResult = rest.getResultList();
        List<Map<String, ?>> rangList = new ArrayList();
        rangResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("LOAN_SIZE", w[0] == null ? "" : w[0].toString());
            parm.put("ACTV_LNS", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("BSBMT_AMT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("OST_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("OD_AMT_CL", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("OD_LOANS", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("OST_PAR_AMT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("OD_LOANS_30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("OST_PAR_AMT_30", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("OD_DYS", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            rangList.add(parm);
        });
        params.put("rang_dataset", getJRDataSource(rangList));
        String cycleQry;
        cycleQry = readFile(Charset.defaultCharset(), "PortfolioConcentrationrangQry.txt");

        Query rs = em.createNativeQuery(bussQry).setParameter("prdSeq", prdSeq).setParameter("brnch", brnch)
                .setParameter("adt", asDt);

        /*
         * Query cycleQry = em.createNativeQuery( "select \r\n" +
         * "la.loan_cycl_num,\r\n" + "count(la.loan_app_seq) actv_loans ,\r\n" +
         * "sum(nvl(la.aprvd_loan_amt,0)) loan_amt,\r\n" +
         * "sum(nvl(loan_app_ost(la.loan_app_seq,to_date(:adt,'dd-mm-yyyy')),0)) ost_amt,\r\n"
         * + "sum(nvl((nvl(od_cl.pr_od,0) + nvl(od_cl.sc_od,0)),0)) od_amt_cl,\r\n" +
         * "sum(nvl(od_cl.loan_app_seq,0)) od_loans,\r\n" +
         * "sum(nvl((case when od_cl.loan_app_seq > 0 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:adt,'dd-mm-yyyy')),0) end),0)) ost_par_amt,\r\n"
         * +
         * "sum(nvl((case when nvl(od_cl.od_dys,0) >= 30 then od_cl.loan_app_seq end),0)) od_loans_30,\r\n"
         * +
         * "sum(nvl((case when nvl(od_cl.od_dys,0) >= 30 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:adt,'dd-mm-yyyy')),0) end),0)) ost_par_amt_30,\r\n"
         * + "sum(nvl(od_cl.od_dys,0)) od_dys \r\n" + "from mw_loan_app la, \r\n" +
         * "     mw_port mp,\r\n" + "     mw_prd prd,\r\n" + "mw_ref_cd_val asts, \r\n"
         * + "(\r\n" + "select shld_rec.loan_app_seq,\r\n" +
         * "pr_due - pr_rec pr_od,\r\n" + "sc_due - sc_rec sc_od,\r\n" +
         * "chrg_due - chrg_rec chrg_od,\r\n" +
         * "nvl(to_date(:adt,'dd-mm-yyyy') - od_dt,0) od_dys\r\n" + "from\r\n" + "(\r\n"
         * +
         * "select ap.loan_app_seq, nvl(sum(psd.ppal_amt_due),0) pr_due, nvl(sum(tot_chrg_due),0) sc_due,\r\n"
         * +
         * "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and crnt_rec_flg=1)),0) chrg_due,\r\n"
         * + "max(inst_num) inst_num, min(psd.due_dt) od_dt\r\n" +
         * "from mw_loan_app ap\r\n" +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
         * + "and psd.due_dt <= to_date(:adt,'dd-mm-yyyy')\r\n" +
         * "and (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1)\r\n"
         * +
         * "or (psd.pymt_sts_key = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1)\r\n"
         * + "and (\r\n" + "select max(trx.pymt_dt)\r\n" +
         * "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * +
         * "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > to_date(:adt,'dd-mm-yyyy')\r\n"
         * + ")\r\n" + ")\r\n" + "and ap.crnt_rec_flg =1\r\n" +
         * "and ap.loan_app_sts not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and ref_cd_grp_key = 106 and val.crnt_rec_flg=1)\r\n"
         * + "group by ap.loan_app_seq\r\n" + ")shld_rec,\r\n" + "(\r\n" +
         * "select ap.loan_app_seq,\r\n" + "sum(nvl(pr_rec,0)) pr_rec,\r\n" +
         * "sum(nvl(sc_rec,0)) sc_rec,\r\n" + "sum(nvl(chrg_rec,0)) chrg_rec\r\n" +
         * "from mw_loan_app ap\r\n" +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * +
         * "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
         * +
         * "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
         * + "left outer join (\r\n" + "select rdtl.pymt_sched_dtl_seq , pymt_dt ,\r\n"
         * +
         * "(case when chrg_typ_key = -1 then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) pr_rec,\r\n"
         * +
         * "(case when chrg_typ_key in (416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) sc_rec,\r\n"
         * +
         * "(case when chrg_typ_key not in (-1,416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) chrg_rec\r\n"
         * + "from mw_pymt_sched_dtl psd\r\n" +
         * "join mw_rcvry_dtl rdtl on rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
         * +
         * "join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * + "where psd.crnt_rec_flg=1\r\n" +
         * "and trx.pymt_dt <= to_date(:adt,'dd-mm-yyyy')\r\n" +
         * "group by rdtl.pymt_sched_dtl_seq,pymt_dt,chrg_typ_key) pdt on pdt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
         * + "where ap.crnt_rec_flg=1\r\n" +
         * "and psd.due_dt <= to_date(:adt,'dd-mm-yyyy')\r\n" +
         * "and (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1)\r\n"
         * +
         * "or (psd.pymt_sts_key = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1)\r\n"
         * + "and (\r\n" + "select max(trx.pymt_dt)\r\n" +
         * "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
         * +
         * "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > to_date(:adt,'dd-mm-yyyy')\r\n"
         * + ")\r\n" + ")\r\n" + "and ap.crnt_rec_flg =1\r\n" +
         * "and ap.loan_app_sts not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and ref_cd_grp_key = 106 and val.crnt_rec_flg=1)\r\n"
         * + "group by ap.loan_app_seq\r\n" + ") actl_rec\r\n" +
         * "where shld_rec.loan_app_seq = actl_rec.loan_app_seq(+)\r\n" +
         * "and ((pr_due - pr_rec) > 0 or (sc_due - sc_rec) > 0 or (chrg_due - chrg_rec) > 0)\r\n"
         * + ") od_cl\r\n" + "where asts.ref_cd_seq=la.loan_app_sts\r\n" +
         * "and asts.crnt_rec_flg=1\r\n" + "and la.port_seq = mp.port_seq\r\n" +
         * "and prd.prd_seq=la.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "and prd.prd_grp_seq = :prdSeq\r\n" + "and mp.brnch_seq = :brnch\r\n" +
         * "and mp.crnt_rec_flg=1\r\n" +
         * "and la.loan_app_seq = od_cl.loan_app_seq (+)\r\n" +
         * "and ((asts.ref_cd='0005' and to_date(la.loan_app_sts_dt) <= to_date(:adt,'dd-mm-yyyy') and la.crnt_rec_flg=1) \r\n"
         * +
         * "or (asts.ref_cd='0006' and to_date(la.loan_app_sts_dt) > to_date(:adt,'dd-mm-yyyy'))\r\n"
         * +
         * "or (asts.ref_cd='1245' and to_date(la.loan_app_sts_dt) > to_date(:adt,'dd-mm-yyyy')))\r\n"
         * + "and la.crnt_rec_flg =1\r\n" +
         * "and loan_app_ost(la.loan_app_seq,to_date(:adt,'dd-mm-yyyy')) > 0\r\n" +
         * "group by \r\n" + "         la.loan_cycl_num order by la.loan_cycl_num"
         * ).setParameter( "prdSeq", prdSeq ).setParameter( "brnch", brnch )
         * .setParameter( "adt", asDt );
         */
        List<Object[]> cycleResult = rs.getResultList();
        List<Map<String, ?>> cycleList = new ArrayList();
        cycleResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("LOAN_CYCL_NUM", w[0] == null ? "" : w[0].toString());
            parm.put("ACTV_LOANS", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("LOAN_AMT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("OST_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("OD_AMT_CL", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("OD_LOANS", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("OST_PAR_AMT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("OD_LOANS_30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("OST_PAR_AMT_30", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("OD_DYS", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            cycleList.add(parm);
        });
        params.put("cycle_dataset", getJRDataSource(cycleList));
        return reportComponent.generateReport(PORTFOLIO_CONCENTRATION, params, null);
    }

    public byte[] getPendingClientsReport(int typ, long prdSeq, long brnch, String asDt, String userId, long portSeq)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("asDt", asDt);

        Query prdQ = em
                .createNativeQuery(
                        "select PRD_GRP_NM from mw_prd_grp grp where grp.PRD_GRP_SEQ=:prdSeq and grp.crnt_rec_flg=1")
                .setParameter("prdSeq", prdSeq);
        String prd = (String) prdQ.getSingleResult();

        params.put("prd", prd);
        params.put("typ", typ);

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(asDt));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        c.add(Calendar.DAY_OF_MONTH, -30);

        String fromDt = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
        String toDt = asDt;

        if (typ == 1) {
            toDt = fromDt;
            c.add(Calendar.DAY_OF_MONTH, -335);
            fromDt = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
        }

        String bussQry;
        bussQry = readFile(Charset.defaultCharset(), "PendingClientsReportbussQry.txt");

        Query rs = em.createNativeQuery(bussQry).setParameter("prd", prdSeq).setParameter("brnch", brnch)
                .setParameter("asDt", asDt).setParameter("fromDt", fromDt).setParameter("toDt", toDt).setParameter("P_PORT_SEQ", portSeq);

        /*
         * Query bussQry = em.createNativeQuery( "select mp.port_nm,\r\n" +
         * "get_port_bdo(la.port_seq) bdo_name,\r\n" + "clnt.clnt_id, \r\n" +
         * "clnt.frst_nm||' '||clnt.last_nm clnt_nm, fthr_frst_nm ||' '||fthr_last_nm fthr_nm, \r\n"
         * + "spz_frst_nm ||' '||spz_last_nm spz_nm, clnt.ph_num, \r\n" +
         * "(select ref.ref_cd_dscr from mw_biz_aprsl aprsl join mw_ref_cd_val ref on ref.ref_cd_seq=aprsl.prsn_run_the_biz and ref.crnt_rec_flg=1 where aprsl.crnt_rec_flg=1 and aprsl.loan_app_seq=la.loan_app_seq) loanuser, \r\n"
         * +
         * "adr.hse_num ||' '||adr.strt ||' '||adr.oth_dtl||' '||city_nm addr, loan_cycl_num, \r\n"
         * + "la.aprvd_loan_amt dsbmt_amt, \r\n" +
         * "LST_LOAN_CMPLTN_DT(la.loan_app_seq) cmp_dt, \r\n" +
         * "get_pd_od_inst(la.loan_app_seq) od_inst, \r\n" +
         * "la.LOAN_APP_SEQ loans,\r\n" +
         * "to_date(:asDt,'dd-mm-yyyy') - TO_DATE (la.LOAN_APP_STS_DT) dys \r\n" +
         * "from mw_loan_app la\r\n" +
         * "join MW_PORT MP on mp.port_seq=la.port_seq and mp.crnt_rec_flg=1\r\n" +
         * "join mw_prd mpg on mpg.prd_seq=la.prd_seq and mpg.crnt_rec_flg=1\r\n" +
         * "join mw_clnt clnt on clnt.clnt_seq=la.clnt_seq and clnt.crnt_rec_flg=1\r\n"
         * +
         * "join mw_addr_rel adrl on adrl.enty_key=clnt.clnt_seq and adrl.enty_typ='Client' and adrl.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_addr adr on adr.addr_seq=adrl.addr_seq and adr.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_city_uc_rel ucrl on ucrl.city_uc_rel_seq=adr.city_seq and ucrl.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_city cty on cty.city_seq=ucrl.city_seq and cty.crnt_rec_flg=1 \r\n"
         * + "WHERE MP.CRNT_REC_FLG = 1\r\n" + "AND MP.BRNCH_SEQ = :brnch\r\n" +
         * "and not exists (select * from MW_DSBMT_VCHR_HDR dvh, mw_loan_app ap where ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n"
         * +
         * "and ap.CLNT_SEQ = la.CLNT_SEQ and ap.PRD_SEQ = la.PRD_SEQ and ap.CRNT_REC_FLG = 1\r\n"
         * +
         * "and dvh.crnt_rec_flg=1 and to_date(dvh.DSBMT_DT) > TO_DATE (:fromDt,'dd-mm-yyyy'))\r\n"
         * + "and mpg.CRNT_REC_FLG = 1 \r\n" + "and la.LOAN_APP_STS = 704\r\n" +
         * "AND mpg.prd_grp_seq=:prd\r\n" +
         * "AND TO_DATE (la.LOAN_APP_STS_DT) BETWEEN TO_DATE (:fromDt,'dd-mm-yyyy') AND TO_DATE (to_date(:toDt,'dd-mm-yyyy')) order by bdo_name,dys desc"
         * ) .setParameter( "prd", prdSeq ).setParameter( "brnch", brnch ).setParameter(
         * "asDt", asDt ).setParameter( "fromDt", fromDt ) .setParameter( "toDt", toDt
         * );
         */
        List<Object[]> bussResult = rs.getResultList();
        List<Map<String, ?>> bussList = new ArrayList();
        bussResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("PORT_NM", w[0] == null ? "" : w[0].toString());
            parm.put("BDO", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_ID", w[2] == null ? "" : w[2].toString());
            parm.put("CLNT_NM", w[3] == null ? "" : w[3].toString());
            parm.put("FTHR_NM", w[4] == null ? "" : w[4].toString());
            parm.put("SPZ_NM", w[5] == null ? "" : w[5].toString());
            parm.put("PH_NUM", w[6] == null ? "" : w[6].toString());
            parm.put("LOANUSER", w[7] == null ? "" : w[7].toString());
            parm.put("ADDR", w[8] == null ? "" : w[8].toString());
            parm.put("LOAN_CYCL_NUM", w[9] == null ? "" : w[9].toString());
            parm.put("DSBMT_AMT", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("CMP_DT", w[11] == null ? "" : getFormaedDate(w[11].toString()));
            parm.put("OD_INST", w[12] == null ? "" : w[12].toString());
            parm.put("PND_DAYS", w[14] == null ? "" : w[14].toString());

            bussList.add(parm);
        });
        return reportComponent.generateReport(PENDINGCLIENTS, params, getJRDataSource(bussList));
    }

    public byte[] getTaggedClientCliamReport(long prdSeq, long brnch, String asDt, String userId) throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("asDt", asDt);

        Query prdQ = em
                .createNativeQuery("select prd_cmnt \r\n" + "from mw_prd p\r\n"
                        + "join mw_prd_grp pg on pg.prd_grp_seq = p.prd_grp_seq and pg.crnt_rec_flg = 1 \r\n"
                        + "where pg.prd_grp_seq=:prdSeq and p.crnt_rec_flg=1\r\n" + "group by prd_cmnt")
                .setParameter("prdSeq", prdSeq);
        String prd = (String) prdQ.getSingleResult();

        params.put("prd", prd);
        String bussQry;

        bussQry = readFile(Charset.defaultCharset(), "TAGGED_CLIENT_CLAIM.txt");

        Query rs = em.createNativeQuery(bussQry).setParameter("prdSeq", prdSeq).setParameter("brnchSeq", brnch)
                .setParameter("aDt", asDt);

        /*
         * Query bussQry = em.createNativeQuery(
         * com.idev4.rds.util.Queries.TAGGED_CLIENT_CLAIM ).setParameter( "prdSeq",
         * prdSeq ) .setParameter( "brnchSeq", brnch ).setParameter( "aDt", asDt );
         */
        List<Object[]> bussResult = rs.getResultList();
        List<Map<String, ?>> bussList = new ArrayList();
        bussResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();

            parm.put("EMP_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLSNG_CLNT", w[1] == null ? "" : w[1].toString());
            parm.put("CLSNG_CLNT_NAME", w[2] == null ? "" : w[2].toString());
            parm.put("TAG_DT", w[3] == null ? "" : getFormaedDate(w[3].toString()));
            parm.put("PH_NUM", w[4] == null ? "" : w[4].toString());
            parm.put("ADDRESS", w[5] == null ? "" : w[5].toString());
            parm.put("DSBMT_DT", w[6] == null ? "" : getFormaedDate(w[6].toString()));
            parm.put("DISB_AMT", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("CLSNG_REM_INST", w[8] == null ? "" : w[8].toString());
            parm.put("CLSNG_OST_AMT", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("CLSNG_OD_REM_INST", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("CLSNG_OD_AMT", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("OD_DAYS", w[12] == null ? 0 : new BigDecimal(w[12].toString()).longValue());
            parm.put("OD_DAYS_WHEN_TAGED", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());

            bussList.add(parm);
        });
        return reportComponent.generateReport(TAGGEDCLIENTSCLAIM, params, getJRDataSource(bussList));
    }

    public byte[] getProductWiseReprotAddition(String frmDt, String toDt, long brnch, String userId)
            throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnch);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("frmDt", frmDt);
        params.put("toDt", toDt);

        String bussQry;
        bussQry = readFile(Charset.defaultCharset(), "ProductWiseReprotAdditionbussQry.txt");

        Query rs = em.createNativeQuery(bussQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("brnch", brnch);

        /*
         * Query bussQry = em .createNativeQuery(
         * "select to_char(hdr.dsbmt_dt,'YY-Mon') months,\r\n" +
         * "       prd.prd_cmnt,\r\n" +
         * "       count(distinct hdr.loan_app_seq) clnt_cnt,\r\n" +
         * "       sum(amt) dsbmt_amt\r\n" + "  from mw_dsbmt_vchr_hdr hdr\r\n" +
         * "  join mw_dsbmt_vchr_dtl dtl on dtl.dsbmt_hdr_seq=hdr.dsbmt_hdr_seq and dtl.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_typs ptyp on ptyp.typ_seq=dtl.pymt_typs_seq and ptyp.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_loan_app ap on ap.loan_app_seq=hdr.loan_app_seq and ap.crnt_rec_flg=1\r\n"
         * + "  join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * + "  join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "where hdr.crnt_rec_flg=1 and ptyp.typ_id='0008'\r\n" +
         * "and dsbmt_dt between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') and prt.brnch_seq=:brnch\r\n"
         * + "group by to_char(hdr.dsbmt_dt,'YY-Mon'),prd.prd_cmnt\r\n" + "order by 1,2"
         * ) .setParameter( "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter(
         * "brnch", brnch );
         */
        List<Object[]> bussResult = rs.getResultList();
        List<Map<String, ?>> bussList = new ArrayList();
        bussResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("MONTHS", w[0] == null ? "" : w[0].toString());
            parm.put("PRD_CMNT", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_CNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("DSBMT_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            bussList.add(parm);
        });

        params.put("data_cash", getJRDataSource(bussList));

        String easyPayQry;
        easyPayQry = readFile(Charset.defaultCharset(), "ProductWiseReprotAdditioneasyPayQry.txt");

        Query res = em.createNativeQuery(easyPayQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("brnch", brnch);

        /*
         * Query easyPayQry = em .createNativeQuery(
         * "select to_char(hdr.dsbmt_dt,'YY-Mon') months,\r\n" +
         * "       prd.prd_cmnt,\r\n" +
         * "       count(distinct hdr.loan_app_seq) clnt_cnt,\r\n" +
         * "       sum(amt) dsbmt_amt\r\n" + "  from mw_dsbmt_vchr_hdr hdr\r\n" +
         * "  join mw_dsbmt_vchr_dtl dtl on dtl.dsbmt_hdr_seq=hdr.dsbmt_hdr_seq and dtl.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_typs ptyp on ptyp.typ_seq=dtl.pymt_typs_seq and ptyp.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_loan_app ap on ap.loan_app_seq=hdr.loan_app_seq and ap.crnt_rec_flg=1\r\n"
         * + "  join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * + "  join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "where hdr.crnt_rec_flg=1 and ptyp.typ_id!='0008'\r\n" +
         * "and dsbmt_dt between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') and prt.brnch_seq=:brnch\r\n"
         * + "group by to_char(hdr.dsbmt_dt,'YY-Mon'),prd.prd_cmnt\r\n" + "order by 1,2"
         * ) .setParameter( "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter(
         * "brnch", brnch );
         */
        List<Object[]> easypayRes = res.getResultList();
        List<Map<String, ?>> esaypayList = new ArrayList();
        easypayRes.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("MONTHS", w[0] == null ? "" : w[0].toString());
            parm.put("PRD_CMNT", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_CNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("DSBMT_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            esaypayList.add(parm);
        });
        params.put("data_easypay", getJRDataSource(esaypayList));
        String allQry;
        allQry = readFile(Charset.defaultCharset(), "ProductWiseReprotAdditionallQry.txt");

        Query result = em.createNativeQuery(easyPayQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("brnch", brnch);
        /*
         * Query allQry = em .createNativeQuery(
         * "select to_char(hdr.dsbmt_dt,'YY-Mon') months,\r\n" +
         * "       prd.prd_cmnt,\r\n" +
         * "       count(distinct hdr.loan_app_seq) clnt_cnt,\r\n" +
         * "       sum(amt) dsbmt_amt\r\n" + "  from mw_dsbmt_vchr_hdr hdr\r\n" +
         * "  join mw_dsbmt_vchr_dtl dtl on dtl.dsbmt_hdr_seq=hdr.dsbmt_hdr_seq and dtl.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_typs ptyp on ptyp.typ_seq=dtl.pymt_typs_seq and ptyp.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_loan_app ap on ap.loan_app_seq=hdr.loan_app_seq and ap.crnt_rec_flg=1\r\n"
         * + "  join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * + "  join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "where hdr.crnt_rec_flg=1 \r\n" +
         * "and dsbmt_dt between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') and prt.brnch_seq=:brnch\r\n"
         * + "group by to_char(hdr.dsbmt_dt,'YY-Mon'),prd.prd_cmnt\r\n" + "order by 1,2"
         * ) .setParameter( "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter(
         * "brnch", brnch );
         */
        List<Object[]> allRes = result.getResultList();
        List<Map<String, ?>> allList = new ArrayList();
        allRes.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("MONTHS", w[0] == null ? "" : w[0].toString());
            parm.put("PRD_CMNT", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_CNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("DSBMT_AMT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            allList.add(parm);
        });
        params.put("data_all", getJRDataSource(allList));
        return reportComponent.generateReport(PRODUCTWISEREPORTADDITION, params, null);
    }

    public byte[] getAgencyTrackingReport(long brnchSeq, String userId) throws IOException {
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String rateOfRetObjQury;
        rateOfRetObjQury = readFile(Charset.defaultCharset(), "AgencyTrackingReportrateOfRetObjQury.txt");

        Query result = em.createNativeQuery(rateOfRetObjQury).setParameter("brnchSeq", brnchSeq);
        /*
         * Query rateOfRetObjQury = em.createNativeQuery( "select mnth dsbmt_mnth,\r\n"
         * + "mnth_ordr,\r\n" + "tgt.trgt_clnts,\r\n" +
         * "Agencies,month01,month02,month03,month04,month05,month06,month07,month08,month09,month10,month11,month12\r\n"
         * + "from\r\n" + "(\r\n" +
         * "select to_char(hdr.dsbmt_dt,'Mon-YYYY') mnth, to_char(to_date(hdr.dsbmt_dt),'YYYYMM') mnth_ordr,\r\n"
         * + "count(distinct ap.loan_app_seq) Agencies,\r\n" +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='01' then ap.loan_app_seq else null end) month01,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='02' then ap.loan_app_seq else null end) month02,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='03' then ap.loan_app_seq else null end) month03,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='04' then ap.loan_app_seq else null end) month04,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='05' then ap.loan_app_seq else null end) month05,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='06' then ap.loan_app_seq else null end) month06,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='07' then ap.loan_app_seq else null end) month07,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='08' then ap.loan_app_seq else null end) month08,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='09' then ap.loan_app_seq else null end) month09,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='10' then ap.loan_app_seq else null end) month10,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='11' then ap.loan_app_seq else null end) month11,\r\n"
         * +
         * "count(distinct case when to_char(hdr.dsbmt_dt,'MM')='12' then ap.loan_app_seq else null end) month12\r\n"
         * + "from mw_loan_app ap\r\n" +
         * "join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=ap.loan_app_seq\r\n" +
         * "join mw_port prt on prt.port_seq= ap.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:brnchSeq\r\n"
         * +
         * "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
         * + "where ap.crnt_rec_flg=1\r\n" + "and lsts.ref_cd='0005' \r\n" +
         * "and to_number(to_char(to_date(hdr.dsbmt_dt),'YYYY'))=to_number(to_char(sysdate,'YYYY'))\r\n"
         * +
         * "group by to_char(hdr.dsbmt_dt,'Mon-YYYY'), to_char(to_date(hdr.dsbmt_dt),'YYYYMM')\r\n"
         * + ") disb\r\n" + "left outer join\r\n" +
         * "( select trgt_perd,sum(trgt_clients) trgt_clnts from mw_brnch_trgt where brnch_seq=:brnchSeq and trgt_yr=to_char(sysdate,'YYYY') group by  trgt_perd) tgt on tgt.trgt_perd=disb.mnth_ordr"
         * ) .setParameter( "brnchSeq", brnchSeq );
         */
        List<Object[]> rateOfRetObj = result.getResultList();
        List<Map<String, ?>> rateOfRetObjList = new ArrayList();
        int c = 1;

        DecimalFormat df = new DecimalFormat("#.##");
        for (Object[] w : rateOfRetObj) {
            params.put("MONTH" + (c < 10 ? "0" : "") + c, w[0].toString());
            Map<String, Object> parm = new HashMap<>();
            parm.put("CMPLTD_MNTH", w[0].toString());
            parm.put("MNTH_ORDR", w[1].toString());
            parm.put("TRGT_CLNTS", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("AGENCIES", new BigDecimal(w[3].toString()).longValue());
            parm.put("MONTH01", new BigDecimal(w[4].toString()).longValue());
            parm.put("MONTH02", new BigDecimal(w[5].toString()).longValue());
            parm.put("MONTH03", new BigDecimal(w[6].toString()).longValue());
            parm.put("MONTH04", new BigDecimal(w[7].toString()).longValue());
            parm.put("MONTH05", new BigDecimal(w[8].toString()).longValue());
            parm.put("MONTH06", new BigDecimal(w[9].toString()).longValue());
            parm.put("MONTH07", new BigDecimal(w[10].toString()).longValue());
            parm.put("MONTH08", new BigDecimal(w[11].toString()).longValue());
            parm.put("MONTH09", new BigDecimal(w[12].toString()).longValue());
            parm.put("MONTH10", new BigDecimal(w[13].toString()).longValue());
            parm.put("MONTH11", new BigDecimal(w[14].toString()).longValue());
            parm.put("MONTH12", new BigDecimal(w[15].toString()).longValue());
            long achivTtl = new BigDecimal(w[15].toString()).longValue() + new BigDecimal(w[4].toString()).longValue()
                    + new BigDecimal(w[5].toString()).longValue() + new BigDecimal(w[6].toString()).longValue()
                    + new BigDecimal(w[7].toString()).longValue() + new BigDecimal(w[8].toString()).longValue()
                    + new BigDecimal(w[9].toString()).longValue() + new BigDecimal(w[10].toString()).longValue()
                    + new BigDecimal(w[11].toString()).longValue() + new BigDecimal(w[12].toString()).longValue()
                    + new BigDecimal(w[13].toString()).longValue() + new BigDecimal(w[14].toString()).longValue();

            double achvRat = (achivTtl == 0 || w[2] == null) ? 0
                    : ((double) achivTtl / new BigDecimal(w[2].toString()).longValue()) * 100;
            parm.put("ACHIV_TTL", achivTtl);
            parm.put("ACHIV_PER", df.format(achvRat));
            parm.put("VARI", (w[2] == null ? 0L : new BigDecimal(w[2].toString()).longValue()) - achivTtl);
            rateOfRetObjList.add(parm);
            c++;
        }

        return reportComponent.generateReport(AGENCIES_TRAGET_TRACKING, params, getJRDataSource(rateOfRetObjList));
    }

    public byte[] getTransferredClients(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("frm_dt", frmDt);
        params.put("curr_user", userId);
        params.put("to_dt", toDt);

        String rateOfRetObjQury;
        rateOfRetObjQury = readFile(Charset.defaultCharset(), "TRANSFERRED_CLIENTS.txt");

        Query result = em.createNativeQuery(rateOfRetObjQury).setParameter("frmDt", frmDt).setParameter("toDt", toDt);

        // Query bussQry = em.createNativeQuery(
        // com.idev4.rds.util.Queries.TRANSFERRED_CLIENTS ).setParameter( "frmDt", frmDt
        // )
        // .setParameter( "toDt", toDt );
        List<Object[]> bussResult = result.getResultList();
        List<Map<String, ?>> bussList = new ArrayList();
        bussResult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("PRD_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_ID", w[1] == null ? "" : w[1].toString());
            parm.put("LOAN_APP_SEQ", w[2] == null ? "" : w[2].toString());
            parm.put("CLNT_NM", w[3] == null ? "" : w[3].toString());
            parm.put("FRM_BDO", w[4] == null ? "" : w[4].toString());
            parm.put("TO_BDO", w[5] == null ? "" : w[5].toString());
            parm.put("FRM_BRNCH", w[6] == null ? "" : w[6].toString());
            parm.put("TO_BRNCH", w[7] == null ? "" : w[7].toString());
            parm.put("LOAN_APP_STS_DT", w[8] == null ? "" : getFormaedDate(w[8].toString()));
            parm.put("APRVD_LOAN_AMT", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("INST_NUM", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("OST_PR", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("OST_SC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).longValue());
            parm.put("KSZB_OST", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("DOC_FEE_OST", w[14] == null ? 0 : new BigDecimal(w[14].toString()).longValue());
            parm.put("TRN_OST", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("INS_OST", w[16] == null ? 0 : new BigDecimal(w[16].toString()).longValue());
            parm.put("EX_RCVRY", w[17] == null ? 0 : new BigDecimal(w[17].toString()).longValue());
            bussList.add(parm);
        });
        return reportComponent.generateReport(TRANSFERRED_CLIENTS, params, getJRDataSource(bussList));
    }

    public byte[] getTurnAroundTimeReport(String frmDt, String toDt, String userId, long rpt_flg, long areaSeq, long regSeq,
                                          long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);
        String rateOfRetObjQury;
        rateOfRetObjQury = readFile(Charset.defaultCharset(), "TurnAroundTimeReportbdoQry.txt");

        Query result = em.createNativeQuery(rateOfRetObjQury).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt);

        List<Object[]> bdoObj = result.getResultList();

        List<Map<String, ?>> bdos = new ArrayList();
        bdoObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("DAYS_30", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("DAYS_1_7", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("DAYS_8_15", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("DAYS_16_22", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("DAYS_23_30", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            bdos.add(map);
        });
        params.put("bdos", getJRDataSource(bdos));

        String prdQry;
        prdQry = readFile(Charset.defaultCharset(), "TurnAroundTimeReportprdQry.txt");

        Query res = em.createNativeQuery(prdQry).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt);

        List<Object[]> prdObj = res.getResultList();

        List<Map<String, ?>> prds = new ArrayList();
        prdObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRD_GRP_NM", l[0] == null ? "" : l[0].toString());
            map.put("DAYS_30", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("DAYS_1_7", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("DAYS_8_15", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("DAYS_16_22", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("DAYS_23_30", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            prds.add(map);
        });
        params.put("prds", getJRDataSource(prds));
        return reportComponent.generateReport(TURNAROUNDTIME, params, null);
    }

    public byte[] getFemaleParticipationReport(String toDt, String userId, long rpt_flg, long areaSeq, long regSeq,
                                               long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
//        params.put( "from_dt", frmDt );
        params.put("to_dt", toDt);

        String allQry;
        allQry = readFile(Charset.defaultCharset(), "FemaleParticipationReportallQry.txt");

        Query res = em.createNativeQuery(allQry).setParameter("todt", toDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> allObj = res.getResultList();

        List<Map<String, ?>> all = new ArrayList();
        allObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("FEMALE", l[1] == null ? "" : new BigDecimal(l[1].toString()).longValue());
            map.put("MALE", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("JOINT_USER", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            all.add(map);
        });

        params.put("all", getJRDataSource(all));

        String loanQry;
        loanQry = readFile(Charset.defaultCharset(), "FemaleParticipationReportloanQry.txt");

        Query rs = em.createNativeQuery(loanQry).setParameter("todt", toDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> loanObj = rs.getResultList();

        List<Map<String, ?>> loans = new ArrayList();
        loanObj.forEach(l -> {
            Map<String, Object> map = new HashMap();

            map.put("MALE_NEW", l[0] == null ? 0 : new BigDecimal(l[0].toString()).longValue());
            map.put("FEMALE_NEW", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("JOINT_USER_NEW", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("MALE_REP", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("FEMALE_REP", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("JOINT_USER_REP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());

            loans.add(map);
        });

        params.put("loans", getJRDataSource(loans));

        String prdQry;
        prdQry = readFile(Charset.defaultCharset(), "FemaleParticipationReportprdQry.txt");

        Query result = em.createNativeQuery(prdQry).setParameter("todt", toDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> prdObj = result.getResultList();

        List<Map<String, ?>> prds = new ArrayList();
        prdObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRD_GRP_NM", l[0] == null ? "" : l[0].toString());
            map.put("FEMALE", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("MALE", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("JOINT_USER", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            prds.add(map);
        });

        params.put("prds", getJRDataSource(prds));

        return reportComponent.generateReport(FEMALEPARTICIPATION, params, null);
    }

    /**
     * @Update: Naveed
     * @SCR: Rescheduled Branch Reports (Operation)
     * @Date: 02-11-2022
     */
    public byte[] getDuesReport(String frmDt, String toDt, String userId, long branch, String type) throws IOException {
        Map<String, Object> params = new HashMap<>();

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branch);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);
        params.put("type", type);

        String detailQry;
        detailQry = readFile(Charset.defaultCharset(), "DuesReportdetailQry.txt");

        Query result = em.createNativeQuery(detailQry).setParameter("brnch_seq", branch).setParameter("frm_dt", frmDt)
                .setParameter("to_dt", toDt).setParameter("P_TYPE", type);

        /*
         * Query detailQry = em.createNativeQuery(
         * "select emp.emp_seq, emp.emp_nm,c.clnt_id,c.frst_nm ||' '||c.last_nm name,\r\n"
         * +
         * "               (select frst_nm||' '||last_nm from mw_clnt_rel where loan_app_seq=app.loan_app_seq and crnt_rec_flg=1 and rel_typ_flg=1) nom_nm,\r\n"
         * +
         * "               c.ph_num,'St. '||ad.strt||' H. '||ad.hse_num||' '||ad.oth_dtl||', '||city.city_nm addr,\r\n"
         * + "               psd.inst_num,loan_cycl_num,prd.prd_cmnt,\r\n" +
         * "               psd.ppal_amt_due+nvl(tot_chrg_due,0)+\r\n" +
         * "               nvl((select sum(amt) from mw_pymt_sched_chrg pdtl where pdtl.crnt_rec_flg=1 and pdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq),0) inst_amt,\r\n"
         * +
         * "               to_char(psd.due_dt,'dd-MM-yyyy') due_dt,get_od_info(app.loan_app_seq, sysdate,'i') od_inst  ,\r\n"
         * + "               get_od_info(app.loan_app_seq, sysdate,'d') od_days  ,\r\n"
         * +
         * "               case when c.slf_pdc_flg=1 then c.frst_nm||' '||c.last_nm \r\n"
         * +
         * "                       when c.co_bwr_san_flg=1 then (select frst_nm||' '||last_nm from mw_clnt_rel where loan_app_seq=app.loan_app_seq and crnt_rec_flg=1 and rel_typ_flg=1)\r\n"
         * + "                   else\r\n" +
         * "                       (select frst_nm||' '||last_nm from mw_clnt_rel where loan_app_seq=app.loan_app_seq and crnt_rec_flg=1 and rel_typ_flg=3)\r\n"
         * +
         * "               end pdc_hldr_nm,case when c.slf_pdc_flg=1 then c.ph_num\r\n"
         * +
         * "                       when c.co_bwr_san_flg=1 then (select ph_num from mw_clnt_rel where loan_app_seq=app.loan_app_seq and crnt_rec_flg=1 and rel_typ_flg=1)\r\n"
         * + "                   else\r\n" +
         * "                       (select c.ph_num from mw_clnt_rel where loan_app_seq=app.loan_app_seq and crnt_rec_flg=1 and rel_typ_flg=3)\r\n"
         * + "               end pdc_hldr_phn \r\n" +
         * "               FROM mw_loan_app app \r\n" +
         * "               join mw_prd prd on prd.prd_seq=app.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * +
         * "               join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "               JOIN mw_clnt c ON c.clnt_seq = app.clnt_seq AND c.crnt_rec_flg = 1\r\n"
         * +
         * "               JOIN mw_pymt_sched_hdr psh ON psh.loan_app_seq = app.loan_app_seq AND psh.crnt_rec_flg = 1\r\n"
         * +
         * "               join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "               JOIN mw_ref_cd_val val ON val.ref_cd_seq = app.loan_app_sts AND val.crnt_rec_flg = 1 AND val.del_flg = 0 \r\n"
         * +
         * "               join mw_port p on p.port_seq = app.port_seq and p.crnt_rec_flg = 1 \r\n"
         * +
         * "               join mw_port_emp_rel per on per.port_seq=p.port_seq and per.crnt_rec_flg=1 \r\n"
         * + "               join mw_emp emp on emp.emp_seq=per.emp_seq\r\n" +
         * "               join mw_addr_rel ar on ar.enty_key=c.clnt_seq and ar.crnt_rec_flg = 1 and ar.enty_typ='Client' \r\n"
         * +
         * "               join mw_addr ad on ad.addr_seq = ar.addr_seq and ad.crnt_rec_flg = 1 \r\n"
         * +
         * "               join mw_city_uc_rel cur on cur.city_uc_rel_seq = ad.city_seq \r\n"
         * +
         * "               join mw_city city on city.city_seq = cur.city_seq and city.crnt_rec_flg = 1  WHERE app.crnt_rec_flg=1\r\n"
         * +
         * "               and val.ref_cd = '0005'  and (psd.pymt_sts_key in (945,1145 ))\r\n"
         * +
         * "               and TO_DATE(psd.due_dt) between TO_DATE(:frm_dt,'dd-mm-yyyy') and TO_DATE(:to_dt,'dd-mm-yyyy') order by 2,12"
         * ) .setParameter( "user_id", userId ).setParameter( "frm_dt", frmDt
         * ).setParameter( "to_dt", toDt );
         */

        List<Object[]> dtlObj = result.getResultList();

        List<Map<String, ?>> pymts = new ArrayList();
        dtlObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PORT_TYP", l[0] == null ? "" : l[0].toString());
            map.put("EMP_SEQ", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("EMP_NM", l[2] == null ? "" : l[2].toString());
            map.put("CLNT_ID", l[3] == null ? "" : l[3].toString());
            map.put("NAME", l[4] == null ? "" : l[4].toString());
            map.put("NOM_NM", l[5] == null ? "" : l[5].toString());
            map.put("PH_NUM", l[6] == null ? "" : l[6].toString());
            map.put("ADDR", l[7] == null ? "" : l[7].toString());
            map.put("INST_NUM", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("LOAN_CYCL_NUM", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("PRD_CMNT", l[10] == null ? "" : l[10].toString());
            map.put("INST_AMT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("DUE_DT", l[12] == null ? "" : l[12].toString());
            map.put("OD_INST", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            map.put("OD_DAYS", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            map.put("PDC_HLDR_NM", l[15] == null ? "" : l[15].toString());
            map.put("PDC_HLDR_PHN", l[16] == null ? "" : l[16].toString());
            pymts.add(map);
        });
        return reportComponent.generateReport(DUES, params,

                getJRDataSource(pymts));
    }

    public byte[] getPortfolioSegReport(String toDt, String userId, long rpt_flg, long areaSeq, long regSeq, long brnchSeq)
            throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
        params.put("to_dt", toDt);

        String secQry;
        secQry = readFile(Charset.defaultCharset(), "portfolioSegmentationScript.txt");

        Query result = em.createNativeQuery(secQry).setParameter("todt", toDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> secObj = result.getResultList();

        List<Map<String, ?>> sectors = new ArrayList();
        secObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : l[0].toString());
            map.put("SECT", l[1] == null ? "" : l[1].toString());
            map.put("LOAN_CNT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("APRVD_LOAN_AMT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("OST_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            sectors.add(map);
        });

        return reportComponent.generateReport(PORTFOLIO_SEG, params, getJRDataSource(sectors));
    }

    public byte[] getPortfolioAtRiskReport(String asDt, String userId, long rpt_flg, long areaSeq, long regSeq, long brnchSeq)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
        params.put("as_dt", asDt);

        String secQry;
        secQry = readFile(Charset.defaultCharset(), "portfolioAtRiskScript.txt");

        Query reulSet = em.createNativeQuery(secQry).setParameter("todt", asDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> secObj = reulSet.getResultList();

        List<Map<String, ?>> details = new ArrayList();

        secObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : l[0].toString());
            map.put("PRD_GRP_NM", l[1] == null ? "" : l[1].toString());
            map.put("TOT_CLNT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("APRVD_LOAN_AMT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("OST_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("PAR_1_DY_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("PAR_1_4_DAY_CNT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("PAR_1_4_DAY_OD_AMT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("PAR_1_4_DAY_AMT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("PAR_5_15_DAY_CNT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("PAR_5_15_DAY_OD_AMT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("PAR_5_15_DAY_AMT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("PAR_16_30_DAY_CNT", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("PAR_16_30_DAY_OD_AMT", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            map.put("PAR_16_30_DAY_AMT", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            map.put("PAR_OVER_30_DAY_CNT", l[15] == null ? 0 : new BigDecimal(l[15].toString()).longValue());
            map.put("PAR_OVER_30_DAY_OD_AMT", l[16] == null ? 0 : new BigDecimal(l[16].toString()).longValue());
            map.put("PAR_OVER_30_AMT", l[17] == null ? 0 : new BigDecimal(l[17].toString()).longValue());

            details.add(map);
        });

        String parQry;
        parQry = readFile(Charset.defaultCharset(), "PortfolioAtRiskReportParQry.txt");

        Query reSet1 = em.createNativeQuery(parQry).setParameter("todt", asDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> parObj = reSet1.getResultList();

        List<Map<String, ?>> par = new ArrayList();
        parObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("BDO_NM", l[0] == null ? "" : l[0].toString());
            map.put("NAME", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_ID", l[2] == null ? "" : l[2].toString());
            map.put("PH_NUM", l[3] == null ? "" : l[3].toString());
            map.put("ADDR", l[4] == null ? "" : l[4].toString());
            map.put("LOAN_CYCL_NUM", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("PRD_GRP_NM", l[6] == null ? "" : l[6].toString());
            map.put("DIS_AMT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("OD_AMT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("OD_DAYS", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("OD_INST", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("OST_BAL", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("NOM_NM", l[12] == null ? "" : l[12].toString());
            map.put("CMP_DT", l[13] == null ? "" : l[13].toString());
            map.put("PAID_INST", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            par.add(map);
        });
        params.put("par", getJRDataSource(par));

        return reportComponent.generateReport(PORTFOLIO_AT_RISK, params, getJRDataSource(details));
    }

    public byte[] getRiskFlaggingReport(String frmDt, String toDt, String userId, long rpt_flg, long areaSeq, long regSeq, long brnchSeq)
            throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String jumpsQry;
        jumpsQry = readFile(Charset.defaultCharset(), "RiskFlaggingReportJumpsQryy.txt");

        Query reSet1 = em.createNativeQuery(jumpsQry).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt);
        List<Object[]> jumpsObj = reSet1.getResultList();

        List<Map<String, ?>> jumps = new ArrayList();
        jumpsObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("NAME", l[0] == null ? "" : l[0].toString());
            map.put("CLNT_ID", l[1] == null ? "" : l[1].toString());
            map.put("LOAN_CYCL_NUM", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("PRD_GRP_NM", l[3] == null ? "" : l[3].toString());
            map.put("APRVD_LOAN_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("LOAN_JUMP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("EMP_NM", l[6] == null ? "" : l[6].toString());
            map.put("OD_INST", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            jumps.add(map);
        });

        params.put("jumps", getJRDataSource(jumps));

        String disbQry;
        disbQry = readFile(Charset.defaultCharset(), "RiskFlaggingReportDisbQry.txt");

        Query rs = em.createNativeQuery(disbQry).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt);
        List<Object[]> disbObj = rs.getResultList();

        List<Map<String, ?>> disb = new ArrayList();
        disbObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("NAME", l[0] == null ? "" : l[0].toString());
            map.put("CLNT_ID", l[1] == null ? "" : l[1].toString());
            map.put("LOAN_CYCL_NUM", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("PRD_GRP_NM", l[3] == null ? "" : l[3].toString());
            map.put("APRVD_LOAN_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("LOAN_JUMP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("EMP_NM", l[6] == null ? "" : l[6].toString());
            map.put("OD_INST", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            disb.add(map);
        });
        params.put("disb", getJRDataSource(disb));

        String mfisQry;
        mfisQry = readFile(Charset.defaultCharset(), "RiskFlaggingReportmfisQry.txt");

        Query rset = em.createNativeQuery(mfisQry).setParameter("user_id", userId).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt).setParameter("rpt_flg", rpt_flg).setParameter("areaSeq", areaSeq)
                .setParameter("regSeq", regSeq).setParameter("brnch_seq", brnchSeq);
        List<Object[]> mfisObj = rset.getResultList();

        List<Map<String, ?>> mfis = new ArrayList();
        mfisObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("NAME", l[0] == null ? "" : l[0].toString());
            map.put("CLNT_ID", l[1] == null ? "" : l[1].toString());
            map.put("LOAN_CYCL_NUM", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("PRD_CMNT", l[3] == null ? "" : l[3].toString());
            map.put("APRVD_LOAN_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("LOAN_JUMP", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("EMP_NM", l[6] == null ? "" : l[6].toString());
            map.put("OD_INST", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("OTH_LOANS", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            mfis.add(map);
        });
        params.put("mfis", getJRDataSource(mfis));

        String rentQry;
        rentQry = readFile(Charset.defaultCharset(), "RiskFlaggingReportRentQry.txt");

        Query resset = em.createNativeQuery(rentQry).setParameter("brnch_seq", brnchSeq).setParameter("todt", toDt);
        List<Object[]> rentObj = resset.getResultList();

        List<Map<String, ?>> rented = new ArrayList();
        rentObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REF_CD_DSCR", l[0] == null ? "" : l[0].toString());
            map.put("LOANS", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            rented.add(map);
        });


        String jum30pieScript;
        jum30pieScript = readFile(Charset.defaultCharset(), "RiskFlaggingReportJump30Qry.txt");
        Query jum30 = em.createNativeQuery(jum30pieScript).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt).setParameter("todt", toDt);
        List<Object[]> jumobj = jum30.getResultList();

        List<Map<String, ?>> jum30param = new ArrayList();
        jumobj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSCR", l[0] == null ? "" : l[0].toString());
            map.put("LOANS_30", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            jum30param.add(map);
        });

        params.put("jum30pie", getJRDataSource(jum30param));


        String jum80pieScript;
        jum80pieScript = readFile(Charset.defaultCharset(), "RiskFlaggingReport80Qry.txt");
        Query jum80 = em.createNativeQuery(jum80pieScript).setParameter("brnch_seq", brnchSeq).setParameter("frmdt", frmDt).setParameter("todt", toDt);
        List<Object[]> jum80obj = jum80.getResultList();

        List<Map<String, ?>> jum80param = new ArrayList();
        jum80obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSCR", l[0] == null ? "" : l[0].toString());
            map.put("LOANS_80", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            jum80param.add(map);
        });

        params.put("jum80pie", getJRDataSource(jum80param));


        return reportComponent.generateReport(RISKFLAG, params, getJRDataSource(rented));
    }

    public byte[] getPortfolioStatusActiveReport(String toDt, String userId, long rpt_flg, long areaSeq, long regSeq,
                                                 long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
        params.put("to_dt", toDt);

        String activePortObjQry;
        activePortObjQry = readFile(Charset.defaultCharset(), "portfolioStatusScript.txt");

        Query resSet = em.createNativeQuery(activePortObjQry).setParameter("brnchSeq", brnchSeq).setParameter("to_dt", toDt);
        List<Object[]> activePortObj = resSet.getResultList();

        List<Map<String, ?>> activePorts = new ArrayList();
        activePortObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : l[0].toString());
            map.put("PRD_GRP_NM", l[1] == null ? "" : l[1].toString());
            map.put("TOT_CLNT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("TOT_CLNT_AMT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("NEW_CLNT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("NEW_CLNT_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("RPT_CLNT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("RPT_CLNT_AMT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("OST_AMT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("PAR_1_DY_AMT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("PAR_1__29_DAY_AMT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("PAR_OVER_30_AMT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("OD_CLNTS", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("OD_CLNTS_30", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            map.put("RNW_CLNT_MONTH", l[14] == null ? 0 : new BigDecimal(l[14].toString()).longValue());
            map.put("CMPLTD_MNTH", l[15] == null ? 0 : new BigDecimal(l[15].toString()).longValue());
            map.put("REP_MNTH", l[16] == null ? 0 : new BigDecimal(l[16].toString()).longValue());
            map.put("DSBMT_MNTH", l[17] == null ? 0 : new BigDecimal(l[17].toString()).longValue());
            map.put("FML_PRT", l[18] == null ? 0 : new BigDecimal(l[18].toString()).longValue());
            map.put("FML_TTOL", l[19] == null ? 0 : new BigDecimal(l[19].toString()).longValue());

            activePorts.add(map);
        });

        return reportComponent.generateReport(PORTFOLIO_STAT_REP, params, getJRDataSource(activePorts));
    }

    public byte[] getConsolidatedLoanReport(String frmDt, String toDt, String userId, long rpt_flg, long areaSeq, long regSeq, long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String activePortObjQry;
        activePortObjQry = readFile(Charset.defaultCharset(), "ConsolidatedLoanReportActivePortObjQry.txt");

        Query rSet = em.createNativeQuery(activePortObjQry).setParameter("brnch_seq", brnchSeq).setParameter("frm_dt", frmDt)
                .setParameter("to_dt", toDt);

        List<Object[]> activePortObj = rSet.getResultList();

        List<Map<String, ?>> activePorts = new ArrayList();
        activePortObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_SEQ", l[0] == null ? 0 : new BigDecimal(l[0].toString()).longValue());
            map.put("EMP_NM", l[1] == null ? "" : l[1].toString());
            map.put("NAME", l[2] == null ? "" : l[2].toString());
            map.put("NOM_NM", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_ID", l[4] == null ? "" : l[4].toString());
            map.put("LOAN_CYCL_NUM", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("PRD_CMNT", l[6] == null ? "" : l[6].toString());
            map.put("PH_NUM", l[7] == null ? "" : l[7].toString());
            map.put("ADDR", l[8] == null ? "" : l[8].toString());
            map.put("LST_LOAN_AMT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("CMP_DT", l[10] == null ? "" : l[10].toString());
            map.put("LOAN_APP_STS_DT", l[11] == null ? "" : l[11].toString());
            map.put("DAYS_CMP", l[12] == null ? 0 : new BigDecimal(l[12].toString()).longValue());
            map.put("OD_INST", l[13] == null ? 0 : new BigDecimal(l[13].toString()).longValue());
            map.put("TAG_STS", l[14] == null ? "" : l[14].toString());
            activePorts.add(map);
        });

        return reportComponent.generateReport(CONSOLIDATED_LOAN, params, getJRDataSource(activePorts));
    }

    public byte[] getRORReport(String asDt, String userId, long rpt_flg, long areaSeq, long regSeq, long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }

        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date date = new Date();
        try {
            date = inputFormat.parse(asDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int asOfDateMonth = cal.get(Calendar.MONTH) + 1;
        int asOfDateYear = cal.get(Calendar.YEAR);

        if ((asOfDateMonth >= currentMonth) && (asOfDateYear >= currentYear)) {
            // Convert Date to Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            asDt = inputFormat.format(calendar.getTime());
        }

        params.put("curr_user", userId);
        params.put("as_dt", asDt);

        String graphQry;
        graphQry = readFile(Charset.defaultCharset(), "RORReportGraphQry.txt");

        Query rSet = em.createNativeQuery(graphQry).setParameter("toDt", asDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> graphObj = rSet.getResultList();

        List<Map<String, ?>> graph = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : new BigDecimal(l[0].toString()).longValue());
            map.put("ROR_MONTH", l[1] == null ? "" : l[1].toString());
            map.put("COMP", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("REP", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("ROR_PERC", l[4] == null ? 0 : new BigDecimal(l[4].toString()).doubleValue());
            graph.add(map);
        });

        String prdQry;
        prdQry = readFile(Charset.defaultCharset(), "RORReportPrdQry.txt");

        Query rs = em.createNativeQuery(prdQry).setParameter("toDt", asDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> prdObj = rs.getResultList();

        List<Map<String, ?>> prds = new ArrayList();
        prdObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : new BigDecimal(l[0].toString()).longValue());
            map.put("ROR_MONTH", l[1] == null ? "" : l[1].toString());
            map.put("PRD_GRP_NM", l[2] == null ? "" : l[2].toString());
            map.put("COMP", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("REP", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("ROR_PERC", l[5] == null ? 0 : new BigDecimal(l[5].toString()).doubleValue());
            prds.add(map);
        });
        params.put("prds", getJRDataSource(prds));

        String bdoQry;
        bdoQry = readFile(Charset.defaultCharset(), "RORReportBdoQry.txt");

        Query rs1 = em.createNativeQuery(bdoQry).setParameter("toDt", asDt).setParameter("brnch_seq", brnchSeq);

        List<Object[]> bdoObj = rs1.getResultList();

        List<Map<String, ?>> bdos = new ArrayList();
        bdoObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("ORD", l[0] == null ? "" : new BigDecimal(l[0].toString()).longValue());
            map.put("ROR_MONTH", l[1] == null ? "" : l[1].toString());
            map.put("EMP_NM", l[2] == null ? "" : l[2].toString());
            map.put("COMP", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("REP", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("ROR_PERC", l[5] == null ? 0 : new BigDecimal(l[5].toString()).doubleValue());
            bdos.add(map);
        });
        params.put("bdos", getJRDataSource(bdos));

        return reportComponent.generateReport(ROR, params, getJRDataSource(graph));
    }

    public byte[] getLoanUtilizationReport(String frmDt, String toDt, String userId, long rpt_flg, long areaSeq, long regSeq,
                                           long brnchSeq) throws IOException {
        Map<String, Object> params = new HashMap<>();

        if (brnchSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BRNCH_SEQ).setParameter("brnchSeq", brnchSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else if (areaSeq != 0) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO_BY_AREA_SEQ).setParameter("areaSeq", areaSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
        } else if (regSeq != 0) {
            Query bi = em.createNativeQuery("select rg.reg_nm,rg.reg_seq from mw_reg rg where rg.reg_seq=:regSeq and rg.crnt_rec_flg=1")
                    .setParameter("regSeq", regSeq);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
        }
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String graphQry;
        graphQry = readFile(Charset.defaultCharset(), "LoanUtilizationReportGraphQry.txt");

        Query rs1 = em.createNativeQuery(graphQry).setParameter("user_id", userId).setParameter("frm_dt", frmDt)
                .setParameter("to_dt", toDt).setParameter("brnchSeq", brnchSeq);
        List<Object[]> graphObj = rs1.getResultList();

        List<Map<String, ?>> graph = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("GRP_NM", l[0] == null ? "" : l[0].toString());
            map.put("DSCR", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_CNT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            graph.add(map);
        });

        params.put("loanuti", getJRDataSource(graph));

        String bdoQry;
        bdoQry = readFile(Charset.defaultCharset(), "LoanUtilizationReportQry.txt");

        Query rs2 = em.createNativeQuery(bdoQry).setParameter("frmdt", frmDt).setParameter("todt", toDt).setParameter("brnch_seq", brnchSeq);
        List<Object[]> bdoObj = rs2.getResultList();

        List<Map<String, ?>> bdosList = new ArrayList();
        bdoObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("CLNT_SEQ", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_NM", l[2] == null ? "" : l[2].toString());
            map.put("PH_NUM", l[3] == null ? "" : l[3].toString());
            map.put("NOMINEE_NM", l[4] == null ? "" : l[4].toString());
            map.put("NOMINEE_PH_NUM", l[5] == null ? "" : l[5].toString());
            map.put("LOAN_CYCL_NUM", l[6] == null ? "" : l[6].toString());
            map.put("PRD_GRP_NM", l[7] == null ? "" : l[7].toString());
            map.put("DAYS_MISSED", l[8] == null ? "" : l[8].toString());

            bdosList.add(map);
        });

        params.put("bdos", getJRDataSource(bdosList));

        return reportComponent.generateReport(LOAN_UTI_FORM, params, null);
    }

    public byte[] getMonthlyStatusReport(String frmDt, String toDt, String userId, long rpt_flg) throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (rpt_flg == 1) {

            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO).setParameter("userId", userId);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());

        }
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String graphQry;
        graphQry = readFile(Charset.defaultCharset(), "MonthlyStatusReportGraphQry.txt");

        Query rs1 = em.createNativeQuery(graphQry).setParameter("user_id", userId).setParameter("frm_dt", frmDt)
                .setParameter("to_dt", toDt).setParameter("rpt_flg", rpt_flg);

        /*
         * Query graphQry = em.createNativeQuery(
         * "select emp_nm,  count(distinct app.loan_app_seq) ach_clnt,\r\n" +
         * " sum(app.aprvd_loan_amt) ach_amt,\r\n" +
         * " max((select sum(trgt_clients) from mw_brnch_trgt where del_flg=0 and brnch_seq=:brnch and trgt_perd between to_char(to_date(:frm_dt,'dd-MM-yyyy'),'YYYYMM') and to_char(to_date(:to_dt,'dd-MM-yyyy'),'YYYYMM') )) trgt_clnts,\r\n"
         * +
         * " max((select sum(trgt_amt) from mw_brnch_trgt where del_flg=0 and brnch_seq=:brnch and trgt_perd between to_char(to_date(:frm_dt,'dd-MM-yyyy'),'YYYYMM') and to_char(to_date(:to_dt,'dd-MM-yyyy'),'YYYYMM') )) trgt_amt,\r\n"
         * +
         * " max((select count(distinct ap.port_seq) from mw_loan_app ap join mw_port port on port.port_seq=ap.port_seq and port.crnt_rec_flg=1 and port.brnch_seq=:brnch where ap.crnt_rec_flg=1 and ap.loan_app_sts=703)) prt_cnt,\r\n"
         * +
         * " max((select (count(distinct la.clnt_seq) / max((select count(distinct ap.clnt_seq)\r\n"
         * + " from mw_loan_app ap\r\n" +
         * " join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n" +
         * " where ap.crnt_rec_flg=1  and loan_app_sts=704\r\n" +
         * " and to_char(loan_app_sts_dt,'YYYYMM')=to_char(la.loan_app_sts_dt,'YYYYMM'))))*100 ror\r\n"
         * +
         * " from mw_loan_app la  where la.crnt_rec_flg=1  and la.port_seq=app.port_seq\r\n"
         * +
         * " and to_char(loan_app_sts_dt,'YYYYMM')=to_char(lst_loan_cmpltn_dt(la.loan_app_seq),'YYYYMM')\r\n"
         * +
         * " and la.loan_app_sts_dt between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')  )) ror,\r\n"
         * +
         * "max((  select (count(ap.clnt_seq) / max(tot_cnt))*100 fp_rt  from mw_loan_app ap\r\n"
         * +
         * " join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + " join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * " join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n" +
         * " join mw_port prt on prt.port_seq=ap.port_seq\r\n" +
         * " join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * + " join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * " join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1\r\n"
         * +
         * " join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1  \r\n"
         * + " join ( \r\n" +
         * " select ap.port_seq,count(distinct prnt_loan_app_seq) tot_cnt \r\n" +
         * " from mw_loan_app ap \r\n" +
         * " join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * +
         * " join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1 \r\n"
         * +
         * " join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1\r\n"
         * + " join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n"
         * + " where ap.crnt_rec_flg=1 and loan_app_sts=703\r\n" +
         * " and to_date(jv_dt) between add_months(to_date(:frm_dt,'dd-MM-yyyy'),-5) and to_date(:to_dt,'dd-MM-yyyy')\r\n"
         * + " group by ap.port_seq  ) ttls on ttls.port_seq=ap.port_seq\r\n" +
         * " where ap.crnt_rec_flg=1  and ap.loan_app_sts=703  and wp.ref_cd_dscr='SELF'\r\n"
         * +
         * " and to_date(jv_dt) between add_months(to_date(:frm_dt,'dd-MM-yyyy'),-5) and to_date(:to_dt,'dd-MM-yyyy')\r\n"
         * + " and ap.port_seq=app.port_seq  )) fp, max((\r\n" +
         * " select sum(ap.aprvd_loan_amt)/count(distinct clnt_seq)  from mw_loan_app ap\r\n"
         * + " join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n"
         * +
         * " join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=ap.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
         * +
         * " join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + " join mw_emp emp on emp.emp_seq=erl.emp_seq  where ap.crnt_rec_flg=1 \r\n"
         * + " and ap.port_seq=app.port_seq\r\n" +
         * " and to_date(hdr.dsbmt_dt) between to_date(:frm_dt,'dd-MM-yyyy') and to_date(:to_dt,'dd-MM-yyyy') ))ads,\r\n"
         * + " max((  select count(distinct clnt_seq) adv_mat\r\n" +
         * " from mw_loan_app ap join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id where ap.crnt_rec_flg=1 and ap.loan_app_sts=704\r\n"
         * + " and ( select max(psd.due_dt)  from mw_pymt_sched_hdr psh\r\n" +
         * " join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * + " where psh.crnt_rec_flg=1  and psh.loan_app_seq=ap.loan_app_seq\r\n" +
         * " and ap.port_seq=app.port_seq\r\n" +
         * " and to_date(ap.loan_app_sts_dt) between to_date(:frm_dt,'dd-MM-yyyy') and to_date(:to_dt,'dd-MM-yyyy'))> lst_loan_cmpltn_dt(ap.loan_app_seq)\r\n"
         * + " )) adv_mat  \r\n" + " from mw_loan_app app \r\n" +
         * "join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=app.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
         * + "join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "join mw_port_emp_rel per on per.port_seq=app.port_seq and per.crnt_rec_flg=1 \r\n"
         * + "join mw_emp emp on emp.emp_seq=per.emp_seq where app.crnt_rec_flg=1\r\n" +
         * "and to_date(hdr.dsbmt_dt) between to_date(:frm_dt,'dd-MM-yyyy') and to_date(:to_dt,'dd-MM-yyyy')\r\n"
         * + "group by emp.emp_nm" ).setParameter( "user_id", userId ).setParameter(
         * "frm_dt", frmDt ).setParameter( "to_dt", toDt ) .setParameter( "brnch", brnch
         * );
         */

        List<Object[]> graphObj = rs1.getResultList();

        List<Map<String, ?>> graph = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("ACH_CLNT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("ACH_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("TRGT_CLNTS", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("TRGT_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("ROR", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("FP", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("ADS", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("ADV_MAT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            graph.add(map);
        });
        return reportComponent.generateReport(MONTHLY_STATUS, params, getJRDataSource(graph));
    }

    public byte[] getPortfolioAtRiskTimeSerise(String asDt, String userId, long rpt_flg) throws IOException {

        Map<String, Object> params = new HashMap<>();
        if (rpt_flg == 1) {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());
            params.put("brnch_nm", obj[2].toString());
            params.put("brnch_cd", obj[3].toString());
        } else {
            Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.AREA_INFO).setParameter("userId", userId);
            Object[] obj = (Object[]) bi.getSingleResult();
            params.put("reg_nm", obj[0].toString());
            params.put("area_nm", obj[1].toString());

        }
        params.put("curr_user", userId);
        params.put("as_dt", asDt);

        String graphQry;
        graphQry = readFile(Charset.defaultCharset(), "PortfolioAtRiskTimeSeriseGraphQry.txt");

        Query rs1 = em.createNativeQuery(graphQry).setParameter("user_id", userId).setParameter("as_dt", asDt);

        /*
         * Query graphQry = em.createNativeQuery(
         * "select to_char(to_date(:as_dt,'dd-mm-yyyy'),'MON-yyyy'),\r\n" +
         * "                       sum(case when get_od_info(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'psc') > 0 then loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(to_date(:as_dt,'dd-mm-yyyy'),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= to_date(:as_dt,'dd-MM-yyyy'))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:as_dt,'dd-MM-yyyy')) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy') and to_date(dvh.dsbmt_dt) <= to_date(:as_dt,'dd-MM-yyyy'))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy')))              \r\n"
         * + "union\r\n" +
         * "select to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)))              \r\n"
         * + "union\r\n" +
         * "select to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)))               \r\n"
         * + "union\r\n" +
         * "select to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)))               \r\n"
         * + "union\r\n" +
         * "select to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)))               \r\n"
         * + "union\r\n" +
         * "select to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)))               \r\n"
         * + "order by 3" ).setParameter( "user_id", userId ).setParameter( "as_dt",
         * asDt );
         */

        List<Object[]> graphObj = rs1.getResultList();

        List<Map<String, ?>> graph = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("MNTH", l[0] == null ? "" : l[0].toString());
            map.put("PAR", l[1] == null ? 0 : new BigDecimal(l[1].toString()).floatValue());
            graph.add(map);
        });

        String prdQry;
        prdQry = readFile(Charset.defaultCharset(), "PortfolioAtRiskTimeSerisePrdQry.txt");

        Query rs = em.createNativeQuery(prdQry).setParameter("user_id", userId).setParameter("as_dt", asDt);

        /*
         * Query prdQry = em.createNativeQuery(
         * "select prd_cmnt, to_char(to_date(:as_dt,'dd-mm-yyyy'),'MON-yyyy'),\r\n" +
         * "                       sum(case when get_od_info(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'psc') > 0 then loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(to_date(:as_dt,'dd-mm-yyyy'),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= to_date(:as_dt,'dd-MM-yyyy'))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:as_dt,'dd-MM-yyyy')) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy') and to_date(dvh.dsbmt_dt) <= to_date(:as_dt,'dd-MM-yyyy'))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy')))              \r\n"
         * + "group by prd_cmnt \r\n" + "union\r\n" +
         * "select prd_cmnt, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)))              \r\n"
         * + "group by prd_cmnt \r\n" + "union\r\n" +
         * "select prd_cmnt, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)))              \r\n"
         * + "group by prd_cmnt \r\n" + "union\r\n" +
         * "select prd_cmnt, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)))              \r\n"
         * + "group by prd_cmnt \r\n" + "union\r\n" +
         * "select prd_cmnt, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)))              \r\n"
         * + "group by prd_cmnt \r\n" + "union\r\n" +
         * "select prd_cmnt, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
         * + "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)))              \r\n"
         * + "group by prd_cmnt \r\n" + "order by 4" ).setParameter( "user_id", userId
         * ).setParameter( "as_dt", asDt );
         */
        List<Object[]> prdObj = rs.getResultList();

        List<Map<String, ?>> prds = new ArrayList();
        prdObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRD_CMNT", l[0] == null ? "" : l[0].toString());
            map.put("MNTH", l[1] == null ? "" : l[1].toString());
            map.put("PAR", l[2] == null ? 0 : new BigDecimal(l[2].toString()).floatValue());
            prds.add(map);
        });
        params.put("prds", getJRDataSource(prds));

        String bdoQry;
        bdoQry = readFile(Charset.defaultCharset(), "PortfolioAtRiskTimeSeriseBdoQry.txt");
        Query resSet = em.createNativeQuery(bdoQry).setParameter("user_id", userId).setParameter("as_dt", asDt)
                .setParameter("rpt_flg", rpt_flg);

        /*
         * Query bdoQry = em.createNativeQuery(
         * "select emp_nm, to_char(to_date(:as_dt,'dd-mm-yyyy'),'MON-yyyy'),\r\n" +
         * "                       sum(case when get_od_info(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'psc') > 0 then loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,to_date(:as_dt,'dd-MM-yyyy'),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(to_date(:as_dt,'dd-mm-yyyy'),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= to_date(:as_dt,'dd-MM-yyyy'))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:as_dt,'dd-MM-yyyy')) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy') and to_date(dvh.dsbmt_dt) <= to_date(:as_dt,'dd-MM-yyyy'))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:as_dt,'dd-MM-yyyy')))              \r\n"
         * + "group by emp_nm \r\n" + "union\r\n" +
         * "select emp_nm, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-1),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-1))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-1)))              \r\n"
         * + "group by emp_nm \r\n" + "union\r\n" +
         * "select emp_nm, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-2),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-2))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-2)))              \r\n"
         * + "group by emp_nm \r\n" + "union\r\n" +
         * "select emp_nm, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-3),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-3))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-3)))              \r\n"
         * + "group by emp_nm \r\n" + "union\r\n" +
         * "select emp_nm, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-4),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-4))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-4)))              \r\n"
         * + "group by emp_nm \r\n" + "union\r\n" +
         * "select emp_nm, to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'MON-yyyy'),\r\n"
         * +
         * "                       sum(case when get_od_info(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'psc') > 0 then loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps') else 0 end)/\r\n"
         * +
         * "                       sum(loan_app_ost(ap.loan_app_seq,add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'ps')) * 100 ost_amt,\r\n"
         * +
         * "                       to_char(add_months(to_date(:as_dt,'dd-mm-yyyy'),-5),'yyyymm') ost\r\n"
         * + "            from mw_loan_app ap \r\n" +
         * "            join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id  \r\n"
         * +
         * "            join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=ap.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "            join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "            join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "            where ap.crnt_rec_flg=1\r\n" +
         * "            and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))\r\n"
         * +
         * "            and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)) \r\n"
         * +
         * "              or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5) and to_date(dvh.dsbmt_dt) <= add_months(to_date(:as_dt,'dd-mm-yyyy'),-5))                                             \r\n"
         * +
         * "              or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > add_months(to_date(:as_dt,'dd-mm-yyyy'),-5)))              \r\n"
         * + "group by emp_nm \r\n" + "order by 4" ).setParameter( "user_id", userId
         * ).setParameter( "as_dt", asDt );
         */

        List<Object[]> bdoObj = resSet.getResultList();

        List<Map<String, ?>> bdos = new ArrayList();
        bdoObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("MNTH", l[1] == null ? "" : l[1].toString());
            map.put("PAR", l[2] == null ? 0 : new BigDecimal(l[2].toString()).floatValue());
            bdos.add(map);
        });
        params.put("bdos", getJRDataSource(bdos));

        return reportComponent.generateReport(PORTFOLIO_RISK, params, getJRDataSource(graph));
    }

    public byte[] getFemaleParticipationRatio(String asDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("as_dt", asDt);

        String graphQry;
        graphQry = readFile(Charset.defaultCharset(), "FemaleParticipationRatioGraphQry.txt");
        Query resSet = em.createNativeQuery(graphQry).setParameter("user_id", userId).setParameter("as_dt", asDt);

        /*
         * Query graphQry = em.createNativeQuery(
         * "select mnth,month,round(count(distinct tot_usr)/tot_cnt*100,2) total ,round(count(distinct new_clnts)/tot_cnt*100,2) new,round(count(distinct rpt_clnts)/tot_cnt*100,2) repeat\r\n"
         * + "from (\r\n" +
         * "select to_char(jv_dt,'YYYYMM') mnth,to_char(jv_dt,'Mon-YYYY') month,\r\n" +
         * "case when wp.ref_cd_dscr='SELF' then 'SELF' \r\n" +
         * "when wp.ref_cd_dscr in ('HUSBAND','BROTHER IN LAW','FATHER IN LAW','BROTHER','SON','FATHER') then 'MALE'\r\n"
         * + "when wp.ref_cd_dscr in ('JOINT USER') then 'JOINT USER'\r\n" +
         * "else 'OTHERS' end Loan_user, \r\n" + "ap.clnt_seq tot_usr,\r\n" +
         * "tot_cnt,\r\n" +
         * "case when loan_cycl_num=1 then ap.clnt_seq else null end new_clnts,\r\n" +
         * "case when loan_cycl_num>1 then ap.clnt_seq else null end rpt_clnts\r\n" +
         * "from mw_loan_app ap\r\n" +
         * "join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n" +
         * "join mw_port prt on prt.port_seq=ap.port_seq\r\n" +
         * "join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1\r\n"
         * + "join ( \r\n" +
         * "select to_char(jv_dt,'Mon-YYYY') mnth,count(distinct prnt_loan_app_seq) tot_cnt \r\n"
         * + "from mw_loan_app ap \r\n" +
         * "join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * +
         * "join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1\r\n"
         * + "join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n"
         * + "WHERE ap.crnt_rec_flg=1 and loan_app_sts=703\r\n" +
         * "and to_date(jv_dt) between add_months(to_date(:as_dt,'dd-MM-yyyy'),-6) and to_date(:as_dt,'dd-MM-yyyy')\r\n"
         * + "group by to_char(jv_dt,'Mon-YYYY')\r\n" +
         * ") ttls on ttls.mnth=to_char(jv_dt,'Mon-YYYY')\r\n" +
         * "where ap.crnt_rec_flg=1\r\n" + "and ap.loan_app_sts=703\r\n" +
         * "and wp.ref_cd_dscr='SELF'\r\n" +
         * "and to_date(jv_dt) between add_months(to_date(:as_dt,'dd-MM-yyyy'),-6) and to_date(:as_dt,'dd-MM-yyyy') \r\n"
         * + ")\r\n" + "group by mnth,month,tot_cnt order by 1" ) .setParameter(
         * "user_id", userId ).setParameter( "as_dt", asDt );
         */

        List<Object[]> graphObj = resSet.getResultList();

        List<Map<String, ?>> graph = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("MONTH", l[1] == null ? "" : l[1].toString());
            map.put("TOTAL", l[2] == null ? 0 : new BigDecimal(l[2].toString()).floatValue());
            map.put("NEW", l[3] == null ? 0 : new BigDecimal(l[3].toString()).floatValue());
            map.put("REPEAT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).floatValue());
            graph.add(map);
        });

        String userQry;
        userQry = readFile(Charset.defaultCharset(), "FemaleParticipationRatioUserQry.txt");
        Query ret = em.createNativeQuery(userQry).setParameter("user_id", userId).setParameter("as_dt", asDt);

        /*
         * Query userQry = em.createNativeQuery(
         * "select to_char(jv_dt,'YYYYMM') mnth,to_char(jv_dt,'Mon-YYYY') month,\r\n" +
         * "count( distinct case when wp.ref_cd_dscr='SELF' then ap.clnt_seq else null end) fe_tot,\r\n"
         * +
         * "count( distinct case when wp.ref_cd_dscr='SELF' and loan_cycl_num=1 then ap.clnt_seq else null end) fe_new,\r\n"
         * +
         * "count( distinct case when wp.ref_cd_dscr='SELF' and loan_cycl_num>1 then ap.clnt_seq else null end) fe_rpt,\r\n"
         * +
         * "count ( distinct case when wp.ref_cd_dscr='JOINT USER' then ap.clnt_seq else null end) jnt_tot,\r\n"
         * +
         * "count ( distinct case when wp.ref_cd_dscr='JOINT USER' and loan_cycl_num=1 then ap.clnt_seq else null end) jnt_new,\r\n"
         * +
         * "count ( distinct case when wp.ref_cd_dscr='JOINT USER' and loan_cycl_num>1 then ap.clnt_seq else null end) jnt_rpt,\r\n"
         * +
         * "count ( distinct case when wp.ref_cd_dscr in ('HUSBAND','BROTHER IN LAW','FATHER IN LAW','BROTHER','SON','FATHER') then ap.clnt_seq else null end) male_tot,\r\n"
         * +
         * "count ( distinct case when wp.ref_cd_dscr in ('HUSBAND','BROTHER IN LAW','FATHER IN LAW','BROTHER','SON','FATHER') and loan_cycl_num=1 then ap.clnt_seq else null end) male_new,\r\n"
         * +
         * "count (distinct case when wp.ref_cd_dscr in ('HUSBAND','BROTHER IN LAW','FATHER IN LAW','BROTHER','SON','FATHER') and loan_cycl_num>1 then ap.clnt_seq else null end) male_rpt,tot_cnt\r\n"
         * + "from mw_loan_app ap\r\n" +
         * "join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n" +
         * "join mw_port prt on prt.port_seq=ap.port_seq\r\n" +
         * "join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n" +
         * "join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1\r\n"
         * +
         * "join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1\r\n"
         * + "join ( \r\n" +
         * "select to_char(jv_dt,'Mon-YYYY') mnth,count(distinct prnt_loan_app_seq) tot_cnt \r\n"
         * + "from mw_loan_app ap \r\n" +
         * "join mw_jv_hdr jh on jh.enty_seq=ap.loan_app_seq and enty_typ='Disbursement'\r\n"
         * +
         * "join mw_biz_aprsl aprsl on aprsl.loan_app_seq=ap.loan_app_seq and aprsl.crnt_rec_flg=1 \r\n"
         * +
         * "join mw_ref_cd_val wp on wp.ref_cd_seq=aprsl.prsn_run_the_biz and wp.crnt_rec_flg=1\r\n"
         * + "join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n"
         * + "where ap.crnt_rec_flg=1 and loan_app_sts=703\r\n" +
         * "and to_date(jv_dt) between add_months(to_date(:as_dt,'dd-MM-yyyy'),-6) and to_date(:as_dt,'dd-MM-yyyy')\r\n"
         * + "group by to_char(jv_dt,'Mon-YYYY')\r\n" +
         * ") ttls on ttls.mnth=to_char(jv_dt,'Mon-YYYY')\r\n" +
         * "where ap.crnt_rec_flg=1\r\n" + "and ap.loan_app_sts=703\r\n" +
         * "and to_date(jv_dt) between add_months(to_date(:as_dt,'dd-MM-yyyy'),-6) and to_date(:as_dt,'dd-MM-yyyy')\r\n"
         * +
         * "group by to_char(jv_dt,'YYYYMM'),to_char(jv_dt,'Mon-YYYY'),tot_cnt order by 1"
         * ).setParameter( "user_id", userId ) .setParameter( "as_dt", asDt );
         */

        List<Object[]> userObj = ret.getResultList();

        List<Map<String, ?>> users = new ArrayList();
        userObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("MONTH", l[1] == null ? "" : l[1].toString());
            map.put("FE_TOT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("FE_NEW", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("FE_RPT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("JNT_TOT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("JNT_NEW", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("JNT_RPT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("MALE_TOT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("MALE_NEW", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("MALE_RPT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("TOT_CNT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            users.add(map);
        });
        params.put("users", getJRDataSource(users));

        return reportComponent.generateReport(FEMALE_PARTICIPATION_RATIO, params, getJRDataSource(graph));
    }

    public byte[] getBranchTargetManagementTool(String asDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("as_dt", asDt);

        String targQry;
        targQry = readFile(Charset.defaultCharset(), "BranchTargetManagementTargQry.txt");
        Query resSet = em.createNativeQuery(targQry).setParameter("user_id", userId).setParameter("as_dt", asDt);

        /*
         * Query targQry = em.createNativeQuery( "select distinct emp.emp_nm,\r\n" +
         * " max((select sum(trgt_clients) from mw_brnch_trgt where del_flg=0   and brnch_seq=prt.brnch_seq  and trgt_perd = to_char(to_date(:as_dt,'dd-MM-yyyy'),'YYYYMM')  ))/count(distinct emp_nm) over () trgt_clnts,\r\n"
         * +
         * " max((select sum(trgt_amt) from mw_brnch_trgt where del_flg=0   and brnch_seq=prt.brnch_seq  and trgt_perd = to_char(to_date(:as_dt,'dd-MM-yyyy'),'YYYYMM')))/count(distinct emp_nm) over () trgt_amt\r\n"
         * + "from mw_loan_app ap \r\n" +
         * "join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n" +
         * "join mw_acl acl on acl.port_seq=ap.port_seq and acl.user_id=:user_id\r\n" +
         * "join mw_port_emp_rel per on per.port_seq=ap.port_seq and per.crnt_rec_flg=1 \r\n"
         * + "join mw_emp emp on emp.emp_seq=per.emp_seq\r\n" +
         * "where ap.crnt_rec_flg=1 \r\n" +
         * "and ap.loan_app_sts=703 or (loan_app_sts=704 and loan_app_sts_dt > to_date(:as_dt,'dd-MM-yyyy'))\r\n"
         * + "group by prt.brnch_seq,emp.emp_nm" ).setParameter( "user_id", userId
         * ).setParameter( "as_dt", asDt );
         */

        List<Object[]> targObj = resSet.getResultList();

        List<Map<String, ?>> targets = new ArrayList();
        targObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("TRGT_CLNTS", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("TRGT_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            targets.add(map);
        });

        return reportComponent.generateReport(BRANCH_TARGET_MANG, params, getJRDataSource(targets));
    }

    public byte[] getPortfolioStatusActiveFromDateReport(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("to_dt", toDt);
        params.put("frm_dt", frmDt);

        String activePortObjQry;
        activePortObjQry = readFile(Charset.defaultCharset(), "PortfolioStatusActiveDurationActivePortObjQry.txt");
        Query resSet = em.createNativeQuery(activePortObjQry).setParameter("user_id", userId)
                .setParameter("to_dt", toDt).setParameter("frm_dt", frmDt);

        /*
         * Query activePortObjQry = em.createNativeQuery( "select pg.prd_grp_nm,\r\n" +
         * " count(distinct app.clnt_seq) tot_clnt,\r\n" +
         * " sum(app.aprvd_loan_amt) tot_clnt_amt,\r\n" +
         * " count(distinct case when app.loan_cycl_num =1 then app.clnt_seq else null end) new_clnt,\r\n"
         * +
         * " sum(case when app.loan_cycl_num = 1 then app.aprvd_loan_amt else 0 end) new_clnt_amt,\r\n"
         * +
         * " count(distinct case when app.loan_cycl_num > 1 then app.clnt_seq else null end) rpt_clnt,\r\n"
         * +
         * " sum(case when app.loan_cycl_num > 1 then app.aprvd_loan_amt else 0 end) rpt_clnt_amt,\r\n"
         * + " max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n" +
         * " join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * " where app.crnt_rec_flg=1  and loan_app_sts=703  and app.prd_seq=prd.prd_seq\r\n"
         * +
         * " and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * +
         * " and to_char(lst_loan_cmpltn_dt(app.loan_app_seq),'YYYYMM')=TO_CHAR(to_date(:to_dt,'dd-mm-yyyy'),'YYYYMM')\r\n"
         * + "  )) rnw_clnt_month,   \r\n" +
         * "  max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n" +
         * " join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "  where app.crnt_rec_flg=1  and app.prd_seq=prd.prd_seq  and loan_app_sts=704\r\n"
         * +
         * "   and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')  )) cmpltd_mnth,\r\n"
         * + "   \r\n" +
         * "                 max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n"
         * +
         * "  join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "  where app.crnt_rec_flg=1  and app.prd_seq=prd.prd_seq  and loan_app_sts=703\r\n"
         * +
         * "   and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy') )) dsbmt_mnth \r\n"
         * + "  \r\n" +
         * "  from mw_loan_app app join mw_prd prd on prd.prd_seq=app.prd_seq and prd.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_prd_grp pg on pg.prd_grp_seq=prd.prd_grp_seq and pg.crnt_rec_flg=1\r\n"
         * +
         * "  join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id where app.crnt_rec_flg=1\r\n"
         * + "   and loan_app_sts=703\r\n" +
         * "   and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * + "   group by pg.prd_grp_nm" ).setParameter( "user_id", userId
         * ).setParameter( "to_dt", toDt ) .setParameter( "frm_dt", frmDt );
         */

        List<Object[]> activePortObj = resSet.getResultList();

        List<Map<String, ?>> activePorts = new ArrayList();
        activePortObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRD_GRP_NM", l[0] == null ? "" : l[0].toString());
            map.put("TOT_CLNT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("TOT_CLNT_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("NEW_CLNT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("NEW_CLNT_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("RPT_CLNT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("RPT_CLNT_AMT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("RNW_CLNT_MONTH", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("CMPLTD_MNTH", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("DSBMT_MNTH", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            activePorts.add(map);
        });

        String bdoQry;
        bdoQry = readFile(Charset.defaultCharset(), "PortfolioStatusActiveDurationActivePortBdoQry.txt");
        Query rSet = em.createNativeQuery(bdoQry).setParameter("user_id", userId).setParameter("to_dt", toDt)
                .setParameter("frm_dt", frmDt);

        /*
         * Query bdoQry = em.createNativeQuery( "select emp_nm,\r\n" +
         * "       count(distinct app.clnt_seq) tot_clnt,\r\n" +
         * "       sum(app.aprvd_loan_amt) tot_clnt_amt,\r\n" +
         * "       count(distinct case when app.loan_cycl_num =1 then app.clnt_seq else null end) new_clnt,\r\n"
         * +
         * "        sum(case when app.loan_cycl_num = 1 then app.aprvd_loan_amt else 0 end) new_clnt_amt,\r\n"
         * +
         * "        count(distinct case when app.loan_cycl_num > 1 then app.clnt_seq else null end) rpt_clnt,\r\n"
         * +
         * "        sum(case when app.loan_cycl_num > 1 then app.aprvd_loan_amt else 0 end) rpt_clnt_amt,\r\n"
         * +
         * "        max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n"
         * +
         * "        join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "        where app.crnt_rec_flg=1  and loan_app_sts=703  and app.port_seq=erl.port_seq\r\n"
         * +
         * "                 and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * +
         * "        and to_char(lst_loan_cmpltn_dt(app.loan_app_seq),'YYYYMM')=TO_CHAR(to_date(:to_dt,'dd-mm-yyyy'),'YYYYMM')\r\n"
         * + "                 )) rnw_clnt_month,   \r\n" +
         * "                 max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n"
         * +
         * "                 join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "                 where app.crnt_rec_flg=1  and app.port_seq=erl.port_seq  and loan_app_sts=704\r\n"
         * +
         * "                 and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')  )) cmpltd_mnth,                 \r\n"
         * +
         * "                 max((select count(distinct app.clnt_seq)  from mw_loan_app app\r\n"
         * +
         * "                 join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id\r\n"
         * +
         * "                 where app.crnt_rec_flg=1  and app.port_seq=erl.port_seq  and loan_app_sts=703\r\n"
         * +
         * "                 and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy') )) dsbmt_mnth                  \r\n"
         * + "                from mw_loan_app app \r\n" +
         * "                join mw_port_emp_rel erl on erl.port_seq=app.port_seq and erl.crnt_rec_flg=1\r\n"
         * + "                join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n" +
         * "                join mw_acl acl on acl.port_seq=app.port_seq and acl.user_id=:user_id where app.crnt_rec_flg=1\r\n"
         * + "                and loan_app_sts=703\r\n" +
         * "                and to_date(loan_app_sts_dt) between to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * + "                group by emp_nm" ).setParameter( "user_id", userId
         * ).setParameter( "to_dt", toDt ) .setParameter( "frm_dt", frmDt );
         */

        List<Object[]> bdoObj = rSet.getResultList();

        List<Map<String, ?>> bdos = new ArrayList();
        bdoObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("TOT_CLNT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("TOT_CLNT_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("NEW_CLNT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("NEW_CLNT_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("RPT_CLNT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("RPT_CLNT_AMT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("RNW_CLNT_MONTH", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("CMPLTD_MNTH", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("DSBMT_MNTH", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            bdos.add(map);
        });
        params.put("bdos", getJRDataSource(bdos));
        return reportComponent.generateReport(PORTFOLIO_STAT_FROM, params, getJRDataSource(activePorts));
    }

    public byte[] getMcbChequeInfoReport(String frmDt, String toDt) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("to_dt", toDt);
        params.put("frm_dt", frmDt);

        String targQry;
        targQry = readFile(Charset.defaultCharset(), "McbChequeInfoReport.txt");
        Query set = em.createNativeQuery(targQry).setParameter("to_dt", toDt).setParameter("frm_dt", frmDt);

        /*
         * Query targQry = em.createNativeQuery(
         * "SELECT TO_CHAR(TO_DATE(SYSDATE),'dd-MON-YYYY') VDATE,  \r\n" +
         * "    to_char(DH.LOAN_APP_SEQ) LOAN_APP_SEQ,\r\n" +
         * "    to_char(MC.CNIC_NUM) CNIC_NUM,\r\n" +
         * "    to_date(MC.CNIC_EXPRY_DT) CNIC_EXPRY_DT,\r\n" +
         * "    to_char(MC.CLNT_SEQ) CLNT_SEQ,\r\n" +
         * "    MC.FRST_NM||' '||MC.LAST_NM NAME,\r\n" +
         * "    'KASHF FOUNDATION '||B.BRNCH_NM as BR_NAME,\r\n" +
         * "    dd.AMT DISB_AMOUNT,    \r\n" + "    RV.REF_CD_DSCR BANK,\r\n" +
         * "    BST.IBAN BANK_CODE,    \r\n" + "    BST.BANK_BRNCH,\r\n" +
         * "    PRD.PRD_NM,\r\n" + "    TY.TYP_STR\r\n" + "    FROM MW_REG R,\r\n" +
         * "         MW_AREA A,\r\n" + "         MW_BRNCH B,\r\n" +
         * "         MW_BRNCH_ACCT_SET BST,\r\n" + "         MW_REF_CD_VAL RV,\r\n" +
         * "         MW_PORT P,\r\n" + "         MW_LOAN_APP LA,\r\n" +
         * "         MW_DSBMT_VCHR_HDR DH,\r\n" + "         MW_DSBMT_VCHR_DTL DD,\r\n" +
         * "         MW_PRD PRD,\r\n" + "         MW_TYPS TY,\r\n" +
         * "         MW_CLNT MC\r\n" + "   WHERE     R.REG_SEQ = A.REG_SEQ\r\n" +
         * "         AND A.AREA_SEQ = B.AREA_SEQ\r\n" +
         * "         AND B.BRNCH_SEQ = BST.BRNCH_SEQ\r\n" +
         * "         AND BST.BANK_NM = RV.REF_CD\r\n" +
         * "         AND RV.REF_CD_GRP_KEY = 178\r\n" +
         * "         AND B.BRNCH_SEQ = P.BRNCH_SEQ\r\n" +
         * "         AND P.PORT_SEQ = LA.PORT_SEQ\r\n" +
         * "         AND LA.LOAN_APP_SEQ = DH.LOAN_APP_SEQ\r\n" +
         * "         AND DH.DSBMT_HDR_SEQ = DD.DSBMT_HDR_SEQ\r\n" +
         * "         AND LA.PRD_SEQ = PRD.PRD_SEQ                          \r\n" +
         * "         AND DD.PYMT_TYPS_SEQ = TY.TYP_SEQ\r\n" +
         * "         AND TY.TYP_SEQ in (221)      \r\n" +
         * "         AND B.BRNCH_SEQ = 98\r\n" + "         AND B.CRNT_REC_FLG = 1\r\n" +
         * "         AND MC.CLNT_SEQ = LA.CLNT_SEQ\r\n" +
         * "         --AND JH.ENTY_SEQ = 2058\r\n" +
         * "         AND TO_DATE(DH.DSBMT_DT)  BETWEEN to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * + "         AND B.CRNT_REC_FLG = A.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = A.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = BST.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = RV.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = MC.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = P.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = LA.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = DH.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = DD.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = PRD.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = TY.CRNT_REC_FLG\r\n" + "" ).setParameter(
         * "to_dt", toDt ).setParameter( "frm_dt", frmDt );
         */

        List<Object[]> targObj = set.getResultList();

        List<Map<String, ?>> targets = new ArrayList();
        targObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("VDATE", l[0] == null ? "" : l[0].toString());
            map.put("LOAN_APP_SEQ", l[1] == null ? "" : l[1].toString());
            map.put("CNIC_NUM", l[2] == null ? "" : l[2].toString());
            map.put("CNIC_EXPRY_DT", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_SEQ", l[4] == null ? "" : l[4].toString());
            map.put("BR_NAME", l[5] == null ? "" : l[5].toString());
            map.put("NAME", l[6] == null ? "" : l[6].toString());
            map.put("DISB_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("BANK", l[8] == null ? "" : l[8].toString());
            map.put("BANK_CODE", l[9] == null ? "" : l[9].toString());
            map.put("BANK_BRNCH", l[10] == null ? "" : l[10].toString());
            targets.add(map);
        });

        return reportComponent.generateReport(MCB_CHEQUE, params, getJRDataSource(targets));
    }

    public byte[] getBOPInfoReport(String frmDt, String toDt) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("to_dt", toDt);
        params.put("frm_dt", frmDt);

        String targQry;
        targQry = readFile(Charset.defaultCharset(), "BOPInfoReport.txt");
        Query set = em.createNativeQuery(targQry).setParameter("to_dt", toDt).setParameter("frm_dt", frmDt);

        /*
         * Query targQry = em.createNativeQuery(
         * "SELECT TO_CHAR(TO_DATE(SYSDATE),'dd-MON-YYYY') VDATE,  \r\n" +
         * "    to_char(DH.LOAN_APP_SEQ) LOAN_APP_SEQ,\r\n" +
         * "    to_char(MC.CNIC_NUM) CNIC_NUM,\r\n" +
         * "    to_date(MC.CNIC_EXPRY_DT) CNIC_EXPRY_DT,\r\n" +
         * "    to_char(MC.CLNT_SEQ) CLNT_SEQ,\r\n" +
         * "    MC.FRST_NM||' '||MC.LAST_NM NAME,\r\n" +
         * "    'KASHF FOUNDATION '||B.BRNCH_NM as BR_NAME,\r\n" +
         * "    dd.AMT DISB_AMOUNT,    \r\n" + "    RV.REF_CD_DSCR BANK,\r\n" +
         * "    BST.IBAN BANK_CODE,    \r\n" + "    BST.BANK_BRNCH,\r\n" +
         * "    PRD.PRD_NM,\r\n" + "    TY.TYP_STR\r\n" + "    FROM MW_REG R,\r\n" +
         * "         MW_AREA A,\r\n" + "         MW_BRNCH B,\r\n" +
         * "         MW_BRNCH_ACCT_SET BST,\r\n" + "         MW_REF_CD_VAL RV,\r\n" +
         * "         MW_PORT P,\r\n" + "         MW_LOAN_APP LA,\r\n" +
         * "         MW_DSBMT_VCHR_HDR DH,\r\n" + "         MW_DSBMT_VCHR_DTL DD,\r\n" +
         * "         MW_PRD PRD,\r\n" + "         MW_TYPS TY,\r\n" +
         * "         MW_CLNT MC\r\n" + "   WHERE     R.REG_SEQ = A.REG_SEQ\r\n" +
         * "         AND A.AREA_SEQ = B.AREA_SEQ\r\n" +
         * "         AND B.BRNCH_SEQ = BST.BRNCH_SEQ\r\n" +
         * "         AND BST.BANK_NM = RV.REF_CD\r\n" +
         * "         AND RV.REF_CD_GRP_KEY = 178\r\n" +
         * "         AND B.BRNCH_SEQ = P.BRNCH_SEQ\r\n" +
         * "         AND P.PORT_SEQ = LA.PORT_SEQ\r\n" +
         * "         AND LA.LOAN_APP_SEQ = DH.LOAN_APP_SEQ\r\n" +
         * "         AND DH.DSBMT_HDR_SEQ = DD.DSBMT_HDR_SEQ\r\n" +
         * "         AND LA.PRD_SEQ = PRD.PRD_SEQ                          \r\n" +
         * "         AND DD.PYMT_TYPS_SEQ = TY.TYP_SEQ\r\n" +
         * "         AND TY.TYP_SEQ in (221)      \r\n" +
         * "         AND B.BRNCH_SEQ = 98\r\n" + "         AND B.CRNT_REC_FLG = 1\r\n" +
         * "         AND MC.CLNT_SEQ = LA.CLNT_SEQ\r\n" +
         * "         --AND JH.ENTY_SEQ = 2058\r\n" +
         * "         AND TO_DATE(DH.DSBMT_DT)  BETWEEN to_date(:frm_dt,'dd-mm-yyyy') and to_date(:to_dt,'dd-mm-yyyy')\r\n"
         * + "         AND B.CRNT_REC_FLG = A.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = A.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = BST.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = RV.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = MC.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = P.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = LA.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = DH.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = DD.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = PRD.CRNT_REC_FLG\r\n" +
         * "         AND R.CRNT_REC_FLG = TY.CRNT_REC_FLG\r\n" + "" ).setParameter(
         * "to_dt", toDt ).setParameter( "frm_dt", frmDt );
         */

        List<Object[]> targObj = set.getResultList();

        List<Map<String, ?>> targets = new ArrayList();
        targObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("VDATE", l[0] == null ? "" : l[0].toString());
            map.put("LOAN_APP_SEQ", l[1] == null ? "" : l[1].toString());
            map.put("CNIC_NUM", l[2] == null ? "" : l[2].toString());
            map.put("CNIC_EXPRY_DT", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_SEQ", l[4] == null ? "" : l[4].toString());
            map.put("BR_NAME", l[5] == null ? "" : l[5].toString());
            map.put("NAME", l[6] == null ? "" : l[6].toString());
            map.put("DISB_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("BANK", l[7] == null ? "" : l[7].toString());
            map.put("BANK_CODE", l[8] == null ? "" : l[8].toString());
            map.put("BANK_BRNCH", l[9] == null ? "" : l[9].toString());
            targets.add(map);
        });

        return reportComponent.generateReport(BOP_INFO, params, getJRDataSource(targets));
    }

    public byte[] getEPAndRemReport(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO).setParameter("userId", userId);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String remQry;
        remQry = readFile(Charset.defaultCharset(), "EPAndRemReport.txt");
        Query set = em.createNativeQuery(remQry).setParameter("userid", userId).setParameter("frmdt", frmDt)
                .setParameter("todt", toDt);

        /*
         * Query remQry = em .createNativeQuery(
         * "select to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY') mon, \r\n" +
         * "       count( distinct case when mt.typ_id='0008' then ap.loan_app_seq else null end) bnk_clnt_cnt,\r\n"
         * +
         * "       sum(case when mt.typ_id='0008' then ap.aprvd_loan_amt else 0 end) bnk_clnt_amt,\r\n"
         * +
         * "       count(distinct case when mt.typ_id not in ('0008','0001') then ap.loan_app_seq else null end) ep_cnt,\r\n"
         * +
         * "       sum(case when mt.typ_id not in ('0008','0001') then ap.aprvd_loan_amt else 0 end)  ep_amt\r\n"
         * +
         * "from mw_loan_app ap, MW_DSBMT_VCHR_HDR dvh, MW_DSBMT_VCHR_DTL dvd, mw_port mp, mw_acl ma, mw_typs mt\r\n"
         * + "where ap.crnt_rec_flg=1 \r\n" +
         * "and ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n" +
         * "and dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n" +
         * "and dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n" + "and mt.TYP_CTGRY_KEY = 3 \r\n" +
         * "and dvd.CRNT_REC_FLG = 1 \r\n" + "and dvh.crnt_rec_flg=1 \r\n" +
         * "and dvh.crnt_rec_flg=1 \r\n" + "and ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "and ap.PORT_SEQ = ma.PORT_SEQ\r\n" + "and ma.USER_ID = :userid\r\n" +
         * "and to_date(dvh.DSBMT_DT) between to_date(:frmdt, 'dd-MM-yyyy') and to_date(:todt, 'dd-MM-yyyy') \r\n"
         * + "group by to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY')" ) .setParameter(
         * "userid", userId ).setParameter( "frmdt", frmDt ).setParameter( "todt", toDt
         * );
         */

        List<Object[]> graphObj = set.getResultList();

        List<Map<String, ?>> remittance = new ArrayList();
        graphObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("MON", l[0] == null ? "" : l[0].toString());
            map.put("BNK_CLNT_CNT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("BNK_CLNT_AMT", l[2] == null ? 0 : new BigDecimal(l[2].toString()).longValue());
            map.put("EP_CNT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("EP_AMT", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            remittance.add(map);
        });
        return reportComponent.generateReport(REM_REPORT, params, getJRDataSource(remittance));
    }

    public byte[] getFundsReport(String frmDt, String toDt, String userId, long brnch, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String fundTraQry;
        fundTraQry = readFile(Charset.defaultCharset(), "FundsReport.txt");
        Query set = em.createNativeQuery(fundTraQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("brnch", brnch);

        /*
         * Query fundTraQry = em .createNativeQuery(
         * "select mr.REG_NM, ma.AREA_NM, mb.BRNCH_NM, to_date(me.CRTD_DT , 'dd-MM-yyyy') exp_date,\r\n"
         * +
         * "(select typ_str from mw_typs mt where mt.TYP_SEQ = me.EXPNS_TYP_SEQ and mt.TYP_CTGRY_KEY in (2,6)) exp_typ,\r\n"
         * + "me.EXP_SEQ, me.EXP_REF, me.EXPNS_DSCR narration, me.INSTR_NUM, \r\n" +
         * "(select typ_str from mw_typs mt where mt.TYP_SEQ = me.pymt_typ_seq and mt.TYP_CTGRY_KEY = 6) pymt_typ,\r\n"
         * + "(case when me.PYMT_RCT_FLG = 2 then me.EXPNS_AMT end) recpt,\r\n" +
         * "(case when me.PYMT_RCT_FLG = 1 then me.EXPNS_AMT end) pymt\r\n" +
         * "from mw_exp me, mw_brnch mb, mw_area ma, mw_reg mr\r\n" +
         * "where me.BRNCH_SEQ = mb.BRNCH_SEQ\r\n" + "and mb.AREA_SEQ = ma.AREA_SEQ\r\n"
         * + "and ma.REG_SEQ = mr.REG_SEQ\r\n" +
         * "and me.EXPNS_TYP_SEQ in (420,423,580,343)\r\n" + "and me.POST_FLG = 1\r\n" +
         * "and me.CRNT_REC_FLG = 1\r\n" + "and mb.BRNCH_SEQ = :brnch\r\n" +
         * "and to_date(me.CRTD_DT) between to_date(:frmdt , 'dd-MM-yyyy') and to_date(:todt, 'dd-MM-yyyy') "
         * ) .setParameter( "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter(
         * "brnch", brnch );
         */

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> funds = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REG_NM", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NM", l[1] == null ? "" : l[1].toString());
            map.put("BRNCH_NM", l[2] == null ? "" : l[2].toString());
            map.put("EXP_DATE", l[3] == null ? "" : l[3].toString());
            map.put("EXP_TYP", l[4] == null ? "" : l[4].toString());
            map.put("EXP_SEQ", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("EXP_REF", l[6] == null ? "" : l[6].toString());
            map.put("NARRATION", l[7] == null ? "" : l[7].toString());
            map.put("INSTR_NUM", l[8] == null ? "" : l[8].toString());
            map.put("PYMT_TYP", l[9] == null ? "" : l[9].toString());
            map.put("RECPT", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("PYMT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            funds.add(map);
        });
        return reportComponent.generateReport(FUND_TRANSFER_REPORT, params, getJRDataSource(funds), isXls);
    }

    public byte[] getDisbursementReport(String frmDt, String toDt, String userId, long brnch, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "DisbursementReport.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("brnch", brnch);
        /*
         * Query disQry = em.createNativeQuery(
         * "select * from ( select mr.REG_NM, ma.AREA_NM, mb.BRNCH_NM, mt.TYP_STR, to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY') mon,\r\n"
         * + "                   (case when mpg.PRD_GRP_SEQ = 9 then 'SCHOOL'\r\n" +
         * "                        when mpg.PRD_GRP_SEQ in (13,19,2) then 'DOMESTIC'\r\n"
         * +
         * "                        else nvl(get_app_sect(ap.loan_app_seq),'OTHERS')\r\n"
         * + "                   end) biz_sect,\r\n" +
         * "                   mpg.PRD_GRP_NM, count(distinct ap.loan_app_seq) no_of_loans, \r\n"
         * + "                    count(distinct ap.clnt_seq) no_of_clnts,\r\n" +
         * "                    sum(ap.aprvd_loan_amt) disb_amt                \r\n" +
         * "                    from mw_loan_app ap, MW_DSBMT_VCHR_HDR dvh, MW_DSBMT_VCHR_DTL dvd, mw_port mp, mw_acl ma, mw_typs mt,\r\n"
         * +
         * "                    mw_brnch mb, mw_area ma, mw_reg mr,mw_prd mprd, mw_prd_grp mpg\r\n"
         * + "                    where ap.crnt_rec_flg=1 \r\n" +
         * "                    and ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n" +
         * "                    and dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n" +
         * "                    and dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n" +
         * "                    and mt.TYP_CTGRY_KEY = 3 \r\n" +
         * "                    and dvd.CRNT_REC_FLG = 1 \r\n" +
         * "                    and dvh.crnt_rec_flg=1 \r\n" +
         * "                    and dvh.crnt_rec_flg=1 \r\n" +
         * "                    and ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "                    and ap.PORT_SEQ = ma.PORT_SEQ\r\n" +
         * "                    and mp.BRNCH_SEQ = mb.BRNCH_SEQ\r\n" +
         * "                    and mb.AREA_SEQ = ma.AREA_SEQ\r\n" +
         * "                    and ma.REG_SEQ = mr.REG_SEQ\r\n" +
         * "                    and mb.CRNT_REC_FLG = 1\r\n" +
         * "                    and ap.PRD_SEQ = mprd.PRD_SEQ\r\n" +
         * "                    and mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n" +
         * "                    and to_date(dvh.DSBMT_DT) between to_date(:frmdt , 'dd-MM-yyyy') and to_date(:todt, 'dd-MM-yyyy')\r\n"
         * + "                    and mb.BRNCH_SEQ = :brnch \r\n" +
         * "                   group by mt.TYP_STR,to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY'), \r\n"
         * +
         * "                    mr.REG_NM, ma.AREA_NM, mb.BRNCH_NM, mpg.PRD_GRP_NM, get_app_sect(ap.loan_app_seq), mpg.PRD_GRP_SEQ\r\n"
         * + "                    \r\n" + "  )                " ) .setParameter(
         * "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter( "brnch", brnch );
         */

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REG_NM", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NM", l[1] == null ? "" : l[1].toString());
            map.put("BRNCH_NM", l[2] == null ? "" : l[2].toString());
            map.put("TYP_STR", l[3] == null ? "" : l[3].toString());
            map.put("MON", l[4] == null ? "" : l[4].toString());
            map.put("BIZ_SECT", l[5] == null ? "" : l[5].toString());
            map.put("PRD_GRP_NM", l[6] == null ? "" : l[6].toString());
            map.put("NO_OF_LOANS", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("NO_OF_CLNTS", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("DISB_AMT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            disbursements.add(map);
        });
        return reportComponent.generateReport(DISBURSEMENT_REPORT, params, getJRDataSource(disbursements), isXls);
    }

    public byte[] getRecoveryReport(String frmDt, String toDt, String userId, boolean isXls) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String recoveryQry;
        recoveryQry = readFile(Charset.defaultCharset(), "RecoveryReport.txt");
        Query set = em.createNativeQuery(recoveryQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        /*
         * Query recoveryQry = em.createNativeQuery(
         * "select mr.REG_NM, mar.AREA_NM, mb.BRNCH_NM, \r\n" +
         * "        mt.TYP_STR, mpg.PRD_GRP_NM, to_char(to_date(trx.pymt_dt),'Mon-YYYY') pymt_dt,         \r\n"
         * + "        (case when dtl.CHRG_TYP_KEY = -2 then 'KSZB'\r\n" +
         * "             when dtl.CHRG_TYP_KEY = -1 then 'PRINCIPAL'\r\n" +
         * "           else (select mts.TYP_STR from mw_typs mts where mts.TYP_SEQ = dtl.CHRG_TYP_KEY)\r\n"
         * + "        end) chrg_typ,       \r\n" +
         * "        count(DISTINCT ap.loan_app_seq) rcvrd_clnt,\r\n" +
         * "        sum(dtl.pymt_amt) rcvrd_amt\r\n" + "        from mw_loan_app ap\r\n"
         * +
         * "        join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
         * +
         * "        join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
         * +
         * "        join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
         * +
         * "        join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1  \r\n"
         * +
         * "        join mw_rcvry_dtl dtl on dtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and dtl.crnt_rec_flg=1        \r\n"
         * +
         * "        join mw_rcvry_trx trx on trx.rcvry_trx_seq=dtl.rcvry_trx_seq and trx.crnt_rec_flg=1,\r\n"
         * +
         * "        mw_port mp, mw_brnch mb, mw_area mar, mw_reg mr, mw_typs mt, mw_acl ma, mw_prd mprd, mw_prd_grp mpg   \r\n"
         * + "    where ap.crnt_rec_flg=1\r\n" +
         * "    and trx.pymt_dt between to_date(:frmdt, 'dd-MM-yyyy') and to_date(:todt, 'dd-MM-yyyy')\r\n"
         * + "    and ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "    and mp.BRNCH_SEQ = mb.BRNCH_SEQ\r\n" +
         * "    and mb.AREA_SEQ = mar.AREA_SEQ\r\n" +
         * "    and mar.REG_SEQ = mr.REG_SEQ\r\n" + "    and mb.CRNT_REC_FLG = 1\r\n" +
         * "    and mp.CRNT_REC_FLG = 1\r\n" + "    and mar.CRNT_REC_FLG = 1\r\n" +
         * "    and mr.CRNT_REC_FLG = 1\r\n" +
         * "    and trx.RCVRY_TYP_SEQ = mt.TYP_SEQ\r\n" +
         * "    and mt.CRNT_REC_FLG = 1\r\n" + "    and ap.PORT_SEQ = ma.PORT_SEQ\r\n" +
         * "    and ap.PRD_SEQ = mprd.PRD_SEQ\r\n" +
         * "    and mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n" +
         * "   and dtl.CHRG_TYP_KEY = -1\r\n" +
         * "    group by mr.REG_NM, mar.AREA_NM, mb.BRNCH_NM,mt.TYP_STR,to_char(to_date(trx.pymt_dt),'Mon-YYYY'),\r\n"
         * + "    dtl.CHRG_TYP_KEY, mpg.PRD_GRP_NM" ).setParameter( "frmdt", frmDt
         * ).setParameter( "todt", toDt );
         */

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> recovery = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REG_NM", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NM", l[1] == null ? "" : l[1].toString());
            map.put("BRNCH_NM", l[2] == null ? "" : l[2].toString());
            map.put("TYP_STR", l[3] == null ? "" : l[3].toString());
            map.put("PRD_GRP_NM", l[4] == null ? "" : l[4].toString());
            map.put("PYMT_DT", l[5] == null ? "" : l[5].toString());
            map.put("CHRG_TYP", l[5] == null ? "" : l[5].toString());
            map.put("RCVRD_CLNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("RCVRD_AMT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            recovery.add(map);
        });
        return reportComponent.generateReport(RECOVERY_REPORT, params, getJRDataSource(recovery), isXls);
    }

    public byte[] getFundManagmentReport(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String recoveryQry;
        recoveryQry = readFile(Charset.defaultCharset(), "FundManagmentRecoveryQry.txt");
        Query set = em.createNativeQuery(recoveryQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        /*
         * Query recoveryQry = em.createNativeQuery(
         * "  SELECT TO_CHAR (dvh.DSBMT_DT, 'yyyymm')\r\n" +
         * "             DSBMT_DT,\r\n" +
         * "         (CASE WHEN mt.TYP_ID = '0008' THEN 'BANK' ELSE MT.TYP_STR END)\r\n"
         * + "             TYP_STR,\r\n" +
         * "         TO_CHAR (TO_DATE (dvh.DSBMT_DT), 'Mon-YYYY')\r\n" +
         * "             mon,\r\n" + "         COUNT (DISTINCT ap.loan_app_seq)\r\n" +
         * "             no_of_loans,\r\n" + "         COUNT (DISTINCT ap.clnt_seq)\r\n"
         * + "             no_of_clnts,\r\n" + "         SUM (ap.aprvd_loan_amt)\r\n" +
         * "             disb_amt\r\n" + "    FROM mw_loan_app      ap,\r\n" +
         * "         MW_DSBMT_VCHR_HDR dvh,\r\n" + "         MW_DSBMT_VCHR_DTL dvd,\r\n"
         * + "         mw_port          mp,\r\n" + "         mw_acl           ma,\r\n" +
         * "         mw_typs          mt\r\n" + "   WHERE     ap.crnt_rec_flg = 1\r\n" +
         * "         AND ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n" +
         * "         AND dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n" +
         * "         AND dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n" +
         * "         AND mt.TYP_CTGRY_KEY = 3\r\n" +
         * "         AND dvd.CRNT_REC_FLG = 1\r\n" +
         * "         AND dvh.crnt_rec_flg = 1\r\n" +
         * "         AND dvh.crnt_rec_flg = 1\r\n" +
         * "         AND ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "         AND ap.PORT_SEQ = ma.PORT_SEQ\r\n" +
         * "         AND dvh.DSBMT_DT BETWEEN TO_DATE ( :frmdt, 'dd-mm-yyyy')\r\n" +
         * "                              AND TO_DATE ( :todt, 'dd-mm-yyyy')\r\n" +
         * "        AND mt.TYP_ID NOT IN ('0009','0010','0011')                      \r\n"
         * + "GROUP BY TO_CHAR (dvh.DSBMT_DT, 'yyyymm'),\r\n" +
         * "         mt.TYP_STR,\r\n" +
         * "         TO_CHAR (TO_DATE (dvh.DSBMT_DT), 'Mon-YYYY'),\r\n" +
         * "         mt.TYP_ID\r\n" + "ORDER BY 1" ) .setParameter( "frmdt", frmDt
         * ).setParameter( "todt", toDt );
         */

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> recovery = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSBMT_DT", l[0] == null ? "" : l[0].toString());
            map.put("TYP_STR", l[1] == null ? "" : l[1].toString());
            map.put("MON", l[2] == null ? "" : l[2].toString());
            map.put("NO_OF_LOANS", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("NO_OF_CLNTS", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("DISB_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            recovery.add(map);
        });
        String cashlessQry;
        cashlessQry = readFile(Charset.defaultCharset(), "FundManagmentRecoveryQry.txt");
        Query rs = em.createNativeQuery(cashlessQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        /*
         * Query cashlessQry = em.createNativeQuery(
         * "  SELECT TO_CHAR (dvh.DSBMT_DT, 'yyyymm')\r\n" +
         * "             DSBMT_DT,\r\n" +
         * "         (CASE WHEN mt.TYP_ID = '0008' THEN 'BANK' ELSE MT.TYP_STR END)\r\n"
         * + "             TYP_STR,\r\n" +
         * "         TO_CHAR (TO_DATE (dvh.DSBMT_DT), 'Mon-YYYY')\r\n" +
         * "             mon,\r\n" + "         COUNT (DISTINCT ap.loan_app_seq)\r\n" +
         * "             no_of_loans,\r\n" + "         COUNT (DISTINCT ap.clnt_seq)\r\n"
         * + "             no_of_clnts,\r\n" + "         SUM (ap.aprvd_loan_amt)\r\n" +
         * "             disb_amt\r\n" + "    FROM mw_loan_app      ap,\r\n" +
         * "         MW_DSBMT_VCHR_HDR dvh,\r\n" + "         MW_DSBMT_VCHR_DTL dvd,\r\n"
         * + "         mw_port          mp,\r\n" + "         mw_acl           ma,\r\n" +
         * "         mw_typs          mt\r\n" + "   WHERE     ap.crnt_rec_flg = 1\r\n" +
         * "         AND ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n" +
         * "         AND dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n" +
         * "         AND dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n" +
         * "         AND mt.TYP_CTGRY_KEY = 3\r\n" +
         * "         AND dvd.CRNT_REC_FLG = 1\r\n" +
         * "         AND dvh.crnt_rec_flg = 1\r\n" +
         * "         AND dvh.crnt_rec_flg = 1\r\n" +
         * "         AND ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "         AND ap.PORT_SEQ = ma.PORT_SEQ\r\n" +
         * "         AND dvh.DSBMT_DT BETWEEN TO_DATE ( :frmdt, 'dd-mm-yyyy')\r\n" +
         * "                              AND TO_DATE ( :todt, 'dd-mm-yyyy')\r\n" +
         * "        AND mt.TYP_ID NOT IN ('0001','0009','0010','0011')                   \r\n"
         * + "GROUP BY TO_CHAR (dvh.DSBMT_DT, 'yyyymm'),\r\n" +
         * "         mt.TYP_STR,\r\n" +
         * "         TO_CHAR (TO_DATE (dvh.DSBMT_DT), 'Mon-YYYY'),\r\n" +
         * "         mt.TYP_ID\r\n" + "ORDER BY 1" ) .setParameter( "frmdt", frmDt
         * ).setParameter( "todt", toDt );
         */

        List<Object[]> cashLessObj = rs.getResultList();

        List<Map<String, ?>> cashLess = new ArrayList();
        cashLessObj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSBMT_DT", l[0] == null ? "" : l[0].toString());
            map.put("TYP_STR", l[1] == null ? "" : l[1].toString());
            map.put("MON", l[2] == null ? "" : l[2].toString());
            map.put("NO_OF_LOANS", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            map.put("NO_OF_CLNTS", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("DISB_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            cashLess.add(map);
        });
        params.put("CASH_LESS", getJRDataSource(cashLess));
        return reportComponent.generateReport(REM_RATIO_REP, params, getJRDataSource(recovery));
    }

    public byte[] getOrganizationTaggingReport(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String recoveryQry;
        recoveryQry = readFile(Charset.defaultCharset(), "OrganizationTaggingReport.txt");
        Query rs = em.createNativeQuery(recoveryQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setMaxResults(MaxResultSet);

        /*
         * Query recoveryQry = em.createNativeQuery(
         * "select reg.reg_nm,ara.area_nm,mb.BRNCH_NM, cl.CLNT_SEQ, cl.FRST_NM||' '||cl.LAST_NM clnt_name, ap.loan_app_seq, \r\n"
         * +
         * "                            (case when mpg.PRD_GRP_SEQ = 9 then 'SCHOOL'\r\n"
         * +
         * "                                              when mpg.PRD_GRP_SEQ in (13,19,2) then 'DOMESTIC'\r\n"
         * +
         * "                                              else nvl(get_app_sect(ap.loan_app_seq),'OTHERS')\r\n"
         * +
         * "                             end) biz_sect, mpg.PRD_GRP_NM, ap.LOAN_CYCL_NUM, \r\n"
         * +
         * "                             (case when mt.TYP_ID = '0008' THEN 'BANK' ELSE MT.TYP_STR END) TYP_STR, \r\n"
         * +
         * "                              to_char(dvh.DSBMT_DT,'dd-mm-yyyy') disb_dt, \r\n"
         * +
         * "                                          ap.aprvd_loan_amt disb_amt , to_char( cl.DOB,'dd-mm-yyyy') , \r\n"
         * +
         * "                                          (select REF_CD_DSCR from mw_ref_cd_val vl where vl.REF_CD_seq = cl.GNDR_KEY) gndr\r\n"
         * +
         * "                                          from mw_loan_app ap, MW_DSBMT_VCHR_HDR dvh, MW_DSBMT_VCHR_DTL dvd, mw_port mp, mw_acl ma, mw_typs mt,\r\n"
         * +
         * "                                          mw_clnt cl, mw_prd mprd, mw_prd_grp mpg, mw_brnch mb, mw_area ara, mw_reg reg\r\n"
         * + "                                          where ap.crnt_rec_flg=1 \r\n" +
         * "                                          and ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n"
         * +
         * "                                          and dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n"
         * +
         * "                                          and dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n"
         * +
         * "                                          and ap.clnt_seq = cl.clnt_seq\r\n"
         * +
         * "                                          and mp.BRNCH_SEQ = mb.BRNCH_SEQ                     and cl.CRNT_REC_FLG = 1\r\n"
         * +
         * "                                          and ara.area_seq=mb.area_seq and ara.crnt_rec_flg=1\r\n"
         * +
         * "                                          and reg.reg_seq=ara.reg_seq and reg.crnt_rec_flg=1\r\n"
         * +
         * "                                          and mb.CRNT_REC_FLG = 1                     and mprd.CRNT_REC_FLG = 1\r\n"
         * +
         * "                                          and mt.TYP_CTGRY_KEY = 3                      and dvd.CRNT_REC_FLG = 1 \r\n"
         * +
         * "                                          and dvh.crnt_rec_flg=1                      and dvh.crnt_rec_flg=1 \r\n"
         * +
         * "                                          and ap.PORT_SEQ = mp.PORT_SEQ                     and ap.PORT_SEQ = ma.PORT_SEQ\r\n"
         * +
         * "                                          and ap.prd_seq = mprd.PRD_SEQ\r\n"
         * +
         * "                                          and mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n"
         * +
         * "                                          and to_date(dvh.DSBMT_DT) between to_date(:frmdt,'dd-mm-yyyy') and to_date(:todt,'dd-mm-yyyy')"
         * ) .setParameter( "frmdt", frmDt ).setParameter( "todt", toDt );
         */

        List<Object[]> Obj = rs.getResultList();

        List<Map<String, ?>> recovery = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REG_NM", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NM", l[1] == null ? "" : l[1].toString());
            map.put("BRNCH_NM", l[2] == null ? "" : l[2].toString());
            map.put("CLNT_SEQ", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_NAME", l[4] == null ? "" : l[4].toString());
            map.put("LOAN_APP_SEQ", l[5] == null ? "" : l[5].toString());
            map.put("BIZ_SECT", l[6] == null ? "" : l[6].toString());
            map.put("PRD_GRP_NM", l[7] == null ? "" : l[7].toString());
            map.put("LOAN_CYCL_NUM", l[8] == null ? "" : l[8].toString());
            map.put("TYP_STR", l[9] == null ? "" : l[9].toString());
            map.put("DISB_DT", l[10] == null ? "" : l[10].toString());
            map.put("DISB_AMT", l[11] == null ? 0 : new BigDecimal(l[11].toString()).longValue());
            map.put("DOB", l[12] == null ? "" : l[12].toString());
            map.put("GNDR", l[13] == null ? "" : l[13].toString());
            recovery.add(map);
        });
        return reportComponent.generateReport(ORG_TAGGING_REPORT, params, getJRDataSource(recovery));
    }

    public byte[] getProductWiseDisbursementReport(String frmDt, String toDt, Long prd, String userId)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);
        String prdString = "";
        if (prd == 0) {
            prdString = " and grp.PRD_GRP_SEQ=:prd\r\n";
        }

        /*
         * String query =
         * "select to_char(to_date(dvh.DSBMT_DT),'YYYYMM') DSBMT_DT,to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY') mon, mt.TYP_STR,\r\n"
         * + "                    grp.PRD_GRP_NM, \r\n" +
         * "                    count(distinct ap.loan_app_seq) no_of_loans, \r\n" +
         * "                    count(distinct ap.clnt_seq) no_of_clnts,\r\n" +
         * "                    sum(ap.aprvd_loan_amt) disb_amt \r\n" +
         * "                    from mw_loan_app ap, MW_DSBMT_VCHR_HDR dvh, \r\n" +
         * "                    MW_DSBMT_VCHR_DTL dvd, \r\n" +
         * "                    mw_port mp, mw_acl ma, mw_typs mt,\r\n" +
         * "                    mw_prd prd, mw_prd_grp  grp\r\n" +
         * "                    where ap.crnt_rec_flg=1 \r\n" +
         * "                    and ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n" +
         * "                    and dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n" +
         * "                    and dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ\r\n" +
         * "                    and mt.TYP_CTGRY_KEY = 3 \r\n" +
         * "                    and dvd.CRNT_REC_FLG = 1 \r\n" +
         * "                    and ap.PRD_SEQ = prd.PRD_SEQ\r\n" +
         * "                    and prd.PRD_GRP_SEQ = grp.PRD_GRP_SEQ\r\n" + prdString +
         * "                    and dvh.crnt_rec_flg=1 \r\n" +
         * "                    and dvh.crnt_rec_flg=1 \r\n" +
         * "                    and ap.PORT_SEQ = mp.PORT_SEQ\r\n" +
         * "                    and ap.PORT_SEQ = ma.PORT_SEQ\r\n" +
         * "                    and to_date(dvh.DSBMT_DT) between to_date(:frmdt,'dd-mm-yyyy') and to_date(:todt,'dd-mm-yyyy')\r\n"
         * +
         * "                    group by to_char(to_date(dvh.DSBMT_DT),'YYYYMM'), grp.PRD_GRP_NM,mt.TYP_STR,to_char(to_date(dvh.DSBMT_DT),'Mon-YYYY') order by 1"
         * ;
         */

        String query;
        query = (readFile(Charset.defaultCharset(), "ProductWiseDisbursementReport.txt")).replace("prdString",
                prdString);
        // query.replaceAll( "prdString", prdString );
        Query recoveryQry = em.createNativeQuery(query).setParameter("frmdt", frmDt).setParameter("todt", toDt);
        if (prd == 0) {
            recoveryQry.setParameter("prd", prd);
        }

        List<Object[]> Obj = recoveryQry.getResultList();

        List<Map<String, ?>> recovery = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSBMT_DT", l[0] == null ? "" : l[0].toString());
            map.put("MON", l[1] == null ? "" : l[1].toString());
            map.put("TYP_STR", l[2] == null ? "" : l[2].toString());
            map.put("PRD_GRP_NM", l[3] == null ? "" : l[3].toString());
            map.put("NO_OF_LOANS", l[4] == null ? 0 : new BigDecimal(l[4].toString()).longValue());
            map.put("NO_OF_CLNTS", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("DISB_AMT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            recovery.add(map);
        });
        return reportComponent.generateReport(PRODUCT_WISE_DISBURSEMENT, params, getJRDataSource(recovery));
    }

    public byte[] getOrganizationTagTimeReport(String frmDt, String toDt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String recoveryQry;

        recoveryQry = readFile(Charset.defaultCharset(), "OrganizationTagTimeReport.txt");
        Query rs = em.createNativeQuery(recoveryQry).setParameter("frmdt", frmDt).setParameter("todt", toDt)
                .setParameter("userid", userId);

        /*
         * Query recoveryQry = em .createNativeQuery(
         * " select reg.reg_nm,area.area_nm,mb.BRNCH_NM, to_char(dvh.DSBMT_DT,'dd-mm-yyyy') disb_dt,   \r\n"
         * +
         * "                      mpg.PRD_GRP_NM,        (case when mt.TYP_ID = '0008' THEN 'BANK' ELSE MT.TYP_STR END) TYP_STR,\r\n"
         * +
         * "                       count(cl.CLNT_SEQ) clnts,sum(ap.aprvd_loan_amt) disb_amt \r\n"
         * +
         * "                                    from mw_loan_app ap, MW_DSBMT_VCHR_HDR dvh, MW_DSBMT_VCHR_DTL dvd, mw_port mp, mw_acl ma, mw_typs mt,\r\n"
         * +
         * "                                    mw_clnt cl, mw_prd mprd, mw_prd_grp mpg, mw_brnch mb,mw_area area,mw_reg reg\r\n"
         * +
         * "                                    where ap.crnt_rec_flg=1                      and ap.LOAN_APP_SEQ = dvh.LOAN_APP_SEQ\r\n"
         * +
         * "                                    and dvh.DSBMT_HDR_SEQ = dvd.DSBMT_HDR_SEQ\r\n"
         * +
         * "                                    and dvd.PYMT_TYPS_SEQ = mt.TYP_SEQ                     and ap.clnt_seq = cl.clnt_seq\r\n"
         * +
         * "                                    and mp.BRNCH_SEQ = mb.BRNCH_SEQ                     and cl.CRNT_REC_FLG = 1\r\n"
         * +
         * "                                    and area.area_seq=mb.area_seq and area.crnt_rec_flg=1\r\n"
         * +
         * "                                    and reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1\r\n"
         * +
         * "                                    and mb.CRNT_REC_FLG = 1                     and mprd.CRNT_REC_FLG = 1\r\n"
         * +
         * "                                    and mt.TYP_CTGRY_KEY = 3                      and dvd.CRNT_REC_FLG = 1 \r\n"
         * +
         * "                                    and dvh.crnt_rec_flg=1                      and dvh.crnt_rec_flg=1 \r\n"
         * +
         * "                                    and ap.PORT_SEQ = mp.PORT_SEQ                     and ap.PORT_SEQ = ma.PORT_SEQ\r\n"
         * +
         * "                                    and ap.prd_seq = mprd.PRD_SEQ                     and mprd.PRD_GRP_SEQ = mpg.PRD_GRP_SEQ\r\n"
         * + "                                    and ma.USER_ID = :userid\r\n" +
         * "                                    and to_date(dvh.DSBMT_DT) between to_date(:frmdt,'dd-mm-yyyy') and to_date(:todt,'dd-mm-yyyy')\r\n"
         * +
         * "        group by reg.reg_nm,area.area_nm,mb.BRNCH_NM,to_char(dvh.DSBMT_DT,'dd-mm-yyyy') , mpg.PRD_GRP_NM,\r\n"
         * +
         * "            (case when mt.TYP_ID = '0008' THEN 'BANK' ELSE MT.TYP_STR END), \r\n"
         * + "            to_date(dvh.DSBMT_DT),mpg.PRD_GRP_SEQ" ) .setParameter(
         * "frmdt", frmDt ).setParameter( "todt", toDt ).setParameter( "userid", userId
         * );
         */

        List<Object[]> Obj = rs.getResultList();

        List<Map<String, ?>> recovery = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REG_NM", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NM", l[1] == null ? "" : l[1].toString());
            map.put("BRNCH_NM", l[2] == null ? "" : l[2].toString());
            map.put("DISB_DT", l[3] == null ? "" : l[3].toString());
            map.put("PRD_GRP_NM", l[4] == null ? "" : l[4].toString());
            map.put("TYP_STR", l[5] == null ? "" : l[5].toString());
            map.put("CLNTS", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("DISB_AMT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            recovery.add(map);
        });
        return reportComponent.generateReport(ORG_TAGGING_TIME_REPORT, params, getJRDataSource(recovery));
    }

    public byte[] getReversalEnteriesReport(String frmdt, String todt, long brnch, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnch);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", user);
        params.put("from_dt", frmdt);
        params.put("to_dt", todt);
        String ql;
        ql = readFile(Charset.defaultCharset(), "ReversalEnteries.txt");
        Query rs = em.createNativeQuery(ql).setParameter("frmdt", frmdt).setParameter("todt", todt)
                .setParameter("brnch", brnch);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("GL_ACCT_NUM", w[0] == null ? "" : w[0].toString());
            parm.put("TYP_STR", w[1] == null ? "" : w[1].toString());
            parm.put("JV_TYPE", w[2] == null ? "" : w[2].toString());
            parm.put("CLIENT_ID", w[3] == null ? "" : w[3].toString());
            // parm.put( "CLIENT_ID", w[ 3 ] == null ? 0 : new BigDecimal( w[ 3 ].toString()
            // ).longValue() );
            parm.put("INSTR_NO", w[4] == null ? "" : w[4].toString());
            parm.put("ENTY_TYP", w[5] == null ? "" : w[5].toString());
            parm.put("JV_DSCR", w[6] == null ? "" : w[6].toString());
            parm.put("ACTUAL_JV_HDR_SEQ", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("ACTUAL_REF", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("ACTUAL_VALUE_DT", w[9] == null ? "" : w[9].toString());
            parm.put("ACTUAL_DEBIT", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("ACTUAL_CREDIT", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("JV_HDR_SEQ", w[12] == null ? 0 : new BigDecimal(w[12].toString()).longValue());
            parm.put("REV_REF_KEY", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("REVERSAL_DT", w[14] == null ? "" : w[14].toString());
            parm.put("REV_DEBIT", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("REV_CREDIT", w[16] == null ? 0 : new BigDecimal(w[16].toString()).longValue());
            expList.add(parm);
        });

        return reportComponent.generateReport(REVRSAL_ENTERIES_REPORT, params, getJRDataSource(expList));
    }

    public byte[] getTrailBalance(String frmdt, String todt, long brnch, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnch);
        Object[] obj = (Object[]) bi.getSingleResult();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", user);
        params.put("from_dt", frmdt);
        params.put("to_dt", todt);
        String ql;
        ql = readFile(Charset.defaultCharset(), "Trail_Balance_Report.txt");
        Query rs = em.createNativeQuery(ql).setParameter("frmdt", frmdt).setParameter("todt", todt)
                .setParameter("brnch", brnch);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("ACCOUNT_CODE", w[0] == null ? "" : w[0].toString());
            parm.put("LAGACY_CODE", w[1] == null ? "" : w[1].toString());
            parm.put("LAGACY_DESC", w[2] == null ? "" : w[2].toString());
            parm.put("DEBIT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("CREDI", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            expList.add(parm);
        });

        return reportComponent.generateReport(TRAIL_BALANCE_REPORT, params, getJRDataSource(expList));
    }

    public byte[] getBrnchRnkngReport(long vstseq, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "BranchRanking.txt");
        Query rs = em.createNativeQuery(ql).setParameter("vstseq", vstseq);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REF_CD_DSCR", w[0] == null ? "" : w[0].toString());
            parm.put("CTGRY_NM", w[1] == null ? "" : w[1].toString());
            parm.put("CTGRY_CMNT", w[2] == null ? "" : w[2].toString());
            parm.put("CTGRY_SCR", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("VST_SCR", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("COUNTKEYS", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("ACHVD", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("ISU_NM", w[7] == null ? "" : w[7].toString());
            parm.put("ISU_STS", w[8] == null ? "" : w[8].toString());
            parm.put("ISU_CMNT", w[9] == null ? "" : w[9].toString());

            expList.add(parm);
        });

        return reportComponent.generateReport(BRANCH_RANKING, params, getJRDataSource(expList));
    }

    public byte[] getCpcReport(long vstseq, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "CpcReport.txt");
        Query rs = em.createNativeQuery(ql).setParameter("vstseq", vstseq);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("CLNT_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_ID", w[1] == null ? "" : w[1].toString());
            parm.put("BDO_NM", w[2] == null ? "" : w[2].toString());
            parm.put("CLNT_CTGRY", w[3] == null ? "" : w[3].toString());
            parm.put("DT_OF_VST", w[4] == null ? "" : w[4].toString());
            parm.put("QST_STR", w[5] == null ? "" : w[5].toString());
            parm.put("ANSWR_STR", w[6] == null ? "" : w[6].toString());
            parm.put("CMNT", w[7] == null ? "" : w[7].toString());
            expList.add(parm);
        });

        return reportComponent.generateReport(CPC_REPORT, params, getJRDataSource(expList));
    }

    public byte[] getMonthlyRanking(long vst_seq, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "MonthyRankReport.txt");
        Query rs = em.createNativeQuery(ql).setParameter("vst_seq", vst_seq);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REG_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_ID", w[1] == null ? "" : w[1].toString());
            parm.put("VSTD_CLNTS", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("REF_CD_DSCR", w[3] == null ? "" : w[3].toString());
            parm.put("SCR", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("REF_CD_DSCR", w[5] == null ? "" : w[5].toString());
            expList.add(parm);
        });

        return reportComponent.generateReport(MONTHLY_RANKING, params, getJRDataSource(expList));
    }

    public byte[] getacigReport(long trng_seq, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "ACIGREPORT.txt");
        Query rs = em.createNativeQuery(ql).setParameter("trng_seq", trng_seq);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("TRN_DT", w[0] == null ? "" : w[0].toString());
            parm.put("REG_NM", w[1] == null ? "" : w[1].toString());
            parm.put("AREA_NM", w[2] == null ? "" : w[2].toString());
            parm.put("TRNR_NM", w[3] == null ? "" : w[3].toString());
            parm.put("BRNCH_NM", w[4] == null ? "" : w[4].toString());
            parm.put("PRTCPNT_ID", w[5] == null ? "" : w[5].toString());
            parm.put("PRTCPNT_CNIC_NUM", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("GROUP1", w[7] == null ? "" : w[7].toString());
            parm.put("PRTCPNT_NM", w[8] == null ? "" : w[8].toString());
            parm.put("RELATION", w[9] == null ? "" : w[9].toString());
            parm.put("TRNG_STS_KEY", w[10] == null ? "" : w[10].toString().equals("1") ? "InActive" : "Active");

            expList.add(parm);
        });

        return reportComponent.generateReport(AICG_TRAINING, params, getJRDataSource(expList));
    }

    public byte[] getGesaSFE(long trng_seq, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "GESA.txt");
        Query rs = em.createNativeQuery(ql).setParameter("trng_seq", trng_seq);

        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> expList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("TRNR_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_ID", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_NM", w[2] == null ? "" : w[2].toString());
            parm.put("TRN_DT", w[3] == null ? "" : w[3].toString());

            expList.add(parm);
        });

        return reportComponent.generateReport(GESA, params, getJRDataSource(expList));
    }

    // Read Query files
    private String readFile(Charset encoding, String fileName) {

        String QUERY_FILE_PATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator
                + "reports" + file.separator + "queries" + file.separator;
        //String QUERY_FILE_PATH = "D:\\Program.Files\\Reports for MWX\\reports\\queries\\";
        //String QUERY_FILE_PATH = "D:\\GIT-MWX\\MW-RDS\\src\\main\\resources\\reports\\queries\\";
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(Paths.get(QUERY_FILE_PATH + fileName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    public byte[] getPARMD(String toDt, String user) {
        Map<String, Object> params = new HashMap<>();

        // params.put( "frm_dt", frmDt );
        params.put("to_dt", toDt);

        // MD Report PAR Region wise
        Query regionWiseParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "RegionWiseParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> regionWiseParRs = regionWiseParQuery.getResultList();

        List<Map<String, ?>> regionWiseParData = new ArrayList();
        regionWiseParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            // parm.put( "REGION_NAME", w[ 0 ] == null ? "" : w[ 0 ].toString() );
            if (w[0].equals("AAOverAll")) {
                parm.put("REGION_NAME", w[0] == null ? "" : w[0].toString().substring(2));
            } else {
                parm.put("REGION_NAME", w[0] == null ? "" : w[0].toString());
            }
            parm.put("PAR_1_PERC", w[1] == null ? 0 : new BigDecimal(w[1].toString()).doubleValue());
            parm.put("PAR_11_PERC", w[2] == null ? 0 : new BigDecimal(w[2].toString()).doubleValue());
            parm.put("PAR_5_PERC", w[3] == null ? 0 : new BigDecimal(w[3].toString()).doubleValue());
            parm.put("PAR_51_PERC", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("PAR_15_PERC", w[5] == null ? 0 : new BigDecimal(w[5].toString()).doubleValue());
            parm.put("PAR_151_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("PAR_30_PERC", w[7] == null ? 0 : new BigDecimal(w[7].toString()).doubleValue());
            parm.put("PAR_301_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("PAR_45_PERC", w[9] == null ? 0 : new BigDecimal(w[9].toString()).doubleValue());
            parm.put("PAR_451_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("PAR_60_PERC", w[11] == null ? 0 : new BigDecimal(w[11].toString()).doubleValue());
            parm.put("PAR_601_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            parm.put("PAR_90_PERC", w[13] == null ? 0 : new BigDecimal(w[13].toString()).doubleValue());
            parm.put("PAR_901_PERC", w[14] == null ? 0 : new BigDecimal(w[14].toString()).doubleValue());
            parm.put("PAR_91_PERC", w[15] == null ? 0 : new BigDecimal(w[15].toString()).doubleValue());
            parm.put("PAR_911_PERC", w[16] == null ? 0 : new BigDecimal(w[16].toString()).doubleValue());
            regionWiseParData.add(parm);
        });

        params.put("reg_data", getJRDataSource(regionWiseParData));

        // MD Report PAR Region wise
        Query prdWiseParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "PrdWiseParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> prdWiseParRs = prdWiseParQuery.getResultList();

        List<Map<String, ?>> prdWiseParData = new ArrayList();
        prdWiseParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            if (w[0].equals("AAOverall")) {
                parm.put("PRODUCT_DESC", w[0] == null ? "" : w[0].toString().substring(2));
            } else {
                parm.put("PRODUCT_DESC", w[0] == null ? "" : w[0].toString());
            }
            // parm.put( "PRODUCT_DESC", w[ 0 ] == null ? "" : w[ 0 ].toString() );
            parm.put("PAR_1_PERC", w[1] == null ? 0 : new BigDecimal(w[1].toString()).doubleValue());
            parm.put("PAR_11_PERC", w[2] == null ? 0 : new BigDecimal(w[2].toString()).doubleValue());
            parm.put("PAR_5_PERC", w[3] == null ? 0 : new BigDecimal(w[3].toString()).doubleValue());
            parm.put("PAR_51_PERC", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("PAR_15_PERC", w[5] == null ? 0 : new BigDecimal(w[5].toString()).doubleValue());
            parm.put("PAR_151_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("PAR_30_PERC", w[7] == null ? 0 : new BigDecimal(w[7].toString()).doubleValue());
            parm.put("PAR_301_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("PAR_45_PERC", w[9] == null ? 0 : new BigDecimal(w[9].toString()).doubleValue());
            parm.put("PAR_451_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("PAR_60_PERC", w[11] == null ? 0 : new BigDecimal(w[11].toString()).doubleValue());
            parm.put("PAR_601_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            parm.put("PAR_90_PERC", w[13] == null ? 0 : new BigDecimal(w[13].toString()).doubleValue());
            parm.put("PAR_901_PERC", w[14] == null ? 0 : new BigDecimal(w[14].toString()).doubleValue());
            parm.put("PAR_91_PERC", w[15] == null ? 0 : new BigDecimal(w[15].toString()).doubleValue());
            parm.put("PAR_911_PERC", w[16] == null ? 0 : new BigDecimal(w[16].toString()).doubleValue());
            prdWiseParData.add(parm);
        });

        params.put("prd_data", getJRDataSource(prdWiseParData));

        // MD Report PAR Loan Cycle wise
        Query loanCycleParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "LoanCycleParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> loanCycleParRs = loanCycleParQuery.getResultList();

        List<Map<String, ?>> loanCycleParData = new ArrayList();
        loanCycleParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            if (w[0].toString().equals("0")) {
                parm.put("LOAN_CYCLE_CD", "Overall");
            } else if (w[0].toString().equals("99")) {
                parm.put("LOAN_CYCLE_CD", "Above 10");
            } else {
                parm.put("LOAN_CYCLE_CD", w[0] == null ? "" : w[0].toString());
            }
            // parm.put( "LOAN_CYCLE_CD", w[ 0 ] == null ? "" : w[ 0 ].toString() );
            parm.put("OD_P1", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("PAR_1_PERC", w[2] == null ? 0 : new BigDecimal(w[2].toString()).doubleValue());
            parm.put("OD_P5", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("PAR_5_PERC", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("OD_P15", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("PAR_15_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("OD_P30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("PAR_30_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("OD_P45", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("PAR_45_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("OD_P60", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("PAR_60_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            parm.put("OD_P90", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("PAR_90_PERC", w[14] == null ? 0 : new BigDecimal(w[14].toString()).doubleValue());
            parm.put("OD_P91", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("PAR_91_PERC", w[16] == null ? 0 : new BigDecimal(w[16].toString()).doubleValue());
            loanCycleParData.add(parm);
        });

        params.put("lc_data", getJRDataSource(loanCycleParData));

        // MD Report PAR Amount wise
        Query amtWiseParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "AmtWiseParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> amtWiseParRs = amtWiseParQuery.getResultList();

        List<Map<String, ?>> amtWiseParData = new ArrayList();
        amtWiseParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            if (w[0].equals("AAOverall")) {
                parm.put("DISBURSED_AMOUNT", "Overall");
            } else if (w[0].equals("disb_4k")) {
                parm.put("DISBURSED_AMOUNT", "20,000 - 40,000");
            } else if (w[0].equals("disb_6k")) {
                parm.put("DISBURSED_AMOUNT", "41,000 - 60,000");
            } else if (w[0].equals("disb_8k")) {
                parm.put("DISBURSED_AMOUNT", "60,001 - 80,000");
            } else if (w[0].equals("disb_8kk")) {
                parm.put("DISBURSED_AMOUNT", "80,001 - 100,000");
            } else if (w[0].equals("disb_91k")) {
                parm.put("DISBURSED_AMOUNT", "Above 100,000");
            }
            parm.put("OD_P1", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("PAR_1_PERC", w[2] == null ? 0 : new BigDecimal(w[2].toString()).doubleValue());
            parm.put("OD_P5", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("PAR_5_PERC", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("OD_P15", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("PAR_15_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("OD_P30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("PAR_30_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("OD_P45", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("PAR_45_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("OD_P60", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("PAR_60_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            parm.put("OD_P90", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("PAR_90_PERC", w[14] == null ? 0 : new BigDecimal(w[14].toString()).doubleValue());
            parm.put("OD_P91", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("PAR_91_PERC", w[16] == null ? 0 : new BigDecimal(w[16].toString()).doubleValue());
            amtWiseParData.add(parm);
        });

        params.put("amt_data", getJRDataSource(amtWiseParData));

        // MD Report PAR Loan USer wise
        Query loanUsrParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "LoanUsrParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> loanUsrParRs = loanUsrParQuery.getResultList();

        List<Map<String, ?>> loanUsrParData = new ArrayList();
        loanUsrParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("LOAN_USER", w[0] == null ? "" : w[0].toString());
            parm.put("OD_P1", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("PAR_1_PERC", w[2] == null ? 0 : new BigDecimal(w[2].toString()).doubleValue());
            parm.put("OD_P5", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("PAR_5_PERC", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("OD_P15", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("PAR_15_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("OD_P30", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("PAR_30_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("OD_P45", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("PAR_45_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("OD_P60", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());
            parm.put("PAR_60_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            parm.put("OD_P90", w[13] == null ? 0 : new BigDecimal(w[13].toString()).longValue());
            parm.put("PAR_90_PERC", w[14] == null ? 0 : new BigDecimal(w[14].toString()).doubleValue());
            parm.put("OD_P91", w[15] == null ? 0 : new BigDecimal(w[15].toString()).longValue());
            parm.put("PAR_91_PERC", w[16] == null ? 0 : new BigDecimal(w[16].toString()).doubleValue());
            loanUsrParData.add(parm);
        });

        params.put("user_data", getJRDataSource(loanUsrParData));

        // MD Report PAR Reagion wise Od
        Query regOdParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "RegOdParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> regOdParRs = regOdParQuery.getResultList();

        List<Map<String, ?>> regOdParData = new ArrayList();
        regOdParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REGION_NAME", w[0] == null ? "" : w[0].toString());
            parm.put("ACTIVE_CLIENTS", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("REGULAR_OD_CLIENTS", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("OUTSTANDING", w[3] == null ? 0 : new BigDecimal(w[3].toString()).doubleValue());
            parm.put("REGULAR_OD_AMOUNT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).doubleValue());
            parm.put("PAR_1_PERC", w[5] == null ? 0 : new BigDecimal(w[5].toString()).doubleValue());
            parm.put("PAR_5_PERC", w[6] == null ? 0 : new BigDecimal(w[6].toString()).doubleValue());
            parm.put("PAR_15_PERC", w[7] == null ? 0 : new BigDecimal(w[7].toString()).doubleValue());
            parm.put("PAR_30_PERC", w[8] == null ? 0 : new BigDecimal(w[8].toString()).doubleValue());
            parm.put("PAR_45_PERC", w[9] == null ? 0 : new BigDecimal(w[9].toString()).doubleValue());
            parm.put("PAR_60_PERC", w[10] == null ? 0 : new BigDecimal(w[10].toString()).doubleValue());
            parm.put("PAR_90_PERC", w[11] == null ? 0 : new BigDecimal(w[11].toString()).doubleValue());
            parm.put("PAR_91_PERC", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            regOdParData.add(parm);
        });

        params.put("reg_od_data", getJRDataSource(regOdParData));

        // MD Report PAR Reagion wise branch
        Query regbrnchOdParQuery = em.createNativeQuery(readFile(Charset.defaultCharset(), "RegbrnchOdParQuery.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> regbrnchOdParRs = regbrnchOdParQuery.getResultList();

        List<Map<String, ?>> regbrnchOdParData = new ArrayList();
        regbrnchOdParRs.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REGION_NAME", w[0] == null ? "" : w[0].toString());
            parm.put("P_5", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("P_2", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("P_3", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("P_5_1", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("P_6", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            regbrnchOdParData.add(parm);
        });

        params.put("reg_od_brnch_data", getJRDataSource(regbrnchOdParData));

        // MD Report PAR Reagion wise branch 2
        Query regbrnchOdParQuery2 = em.createNativeQuery(readFile(Charset.defaultCharset(), "RegbrnchOdParQuery2.txt"))
                .setParameter("to_dt", toDt);

        List<Object[]> regbrnchOdParRs2 = regbrnchOdParQuery2.getResultList();

        List<Map<String, ?>> regbrnchOdParData2 = new ArrayList();
        regbrnchOdParRs2.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("REGION_NAME", w[0] == null ? "" : w[0].toString());
            parm.put("P_1", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("P_2", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("P_3", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("P_4", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            regbrnchOdParData2.add(parm);
        });

        params.put("reg_od_brnch_data2", getJRDataSource(regbrnchOdParData2));

        return reportComponent.generateReport(PAR_REPORT, params, null);
    }

    public byte[] getAdvanceRecoveryReport(String frmdt, String todt, String user) throws IOException {
        Map<String, Object> params = new HashMap<>();
        /*
         * Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
         * ).setParameter( "userId", user ); Object[] obj = ( Object[] )
         * bi.getSingleResult(); params.put( "reg_nm", obj[ 0 ].toString() );
         * params.put( "area_nm", obj[ 1 ].toString() ); params.put( "brnch_nm", obj[ 2
         * ].toString() ); params.put( "brnch_cd", obj[ 3 ].toString() );
         */
        params.put("curr_user", user);
        params.put("from_dt", frmdt);
        params.put("to_dt", todt);
        String ql;
        ql = readFile(Charset.defaultCharset(), "AdvanceRecovery.txt");
        Query rs = em.createNativeQuery(ql).setParameter("frmdt", frmdt).setParameter("todt", todt).setParameter("user",
                user);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("'ZZOVERALL'", w[0] == null ? "" : w[0].toString());
            parm.put("'ZZOVERALL_1'", w[1] == null ? "" : w[1].toString());
            parm.put("SM_DAY_CNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("ADV_1_3", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("ADV_4_6", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("ADV_7_10", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("ADV_OVR_10", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            recList.add(parm);
        });

        return reportComponent.generateReport(ADVANCE_RECOVERY_REPORT, params, getJRDataSource(recList));
    }

    public byte[] getAdvanceClientReport(String frmdt, String todt, String user, long rpt_flg) throws IOException {
        Map<String, Object> params = new HashMap<>();
        /*
         * Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
         * ).setParameter( "userId", user ); Object[] obj = ( Object[] )
         * bi.getSingleResult(); params.put( "reg_nm", obj[ 0 ].toString() );
         * params.put( "area_nm", obj[ 1 ].toString() ); params.put( "brnch_nm", obj[ 2
         * ].toString() ); params.put( "brnch_cd", obj[ 3 ].toString() );
         */
        params.put("curr_user", user);
        params.put("from_dt", frmdt);
        params.put("to_dt", todt);
        String ql;
        ql = readFile(Charset.defaultCharset(), "AdvClientRecovery.txt");
        Query rs = em.createNativeQuery(ql).setParameter("frmdt", frmdt).setParameter("todt", todt).setParameter("user",
                user);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("BRNCH_NM", w[0] == null ? "" : w[0].toString());
            parm.put("AREA_NM", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_NM", w[2] == null ? "" : w[2].toString());
            parm.put("CLNT_ID", w[3] == null ? "" : w[3].toString());
            parm.put("CLNT_CNTCT", w[4] == null ? "" : w[4].toString());
            parm.put("PRD_NM", w[5] == null ? "" : w[5].toString());
            parm.put("LOAN_CYCL_NUM", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("APRVD_LOAN_AMT", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("INST_NUM", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("OD_INS", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());
            parm.put("OST_AMT", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            recList.add(parm);
        });

        return reportComponent.generateReport(ADVANCE_CLIENT_REPORT, params, getJRDataSource(recList));
    }

    public byte[] getAdvanceMaturityReport(String as_dt, String user, long rpt_flg) throws IOException {
        Map<String, Object> params = new HashMap<>();
        /*
         * Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
         * ).setParameter( "userId", user ); Object[] obj = ( Object[] )
         * bi.getSingleResult(); params.put( "reg_nm", obj[ 0 ].toString() );
         * params.put( "area_nm", obj[ 1 ].toString() ); params.put( "brnch_nm", obj[ 2
         * ].toString() ); params.put( "brnch_cd", obj[ 3 ].toString() );
         */
        params.put("as_dt", as_dt);
        params.put("curr_user", user);
        String ql;
        ql = readFile(Charset.defaultCharset(), "AdvanceMaturity.txt");
        Query rs = em.createNativeQuery(ql).setParameter("as_dt", as_dt).setParameter("user", user)
                .setParameter("rpt_flg", rpt_flg);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();

            if (w[0].equals("zzzOverall")) {
                parm.put("GRP_NM", w[0] == null ? "" : w[0].toString().substring(3));
            } else {
                parm.put("GRP_NM", w[0] == null ? "" : w[0].toString());
            }

            parm.put("MAT_15_30", w[1] == null ? 0 : new BigDecimal(w[1].toString()).longValue());
            parm.put("MAT_31_60", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("MAT_61_90", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("MAT_61_90_1", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            recList.add(parm);
        });

        return reportComponent.generateReport(ADVANCE_MATURITY_REPORT, params, getJRDataSource(recList));
    }

    public byte[] getWeeklyTargetReport(String as_dt, String userid) throws IOException {
        Map<String, Object> params = new HashMap<>();
        /*
         * Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
         * ).setParameter( "userId", userid ); Object[] obj = ( Object[] )
         * bi.getSingleResult(); params.put( "reg_nm", obj[ 0 ].toString() );
         * params.put( "area_nm", obj[ 1 ].toString() ); params.put( "brnch_nm", obj[ 2
         * ].toString() ); params.put( "brnch_cd", obj[ 3 ].toString() );
         */
        params.put("as_dt", as_dt);
        params.put("curr_user", userid);
        String ql;
        ql = readFile(Charset.defaultCharset(), "WeeklyTarget.txt");
        Query rs = em.createNativeQuery(ql).setParameter("as_dt", as_dt).setParameter("userid", userid);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("AREA_NM", w[0] == null ? "" : w[0].toString());
            parm.put("BRNCH_NM", w[1] == null ? "" : w[1].toString());
            parm.put("WEEKS", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("PRD_GRP_NM", w[3] == null ? "" : w[3].toString());
            parm.put("ACHVD_CLNT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("ACHVD_AMT", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            recList.add(parm);
        });

        return reportComponent.generateReport(WEEKLY_TARGET_REPORT, params, getJRDataSource(recList));
    }

    public byte[] getAreaDisbursReport(String as_dt, String userid) throws IOException {
        Map<String, Object> params = new HashMap<>();
        /*
         * Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
         * ).setParameter( "userId", userid ); Object[] obj = ( Object[] )
         * bi.getSingleResult(); params.put( "reg_nm", obj[ 0 ].toString() );
         * params.put( "area_nm", obj[ 1 ].toString() ); params.put( "brnch_nm", obj[ 2
         * ].toString() ); params.put( "brnch_cd", obj[ 3 ].toString() );
         */
        params.put("as_dt", as_dt);
        params.put("curr_user", userid);
        String ql;
        ql = readFile(Charset.defaultCharset(), "AreaDisbReport.txt");
        Query rs = em.createNativeQuery(ql).setParameter("as_dt", as_dt).setParameter("userid", userid);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("AREA_NM", w[0] == null ? "" : w[0].toString());
            parm.put("BRNCH_NM", w[1] == null ? "" : w[1].toString());
            parm.put("PRD_GRP_NM", w[2] == null ? "" : w[2].toString());
            parm.put("TRGT_CLNTS", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());
            parm.put("ACHVD_CLNT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("TRGT_AMT", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("ACHVD_AMT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            recList.add(parm);
        });

        return reportComponent.generateReport(AREA_DISBURSEMENT_REPORT, params, getJRDataSource(recList));
    }

    public byte[] getLeaveAndAttendanceMonitoringReport(String frmDt, String toDt, String userId, long rpt_flg)
            throws IOException {

	    /*
		 	UPDATED BY YOUSAF AS ATTANDANCE REPORT IS AVAILABALE IN HARMONY
		 */


		/*Map<String, Object> params = new HashMap<>();

		if (rpt_flg == 1) {

			Query headerQuery = em.createNativeQuery("select e.EMPLOYEE_ID,e.FIRST_NAME,\r\n"
					+ "       dl.DESCRIPTION detail_location, \r\n" + "       ml.DESCRIPTION master_location,\r\n"
					+ "       r.REGION_NAME\r\n" + "from \r\n" + "    mw_emp emp, \r\n"
					+ "	employee@DBLINK_NEWMW_TO_H0DB1 e,\r\n" + "	detail_Location@DBLINK_NEWMW_TO_H0DB1 dl,\r\n"
					+ "	master_location@DBLINK_NEWMW_TO_H0DB1 ml,\r\n" + "	region@DBLINK_NEWMW_TO_H0DB1 r\r\n"
					+ "	\r\n" + "where\r\n" + "emp.hrid=e.employee_id \r\n"
					+ "and e.DETAIL_LOCATION_ID = dl.DETAIL_LOCATION_ID\r\n"
					+ "and e.MASTER_LOCATION_ID = ml.MASTER_LOCATION_ID\r\n" + "and ml.REGION_CD = r.REGION_CD\r\n"
					+ "and dl.ACTIVE = 'Y'\r\n" + "and ml.ACTIVE = 'Y'\r\n" + "and emp.emp_lan_id=:emplnId")
					.setParameter("emplnId", userId);

			// Query bi = em.createNativeQuery( com.idev4.rds.util.Queries.BRANCH_INFO
			// ).setParameter( "userId", userId );
			Object[] obj = (Object[]) headerQuery.getSingleResult();
			params.put("emp_id", obj[0].toString());
			params.put("emp_nm", obj[1].toString());
			params.put("brnch_nm", obj[2].toString());
			params.put("area_nm", obj[3].toString());
			params.put("reg_nm", obj[4].toString());
		}
		params.put("curr_user", userId);
		params.put("from_dt", frmDt);
		params.put("to_dt", toDt);

		Query brnchDetil = em
				.createNativeQuery("Select A.detail_location_id, B.master_location_id , A.region_cd \r\n"
						+ "from EMPLOYEE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK A\r\n"
						+ "join detail_location@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK B \r\n"
						+ "ON A.detail_location_id=B.detail_location_id\r\n" + "join mw_emp C \r\n"
						+ "on c.hrid=A.employee_id\r\n" + "and C.emp_lan_id=:userId\r\n" + "and B.active='Y'")
				.setParameter("userId", userId);

		Object[] brnchObjet = (Object[]) brnchDetil.getSingleResult();
		String detLoc = brnchObjet[0].toString();
		String mstLoc = brnchObjet[1].toString();
		String region = brnchObjet[2].toString();

		Query bodyQuery = em.createNativeQuery("select \r\n" + "' '||B.BRANCH AS BRANCH,\r\n"
				+ "' '||b.AREA AS AREA,\r\n" + "' '||B.REGION AS REGION,\r\n" + "B.HRID,\r\n"
				+ "' '||B.NAME AS NAME,\r\n" + "' '||B.DESIGNATION AS DESIGNATION,\r\n" + "B.NDATE DATES,\r\n"
				+ "B.OPBAL_AL,\r\n" + "B.OPBAL_ML,\r\n" + "B.OPBAL_CL,\r\n"
				+ "nvl(B.opbal_al,0)-nvl(B.avail_al,0) - nvl((SELECT ld.no_of_leave_deduct\r\n"
				+ "                                        FROM leave_deduction@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK ld\r\n"
				+ "                                        WHERE ld.POST = 'Y'\r\n"
				+ "                                        AND LD.LEAVE_ID=1\r\n"
				+ "                                        AND LD.employee_id = B.HRID\r\n"
				+ "                                        AND ld.from_date Between to_date(:from_date,'dd-MM-yyyy') AND to_date(:to_date,'dd-MM-yyyy')\r\n"
				+ "                                        ),0) clbal_al,\r\n"
				+ "nvl(B.opbal_ml,0)-nvl(B.avail_ml,0) clbal_ml,\r\n"
				+ "nvl(B.opbal_cl,0)-nvl(B.avail_cl,0) clbal_cl,\r\n" + "ev.SHORT_DESC status,\r\n"
				+ "ev.TIME_IN TIMEIN,\r\n" + "ev.TIMEOUT TIME_OUT,\r\n" + "TO_CHAR(B.NDATE,'YYMMDD') ADATE,\r\n"
				+ "b.HRIDS\r\n" + "from \r\n" + "(select \r\n" + "dl.DETAIL_LOCATION_ID,\r\n"
				+ "dl.MASTER_LOCATION_ID,\r\n" + "initcap(dl.DESCRIPTION) branch,\r\n"
				+ "initcap(ml.DESCRIPTION) area,\r\n" + "initcap(r.Region_name) Region,\r\n" + "e.EMPLOYEE_ID HRID,\r\n"
				+ "INITCAP (          e.first_name\r\n" + "                   || ' '\r\n"
				+ "                   || e.middle_name\r\n" + "                   || ' '\r\n"
				+ "                   || e.last_name\r\n" + "                  ) AS name,\r\n"
				+ "p.SHORT_DESC Designation,\r\n" + "subquery_name.ndate,\r\n" + "((SELECT nvl(LB.LOADEDBAL,0)\r\n"
				+ "--into OP_BAL \r\n" + "  FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "  where LB.employee_id = e.EMPLOYEE_ID\r\n" + "  AND LB.ACTIVE  = 'Y'\r\n"
				+ "  AND LB.POST = 'Y'\r\n" + "AND LB.LEAVE_ID = 1)-\r\n" + "(select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 1\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= (SELECT LB.TRANS_DATE\r\n"
				+ "                                                        FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "                                                        where LB.employee_id = e.EMPLOYEE_ID\r\n"
				+ "                                                        AND LB.ACTIVE  = 'Y'\r\n"
				+ "                                                        AND LB.POST = 'Y'\r\n"
				+ "                                                        AND LB.LEAVE_ID = 1\r\n"
				+ "                      )\r\n"
				+ "    and LQ.APPLICATION_DATE < to_date(:from_date,'dd-MM-yyyy')))) opbal_al,\r\n"
				+ "    ((SELECT nvl(LB.LOADEDBAL,0)\r\n" + "--into OP_BAL \r\n"
				+ "  FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "  where LB.employee_id = e.EMPLOYEE_ID\r\n" + "  AND LB.ACTIVE  = 'Y'\r\n"
				+ "  AND LB.POST = 'Y'\r\n" + "AND LB.LEAVE_ID = 2)-\r\n" + "(select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 2\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= (SELECT LB.TRANS_DATE\r\n"
				+ "                                                        FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "                                                        where LB.employee_id = e.EMPLOYEE_ID\r\n"
				+ "                                                        AND LB.ACTIVE  = 'Y'\r\n"
				+ "                                                        AND LB.POST = 'Y'\r\n"
				+ "                                                        AND LB.LEAVE_ID = 2\r\n"
				+ "                      )\r\n"
				+ "    and LQ.APPLICATION_DATE < to_date(:from_date,'dd-MM-yyyy')))) opbal_ml,\r\n"
				+ "     ((SELECT nvl(LB.LOADEDBAL,0)\r\n" + "--into OP_BAL \r\n"
				+ "  FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "  where LB.employee_id = e.EMPLOYEE_ID\r\n" + "  AND LB.ACTIVE  = 'Y'\r\n"
				+ "  AND LB.POST = 'Y'\r\n" + "AND LB.LEAVE_ID = 3)-\r\n" + "(select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 3\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= (SELECT LB.TRANS_DATE\r\n"
				+ "                                                        FROM LEAVE_BALANCE@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LB\r\n"
				+ "                                                        where LB.employee_id = e.EMPLOYEE_ID\r\n"
				+ "                                                        AND LB.ACTIVE  = 'Y'\r\n"
				+ "                                                        AND LB.POST = 'Y'\r\n"
				+ "                                                        AND LB.LEAVE_ID = 3\r\n"
				+ "                      )\r\n"
				+ "    and LQ.APPLICATION_DATE < to_date(:from_date,'dd-MM-yyyy')))) opbal_cl,\r\n"
				+ "  (select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "    FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 1\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= to_date(:from_date,'dd-MM-yyyy') and LQ.APPLICATION_DATE <= to_date(:to_date,'dd-MM-yyyy')))avail_al,\r\n"
				+ "    (select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "    FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 2\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= to_date(:from_date,'dd-MM-yyyy') and LQ.APPLICATION_DATE <= to_date(:to_date,'dd-MM-yyyy')))avail_ml,\r\n"
				+ "     (select NVL(SUM(LQ.TOTAL_DAYS),0) \r\n"
				+ "    FROM LEAVE_REQUEST@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK LQ\r\n"
				+ "    where Lq.employee_id = e.EMPLOYEE_ID\r\n" + "    AND  LQ.LEAVE_ID = 3\r\n"
				+ "    AND (LQ.APPLICATION_DATE >= to_date(:from_date,'dd-MM-yyyy') and LQ.APPLICATION_DATE <= to_date(:to_date,'dd-MM-yyyy')))avail_cl,\r\n"
				+ "TO_NUMBER(e.EMPLOYEE_ID) HRIDS\r\n" + "from \r\n"
				+ "region@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK r,\r\n"
				+ "employee@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK e,\r\n"
				+ "position@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK p,\r\n"
				+ "master_location@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK ml,\r\n"
				+ "detail_location@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK dl,\r\n" + "(          \r\n" + " SELECT NDATE\r\n"
				+ "FROM (\r\n" + "SELECT ROWNUM - 1 + to_date (to_date(:from_date,'dd-MM-yyyy'), 'dd-mon-yy') ndate\r\n"
				+ "  FROM all_tab_columns\r\n" + "   WHERE ROWNUM <= (               \r\n"
				+ "   SELECT TRIM (to_date(:to_date,'dd-MM-yyyy') - to_date(:from_date,'dd-MM-yyyy')) + 1\r\n"
				+ "  FROM DUAL                                          \r\n" + "  ))\r\n"
				+ "  WHERE LTRIM (RTRIM (TO_CHAR(NDATE,'DAY'))) NOT IN ('SATURDAY', 'SUNDAY')\r\n"
				+ ") subquery_name\r\n" + "where ml.REGION_CD=r.REGION_CD\r\n"
				+ "and dl.MASTER_LOCATION_ID=ml.MASTER_LOCATION_ID\r\n"
				+ "and e.MASTER_LOCATION_ID=ml.MASTER_LOCATION_ID\r\n"
				+ "and e.DETAIL_LOCATION_ID=dl.DETAIL_LOCATION_ID\r\n" + "and e.POSITION_ID=p.POSITION_ID\r\n"
				+ "and ml.ACTIVE='Y'\r\n" + "and dl.ACTIVE='Y'\r\n" + "and e.ACTIVE='Y'\r\n"
				+ "and LTRIM(UPPER(DL.DETAIL_LOCATION_ID))=(CASE when:det_loc='001' THEN  LTRIM(UPPER(DL.DETAIL_LOCATION_ID)) ELSE REPLACE(UPPER(:det_loc),'_',' ') END)\r\n"
				+ "and LTRIM(UPPER(ML.MASTER_LOCATION_ID))=(case when :mst_loc='001' THEN   LTRIM(UPPER(ML.MASTER_LOCATION_ID)) ELSE REPLACE(UPPER(:mst_loc),'_',' ')   END)\r\n"
				+ "AND LTRIM(UPPER(R.REGION_CD))=(CASE WHEN :regions='999' THEN LTRIM(UPPER(R.REGION_CD)) ELSE REPLACE(upper(:regions),'_',' ') END)\r\n"
				+ "--and e.EMPLOYEE_ID='04758'\r\n" + "--and initcap(dl.DESCRIPTION)='Qanchi'\r\n" + "and exists\r\n"
				+ "(select hr_branch_cd from hr_mw6_branch_maps@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK hmbm\r\n"
				+ "where hmbm.mw6_branch_cd is not null\r\n" + "and hmbm.hr_branch_cd=dl.DETAIL_LOCATION_ID))b,\r\n"
				+ "EMPLOYEE_ATTENDANCE_V@DBLINK_NEWMW_TO_H0DB1.KASHF.ORG.PK ev\r\n"
				+ "where b.HRID=ev.EMPLOYEE_ID(+)\r\n" + "--and   b.detail_location_id=ev.DETAIL_LOCATION_ID(+)\r\n"
				+ "and   b.NDATE=ev.ATT_DATE(+)").setParameter("from_date", frmDt).setParameter("to_date", toDt)
				.setParameter("regions", region).setParameter("mst_loc", mstLoc).setParameter("det_loc", detLoc);

		List<Object[]> qryObj = bodyQuery.getResultList();
		List<Map<String, ?>> result = new ArrayList();

		qryObj.forEach(data -> {
			Map<String, Object> mapList = new HashMap<>();

			mapList.put("HRID", data[3] == null ? "" : data[3].toString());
			mapList.put("NAME", data[4] == null ? "" : data[4].toString());
			mapList.put("DESIGNATION", data[5] == null ? "" : data[5].toString());
			mapList.put("DATES", data[6]);
			mapList.put("OPBAL_AL", data[7] == null ? 0 : new BigDecimal(data[7].toString()).longValue());
			mapList.put("OPBAL_ML", data[8] == null ? 0 : new BigDecimal(data[8].toString()).longValue());
			mapList.put("OPBAL_CL", data[9] == null ? 0 : new BigDecimal(data[9].toString()).doubleValue());

			mapList.put("CLBAL_AL", data[10] == null ? 0 : new BigDecimal(data[10].toString()).longValue());
			mapList.put("CLBAL_ML", data[11] == null ? 0 : new BigDecimal(data[11].toString()).longValue());
			mapList.put("CLBAL_CL", data[12] == null ? 0 : new BigDecimal(data[12].toString()).doubleValue());

			mapList.put("STATUS", data[13] == null ? "" : data[13].toString());
			mapList.put("TIMEIN", data[14] == null ? null : data[14].toString());
			mapList.put("TIME_OUT", data[15] == null ? "" : data[15].toString());

			result.add(mapList);
		});

		return reportComponent.generateReport(LEAVE_AND_ATTENDANCE_MONITORING_REPORT, params, getJRDataSource(result));*/
        return null;
    }

    public byte[] getSale2PendingReport(String fromDt, String toDt, long brnchSeq, String userId) throws IOException {

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                brnchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", fromDt);
        params.put("to_dt", toDt);

        String salePending;
        salePending = readFile(Charset.defaultCharset(), "SALE2PENDING.txt");
        Query rs = em.createNativeQuery(salePending).setParameter("from_dt", fromDt).setParameter("to_dt", toDt)
                .setParameter("brnch_cd", obj[4].toString()).setParameter("usrid", userId);

        List<Object[]> sale2PendingList = rs.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        sale2PendingList.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("EMP_NM", l[0] == null ? "" : l[0].toString());
            map.put("PRD_GRP_NM", l[1] == null ? "" : l[1].toString());
            map.put("CLNT_ID", l[2] == null ? "" : l[2].toString());
            map.put("CLNT_NM", l[3] == null ? "" : l[3].toString());
            map.put("HSBND_NM", l[4] == null ? "" : l[4].toString());
            map.put("APRVD_LOAN_AMT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("DSB_DT", l[6] == null ? "" : l[6].toString());
            map.put("PNDNG_DYS", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());

            reportParams.add(map);
        });
        params.put("dataset", getJRDataSource(reportParams));

        return reportComponent.generateReport(SALE_2_PENDING, params, getJRDataSource(reportParams));
    }

    public byte[] getAgencyInfo(long loanAppSeq, String user) throws IOException {

        String ql;
        ql = readFile(Charset.defaultCharset(), "AgencyInfo_1.txt");
        Query rs = em.createNativeQuery(ql).setParameter("loanAppSeq", loanAppSeq);

        // Object[] obj = ( Object[] ) rs.getSingleResult();
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", user);

        List<Object[]> result = rs.getResultList();
        List<Map<String, ?>> reportParams1 = new ArrayList();
        result.forEach(obj -> {
            Map<String, Object> map = new HashMap();
            params.put("curr_user", user);
            params.put("area", (obj[9] == null ? "" : obj[9].toString()));
            params.put("brnch", (obj[8] == null ? "" : obj[8].toString()));
            params.put("bdo", (obj[15] == null ? "" : obj[15].toString()));
            map.put("CLNT_ID", obj[0] == null ? "" : obj[0].toString());
            map.put("FRST_NM", ((obj[1] == null) ? "" : obj[1].toString()));
            map.put("LAST_NM", ((obj[2] == null) ? "" : obj[2].toString()));
            map.put("SFFNM", (obj[3] == null ? "" : obj[3].toString()));
            map.put("SFLNM", (obj[4] == null ? "" : obj[4].toString()));
            map.put("PORT_CD", (obj[5] == null ? "" : obj[5].toString()));
            map.put("PORT_NM", (obj[6] == null ? "" : obj[6].toString()));
            map.put("BRNCH_CD", (obj[7] == null ? "" : obj[7].toString()));
            map.put("BRNCH_NM", (obj[8] == null ? "" : obj[8].toString()));
            map.put("AREA_NM", (obj[9] == null ? "" : obj[9].toString()));
            map.put("REG_NM", (obj[10] == null ? "" : obj[10].toString()));
            map.put("PRD_NM", (obj[11] == null ? "" : obj[11].toString()));
            map.put("PRD_ID", (obj[12] == null ? "" : obj[12].toString()));
            map.put("RQSTD_LOAN_AMT", (obj[13] == null ? "" : new BigDecimal(obj[13].toString()).longValue()));
            map.put("APRVD_LOAN_AMT", (obj[14] == null ? "" : new BigDecimal(obj[14].toString()).longValue()));
            map.put("BDO", (obj[15] == null ? "" : obj[15].toString()));
            map.put("CLNT_DOB", (obj[16] == null ? "" : obj[16].toString()));
            map.put("CLNT_AGE", (obj[17] == null ? "" : obj[17].toString()));
            map.put("NOM_NM", (obj[18] == null ? "" : obj[18].toString()));
            map.put("NOM_CNIC", (obj[19] == null ? "" : obj[19].toString()));
            map.put("NOM_AGE", (obj[20] == null ? "" : obj[20].toString()));
            map.put("CBWR_NM", (obj[21] == null ? "" : obj[21].toString()));
            map.put("CBWR_CNIC", (obj[22] == null ? "" : obj[22].toString()));
            map.put("CBWR_AGE", (obj[23] == null ? "" : obj[23].toString()));
            map.put("PYMT_MODE", (obj[24] == null ? "" : obj[24].toString()));
            map.put("CNIC_NUM", (obj[25] == null ? "" : obj[25].toString()));
            map.put("NOM_DOB", (obj[26] == null ? "" : obj[26].toString()));
            map.put("CBWR_DOB", (obj[27] == null ? "" : obj[27].toString()));
            map.put("HSBND_CNIC", (obj[28] == null ? "" : obj[28].toString()));
            reportParams1.add(map);
        });

        String agencyInfo;
        agencyInfo = readFile(Charset.defaultCharset(), "AgencyInfo_2.txt");
        Query rs1 = em.createNativeQuery(agencyInfo).setParameter("loanAppSeq", loanAppSeq);

        List<Object[]> agencyInfoList = rs1.getResultList();
        List<Map<String, ?>> reportParams = new ArrayList();
        agencyInfoList.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("PRVDR_NM", l[0] == null ? "" : l[0].toString());
            map.put("ACCT_NUM", l[1] == null ? "" : l[1].toString());
            map.put("CHQ_NUM", l[2] == null ? "" : l[2].toString());
            map.put("AMT", l[3] == null ? "" : new BigDecimal(l[3].toString()).longValue());

            reportParams.add(map);
        });

        params.put("dataset", getJRDataSource(reportParams));

        return reportComponent.generateReport(AGENCY_INFO, params, getJRDataSource(reportParams1));
    }

    public byte[] getBmBdoRecoveryReport(String frmdt, String todt, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("curr_user", userId);
        params.put("from_dt", frmdt);
        params.put("to_dt", todt);

        Date frmDt = null;
        Date tooDt = null;
        try {
            frmDt = new SimpleDateFormat("dd-MM-yyyy").parse(frmdt);
            tooDt = new SimpleDateFormat("dd-MM-yyyy").parse(todt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        frmdt = new SimpleDateFormat("dd-MMM-yyyy").format(frmDt);
        todt = new SimpleDateFormat("dd-MMM-yyyy").format(tooDt);

        String script;
        script = readFile(Charset.defaultCharset(), "bmBdoRecovery.txt");
        Query bmBdoRecoveryQuery = em.createNativeQuery(script).setParameter("userId", userId)
                .setParameter("todt", todt).setParameter("frmdt", frmdt);
        List<Object[]> bmBdoRecoveryresult = bmBdoRecoveryQuery.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        bmBdoRecoveryresult.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("BRANCH", w[0] == null ? "" : w[0].toString());
            parm.put("BDO_NAME", w[1] == null ? "" : w[1].toString());

            parm.put("DUE_RECOVERY_COUNT", w[2] == null ? 0 : new BigDecimal(w[2].toString()).longValue());
            parm.put("MONTHLY_DUE_AMOUNT", w[3] == null ? 0 : new BigDecimal(w[3].toString()).longValue());

            parm.put("DUE_RECOVERED_COUNT", w[4] == null ? 0 : new BigDecimal(w[4].toString()).longValue());
            parm.put("DUE_RECOVERED", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());

            parm.put("OVERDUE_RECOVERED_COUNT", w[6] == null ? 0 : new BigDecimal(w[6].toString()).longValue());
            parm.put("OVERDUE_RECOVERED", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());

            parm.put("ADVANCE_RECOVERED_COUNT", w[8] == null ? 0 : new BigDecimal(w[8].toString()).longValue());
            parm.put("ADVANCE_RECOVERED", w[9] == null ? 0 : new BigDecimal(w[9].toString()).longValue());

            parm.put("TOTAL_RECOVERED_COUNT", w[10] == null ? 0 : new BigDecimal(w[10].toString()).longValue());
            parm.put("TOTAL_RECOVERED_AMOUNT", w[11] == null ? 0 : new BigDecimal(w[11].toString()).longValue());

            parm.put("RECOVERED_VS_MONTHLY_DUE", w[12] == null ? 0 : new BigDecimal(w[12].toString()).doubleValue());
            recList.add(parm);
        });
        return reportComponent.generateReport(BM_BDO_RECOVERY, params, getJRDataSource(recList));
    }

    public byte[] getClientLoanMaturityReport(String userId, String frmDt, String toDt, long branchSeq, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String ql;
        ql = readFile(Charset.defaultCharset(), "ClientsLoansMaturityReportScript.txt");
        Query rs = em.createNativeQuery(ql).setParameter("todt", toDt).setParameter("frmdt", frmDt)
                .setParameter("brnch_seq", branchSeq);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_SEQ", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_NM", w[2] == null ? "" : w[2].toString());
            parm.put("PH_NM", w[3] == null ? "" : w[3].toString());
            parm.put("LOAN_APP_SEQ", w[4] == null ? "" : w[4].toString());
            parm.put("DSBMT_DT", w[5] == null ? "" : w[5].toString());
            parm.put("CLNT_CNIC", w[6] == null ? "" : w[6].toString());
            parm.put("DSB_AMT", w[7] == null ? "" : w[7].toString());
            parm.put("OUTS", w[8] == null ? "" : w[8].toString());
            parm.put("OD_AMT", w[9] == null ? "" : w[9].toString());
            parm.put("OD_DYS", w[10] == null ? "" : w[10].toString());
            parm.put("REMITANCE_ACC_#", w[11] == null ? "" : w[11].toString());
            parm.put("REMITTANCE_TYP", w[12] == null ? "" : w[12].toString());
            parm.put("LOAN_MTURTY_DT", w[13] == null ? "" : w[13].toString());
            parm.put("BRNCH_NM", w[14] == null ? "" : w[14].toString());
            recList.add(parm);
        });

        if (isXls) {
            return reportComponent.generateReport(CLIENT_LOAN_MATRITY_EXCEL, params, getJRDataSource(recList), isXls);
        } else {
            return reportComponent.generateReport(CLIENT_LOAN_MATRITY, params, getJRDataSource(recList));
        }
    }

    public byte[] getMobileWalletDueReport(String userId, String frmDt, String toDt, long branchSeq, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String ql;
        ql = readFile(Charset.defaultCharset(), "MobileWalletDisbursementDueScript.txt");
        Query rs = em.createNativeQuery(ql).setParameter("todt", toDt).setParameter("frmdt", frmDt)
                .setParameter("brnch_seq", branchSeq);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0] == null ? "" : w[0].toString());
            parm.put("CLNT_SEQ", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_NM", w[2] == null ? "" : w[2].toString());
            parm.put("DSBMT_DT", w[3] == null ? "" : w[3].toString());
            parm.put("DUE_DT", w[4] == null ? "" : w[4].toString());
            parm.put("TOT_DUE", w[5] == null ? 0 : new BigDecimal(w[5].toString()).longValue());
            parm.put("REMITANCE_ACC_#", w[6] == null ? "" : w[6].toString());
            parm.put("REMITTANCE_TYP", w[7] == null ? "" : w[7].toString());
            parm.put("BRNCH_NM", w[8] == null ? "" : w[8].toString());

            recList.add(parm);
        });

        if (isXls) {
            return reportComponent.generateReport(MOBILE_WALLET_DUE_EXCEL, params, getJRDataSource(recList), isXls);
        } else {
            return reportComponent.generateReport(MOBILE_WALLET_DUE, params, getJRDataSource(recList));
        }
    }

    public byte[] getMobileWalletReport(String userId, String frmDt, String toDt, long branchSeq, boolean isXls) throws IOException {
        Map<String, Object> params = new HashMap<>();
        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.BRANCH_INFO_BY_BRANCH).setParameter("brnchSeq",
                branchSeq);
        Object[] obj = (Object[]) bi.getSingleResult();

        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String ql;
        ql = readFile(Charset.defaultCharset(), "MobileWalletDisbursementScript.txt");
        Query rs = em.createNativeQuery(ql).setParameter("todt", toDt).setParameter("frmdt", frmDt)
                .setParameter("brnch_seq", branchSeq);
        List<Object[]> result = rs.getResultList();

        List<Map<String, ?>> recList = new ArrayList();
        result.forEach(w -> {
            Map<String, Object> parm = new HashMap<>();
            parm.put("EMP_NM", w[0] == null ? "" : w[0].toString());
            parm.put("DSBMT_DT", w[1] == null ? "" : w[1].toString());
            parm.put("CLNT_SEQ", w[2] == null ? "" : w[2].toString());
            parm.put("CLNT_NM", w[3] == null ? "" : w[3].toString());
            parm.put("LOAN_APP_SEQ", w[4] == null ? "" : w[4].toString());
            parm.put("CLNT_CNIC", w[5] == null ? "" : w[5].toString());
            parm.put("REMITANCE_ACC_#", w[6] == null ? "" : w[6].toString());
            parm.put("AMOUNT", w[7] == null ? 0 : new BigDecimal(w[7].toString()).longValue());
            parm.put("REMITTANCE_TYP", w[8] == null ? "" : w[8].toString());
            parm.put("DT", w[9] == null ? "" : w[9].toString());
            parm.put("BRNCH_NM", w[10] == null ? "" : w[10].toString());

            recList.add(parm);
        });

        if (isXls) {
            return reportComponent.generateReport(MOBILE_WALLET_EXCEL, params, getJRDataSource(recList), isXls);
        } else {
            return reportComponent.generateReport(MOBILE_WALLET, params, getJRDataSource(recList));
        }
    }

    public byte[] getMcbRemmitanceDisbursementFundsReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "MCBRemittanceDisbursementFundsScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("VDATE", l[0] == null ? "" : l[0].toString());
            map.put("LOAN_APP_SEQ", l[1] == null ? "" : l[1].toString());
            map.put("CNIC_NUM", l[2] == null ? "" : l[2].toString());
            map.put("CNIC_EXPRY_DT", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_SEQ", l[4] == null ? "" : l[4].toString());
            map.put("NAME", l[5] == null ? "" : l[5].toString());
            map.put("BRNCH_NM", l[6] == null ? "" : l[6].toString());
            map.put("DISB_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("BANK", l[8] == null ? "" : l[8].toString());
            map.put("BANK_CODE", l[9] == null ? "" : l[9].toString());
            map.put("BANK_BRNCH", l[10] == null ? "" : l[10].toString());
            map.put("PRD_NM", l[11] == null ? "" : l[11].toString());
            map.put("TYP_STR", l[12] == null ? "" : l[12].toString());


            disbursements.add(map);
        });

        if (isXls) {
            return reportComponent.generateReport(MCB_FUNDS_EXCEL, params, getJRDataSource(disbursements), isXls);
        } else {
            return reportComponent.generateReport(MCB_FUNDS, params, getJRDataSource(disbursements));
        }
    }

    public byte[] getMcbRemmitanceDisbursementLoaderReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "MCBRemittanceDisbursmentfundsLoaderScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("BRNCH_NM", l[0] == null ? "" : l[0].toString());
            map.put("BANK", l[1] == null ? "" : l[1].toString());
            map.put("AMOUNT", l[3] == null ? 0 : new BigDecimal(l[3].toString()).longValue());
            disbursements.add(map);
        });

        if (isXls) {
            return reportComponent.generateReport(MCB_LOADER_EXCEL, params, getJRDataSource(disbursements), isXls);
        } else {
            return reportComponent.generateReport(MCB_LOADER, params, getJRDataSource(disbursements));
        }
    }

    public byte[] getMcbRemmitanceDisbursementLetterReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);
        String refDt = "";
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(toDt);
            DateFormat frmt = new SimpleDateFormat("yyyyMMdd");
            refDt = frmt.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        params.put("ref_dt", refDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "MCBRemittanceDisbursmentfundsLetterScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        Object Obj = set.getSingleResult();
        params.put("AMOUNT", Obj == null ? 0L : new BigDecimal(Obj.toString()).longValue());

        if (isXls) {
            return reportComponent.generateReport(MCB_LETTER_EXCEL, params, null, isXls);
        } else {
            return reportComponent.generateReport(MCB_LETTER, params, null);
        }
    }

    public byte[] getCheckDisbursementFundsReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "ChequesDisbursmentFunds.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSBMT_HDR_SEQ", l[0] == null ? 0 : new BigDecimal(l[0].toString()).longValue());
            map.put("REG_NM", l[1] == null ? "" : l[1].toString());
            map.put("AREA_NM", l[2] == null ? "" : l[2].toString());
            map.put("BRNCH_NM", l[3] == null ? "" : l[3].toString());
            map.put("ACCT_NUM", l[4] == null ? "" : l[4].toString());
            map.put("BANK", l[5] == null ? "" : l[5].toString());
            map.put("STATUS", l[6] == null ? "" : l[6].toString());
            map.put("BANK_BRNCH", l[7] == null ? "" : l[7].toString());
            map.put("BANK_CODE", l[8] == null ? "" : l[8].toString());
            map.put("DISB_AMOUNT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("CLIENTS", l[10] == null ? 0 : new BigDecimal(l[10].toString()).longValue());
            map.put("PRD_NM", l[11] == null ? "" : l[11].toString());
            map.put("TYP_STR", l[12] == null ? "" : l[12].toString());

            disbursements.add(map);
        });

        if (isXls) {
            return reportComponent.generateReport(CHEQUE_DISBURSMENT_EXCEL, params, getJRDataSource(disbursements), isXls);
        } else {
            return reportComponent.generateReport(CHEQUE_DISBURSMENT, params, getJRDataSource(disbursements));
        }
    }

    public byte[] getEasyPaisaFundsReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "EasyPaisaDisbursementsFundScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DSBMT_DT", l[0] == null ? "" : l[0].toString());
            map.put("BRNCH_NM", l[1] == null ? "" : l[1].toString());
            map.put("LOAN_ID", l[2] == null ? "" : l[2].toString());
            map.put("CLNT_SEQ_1", l[3] == null ? "" : l[3].toString());
            map.put("CLNT_SEQ", l[4] == null ? "" : l[4].toString());
            map.put("CLNT_NM", l[5] == null ? "" : l[5].toString());
            map.put("CLNT_CNIC", l[6] == null ? "" : l[6].toString());
            map.put("REMITANCE_ACC_#", l[7] == null ? "" : l[7].toString());
            map.put("DISB_AMOUNT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("REMITTANCE_TYP", l[9] == null ? "" : l[9].toString());

            disbursements.add(map);
        });

        if (isXls) {
            return reportComponent.generateReport(EASYPAISA_FUNDS_EXCEL, params, getJRDataSource(disbursements), isXls);
        } else {
            return reportComponent.generateReport(EASYPAISA_FUNDS, params, getJRDataSource(disbursements));
        }
    }

    public byte[] getEasyPaisaLoaderReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "EasyPaisaDisbursementsLoaderScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        List<Object[]> Obj = set.getResultList();

        List<Map<String, ?>> disbursements = new ArrayList();
        Obj.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("BRNCH_NM", l[0] == null ? "" : l[0].toString());
            map.put("DISB_AMOUNT", l[1] == null ? 0 : new BigDecimal(l[1].toString()).longValue());
            map.put("BANK", l[2] == null ? "" : l[2].toString());
            disbursements.add(map);
        });

        if (isXls) {
            return reportComponent.generateReport(EASYPAISA_LOADER_EXCEL, params, getJRDataSource(disbursements), isXls);
        } else {
            return reportComponent.generateReport(EASYPAISA_LOADER, params, getJRDataSource(disbursements));
        }
    }

    public byte[] getEasyPaisaLetterReport(String frmDt, String toDt, String userId, boolean isXls)
            throws IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", userId);
        params.put("from_dt", frmDt);
        params.put("to_dt", toDt);
        String refDt = "";
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(toDt);
            DateFormat frmt = new SimpleDateFormat("yyyyMMdd");
            refDt = frmt.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        params.put("ref_dt", refDt);

        String disQry;
        disQry = readFile(Charset.defaultCharset(), "EasyPaisaDisbursementsLetterScript.txt");
        Query set = em.createNativeQuery(disQry).setParameter("frmdt", frmDt).setParameter("todt", toDt);

        Object Obj = set.getSingleResult();
        params.put("AMOUNT", Obj == null ? 0L : new BigDecimal(Obj.toString()).longValue());

        if (isXls) {
            return reportComponent.generateReport(EASYPAISA_LETTER_EXCEL, params, null, isXls);
        } else {
            return reportComponent.generateReport(EASYPAISA_LETTER, params, null);
        }
    }

    public byte[] getClntInfoJazzDueReport(String user, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "ClientInfoJazzDues.txt");
        Query set = em.createNativeQuery(query);

        List<Object[]> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REGION_NAME", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NAME", l[1] == null ? "" : l[1].toString());
            map.put("Transaction_Number", l[2] == null ? "" : l[2].toString());
            map.put("BRANCH_DESC", l[3] == null ? "" : l[3].toString());
            map.put("CLIENT_PARTY_ID", l[4] == null ? "" : l[4].toString());
            map.put("NAME", l[5] == null ? "" : l[5].toString());
            map.put("CNIC", l[6] == null ? "" : l[6].toString());
            map.put("DUE_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("SVG_AMOUNT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("TOTAL_AMOUNT", l[9] == null ? 0 : new BigDecimal(l[9].toString()).longValue());
            map.put("TRANSACTION", l[10] == null ? "" : l[10].toString());
            map.put("AGENT", l[11] == null ? "" : l[11].toString());

            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(JAZZ_CASH_INFO_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(JAZZ_CASH_INFO_DUES, params, getJRDataSource(lists));
        }

    }

    public byte[] getClientUpldDuesReport(String user, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "ClientUploadingJazzDues.txt");
        Query set = em.createNativeQuery(query);

        List<Object[]> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REGION_NAME", l[0] == null ? "" : l[0].toString());
            map.put("AREA_NAME", l[1] == null ? "" : l[1].toString());
            map.put("CLIENT_PARTY_ID", l[2] == null ? "" : l[2].toString());
            map.put("NAME", l[3] == null ? "" : l[3].toString());
            map.put("BIL_MNT", l[4] == null ? "" : l[4].toString());
            map.put("BFR_DT", l[5] == null ? "" : l[5].toString());
            map.put("AFTR_DT", l[6] == null ? "" : l[6].toString());
            map.put("DF", l[7] == null ? "" : l[7].toString());
            map.put("CLNT", l[8] == null ? "" : l[8].toString());

            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(JAZZ_CASH_UPLOAD_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(JAZZ_CASH_UPLOAD_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getEasyPaisaDuesReport(String user, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "easyPaisaDues.txt");
        Query set = em.createNativeQuery(query);

        List<String> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DATA", l.toString());
            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(EASY_PAISA_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(EASY_PAISA_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getUblOmniDuesReport(String user, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "ublOmniDues.txt");
        Query set = em.createNativeQuery(query);

        List<Object[]> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("REGION_NAME", l[0] == null ? "" : l[0].toString());
            map.put("BRANCH_DESC", l[1] == null ? "" : l[1].toString());
            map.put("CLIENT_PARTY_ID", l[2] == null ? "" : l[2].toString());
            map.put("NAME", l[3] == null ? "" : l[3].toString());
            map.put("CNIC", l[4] == null ? "" : l[4].toString());
            map.put("HUSBAND", l[5] == null ? "" : l[5].toString());
            map.put("DUE_AMOUNT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("SAVING_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("DISB_DT", l[8] == null ? "" : l[8].toString());
            map.put("END_DT", l[9] == null ? "" : l[9].toString());

            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(UBL_OMNI_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(UBL_OMNI_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getHblConnectReport(String user, String todt, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "hblConnectDues.txt");
        Query set = em.createNativeQuery(query).setParameter("todt", todt);

        List<Object[]> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("CLIENT_PARTY_ID", l[0] == null ? "" : l[0].toString());
            map.put("INVOICE_ID", l[1] == null ? "" : l[1].toString());
            map.put("NAME", l[2] == null ? "" : l[2].toString());
            map.put("BRANCH_DESC", l[3] == null ? "" : l[3].toString());
            map.put("BILLING_MONTH", l[4] == null ? "" : l[4].toString());
            map.put("DUE_AMOUNT", l[5] == null ? 0 : new BigDecimal(l[5].toString()).longValue());
            map.put("SAVING_AMOUNT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("TOTAL_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("PAYABLE_AMOUNT_AFT_DUE_DATE", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("DUE_DATE", l[9] == null ? "" : l[9].toString());

            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(HBL_CONNECT_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(HBL_CONNECT_DUES, params, getJRDataSource(lists));
        }

    }


    public byte[] getAblLoanDuesReport(String user, boolean isxls) {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "AblRecoveryDues.txt");
        Query set = em.createNativeQuery(query);

        List<String> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DATA", l.toString());
            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(ABL_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(ABL_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getMcbCollectReport(String user, boolean isxls) {

        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "McbCollectDues.txt");
        Query set = em.createNativeQuery(query);

        List<Object[]> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("TRANSACTION_NUMBER", l[0] == null ? "" : l[0].toString());
            map.put("CLIENT_ID", l[1] == null ? "" : l[1].toString());
            map.put("CLIENT_NAME", l[2] == null ? "" : l[2].toString());
            map.put("SPOUSE_NAME", l[3] == null ? "" : l[3].toString());
            map.put("CNIC_NO", l[4] == null ? "" : l[4].toString());
            map.put("KASHF_BRANCH_NAME", l[5] == null ? "" : l[5].toString());
            map.put("RECOVERY_AMOUNT", l[6] == null ? 0 : new BigDecimal(l[6].toString()).longValue());
            map.put("SAVING_AMOUNT", l[7] == null ? 0 : new BigDecimal(l[7].toString()).longValue());
            map.put("TOTAL_AMOUNT", l[8] == null ? 0 : new BigDecimal(l[8].toString()).longValue());
            map.put("TRANSACTION_DATE", l[9] == null ? "" : l[9].toString());
            map.put("MCB_CODE", l[10] == null ? "" : l[10].toString());
            map.put("MCB_BRANCH_NAME", l[11] == null ? "" : l[11].toString());

            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(MCB_COLLECT_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(MCB_COLLECT_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getNadraDuesReport(String user, boolean isxls) {
        Map<String, Object> params = new HashMap<>();

        params.put("curr_user", user);

        String query;
        query = readFile(Charset.defaultCharset(), "NadraDues.txt");
        Query set = em.createNativeQuery(query);

        List<String> Objs = set.getResultList();

        List<Map<String, ?>> lists = new ArrayList();
        Objs.forEach(l -> {
            Map<String, Object> map = new HashMap();
            map.put("DATA", l.toString());
            lists.add(map);
        });

        if (isxls) {
            return reportComponent.generateReport(NADRA_DUES_EXCEL, params, getJRDataSource(lists), isxls);
        } else {
            return reportComponent.generateReport(NADRA_DUES, params, getJRDataSource(lists));
        }
    }

    public byte[] getMfcib(long loanApp) throws Exception {

        Clob clob = null;
        ObjectMapper objectMapper = new ObjectMapper();

        String script = "select doc.DOC_IMG from  mw_loan_app_doc doc where doc.CRNT_REC_FLG =1 and doc.doc_seq = 0 and doc.loan_App_Seq = :loanApp";
        Query query = em.createNativeQuery(script).setParameter("loanApp", loanApp);
        List<Object> result = query.getResultList();

        if (result.size() != 1) {
            return new byte[0];
        }

        clob = (Clob) result.get(0);
        String jsonObject = "";
        Map<String, Map<String, Object>> map = new HashMap<>();

        try {
            jsonObject = clob == null ? null : clob.getSubString(1, (int) clob.length());
            map = objectMapper.readValue(jsonObject, Map.class);
        } catch (Exception exp) {
            throw new Exception(exp);
        }

        Map<String, Object> params = new HashMap<>();

        Map<String, Object> root = new HashMap<>();
        if (map.containsKey("report")) {
            root = (Map<String, Object>) map.get("report").get("ROOT");
        } else if (map.containsKey("ROOT")) {
            root = (Map<String, Object>) map.get("ROOT");
        } else {
            return new byte[0];
        }

        List<Map<String, Object>> personDetail = new ArrayList<>();
        List<Map<String, Object>> addrDetail = new ArrayList<>();
        List<Map<String, Object>> creditApp = new ArrayList<>();
        List<Map<String, Object>> defalt = new ArrayList<>();
        List<Map<String, Object>> fileNotes = new ArrayList<>();
        List<Map<String, Object>> employeeInfo = new ArrayList<>();
        List<Map<String, Object>> collateral = new ArrayList<>();
        List<Map<String, Object>> association = new ArrayList<>();
        List<Map<String, Object>> gurantyDetail = new ArrayList<>();
        List<Map<String, Object>> coborrowerDetail = new ArrayList<>();
        List<Map<String, Object>> bankRuptcyDetail = new ArrayList<>();
        List<Map<String, Object>> reviewDetail = new ArrayList<>();
        List<Map<String, Object>> reportMessage = new ArrayList<>();

        if (root.containsKey("INDIVIDUAL_DETAIL")) {
            if (isList(root.get("INDIVIDUAL_DETAIL"))) {
                personDetail = getMapList((List<Map<String, Object>>) root.get("INDIVIDUAL_DETAIL"));
            } else {
                personDetail = getMapList((Map<String, Object>) root.get("INDIVIDUAL_DETAIL"));
            }
        }
        if (root.containsKey("HOME_INFORMATION")) {
            if (isList(root.get("HOME_INFORMATION"))) {
                addrDetail = getMapList((List<Map<String, Object>>) root.get("HOME_INFORMATION"));
            } else {
                addrDetail = getMapList((Map<String, Object>) root.get("HOME_INFORMATION"));
            }
        }

        if (root.containsKey("ENQUIRIES")) {
            if (isList(root.get("ENQUIRIES"))) {
                creditApp = getMapList((List<Map<String, Object>>) root.get("ENQUIRIES"));
            } else {
                creditApp = getMapList((Map<String, Object>) root.get("ENQUIRIES"));
            }
        }

        if (root.containsKey("DEFAULTS")) {
            if (isList(root.get("DEFAULTS"))) {
                defalt = getMapList((List<Map<String, Object>>) root.get("DEFAULTS"));
            } else {
                defalt = getMapList((Map<String, Object>) root.get("DEFAULTS"));
            }
        }

        if (root.containsKey("FILE_NOTES")) {
            if (isList(root.get("FILE_NOTES"))) {
                fileNotes = getMapList((List<Map<String, Object>>) root.get("FILE_NOTES"));
            } else {
                fileNotes = getMapList((Map<String, Object>) root.get("FILE_NOTES"));
            }
        }

        if (root.containsKey("EMPLOYER_INFORMATION")) {
            if (isList(root.get("EMPLOYER_INFORMATION"))) {
                employeeInfo = getMapList((List<Map<String, Object>>) root.get("EMPLOYER_INFORMATION"));
            } else {
                employeeInfo = getMapList((Map<String, Object>) root.get("EMPLOYER_INFORMATION"));
            }
        }

        if (root.containsKey("COLLATERAL")) {
            if (isList(root.get("COLLATERAL"))) {
                collateral = getMapList((List<Map<String, Object>>) root.get("COLLATERAL"));
            } else {
                collateral = getMapList((Map<String, Object>) root.get("COLLATERAL"));
            }
        }

        if (root.containsKey("ASSOCIATION")) {
            if (isList(root.get("ASSOCIATION"))) {
                association = getMapList((List<Map<String, Object>>) root.get("ASSOCIATION"));
            } else {
                association = getMapList((Map<String, Object>) root.get("ASSOCIATION"));
            }
        }

        if (root.containsKey("GUARANTEES_DETAILS")) {
            if (isList(root.get("GUARANTEES_DETAILS"))) {
                gurantyDetail = getMapList((List<Map<String, Object>>) root.get("GUARANTEES_DETAILS"));
            } else {
                gurantyDetail = getMapList((Map<String, Object>) root.get("GUARANTEES_DETAILS"));
            }
        }

        if (root.containsKey("COBORROWER_DETAILS")) {
            if (isList(root.get("COBORROWER_DETAILS"))) {
                coborrowerDetail = getMapList((List<Map<String, Object>>) root.get("COBORROWER_DETAILS"));
            } else {
                coborrowerDetail = getMapList((Map<String, Object>) root.get("COBORROWER_DETAILS"));
            }
        }

        if (root.containsKey("BANKRUPTCY_DETAILS")) {
            if (isList(root.get("BANKRUPTCY_DETAILS"))) {
                bankRuptcyDetail = getMapList((List<Map<String, Object>>) root.get("BANKRUPTCY_DETAILS"));
            } else {
                bankRuptcyDetail = getMapList((Map<String, Object>) root.get("BANKRUPTCY_DETAILS"));
            }
        }

        if (root.containsKey("REVIEW")) {
            if (isList(root.get("REVIEW"))) {
                reviewDetail = getMapList((List<Map<String, Object>>) root.get("REVIEW"));
            } else {
                reviewDetail = getMapList((Map<String, Object>) root.get("REVIEW"));
            }
        }

        if (root.containsKey("REPORT_MESSAGE")) {
            if (isList(root.get("REPORT_MESSAGE"))) {
                reportMessage = getMapList((List<Map<String, Object>>) root.get("REPORT_MESSAGE"));
            } else {
                reportMessage = getMapList((Map<String, Object>) root.get("REPORT_MESSAGE"));
            }
        }

        List<Map<String, Object>> delinq = getCcpMaster(root, true, false);
        List<Map<String, Object>> ccpDetail = getCcpMaster(root, true, true);

        params.put("fileIdentity", getJRDataSource(personDetail));
        params.put("personInfo", getJRDataSource(personDetail));
        params.put("crntAddr", getJRDataSource(addrDetail.get(0) != null ? getLists(addrDetail.get(0)) : null));
        if (addrDetail.size() > 1) {
            params.put("prmAddr", getJRDataSource(addrDetail.get(1) != null ? getLists(addrDetail.get(1)) : null));
        }
        params.put("sumry", getJRDataSource(delinq));
        params.put("deflt", getJRDataSource(defalt));
        params.put("credit", getJRDataSource(creditApp));
        params.put("ccpDetail", getJRDataSource(ccpDetail));
        params.put("file", getJRDataSource(fileNotes));
        params.put("review", getJRDataSource(reviewDetail));
        params.put("collateral", getJRDataSource(collateral));
        params.put("association", getJRDataSource(association));
        params.put("gurantyDetail", getJRDataSource(gurantyDetail));
        params.put("coborrowerDetail", getJRDataSource(coborrowerDetail));
        params.put("bankRuptcyDetail", getJRDataSource(bankRuptcyDetail));
        params.put("reportMessage", getJRDataSource(reportMessage));
        params.put("employeeInfo", getJRDataSource(employeeInfo));

        String[] accTyp = new String[2];
        creditApp.forEach(r -> {
            if (r.containsKey("ACCT_TY") || r.containsKey("MAPPED_ACCT_TY")) {
                if (r.get("ACCT_TY").toString().equals("IN") || r.get("MAPPED_ACCT_TY").toString().equals("57")) {
                    accTyp[0] = "IN-57:INCOME GENERATION";
                } else if (r.get("ACCT_TY").toString().equals("MFE") || r.get("MAPPED_ACCT_TY").toString().equals("28")) {
                    accTyp[1] = "MFE-28:MICRO CREDIT- ENTERPRISE";
                }
            }
        });

        ccpDetail.forEach(r -> {
            if (r.containsKey("ACCT_TY") || r.containsKey("MAPPED_ACCT_TY")) {
                if (r.get("ACCT_TY").toString().equals("IN") || r.get("MAPPED_ACCT_TY").toString().equals("57")) {
                    accTyp[0] = "IN-57:INCOME GENERATION";
                } else if (r.get("ACCT_TY").toString().equals("MFE") || r.get("MAPPED_ACCT_TY").toString().equals("28")) {
                    accTyp[1] = "MFE-28:MICRO CREDIT- ENTERPRISE";
                }
            }
        });

        params.put("report_type", delinq.size() != 0 ? "Strong Match" : defalt.size() != 0 ? "Strong Match" : "New File Created");
        params.put("typIN", accTyp[0]);
        params.put("typMFE", accTyp[1]);

        return reportComponent.generateReport(Mfcib_Report, params, null);
    }

    private List<Map<String, Object>> getCcpMaster(Map<String, Object> objs, final boolean isSummery, final boolean isCppDetail) {

        List<Map<String, Object>> ccpMaster = new ArrayList<>();
        List<Map<String, Object>> ccpSummary = new ArrayList<>();
        List<Map<String, Object>> ccpDetail = new ArrayList<>();

        if (objs.containsKey("CCP_MASTER")) {
            if (isList(objs.get("CCP_MASTER"))) {
                ccpMaster = (List<Map<String, Object>>) objs.get("CCP_MASTER");
            } else {
                ccpMaster.add((Map<String, Object>) objs.get("CCP_MASTER"));
            }
        }

        if (objs.containsKey("CCP_SUMMARY")) {
            if (isList(objs.get("CCP_SUMMARY"))) {
                ccpSummary = (List<Map<String, Object>>) objs.get("CCP_SUMMARY");
            } else {
                ccpSummary.add((Map<String, Object>) objs.get("CCP_SUMMARY"));
            }
        }
        if (objs.containsKey("CCP_DETAIL")) {
            if (isList(objs.get("CCP_DETAIL"))) {
                ccpDetail = (List<Map<String, Object>>) objs.get("CCP_DETAIL");
            } else {
                ccpDetail.add((Map<String, Object>) objs.get("CCP_DETAIL"));
            }
        }

        List<Map<String, Object>> maps = new ArrayList<>();

        if (ccpMaster.get(0).get("FILE_NO").toString().isEmpty()) {
            return maps;
        }

        List<Map<String, Object>> finalCcpSummary = ccpSummary;
        List<Map<String, Object>> finalCcpDetail = ccpDetail;
        ccpMaster.forEach(ccp -> {
            Map<String, Object> map = new HashMap<>();
            map.putAll(ccp);
            if (isSummery) {
                finalCcpSummary.forEach(sm -> {
                    if (sm.get("SEQ_NO").toString().equals(ccp.get("SEQ_NO").toString())) {
                        map.putAll(sm);
                    }
                });
            }
            if (isCppDetail) {
                int count = 1;
                for (int i = 0; i < finalCcpDetail.size(); i++) {
                    Map<String, Object> detail = finalCcpDetail.get(i);
                    if (detail.get("SEQ_NO").toString().equals(ccp.get("SEQ_NO").toString())) {

                        int date = 0;
                        if (detail.get("STATUS_MONTH").toString().length() == 6) {
                            date = Integer.parseInt(detail.get("STATUS_MONTH").toString().substring(0, 2));
                            if (date == 0) {
                                date = Integer.parseInt(detail.get("STATUS_MONTH").toString().substring(1, 2));
                            }
                        } else if (detail.get("STATUS_MONTH").toString().length() == 5) {
                            date = Integer.parseInt(detail.get("STATUS_MONTH").toString().substring(0, 1));
                        }
                        StringBuffer buffer = new StringBuffer("");
                        if (date == 10 || date == 11 || date == 12) {
                            map.put("STATUS_MONTH_" + count, date);
                        } else if (date == 1) {
                            buffer = new StringBuffer(detail.get("STATUS_MONTH").toString());
                            if (buffer.length() == 6) {
                                buffer.insert(2, '/');
                            } else if (buffer.length() == 5) {
                                buffer.insert(0, "0").insert(2, '/');
                            }
                            map.put("STATUS_MONTH_" + count, buffer.toString());
                        } else {
                            map.put("STATUS_MONTH_" + count, date);
                        }

                        map.put("PAYMENT_STATUS_" + count, detail.get("PAYMENT_STATUS"));
                        map.put("OVERDUEAMOUNT_" + count, detail.get("OVERDUEAMOUNT"));
                        count++;
                    }
                }
            }
            maps.add(stringifyValues(map));
        });
        return maps;
    }

    private List<Map<String, Object>> getMapList(List<Map<String, Object>> mapObject) {
        List<Map<String, Object>> maps = new ArrayList<>();

        if (mapObject.get(0) != null) {
            if (mapObject.get(0).containsKey("FILE_NO")) {
                if (mapObject.get(0).get("FILE_NO").toString().isEmpty()) {
                    return maps;
                }
            }
        }

        mapObject.forEach(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.putAll(obj);
            maps.add(stringifyValues(map));
        });
        return maps;
    }

    private List<Map<String, Object>> getMapList(Map<String, Object> mapObject) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> maps = new ArrayList<>();
        if (mapObject.containsKey("FILE_NO")) {
            if (mapObject.get("FILE_NO").toString().isEmpty()) {
                return maps;
            }
        }
        map.putAll(mapObject);
        maps.add(map);
        return maps;
    }


    public List<Map<String, Object>> getLists(Map<String, Object> map) {
        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return maps;
    }

    private Map<String, Object> stringifyValues(Map<String, Object> variables) {
        return variables.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
//         .filter(m -> m.getKey() != null && m.getValue() !=null)
    }

    private boolean isList(Object object) {
        return object instanceof ArrayList;
    }

    //Added by Areeba
    public byte[] getInsuClmFrmForDsblty(long clntSeq, String userId) throws IOException {

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(clntSeq, true);

        Query bi = em.createNativeQuery(com.idev4.rds.util.Queries.USER_BRANCH_INFO).setParameter("portKey",
                clnt.getPortKey());
        Object[] obj = (Object[]) bi.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("reg_nm", obj[0].toString());
        params.put("area_nm", obj[1].toString());
        params.put("brnch_nm", obj[2].toString());
        params.put("brnch_cd", obj[3].toString());
        params.put("curr_user", userId);

        String clntinfoQry;
        clntinfoQry = readFile(Charset.defaultCharset(), "InsuClmFrmclntinfoQryDsblty.txt");
        Query rs = em.createNativeQuery(clntinfoQry).setParameter("clntSeq", clntSeq);

        Object[] clntObj = (Object[]) rs.getSingleResult();

        params.put("clnt_id", clntObj[0].toString());
        params.put("clnt_nm", clntObj[1].toString());
        params.put("rel_nm", clntObj[6] == null ? "" : clntObj[6].toString());
        params.put("dt_of_dsblty", getFormaedDate(clntObj[4].toString()));
        int flg = new BigDecimal(clntObj[5].toString()).intValue();
        params.put("des_nm", flg == 0 ? clntObj[1].toString() : clntObj[6].toString());
        String desAg = flg == 0 ? (clntObj[3] == null ? "" : clntObj[3].toString())
                : (clntObj[8] == null ? "" : clntObj[8].toString());
        params.put("des_ag", calculateAge(getLocalDate(desAg), LocalDate.now()));
        params.put("des_cnic", flg == 0 ? (clntObj[2] == null ? "" : clntObj[2].toString())
                : clntObj[7] == null ? "" : clntObj[7].toString());
        params.put("form_title_main", "Credit for Life");
        params.put("form_title", " Disability Claim Form");

        String loanInfoQry;
        loanInfoQry = readFile(Charset.defaultCharset(), "InsuClmFrmloanInfoQry.txt");
        Query rs1 = em.createNativeQuery(loanInfoQry).setParameter("clntSeq", clntSeq);

        List<Object[]> loanInfo = rs1.getResultList();
        List<Map> loanInfoList = new ArrayList();
        loanInfo.forEach(l -> {
            Map loan = new HashMap();
            loan.put("prd_name", l[0].toString());
            loan.put("dis_dt", getFormaedDate(l[1].toString()));
            loan.put("dis_amt", getLongValue(l[2].toString()));
            loan.put("ag_dt", l[3] == null ? "" : getFormaedDate(l[3].toString()));
            loan.put("ad_amt", l[4] == null ? 0 : getLongValue(l[4].toString()));
            loan.put("rec_amt",
                    (l[2] == null ? 0 : getLongValue(l[2].toString()))
                            + (l[5] == null ? 0 : getLongValue(l[5].toString()))
                            - (l[6] == null ? 0 : getLongValue(l[6].toString())));
            loanInfoList.add(loan);
        });
        params.put("dataset", getJRDataSource(loanInfoList));

        String paymentsQry;
        paymentsQry = readFile(Charset.defaultCharset(), "InsuClmFrmpaymentsQry.txt");
        Query rs2 = em.createNativeQuery(paymentsQry).setParameter("clntSeq", clntSeq);

        List<Object[]> pymtsObj = rs2.getResultList();
        List<Map<String, ?>> pymts = new ArrayList();
        Long outSts = 0L;
        long inst = 0;
        String oldDate = "";
        for (Object[] l : pymtsObj) {
            Map<String, Object> map = new HashMap();
            if (!l[0].toString().equals(oldDate)) {
                inst++;
            }
            map.put("INST_NUM", inst);
            map.put("DUE_DT", getFormaedDate(l[0].toString()));
            map.put("PPAL_AMT_DUE", l[1] == null ? 0 : getLongValue(l[1].toString()));
            map.put("TOT_CHRG_DUE", l[2] == null ? 0 : getLongValue(l[2].toString()));
            map.put("AMT", l[3] == null ? 0 : getLongValue(l[3].toString()));
            map.put("PR_REC", l[4] == null ? 0 : getLongValue(l[4].toString()));
            map.put("SC_REC", l[5] == null ? 0 : getLongValue(l[5].toString()));
            map.put("PYMT_DT", l[6] == null ? "" : getFormaedDate(l[6].toString()));

            oldDate = l[0].toString();
            pymts.add(map);

        }

        return reportComponent.generateReport(INSURANCECLAIM_DSBLTY, params, getJRDataSource(pymts));
    }
    //Ended by Areeba

    // Added by Areeba - 31-10-2022
    public byte[] getOneLinkReport(long loanAppSeq) throws IOException {
        String q;

        String REPORTS_BASEPATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator + "reports"
                + file.separator;

        Map<String, Object> params = new HashMap<>();
        params.put("MUNSALIK_IMG", REPORTS_BASEPATH + "Munsalik.png");
        params.put("ONE_LINK_IMG", REPORTS_BASEPATH + "1link.png");

        q = readFile(Charset.defaultCharset(), "ONE_LINK_DEPOSITORY_SLIP.txt");
        Query rs = em.createNativeQuery(q).setParameter("loanAppSeq", loanAppSeq);
        List<Object[]> obj = rs.getResultList();
        List<Map<String, ?>> insts = new ArrayList();

        for (Object[] l : obj) {
            Map<String, Object> map = new HashMap();
            map.put("CLNT_NM", l[0] == null ? "" : l[0].toString());
            map.put("ONE_BILL_ID", l[1] == null ? "" : l[1].toString());
            map.put("DUE_DT", l[2]);
            map.put("BRNCH_NM", l[3]);
            map.put("INST_NUM", l[4] == null ? 0L : new BigDecimal(l[4].toString()).longValue());
            map.put("AMT", l[5] == null ? 0L : new BigDecimal(l[5].toString()).longValue());

            insts.add(map);
        }

        return reportComponent.generateReport(ONE_LINK_DEPOSIT_SLIP, params, getJRDataSource(insts));
    }
    // Ended by Areeba

}
