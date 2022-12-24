package com.idev4.rds.domain;

import com.idev4.rds.ids.MwRcvryTypsId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_RCVRY")
@IdClass(MwRcvryTypsId.class)
public class MwRcvryTyps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RCVRY_TYPS_SEQ")
    private Long rcvryTypsSeq;

    @Column(name = "RCVRY_TYP_ID")
    private String rcvryTypId;

    @Column(name = "RCVRY_TYP")
    private String rcvryTyp;

    @Column(name = "GL_ACCT_NUM")
    private String glAcctNum;

    @Column(name = "RCVRY_TYP_STS_KEY")
    private Long rcvryTypStsKey;

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

    public Long getRcvryTypsSeq() {
        return rcvryTypsSeq;
    }

    public void setRcvryTypsSeq(Long rcvryTypsSeq) {
        this.rcvryTypsSeq = rcvryTypsSeq;
    }

    public String getRcvryTypId() {
        return rcvryTypId;
    }

    public void setRcvryTypId(String rcvryTypId) {
        this.rcvryTypId = rcvryTypId;
    }

    public String getRcvryTyp() {
        return rcvryTyp;
    }

    public void setRcvryTyp(String rcvryTyp) {
        this.rcvryTyp = rcvryTyp;
    }

    public String getGlAcctNum() {
        return glAcctNum;
    }

    public void setGlAcctNum(String glAcctNum) {
        this.glAcctNum = glAcctNum;
    }

    public Long getRcvryTypStsKey() {
        return rcvryTypStsKey;
    }

    public void setRcvryTypStsKey(Long rcvryTypStsKey) {
        this.rcvryTypStsKey = rcvryTypStsKey;
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

}
