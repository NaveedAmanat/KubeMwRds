package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.domain.LoanApplication;
import com.idev4.rds.domain.MwPrdGrp;
import com.idev4.rds.domain.Types;
import com.idev4.rds.dto.*;
import com.idev4.rds.repository.LoanApplicationRepository;
import com.idev4.rds.repository.MwPdcDtlRepository;
import com.idev4.rds.repository.MwPrdGrpRepository;
import com.idev4.rds.repository.TypesRepository;
import com.idev4.rds.service.DisbursementService;
import com.idev4.rds.service.PaymentScheduleService;
import com.idev4.rds.service.ReportComponent;
import com.idev4.rds.service.ReportsService;
import com.idev4.rds.util.Queries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DisbursementController {

    private final Logger log = LoggerFactory.getLogger(DisbursementController.class);

    private final DisbursementService disbursementService;
    private final EntityManager em;
    @Autowired
    PaymentScheduleService paymentScheduleService;
    @Autowired
    ReportComponent reportComponent;
    @Autowired
    ReportsService reportsService;
    @Autowired
    TypesRepository typesRepository;
    @Autowired
    MwPrdGrpRepository mwPrdGrpRepository;
    @Autowired
    MwPdcDtlRepository mwPdcDtlRepository;
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    public DisbursementController(DisbursementService disbursementService, EntityManager em) {
        super();
        this.disbursementService = disbursementService;
        this.em = em;
    }

    @GetMapping("/application/{id}")
    @Timed
    public ResponseEntity<LoanApplicationHeaderDTO> getLoanApplicationsByApplicationId(@PathVariable Long id) {
        return new ResponseEntity<>(disbursementService.getLoanApplicationById(id), HttpStatus.OK);
    }

    // @GetMapping ( "/disbursement-verification/{user}/{roll}" )
    // @Timed
    // public ResponseEntity< List< LoanApplicationDTO > > getDisbursementVerification( Pageable pageable, String user, String role ) {
    // log.debug( "REST request to verifi client" );
    // return new ResponseEntity<>( disbursementService.getAllLoanApplications( user, role ), HttpStatus.OK );
    // }

    // @GetMapping ( "/disbursement" )
    // @Timed
    // public ResponseEntity< Map< ? , ? > > getAllApplications( @RequestParam String user, @RequestParam String role,
    // @RequestParam ( required = false ) String filter, @RequestParam ( required = false ) String sort,
    // @PathVariable ( required = false ) String direction, @RequestParam Integer pageNumber, @RequestParam Integer pageSize ) {
    // log.debug( "REST request to get LoanApplication in Disbursement" );
    // return new ResponseEntity<>(
    // disbursementService.getAllLoanApplications( user, role, filter, sort, direction, pageNumber, pageSize ), HttpStatus.OK );
    // }

    @GetMapping("/disbursement")
    @Timed
    public ResponseEntity<Map<?, ?>> getAllApplications(@RequestParam String user, @RequestParam Long brnchSeq,
                                                        @RequestParam String role, @RequestParam(required = false) String filter, @RequestParam(required = false) String sort,
                                                        @PathVariable(required = false) String direction, @RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                        @RequestParam Boolean isCount) {
        log.debug("REST request to get LoanApplication in Disbursement");
        return new ResponseEntity<>(
                disbursementService.getAllLoanApplications(user, brnchSeq, role, filter, sort, direction, pageNumber, pageSize, isCount),
                HttpStatus.OK);
    }

    @GetMapping("/disbursement-posting/{id}/{post}/{amlChck}")
    @Timed
    public ResponseEntity<?> disbursementPosting(@PathVariable Long id, @PathVariable Boolean post,
                                                 @PathVariable Boolean amlChck, @RequestHeader(value = "Authorization") String token) {

        log.debug("REST request Disbursement Posting");
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String status = "";
        MwPrdGrp grp = mwPrdGrpRepository.findOneByLoanAppSeq(id);
        try {
            Query qry = em.createNativeQuery(Queries.verisysBmApprovalFunc).setParameter("P_LOAN_APP_SEQ", id);
            String versts = qry.getSingleResult().toString();
            if (versts != null && versts.equals("0")) {
                resp.put("status", "2");
                resp.put("canProceed", "false");
                resp.put("message", "NADRA verification is pending");
                return ResponseEntity.ok().body(resp);
            }
            // Added By Naveed - Dated - 24-11-2021
            // Operation - SCR System Control - 30-day loan application policy
            // organized Response Status
            LoanApplication loanApplication = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(id, true);
            // Zohaib Asim - Dated 21-01-2022 - KM Products Sale01 not included in 30-Days Policy
            if ((ChronoUnit.DAYS.between(loanApplication.getEffStartDt(), Instant.now())) > 30
                    && loanApplication.getLoanAppSts() != 1305) {
                resp.put("status", "1");
                resp.put("canProceed", "false");
                resp.put("message", "30-day loan application policy");
                return ResponseEntity.ok().body(resp);
            }
            // Ended By Naveed - Dated - 24-11-2021

            List<Object> isAppraisal = em.createNativeQuery(Queries.IS_APPRAISAL_BY_LOAN_APP_SEQ)
                    .setParameter("loanAppSeq", id).getResultList();
            if (!isAppraisal.isEmpty()) {
                long ndi = new BigDecimal(em.createNativeQuery(Queries.NADI_BY_LOAN_APP_SEQ)
                        .setParameter("loanAppSeq", id).getSingleResult().toString()).longValue();
                if (ndi <= 0) {
                    resp.put("status", "3");
                    resp.put("canProceed", "false");
                    resp.put("message", "NDI should be greater then zero");
                    return ResponseEntity.ok().body(resp);
                }
            }
            status = disbursementService.updateLoanApp(id, post, currUser, token, amlChck);
        } catch (Exception e) {
            if (grp.getPrdGrpId().equals("5765")) {
                disbursementService.revertKrkLoan(id, currUser);
            }
        }
        if (status.contains("Disbursement Posted")) {
            // Added by Zohaib Asim - Dated 24-12-2020
            // KSZB Claims - Response
            String statusSplitArr[] = status.split("/");

            resp.put("status", "0");
            resp.put("canProceed", "true");
            resp.put("sucess", statusSplitArr[0]);
            resp.put("loanAppSts", statusSplitArr[1]);
            resp.put("prdSeq", statusSplitArr[2]);
            // End by Zohaib Asim

            if (grp.getPrdGrpId().equals("5765")) {
                try {
                    resp.put("chckAmnt", "" + disbursementService.getCheckAmount(id));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }
        if (grp.getPrdGrpId().equals("5765")) {
            disbursementService.revertKrkLoan(id, currUser);
        }
        resp.put("status", "4");
        resp.put("canProceed", "false");
        resp.put("message", status);
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/disbursement-multiple-posting/{ids}")
    @Timed
    public ResponseEntity<Map> disbursementPosting(@PathVariable String ids, @RequestHeader(value = "Authorization") String token) {
        log.debug("REST request Disbursement Posting");
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        disbursementService.disbursementMultiplePosting(ids, currUser, token);
        resp.put("status", ids);
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/delete-application/{id}/{cmnt}")
    @Timed
    public ResponseEntity<Map> deleteLoanApp(@PathVariable Long id, @PathVariable String cmnt,
                                             @RequestHeader(value = "Authorization") String token) {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id == null || id <= 0) {
            resp.put("error", "Seems Incorrect Application ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        boolean flag = disbursementService.deleteLoanApp(id, cmnt, currUser, token);
        resp.put("loanAppSeq", String.valueOf(id));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/defered-application/{id}/{cmnt}/{role}")
    public ResponseEntity<Map> deferedLoanApp(@PathVariable Long id, @PathVariable String cmnt,
                                              @PathVariable String role,
                                              @RequestHeader(value = "Authorization") String token) throws Exception {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id == null || id <= 0) {
            resp.put("error", "Seems Incorrect Application ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        boolean flag = disbursementService.deferedLoanApp(id, cmnt, currUser, token, role);
        log.info("Deferred Application: loanAppSeq->" + id + ", Status->" + flag);
        resp.put("loanAppSeq", String.valueOf(id));
        resp.put("status", "" + flag);
        resp.put("msg", "ADC Disbursement processed and paid");
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/revert-application/{id}/{cmnt}")
    @Timed
    public ResponseEntity<Map> revertLoanApp(@PathVariable Long id, @PathVariable String cmnt) {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id == null || id <= 0) {
            resp.put("error", "Seems Incorrect Application ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        boolean flag = disbursementService.revertLoanApp(id, cmnt, currUser);
        resp.put("loanAppSeq", String.valueOf(id));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/disbursement-voucher/{id}")
    @Timed
    public ResponseEntity<DisbursementVoucherHeaderDTO> disbursementVoucher(@PathVariable Long id) {
        log.debug("REST request to get disbursement voucher");
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(disbursementService.getDisbursementVoucherByLoanAppSeq(id, currUser), HttpStatus.OK);
    }

    @PostMapping("/add-disbursement-voucher")
    @Timed
    public ResponseEntity<Map> createDisbursementVoucher(
            @RequestBody DisbursementVoucherDetailDTO disbursementVoucherDetailDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (disbursementVoucherDetailDTO.dsbmtHdrSeq == null || disbursementVoucherDetailDTO.dsbmtHdrSeq <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Header !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.pymtTypSeq == null || disbursementVoucherDetailDTO.pymtTypSeq <= 0) {
            resp.put("error", "Seems Incorrect Payment Mode !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.instrNum == null || disbursementVoucherDetailDTO.instrNum.isEmpty()) {
            resp.put("error", "    Instruement No. is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.amt == null) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.addDisbursementVoucher(disbursementVoucherDetailDTO, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/update-disbursement-voucher")
    @Timed
    public ResponseEntity<Map> updateDisbursementVoucher(
            @RequestBody DisbursementVoucherDetailDTO disbursementVoucherDetailDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (disbursementVoucherDetailDTO.dsbmtDtlKey == null || disbursementVoucherDetailDTO.dsbmtDtlKey <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Voucher ID !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.pymtTypSeq == null || disbursementVoucherDetailDTO.pymtTypSeq <= 0) {
            resp.put("error", "Seems Incorrect Payment Mode !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.instrNum == null || disbursementVoucherDetailDTO.instrNum.isEmpty()) {
            resp.put("error", "    Instruement No. is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.amt == null) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.updateDisbursementVoucher(disbursementVoucherDetailDTO, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/delete-disbursement-voucher")
    @Timed
    public ResponseEntity<Map> deleteDisbursementVoucher(@RequestBody DisbursementVoucherDetailDTO disbursementVoucherDetailDTO)
            throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (disbursementVoucherDetailDTO.dsbmtDtlKey == null || disbursementVoucherDetailDTO.dsbmtDtlKey <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Voucher ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.updateDisbursementVoucher(disbursementVoucherDetailDTO, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/agency/{id}")
    @Timed
    public ResponseEntity<DisbursementVoucherHeaderDTO> agency(@PathVariable Long id) {
        log.debug("REST request to get disbursement voucher");
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(disbursementService.getAgencyByLoanAppSeq(id, currUser), HttpStatus.OK);
    }

    @PostMapping("/add-agency")
    @Timed
    public ResponseEntity<Map> createAgency(@RequestBody DisbursementVoucherDetailDTO disbursementVoucherDetailDTO)
            throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (disbursementVoucherDetailDTO.loanAppSeq <= 0) {
            resp.put("error", "Invalid Loan App Seq !!");
            return ResponseEntity.badRequest().body(resp);
        }
        // Added By Naveed - Date - 23-01-2022
        // SCR - Mobile Wallet Control
        // is Mobile Wallet is Valid against client
        resp.putAll(disbursementService.isMobileWalletNumValid(disbursementVoucherDetailDTO));
        if (resp.containsKey("mobInvalid")) {
            resp.put("mobInvalid", resp.get("mobInvalid"));
            return ResponseEntity.ok().body(resp);
        }
        // Modified By Naveed - Date - 23-01-2022
        // pass complete dto instead of loanAppSeq
        disbursementVoucherDetailDTO.dsbmtHdrSeq = disbursementService
                .addDisbursementVoucherHeader(disbursementVoucherDetailDTO, currUser);
        // Ended By Naveed - Date - 23-01-2022
        if (disbursementVoucherDetailDTO.dsbmtHdrSeq == null || disbursementVoucherDetailDTO.dsbmtHdrSeq <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Header !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.pymtTypSeq == null || disbursementVoucherDetailDTO.pymtTypSeq <= 0) {
            resp.put("error", "Seems Incorrect Payment Mode !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (disbursementVoucherDetailDTO.amt == null) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        // boolean isKRKPrdGrp = disbursementService.IsKRKPrdGrp( disbursementVoucherDetailDTO.loanAppSeq );
        // if ( isKRKPrdGrp ) {
        // Types type = typesRepository.findOneByTypSeqAndCrntRecFlg( disbursementVoucherDetailDTO.pymtTypSeq, true );
        // if ( !( type.getTypId().equals( "0008" ) || type.getTypId().equals( "0007" ) ) ) {
        // resp.put( "error", "Only BANK / Remitance mode is allowed" );
        // return ResponseEntity.badRequest().body( resp );
        // }
        // }

        try {
            boolean hasDefLoans = disbursementService.hasDefferedLoan(disbursementVoucherDetailDTO.loanAppSeq);
            if (hasDefLoans) {
                Types type = typesRepository.findOneByTypSeqAndCrntRecFlg(disbursementVoucherDetailDTO.pymtTypSeq, true);
                if (type.getTypId().equals("0007") || type.getTypId().equals("0004")) {
                    resp.put("error", "Incorrect Payment Mode (Loan Deffered)");
                    return ResponseEntity.badRequest().body(resp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", "Unable to read Hist data. Contact Admin.");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.addDisbursementVoucher(disbursementVoucherDetailDTO, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/update-agency")
    @Timed
    public ResponseEntity<Map> updateAgencyVoucher(@RequestBody DisbursementVoucherDetailDTO disbursementVoucherDetailDTO)
            throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (disbursementVoucherDetailDTO.dsbmtDtlKey == null || disbursementVoucherDetailDTO.dsbmtDtlKey <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Voucher ID !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (disbursementVoucherDetailDTO.pymtTypSeq == null || disbursementVoucherDetailDTO.pymtTypSeq <= 0) {
            resp.put("error", "Seems Incorrect Payment Mode !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (disbursementVoucherDetailDTO.amt == null) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        // Added By Naveed - Date - 23-01-2022
        // SCR - Mobile Wallet Control
        // is Mobile Wallet is Valid against client
        resp.putAll(disbursementService.isMobileWalletNumValid(disbursementVoucherDetailDTO));
        if (resp.containsKey("mobInvalid")) {
            resp.put("mobInvalid", resp.get("mobInvalid"));
            return ResponseEntity.ok().body(resp);
        }

        disbursementVoucherDetailDTO.dsbmtHdrSeq = disbursementService
                .addDisbursementVoucherHeader(disbursementVoucherDetailDTO, currUser);
        if (disbursementVoucherDetailDTO.dsbmtHdrSeq == null || disbursementVoucherDetailDTO.dsbmtHdrSeq <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Header !!");
            return ResponseEntity.badRequest().body(resp);
        }
        // Ended By Naveed - Date 23-01-2022

        // boolean isKRKPrdGrp = disbursementService.IsKRKPrdGrp( disbursementVoucherDetailDTO.loanAppSeq );
        // if ( isKRKPrdGrp ) {
        // Types type = typesRepository.findOneByTypSeqAndCrntRecFlg( disbursementVoucherDetailDTO.pymtTypSeq, true );
        // if ( !( type.getTypId().equals( "0008" ) || type.getTypId().equals( "0007" ) ) ) {
        // resp.put( "error", "Only BANK / Remitance mode is allowed" );
        // return ResponseEntity.badRequest().body( resp );
        // }
        // }
        try {
            boolean hasDefLoans = disbursementService.hasDefferedLoan(disbursementVoucherDetailDTO.loanAppSeq);
            if (hasDefLoans) {
                Types type = typesRepository.findOneByTypSeqAndCrntRecFlg(disbursementVoucherDetailDTO.pymtTypSeq, true);
                if (type.getTypId().equals("0007") || type.getTypId().equals("0004")) {
                    resp.put("error", "Incorrect Payment Mode (Loan Deffered)");
                    return ResponseEntity.badRequest().body(resp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", "Unable to read Hist data. Contact Admin.");
            return ResponseEntity.badRequest().body(resp);
        }
        long commSeq = disbursementService.updateAgency(disbursementVoucherDetailDTO, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @DeleteMapping("/delete-agency/{dsbmtDtlKey}")
    @Timed
    public ResponseEntity<Map> deleteAgency(@PathVariable Long dsbmtDtlKey) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (dsbmtDtlKey <= 0) {
            resp.put("error", "Seems Incorrect Disbursement Voucher ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.deleteDisbursementVoucher(dsbmtDtlKey, currUser);
        resp.put("dsbmtDtlKey", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/payment-schedule/{id}")
    @Timed
    public ResponseEntity<List<PaymentScheduleDetailDTO>> paymentSchedule(@PathVariable Long id) {
        log.debug("REST request to get Payment Schedule");
        return new ResponseEntity<>(disbursementService.getPaymentSchedules(id), HttpStatus.OK);
    }

    @GetMapping("/expected-payment-schedule/{id}")
    @Timed
    public ResponseEntity<List<?>> getExpectedPaymentSchedule(@PathVariable Long id) {
        log.debug("REST request to get Payment Schedule");
        return new ResponseEntity<>(paymentScheduleService.getExpectedPaymentSchedule(id), HttpStatus.OK);
    }

    @GetMapping("/payment-schedule-detail/{id}")
    @Timed
    public ResponseEntity<List<PaymentScheduleChargersDTO>> paymentScheduleDetail(@PathVariable Long id) {
        log.debug("REST request to get Payment Schedule");
        return new ResponseEntity<>(disbursementService.getPaymentSchedulesDetial(id), HttpStatus.OK);
    }

    @GetMapping("/pdcs/{id}")
    @Timed
    public ResponseEntity<PdcHeaderDTO> getPdcHeader(@PathVariable Long id) {
        log.debug("REST request to get Payment Schedule");
        return new ResponseEntity<>(disbursementService.getPdcHeaderByLoanAppSeq(id), HttpStatus.OK);
    }

    @PostMapping("/add-pdc-header")
    @Timed
    public ResponseEntity<Map> createPdcHeader(@RequestBody PdcHeaderDTO pdcHeaderDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (pdcHeaderDTO.loanAppSeq == null || pdcHeaderDTO.loanAppSeq == 0L) {
            resp.put("error", "Loan App Seq is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (pdcHeaderDTO.bankKey == null || pdcHeaderDTO.bankKey == 0L) {
            resp.put("error", "Bank is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (pdcHeaderDTO.brnchNm == null || pdcHeaderDTO.brnchNm.isEmpty()) {
            resp.put("error", "Branch Name is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (pdcHeaderDTO.acctNum == null || pdcHeaderDTO.acctNum.isEmpty()) {
            resp.put("error", "Account Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.addPdcHeader(pdcHeaderDTO, currUser);
        if (commSeq == 0) {
            resp.put("error", "Account Number Already Exist");
            return ResponseEntity.badRequest().body(resp);
        }
        resp.put("pdcHdrSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/update-pdc-header")
    @Timed
    public ResponseEntity<Map> updatePdcHeader(@RequestBody PdcHeaderDTO pdcHeaderDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (pdcHeaderDTO.bankKey == null || pdcHeaderDTO.bankKey == 0L) {
            resp.put("error", "Bank Name is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (pdcHeaderDTO.brnchNm == null || pdcHeaderDTO.brnchNm.isEmpty()) {
            resp.put("error", "Branch Name is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (pdcHeaderDTO.acctNum == null || pdcHeaderDTO.acctNum.isEmpty()) {
            resp.put("error", "Account Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.updatePdcHeader(pdcHeaderDTO, currUser);
        resp.put("pdcHdrSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/add-pdc-detail")
    @Timed
    public ResponseEntity<Map> createPdcDetail(@RequestBody MwPdcDtlDTO mwPdcDtlDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (mwPdcDtlDTO.pdcHdrSeq == null || mwPdcDtlDTO.pdcHdrSeq <= 0) {
            resp.put("error", "Seems Incorrect PDC Header !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.pdcId == null || mwPdcDtlDTO.pdcId.isEmpty()) {
            resp.put("error", "PDC Id is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.collDt == null) {
            resp.put("error", "Collection Date is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.amt == null || mwPdcDtlDTO.amt <= 0) {
            resp.put("error", "Seems Incorrect Amount !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (mwPdcDtlDTO.cheqNum == null || mwPdcDtlDTO.cheqNum.isEmpty()) {
            resp.put("error", "Check Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.addPdcDetail(mwPdcDtlDTO, currUser);

        if (commSeq == -1) {
            resp.put("error", "Checque Number is duplicate !!");
            return ResponseEntity.badRequest().body(resp);
        }
        resp.put("pdcDtlSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/update-pdc-detail")
    @Timed
    public ResponseEntity<Map> updatePdcDetail(@RequestBody MwPdcDtlDTO mwPdcDtlDTO) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (mwPdcDtlDTO.pdcDtlSeq == null || mwPdcDtlDTO.pdcDtlSeq <= 0) {
            resp.put("error", "Seems Incorrect PDC Detail Seq!!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.pdcId == null || mwPdcDtlDTO.pdcId.isEmpty()) {
            resp.put("error", "PDC Id is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.collDt == null) {
            resp.put("error", "Collection Date is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.cheqNum == null || mwPdcDtlDTO.cheqNum.isEmpty()) {
            resp.put("error", "Check Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (mwPdcDtlDTO.amt == null || mwPdcDtlDTO.amt <= 0) {
            resp.put("error", "Seems Incorrect Amount !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.updatePdcDetail(mwPdcDtlDTO, currUser);
        if (commSeq == -1) {
            resp.put("error", "Checque Number is duplicate !!");
            return ResponseEntity.badRequest().body(resp);
        }
        resp.put("pdcDtlSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @DeleteMapping("/delete-pdc-detail/{id}")
    @Timed
    public ResponseEntity<Map> deletePdcDetail(@PathVariable Long id) throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id == null || id <= 0) {
            resp.put("error", "pdcDtlSeq Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.deletePdcDetail(id, currUser);
        resp.put("pdcDtlSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/genrate-pdc")
    @Timed
    public ResponseEntity<?> genratePdcs(@RequestBody GenratePdcsDTO denratePdcsDTO) {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (denratePdcsDTO.loanAppSeq == null || denratePdcsDTO.loanAppSeq == 0L) {
            resp.put("error", "Loan App Seq is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (denratePdcsDTO.bankKey == null || denratePdcsDTO.bankKey == 0L) {
            resp.put("error", "Bank is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (denratePdcsDTO.brnchNm == null || denratePdcsDTO.brnchNm.isEmpty()) {
            resp.put("error", "Branch Name is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (denratePdcsDTO.acctNum == null || denratePdcsDTO.acctNum.isEmpty()) {
            resp.put("error", "Account Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (denratePdcsDTO.cheqNum == null || denratePdcsDTO.cheqNum.isEmpty()) {
            resp.put("error", "Check Number is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        PdcHeaderDTO dto = disbursementService.genratePDCs(denratePdcsDTO, currUser);
        if (dto == null) {
            resp.put("error", "PDC Already Generated or Checque number already used!!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (dto.error) {
            resp.put("error", "Account Number Already Exist");
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping("/batch-pdc-posting/{id}")
    @Timed
    public ResponseEntity<Map> batchPdcPosting(@PathVariable Long id, @RequestBody List<MwPdcDtlDTO> mwPdcDtlDTOs)
            throws URISyntaxException {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (mwPdcDtlDTOs == null || mwPdcDtlDTOs.isEmpty()) {
            resp.put("error", "Seems Incorrect PDC Header !!");
            return ResponseEntity.badRequest().body(resp);
        }

        long commSeq = disbursementService.batchPostingPdcs(id, mwPdcDtlDTOs, currUser);
        resp.put("pdcDtlSeq", String.valueOf(commSeq));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/get-types/{id}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getPaymentMode(@PathVariable Long id) {
        return new ResponseEntity<>(disbursementService.getTypeByCatgryKey(id), HttpStatus.OK);
    }

    @GetMapping("/get-type-branch-catgry/{catKey}/{brnch}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getTypsByBrnachAndCatgry(@PathVariable Long catKey, @PathVariable Long brnch) {
        return new ResponseEntity<>(disbursementService.getTypeByBranchCatgryKey(catKey, brnch), HttpStatus.OK);
    }

    @GetMapping("/get-type-branch/{brnch}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getTypsByBrnach(@PathVariable Long brnch) {
        return new ResponseEntity<>(disbursementService.getTypeByBranch(brnch), HttpStatus.OK);
    }

    @GetMapping("/get-clnt-pymnt/{clntSeq}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getTypeByClntSeq(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(disbursementService.getTypeByClntSeq(clntSeq), HttpStatus.OK);
    }

    @GetMapping("/get-wrt-of-clnt-pymnt/{clntSeq}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getTypeByWrtOffClntSeq(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(disbursementService.getTypeByWrtOffClntSeq(clntSeq), HttpStatus.OK);
    }

    @GetMapping("/get-clnt-remit/{loanAppSeq}")
    @Timed
    public ResponseEntity<List<TypesDTO>> getClntRemit(@PathVariable Long loanAppSeq) {
        return new ResponseEntity<>(disbursementService.getClntRemtType(loanAppSeq), HttpStatus.OK);
    }

    @GetMapping("/get-brnch-remit/{user}")
    @Timed
    public ResponseEntity<List<TypesDTO>> get(@PathVariable String user) {
        return new ResponseEntity<>(disbursementService.getBrnchRemit(user), HttpStatus.OK);
    }

    // @GetMapping ( "/upcomeing-due-installment/{id}" )
    // @Timed
    // public ResponseEntity< Map< ? , ? > > nextDueInstallment( @PathVariable long id ) {
    // return new ResponseEntity<>( paymentScheduleService.nextDueInst( id ), HttpStatus.OK );
    // }

    @GetMapping("/genrate-payment-schedule/{id}/{date}")
    @Timed
    public ResponseEntity<?> genratePaymentSchedule(@PathVariable Long id, @PathVariable String date) {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id == null || id <= 0) {
            resp.put("error", "Seems Incorrect Loan App Id ");
            return ResponseEntity.badRequest().body(resp);
        }
        if (date == null || date.isEmpty()) {
            resp.put("error", "First Installment Date is Missing ");
            return ResponseEntity.badRequest().body(resp);
        }

        Boolean dtlFlag = paymentScheduleService.hasDsbmtDtls(id);
        if (!dtlFlag) {
            resp.put("error", "Disbursement Voucher Detail Missing ");
            return ResponseEntity.badRequest().body(resp);
        }
        Boolean flag = paymentScheduleService.genratePaymentSchedule(id, date, currUser);
        if (flag)
            return new ResponseEntity<>(disbursementService.getPaymentSchedules(id), HttpStatus.OK);
        else
            resp.put("error", "Something went wrong !!");
        return ResponseEntity.badRequest().body(resp);
    }

    // @GetMapping ( "/genrate-repayment-schedule/{method}/{clntSeq}/{months}" )
    // @Timed
    // public ResponseEntity< ? > genrateRepaymentSchedule( @PathVariable Integer method, @PathVariable Long clntSeq,
    // @PathVariable Integer months ) {
    // Map< String, String > resp = new HashMap< String, String >();
    // if ( clntSeq == null || clntSeq <= 0 ) {
    // resp.put( "error", "Seems Incorrect Client Id !!" );
    // return ResponseEntity.badRequest().body( resp );
    // }
    // if ( months == null || months < 1 ) {
    // resp.put( "error", "Seems Incorrect Number of Months!!" );
    // return ResponseEntity.badRequest().body( resp );
    // }
    // String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
    // if ( method.intValue() == 1 ) {
    // return new ResponseEntity<>( paymentScheduleService.reScheduleLoanMethodA( clntSeq, months, currUser ), HttpStatus.OK );
    // } else if ( method.intValue() == 2 ) {
    // return new ResponseEntity<>( paymentScheduleService.reScheduleLoanMethodB( clntSeq, months, currUser ), HttpStatus.OK );
    // }
    // resp.put( "error", "Seems Incorrect Method!!" );
    // return ResponseEntity.badRequest().body( resp );
    // }

    @PostMapping("/reshedule-loan")
    @Timed
    public ResponseEntity<?> resheduleLoan(@RequestBody LoanRschdDto loanRschdDto,
                                           @RequestHeader(value = "Authorization") String token) {

        Map<String, String> resp = new HashMap<String, String>();
        if (loanRschdDto.clntSeq == null || loanRschdDto.clntSeq <= 0) {
            resp.put("error", "Seems Incorrect Client Id !!");
            return ResponseEntity.badRequest().body(resp);
        }
        if (loanRschdDto.perd == null || loanRschdDto.perd < 1) {
            resp.put("error", "Seems Incorrect Number of Months!!");
            return ResponseEntity.badRequest().body(resp);
        }
        boolean posted = paymentScheduleService.isLoanReshelduled(loanRschdDto);
        if (posted) {
            resp.put("error", "Loan Already Resheduled");
            return ResponseEntity.badRequest().body(resp);
        }
        if (loanRschdDto.perd == 1) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        loanRschdDto.perd = Long.valueOf(loanRschdDto.perd.intValue() - 1);

        boolean eligible = paymentScheduleService.resheduleEligibiltyCriteriaCheck(loanRschdDto.loanAppSeq);
        if (!eligible) {
            resp.put("error", "Loan Not Eligible For Resheduling");
            return ResponseEntity.badRequest().body(resp);
        }

        String partialInstHavingZeroOutstanding = paymentScheduleService.comparePartialAndOutstanding(loanRschdDto);
        if (partialInstHavingZeroOutstanding.length() > 0) {
            resp.put("error",
                    "Instalments # " + partialInstHavingZeroOutstanding + " have Partial Status with 0 Outstanding. Contact Admin.");
            return ResponseEntity.badRequest().body(resp);
        }
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (loanRschdDto.methdSeq.intValue() == 1) {
            return new ResponseEntity<>(paymentScheduleService.reScheduleLoanMethodA(loanRschdDto, currUser, token), HttpStatus.OK);
        } else if (loanRschdDto.methdSeq.intValue() == 2) {
            boolean flg = paymentScheduleService.reverseMethodC(loanRschdDto, currUser);
            return new ResponseEntity<>(paymentScheduleService.reScheduleLoanMethodB(loanRschdDto, currUser, token), HttpStatus.OK);
        } else if (loanRschdDto.methdSeq.intValue() == 3) {
            boolean flg = paymentScheduleService.reverseMethodC(loanRschdDto, currUser);
            return new ResponseEntity<>(paymentScheduleService.reScheduleLoanMethodC(loanRschdDto, currUser, token), HttpStatus.OK);
        }
        resp.put("error", "Seems Incorrect Method!!");
        return ResponseEntity.badRequest().body(resp);
    }

    @GetMapping("/print-payment-schedule/{id}")
    @Timed
    public HttpEntity<byte[]> report(HttpServletResponse response, @PathVariable Long id) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getRepaymentReport(id, currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Payment Schedule.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }

    // Added by Areeba - 3-11-2022
    @GetMapping("/print-payment-schedule-kswk")
    @Timed
    public HttpEntity<byte[]> reportKSWK(HttpServletResponse response) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getRepaymentInfoKSWKReport(currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Payment Schedule More Info (KSWK).pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }
    // Ended by Areeba

    //ADDED BY YOUSAF DATED: 14-OCT-2022
    @GetMapping("/print-payment-schedule-ktk/{id}")
    @Timed
    public HttpEntity<byte[]> reportKTK(HttpServletResponse response, @PathVariable Long id) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getRepaymentReportKtk(id, currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Payment Schedule.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);

    }

    @GetMapping("/print-health-card/{id}")
    @Timed
    public HttpEntity<byte[]> printHealthCard(@PathVariable Long id) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getHealthCardReport(id, currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Health Card.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }

    @GetMapping(value = "/print-undertaking/{id}")
    @Timed
    public HttpEntity<byte[]> printUndertaking(@PathVariable Long id) throws IOException {
        // String path = request.getContextPath();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getUndertakingReport(id, currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Undertaking.pdf");
        header.setContentLength(bytes.length);

        return new HttpEntity<byte[]>(bytes, header);
    }

    @GetMapping(value = "/print-client-info/{id}")
    @Timed
    public HttpEntity<byte[]> printClientInfo(@PathVariable Long id) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getClientInfoReport(id, currUser);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Client Info.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }

    // @GetMapping ( value = "/print-posted-report/{date}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printPostedReport( @PathVariable String date, @PathVariable long branch ) throws IOException {
    // String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getPostedReport( date, currUser, branch );
    // HttpHeaders header = new HttpHeaders();
    // header.setContentType( MediaType.APPLICATION_PDF );
    // header.set( HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Posted.pdf" );
    // header.setContentLength( bytes.length );
    // return new HttpEntity< byte[] >( bytes, header );
    // }

    @GetMapping("/print-lpd/{id}")
    @Timed
    public HttpEntity<byte[]> printLpd(HttpServletResponse response, @PathVariable long id) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getKSKLPD(id, currUser);
        return new HttpEntity<byte[]>(bytes, getHeader("KSK LPD", bytes.length));
    }

    private HttpHeaders getHeader(String name, int lenght) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + name + ".pdf");
        header.setContentLength(lenght);
        return header;
    }

    @PutMapping("/save-vouchers/{loanAppSeq}")
    @Timed
    public ResponseEntity<Map> batchPdcPosting(@PathVariable Long loanAppSeq) {

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (loanAppSeq == null || loanAppSeq <= 0) {
            resp.put("error", "Seems Incorrect Loan ID !!");
            return ResponseEntity.badRequest().body(resp);
        }

        Boolean flag = disbursementService.saveVouchers(loanAppSeq, currUser);
        resp.put("flag", String.valueOf(flag));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/delete-health-insur-on-active-loan/{loanAppSeq}")
    @Timed
    public ResponseEntity<?> deleteHealthInsurOnActiveLoan(@PathVariable Long loanAppSeq) {
        Map<String, String> resp = new HashMap<String, String>();
        if (loanAppSeq == null || loanAppSeq <= 0) {
            resp.put("error", "Seems Incorrect Loan Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean flg = paymentScheduleService.deleteHealthInsuranceCharges(loanAppSeq, currUser);
        return new ResponseEntity<>(true, HttpStatus.OK);

    }

    @GetMapping("/revert-health-insur-on-active-loan/{clntSeq}")
    @Timed
    public ResponseEntity<?> revertHealthInsurOnActiveLoan(@PathVariable Long clntSeq) {
        Map<String, String> resp = new HashMap<String, String>();
        if (clntSeq == null || clntSeq <= 0) {
            resp.put("error", "Seems Incorrect Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean flg = paymentScheduleService.revertHealthInsuranceCharges(clntSeq, currUser);
        return new ResponseEntity<>(true, HttpStatus.OK);

    }

    @GetMapping("/print-agency-info-report/{loanAppSeq}")
    @Timed
    public HttpEntity<byte[]> printAgencyInfo(HttpServletResponse response, @PathVariable long loanAppSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getAgencyInfo(loanAppSeq, user);
        return new HttpEntity<byte[]>(bytes, getHeader("Agency Info", bytes.length));
    }

    // Added by Zohaib Asim - Dated 16-03-2021
    @GetMapping("/pymt-sched-dtl-generated/{loanAppSeq}")
    public ResponseEntity<?> pymtSchedDtlGenerated(@PathVariable Long loanAppSeq) throws IOException {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        int psdCount = paymentScheduleService.pymtSchedDtlGenerated(loanAppSeq);
        System.out.println("PSD Count:" + psdCount);
        resp.put("PSDCount", String.valueOf(psdCount));
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
    // End by Zohaib Asim

    // Added By Naveed - Date - 23-01-2022
    // SCR - Mobile Wallet Control
    // all mobile wallet history against client
    @GetMapping("/get-mobile-wallet-history/{loanAppSeq}")
    @Timed
    public ResponseEntity<?> getPrevLoanDisburseMode(@PathVariable long loanAppSeq) {
        return ResponseEntity.ok().body(disbursementService.getClientMobileWalletHist(loanAppSeq));
    }

    // all mobile wallet channel modes
    @GetMapping("/get-mobile-wallet-types")
    @Timed
    public ResponseEntity<List<TypesDTO>> mobileWalletTypes() {
        return new ResponseEntity<>(disbursementService.getMobileWalletTypes(), HttpStatus.OK);
    }
    // Ended by Naveed - Date - 23-01-2022

    // Added by Areeba - Date - 31-10-2022
    @GetMapping("/print-one-link-slip/{id}")
    @Timed
    public HttpEntity<byte[]> printOneLinkSlip(@PathVariable Long id) throws IOException {
        byte[] bytes = reportsService.getOneLinkReport(id);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Munsalik-1Link Depository Slip.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }
    // Ended by Areeba
}
