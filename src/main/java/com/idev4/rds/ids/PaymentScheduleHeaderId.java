/**
 *
 */

package com.idev4.rds.ids;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Home
 *
 */
public class PaymentScheduleHeaderId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long paySchedHdrSeq;

    private Instant effStartDt;
}
