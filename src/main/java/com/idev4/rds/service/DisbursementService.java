package com.idev4.rds.service;

//import static org.assertj.core.api.Assertions.extractProperty;

import com.idev4.rds.domain.*;
import com.idev4.rds.dto.*;
import com.idev4.rds.feignclient.LoanServiceClient;
import com.idev4.rds.feignclient.SetupServiceClient;
import com.idev4.rds.repository.*;
import com.idev4.rds.util.SequenceFinder;
import com.idev4.rds.util.Sequences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class DisbursementService {

    private static final String APROVED_STATUS = "0004";
    private static final String SUBMITTED_STATUS = "0002";
    private static final String ACTIVE_STATUS = "0005";
    private static final String DISBIRST_STATUS = "0009";
    private static final String DISGARD_STATUS = "0008";
    private static final String DEFERRED_STATUS = "1285";
    private static final String ADVANCE_STATUS = "1305";
    private static final String DISBURSEMENT = "Disbursement";
    private static final String ISLAMIC_TYPE = "0002";
    private static final String KSK_TYPE = "1165";
    private static final String APPLICATIONS_STATUS = "0106";
    private static final Object ADMIN = "admin";
    private static final Object ITO = "ito";
    private static final String defaultDateStr = "01-01-1901";
    private final Logger log = LoggerFactory.getLogger(DisbursementService.class);
    private final LoanApplicationRepository loanApplicationRepository;
    private final ClientRepository clientRepository;
    private final PaymentScheduleHeaderRespository paymentScheduleHeaderRespository;
    private final PaymentScheduleChargersRepository paymentScheduleChargersRepository;
    private final DisbursementVoucherHeaderRepository disbursementVoucherHeaderRepository;
    private final MwPdcDtlRepository mwPdcDtlRepository;
    private final PdcHeaderRepository pdcHeaderRepository;
    private final DisbursementVoucherDetailRepository disbursementVoucherDetailRepository;
    private final TypesRepository typesRepository;
    private final EntityManager em;
    private final VchrPostingService vchrPostingService;
    private final MwRefCdValRepository mwRefCdValRepository;
    private final MwPrdRepository mwPrdRepository;
    @Autowired
    MwExpRepository mwExpRepository;
    @Autowired
    MwBrnchRepository mwBrnchRepository;

    @Autowired
    LoanServiceClient loanServiceClient;

    @Autowired
    PaymentScheduleDetailRepository paymentScheduleDetailRepository;

    @Autowired
    MwPrdPdcSgrtRepository mwPrdPdcSgrtRepository;

    @Autowired
    MwClntHlthInsrRepository mwClntHlthInsrRepository;

    @Autowired
    SetupServiceClient setupServiceClient;

    @Autowired
    MwHlthInsrPlanRepository mwHlthInsrPlanRepository;

    @Autowired
    MwPrdGrpRepository mwPrdGrpRepository;

    // Added by Zohaib Asim - Dated 26-12-2020
    // KSZB Claims
    @Autowired
    MwJvHdrRepository mwJvHdrRepository;

    // MCB Disbursements - Dated 06-05-2022
    @Autowired
    MwAdcDsbmtQueRepository mwAdcDsbmtQueRepository;

    @Autowired
    MwRefCdGrpRepository mwRefCdGrpRepository;
    // End by Zohaib Asim

    @Autowired
    MwClntRelRepository mwClntRelRepository;

    /* Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control */
    @Autowired
    private MwMobWalInfoRepository mwMobWalInfoRepository;
    // Ended By Naveed - Date - 23-01-2022

    public DisbursementService(LoanApplicationRepository loanApplicationRepository, ClientRepository clientRepository,
                               PaymentScheduleHeaderRespository paymentScheduleHeaderRespository,
                               PaymentScheduleChargersRepository paymentScheduleChargersRepository,
                               DisbursementVoucherHeaderRepository disbursementVoucherHeaderRepository, PdcHeaderRepository pdcHeaderRepository,
                               MwPdcDtlRepository mwPdcDtlRepository, DisbursementVoucherDetailRepository disbursementVoucherDetailRepository,
                               TypesRepository typesRepository, EntityManager em, VchrPostingService vchrPostingService,
                               MwRefCdValRepository mwRefCdValRepository, MwPrdRepository mwPrdRepository) {
        super();
        this.loanApplicationRepository = loanApplicationRepository;
        this.clientRepository = clientRepository;
        this.paymentScheduleHeaderRespository = paymentScheduleHeaderRespository;
        this.paymentScheduleChargersRepository = paymentScheduleChargersRepository;
        this.disbursementVoucherHeaderRepository = disbursementVoucherHeaderRepository;
        this.pdcHeaderRepository = pdcHeaderRepository;
        this.mwPdcDtlRepository = mwPdcDtlRepository;
        this.disbursementVoucherDetailRepository = disbursementVoucherDetailRepository;
        this.typesRepository = typesRepository;
        this.em = em;
        this.vchrPostingService = vchrPostingService;
        this.mwRefCdValRepository = mwRefCdValRepository;
        this.mwPrdRepository = mwPrdRepository;
    }

    public LoanApplicationHeaderDTO getLoanApplicationById(long loanAppSeq) {
        /**
         * @Date: 28-11-2022
         * @Update: Naveed
         * @Description: Production Issue - thrown Exception when multiple records against loanAppSeq
         * @Fixation: if multiple then get most recent records
         * */

        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.loanInfoByLoanAppSeq).setParameter("loanAppSeq", loanAppSeq);
        List<Object[]> obj = q.getResultList();
        Query qt = em.createNativeQuery(com.idev4.rds.util.Queries.TOTAL_RECEIVEABLE_AMOUNT).setParameter("loanAppSeq", loanAppSeq);
        Object[] qtObj = (Object[]) qt.getSingleResult();
        LoanApplicationHeaderDTO dto = new LoanApplicationHeaderDTO();

        if (obj != null && obj.size() > 0) {
            dto.loanAppId = obj.get(0)[0] == null ? "" : obj.get(0)[0].toString();
            dto.loanAppStatus = obj.get(0)[1] == null ? "" : obj.get(0)[1].toString();
            dto.firstName = obj.get(0)[2] == null ? "" : obj.get(0)[2].toString();
            dto.lastName = obj.get(0)[3] == null ? "" : obj.get(0)[3].toString();
            dto.clientId = (obj.get(0)[4] == null) ? "" : obj.get(0)[4].toString();
            dto.cnicNum = obj.get(0)[5] == null ? "" : obj.get(0)[5].toString();
            dto.gender = obj.get(0)[6] == null ? "" : obj.get(0)[6].toString();
            dto.maritalStatus = obj.get(0)[7] == null ? "" : obj.get(0)[7].toString();
            dto.house_num = obj.get(0)[8] == null ? "" : obj.get(0)[8].toString();
            dto.city = obj.get(0)[9] == null ? "" : obj.get(0)[9].toString();
            dto.uc = obj.get(0)[10] == null ? "" : obj.get(0)[10].toString();
            dto.tehsil = obj.get(0)[11] == null ? "" : obj.get(0)[11].toString();
            dto.dist = obj.get(0)[12] == null ? "" : obj.get(0)[12].toString();
            dto.state = obj.get(0)[13] == null ? "" : obj.get(0)[13].toString();
            dto.country = obj.get(0)[14] == null ? "" : obj.get(0)[14].toString();
            dto.portSeq = obj.get(0)[15] == null ? "" : obj.get(0)[15].toString();
            dto.portNm = obj.get(0)[16] == null ? "" : obj.get(0)[16].toString();
            dto.brnchSeq = new BigDecimal(obj.get(0)[17].toString()).longValue();
            dto.brnchNm = obj.get(0)[18] == null ? "" : obj.get(0)[18].toString();
            dto.area = obj.get(0)[19] == null ? "" : obj.get(0)[19].toString();
            dto.region = obj.get(0)[20] == null ? "" : obj.get(0)[20].toString();
            dto.prdNm = obj.get(0)[21] == null ? "" : obj.get(0)[21].toString();
            dto.prdSeq = obj.get(0)[22] == null ? "" : obj.get(0)[22].toString();
            dto.aprvdLoanAmt = (obj.get(0)[23] == null) ? 0D : new BigDecimal(obj.get(0)[23].toString()).doubleValue();
            dto.frstInstDt = obj.get(0)[24] != null ? obj.get(0)[24].toString() : "";
            dto.bizActyNm = obj.get(0)[25] != null ? obj.get(0)[25].toString() : "";
            dto.bizSectNm = obj.get(0)[26] != null ? obj.get(0)[26].toString() : "";
            dto.bdoNm = obj.get(0)[27] != null ? obj.get(0)[27].toString() : "";
            dto.scheduleId = (obj.get(0)[28] == null) ? 0L : new BigDecimal(obj.get(0)[28].toString()).longValue();
            dto.loanCyclNum = (obj.get(0)[33] == null) ? 0 : new BigDecimal(obj.get(0)[33].toString()).intValue();
            dto.preActivity = obj.get(0)[34] != null ? obj.get(0)[34].toString() : "";
            dto.totRecv = ((qtObj[0] == null) ? 0L : new BigDecimal(qtObj[0].toString()).longValue())
                    + ((qtObj[1] == null) ? 0L : new BigDecimal(qtObj[1].toString()).longValue())
                    + ((qtObj[2] == null) ? 0L : new BigDecimal(qtObj[2].toString()).longValue());
            dto.pdcNum = obj.get(0)[36] == null ? 0 : new BigDecimal(obj.get(0)[36].toString()).intValue();
            /*
             * Added by Naveed - Date - 11-05-2022
             * add Product Group Seq in Response
             * */
            dto.prdGrpSeq = obj.get(0)[37] == null ? 0 : new BigDecimal(obj.get(0)[37].toString()).longValue();
            // End by Naveed
        }
        return dto;
    }

    public LoanApplicationHeaderDTO getLoanApplicationsByApplicationId(long loanAppSeq) {
        Map<String, ?> m = loanApplicationRepository.findOneByLoanAppSeq(loanAppSeq);
        LoanApplicationHeaderDTO loanApplicationDto = new LoanApplicationHeaderDTO();
        BigDecimal loanAppSeqValue = new BigDecimal(m.get("LOAN_APP_SEQ").toString());
        loanApplicationDto.loanAppSeq = loanAppSeqValue.longValue();
        loanApplicationDto.loanId = (String) m.get("LOAN_ID");
        loanApplicationDto.brnchNm = (String) m.get("BRNCH_NM");
        loanApplicationDto.portNm = (String) m.get("PORT_NM");
        loanApplicationDto.bizActyNm = (String) m.get("BIZ_ACTY_NM");
        loanApplicationDto.bizSectNm = (String) m.get("BIZ_SECT_NM");
        loanApplicationDto.frstInstDt = (String) m.get("FRST_INST_DT");
        loanApplicationDto.totRecv = (long) 0;
        BigDecimal bd = new BigDecimal(m.get("APRVD_LOAN_AMT").toString());
        loanApplicationDto.aprvdLoanAmt = bd.doubleValue();
        MwPrdDTO mwPrdDTO = new MwPrdDTO();
        mwPrdDTO.prdNm = (String) m.get("PRD_NM");
        mwPrdDTO.prdId = (String) m.get("PRD_ID");
        loanApplicationDto.mwPrdDTO = mwPrdDTO;
        ClientDto cdDto = new ClientDto();
        cdDto.frstNm = (String) m.get("FRST_NM");
        cdDto.lastNm = (String) m.get("LAST_NM");
        loanApplicationDto.clientDto = cdDto;
        return loanApplicationDto;

    }

    // public Map< ? , ? > getAllLoanApplications( String user, String role, String filter, String sort, String directions, int pageNumber,
    // int pageSize ) {
    // String search = " and (lower(c.clnt_id) like '%?%' OR lower(c.frst_nm) like '%?%' or lower(c.last_nm) like '%?%' or lower(emp.emp_nm)
    // like '%?%' or c.cnic_num like '%?%') "
    // .replace( "?", filter.toLowerCase() );
    //
    // Query q = em
    // .createNativeQuery( com.idev4.rds.util.Queries.getDisbursmentApplications + search + "\r\norder by l.loan_app_sts_dt desc" )
    // .setParameter( "user", user ).setFirstResult( ( pageNumber - 1 ) * pageSize ).setMaxResults( pageSize );
    // if ( !role.isEmpty() && role.equals( ADMIN ) ) {
    // q = em.createNativeQuery(
    // com.idev4.rds.util.Queries.getDisbursmentApplicationsActive + search + "\r\norder by l.loan_app_sts_dt desc" )
    // .setFirstResult( ( pageNumber - 1 ) * pageSize ).setMaxResults( pageSize );
    // }
    // List< Object[] > results = q.getResultList();
    // List< LoanApplicationDTO > loanApplicationDtos = new ArrayList();
    //
    // if ( results != null && results.size() > 0 ) {
    //
    // results.forEach( obj -> {
    // LoanApplicationDTO dto = new LoanApplicationDTO();
    // dto.loanAppSeq = new BigDecimal( obj[ 0 ].toString() ).longValue();
    // dto.aprvdLoanAmt = ( obj[ 3 ] == null ) ? 0D : new BigDecimal( obj[ 3 ].toString() ).doubleValue();
    // dto.crtdDt = obj[ 17 ].toString();
    // dto.lastUpdDt = obj[ 18 ] == null ? "" : obj[ 18 ].toString();
    // dto.portSeq = obj[ 19 ] == null ? "" : obj[ 19 ].toString();
    // dto.loanAppSts = obj[ 23 ].toString();
    //
    // MwPrdDTO mwPrdDTO = new MwPrdDTO();
    // mwPrdDTO.prdNm = obj[ 5 ].toString();
    // mwPrdDTO.prdId = obj[ 22 ].toString();
    // dto.mwPrdDTO = mwPrdDTO;
    //
    // ClientDto cdDto = new ClientDto();
    //
    // cdDto.clntSeq = new BigDecimal( obj[ 4 ].toString() ).longValue();
    // cdDto.frstNm = obj[ 15 ].toString();
    // cdDto.lastNm = ( obj[ 16 ] == null ) ? "" : obj[ 16 ].toString();
    //
    // MwPortDTO mwPortDTO = new MwPortDTO();
    // mwPortDTO.portSeq = new BigDecimal( obj[ 19 ].toString() ).longValue();
    // mwPortDTO.portNm = obj[ 21 ].toString();
    // dto.mwPortDTO = mwPortDTO;
    // dto.clientDto = cdDto;
    // loanApplicationDtos.add( dto );
    //
    // } );
    // }
    //
    // Query countQry = em
    // .createNativeQuery( com.idev4.rds.util.Queries.getDisbursmentApplications + search + "\r\norder by l.loan_app_sts_dt desc" )
    // .setParameter( "user", user );
    // if ( !role.isEmpty() && role.equals( ADMIN ) ) {
    // countQry = em.createNativeQuery(
    // com.idev4.rds.util.Queries.getDisbursmentApplicationsActive + search + "\r\norder by l.loan_app_sts_dt desc" );
    // }
    //
    // List< Object[] > count = countQry.getResultList();
    //
    // Map res = new HashMap();
    // res.put( "data", loanApplicationDtos );
    //
    // res.put( "count", count.size() );
    // return res;
    //
    // }

    public Map<?, ?> getAllLoanApplications(String user, Long brnchSeq, String role, String filter, String sort, String directions,
                                            int pageNumber, int pageSize, Boolean isCount) {

        String disburmtAppsQuery = com.idev4.rds.util.Queries.getDisbursmentApplications;
        String disburmtActvAppsQuery = com.idev4.rds.util.Queries.getDisbursmentApplicationsActive;

        String disburmtAppsQueryCount = com.idev4.rds.util.Queries.getDisbursmentApplicationsCount;
        String disburmtActvAppsQueryCount = com.idev4.rds.util.Queries.getDisbursmentApplicationsActiveCount;

        if (filter.length() > 0 && filter != null) {
            String search = " and (((c.clnt_id) like '%?%' OR (l.loan_id) like '%?%' OR lower(c.frst_nm) like '%?%' or lower(c.last_nm) like '%?%' or lower(emp.emp_nm) like '%?%' or (c.cnic_num) like '%?%'))"
                    .replace("?", filter.toLowerCase());

            disburmtAppsQuery += search;
            disburmtActvAppsQuery += search;

            disburmtAppsQueryCount += search;
            disburmtActvAppsQueryCount += search;
        }
        Query q = em.createNativeQuery(disburmtAppsQuery + "\r\norder by l.loan_app_sts_dt desc").setParameter("user", user)
                .setParameter("brnch_seq", brnchSeq).setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
        if (!role.isEmpty() && (role.equals(ADMIN) || role.equals(ITO))) {

            q = em.createNativeQuery(disburmtActvAppsQuery + "\r\norder by l.loan_app_sts_dt desc").setParameter("brnch_seq", brnchSeq)
                    .setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
        }
        List<Object[]> results = q.getResultList();
        List<LoanApplicationDTO> loanApplicationDtos = new ArrayList();

        if (results != null && results.size() > 0) {

            results.forEach(obj -> {
                LoanApplicationDTO dto = new LoanApplicationDTO();
                dto.loanAppSeq = new BigDecimal(obj[0].toString()).longValue();
                dto.aprvdLoanAmt = (obj[3] == null) ? 0D : new BigDecimal(obj[3].toString()).doubleValue();
                dto.crtdDt = obj[17].toString();
                dto.lastUpdDt = obj[18] == null ? "" : obj[18].toString();
                dto.portSeq = obj[19] == null ? "" : obj[19].toString();
                dto.loanAppSts = obj[23].toString();

                MwPrdDTO mwPrdDTO = new MwPrdDTO();
                mwPrdDTO.prdNm = obj[5].toString();
                mwPrdDTO.prdId = obj[22].toString();
                dto.mwPrdDTO = mwPrdDTO;

                ClientDto cdDto = new ClientDto();

                cdDto.clntSeq = new BigDecimal(obj[4].toString()).longValue();
                cdDto.frstNm = obj[15].toString();
                cdDto.lastNm = (obj[16] == null) ? "" : obj[16].toString();

                MwPortDTO mwPortDTO = new MwPortDTO();
                mwPortDTO.portSeq = new BigDecimal(obj[19].toString()).longValue();
                mwPortDTO.portNm = obj[21].toString();
                dto.mwPortDTO = mwPortDTO;
                dto.clientDto = cdDto;
                dto.mwPrdDTO.prdGrpSeq = obj[24] == null ? "" : obj[24].toString();
                loanApplicationDtos.add(dto);

            });
        }

        long totalResultCount = 0L;
        if (isCount.booleanValue()) {
            totalResultCount = new BigDecimal(em.createNativeQuery(disburmtAppsQueryCount).setParameter("user", user)
                    .setParameter("brnch_seq", brnchSeq).getSingleResult().toString()).longValue();
            if (!role.isEmpty() && (role.equals(ADMIN) || role.equals(ITO))) {
                totalResultCount = new BigDecimal(em.createNativeQuery(disburmtActvAppsQueryCount).setParameter("brnch_seq", brnchSeq)
                        .getSingleResult().toString()).longValue();
            }
        }

        Map res = new HashMap();
        res.put("data", loanApplicationDtos);
        res.put("count", totalResultCount);
        return res;

    }

    public List<PaymentScheduleDetailDTO> getPaymentSchedules(long paySchedChrgSeq) {
        List<PaymentScheduleDetailDTO> paymentScheduleHeaderDTOs = new ArrayList();
        List<Object[]> scheHeaders = paymentScheduleHeaderRespository.getPaymentScheduleHeaderByLoanAppSeq(paySchedChrgSeq);
        if (scheHeaders != null && scheHeaders.size() != 0) {
            scheHeaders.forEach(obj -> {
                PaymentScheduleHeaderDTO paymentScheduleHeaderDTO = new PaymentScheduleHeaderDTO();
                paymentScheduleHeaderDTO.paySchedHdrSeq = new BigDecimal(obj[0].toString()).longValue();
                paymentScheduleHeaderDTO.paySchedId = obj[1].toString();
                PaymentScheduleDetailDTO paymentScheduleDetailDTO = new PaymentScheduleDetailDTO();
                paymentScheduleDetailDTO.paySchedHdrSeq = new BigDecimal(obj[0].toString()).longValue();
                paymentScheduleDetailDTO.paySchedDtlSeq = new BigDecimal(obj[5].toString()).longValue();
                paymentScheduleDetailDTO.instNum = new BigDecimal(obj[6].toString()).longValue();
                paymentScheduleDetailDTO.dueDt = (Date) obj[7];
                paymentScheduleDetailDTO.ppalAmtDue = new BigDecimal(obj[8].toString()).longValue();

                paymentScheduleDetailDTO.totChrgDue = (obj[9] == null ? 0L : new BigDecimal(obj[9].toString()).longValue())
                        + (obj[10] == null ? 0L : new BigDecimal(obj[10].toString()).longValue());
                paymentScheduleHeaderDTOs.add(paymentScheduleDetailDTO);
            });
        }
        return paymentScheduleHeaderDTOs;
    }

    public List<PaymentScheduleChargersDTO> getPaymentSchedulesDetial(long paySchedDtlSeq) {
        List<PaymentScheduleChargersDTO> paymentScheduleChargersDTOs = new ArrayList();
        List<Object[]> scheDetail = paymentScheduleChargersRepository.getPaymentScheduleChargersBy(paySchedDtlSeq);
        if (scheDetail != null && scheDetail.size() != 0) {
            scheDetail.forEach(obj -> {
                PaymentScheduleChargersDTO paymentScheduleChargersDTO = new PaymentScheduleChargersDTO();
                paymentScheduleChargersDTO.amt = (obj[0] == null) ? 0L : new BigDecimal(obj[0].toString()).longValue();
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.chrgTyp = obj[1] != null ? obj[1].toString() : "";
                if (obj[2] != null && new BigDecimal(obj[2].toString()).longValue() == -2) {
                    typesDTO.chrgTyp = "HEALTH INSURANCE";
                }
                paymentScheduleChargersDTO.chargesTypesDTO = typesDTO;
                paymentScheduleChargersDTOs.add(paymentScheduleChargersDTO);
            });
        }
        return paymentScheduleChargersDTOs;
    }

    public DisbursementVoucherHeaderDTO getDisbursementVoucherByLoanAppSeq(long loanAppSeq, String currUser) {

        DisbursementVoucherHeader disbursement = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        DisbursementVoucherHeaderDTO disbursementVoucherHeaderDTO = new DisbursementVoucherHeaderDTO();
        if (disbursement == null) {
            DisbursementVoucherHeader entity = new DisbursementVoucherHeader();
            long seq = SequenceFinder.findNextVal(Sequences.DSBMT_HDR_SEQ);
            Instant currIns = Instant.now();
            entity.setLoanAppSeq(loanAppSeq);
            entity.setDsbmtHdrSeq(seq);
            // entity.setDsbmtVchrTyp( ( long ) 2 );
            entity.setCrntRecFlg(true);
            entity.setCrtdBy(currUser);
            entity.setCrtdDt(currIns);
            entity.setDelFlg(false);
            entity.setEffStartDt(currIns);
            entity.setLastUpdBy(currUser);
            entity.setLastUpdDt(currIns);
            disbursementVoucherHeaderDTO.dsbmtHdrSeq = disbursementVoucherHeaderRepository.save(entity).getDsbmtHdrSeq();
        } else {
            disbursementVoucherHeaderDTO.dsbmtHdrSeq = disbursement.getDsbmtHdrSeq();
        }

        List<Map<String, ?>> apps = disbursementVoucherHeaderRepository.getDisbursementVoucherByLoanAppSeq(loanAppSeq);
        List<DisbursementVoucherDetailDTO> disbursementVoucherDetails = new ArrayList();
        if (apps != null && apps.size() != 0) {
            apps.forEach(voucher -> {
                Map m = voucher;
                DisbursementVoucherDetailDTO disbursementVoucherDetailDTO = new DisbursementVoucherDetailDTO();
                disbursementVoucherDetailDTO.dsbmtDtlKey = new BigDecimal(m.get("DSBMT_DTL_KEY").toString()).longValue();
                disbursementVoucherDetailDTO.instrNum = (String) m.get("INSTR_NUM");
                disbursementVoucherDetailDTO.amt = new BigDecimal(m.get("AMT").toString()).longValue();
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = new BigDecimal(m.get("TYP_SEQ").toString()).longValue();
                typesDTO.typStr = (String) m.get("TYP_STR");
                disbursementVoucherDetailDTO.paymtTypesDTO = typesDTO;
                disbursementVoucherDetails.add(disbursementVoucherDetailDTO);
            });
        }
        disbursementVoucherHeaderDTO.disbursementVoucherDetailDTOs = disbursementVoucherDetails;
        return disbursementVoucherHeaderDTO;

    }

    public DisbursementVoucherHeaderDTO getAgencyByLoanAppSeq(long loanAppSeq, String currUser) {

        DisbursementVoucherHeader disbursement = disbursementVoucherHeaderRepository
                .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 0);
        DisbursementVoucherHeaderDTO disbursementVoucherHeaderDTO = new DisbursementVoucherHeaderDTO();
        if (disbursement != null) {
            disbursementVoucherHeaderDTO.dsbmtHdrSeq = disbursement.getDsbmtHdrSeq();
        }

        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);

        List<DisbursementVoucherDetail> details = new ArrayList<DisbursementVoucherDetail>();
        List<DisbursementVoucherDetailDTO> disbursementVoucherDetails = new ArrayList();
        try {
            details = disbursementVoucherDetailRepository.findAllByDsbmtHdrSeqAndType(disbursementVoucherHeaderDTO.dsbmtHdrSeq, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
            disbursement = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 1);

            try {
                disbursementVoucherHeaderDTO.dsbmtHdrSeq = disbursement.getDsbmtHdrSeq();
                details = disbursementVoucherDetailRepository.findAllByDsbmtHdrSeqAndType(disbursementVoucherHeaderDTO.dsbmtHdrSeq, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!details.isEmpty() && details.size() != 0) {
            details.forEach(voucher -> {
                /*
                 * Modified By Naveed - Date - 23-01-2022
                 * SCR - Mobile Wallet Control
                 * get mobile wallet info against loan and
                 * put into response*/
                MwMobWalInfo mobWalInfo = mwMobWalInfoRepository.findMobInfoByLoanApp(loanAppSeq);

                DisbursementVoucherDetailDTO dist = new DisbursementVoucherDetailDTO();
                dist.amt = voucher.getAmt();
                dist.dsbmtDtlKey = voucher.getDsbmtDtlKey();
                dist.dsbmtHdrSeq = voucher.getDsbmtHdrSeq();
                dist.instrNum = voucher.getInstrNum();
                dist.loanAppSeq = loanAppSeq;
                dist.pymtTypSeq = voucher.getPymtTypSeq();
                dist.mobWalChnl = mobWalInfo != null ? mobWalInfo.getMobWalChnl() : "";
                dist.mobWalNum = mobWalInfo != null ? mobWalInfo.getMobWalNo() : "";
                disbursementVoucherDetails.add(dist);
            });
            // Ended By Naveed - Date - 23-01-2022
        }
        disbursementVoucherHeaderDTO.disbursementVoucherDetailDTOs = disbursementVoucherDetails;
        return disbursementVoucherHeaderDTO;
    }

    public boolean hasDefferedLoan(long loanAppSeq) throws Exception {
        MwRefCdVal val = mwRefCdValRepository.findRefCdByGrpAndVal("0106", "1285");
        List<LoanApplication> defloans = loanApplicationRepository.findAllByLoanAppSeqAndLoanAppSts(loanAppSeq, val.getRefCdSeq());
        return (defloans.size() > 0) ? true : false;
    }

    public boolean IsKRKPrdGrp(long loanAppSeq) {
        MwPrdGrp grp = mwPrdGrpRepository.findOneByLoanAppSeq(loanAppSeq);
        return (grp.getPrdGrpId().equals("5765")) ? true : false;
    }

    public long addDisbursementVoucher(DisbursementVoucherDetailDTO disbursementVoucherDetailDTO, String currUser) {

        long seq = SequenceFinder.findNextVal(Sequences.DSBMT_DTL_KEY);
        disbursementVoucherDetailDTO.instrNum = disbursementVoucherDetailDTO.instrNum == null ? Long.toString(seq)
                : disbursementVoucherDetailDTO.instrNum;
        DisbursementVoucherDetail disbursementVoucherDetail = new DisbursementVoucherDetail();
        Instant currIns = Instant.now();
        disbursementVoucherDetail.setDsbmtDtlKey(seq);
        disbursementVoucherDetail.setDsbmtHdrSeq(disbursementVoucherDetailDTO.dsbmtHdrSeq);
        disbursementVoucherDetail.setInstrNum(disbursementVoucherDetailDTO.instrNum);
        disbursementVoucherDetail.setAmt(disbursementVoucherDetailDTO.amt);
        disbursementVoucherDetail.setPymtTypSeq(disbursementVoucherDetailDTO.pymtTypSeq);
        disbursementVoucherDetail.setCrntRecFlg(true);
        disbursementVoucherDetail.setCrtdBy(currUser);
        disbursementVoucherDetail.setCrtdDt(currIns);
        disbursementVoucherDetail.setDelFlg(false);
        disbursementVoucherDetail.setEffStartDt(currIns);
        disbursementVoucherDetail.setLastUpdBy(currUser);
        disbursementVoucherDetail.setLastUpdDt(currIns);

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(disbursementVoucherDetailDTO.loanAppSeq, true);
        Query dsbmtDtlQury = em.createNativeQuery("select sum(amt) from mw_dsbmt_vchr_dtl where DSBMT_HDR_SEQ=:hdrSeq and crnt_rec_flg=1")
                .setParameter("hdrSeq", disbursementVoucherDetailDTO.dsbmtHdrSeq);
        Object dsbmtAmtObj = (Object) dsbmtDtlQury.getSingleResult();
        long dsbmtAmt = (dsbmtAmtObj == null) ? 0 : new BigDecimal(dsbmtAmtObj.toString()).longValue();

        long aprvdAmt = app.getAprvdLoanAmt();
        if ((dsbmtAmt + disbursementVoucherDetailDTO.amt.longValue()) > aprvdAmt)
            return 0;

        return disbursementVoucherDetailRepository.save(disbursementVoucherDetail).getDsbmtDtlKey();

    }

    public long updateDisbursementVoucher(DisbursementVoucherDetailDTO disbursementVoucherDetailDTO, String currUser) {

        DisbursementVoucherDetail exDisbursementVoucherDetail = disbursementVoucherDetailRepository
                .findOneByDsbmtDtlKeyAndCrntRecFlg(disbursementVoucherDetailDTO.dsbmtDtlKey, true);
        Instant currIns = Instant.now();

        exDisbursementVoucherDetail.setDelFlg(true);
        exDisbursementVoucherDetail.setLastUpdBy(currUser);
        exDisbursementVoucherDetail.setLastUpdDt(currIns);
        exDisbursementVoucherDetail.setCrntRecFlg(false);

        disbursementVoucherDetailRepository.save(exDisbursementVoucherDetail).getDsbmtDtlKey();

        DisbursementVoucherDetail disbursementVoucherDetail = new DisbursementVoucherDetail();

        disbursementVoucherDetail.setAmt(disbursementVoucherDetailDTO.amt);
        disbursementVoucherDetail.setCrntRecFlg(true);
        disbursementVoucherDetail.setCrtdBy(currUser);
        disbursementVoucherDetail.setCrtdDt(currIns);
        disbursementVoucherDetail.setDelFlg(false);
        disbursementVoucherDetail.setDsbmtDtlKey(disbursementVoucherDetailDTO.dsbmtDtlKey);
        disbursementVoucherDetail.setDsbmtHdrSeq(disbursementVoucherDetailDTO.dsbmtHdrSeq);
        disbursementVoucherDetail.setEffStartDt(currIns);
        disbursementVoucherDetail.setInstrNum(disbursementVoucherDetailDTO.instrNum);
        disbursementVoucherDetail.setLastUpdDt(currIns);
        disbursementVoucherDetail.setPymtTypSeq(disbursementVoucherDetailDTO.pymtTypSeq);

        return disbursementVoucherDetailRepository.save(disbursementVoucherDetail).getDsbmtDtlKey();

    }

    public long updateAgency(DisbursementVoucherDetailDTO disbursementVoucherDetailDTO, String currUser) {
        DisbursementVoucherDetail exDisbursementVoucherDetail = disbursementVoucherDetailRepository
                .findOneByDsbmtDtlKeyAndCrntRecFlg(disbursementVoucherDetailDTO.dsbmtDtlKey, true);
        Instant currIns = Instant.now();

        DisbursementVoucherHeader disbursementVoucherHeader = disbursementVoucherHeaderRepository
                .findOneByDsbmtHdrSeqAndCrntRecFlg(exDisbursementVoucherDetail.getDsbmtHdrSeq(), true);
        MwRefCdVal actSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(disbursementVoucherHeader.getLoanAppSeq(), true);

        if (actSts.getRefCdSeq().longValue() == app.getLoanAppSts().longValue()) {
            MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(app.getPrdSeq(), true);
            Types exPymtType = typesRepository.findOneByTypSeqAndCrntRecFlg(exDisbursementVoucherDetail.getPymtTypSeq(), true);
            Types pymtType = typesRepository.findOneByTypSeqAndCrntRecFlg(exDisbursementVoucherDetail.getPymtTypSeq(), true);

            Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(app.getClntSeq(), true);
            String clntName = clnt.getFrstNm() + " " + clnt.getLastNm();
            String reverseDesc = new String("Client ").concat(clntName).concat(" Payment mode changed from ")
                    .concat(exPymtType.getTypStr()).concat(" to ").concat(pymtType.getTypStr());
            vchrPostingService.reverseVchrPosting(disbursementVoucherHeader.getDsbmtHdrSeq(), DISBURSEMENT, currUser, reverseDesc);

            MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(app.getLoanAppSeq());
            MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(app.getLoanAppSeq());
            vchrPostingService.genrateDisbVchr(app, prd.getPrdCmnt(), currUser, sts.getRefCd(), type.getRefCd(),
                    disbursementVoucherHeader.getDsbmtVchrTyp());
        }
        exDisbursementVoucherDetail.setAmt(disbursementVoucherDetailDTO.amt);
        exDisbursementVoucherDetail.setDsbmtDtlKey(disbursementVoucherDetailDTO.dsbmtDtlKey);
        exDisbursementVoucherDetail.setInstrNum(disbursementVoucherDetailDTO.instrNum);
        exDisbursementVoucherDetail.setPymtTypSeq(disbursementVoucherDetailDTO.pymtTypSeq);
        disbursementVoucherDetailRepository.save(exDisbursementVoucherDetail);
        return exDisbursementVoucherDetail.getDsbmtDtlKey();

    }

    /*
     * Modified By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * */
    public long addDisbursementVoucherHeader(DisbursementVoucherDetailDTO detailDTO, String currUser) {
        long dbsSeq = 0;
        long loanAppSeq = detailDTO.loanAppSeq;
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);

        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);

//        DisbursementVoucherHeader exDisbursementVoucherHeader = disbursementVoucherHeaderRepository
//            .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 0);

        List<DisbursementVoucherHeader> voucherHeaderList = disbursementVoucherHeaderRepository
                .findAllByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTypOrderByCrtdDtDesc(loanAppSeq, true, 0);

        DisbursementVoucherHeader exDisbursementVoucherHeader = voucherHeaderList != null && voucherHeaderList.size() > 0 ? voucherHeaderList.get(0) : null;

        if (!type.getRefCd().equals(ISLAMIC_TYPE)) {
            if (exDisbursementVoucherHeader != null) {
                long mobileWalletSeq = saveMobileWalletInfo(detailDTO, currUser);
                if (mobileWalletSeq != 0) {
                    exDisbursementVoucherHeader.setMobWalSeq(mobileWalletSeq);
                    dbsSeq = disbursementVoucherHeaderRepository.save(exDisbursementVoucherHeader).getDsbmtHdrSeq();
                } else {
                    dbsSeq = exDisbursementVoucherHeader.getDsbmtHdrSeq();
                }
            } else {
                dbsSeq = addDisbursementVoucherHeader(detailDTO, currUser, 0);
            }
        } else {
            int typ = -1;
            if (sts.getRefCd().equals(APROVED_STATUS)) {
                typ = 1;
            } else if (sts.getRefCd().equals(ADVANCE_STATUS)) {
                typ = 0;
            }

            if (typ != -1) {
                DisbursementVoucherHeader dsb = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
                        true, typ);
                if (dsb != null) {
                    long mobileWalletSeq = saveMobileWalletInfo(detailDTO, currUser);
                    if (mobileWalletSeq != 0) {
                        exDisbursementVoucherHeader.setMobWalSeq(mobileWalletSeq);
                        dbsSeq = disbursementVoucherHeaderRepository.save(exDisbursementVoucherHeader).getDsbmtHdrSeq();
                    } else {
                        dbsSeq = dsb.getDsbmtHdrSeq();
                    }
                } else {
                    dbsSeq = addDisbursementVoucherHeader(detailDTO, currUser, typ);
                }
            }
        }

//        if (!type.getRefCd().equals(ISLAMIC_TYPE) && exDisbursementVoucherHeader != null)
//            dbsSeq = exDisbursementVoucherHeader.getDsbmtHdrSeq();
//
//        if (!type.getRefCd().equals(ISLAMIC_TYPE) && exDisbursementVoucherHeader == null)
//            dbsSeq = addDisbursementVoucherHeader(loanAppSeq, currUser, 0);

//        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
//            DisbursementVoucherHeader dsb = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
//                true, 1);
//            dbsSeq = dsb != null ? dsb.getDsbmtHdrSeq() : addDisbursementVoucherHeader(loanAppSeq, currUser, 1);
//
//        }
//
//        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(ADVANCE_STATUS)) {
//            DisbursementVoucherHeader dsb = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
//                true, 0);
//            dbsSeq = dsb != null ? dsb.getDsbmtHdrSeq() : addDisbursementVoucherHeader(loanAppSeq, currUser, 0);
//
//        }
        return dbsSeq;
    }
    // Ended By Naveed - Date - 23-01-2022

    // Added By Naveed - Date - 23-01-2023
    // SCR - Mobilw Wallet Control
    private long addDisbursementVoucherHeader(DisbursementVoucherDetailDTO detailDTO, String currUser, int type) {

        DisbursementVoucherHeader voucherHeader = new DisbursementVoucherHeader();
        Instant currIns = Instant.now();

        voucherHeader.setCrntRecFlg(true);
        voucherHeader.setCrtdBy(currUser);
        voucherHeader.setCrtdDt(currIns);
        voucherHeader.setDelFlg(false);
        voucherHeader.setDsbmtHdrSeq(SequenceFinder.findNextVal(Sequences.DSBMT_HDR_SEQ));
        voucherHeader.setDsbmtId(String.format("%04d", voucherHeader.getDsbmtHdrSeq()));

        voucherHeader.setEffStartDt(currIns);
        voucherHeader.setLastUpdBy(currUser);
        voucherHeader.setLastUpdDt(currIns);
        voucherHeader.setDsbmtVchrTyp(type);
        voucherHeader.setLoanAppSeq(detailDTO.loanAppSeq);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date defaultDate = format.parse(defaultDateStr);
            voucherHeader.setDsbmtDt(defaultDate.toInstant());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        voucherHeader.setDsbmtStsKey(-1L);
        long mobileWalletSeq = saveMobileWalletInfo(detailDTO, currUser);
        if (mobileWalletSeq != 0) {
            voucherHeader.setMobWalSeq(mobileWalletSeq);
        }
        return disbursementVoucherHeaderRepository.save(voucherHeader).getDsbmtHdrSeq();
    }
    // Ended By Naveed - Date - 23-01-2022

    private long addDisbursementVoucherHeader(long loanAppSeq, String currUser, int type) {

        DisbursementVoucherHeader disbursementVoucherHeader = new DisbursementVoucherHeader();
        Instant currIns = Instant.now();

        disbursementVoucherHeader.setCrntRecFlg(true);
        disbursementVoucherHeader.setCrtdBy(currUser);
        disbursementVoucherHeader.setCrtdDt(currIns);
        disbursementVoucherHeader.setDelFlg(false);
        disbursementVoucherHeader.setDsbmtHdrSeq(SequenceFinder.findNextVal(Sequences.DSBMT_HDR_SEQ));
        disbursementVoucherHeader.setDsbmtId(String.format("%04d", disbursementVoucherHeader.getDsbmtHdrSeq()));

        disbursementVoucherHeader.setEffStartDt(currIns);
        disbursementVoucherHeader.setLastUpdBy(currUser);
        disbursementVoucherHeader.setLastUpdDt(currIns);
        disbursementVoucherHeader.setDsbmtVchrTyp(type);
        disbursementVoucherHeader.setLoanAppSeq(loanAppSeq);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date defaultDate = format.parse(defaultDateStr);
            disbursementVoucherHeader.setDsbmtDt(defaultDate.toInstant());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        disbursementVoucherHeader.setDsbmtStsKey(-1L);
        DisbursementVoucherHeader exDisbursementVoucherHeader = disbursementVoucherHeaderRepository
                .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 0);
        if (exDisbursementVoucherHeader != null)
            return exDisbursementVoucherHeader.getDsbmtHdrSeq();

        return disbursementVoucherHeaderRepository.save(disbursementVoucherHeader).getDsbmtHdrSeq();
    }

    public long deleteDisbursementVoucher(long dsbmtDtlKey, String currUser) {
        DisbursementVoucherDetail disbursementVoucherDetail = disbursementVoucherDetailRepository
                .findOneByDsbmtDtlKeyAndCrntRecFlg(dsbmtDtlKey, true);
        disbursementVoucherDetail.setEffEndDt(Instant.now());
        disbursementVoucherDetail.setLastUpdBy(currUser);
        disbursementVoucherDetail.setLastUpdDt(Instant.now());
        disbursementVoucherDetail.setDelFlg(true);
        disbursementVoucherDetail.setCrntRecFlg(false);
        disbursementVoucherDetailRepository.save(disbursementVoucherDetail);

        return disbursementVoucherDetail.getDsbmtDtlKey();
    }

    public PdcHeaderDTO getPdcHeaderByLoanAppSeq(long loanAppSeq) {
        MwPdcHdr mwPdcHdr = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        PdcHeaderDTO pdcHeaderDTO = new PdcHeaderDTO();
        if (mwPdcHdr != null) {
            pdcHeaderDTO.pdcHdrSeq = mwPdcHdr.getPdcHdrSeq();
            pdcHeaderDTO.bankKey = mwPdcHdr.getBankKey();
            pdcHeaderDTO.brnchNm = mwPdcHdr.getBrnchNm();
            pdcHeaderDTO.acctNum = mwPdcHdr.getAcctNum();
            List<MwPdcDtl> MwPdcDtls = mwPdcDtlRepository.findAllByPdcHdrSeqAndCrntRecFlgOrderByPdcIdAsc(mwPdcHdr.getPdcHdrSeq(), true);
            List<MwPdcDtlDTO> mwPdcDtlDTOs = new ArrayList();
            MwPdcDtls.forEach(pdcDtl -> {
                MwPdcDtlDTO mwPdcDtlDTO = new MwPdcDtlDTO();
                mwPdcDtlDTO.pdcDtlSeq = pdcDtl.getPdcDtlSeq();
                mwPdcDtlDTO.pdcHdrSeq = pdcDtl.getPdcHdrSeq();
                mwPdcDtlDTO.collDt = pdcDtl.getCollDt().toString();
                mwPdcDtlDTO.amt = pdcDtl.getAmt();
                mwPdcDtlDTO.cheqNum = pdcDtl.getcheqNum();
                mwPdcDtlDTO.pdcId = pdcDtl.getPdcId();
                mwPdcDtlDTOs.add(mwPdcDtlDTO);
            });
            pdcHeaderDTO.mwPdcDtlDTOs = mwPdcDtlDTOs;
        } else {
            List<Object[]> oldLoanPdcHdr = em.createNativeQuery("SELECT PH.*\n" +
                    "  FROM MW_LOAN_APP  AP\n" +
                    "       JOIN MW_PDC_HDR PH\n" +
                    "           ON PH.LOAN_APP_SEQ = AP.LOAN_APP_SEQ AND PH.CRNT_REC_FLG = 1\n" +
                    " WHERE     AP.LOAN_CYCL_NUM = (SELECT MAX (LOAN_CYCL_NUM) - 1\n" +
                    "                                 FROM MW_LOAN_APP AP\n" +
                    "                                WHERE AP.LOAN_APP_SEQ = :loanAppSeq)\n" +
                    "       AND AP.CLNT_SEQ = (SELECT MAX (CLNT_SEQ)\n" +
                    "                            FROM MW_LOAN_APP AP\n" +
                    "                           WHERE AP.LOAN_APP_SEQ = :loanAppSeq)\n" +
                    "       AND AP.LOAN_APP_SEQ = AP.PRNT_LOAN_APP_SEQ\n" +
                    "       AND AP.CRNT_REC_FLG = 1").setParameter("loanAppSeq", loanAppSeq).getResultList();

            if (oldLoanPdcHdr != null) {
                oldLoanPdcHdr.forEach(pdc -> {
                    pdcHeaderDTO.bankKey = Long.parseLong(pdc[2].toString());
                    pdcHeaderDTO.brnchNm = pdc[3].toString();
                    pdcHeaderDTO.acctNum = pdc[4].toString();
                });
            }
        }
        return pdcHeaderDTO;
    }

    public long addPdcHeader(PdcHeaderDTO pdcHeaderDTO, String currUser) {

        String accCheckQueryStr = "select get_PDC_acct_info(:bankKey, :accNum, :loanAppSeq) from dual";
        int found = new BigDecimal(
                em.createNativeQuery(accCheckQueryStr)
                        .setParameter("bankKey", pdcHeaderDTO.bankKey)
                        .setParameter("accNum", pdcHeaderDTO.acctNum)
                        .setParameter("loanAppSeq", pdcHeaderDTO.loanAppSeq).getSingleResult().toString()).intValue();

//        String accCheckQueryStr = "select get_PDC_acct_info(:bankKey, :accNum) from dual";
//        int found = new BigDecimal(em.createNativeQuery(accCheckQueryStr).setParameter("bankKey", pdcHeaderDTO.bankKey)
//            .setParameter("accNum", pdcHeaderDTO.acctNum).getSingleResult().toString()).intValue();

        if (found >= 1)
            return 0;
        MwPdcHdr exMwPdcHdr = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(pdcHeaderDTO.loanAppSeq, true);

        if (exMwPdcHdr != null) {
            return updatePdcHeader(pdcHeaderDTO, currUser);
        }

        pdcHeaderDTO.pdcHdrSeq = SequenceFinder.findNextVal(Sequences.PDC_HDR_SEQ);
        MwPdcHdr mwPdcHdr = new MwPdcHdr();
        Instant currIns = Instant.now();
        mwPdcHdr.setPdcHdrSeq(pdcHeaderDTO.pdcHdrSeq);
        mwPdcHdr.setBankKey(pdcHeaderDTO.bankKey);
        mwPdcHdr.setBrnchNm(pdcHeaderDTO.brnchNm);
        mwPdcHdr.setAcctNum(pdcHeaderDTO.acctNum);
        mwPdcHdr.setLoanAppSeq(pdcHeaderDTO.loanAppSeq);
        mwPdcHdr.setCrntRecFlg(true);
        mwPdcHdr.setCrtdBy(currUser);
        mwPdcHdr.setCrtdDt(currIns);
        mwPdcHdr.setDelFlg(false);
        mwPdcHdr.setEffStartDt(currIns);
        mwPdcHdr.setLastUpdBy(currUser);
        mwPdcHdr.setLastUpdDt(currIns);
        return pdcHeaderRepository.save(mwPdcHdr).getPdcHdrSeq();

    }

    public long updatePdcHeader(PdcHeaderDTO pdcHeaderDTO, String currUser) {
        Instant currIns = Instant.now();

        MwPdcHdr exMwPdcHdr = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(pdcHeaderDTO.loanAppSeq, true);
        exMwPdcHdr.setAcctNum(pdcHeaderDTO.acctNum);
        exMwPdcHdr.setBankKey(pdcHeaderDTO.bankKey);
        exMwPdcHdr.setBrnchNm(pdcHeaderDTO.brnchNm);

        return pdcHeaderRepository.save(exMwPdcHdr).getPdcHdrSeq();

    }

    public long addPdcDetail(MwPdcDtlDTO mwPdcDtlDTO, String currUser) {
        long seq = SequenceFinder.findNextVal(Sequences.PDC_DTL_SEQ);
        MwPdcDtl mwPdcDtl = new MwPdcDtl();
        Instant currIns = Instant.now();
        mwPdcDtl.setPdcDtlSeq(seq);
        mwPdcDtl.setPdcHdrSeq(mwPdcDtlDTO.pdcHdrSeq);
        mwPdcDtl.setPdcId(mwPdcDtlDTO.pdcId);
        mwPdcDtl.setCollDt(Instant.parse(mwPdcDtlDTO.collDt));

        // added by YOUSAF DATED: 30-AUG-2022 TO CHECK DUPLICATE CHECQUE NUMBERS
        MwPdcHdr exMwPdcHdr = pdcHeaderRepository.findOneByPdcHdrSeqAndCrntRecFlg(mwPdcDtlDTO.pdcHdrSeq, true);

        long findDuplicateChecqueNumber = new BigDecimal(em.createNativeQuery("SELECT COUNT (1)\n" +
                        "  FROM MW_PDC_DTL  pdd\n" +
                        "       JOIN MW_PDC_HDR pdh\n" +
                        "           ON pdh.PDC_HDR_SEQ = pdd.PDC_HDR_SEQ AND pdh.CRNT_REC_FLG = 1\n" +
                        " WHERE     pdd.CHQ_NUM = :cheqNum\n" +
                        "       AND pdh.CRNT_REC_FLG = 1\n" +
                        "       AND pdh.BANK_KEY = :bankKey\n" +
                        "       AND pdh.ACCT_NUM = :acctNum")
                .setParameter("cheqNum", mwPdcDtlDTO.cheqNum)
                .setParameter("acctNum", exMwPdcHdr.getAcctNum())
                .setParameter("bankKey", exMwPdcHdr.getBankKey())
                .getSingleResult().toString()).longValue();

        log.debug("mwPdcDtlDTO.cheqNum: " + mwPdcDtlDTO.cheqNum);
        log.debug("mwPdcDtlDTO.getAcctNum: " + exMwPdcHdr.getAcctNum());
        log.debug("mwPdcDtlDTO.getBankKey: " + exMwPdcHdr.getBankKey());
        log.debug("findDuplicateChecqueNumber: " + findDuplicateChecqueNumber);
        if (findDuplicateChecqueNumber != 0L) {
            return -1;
        }
        mwPdcDtl.setcheqNum(mwPdcDtlDTO.cheqNum);
        mwPdcDtl.setAmt(mwPdcDtlDTO.amt);
        mwPdcDtl.setCrntRecFlg(true);
        mwPdcDtl.setCrtdBy(currUser);
        mwPdcDtl.setCrtdDt(currIns);
        mwPdcDtl.setDelFlg(false);
        mwPdcDtl.setEffStartDt(currIns);
        mwPdcDtl.setLastUpdBy(currUser);
        mwPdcDtl.setLastUpdDt(currIns);
        return mwPdcDtlRepository.save(mwPdcDtl).getPdcDtlSeq();
    }

    public long updatePdcDetail(MwPdcDtlDTO mwPdcDtlDTO, String currUser) {

        MwPdcDtl exMwPdcDtl = mwPdcDtlRepository.findOneByPdcDtlSeqAndCrntRecFlg(mwPdcDtlDTO.pdcDtlSeq, true);
        // added by YOUSAF DATED: 30-AUG-2022 TO CHECK DUPLICATE CHECQUE NUMBERS
        MwPdcHdr exMwPdcHdr = pdcHeaderRepository.findOneByPdcHdrSeqAndCrntRecFlg(mwPdcDtlDTO.pdcHdrSeq, true);
        long findDuplicateChecqueNumber = new BigDecimal(em.createNativeQuery("SELECT COUNT (1)\n" +
                        "  FROM MW_PDC_DTL  pdd\n" +
                        "       JOIN MW_PDC_HDR pdh\n" +
                        "           ON pdh.PDC_HDR_SEQ = pdd.PDC_HDR_SEQ AND pdh.CRNT_REC_FLG = 1\n" +
                        " WHERE     pdd.CHQ_NUM = :cheqNum\n" +
                        "       AND pdh.CRNT_REC_FLG = 1\n" +
                        "       AND pdh.BANK_KEY = :bankKey\n" +
                        "       AND pdh.ACCT_NUM = :acctNum")
                .setParameter("cheqNum", mwPdcDtlDTO.cheqNum)
                .setParameter("acctNum", exMwPdcHdr.getAcctNum())
                .setParameter("bankKey", exMwPdcHdr.getBankKey())
                .getSingleResult().toString()).longValue();

        log.debug("mwPdcDtlDTO.cheqNum: " + mwPdcDtlDTO.cheqNum);
        log.debug("mwPdcDtlDTO.getAcctNum: " + exMwPdcHdr.getAcctNum());
        log.debug("mwPdcDtlDTO.getBankKey: " + exMwPdcHdr.getBankKey());
        log.debug("findDuplicateChecqueNumber: " + findDuplicateChecqueNumber);

        if (findDuplicateChecqueNumber != 0L) {
            return -1;
        }
        exMwPdcDtl.setcheqNum(mwPdcDtlDTO.cheqNum);
        return mwPdcDtlRepository.save(exMwPdcDtl).getPdcDtlSeq();
    }

    public long deletePdcDetail(long pdcDtlSeq, String currUser) {
        MwPdcDtl mwPdcDtl = mwPdcDtlRepository.findOneByPdcDtlSeqAndCrntRecFlg(pdcDtlSeq, true);
        mwPdcDtl.setEffEndDt(Instant.now());
        mwPdcDtl.setLastUpdBy(currUser);
        mwPdcDtl.setLastUpdDt(Instant.now());
        mwPdcDtl.setDelFlg(true);
        mwPdcDtl.setCrntRecFlg(false);
        mwPdcDtlRepository.save(mwPdcDtl);
        return mwPdcDtl.getPdcDtlSeq();
    }

    @Transactional
    public PdcHeaderDTO genratePDCs(GenratePdcsDTO genratePdcsDTO, String currUser) {
        MwPrdGrp grp = mwPrdGrpRepository.findOneByLoanAppSeq(genratePdcsDTO.loanAppSeq);

        // KASHF RECAPITALIZATION KARZA
        if (!grp.getPrdGrpId().equals("5765")) {
            String accCheckQueryStr = "select get_PDC_acct_info(:bankKey, :accNum, :loanAppSeq) from dual";
            int found = new BigDecimal(
                    em.createNativeQuery(accCheckQueryStr)
                            .setParameter("bankKey", genratePdcsDTO.bankKey)
                            .setParameter("accNum", genratePdcsDTO.acctNum)
                            .setParameter("loanAppSeq", genratePdcsDTO.loanAppSeq).getSingleResult().toString()).intValue();

            if (found >= 1) {
                PdcHeaderDTO dto = new PdcHeaderDTO();
                dto.error = true;
                return dto;
            }
        }
        Instant currIns = Instant.now();
        MwPdcHdr exMwPdcHdr = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(genratePdcsDTO.loanAppSeq, true);
        if (exMwPdcHdr != null)
            return null;
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(genratePdcsDTO.loanAppSeq, true);
        List<PaymentScheduleDetail> psdList = paymentScheduleDetailRepository
                .getAllInstallmentsWithTotalDueAmount(genratePdcsDTO.loanAppSeq);

        Map<Long, Instant> installments = psdList.stream()
                .collect(Collectors.toMap(PaymentScheduleDetail::getInstNum, PaymentScheduleDetail::getDueDt));

        List<MwPrdPdcSgrt> prdPdcSgrts = mwPrdPdcSgrtRepository.findAllByPrdSeqAndCrntRecFlgOrderByInstNumAsc(app.getPrdSeq(), true);

        long slot = psdList.size() / prdPdcSgrts.size();

        long hdrSeq = SequenceFinder.findNextVal(Sequences.PDC_HDR_SEQ);
        Integer i = 1;
        Long checkNo = Long.parseLong(genratePdcsDTO.cheqNum);
        List<MwPdcDtl> pdcsList = new ArrayList();
        long lastInst = 0;
        int j = 0;
        for (MwPrdPdcSgrt p : prdPdcSgrts) {
            long amt = 0;
            while (j < psdList.size()) {
                PaymentScheduleDetail psd = psdList.get(j);
                if (psd.getInstNum().longValue() > lastInst && psd.getInstNum().longValue() % slot != 0) {
                    amt = amt + psd.getPpalAmtDue().longValue() + psd.getTotChrgDue().longValue();
                }
                if (psd.getInstNum().longValue() > lastInst && psd.getInstNum().longValue() % slot == 0) {
                    amt = amt + psd.getPpalAmtDue().longValue() + psd.getTotChrgDue().longValue();
                    lastInst = psd.getInstNum().longValue();
                    break;
                }
                j++;
            }

            long seq = SequenceFinder.findNextVal(Sequences.PDC_DTL_SEQ);
            MwPdcDtl mwPdcDtl = new MwPdcDtl();
            mwPdcDtl.setPdcDtlSeq(seq);
            mwPdcDtl.setPdcHdrSeq(hdrSeq);
            mwPdcDtl.setPdcId(i.toString());
            mwPdcDtl.setCollDt(installments.get(p.getInstNum()));
            mwPdcDtl.setcheqNum(checkNo.toString());

            // added by YOUSAF DATED: 30-AUG-2022 TO CHECK DUPLICATE CHECQUE NUMBERS

            long findDuplicateChecqueNumber = new BigDecimal(em.createNativeQuery("SELECT COUNT (1)\n" +
                            "  FROM MW_PDC_DTL  pdd\n" +
                            "       JOIN MW_PDC_HDR pdh\n" +
                            "           ON pdh.PDC_HDR_SEQ = pdd.PDC_HDR_SEQ AND pdh.CRNT_REC_FLG = 1\n" +
                            " WHERE     pdd.CHQ_NUM = :cheqNum\n" +
                            "       AND pdh.CRNT_REC_FLG = 1\n" +
                            "       AND pdh.BANK_KEY = :bankKey\n" +
                            "       AND pdh.ACCT_NUM = :acctNum")
                    .setParameter("cheqNum", checkNo.toString())
                    .setParameter("bankKey", genratePdcsDTO.bankKey)
                    .setParameter("acctNum", genratePdcsDTO.acctNum)
                    .getSingleResult().toString()).longValue();

            /*log.debug("checkNo.toString(): " + checkNo.toString());
            log.debug("genratePdcsDTO.acctNum : " + genratePdcsDTO.acctNum);
            log.debug("genratePdcsDTO.bankKey : " + genratePdcsDTO.bankKey);
            log.debug("findDuplicateChecqueNumber: " + findDuplicateChecqueNumber);*/

            if (findDuplicateChecqueNumber != 0L) {
                return null;
            }

            mwPdcDtl.setAmt(amt);
            mwPdcDtl.setCrntRecFlg(true);
            mwPdcDtl.setCrtdBy(currUser);
            mwPdcDtl.setCrtdDt(currIns);
            mwPdcDtl.setDelFlg(false);
            mwPdcDtl.setEffStartDt(currIns);
            mwPdcDtl.setLastUpdBy(currUser);
            mwPdcDtl.setLastUpdDt(currIns);
            pdcsList.add(mwPdcDtl);
            i++;
            checkNo++;
        }

        MwPdcHdr mwPdcHdr = new MwPdcHdr();
        mwPdcHdr.setPdcHdrSeq(hdrSeq);
        mwPdcHdr.setBankKey(genratePdcsDTO.bankKey);
        mwPdcHdr.setBrnchNm(genratePdcsDTO.brnchNm);
        mwPdcHdr.setAcctNum(genratePdcsDTO.acctNum);
        mwPdcHdr.setLoanAppSeq(genratePdcsDTO.loanAppSeq);
        mwPdcHdr.setCrntRecFlg(true);
        mwPdcHdr.setCrtdBy(currUser);
        mwPdcHdr.setCrtdDt(currIns);
        mwPdcHdr.setDelFlg(false);
        mwPdcHdr.setEffStartDt(currIns);
        mwPdcHdr.setLastUpdBy(currUser);
        mwPdcHdr.setLastUpdDt(currIns);

        exMwPdcHdr = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(genratePdcsDTO.loanAppSeq, true);
        if (exMwPdcHdr != null)
            return null;
        pdcHeaderRepository.save(mwPdcHdr).getPdcHdrSeq();
        mwPdcDtlRepository.save(pdcsList);
        PdcHeaderDTO dto = new PdcHeaderDTO();
        return dto.getPdcHeaderDTO(mwPdcHdr, pdcsList);

    }

    @Transactional
    public long batchPostingPdcs(long hdrSeq, List<MwPdcDtlDTO> mwPdcDtlDTOs, String user) {
        Instant now = Instant.now();
        List<MwPdcDtl> mwPdcDtls = mwPdcDtlRepository.findAllByPdcHdrSeqAndCrntRecFlgOrderByPdcIdAsc(hdrSeq, true);
        mwPdcDtls.forEach(p -> {
            p.setDelFlg(true);
            p.setLastUpdBy(user);
            p.setLastUpdDt(now);
            p.setCrntRecFlg(false);
            p.setEffEndDt(now);
        });
        mwPdcDtlRepository.save(mwPdcDtls);

        mwPdcDtlDTOs.forEach(p -> {
            long seq = SequenceFinder.findNextVal(Sequences.PDC_DTL_SEQ);
            MwPdcDtl mwPdcDtl = new MwPdcDtl();
            mwPdcDtl.setPdcDtlSeq(seq);
            mwPdcDtl.setPdcHdrSeq(p.pdcHdrSeq);
            mwPdcDtl.setPdcId(p.pdcId);
            mwPdcDtl.setCollDt(Instant.parse(p.collDt));
            mwPdcDtl.setcheqNum(p.cheqNum);
            mwPdcDtl.setAmt(p.amt);
            mwPdcDtl.setCrntRecFlg(true);
            mwPdcDtl.setCrtdBy(user);
            mwPdcDtl.setCrtdDt(now);
            mwPdcDtl.setDelFlg(false);
            mwPdcDtl.setEffStartDt(now);
            mwPdcDtl.setLastUpdBy(user);
            mwPdcDtl.setLastUpdDt(now);
            mwPdcDtls.add(mwPdcDtl);

        });
        mwPdcDtlRepository.save(mwPdcDtls);

        return hdrSeq;

    }

    public List<TypesDTO> getTypeByCatgryKey(long typSeq) {
        List<Map> typs = typesRepository.findAllActiveByTypCtgryKey(typSeq);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = new BigDecimal(typ.get("typ_seq").toString()).longValue();
                typesDTO.typStr = typ.get("typ_str").toString();
                typesDTO.typId = typ.get("typ_id").toString();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getTypeByBranchCatgryKey(long typSeq, long branch) {
        List<Types> typs = typesRepository.findAllByBranchTypCtgryKeyAndCrntRecFlg(typSeq, branch);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = typ.getTypSeq();
                typesDTO.typStr = typ.getTypStr();
                typesDTO.typId = typ.getTypId();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getTypeByBranch(long branch) {
        List<Types> typs = typesRepository.findAllByBranchAndCrntRecFlg(branch);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = typ.getTypSeq();
                typesDTO.typStr = typ.getTypStr();
                typesDTO.typId = typ.getTypId();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getTypeByClntSeq(long clntSeq) {
        long branch = mwBrnchRepository.findOneByClntSeq(clntSeq).getBrnchSeq();
        List<Types> typs = typesRepository.findAllByBranchTypCtgryKeyAndCrntRecFlg(4L, branch);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = typ.getTypSeq();
                typesDTO.typStr = typ.getTypStr();
                typesDTO.typId = typ.getTypId();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getTypeByWrtOffClntSeq(long clntSeq) {

        Query brnchQuery = em.createNativeQuery("select distinct brnch_seq from MW_WRT_OF_CLNT where clnt_seq=:clntSeq")
                .setParameter("clntSeq", clntSeq);
        Object brnchSeqObj = (Object) brnchQuery.getSingleResult();
        long branch = (brnchSeqObj == null) ? 0 : new BigDecimal(brnchSeqObj.toString()).intValue();
        List<Types> typs = typesRepository.findAllByBranchTypCtgryKeyAndCrntRecFlg(4L, branch);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = typ.getTypSeq();
                typesDTO.typStr = typ.getTypStr();
                typesDTO.typId = typ.getTypId();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getClntRemtType(long loanAppSeq) {
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

        long branch = mwBrnchRepository.findOneByClntSeq(app.getClntSeq()).getBrnchSeq();
        List<Types> typs = typesRepository.findAllByBranchTypCtgryKeyAndCrntRecFlg(3L, branch);
        List<TypesDTO> dtoList = new ArrayList();
        if (typs != null && typs.size() != 0) {
            typs.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typSeq = typ.getTypSeq();
                typesDTO.typStr = typ.getTypStr();
                typesDTO.typId = typ.getTypId();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }

    public List<TypesDTO> getBrnchRemit(String userName) {
        List<Object[]> typs = em.createNativeQuery("select typ.typ_seq,typ.typ_str,typ.typ_id\r\n" + "from mw_brnch_remit_rel rmt \r\n"
                + "join mw_typs typ on typ.typ_seq = rmt.pymt_typ_seq and typ.crnt_rec_flg = 1\r\n"
                + "join MW_BRNCH b on b.BRNCH_SEQ = rmt.BRNCH_SEQ and b.crnt_rec_flg = 1\r\n"
                + "join mw_brnch_emp_rel rl on rl.BRNCH_SEQ=b.BRNCH_SEQ and rl.crnt_rec_flg = 1  and rl.del_flg=0\r\n"
                + "join mw_emp emp on emp.emp_seq = rl.emp_seq and rl.crnt_rec_flg = 1 and emp.emp_lan_id =:userName\r\n"
                + "where rmt.crnt_rec_flg = 1\r\n" + "order by typ_str").setParameter("userName", userName).getResultList();
        List<TypesDTO> dtoList = new ArrayList();
        typs.forEach(typ -> {
            TypesDTO typesDTO = new TypesDTO();
            typesDTO.typSeq = new BigDecimal(typ[0].toString()).longValue();
            typesDTO.typStr = typ[1].toString();
            typesDTO.typId = typ[2].toString();
            dtoList.add(typesDTO);
        });
        return dtoList;
    }

    @Transactional
    public String updateLoanApp(long loanAppSeq, boolean post, String currUser, String token, boolean amlChck)
            throws Exception {

        // Zohaib Asim - Dated 27-04-2021
        Instant instant = new Date().toInstant();
        Instant now = Instant.now();

        // Business Functionality moved to Procedure
        String parmOutputProcedure = "";
        String rtnResp = "";
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRC_POST_DISBURSEMENT");
        storedProcedure.registerStoredProcedureParameter("P_LOAN_APP_SEQ", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_POST", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_AML_CHK", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_USER_ID", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_RTN_MSG", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("P_LOAN_APP_SEQ", loanAppSeq);
        storedProcedure.setParameter("P_POST", post == true ? 1L : 0L);
        storedProcedure.setParameter("P_AML_CHK", amlChck == true ? 1L : 0L);
        storedProcedure.setParameter("P_USER_ID", currUser);
        //storedProcedure.setParameter("P_RTN_MSG", parmOutputProcedure);
        storedProcedure.execute();

        System.out.println(storedProcedure.getOutputParameterValue("P_RTN_MSG"));

        parmOutputProcedure = storedProcedure.getOutputParameterValue("P_RTN_MSG").toString();
        if (parmOutputProcedure.contains("Disbursement Posted")) {
            log.info("PRC_POST_DISBURSEMENT: Successfully Executed.");

            String statusSplitArr[] = parmOutputProcedure.split("/");

            // Zohaib Asim - Dated 06-01-2022
            // Production Issue: Updated Loan App Status required after procedure execution.
            // Loan Application
            LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

            // TO PRINT THE REPORT FETCHED INFO
            if (post) {
                // ADDED BY YOUSAF DATED: 14/07/2022 => Production Bug
                MwClntHlthInsr hlthInsrCard = mwClntHlthInsrRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

                if (hlthInsrCard != null) {
                    HealthCardDto hcDto = new HealthCardDto();
                    hcDto.loanAppSeq = loanAppSeq;
                    hcDto.cardExpiryDate = now.plus(365, ChronoUnit.DAYS);
                    if (app.getPrdSeq() == 10 || app.getPrdSeq() == 39 || app.getPrdSeq() == 40) {
                        hcDto.loanAppSts = statusSplitArr[1].equals("0") ? app.getLoanAppSts().toString() : statusSplitArr[1];
                        hcDto.prdSeq = app.getPrdSeq();
                    }
                    ResponseEntity<Map> rsp = loanServiceClient.addHealthCard(hcDto, token);
                }
            }
        } else {
            log.error("PRC_POST_DISBURSEMENT: Execution unSuccessfully.");
        }
        //
        rtnResp = parmOutputProcedure;

        return rtnResp;
    }

    @Transactional
    public String updateLoanAppNew(long loanAppSeq, boolean post, String currUser, String token) {

        PaymentScheduleHeader paymentScheduleHeader = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        Instant instant = new Date().toInstant();
        Instant now = Instant.now();
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);
        int min = paymentScheduleHeader != null ? paymentScheduleHeader.getFrstInstDt().compareTo(instant) : 0;
        int max = paymentScheduleHeader != null ? paymentScheduleHeader.getFrstInstDt().compareTo(instant.plus(Duration.ofDays(30)))
                : 0;
        if ((type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(ADVANCE_STATUS)) && (min == -1 && max != -1)) {
            return "Disbursent Date is wrong";
        }
        if ((!type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) && (min == -1 && max != -1)) {
            return "Disbursent Date is wrong";
        }

        if (paymentScheduleHeader == null && (!type.getRefCd().equals(ISLAMIC_TYPE)
                || (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(ADVANCE_STATUS)))) {
            return "Pyment Schedula does not exist";
        }

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(app.getClntSeq(), true);
        ValidationDto dto = new ValidationDto();
        dto.cnicNum = clnt.getCnicNum();
        ResponseEntity<Map> result = loanServiceClient.tagsValidation(dto, token);
        Map tag = (Map) ((Map) result.getBody().get("client")).get("tag");

        boolean canProceed = tag == null || Integer.parseInt(tag.get("SvrtyFlagKey").toString()) == 0 ? true : false;
        if (!canProceed) {
            return "NACTA verification failed";
        }
        Map client = (Map) ((Map) result.getBody().get("client")).get("client");
        String status = (client == null) ? "" : (client.get("status") == null) ? "" : client.get("status").toString();
        if (status.toLowerCase().equals("active") && app.getPrdSeq() != 29) {
            return "Client is already Active";
        }
        AmlCheckDto aml = new AmlCheckDto();
        CheckDto check = new CheckDto();
        check.firstName = clnt.getFrstNm();
        check.lastName = clnt.getLastNm();
        aml.clnt = check;
        ResponseEntity<Map> amlResult = setupServiceClient.amlValidation(aml, token);
        String valid = amlResult.getBody().get("canProceed").toString();
        if (valid.equals("false")) {
            return "AML verification failed";
        }

        ResponseEntity<Map> advResult = loanServiceClient.advRulChk(loanAppSeq, token);
        String ruleResult = advResult.getBody().get("result").toString();
        if (!ruleResult.equalsIgnoreCase("pass")) {
            return ruleResult;
        }
        Query pdcCountQury = em.createNativeQuery("select count(pd.pdc_dtl_seq) \r\n" + "from mw_pdc_hdr  ph\r\n"
                + "join mw_pdc_dtl pd on ph.pdc_hdr_seq = pd.pdc_hdr_seq and pd.crnt_rec_flg=1\r\n"
                + "where loan_app_seq=:loanAppSeq and ph.crnt_rec_flg=1").setParameter("loanAppSeq", loanAppSeq);
        Object pdcCount = (Object) pdcCountQury.getSingleResult();
        Integer numPdc = (pdcCount == null) ? 0 : new BigDecimal(pdcCount.toString()).intValue();

        MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(app.getPrdSeq(), true);

        if ((prd.getPdcNum() != null && prd.getPdcNum() != numPdc) && (!type.getRefCd().equals(ISLAMIC_TYPE)
                || (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)))) {
            return "PCDs are Wrong";
        }

        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.APPLICATION_STS).setParameter("refCd",
                post ? ACTIVE_STATUS : DISBIRST_STATUS);
        int vchrTyp = 0;
        if (type.getRefCd().equals(ISLAMIC_TYPE) && sts.getRefCd().equals(APROVED_STATUS)) {
            q = em.createNativeQuery(com.idev4.rds.util.Queries.APPLICATION_STS).setParameter("refCd",
                    post ? ADVANCE_STATUS : APROVED_STATUS);
            vchrTyp = 1;
        }

        DisbursementVoucherHeader vhdr = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
                true, vchrTyp);
        String dbmtDt = new SimpleDateFormat("dd-MM-yyyy").format(vhdr.getDsbmtDt());
        if (dbmtDt.equals(defaultDateStr)) {
            return "This Loan Application is Already Disbursed, Please Contact Admin";
        }

        Object obj = q.getSingleResult();

        Query dvQyt = em
                .createNativeQuery("select sum(dvd.amt) amt\r\n" + "from mw_dsbmt_vchr_hdr dvh\r\n"
                        + "join  mw_dsbmt_vchr_dtl dvd on dvd.dsbmt_hdr_seq = dvh.dsbmt_hdr_seq and dvd.crnt_rec_flg=1\r\n"
                        + "where dvh.loan_app_seq=:loanAppSeq and dvh.crnt_rec_flg=1 and dvh.dsbmt_vchr_typ=:type")
                .setParameter("loanAppSeq", loanAppSeq).setParameter("type", vchrTyp);
        Object dvObj = (Object) dvQyt.getSingleResult();
        Long dvdAmt = (dvObj == null) ? 0L : new BigDecimal(dvObj.toString()).longValue();

        if (dvdAmt.longValue() != 0L && dvdAmt.longValue() == app.getAprvdLoanAmt()) {
            app.setLastUpdBy(currUser);
            app.setLastUpdDt(now);
            app.setCrntRecFlg(false);
            app.setDelFlg(true);
            LoanApplication napp = new LoanApplication();
            napp.setAprvdLoanAmt(app.getAprvdLoanAmt());
            napp.setClntSeq(app.getClntSeq());
            napp.setCmnt(app.getCmnt());
            napp.setCrntRecFlg(true);
            napp.setCrtdBy(app.getCrtdBy());
            napp.setCrtdDt(app.getCrtdDt());
            napp.setEffStartDt(now);
            napp.setLastUpdBy("w-" + currUser);
            napp.setLastUpdDt(now);
            napp.setLoanAppId(app.getLoanAppId());
            napp.setLoanAppSeq(app.getLoanAppSeq());
            napp.setLoanAppSts(new BigDecimal(obj.toString()).longValue());
            napp.setLoanAppStsDt(now);
            napp.setLoanCyclNum(app.getLoanCyclNum());
            napp.setLoanId(app.getLoanId());
            napp.setPortSeq(app.getPortSeq());
            napp.setPrdSeq(app.getPrdSeq());
            napp.setPrntLoanAppSeq(app.getPrntLoanAppSeq());
            napp.setRcmndLoanAmt(app.getRcmndLoanAmt());
            napp.setRejectionReasonCd(app.getRejectionReasonCd());
            napp.setRqstdLoanAmt(app.getRqstdLoanAmt());
            napp.setDelFlg(false);
            napp.setPscScore(app.getPscScore());
            napp.setTblScrn(app.getTblScrn());
            napp.setSyncFlg(true);
            napp.setRelAddrAsClnt_Flg(app.getRelAddrAsClnt_Flg());
            napp.setCoBwrAddrAsClntFlg(app.getCoBwrAddrAsClntFlg());
            napp.setLoanUtlStsSeq(app.getLoanUtlStsSeq());
            napp.setLoanUtlCmnt(app.getLoanUtlCmnt());
            napp.setCrdtBnd(app.getCrdtBnd());
            napp.setAppVrsn(app.getAppVrsn());
            if (post) {

                if (vchrTyp == 0 && type.getRefCd().equals(ISLAMIC_TYPE)) {
                    DisbursementVoucherHeader saleOneVchr = disbursementVoucherHeaderRepository
                            .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 1);
                    vchrPostingService.reverseVchrPosting(saleOneVchr.getDsbmtHdrSeq(), DISBURSEMENT, currUser, DISBURSEMENT);
                }

                List<DisbursementVoucherDetail> newvouchers = new ArrayList();
                if (type.getRefCd().equals(ISLAMIC_TYPE) && vchrTyp == 1) {
                    long hdrSeq = addDisbursementVoucherHeader(loanAppSeq, currUser, 0);

                    List<DisbursementVoucherDetail> vouchers = disbursementVoucherDetailRepository
                            .findAllByDsbmtHdrSeqAndCrntRecFlg(vhdr.getDsbmtHdrSeq(), true);
                    vouchers.forEach(v -> {

                        long seq = SequenceFinder.findNextVal(Sequences.DSBMT_DTL_KEY);
                        DisbursementVoucherDetail disbursementVoucherDetail = new DisbursementVoucherDetail();
                        disbursementVoucherDetail.setDsbmtDtlKey(seq);
                        disbursementVoucherDetail.setDsbmtHdrSeq(hdrSeq);
                        disbursementVoucherDetail.setInstrNum(v.getInstrNum());
                        disbursementVoucherDetail.setAmt(v.getAmt());
                        disbursementVoucherDetail.setPymtTypSeq(v.getPymtTypSeq());
                        disbursementVoucherDetail.setCrntRecFlg(true);
                        disbursementVoucherDetail.setCrtdBy(currUser);
                        disbursementVoucherDetail.setCrtdDt(now);
                        disbursementVoucherDetail.setDelFlg(false);
                        disbursementVoucherDetail.setEffStartDt(now);
                        disbursementVoucherDetail.setLastUpdBy(currUser);
                        disbursementVoucherDetail.setLastUpdDt(now);
                        newvouchers.add(disbursementVoucherDetail);

                    });

                }

                DisbursementVoucherHeader validateVhdr = disbursementVoucherHeaderRepository
                        .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, vchrTyp);
                String dbmtDtStr = new SimpleDateFormat("dd-MM-yyyy").format(validateVhdr.getDsbmtDt());
                if (dbmtDtStr.equals(defaultDateStr)) {
                    return "This Loan Application is Already Disbursed, Please Contact Admin";
                }
                disbursementVoucherDetailRepository.save(newvouchers);

                loanApplicationRepository.save(app);
                loanApplicationRepository.save(napp);
                vhdr.setDsbmtDt(now);
                disbursementVoucherHeaderRepository.save(vhdr);
                vchrPostingService.genrateDisbVchr(napp, prd.getPrdCmnt(), currUser, sts.getRefCd(), type.getRefCd(), vchrTyp);
                log.warn("Disbustment--" + napp.getLoanAppSeq());

                // ADDED BY YOUSAF DATED: 14/07/2022 => Production Bug
                MwClntHlthInsr hlthInsrCard = mwClntHlthInsrRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);

                if (hlthInsrCard != null) {
                    HealthCardDto hcDto = new HealthCardDto();
                    hcDto.loanAppSeq = loanAppSeq;
                    hcDto.cardExpiryDate = now.plus(365, ChronoUnit.DAYS);
                    ResponseEntity<Map> rsp = loanServiceClient.addHealthCard(hcDto, token);
                }

//                MwHlthInsrPlan plan = mwHlthInsrPlanRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq);
//
//
//                if (plan != null && plan.getHlthCardFlg() != null && plan.getHlthCardFlg() == true) {
//                    HealthCardDto hcDto = new HealthCardDto();
//                    hcDto.loanAppSeq = loanAppSeq;
//                    hcDto.cardExpiryDate = now.plus(365, ChronoUnit.DAYS);
//                    ResponseEntity<Map> rsp = loanServiceClient.addHealthCard(hcDto, token);
//                }

            }
            return "disbursment";
        }

        return "Some thing went wrong";

    }

    public void disbursementMultiplePosting(String apps, String currUser, String token) {
        Pattern pattern = Pattern.compile(",");
        List<Long> list = pattern.splitAsStream(apps).map(Long::valueOf).collect(Collectors.toList());

        list.forEach(e -> {
            try {
                updateLoanApp(e, true, currUser, token, false);
            } catch (Exception ex) {

            }
        });

    }

    @Transactional
    public boolean deleteLoanApp(long loanAppSeq, String reson, String currUser, String token) {

        Instant now = Instant.now();
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.APPLICATION_STS).setParameter("refCd", ACTIVE_STATUS);
        Object obj = q.getSingleResult();

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (new BigDecimal(obj.toString()).longValue() == app.getLoanAppSts().longValue()) {
            return false;
        }//deffer
        Query disQury = em.createNativeQuery(com.idev4.rds.util.Queries.APPLICATION_STS).setParameter("refCd", DISGARD_STATUS);
        Object dis = disQury.getSingleResult();
        List<DisbursementVoucherHeader> vouchers = disbursementVoucherHeaderRepository.findAllByLoanAppSeqAndCrntRecFlg(loanAppSeq,
                true);
        if (vouchers != null) {
            vouchers.forEach(v -> {
                v.setDelFlg(true);
                v.setCrntRecFlg(false);
                v.setLastUpdBy(currUser);
                v.setLastUpdDt(now);
                v.setEffEndDt(now);

            });
            disbursementVoucherHeaderRepository.save(vouchers);
        }

        PaymentScheduleHeader pyment = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pyment != null) {
            pyment.setDelFlg(true);
            pyment.setCrntRecFlg(false);
            pyment.setLastUpdBy(currUser);
            pyment.setLastUpdDt(now);
            pyment.setEffEndDt(now);
            paymentScheduleHeaderRespository.save(pyment);
        }

        MwPdcHdr pdc = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pdc != null) {
            pdc.setDelFlg(true);
            pdc.setCrntRecFlg(false);
            pdc.setLastUpdBy(currUser);
            pdc.setLastUpdDt(now);
            pdc.setEffEndDt(now);
            pdcHeaderRepository.save(pdc);
        }

        loanServiceClient.deleteApplication(loanAppSeq, token, new BigDecimal(dis.toString()).longValue(), reson);

        return true;

    }

    // revert to back status
    @Transactional
    public boolean revertLoanApp(long loanAppSeq, String reson, String currUser) {
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);
        Instant now = Instant.now();
        MwRefCdVal appSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);

        MwRefCdVal advanceSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ADVANCE_STATUS);

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (appSts.getRefCdSeq().longValue() == app.getLoanAppSts().longValue()) {
            return false;
        }
        int vchrTyp = 0;
        if (advanceSts.getRefCdSeq().longValue() == app.getLoanAppSts().longValue()) {
            vchrTyp = 1;
        }

        DisbursementVoucherHeader vchr = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
                true, vchrTyp);

        List<DisbursementVoucherHeader> vouchers = disbursementVoucherHeaderRepository.findAllByLoanAppSeqAndCrntRecFlg(loanAppSeq,
                true);
        if (vouchers != null) {
            List<Long> hdrSeqs = vouchers.stream().map(DisbursementVoucherHeader::getDsbmtHdrSeq).collect(Collectors.toList());
            List<DisbursementVoucherDetail> dtls = disbursementVoucherDetailRepository.findAllByDsbmtHdrSeqInAndCrntRecFlg(hdrSeqs,
                    true);
            dtls.forEach(dtl -> {
                dtl.setEffEndDt(Instant.now());
                dtl.setLastUpdBy(currUser);
                dtl.setLastUpdDt(Instant.now());
                dtl.setDelFlg(true);
                dtl.setCrntRecFlg(false);
            });
            disbursementVoucherDetailRepository.save(dtls);
            vouchers.forEach(v -> {
                v.setEffEndDt(Instant.now());
                v.setLastUpdBy(currUser);
                v.setLastUpdDt(Instant.now());
                v.setDelFlg(true);
                v.setCrntRecFlg(false);
            });
            disbursementVoucherHeaderRepository.save(vouchers);
        }

        PaymentScheduleHeader pyment = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pyment != null) {
            List<PaymentScheduleDetail> dtls = paymentScheduleDetailRepository
                    .findAllByPaySchedHdrSeqAndCrntRecFlgOrderByInstNumAsc(pyment.getPaySchedHdrSeq(), true);
            List<Long> dtlSeqs = dtls.stream().map(PaymentScheduleDetail::getPaySchedDtlSeq).collect(Collectors.toList());
            List<PaymentScheduleChargers> chrgs = paymentScheduleChargersRepository.findAllByPaySchedDtlSeqInAndCrntRecFlg(dtlSeqs,
                    true);
            chrgs.forEach(c -> {
                c.setEffEndDt(Instant.now());
                c.setLastUpdBy(currUser);
                c.setLastUpdDt(Instant.now());
                c.setDelFlg(true);
                c.setCrntRecFlg(false);
            });
            dtls.forEach(d -> {
                d.setEffEndDt(Instant.now());
                d.setLastUpdBy(currUser);
                d.setLastUpdDt(Instant.now());
                d.setDelFlg(true);
                d.setCrntRecFlg(false);
            });

            pyment.setEffEndDt(Instant.now());
            pyment.setLastUpdBy(currUser);
            pyment.setLastUpdDt(Instant.now());
            pyment.setDelFlg(true);
            pyment.setCrntRecFlg(false);
            paymentScheduleChargersRepository.save(chrgs);
            paymentScheduleDetailRepository.save(dtls);
            paymentScheduleHeaderRespository.save(pyment);
        }

        MwPdcHdr pdc = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pdc != null) {
            List<MwPdcDtl> dts = mwPdcDtlRepository.findAllByPdcHdrSeqAndCrntRecFlg(pdc.getPdcHdrSeq(), true);
            dts.forEach(d -> {
                d.setEffEndDt(Instant.now());
                d.setLastUpdBy(currUser);
                d.setLastUpdDt(Instant.now());
                d.setDelFlg(true);
                d.setCrntRecFlg(false);
            });

            pdc.setEffEndDt(Instant.now());
            pdc.setLastUpdBy(currUser);
            pdc.setLastUpdDt(Instant.now());
            pdc.setDelFlg(true);
            pdc.setCrntRecFlg(false);
            mwPdcDtlRepository.save(dts);
            pdcHeaderRepository.save(pdc);
        }

        // Added by Areeba
        try {
            MwExp exp = mwExpRepository.findOneByExpRefAndCrntRecFlg(String.valueOf(loanAppSeq), true);
            if (exp != null) {
                exp.setEffEndDt(Instant.now());
                exp.setLastUpdBy(currUser);
                exp.setLastUpdDt(Instant.now());
                exp.setDelFlg(true);
                exp.setCrntRecFlg(false);

                mwExpRepository.save(exp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        MwRefCdVal subSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, SUBMITTED_STATUS);
        MwRefCdVal aprvSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, APROVED_STATUS);

        app.setDelFlg(type.getRefCd().equals(KSK_TYPE) ? true : false);
        app.setCrntRecFlg(type.getRefCd().equals(KSK_TYPE) ? false : true);
        app.setCmnt(reson);
        app.setLoanAppSts(vchrTyp == 1 ? aprvSts.getRefCdSeq().longValue() : subSts.getRefCdSeq().longValue());
        app.setLoanAppStsDt(now);
        app.setLastUpdDt(now);
        app.setLastUpdBy("w-" + currUser);
        loanApplicationRepository.save(app).getLoanAppSeq();
        if (vchrTyp == 1 && vchr != null) {
            vchrPostingService.reverseVchrPosting(vchr.getDsbmtHdrSeq(), DISBURSEMENT, currUser, DISBURSEMENT);
        }

        return true;

    }

    @Transactional
    public boolean deferedLoanApp(long loanAppSeq, String reson, String currUser, String token, String role) throws Exception {

        boolean rtnVal = false;

        log.info("deferedLoanApp -> LoanAppSeq: " + loanAppSeq + ", Role: " + role + ", Reason: " + reson);

        String parmOutputProcedure = "";
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRC_LOAN_APP_DEFER");
        storedProcedure.registerStoredProcedureParameter("P_LOAN_APP_SEQ", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_USER_ROLE", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_REMARKS", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_USER_ID", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("OP_RTN_MSG", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("P_LOAN_APP_SEQ", loanAppSeq);
        storedProcedure.setParameter("P_USER_ROLE", role);
        storedProcedure.setParameter("P_REMARKS", reson);
        storedProcedure.setParameter("P_USER_ID", currUser);

        storedProcedure.execute();
        parmOutputProcedure = storedProcedure.getOutputParameterValue("OP_RTN_MSG").toString();

        // ZOHAIB ASIM - DATED 3-11-2021 - SANCTION LIST PHASE 2
        // CONDITIONS MODIFIED
        if (parmOutputProcedure.contains("SUCCESS")) {
            log.info("PRC_LOAN_APP_DEFER: Successfully Executed.");
            rtnVal = true;
        } else if (parmOutputProcedure.contains("FAILED")) {
            log.error("PRC_LOAN_APP_DEFER:" + "Execution Failed.");
            rtnVal = false;
        }

        return rtnVal;


        /* Commented by Zohaib Asim - Dated 11-08-2022 - Logic moved on Procedure
        Instant now = Instant.now();
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(app.getClntSeq(), true);
        String clntName = clnt.getFrstNm() + " " + clnt.getLastNm();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(app.getPrdSeq(), true);

        MwPrdGrp grp = mwPrdGrpRepository.findOneByLoanAppSeq(loanAppSeq);
        MwRefCdVal prdTyp = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);

        if (prdTyp.getRefCd().equals("0002")) { // ISLAMIC PRODUCT
            MwRefCdVal appSts = mwRefCdValRepository.findOneByRefCdSeqAndCrntRecFlg(app.getLoanAppSts(), true);
            if (appSts.getRefCd().equals("0005")) { // ACTIVE

                MwRefCdVal advanceSts = mwRefCdValRepository.findRefCdByGrpAndVal("0106", "1305");

                DisbursementVoucherHeader saleTwoVchr = disbursementVoucherHeaderRepository
                    .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 0);
                String dateString = format.format(Date.from(saleTwoVchr.getDsbmtDt()));
                saleTwoVchr.setDsbmtVchrTyp(1);
                DateFormat formatDT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Date defaultDate = formatDT.parse(defaultDateStr);
                saleTwoVchr.setDsbmtDt(defaultDate.toInstant());

                disbursementVoucherHeaderRepository.save(saleTwoVchr);
                List<DisbursementVoucherDetail> saleTwovoucherDtls = disbursementVoucherDetailRepository
                    .findAllByDsbmtHdrSeqAndCrntRecFlg(saleTwoVchr.getDsbmtHdrSeq(), true);
                Types advTyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0501", 1, 0, true);
                MwExp expense = mwExpRepository.findOneByExpRefAndExpnsTypSeqAndCrntRecFlgAndDelFlg("" + loanAppSeq, advTyp.getTypSeq(), true, false);

                String types = new String("");
                for (DisbursementVoucherDetail v : saleTwovoucherDtls) {
                    Types pymtType = typesRepository.findOneByTypSeqAndCrntRecFlg(v.getPymtTypSeq(), true);
                    if (pymtType != null)
                        types = types.concat(pymtType.getTypStr()).concat(",");

                    if (expense != null)
                        v.setPymtTypSeq(expense.getPymtTypSeq());
                }

                String desc = prd.getPrdCmnt() + " Sale-02 Disbursed Reversed date " + dateString + " Client " + clntName + " through "
                    + types;
                vchrPostingService.reverseVchrPosting(saleTwoVchr.getDsbmtHdrSeq(), DISBURSEMENT, currUser, desc);


                app.setLastUpdBy("w-" + currUser);
                app.setLastUpdDt(now);
                app.setLoanAppSts(advanceSts.getRefCdSeq());
                app.setLoanAppStsDt(now);
                loanApplicationRepository.save(app);
                disbursementVoucherDetailRepository.save(saleTwovoucherDtls);
                return true;
            }
        }

        // Added by Zohaib Asim - Dated 06-05-2022 - MCB Disbursement
        /*String  canProceedSts = disbursementVoucherHeaderRepository.getADCProceedingSts(loanAppSeq);
        if ( canProceedSts.equals("N") ){
            return false;
        }*//*
        // End

        DisbursementVoucherHeader voucher = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
            true, 0);
        if (prdTyp.getRefCd().equals("0002")) {
            MwRefCdVal appSts = mwRefCdValRepository.findOneByRefCdSeqAndCrntRecFlg(app.getLoanAppSts(), true);
            if (appSts.getRefCd().equals("1305")) {
                voucher = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq, true, 1);
            }
        }
        List<DisbursementVoucherHeader> vouchers = disbursementVoucherHeaderRepository.findAllByLoanAppSeqAndCrntRecFlg(loanAppSeq,
            true);
        String types = new String("");

        List<MwMobWalInfo> mobWalInfoList = new ArrayList<>();

        if (vouchers != null) {
            vouchers.forEach(v -> {
                v.setDelFlg(true);
                v.setCrntRecFlg(false);
                v.setLastUpdBy(currUser);
                v.setLastUpdDt(now);

                // Added by Zohaib Asim - Dated 10-05-2022 - Mobile Wallet Info
                if(v.getMobWalSeq() != null){
                    MwMobWalInfo mobWalInfo = mwMobWalInfoRepository.findByMobWalSeqAndCrntRecFlg(v.getMobWalSeq(), true);
                    if( mobWalInfo != null ){
                        mobWalInfo.setDelFlg(true);
                        mobWalInfo.setCrntRecFlg(false);
                        mobWalInfo.setLastUpdBy(currUser);
                        mobWalInfo.setLastUpdDt(Instant.now());

                        mobWalInfoList.add(mobWalInfo);
                    }
                }
                // End
            });
            disbursementVoucherHeaderRepository.save(vouchers);

            // Added by Zohaib Asim - Dated 10-05-2022 - MCB Disbursement Queue
            mwMobWalInfoRepository.save(mobWalInfoList);
            // End

            List<Long> vcherhdrSeqs = vouchers.stream().map(d -> d.getDsbmtHdrSeq()).collect(Collectors.toList());
            List<DisbursementVoucherDetail> dtls = disbursementVoucherDetailRepository.findAllByDsbmtHdrSeqInAndCrntRecFlg(vcherhdrSeqs,
                true);
            List<MwAdcDsbmtQue> adcDsbmtQueList = new ArrayList<>();
            for (DisbursementVoucherDetail v : dtls) {
                v.setDelFlg(true);
                v.setCrntRecFlg(false);
                v.setLastUpdBy(currUser);
                v.setLastUpdDt(now);
                Types pymtType = typesRepository.findOneByTypSeqAndCrntRecFlg(v.getPymtTypSeq(), true);
                if (pymtType != null)
                    types = types.concat(pymtType.getTypStr()).concat(",");

                // Added by Zohaib Asim - Dated 10-05-2022 - MCB Disbursement Queue
                MwAdcDsbmtQue adcDsbmtQue = mwAdcDsbmtQueRepository.findMwAdcDsbmtQueByCrntRecFlgAndDsbmtDtlKey(true, v.getDsbmtDtlKey());
                if(adcDsbmtQue != null ){
                    MwRefCdVal refCdVal = mwRefCdValRepository.findRefCdByGrpAndVal("0040", "0007");
                    if ( refCdVal != null ){
                        adcDsbmtQue.setAdcStsSeq(refCdVal.getRefCdSeq());
                        adcDsbmtQue.setAdcStsDt(new Date());
                        adcDsbmtQue.setLastUpdBy(currUser);
                        adcDsbmtQue.setLastUpdDt(new Date());

                        adcDsbmtQueList.add(adcDsbmtQue);
                    }
                }
                // End
            }
            disbursementVoucherDetailRepository.save(dtls);

            // Added by Zohaib Asim - Dated 10-05-2022 - MCB Disbursement Queue
            mwAdcDsbmtQueRepository.save(adcDsbmtQueList);
            // End
        }

        // LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg( voucher.getLoanAppSeq(), true );
        //// Types pymtType = typesRepository.findOneByTypSeqAndCrntRecFlg( voucher.get, true );
        if (prdTyp.getRefCd().equals("0002")) {
            MwRefCdVal appSts = mwRefCdValRepository.findOneByRefCdSeqAndCrntRecFlg(app.getLoanAppSts(), true);
            if (appSts.getRefCd().equals("1305")) {
                Types advTyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0501", 1, 0, true);
                MwExp expense = mwExpRepository.findOneByExpRefAndExpnsTypSeqAndCrntRecFlgAndDelFlg("" + loanAppSeq, advTyp.getTypSeq(), true, false);
                expense.setDelFlg(true);
                expense.setLastUpdBy(currUser);
                expense.setLastUpdDt(now);
                String dateString = format.format(Date.from(expense.getCrtdDt()));
                String desc = new String("Sale-01 Expense Reversed - ").concat(expense.getExpnsDscr()).concat(" dated ")
                    .concat(dateString);
                vchrPostingService.reverseVchrPosting(expense.getExpSeq(), "Expense", currUser, desc);
                mwExpRepository.save(expense);
            }
        } else {

            String dateString = format.format(Date.from(voucher.getDsbmtDt()));
            String desc = prd.getPrdCmnt() + " Disbursed Reversed date " + dateString + " Client " + clntName + " through " + types;
            vchrPostingService.reverseVchrPosting(voucher.getDsbmtHdrSeq(), DISBURSEMENT, currUser, desc);
        }
        PaymentScheduleHeader pyment = paymentScheduleHeaderRespository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pyment != null) {
            pyment.setDelFlg(true);
            pyment.setCrntRecFlg(false);
            pyment.setLastUpdBy(currUser);
            pyment.setLastUpdDt(now);
            paymentScheduleHeaderRespository.save(pyment);

            List<PaymentScheduleDetail> schDtls = paymentScheduleDetailRepository
                .findAllByPaySchedHdrSeqAndCrntRecFlgOrderByInstNumAsc(pyment.getPaySchedHdrSeq(), true);
            if (schDtls != null) {
                schDtls.forEach(schDtl -> {
                    schDtl.setDelFlg(true);
                    schDtl.setCrntRecFlg(false);
                    schDtl.setLastUpdBy(currUser);
                    schDtl.setLastUpdDt(now);
                });
                paymentScheduleDetailRepository.save(schDtls);

                List<Long> schDtlSeqs = schDtls.stream().map(d -> d.getPaySchedDtlSeq()).collect(Collectors.toList());
                List<PaymentScheduleChargers> dtls = paymentScheduleChargersRepository.findAllByPaySchedDtlSeqInAndCrntRecFlg(schDtlSeqs,
                    true);
                dtls.forEach(v -> {
                    v.setDelFlg(true);
                    v.setCrntRecFlg(false);
                    v.setLastUpdBy(currUser);
                    v.setLastUpdDt(now);
                });
                paymentScheduleChargersRepository.save(dtls);
            }

        }

        MwPdcHdr pdc = pdcHeaderRepository.findOneByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        if (pdc != null) {
            pdc.setDelFlg(true);
            pdc.setCrntRecFlg(false);
            pdc.setLastUpdBy(currUser);
            pdc.setLastUpdDt(now);
            pdcHeaderRepository.save(pdc);

            List<MwPdcDtl> pdcDtls = mwPdcDtlRepository.findAllByPdcHdrSeqAndCrntRecFlgOrderByPdcIdAsc(pdc.getPdcHdrSeq(), true);

            pdcDtls.forEach(schDtl -> {
                schDtl.setDelFlg(true);
                schDtl.setCrntRecFlg(false);
                schDtl.setLastUpdBy(currUser);
                schDtl.setLastUpdDt(now);
            });
            mwPdcDtlRepository.save(pdcDtls);
        }

        loanServiceClient.defferApplication(loanAppSeq, token);

        // KASHF RECAPITALIZATION KARZA
        if (grp.getPrdGrpId().equals("5765")) {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("recovery.reverse_krk_recovery");
            storedProcedure.registerStoredProcedureParameter("p_clnt_seq", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("mUsrId", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("p_msg", String.class, ParameterMode.OUT);

            storedProcedure.setParameter("p_clnt_seq", app.getClntSeq());
            storedProcedure.setParameter("mUsrId", currUser);
            // execute SP
            storedProcedure.execute();
            // get result
            String resp = (String) storedProcedure.getOutputParameterValue("p_msg");
            if (resp.contains("Recovery reversal failed")) {
                throw new Exception();
            }
        }

        return true;*/

    }

    public String revertKrkLoan(long loanAppSeq, String currUser) {
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("recovery.reverse_krk_recovery");
        storedProcedure.registerStoredProcedureParameter("p_clnt_seq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mUsrId", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_msg", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("p_clnt_seq", app.getClntSeq());
        storedProcedure.setParameter("mUsrId", currUser);
        // execute SP
        storedProcedure.execute();
        // get result
        return (String) storedProcedure.getOutputParameterValue("p_msg");
    }

    @Transactional
    public Boolean saveVouchers(long loanAppSeq, String currUser) {

        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        MwRefCdVal sts = mwRefCdValRepository.findAppTypeByLoanApp(loanAppSeq);
        MwRefCdVal type = mwRefCdValRepository.findProductTypeByLoanApp(loanAppSeq);

        DisbursementVoucherHeader vhdr = disbursementVoucherHeaderRepository.findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(loanAppSeq,
                true, 0);
        MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(app.getPrdSeq(), true);

        vchrPostingService.reverseVchrPosting(vhdr.getDsbmtHdrSeq(), DISBURSEMENT, currUser, DISBURSEMENT);

        vchrPostingService.genrateDisbVchr(app, prd.getPrdCmnt(), currUser, sts.getRefCd(), type.getRefCd(), vhdr.getDsbmtVchrTyp());

        return true;

    }

    public Long getCheckAmount(long loanAppSeq) {
        // String loanOstQuery = "select get_KRK_dsb_amt( " + loanAppSeq + ") from dual";
        // return new BigDecimal( em.createNativeQuery( loanOstQuery ).getSingleResult().toString() ).longValue();
        Long vDsbAmt = 0L;
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("get_KRK_dsb_amt_");
            // storedProcedure.s
            storedProcedure.registerStoredProcedureParameter("p_loan_app_seq", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("v_dsb_amt", Long.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("P_PRD_GRP_SEQ", Long.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("v_msg", String.class, ParameterMode.OUT);

            storedProcedure.setParameter("p_loan_app_seq", loanAppSeq);
            // execute SP
            storedProcedure.execute();
            // get result
            vDsbAmt = (Long) storedProcedure.getOutputParameterValue("v_dsb_amt");
            Long prdGrpSeq = (Long) storedProcedure.getOutputParameterValue("P_PRD_GRP_SEQ");
            String v_msg = (String) storedProcedure.getOutputParameterValue("v_msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vDsbAmt;
    }


    /* Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * save mobile wallet info */
    public long saveMobileWalletInfo(DisbursementVoucherDetailDTO detailDTO, String currUser) {
        long mobileWalletFlag = 0;
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(detailDTO.loanAppSeq, true);
        List<MwMobWalInfo> mobWalInfoList = mwMobWalInfoRepository.findAllByClntSeqAndCrntRecFlgOrderByMobWalChnlAscCrtdDtDesc(app.getClntSeq(), true);

        for (MwMobWalInfo mobWalInfo : mobWalInfoList) {
            if ((detailDTO.mobWalNum != null && !detailDTO.mobWalNum.isEmpty()) || (detailDTO.mobWalChnl != null && !detailDTO.mobWalChnl.isEmpty())) {
                if (mobWalInfo.getMobWalNo().equals(detailDTO.mobWalNum) && mobWalInfo.getMobWalChnl().equals(detailDTO.mobWalChnl)) {
                    mobileWalletFlag = mobWalInfo.getMobWalSeq();
                    break;
                }
            }
        }
        if (mobWalInfoList.isEmpty() || mobileWalletFlag == 0) {
            if ((detailDTO.mobWalNum != null && !detailDTO.mobWalNum.isEmpty()) || (detailDTO.mobWalChnl != null && !detailDTO.mobWalChnl.isEmpty())) {
                MwMobWalInfo mobInfo = new MwMobWalInfo();
                mobInfo.setMobWalSeq(SequenceFinder.findNextVal(Sequences.MOB_WAL_SEQ));
                mobInfo.setClntSeq(app.getClntSeq());
                mobInfo.setMobWalNo(detailDTO.mobWalNum);
                mobInfo.setMobWalChnl(detailDTO.mobWalChnl);
                mobInfo.setCrntRecFlg(true);
                mobInfo.setDelFlg(false);
                mobInfo.setCrtdBy(currUser);
                mobInfo.setCrtdDt(Instant.now());
                mobileWalletFlag = mwMobWalInfoRepository.save(mobInfo).getMobWalSeq();
            }
        }
        return mobileWalletFlag;
    }
    // Ended by Naveed - Date 23-01-2022

    /* Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * mobile wallet history against client */
    public List<Map<String, String>> getClientMobileWalletHist(long loanAppSeq) {
        List<Map<String, String>> resp = new ArrayList<>();

        LoanApplication loanApp = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        List<MwMobWalInfo> mobWalInfoList = mwMobWalInfoRepository.findAllByClntSeqAndCrntRecFlgOrderByMobWalChnlAscCrtdDtDesc(loanApp.getClntSeq(), true);

        if (!mobWalInfoList.isEmpty()) {
            mobWalInfoList.forEach(wallet -> {
                Map<String, String> map = new HashMap<>();
                map.put("mobWalChnl", wallet.getMobWalChnl());
                map.put("mobWalNum", wallet.getMobWalNo());
                resp.add(map);
            });
        }
        return resp;
    }
    // Ended By Naveed - Date 23-01-2022

    /* Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * is mobile wallet valid */
    public Map<String, String> isMobileWalletNumValid(DisbursementVoucherDetailDTO detailDTO) {

        Map<String, String> mobileInValidResp = new HashMap<>();
        if (detailDTO.mobWalNum != null || detailDTO.mobWalChnl != null) {
            LoanApplication loanApp = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(detailDTO.loanAppSeq, true);
            List<MwMobWalInfo> mobWalInfoList = mwMobWalInfoRepository.findAllByMobWalNoAndCrntRecFlgAndClntSeqNotIn(detailDTO.mobWalNum, true, loanApp.getClntSeq());

            if (!mobWalInfoList.isEmpty()) {
                Clients client = clientRepository.findOneByClntSeqAndCrntRecFlg(mobWalInfoList.get(0).getClntSeq(), true);
                mobileInValidResp.put("mobInvalid", "Mobile No. " + mobWalInfoList.get(0).getMobWalNo() + " already registered with Client Id "
                        + client.getClntId() + " Client Name " + client.getFrstNm() + " " + client.getLastNm() + " on Loan Id " + detailDTO.loanAppSeq);
            }
        }
        return mobileInValidResp;
    }
    // Ended By Naveed - Date - 23-01-2022

    /* Added By Naveed - Date - 23-01-2022
     * SCR - Mobile Wallet Control
     * all mobile wallet channel */
    public List<TypesDTO> getMobileWalletTypes() {
        List<Object[]> types = typesRepository.findAllByTypStr();
        List<TypesDTO> dtoList = new ArrayList();
        if (types != null && types.size() > 0) {
            types.forEach(typ -> {
                TypesDTO typesDTO = new TypesDTO();
                typesDTO.typStr = typ[0] == null ? "" : typ[0].toString();
                typesDTO.typId = typ[1] == null ? "0" : typ[1].toString();
                dtoList.add(typesDTO);
            });
        }
        return dtoList;
    }
    // Ended By Naveed - Date - 23-01-2022


    // Added by Areeba - Date - 18-10-2022
    @Transactional
    public Map<Boolean, String> discardLoanApp(long loanAppSeq, String reason, String currUser, String token) {

        Map<Boolean, String> discardResp = new HashMap<>();
        Instant now = Instant.now();
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.APPLICATION_STS).setParameter("refCd", ACTIVE_STATUS);
        Object obj = q.getSingleResult();

        String parmOutputProcedure = "";
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRC_DISCARD_LOAN_APP");
        storedProcedure.registerStoredProcedureParameter("P_LOAN_APP_SEQ", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_USER", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_RSN", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("P_RTN_MSG", String.class, ParameterMode.OUT);

        storedProcedure.setParameter("P_LOAN_APP_SEQ", String.valueOf(loanAppSeq));
        storedProcedure.setParameter("P_USER", currUser);
        storedProcedure.setParameter("P_RSN", reason);
        storedProcedure.execute();

        System.out.println(storedProcedure.getOutputParameterValue("P_RTN_MSG"));

        parmOutputProcedure = storedProcedure.getOutputParameterValue("P_RTN_MSG").toString();
        if (parmOutputProcedure.contains("success")) {
            discardResp.put(true, parmOutputProcedure);
            return discardResp;
        } else {
            log.info("Exception : " + parmOutputProcedure);
            discardResp.put(false, parmOutputProcedure);
            return discardResp;
        }
    }
}
