package com.idev4.rds.dto;

import java.time.Instant;

public class HealthCardDto {

    public Instant cardExpiryDate;

    public long loanAppSeq;

    // Added by Zohaib Asim - Dated 19-12-2020
    // CR: KSZB Claims
    public String loanAppSts;
    public long prdSeq;

}
