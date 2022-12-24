package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPrdId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MwPrd.
 */
@Entity
@Table(name = "mw_prd")
@IdClass(MwPrdId.class)

public class MwPrd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prd_seq")
    private Long prdSeq;

    @Column(name = "prd_grp_seq")
    private Long prdGrpSeq;

    @Column(name = "prd_id")
    private String prdId;

    @Column(name = "prd_nm")
    private String prdNm;

    @Column(name = "prd_cmnt")
    private String prdCmnt;

    @Column(name = "prd_sts_key")
    private Long prdStsKey;

    @Column(name = "prd_typ_key")
    private Long prdTypKey;

    @Column(name = "irr_flg")
    private Boolean irrFlg;

    @Column(name = "rndng_scl")
    private Long rndngScl;

    @Column(name = "rndng_adj")
    private Long rndngAdj;

    @Column(name = "daily_accural_flg")
    private Boolean dailyAccuralFlg;

    @Column(name = "fnd_by_key")
    private Long fndByKey;

    @Column(name = "crncy_cd_key")
    private Long crncyCdKey;

    @Column(name = "irr_val")
    private Double irrVal;

    @Column(name = "mlt_loan_flg")
    private Boolean mltLoanFlg;

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

    @Column(name = "pdc_num")
    private Integer pdcNum;

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public Long getPrdGrpSeq() {
        return prdGrpSeq;
    }

    public void setPrdGrpSeq(Long prdGrpSeq) {
        this.prdGrpSeq = prdGrpSeq;
    }

    public String getPrdId() {
        return prdId;
    }

    public void setPrdId(String prdId) {
        this.prdId = prdId;
    }

    public String getPrdNm() {
        return prdNm;
    }

    public void setPrdNm(String prdNm) {
        this.prdNm = prdNm;
    }

    public String getPrdCmnt() {
        return prdCmnt;
    }

    public void setPrdCmnt(String prdCmnt) {
        this.prdCmnt = prdCmnt;
    }

    public Long getPrdStsKey() {
        return prdStsKey;
    }

    public void setPrdStsKey(Long prdStsKey) {
        this.prdStsKey = prdStsKey;
    }

    public Long getPrdTypKey() {
        return prdTypKey;
    }

    public void setPrdTypKey(Long prdTypKey) {
        this.prdTypKey = prdTypKey;
    }

    public Boolean getIrrFlg() {
        return irrFlg;
    }

    public void setIrrFlg(Boolean irrFlg) {
        this.irrFlg = irrFlg;
    }

    public Long getRndngScl() {
        return rndngScl;
    }

    public void setRndngScl(Long rndngScl) {
        this.rndngScl = rndngScl;
    }

    public Long getRndngAdj() {
        return rndngAdj;
    }

    public void setRndngAdj(Long rndngAdj) {
        this.rndngAdj = rndngAdj;
    }

    public Boolean getDailyAccuralFlg() {
        return dailyAccuralFlg;
    }

    public void setDailyAccuralFlg(Boolean dailyAccuralFlg) {
        this.dailyAccuralFlg = dailyAccuralFlg;
    }

    public Long getFndByKey() {
        return fndByKey;
    }

    public void setFndByKey(Long fndByKey) {
        this.fndByKey = fndByKey;
    }

    public Long getCrncyCdKey() {
        return crncyCdKey;
    }

    public void setCrncyCdKey(Long crncyCdKey) {
        this.crncyCdKey = crncyCdKey;
    }

    public Double getIrrVal() {
        return irrVal;
    }

    public void setIrrVal(Double irrVal) {
        this.irrVal = irrVal;
    }

    public Boolean getMltLoanFlg() {
        return mltLoanFlg;
    }

    public void setMltLoanFlg(Boolean mltLoanFlg) {
        this.mltLoanFlg = mltLoanFlg;
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

    public Boolean getCrntRecFlg() {
        return crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Integer getPdcNum() {
        return pdcNum;
    }

    public void setPdcNum(Integer pdcNum) {
        this.pdcNum = pdcNum;
    }

}
