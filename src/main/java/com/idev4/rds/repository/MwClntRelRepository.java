package com.idev4.rds.repository;

import com.idev4.rds.domain.MwClntRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Spring Data repository for the MwClntRel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwClntRelRepository extends JpaRepository<MwClntRel, Long> {

    public MwClntRel findOneByClntRelSeq(long seq);

    public MwClntRel findOneByClntRelSeqAndCrntRecFlg(long seq, boolean flag);

    public MwClntRel findOneByClntRelSeqAndRelTypFlgAndCrntRecFlg(long seq, long relTyp, boolean flag);

    public MwClntRel findOneByLoanAppSeqAndRelTypFlgAndCrntRecFlg(long seq, long relKey, boolean flag);

    public MwClntRel findOneByLoanAppSeqAndCrntRecFlg(long seq, boolean flag);


    public List<MwClntRel> findAllByLoanAppSeqAndCrntRecFlg(long seq, boolean flag);


    @Query(value = "select c.clnt_seq seq,cnic_num, cnic_expry_dt, frst_nm, last_nm, ph_num,dob,gndr.ref_cd_dscr gndr,'Client' rel_typ,0 rel_typ_flg,'Self' rel,\n"
            + " (select hse_num|| ', ' || strt|| ', ' || vlg|| ', ' || cmnty_nm|| ', ' || city_nm\n" + "from mw_addr_rel ar\n"
            + " join mw_addr a on ar.addr_seq=a.addr_seq and a.crnt_rec_flg=1 and a.del_flg=0\n"
            + " join mw_city c on  a.city_seq= c.city_seq  and c.crnt_rec_flg=1 and c.del_flg=0\n"
            + " full outer join mw_cmnty cm on a.cmnty_seq=cm.cmnty_seq and cm.crnt_rec_flg=1 and cm.del_flg=0\n"
            + "where ar.enty_key=c.clnt_seq\n" + " ) as addr\n" + "from mw_clnt c \n"
            + "join mw_loan_app la on la.clnt_seq=c.clnt_seq and c.crnt_rec_flg=1\n"
            + " join mw_ref_cd_val gndr on c.gndr_key=gndr.ref_cd_seq and gndr.crnt_rec_flg=1 and gndr.del_flg=0\n"
            + " where la.loan_app_seq=:loanAppSeq and la.crnt_rec_flg=1\n" + "union\n"
            + " select cr.clnt_rel_seq seq,cnic_num, cnic_expry_dt, frst_nm, last_nm, ph_num,dob,gndr.ref_cd_dscr gndr,'Nominee' rel_typ,cr.rel_typ_flg,rel.ref_cd_dscr rel,\n"
            + " (select hse_num|| ', ' || strt|| ', ' || vlg|| ', ' || cmnty_nm|| ', ' || city_nm\n" + "from mw_addr_rel ar\n"
            + " join mw_addr a on ar.addr_seq=a.addr_seq and a.crnt_rec_flg=1 and a.del_flg=0\n"
            + " join mw_city c on  a.city_seq= c.city_seq  and c.crnt_rec_flg=1 and c.del_flg=0\n"
            + " full outer join mw_cmnty cm on a.cmnty_seq=cm.cmnty_seq and cm.crnt_rec_flg=1 and cm.del_flg=0\n"
            + " where ar.enty_key=cr.clnt_rel_seq and ar.enty_typ='Nominee'\n" + ") as addr\n" + "from mw_clnt_rel cr\n"
            + " join mw_ref_cd_val gndr on cr.gndr_key=gndr.ref_cd_seq and gndr.crnt_rec_flg=1 and gndr.del_flg=0\n"
            + " join mw_ref_cd_val rel on  cr.rel_wth_clnt_key=rel.ref_cd_seq and rel.crnt_rec_flg=1 and rel.del_flg=0\n"
            + " where cr.loan_app_seq=:loanAppSeq and cr.crnt_rec_flg=1 and cr.rel_typ_flg=1\n" + "union\n"
            + " select cr.clnt_rel_seq seq,cnic_num, cnic_expry_dt, frst_nm, last_nm, ph_num,dob,gndr.ref_cd_dscr gndr,'Co-Borrower' rel_typ,cr.rel_typ_flg,rel.ref_cd_dscr rel,\n"
            + " (select hse_num|| ', ' || strt|| ', ' || vlg|| ', ' || cmnty_nm|| ', ' || city_nm\n" + "from mw_addr_rel ar\n"
            + " join mw_addr a on ar.addr_seq=a.addr_seq and a.crnt_rec_flg=1 and a.del_flg=0\n"
            + " join mw_city c on  a.city_seq= c.city_seq  and c.crnt_rec_flg=1 and c.del_flg=0\n"
            + " full outer join mw_cmnty cm on a.cmnty_seq=cm.cmnty_seq and cm.crnt_rec_flg=1 and cm.del_flg=0\n"
            + " where ar.enty_key=cr.clnt_rel_seq and ar.enty_typ='Co-Borrower'\n" + ") as addr\n" + "from mw_clnt_rel cr\n"
            + " join mw_ref_cd_val gndr on cr.gndr_key=gndr.ref_cd_seq and gndr.crnt_rec_flg=1 and gndr.del_flg=0\n"
            + " join mw_ref_cd_val rel on  cr.rel_wth_clnt_key=rel.ref_cd_seq and rel.crnt_rec_flg=1 and rel.del_flg=0\n"
            + " where cr.loan_app_seq= :loanAppSeq and cr.crnt_rec_flg=1 and cr.rel_typ_flg=2\n"
            + "order by rel_typ_flg", nativeQuery = true)
    public List<Map<String, ?>> findAllByLoanAppSeq(@Param("loanAppSeq") long loanAppSeq);

}
