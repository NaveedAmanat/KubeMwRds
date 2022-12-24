package com.idev4.rds.service;

import com.idev4.rds.domain.MwRefCdGrp;
import com.idev4.rds.dto.CodeGroupDto;
import com.idev4.rds.repository.MwRefCdGrpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service Implementation for managing MwRefCdGrp.
 */
@Service
@Transactional
public class MwRefCdGrpService {

    private final Logger log = LoggerFactory.getLogger(MwRefCdGrpService.class);

    private final MwRefCdGrpRepository mwRefCdGrpRepository;

    public MwRefCdGrpService(MwRefCdGrpRepository mwRefCdGrpRepository) {
        this.mwRefCdGrpRepository = mwRefCdGrpRepository;
    }

    /**
     * Save a mwRefCdGrp.
     *
     * @param mwRefCdGrp the entity to save
     * @return the persisted entity
     */
    public MwRefCdGrp save(MwRefCdGrp mwRefCdGrp) {
        log.debug("Request to save MwRefCdGrp : {}", mwRefCdGrp);
        return mwRefCdGrpRepository.save(mwRefCdGrp);
    }

    /**
     * Get all the mwRefCdGrps.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MwRefCdGrp> findAll(Pageable pageable) {
        log.debug("Request to get all MwRefCdGrps");
        return mwRefCdGrpRepository.findAll(pageable);
    }

    /**
     * Get one mwRefCdGrp by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MwRefCdGrp findOne(Long id) {
        log.debug("Request to get MwRefCdGrp : {}", id);
        return mwRefCdGrpRepository.findOneByRefCdGrpSeqAndCrntRecFlg(id, true);
    }

    public MwRefCdGrp updateExistingCodesGroup(CodeGroupDto codeGroup, String currUser) {
        MwRefCdGrp exRefCodeGrp = mwRefCdGrpRepository.findOneByRefCdGrpSeqAndCrntRecFlg(codeGroup.groupSeq, true);
        Instant currIns = Instant.now();
        if (exRefCodeGrp == null) {
            return null;
        }

        exRefCodeGrp.setLastUpdBy(currUser);
        exRefCodeGrp.setLastUpdDt(currIns);
        exRefCodeGrp.setRefCdGrp(codeGroup.groupCode);
        exRefCodeGrp.setRefCdGrpName(codeGroup.groupName);
        exRefCodeGrp.setRefCdGrpCmnt(codeGroup.groupDescription);

        return mwRefCdGrpRepository.save(exRefCodeGrp);

    }

}
