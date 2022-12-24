package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPrdChrgAdjOrdrId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "mw_prd_chrg_adj_ordr")
@IdClass(MwPrdChrgAdjOrdrId.class)
public class MwPrdChrgAdjOrdr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prd_chrg_adj_ordr_SEQ")
    private Long prdChrgAdjOrdrSeq;

    @Column(name = "adj_ordr")
    private Long adjOrdr;

    @Column(name = "prd_SEQ")
    private Long prdSeq;

    @Column(name = "prd_chrg_SEQ")
    private String prdChrgSeq;

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

    public Long getPrdChrgAdjOrdrSeq() {
        return prdChrgAdjOrdrSeq;
    }

    public void setPrdChrgAdjOrdrSeq(Long prdChrgAdjOrdrSeq) {
        this.prdChrgAdjOrdrSeq = prdChrgAdjOrdrSeq;
    }

    public Long getAdjOrdr() {
        return adjOrdr;
    }

    public void setAdjOrdr(Long adjOrdr) {
        this.adjOrdr = adjOrdr;
    }

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public String getPrdChrgSeq() {
        return prdChrgSeq;
    }

    public void setPrdChrgSeq(String prdChrgSeq) {
        this.prdChrgSeq = prdChrgSeq;
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
