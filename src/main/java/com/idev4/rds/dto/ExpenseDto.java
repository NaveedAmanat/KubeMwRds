package com.idev4.rds.dto;

public class ExpenseDto {

    public String rmrks;
    private Long expSeq;
    private Long pymtTypSeq;
    private Long brnchSeq;
    private Long expnsStsKey;
    private String expnsId;
    private String expnsDscr;
    private String instrNum;
    private Long expnsAmt;
    private Long expnsTypSeq;
    private String expRef;
    private Long pymtRctFlg;

    public String getExpRef() {
        return expRef;
    }

    public void setExpRef(String expRef) {
        this.expRef = expRef;
    }

    public Long getPymtTypSeq() {
        return pymtTypSeq;
    }

    public void setPymtTypSeq(Long pymtTypSeq) {
        this.pymtTypSeq = pymtTypSeq;
    }

    public Long getExpSeq() {
        return expSeq;
    }

    public void setExpSeq(Long expSeq) {
        this.expSeq = expSeq;
    }

    public Long getBrnchSeq() {
        return brnchSeq;
    }

    public void setBrnchSeq(Long brnchSeq) {
        this.brnchSeq = brnchSeq;
    }

    public Long getExpnsStsKey() {
        return expnsStsKey;
    }

    public void setExpnsStsKey(Long expnsStsKey) {
        this.expnsStsKey = expnsStsKey;
    }

    public String getExpnsId() {
        return expnsId;
    }

    public void setExpnsId(String expnsId) {
        this.expnsId = expnsId;
    }

    public String getExpnsDscr() {
        return expnsDscr;
    }

    public void setExpnsDscr(String expnsDscr) {
        this.expnsDscr = expnsDscr;
    }

    public String getInstrNum() {
        return instrNum;
    }

    public void setInstrNum(String instrNum) {
        this.instrNum = instrNum;
    }

    public Long getExpnsAmt() {
        return expnsAmt;
    }

    public void setExpnsAmt(Long expnsAmt) {
        this.expnsAmt = expnsAmt;
    }

    public Long getExpnsTypSeq() {
        return expnsTypSeq;
    }

    public void setExpnsTypSeq(Long expnsTypSeq) {
        this.expnsTypSeq = expnsTypSeq;
    }

    public Long getPymtRctFlg() {
        return pymtRctFlg;
    }

    public void setPymtRctFlg(Long pymtRctFlg) {
        this.pymtRctFlg = pymtRctFlg;
    }

}
