package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPdcHdrId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "mw_pdc_hdr")
@IdClass(MwPdcHdrId.class)
public class MwPdcHdr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pdc_hdr_seq")
    private Long pdcHdrSeq;

    @Column(name = "bank_key")
    private Long bankKey;

    @Column(name = "brnch_nm")
    private String brnchNm;

    @Column(name = "acct_num")
    private String acctNum;

    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

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

    public MwPdcHdr(Long pdcHdrSeq, Long bankKey, String brnchNm, String acctNum, Long loanAppSeq, Instant effStartDt, Instant effEndDt,
                    String crtdBy, Instant crtdDt, String lastUpdBy, Instant lastUpdDt, Boolean delFlg, Boolean crntRecFlg) {
        super();
        this.pdcHdrSeq = pdcHdrSeq;
        this.bankKey = bankKey;
        this.brnchNm = brnchNm;
        this.acctNum = acctNum;
        this.loanAppSeq = loanAppSeq;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
        this.crtdBy = crtdBy;
        this.crtdDt = crtdDt;
        this.lastUpdBy = lastUpdBy;
        this.lastUpdDt = lastUpdDt;
        this.delFlg = delFlg;
        this.crntRecFlg = crntRecFlg;
    }

    public MwPdcHdr() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Long getPdcHdrSeq() {
        return pdcHdrSeq;
    }

    public void setPdcHdrSeq(Long pdcHdrSeq) {
        this.pdcHdrSeq = pdcHdrSeq;
    }

    public Long getBankKey() {
        return bankKey;
    }

    public void setBankKey(Long bankKey) {
        this.bankKey = bankKey;
    }

    public String getBrnchNm() {
        return brnchNm;
    }

    public void setBrnchNm(String brnchNm) {
        this.brnchNm = brnchNm;
    }

    public String getAcctNum() {
        return acctNum;
    }

    public void setAcctNum(String acctNum) {
        this.acctNum = acctNum;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
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

    public MwPdcHdr pdcHdrSeq(Long pdcHdrSeq) {
        this.pdcHdrSeq = pdcHdrSeq;
        return this;
    }

    public MwPdcHdr bankNm(Long bankKey) {
        this.bankKey = bankKey;
        return this;
    }

    public MwPdcHdr brnchNm(String brnchNm) {
        this.brnchNm = brnchNm;
        return this;
    }

    public MwPdcHdr acctNum(String acctNum) {
        this.acctNum = acctNum;
        return this;
    }

    public MwPdcHdr loanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
        return this;
    }

    public MwPdcHdr effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public MwPdcHdr effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public MwPdcHdr lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public MwPdcHdr crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public MwPdcHdr crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public MwPdcHdr lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public MwPdcHdr delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
    }

    public MwPdcHdr crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    @Override
    public String toString() {
        return "MwPdcHdr [pdcHdrSeq=" + pdcHdrSeq + ", bankKey=" + bankKey + ", brnchNm=" + brnchNm + ", acctNum=" + acctNum
                + ", loanAppSeq=" + loanAppSeq + ", effStartDt=" + effStartDt + ", effEndDt=" + effEndDt + ", crtdBy=" + crtdBy
                + ", crtdDt=" + crtdDt + ", lastUpdBy=" + lastUpdBy + ", lastUpdDt=" + lastUpdDt + ", delFlg=" + delFlg + ", crntRecFlg="
                + crntRecFlg + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((acctNum == null) ? 0 : acctNum.hashCode());
        result = prime * result + ((bankKey == null) ? 0 : bankKey.hashCode());
        result = prime * result + ((brnchNm == null) ? 0 : brnchNm.hashCode());
        result = prime * result + ((crntRecFlg == null) ? 0 : crntRecFlg.hashCode());
        result = prime * result + ((crtdBy == null) ? 0 : crtdBy.hashCode());
        result = prime * result + ((crtdDt == null) ? 0 : crtdDt.hashCode());
        result = prime * result + ((delFlg == null) ? 0 : delFlg.hashCode());
        result = prime * result + ((effEndDt == null) ? 0 : effEndDt.hashCode());
        result = prime * result + ((effStartDt == null) ? 0 : effStartDt.hashCode());
        result = prime * result + ((lastUpdBy == null) ? 0 : lastUpdBy.hashCode());
        result = prime * result + ((lastUpdDt == null) ? 0 : lastUpdDt.hashCode());
        result = prime * result + ((loanAppSeq == null) ? 0 : loanAppSeq.hashCode());
        result = prime * result + ((pdcHdrSeq == null) ? 0 : pdcHdrSeq.hashCode());
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
        MwPdcHdr other = (MwPdcHdr) obj;
        if (acctNum == null) {
            if (other.acctNum != null)
                return false;
        } else if (!acctNum.equals(other.acctNum))
            return false;
        if (bankKey == null) {
            if (other.bankKey != null)
                return false;
        } else if (!bankKey.equals(other.bankKey))
            return false;
        if (brnchNm == null) {
            if (other.brnchNm != null)
                return false;
        } else if (!brnchNm.equals(other.brnchNm))
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
        if (loanAppSeq == null) {
            if (other.loanAppSeq != null)
                return false;
        } else if (!loanAppSeq.equals(other.loanAppSeq))
            return false;
        if (pdcHdrSeq == null) {
            if (other.pdcHdrSeq != null)
                return false;
        } else if (!pdcHdrSeq.equals(other.pdcHdrSeq))
            return false;
        return true;
    }

}
