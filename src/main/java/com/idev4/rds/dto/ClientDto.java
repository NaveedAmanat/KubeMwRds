package com.idev4.rds.dto;

import java.time.Instant;
import java.util.Date;

public class ClientDto {

    public Long clntSeq;

    public Instant effStartDt;

    public Instant effEndDt;

    public String clntId;

    public Long cnicNum;

    public Date cnicExpryDt;

    public String frstNm;

    public String lastNm;

    public String nickNm;

    public String mthMadnNm;

    public String FathrFrstNm;

    public String fathrLastNm;

    public String spzFrstNm;

    public String spzLastNm;

    public Date dob;

    public Integer numOfDpnd;

    public Integer erngMemb;

    public Integer hseHldMemb;

    public Integer numOfChldrn;

    public Integer numOfErngMemb;

    public Integer yrsRes;

    public Integer mnthsRes;

    public Integer portKey;

    public Integer gndrKey;

    public Integer mrtlStsKey;

    public Integer eduLvlKey;

    public Integer occKey;

    public Integer natrOfDisKey;

    public Integer clntStsKey;

    public Integer resTypKey;

    public Integer nomDtlAvailableFlg;

    public Integer slfPdcFlg;

    public Integer crntAddrPermFlg;

    public Integer coBwrSanFlg;

    public String phNum;

    public Integer totIncomOfEngMem;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (clntSeq ^ (clntSeq >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientDto other = (ClientDto) obj;
        if (clntSeq != other.clntSeq)
            return false;
        return true;
    }

}
