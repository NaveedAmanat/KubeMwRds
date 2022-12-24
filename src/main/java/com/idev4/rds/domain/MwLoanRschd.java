package com.idev4.rds.domain;

import com.idev4.rds.ids.MwLoanRschdId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MwBrnch.
 */
@Entity
@Table(name = "MW_LOAN_RSCHD")
@IdClass(MwLoanRschdId.class)
public class MwLoanRschd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOAN_RSCHD_SEQ")
    private Long loanRschdSeq;

    @Id
    @Column(name = "EFF_START_DT")
    private Instant effStartDt;

    @Column(name = "LOAN_APP_SEQ")
    private Long loanAppSeq;

    @Column(name = "RSCHD_RSN_SEQ")
    private Long rschdRsnSeq;

    @Column(name = "RSCHD_MTHD_SEQ")
    private Long rschdMthdSeq;

    @Column(name = "GRACE_PERD")
    private Long gracePerd;

    @Column(name = "CMNT")
    private String cmnt;

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

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    @Column(name = "RVSL_INST")
    private Integer rvslInst;

    @Column(name = "BGN_INST_NUM")
    private Long bgnInstNum;

    @Column(name = "ACRD_SRVC_CHRG")
    private Long acrdSrvcChrg;

    public Long getAcrdSrvcChrg() {
        return acrdSrvcChrg;
    }

    public void setAcrdSrvcChrg(Long acrdSrvcChrg) {
        this.acrdSrvcChrg = acrdSrvcChrg;
    }

    public Long getBgnInstNum() {
        return bgnInstNum;
    }

    public void setBgnInstNum(Long bgnInstNum) {
        this.bgnInstNum = bgnInstNum;
    }

    public Integer getRvslInst() {
        return rvslInst;
    }

    public void setRvslInst(Integer rvslInst) {
        this.rvslInst = rvslInst;
    }

    public Long getLoanRschdSeq() {
        return loanRschdSeq;
    }

    public void setLoanRschdSeq(Long loanRschdSeq) {
        this.loanRschdSeq = loanRschdSeq;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public Long getRschdRsnSeq() {
        return rschdRsnSeq;
    }

    public void setRschdRsnSeq(Long rschdRsnSeq) {
        this.rschdRsnSeq = rschdRsnSeq;
    }

    public Long getRschdMthdSeq() {
        return rschdMthdSeq;
    }

    public void setRschdMthdSeq(Long rschdMthdSeq) {
        this.rschdMthdSeq = rschdMthdSeq;
    }

    public Long getGracePerd() {
        return gracePerd;
    }

    public void setGracePerd(Long gracePerd) {
        this.gracePerd = gracePerd;
    }

    public String getCmnt() {
        return cmnt;
    }

    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
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

}
