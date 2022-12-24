package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.dto.AccountLedger;
import com.idev4.rds.dto.BookDetailsDTO;
import com.idev4.rds.dto.MwPrdDTO;
import com.idev4.rds.service.GenericService;
import com.idev4.rds.service.ReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final Logger log = LoggerFactory.getLogger(DisbursementController.class);

    @Autowired
    ReportsService reportsService;

    @Autowired
    GenericService genericService;

    @GetMapping("/brnch-prds/{userId}")
    @Timed
    public ResponseEntity<List<MwPrdDTO>> getBranchProducts(@PathVariable String userId) {
        return new ResponseEntity<List<MwPrdDTO>>(genericService.getBranchPrds(userId), HttpStatus.OK);
    }

    @GetMapping("/print-overdue-loans/{prdSeq}/{asDt}/{branch}/{type}")
    @Timed
    public HttpEntity<byte[]> printOverdueLoans(HttpServletResponse response, @PathVariable long prdSeq, @PathVariable String asDt,
                                                @PathVariable long branch, @PathVariable String type) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getOverdueLoansReport(prdSeq, asDt, user, branch, type);
        return new HttpEntity<byte[]>(bytes, getHeader("Overdue Loans", bytes.length));
    }

    @GetMapping("/print-portfolio/{fromDt}/{toDt}/{branch}/{type}")
    @Timed
    public HttpEntity<byte[]> printPortfolio(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                             @PathVariable long branch, @PathVariable String type) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPortfolioReport(fromDt, toDt, user, branch, type);
        return new HttpEntity<byte[]>(bytes, getHeader("Portfolio", bytes.length));
    }

    @GetMapping("/print-portfolio-new/{fromDt}/{toDt}/{branch}")
    @Timed
    public HttpEntity<byte[]> printPortfolioNew(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt, @PathVariable long branch) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPortfolioMonitoringNewReport(fromDt, toDt, user, branch);
        return new HttpEntity<byte[]>(bytes, getHeader("Portfolio", bytes.length));
    }

    @GetMapping("/print-fund-statment/{fromDt}/{toDt}/{branch}/{userId}")
    @Timed
    public HttpEntity<byte[]> printFundStatment(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                @PathVariable long branch, @PathVariable String userId) throws IOException {
        byte[] bytes = reportsService.getFundStatmentReport(fromDt, toDt, branch, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Fund Statment", bytes.length));
    }

    @GetMapping(value = "/print-posted-report/{date}/{branch}")
    @Timed
    public HttpEntity<byte[]> printPostedReport(@PathVariable String date, @PathVariable long branch) throws IOException {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPostedReport(date, currUser, branch);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Posted.pdf");
        header.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, header);
    }

    @GetMapping("/print-insu-clm-frm/{clntSeq}/{userId}")
    @Timed
    public HttpEntity<byte[]> printInsuClmFrm(HttpServletResponse response, @PathVariable long clntSeq, @PathVariable String userId)
            throws IOException {
        byte[] bytes = reportsService.getInsuClmFrm(clntSeq, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Insurance Claim Form", bytes.length));
    }

    //Added by Areeba
    @GetMapping("/print-insu-clm-frm-dsblty/{clntSeq}/{userId}")
    @Timed
    public HttpEntity<byte[]> printInsuClmFrmForDsblty(HttpServletResponse response, @PathVariable long clntSeq, @PathVariable String userId)
            throws IOException {
        byte[] bytes = reportsService.getInsuClmFrmForDsblty(clntSeq, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Insurance Claim Form", bytes.length));
    }
    //Ended by Areeba

    @PostMapping("/print-account-ledger/{userId}")
    @Timed
    public HttpEntity<byte[]> printAccountLedger(HttpServletResponse response, @PathVariable String userId,
                                                 @RequestBody AccountLedger accountLedger) throws IOException {
        byte[] bytes = reportsService.getAccountLedger(accountLedger, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Account Ledger", bytes.length));
    }

    @GetMapping("/print-kcr-report/{trxSeq}/{userId}/{type}")
    @Timed
    public HttpEntity<byte[]> printKcrReport(HttpServletResponse response, @PathVariable long trxSeq, @PathVariable String userId,
                                             @PathVariable int type) throws IOException {
        byte[] bytes = reportsService.getKcrReport(trxSeq, userId, type);
        if (bytes == null)
            return null;
        return new HttpEntity<byte[]>(bytes, getHeader("Account Ledger", bytes.length));
    }

    @PostMapping("/print-book-details/{userId}")
    @Timed
    public HttpEntity<byte[]> printBookDetails(HttpServletResponse response, @PathVariable String userId,
                                               @RequestBody BookDetailsDTO bookDetailsDTO) throws IOException {
        byte[] bytes = reportsService.getBookDetails(bookDetailsDTO, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Account Ledger", bytes.length));
    }

    @PostMapping("/print-due-recovery/{userId}")
    @Timed
    public HttpEntity<byte[]> printDueRecovery(HttpServletResponse response, @PathVariable String userId,
                                               @RequestBody BookDetailsDTO bookDetailsDTO) throws IOException {
        byte[] bytes = reportsService.getDueRecovery(bookDetailsDTO, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Account Ledger", bytes.length));
    }

    // @GetMapping ( "/print-women-participation/{date}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printWomenParticipation( HttpServletResponse response, @PathVariable String date,
    // @PathVariable long branch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getWomenParticipation( date, branch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Account Ledger", bytes.length ) );
    // }

    @GetMapping("/print-clt-health-beneficiary/{fromDt}/{toDt}/{branch}/{userId}")
    @Timed
    public HttpEntity<byte[]> printClientHealthBeneficiary(HttpServletResponse response, @PathVariable String fromDt,
                                                           @PathVariable String toDt, @PathVariable long branch, @PathVariable String userId) throws IOException {
        byte[] bytes = reportsService.getClientHealthBeneficiaryReport(fromDt, toDt, branch, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Client Health Beneficiary", bytes.length));
    }

    @GetMapping("/print-insurance-claim/{fromDt}/{toDt}/{branch}/{userId}")
    @Timed
    public HttpEntity<byte[]> printInsuranceClaim(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                  @PathVariable long branch, @PathVariable String userId) throws IOException {
        byte[] bytes = reportsService.getInsuranceClaimReport(fromDt, toDt, branch, userId);
        return new HttpEntity<byte[]>(bytes, getHeader("Insurance Claim", bytes.length));
    }

    // @GetMapping ( "/print-par-branch-wise/{fromDt}/{toDt}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printPARBranchWise( HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
    // @PathVariable long branch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getParBranchWiseReport( fromDt, toDt, branch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Client Health Beneficiary", bytes.length ) );
    // }

    // @GetMapping ( "/print-branch-performance-review/{fromDt}/{toDt}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printBranchPerformanceReview( HttpServletResponse response, @PathVariable String fromDt,
    // @PathVariable String toDt, @PathVariable long branch ) {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getBranchPerformanceReport( fromDt, toDt, branch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Client Health Beneficiary", bytes.length ) );
    // }

    private HttpHeaders getHeader(String name, int lenght) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + name + ".pdf");
        header.setContentLength(lenght);
        return header;
    }

    // @GetMapping ( "/print-brnch-turnover-anlysis/{date}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printBranchTurnoverAnalysisAndPlaning( HttpServletResponse response, @PathVariable String date,
    // @PathVariable long branch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getBranchTurnoverAnalysisAndPlaningReport( date, branch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Client Health Beneficiary", bytes.length ) );
    // }
    //
    @GetMapping("/print-five-days-advance-recovery-trends/{fromDt}/{toDt}/{branch}")
    @Timed
    public HttpEntity<byte[]> printFiveDaysAdvanceRecoveryTrends(HttpServletResponse response, @PathVariable String fromDt,
                                                                 @PathVariable String toDt, @PathVariable long branch) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getFiveDaysAdvanceRecoveryTrends(fromDt, toDt, branch, user);
        return new HttpEntity<byte[]>(bytes, getHeader("Client Health Beneficiary", bytes.length));
    }
    //
    // @GetMapping ( "/print-top-sheet/{fromDt}/{toDt}/{branch}/{prd}/{flg}" )
    // @Timed
    // public HttpEntity< byte[] > printTopSheet( HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
    // @PathVariable long branch, @PathVariable long prd, @PathVariable int flg ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getTopSheet( fromDt, toDt, branch, prd, flg, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Client Health Beneficiary", bytes.length ) );
    // }

    // @GetMapping ( "/print-pdc-detail/{fromDt}/{toDt}/{branch}" )
    // @Timed
    // public HttpEntity< byte[] > printPDCDetails( HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
    // @PathVariable long branch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getPDCDetailReport( fromDt, toDt, branch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Client Health Beneficiary", bytes.length ) );
    // }
    //
    // @GetMapping ( "/rate-of-retention/{brnchSeq}" )
    // @Timed
    // public HttpEntity< byte[] > printRateOfRetention( HttpServletResponse response, @PathVariable Long brnchSeq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getRateOfRetentionReport( brnchSeq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Rate Of Retention", bytes.length ) );
    // }
    //
    @GetMapping("/print-projected-clients-loan-completetion/{fromDt}/{toDt}/{branch}")
    @Timed
    public HttpEntity<byte[]> printProjectedClientsLoanCompletetion(HttpServletResponse response, @PathVariable String fromDt,
                                                                    @PathVariable String toDt, @PathVariable long branch) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getProjectedClientLoanCompletion(fromDt, toDt, branch, user);
        return new HttpEntity<byte[]>(bytes, getHeader("Client Health Beneficiary", bytes.length));
    }
    //
    // @GetMapping ( "/print-region-wise-adc/{fromDt}/{toDt}" )
    // @Timed
    // public HttpEntity< byte[] > printRegionWiseAdc( HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt
    // )
    // throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getRegionMonthWiseADCReport( fromDt, toDt, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }

    @GetMapping("/print-anml-insur-claim-form/{anmlRgstrSeq}")
    @Timed
    public HttpEntity<byte[]> printAnmlInsuClmFrm(HttpServletResponse response, @PathVariable long anmlRgstrSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getAnmlInsuClmFrm(anmlRgstrSeq, user);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-pending-client/{prd}/{brnch}/{asDt}/{typ}/{portSeq}")
    @Timed
    public HttpEntity<byte[]> printPendingClient(HttpServletResponse response, @PathVariable long prd, @PathVariable long brnch,
                                                 @PathVariable String asDt, @PathVariable int typ, @PathVariable long portSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPendingClientsReport(typ, prd, brnch, asDt, user, portSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Portfolio Concentration", bytes.length));
    }

    @GetMapping("/print-dues/{fromDt}/{toDt}/{branch}/{type}")
    @Timed
    public HttpEntity<byte[]> printDuesReport(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                              @PathVariable long branch, @PathVariable String type) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getDuesReport(fromDt, toDt, user, branch, type);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-organization-tagging-report/{frmDt}/{toDt}")
    @Timed
    public HttpEntity<byte[]> printOrganizationTaggingReport(HttpServletResponse response, @PathVariable String frmDt,
                                                             @PathVariable String toDt) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getOrganizationTaggingReport(frmDt, toDt, user);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }
    //
    // @GetMapping ( "/print-product-wise-disbursement-report/{frmDt}/{toDt}/{prd}" )
    // @Timed
    // public HttpEntity< byte[] > printProductWiseDisbursementReport( HttpServletResponse response, @PathVariable String frmDt,
    // @PathVariable String toDt, @PathVariable Long prd ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getProductWiseDisbursementReport( frmDt, toDt, prd, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    //
    // }

    private HttpHeaders getHeaderXls(String name, int lenght) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name + ".xlsx");
        header.setContentLength(lenght);
        return header;
    }

    // @GetMapping ( "/print-ogranization-time-report/{frmDt}/{toDt}" )
    // @Timed
    // public HttpEntity< byte[] > printOrganziationTagTimeReport( HttpServletResponse response, @PathVariable String frmDt,
    // @PathVariable String toDt ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getOrganizationTagTimeReport( frmDt, toDt, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-reversal-enteries-report/{frmdt}/{todt}/{brnch}" )
    // @Timed
    // public HttpEntity< byte[] > printReversalEnteriesReport( HttpServletResponse response, @PathVariable String frmdt,
    // @PathVariable String todt, @PathVariable long brnch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getReversalEnteriesReport( frmdt, todt, brnch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-trail-balance-report/{frmdt}/{todt}/{brnch}" )
    // @Timed
    // public HttpEntity< byte[] > printTrailBalancesReport( HttpServletResponse response, @PathVariable String frmdt,
    // @PathVariable String todt, @PathVariable long brnch ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getTrailBalance( frmdt, todt, brnch, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-barch-ranking/{vstseq}" )
    // @Timed
    // public HttpEntity< byte[] > printBrnchRankngReport( HttpServletResponse response, @PathVariable long vstseq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getBrnchRnkngReport( vstseq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-cpc-report/{vstseq}" )
    // @Timed
    // public HttpEntity< byte[] > printCPCReport( HttpServletResponse response, @PathVariable long vstseq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getCpcReport( vstseq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-aicg-report/{trng_seq}" )
    // @Timed
    // public HttpEntity< byte[] > printaicgReport( HttpServletResponse response, @PathVariable long trng_seq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getacigReport( trng_seq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }

    // @GetMapping ( "/print-monthly-ranking-report/{vstseq}" )
    // @Timed
    // public HttpEntity< byte[] > printMonthlyRanking( HttpServletResponse response, @PathVariable long vstseq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getMonthlyRanking( vstseq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-gesa-report/{trng_seq}" )
    // @Timed
    // public HttpEntity< byte[] > printGesaSfe( HttpServletResponse response, @PathVariable long trng_seq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getGesaSFE( trng_seq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "GESA", bytes.length ) );
    // }

    @GetMapping("/print-md-par_report/{toDt}")
    @Timed
    public HttpEntity<byte[]> printMdParReport(HttpServletResponse response, @PathVariable String toDt) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPARMD(toDt, user);
        return new HttpEntity<byte[]>(bytes, getHeader("PAR MD", bytes.length));
    }

    // @GetMapping ( "/print-advance-recovery-report/{frmdt}/{todt}" )
    // @Timed
    // public HttpEntity< byte[] > printAdvanceRecoveryReport( HttpServletResponse response, @PathVariable String frmdt,
    // @PathVariable String todt ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getAdvanceRecoveryReport( frmdt, todt, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-advance-client-report/{frmdt}/{todt}/{rpt_flg}" )
    // @Timed
    // public HttpEntity< byte[] > printAdvanceClientReport( HttpServletResponse response, @PathVariable String frmdt,
    // @PathVariable String todt, @PathVariable long rpt_flg ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getAdvanceClientReport( frmdt, todt, user, rpt_flg );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-advance-maturity-report/{as_dt}/{rpt_flg}" )
    // @Timed
    // public HttpEntity< byte[] > printAdvanceMaturityReport( HttpServletResponse response, @PathVariable String as_dt,
    // @PathVariable long rpt_flg ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getAdvanceMaturityReport( as_dt, user, rpt_flg );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-weekly-target-report/{as_dt}" )
    // @Timed
    // public HttpEntity< byte[] > printAdvanceMaturityReport( HttpServletResponse response, @PathVariable String as_dt ) throws IOException
    // {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getWeeklyTargetReport( as_dt, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }

    // @GetMapping ( "/print-area-disb-report/{as_dt}" )
    // @Timed
    // public HttpEntity< byte[] > printAreaDisbursementReport( HttpServletResponse response, @PathVariable String as_dt ) throws
    // IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getAreaDisbursReport( as_dt, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/leave-and-attendance-monitoring-report/{frmdt}/{todt}/{rpt_flg}" )
    // @Timed
    // public HttpEntity< byte[] > printAttendanceMonitoringReport( HttpServletResponse reponse, @PathVariable String frmdt,
    // @PathVariable String todt, @PathVariable long rpt_flg ) throws IOException {
    // // get Current user
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    //
    // byte[] bytes = reportsService.getLeaveAndAttendanceMonitoringReport( frmdt, todt, user, rpt_flg );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Region Wise ADC", bytes.length ) );
    // }
    //
    // @GetMapping ( "/print-sale-2-pending-report/{fromDt}/{toDt}/{branch}/{userId}" )
    // @Timed
    // public HttpEntity< byte[] > printSale2PendingReport( HttpServletResponse response, @PathVariable String fromDt,
    // @PathVariable String toDt, @PathVariable long branch, @PathVariable String userId ) throws IOException {
    // byte[] bytes = reportsService.getSale2PendingReport( fromDt, toDt, branch, userId );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Sale 2 Pending", bytes.length ) );
    // }

    // @GetMapping ( "/print-agency-info-report/{loanAppSeq}" )
    // @Timed
    // public HttpEntity< byte[] > printAgencyInfo( HttpServletResponse response, @PathVariable long loanAppSeq ) throws IOException {
    // String user = SecurityContextHolder.getContext().getAuthentication().getName();
    // byte[] bytes = reportsService.getAgencyInfo( loanAppSeq, user );
    // return new HttpEntity< byte[] >( bytes, getHeader( "Agency Info", bytes.length ) );
    // }

    @GetMapping("/bm-bdo-recovery/{fromDt}/{toDt}")
    @Timed
    public HttpEntity<byte[]> printBmBdoRecovery(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getBmBdoRecoveryReport(fromDt, toDt, user);
        return new HttpEntity<byte[]>(bytes, getHeader("bm bdo Recovery", bytes.length));

    }

    @GetMapping("/client-loan-maturity/{fromDt}/{toDt}/{branch}/{isXls}")
    @Timed
    public HttpEntity<byte[]> printClientLoanMaturity(HttpServletResponse reponse, @PathVariable String fromDt, @PathVariable String toDt, @PathVariable long branch, @PathVariable Boolean isXls) throws IOException {
        // get Current user
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getClientLoanMaturityReport(user, fromDt, toDt, branch, isXls);
        return new HttpEntity<byte[]>(bytes, getHeader("client-loan-maturity", bytes.length));
    }

    @GetMapping("/mobile-wallet-due/{fromDt}/{toDt}/{branch}/{isXls}")
    @Timed
    public HttpEntity<byte[]> printMobileWalletDue(HttpServletResponse reponse, @PathVariable String fromDt, @PathVariable String toDt, @PathVariable long branch, @PathVariable Boolean isXls) throws IOException {
        // get Current user
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getMobileWalletDueReport(user, fromDt, toDt, branch, isXls);
        return new HttpEntity<byte[]>(bytes, getHeader("Mobile Wallet Due", bytes.length));
    }


    @GetMapping("/mobile-wallet/{fromDt}/{toDt}/{branch}/{isXls}")
    @Timed
    public HttpEntity<byte[]> printMobileWallet(HttpServletResponse reponse, @PathVariable String fromDt, @PathVariable String toDt, @PathVariable long branch, @PathVariable boolean isXls) throws IOException {
        // get Current user
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getMobileWalletReport(user, fromDt, toDt, branch, isXls);
        return new HttpEntity<byte[]>(bytes, getHeader("Mobile Wallet", bytes.length));
    }

    @GetMapping("/print-portfolio-status/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printPortfolioStatus(HttpServletResponse response, @PathVariable String toDt,
                                                   @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPortfolioStatusActiveReport(toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-loan_utlization/{fromDt}/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printLoanUtlization(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                  @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getLoanUtilizationReport(fromDt, toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-female-participation-time/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printFemalearticipationTime(HttpServletResponse response,
                                                          @PathVariable String toDt, @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq,
                                                          @PathVariable long brnchSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getFemaleParticipationReport(toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-turn-around-time/{fromDt}/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printTurnAroundTime(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                  @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getTurnAroundTimeReport(fromDt, toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-portfolio-at-risk/{fromDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printPortfolioAtRisk(HttpServletResponse response, @PathVariable String fromDt, @PathVariable long rpt_flg,
                                                   @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPortfolioAtRiskReport(fromDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-risk-flagging/{toDt}/{fromDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printRiskFlagging(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getRiskFlaggingReport(fromDt, toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-ror/{asDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printROR(HttpServletResponse response, @PathVariable String asDt, @PathVariable long rpt_flg,
                                       @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getRORReport(asDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-portfolio-segmentation/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printPortfolioSegmentation(HttpServletResponse response,
                                                         @PathVariable String toDt, @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq,
                                                         @PathVariable long brnchSeq) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getPortfolioSegReport(toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-consolidated-loan/{fromDt}/{toDt}/{rpt_flg}/{areaSeq}/{regSeq}/{brnchSeq}")
    @Timed
    public HttpEntity<byte[]> printConsolidatedLoan(HttpServletResponse response, @PathVariable String fromDt, @PathVariable String toDt,
                                                    @PathVariable long rpt_flg, @PathVariable long areaSeq, @PathVariable long regSeq, @PathVariable long brnchSeq)
            throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getConsolidatedLoanReport(fromDt, toDt, user, rpt_flg, areaSeq, regSeq, brnchSeq);
        return new HttpEntity<byte[]>(bytes, getHeader("Region Wise ADC", bytes.length));
    }

    @GetMapping("/print-mcb-remittance-disbursement-funds/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printMcbRemmitanceDisbursementFundsReport(HttpServletResponse response, @PathVariable String frmDt,
                                                                        @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getMcbRemmitanceDisbursementFundsReport(frmDt, toDt, user, isxls);
        if (isxls) {
            return new HttpEntity<byte[]>(bytes, getHeaderXls("Disbursement funds", bytes.length));
        }
        return new HttpEntity<byte[]>(bytes, getHeader("Disbursement funds", bytes.length));
    }

    @GetMapping("/print-mcb-remittance-disbursement-loader/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printMcbRemmitanceDisbursementLoaderReport(HttpServletResponse response, @PathVariable String frmDt,
                                                                         @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getMcbRemmitanceDisbursementLoaderReport(frmDt, toDt, user, isxls);
        if (isxls) {
            return new HttpEntity<byte[]>(bytes, getHeaderXls("Disbursement loader", bytes.length));
        }
        return new HttpEntity<byte[]>(bytes, getHeader("Disbursement loader", bytes.length));
    }

    @GetMapping("/print-mcb-remittance-disbursement-letter/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printMcbRemmitanceDisbursementLetterReport(HttpServletResponse response, @PathVariable String frmDt,
                                                                         @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getMcbRemmitanceDisbursementLetterReport(frmDt, toDt, user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("Disbursement Letter", bytes.length));
    }

    @GetMapping("/print-check-disbursement-funds/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printCheckDisbursmentFundsReport(HttpServletResponse response, @PathVariable String frmDt,
                                                               @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getCheckDisbursementFundsReport(frmDt, toDt, user, isxls);
        if (isxls) {
            return new HttpEntity<byte[]>(bytes, getHeaderXls("Disbursement cheque", bytes.length));
        }
        return new HttpEntity<byte[]>(bytes, getHeader("Disbursement cheque", bytes.length));
    }


    @GetMapping("/print-easy-paisa-funds/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printEasyPaisaFundsReport(HttpServletResponse response, @PathVariable String frmDt,
                                                        @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getEasyPaisaFundsReport(frmDt, toDt, user, isxls);
        if (isxls) {
            return new HttpEntity<byte[]>(bytes, getHeaderXls("EasyPaisa funds", bytes.length));
        }
        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa funds", bytes.length));
    }

    @GetMapping("/print-easy-paisa-loader/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printEasyPaisaLoaderReport(HttpServletResponse response, @PathVariable String frmDt,
                                                         @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] bytes = reportsService.getEasyPaisaLoaderReport(frmDt, toDt, user, isxls);
        if (isxls) {
            return new HttpEntity<byte[]>(bytes, getHeaderXls("EasyPaisa loader", bytes.length));
        }
        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa loader", bytes.length));
    }

    @GetMapping("/print-easy-paisa-letter/{frmDt}/{toDt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printEasyPaisaLetterReport(HttpServletResponse response, @PathVariable String frmDt,
                                                         @PathVariable String toDt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getEasyPaisaLetterReport(frmDt, toDt, user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/clnt-info-jazz-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printClientInfoJazzDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getClntInfoJazzDueReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/clnt-upload-jazz-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printClientUpldDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getClientUpldDuesReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/easy-paisa-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printEasyPaisaDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getEasyPaisaDuesReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/ubl-omni-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printUblOmniDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getUblOmniDuesReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/hbl-connect-dues/{todt}/{isxls}")
    @Timed
    public HttpEntity<byte[]> printHblConnectDuesReport(HttpServletResponse response, @PathVariable String todt, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getHblConnectReport(user, todt, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/abl-loan-recovery-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printAblLoanRecoveryDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getAblLoanDuesReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/mcb-collect-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printMcbCollectDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getMcbCollectReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/nadra-dues/{isxls}")
    @Timed
    public HttpEntity<byte[]> printNadraDuesReport(HttpServletResponse response, @PathVariable boolean isxls) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        byte[] bytes = reportsService.getNadraDuesReport(user, isxls);

        return new HttpEntity<byte[]>(bytes, getHeader("EasyPaisa Letter", bytes.length));
    }

    @GetMapping("/print-mfcib/{loanApp}")
    @Timed
    public HttpEntity<byte[]> printMfcib(HttpServletResponse reponse, @PathVariable long loanApp) throws IOException {

        byte[] bytes = null;
        try {
            bytes = reportsService.getMfcib(loanApp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new HttpEntity<byte[]>(bytes, getHeader("Mfcib Report", bytes.length));
    }
}
