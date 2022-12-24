package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "MW_LOAN_APP_PPAL_STNGS")
public class MwLoanAppPpalStngs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOAN_APP_PPAL_STNGS_SEQ")
    private Long loanAppPpalStngsSeq;

    @Column(name = "loan_app_seq")
    private Long loanAppSeq;

    @Column(name = "PRD_SEQ")
    private Long prdSeq;

    @Column(name = "NUM_OF_INST")
    private Long numOfInst;

    @Column(name = "PYMT_FREQ")
    private Long pymtFreq;

    @Column(name = "NUM_OF_INST_SGRT")
    private Long numOfInstSgrt;

    @Column(name = "SGRT_INST")
    private String sgrtInst;

    @Column(name = "IRR_FLG")
    private Long irrFlg;

    @Column(name = "IRR_RATE")
    private Double irrRate;

    @Column(name = "RNDNG_SCL")
    private Long rndngScl;

    @Column(name = "RNDNG_ADJ_INST")
    private Long rndngAdjInst;

    public Long getLoanAppPpalStngsSeq() {
        return loanAppPpalStngsSeq;
    }

    public void setLoanAppPpalStngsSeq(Long loanAppPpalStngsSeq) {
        this.loanAppPpalStngsSeq = loanAppPpalStngsSeq;
    }

    public Long getPrdSeq() {
        return prdSeq;
    }

    public void setPrdSeq(Long prdSeq) {
        this.prdSeq = prdSeq;
    }

    public Long getNumOfInst() {
        return numOfInst;
    }

    public void setNumOfInst(Long numOfInst) {
        this.numOfInst = numOfInst;
    }

    public Long getPymtFreq() {
        return pymtFreq;
    }

    public void setPymtFreq(Long pymtFreq) {
        this.pymtFreq = pymtFreq;
    }

    public Long getNumOfInstSgrt() {
        return numOfInstSgrt;
    }

    public void setNumOfInstSgrt(Long numOfInstSgrt) {
        this.numOfInstSgrt = numOfInstSgrt;
    }

    public String getSgrtInst() {
        return sgrtInst;
    }

    public void setSgrtInst(String sgrtInst) {
        this.sgrtInst = sgrtInst;
    }

    public Long getIrrFlg() {
        return irrFlg;
    }

    public void setIrrFlg(Long irrFlg) {
        this.irrFlg = irrFlg;
    }

    public Double getIrrRate() {
        return irrRate;
    }

    public void setIrrRate(Double irrRate) {
        this.irrRate = irrRate;
    }

    public Long getRndngScl() {
        return rndngScl;
    }

    public void setRndngScl(Long rndngScl) {
        this.rndngScl = rndngScl;
    }

    public Long getRndngAdjInst() {
        return rndngAdjInst;
    }

    public void setRndngAdjInst(Long rndngAdjInst) {
        this.rndngAdjInst = rndngAdjInst;
    }

    public Long getLoanAppSeq() {
        return loanAppSeq;
    }

    public void setLoanAppSeq(Long loanAppSeq) {
        this.loanAppSeq = loanAppSeq;
    }

}
