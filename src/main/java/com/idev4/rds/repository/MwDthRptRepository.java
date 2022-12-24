package com.idev4.rds.repository;

import com.idev4.rds.domain.MwDthRpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MwDthRptRepository extends JpaRepository<MwDthRpt, Long> {

    public MwDthRpt findTopByClntSeqAndCrntRecFlgOrderByDtOfDthDesc(long clntSeq, boolean flag);

    //Added by Areeba
    @Query(value = "SELECT * FROM MW_DTH_RPT WHERE CLNT_SEQ = :clntSeq AND CRNT_REC_FLG = 1 AND DT_OF_DTH = to_date(:dt, 'dd-MM-yyyy')" +
            " ORDER BY DT_OF_DTH FETCH FIRST 1 ROWS ONLY ", nativeQuery = true)
    public MwDthRpt findTopByClntSeqAndCrntRecFlgAndDtOfDth(@Param("clntSeq") long clntSeq, @Param("dt") String dt);
    //Ended by Areeba
}
