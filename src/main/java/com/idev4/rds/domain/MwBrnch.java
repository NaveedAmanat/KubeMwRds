package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwBrnch.
 */
@Entity
@Table(name = "mw_brnch")
public class MwBrnch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "brnch_seq")
    private Long brnchSeq;

    @Column(name = "brnch_cd")
    private String brnchCd;

    @Column(name = "brnch_nm")
    private String brnchNm;

    @Column(name = "brnch_dscr")
    private String brnchDscr;

    @Column(name = "brnch_sts_key")
    private Long brnchStsKet;

    @Column(name = "brnch_typ_key")
    private Long brnchTypKet;

    @Column(name = "brnch_ph_num")
    private String brnchPhNum;

    @Column(name = "area_seq")
    private Long areaSeq;

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

    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    public Long getBrnchSeq() {
        return brnchSeq;
    }

    public void setBrnchSeq(Long brnchSeq) {
        this.brnchSeq = brnchSeq;
    }

    public String getBrnchCd() {
        return brnchCd;
    }

    public void setBrnchCd(String brnchCd) {
        this.brnchCd = brnchCd;
    }

    public String getBrnchNm() {
        return brnchNm;
    }

    public void setBrnchNm(String brnchNm) {
        this.brnchNm = brnchNm;
    }

    public String getBrnchDscr() {
        return brnchDscr;
    }

    public void setBrnchDscr(String brnchDscr) {
        this.brnchDscr = brnchDscr;
    }

    public Long getBrnchStsKet() {
        return brnchStsKet;
    }

    public void setBrnchStsKet(Long brnchStsKet) {
        this.brnchStsKet = brnchStsKet;
    }

    public Long getBrnchTypKet() {
        return brnchTypKet;
    }

    public void setBrnchTypKet(Long brnchTypKet) {
        this.brnchTypKet = brnchTypKet;
    }

    public String getBrnchPhNum() {
        return brnchPhNum;
    }

    public void setBrnchPhNum(String brnchPhNum) {
        this.brnchPhNum = brnchPhNum;
    }

    public Long getAreaSeq() {
        return areaSeq;
    }

    public void setAreaSeq(Long areaSeq) {
        this.areaSeq = areaSeq;
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

    public Boolean getCrntRecFlg() {
        return crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MwBrnch mwBrnch = (MwBrnch) o;
        if (mwBrnch.getBrnchSeq() == null || getBrnchSeq() == null) {
            return false;
        }
        return Objects.equals(getBrnchSeq(), mwBrnch.getBrnchSeq());
    }

}
