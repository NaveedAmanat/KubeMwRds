package com.idev4.rds.repository;

import com.idev4.rds.domain.MwPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MwPort entity.
 */

@Repository
public interface MwPortRepository extends JpaRepository<MwPort, Long> {

    public MwPort findOneByPortSeqAndCrntRecFlg(long seq, boolean flag);

}
