package com.idev4.rds.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

@Component
public class StaticValusOnStartup {

    public static long ADVANCE = 0;

    public static long DELIQUENCY = 0;

    public static long SAMEDAY = 0;

    public static long PARTIAL = 0;

    @Autowired
    EntityManager em;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

        List<Object[]> instStss = em.createNativeQuery("select val.ref_cd_seq, val.ref_cd\r\n" + "from mw_ref_cd_val val\r\n"
                + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 \r\n"
                + "where val.crnt_rec_flg=1 and grp.ref_cd_grp='0179'").getResultList();

        for (Object[] v : instStss) {
            if (v[1].toString().equals("0946")) {
                SAMEDAY = v[0] == null ? 0L : new BigDecimal(v[0].toString()).longValue();
            }
            if (v[1].toString().equals("0947")) {
                ADVANCE = v[0] == null ? 0L : new BigDecimal(v[0].toString()).longValue();
            }
            if (v[1].toString().equals("0948")) {
                DELIQUENCY = v[0] == null ? 0L : new BigDecimal(v[0].toString()).longValue();
            }
            if (v[1].toString().equals("1145")) {
                PARTIAL = v[0] == null ? 0L : new BigDecimal(v[0].toString()).longValue();
            }
        }
    }

}
