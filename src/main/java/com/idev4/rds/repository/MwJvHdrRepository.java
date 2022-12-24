package com.idev4.rds.repository;

import com.idev4.rds.domain.MwJvHdr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MwJvHdrRepository extends JpaRepository<MwJvHdr, Long> {

    // public MwJvHdr findTop1ByRcvryTrxSeqOrderByJvHdrSeqDesc( long rcvryTrxSeq );

    public MwJvHdr findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDesc(long entySeq, String entyTyp);

    //Added by Areeba
    @Query(value = "SELECT * FROM MW_JV_HDR JH WHERE ENTY_SEQ = :entySeq AND UPPER(ENTY_TYP) = UPPER(:entyTyp)\n" +
            " ORDER BY JV_HDR_SEQ DESC\n" +
            " FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
    public MwJvHdr findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDescAlt(@Param("entySeq") long entySeq, @Param("entyTyp") String entyTyp);
    //Ended by Areeba

    public Page<MwJvHdr> findAll(Pageable pageable);

    @Query(value = "select hdr.* from mw_jv_hdr hdr :filterStr order by hdr.jv_dt desc --\n{#pageable}\n", countQuery = "SELECT count(*) from mw_jv_hdr hdr :filterStr order by hdr.jv_dt desc \n--{#pageable}\n", nativeQuery = true)
    public Page<MwJvHdr> findAllMwJvHdrPageableFilter(Pageable pageable, @Param("filterStr") String filterStr);

    public List<MwJvHdr> findAllByEntySeqAndEntyTypOrderByJvHdrSeqDesc(long entySeq, String entyTyp);

}
