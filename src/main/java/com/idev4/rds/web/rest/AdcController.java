package com.idev4.rds.web.rest;

import com.idev4.rds.dto.AdcRcvryDTO;
import com.idev4.rds.dto.BillInquiry;
import com.idev4.rds.dto.BillPayment;
import com.idev4.rds.service.RecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class AdcController {

    @Autowired
    RecoveryService recoveryService;

    @PostMapping("/bill-payment")
    public ResponseEntity<BillPayment> applyPaymentByThirdParty(@RequestBody AdcRcvryDTO adcRcvryDTO) {
        return new ResponseEntity<>(recoveryService.applyAdcPayment(adcRcvryDTO), HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/bill-inquiry/{id}")
    public ResponseEntity<BillInquiry> getTotalPayAbleByThirdParty(@PathVariable String id) {
        return new ResponseEntity<>(recoveryService.lastDueInst(id), HttpStatus.OK);
    }

}
