package com.idev4.rds.domain;

import com.idev4.rds.ids.PaymentScheduleChargersId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_PYMT_SCHED_CHRG")
@IdClass(PaymentScheduleChargersId.class)
public class PaymentScheduleChargers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PYMT_SCHED_CHRG_SEQ")
    private Long paySchedChrgSeq;

    @Column(name = "PYMT_SCHED_DTL_SEQ")
    private Long paySchedDtlSeq;

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "AMT")
    private Long amt;

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

    @Column(name = "CHRG_TYPS_SEQ")
    private Long chrgTypsSeq;

    @Column(name = "SYNC_FLG")
    private Long syncFlg;

    public Long getPaySchedChrgSeq() {
        return paySchedChrgSeq;
    }

    public void setPaySchedChrgSeq(Long paySchedChrgSeq) {
        this.paySchedChrgSeq = paySchedChrgSeq;
    }

    public Long getPaySchedDtlSeq() {
        return paySchedDtlSeq;
    }

    public void setPaySchedDtlSeq(Long paySchedDtlSeq) {
        this.paySchedDtlSeq = paySchedDtlSeq;
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

    public Long getAmt() {
        return amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
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

    public Long getChrgTypsSeq() {
        return chrgTypsSeq;
    }

    public void setChrgTypsSeq(Long chrgTypsSeq) {
        this.chrgTypsSeq = chrgTypsSeq;
    }

    public Long getSyncFlg() {
        return syncFlg;
    }

    public void setSyncFlg(Long syncFlg) {
        this.syncFlg = syncFlg;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amt == null) ? 0 : amt.hashCode());
        result = prime * result + ((chrgTypsSeq == null) ? 0 : chrgTypsSeq.hashCode());
        result = prime * result + ((crntRecFlg == null) ? 0 : crntRecFlg.hashCode());
        result = prime * result + ((crtdBy == null) ? 0 : crtdBy.hashCode());
        result = prime * result + ((crtdDt == null) ? 0 : crtdDt.hashCode());
        result = prime * result + ((delFlg == null) ? 0 : delFlg.hashCode());
        result = prime * result + ((effEndDt == null) ? 0 : effEndDt.hashCode());
        result = prime * result + ((effStartDt == null) ? 0 : effStartDt.hashCode());
        result = prime * result + ((lastUpdBy == null) ? 0 : lastUpdBy.hashCode());
        result = prime * result + ((lastUpdDt == null) ? 0 : lastUpdDt.hashCode());
        result = prime * result + ((paySchedChrgSeq == null) ? 0 : paySchedChrgSeq.hashCode());
        result = prime * result + ((paySchedDtlSeq == null) ? 0 : paySchedDtlSeq.hashCode());
        result = prime * result + ((syncFlg == null) ? 0 : syncFlg.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentScheduleChargers other = (PaymentScheduleChargers) obj;
        if (amt == null) {
            if (other.amt != null)
                return false;
        } else if (!amt.equals(other.amt))
            return false;
        if (chrgTypsSeq == null) {
            if (other.chrgTypsSeq != null)
                return false;
        } else if (!chrgTypsSeq.equals(other.chrgTypsSeq))
            return false;
        if (crntRecFlg == null) {
            if (other.crntRecFlg != null)
                return false;
        } else if (!crntRecFlg.equals(other.crntRecFlg))
            return false;
        if (crtdBy == null) {
            if (other.crtdBy != null)
                return false;
        } else if (!crtdBy.equals(other.crtdBy))
            return false;
        if (crtdDt == null) {
            if (other.crtdDt != null)
                return false;
        } else if (!crtdDt.equals(other.crtdDt))
            return false;
        if (delFlg == null) {
            if (other.delFlg != null)
                return false;
        } else if (!delFlg.equals(other.delFlg))
            return false;
        if (effEndDt == null) {
            if (other.effEndDt != null)
                return false;
        } else if (!effEndDt.equals(other.effEndDt))
            return false;
        if (effStartDt == null) {
            if (other.effStartDt != null)
                return false;
        } else if (!effStartDt.equals(other.effStartDt))
            return false;
        if (lastUpdBy == null) {
            if (other.lastUpdBy != null)
                return false;
        } else if (!lastUpdBy.equals(other.lastUpdBy))
            return false;
        if (lastUpdDt == null) {
            if (other.lastUpdDt != null)
                return false;
        } else if (!lastUpdDt.equals(other.lastUpdDt))
            return false;
        if (paySchedChrgSeq == null) {
            if (other.paySchedChrgSeq != null)
                return false;
        } else if (!paySchedChrgSeq.equals(other.paySchedChrgSeq))
            return false;
        if (paySchedDtlSeq == null) {
            if (other.paySchedDtlSeq != null)
                return false;
        } else if (!paySchedDtlSeq.equals(other.paySchedDtlSeq))
            return false;
        if (syncFlg == null) {
            if (other.syncFlg != null)
                return false;
        } else if (!syncFlg.equals(other.syncFlg))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentScheduleChargers [paySchedChrgSeq=" + paySchedChrgSeq + ", paySchedDtlSeq=" + paySchedDtlSeq + ", effStartDt="
                + effStartDt + ", effEndDt=" + effEndDt + ", amt=" + amt + ", crtdBy=" + crtdBy + ", crtdDt=" + crtdDt + ", lastUpdBy="
                + lastUpdBy + ", lastUpdDt=" + lastUpdDt + ", delFlg=" + delFlg + ", crntRecFlg=" + crntRecFlg + ", chrgTypsSeq="
                + chrgTypsSeq + ", syncFlg=" + syncFlg + "]";
    }

}
