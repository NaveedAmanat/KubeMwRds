package com.idev4.rds.domain;

import com.idev4.rds.ids.MwClntId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "MW_clnt")
@IdClass(MwClntId.class)
public class Clients implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CLNT_SEQ")
    private Long clntSeq;

    @Id
    @Column(name = "eff_start_dt")
    private Instant effStartDt;

    @Column(name = "eff_end_dt")
    private Instant effEndDt;

    @Column(name = "CLNT_ID")
    private String clntId;

    @Column(name = "CNIC_NUM")
    private Long cnicNum;

    @Column(name = "CNIC_EXPRY_DT")
    private Date cnicExpryDt;

    @Column(name = "FRST_NM")
    private String frstNm;

    @Column(name = "LAST_NM")
    private String lastNm;

    @Column(name = "NICK_NM")
    private String nickNm;

    @Column(name = "MTHR_MADN_NM")
    private String mthMadnNm;

    @Column(name = "FTHR_FRST_NM")
    private String FathrFrstNm;

    @Column(name = "FTHR_LAST_NM")
    private String fathrLastNm;

    @Column(name = "SPZ_FRST_NM")
    private String spzFrstNm;

    @Column(name = "SPZ_LAST_NM")
    private String spzLastNm;

    @Column(name = "DOB")
    private Date dob;

    @Column(name = "NUM_OF_DPND")
    private Long numOfDpnd;

    @Column(name = "ERNG_MEMB")
    private Long erngMemb;

    @Column(name = "HSE_HLD_MEMB")
    private Long hseHldMemb;

    @Column(name = "NUM_OF_CHLDRN")
    private Long numOfChldrn;

    @Column(name = "NUM_OF_ERNG_MEMB")
    private Long numOfErngMemb;

    @Column(name = "YRS_RES")
    private Long yrsRes;

    @Column(name = "MNTHS_RES")
    private Long mnthsRes;

    @Column(name = "PORT_KEY")
    private Long portKey;

    @Column(name = "GNDR_KEY")
    private Long gndrKey;

    @Column(name = "MRTL_STS_KEY")
    private Long mrtlStsKey;

    @Column(name = "EDU_LVL_KEY")
    private Long eduLvlKey;

    @Column(name = "OCC_KEY")
    private Integer occKey;

    @Column(name = "NATR_OF_DIS_KEY")
    private Long natrOfDisKey;

    @Column(name = "CLNT_STS_KEY")
    private Long clntStsKey;

    @Column(name = "RES_TYP_KEY")
    private Long resTypKey;

    @Column(name = "NOM_DTL_AVAILABLE_FLG")
    private Long nomDtlAvailableFlg;

    @Column(name = "SLF_PDC_FLG")
    private Long slfPdcFlg;

    @Column(name = "CRNT_ADDR_PERM_FLG")
    private Long crntAddrPermFlg;

    @Column(name = "CO_BWR_SAN_FLG")
    private Long coBwrSanFlg;

    @Column(name = "PH_NUM")
    private String phNum;

    @Column(name = "TOT_INCM_OF_ERNG_MEMB")
    private Long totIncomOfEngMem;

    @Column(name = "crtd_by")
    private String crtdBy;

    @Column(name = "crtd_dt")
    private Instant crtdDt;

    @Column(name = "last_upd_by")
    private String lastUpdBy;

    @Column(name = "last_upd_dt")
    private Instant lastUpdDt;

    @Column(name = "del_flg")
    private Boolean delFlg;

    @Column(name = "crnt_rec_flg")
    private Boolean crntRecFlg;

    // Added by Areeba - Dated - 13-05-2022
    @Column(name = "MEMBRSHP_DT")
    private Instant membrshpDt;

    @Column(name = "REF_CD_LEAD_TYP_SEQ")
    private Long refCdLeadTypSeq;
    // Ended by Areeba

    public Long getClntSeq() {
        return clntSeq;
    }

    public void setClntSeq(Long clntSeq) {
        this.clntSeq = clntSeq;
    }

    public Instant getEffStartDt() {
        return effStartDt;
    }

    public void setEffStartDt(Instant effStartDt) {
        this.effStartDt = effStartDt;
    }

    public Instant getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Instant effEndDt) {
        this.effEndDt = effEndDt;
    }

    public String getClntId() {
        return clntId;
    }

    public void setClntId(String clntId) {
        this.clntId = clntId;
    }

    public Long getCnicNum() {
        return cnicNum;
    }

    public void setCnicNum(Long cnicNum) {
        this.cnicNum = cnicNum;
    }

    public Date getCnicExpryDt() {
        return cnicExpryDt;
    }

    public void setCnicExpryDt(Date cnicExpryDt) {
        this.cnicExpryDt = cnicExpryDt;
    }

    public String getFrstNm() {
        return frstNm;
    }

    public void setFrstNm(String frstNm) {
        this.frstNm = frstNm;
    }

    public String getLastNm() {
        return lastNm;
    }

    public void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public String getNickNm() {
        return nickNm;
    }

    public void setNickNm(String nickNm) {
        this.nickNm = nickNm;
    }

    public String getMthMadnNm() {
        return mthMadnNm;
    }

    public void setMthMadnNm(String mthMadnNm) {
        this.mthMadnNm = mthMadnNm;
    }

    public String getFathrFrstNm() {
        return FathrFrstNm;
    }

    public void setFathrFrstNm(String fathrFrstNm) {
        FathrFrstNm = fathrFrstNm;
    }

    public String getFathrLastNm() {
        return fathrLastNm;
    }

    public void setFathrLastNm(String fathrLastNm) {
        this.fathrLastNm = fathrLastNm;
    }

    public String getSpzFrstNm() {
        return spzFrstNm;
    }

    public void setSpzFrstNm(String spzFrstNm) {
        this.spzFrstNm = spzFrstNm;
    }

    public String getSpzLastNm() {
        return spzLastNm;
    }

    public void setSpzLastNm(String spzLastNm) {
        this.spzLastNm = spzLastNm;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Long getNumOfDpnd() {
        return numOfDpnd;
    }

    public void setNumOfDpnd(Long numOfDpnd) {
        this.numOfDpnd = numOfDpnd;
    }

    public Long getErngMemb() {
        return erngMemb;
    }

    public void setErngMemb(Long erngMemb) {
        this.erngMemb = erngMemb;
    }

    public Long getHseHldMemb() {
        return hseHldMemb;
    }

    public void setHseHldMemb(Long hseHldMemb) {
        this.hseHldMemb = hseHldMemb;
    }

    public Long getNumOfChldrn() {
        return numOfChldrn;
    }

    public void setNumOfChldrn(Long numOfChldrn) {
        this.numOfChldrn = numOfChldrn;
    }

    public Long getNumOfErngMemb() {
        return numOfErngMemb;
    }

    public void setNumOfErngMemb(Long numOfErngMemb) {
        this.numOfErngMemb = numOfErngMemb;
    }

    public Long getYrsRes() {
        return yrsRes;
    }

    public void setYrsRes(Long yrsRes) {
        this.yrsRes = yrsRes;
    }

    public Long getMnthsRes() {
        return mnthsRes;
    }

    public void setMnthsRes(Long mnthsRes) {
        this.mnthsRes = mnthsRes;
    }

    public Long getPortKey() {
        return portKey;
    }

    public void setPortKey(Long portKey) {
        this.portKey = portKey;
    }

    public Long getGndrKey() {
        return gndrKey;
    }

    public void setGndrKey(Long gndrKey) {
        this.gndrKey = gndrKey;
    }

    public Long getMrtlStsKey() {
        return mrtlStsKey;
    }

    public void setMrtlStsKey(Long mrtlStsKey) {
        this.mrtlStsKey = mrtlStsKey;
    }

    public Long getEduLvlKey() {
        return eduLvlKey;
    }

    public void setEduLvlKey(Long eduLvlKey) {
        this.eduLvlKey = eduLvlKey;
    }

    public Integer getOccKey() {
        return occKey;
    }

    public void setOccKey(Integer occKey) {
        this.occKey = occKey;
    }

    public Long getNatrOfDisKey() {
        return natrOfDisKey;
    }

    public void setNatrOfDisKey(Long natrOfDisKey) {
        this.natrOfDisKey = natrOfDisKey;
    }

    public Long getClntStsKey() {
        return clntStsKey;
    }

    public void setClntStsKey(Long clntStsKey) {
        this.clntStsKey = clntStsKey;
    }

    public Long getResTypKey() {
        return resTypKey;
    }

    public void setResTypKey(Long resTypKey) {
        this.resTypKey = resTypKey;
    }

    public Long getNomDtlAvailableFlg() {
        return nomDtlAvailableFlg;
    }

    public void setNomDtlAvailableFlg(Long nomDtlAvailableFlg) {
        this.nomDtlAvailableFlg = nomDtlAvailableFlg;
    }

    public Long getSlfPdcFlg() {
        return slfPdcFlg;
    }

    public void setSlfPdcFlg(Long slfPdcFlg) {
        this.slfPdcFlg = slfPdcFlg;
    }

    public Long getCrntAddrPermFlg() {
        return crntAddrPermFlg;
    }

    public void setCrntAddrPermFlg(Long crntAddrPermFlg) {
        this.crntAddrPermFlg = crntAddrPermFlg;
    }

    public Long getCoBwrSanFlg() {
        return coBwrSanFlg;
    }

    public void setCoBwrSanFlg(Long coBwrSanFlg) {
        this.coBwrSanFlg = coBwrSanFlg;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public Long getTotIncomOfEngMem() {
        return totIncomOfEngMem;
    }

    public void setTotIncomOfEngMem(Long totIncomOfEngMem) {
        this.totIncomOfEngMem = totIncomOfEngMem;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Instant getCrtdDt() {
        return crtdDt;
    }

    public void setCrtdDt(Instant crtdDt) {
        this.crtdDt = crtdDt;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public Instant getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(Instant lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    public Boolean getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }

    public Boolean getCrntRecFlg() {
        return crntRecFlg;
    }

    public void setCrntRecFlg(Boolean crntRecFlg) {
        this.crntRecFlg = crntRecFlg;
    }

    public Instant getMembrshpDt() {
        return membrshpDt;
    }

    public void setMembrshpDt(Instant membrshpDt) {
        this.membrshpDt = membrshpDt;
    }

    public Long getRefCdLeadTypSeq() {
        return refCdLeadTypSeq;
    }

    public void setRefCdLeadTypSeq(Long refCdLeadTypSeq) {
        this.refCdLeadTypSeq = refCdLeadTypSeq;
    }
}
