package com.idev4.rds.domain;

import com.idev4.rds.ids.DisbursementVoucherDetailId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_DSBMT_VCHR_DTL")
@IdClass(DisbursementVoucherDetailId.class)
public class DisbursementVoucherDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DSBMT_DTL_KEY")
    private Long dsbmtDtlKey;

    @Column(name = "INSTR_NUM")
    private String instrNum;

    @Column(name = "AMT")
    private Long amt;

    @Column(name = "DSBMT_HDR_SEQ")
    private Long dsbmtHdrSeq;

    @Column(name = "PYMT_TYPS_SEQ")
    private Long pymtTypSeq;

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

    public Long getDsbmtDtlKey() {
        return dsbmtDtlKey;
    }

    public void setDsbmtDtlKey(Long dsbmtDtlKey) {
        this.dsbmtDtlKey = dsbmtDtlKey;
    }

    public String getInstrNum() {
        return instrNum;
    }

    public void setInstrNum(String instrNum) {
        this.instrNum = instrNum;
    }

    public Long getAmt() {
        return amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
    }

    public Long getDsbmtHdrSeq() {
        return dsbmtHdrSeq;
    }

    public void setDsbmtHdrSeq(Long dsbmtHdrSeq) {
        this.dsbmtHdrSeq = dsbmtHdrSeq;
    }

    public Long getPymtTypSeq() {
        return pymtTypSeq;
    }

    public void setPymtTypSeq(Long pymtTypSeq) {
        this.pymtTypSeq = pymtTypSeq;
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
