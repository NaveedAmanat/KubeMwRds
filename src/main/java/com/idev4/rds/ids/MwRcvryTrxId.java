package com.idev4.rds.ids;

import java.io.Serializable;
import java.time.Instant;

public class MwRcvryTrxId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long rcvryTrxSeq;

    private Instant effStartDt;
}
