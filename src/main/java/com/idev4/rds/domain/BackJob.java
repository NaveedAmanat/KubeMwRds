package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "BCK_JOB")
public class BackJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "job_seq")
    private Long jobSeq;

    @Column(name = "job_dscr")
    private String jobDscr;

    @Column(name = "strt_dt")
    private Instant strtDt;

    @Column(name = "end_dt")
    private Instant endDt;

    @Column(name = "sts")
    private Integer sts;

    @Column(name = "tot_rec")
    private Integer totRec;

    @Column(name = "rj_rec")
    private Integer rjRec;

    public Long getJobSeq() {
        return jobSeq;
    }

    public void setJobSeq(Long jobSeq) {
        this.jobSeq = jobSeq;
    }

    public String getJobDscr() {
        return jobDscr;
    }

    public void setJobDscr(String jobDscr) {
        this.jobDscr = jobDscr;
    }

    public Instant getStrtDt() {
        return strtDt;
    }

    public void setStrtDt(Instant strtDt) {
        this.strtDt = strtDt;
    }

    public Instant getEndDt() {
        return endDt;
    }

    public void setEndDt(Instant endDt) {
        this.endDt = endDt;
    }

    public Integer getSts() {
        return sts;
    }

    public void setSts(Integer sts) {
        this.sts = sts;
    }

    public Integer getTotRec() {
        return totRec;
    }

    public void setTotRec(Integer totRec) {
        this.totRec = totRec;
    }

    public Integer getRjRec() {
        return rjRec;
    }

    public void setRjRec(Integer rjRec) {
        this.rjRec = rjRec;
    }

}
