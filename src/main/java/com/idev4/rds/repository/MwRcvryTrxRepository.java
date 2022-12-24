package com.idev4.rds.repository;

import com.idev4.rds.domain.MwRcvryTrx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface MwRcvryTrxRepository extends JpaRepository<MwRcvryTrx, Long> {

    @Query(value = "select t.gl_acct_num\r\n" + "from mw_rcvry_trx rt\r\n"
            + "join mw_typs t on t.typ_seq=rt.rcvry_typ_seq and t.crnt_rec_flg =1\r\n"
            + "where rt.rcvry_trx_seq =? and rt.crnt_rec_flg =1", nativeQuery = true)
    public Map<String, ?> getGlAcctNumByRcvryTrxSeq(long rcvryTrxSeq);

    public MwRcvryTrx findOneByRcvryTrxSeqAndCrntRecFlg(long rcvryTrxSeq, boolean flag);

    public MwRcvryTrx findAllByRcvryTrxSeqAndPymtRefAndCrntRecFlgOrderByRcvryTrxSeq(long rcvryTrxSeq, Long pymtRef, boolean flag);

    public MwRcvryTrx findOneByInstrNumAndCrntRecFlg(String rcvryTrxSeq, boolean flag);

    public MwRcvryTrx findOneByRcvryTypSeqAndPymtRefAndCrntRecFlg(long rcvryTypSeq, long pymtRef, boolean flag);

    @Query(value = "select rt.* from mw_rcvry_trx rt \r\n" + "where rt.rcvry_trx_seq =? and rt.crnt_rec_flg =0\r\n"
            + "ORDER BY rt.eff_start_dt DESC \r\n" + "OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    public MwRcvryTrx findTop1ByRcvryTrxSeqAndCrntRecFlgFalse(long rcvryTrxSeq);

    @Query(value = "select distinct  trx.* from mw_rcvry_trx trx \r\n"
            + "            join MW_RCVRY_DTL dtl on dtl.RCVRY_TRX_SEQ = trx.RCVRY_TRX_SEQ and dtl.crnt_rec_flg=1\r\n"
            + "            where trx.crnt_rec_flg=1 and dtl.PYMT_SCHED_DTL_SEQ=?", nativeQuery = true)
    public List<MwRcvryTrx> findRecoveryTrxForPymtSchedDtlSeq(long rcvryTrxSeq);

    @Query(value = "select rt.* from mw_rcvry_trx rt \r\n" + "where rt.crnt_rec_flg =1 and rt.PYMT_REF=:clntId\r\n"
            + "ORDER BY rt.eff_start_dt DESC \r\n" + "OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    public MwRcvryTrx findTop1ByPymtRef(@Param("clntId") String clntId);

    // Added by Zohaib Asim - Dated 20/01/2020
    // IN CASE MULTIPLE RECOVERY TRANSACTION EXIST FOR SPECIFIC USER
    @Query(value = "SELECT *\n" +
            "  FROM MW_RCVRY_TRX RT\n" +
            " WHERE     RT.CHNG_RSN_CMNT LIKE :expSeq\n" +
            "       AND RT.RCVRY_TYP_SEQ = :rcvryTypSeq\n" +
            "       AND RT.PYMT_REF = :pymtRef\n" +
            "       AND RT.CRNT_REC_FLG = 1", nativeQuery = true)
    public List<MwRcvryTrx> findByRcvryTypSeqAndPymtRefAndCrntRecFlg(
            @Param("expSeq") String expSeq, @Param("rcvryTypSeq") long rcvryTypSeq,
            @Param("pymtRef") long pymtRef);

    // MwRcvryTrx converted to list for multiple execution
    // REASON: IN CASE MULTIPLE RECOVERY TRANSACTION EXIST FOR SPECIFIC USER
    public List<MwRcvryTrx> findByRcvryTypSeqAndPymtRefAndCrntRecFlg(long rcvryTypSeq, long pymtRef, boolean flag);

    // Get Partial Transaction Detail against PSD Sequence
    @Query(value = "SELECT DISTINCT RT.* FROM MW_RCVRY_DTL RD\n" +
            "JOIN MW_RCVRY_TRX RT ON RT.RCVRY_TRX_SEQ = RD.RCVRY_TRX_SEQ\n" +
            "    AND RT.CRNT_REC_FLG = RD.CRNT_REC_FLG\n" +
            "WHERE RD.PYMT_SCHED_DTL_SEQ = :psdSeq AND RD.CRNT_REC_FLG = 1", nativeQuery = true)
    List<MwRcvryTrx> getPartialTransDetailByPsdSeq(@Param("psdSeq") long psdSeq);

    // End by Zohaib Asim

    //Added by Areeba
    @Query(value = " SELECT * FROM MW_RCVRY_TRX \n" +
            " WHERE PYMT_REF = :clntSeq AND \n" +
            " RCVRY_TYP_SEQ = (SELECT TYP_SEQ FROM MW_TYPS MT WHERE TYP_ID = :rcvryTyp AND MT.TYP_CTGRY_KEY = 4 AND MT.CRNT_REC_FLG = 1)\n" +
            " ORDER BY EFF_START_DT DESC FETCH FIRST 1 ROWS ONLY ", nativeQuery = true)
    MwRcvryTrx findTop1ByPymtRefAndRcvryTypSeq(@Param("clntSeq") long clntSeq, @Param("rcvryTyp") String rcvryTyp);
    //Ended by Areeba

}
