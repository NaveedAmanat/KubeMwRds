package com.idev4.rds.domain;

import com.idev4.rds.ids.LoanApplicationId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_loan_App")
@IdClass(LoanApplicationId.class)
public class LoanApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    @Column(name = "LOAN_ID")
    private String loanId;

    @Column(name = "LOAN_CYCL_NUM")
    private Long loanCyclNum;

    @Column(name = "RQSTD_LOAN_AMT")
    private Long rqstdLoanAmt;

    @Column(name = "APRVD_LOAN_AMT")
    private Long aprvdLoanAmt;

    @Column(name = "CLNT_SEQ")
    private Long clntSeq;

    @Column(name = "PRD_SEQ")
    private Long prdSeq;

    @Column(name = "RCMND_LOAN_AMT")
    private Long rcmndLoanAmt;

    @Column(name = "crtd_by")
    private String crtdBy;

    @Column(name = "crtd_dt")
    private Instant crtdDt;

    @Column(name = "last_upd_by")
    private String lastUpdBy;

    @Column(name = "last_upd_dt")
    private Instant lastUpdDt;

    @Column(name = "del_flg")
    private Boolean delFlg;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "LOAN_APP_STS")
    private Long loanAppSts;

    @Column(name = "CMNT")
    private String cmnt;

    @Column(name = "REJECTION_REASON_CD")
    private String rejectionReasonCd;

    @Column(name = "port_SEQ")
    private Long portSeq;

    @Column(name = "LOAN_APP_ID")
    private String loanAppId;

    @Column(name = "LOAN_APP_STS_DT")
    private Instant loanAppStsDt;

    @Column(name = "prnt_loan_app_seq")
    private Long prntLoanAppSeq;

    @Column(name = "LOAN_APP_PVRTY_SCR")
    private Long pscScore;

    @Column(name = "TBL_SCRN_FLG")
    private Boolean tblScrn;

    @Column(name = "SYNC_FLG")
    private Boolean syncFlg;

    @Column(name = "REL_ADDR_AS_CLNT_FLG")
    private Long relAddrAsClnt_Flg;

    @Column(name = "CO_BWR_ADDR_AS_CLNT_FLG")
    private Long coBwrAddrAsClntFlg;

    @Column(name = "LOAN_UTL_STS_SEQ")
    private Long loanUtlStsSeq;

    @Column(name = "LOAN_UTL_CMNT")
    private String loanUtlCmnt;

    @Column(name = "CRDT_BND")
    private String crdtBnd;

    @Column(name = "APP_VRSN")
    private String AppVrsn;

    @Column(name = "AML_DOC_CHK_FLG")
    private Boolean amlDocChkFlg;

    public Boolean getAmlDocChkFlg() {
        return amlDocChkFlg;
    }

    public void setAmlDocChkFlg(Boolean amlDocChkFlg) {
        this.amlDocChkFlg = amlDocChkFlg;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public LoanApplication loanAppSetSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
        return this;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Long getLoanCyclNum() {
        return loanCyclNum;
    }

    public void setLoanCyclNum(Long loanCyclNum) {
        this.loanCyclNum = loanCyclNum;
    }

    public LoanApplication loanCyclSetNum(Long loanCyclNum) {
        this.loanCyclNum = loanCyclNum;
        return this;
    }

    public Long getRqstdLoanAmt() {
        return rqstdLoanAmt;
    }

    public void setRqstdLoanAmt(Long rqstdLoanAmt) {
        this.rqstdLoanAmt = rqstdLoanAmt;
    }

    public LoanApplication rqstdLoanSetAmt(Long rqstdLoanAmt) {
        this.rqstdLoanAmt = rqstdLoanAmt;
        return this;
    }

    public Long getAprvdLoanAmt() {
        return aprvdLoanAmt;
    }

    public void setAprvdLoanAmt(Long aprvdLoanAmt) {
        this.aprvdLoanAmt = aprvdLoanAmt;
    }

    public LoanApplication aprvdLoanSetAmt(Long aprvdLoanAmt) {
        this.aprvdLoanAmt = aprvdLoanAmt;
        return this;
    }

    public Long getClntSeq() {
        return clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public LoanApplication clntSetSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
        return this;
    }

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public LoanApplication prdSetSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
        return this;
    }

    public Long getRcmndLoanAmt() {
        return rcmndLoanAmt;
    }

    public void setRcmndLoanAmt(Long rcmndLoanAmt) {
        this.rcmndLoanAmt = rcmndLoanAmt;
    }

    public LoanApplication rcmndLoanSetAmt(Long rcmndLoanAmt) {
        this.rcmndLoanAmt = rcmndLoanAmt;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public Boolean getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }

    public Boolean getCrntRecFlg() {
        return crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public Long getLoanAppSts() {
        return loanAppSts;
    }

    public void setLoanAppSts(Long loanAppSts) {
        this.loanAppSts = loanAppSts;
    }

    public LoanApplication loanAppSetSts(Long loanAppSts) {
        this.loanAppSts = loanAppSts;
        return this;
    }

    public String getCmnt() {
        return cmnt;
    }

    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }

    public String getRejectionReasonCd() {
        return rejectionReasonCd;
    }

    public void setRejectionReasonCd(String rejectionReasonCd) {
        this.rejectionReasonCd = rejectionReasonCd;
    }

    public Long getPortSeq() {
        return portSeq;
    }

    public void setPortSeq(Long portSeq) {
        this.portSeq = portSeq;
    }

    public LoanApplication portSetSeq(Long portSeq) {
        this.portSeq = portSeq;
        return this;
    }

    public String getLoanAppId() {
        return loanAppId;
    }

    public void setLoanAppId(String loanAppId) {
        this.loanAppId = loanAppId;
    }

    public Instant getLoanAppStsDt() {
        return loanAppStsDt;
    }

    public void setLoanAppStsDt(Instant loanAppStsDt) {
        this.loanAppStsDt = loanAppStsDt;
    }

    public Long getPrntLoanAppSeq() {
        return prntLoanAppSeq;
    }

    public void setPrntLoanAppSeq(Long prntLoanAppSeq) {
        this.prntLoanAppSeq = prntLoanAppSeq;
    }

    public Long getPscScore() {
        return pscScore;
    }

    public void setPscScore(Long pscScore) {
        this.pscScore = pscScore;
    }

    public Boolean getTblScrn() {
        return tblScrn;
    }

    public void setTblScrn(Boolean tblScrn) {
        this.tblScrn = tblScrn;
    }

    public Boolean getSyncFlg() {
        return syncFlg;
    }

    public void setSyncFlg(Boolean syncFlg) {
        this.syncFlg = syncFlg;
    }

    public Long getRelAddrAsClnt_Flg() {
        return relAddrAsClnt_Flg;
    }

    public void setRelAddrAsClnt_Flg(Long relAddrAsClnt_Flg) {
        this.relAddrAsClnt_Flg = relAddrAsClnt_Flg;
    }

    public Long getCoBwrAddrAsClntFlg() {
        return coBwrAddrAsClntFlg;
    }

    public void setCoBwrAddrAsClntFlg(Long coBwrAddrAsClntFlg) {
        this.coBwrAddrAsClntFlg = coBwrAddrAsClntFlg;
    }

    public Long getLoanUtlStsSeq() {
        return loanUtlStsSeq;
    }

    public void setLoanUtlStsSeq(Long loanUtlStsSeq) {
        this.loanUtlStsSeq = loanUtlStsSeq;
    }

    public String getLoanUtlCmnt() {
        return loanUtlCmnt;
    }

    public void setLoanUtlCmnt(String loanUtlCmnt) {
        this.loanUtlCmnt = loanUtlCmnt;
    }

    public String getCrdtBnd() {
        return crdtBnd;
    }

    public void setCrdtBnd(String crdtBnd) {
        this.crdtBnd = crdtBnd;
    }

    public String getAppVrsn() {
        return AppVrsn;
    }

    public void setAppVrsn(String appVrsn) {
        AppVrsn = appVrsn;
    }

}
