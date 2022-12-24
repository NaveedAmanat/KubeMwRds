package com.idev4.rds.repository;

import com.idev4.rds.domain.MwLedgerHeads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MwLedgerHeadsRepository extends JpaRepository<MwLedgerHeads, String> {

    public MwLedgerHeads findOneByCustSegments(String custSegments);
}
