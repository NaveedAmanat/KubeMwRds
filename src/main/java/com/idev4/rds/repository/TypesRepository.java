package com.idev4.rds.repository;

import com.idev4.rds.domain.Types;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TypesRepository extends JpaRepository<Types, Long> {

    public List<Types> findAllByTypCtgryKeyAndCrntRecFlg(long typCtgryKey, boolean flg);

    public Types findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg(String typId, long typCtgryKey, long brnchSeq, boolean flg);

    public Types findOneByTypSeqAndCrntRecFlg(long typSeq, boolean flg);

    public Types findOneByTypStrAndCrntRecFlg(String typStr, boolean flg);

    @Query(value = "select typ_seq,typ_str ,typ_id\r\n" + "from mw_typs t\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=t.typ_sts_key and rcv.crnt_rec_flg = 1 and ref_cd='0200'\r\n"
            + "where t.typ_ctgry_key=? and t.crnt_rec_flg = 1", nativeQuery = true)
    public List<Map> findAllActiveByTypCtgryKey(long typCtgryKey);

    @Query(value = "select t.* from mw_typs t\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=t.typ_sts_key and rcv.crnt_rec_flg = 1 and ref_cd='0200'\r\n"
            + "where t.typ_ctgry_key=:typCtgryKey and (t.BRNCH_SEQ=:brnch OR t.BRNCH_SEQ=0) and t.crnt_rec_flg = 1 and t.del_flg=0 order by typ_str", nativeQuery = true)
    public List<Types> findAllByBranchTypCtgryKeyAndCrntRecFlg(@Param("typCtgryKey") long typCtgryKey,
                                                               @Param("brnch") long brnch);

    @Query(value = "select t.* from mw_typs t\r\n"
            + "join mw_ref_cd_val rcv on rcv.ref_cd_seq=t.typ_sts_key and rcv.crnt_rec_flg = 1 and ref_cd='0200'\r\n"
            + "where (t.BRNCH_SEQ=:brnch OR t.BRNCH_SEQ=0) and t.crnt_rec_flg = 1 and t.del_flg=0 order by typ_str", nativeQuery = true)
    public List<Types> findAllByBranchAndCrntRecFlg(@Param("brnch") long brnch);

    public Types findOneByDfrdAcctNumAndTypIdInAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg(String typStr, List<String> typIds,
                                                                                      long typCtgryKey, long brnchSeq, boolean flg);

    /*
     * Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * all mobile wallet channel
     * */
    @Query(value = "select typ.TYP_STR, typ.TYP_ID\n" +
            "from mw_typs typ\n" +
            "where UPPER(typ.TYP_STR) like '%MOBILE WALLET%'\n" +
            "GROUP BY typ.TYP_STR, typ.TYP_ID", nativeQuery = true)
    public List<Object[]> findAllByTypStr();
    // Ended By Naveed - Date - 23-01-2022
}

