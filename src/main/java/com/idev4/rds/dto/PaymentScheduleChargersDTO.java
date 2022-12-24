package com.idev4.rds.dto;

import java.time.Instant;

public class PaymentScheduleChargersDTO {

    public Long paySchedChrgSeq;

    public Long paySchedDtlSeq;

    public Instant effStartDt;

    public Instant effEndDt;

    public Long amt;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;

    public Long chrgTypsSeq;

    public TypesDTO chargesTypesDTO;

}
