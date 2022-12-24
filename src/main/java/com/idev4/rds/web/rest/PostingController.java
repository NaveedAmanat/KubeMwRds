package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.service.PostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostingController {

    @Autowired
    PostingService postingService;

    /* @GetMapping ( "/post-access-recovery/{id}" )
    @Timed
    public ResponseEntity< Map > postAccessRecovery( @PathVariable long id ) {
        Map< String, String > resp = new HashMap< String, String >();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if ( id <= 0 ) {
            resp.put( "error", "Seems Incorrect Id !!" );
            return ResponseEntity.badRequest().body( resp );
        }

        long expSeq = postingService.postAccessRecovry( id, currUser );
        resp.put( "expSeq", String.valueOf( expSeq ) );
        return ResponseEntity.ok().body( resp );
    }

    @GetMapping ( "/post-health-insurance/{id}" )
    @Timed
    public ResponseEntity< Map > postHealthInsurance( @PathVariable long id ) {
        Map< String, String > resp = new HashMap< String, String >();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if ( id <= 0 ) {
            resp.put( "error", "Seems Incorrect Id !!" );
            return ResponseEntity.badRequest().body( resp );
        }

        long expSeq = postingService.postHealthInsurance( id, currUser );
        resp.put( "expSeq", String.valueOf( expSeq ) );
        return ResponseEntity.ok().body( resp );
    }*/

    @GetMapping("/post-expense/{id}")
    @Timed
    public ResponseEntity<Map> postExpense(@PathVariable long id) throws Exception {
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id <= 0) {
            resp.put("error", "Seems Incorrect Id !!");
            return ResponseEntity.badRequest().body(resp);
        }
        boolean posted = postingService.isExpensePosted(id);
        if (posted) {
            resp.put("error", "Expense Already Posted");
            return ResponseEntity.badRequest().body(resp);
        }
        long expSeq = postingService.postExpense(id, currUser);
        resp.put("expSeq", String.valueOf(expSeq));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/post-multiple-expense/{ids}")
    @Timed
    public ResponseEntity<Map> postMultiExpense(@PathVariable String ids) {
        Map<String, String> resp = new HashMap<String, String>();

        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (ids == null && ids.isEmpty()) {
            resp.put("error", "Seems Incorrect Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        postingService.postMultiExpense(ids, currUser);
        resp.put("expSeq", String.valueOf(ids));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/reverse-expense/{id}/{reason}")
    @Timed
    public ResponseEntity<Map> reverseExpense(@PathVariable long id, @PathVariable String reason) {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> resp = new HashMap<String, String>();
        if (id <= 0) {
            resp.put("error", "Seems Incorrect Id !!");
            return ResponseEntity.badRequest().body(resp);
        }
        postingService.reverseExpense(id, currUser, reason);
        resp.put("expSeq", String.valueOf(id));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/reverse-excess-recovery/{trxId}/{reason}")
    @Timed
    public ResponseEntity<Map> reverseExcessRecovery(@PathVariable long trxId, @PathVariable String reason,
                                                     @RequestHeader(value = "Authorization") String token) {
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> resp = new HashMap<String, String>();
        if (trxId <= 0) {
            resp.put("error", "Seems Incorrect Id !!");
            return ResponseEntity.badRequest().body(resp);
        }

        postingService.reverseExcessRecovery(trxId, currUser, reason, token);
        resp.put("trxId", String.valueOf(trxId));
        return ResponseEntity.ok().body(resp);
    }
}
