package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "MW_DSBLTY_RPT")
public class MwDsbltyRpt {
    @Id
    @Column(name = "DSBLTY_RPT_SEQ")
    private Long dsbltyRptSeq;

    @Column(name = "CLNT_SEQ")
    private Long clntSeq;

    @Column(name = "EFF_START_DT")
    private Instant effStartDt;

    @Column(name = "CLNT_NOM_FLG")
    private Integer clntNomFlg;

    @Column(name = "DT_OF_DSBLTY")
    private Instant dtOfDsblty;

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

    @Column(name = "EFF_END_DT")
    private Instant effEndDt;

    @Column(name = "CRNT_REC_FLG")
    private Boolean crntRecFlg;

    @Column(name = "AMT")
    private Long amt;

    @Column(name = "CMNT")
    private String cmnt;

    @Column(name = "INSR_CLM_STS")
    private Long insrClmSts;

    @Column(name = "ADJ_FLG")
    private Long adjFlg;

    public Long getDsbltyRptSeq() {
        return this.dsbltyRptSeq;
    }

    public void setDsbltyRptSeq(Long dsbltyRptSeq) {
        this.dsbltyRptSeq = dsbltyRptSeq;
    }

    public Long getClntSeq() {
        return this.clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public Instant getEffStartDt() {
        return this.effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public Integer getClntNomFlg() {
        return this.clntNomFlg;
    }

    public void setClntNomFlg(Integer clntNomFlg) {
        this.clntNomFlg = clntNomFlg;
    }

    public Instant getDtOfDsblty() {
        return this.dtOfDsblty;
    }

    public void setDtOfDsblty(Instant dtOfDsblty) {
        this.dtOfDsblty = dtOfDsblty;
    }

    public String getCrtdBy() {
        return this.crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Instant getCrtdDt() {
        return this.crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public String getLastUpdBy() {
        return this.lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public Instant getLastUpdDt() {
        return this.lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public Boolean getDelFlg() {
        return this.delFlg;
    }

    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }

    public Instant getEffEndDt() {
        return this.effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public Boolean getCrntRecFlg() {
        return this.crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Long getAmt() {
        return this.amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
    }

    public String getCmnt() {
        return this.cmnt;
    }

    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }

    public Long getInsrClmSts() {
        return this.insrClmSts;
    }

    public void setInsrClmSts(Long insrClmSts) {
        this.insrClmSts = insrClmSts;
    }

    public Long getAdjFlg() {
        return this.adjFlg;
    }

    public void setAdjFlg(Long adjFlg) {
        this.adjFlg = adjFlg;
    }
}
