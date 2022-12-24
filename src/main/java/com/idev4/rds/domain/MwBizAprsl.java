package com.idev4.rds.domain;

import com.idev4.rds.ids.MwBizAprslId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwBizAprsl.
 */
@Entity
@Table(name = "mw_biz_aprsl")
@IdClass(MwBizAprslId.class)
public class MwBizAprsl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "biz_aprsl_seq")
    private Long bizAprslSeq;

    @Column(name = "sect_key")
    private Long sectKey;

    @Column(name = "acty_key")
    private Long actyKey;

    @Column(name = "biz_dtl_key")
    private Long bizDtlKey;

    @Column(name = "prsn_run_the_biz")
    private Long prsnRunTheBiz;

    @Column(name = "biz_own")
    private Long bizOwn;

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

    @Column(name = "yrs_in_biz")
    private Integer yrsInBiz;

    @Column(name = "mnth_in_biz")
    private Integer mnthInBiz;

    @Column(name = "loan_app_seq")
    private Long mwLoanApp;

    @Column(name = "biz_addr_same_as_home_flg")
    private Boolean bizAddrSameAsHomeFlg;

    @Column(name = "biz_property_own_key")
    private Long bizPropertyOwnKey;

    @Column(name = "biz_ph_num")
    private String bizPhNum;

    // @ManyToOne
    // private MwLoanApp mwLoanApp;

    public Boolean getBizAddrSameAsHomeFlg() {
        return bizAddrSameAsHomeFlg;
    }

    public void setBizAddrSameAsHomeFlg(Boolean bizAddrSameAsHomeFlg) {
        this.bizAddrSameAsHomeFlg = bizAddrSameAsHomeFlg;
    }

    public Long getBizPropertyOwnKey() {
        return bizPropertyOwnKey;
    }

    public void setBizPropertyOwnKey(Long bizPropertyOwnKey) {
        this.bizPropertyOwnKey = bizPropertyOwnKey;
    }

    public String getBizPhNum() {
        return bizPhNum;
    }

    public void setBizPhNum(String bizPhNum) {
        this.bizPhNum = bizPhNum;
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

    public Long getBizAprslSeq() {
        return bizAprslSeq;
    }

    public void setBizAprslSeq(Long bizAprslSeq) {
        this.bizAprslSeq = bizAprslSeq;
    }

    public MwBizAprsl bizAprslSeq(Long bizAprslSeq) {
        this.bizAprslSeq = bizAprslSeq;
        return this;
    }

    public Long getSectKey() {
        return sectKey;
    }

    public void setSectKey(Long sectKey) {
        this.sectKey = sectKey;
    }

    public MwBizAprsl sectKey(Long sectKey) {
        this.sectKey = sectKey;
        return this;
    }

    public Long getActyKey() {
        return actyKey;
    }

    public void setActyKey(Long actyKey) {
        this.actyKey = actyKey;
    }

    public MwBizAprsl actyKey(Long actyKey) {
        this.actyKey = actyKey;
        return this;
    }

    public Long getBizDtlKey() {
        return bizDtlKey;
    }

    public void setBizDtlKey(Long bizDtlKey) {
        this.bizDtlKey = bizDtlKey;
    }

    public MwBizAprsl bizDtlKey(Long bizDtlKey) {
        this.bizDtlKey = bizDtlKey;
        return this;
    }

    public Long getPrsnRunTheBiz() {
        return prsnRunTheBiz;
    }

    public void setPrsnRunTheBiz(Long prsnRunTheBiz) {
        this.prsnRunTheBiz = prsnRunTheBiz;
    }

    public MwBizAprsl prsnRunTheBiz(Long prsnRunTheBiz) {
        this.prsnRunTheBiz = prsnRunTheBiz;
        return this;
    }

    public Long getBizOwn() {
        return bizOwn;
    }

    public void setBizOwn(Long bizOwn) {
        this.bizOwn = bizOwn;
    }

    public MwBizAprsl bizOwn(Long bizOwn) {
        this.bizOwn = bizOwn;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwBizAprsl crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwBizAprsl crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwBizAprsl lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwBizAprsl lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwBizAprsl delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public MwBizAprsl effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public MwBizAprsl effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwBizAprsl crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    public Integer getYrsInBiz() {
        return yrsInBiz;
    }

    public void setYrsInBiz(Integer yrsInBiz) {
        this.yrsInBiz = yrsInBiz;
    }

    public MwBizAprsl yrsInBiz(Integer yrsInBiz) {
        this.yrsInBiz = yrsInBiz;
        return this;
    }

    public Integer getMnthInBiz() {
        return mnthInBiz;
    }

    public void setMnthInBiz(Integer mnthInBiz) {
        this.mnthInBiz = mnthInBiz;
    }

    public MwBizAprsl mnthInBiz(Integer mnthInBiz) {
        this.mnthInBiz = mnthInBiz;
        return this;
    }

    public Long getMwLoanApp() {
        return mwLoanApp;
    }

    public void setMwLoanApp(Long mwLoanApp) {
        this.mwLoanApp = mwLoanApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MwBizAprsl mwBizAprsl = (MwBizAprsl) o;
        if (mwBizAprsl.getBizAprslSeq() == null || getBizAprslSeq() == null) {
            return false;
        }
        return Objects.equals(getBizAprslSeq(), mwBizAprsl.getBizAprslSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getBizAprslSeq());
    }

    @Override
    public String toString() {
        return "MwBizAprsl{" + "id=" + getBizAprslSeq() + ", bizAprslSeq=" + getBizAprslSeq() + ", sectKey=" + getSectKey() + ", actyKey="
                + getActyKey() + ", bizDtlKey=" + getBizDtlKey() + ", prsnRunTheBiz=" + getPrsnRunTheBiz() + ", bizOwn=" + getBizOwn()
                + ", crtdBy='" + getCrtdBy() + "'" + ", crtdDt='" + getCrtdDt() + "'" + ", lastUpdBy='" + getLastUpdBy() + "'"
                + ", lastUpdDt='" + getLastUpdDt() + "'" + ", delFlg='" + isDelFlg() + "'" + ", effStartDt='" + getEffStartDt() + "'"
                + ", effEndDt='" + getEffEndDt() + "'" + ", crntRecFlg='" + isCrntRecFlg() + "'" + ", yrsInBiz=" + getYrsInBiz()
                + ", mnthInBiz=" + getMnthInBiz() + "}";
    }
}
