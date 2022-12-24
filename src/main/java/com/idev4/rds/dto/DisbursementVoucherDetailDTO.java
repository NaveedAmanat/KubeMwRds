package com.idev4.rds.dto;

public class DisbursementVoucherDetailDTO {

    public Long dsbmtDtlKey;

    public String instrNum;

    public Long amt;

    public Long dsbmtHdrSeq;

    public Long pymtTypSeq;

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

    public TypesDTO paymtTypesDTO;

    public long loanAppSeq;

    // Modified By Naveed - Date - 23-01-2022
    // SCR - Mobile Wallet Control
    public String mobWalNum;

    public String mobWalChnl;
    // Ended By Naveed - Date - 23-01-2022
}
