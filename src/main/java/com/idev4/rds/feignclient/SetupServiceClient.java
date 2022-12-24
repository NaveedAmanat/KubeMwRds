package com.idev4.rds.feignclient;

import com.idev4.rds.dto.AmlCheckDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.HttpHeaders;
import java.util.Map;

//test http://localhost:8080
//live https://apps.kashf.org:8443

@FeignClient(name = "setupservice", url = "http://localhost:8080/setupservice")
public interface SetupServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/add-clnt-tag-list/{cnic}/{loanAppSeq}")
    ResponseEntity<Map> addClntTagList(@PathVariable(value = "cnic") Long cnic,
                                       @PathVariable(value = "loanAppSeq") Long loanAppSeq, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(method = RequestMethod.POST, value = "/api/verify-aml")
    ResponseEntity<Map> amlValidation(@RequestBody AmlCheckDto dto, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
