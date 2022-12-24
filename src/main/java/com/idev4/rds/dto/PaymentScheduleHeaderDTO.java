package com.idev4.rds.dto;

import java.time.Instant;

public class PaymentScheduleHeaderDTO {

    public Long paySchedHdrSeq;

    public String paySchedId;

    public Instant frstInstDt;

    public Instant effStartDt;

    public Instant effEndDt;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;

    public Long loanAppSeq;

    public Long schedStsKey;
}
