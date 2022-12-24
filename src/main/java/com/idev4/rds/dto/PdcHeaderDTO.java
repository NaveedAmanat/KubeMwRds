package com.idev4.rds.dto;

import com.idev4.rds.domain.MwPdcDtl;
import com.idev4.rds.domain.MwPdcHdr;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PdcHeaderDTO {

    public Long pdcHdrSeq;

    public Long bankKey;

    public String brnchNm;

    public String acctNum;

    public Long loanAppSeq;

    public Instant effStartDt;

    public Instant effEndDt;

    public String crtdBy;

    public Instant crtdDt;

    public String lastUpdBy;

    public Instant lastUpdDt;

    public Boolean delFlg;

    public Boolean crntRecFlg;

    public List<MwPdcDtlDTO> mwPdcDtlDTOs;

    public boolean error = false;

    public PdcHeaderDTO(Long pdcHdrSeq, Long bankKey, String brnchNm, String acctNum, Long loanAppSeq, Instant effStartDt,
                        Instant effEndDt, String crtdBy, Instant crtdDt, String lastUpdBy, Instant lastUpdDt, Boolean delFlg, Boolean crntRecFlg,
                        List<MwPdcDtlDTO> mwPdcDtlDTOs) {
        super();
        this.pdcHdrSeq = pdcHdrSeq;
        this.bankKey = bankKey;
        this.brnchNm = brnchNm;
        this.acctNum = acctNum;
        this.loanAppSeq = loanAppSeq;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
        this.crtdBy = crtdBy;
        this.crtdDt = crtdDt;
        this.lastUpdBy = lastUpdBy;
        this.lastUpdDt = lastUpdDt;
        this.delFlg = delFlg;
        this.crntRecFlg = crntRecFlg;
        this.mwPdcDtlDTOs = mwPdcDtlDTOs;
    }

    public PdcHeaderDTO() {
        super();
        // TODO Auto-generated constructor stub
    }

    public PdcHeaderDTO getPdcHeaderDTO(MwPdcHdr hdr, List<MwPdcDtl> dtl) {
        PdcHeaderDTO dto = new PdcHeaderDTO();
        dto.pdcHdrSeq = hdr.getPdcHdrSeq();
        dto.bankKey = hdr.getBankKey();
        dto.brnchNm = hdr.getBrnchNm();
        dto.acctNum = hdr.getAcctNum();
        dto.mwPdcDtlDTOs = new ArrayList();
        dtl.forEach(d -> {
            MwPdcDtlDTO dtlDto = new MwPdcDtlDTO();
            dtlDto.pdcDtlSeq = d.getPdcDtlSeq();
            dtlDto.pdcHdrSeq = d.getPdcHdrSeq();
            dtlDto.pdcId = d.getPdcId();
            dtlDto.cheqNum = d.getcheqNum();
            dtlDto.amt = d.getAmt();
            dtlDto.collDt = d.getCollDt().toString();
            dto.mwPdcDtlDTOs.add(dtlDto);
        });

        return dto;
    }

}
