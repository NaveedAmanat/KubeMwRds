package com.idev4.rds.domain;

import com.idev4.rds.ids.MwRcvryDtlId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "mw_rcvry_dtl")
@IdClass(MwRcvryDtlId.class)
public class MwRcvryDtl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rcvry_chrg_seq")
    private Long rcvryChrgSeq;

    @Column(name = "rcvry_trx_seq")
    private Long rcvryTrxSeq;

    @Column(name = "pymt_sched_dtl_seq")
    private Long paySchedDtlSeq;

    @Column(name = "chrg_typ_key")
    private Long chrgTypKey;

    @Column(name = "pymt_amt")
    private Long pymtAmt;

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

    public Long getRcvryChrgSeq() {
        return rcvryChrgSeq;
    }

    public void setRcvryChrgSeq(Long rcvryChrgSeq) {
        this.rcvryChrgSeq = rcvryChrgSeq;
    }

    public Long getRcvryTrxSeq() {
        return rcvryTrxSeq;
    }

    public void setRcvryTrxSeq(Long rcvryTrxSeq) {
        this.rcvryTrxSeq = rcvryTrxSeq;
    }

    public Long getPaySchedDtlSeq() {
        return paySchedDtlSeq;
    }

    public void setPaySchedDtlSeq(Long paySchedDtlSeq) {
        this.paySchedDtlSeq = paySchedDtlSeq;
    }

    public Long getChrgTypKey() {
        return chrgTypKey;
    }

    public void setChrgTypKey(Long chrgTypKey) {
        this.chrgTypKey = chrgTypKey;
    }

    public Long getPymtAmt() {
        return pymtAmt;
    }

    public void setPymtAmt(Long pymtAmt) {
        this.pymtAmt = pymtAmt;
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
