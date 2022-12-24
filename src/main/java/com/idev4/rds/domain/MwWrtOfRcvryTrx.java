package com.idev4.rds.domain;

import com.idev4.rds.ids.MwWrtOfRcvryTrxId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "MW_WRT_OF_RCVRY_TRX")
@IdClass(MwWrtOfRcvryTrxId.class)
public class MwWrtOfRcvryTrx implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "WRT_OF_RCVRY_TRX_SEQ")
    private Long wrtOfRcvryTrxSeq;

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "CLNT_SEQ")
    private Long clntSeq;

    @Column(name = "PYMT_MOD_KEY")
    private Long pymtModKey;

    @Column(name = "INSTR_NUM")
    private String instrNum;

    @Column(name = "PYMT_DT")
    private Date pymtDt;

    @Column(name = "PYMT_AMT")
    private Long pymtAmt;

    @Column(name = "RCVRY_TYP_SEQ")
    private Long rcvryTypSeq;

    @Column(name = "PYMT_STS_KEY")
    private Long pymtStsKey;

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

    public Long getWrtOfRcvryTrxSeq() {
        return wrtOfRcvryTrxSeq;
    }

    public void setWrtOfRcvryTrxSeq(Long wrtOfRcvryTrxSeq) {
        this.wrtOfRcvryTrxSeq = wrtOfRcvryTrxSeq;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public Long getClntSeq() {
        return clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public Long getPymtModKey() {
        return pymtModKey;
    }

    public void setPymtModKey(Long pymtModKey) {
        this.pymtModKey = pymtModKey;
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

    public Long getPymtStsKey() {
        return pymtStsKey;
    }

    public void setPymtStsKey(Long pymtStsKey) {
        this.pymtStsKey = pymtStsKey;
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
