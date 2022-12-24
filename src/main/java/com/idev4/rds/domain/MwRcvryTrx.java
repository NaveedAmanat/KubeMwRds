package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "mw_rcvry_trx")
// @IdClass ( MwRcvryTrxId.class )
public class MwRcvryTrx implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rcvry_trx_seq")
    private Long rcvryTrxSeq;

    @Column(name = "instrNum")
    private String instrNum;

    @Column(name = "pymtDt")
    private Date pymtDt;

    @Column(name = "pymtAmt")
    private Long pymtAmt;

    @Column(name = "rcvry_typ_seq")
    private Long rcvryTypSeq;

    @Column(name = "pymt_mod_key")
    private Long pymtModKey;

    @Column(name = "pymt_sts_key")
    private Long pymtStsKey;

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

    @Column(name = "pymt_ref")
    private Long pymtRef;

    @Column(name = "post_flg")
    private Long postFlg;

    @Column(name = "chng_rsn_key")
    private Long chngRsnKey;

    @Column(name = "chng_rsn_cmnt")
    private String chngRsnCmnt;

    @Column(name = "PRNT_RCVRY_REF")
    private Long prntRcvryRef;

    @Column(name = "DPST_SLP_DT")
    private Instant dpstSlpDt;

    @Column(name = "PRNT_LOAN_APP_SEQ")
    private Long prntLoanAppSeq;

    public Long getPrntRcvryRef() {
        return prntRcvryRef;
    }

    public void setPrntRcvryRef(Long prntRcvryRef) {
        this.prntRcvryRef = prntRcvryRef;
    }

    public Instant getDpstSlpDt() {
        return dpstSlpDt;
    }

    public void setDpstSlpDt(Instant dpstSlpDt) {
        this.dpstSlpDt = dpstSlpDt;
    }

    public Long getRcvryTrxSeq() {
        return rcvryTrxSeq;
    }

    public void setRcvryTrxSeq(Long rcvryTrxSeq) {
        this.rcvryTrxSeq = rcvryTrxSeq;
    }

    public String getInstrNum() {
        return instrNum;
    }

    public void setInstrNum(String instrNum) {
        this.instrNum = instrNum;
    }

    public Date getPymtDt() {
        return pymtDt;
    }

    public void setPymtDt(Date pymtDt) {
        this.pymtDt = pymtDt;
    }

    public Long getPymtAmt() {
        return pymtAmt;
    }

    public void setPymtAmt(Long pymtAmt) {
        this.pymtAmt = pymtAmt;
    }

    public Long getRcvryTypSeq() {
        return rcvryTypSeq;
    }

    public void setRcvryTypSeq(Long rcvryTypSeq) {
        this.rcvryTypSeq = rcvryTypSeq;
    }

    public Long getPymtModKey() {
        return pymtModKey;
    }

    public void setPymtModKey(Long pymtModKey) {
        this.pymtModKey = pymtModKey;
    }

    public Long getPymtStsKey() {
        return pymtStsKey;
    }

    public void setPymtStsKey(Long pymtStsKey) {
        this.pymtStsKey = pymtStsKey;
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

    public Long getPymtRef() {
        return pymtRef;
    }

    public void setPymtRef(Long pymtRef) {
        this.pymtRef = pymtRef;
    }

    public Long getPostFlg() {
        return postFlg;
    }

    public void setPostFlg(Long postFlg) {
        this.postFlg = postFlg;
    }

    public Long getChngRsnKey() {
        return chngRsnKey;
    }

    public void setChngRsnKey(Long chngRsnKey) {
        this.chngRsnKey = chngRsnKey;
    }

    public String getChngRsnCmnt() {
        return chngRsnCmnt;
    }

    public void setChngRsnCmnt(String chngRsnCmnt) {
        this.chngRsnCmnt = chngRsnCmnt;
    }

    public Long getPrntLoanAppSeq() {
        return prntLoanAppSeq;
    }

    public void setPrntLoanAppSeq(Long prntLoanAppSeq) {
        this.prntLoanAppSeq = prntLoanAppSeq;
    }
}
