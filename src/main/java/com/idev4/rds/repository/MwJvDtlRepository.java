package com.idev4.rds.repository;

import com.idev4.rds.domain.MwJvDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MwJvDtlRepository extends JpaRepository<MwJvDtl, Long> {

    public List<MwJvDtl> findAllByJvHdrSeq(long jvHdrSeq);

}
