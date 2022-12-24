package com.idev4.rds.domain;

import com.idev4.rds.ids.DisbursementVoucherHeaderId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_DSBMT_VCHR_HDR")
@IdClass(DisbursementVoucherHeaderId.class)
public class DisbursementVoucherHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DSBMT_HDR_SEQ")
    private Long dsbmtHdrSeq;

    @Column(name = "DSBMT_ID")
    private String dsbmtId;

    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    @Column(name = "DSBMT_DT")
    private Instant dsbmtDt;

    @Column(name = "DSBMT_VCHR_TYP")
    private Integer dsbmtVchrTyp;
    @Column(name = "DSBMT_STS_KEY")
    private Long dsbmtStsKey;
    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;
    @Column(name = "eff_end_dt")
    private Instant effEndDt;
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
    // Modified By Naveed - Date - 23-01-2022
    // SCR - Mobile Wallet Control
    // all mobile wallet history against client
    @Column(name = "MOB_WAL_SEQ")
    private Long mobWalSeq;

    public Integer getDsbmtVchrTyp() {
        return dsbmtVchrTyp;
    }

    public void setDsbmtVchrTyp(Integer dsbmtVchrTyp) {
        this.dsbmtVchrTyp = dsbmtVchrTyp;
    }
    // Ended By Naveed - Date - 23-01-2022

    public Long getDsbmtHdrSeq() {
        return dsbmtHdrSeq;
    }

    public void setDsbmtHdrSeq(Long dsbmtHdrSeq) {
        this.dsbmtHdrSeq = dsbmtHdrSeq;
    }

    public String getDsbmtId() {
        return dsbmtId;
    }

    public void setDsbmtId(String dsbmtId) {
        this.dsbmtId = dsbmtId;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public Instant getDsbmtDt() {
        return dsbmtDt;
    }

    public void setDsbmtDt(Instant dsbmtDt) {
        this.dsbmtDt = dsbmtDt;
    }

    public Long getDsbmtStsKey() {
        return dsbmtStsKey;
    }

    public void setDsbmtStsKey(Long dsbmtStsKey) {
        this.dsbmtStsKey = dsbmtStsKey;
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

    public Long getMobWalSeq() {
        return mobWalSeq;
    }

    public void setMobWalSeq(Long mobWalSeq) {
        this.mobWalSeq = mobWalSeq;
    }
}
