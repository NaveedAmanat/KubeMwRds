package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "mw_jv_hdr")
public class MwJvHdr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "jv_hdr_seq")
    private Long jvHdrSeq;

    @Column(name = "rcvry_trx_seq")
    private Long rcvryTrxSeq;

    @Column(name = "prnt_vchr_ref")
    private Long prntVchrRef;

    @Column(name = "jv_id")
    private String jvId;

    @Column(name = "jv_dt")
    private Instant jvDt;

    @Column(name = "jv_dscr")
    private String jvDscr;

    @Column(name = "jv_typ_key")
    private Long jvTypKey;

    @Column(name = "enty_seq")
    private Long entySeq;

    @Column(name = "enty_typ")
    private String entyTyp;

    @Column(name = "crtd_by")
    private String crtdBy;

    @Column(name = "post_flg")
    private Long postFlg;

    @Column(name = "brnch_seq")
    private Long brnchSeq;

    public Long getJvHdrSeq() {
        return jvHdrSeq;
    }

    public void setJvHdrSeq(Long jvHdrSeq) {
        this.jvHdrSeq = jvHdrSeq;
    }

    public Long getPrntVchrRef() {
        return prntVchrRef;
    }

    public void setPrntVchrRef(Long prntVchrRef) {
        this.prntVchrRef = prntVchrRef;
    }

    public String getJvId() {
        return jvId;
    }

    public void setJvId(String jvId) {
        this.jvId = jvId;
    }

    public Instant getJvDt() {
        return jvDt;
    }

    public void setJvDt(Instant jvDt) {
        this.jvDt = jvDt;
    }

    public String getJvDscr() {
        return jvDscr;
    }

    public void setJvDscr(String jvDscr) {
        this.jvDscr = jvDscr;
    }

    public Long getJvTypKey() {
        return jvTypKey;
    }

    public void setJvTypKey(Long jvTypKey) {
        this.jvTypKey = jvTypKey;
    }

    public Long getEntySeq() {
        return entySeq;
    }

    public void setEntySeq(Long entySeq) {
        this.entySeq = entySeq;
    }

    public String getEntyTyp() {
        return entyTyp;
    }

    public void setEntyTyp(String entyTyp) {
        this.entyTyp = entyTyp;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Long getPostFlg() {
        return postFlg;
    }

    public void setPostFlg(Long postFlg) {
        this.postFlg = postFlg;
    }

    public Long getRcvryTrxSeq() {
        return rcvryTrxSeq;
    }

    public void setRcvryTrxSeq(Long rcvryTrxSeq) {
        this.rcvryTrxSeq = rcvryTrxSeq;
    }

    public Long getBrnchSeq() {
        return brnchSeq;
    }

    public void setBrnchSeq(Long brnchSeq) {
        this.brnchSeq = brnchSeq;
    }

}
