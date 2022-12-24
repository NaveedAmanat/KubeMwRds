package com.idev4.rds.domain;


import com.idev4.rds.ids.MwRefCdGrpId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MwRefCdGrp.
 */
@Entity
@Table(name = "mw_ref_cd_grp")
@IdClass(MwRefCdGrpId.class)
public class MwRefCdGrp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ref_cd_grp_seq")
    private Long refCdGrpSeq;

    @Column(name = "ref_cd_grp")
    private String refCdGrp;

    @Column(name = "ref_cd_grp_nm")
    private String refCdGrpName;

    @Column(name = "REF_CD_GRP_DSCR")
    private String refCdGrpCmnt;

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

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    public Long getRefCdGrpSeq() {
        return refCdGrpSeq;
    }

    public void setRefCdGrpSeq(Long refCdGrpSeq) {
        this.refCdGrpSeq = refCdGrpSeq;
    }

    public MwRefCdGrp refCdGrpSeq(Long refCdGrpSeq) {
        this.refCdGrpSeq = refCdGrpSeq;
        return this;
    }

    public String getRefCdGrp() {
        return refCdGrp;
    }

    public void setRefCdGrp(String refCdGrp) {
        this.refCdGrp = refCdGrp;
    }

    public MwRefCdGrp refCdGrp(String refCdGrp) {
        this.refCdGrp = refCdGrp;
        return this;
    }

    public String getRefCdGrpName() {
        return refCdGrpName;
    }

    public void setRefCdGrpName(String refCdGrpName) {
        this.refCdGrpName = refCdGrpName;
    }

    public MwRefCdGrp refCdGrpName(String refCdGrpName) {
        this.refCdGrpName = refCdGrpName;
        return this;
    }

    public String getRefCdGrpCmnt() {
        return refCdGrpCmnt;
    }

    public void setRefCdGrpCmnt(String refCdGrpCmnt) {
        this.refCdGrpCmnt = refCdGrpCmnt;
    }

    public MwRefCdGrp refCdGrpCmnt(String refCdGrpCmnt) {
        this.refCdGrpCmnt = refCdGrpCmnt;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwRefCdGrp crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwRefCdGrp crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwRefCdGrp lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwRefCdGrp lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwRefCdGrp delFlg(Boolean delFlg) {
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

    public MwRefCdGrp effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public MwRefCdGrp effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwRefCdGrp crntRecFlg(Boolean crntRecFlg) {
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
        MwRefCdGrp mwRefCdGrp = (MwRefCdGrp) o;
        if (mwRefCdGrp.getRefCdGrpSeq() == null || getRefCdGrpSeq() == null) {
            return false;
        }
        return Objects.equals(getRefCdGrpSeq(), mwRefCdGrp.getRefCdGrpSeq());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRefCdGrpSeq());
    }

    @Override
    public String toString() {
        return "MwRefCdGrp{" +
                "id=" + getRefCdGrpSeq() +
                ", refCdGrpSeq='" + getRefCdGrpSeq() + "'" +
                ", refCdGrp='" + getRefCdGrp() + "'" +
                ", refCdGrpName='" + getRefCdGrpName() + "'" +
                ", refCdGrpCmnt='" + getRefCdGrpCmnt() + "'" +
                ", crtdBy='" + getCrtdBy() + "'" +
                ", crtdDt='" + getCrtdDt() + "'" +
                ", lastUpdBy='" + getLastUpdBy() + "'" +
                ", lastUpdDt='" + getLastUpdDt() + "'" +
                ", delFlg='" + isDelFlg() + "'" +
                ", effStartDt='" + getEffStartDt() + "'" +
                ", effEndDt='" + getEffEndDt() + "'" +
                ", crntRecFlg='" + isCrntRecFlg() + "'" +
                "}";
    }
}
