package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPrdGrp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the MwPrdGrp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwPrdGrpRepository extends JpaRepository<MwPrdGrp, Long> {

    public MwPrdGrp findOneByPrdGrpSeqAndCrntRecFlg(Long prdGrpSeq, boolean flag);

    public List<MwPrdGrp> findAllByCrntRecFlgOrderByPrdGrpSeq(boolean flag);

    @Query(value = "select grp.* from mw_prd_grp grp \r\n"
            + "join mw_prd prd on prd.prd_grp_seq=grp.prd_grp_seq and prd.crnt_rec_flg=1\r\n"
            + "where grp.crnt_rec_flg=1 and prd_seq=:prdSeq", nativeQuery = true)
    public MwPrdGrp findOneByPrdSeq(@Param("prdSeq") Long prdSeq);

    @Query(value = "select grp.* from mw_prd_grp grp \r\n"
            + "join mw_prd prd on prd.prd_grp_seq=grp.prd_grp_seq and prd.crnt_rec_flg=1\r\n"
            + "join mw_loan_app app on app.prd_seq=prd.prd_seq and app.crnt_rec_flg=1\r\n"
            + "where grp.crnt_rec_flg=1 and loan_app_seq=:loanAppSeq", nativeQuery = true)
    public MwPrdGrp findOneByLoanAppSeq(@Param("loanAppSeq") Long loanAppSeq);

}
