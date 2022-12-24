package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "mw_rcvry_load_stg")
public class MwRcvryLoadStg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rcvry_load_stg_seq")
    private Long rcvryLoadStgSeq;

    @Column(name = "clnt_id")
    private String clntId;

    @Column(name = "trx_id")
    private String trxId;

    @Column(name = "trx_dt")
    private Date trxDt;

    @Column(name = "amt")
    private Long amt;

    @Column(name = "trx_sts_key")
    private Boolean trxStsKey;

    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "load_dt")
    private Instant loadDt;

    @Column(name = "cmnt")
    private String cmnt;

    public Long getRcvryLoadStgSeq() {
        return rcvryLoadStgSeq;
    }

    public void setRcvryLoadStgSeq(Long rcvryLoadStgSeq) {
        this.rcvryLoadStgSeq = rcvryLoadStgSeq;
    }

    public String getClntId() {
        return clntId;
    }

    public void setClntId(String clntId) {
        this.clntId = clntId;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public Date getTrxDt() {
        return trxDt;
    }

    public void setTrxDt(Date trxDt) {
        this.trxDt = trxDt;
    }

    public Long getAmt() {
        return amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
    }

    public Boolean getTrxStsKey() {
        return trxStsKey;
    }

    public void setTrxStsKey(Boolean trxStsKey) {
        this.trxStsKey = trxStsKey;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Instant getLoadDt() {
        return loadDt;
    }

    public void setLoadDt(Instant loadDt) {
        this.loadDt = loadDt;
    }

    public String getCmnt() {
        return cmnt;
    }

    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }

}
