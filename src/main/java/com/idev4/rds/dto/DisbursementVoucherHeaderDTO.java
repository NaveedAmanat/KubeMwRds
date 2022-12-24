package com.idev4.rds.dto;

import java.time.Instant;
import java.util.List;

public class DisbursementVoucherHeaderDTO {

    public Long dsbmtHdrSeq;

    public String dsbmtId;

    public Long loanAppSeq;

    public Instant dsbmtDt;

    public Long dsbmtStsKey;

//    public Instant effStartDt;
//
//    public Instant effEndDt;
//
//    public String crtdBy;
//
//    public Instant crtdDt;
//
//    public String lastUpdBy;
//
//    public Instant lastUpdDt;
//
//    public Boolean delFlg;
//
//    public Boolean crntRecFlg;

    public List<DisbursementVoucherDetailDTO> disbursementVoucherDetailDTOs;
}
