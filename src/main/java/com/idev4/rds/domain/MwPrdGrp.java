package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPrdGrpId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MwPrdGrp.
 */
@Entity
@Table(name = "mw_Prd_Grp")
@IdClass(MwPrdGrpId.class)

public class MwPrdGrp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prd_grp_seq")
    private Long prdGrpSeq;

    @Column(name = "prd_grp_id")
    private String prdGrpId;

    @Column(name = "prd_grp_nm")
    private String prdGrpNm;

    @Column(name = "prd_grp_sts")
    private Long prdGrpSts;

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

    public Long getPrdGrpSeq() {
        return prdGrpSeq;
    }

    public void setPrdGrpSeq(Long prdGrpSeq) {
        this.prdGrpSeq = prdGrpSeq;
    }

    public String getPrdGrpId() {
        return prdGrpId;
    }

    public void setPrdGrpId(String prdGrpId) {
        this.prdGrpId = prdGrpId;
    }

    public String getPrdGrpNm() {
        return prdGrpNm;
    }

    public void setPrdGrpNm(String prdGrpNm) {
        this.prdGrpNm = prdGrpNm;
    }

    public Long getPrdGrpSts() {
        return prdGrpSts;
    }

    public void setPrdGrpSts(Long prdGrpSts) {
        this.prdGrpSts = prdGrpSts;
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

}
