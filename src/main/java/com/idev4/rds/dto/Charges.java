package com.idev4.rds.dto;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Charges {

    public long inst;

    public String sgrtInst;

    public long roundFlg;

    public double val;

    public long adjFlg;

    public String chrgName;

    public long type;

    public String acct;

    public String typeId;

    public String defAcct;

    public List<Charges> getListofCharge(long appAmt, List<Object[]> charges) {
        long prev = 0;
        List<Charges> chrgList = new ArrayList<Charges>();
        for (Object[] obj : charges) {
            if (prev != new BigDecimal(obj[10].toString()).longValue()) {
                Charges c = new Charges();
                c.inst = obj[1] == null ? 0 : new BigDecimal(obj[1].toString()).longValue();

                c.sgrtInst = obj[2] == null ? "" : obj[2].toString();

                c.roundFlg = obj[3] == null ? 0 : new BigDecimal(obj[3].toString()).longValue();

                if (new BigDecimal(obj[5].toString()).longValue() == 0 && obj[6] == null) {
                    c.val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? Math.ceil((appAmt / 100) * new BigDecimal(obj[7].toString()).doubleValue())
                            : new BigDecimal(obj[7].toString()).doubleValue();
                } else if (new BigDecimal(obj[5].toString()).longValue() == 0
                        && (obj[6] != null && new BigDecimal(obj[6].toString()).longValue() > 0)) {
                    c.val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? Math.ceil(((new BigDecimal(obj[6].toString()).longValue() / 100)
                            * new BigDecimal(obj[7].toString()).doubleValue()))
                            : new BigDecimal(obj[7].toString()).doubleValue();
                    appAmt = appAmt - new BigDecimal(obj[6].toString()).longValue();
                } else if (new BigDecimal(obj[5].toString()).longValue() > 0 && obj[6] == null) {
                    c.val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? Math.ceil(((appAmt / 100) * new BigDecimal(obj[7].toString()).doubleValue()))
                            : new BigDecimal(obj[7].toString()).doubleValue();
                }

                c.adjFlg = obj[8] == null ? 0 : new BigDecimal(obj[8].toString()).longValue();

                c.chrgName = obj[9] == null ? "" : obj[9].toString();

                c.type = obj[10] == null ? 0 : new BigDecimal(obj[10].toString()).longValue();

                c.acct = obj[11] == null ? "" : obj[11].toString();

                c.typeId = obj[12] == null ? "" : obj[12].toString();
                c.defAcct = obj[13] == null ? "" : obj[13].toString();

                chrgList.add(c);

            } else {
                double val = 0;
                if (new BigDecimal(obj[5].toString()).longValue() == 0 && obj[6] == null) {
                    val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? ((appAmt / 100) * new BigDecimal(obj[7].toString()).doubleValue())
                            : new BigDecimal(obj[7].toString()).doubleValue();
                } else if (new BigDecimal(obj[5].toString()).longValue() == 0
                        && (obj[6] != null && new BigDecimal(obj[6].toString()).longValue() > 0)) {
                    val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? ((new BigDecimal(obj[6].toString()).longValue() / 100)
                            * new BigDecimal(obj[7].toString()).doubleValue())
                            : new BigDecimal(obj[7].toString()).doubleValue();
                    appAmt = appAmt - new BigDecimal(obj[6].toString()).longValue();
                } else if (new BigDecimal(obj[5].toString()).longValue() > 0 && obj[6] == null) {
                    val = new BigDecimal(obj[4].toString()).longValue() == 2
                            ? ((appAmt / 100) * new BigDecimal(obj[7].toString()).doubleValue())
                            : new BigDecimal(obj[7].toString()).doubleValue();
                }

                for (Charges c : chrgList) {
                    if (c.type == new BigDecimal(obj[10].toString()).longValue()) {
                        c.val = c.val + val;
                    }
                }

            }
            prev = new BigDecimal(obj[10].toString()).longValue();
        }

        return chrgList;

    }

}
