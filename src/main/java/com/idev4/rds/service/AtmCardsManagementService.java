package com.idev4.rds.service;

import com.idev4.rds.domain.MwMobWalInfo;
import com.idev4.rds.dto.AtmCardsDTO;
import com.idev4.rds.repository.MwMobWalInfoRepository;
import com.idev4.rds.util.Queries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  Added By Naveed - Date - 24-02-2022
 *  Upaisa and HBL Konnect Mobile wallet Payment Mode
 * */
@Service
public class AtmCardsManagementService {
    private final Logger log = LoggerFactory.getLogger(AtmCardsManagementService.class);

    @Autowired
    EntityManager entityManager;

    @Autowired
    private MwMobWalInfoRepository mobWalInfoRepository;

    public Map<String, Object> getAtmCardsListing(Long brnch_seq, Integer pageIndex, Integer pageSize, String filter, Boolean isCount) {

        String atmCardsScript = Queries.ATM_CARDS_MANAGEMENT_SCRIPT;
        String atmCardsCountScript = Queries.ATM_CARDS_MANAGEMENT_COUNT_SCRIPT;
        if (filter != null && filter.length() > 0) {
            String search = " and ((lower(c.frst_nm) like '%?%') OR (lower(c.last_nm) like '%?%') OR (c.CLNT_ID like '%?%') OR (mob.MOB_WAL_NO like '%?%') OR ( mob.ATM_CARD_NO like '%?%'))".replace("?",
                    filter.toLowerCase());
            atmCardsScript += search;
            atmCardsCountScript += search;
        }

        List<Object[]> result = entityManager.createNativeQuery(atmCardsScript + "\r\norder by hdr.DSBMT_DT DESC, emp.emp_nm").setParameter("brnch_seq", brnch_seq)
                .setFirstResult((pageIndex) * pageSize).setMaxResults(pageSize).getResultList();

        List<AtmCardsDTO> atmCardsDTOList = new ArrayList<>();

        for (Object[] res : result) {
            AtmCardsDTO dto = new AtmCardsDTO();

            dto.bdoName = (res[0] == null) ? "" : res[0].toString();
            dto.clientId = (res[1] == null) ? "" : res[1].toString();
            dto.clientName = (res[2] == null) ? "" : res[2].toString();
            dto.clientCnic = (res[3] == null) ? "" : res[3].toString();
            dto.walletNum = (res[4] == null) ? "" : res[4].toString();
            dto.atmCardNum = (res[5] == null) ? "" : res[5].toString();
            dto.atmCardReceivingDate = (res[6] == null) ? "" : res[6].toString();
            dto.atmCardDeliveredDate = (res[7] == null) ? "" : res[7].toString();
            dto.walletSeq = (res[8] == null) ? "" : res[8].toString();
            atmCardsDTOList.add(dto);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("atmCards", atmCardsDTOList);

        Long totalCountResult = 0L;
        if (isCount.booleanValue()) {
            totalCountResult = new BigDecimal(
                    entityManager.createNativeQuery(atmCardsCountScript).setParameter("brnch_seq", brnch_seq).getSingleResult().toString()).longValue();
        }
        resp.put("count", totalCountResult);
        return resp;
    }

    @Transactional
    public ResponseEntity<Map> Update(AtmCardsDTO dto, String currUser) throws ParseException {
        Map<String, String> resp = new HashMap<String, String>();
        Instant currIns = Instant.now();
        MwMobWalInfo mobWalInfo = mobWalInfoRepository.findByMobWalSeqAndCrntRecFlg(Long.parseLong(dto.walletSeq), true);
        if (mobWalInfo == null) {
            resp.put("warning", "Mobile Wallet Number Not Found !!");
            return ResponseEntity.ok().body(resp);
        }
        if (dto.atmCardNum != null) {
            List<MwMobWalInfo> lists = mobWalInfoRepository.findAllByAtmCardNoAndCrntRecFlgAndClntSeqNotIn(dto.atmCardNum, true, mobWalInfo.getClntSeq());
            if (!lists.isEmpty()) {
                resp.put("warning", "This ATM Card Number Already Exists" + dto.atmCardNum);
                return ResponseEntity.ok().body(resp);
            }
        }
        mobWalInfo.setLastUpdDt(currIns);
        mobWalInfo.setLastUpdBy(currUser);
        mobWalInfo.setAtmCardNo(dto.atmCardNum == null ? null : dto.atmCardNum);
        mobWalInfo.setAtmCardDelvryDt(dto.atmCardDeliveredDate == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dto.atmCardDeliveredDate).toInstant());
        mobWalInfo.setAtmCardRecDt(dto.atmCardReceivingDate == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dto.atmCardReceivingDate).toInstant());

        mobWalInfoRepository.save(mobWalInfo);
        resp.put("success", "Record Updated Successfully");
        return ResponseEntity.ok().body(resp);
    }
}
