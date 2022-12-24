package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPrdAcctSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MwPrdAcctSetRepository extends JpaRepository<MwPrdAcctSet, Long> {

    /*  @Query ( value = "select gl_acct_num\r\n" + "from mw_prd_acct_set pas \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=pas.acct_ctgry_key and val.crnt_rec_flg=1 and val.ref_cd = '0001'\r\n"
            + "where pas.prd_seq=:prdSeq and pas.crnt_rec_flg=1", nativeQuery = true )
    // Map findOneByPrdSeq( @Param ( "prdSeq" ) long prdSeq );
    */
    public MwPrdAcctSet findOneByPrdSeqAndCrntRecFlg(long prdSeq, boolean flag);

    @Query(value = "select * from mw_prd_acct_set pas \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=pas.acct_ctgry_key and val.crnt_rec_flg=1 and val.ref_cd =:refCd\r\n"
            + "where pas.prd_seq=:prdSeq and pas.crnt_rec_flg=1", nativeQuery = true)
    public MwPrdAcctSet findOneByrefCdAndPrdSeq(@Param("refCd") String refCd, @Param("prdSeq") long prdSeq);

    public MwPrdAcctSet findOneByPrdSeqAndAcctCtgryKeyAndCrntRecFlg(long prdSeq, long key, boolean flag);

    @Query(value = "select pas.* \r\n" + "from mw_prd_acct_set pas  \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=pas.acct_ctgry_key and val.crnt_rec_flg=1 and val.ref_cd =:refCd\r\n"
            + "join mw_loan_app app on app.PRD_SEQ = pas.prd_seq and app.CRNT_REC_FLG = 1\r\n"
            + "join mw_pymt_sched_hdr hdr on hdr.loan_app_seq=app.loan_app_seq and hdr.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl dtl on dtl.pymt_sched_hdr_seq=hdr.pymt_sched_hdr_seq and dtl.crnt_rec_flg=1 \r\n"
            + "where dtl.pymt_sched_dtl_seq=:pymtSdlDtlSeq and pas.CRNT_REC_FLG = 1", nativeQuery = true)
    public MwPrdAcctSet findOneByrefCdAndPymentDtlSeq(@Param("refCd") String refCd, @Param("pymtSdlDtlSeq") long pymtSdlDtlSeq);

}
