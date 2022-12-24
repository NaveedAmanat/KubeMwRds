package com.idev4.rds.dto;

import java.time.Instant;

public class MwRcvryDTO {

    public Long rcvrySeq;

    public Long paySchedDtlSeq;

    public Long rcvryTypsSeq;

    public String instr;

    public Instant pymtDt;

    public Long pymtAmt;

    public Long pymtStsKey;

    public Instant effStartDt;

    public Instant effEndDt;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;
}
