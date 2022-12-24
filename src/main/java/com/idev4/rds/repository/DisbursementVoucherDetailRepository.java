package com.idev4.rds.repository;

import com.idev4.rds.domain.DisbursementVoucherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisbursementVoucherDetailRepository extends JpaRepository<DisbursementVoucherDetail, Long> {

    public List<DisbursementVoucherDetail> findAllByDsbmtHdrSeq(long loanAppSeq);

    // @Modifying
    // @Query ( "update DisbursementVoucherDetail dvd set dvd.pymtTypSeq = :pymtTypSeq and dvd.instrNum=:instrNum and dvd.amt:amt"
    // + " and dvd.lastUpdBy=:lastUpdBy and dvd.lastUpdDt=:lastUpdDt where dvd.dsbmtDtlKey = :id" )
    // int updateDisbursementVoucher( @Param ( "pymtTypSeq" ) Long pymtTypSeq, @Param ( "instrNum" ) String instrNum,
    // @Param ( "amt" ) Long amt, @Param ( "lastUpdBy" ) String lastUpdBy, @Param ( "lastUpdDt" ) Instant lastUpdDt,
    // @Param ( "id" ) Long id );

    // @Modifying
    // @Query ( "update DisbursementVoucherDetail dvd set dvd.del_flg = :del_flg and dvd.lastUpdBy=:lastUpdBy and dvd.lastUpdDt=:lastUpdDt
    // where dvd.dsbmtDtlKey = :id" )
    // int deleteDisbursementVoucher( @Param ( "del_flg" ) Boolean del_flg, @Param ( "lastUpdBy" ) String lastUpdBy,
    // @Param ( "lastUpdDt" ) Instant lastUpdDt, @Param ( "id" ) Long id );

    DisbursementVoucherDetail findOneByDsbmtDtlKeyAndCrntRecFlg(long seq, boolean flag);

    List<DisbursementVoucherDetail> findAllByDsbmtHdrSeqAndCrntRecFlg(long seq, boolean flag);

    List<DisbursementVoucherDetail> findAllByDsbmtHdrSeqInAndCrntRecFlg(List<Long> seq, boolean flag);

    @Query(value = "select dtl.dsbmt_dtl_key, dtl.eff_start_dt, dtl.instr_num, dtl.amt, dtl.dsbmt_hdr_seq, dtl.crtd_by, dtl.crtd_dt, dtl.last_upd_by, dtl.last_upd_dt, dtl.del_flg, dtl.eff_end_dt, dtl.crnt_rec_flg, dtl.pymt_typs_seq\r\n"
            + " from mw_dsbmt_vchr_dtl dtl\r\n"
            + "join mw_dsbmt_vchr_hdr hdr on hdr.dsbmt_hdr_seq = dtl.dsbmt_hdr_seq and hdr.crnt_rec_flg = 1\r\n"
            + "where dtl.crnt_rec_flg = 1 and hdr.DSBMT_HDR_SEQ = ? and hdr.DSBMT_VCHR_TYP=?", nativeQuery = true)
    List<DisbursementVoucherDetail> findAllByDsbmtHdrSeqAndType(long dsbmtHdrSeq, int dsbmtVchrTyp);


    @Query(value = "select dtl.dsbmt_dtl_key, dtl.eff_start_dt, dtl.instr_num, dtl.amt, dtl.dsbmt_hdr_seq, dtl.crtd_by, dtl.crtd_dt, dtl.last_upd_by, dtl.last_upd_dt, dtl.del_flg, dtl.eff_end_dt, dtl.crnt_rec_flg, dtl.pymt_typs_seq\r\n"
            + " from mw_dsbmt_vchr_dtl dtl\r\n"
            + "join mw_dsbmt_vchr_hdr hdr on hdr.dsbmt_hdr_seq = dtl.dsbmt_hdr_seq and hdr.crnt_rec_flg = 1\r\n"
            + "where dtl.crnt_rec_flg = 1 and hdr.LOAN_APP_SEQ = ? ", nativeQuery = true)
    List<DisbursementVoucherDetail> findAllByLoanAppSeq(long loanAppSeq);

}
