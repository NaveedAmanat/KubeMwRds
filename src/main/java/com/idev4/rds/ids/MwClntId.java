package com.idev4.rds.ids;

import java.io.Serializable;
import java.time.Instant;

/**
 * A MwClnt.
 */
public class MwClntId implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long clntSeq;

    public Instant effStartDt;

}
