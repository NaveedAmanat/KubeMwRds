package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.domain.DisbursementVoucherHeader;
import com.idev4.rds.domain.MwDsbltyRpt;
import com.idev4.rds.domain.MwDthRpt;
import com.idev4.rds.domain.MwRcvryTrx;
import com.idev4.rds.dto.AjRcvryDTO;
import com.idev4.rds.dto.AppRcvryDTO;
import com.idev4.rds.dto.SubRecoveryListingDTO;
import com.idev4.rds.service.GenericService;
import com.idev4.rds.service.RecoveryService;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecoveryController {

    private static String RECOVERY_FILE_PATH = File.separator + "opt" + File.separator + "RecoveryFile" + File.separator + "ADC_FILE.csv";

    private final Logger log = LoggerFactory.getLogger(RecoveryController.class);

    private final RecoveryService recoveryService;

    @Autowired
    GenericService genericService;

    @Autowired
    public RecoveryController(RecoveryService recoveryService) {
        super();
        this.recoveryService = recoveryService;
    }

    @CrossOrigin
    @GetMapping("/recovery")
    public ResponseEntity<Map> getAllRecoverys(@RequestParam String user, @RequestParam(required = false) String filter,
                                               @RequestParam(required = false) String sort, @PathVariable(required = false) String direction,
                                               @RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam(required = false) Long brnchSeq,
                                               @RequestParam Boolean isCount) {
        return new ResponseEntity<>(
                recoveryService.getAllRecovery(user, filter, sort, direction, pageNumber, pageSize, brnchSeq, isCount), HttpStatus.OK);

    }

    @GetMapping("/recovery/{clntSeq}")
    public ResponseEntity<List<SubRecoveryListingDTO>> getAllRecoverys(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(recoveryService.getSingleLoanRecovery(clntSeq), HttpStatus.OK);
    }

    @GetMapping("/recovery/{clntSeq}/{prntLoanAppSeq}/{prd}")
    public ResponseEntity<List<SubRecoveryListingDTO>> getAllRecoveryForPrntLoanApp(@PathVariable Long clntSeq, @PathVariable Long prntLoanAppSeq, @PathVariable String prd) {
        return new ResponseEntity<>(recoveryService.getSingleLoanRecoveryForPrntLoanApp(clntSeq, prntLoanAppSeq, prd), HttpStatus.OK);
    }

    //Added by Areeba
    @GetMapping("/dsblty-recovery/{clntSeq}")
    public ResponseEntity<MwRcvryTrx> getOneRecoveryForDisability(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(recoveryService.getOneRecoveryForDisability(clntSeq), HttpStatus.OK);
    }
    //Ended by Areeba

    //Added by Areeba
    @GetMapping("/reversed-dsblty-recovery/{clntSeq}")
    public ResponseEntity<MwRcvryTrx> getOneReversedRecoveryForDisability(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(recoveryService.getOneReversedRecoveryForDisability(clntSeq), HttpStatus.OK);
    }
    //Ended by Areeba

    @CrossOrigin
    @PostMapping("/apply-payment")
    @Timed
    @Transactional
    public ResponseEntity<Map> applyRecoveryPayment(@RequestBody AppRcvryDTO appRcvryDTO) throws URISyntaxException {
        appRcvryDTO.user = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> resp = new HashMap<String, String>();
        if (appRcvryDTO.clientId == null || appRcvryDTO.clientId.isEmpty()) {
            resp.put("error", "Seems Incorrect Clinent Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (appRcvryDTO.pymtAmt == null || appRcvryDTO.pymtAmt <= 0) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        appRcvryDTO.pymtDt = appRcvryDTO.pymtDt == null ? Instant.now().toString() : appRcvryDTO.pymtDt;
        MwDthRpt dth = genericService.getClntDth(appRcvryDTO.clientId);
        //Added by Areeba
        MwDsbltyRpt dsb = genericService.getClntDsblty(appRcvryDTO.clientId);
        //Ended by Areeba

        DisbursementVoucherHeader hdr = genericService.getDisbursementVoucherHeaderForClientsActiveLoan(appRcvryDTO.clientId);

        if (dth != null && (dth.getDtOfDth().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS)
                .compareTo(hdr.getDsbmtDt().truncatedTo(ChronoUnit.DAYS)) >= 0)) {
            resp.put("error", "This Client is Reported as Dead !!");
            return ResponseEntity.badRequest().body(resp);
        }
        //Added by Areeba
        else if (dsb != null && (dsb.getDtOfDsblty().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS)
                .compareTo(hdr.getDsbmtDt().truncatedTo(ChronoUnit.DAYS)) >= 0)) {
            resp.put("error", "This Client is Reported as Disabled !!");
            return ResponseEntity.badRequest().body(resp);
        }
        //Ended by Areeba

        Map recovery = recoveryService.applyRecoveryPayment(appRcvryDTO);
        if (recovery != null) {
            return new ResponseEntity<>(recovery, HttpStatus.OK);
        }
        resp.put("error", "Something went wrong ");
        return ResponseEntity.badRequest().body(resp);
    }

    /*   @CrossOrigin
    @GetMapping ( "/app-payment" )
    @Timed
    public ResponseEntity< Map > applyPaymentByThirdParty( @RequestBody AppRcvryDTO appRcvryDTO ) {
        Map< String, String > resp = new HashMap< String, String >();
        if ( appRcvryDTO.clientId == null || appRcvryDTO.clientId.isEmpty() ) {
            resp.put( "error", "Seems Incorrect Clinent Id !!" );
            return ResponseEntity.badRequest().body( resp );
        }

        if ( appRcvryDTO.pymtAmt == null || appRcvryDTO.pymtAmt <= 0 ) {
            resp.put( "error", "Seems Incorrect Amount !!" );
            return ResponseEntity.badRequest().body( resp );
        }
        if ( appRcvryDTO.instr == null || !appRcvryDTO.instr.isEmpty() ) {
            resp.put( "error", "Receipt No. is Missing !!" );
            return ResponseEntity.badRequest().body( resp );
        }
        if ( appRcvryDTO.user == null || !appRcvryDTO.user.isEmpty() ) {
            resp.put( "error", "User is Missing !!" );
            return ResponseEntity.badRequest().body( resp );
        }
        Boolean commSeq = recoveryService.applyRecoveryPayment( appRcvryDTO );
        resp.put( "commSeq", String.valueOf( commSeq ) );
        return ResponseEntity.ok().body( resp );
    }*/

    @PostMapping("/adjust-payment")
    @Timed
    public ResponseEntity<?> adjustRecoveryPayment(@RequestBody AjRcvryDTO ajRcvryDTO,
                                                   @RequestHeader(value = "Authorization") String token) {
        Map<String, String> resp = new HashMap<String, String>();
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if (ajRcvryDTO.trxId == null || ajRcvryDTO.trxId <= 0) {
            resp.put("error", "Seems Incorrect Transection Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (ajRcvryDTO.pymtAmt == null || ajRcvryDTO.pymtAmt < 0) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        boolean isExcessPaid = recoveryService.reversePaymentCheckExcessPaid(ajRcvryDTO);
        if (isExcessPaid) {
            resp.put("error", "Excess Paid for this transaction.");
            return ResponseEntity.badRequest().body(resp);
        }
        Long clntSeq = recoveryService.reversePayment(ajRcvryDTO, user, token);
        if (clntSeq != null) {
            return new ResponseEntity<>(recoveryService.getSingleLoanRecovery(clntSeq), HttpStatus.OK);
        }
        resp.put("error", "Something went wrong ");
        return ResponseEntity.badRequest().body(resp);

    }

    @GetMapping("/adjust-loan/{id}")
    @Timed
    public ResponseEntity<?> adjustLoan(@PathVariable String id, @RequestHeader(value = "Authorization") String token) {
        Map<String, String> resp = new HashMap<String, String>();
        if (id == null || id.isEmpty()) {
            resp.put("error", "Seems Incorrect Client Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(recoveryService.recoveryAdjustment(id, crrunUser, token), HttpStatus.OK);

    }

    /*@GetMapping ( "/adc-payment-file" )
    public ResponseEntity< String > getAdcRecoveryFile() {
        String path = File.separator + "opt" + File.separator + "RecoveryFile" + File.separator + "ADC_FILE.csv";
        if ( !Files.exists( Paths.get( path ) ) ) {
            return new ResponseEntity<>( "File does not exist !!", HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity<>( path, HttpStatus.OK );

    }

    @GetMapping ( "/upload-adc-payment" )
    public ResponseEntity< String > uploadAdcRecoveryFile() {

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if ( !Files.exists( Paths.get( RECOVERY_FILE_PATH ) ) ) {
            return new ResponseEntity<>( "File does not exist !!", HttpStatus.BAD_REQUEST );
        }

        recoveryService.uploadAdcRecoveryFile( RECOVERY_FILE_PATH, user );
        return new ResponseEntity<>( "Successfully Satrted Uploading", HttpStatus.OK );

    }*/

    @GetMapping("/post-adc-payment")
    public ResponseEntity<?> postAdcRecoveries() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.postAdcs(user);
        Map<String, String> resp = new HashMap<String, String>();
        resp.put("sucess", "Successfully Satarted Posting !!");
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/bulk-posting/{user}")
    @Timed
    public ResponseEntity<?> bulkPosting(@PathVariable String user) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(recoveryService.bulkPosting(user, crrunUser), HttpStatus.OK);
    }

    @GetMapping("/recovery-posting/{id}")
    @Timed
    public ResponseEntity<?> recoveryPosting(@PathVariable Long id) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> resp = new HashMap<String, String>();
        boolean posted = recoveryService.isRecoveryPosted(id);
        if (posted) {
            resp.put("error", "Recovery Already Posted");
            return ResponseEntity.badRequest().body(resp);
        }
        return new ResponseEntity<>(recoveryService.recoveryPosting(id, crrunUser), HttpStatus.OK);
    }

    @GetMapping("/recovery-multiple-posting/{ids}")
    @Timed
    public ResponseEntity<?> recoveryMultiplePosting(@PathVariable String ids) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.recoveryMultiplePosting(ids, crrunUser);
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @GetMapping("/death-adjustment-advance/{id}/{prntLoanAppSeq}/{dthDate}")
    @Timed
    public ResponseEntity<?> deathAdjustmentAdvance(@PathVariable Long id, @PathVariable Long prntLoanAppSeq, @PathVariable String dthDate) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.reverseAdvancePaymentsToExcess(id, prntLoanAppSeq, dthDate, crrunUser, 0);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/add-excess-manuly")
    @Timed
    public ResponseEntity<?> addExcessManuly(@RequestBody AppRcvryDTO appRcvryDTO) {
        appRcvryDTO.user = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> resp = new HashMap<String, String>();
        if (appRcvryDTO.clientId == null || appRcvryDTO.clientId.isEmpty()) {
            resp.put("error", "Seems Incorrect Clinent Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (appRcvryDTO.pymtAmt == null || appRcvryDTO.pymtAmt <= 0) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        appRcvryDTO.pymtDt = appRcvryDTO.pymtDt == null ? Instant.now().toString() : appRcvryDTO.pymtDt;

        long recovery = recoveryService.addExcessRecovery(appRcvryDTO);
        if (recovery != 0) {
            return new ResponseEntity<>(recovery, HttpStatus.OK);
        }
        resp.put("error", "Something went wrong ");
        return ResponseEntity.badRequest().body(resp);

    }

    // to reverse the access recoveries genrated in case of death
    @GetMapping("/death-adjustment-reverse/{clntSeq}")
    @Timed
    @Transactional
    public ResponseEntity<?> deathAdjustmentReverse(@PathVariable Long clntSeq) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        //Modified by Areeba
        recoveryService.reverseExcessToRecovery(clntSeq, crrunUser, 0);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/pay-off-loan/{appSeq}")
    @Timed
    public ResponseEntity<?> payOffLoan(@PathVariable Long appSeq) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        // recoveryService.reverseExcessToRecovery( clntSeq, crrunUser );
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/anml-loan-adjust/{loanAppSeq}/{amt}/{anmlRgstrSeq}")
    @Timed
    public ResponseEntity<?> anmlLaonAdjust(@PathVariable Long loanAppSeq, @PathVariable Long amt,
                                            @PathVariable Long anmlRgstrSeq) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.anmlLoanAdjust(loanAppSeq, amt, crrunUser, anmlRgstrSeq);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    // @GetMapping ( "/write-off-loans/{brnchSeq}" )
    // @Timed
    // public ResponseEntity< ? > writeoffLoans( @PathVariable Long brnchSeq ) {
    // Map< String, String > resp = new HashMap< String, String >();
    // if ( brnchSeq == null || brnchSeq <= 0 ) {
    // resp.put( "error", "Seems Incorrect Branch Seq !!" );
    // return ResponseEntity.badRequest().body( resp );
    // }
    // return new ResponseEntity<>( recoveryService.getWriteOffClntsForBrnch( brnchSeq ), HttpStatus.OK );
    // }

    @GetMapping("/write-off-loans")
    @Timed
    public ResponseEntity<?> writeoffLoans(@RequestParam Long brnchSeq, @RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                                           @RequestParam(required = false) String filter, @RequestParam Boolean isCount) {
        Map<String, String> resp = new HashMap<String, String>();
        if (brnchSeq == null || brnchSeq <= 0) {
            resp.put("error", "Seems Incorrect Branch Seq !!");
            return ResponseEntity.badRequest().body(resp);
        }
        return new ResponseEntity<>(recoveryService.getWriteOffClntsForBrnch(brnchSeq, pageIndex, pageSize, filter, isCount),
                HttpStatus.OK);
    }

    @GetMapping("/get-wrt-trx-for-clnt/{clntSeq}")
    @Timed
    public ResponseEntity<?> adjustLoan(@PathVariable Long clntSeq) {
        Map<String, String> resp = new HashMap<String, String>();
        if (clntSeq == null || clntSeq <= 0) {
            resp.put("error", "Seems Incorrect Client Seq !!");
            return ResponseEntity.badRequest().body(resp);
        }
        return new ResponseEntity<>(recoveryService.getWriteOffRcvryTrxForClnt(clntSeq), HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/apply-wrt-off-payment")
    @Timed
    @Transactional
    public ResponseEntity<Map> applyWrtOffRecoveryPayment(@RequestBody AppRcvryDTO appRcvryDTO) throws URISyntaxException {
        appRcvryDTO.user = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> resp = new HashMap<String, String>();
        if (appRcvryDTO.clientId == null || appRcvryDTO.clientId.isEmpty()) {
            resp.put("error", "Seems Incorrect Clinent Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (appRcvryDTO.pymtAmt == null || appRcvryDTO.pymtAmt <= 0) {
            resp.put("error", "Amount is Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }
        appRcvryDTO.pymtDt = appRcvryDTO.pymtDt == null ? Instant.now().toString() : appRcvryDTO.pymtDt;

        Map recovery = recoveryService.applyWrtOffRecoveryPayment(appRcvryDTO);
        return new ResponseEntity<>(recovery, HttpStatus.OK);
    }

    @GetMapping("/wrt-of-trx-reversal/{trxSeq}/{loanAppSeq}")
    @Timed
    @Transactional
    public ResponseEntity<?> wrtOffRcvryRvrsl(@PathVariable Long trxSeq, @PathVariable Long loanAppSeq) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean flg = recoveryService.wrtOffRcvryReversal(trxSeq, loanAppSeq, user);
        Map<String, String> resp = new HashMap<String, String>();
        if (flg) {
            resp.put("success", "Recovery Reversed");
            return ResponseEntity.ok().body(resp);
        } else {
            resp.put("error", "Something went Wrong");
            return ResponseEntity.badRequest().body(resp);
        }
        // return new ResponseEntity<>( resp, HttpStatus.OK );
    }

    // to reverse the access recoveries genrated in case of disability
    // Added by Areeba
    @GetMapping("/dsblty-adjustment-reverse/{clntSeq}")
    @Timed
    @Transactional
    public ResponseEntity<?> disabilityAdjustmentReverse(@PathVariable Long clntSeq) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.reverseExcessToRecovery(clntSeq, crrunUser, 1);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/dsblty-adjustment-advance/{id}/{prntLoanAppSeq}/{dtOfDsblty}")
    @Timed
    public ResponseEntity<?> dsbltyAdjustmentAdvance(@PathVariable Long id, @PathVariable Long prntLoanAppSeq, @PathVariable String dtOfDsblty) {
        String crrunUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recoveryService.reverseAdvancePaymentsToExcess(id, prntLoanAppSeq, dtOfDsblty, crrunUser, 1);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    // Ended by Areeba

}

