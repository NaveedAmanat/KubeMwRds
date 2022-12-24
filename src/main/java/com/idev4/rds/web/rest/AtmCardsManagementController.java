package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.dto.AtmCardsDTO;
import com.idev4.rds.service.AtmCardsManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;

/*
 *  Added By Naveed - Date - 24-02-2022
 *  Upaisa and HBL Konnect Mobile wallet Payment Mode
 * */
@RestController
@RequestMapping("/api")
public class AtmCardsManagementController {

    private final Logger log = LoggerFactory.getLogger(AtmCardsManagementController.class);
    @Autowired
    private AtmCardsManagementService atmCardsManagementService;

    @GetMapping("/atm-cards-listing")
    public ResponseEntity<Map<String, Object>> getLoanApplicationsByApplicationId(
            @RequestParam Long brnchSeq,
            @RequestParam Integer pageIndex,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String filter,
            @RequestParam Boolean isCount) {
        return new ResponseEntity<Map<String, Object>>(atmCardsManagementService.getAtmCardsListing(brnchSeq, pageIndex, pageSize, filter, isCount), HttpStatus.OK);
    }

    @PutMapping("/update-atm")
    @Timed
    public ResponseEntity<Map> updateMwCity(@RequestBody AtmCardsDTO atmCardsDTO) throws URISyntaxException, ParseException {
        log.debug("REST request to update ATM Card : {}", atmCardsDTO);
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return atmCardsManagementService.Update(atmCardsDTO, currUser);
    }
}
