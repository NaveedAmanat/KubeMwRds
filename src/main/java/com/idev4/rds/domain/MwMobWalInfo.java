package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

/*
 * Added By Naveed - Date - 23-01-2022
 * SCR - Mobile Wallet Control
 * */

@Entity
@Table(name = "MW_MOB_WAL_INFO")
public class MwMobWalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MOB_WAL_SEQ")
    private Long mobWalSeq;
//
//    @Column ( name = "LOAN_APP_SEQ" )
//    private Long loanAppSeq;

    @Column(name = "CLNT_SEQ")
    private Long clntSeq;

    @Column(name = "MOB_WAL_CHNL")
    private String mobWalChnl;

    @Column(name = "MOB_WAL_NO")
    private String mobWalNo;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CRTD_BY")
    private String crtdBy;

    @Column(name = "CRTD_DT")
    private Instant crtdDt;

    @Column(name = "LAST_UPD_BY")
    private String lastUpdBy;

    @Column(name = "LAST_UPD_DT")
    private Instant lastUpdDt;

    @Column(name = "DEL_FLG")
    private Boolean delFlg;

    @Column(name = "CRNT_REC_FLG")
    private Boolean crntRecFlg;

    // Modified By Naveed - Date - 24-02-2022
    // Upaisa and HBL Konnect Mobile wallet Payment Mode
    @Column(name = "ATM_CARD_NO")
    private String atmCardNo;

    @Column(name = "ATM_CARD_REC_DT")
    private Instant atmCardRecDt;

    @Column(name = "ATM_CARD_DELVRY_DT")
    private Instant atmCardDelvryDt;
    // Ended By Naveed

    public MwMobWalInfo() {
    }

    public MwMobWalInfo(Long mobWalSeq, Long clntSeq, String mobWalChnl, String mobWalNo, String remarks, String crtdBy, Instant crtdDt, String lastUpdBy, Instant lastUpdDt, Boolean delFlg, Boolean crntRecFlg, String atmCardNo, Instant atmCardRecDt, Instant atmCardDelvryDt) {
        this.mobWalSeq = mobWalSeq;
        this.clntSeq = clntSeq;
        this.mobWalChnl = mobWalChnl;
        this.mobWalNo = mobWalNo;
        this.remarks = remarks;
        this.crtdBy = crtdBy;
        this.crtdDt = crtdDt;
        this.lastUpdBy = lastUpdBy;
        this.lastUpdDt = lastUpdDt;
        this.delFlg = delFlg;
        this.crntRecFlg = crntRecFlg;
        this.atmCardNo = atmCardNo;
        this.atmCardRecDt = atmCardRecDt;
        this.atmCardDelvryDt = atmCardDelvryDt;
    }

    public Long getMobWalSeq() {
        return mobWalSeq;
    }

    public void setMobWalSeq(Long mobWalSeq) {
        this.mobWalSeq = mobWalSeq;
    }

    public Long getClntSeq() {
        return clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public String getMobWalChnl() {
        return mobWalChnl;
    }

    public void setMobWalChnl(String mobWalChnl) {
        this.mobWalChnl = mobWalChnl;
    }

    public String getMobWalNo() {
        return mobWalNo;
    }

    public void setMobWalNo(String mobWalNo) {
        this.mobWalNo = mobWalNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getAtmCardNo() {
        return atmCardNo;
    }

    public void setAtmCardNo(String atmCardNo) {
        this.atmCardNo = atmCardNo;
    }

    public Instant getAtmCardRecDt() {
        return atmCardRecDt;
    }

    public void setAtmCardRecDt(Instant atmCardRecDt) {
        this.atmCardRecDt = atmCardRecDt;
    }

    public Instant getAtmCardDelvryDt() {
        return atmCardDelvryDt;
    }

    public void setAtmCardDelvryDt(Instant atmCardDelvryDt) {
        this.atmCardDelvryDt = atmCardDelvryDt;
    }
}
