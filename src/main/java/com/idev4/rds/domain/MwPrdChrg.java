package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPrdChrgId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_PRD_CHRG")
@IdClass(MwPrdChrgId.class)
public class MwPrdChrg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PRD_CHRG_SEQ")
    private Long prdChrgSeq;

    @Column(name = "PRD_SEQ")
    private Long prdSeq;

    @Column(name = "RUL_SEQ")
    private Long rulSeq;

    @Column(name = "CHRG_CALC_TYP_KEY")
    private Long chrgCalcTypKey;

    @Column(name = "CHRG_VAL")
    private Double chrgVal;

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

    @Column(name = "UPFRONT_FLG")
    private Boolean upfrontFlg;

    @Column(name = "SGRT_INST_NUM")
    private Long sgrtInstNum;

    @Column(name = "ADJUST_ROUNDING_FLG")
    private Long adjustRoundingFlg;

    public Long getPrdChrgSeq() {
        return prdChrgSeq;
    }

    public void setPrdChrgSeq(Long prdChrgSeq) {
        this.prdChrgSeq = prdChrgSeq;
    }

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public Long getRulSeq() {
        return rulSeq;
    }

    public void setRulSeq(Long rulSeq) {
        this.rulSeq = rulSeq;
    }

    public Long getChrgCalcTypKey() {
        return chrgCalcTypKey;
    }

    public void setChrgCalcTypKey(Long chrgCalcTypKey) {
        this.chrgCalcTypKey = chrgCalcTypKey;
    }

    public Double getChrgVal() {
        return chrgVal;
    }

    public void setChrgVal(Double chrgVal) {
        this.chrgVal = chrgVal;
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

    public Boolean getUpfrontFlg() {
        return upfrontFlg;
    }

    public void setUpfrontFlg(Boolean upfrontFlg) {
        this.upfrontFlg = upfrontFlg;
    }

    public Long getSgrtInstNum() {
        return sgrtInstNum;
    }

    public void setSgrtInstNum(Long sgrtInstNum) {
        this.sgrtInstNum = sgrtInstNum;
    }

    public Long getAdjustRoundingFlg() {
        return adjustRoundingFlg;
    }

    public void setAdjustRoundingFlg(Long adjustRoundingFlg) {
        this.adjustRoundingFlg = adjustRoundingFlg;
    }

}
