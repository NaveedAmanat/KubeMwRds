package com.idev4.rds.repository;

import com.idev4.rds.domain.MwAdcDsbmtQue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MwAdcDsbmtQueRepository extends JpaRepository<MwAdcDsbmtQue, Long> {

    MwAdcDsbmtQue findMwAdcDsbmtQueByCrntRecFlgAndDsbmtDtlKey(Boolean crntRecFlg, Long dsbmtDtlKey);

    List<MwAdcDsbmtQue> findMwAdcDsbmtQueByCrntRecFlgAndDsbmtHdrSeq(Boolean crntRecFlg, Long dsbmtHdrSeq);

}
