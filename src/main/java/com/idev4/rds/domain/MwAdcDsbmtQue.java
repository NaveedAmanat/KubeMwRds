package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "MW_ADC_DSBMT_QUE")
public class MwAdcDsbmtQue {
    @Id
    @Column(name = "DSBMT_DTL_KEY")
    private Long dsbmtDtlKey;

    @Column(name = "DSBMT_HDR_SEQ")
    private Long dsbmtHdrSeq;

    @Column(name = "DSBMT_STS_SEQ")
    private Long dsbmtStsSeq;

    @Column(name = "DSBMT_STS_DT")
    private Date dsbmtStsDt;

    @Column(name = "ADC_STS_SEQ")
    private Long adcStsSeq;

    @Column(name = "ADC_STS_DT")
    private Date adcStsDt;

    @Column(name = "IS_PROCESSED")
    private Boolean isProcessed;

    @Column(name = "CRNT_REC_FLG")
    private Boolean crntRecFlg;

    @Column(name = "CRTD_DT")
    private Date crtdDt;

    @Column(name = "CRTD_BY")
    private String crtdBy;

    @Column(name = "LAST_UPD_DT")
    private Date lastUpdDt;

    @Column(name = "LAST_UPD_BY")
    private String lastUpdBy;

    public Long getDsbmtDtlKey() {
        return this.dsbmtDtlKey;
    }

    public void setDsbmtDtlKey(Long dsbmtDtlKey) {
        this.dsbmtDtlKey = dsbmtDtlKey;
    }

    public Long getDsbmtHdrSeq() {
        return this.dsbmtHdrSeq;
    }

    public void setDsbmtHdrSeq(Long dsbmtHdrSeq) {
        this.dsbmtHdrSeq = dsbmtHdrSeq;
    }

    public Long getDsbmtStsSeq() {
        return this.dsbmtStsSeq;
    }

    public void setDsbmtStsSeq(Long dsbmtStsSeq) {
        this.dsbmtStsSeq = dsbmtStsSeq;
    }

    public Date getDsbmtStsDt() {
        return this.dsbmtStsDt;
    }

    public void setDsbmtStsDt(Date dsbmtStsDt) {
        this.dsbmtStsDt = dsbmtStsDt;
    }

    public Long getAdcStsSeq() {
        return this.adcStsSeq;
    }

    public void setAdcStsSeq(Long adcStsSeq) {
        this.adcStsSeq = adcStsSeq;
    }

    public Date getAdcStsDt() {
        return this.adcStsDt;
    }

    public void setAdcStsDt(Date adcStsDt) {
        this.adcStsDt = adcStsDt;
    }

    public Boolean getIsProcessed() {
        return this.isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Boolean getCrntRecFlg() {
        return this.crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Date getCrtdDt() {
        return this.crtdDt;
    }

    public void setCrtdDt(Date crtdDt) {
        this.crtdDt = crtdDt;
    }

    public String getCrtdBy() {
        return this.crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Date getLastUpdDt() {
        return this.lastUpdDt;
    }

    public void setLastUpdDt(Date lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public String getLastUpdBy() {
        return this.lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }
}
