package com.idev4.rds.domain;

import com.idev4.rds.ids.MwMfcibOthOutsdLoanId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwMfcibOthOutsdLoan.
 */
@Entity
@Table(name = "mw_mfcib_oth_outsd_loan")
@IdClass(MwMfcibOthOutsdLoanId.class)
public class MwMfcibOthOutsdLoan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "oth_outsd_loan_seq")
    private Long othOutsdLoanSeq;

    @Column(name = "instn_nm")
    private String instnNm;

    @Column(name = "tot_loan_amt")
    private Long totLoanAmt;

    @Column(name = "loan_prps")
    private String loanPrps;

    @Column(name = "crnt_outsd_amt")
    private Long crntOutsdAmt;

    @Column(name = "cmpl_dt")
    private Instant cmplDt;

    @Column(name = "mnth_exp_flg")
    private Boolean mnthExpFlg;

    @Column(name = "mfcib_flg")
    private Boolean mfcibFlg;

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

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    public Long getOthOutsdLoanSeq() {
        return othOutsdLoanSeq;
    }

    public void setOthOutsdLoanSeq(Long othOutsdLoanSeq) {
        this.othOutsdLoanSeq = othOutsdLoanSeq;
    }

    public MwMfcibOthOutsdLoan othOutsdLoanSeq(Long othOutsdLoanSeq) {
        this.othOutsdLoanSeq = othOutsdLoanSeq;
        return this;
    }

    public String getInstnNm() {
        return instnNm;
    }

    public void setInstnNm(String instnNm) {
        this.instnNm = instnNm;
    }

    public MwMfcibOthOutsdLoan instnNm(String instnNm) {
        this.instnNm = instnNm;
        return this;
    }

    public Long getTotLoanAmt() {
        return totLoanAmt;
    }

    public void setTotLoanAmt(Long totLoanAmt) {
        this.totLoanAmt = totLoanAmt;
    }

    public MwMfcibOthOutsdLoan totLoanAmt(Long totLoanAmt) {
        this.totLoanAmt = totLoanAmt;
        return this;
    }

    public String getLoanPrps() {
        return loanPrps;
    }

    public void setLoanPrps(String loanPrps) {
        this.loanPrps = loanPrps;
    }

    public MwMfcibOthOutsdLoan loanPrps(String loanPrps) {
        this.loanPrps = loanPrps;
        return this;
    }

    public Long getCrntOutsdAmt() {
        return crntOutsdAmt;
    }

    public void setCrntOutsdAmt(Long crntOutsdAmt) {
        this.crntOutsdAmt = crntOutsdAmt;
    }

    public MwMfcibOthOutsdLoan crntOutsdAmt(Long crntOutsdAmt) {
        this.crntOutsdAmt = crntOutsdAmt;
        return this;
    }

    public Instant getCmplDt() {
        return cmplDt;
    }

    public void setCmplDt(Instant cmplDt) {
        this.cmplDt = cmplDt;
    }

    public MwMfcibOthOutsdLoan cmplDt(Instant cmplDt) {
        this.cmplDt = cmplDt;
        return this;
    }

    public Boolean isMnthExpFlg() {
        return mnthExpFlg;
    }

    public MwMfcibOthOutsdLoan mnthExpFlg(Boolean mnthExpFlg) {
        this.mnthExpFlg = mnthExpFlg;
        return this;
    }

    public void setMnthExpFlg(Boolean mnthExpFlg) {
        this.mnthExpFlg = mnthExpFlg;
    }

    public Boolean isMfcibFlg() {
        return mfcibFlg;
    }

    public MwMfcibOthOutsdLoan mfcibFlg(Boolean mfcibFlg) {
        this.mfcibFlg = mfcibFlg;
        return this;
    }

    public void setMfcibFlg(Boolean mfcibFlg) {
        this.mfcibFlg = mfcibFlg;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwMfcibOthOutsdLoan crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwMfcibOthOutsdLoan crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwMfcibOthOutsdLoan lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwMfcibOthOutsdLoan lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwMfcibOthOutsdLoan delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
    }

    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public MwMfcibOthOutsdLoan effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public MwMfcibOthOutsdLoan effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwMfcibOthOutsdLoan crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MwMfcibOthOutsdLoan mwMfcibOthOutsdLoan = (MwMfcibOthOutsdLoan) o;
        if (mwMfcibOthOutsdLoan.getOthOutsdLoanSeq() == null || getOthOutsdLoanSeq() == null) {
            return false;
        }
        return Objects.equals(getOthOutsdLoanSeq(), mwMfcibOthOutsdLoan.getOthOutsdLoanSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOthOutsdLoanSeq());
    }

    @Override
    public String toString() {
        return "MwMfcibOthOutsdLoan{" + "id=" + getOthOutsdLoanSeq() + ", othOutsdLoanSeq=" + getOthOutsdLoanSeq()
                + ", instnNm='" + getInstnNm() + "'" + ", totLoanAmt=" + getTotLoanAmt() + ", loanPrps='"
                + getLoanPrps() + "'" + ", crntOutsdAmt=" + getCrntOutsdAmt() + ", cmplDt='" + getCmplDt() + "'"
                + ", mnthExpFlg='" + isMnthExpFlg() + "'" + ", mfcibFlg='" + isMfcibFlg() + "'" + ", crtdBy='"
                + getCrtdBy() + "'" + ", crtdDt='" + getCrtdDt() + "'" + ", lastUpdBy='" + getLastUpdBy() + "'"
                + ", lastUpdDt='" + getLastUpdDt() + "'" + ", delFlg='" + isDelFlg() + "'" + ", effStartDt='"
                + getEffStartDt() + "'" + ", effEndDt='" + getEffEndDt() + "'" + ", crntRecFlg='" + isCrntRecFlg() + "'"
                + "}";
    }
}
