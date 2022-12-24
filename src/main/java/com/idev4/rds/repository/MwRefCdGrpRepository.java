package com.idev4.rds.repository;

import com.idev4.rds.domain.MwRefCdGrp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the MwRefCdGrp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MwRefCdGrpRepository extends JpaRepository<MwRefCdGrp, Long> {

    public MwRefCdGrp findOneByRefCdGrpSeqAndCrntRecFlg(long seq, boolean flag);

    public MwRefCdGrp findOneByRefCdGrpNameAndCrntRecFlg(String name, boolean flag);

    public List<MwRefCdGrp> findAllByCrntRecFlg(boolean flag);

}
