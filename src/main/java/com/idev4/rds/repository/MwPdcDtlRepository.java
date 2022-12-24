package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPdcDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MwPdcDtlRepository extends JpaRepository<MwPdcDtl, Long> {

    public List<MwPdcDtl> findAllByPdcHdrSeqAndCrntRecFlgOrderByPdcIdAsc(long pdcHdrSeq, boolean flag);

    // @Modifying
    // @Query ( "update MwPdcDtl pd set pd.pdcId = :pdcId and pd.collDt=:collDt and pd.chqqNum=:chqqNum and pd.amt=:amt"
    // + " and pd.lastUpdBy=:lastUpdBy and pd.lastUpdDt=:lastUpdDt where pd.pdcDtlSeq = :id" )
    // int updatePdcDetail( @Param ( "pdcId" ) String pdcId, @Param ( "collDt" ) Instant collDt, @Param ( "chqqNum" ) String chqqNum,
    // @Param ( "amt" ) Long amt, @Param ( "lastUpdBy" ) String lastUpdBy, @Param ( "lastUpdDt" ) Instant lastUpdDt,
    // @Param ( "id" ) Long id );

    // @Modifying
    // @Query ( "update MwPdcDtl pd set pd.delFlg = :delFlg and pd.lastUpdBy=:lastUpdBy and pd.lastUpdDt=:lastUpdDt where pd.pdcDtlSeq =
    // :id" )
    // int deletePdcDetail( @Param ( "delFlg" ) Boolean delFlg, @Param ( "lastUpdBy" ) String lastUpdBy,
    // @Param ( "lastUpdDt" ) Instant lastUpdDt, @Param ( "id" ) Long id );

    MwPdcDtl findOneByPdcDtlSeqAndCrntRecFlg(long seq, boolean flag);

    public List<MwPdcDtl> findAllByPdcHdrSeqAndCrntRecFlg(Long pdcHdrSeq, boolean b);

}
