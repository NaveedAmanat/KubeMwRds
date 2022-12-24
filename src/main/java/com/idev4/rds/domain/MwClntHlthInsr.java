package com.idev4.rds.domain;

import com.idev4.rds.ids.MwClntHlthInsrId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwClntHlthInsr.
 */
@Entity
@Table(name = "mw_clnt_hlth_insr")
@IdClass(MwClntHlthInsrId.class)
public class MwClntHlthInsr implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "hlth_insr_plan_seq")
    public Long mwHlthInsrPlan;
    @Column(name = "rel_wth_bread_earner_key")
    public Long relWthBreadEarnerKey;
    @Column(name = "main_bread_earner_nm")
    public String mainBreadEarnerNm;
    @Id
    @Column(name = "clnt_hlth_insr_seq")
    private Long clntHlthInsrSeq;
    @Column(name = "loan_App_SEQ")
    private Long loanAppSeq;
    @Column(name = "excl_ctgry_key")
    private Long exclCtgryKey;
    @Column(name = "hlth_insr_flg")
    private Boolean hlthInsrFlg;
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
    @Column(name = "SYNC_FLG")
    private Boolean syncFlg;

    public Boolean getSyncFlg() {
        return syncFlg;
    }

    public void setSyncFlg(Boolean syncFlg) {
        this.syncFlg = syncFlg;
    }
    /*@ManyToOne
    private MwClnt mwClnt;
    
    @ManyToOne
    private MwHlthInsrPlan mwHlthInsrPlan;*/

    public Long getRelWthBreadEarnerKey() {
        return relWthBreadEarnerKey;
    }

    public void setRelWthBreadEarnerKey(Long relWthBreadEarnerKey) {
        this.relWthBreadEarnerKey = relWthBreadEarnerKey;
    }

    public String getMainBreadEarnerNm() {
        return mainBreadEarnerNm;
    }

    public void setMainBreadEarnerNm(String mainBreadEarnerNm) {
        this.mainBreadEarnerNm = mainBreadEarnerNm;
    }

    public Boolean getHlthInsrFlg() {
        return hlthInsrFlg;
    }

    public void setHlthInsrFlg(Boolean hlthInsrFlg) {
        this.hlthInsrFlg = hlthInsrFlg;
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

    public Long getClntHlthInsrSeq() {
        return clntHlthInsrSeq;
    }

    public void setClntHlthInsrSeq(Long clntHlthInsrSeq) {
        this.clntHlthInsrSeq = clntHlthInsrSeq;
    }

    public MwClntHlthInsr clntHlthInsrSeq(Long clntHlthInsrSeq) {
        this.clntHlthInsrSeq = clntHlthInsrSeq;
        return this;
    }

    /**
     * @return the loanAppSeq
     */
    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    /**
     * @param loanAppSeq the loanAppSeq to set
     */
    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public Long getExclCtgryKey() {
        return exclCtgryKey;
    }

    public void setExclCtgryKey(Long exclCtgryKey) {
        this.exclCtgryKey = exclCtgryKey;
    }

    public MwClntHlthInsr exclCtgryKey(Long exclCtgryKey) {
        this.exclCtgryKey = exclCtgryKey;
        return this;
    }

    public Boolean isHlthInsrFlg() {
        return hlthInsrFlg;
    }

    public MwClntHlthInsr hlthInsrFlg(Boolean hlthInsrFlg) {
        this.hlthInsrFlg = hlthInsrFlg;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwClntHlthInsr crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwClntHlthInsr crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwClntHlthInsr lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwClntHlthInsr lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwClntHlthInsr delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public MwClntHlthInsr effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public MwClntHlthInsr effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwClntHlthInsr crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    /**
     * @return the mwHlthInsrPlan
     */
    public Long getMwHlthInsrPlan() {
        return mwHlthInsrPlan;
    }

    /**
     * @param mwHlthInsrPlan the mwHlthInsrPlan to set
     */
    public void setMwHlthInsrPlan(Long mwHlthInsrPlan) {
        this.mwHlthInsrPlan = mwHlthInsrPlan;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MwClntHlthInsr mwClntHlthInsr = (MwClntHlthInsr) o;
        if (mwClntHlthInsr.getClntHlthInsrSeq() == null || getClntHlthInsrSeq() == null) {
            return false;
        }
        return Objects.equals(getClntHlthInsrSeq(), mwClntHlthInsr.getClntHlthInsrSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClntHlthInsrSeq());
    }

    @Override
    public String toString() {
        return "MwClntHlthInsr{" + "id=" + getClntHlthInsrSeq() + ", clntHlthInsrSeq=" + getClntHlthInsrSeq() + ", exclCtgryKey="
                + getExclCtgryKey() + ", hlthInsrFlg='" + isHlthInsrFlg() + "'" + ", crtdBy='" + getCrtdBy() + "'" + ", crtdDt='"
                + getCrtdDt() + "'" + ", lastUpdBy='" + getLastUpdBy() + "'" + ", lastUpdDt='" + getLastUpdDt() + "'" + ", delFlg='"
                + isDelFlg() + "'" + ", effStartDt='" + getEffStartDt() + "'" + ", effEndDt='" + getEffEndDt() + "'" + ", crntRecFlg='"
                + isCrntRecFlg() + "'" + "}";
    }
}
