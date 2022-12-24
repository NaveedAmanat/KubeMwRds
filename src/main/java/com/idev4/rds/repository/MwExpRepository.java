package com.idev4.rds.repository;

import com.idev4.rds.domain.MwExp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MwExpRepository extends JpaRepository<MwExp, Long> {

    public MwExp findOneByExpSeqAndCrntRecFlg(long expSeq, boolean flag);

    @Query(value = "SELECT exp.* FROM MW_RCVRY_DTL DTL\r\n"
            + "JOIN mw_exp exp on exp.EXP_REF = to_char(DTL.RCVRY_TRX_SEQ) and exp.crnt_rec_flg=1 and exp.del_flg=0\r\n"
            + "Join mw_typs typ on typ.typ_seq=exp.EXPNS_TYP_SEQ and typ.crnt_rec_flg=1 and typ.del_flg=0 and typ.typ_id='0005'\r\n"
            + "where DTL.RCVRY_TRX_SEQ=? AND DTL.PYMT_SCHED_DTL_SEQ = -1 AND DTL.CRNT_REC_FLG = 1 AND DTL.Del_FLG = 0 order by dtl.rcvry_trx_seq", nativeQuery = true)
    public MwExp getExcessExpenseForTrx(Long trxSeq);

    public MwExp findOneByExpRefAndExpnsTypSeqAndCrntRecFlgAndDelFlg(String expRef, long typSeq, boolean flag, boolean delflg);

    // Added by Zohaib Asim - Dated 26-12-2020
    // KSZB Claims
    public MwExp findOneByExpRefAndCrntRecFlg(String expRef, boolean flag);
    // End by Zohaib Asim
}
