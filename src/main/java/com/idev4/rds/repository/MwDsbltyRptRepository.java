package com.idev4.rds.repository;

import com.idev4.rds.domain.MwDsbltyRpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MwDsbltyRptRepository extends JpaRepository<MwDsbltyRpt, Long> {

    public MwDsbltyRpt findTopByClntSeqAndCrntRecFlgOrderByDtOfDsbltyDesc(long clntSeq, boolean flag);

    public MwDsbltyRpt findByClntSeqAndCrntRecFlg(long clntSeq, boolean flag);

    @Query(value = "SELECT * FROM MW_DSBLTY_RPT WHERE CLNT_SEQ = :clntSeq AND CRNT_REC_FLG = 1 AND DT_OF_DSBLTY = to_date(:dt, 'dd-MM-yyyy')" +
            " ORDER BY DT_OF_DSBLTY FETCH FIRST 1 ROWS ONLY ", nativeQuery = true)
    public MwDsbltyRpt findTopByClntSeqAndCrntRecFlgAndDtOfDsblty(@Param("clntSeq") long clntSeq, @Param("dt") String dt);
}
