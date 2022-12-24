package com.idev4.rds.dto;

import java.time.Instant;

public class LoanApplicationDTO {

    public Long loanAppSeq;

    public String loanId;

    public Integer loanCyclNum;

    public Double rqstdLoanAmt;

    public Double aprvdLoanAmt;

    public Long clntSeq;

    public String prdSeq;

    /*
     * Added by Naveed - Date - 11-05-2022
     * add Product Group Seq in Response
     * */
    public Long prdGrpSeq;
    // End by Naveed

    public Double rcmndLoanAmt;

    public String crtdBy;

    public String crtdDt;

    public String lastUpdBy;

    public String lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;

    public Instant effStartDt;

    public Instant effEndDt;

    public String loanAppSts;

    public Long cmnt;

    public String rejectionReasonCd;

    public String portSeq;

    public String prdNm;

    public String loanAppId;

    public ClientDto clientDto;

    public MwPrdDTO mwPrdDTO;

    public MwPortDTO mwPortDTO;

    // @Override
    // public int hashCode() {
    // final int prime = 31;
    // int result = 1;
    // result = prime * result + ( int ) ( loanAppSeq ^ ( loanAppSeq >>> 32 ) );
    // return result;
    // }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoanApplicationDTO other = (LoanApplicationDTO) obj;
        if (loanAppSeq != other.loanAppSeq)
            return false;
        return true;
    }

}
