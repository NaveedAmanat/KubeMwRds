package com.idev4.rds.repository;

import com.idev4.rds.domain.MwRcvryLoadStg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MwRcvryLoadStgRepository extends JpaRepository<MwRcvryLoadStg, Long> {

    List<MwRcvryLoadStg> findAllByTrxStsKey(boolean flg);

}
