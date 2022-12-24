package com.idev4.rds.repository;

import com.idev4.rds.domain.MwHlthInsrMemb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MwHlthInsrMembRepository extends JpaRepository<MwHlthInsrMemb, Long> {

    // Modified by Muhammad Bassam - Dated 30-12-2020
    // added inst_agr_crtria
    @Query(value = "select hlth_insr_memb_seq,\r\n" + "       eff_start_dt,\r\n" + "       member_cnic_num,\r\n" + "       member_nm,\r\n"
            + "       dob,\r\n" + "       gndr_key,\r\n" + "       rel_key,\r\n" + "       mrtl_sts_key,\r\n" + "       crtd_by,\r\n"
            + "       crtd_dt,\r\n" + "       last_upd_by,\r\n" + "       last_upd_dt,\r\n" + "       del_flg,\r\n"
            + "       eff_end_dt,\r\n" + "       crnt_rec_flg,\r\n" + "       loan_app_seq,\r\n" + "       member_id,\r\n"
            + "       sync_flg\r\n" + "  from mw_hlth_insr_memb\r\n" + " where loan_app_seq =:loanAppSeq  and crnt_rec_flg = 1 and inst_agr_crtria(dob) = 1 \r\n"
            + "union\r\n" + "select rl.clnt_rel_seq,\r\n" + "       rl.eff_start_dt,\r\n" + "       rl.cnic_num,\r\n"
            + "       rl.frst_nm || ' ' || rl.last_nm     member_nm,\r\n" + "       rl.dob,\r\n" + "       rl.gndr_key,\r\n"
            + "       rl.rel_wth_clnt_key,\r\n" + "       rl.mrtl_sts_key,\r\n" + "       rl.crtd_by,\r\n" + "       rl.crtd_dt,\r\n"
            + "       rl.last_upd_by,\r\n" + "       rl.last_upd_dt,\r\n" + "       rl.del_flg,\r\n" + "       rl.eff_end_dt,\r\n"
            + "       rl.crnt_rec_flg,\r\n" + "       rl.loan_app_seq,\r\n" + "       ' 'member_id,\r\n" + "       rl.sync_flg\r\n"
            + "  from mw_clnt_rel rl\r\n" + "  join mw_ref_cd_val vl on vl.ref_cd_seq=rl.rel_wth_clnt_key and vl.crnt_rec_flg=1\r\n"
            + " where     rl.crnt_rec_flg = 1\r\n" + "       and rl.loan_app_seq = :loanAppSeq\r\n"
            + "       and vl.ref_cd = '0008' and inst_agr_crtria(dob) = 1", nativeQuery = true)
    public List<MwHlthInsrMemb> findByLoanAppSeqAndCrntRecFlg(@Param("loanAppSeq") long loanAppSeq);
}
