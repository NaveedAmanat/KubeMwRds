package com.idev4.rds.domain;

import com.idev4.rds.ids.TypesId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_TYPS")
@IdClass(TypesId.class)
public class Types implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TYP_SEQ")
    private Long typSeq;

    @Column(name = "TYP_STR")
    private String typStr;

    @Column(name = "GL_ACCT_NUM")
    private String glAcctNum;

    @Column(name = "TYP_STS_KEY")
    private Long typStsKey;

    @Column(name = "TYP_CTGRY_KEY")
    private Long typCtgryKey;

    @Column(name = "TYP_ID")
    private String typId;

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

    @Column(name = "BRNCH_SEQ")
    private Long brnchSeq;

    @Column(name = "DFRD_ACCT_NUM")
    private String dfrdAcctNum;

    public String getDfrdAcctNum() {
        return dfrdAcctNum;
    }

    public void setDfrdAcctNum(String dfrdAcctNum) {
        this.dfrdAcctNum = dfrdAcctNum;
    }

    public Long getTypSeq() {
        return typSeq;
    }

    public void setTypSeq(Long typSeq) {
        this.typSeq = typSeq;
    }

    public String getTypStr() {
        return typStr;
    }

    public void setTypStr(String typStr) {
        this.typStr = typStr;
    }

    public String getGlAcctNum() {
        return glAcctNum;
    }

    public void setGlAcctNum(String glAcctNum) {
        this.glAcctNum = glAcctNum;
    }

    public Long getTypStsKey() {
        return typStsKey;
    }

    public void setTypStsKey(Long typStsKey) {
        this.typStsKey = typStsKey;
    }

    public Long getTypCtgryKey() {
        return typCtgryKey;
    }

    public void setTypCtgryKey(Long typCtgryKey) {
        this.typCtgryKey = typCtgryKey;
    }

    public String getTypId() {
        return typId;
    }

    public void setTypId(String typId) {
        this.typId = typId;
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

    public Long getBrnchSeq() {
        return brnchSeq;
    }

    public void setBrnchSeq(Long brnchSeq) {
        this.brnchSeq = brnchSeq;
    }

}
