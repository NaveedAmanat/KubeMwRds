package com.idev4.rds.domain;

import com.idev4.rds.ids.MwDthRptId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "mw_dth_rpt")
@IdClass(MwDthRptId.class)
public class MwDthRpt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "dth_rpt_seq")
    private Long dthRptSeq;

    @Column(name = "clnt_seq")
    private Long clntSeq;

    @Column(name = "clnt_nom_flg")
    private Long clntNomFlg;

    @Column(name = "dt_of_dth")
    private Instant dtOfDth;

    @Column(name = "cause_of_dth")
    private String causeOfDth;

    @Column(name = "dth_cert_num")
    private String dthCertNum;

    @Column(name = "amt")
    private Long amt;

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

    public Long getDthRptSeq() {
        return dthRptSeq;
    }

    public void setDthRptSeq(Long dthRptSeq) {
        this.dthRptSeq = dthRptSeq;
    }

    public Long getClntSeq() {
        return clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public Long getClntNomFlg() {
        return clntNomFlg;
    }

    public void setClntNomFlg(Long clntNomFlg) {
        this.clntNomFlg = clntNomFlg;
    }

    public Instant getDtOfDth() {
        return dtOfDth;
    }

    public void setDtOfDth(Instant dtOfDth) {
        this.dtOfDth = dtOfDth;
    }

    public String getCauseOfDth() {
        return causeOfDth;
    }

    public void setCauseOfDth(String causeOfDth) {
        this.causeOfDth = causeOfDth;
    }

    public String getDthCertNum() {
        return dthCertNum;
    }

    public void setDthCertNum(String dthCertNum) {
        this.dthCertNum = dthCertNum;
    }

    public Long getAmt() {
        return amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
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
