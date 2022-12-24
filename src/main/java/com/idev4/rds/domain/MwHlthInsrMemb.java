package com.idev4.rds.domain;

import com.idev4.rds.ids.MwHlthInsrMembId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_HLTH_INSR_MEMB")
@IdClass(MwHlthInsrMembId.class)
public class MwHlthInsrMemb implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "HLTH_INSR_MEMB_SEQ")
    private Long hlthInsrMembSeq;

    @Id
    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    @Column(name = "MEMBER_CNIC_NUM")
    private Long memberCnicNum;

    @Column(name = "MEMBER_NM")
    private String memberNm;

    @Column(name = "MEMBER_ID")
    private String memberId;

    @Column(name = "DOB")
    private Instant dob;

    @Column(name = "GNDR_KEY")
    private Long gndrKey;
    @Column(name = "MRTL_STS_KEY")
    private Long mrtlStsKey;

    @Column(name = "REL_KEY")
    private Long relKey;

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

    public Long getHlthInsrMembSeq() {
        return hlthInsrMembSeq;
    }

    public void setHlthInsrMembSeq(Long hlthInsrMembSeq) {
        this.hlthInsrMembSeq = hlthInsrMembSeq;
    }

    public Long getMemberCnicNum() {
        return memberCnicNum;
    }

    public void setMemberCnicNum(Long memberCnicNum) {
        this.memberCnicNum = memberCnicNum;
    }

    public String getMemberNm() {
        return memberNm;
    }

    public void setMemberNm(String memberNm) {
        this.memberNm = memberNm;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Instant getDob() {
        return dob;
    }

    public void setDob(Instant dob) {
        this.dob = dob;
    }

    public Long getGndrKey() {
        return gndrKey;
    }

    public void setGndrKey(Long gndrKey) {
        this.gndrKey = gndrKey;
    }

    public Long getMrtlStsKey() {
        return mrtlStsKey;
    }

    public void setMrtlStsKey(Long mrtlStsKey) {
        this.mrtlStsKey = mrtlStsKey;
    }

    public Long getRelKey() {
        return relKey;
    }

    public void setRelKey(Long relKey) {
        this.relKey = relKey;
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

}
