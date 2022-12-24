package com.idev4.rds.repository;

import com.idev4.rds.domain.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ClientRepository extends JpaRepository<Clients, Long> {

    // @Query ( value = "SELECT CLNT_SEQ, EFF_START_DT, CLNT_ID, CNIC_NUM, CNIC_EXPRY_DT, FRST_NM, LAST_NM, NICK_NM, MTHR_MADN_NM,
    // FTHR_FRST_NM, FTHR_LAST_NM, SPZ_FRST_NM, SPZ_LAST_NM, DOB, NUM_OF_DPND, ERNG_MEMB, HSE_HLD_MEMB, NUM_OF_CHLDRN, NUM_OF_ERNG_MEMB,
    // YRS_RES, MNTHS_RES, PORT_KEY, GNDR_KEY, MRTL_STS_KEY, EDU_LVL_KEY, OCC_KEY, NATR_OF_DIS_KEY, CLNT_STS_KEY, RES_TYP_KEY, DIS_FLG,
    // NOM_DTL_AVAILABLE_FLG, SLF_PDC_FLG, CRNT_ADDR_PERM_FLG, CO_BWR_SAN_FLG, PH_NUM, TOT_INCM_OF_ERNG_MEMB, CRTD_BY, CRTD_DT, LAST_UPD_BY,
    // LAST_UPD_DT, DEL_FLG, EFF_END_DT, CRNT_REC_FLG"
    // + " FROM mw_clnt WHERE CLNT_SEQ=?", nativeQuery = true )
    public Clients findOneByClntSeq(long clntSeq);

    @Query(value = "select app.loan_id,app.prd_seq,prd.prd_nm,app.loan_cycl_num,app.rqstd_loan_amt,app.aprvd_loan_amt,clnt.clnt_seq,mrtl.ref_cd_dscr mrtl,occ.ref_cd_dscr occ,res.ref_cd_dscr res,clnt.yrs_res,clnt.num_of_dpnd,clnt.num_of_chldrn,clnt.num_of_erng_memb,clnt.frst_nm, clnt.last_nm, clnt.clnt_id, clnt.cnic_num,port.port_nm,brnch.brnch_cd,brnch.brnch_nm, area.area_nm, reg.reg_nm\r\n"
            + " from mw_loan_app app\r\n" + " join mw_prd prd on prd.prd_seq=app.prd_seq and prd.crnt_rec_flg=1 and prd.del_flg=0\r\n"
            + " join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1\r\n"
            + " join mw_ref_cd_val mrtl on clnt.mrtl_sts_key=mrtl.ref_cd_seq and mrtl.crnt_rec_flg=1 and mrtl.del_flg=0\r\n"
            + " join mw_ref_cd_val occ on clnt.occ_key=occ.ref_cd_seq and occ.crnt_rec_flg=1 and occ.del_flg=0\r\n"
            + " join mw_ref_cd_val res on clnt.res_typ_key=res.ref_cd_seq and res.crnt_rec_flg=1 and res.del_flg=0 \r\n"
            + " join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1  \r\n"
            + " join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1   \r\n"
            + " join mw_area area on area.area_seq=brnch.area_seq and area.crnt_rec_flg=1   \r\n"
            + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1  \r\n"
            + " where app.crnt_rec_flg=1 and app.loan_app_seq =:loanAppSeq", nativeQuery = true)
    public Map<String, Object> findClientInfoByLoanAppSeq(@Param("loanAppSeq") long loanAppSeq);

    public Clients findOneByClntIdAndCrntRecFlg(String clntId, boolean flg);

    public Clients findOneByClntSeqAndCrntRecFlg(long clntSeq, boolean flg);

    @Query(value = "select distinct mc.*\r\n" + "from mw_rcvry_trx mrt\r\n"
            + "join  mw_rcvry_dtl mrd on mrd.rcvry_trx_seq = mrt.rcvry_trx_seq\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = mrd.pymt_sched_dtl_seq\r\n"
            + "join MW_PYMT_SCHED_HDR mpsh on mpsh.PYMT_SCHED_HDR_SEQ = psd.PYMT_SCHED_HDR_SEQ\r\n"
            + "join mw_loan_app mla on mla.LOAN_APP_SEQ = mpsh.LOAN_APP_SEQ\r\n"
            + "join mw_clnt mc on mc.clnt_seq=mla.clnt_seq and mc.crnt_rec_flg=1 \r\n"
            + "where mrt.rcvry_trx_seq = :rcvryTrxSeq and mla.crnt_rec_flg=1 and mrd.crnt_rec_flg=1 and  psd.crnt_rec_flg=1 and  mpsh.crnt_rec_flg=1", nativeQuery = true)
    public Clients findOneClntByTrx(@Param("rcvryTrxSeq") long rcvryTrxSeq);
}
