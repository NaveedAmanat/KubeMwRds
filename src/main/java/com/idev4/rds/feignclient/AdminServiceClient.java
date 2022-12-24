package com.idev4.rds.feignclient;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.core.HttpHeaders;

//test http://localhost:8080
//live https://apps.kashf.org:8443

@FeignClient(name = "adminservice", url = "http://localhost:8080/adminservice")
public interface AdminServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/mark-dth-rpt-sts-rvrt/{anmlRgstSeq}")
    ResponseEntity revertAnmlSts(@PathVariable(value = "anmlRgstSeq") long anmlRgstSeq,
                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}