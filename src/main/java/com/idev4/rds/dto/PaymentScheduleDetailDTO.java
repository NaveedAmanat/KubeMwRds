package com.idev4.rds.dto;

import java.time.Instant;
import java.util.Date;

public class PaymentScheduleDetailDTO {

    public Long paySchedDtlSeq;

    public Long paySchedHdrSeq;

    public Long instNum;

    public Date dueDt;

    public Long ppalAmtDue;

    public Long totChrgDue;

    public Instant effStartDt;

    public Instant effEndDt;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;
}
