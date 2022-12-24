package com.idev4.rds.service;

import com.fasterxml.uuid.Logger;
import com.idev4.rds.domain.*;
import com.idev4.rds.dto.AjRcvryDTO;
import com.idev4.rds.dto.AppRcvryDTO;
import com.idev4.rds.dto.Charges;
import com.idev4.rds.dto.LoanRschdDto;
import com.idev4.rds.repository.*;
import com.idev4.rds.util.SequenceFinder;
import com.idev4.rds.util.Sequences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Transactional
public class PaymentScheduleService {

    private static final String APROVED_STATUS = "0004";
    private static final String ISLAMIC_TYPE = "0002";
    private static final String ADVANCE_STATUS = "1305";
    private final EntityManager em;
    @Autowired
    LoanApplicationRepository loanApplicationRepository;
    @Autowired
    PaymentScheduleHeaderRespository paymentScheduleHeaderRespository;
    @Autowired
    MwLoanAppPpalStngsRepository mwLoanAppPpalStngsRepository;
    @Autowired
    MwLoanAppChrgStngsRepository mwLoanAppChrgStngsRepository;
    @Autowired
    PaymentScheduleChargersRepository paymentScheduleChargersRepository;
    @Autowired
    PaymentScheduleDetailRepository paymentScheduleDetailRepository;
    @Autowired
    PaymentScheduleComponent paymentScheduleComponent;
    @Autowired
    MwRefCdValRepository mwRefCdValRepository;
    @Autowired
    DisbursementVoucherHeaderRepository disbursementVoucherHeaderRepository;
    @Autowired
    Charges charges;
    @Autowired
    MwBrnchRepository mwBrnchRepository;
    @Autowired
    MwLoanRschdRepository mwLoanRschdRepository;
    @Autowired
    VchrPostingService vchrPostingService;
    @Autowired
    MwRcvryTrxRepository mwRcvryTrxRepository;
    @Autowired
    RecoveryService recoveryService;
    @Autowired
    MwClntHlthInsrCardRepository mwClntHlthInsrCardRepository;
    @Autowired
    MwPrdRepository mwPrdRepository;
    @Autowired
    MwExpRepository mwExpRepository;
    @Autowired
    TypesRepository typesRepository;
    @Autowired
    DisbursementVoucherDetailRepository disbursementVoucherDetailRepository;

    public PaymentScheduleService(EntityManager em) {
        super();
        this.em = em;
    }

    public static Date addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }

    private static long roundUp(long n, long m) {
        // return n >= 0 ? ( ( n + m - 1 ) / m ) * m : ( n / m ) * m;
        return (Math.round((double) n / m)) * m;
    }

    private static List<PaymentScheduleDetail> getInstalmentWithIrrAndAllInst(Long numOfInst, Long aprvdLoanAmt, Double irrRate,
                                                                              Double totPrSc, Long rndngAdjInst, double servTotal) {
        double flatinstAmt = totPrSc / numOfInst;
        List<PaymentScheduleDetail> paymentScheduleDetails = new ArrayList();
        long totlgenAmt = 0;
        long apprvdAmt = aprvdLoanAmt;
        long roundedServTtl = 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleDetail paymentScheduleDetail = new PaymentScheduleDetail();
            long rounded = Math.round(aprvdLoanAmt * irrRate);
            long ppalAmtDue = (long) Math.round(flatinstAmt - rounded);
            roundedServTtl = roundedServTtl + rounded;
            totlgenAmt = totlgenAmt + ppalAmtDue;
            aprvdLoanAmt = aprvdLoanAmt - ppalAmtDue;
            paymentScheduleDetail.setPpalAmtDue(ppalAmtDue);
            paymentScheduleDetail.setTotChrgDue(rounded);
            paymentScheduleDetails.add(paymentScheduleDetail);
        }

        long serivDiff = (long) (servTotal - roundedServTtl);

        long diffrence = apprvdAmt - totlgenAmt;
        long addmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getPpalAmtDue() + diffrence;
        long addServcAmount = (long) (paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getTotChrgDue() + serivDiff);
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setPpalAmtDue(addmount);
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setTotChrgDue(addServcAmount);

        return paymentScheduleDetails;
    }

    private static List<PaymentScheduleDetail> getInstalmentWithIrrAndBuiltInst(Long numOfInst, Long aprvdLoanAmt, Double irrRate,
                                                                                String sgrtInstk, Long rndngAdjInst, double servTotal) {

        List<PaymentScheduleDetail> paymentScheduleDetails = new ArrayList();
        List<Integer> list = new ArrayList();
        if (sgrtInstk != null) {
            list = Stream.of(sgrtInstk.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            list = IntStream.rangeClosed(1, numOfInst.intValue()).boxed().collect(Collectors.toList());
        }
        Long flatinstAmt = (long) (aprvdLoanAmt / list.size());
        Long totlgenAmt = 0L;
        long apprvdAmt = aprvdLoanAmt;

        long roundedServTtl = 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleDetail paymentScheduleDetail = new PaymentScheduleDetail();
            Long rounded = Math.round(aprvdLoanAmt * irrRate);
            paymentScheduleDetail.setTotChrgDue(rounded);
            paymentScheduleDetail.setPpalAmtDue((long) 0);
            roundedServTtl = roundedServTtl + rounded;
            if (list.contains(i)) {
                aprvdLoanAmt = (long) (aprvdLoanAmt - flatinstAmt);
                totlgenAmt = totlgenAmt + flatinstAmt;
                paymentScheduleDetail.setPpalAmtDue(flatinstAmt);
            }
            paymentScheduleDetails.add(paymentScheduleDetail);

        }
        long serivDiff = (long) (servTotal - roundedServTtl);

        Long diffrence = apprvdAmt - totlgenAmt;
        Long addmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getPpalAmtDue() + diffrence;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setPpalAmtDue(addmount);
        long addServcAmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getTotChrgDue() + serivDiff;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setTotChrgDue(addServcAmount);
        return paymentScheduleDetails;
    }

    private static List<PaymentScheduleDetail> getInstalmentWithoutIrrAndBuiltInst(Long numOfInst, Long aprvdLoanAmt, Double servChrgs,
                                                                                   String sgrtInstk, Long rndngAdjInst, double servTotal) {

        List<PaymentScheduleDetail> paymentScheduleDetails = new ArrayList();
        List<Integer> list = new ArrayList();
        if (sgrtInstk != null) {
            list = Stream.of(sgrtInstk.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            list = IntStream.rangeClosed(1, numOfInst.intValue()).boxed().collect(Collectors.toList());
        }
        Long flatinstAmt = (long) (aprvdLoanAmt / list.size());
        Long totlgenAmt = 0L;
        long roundedServTtl = 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleDetail paymentScheduleDetail = new PaymentScheduleDetail();
            Long rounded = Math.round(servChrgs / numOfInst);
            roundedServTtl = roundedServTtl + rounded;

            paymentScheduleDetail.setTotChrgDue((long) (rounded));
            paymentScheduleDetail.setPpalAmtDue((long) 0);
            if (list.contains(i)) {
                totlgenAmt = totlgenAmt + flatinstAmt;
                paymentScheduleDetail.setPpalAmtDue(flatinstAmt);
            }
            paymentScheduleDetails.add(paymentScheduleDetail);

        }
        long serivDiff = (long) (servTotal - roundedServTtl);
        Long diffrence = aprvdLoanAmt - totlgenAmt;
        Long addmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getPpalAmtDue() + diffrence;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setPpalAmtDue(addmount);
        long addServcAmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getTotChrgDue() + serivDiff;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setTotChrgDue(addServcAmount);
        return paymentScheduleDetails;
    }

    public List<Map> getLoanAppDataSet(long loanAppSeq) {
        return paymentScheduleDetailRepository.getLoanAppDataSet(loanAppSeq);
    }

    public boolean hasDsbmtDtls(long loanAppSeq) {
        List<DisbursementVoucherDetail> dtls = disbursementVoucherDetailRepository.findAllByLoanAppSeq(loanAppSeq);
        return dtls.size() > 0 ? true : false;
    }

    public Boolean genratePaymentSchedule(long loanAppSeq, String frstInstDt, String currUser) {

        // Added by Zohaib Asim - Dated 07-04-2021
        // Business Functionality moved to Procedure
        String parmOutputProcedure = "";
        Boolean rtnResp = false;
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRC_GENERATE_PYMT_SCHED");
        storedProcedure.registerStoredProcedureParameter("P_LOAN_APP_SEQ", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_FRST_INST_DT", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_USER_ID", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_RTN_MSG", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("P_LOAN_APP_SEQ", loanAppSeq);
        storedProcedure.setParameter("P_FRST_INST_DT", frstInstDt);
        storedProcedure.setParameter("P_USER_ID", currUser);
        storedProcedure.execute();

        System.out.println(storedProcedure.getOutputParameterValue("P_RTN_MSG"));

        parmOutputProcedure = storedProcedure.getOutputParameterValue("P_RTN_MSG").toString();
        if (parmOutputProcedure.contains("SUCCESS")) {
            Logger.logInfo("PRC_GENERATE_PYMT_SCHED: Successfully Executed.");
            rtnResp = true;
        } else {
            Logger.logError("PRC_GENERATE_PYMT_SCHED: Execution unSuccessfully.");
        }
        return rtnResp;
        // End by Zohaib Asim

        // Commented by Zohaib Asim - Dated 07-04-2021
        /*PaymentScheduleHeader paymentScheduleHeader = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg( loanAppSeq, true );
        boolean reGenFlag = false;
        List< PaymentScheduleDetail > oldPaymentScheduleDetails = new ArrayList< PaymentScheduleDetail >();
        List< PaymentScheduleChargers > oldPaymentScheduleChargers = new ArrayList< PaymentScheduleChargers >();
        // Ex-Payment Schedule
        long paySchedHdrSeq = 0L;
        if ( paymentScheduleHeader != null ) {
            reGenFlag = true;
            Instant currIns = Instant.now();
            paySchedHdrSeq = paymentScheduleHeader.getPaySchedHdrSeq();
            oldPaymentScheduleDetails = paymentScheduleDetailRepository
                    .findAllByPaySchedHdrSeqAndCrntRecFlgOrderByInstNumAsc( paymentScheduleHeader.getPaySchedHdrSeq(), true );

            oldPaymentScheduleDetails.forEach( obj -> {
                oldPaymentScheduleChargers
                        .addAll( paymentScheduleChargersRepository.findAllByPaySchedDtlSeqAndCrntRecFlg( obj.getPaySchedDtlSeq(), true ) );
            } );

        } else {
            paymentScheduleHeader = new PaymentScheduleHeader();
            paySchedHdrSeq = SequenceFinder.findNextVal( Sequences.PYMT_SCHED_DTL_SEQ );
            paymentScheduleHeader.setPaySchedHdrSeq( paySchedHdrSeq );
        }
        // Product Type (Islamic / Conventional )
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp( loanAppSeq );
        // Loan App Status ( Active / Disbursed / Approved ) etc
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp( loanAppSeq );
        long vchrTyp = 0;
        if ( type.getRefCd().equals( ISLAMIC_TYPE ) && sts.getRefCd().equals( APROVED_STATUS ) ) {
            vchrTyp = 1;
        }
        Long aprvdLoanAmt = 0L;
        long pplAmt = 0;
        // Get Loan Application Data
        LoanApplication loanApplication = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg( loanAppSeq, true );
        List< Map< String, ? > > vouchers = disbursementVoucherHeaderRepository.getVouchersbyLoanAppAndType( loanAppSeq, vchrTyp );
        Charges karzHassna = new Charges();
        for ( Map map : vouchers ) {
            if ( !map.get( "typ_id" ).equals( "0009" ) && !map.get( "typ_id" ).equals( "0010" ) && !map.get( "typ_id" ).equals( "0011" ) ) {
                pplAmt = pplAmt + Long.parseLong( map.get( "amt" ).toString() );
            }
            if ( map.get( "typ_id" ).equals( "0010" ) ) {
                karzHassna.acct = map.get( "gl_acct_num" ).toString();
                karzHassna.chrgName = map.get( "typ_str" ).toString();
                karzHassna.roundFlg = 1;
                karzHassna.val = Long.parseLong( map.get( "amt" ).toString() );
                karzHassna.type = Long.parseLong( map.get( "typ_seq" ).toString() );
                karzHassna.typeId = map.get( "typ_id" ).toString();
            }
        }
        aprvdLoanAmt = ( long ) ( pplAmt + karzHassna.val );
        Date date = null;
        try {
            date = new SimpleDateFormat( "dd-MM-yyyy" ).parse( frstInstDt );
        } catch ( ParseException e ) {

        }

        // Get Payment Schedule Header Information
        Instant currIns = Instant.now();
        paymentScheduleHeader.setLoanAppSeq( loanAppSeq );
        paymentScheduleHeader.setFrstInstDt( date.toInstant() );
        paymentScheduleHeader.setPaySchedId( Long.toString( paySchedHdrSeq ) );
        paymentScheduleHeader.setCrntRecFlg( true );
        paymentScheduleHeader.setCrtdBy( currUser );
        paymentScheduleHeader.setCrtdDt( currIns );
        paymentScheduleHeader.setDelFlg( false );
        paymentScheduleHeader.setEffStartDt( currIns );
        paymentScheduleHeader.setLastUpdBy( currUser );
        paymentScheduleHeader.setLastUpdDt( currIns );
        // Loan App Settings
        MwLoanAppPpalStngs mwLoanAppPpalStngs = mwLoanAppPpalStngsRepository.findOneByPrdSeqAndLoanAppSeq( loanApplication.getPrdSeq(),
                loanAppSeq );
        // Default Status Seq for Payment Schedule Detail
        Long dueSts = mwRefCdValRepository.findOneByRefCdAndCrntRecFlg( "0945", true ).getRefCdSeq();
        double servChrgs = 0;
        long roundFlg = 0;
        // int rndndFlgTypSeq = 0;

        // Charges Calculations
        // Charges Including Health Ins. & Other Charges
        List< Object[] > chargesObj = mwLoanAppChrgStngsRepository.getLoanAppChrgStngsByRddSeqAndLoanAppSeq( loanApplication.getPrdSeq(),
                loanAppSeq );

        List< Charges > chargesList = charges.getListofCharge( aprvdLoanAmt, chargesObj );
        if ( karzHassna.type != 0 )
            chargesList.add( karzHassna );
        // 0017 is type id for service chargers
        for ( Charges obj : chargesList ) {
            if ( obj.typeId.equals( "0017" ) ) {
                servChrgs = Math.round( obj.val );
            }
            if ( obj.roundFlg != 0 ) {
                roundFlg = obj.type;
            }
        }

        // double servChrgs = ( double ) ( ( aprvdLoanAmt / 100 ) * servChrgRate );

        Double totPrSc = aprvdLoanAmt + servChrgs;

        List< PaymentScheduleDetail > pplInts = new ArrayList< PaymentScheduleDetail >();
        if ( mwLoanAppPpalStngs.getIrrFlg() != null && mwLoanAppPpalStngs.getIrrFlg().longValue() == 1L ) {
            if ( ( mwLoanAppPpalStngs.getNumOfInst() == mwLoanAppPpalStngs.getNumOfInstSgrt() )
                    || ( mwLoanAppPpalStngs.getNumOfInstSgrt().longValue() == 0L || mwLoanAppPpalStngs.getNumOfInstSgrt() == null ) ) {
                pplInts = getInstalmentWithIrrAndAllInst( mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt, mwLoanAppPpalStngs.getIrrRate(),
                        totPrSc, mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs );
            } else {
                pplInts = getInstalmentWithIrrAndBuiltInst( mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt,
                        mwLoanAppPpalStngs.getIrrRate(), mwLoanAppPpalStngs.getSgrtInst(), mwLoanAppPpalStngs.getRndngAdjInst(),
                        servChrgs );
            }
        } else {
            if ( ( mwLoanAppPpalStngs.getNumOfInst().longValue() == mwLoanAppPpalStngs.getNumOfInstSgrt().longValue() )
                    || ( mwLoanAppPpalStngs.getNumOfInstSgrt().longValue() == 0L || mwLoanAppPpalStngs.getNumOfInstSgrt() == null ) ) {
                pplInts = getInstalmentWithoutIrr( mwLoanAppPpalStngs.getNumOfInst(), pplAmt, servChrgs,
                        mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs );
            } else {
                pplInts = getInstalmentWithoutIrrAndBuiltInst( mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt, servChrgs,
                        mwLoanAppPpalStngs.getSgrtInst(), mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs );
            }
        }

        int isKsk = 0;
        List< PaymentScheduleDetail > nextInst = new ArrayList<>();
        if ( loanApplication.getPrntLoanAppSeq().longValue() != loanAppSeq ) {
            nextInst = paymentScheduleDetailRepository.getNextDueInst( loanApplication.getPrntLoanAppSeq().longValue(),
                    mwLoanAppPpalStngs.getNumOfInst() );
            isKsk = 1;
        }

        Date dueDate = date;
        int i = 0;
        Long instCount = 1L;
        for ( PaymentScheduleDetail psd : pplInts ) {
            long seq = reGenFlag ? oldPaymentScheduleDetails.get( i ).getPaySchedDtlSeq()
                    : SequenceFinder.findNextVal( Sequences.PYMT_SCHED_DTL_SEQ );
            psd.setPaySchedDtlSeq( seq );
            psd.setPaySchedHdrSeq( paySchedHdrSeq );
            psd.setDueDt( isKsk == 1 ? nextInst.get( i ).getDueDt() : getAdjustedDay( dueDate.toInstant() ) );
            psd.setInstNum( isKsk == 1 ? nextInst.get( i ).getInstNum() : instCount );
            psd.setCrntRecFlg( true );
            psd.setCrtdBy( currUser );
            psd.setCrtdDt( currIns );
            psd.setDelFlg( false );
            psd.setEffStartDt( currIns );
            psd.setLastUpdBy( currUser );
            psd.setLastUpdDt( currIns );
            psd.setPymtStsKey( dueSts.longValue() );
            dueDate = addMonth( dueDate, 1 );
            instCount++;
            i++;
        }

        *//*  for ( Object[] obj : charges ) {
            if ( new BigDecimal( obj[ 8 ].toString() ).longValue() != 17L ) {
                List< PaymentScheduleChargers > chargesInsts = getPymntSchChrgInst( obj, mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt,
                        currUser, mwLoanAppPpalStngs.getRndngAdjInst() );
                for ( int j = 0; j < pplInts.size(); j++ ) {
                    chargesInsts.get( j ).setPaySchedDtlSeq( pplInts.get( j ).getPaySchedDtlSeq() );
                }

            }
        }*//*

        List< ExpectedPaymentSchedule > expectedPaymentSchedules = new ArrayList< ExpectedPaymentSchedule >();
        for ( PaymentScheduleDetail psd : pplInts ) {
            ExpectedPaymentSchedule expectedPaymentSchedule = new ExpectedPaymentSchedule();
            List< PaymentScheduleChargers > chargers = new ArrayList();
            expectedPaymentSchedule.setDetail( psd );
            expectedPaymentSchedule.setChargers( chargers );
            expectedPaymentSchedules.add( expectedPaymentSchedule );
        }

        for ( Charges obj : chargesList ) {
            if ( !obj.typeId.equals( "0017" ) ) {
                List< PaymentScheduleChargers > chargesInsts = getPymntSchChrgInst( obj, mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt,
                        currUser, mwLoanAppPpalStngs.getRndngAdjInst() );
                for ( int j = 0; j < pplInts.size(); j++ ) {
                    chargesInsts.get( j ).setPaySchedDtlSeq( pplInts.get( j ).getPaySchedDtlSeq() );
                    PaymentScheduleChargers charger = chargesInsts.get( j );
                    expectedPaymentSchedules.get( j ).getChargers().add( charger );
                }
            }
        }
        long allTtl = 0;
        boolean roundService = true;
        for ( ExpectedPaymentSchedule eps : expectedPaymentSchedules ) {
            Long chrgTtl = 0L;
            for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                chrgTtl = chrgTtl + ( chr.getAmt() == null ? 0 : chr.getAmt() );
            }
            long total = eps.getDetail().getPpalAmtDue() + eps.getDetail().getTotChrgDue() + chrgTtl;
            long rounded = roundUp( total, mwLoanAppPpalStngs.getRndngScl() );
            long diff = rounded - total;

            for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                if ( chr.getChrgTypsSeq().longValue() == roundFlg ) {
                    Long addmount = chr.getAmt();
                    chr.setAmt( addmount + diff );
                    roundService = false;
                }
            }
            if ( roundService ) {
                Long addmount = eps.getDetail().getTotChrgDue();
                eps.getDetail().setTotChrgDue( addmount + diff );
            }
            allTtl = allTtl + diff;

        }
        if ( roundService ) {

            Long addmount = expectedPaymentSchedules.get( 0 ).detail.getTotChrgDue();
            expectedPaymentSchedules.get( 0 ).detail.setTotChrgDue( addmount - allTtl );
        } else {
            // long diff = allTtl - roundTtl;
            for ( Charges obj : chargesList ) {
                if ( obj.adjFlg != 0 && obj.adjFlg == 1L && !obj.typeId.equals( "0017" ) ) {
                    for ( PaymentScheduleChargers chr : expectedPaymentSchedules.get( mwLoanAppPpalStngs.getRndngAdjInst().intValue() - 1 )
                            .getChargers() ) {
                        if ( chr.getChrgTypsSeq().longValue() == obj.type ) {
                            Long addmount = chr.getAmt();
                            chr.setAmt( addmount - allTtl );

                        }
                    }

                }
            }
        }
        List< PaymentScheduleChargers > chargesInsts = new ArrayList();
        for ( ExpectedPaymentSchedule eps : expectedPaymentSchedules ) {
            for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                chargesInsts.add( chr );
            }
        }
        // oldPaymentScheduleHeader = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg( loanAppSeq, true );
        // if ( oldPaymentScheduleHeader != null )
        // return false;
        return paymentScheduleComponent.savePaymentScheduleService( reGenFlag, paymentScheduleHeader, pplInts, chargesInsts,
                oldPaymentScheduleDetails, oldPaymentScheduleChargers );*/
    }

    private List<PaymentScheduleDetail> getInstalmentWithoutIrr(Long numOfInst, Long aprvdLoanAmt, Double servChrgs, Long rndngAdjInst,
                                                                double servTotal) {
        List<PaymentScheduleDetail> paymentScheduleDetails = new ArrayList();
        long flatinstAmt = (long) (aprvdLoanAmt / numOfInst);
        long totlgenAmt = 0;
        long roundedServTtl = 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleDetail paymentScheduleDetail = new PaymentScheduleDetail();
            totlgenAmt = totlgenAmt + flatinstAmt;
            paymentScheduleDetail.setPpalAmtDue(flatinstAmt);
            Long rounded = Math.round(servChrgs / numOfInst);
            roundedServTtl = roundedServTtl + rounded;
            paymentScheduleDetail.setTotChrgDue((long) (rounded));
            paymentScheduleDetails.add(paymentScheduleDetail);
        }

        long serivDiff = (long) (servTotal - roundedServTtl);

        Long diffrence = aprvdLoanAmt - totlgenAmt;
        Long addmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getPpalAmtDue() + diffrence;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setPpalAmtDue(addmount);
        long addServcAmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getTotChrgDue() + serivDiff;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setTotChrgDue(addServcAmount);
        return paymentScheduleDetails;
    }

    private List<PaymentScheduleDetail> getInstalmentAccuralImpl(Long numOfInst, Double servChrgs, Long rndngAdjInst, double servTotal,
                                                                 List<PaymentScheduleDetail> paymentScheduleDetails) {
        long roundedServTtl = 0;
        for (int i = 0; i < numOfInst; i++) {
            PaymentScheduleDetail paymentScheduleDetail = paymentScheduleDetails.get(i);
            Long rounded = Math.round(servChrgs / numOfInst);
            roundedServTtl = roundedServTtl + rounded;
            rounded = rounded.longValue() + paymentScheduleDetail.getTotChrgDue();
            paymentScheduleDetail.setTotChrgDue((long) (rounded));
        }

        long serivDiff = (long) (servTotal - roundedServTtl);
        long addServcAmount = paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).getTotChrgDue() + serivDiff;
        paymentScheduleDetails.get(rndngAdjInst.intValue() - 1).setTotChrgDue(addServcAmount);
        return paymentScheduleDetails;
    }

    private List<PaymentScheduleChargers> getPymntSchChrgInst(Charges charges, Long numOfInst, Long aprvdLoanAmt, String currUser,
                                                              Long rndngAdjInst) {
        List<PaymentScheduleChargers> object = new ArrayList<PaymentScheduleChargers>();
        List<Integer> sgrtInsts = new ArrayList<Integer>();
        if (charges.sgrtInst != null && !charges.sgrtInst.equals("")) {
            sgrtInsts = Stream.of((charges.sgrtInst.toString()).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            sgrtInsts = IntStream.rangeClosed(1, numOfInst.intValue()).boxed().collect(Collectors.toList());
        }

        long numOfInstSgrt = sgrtInsts.size();
        double instAmt = charges.val / numOfInstSgrt;
        instAmt = Math.round(instAmt);
        double totalAmt = 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleChargers psChrgs = new PaymentScheduleChargers();
            long seq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_CHRG_SEQ);
            Instant currIns = Instant.now();
            psChrgs.setPaySchedChrgSeq(seq);
            psChrgs.setCrntRecFlg(true);
            psChrgs.setCrtdBy(currUser);
            psChrgs.setCrtdDt(currIns);
            psChrgs.setDelFlg(false);
            psChrgs.setEffStartDt(currIns);
            psChrgs.setLastUpdBy(currUser);
            psChrgs.setLastUpdDt(currIns);
            psChrgs.setChrgTypsSeq(charges.type);
            if (sgrtInsts.contains(i)) {
                psChrgs.setAmt(Math.round(instAmt));
                totalAmt = totalAmt + instAmt;
            }
            object.add(psChrgs);
        }

        long diffrence = (long) (Math.round(charges.val - totalAmt));
        long addmount = object.get(rndngAdjInst.intValue() - 1).getAmt() + diffrence;
        object.get(rndngAdjInst.intValue() - 1).setAmt(addmount);
        return object;

    }

    private Instant getAdjustedDay(Instant instDate) {
        Instant dueDt = null;
        LocalDateTime localDate = instDate.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DayOfWeek dueday = localDate.getDayOfWeek();
        switch (dueday) {
            case SATURDAY:
                dueDt = instDate.minus(Duration.ofDays(1));
                break;
            case SUNDAY:
                dueDt = instDate.plus(Duration.ofDays(1));
                break;
            default:
                dueDt = instDate;
                break;
        }
        return dueDt;

    }

    public List<ExpectedPaymentSchedule> getExpectedPaymentSchedule(long loanAppSeq) {

        LoanApplication loanApplication = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        Long aprvdLoanAmt = 0L;

        MwLoanAppPpalStngs mwLoanAppPpalStngs = mwLoanAppPpalStngsRepository.findOneByPrdSeqAndLoanAppSeq(loanApplication.getPrdSeq(),
                loanAppSeq);
        Double servChrgs = 0D;
        long roundFlg = 0;
        // Charges Calculations

        List<Object[]> chargesObj = mwLoanAppChrgStngsRepository.getLoanAppChrgStngsByRddSeqAndLoanAppSeq(loanApplication.getPrdSeq(),
                loanAppSeq);

        MwRefCdVal sts = mwRefCdValRepository.findRefCdByGrpAndVal("0106", APROVED_STATUS);
        MwRefCdVal typ = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        int vtyp = 0;
        if (sts.getRefCdSeq().longValue() == loanApplication.getLoanAppSts().longValue() && typ.getRefCd().equals(ISLAMIC_TYPE)) {
            vtyp = 1;
        }

        List<Map<String, ?>> vouchers = disbursementVoucherHeaderRepository.getVouchersbyLoanAppAndType(loanAppSeq, vtyp);
        Charges karzHassna = new Charges();
        for (Map map : vouchers) {
            if (!map.get("typ_id").equals("0009") && !map.get("typ_id").equals("0011")) {
                aprvdLoanAmt = aprvdLoanAmt + Long.parseLong(map.get("amt").toString());
            }
            if (map.get("typ_id").equals("0010")) {
                karzHassna.acct = map.get("gl_acct_num").toString();
                karzHassna.chrgName = map.get("typ_str").toString();
                karzHassna.roundFlg = 1;
                karzHassna.val = Long.parseLong(map.get("amt").toString());
                karzHassna.type = Long.parseLong(map.get("typ_seq").toString());
                karzHassna.typeId = map.get("typ_id").toString();
            }

        }

        List<Charges> chargesList = charges.getListofCharge(aprvdLoanAmt, chargesObj);

        for (Charges obj : chargesList) {
            if (obj.typeId.equals("0017")) {
                servChrgs = obj.val;
            }
            if (obj.roundFlg != 0) {
                roundFlg = obj.roundFlg;
            }
        }
        if (karzHassna.typeId != null)
            chargesList.add(karzHassna);

        Double totPrSc = aprvdLoanAmt + servChrgs;

        List<PaymentScheduleDetail> pplInts = new ArrayList();
        if (mwLoanAppPpalStngs.getIrrFlg() == 1) {
            if ((mwLoanAppPpalStngs.getNumOfInst() == mwLoanAppPpalStngs.getNumOfInstSgrt())
                    || (mwLoanAppPpalStngs.getNumOfInstSgrt() == 0L || mwLoanAppPpalStngs.getNumOfInstSgrt() == null)) {
                pplInts = getInstalmentWithIrrAndAllInst(mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt, mwLoanAppPpalStngs.getIrrRate(),
                        totPrSc, mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs);
            } else {
                pplInts = getInstalmentWithIrrAndBuiltInst(mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt,
                        mwLoanAppPpalStngs.getIrrRate(), mwLoanAppPpalStngs.getSgrtInst(), mwLoanAppPpalStngs.getRndngAdjInst(),
                        servChrgs);
            }
        } else {
            if ((mwLoanAppPpalStngs.getNumOfInst() == mwLoanAppPpalStngs.getNumOfInstSgrt())
                    || (mwLoanAppPpalStngs.getNumOfInstSgrt() == 0L || mwLoanAppPpalStngs.getNumOfInstSgrt() == null)) {
                pplInts = getInstalmentWithoutIrr(mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt, servChrgs,
                        mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs);
            } else {
                pplInts = getInstalmentWithoutIrrAndBuiltInst(mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt, servChrgs,
                        mwLoanAppPpalStngs.getSgrtInst(), mwLoanAppPpalStngs.getRndngAdjInst(), servChrgs);
            }
        }

        long instCount = 1L;
        List<ExpectedPaymentSchedule> expectedPaymentSchedules = new ArrayList();
        for (PaymentScheduleDetail psd : pplInts) {
            ExpectedPaymentSchedule expectedPaymentSchedule = new ExpectedPaymentSchedule();
            List<PaymentScheduleChargers> chargers = new ArrayList();
            psd.setInstNum(instCount);
            psd.setCrntRecFlg(true);
            instCount++;
            expectedPaymentSchedule.setDetail(psd);
            expectedPaymentSchedule.setChargers(chargers);
            expectedPaymentSchedules.add(expectedPaymentSchedule);

        }

        for (Charges obj : chargesList) {
            if (!obj.typeId.equals("0017")) {
                List<PaymentScheduleChargers> chargesInsts = getExpectedCharger(obj, mwLoanAppPpalStngs.getNumOfInst(), aprvdLoanAmt,
                        mwLoanAppPpalStngs.getRndngAdjInst(), mwLoanAppPpalStngs.getRndngAdjInst());
                for (int j = 0; j < pplInts.size(); j++) {
                    chargesInsts.get(j).setPaySchedDtlSeq(pplInts.get(j).getPaySchedDtlSeq());
                    PaymentScheduleChargers charger = chargesInsts.get(j);
                    expectedPaymentSchedules.get(j).getChargers().add(charger);
                }
            }
        }

        long allTtl = 0;
        for (ExpectedPaymentSchedule eps : expectedPaymentSchedules) {
            Long chrgTtl = 0L;
            for (PaymentScheduleChargers chr : eps.getChargers()) {
                chrgTtl = chrgTtl + (chr.getAmt() == null ? 0 : chr.getAmt());
            }
            long total = eps.getDetail().getPpalAmtDue() + eps.getDetail().getTotChrgDue() + chrgTtl;
            long rounded = roundUp(total, mwLoanAppPpalStngs.getRndngScl());
            long diff = rounded - total;
            for (PaymentScheduleChargers chr : eps.getChargers()) {
                if (chr.getChrgTypsSeq().longValue() == roundFlg) {
                    Long addmount = chr.getAmt();
                    chr.setAmt(addmount + diff);
                }
            }
            allTtl = allTtl + diff;
        }
        // long diff = allTtl - roundTtl;
        for (Charges obj : chargesList) {
            if (obj.adjFlg != 0 && obj.adjFlg == 1L) {
                for (PaymentScheduleChargers chr : expectedPaymentSchedules.get(mwLoanAppPpalStngs.getRndngAdjInst().intValue() - 1)
                        .getChargers()) {
                    if (chr.getChrgTypsSeq().longValue() == obj.type) {
                        Long addmount = chr.getAmt();
                        chr.setAmt(addmount - allTtl);

                    }
                }

            }
        }

        return expectedPaymentSchedules;

    }

    /*    public Map< ? , ? > nextDueInst2( long id ) {
        return paymentScheduleDetailRepository.getNextDueInst( id, 1 );
    }*/

    private List<PaymentScheduleChargers> getExpectedCharger(Charges charges, Long numOfInst, Long aprvdLoanAmt, Long rndndFlg,
                                                             Long rndngAdjInst) {
        List<PaymentScheduleChargers> object = new ArrayList();

        List<Integer> sgrtInsts = new ArrayList();
        if (charges.sgrtInst != null && !charges.sgrtInst.equals("")) {
            sgrtInsts = Stream.of((charges.sgrtInst.toString()).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            sgrtInsts = IntStream.rangeClosed(1, numOfInst.intValue()).boxed().collect(Collectors.toList());
        }
        long numOfInstSgrt = sgrtInsts.size();

        double instAmt = (long) charges.val / numOfInstSgrt;
        instAmt = Math.round(instAmt);
        double totalAmt = (long) 0;
        for (int i = 1; i <= numOfInst; i++) {
            PaymentScheduleChargers psChrgs = new PaymentScheduleChargers();
            psChrgs.setChrgTypsSeq(charges.type);
            if (sgrtInsts.contains(i)) {
                psChrgs.setAmt(Math.round(instAmt));
                totalAmt = totalAmt + instAmt;
            }
            object.add(psChrgs);
        }

        Long diffrence = (long) (charges.val - totalAmt);
        long addmount = object.get(rndngAdjInst.intValue() - 1).getAmt() + diffrence;
        object.get(rndndFlg.intValue() - 1).setAmt(addmount);
        return object;

    }

    public Instant addMonths(java.util.Date aDate, int number) {
        java.util.Calendar aCalendar = java.util.Calendar.getInstance();
        aCalendar.setTime(aDate);
        aCalendar.add(java.util.Calendar.MONTH, number);
        return aCalendar.getTime().toInstant();
    }

    public Instant subtractMonths(java.util.Date aDate, int number) {
        java.util.Calendar aCalendar = java.util.Calendar.getInstance();
        aCalendar.setTime(aDate);
        aCalendar.add(java.util.Calendar.MONTH, -number);
        return aCalendar.getTime().toInstant();
    }

    @Transactional
    public boolean isLoanReshelduled(LoanRschdDto loanRschdDto) {
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanRschdDto.loanAppSeq, true);
        List<LoanApplication> loans = loanApplicationRepository.findAllByPrntLoanAppSeqAndCrntRecFlg(app.getPrntLoanAppSeq(), true);
        boolean isResch = false;
        for (LoanApplication loan : loans) {
            MwLoanRschd rschd = mwLoanRschdRepository.findOneByLoanAppSeqAndCrntRecFlgAndRvslInstIsNull(loan.getLoanAppSeq(), true);
            if (rschd != null && rschd.getRschdMthdSeq().longValue() > 1l)
                isResch = true;
        }
        return isResch;
    }

    @Transactional
    public boolean reScheduleLoanMethodA(LoanRschdDto loanRschdDto, String currUser, String token) {
        Instant currIns = Instant.now();

        MwLoanRschd rschd = new MwLoanRschd();
        rschd.setCmnt(loanRschdDto.cmnt);
        rschd.setCrntRecFlg(true);
        rschd.setCrtdBy(currUser);
        rschd.setCrtdDt(currIns);
        rschd.setDelFlg(false);
        rschd.setEffStartDt(currIns);
        rschd.setGracePerd(loanRschdDto.perd);
        rschd.setLastUpdBy(currUser);
        rschd.setLastUpdDt(currIns);
        rschd.setLoanAppSeq(loanRschdDto.loanAppSeq);
        Long loanRschdSeq = SequenceFinder.findNextVal(Sequences.LOAN_RSCHD_SEQ);
        rschd.setLoanRschdSeq(loanRschdSeq);
        rschd.setRschdMthdSeq(loanRschdDto.methdSeq.longValue());
        rschd.setRschdRsnSeq(loanRschdDto.rsnKey);

        List<PaymentScheduleDetail> insts = new ArrayList<PaymentScheduleDetail>();
        List<PaymentScheduleDetail> paymentScheduleDetail = new ArrayList<PaymentScheduleDetail>();

        List<LoanApplication> activeLoans = loanApplicationRepository.findAllActiveLoansForClnt(loanRschdDto.clntSeq);
        for (LoanApplication loan : activeLoans) {
            List<PaymentScheduleDetail> inst = paymentScheduleDetailRepository.getDuaInstallmentsByLoanAppSeq(loanRschdDto.loanAppSeq);
            inst.forEach(obj -> {
                obj.setCrntRecFlg(false);
                obj.setDelFlg(true);
                obj.setLastUpdBy(currUser);
                obj.setLastUpdDt(currIns);
                PaymentScheduleDetail psd = new PaymentScheduleDetail();
                psd.setCrntRecFlg(true);
                psd.setCrtdBy(currUser);
                psd.setCrtdDt(currIns);
                psd.setDelFlg(false);
                psd.setDueDt(getAdjustedDay(addMonths(Date.from(obj.getDueDt()), loanRschdDto.perd.intValue())));
                psd.setEffEndDt(obj.getEffEndDt());
                psd.setEffStartDt(obj.getEffStartDt());
                psd.setInstNum(obj.getInstNum());
                psd.setLastUpdBy(currUser);
                psd.setPaySchedDtlSeq(obj.getPaySchedDtlSeq());
                psd.setPaySchedHdrSeq(obj.getPaySchedHdrSeq());
                psd.setPpalAmtDue(obj.getPpalAmtDue());
                psd.setPymtStsKey(obj.getPymtStsKey());
                psd.setTotChrgDue(obj.getTotChrgDue());
                paymentScheduleDetail.add(psd);

            });
            insts.addAll(inst);
        }

        insts.addAll(paymentScheduleDetail);
        paymentScheduleDetailRepository.save(insts);
        mwLoanRschdRepository.save(rschd);
        return true;
    }

    public String comparePartialAndOutstanding(LoanRschdDto loanRschdDto) {
        String resp = "";

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanRschdDto.loanAppSeq, true);
        List<LoanApplication> loans = loanApplicationRepository.findAllByPrntLoanAppSeqAndCrntRecFlg(app.getPrntLoanAppSeq(), true);

        for (LoanApplication loan : loans) {
            List<PaymentScheduleDetail> insts = paymentScheduleDetailRepository
                    .getInstallmentsWithPartialAndZeroOutStandingAmt(loan.getLoanAppSeq().longValue());
            for (PaymentScheduleDetail dtl : insts) {
                resp = resp + (resp.length() > 0 ? ", " : "") + dtl.getInstNum();
            }
        }

        return resp;
    }

    public boolean resheduleEligibiltyCriteriaCheck(long loanAppSeq) {
        LoanApplication parentLoan = loanApplicationRepository.findParentLoan(loanAppSeq);
        MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(parentLoan.getPrdSeq(), true);
        if (prd.getPrdGrpSeq().longValue() == 5305l || prd.getPrdSeq().longValue() == 26l)
            return false;
        if (prd.getPrdGrpSeq().longValue() == 6) {
            Types advTyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0501", 1, 0, true);
            MwExp exp = mwExpRepository.findOneByExpRefAndExpnsTypSeqAndCrntRecFlgAndDelFlg(parentLoan.getLoanAppSeq().toString(),
                    advTyp.getTypSeq(), true, false);
            PaymentScheduleDetail inst = paymentScheduleDetailRepository.getDtlWithInstDueInApril(parentLoan.getLoanAppSeq());

            String str_date = "01-Apr-2020";
            DateFormat formatter;
            Date date = null;
            formatter = new SimpleDateFormat("dd-MMM-yy");
            try {
                date = formatter.parse(str_date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (exp != null && inst == null && date.after(Date.from(exp.getCrtdDt())))
                return true;
        } else {
            DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository
                    .getHdrBeforeAprilForResheduleCriteria(parentLoan.getLoanAppSeq());
            PaymentScheduleDetail inst = paymentScheduleDetailRepository.getDtlWithInstDueInApril(parentLoan.getLoanAppSeq());
            if (hdr != null && inst == null)
                return true;
        }
        return false;
    }

    @Transactional
    public boolean reScheduleLoanMethodB(LoanRschdDto loanRschdDto, String currUser, String token) {
        Instant currIns = Instant.now();

        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(loanRschdDto.clntSeq);
        MwRefCdVal partialStsRefCd = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "1145");

        MwLoanRschd rschd = new MwLoanRschd();
        rschd.setCmnt(loanRschdDto.cmnt);
        rschd.setCrntRecFlg(true);
        rschd.setCrtdBy(currUser);
        rschd.setCrtdDt(currIns);
        rschd.setDelFlg(false);
        rschd.setEffStartDt(currIns);
        rschd.setGracePerd(loanRschdDto.perd);
        rschd.setLastUpdBy(currUser);
        rschd.setLastUpdDt(currIns);
        rschd.setLoanAppSeq(loanRschdDto.loanAppSeq);
        Long loanRschdSeq = SequenceFinder.findNextVal(Sequences.LOAN_RSCHD_SEQ);
        rschd.setLoanRschdSeq(loanRschdSeq);
        rschd.setRschdMthdSeq(loanRschdDto.methdSeq.longValue());
        rschd.setRschdRsnSeq(loanRschdDto.rsnKey);

        List<LoanApplication> activeLoans = loanApplicationRepository.findAllActiveLoansForClnt(loanRschdDto.clntSeq);

        long startIndexInstallment = 0L;
        boolean isPartial = false;
        List<AjRcvryDTO> adjDtos = new ArrayList<AjRcvryDTO>();

        if (activeLoans.size() > 1) {
            LoanApplication mainLoan = loanApplicationRepository.findAllActiveLoanForClntBasicPrd(loanRschdDto.clntSeq);
            LoanApplication assocLoan = loanApplicationRepository.findAllActiveLoanForClntAccosPrd(loanRschdDto.clntSeq);

            List<PaymentScheduleDetail> mainInst = paymentScheduleDetailRepository
                    .getDuaAndPartialInstallmentsByLoanAppSeq(mainLoan.getLoanAppSeq());
            List<PaymentScheduleDetail> assocInst = paymentScheduleDetailRepository
                    .getDuaAndPartialInstallmentsByLoanAppSeq(assocLoan.getLoanAppSeq());

            if (mainInst.size() > 0 && assocInst.size() > 0) {
                if (mainInst.get(0).getInstNum().longValue() != assocInst.get(0).getInstNum().longValue()) {

                    PaymentScheduleDetail inst = paymentScheduleDetailRepository.getInstByLoanAppSeqAndInstNum(mainLoan.getLoanAppSeq(),
                            assocInst.get(0).getInstNum().longValue());

                    List<MwRcvryTrx> trxs = mwRcvryTrxRepository.findRecoveryTrxForPymtSchedDtlSeq(inst.getPaySchedDtlSeq());
                    for (MwRcvryTrx trx : trxs) {
                        AjRcvryDTO adjDto = new AjRcvryDTO();
                        adjDto.trxId = trx.getRcvryTrxSeq();
                        adjDto.pymtAmt = 0L;
                        adjDto.chngRsnCmnt = "Resheduling";
                        adjDto.chngRsnKey = 0L;
                        adjDto.pymtDt = new SimpleDateFormat("dd/MM/yyyy").format(trx.getPymtDt());
                        adjDto.rcvryTypsSeq = trx.getRcvryTypSeq();
                        adjDto.instr = trx.getInstrNum();
                        Long clntSeq = recoveryService.reversePayment(adjDto, currUser, token);
                        adjDto.rePymtAmt = trx.getPymtAmt();
                        adjDtos.add(adjDto);
                    }

                }
            }
        }

        for (LoanApplication loan : activeLoans) {
            // if ( activeLoans.size() == 1 ) {
            // LoanApplication loan = activeLoans.get( 0 );
            List<PaymentScheduleDetail> paymentScheduleDetail = new ArrayList<PaymentScheduleDetail>();
            List<PaymentScheduleChargers> chrges = new ArrayList<PaymentScheduleChargers>();

            DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loan.getLoanAppSeq(),
                    true);
            List<PaymentScheduleChargers> nchrges = new ArrayList<PaymentScheduleChargers>();
            List<PaymentScheduleDetail> inst = paymentScheduleDetailRepository
                    .getDuaAndPartialInstallmentsByLoanAppSeq(loan.getLoanAppSeq());

            if (inst.size() > 0) {
                startIndexInstallment = inst.get(0).getInstNum();
                rschd.setBgnInstNum(startIndexInstallment);
                if (partialStsRefCd.getRefCdSeq().longValue() == inst.get(0).getPymtStsKey().longValue()) {
                    isPartial = true;
                    List<PaymentScheduleDetail> partialInsts = paymentScheduleDetailRepository
                            .getPartialInstallmentsByLoanAppSeq(loan.getLoanAppSeq());

                    for (PaymentScheduleDetail pInst : partialInsts) {
                        List<MwRcvryTrx> trxs = mwRcvryTrxRepository.findRecoveryTrxForPymtSchedDtlSeq(pInst.getPaySchedDtlSeq());
                        for (MwRcvryTrx trx : trxs) {
                            AjRcvryDTO adjDto = new AjRcvryDTO();
                            adjDto.trxId = trx.getRcvryTrxSeq();
                            adjDto.pymtAmt = 0L;
                            adjDto.chngRsnCmnt = "Resheduling";
                            adjDto.chngRsnKey = 0L;
                            adjDto.pymtDt = new SimpleDateFormat("dd/MM/yyyy").format(trx.getPymtDt());
                            adjDto.rcvryTypsSeq = trx.getRcvryTypSeq();
                            adjDto.instr = trx.getInstrNum();
                            Long clntSeq = recoveryService.reversePayment(adjDto, currUser, token);
                            adjDto.rePymtAmt = trx.getPymtAmt();
                            adjDtos.add(adjDto);
                        }
                    }
                }
                List<PaymentScheduleChargers> nextDueInstChrges = paymentScheduleChargersRepository
                        .findAllByPaySchedDtlSeqAndCrntRecFlg(inst.get(0).getPaySchedDtlSeq(), true);
                for (int i = 0; i < loanRschdDto.perd.intValue(); i++) {
                    PaymentScheduleDetail psd = new PaymentScheduleDetail();
                    psd.setCrntRecFlg(true);
                    psd.setCrtdBy(currUser);
                    psd.setCrtdDt(currIns);
                    psd.setDelFlg(false);
                    psd.setDueDt(getAdjustedDay(addMonths(Date.from(inst.get(0).getDueDt()), i)));
                    psd.setEffStartDt(currIns);
                    psd.setInstNum(inst.get(0).getInstNum() + i);
                    psd.setLastUpdBy(currUser);
                    Long dtlSeq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_DTL_SEQ);
                    psd.setPaySchedDtlSeq(dtlSeq);
                    psd.setPaySchedHdrSeq(inst.get(0).getPaySchedHdrSeq());
                    psd.setPpalAmtDue(0L);
                    psd.setPymtStsKey(inst.get(0).getPymtStsKey());
                    psd.setTotChrgDue(inst.get(0).getTotChrgDue());
                    paymentScheduleDetail.add(psd);

                    for (PaymentScheduleChargers chrg : nextDueInstChrges) {
                        PaymentScheduleChargers nChrg = new PaymentScheduleChargers();
                        nChrg.setAmt(chrg.getAmt());
                        nChrg.setChrgTypsSeq(chrg.getChrgTypsSeq());
                        nChrg.setCrntRecFlg(true);
                        nChrg.setCrtdBy(currUser);
                        nChrg.setCrtdDt(currIns);
                        nChrg.setDelFlg(false);
                        nChrg.setEffStartDt(currIns);
                        nChrg.setLastUpdBy(currUser);
                        nChrg.setLastUpdDt(currIns);
                        Long chrgSeq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_CHRG_SEQ);
                        nChrg.setPaySchedChrgSeq(chrgSeq);
                        nChrg.setPaySchedDtlSeq(dtlSeq);
                        chrges.add(nChrg);
                        nchrges.add(nChrg);
                    }
                }
                for (PaymentScheduleDetail obj : inst) {
                    obj.setCrntRecFlg(false);
                    obj.setDelFlg(true);
                    obj.setLastUpdBy(currUser);
                    obj.setLastUpdDt(currIns);
                    paymentScheduleDetail.add(obj);
                    PaymentScheduleDetail psd = new PaymentScheduleDetail();
                    psd.setCrntRecFlg(true);
                    psd.setCrtdBy(currUser);
                    psd.setCrtdDt(currIns);
                    psd.setDelFlg(false);
                    psd.setDueDt(getAdjustedDay(addMonths(Date.from(obj.getDueDt()), loanRschdDto.perd.intValue())));
                    psd.setEffEndDt(obj.getEffEndDt());
                    psd.setEffStartDt(obj.getEffStartDt());
                    psd.setInstNum(obj.getInstNum() + loanRschdDto.perd.intValue());
                    psd.setLastUpdBy(currUser);
                    // Long dtlSeq = SequenceFinder.findNextVal( Sequences.PYMT_SCHED_DTL_SEQ );
                    psd.setPaySchedDtlSeq(obj.getPaySchedDtlSeq());
                    psd.setPaySchedHdrSeq(obj.getPaySchedHdrSeq());
                    psd.setPpalAmtDue(obj.getPpalAmtDue());
                    psd.setPymtStsKey(obj.getPymtStsKey());
                    psd.setTotChrgDue(obj.getTotChrgDue());
                    paymentScheduleDetail.add(psd);

                    List<PaymentScheduleChargers> instChrges = paymentScheduleChargersRepository
                            .findAllByPaySchedDtlSeqAndCrntRecFlg(psd.getPaySchedDtlSeq(), true);
                    for (PaymentScheduleChargers nChrg : instChrges) {
                        nChrg.setLastUpdBy(currUser);
                        nChrg.setLastUpdDt(currIns);
                        Long chrgSeq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_CHRG_SEQ);
                        nChrg.setPaySchedChrgSeq(chrgSeq);
                        nChrg.setPaySchedDtlSeq(obj.getPaySchedDtlSeq());
                        chrges.add(nChrg);
                    }

                }
                if (nchrges.size() > 0)
                    vchrPostingService.resheduleChargesJV(nchrges, hdr.getDsbmtHdrSeq(), currUser, brnch.getBrnchSeq(),
                            loan.getLoanAppSeq());

                paymentScheduleDetailRepository.save(paymentScheduleDetail);
                paymentScheduleChargersRepository.save(chrges);

            }

            if (loan.getLoanAppSeq().longValue() == loan.getPrntLoanAppSeq().longValue()) {
                MwClntHlthInsrCard exCard = mwClntHlthInsrCardRepository.findOneByLoanAppSeqAndCrntRecFlg(loan.getLoanAppSeq(), true);
                if (exCard != null) {
                    exCard.setCrntRecFlg(false);
                    exCard.setLastUpdBy(currUser);
                    exCard.setLastUpdDt(currIns);
                    exCard.setEffEndDt(currIns);
                    exCard.setDelFlg(true);

                    MwClntHlthInsrCard card = new MwClntHlthInsrCard();
                    card.setCardExpiryDt(addMonths(Date.from(exCard.getCardExpiryDt()), loanRschdDto.perd.intValue()));
                    card.setCardNum(exCard.getCardNum());
                    card.setClntHlthInsrCardSeq(exCard.getClntHlthInsrCardSeq());
                    card.setCrntRecFlg(true);
                    card.setCrtdBy(currUser);
                    card.setCrtdDt(currIns);
                    card.setDelFlg(false);
                    card.setEffStartDt(currIns);
                    card.setLastUpdBy(currUser);
                    card.setLastUpdDt(currIns);
                    card.setLoanAppSeq(exCard.getLoanAppSeq());
                    mwClntHlthInsrCardRepository.save(card);

                }

            }
        }

        if (isPartial && adjDtos.size() > 0) {
            for (AjRcvryDTO adjDto : adjDtos) {
                AppRcvryDTO appRcvryDTO = new AppRcvryDTO();
                appRcvryDTO.clientId = "" + loanRschdDto.clntSeq;
                appRcvryDTO.post = true;
                appRcvryDTO.pymtAmt = adjDto.rePymtAmt;
                appRcvryDTO.pymtDt = new SimpleDateFormat("dd/MM/yyyy").format(Date.from(Instant.now()));
                appRcvryDTO.rcvryTypsSeq = adjDto.rcvryTypsSeq;
                appRcvryDTO.instr = adjDto.instr;
                Map m = recoveryService.applyRecoveryPayment(appRcvryDTO);
            }
        }
        // else {
        // LoanApplication mainLoan = loanApplicationRepository.findAllActiveLoanForClntBasicPrd( loanRschdDto.clntSeq );
        // LoanApplication assocLoan = loanApplicationRepository.findAllActiveLoanForClntAccosPrd( loanRschdDto.clntSeq );
        //
        // DisbursementVoucherHeader mainHdr = disbursementVoucherHeaderRepository
        // .findOneByLoanAppSeqAndCrntRecFlg( mainLoan.getLoanAppSeq(), true );
        // DisbursementVoucherHeader assocHdr = disbursementVoucherHeaderRepository
        // .findOneByLoanAppSeqAndCrntRecFlg( assocLoan.getLoanAppSeq(), true );
        //
        // List< PaymentScheduleChargers > nchrges = new ArrayList< PaymentScheduleChargers >();
        //
        // List< PaymentScheduleDetail > mainInst = paymentScheduleDetailRepository
        // .getDuaAndPartialInstallmentsByLoanAppSeq( mainLoan.getLoanAppSeq() );
        // List< PaymentScheduleDetail > assocInst = paymentScheduleDetailRepository
        // .getDuaAndPartialInstallmentsByLoanAppSeq( mainLoan.getLoanAppSeq() );
        //
        //
        // if ( mainInst.size() > 0 ) {
        // if ( partialStsRefCd.getRefCdSeq().longValue() == mainInst.get( 0 ).getPymtStsKey().longValue() ) {
        //
        // }
        // }
        // }
        mwLoanRschdRepository.save(rschd);
        return true;
    }

    @Transactional
    public boolean reverseMethodC(LoanRschdDto loanRschdDto, String currUser) {
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanRschdDto.loanAppSeq, true);
        List<LoanApplication> loans = loanApplicationRepository.findAllByPrntLoanAppSeqAndCrntRecFlg(app.getPrntLoanAppSeq(), true);
        String pattern = "dd-MM-yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        for (LoanApplication loan : loans) {
            MwLoanRschd rschd = mwLoanRschdRepository.findOneByLoanAppSeqAndCrntRecFlgAndRvslInstIsNotNull(loan.getLoanAppSeq(), true);
            if (rschd != null && rschd.getRschdMthdSeq().longValue() == 1l) {
                if (df.format(Date.from(rschd.getCrtdDt())).equals("28-07-2020")) {
                    List<PaymentScheduleDetail> insts = paymentScheduleDetailRepository.getDtlWithInstDueInDate(loan.getLoanAppSeq(),
                            "01-OCT-2020");
                    insts.forEach(inst -> {
                        inst.setDueDt(getAdjustedDay(subtractMonths(Date.from(inst.getDueDt()), rschd.getRvslInst())));
                    });
                    paymentScheduleDetailRepository.save(insts);
                } else if (df.format(Date.from(rschd.getCrtdDt())).equals("14-07-2020")) {
                    List<PaymentScheduleDetail> insts = paymentScheduleDetailRepository.getDtlWithInstDueInDate(loan.getLoanAppSeq(),
                            "01-APR-21");
                    insts.forEach(inst -> {
                        inst.setDueDt(getAdjustedDay(subtractMonths(Date.from(inst.getDueDt()), rschd.getRvslInst())));
                    });
                }
            }
        }
        return true;
    }

    @Transactional
    public boolean reScheduleLoanMethodC(LoanRschdDto loanRschdDto, String currUser, String token) {
        Instant currIns = Instant.now();

        Long dueSts = mwRefCdValRepository.findOneByRefCdAndCrntRecFlg("0945", true).getRefCdSeq();
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(loanRschdDto.clntSeq);
        // MwRefCdVal partialStsRefCd = mwRefCdValRepository.findRefCdByGrpAndVal( "0179", "1145" );
        Types lifeIns = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("4", 1, 0, true);
        Types liveStckIns = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("5", 1, 0, true);
        Types takaful = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0018", 1, 0, true);

        List<LoanApplication> activeLoans = loanApplicationRepository.findAllActiveLoansForClnt(loanRschdDto.clntSeq);

        long startIndexInstallment = 0L;
        // boolean isPartial = false;

        for (LoanApplication loan : activeLoans) {
            MwLoanRschd rschd = new MwLoanRschd();
            rschd.setCmnt(loanRschdDto.cmnt);
            rschd.setCrntRecFlg(true);
            rschd.setCrtdBy(currUser);
            rschd.setCrtdDt(currIns);
            rschd.setDelFlg(false);
            rschd.setEffStartDt(currIns);
            rschd.setGracePerd(loanRschdDto.perd);
            rschd.setLastUpdBy(currUser);
            rschd.setLastUpdDt(currIns);
            rschd.setLoanAppSeq(loan.getLoanAppSeq());
            Long loanRschdSeq = SequenceFinder.findNextVal(Sequences.LOAN_RSCHD_SEQ);
            rschd.setLoanRschdSeq(loanRschdSeq);
            rschd.setRschdMthdSeq(loanRschdDto.methdSeq.longValue());
            rschd.setRschdRsnSeq(loanRschdDto.rsnKey);

            List<PaymentScheduleDetail> paymentScheduleDetail = new ArrayList<PaymentScheduleDetail>();

            List<PaymentScheduleDetail> accruedPsds = new ArrayList<PaymentScheduleDetail>();
            List<PaymentScheduleChargers> chrges = new ArrayList<PaymentScheduleChargers>();

            DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loan.getLoanAppSeq(),
                    true);
            List<PaymentScheduleChargers> nchrges = new ArrayList<PaymentScheduleChargers>();
            List<PaymentScheduleDetail> inst = paymentScheduleDetailRepository
                    .getDuaAndPartialInstallmentsByLoanAppSeq(loan.getLoanAppSeq());

            if (inst.size() > 0) {
                startIndexInstallment = inst.get(0).getInstNum();
                rschd.setBgnInstNum(startIndexInstallment);
                long nextInstSrvChrg = inst.get(0).getTotChrgDue();
                PaymentScheduleDetail secondInstPymtSchedDtl = paymentScheduleDetailRepository
                        .getInstByLoanAppSeqAndInstNumUsingRowIndex(loan.getLoanAppSeq(), 2);
                long remainingLoanAmt = inst.stream().mapToLong(PaymentScheduleDetail::getPpalAmtDue).sum();
                long nmbrOfInst = Long.valueOf(inst.size());
                List<PaymentScheduleChargers> scndInstChrges = paymentScheduleChargersRepository
                        .findAllByPaySchedDtlSeqAndCrntRecFlg(secondInstPymtSchedDtl.getPaySchedDtlSeq(), true);
                for (int i = 0; i < loanRschdDto.perd.intValue(); i++) {
                    PaymentScheduleDetail psd = new PaymentScheduleDetail();
                    psd.setCrntRecFlg(true);
                    psd.setCrtdBy(currUser);
                    psd.setCrtdDt(currIns);
                    psd.setDelFlg(false);
                    psd.setDueDt(getAdjustedDay(addMonths(Date.from(inst.get(0).getDueDt()), i)));
                    psd.setEffStartDt(currIns);
                    psd.setInstNum(inst.get(0).getInstNum() + i);
                    psd.setLastUpdBy(currUser);
                    Long dtlSeq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_DTL_SEQ);
                    psd.setPaySchedDtlSeq(dtlSeq);
                    psd.setPaySchedHdrSeq(inst.get(0).getPaySchedHdrSeq());
                    psd.setPpalAmtDue(0L);
                    psd.setPymtStsKey(dueSts);
                    psd.setTotChrgDue(0L);
                    Long chrgVal = 0L;

                    for (PaymentScheduleChargers chrg : scndInstChrges) {
                        if (chrg.getChrgTypsSeq().longValue() == -2 || chrg.getChrgTypsSeq().longValue() == lifeIns.getTypSeq().longValue()
                                || chrg.getChrgTypsSeq().longValue() == liveStckIns.getTypSeq().longValue()
                                || chrg.getChrgTypsSeq().longValue() == takaful.getTypSeq().longValue()) {
                            chrgVal = chrgVal + chrg.getAmt().longValue();
                            PaymentScheduleChargers nChrg = new PaymentScheduleChargers();
                            nChrg.setAmt(chrg.getAmt());
                            nChrg.setChrgTypsSeq(chrg.getChrgTypsSeq());
                            nChrg.setCrntRecFlg(true);
                            nChrg.setCrtdBy(currUser);
                            nChrg.setCrtdDt(currIns);
                            nChrg.setDelFlg(false);
                            nChrg.setEffStartDt(currIns);
                            nChrg.setLastUpdBy(currUser);
                            nChrg.setLastUpdDt(currIns);
                            Long chrgSeq = SequenceFinder.findNextVal(Sequences.PYMT_SCHED_CHRG_SEQ);
                            nChrg.setPaySchedChrgSeq(chrgSeq);
                            nChrg.setPaySchedDtlSeq(dtlSeq);
                            chrges.add(nChrg);
                            nchrges.add(nChrg);
                        }
                    }

                    paymentScheduleDetail.add(psd);
                }

                MwLoanAppPpalStngs mwLoanAppPpalStngs = mwLoanAppPpalStngsRepository.findOneByPrdSeqAndLoanAppSeq(loan.getPrdSeq(),
                        loan.getLoanAppSeq());
                MwLoanAppChrgStngs srvChrgStng = mwLoanAppChrgStngsRepository.getSrvChrgStng(loan.getLoanAppSeq());
                for (int i = 0; i < inst.size(); i++) {
                    PaymentScheduleDetail obj = inst.get(i);
                    obj.setCrntRecFlg(false);
                    obj.setDelFlg(true);
                    obj.setLastUpdBy(currUser);
                    obj.setLastUpdDt(currIns);
                    paymentScheduleDetail.add(obj);

                    PaymentScheduleDetail psd = new PaymentScheduleDetail();

                    psd.setCrntRecFlg(true);
                    psd.setCrtdBy(currUser);
                    psd.setCrtdDt(currIns);
                    psd.setDelFlg(false);
                    psd.setDueDt(getAdjustedDay(addMonths(Date.from(obj.getDueDt()), loanRschdDto.perd.intValue())));
                    psd.setEffEndDt(obj.getEffEndDt());
                    psd.setEffStartDt(obj.getEffStartDt());
                    psd.setInstNum(obj.getInstNum() + loanRschdDto.perd.intValue());
                    psd.setLastUpdBy(currUser);
                    psd.setPaySchedDtlSeq(obj.getPaySchedDtlSeq());
                    psd.setPaySchedHdrSeq(obj.getPaySchedHdrSeq());
                    psd.setPymtStsKey(obj.getPymtStsKey());
                    psd.setPpalAmtDue(obj.getPpalAmtDue());
                    psd.setTotChrgDue(obj.getTotChrgDue());
                    // paymentScheduleDetail.add( psd );
                    accruedPsds.add(psd);

                }

                double accuralServiceCharge = Math.round(nextInstSrvChrg * loanRschdDto.perd);
                rschd.setAcrdSrvcChrg((long) accuralServiceCharge);
                accruedPsds = getInstalmentAccuralImpl(nmbrOfInst, accuralServiceCharge, mwLoanAppPpalStngs.getRndngAdjInst(),
                        accuralServiceCharge, accruedPsds);

                // List< ExpectedPaymentSchedule > expectedPaymentSchedules = new ArrayList< ExpectedPaymentSchedule >();
                // for ( PaymentScheduleDetail psd : accruedPsds ) {
                // ExpectedPaymentSchedule expectedPaymentSchedule = new ExpectedPaymentSchedule();
                // List< PaymentScheduleChargers > chargers = new ArrayList();
                // expectedPaymentSchedule.setDetail( psd );
                // expectedPaymentSchedule.setChargers( chargers );
                // expectedPaymentSchedules.add( expectedPaymentSchedule );
                // }
                // List< Object[] > chargesObj = mwLoanAppChrgStngsRepository
                // .getLoanAppChrgStngsByRddSeqAndLoanAppSeq( loan.getPrdSeq(), loan.getLoanAppSeq() );
                //
                // List< Charges > chargesList = charges.getListofCharge( loan.getAprvdLoanAmt(), chargesObj );
                //
                // long allTtl = 0;
                // boolean roundService = true;
                // for ( ExpectedPaymentSchedule eps : expectedPaymentSchedules ) {
                // Long chrgTtl = 0L;
                // for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                // chrgTtl = chrgTtl + ( chr.getAmt() == null ? 0 : chr.getAmt() );
                // }
                // long total = eps.getDetail().getPpalAmtDue() + eps.getDetail().getTotChrgDue() + chrgTtl;
                // long rounded = roundUp( total, mwLoanAppPpalStngs.getRndngScl() );
                // long diff = rounded - total;
                //
                // for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                // if ( chr.getChrgTypsSeq().longValue() == srvChrgStng.getRndngFlg().longValue() ) {
                // Long addmount = chr.getAmt();
                // chr.setAmt( addmount + diff );
                // roundService = false;
                // }
                // }
                // if ( roundService ) {
                // Long addmount = eps.getDetail().getTotChrgDue();
                // eps.getDetail().setTotChrgDue( addmount + diff );
                // }
                // allTtl = allTtl + diff;
                //
                // }
                // if ( roundService ) {
                // Long addmount = expectedPaymentSchedules.get( 0 ).detail.getTotChrgDue();
                // expectedPaymentSchedules.get( 0 ).detail.setTotChrgDue( addmount - allTtl );
                // } else {
                // // long diff = allTtl - roundTtl;
                // for ( Charges obj : chargesList ) {
                // if ( obj.adjFlg != 0 && obj.adjFlg == 1L && !obj.typeId.equals( "0017" ) ) {
                // for ( PaymentScheduleChargers chr : expectedPaymentSchedules
                // .get( mwLoanAppPpalStngs.getRndngAdjInst().intValue() - 1 ).getChargers() ) {
                // if ( chr.getChrgTypsSeq().longValue() == obj.type ) {
                // Long addmount = chr.getAmt();
                // chr.setAmt( addmount - allTtl );
                //
                // }
                // }
                //
                // }
                // }
                // }
                // for ( ExpectedPaymentSchedule eps : expectedPaymentSchedules ) {
                // for ( PaymentScheduleChargers chr : eps.getChargers() ) {
                // chrges.add( chr );
                // }
                // }

                paymentScheduleDetail.addAll(accruedPsds);

                if (nchrges.size() > 0)
                    vchrPostingService.resheduleChargesJV(nchrges, hdr.getDsbmtHdrSeq(), currUser, brnch.getBrnchSeq(),
                            loan.getLoanAppSeq());

                paymentScheduleDetailRepository.save(paymentScheduleDetail);
                paymentScheduleChargersRepository.save(chrges);

            }

            if (loan.getLoanAppSeq().longValue() == loan.getPrntLoanAppSeq().longValue()) {
                MwClntHlthInsrCard exCard = mwClntHlthInsrCardRepository.findOneByLoanAppSeqAndCrntRecFlg(loan.getLoanAppSeq(), true);
                if (exCard != null) {
                    exCard.setCrntRecFlg(false);
                    exCard.setLastUpdBy(currUser);
                    exCard.setLastUpdDt(currIns);
                    exCard.setEffEndDt(currIns);
                    exCard.setDelFlg(true);

                    MwClntHlthInsrCard card = new MwClntHlthInsrCard();
                    card.setCardExpiryDt(addMonths(Date.from(exCard.getCardExpiryDt()), loanRschdDto.perd.intValue()));
                    card.setCardNum(exCard.getCardNum());
                    card.setClntHlthInsrCardSeq(exCard.getClntHlthInsrCardSeq());
                    card.setCrntRecFlg(true);
                    card.setCrtdBy(currUser);
                    card.setCrtdDt(currIns);
                    card.setDelFlg(false);
                    card.setEffStartDt(currIns);
                    card.setLastUpdBy(currUser);
                    card.setLastUpdDt(currIns);
                    card.setLoanAppSeq(exCard.getLoanAppSeq());
                    mwClntHlthInsrCardRepository.save(card);

                }

            }

            mwLoanRschdRepository.save(rschd);
        }

        return true;
    }

    public boolean reScheduleLoanDueDate(long clntSeq, int days, String currUser) {
        Instant currIns = Instant.now();

        List<PaymentScheduleDetail> inst = paymentScheduleDetailRepository.getDuaInstallmentsByClientSeq(clntSeq);
        List<PaymentScheduleDetail> paymentScheduleDetail = new ArrayList<PaymentScheduleDetail>();

        inst.forEach(obj -> {
            obj.setCrntRecFlg(false);
            obj.setDelFlg(true);
            obj.setLastUpdBy(currUser);
            obj.setLastUpdDt(currIns);
            PaymentScheduleDetail psd = new PaymentScheduleDetail();
            psd.setCrntRecFlg(true);
            psd.setCrtdBy(currUser);
            psd.setCrtdDt(currIns);
            psd.setDelFlg(false);
            psd.setDueDt(getAdjustedDay(obj.getDueDt().plus(Duration.ofDays(days))));
            psd.setEffEndDt(obj.getEffEndDt());
            psd.setEffStartDt(obj.getEffStartDt());
            psd.setInstNum(obj.getInstNum());
            psd.setLastUpdBy(currUser);
            psd.setPaySchedDtlSeq(obj.getPaySchedDtlSeq());
            psd.setPaySchedHdrSeq(obj.getPaySchedHdrSeq());
            psd.setPpalAmtDue(obj.getPpalAmtDue());
            psd.setPymtStsKey(obj.getPymtStsKey());
            psd.setTotChrgDue(obj.getTotChrgDue());
            paymentScheduleDetail.add(psd);

        });

        inst.addAll(paymentScheduleDetail);
        paymentScheduleDetailRepository.save(inst);
        return true;
    }

    public boolean deleteHealthInsuranceCharges(long loanAppSeq, String user) {
        Instant now = Instant.now();
        List<PaymentScheduleChargers> pscs = paymentScheduleChargersRepository.findAllkashCareChargesByLoanAppSeq(loanAppSeq);
        pscs.forEach(d -> {
            d.setEffEndDt(Instant.now());
            d.setLastUpdBy(user);
            d.setLastUpdDt(Instant.now());
            d.setDelFlg(true);
            d.setCrntRecFlg(false);
        });
        paymentScheduleChargersRepository.save(pscs);
        return true;
    }

    public boolean revertHealthInsuranceCharges(long loanAppSeq, String user) {

        Instant now = Instant.now();
        List<PaymentScheduleChargers> pscs = paymentScheduleChargersRepository.findAllDeletedkashCareChargesByLoanAppSeq(loanAppSeq);
        pscs.forEach(c -> {
            c.setCrntRecFlg(true);
            c.setDelFlg(false);
            c.setLastUpdDt(now);
            c.setLastUpdBy(user);
        });
        paymentScheduleChargersRepository.save(pscs);
        return true;
    }

    // Added by Zohaib Asim - Dated 16-03-2021
    // Production Issue: Report(Clickable) error while Loan is in Submitted Status
    // For Submitted/Approved button will be disable
    public int pymtSchedDtlGenerated(long loanAppSeq) {
        List psdList = paymentScheduleDetailRepository.getPymtSchedDtlByLoanAppSeq(loanAppSeq);
        if (psdList != null && psdList.size() > 0)
            return Integer.parseInt(psdList.get(0).toString());
        else
            return 0;
    }

    private class ExpectedPaymentSchedule {

        PaymentScheduleDetail detail;

        List<PaymentScheduleChargers> chargers;

        public PaymentScheduleDetail getDetail() {
            return detail;
        }

        public void setDetail(PaymentScheduleDetail detail) {
            this.detail = detail;
        }

        public List<PaymentScheduleChargers> getChargers() {
            return chargers;
        }

        public void setChargers(List<PaymentScheduleChargers> chargers) {
            this.chargers = chargers;
        }

    }
    // End by Zohaib Asim

}
