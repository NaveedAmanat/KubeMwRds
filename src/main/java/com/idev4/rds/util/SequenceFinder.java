package com.idev4.rds.util;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
@Transactional
public class SequenceFinder {

    private static EntityManager em;

    public SequenceFinder(EntityManager em) {
        SequenceFinder.em = em;
    }

    public static long findNextVal(String sequence) {
        Query q = em.createNativeQuery("select " + sequence + ".nextval from dual");
        long seq = Long.valueOf(q.getSingleResult().toString());
        return seq;
    }
}
