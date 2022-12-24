package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "mw_jv_dtl")
public class MwJvDtl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "jv_dtl_seq")
    private Long jvDtlSeq;

    @Column(name = "jv_hdr_seq")
    private Long jvHdrSeq;

    @Column(name = "crdt_dbt_flg")
    private Long crdtDbtFlg;

    @Column(name = "amt")
    private Long amt;

    @Column(name = "gl_acct_num")
    private String generalLedgerAcct;

    @Column(name = "dscr")
    private String dscr;

    @Column(name = "ln_itm_num")
    private Long lineItemNum;

    public Long getJvDtlSeq() {
        return jvDtlSeq;
    }

    public void setJvDtlSeq(Long jvDtlSeq) {
        this.jvDtlSeq = jvDtlSeq;
    }

    public Long getJvHdrSeq() {
        return jvHdrSeq;
    }

    public void setJvHdrSeq(Long jvHdrSeq) {
        this.jvHdrSeq = jvHdrSeq;
    }

    public Long getCrdtDbtFlg() {
        return crdtDbtFlg;
    }

    public void setCrdtDbtFlg(Long crdtDbtFlg) {
        this.crdtDbtFlg = crdtDbtFlg;
    }

    public Long getAmt() {
        return amt;
    }

    public void setAmt(Long amt) {
        this.amt = amt;
    }

    public String getGeneralLedgerAcct() {
        return generalLedgerAcct;
    }

    public void setGeneralLedgerAcct(String generalLedgerAcct) {
        this.generalLedgerAcct = generalLedgerAcct;
    }

    public String getDscr() {
        return dscr;
    }

    public void setDscr(String dscr) {
        this.dscr = dscr;
    }

    public Long getLineItemNum() {
        return lineItemNum;
    }

    public void setLineItemNum(Long lineItemNum) {
        this.lineItemNum = lineItemNum;
    }

}
