package com.idev4.rds.domain;

import com.idev4.rds.ids.PaymentScheduleDetailId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_pymt_sched_dtl")
@IdClass(PaymentScheduleDetailId.class)
public class PaymentScheduleDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PYMT_SCHED_DTL_SEQ")
    private Long paySchedDtlSeq;

    @Column(name = "PYMT_SCHED_HDR_SEQ")
    private Long paySchedHdrSeq;

    @Column(name = "INST_NUM")
    private Long instNum;

    @Column(name = "DUE_DT")
    private Instant dueDt;

    @Column(name = "PPAL_AMT_DUE")
    private Long ppalAmtDue;

    @Column(name = "TOT_CHRG_DUE")
    private Long totChrgDue;

    @Column(name = "pymt_sts_key")
    private Long pymtStsKey;

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

    public Long getPaySchedDtlSeq() {
        return paySchedDtlSeq;
    }

    public void setPaySchedDtlSeq(Long paySchedDtlSeq) {
        this.paySchedDtlSeq = paySchedDtlSeq;
    }

    public Long getPaySchedHdrSeq() {
        return paySchedHdrSeq;
    }

    public void setPaySchedHdrSeq(Long paySchedHdrSeq) {
        this.paySchedHdrSeq = paySchedHdrSeq;
    }

    public Long getInstNum() {
        return instNum;
    }

    public void setInstNum(Long instNum) {
        this.instNum = instNum;
    }

    public Instant getDueDt() {
        return dueDt;
    }

    public void setDueDt(Instant dueDt) {
        this.dueDt = dueDt;
    }

    public Long getPpalAmtDue() {
        return ppalAmtDue;
    }

    public void setPpalAmtDue(Long ppalAmtDue) {
        this.ppalAmtDue = ppalAmtDue;
    }

    public Long getTotChrgDue() {
        return totChrgDue;
    }

    public void setTotChrgDue(Long totChrgDue) {
        this.totChrgDue = totChrgDue;
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

    public Long getPymtStsKey() {
        return pymtStsKey;
    }

    public void setPymtStsKey(Long pymtStsKey) {
        this.pymtStsKey = pymtStsKey;
    }

}
