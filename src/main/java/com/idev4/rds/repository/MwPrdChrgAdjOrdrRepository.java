package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPrdChrgAdjOrdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MwPrdChrgAdjOrdrRepository extends JpaRepository<MwPrdChrgAdjOrdr, Long> {

    /* @Query ( value = "select adj_ordr,prcao.prd_chrg_seq,typ_seq,typ_str,t.gl_acct_num\r\n" + "from mw_prd_chrg_adj_ordr prcao\r\n"
            + "left outer join MW_PRD_CHRG pc  on prcao.prd_chrg_seq=pc.PRD_CHRG_SEQ and pc.crnt_rec_flg =1\r\n"
            + "left outer join mw_typs t on pc.CHRG_TYP_SEQ=t.typ_seq and t.crnt_rec_flg =1\r\n" + "where prcao.crnt_rec_flg =1\r\n"
            + "and prcao.prd_seq=? \r\n" + "order by adj_ordr", nativeQuery = true )
    List< Map< String, Object > > getPrdChrgOrdrByPrdSeq( long prdSeq );*/
}
