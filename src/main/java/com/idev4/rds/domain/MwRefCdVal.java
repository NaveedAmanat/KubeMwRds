package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwRefCdVal.
 */
@Entity
@Table(name = "mw_ref_cd_val")
public class MwRefCdVal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ref_cd_seq")
    private Long refCdSeq;

    @Column(name = "ref_cd_grp_key")
    private Long refCdGrpKey;

    @Column(name = "ref_cd")
    private String refCd;

    @Column(name = "ref_cd_dscr")
    private String refCdDscr;

    // @Column(name = "ref_cd_cmnt")
    // private String refCdCmnt;

    @Column(name = "ref_cd_sort_ordr")
    private Long refCdSortOrdr;

    @Column(name = "REF_CD_ACTIVE_FLG")
    private Boolean refCdSts;

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

    public Long getRefCdSeq() {
        return refCdSeq;
    }

    public void setRefCdSeq(Long refCdSeq) {
        this.refCdSeq = refCdSeq;
    }

    public MwRefCdVal refCdSeq(Long refCdSeq) {
        this.refCdSeq = refCdSeq;
        return this;
    }

    public Long getRefCdGrpKey() {
        return refCdGrpKey;
    }

    public void setRefCdGrpKey(Long refCdGrpKey) {
        this.refCdGrpKey = refCdGrpKey;
    }

    public MwRefCdVal refCdGrpKey(Long refCdGrpKey) {
        this.refCdGrpKey = refCdGrpKey;
        return this;
    }

    public String getRefCd() {
        return refCd;
    }

    public void setRefCd(String refCd) {
        this.refCd = refCd;
    }

    public MwRefCdVal refCd(String refCd) {
        this.refCd = refCd;
        return this;
    }

    public String getRefCdDscr() {
        return refCdDscr;
    }

    public void setRefCdDscr(String refCdDscr) {
        this.refCdDscr = refCdDscr;
    }

    public MwRefCdVal refCdDscr(String refCdDscr) {
        this.refCdDscr = refCdDscr;
        return this;
    }

    // public String getRefCdCmnt() {
    // return refCdCmnt;
    // }
    //
    // public MwRefCdVal refCdCmnt(String refCdCmnt) {
    // this.refCdCmnt = refCdCmnt;
    // return this;
    // }

    // public void setRefCdCmnt(String refCdCmnt) {
    // this.refCdCmnt = refCdCmnt;
    // }

    public Long getRefCdSortOrdr() {
        return refCdSortOrdr;
    }

    public void setRefCdSortOrdr(Long refCdSortOrdr) {
        this.refCdSortOrdr = refCdSortOrdr;
    }

    public MwRefCdVal refCdSortOrdr(Long refCdSortOrdr) {
        this.refCdSortOrdr = refCdSortOrdr;
        return this;
    }

    public Boolean getRefCdSts() {
        return refCdSts;
    }

    public void setRefCdSts(Boolean refCdSts) {
        this.refCdSts = refCdSts;
    }

    public MwRefCdVal refCdSts(Boolean refCdSts) {
        this.refCdSts = refCdSts;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwRefCdVal crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwRefCdVal crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwRefCdVal lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwRefCdVal lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwRefCdVal delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
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

    public MwRefCdVal effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public MwRefCdVal effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwRefCdVal crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MwRefCdVal mwRefCdVal = (MwRefCdVal) o;
        if (mwRefCdVal.getRefCdSeq() == null || getRefCdSeq() == null) {
            return false;
        }
        return Objects.equals(getRefCdSeq(), mwRefCdVal.getRefCdSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRefCdSeq());
    }

    @Override
    public String toString() {
        return "MwRefCdVal{" + "id=" + getRefCdSeq() + ", refCdSeq=" + getRefCdSeq() + ", refCdGrpKey=" + getRefCdGrpKey() + ", refCd='"
                + getRefCd() + "'" + ", refCdDscr='" + getRefCdDscr() + "'" + ", refCdSortOrdr=" + getRefCdSortOrdr() + ", refCdSts='"
                + getRefCdSts() + "'" + ", crtdBy='" + getCrtdBy() + "'" + ", crtdDt='" + getCrtdDt() + "'" + ", lastUpdBy='"
                + getLastUpdBy() + "'" + ", lastUpdDt='" + getLastUpdDt() + "'" + ", delFlg='" + isDelFlg() + "'" + ", effStartDt='"
                + getEffStartDt() + "'" + ", effEndDt='" + getEffEndDt() + "'" + ", crntRecFlg='" + isCrntRecFlg() + "'" + "}";
    }
}
