package com.idev4.rds.repository;

import com.idev4.rds.domain.MwWrtOfRcvryTrx;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MwWrtOfRcvryTrxRepository extends JpaRepository<MwWrtOfRcvryTrx, Long> {

    public List<MwWrtOfRcvryTrx> findAllByClntSeqAndCrntRecFlg(long clntSeq, boolean flag);

    public List<MwWrtOfRcvryTrx> findAllByClntSeqAndCrntRecFlgOrderByCrtdDtAsc(long clntSeq, boolean flag);

    MwWrtOfRcvryTrx findByWrtOfRcvryTrxSeqAndCrntRecFlg(long clntSeq, boolean flag);

}
