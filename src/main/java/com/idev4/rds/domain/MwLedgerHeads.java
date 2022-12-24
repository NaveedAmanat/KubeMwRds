package com.idev4.rds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ledger_heads")
public class MwLedgerHeads implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "cust_acc_desc")
    private String custAccDesc;

    @Id
    @Column(name = "cust_segments")
    private String custSegments;

    public String getCustAccDesc() {
        return custAccDesc;
    }

    public void setCustAccDesc(String custAccDesc) {
        this.custAccDesc = custAccDesc;
    }

    public String getCustSegments() {
        return custSegments;
    }

    public void setCustSegments(String custSegments) {
        this.custSegments = custSegments;
    }

}
