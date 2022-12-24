package com.idev4.rds.service;

import com.idev4.rds.domain.MwJvDtl;
import com.idev4.rds.domain.MwJvHdr;
import com.idev4.rds.repository.MwJvDtlRepository;
import com.idev4.rds.repository.MwJvHdrRepository;
import com.idev4.rds.repository.MwPrdAcctSetRepository;
import com.idev4.rds.repository.MwPrdChrgAdjOrdrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class VchrPostingComponent {

    @Autowired
    MwJvHdrRepository mwJvHdrRepository;

    @Autowired
    MwJvDtlRepository mwJvDtlRepository;

    @Autowired
    MwPrdAcctSetRepository mwPrdAcctSetRepository;

    @Autowired
    MwPrdChrgAdjOrdrRepository mwPrdChrgAdjOrdrRepository;

    @Transactional
    public Long saveVchrPosting(MwJvHdr mwJvHdr, List<MwJvDtl> mwJvDtls) {
        Long seq = mwJvHdrRepository.save(mwJvHdr).getJvHdrSeq();
        mwJvDtlRepository.save(mwJvDtls);
        return seq;
    }

    @Transactional
    public List<MwJvHdr> saveVchrPosting(List<MwJvHdr> mwJvHdr, List<MwJvDtl> mwJvDtls) {
        mwJvHdrRepository.save(mwJvHdr);
        mwJvDtlRepository.save(mwJvDtls);
        return mwJvHdr;
    }
}
