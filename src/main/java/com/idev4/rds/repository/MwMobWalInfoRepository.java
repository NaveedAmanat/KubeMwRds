package com.idev4.rds.repository;

import com.idev4.rds.domain.MwMobWalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Added By Naveed - Date - 23-01-2022
 * SCR - Mobile Wallet Control
 * */

@Repository
public interface MwMobWalInfoRepository extends JpaRepository<MwMobWalInfo, Long> {

    List<MwMobWalInfo> findAllByMobWalNoAndCrntRecFlgAndClntSeqNotIn(String mobWaltNo, boolean flag, long clntSeq);

    List<MwMobWalInfo> findAllByClntSeqAndCrntRecFlgOrderByMobWalChnlAscCrtdDtDesc(long clntSeq, boolean flag);

    MwMobWalInfo findByClntSeqAndMobWalChnlAndMobWalNoAndCrntRecFlg(long clntSeq, String mobWalChnl, String mobWalNo, boolean flag);


    @Query(value = "SELECT INFO.*\n" +
            "  FROM MW_LOAN_APP  APP\n" +
            "       JOIN MW_DSBMT_VCHR_HDR HDR\n" +
            "           ON     HDR.LOAN_APP_SEQ = APP.LOAN_APP_SEQ\n" +
            "              AND HDR.CRNT_REC_FLG = 1\n" +
            "       JOIN MW_MOB_WAL_INFO INFO\n" +
            "           ON     INFO.MOB_WAL_SEQ = HDR.MOB_WAL_SEQ\n" +
            "              AND HDR.CRNT_REC_FLG = 1\n" +
            " WHERE APP.CRNT_REC_FLG = 1 AND APP.LOAN_APP_SEQ = :loanAppSeq", nativeQuery = true)
    MwMobWalInfo findMobInfoByLoanApp(@Param("loanAppSeq") long loanAppSeq);

    MwMobWalInfo findByMobWalSeqAndCrntRecFlg(long mobWalSeq, boolean flag);

    // Added BY Naveed - Date - 24-02-2022
    // Upaisa and HBL Konnect Mobile wallet Payment Mode
    List<MwMobWalInfo> findAllByAtmCardNoAndCrntRecFlgAndClntSeqNotIn(String atm, boolean flag, long clntSeq);
    // Ended By Naveed
}
