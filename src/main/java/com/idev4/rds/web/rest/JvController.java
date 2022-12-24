package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.dto.RecoveryJvHostory;
import com.idev4.rds.service.VchrPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JvController {

    @Autowired
    VchrPostingService vchrPostingService;

//    @GetMapping ( "/all-jv-vouchers/{page}/{size}" )
//    public ResponseEntity< ? > getLoanApplicationsByApplicationId( @PathVariable Integer page, @PathVariable Integer size ) {
//        return new ResponseEntity<>( vchrPostingService.getAllJvHdrs( page, size ), HttpStatus.OK );
//    }

    @GetMapping("/all-jv-vouchers")
    public ResponseEntity<Map<String, Object>> getLoanApplicationsByApplicationId(
            @RequestParam Long brnchSeq,
            @RequestParam Integer pageIndex,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String filter,
            @RequestParam Boolean isCount) {
        return new ResponseEntity<Map<String, Object>>(vchrPostingService.getAllJvHdrs(brnchSeq, pageIndex, pageSize, filter, isCount), HttpStatus.OK);
    }

    @GetMapping("/all-jv-vouchers/{page}/{size}/{filter}")
    public ResponseEntity<?> getLoanApplicationsByApplicationId(@PathVariable Integer page, @PathVariable Integer size,
                                                                @PathVariable String filter) {
        return new ResponseEntity<>(vchrPostingService.getAllJvHdrs(page, size, filter), HttpStatus.OK);
    }

    @GetMapping("/jv-vouchers-details/{id}")
    public ResponseEntity<?> getLoanApplicationsByApplicationId(@PathVariable Long id) {
        return new ResponseEntity<>(vchrPostingService.getJVDTLbyHdrSeq(id), HttpStatus.OK);
    }

    @GetMapping("/recovery-vouchers-history/{clntSeq}")
    @Timed
    public ResponseEntity<List<RecoveryJvHostory>> recoveryVouchersHistory(@PathVariable Long clntSeq) {
        return new ResponseEntity<>(vchrPostingService.getRecoveryJvHistory(clntSeq), HttpStatus.OK);
    }

}
