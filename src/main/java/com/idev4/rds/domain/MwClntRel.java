package com.idev4.rds.domain;

import com.idev4.rds.ids.MwClntRelId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MwClntRel.
 */
@Entity
@Table(name = "mw_clnt_rel")
@IdClass(MwClntRelId.class)
public class MwClntRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "clnt_rel_seq")
    private Long clntRelSeq;

    @Id
    @Column(name = "rel_typ_flg")
    private Long relTypFlg;

    @Id
    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "gndr_key")
    private Long gndrKey;

    @Column(name = "occ_key")
    private Long occKey;

    @Column(name = "rel_wth_clnt_key")
    private Long relWthClntKey;

    @Column(name = "mrtl_sts_key")
    private Long mrtlStsKey;

    @Column(name = "res_typ_key")
    private Long resTypKey;

    // @Column(name = "id")
    // private String id;

    @Column(name = "cnic_num")
    private Long cnicNum;

    @Column(name = "cnic_expry_dt")
    private Instant cnicExpryDt;

    @Column(name = "frst_nm")
    private String frstNm;

    @Column(name = "last_nm")
    private String lastNm;

    @Column(name = "co_bwr_san_flg")
    private Boolean coBwrSanFlg;

    @Column(name = "nom_fthr_spz_flg")
    private Boolean nomFthrSpzFlg;

    @Column(name = "dob")
    private Instant dob;

    @Column(name = "ph_num")
    private String phNum;

    @Column(name = "fthr_frst_nm")
    private String fthr_frst_nm;

    @Column(name = "fthr_last_nm")
    private String fthr_last_nm;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove
    // public Long getId() {
    // return id;
    // }
    //
    // public void setId(Long id) {
    // this.id = id;
    // }

    public Long getClntRelSeq() {
        return clntRelSeq;
    }

    public void setClntRelSeq(Long clntRelSeq) {
        this.clntRelSeq = clntRelSeq;
    }

    public MwClntRel clntRelSeq(Long clntRelSeq) {
        this.clntRelSeq = clntRelSeq;
        return this;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public MwClntRel effStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
        return this;
    }

    public Long isRelTypFlg() {
        return relTypFlg;
    }

    public MwClntRel relTypFlg(Long relTypFlg) {
        this.relTypFlg = relTypFlg;
        return this;
    }

    public void setRelTypFlg(Long relTypFlg) {
        this.relTypFlg = relTypFlg;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public MwClntRel loanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
        return this;
    }

    public Long getGndrKey() {
        return gndrKey;
    }

    public void setGndrKey(Long gndrKey) {
        this.gndrKey = gndrKey;
    }

    public MwClntRel gndrKey(Long gndrKey) {
        this.gndrKey = gndrKey;
        return this;
    }

    public Long getOccKey() {
        return occKey;
    }

    public void setOccKey(Long occKey) {
        this.occKey = occKey;
    }

    public MwClntRel occKey(Long occKey) {
        this.occKey = occKey;
        return this;
    }

    public Long getRelWthClntKey() {
        return relWthClntKey;
    }

    public void setRelWthClntKey(Long relWthClntKey) {
        this.relWthClntKey = relWthClntKey;
    }

    public MwClntRel relWthClntKey(Long relWthClntKey) {
        this.relWthClntKey = relWthClntKey;
        return this;
    }

    public Long getMrtlStsKey() {
        return mrtlStsKey;
    }

    public void setMrtlStsKey(Long mrtlStsKey) {
        this.mrtlStsKey = mrtlStsKey;
    }

    public MwClntRel mrtlStsKey(Long mrtlStsKey) {
        this.mrtlStsKey = mrtlStsKey;
        return this;
    }

    public Long getResTypKey() {
        return resTypKey;
    }

    public void setResTypKey(Long resTypKey) {
        this.resTypKey = resTypKey;
    }

    public MwClntRel resTypKey(Long resTypKey) {
        this.resTypKey = resTypKey;
        return this;
    }

    // public String getId() {
    // return id;
    // }

    // public MwClntRel id(String id) {
    // this.id = id;
    // return this;
    // }
    //
    // public void setId(String id) {
    // this.id = id;
    // }

    public Long getCnicNum() {
        return cnicNum;
    }

    public void setCnicNum(Long cnicNum) {
        this.cnicNum = cnicNum;
    }

    public MwClntRel cnicNum(Long cnicNum) {
        this.cnicNum = cnicNum;
        return this;
    }

    public Instant getCnicExpryDt() {
        return cnicExpryDt;
    }

    public void setCnicExpryDt(Instant cnicExpryDt) {
        this.cnicExpryDt = cnicExpryDt;
    }

    public MwClntRel cnicExpryDt(Instant cnicExpryDt) {
        this.cnicExpryDt = cnicExpryDt;
        return this;
    }

    public String getFrstNm() {
        return frstNm;
    }

    public void setFrstNm(String frstNm) {
        this.frstNm = frstNm;
    }

    public MwClntRel frstNm(String frstNm) {
        this.frstNm = frstNm;
        return this;
    }

    public String getLastNm() {
        return lastNm;
    }

    public void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public MwClntRel lastNm(String lastNm) {
        this.lastNm = lastNm;
        return this;
    }

    public Boolean isCoBwrSanFlg() {
        return coBwrSanFlg;
    }

    public MwClntRel coBwrSanFlg(Boolean coBwrSanFlg) {
        this.coBwrSanFlg = coBwrSanFlg;
        return this;
    }

    public void setCoBwrSanFlg(Boolean coBwrSanFlg) {
        this.coBwrSanFlg = coBwrSanFlg;
    }

    public Boolean isNomFthrSpzFlg() {
        return nomFthrSpzFlg;
    }

    public MwClntRel nomFthrSpzFlg(Boolean nomFthrSpzFlg) {
        this.nomFthrSpzFlg = nomFthrSpzFlg;
        return this;
    }

    public void setNomFthrSpzFlg(Boolean nomFthrSpzFlg) {
        this.nomFthrSpzFlg = nomFthrSpzFlg;
    }

    public Instant getDob() {
        return dob;
    }

    public void setDob(Instant dob) {
        this.dob = dob;
    }

    public MwClntRel dob(Instant dob) {
        this.dob = dob;
        return this;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public MwClntRel phNum(String phNum) {
        this.phNum = phNum;
        return this;
    }

    public String getFthr_frst_nm() {
        return fthr_frst_nm;
    }

    public void setFthr_frst_nm(String fthr_frst_nm) {
        this.fthr_frst_nm = fthr_frst_nm;
    }

    public MwClntRel fthr_frst_nm(String fthr_frst_nm) {
        this.fthr_frst_nm = fthr_frst_nm;
        return this;
    }

    public String getFthr_last_nm() {
        return fthr_last_nm;
    }

    public void setFthr_last_nm(String fthr_last_nm) {
        this.fthr_last_nm = fthr_last_nm;
    }

    public MwClntRel fthr_last_nm(String fthr_last_nm) {
        this.fthr_last_nm = fthr_last_nm;
        return this;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public MwClntRel crtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
        return this;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public MwClntRel crtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
        return this;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public MwClntRel lastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
        return this;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public MwClntRel lastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
        return this;
    }

    public Boolean isDelFlg() {
        return delFlg;
    }

    public MwClntRel delFlg(Boolean delFlg) {
        this.delFlg = delFlg;
        return this;
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

    public MwClntRel effEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
        return this;
    }

    public Boolean isCrntRecFlg() {
        return crntRecFlg;
    }

    public MwClntRel crntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
        return this;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here, do not remove

    // @Override
    // public boolean equals(Object o) {
    // if (this == o) {
    // return true;
    // }
    // if (o == null || getClass() != o.getClass()) {
    // return false;
    // }
    // MwClntRel mwClntRel = (MwClntRel) o;
    // if (mwClntRel.getId() == null || getId() == null) {
    // return false;
    // }
    // return Objects.equals(getId(), mwClntRel.getId());
    // }
    //
    // @Override
    // public int hashCode() {
    // return Objects.hashCode(getId());
    // }

    @Override
    public String toString() {
        return "MwClntRel{" +
                // "id=" + getId() +
                ", clntRelSeq=" + getClntRelSeq() + ", effStartDt='" + getEffStartDt() + "'" + ", relTypFlg='" + isRelTypFlg() + "'"
                + ", loanAppSeq=" + getLoanAppSeq() + ", gndrKey=" + getGndrKey() + ", occKey=" + getOccKey() + ", relWthClntKey="
                + getRelWthClntKey() + ", mrtlStsKey=" + getMrtlStsKey() + ", resTypKey=" + getResTypKey() +
                // ", id='" + getId() + "'" +
                ", cnicNum=" + getCnicNum() + ", cnicExpryDt='" + getCnicExpryDt() + "'" + ", frstNm='" + getFrstNm() + "'" + ", lastNm='"
                + getLastNm() + "'" + ", coBwrSanFlg='" + isCoBwrSanFlg() + "'" + ", nomFthrSpzFlg='" + isNomFthrSpzFlg() + "'" + ", dob='"
                + getDob() + "'" + ", phNum='" + getPhNum() + "'" + ", fthr_frst_nm='" + getFthr_frst_nm() + "'" + ", fthr_last_nm='"
                + getFthr_last_nm() + "'" + ", crtdBy='" + getCrtdBy() + "'" + ", crtdDt='" + getCrtdDt() + "'" + ", lastUpdBy='"
                + getLastUpdBy() + "'" + ", lastUpdDt='" + getLastUpdDt() + "'" + ", delFlg='" + isDelFlg() + "'" + ", effEndDt='"
                + getEffEndDt() + "'" + ", crntRecFlg='" + isCrntRecFlg() + "'" + "}";
    }
}
