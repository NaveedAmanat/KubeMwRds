package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPdcHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdcHeaderRepository extends JpaRepository<MwPdcHdr, Long> {

    public MwPdcHdr findOneByLoanAppSeqAndCrntRecFlg(long seq, boolean flag);

    public MwPdcHdr findOneByPdcHdrSeqAndCrntRecFlg(long seq, boolean flag);

//    @Modifying
//    @Query ( "update MwPdcHdr pdh set pdh.bankNm = :bankNm and pdh.brnchNm=:brnchNm and pdh.acctNum=:acctNum"
//            + " and pdh.lastUpdBy=:lastUpdBy and pdh.lastUpdDt=:lastUpdDt where pdh.pdcHdrSeq = :id" )
//    int updateMwPdcHdr( @Param ( "bankNm" ) String bankNm, @Param ( "brnchNm" ) String brnchNm, @Param ( "acctNum" ) String acctNum,
//            @Param ( "lastUpdBy" ) String lastUpdBy, @Param ( "lastUpdDt" ) Instant lastUpdDt, @Param ( "id" ) Long id );

}
