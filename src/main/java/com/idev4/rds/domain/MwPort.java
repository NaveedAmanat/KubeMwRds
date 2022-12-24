package com.idev4.rds.domain;

import com.idev4.rds.ids.MwPortId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MwPort.
 */
@Entity
@Table(name = "mw_port")
@IdClass(MwPortId.class)
public class MwPort implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "port_SEQ")
    private Long portSeq;

    @Column(name = "port_cd")
    private String portCd;

    @Column(name = "port_nm")
    private String portNm;

    @Column(name = "port_dscr")
    private String portDscr;

    @Column(name = "port_sts_Key")
    private Long portStsKey;

    @Column(name = "brnch_seq")
    private Long brnchSeq;

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

    public Long getPortSeq() {
        return portSeq;
    }

    public void setPortSeq(Long portSeq) {
        this.portSeq = portSeq;
    }

    public String getPortCd() {
        return portCd;
    }

    public void setPortCd(String portCd) {
        this.portCd = portCd;
    }

    public String getPortNm() {
        return portNm;
    }

    public void setPortNm(String portNm) {
        this.portNm = portNm;
    }

    public String getPortDscr() {
        return portDscr;
    }

    public void setPortDscr(String portDscr) {
        this.portDscr = portDscr;
    }

    public Long getPortStsKey() {
        return portStsKey;
    }

    public void setPortStsKey(Long portStsKey) {
        this.portStsKey = portStsKey;
    }

    public Long getBrnchSeq() {
        return brnchSeq;
    }

    public void setBrnchSeq(Long brnchSeq) {
        this.brnchSeq = brnchSeq;
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
