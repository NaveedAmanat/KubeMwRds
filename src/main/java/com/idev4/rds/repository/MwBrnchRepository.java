package com.idev4.rds.repository;

import com.idev4.rds.domain.MwBrnch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MwBrnch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwBrnchRepository extends JpaRepository<MwBrnch, Long> {

    @Query(value = "select b.brnch_seq,b.eff_start_dt, b.area_seq, b.brnch_cd, b.brnch_nm, b.brnch_dscr, b.brnch_sts_key, b.brnch_typ_key, b.brnch_ph_num, b.crtd_by, b.crtd_dt, b.last_upd_by, b.last_upd_dt, b.del_flg, b.eff_end_dt, b.crnt_rec_flg\n"
            + "from mw_emp e\n" + "join mw_brnch_emp_rel ber on ber.emp_seq = e.emp_seq and ber.crnt_rec_flg=1 and ber.del_flg=0\n"
            + "join mw_brnch b on b.brnch_seq = ber.brnch_seq and b.crnt_rec_flg = 1\n" + "where e.EMP_LAN_ID=:userId", nativeQuery = true)
    public MwBrnch findOneByUserId(@Param("userId") String userId);

    @Query(value = "select b.*\n" + "from  mw_brnch b\n" + "join mw_port prt on prt.BRNCH_SEQ=b.BRNCH_SEQ and prt.crnt_rec_flg=1\n"
            + "join mw_loan_app ap on ap.PORT_SEQ=prt.PORT_SEQ and ap.crnt_rec_flg=1 and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where crnt_rec_flg=1 and clnt_seq=:clnt_seq)\n"
            + "where b.CRNT_REC_FLG=1 and ap.CLNT_SEQ=:clnt_seq", nativeQuery = true)
    public MwBrnch findOneByClntSeq(@Param("clnt_seq") long clntSeq);


    @Query(value = "select b.*\n" + "from  mw_brnch b\n" + "join mw_port prt on prt.BRNCH_SEQ=b.BRNCH_SEQ and prt.crnt_rec_flg=1\n"
            + "join mw_loan_app ap on ap.PORT_SEQ=prt.PORT_SEQ and ap.crnt_rec_flg=1 and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where crnt_rec_flg=1 and clnt_seq=:clnt_seq and LOAN_APP_STS in (703,1245)) \n"
            + "where b.CRNT_REC_FLG=1 and ap.CLNT_SEQ=:clnt_seq and ap.LOAN_APP_STS in (703,1245)", nativeQuery = true)
    public MwBrnch findOneByClntSeqActiveLoan(@Param("clnt_seq") long clntSeq);
}
