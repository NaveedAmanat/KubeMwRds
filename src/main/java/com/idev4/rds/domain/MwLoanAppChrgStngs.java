package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "MW_LOAN_APP_CHRG_STNGS")
public class MwLoanAppChrgStngs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOAN_APP_CHRG_STNGS_SEQ")
    private Long loanAppChrgStngsSeq;

    @Column(name = "LOAN_APP_SEQ")
    private Long loanAppSeq;

    @Column(name = "PRD_SEQ")
    private Long prdSeq;

    @Column(name = "PRD_CHRG_SEQ")
    private Long prdChrgSeq;

    @Column(name = "NUM_OF_INST_SGRT")
    private Long numOfInstSgrt;

    @Column(name = "SGRT_INST")
    private Long sgrtInst;

    @Column(name = "RNDNG_FLG")
    private Long rndngFlg;

    @Column(name = "UPFRONT_FLG")
    private Long upfrontFlg;

    public Long getLoanAppChrgStngsSeq() {
        return loanAppChrgStngsSeq;
    }

    public void setLoanAppChrgStngsSeq(Long loanAppChrgStngsSeq) {
        this.loanAppChrgStngsSeq = loanAppChrgStngsSeq;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public Long getPrdChrgSeq() {
        return prdChrgSeq;
    }

    public void setPrdChrgSeq(Long prdChrgSeq) {
        this.prdChrgSeq = prdChrgSeq;
    }

    public Long getNumOfInstSgrt() {
        return numOfInstSgrt;
    }

    public void setNumOfInstSgrt(Long numOfInstSgrt) {
        this.numOfInstSgrt = numOfInstSgrt;
    }

    public Long getSgrtInst() {
        return sgrtInst;
    }

    public void setSgrtInst(Long sgrtInst) {
        this.sgrtInst = sgrtInst;
    }

    public Long getRndngFlg() {
        return rndngFlg;
    }

    public void setRndngFlg(Long rndngFlg) {
        this.rndngFlg = rndngFlg;
    }

    public Long getUpfrontFlg() {
        return upfrontFlg;
    }

    public void setUpfrontFlg(Long upfrontFlg) {
        this.upfrontFlg = upfrontFlg;
    }

}
