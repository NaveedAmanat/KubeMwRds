package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPrdPdcSgrt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MwPrdPdcSgrtRepository extends JpaRepository<MwPrdPdcSgrt, Long> {

    List<MwPrdPdcSgrt> findAllByPrdSeqAndCrntRecFlg(long prd, boolean flag);

    List<MwPrdPdcSgrt> findAllByPrdSeqAndCrntRecFlgOrderByInstNumAsc(long prd, boolean flag);

}
