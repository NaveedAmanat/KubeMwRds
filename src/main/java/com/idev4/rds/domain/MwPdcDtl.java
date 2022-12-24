package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPdcDtlId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_PDC_DTL")
@IdClass(MwPdcDtlId.class)
public class MwPdcDtl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PDC_DTL_SEQ")
    private Long pdcDtlSeq;

    @Column(name = "pdc_hdr_seq")
    private Long pdcHdrSeq;

    @Column(name = "PDC_ID")
    private String pdcId;

    @Column(name = "CHQ_NUM")
    private String cheqNum;

    @Column(name = "COLL_DT")
    private Instant collDt;

    @Column(name = "AMT")
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

    public Long getPdcDtlSeq() {
        return pdcDtlSeq;
    }

    public void setPdcDtlSeq(Long pdcDtlSeq) {
        this.pdcDtlSeq = pdcDtlSeq;
    }

    public Long getPdcHdrSeq() {
        return pdcHdrSeq;
    }

    public void setPdcHdrSeq(Long pdcHdrSeq) {
        this.pdcHdrSeq = pdcHdrSeq;
    }

    public String getPdcId() {
        return pdcId;
    }

    public void setPdcId(String pdcId) {
        this.pdcId = pdcId;
    }

    public String getcheqNum() {
        return cheqNum;
    }

    public void setcheqNum(String cheqNum) {
        this.cheqNum = cheqNum;
    }

    public Instant getCollDt() {
        return collDt;
    }

    public void setCollDt(Instant collDt) {
        this.collDt = collDt;
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
