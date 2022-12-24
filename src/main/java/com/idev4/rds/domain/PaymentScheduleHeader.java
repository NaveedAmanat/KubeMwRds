package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_PYMT_SCHED_HDR")
// @IdClass ( PaymentScheduleHeaderId.class )
public class PaymentScheduleHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PYMT_SCHED_HDR_SEQ")
    private Long paySchedHdrSeq;

    @Column(name = "PYMT_SCHED_ID")
    private String paySchedId;

    @Column(name = "FRST_INST_DT")
    private Instant frstInstDt;

    // @Id
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

    @Column(name = "LOAN_APP_SEQ")
    private Long loanAppSeq;

    @Column(name = "SCHED_STS_KEY")
    private Long schedStsKey;

    public Long getPaySchedHdrSeq() {
        return paySchedHdrSeq;
    }

    public void setPaySchedHdrSeq(Long paySchedHdrSeq) {
        this.paySchedHdrSeq = paySchedHdrSeq;
    }

    public String getPaySchedId() {
        return paySchedId;
    }

    public void setPaySchedId(String paySchedId) {
        this.paySchedId = paySchedId;
    }

    public Instant getFrstInstDt() {
        return frstInstDt;
    }

    public void setFrstInstDt(Instant frstInstDt) {
        this.frstInstDt = frstInstDt;
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

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public Long getSchedStsKey() {
        return schedStsKey;
    }

    public void setSchedStsKey(Long schedStsKey) {
        this.schedStsKey = schedStsKey;
    }

}
