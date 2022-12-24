package com.idev4.rds.feignclient;

import com.idev4.rds.dto.HealthCardDto;
import com.idev4.rds.dto.ValidationDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.HttpHeaders;
import java.util.Map;

//test http://localhost:8080
//live https://apps.kashf.org:8443

@FeignClient(name = "loanservice", url = "http://localhost:8080/loanservice")
public interface LoanServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/mw-tags-validation-for-clnt-nom-cob")
    ResponseEntity<Map> tagsValidation(@RequestBody ValidationDto dto, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(method = RequestMethod.POST, value = "/api/add-health-card")
    ResponseEntity<Map> addHealthCard(@RequestBody HealthCardDto dto, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(method = RequestMethod.GET, value = "/api/deffer-application/{loanAppSeq}")
    ResponseEntity defferApplication(@PathVariable(value = "loanAppSeq") long loanAppSeq,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(method = RequestMethod.GET, value = "/api/delete-application/{loanAppSeq}/{sts}/{reason}")
    ResponseEntity deleteApplication(@PathVariable(value = "loanAppSeq") long loanAppSeq,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable(value = "sts") long sts,
                                     @PathVariable(value = "reason") String reason);

    @RequestMapping(method = RequestMethod.GET, value = "/api/adv-rul-check/{loanAppSeq}")
    ResponseEntity<Map> advRulChk(@PathVariable(value = "loanAppSeq") long loanAppSeq,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(method = RequestMethod.GET, value = "/api/nacta-verification/{loanAppSeq}")
    ResponseEntity<Map> nactaVerification(@PathVariable(value = "loanAppSeq") long loanAppSeq,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}