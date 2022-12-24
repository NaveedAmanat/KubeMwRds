package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "MW_AUTO_LIST_STATUS")
public class MwAutoListStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "AUTO_LIST_STATUS_SEQ")
    private long autoListStatusSeq;

    @Column(name = "CRNT_DATE")
    private Instant crntDate;

    @Column(name = "NO_OF_RECORD")
    private Long noOfRecord;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "type")
    private String type;

    public MwAutoListStatus() {
    }

    public MwAutoListStatus(long autoListStatusSeq, Instant crntDate, Long noOfRecord, String status, String type) {
        this.autoListStatusSeq = autoListStatusSeq;
        this.crntDate = crntDate;
        this.noOfRecord = noOfRecord;
        this.status = status;
        this.type = type;
    }

    public long getAutoListStatusSeq() {
        return autoListStatusSeq;
    }

    public void setAutoListStatusSeq(long autoListStatusSeq) {
        this.autoListStatusSeq = autoListStatusSeq;
    }

    public Instant getCrntDate() {
        return crntDate;
    }

    public void setCrntDate(Instant crntDate) {
        this.crntDate = crntDate;
    }

    public Long getNoOfRecord() {
        return noOfRecord;
    }

    public void setNoOfRecord(Long noOfRecord) {
        this.noOfRecord = noOfRecord;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
