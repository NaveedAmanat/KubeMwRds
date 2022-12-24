package com.idev4.rds.service;

import com.idev4.rds.domain.*;
import com.idev4.rds.dto.*;
import com.idev4.rds.feignclient.AdminServiceClient;
import com.idev4.rds.feignclient.SetupServiceClient;
import com.idev4.rds.repository.*;
import com.idev4.rds.util.SequenceFinder;
import com.idev4.rds.util.Sequences;
import com.idev4.rds.util.StaticValusOnStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecoveryService {

    private static final String COMPLETED_STATUS = "0006";
    private static final String ACTIVE_STATUS = "0005";
    private static final String WRITEOFF_STATUS = "1245";
    private static final String APPLICATIONS_STATUS = "0106";
    // Added by Zohaib Asim - Dated 23-02-2021
    // For Partial settlements
    private static final String PARTIAL_STATUS = "1145";
    private static final String PAYMENT_STATUS = "0179";
    private static final String AccessRecovery = "Access Recovery";
    // End by Zohaib Asim
    private static final String RECOVERY = "Recovery";
    private static final String DESC_LOAN_ADJUSTED = "Loan is adjusted against death case";
    private static final String DESC_RECOVERY = "KKK Recovery received from Client Samina Kouser through UBL OMNI";
    private static final String ExcessRecovery = "EXCESS RECOVERY";
    private static final String WORecovery = "WO Recovery";
    private final Logger log = LoggerFactory.getLogger(VchrPostingService.class);
    private final MwRcvryDtlRepository mwRcvryRepository;
    private final PaymentScheduleDetailRepository paymentScheduleDetailRepository;
    private final PaymentScheduleChargersRepository paymentScheduleChargersRepository;
    private final VchrPostingService vchrPostingService;
    private final ClientRepository clientRepository;
    private final EntityManager em;
    private final RecoveryComponent recoveryComponent;
    private final LoanApplicationRepository loanApplicationRepository;
    private final MwRcvryTrxRepository mwRcvryTrxRepository;
    private final MwRcvryLoadStgRepository mwRcvryLoadStgRepository;
    private final TypesRepository typesRepository;
    private final MwExpRepository mwExpRepository;
    private final MwPortRepository mwPortRepository;
    private final MwRefCdValRepository mwRefCdValRepository;
    @Autowired
    SetupServiceClient setupServiceClient;

    @Autowired
    MwDthRptRepository mwDthRptRepository;

    //Added by Areeba
    @Autowired
    MwDsbltyRptRepository mwDsbltyRptRepository;

    @Autowired
    MwClntRelRepository mwClntRelRepository;

    @Autowired
    MwBrnchRepository mwBrnchRepository;

    @Autowired
    BackJobRepository backJobRepository;

    @Autowired
    MwPrdRepository mwPrdRepository;

    @Autowired
    DisbursementVoucherHeaderRepository disbursementVoucherHeaderRepository;

    @Autowired
    PaymentScheduleHeaderRespository paymentScheduleHeaderRespository;

    @Autowired
    AdminServiceClient adminServiceClient;

    @Autowired
    MwWrtOfRcvryTrxRepository mwWrtOfRcvryTrxRepository;

    @Autowired
    MwJvHdrRepository mwJvHdrRepository;

    public RecoveryService(MwRcvryDtlRepository mwRcvryRepository, PaymentScheduleDetailRepository paymentScheduleDetailRepository,
                           PaymentScheduleChargersRepository paymentScheduleChargersRepository, VchrPostingService vchrPostingService, EntityManager em,
                           ClientRepository clientRepository, RecoveryComponent recoveryComponent, LoanApplicationRepository loanApplicationRepository,
                           MwRcvryTrxRepository mwRcvryTrxRepository, MwRcvryLoadStgRepository mwRcvryLoadStgRepository, TypesRepository typesRepository,
                           MwExpRepository mwExpRepository, MwPortRepository mwPortRepository, MwRefCdValRepository mwRefCdValRepository,
                           MwDthRptRepository mwDthRptRepository, MwDsbltyRptRepository mwDsbltyRptRepository) {
        super();
        this.mwRcvryRepository = mwRcvryRepository;
        this.paymentScheduleDetailRepository = paymentScheduleDetailRepository;
        this.paymentScheduleChargersRepository = paymentScheduleChargersRepository;
        this.vchrPostingService = vchrPostingService;
        this.em = em;
        this.clientRepository = clientRepository;
        this.recoveryComponent = recoveryComponent;
        this.loanApplicationRepository = loanApplicationRepository;
        this.mwRcvryTrxRepository = mwRcvryTrxRepository;
        this.mwRcvryLoadStgRepository = mwRcvryLoadStgRepository;
        this.typesRepository = typesRepository;
        this.mwExpRepository = mwExpRepository;
        this.mwPortRepository = mwPortRepository;
        this.mwRefCdValRepository = mwRefCdValRepository;
        this.mwDthRptRepository = mwDthRptRepository;
        this.mwDsbltyRptRepository = mwDsbltyRptRepository;
    }

    public boolean isRecoveryPosted(Long id) {
        MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(id, true);
        return (trx.getPostFlg().longValue() > 0) ? true : false;
    }

    public Map<?, ?> getAllRecovery(String userId, String filter, String sort, String directions, int pageNumber, int pageSize,
                                    Long brnchSeq, Boolean isCount) {

        String search = "where lower(clnt_id) like '%?%' OR lower(frst_nm) like '%?%' or lower(last_nm) like '%?%' or lower(emp_nm) like '%?%' or to_char(nd,'dd-MM-yyyy') like '%?%' or cnic_num like '%?%' "
                .replace("?", filter.toLowerCase());

        String query = "select * from (select *\r\n"
                + "  from (select clnt_seq,\r\n"
                + "               clnt_id,\r\n"
                + "               frst_nm,\r\n"
                + "               last_nm,\r\n"
                + "               listagg(prd_cmnt, ',') within\r\n"
                + "         group(\r\n"
                + "         order by prd_cmnt) prd_cmnt, emp_nm, sum(totl_due), sum(recov), min(nd) nd, REF_CD_DSCR, cnic_num, prnt_loan_app_seq\r\n"
                + "          from (select c.clnt_seq,\r\n"
                + "                       c.clnt_id,\r\n"
                + "                       c.frst_nm,\r\n"
                + "                       c.last_nm,\r\n"
                + "                       c.cnic_num,\r\n"
                + "                       prd.prd_cmnt,\r\n"
                + "                       e.emp_nm,la.prnt_loan_app_seq,\r\n"
                + "                       (select sum(psd.ppal_amt_due) + sum(psd.tot_chrg_due) +\r\n"
                + "                               nvl(sum((select sum(nvl(amt, 0)) amt\r\n"
                + "                                         from mw_pymt_sched_chrg psc\r\n"
                + "                                        where crnt_rec_flg = 1\r\n"
                + "                                          and psc.pymt_sched_dtl_seq =\r\n"
                + "                                              psd.pymt_sched_dtl_seq)),\r\n"
                + "                                   0)\r\n"
                + "                          from mw_pymt_sched_hdr psh\r\n"
                + "                          join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq =\r\n"
                + "                                                        psh.pymt_sched_hdr_seq\r\n"
                + "                                                    and psd.crnt_rec_flg = 1\r\n"
                + "                         where psh.crnt_rec_flg = 1\r\n"
                + "                           and psh.loan_app_seq = la.loan_app_seq) totl_due,\r\n"
                + "                       (select sum(rd.pymt_amt)\r\n"
                + "                          from mw_pymt_sched_hdr psh\r\n"
                + "                          join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq =\r\n"
                + "                                                        psh.pymt_sched_hdr_seq\r\n"
                + "                                                    and psd.crnt_rec_flg = 1\r\n"
                + "                          join mw_rcvry_dtl rd on rd.pymt_sched_dtl_seq =\r\n"
                + "                                                  psd.pymt_sched_dtl_seq\r\n"
                + "                                              and rd.crnt_rec_flg = 1\r\n"
                + "                         where psh.loan_app_seq = la.loan_app_seq\r\n"
                + "                           and psh.crnt_rec_flg = 1) recov,\r\n"
                + "                       (select min(due_dt) next_due\r\n"
                + "                          from mw_pymt_sched_hdr psh1\r\n"
                + "                          join mw_pymt_sched_dtl psd1 on psh1.pymt_sched_hdr_seq =\r\n"
                + "                                                         psd1.pymt_sched_hdr_seq\r\n"
                + "                                                     and psd1.crnt_rec_flg = 1\r\n"
                + "                          join mw_ref_cd_val val on psd1.pymt_sts_key =\r\n"
                + "                                                    val.ref_cd_seq\r\n"
                + "                                                and val.crnt_rec_flg = 1\r\n"
                + "                          join mw_ref_cd_grp grp on grp.ref_cd_grp_seq =\r\n"
                + "                                                    val.ref_cd_grp_key\r\n"
                + "                                                and grp.crnt_rec_flg = 1\r\n"
                + "                         where psh1.crnt_rec_flg = 1\r\n"
                + "                           and val.ref_cd = '0945'\r\n"
                + "                           and grp.ref_cd_grp = '0179'\r\n"
                + "                           and to_date(due_dt) >= to_date(sysdate)\r\n"
                + "                           and psh1.loan_app_seq = la.loan_app_seq) nd,\r\n"
                + "                       val.REF_CD_DSCR\r\n"
                + "                  from mw_loan_app la\r\n"
                + "                  join mw_prd prd on prd.prd_seq = la.prd_seq\r\n"
                + "                                 and prd.crnt_rec_flg = 1\r\n"
                + "                  join mw_clnt c on c.clnt_seq = la.clnt_seq\r\n"
                + "                                and c.crnt_rec_flg = 1\r\n"
                + "                  join mw_port p on la.port_seq = p.port_seq\r\n"
                + "                                and p.crnt_rec_flg = 1\r\n"
                + "                  left outer join mw_port_emp_rel per on per.port_seq =\r\n"
                + "                                                         p.port_seq\r\n"
                + "                                                     and per.crnt_rec_flg = 1\r\n"
                + "                  left outer join mw_emp e on e.emp_seq = per.emp_seq\r\n"
                + "                  join mw_ref_cd_val val on val.ref_cd_seq = la.loan_app_sts\r\n"
                + "                                        and val.crnt_rec_flg = 1\r\n"
                + "                                        and (val.ref_cd = '0005' or\r\n"
                + "                                            val.ref_cd = '1245' or\r\n"
                + "                                            val.ref_cd = '0006')\r\n"
                + "                  join mw_ref_cd_grp grp on grp.ref_cd_grp_seq =\r\n"
                + "                                            val.ref_cd_grp_key\r\n"
                + "                                        and grp.crnt_rec_flg = 1\r\n"
                + "                                        and grp.ref_cd_grp = '0106'\r\n"
                + "                 where la.crnt_rec_flg = 1\r\n"
                + "                   and la.LOAN_CYCL_NUM in\r\n"
                + "                       (select loan_cycl_num\n" +
                "    from mw_loan_app ap\n" +
                "    where ap.clnt_seq = c.clnt_seq\n" +
                "    and ap.crnt_rec_flg = 1\n" +
                "    and ap.loan_app_sts in (703,704)\n" +
                "    union\n" +
                "    select max(loan_cycl_num)-1\n" +
                "    from mw_loan_app ap\n" +
                "    where ap.clnt_seq = c.clnt_seq\n" +
                "    and ap.crnt_rec_flg = 1\n" +
                "    and ap.loan_app_sts in (703, 704)\n" +
                "    order by 1 desc\n" +
                "    fetch first 3 row only)\r\n"
                + "                   and p.brnch_seq = :brnchSeq)\r\n"
                + "         group by clnt_seq,\r\n"
                + "                  clnt_id,\r\n"
                + "                  frst_nm,\r\n"
                + "                  last_nm,\r\n"
                + "                  emp_nm,\r\n"
                + "                  REF_CD_DSCR,\r\n"
                + "                  cnic_num,prnt_loan_app_seq)\r\n"
                + " order by nd nulls last)".concat(filter.isEmpty() ? "" : search);

        Query q = em.createNativeQuery(query).setParameter("brnchSeq", brnchSeq).setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize);

        List<Object[]> results = q.getResultList();
        List<RecoveryListingDTO> recoveryListingDTOs = new ArrayList<RecoveryListingDTO>();
        if (results != null && results.size() != 0) {
            results.forEach(obj -> {
                RecoveryListingDTO recoveryListingDTO = new RecoveryListingDTO();
                recoveryListingDTO.clntSeq = obj[0].toString();
                recoveryListingDTO.clntId = obj[1].toString();
                recoveryListingDTO.frstNm = obj[2] == null ? "" : obj[2].toString();
                recoveryListingDTO.lastNm = obj[3] == null ? "" : obj[3].toString();
                recoveryListingDTO.prd = obj[4].toString();
                recoveryListingDTO.bdo = obj[5] == null ? "" : obj[5].toString();
                recoveryListingDTO.totalDue = obj[6] == null ? 0 : new BigDecimal(obj[6].toString()).longValue();
                recoveryListingDTO.totalRecv = obj[7] == null ? 0 : new BigDecimal(obj[7].toString()).longValue();
                recoveryListingDTO.nextDue = obj[8] == null ? "" : obj[8].toString();
                recoveryListingDTO.status = obj[9] == null ? "" : obj[9].toString();
                recoveryListingDTO.prntLoanAppSeq = obj[11] == null ? 0 : new BigDecimal(obj[11].toString()).longValue();
                recoveryListingDTOs.add(recoveryListingDTO);
            });
        }
        Map res = new HashMap();

        Long totalCount = 0l;

        // Modified by Areeba - 29-11-2022 - Count Removal
        if (isCount.booleanValue()) {
            totalCount = 100l;
//            String countSearch = " where lower(clnt_id) like '%?%' OR lower(frst_nm) like '%?%' or lower(last_nm) like '%?%' or to_char(aa.next_due,'dd-MM-yyyy') like '%?%'  or c.cnic_num like '%?%' "
//                .replace("?", filter.toLowerCase());
//            totalCount = new BigDecimal(em.createNativeQuery("SELECT COUNT (DISTINCT la.CLNT_SEQ)\n" +
//                    "  FROM mw_loan_app  la\n" +
//                    "       JOIN mw_clnt c ON c.clnt_seq = la.clnt_seq AND c.crnt_rec_flg = 1\n" +
//                    "       JOIN\n" +
//                    "       (  SELECT MIN (due_dt) next_due, psh1.loan_app_seq loan_app_seq\n" +
//                    "            FROM mw_pymt_sched_hdr psh1\n" +
//                    "                 JOIN mw_pymt_sched_dtl psd1\n" +
//                    "                     ON     psh1.pymt_sched_hdr_seq = psd1.pymt_sched_hdr_seq\n" +
//                    "                        AND psd1.crnt_rec_flg = 1\n" +
//                    "                        AND psd1.pymt_sts_key = 945\n" +
//                    "           WHERE psh1.crnt_rec_flg = 1\n" +
//                    "        GROUP BY psh1.loan_app_seq) aa\n" +
//                    "           ON     aa.loan_app_seq = la.loan_app_seq\n" +
//                    "              AND la.LOAN_CYCL_NUM =\n" +
//                    "                  (SELECT MAX (loan_cycl_num)\n" +
//                    "                     FROM mw_loan_app ap\n" +
//                    "                    WHERE     ap.clnt_seq = c.clnt_seq\n" +
//                    "                          AND ap.BRNCH_SEQ = la.brnch_Seq)\n" +
//                    "              AND la.crnt_rec_flg = 1\n" +
//                    "              AND la.brnch_seq = :brnchSeq\n" +
//                    "              AND la.loan_app_sts = 703"
//                    + ((brnchSeq == null) ? " " : (" and la.brnch_seq=" + brnchSeq)).concat(filter.isEmpty() ? "" : countSearch))
//                .setParameter("brnchSeq", brnchSeq).getSingleResult().toString()).longValue();
        }
        res.put("data", recoveryListingDTOs);
        res.put("count", totalCount);

        return res;

    }

    // public Map< ? , ? > getAllRecovery( String userId, String filter, String sort, String directions, int pageNumber, int pageSize,
    // Long brnchSeq ) {
    //
    // String search = "where lower(clnt_id) like '%?%' OR lower(frst_nm) like '%?%' or lower(last_nm) like '%?%' or lower(emp_nm) like
    // '%?%' or to_char(nd,'dd-MM-yyyy') like '%?%' or cnic_num like '%?%' "
    // .replace( "?", filter.toLowerCase() );
    //
    // String query = "select * from (select clnt_seq,clnt_id,frst_nm,last_nm, \r\n"
    // + "listagg(prd_cmnt,',') within group (order by prd_cmnt) prd_cmnt, emp_nm,sum(totl_due),sum(recov),min(nd) nd,REF_CD_DSCR,cnic_num
    // \r\n"
    // + "from ( select c.clnt_seq,c.clnt_id,c.frst_nm,c.last_nm,c.cnic_num, prd.prd_cmnt,e.emp_nm, \r\n"
    // + "(select sum(psd.ppal_amt_due)+sum(psd.tot_chrg_due)+nvl(sum(psc.amt),0) from mw_pymt_sched_hdr psh \r\n"
    // + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
    // + "left outer join (select pymt_sched_dtl_seq,sum(amt) amt from mw_pymt_sched_chrg where crnt_rec_flg=1 group by pymt_sched_dtl_seq)
    // psc on psc.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq \r\n"
    // + "where psh.crnt_rec_flg=1 and psh.loan_app_seq=la.loan_app_seq) totl_due, ( select sum(rd.pymt_amt) \r\n"
    // + "from mw_pymt_sched_hdr psh \r\n"
    // + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
    // + "join mw_rcvry_dtl rd on rd.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.crnt_rec_flg=1 \r\n"
    // + "where psh.loan_app_seq=la.loan_app_seq and psh.crnt_rec_flg=1 ) recov, (select min(due_dt) next_due\r\n"
    // + "from mw_pymt_sched_hdr psh1\r\n"
    // + "join mw_pymt_sched_dtl psd1 on psh1.pymt_sched_hdr_seq=psd1.pymt_sched_hdr_seq and psd1.crnt_rec_flg=1\r\n"
    // + "join mw_ref_cd_val val on psd1.pymt_sts_key=val.ref_cd_seq and val.crnt_rec_flg=1\r\n"
    // + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq=val.ref_cd_grp_key and grp.crnt_rec_flg = 1\r\n"
    // + "where psh1.crnt_rec_flg=1 and val.ref_cd='0945' and grp.ref_cd_grp='0179'\r\n"
    // + "and to_date(due_dt) >= to_date(sysdate) and psh1.loan_app_seq=la.loan_app_seq ) nd,val.REF_CD_DSCR\r\n"
    // + "from mw_loan_app la \r\n" + "join mw_acl acl on acl.port_seq = la.port_seq and acl.user_id =:userId \r\n"
    // + "join mw_prd prd on prd.prd_seq=la.prd_seq and prd.crnt_rec_flg = 1 \r\n"
    // + "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1 \r\n"
    // + "join mw_port p on la.port_seq=p.port_seq and p.crnt_rec_flg=1 \r\n"
    // + "left outer join mw_port_emp_rel per on per.port_seq=p.port_seq and per.crnt_rec_flg = 1 \r\n"
    // + "left outer join mw_emp e on e.emp_seq=per.emp_seq \r\n"
    // + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245' or
    // val.ref_cd ='0006') \r\n"
    // + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106' \r\n"
    // + "where la.crnt_rec_flg=1 and la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap where ap.clnt_seq=c.clnt_seq) "
    // + ( ( brnchSeq == null ) ? " " : ( " and p.brnch_seq=" + brnchSeq ) )
    // + ") group by clnt_seq,clnt_id,frst_nm,last_nm,emp_nm,REF_CD_DSCR,cnic_num \r\n"
    // + "order by nd nulls last)".concat( filter.isEmpty() ? "" : search );
    //
    // Query q = em.createNativeQuery( query ).setParameter( "userId", userId ).setFirstResult( ( pageNumber - 1 ) * pageSize )
    // .setMaxResults( pageSize );
    //
    // List< Object[] > results = q.getResultList();
    // List< RecoveryListingDTO > recoveryListingDTOs = new ArrayList< RecoveryListingDTO >();
    // if ( results != null && results.size() != 0 ) {
    // results.forEach( obj -> {
    // RecoveryListingDTO recoveryListingDTO = new RecoveryListingDTO();
    // recoveryListingDTO.clntSeq = obj[ 0 ].toString();
    //
    // recoveryListingDTO.clntId = obj[ 1 ].toString();
    //
    // recoveryListingDTO.frstNm = obj[ 2 ] == null ? "" : obj[ 2 ].toString();
    //
    // recoveryListingDTO.lastNm = obj[ 3 ] == null ? "" : obj[ 3 ].toString();
    // recoveryListingDTO.prd = obj[ 4 ].toString();
    //
    // recoveryListingDTO.bdo = obj[ 5 ] == null ? "" : obj[ 5 ].toString();
    //
    // recoveryListingDTO.totalDue = obj[ 6 ] == null ? 0 : new BigDecimal( obj[ 6 ].toString() ).longValue();
    //
    // recoveryListingDTO.totalRecv = obj[ 7 ] == null ? 0 : new BigDecimal( obj[ 7 ].toString() ).longValue();
    //
    // recoveryListingDTO.nextDue = obj[ 8 ] == null ? "" : obj[ 8 ].toString();
    //
    // recoveryListingDTO.status = obj[ 9 ] == null ? "" : obj[ 9 ].toString();
    // recoveryListingDTOs.add( recoveryListingDTO );
    // } );
    // }
    // Map res = new HashMap();
    // String countSearch = "where lower(clnt_id) like '%?%' OR lower(frst_nm) like '%?%' or lower(last_nm) like '%?%' or lower(emp_nm) like
    // '%?%' or to_char(aa.next_due,'dd-MM-yyyy') like '%?%' or c.cnic_num like '%?%' "
    // .replace( "?", filter.toLowerCase() );
    // Query count = em.createNativeQuery( "select count(distinct la.CLNT_SEQ)\r\n" + "from mw_loan_app la\r\n"
    // + "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1\r\n"
    // + "join mw_port p on la.port_seq=p.port_seq and p.crnt_rec_flg=1\r\n"
    // + "join mw_port_emp_rel per on per.PORT_SEQ=p.PORT_SEQ and per.CRNT_REC_FLG = 1\r\n"
    // + "join mw_emp e on e.EMP_SEQ=per.EMP_SEQ\r\n"
    // + "JOIN mw_acl acl ON acl.port_seq = la.port_seq AND acl.user_id =:userId\r\n"
    // + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245' or
    // val.ref_cd ='0006')\r\n"
    // + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
    // + "join (select min(due_dt) next_due,psh1.loan_app_seq loan_app_seq\r\n" + "from mw_pymt_sched_hdr psh1\r\n"
    // + "join mw_pymt_sched_dtl psd1 on psh1.pymt_sched_hdr_seq=psd1.pymt_sched_hdr_seq and psd1.crnt_rec_flg=1\r\n"
    // + "join mw_ref_cd_val val on psd1.pymt_sts_key=val.REF_CD_SEQ and val.CRNT_REC_FLG=1\r\n"
    // + "join mw_ref_cd_grp grp on grp.REF_CD_GRP_SEQ=val.REF_CD_GRP_KEY and grp.CRNT_REC_FLG = 1\r\n"
    // + "where psh1.crnt_rec_flg=1 and val.REF_CD='0945' and grp.REF_CD_GRP='0179'\r\n" + "group by psh1.loan_app_seq\r\n"
    // + ") aa on aa.loan_app_seq=la.loan_app_seq and la.crnt_rec_flg=1 and la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap
    // where ap.clnt_seq=c.clnt_seq)\r\n"
    // + ( ( brnchSeq == null ) ? " " : ( " and p.brnch_seq=" + brnchSeq ) ).concat( filter.isEmpty() ? "" : countSearch ) )
    // .setParameter( "userId", userId );
    //
    // Object obj = count.getSingleResult();
    // res.put( "data", recoveryListingDTOs );
    // res.put( "count", new BigDecimal( obj.toString() ).longValue() );
    //
    // return res;
    //
    // }

    /*
    Added changes for pportfolio transfer phase 2.0 Date: 14-02-2022
     */

    public List<SubRecoveryListingDTO> getSingleLoanRecovery(long clntSeq) {

        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.ALLONERECOVERYBYLOANAPP);
        q.setParameter("clntSeq", clntSeq);
        List<Object[]> results = q.getResultList();
        List<SubRecoveryListingDTO> recoveryListingDTOs = new ArrayList();

        if (results != null && results.size() != 0) {
            results.forEach(obj -> {
                SubRecoveryListingDTO recoveryListingDTO = new SubRecoveryListingDTO();
                recoveryListingDTO.paySchedDtlSeq = new BigDecimal(obj[0].toString()).longValue();

                recoveryListingDTO.instNum = new BigDecimal(obj[1].toString()).longValue();
                recoveryListingDTO.ppalAmtDue = new BigDecimal(obj[2].toString()).longValue();
                recoveryListingDTO.totChrgDue = (obj[3] == null ? 0L : new BigDecimal(obj[3].toString()).longValue())
                        + (obj[4] == null ? 0L : new BigDecimal(obj[4].toString()).longValue());
                recoveryListingDTO.dueDt = obj[5] == null ? "" : obj[5].toString();
                recoveryListingDTO.sts = obj[5] == null ? "" : obj[6].toString();
                recoveryListingDTO.trxSeq = (obj[7] == null) ? "" : obj[7].toString();
                recoveryListingDTO.pymtDt = (obj[8] == null) ? "" : obj[8].toString();
                recoveryListingDTO.instr = (obj[9] == null) ? "" : obj[9].toString();
                recoveryListingDTO.rcvryTyp = (obj[10] == null) ? "" : obj[10].toString();
                recoveryListingDTO.pymtAmt = obj[11] == null ? "" : obj[11].toString();
                recoveryListingDTO.post = obj[12] == null ? "" : obj[12].toString();
                recoveryListingDTO.trxPymt = obj[13] == null ? "" : obj[13].toString();
                recoveryListingDTO.pymtType = obj[14] == null ? "" : obj[14].toString();
                recoveryListingDTO.prd = obj[15] == null ? "" : obj[15].toString();
                recoveryListingDTO.trf_clnt_seq = obj[16] == null ? "" : obj[16].toString();

                recoveryListingDTOs.add(recoveryListingDTO);
            });
        }
        return recoveryListingDTOs;

    }

    public List<SubRecoveryListingDTO> getSingleLoanRecoveryForPrntLoanApp(long clntSeq, long prntLoanAppSeq, String prd) {
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.ALLONERECOVERYBYPRNTLOANAPP)
                .setParameter("clntSeq", clntSeq)
                .setParameter("prntLoanAppSeq", prntLoanAppSeq)
                .setParameter("prd", prd);

        List<Object[]> results = q.getResultList();
        List<SubRecoveryListingDTO> recoveryListingDTOs = new ArrayList();

        if (results != null && results.size() != 0) {
            results.forEach(obj -> {
                SubRecoveryListingDTO recoveryListingDTO = new SubRecoveryListingDTO();
                recoveryListingDTO.paySchedDtlSeq = new BigDecimal(obj[0].toString()).longValue();

                recoveryListingDTO.instNum = new BigDecimal(obj[1].toString()).longValue();
                recoveryListingDTO.ppalAmtDue = new BigDecimal(obj[2].toString()).longValue();
                recoveryListingDTO.totChrgDue = (obj[3] == null ? 0L : new BigDecimal(obj[3].toString()).longValue())
                        + (obj[4] == null ? 0L : new BigDecimal(obj[4].toString()).longValue());
                recoveryListingDTO.dueDt = obj[5] == null ? "" : obj[5].toString();
                recoveryListingDTO.sts = obj[5] == null ? "" : obj[6].toString();
                recoveryListingDTO.trxSeq = (obj[7] == null) ? "" : obj[7].toString();
                recoveryListingDTO.pymtDt = (obj[8] == null) ? "" : obj[8].toString();
                recoveryListingDTO.instr = (obj[9] == null) ? "" : obj[9].toString();
                recoveryListingDTO.rcvryTyp = (obj[10] == null) ? "" : obj[10].toString();
                //Added by Areeba
                recoveryListingDTO.brnchSeq = obj[11] == null ? "" : obj[11].toString();
                //Ended by Areeba
                recoveryListingDTO.pymtAmt = obj[12] == null ? "" : obj[12].toString();
                recoveryListingDTO.post = obj[13] == null ? "" : obj[13].toString();
                recoveryListingDTO.trxPymt = obj[14] == null ? "" : obj[14].toString();
                recoveryListingDTO.pymtType = obj[15] == null ? "" : obj[15].toString();
                recoveryListingDTO.prd = obj[16] == null ? "" : obj[16].toString();
                recoveryListingDTO.trf_clnt_seq = obj[17] == null ? "" : obj[17].toString();

                recoveryListingDTOs.add(recoveryListingDTO);
            });
        }
        return recoveryListingDTOs;

    }

    public BillPayment applyAdcPayment(AdcRcvryDTO dto) {
        BillPayment bill = new BillPayment();
        MwRcvryTrx trx = mwRcvryTrxRepository.findOneByInstrNumAndCrntRecFlg(dto.Tran_Auth_Id, true);
        if (trx != null) {
            bill.Response_Code = "03";
            return bill;
        }
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(dto.Consumer_Number), true);
        if (clnt == null) {
            bill.Response_Code = "04";
            return bill;
        }

        long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dto.Tran_Date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean post = false;

        MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
        mwRcvryTrx.setRcvryTrxSeq(trxSeq);
        Long clntSeq = adjustRecoveryPaymentAccount(dto.Consumer_Number, dto.Transaction_Amount, dto.Agent_ID, dto.Tran_Auth_Id,
                dto.Agent_ID.toString(), new SimpleDateFormat("dd/MM/yyyy").format(date), post, mwRcvryTrx);
        bill.Response_Code = clntSeq != null ? "01" : "02";
        bill.Identification_Parameter = clntSeq != null ? Long.toString(trxSeq) : "";
        return bill;
    }

    @Transactional
    public Map applyRecoveryPayment(AppRcvryDTO appRcvryDTO) {
        Long clntSeq = null;
        long trxSeq = 0;
        Long mPrntLoanApp = appRcvryDTO.prntLoanApp;
        // if ( appRcvryDTO.post ) {

        log.debug("AppRcvryDTO.appRcvryDTO : {}", appRcvryDTO);
        log.debug("appRcvryDTO.prntLoanApp : {}", appRcvryDTO.prntLoanApp);

        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(appRcvryDTO.pymtDt);
        } catch (ParseException e) {
        }

        List<LoanApplication> loanApplication = loanApplicationRepository.findMaxLoanCyclLoansForClientAndLoanAppSts(Long.parseLong(appRcvryDTO.clientId));
        if (mPrntLoanApp == null) {
            mPrntLoanApp = loanApplication.get(0).getPrntLoanAppSeq();
        }

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(appRcvryDTO.clientId), true);
        MwBrnch branch = mwBrnchRepository.findOneByClntSeqActiveLoan(Long.parseLong(appRcvryDTO.clientId));

        Types rtyps = typesRepository.findOneByTypSeqAndCrntRecFlg(appRcvryDTO.rcvryTypsSeq, true);
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("recovery.PostRecClnt");
        storedProcedure.registerStoredProcedureParameter("mInstNum", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPymtDt", Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPymtAmt", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mTypSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mClntSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mUsrId", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mBrnchSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mAgntNm", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mClntNm", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPostFlg", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPrntLoanApp", Long.class, ParameterMode.IN);

        storedProcedure.setParameter("mInstNum", appRcvryDTO.instr == null ? "" : appRcvryDTO.instr);
        storedProcedure.setParameter("mPymtDt", date);
        storedProcedure.setParameter("mPymtAmt", appRcvryDTO.pymtAmt);
        storedProcedure.setParameter("mTypSeq", appRcvryDTO.rcvryTypsSeq);
        storedProcedure.setParameter("mClntSeq", Long.parseLong(appRcvryDTO.clientId));
        storedProcedure.setParameter("mUsrId", SecurityContextHolder.getContext().getAuthentication().getName());
        storedProcedure.setParameter("mBrnchSeq", branch == null ? 0l : branch.getBrnchSeq());
        storedProcedure.setParameter("mAgntNm", rtyps == null ? "" : rtyps.getTypStr());
        storedProcedure.setParameter("mClntNm", clnt.getFrstNm() + (clnt.getLastNm() != null ? clnt.getLastNm() : ""));
        storedProcedure.setParameter("mPostFlg", appRcvryDTO.post ? 1 : 0);
        storedProcedure.setParameter("mPrntLoanApp", mPrntLoanApp);

        log.debug("recovery.PostRecClnt =========== Parameters");
        log.debug("mInstNum : {}", appRcvryDTO.instr == null ? "" : appRcvryDTO.instr);
        log.debug("mPymtDt : {}", date);
        log.debug("mPymtAmt : {}\"", appRcvryDTO.pymtAmt);
        log.debug("mTypSeq : {}\"", appRcvryDTO.rcvryTypsSeq);
        log.debug("mClntSeq : {}\"", Long.parseLong(appRcvryDTO.clientId));
        log.debug("mUsrId : {}\"", SecurityContextHolder.getContext().getAuthentication().getName());
        log.debug("mBrnchSeq : {}\"", branch == null ? 0l : branch.getBrnchSeq());
        log.debug("mAgntNm : {}\"", rtyps == null ? "" : rtyps.getTypStr());
        log.debug("mClntNm : {}\"", clnt.getFrstNm() + (clnt.getLastNm() != null ? clnt.getLastNm() : ""));
        log.debug("mPostFlg : {}\"", appRcvryDTO.post ? 1 : 0);
        log.debug("mPrntLoanApp : {}\"", mPrntLoanApp);
        // execute SP
        storedProcedure.execute();
        clntSeq = clnt.getClntSeq();

        MwRcvryTrx trx = mwRcvryTrxRepository.findTop1ByPymtRef("" + clnt.getClntSeq());
        trxSeq = trx == null ? 0l : trx.getRcvryTrxSeq();
        // } else {
        // trxSeq = SequenceFinder.findNextVal( Sequences.RCVRY_TRX_SEQ );
        // appRcvryDTO.instr = appRcvryDTO.instr == null ? Long.toString( trxSeq ) : appRcvryDTO.instr;
        // clntSeq = adjustRecoveryPaymentAccount( appRcvryDTO.clientId, appRcvryDTO.pymtAmt, appRcvryDTO.rcvryTypsSeq, appRcvryDTO.instr,
        // appRcvryDTO.user, appRcvryDTO.pymtDt, appRcvryDTO.post, trxSeq );
        // }
        if (clntSeq != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recovery", getSingleLoanRecovery(clntSeq));
            map.put("trxSeq", trxSeq);
            return map;
        }
        return null;

    }


    @Transactional
    public Map applyRecoveryPaymentDth(AppRcvryDTO appRcvryDTO) {
        Long clntSeq = null;
        long trxSeq = 0;
        Long mPrntLoanApp = appRcvryDTO.prntLoanApp;
        // if ( appRcvryDTO.post ) {

        log.debug("AppRcvryDTO.appRcvryDTO : {}", appRcvryDTO);
        log.debug("appRcvryDTO.prntLoanApp : {}", appRcvryDTO.prntLoanApp);

        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(appRcvryDTO.pymtDt);
        } catch (ParseException e) {
        }

        List<LoanApplication> loanApplication = loanApplicationRepository.findMaxLoanCyclLoansForClientAndLoanAppSts(Long.parseLong(appRcvryDTO.clientId));
        if (mPrntLoanApp == null) {
            mPrntLoanApp = loanApplication.get(0).getPrntLoanAppSeq();
        }

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(appRcvryDTO.clientId), true);
        MwBrnch branch = mwBrnchRepository.findOneByClntSeqActiveLoan(Long.parseLong(appRcvryDTO.clientId));

        Types rtyps = typesRepository.findOneByTypSeqAndCrntRecFlg(appRcvryDTO.rcvryTypsSeq, true);
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("recovery.PostRecClnt_");
        storedProcedure.registerStoredProcedureParameter("mInstNum", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPymtDt", Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPymtAmt", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mTypSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mClntSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mUsrId", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mBrnchSeq", Long.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mAgntNm", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mClntNm", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPostFlg", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("mPrntLoanApp", Long.class, ParameterMode.IN);

        storedProcedure.setParameter("mInstNum", appRcvryDTO.instr == null ? "" : appRcvryDTO.instr);
        storedProcedure.setParameter("mPymtDt", date);
        storedProcedure.setParameter("mPymtAmt", appRcvryDTO.pymtAmt);
        storedProcedure.setParameter("mTypSeq", appRcvryDTO.rcvryTypsSeq);
        storedProcedure.setParameter("mClntSeq", Long.parseLong(appRcvryDTO.clientId));
        storedProcedure.setParameter("mUsrId", SecurityContextHolder.getContext().getAuthentication().getName());
        storedProcedure.setParameter("mBrnchSeq", branch == null ? 0l : branch.getBrnchSeq());
        storedProcedure.setParameter("mAgntNm", rtyps == null ? "" : rtyps.getTypStr());
        storedProcedure.setParameter("mClntNm", clnt.getFrstNm() + (clnt.getLastNm() != null ? clnt.getLastNm() : ""));
        storedProcedure.setParameter("mPostFlg", appRcvryDTO.post ? 1 : 0);
        storedProcedure.setParameter("mPrntLoanApp", mPrntLoanApp);

        log.debug("recovery.PostRecClnt =========== Parameters");
        log.debug("mInstNum : {}", appRcvryDTO.instr == null ? "" : appRcvryDTO.instr);
        log.debug("mPymtDt : {}", date);
        log.debug("mPymtAmt : {}\"", appRcvryDTO.pymtAmt);
        log.debug("mTypSeq : {}\"", appRcvryDTO.rcvryTypsSeq);
        log.debug("mClntSeq : {}\"", Long.parseLong(appRcvryDTO.clientId));
        log.debug("mUsrId : {}\"", SecurityContextHolder.getContext().getAuthentication().getName());
        log.debug("mBrnchSeq : {}\"", branch == null ? 0l : branch.getBrnchSeq());
        log.debug("mAgntNm : {}\"", rtyps == null ? "" : rtyps.getTypStr());
        log.debug("mClntNm : {}\"", clnt.getFrstNm() + (clnt.getLastNm() != null ? clnt.getLastNm() : ""));
        log.debug("mPostFlg : {}\"", appRcvryDTO.post ? 1 : 0);
        log.debug("mPrntLoanApp : {}\"", mPrntLoanApp);
        // execute SP
        storedProcedure.execute();
        clntSeq = clnt.getClntSeq();

        MwRcvryTrx trx = mwRcvryTrxRepository.findTop1ByPymtRef("" + clnt.getClntSeq());
        trxSeq = trx == null ? 0l : trx.getRcvryTrxSeq();
        // } else {
        // trxSeq = SequenceFinder.findNextVal( Sequences.RCVRY_TRX_SEQ );
        // appRcvryDTO.instr = appRcvryDTO.instr == null ? Long.toString( trxSeq ) : appRcvryDTO.instr;
        // clntSeq = adjustRecoveryPaymentAccount( appRcvryDTO.clientId, appRcvryDTO.pymtAmt, appRcvryDTO.rcvryTypsSeq, appRcvryDTO.instr,
        // appRcvryDTO.user, appRcvryDTO.pymtDt, appRcvryDTO.post, trxSeq );
        // }
        if (clntSeq != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recovery", getSingleLoanRecovery(clntSeq));
            map.put("trxSeq", trxSeq);
            return map;
        }
        return null;

    }


    // @Transactional
    // public Map applyRecoveryPaymentViaDBPackage() {
    // StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery( "recovery.PostRecClnt" );
    // storedProcedure.registerStoredProcedureParameter( "userid", String.class, ParameterMode.IN );
    // storedProcedure.setParameter( "userid", SecurityContextHolder.getContext().getAuthentication().getName() );
    // // execute SP
    // storedProcedure.execute();
    // // get result
    // Map< String, Object > map = new HashMap< String, Object >();
    // map.put( "recovery", getSingleLoanRecovery( clntSeq ) );
    // map.put( "trxSeq", trxSeq );
    // return map;
    // }

    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    private Long adjustRecoveryPaymentAccount(String clientId, Long payngAmt, Long rcvryTypsSeq, String instr,
                                              String currUser, String pymtDt, boolean post, MwRcvryTrx mwRcvryTrx) {
        log.debug("Recovery Starting : {}", clientId);
        long paidamt = payngAmt.longValue();
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);

        MwDthRpt dth = mwDthRptRepository.findTopByClntSeqAndCrntRecFlgOrderByDtOfDthDesc(clnt.getClntSeq(), true);
        //Added by Areeba
        MwDsbltyRpt dsb = mwDsbltyRptRepository.findTopByClntSeqAndCrntRecFlgOrderByDtOfDsbltyDesc(clnt.getClntSeq(), true);
        //Ended by Areeba

        DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository
                .getDisbursementVoucherHeaderForClientsActiveLoan(clnt.getClntSeq());

        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(pymtDt);
        } catch (ParseException e) {

        }

        List<MwRcvryDtl> rcvryToSave = new ArrayList();
        Instant currIns = Instant.now();
        mwRcvryTrx.setInstrNum(instr);
        mwRcvryTrx.setPymtDt(date);
        mwRcvryTrx.setPymtAmt(payngAmt);
        mwRcvryTrx.setRcvryTypSeq(rcvryTypsSeq);
        mwRcvryTrx.setPymtStsKey(0L);
        mwRcvryTrx.setPostFlg(post ? 1L : 0L);
        mwRcvryTrx.setEffStartDt(currIns);
        mwRcvryTrx.setEffEndDt(currIns);
        mwRcvryTrx.setCrtdBy(currUser);
        mwRcvryTrx.setCrtdDt(currIns);
        mwRcvryTrx.setDelFlg(false);
        mwRcvryTrx.setLastUpdBy(currUser);
        mwRcvryTrx.setLastUpdDt(currIns);
        mwRcvryTrx.setCrntRecFlg(true);
        mwRcvryTrx.setPymtRef(clnt != null ? clnt.getClntSeq() : Long.parseLong(clientId));

        List<Object[]> dueInstallments = mwRcvryRepository.getDuaInstallment(clnt.getClntSeq());
        // if ( dueInstallments == null || dueInstallments.size() == 0 ) {

        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.CHARG_ORDER).setParameter("clntSeq", clnt.getClntSeq());
        List<Object[]> chrgOrder = q.getResultList();

        long totalPayaable = 0L;
        long cruntPaid = payngAmt.longValue();
        List<PaymentScheduleDetail> isntallments = new ArrayList<PaymentScheduleDetail>();
        List loanAppToComplete = new ArrayList();
        List<Long> prds = new ArrayList();

        //Modified by Areeba
        if (dth == null || (dth.getDtOfDth().compareTo(hdr.getDsbmtDt()) < 0) &&
                dsb == null || (dsb.getDtOfDsblty().compareTo(hdr.getDsbmtDt()) < 0)) {
            for (Object[] obj : dueInstallments) {
                Long dueAmt = ((obj[5] == null) ? 0L : new BigDecimal(obj[5].toString()).longValue())
                        + ((obj[6] == null) ? 0L : new BigDecimal(obj[6].toString()).longValue())
                        + ((obj[7] == null) ? 0L : new BigDecimal(obj[7].toString()).longValue());
                Long paidAmt = (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue();
                totalPayaable = totalPayaable + dueAmt - paidAmt;
                Long loanAppSeq = new BigDecimal(obj[2].toString()).longValue();
                Long prdSeq = new BigDecimal(obj[0].toString()).longValue();
                prds.add(prdSeq);
                if (dueAmt.longValue() > paidAmt.longValue() && payngAmt.longValue() != 0) {

                    List<MwRcvryDtl> rcvryToAdd = new ArrayList();
                    Long paySchedDtlSeq = (obj[3] == null) ? 0L : new BigDecimal(obj[3].toString()).longValue();

                    Long ppalChrgSeq = -1L;
                    Long servChrgSeq = (obj[11] == null) ? 0L : new BigDecimal(obj[11].toString()).longValue();

                    boolean partial = (dueAmt.longValue() - paidAmt.longValue()) > payngAmt.longValue() ? true : false;
                    genrate(paySchedDtlSeq, rcvryToAdd, ppalChrgSeq, servChrgSeq, isntallments, date, partial);

                    for (Object[] order : chrgOrder) {
                        if (prdSeq.longValue() == new BigDecimal(order[5].toString()).longValue()) {
                            Long chrgSeq = 0L;
                            if (order[1] != null && new BigDecimal(order[1].toString()).longValue() == -1L)
                                chrgSeq = -1L;
                            else if (order[1] != null && new BigDecimal(order[1].toString()).longValue() == -2L) {
                                chrgSeq = -2L;
                            } else {
                                chrgSeq = new BigDecimal(order[2].toString()).longValue();
                            }
                            for (MwRcvryDtl addRcvry : rcvryToAdd) {
                                if (addRcvry.getChrgTypKey() == chrgSeq.longValue()) {
                                    if (payngAmt > 0) {
                                        Long amt = (long) 0;
                                        if (addRcvry.getPymtAmt() <= payngAmt) {
                                            payngAmt = payngAmt - addRcvry.getPymtAmt();
                                            amt = addRcvry.getPymtAmt();
                                        } else if (addRcvry.getPymtAmt() > payngAmt) {
                                            amt = payngAmt;
                                            payngAmt = (long) 0;
                                        }
                                        long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                                        MwRcvryDtl mwRcvry = new MwRcvryDtl();
                                        mwRcvry.setRcvryChrgSeq(seq);
                                        mwRcvry.setRcvryTrxSeq(mwRcvryTrx.getRcvryTrxSeq());
                                        mwRcvry.setChrgTypKey(addRcvry.getChrgTypKey());
                                        mwRcvry.setPaySchedDtlSeq(paySchedDtlSeq);
                                        mwRcvry.setPymtAmt(amt);
                                        mwRcvry.setCrntRecFlg(true);
                                        mwRcvry.setCrtdBy(currUser);
                                        mwRcvry.setCrtdDt(currIns);
                                        mwRcvry.setDelFlg(false);
                                        mwRcvry.setEffStartDt(currIns);
                                        mwRcvry.setLastUpdBy(currUser);
                                        mwRcvry.setLastUpdDt(currIns);
                                        rcvryToSave.add(mwRcvry);
                                    }
                                }
                            }
                        }
                    }
                    if (!partial
                            && new BigDecimal(obj[4].toString()).longValue() == new BigDecimal(obj[10].toString()).longValue()) {
                        String loanOstQuery = "select loan_app_ost( " + loanAppSeq + ", sysdate, 'psc') from dual";
                        long ostAmt = new BigDecimal(em.createNativeQuery(loanOstQuery).getSingleResult().toString()).longValue();
                        if ((ostAmt - paidamt) <= 0)
                            loanAppToComplete.add(loanAppSeq);
                    }

                }
                log.debug("Loan Application : {}", loanAppSeq);
            }
        }
        Instant pymtDate = date.toInstant();

        if (payngAmt.longValue() > 0) {
            Types typ = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true);
            long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
            MwRcvryDtl mwRcvry = new MwRcvryDtl();
            mwRcvry.setRcvryChrgSeq(seq);
            mwRcvry.setRcvryTrxSeq(mwRcvryTrx.getRcvryTrxSeq());
            mwRcvry.setChrgTypKey(typ.getTypSeq());
            mwRcvry.setPaySchedDtlSeq(null);
            mwRcvry.setPymtAmt(payngAmt.longValue());
            mwRcvry.setCrntRecFlg(true);
            mwRcvry.setCrtdBy(currUser);
            mwRcvry.setCrtdDt(currIns);
            mwRcvry.setDelFlg(false);
            mwRcvry.setEffStartDt(currIns);
            mwRcvry.setLastUpdBy(currUser);
            mwRcvry.setLastUpdDt(currIns);
            rcvryToSave.add(mwRcvry);
            MwBrnch branch = mwBrnchRepository.findOneByClntSeq(clnt.getClntSeq());
            Types rtyps = typesRepository.findOneByTypSeqAndCrntRecFlg(rcvryTypsSeq, true);
            try {
                Long jvhSeq = vchrPostingService.genericJV(mwRcvryTrx.getRcvryTrxSeq(), typ.getTypStr(), typ.getTypStr(),
                        typ.getGlAcctNum(), rtyps.getGlAcctNum(), payngAmt.longValue(), currUser, branch.getBrnchSeq(), pymtDate);
            } catch (Exception e) {
                // TODO: handle exception
                throw new RuntimeException();
            }
        }

        boolean flag = false;
        if (loanAppToComplete != null && loanAppToComplete.size() > 0 && post) {
            MwRefCdVal comSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);
            MwRefCdVal activeSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
            MwRefCdVal writeOffSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, WRITEOFF_STATUS);
            List<Long> stses = new ArrayList();
            stses.add(activeSts.getRefCdSeq());
            stses.add(writeOffSts.getRefCdSeq());
            List<LoanApplication> apps = loanApplicationRepository.findAllByLoanAppSeqInAndLoanAppStsInAndCrntRecFlg(loanAppToComplete,
                    stses, true);

            List<LoanApplication> appsUpdate = new ArrayList();
            apps.forEach(napp -> {
                napp.setEffStartDt(currIns);
                napp.setLastUpdBy("w-" + currUser);
                napp.setLastUpdDt(currIns);
                napp.setLoanAppSts(comSts.getRefCdSeq());
                napp.setLoanAppStsDt(pymtDate);
                appsUpdate.add(napp);

            });

            flag = recoveryComponent.saveRecoveryPayment(mwRcvryTrx, rcvryToSave, appsUpdate, isntallments);
        } else {
            flag = recoveryComponent.saveRecoveryPayment(mwRcvryTrx, rcvryToSave, isntallments);
        }

        log.debug("Client Seq : {}", clnt.getClntSeq());
        if (flag && post && isntallments.size() > 0) {
            Types recType = typesRepository.findOneByTypSeqAndCrntRecFlg(rcvryTypsSeq, true);

            List<MwPrd> mwPrds = mwPrdRepository.findAllByPrdSeqInAndCrntRecFlg(prds, true);

            String joined = mwPrds.stream().map(MwPrd::getPrdCmnt).collect(Collectors.joining(", "));
            joined = joined.concat(" Recovery received from Client ").concat(clnt.getFrstNm()).concat(" ")
                    .concat((clnt.getLastNm() != null ? clnt.getLastNm() : "")).concat(" through ");
            // .concat( ( ( recType == null ) ? "" : recType.getTypStr() ) );
            MwBrnch branch = mwBrnchRepository.findOneByClntSeq(clnt.getClntSeq());
            try {
                MwJvHdr jvh = vchrPostingService.jvPosting(mwRcvryTrx.getRcvryTrxSeq(), RECOVERY, ((joined == null) ? "" : joined),
                        currUser, chrgOrder, rcvryToSave, rcvryTypsSeq, date, branch.getBrnchSeq());
            } catch (Exception e) {
                // TODO: handle exception
                throw new RuntimeException();
            }
        }

        return flag == false ? null : clnt.getClntSeq();

    }

    private void genrate(Long paySchedDtlSeq, List<MwRcvryDtl> rcvryToAdd, Long ppalChrgSeq, Long servChrgSeq,
                         List<PaymentScheduleDetail> isntallments, Date date, boolean partial) {
        PaymentScheduleDetail paymentScheduleDetail = paymentScheduleDetailRepository.findOneByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq,
                true);
        paymentScheduleDetail.setLastUpdBy(SecurityContextHolder.getContext().getAuthentication().getName());
        paymentScheduleDetail.setLastUpdDt(Instant.now());
        List<PaymentScheduleChargers> paymentScheduleChargers = paymentScheduleChargersRepository
                .findAllByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
        List<MwRcvryDtl> recoverd = mwRcvryRepository.findAllByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
        if (!partial && paymentScheduleDetail.getDueDt().compareTo(date.toInstant()) == 0) {
            paymentScheduleDetail.setPymtStsKey(StaticValusOnStartup.SAMEDAY);
        }
        if (!partial && paymentScheduleDetail.getDueDt().compareTo(date.toInstant()) == 1) {
            paymentScheduleDetail.setPymtStsKey(StaticValusOnStartup.ADVANCE);
        }
        if (!partial && paymentScheduleDetail.getDueDt().compareTo(date.toInstant()) == -1) {
            paymentScheduleDetail.setPymtStsKey(StaticValusOnStartup.DELIQUENCY);
        }
        if (partial) {
            paymentScheduleDetail.setPymtStsKey(StaticValusOnStartup.PARTIAL);
        }

        isntallments.add(paymentScheduleDetail);
        List<MwRcvryDtl> ppalAmt = recoverd.stream()
                .filter(recovery -> paymentScheduleDetail.getPaySchedDtlSeq().longValue() == recovery.getPaySchedDtlSeq().longValue()
                        && ppalChrgSeq.longValue() == recovery.getChrgTypKey().longValue())
                .collect(Collectors.toList());
        // .findAny().orElse( null );
        Long ppalAmtRcv = ppalAmt.stream().mapToLong(MwRcvryDtl::getPymtAmt).sum();
        // Long ppalAmtRcv = ppalAmt != null && ppalAmt.getPymtAmt() != null ? ppalAmt.getPymtAmt() : ( long ) 0;
        if ((paymentScheduleDetail.getPpalAmtDue() - ppalAmtRcv) > 0) {
            MwRcvryDtl ppalRevry = new MwRcvryDtl();
            ppalRevry.setPymtAmt(paymentScheduleDetail.getPpalAmtDue() - ppalAmtRcv);
            ppalRevry.setChrgTypKey(ppalChrgSeq);
            ppalRevry.setPaySchedDtlSeq(paySchedDtlSeq);
            rcvryToAdd.add(ppalRevry);
        }

        List<MwRcvryDtl> chrgsAmt = recoverd.stream()
                .filter(recovery -> paymentScheduleDetail.getPaySchedDtlSeq().longValue() == recovery.getPaySchedDtlSeq().longValue()
                        && servChrgSeq.longValue() == recovery.getChrgTypKey().longValue())
                .collect(Collectors.toList());
        // .findAny().orElse( null );
        // Long chrgsAmtRcv = chrgsAmt != null && chrgsAmt.getPymtAmt() != null ? chrgsAmt.getPymtAmt() : ( long ) 0;
        Long chrgsAmtRcv = chrgsAmt.stream().mapToLong(MwRcvryDtl::getPymtAmt).sum();
        if ((paymentScheduleDetail.getTotChrgDue() - chrgsAmtRcv) > 0) {
            MwRcvryDtl chrgsRevry = new MwRcvryDtl();
            chrgsRevry.setPymtAmt(paymentScheduleDetail.getTotChrgDue() - chrgsAmtRcv);
            chrgsRevry.setChrgTypKey(servChrgSeq);
            chrgsRevry.setPaySchedDtlSeq(paySchedDtlSeq);
            rcvryToAdd.add(chrgsRevry);
        }

        for (PaymentScheduleChargers paymentScheduleCharger : paymentScheduleChargers) {
            MwRcvryDtl newRevry = new MwRcvryDtl();
            List<MwRcvryDtl> mwRcvry = recoverd.stream()
                    .filter(recovery -> paymentScheduleCharger.getPaySchedDtlSeq().longValue() == recovery.getPaySchedDtlSeq().longValue()
                            && paymentScheduleCharger.getChrgTypsSeq().longValue() == recovery.getChrgTypKey().longValue())
                    .collect(Collectors.toList());
            // .findAny().orElse( null );
            Long chrgersAmt = mwRcvry.stream().mapToLong(MwRcvryDtl::getPymtAmt).sum();
            Long diff = (paymentScheduleCharger.getAmt() == null ? 0L : paymentScheduleCharger.getAmt()) - chrgersAmt;
            if (diff > 0) {
                newRevry.setPymtAmt(diff);
                newRevry.setChrgTypKey(paymentScheduleCharger.getChrgTypsSeq());
                newRevry.setPaySchedDtlSeq(paySchedDtlSeq);
                rcvryToAdd.add(newRevry);
            }
        }

    }

    public Long totalReciveableAmt(String clientId) {
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);
        Long totdueAmt = (long) 0;
        List<Object[]> dueInstallments = mwRcvryRepository.getDuaInstallment(clnt.getClntSeq());
        if (dueInstallments != null && dueInstallments.size() != 0) {
            for (Object[] obj : dueInstallments) {
                Long dueAmt = (obj[5] == null ? 0L : new BigDecimal(obj[5].toString()).longValue())
                        + (obj[6] == null ? 0L : new BigDecimal(obj[6].toString()).longValue())
                        + (obj[7] == null ? 0L : new BigDecimal(obj[7].toString()).longValue());
                Long paidAmt = (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue();
                if (dueAmt > paidAmt) {
                    Long diff = dueAmt - paidAmt;
                    totdueAmt = totdueAmt + diff;
                }
            }

        }
        return totdueAmt;
    }

    public BillInquiry lastDueInst(String clientId) {
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);
        BillInquiry bill = new BillInquiry();
        bill.response_code = clnt == null ? "01" : "00";
        bill.consumer_detail = clnt.getFrstNm() + " " + (clnt.getLastNm() == null ? "" : clnt.getLastNm());
        Date date = new Date();
        List<Object[]> dueInstallments = mwRcvryRepository.getDueInstForAdc(clnt.getClntSeq());
        if (dueInstallments != null && dueInstallments.size() != 0) {
            for (Object[] obj : dueInstallments) {
                Date dueDate = null;
                try {
                    dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(obj[5].toString());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Long dueAmt = (obj[1] == null ? 0L : new BigDecimal(obj[1].toString()).longValue())
                        + (obj[2] == null ? 0L : new BigDecimal(obj[2].toString()).longValue())
                        + (obj[3] == null ? 0L : new BigDecimal(obj[3].toString()).longValue());
                Long paidAmt = (obj[4] == null) ? 0L : new BigDecimal(obj[4].toString()).longValue();
                if ((dueDate.before(date) || dueDate.equals(date)) && dueAmt > paidAmt) {
                    bill.amount_within_duedate = bill.amount_within_duedate + (double) (dueAmt - paidAmt);
                    bill.due_date = new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
                }
                if (bill.amount_within_duedate == null && dueDate.after(date) && dueAmt > paidAmt) {
                    bill.amount_within_duedate = (double) (dueAmt - paidAmt);
                    bill.due_date = new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
                    break;
                }

            }

        }
        bill.bill_status = bill.amount_within_duedate == null ? "P" : "U";

        return bill;
    }

    public boolean reversePaymentCheckExcessPaid(AjRcvryDTO ajRcvryDTO) {
        MwExp exp = mwExpRepository.getExcessExpenseForTrx(ajRcvryDTO.trxId);
        if (exp != null) {
            boolean postFlg = exp.getPostFlg() == null ? false : exp.getPostFlg().longValue() == 0 ? false : true;
            if (!postFlg) {
                exp.setCrntRecFlg(false);
                exp.setDelFlg(true);
                exp.setLastUpdBy("REV-" + SecurityContextHolder.getContext().getAuthentication().getName());
                exp.setLastUpdDt(Instant.now());
                exp.setEffEndDt(Instant.now());
                mwExpRepository.save(exp);
                return false;
            }
        }
        return exp == null ? false : true;
    }

    @Transactional
    public Long reversePayment(AjRcvryDTO ajRcvryDTO, String currUser, String token) {

        MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(ajRcvryDTO.trxId, true);
        Long clntSeq = null;
        if (trx == null)
            return null;
        MwRefCdVal actSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        MwRefCdVal comSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);

        List<LoanApplication> apps = loanApplicationRepository.findAllPaidLoanAppByTrx(trx.getRcvryTrxSeq().longValue());
        Instant currIns = Instant.now();
        trx.setDelFlg(true);
        trx.setLastUpdBy(currUser);
        trx.setLastUpdDt(currIns);
        trx.setCrntRecFlg(false);
        trx.setChngRsnKey(ajRcvryDTO.chngRsnKey);
        trx.setChngRsnCmnt(ajRcvryDTO.chngRsnCmnt);

        Types rcTyp = typesRepository.findOneByTypSeqAndCrntRecFlg(trx.getRcvryTypSeq(), true);
        if (rcTyp != null && rcTyp.getTypId().equals("0454")) {
            adminServiceClient.revertAnmlSts(trx.getPrntRcvryRef(), token);
        }

        List<MwRcvryDtl> rcvrys = mwRcvryRepository.findAllByRcvryTrxSeqAndCrntRecFlg(trx.getRcvryTrxSeq(), true);
        MwRcvryDtl excessRcvry = null;
        for (MwRcvryDtl r : rcvrys) {
            r.setDelFlg(true);
            r.setLastUpdBy(currUser);
            r.setLastUpdDt(currIns);
            r.setCrntRecFlg(false);
            if (r.getPaySchedDtlSeq() == null)
                excessRcvry = r;
        }
        MwRefCdVal dueSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0945");
        MwRefCdVal partialSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "1145");
        List<PaymentScheduleDetail> install = paymentScheduleDetailRepository.getPaidInstallmentByTrxSeq(trx.getRcvryTrxSeq());
        install.forEach(i -> {
            PaymentScheduleDetail dtl = paymentScheduleDetailRepository.getDtlIfComopletelyUnpaid(i.getPaySchedDtlSeq());
            if (dtl == null)
                i.setPymtStsKey(partialSts.getRefCdSeq());
            else
                i.setPymtStsKey(dueSts.getRefCdSeq());
            i.setLastUpdBy(currUser);
            i.setLastUpdDt(currIns);
        });

        // MwRefCdVal comSts = mwRefCdValRepository.findRefCdByGrpAndVal( APPLICATIONS_STATUS, COMPLETED_STATUS );

        // List< LoanApplication > actapps = loanApplicationRepository.findAllPaidLoanAppByTrx( trx.getRcvryTrxSeq(), actSts.getRefCdSeq()
        // );
        List<LoanApplication> appsUpdate = new ArrayList();
        String prdNm = "";
        for (LoanApplication a : apps) {
            MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(a.getPrdSeq(), true);
            DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository
                    .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(a.getLoanAppSeq(), true, 0);

            if (prd != null) {
                if (prdNm.length() == 0) {
                    prdNm = prd.getPrdCmnt();
                } else {
                    prdNm = prdNm + "," + prd.getPrdCmnt();
                }
            }
            if (a.getLoanAppSts().longValue() != actSts.getRefCdSeq().longValue()) {
                a.setLastUpdBy("w-" + currUser);
                a.setLastUpdDt(currIns);

                if (a.getLoanAppSts().equals(comSts.getRefCdSeq())) {
                    a.setLoanAppStsDt(hdr.getDsbmtDt());
                } else {
                    a.setLoanAppStsDt(currIns);
                }
                a.setLoanAppSts(actSts.getRefCdSeq());
                appsUpdate.add(a);
            }
        }
        // for ( LoanApplication a : actapps ) {
        // MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg( a.getPrdSeq(), true );
        // if ( prd != null ) {
        // if ( prdNm.length() == 0 ) {
        // prdNm = prd.getPrdCmnt();
        // } else {
        // prdNm = prdNm + "," + prd.getPrdCmnt();
        // }
        // }
        // }

        boolean flag = recoveryComponent.reverseRecoveryPayment(trx, rcvrys);
        if (flag) {
            paymentScheduleDetailRepository.save(install);
            loanApplicationRepository.save(appsUpdate);
        }
        if (trx.getPostFlg() != null && trx.getPostFlg().longValue() == 1) {
            Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(trx.getPymtRef(), true);
            // MwRefCdVal rsnVal = mwRefCdValRepository.findOneByRefCdSeqAndCrntRecFlg( ajRcvryDTO.chngRsnKey, true );
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = format.format(trx.getPymtDt());
            String desc = new String(prdNm).concat(" ").concat(RECOVERY).concat(" reversed dated ").concat(dateString)
                    .concat(" Client of ").concat(clnt.getFrstNm()).concat(" ")
                    .concat((clnt.getLastNm() == null ? "" : clnt.getLastNm())).concat(" through ").concat(rcTyp.getTypStr());
            vchrPostingService.reverseVchrPosting(trx.getRcvryTrxSeq(), RECOVERY, currUser, desc);
            if (excessRcvry != null) {
                desc = new String(ExcessRecovery).concat(" reversed dated ").concat(dateString).concat("  ")
                        .concat(clnt.getFrstNm()).concat(" ").concat((clnt.getLastNm() == null ? "" : clnt.getLastNm()));
                // .concat( rsnVal.getRefCdDscr() );
                vchrPostingService.reverseVchrPosting(trx.getRcvryTrxSeq(), ExcessRecovery, currUser, desc);
            }
        }
        if (flag && ajRcvryDTO.pymtAmt.longValue() > 0) {
            Date dt = trx.getPymtDt();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
            mwRcvryTrx.setRcvryTrxSeq(trx.getRcvryTrxSeq());
            clntSeq = adjustRecoveryPaymentAccount(trx.getPymtRef().toString(), ajRcvryDTO.pymtAmt, trx.getRcvryTypSeq(),
                    trx.getInstrNum(), currUser, formatter.format(dt), true, mwRcvryTrx);

        } else
            clntSeq = 1L;

        loanApplicationRepository.save(appsUpdate);
        return clntSeq;

    }

    @Transactional
    public Long reversePaymentWithoutExcess(MwRcvryTrx trx, Clients clnt, String currUser, String token) {
        Long clntSeq = null;
        if (trx == null)
            return null;
        MwRefCdVal actSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        List<LoanApplication> apps = loanApplicationRepository.findAllPaidLoanAppByTrx(trx.getRcvryTrxSeq().longValue());
        Instant currIns = Instant.now();

        Types rcTyp = typesRepository.findOneByTypSeqAndCrntRecFlg(trx.getRcvryTypSeq(), true);
        if (rcTyp != null && rcTyp.getTypId().equals("0454")) {
            adminServiceClient.revertAnmlSts(trx.getPrntRcvryRef(), token);
        }

        MwRefCdVal dueSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0945");
        List<PaymentScheduleDetail> install = paymentScheduleDetailRepository.getPaidInstallmentByTrxSeq(trx.getRcvryTrxSeq());
        install.forEach(i -> {
            i.setPymtStsKey(dueSts.getRefCdSeq());
            i.setLastUpdBy(currUser);
            i.setLastUpdDt(currIns);
        });

        List<LoanApplication> appsUpdate = new ArrayList();
        String prdNm = "";
        for (LoanApplication a : apps) {
            MwPrd prd = mwPrdRepository.findOneByPrdSeqAndCrntRecFlg(a.getPrdSeq(), true);
            if (prd != null) {
                if (prdNm.length() == 0) {
                    prdNm = prd.getPrdCmnt();
                } else {
                    prdNm = prdNm + "," + prd.getPrdCmnt();
                }
            }
            if (a.getLoanAppSts().longValue() != actSts.getRefCdSeq().longValue()) {
                a.setLastUpdBy("w-" + currUser);
                a.setLastUpdDt(currIns);
                a.setLoanAppSts(actSts.getRefCdSeq());
                a.setLoanAppStsDt(currIns);
                appsUpdate.add(a);
            }
        }
        boolean flag = recoveryComponent.reverseRecoveryPayment(trx, new ArrayList<>());
        if (flag) {
            paymentScheduleDetailRepository.save(install);
            loanApplicationRepository.save(appsUpdate);
        }
        if (trx.getPostFlg() != null && trx.getPostFlg().longValue() == 1) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = format.format(trx.getPymtDt());
            String desc = new String(prdNm).concat(" ").concat(RECOVERY).concat(" reversed dated ").concat(dateString)
                    .concat(" Client of ").concat(clnt.getFrstNm()).concat(" ")
                    .concat((clnt.getLastNm() == null ? "" : clnt.getLastNm())).concat(" through ").concat(rcTyp.getTypStr());
            vchrPostingService.reverseVchrPosting(trx.getRcvryTrxSeq(), RECOVERY, currUser, desc);
        }

        loanApplicationRepository.save(appsUpdate);
        return clntSeq;

    }

    // Modified by Areeba
    @Transactional
    public void reverseAdvancePaymentsToExcess(long clntSeq, Long prntLoanAppSeq, String incidentDate, String currUser, Integer incidentTyp) {
        Instant currIns = Instant.now();
        MwRefCdVal dueSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0945");
        MwBrnch branch = mwBrnchRepository.findOneByClntSeq(clntSeq);

        List<PaymentScheduleDetail> advance = paymentScheduleDetailRepository.getAdvancedPaid(clntSeq, prntLoanAppSeq, incidentDate);

        List<MwRcvryDtl> advRecs = new ArrayList();
        List<MwRcvryDtl> exeRecv = new ArrayList();
        MwRefCdVal actSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        MwRefCdVal comSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);
        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true);

        // Added by Zohaib Asim - Dated 27/01/2021
        // CR: IN CASE PAYMENT DATE IS BEFORE DEATH DATE - No EXCESS/REVERSAL TOOK PLACE
        MwRefCdVal partialSts = mwRefCdValRepository.findRefCdByGrpAndVal(PAYMENT_STATUS, PARTIAL_STATUS);
        List<PaymentScheduleDetail> copyAdvance = new ArrayList<PaymentScheduleDetail>(advance);
        advance.forEach(psd -> {
            List<PaymentScheduleDetail> pymtBfIncdnt = paymentScheduleDetailRepository.getAdvncPymntsAftrIncdnt(clntSeq, psd.getPaySchedDtlSeq(), incidentDate);
            if (pymtBfIncdnt != null && pymtBfIncdnt.size() == 0) {
                copyAdvance.remove(psd);
            }
        });
        advance = copyAdvance;

        //Added by Areeba
        MwDthRpt dth = mwDthRptRepository.findTopByClntSeqAndCrntRecFlgAndDtOfDth(clntSeq, incidentDate);
        MwDsbltyRpt dsb = mwDsbltyRptRepository.findTopByClntSeqAndCrntRecFlgAndDtOfDsblty(clntSeq, incidentDate);

        // Added by Zohaib Asim - Dated 23-02-2021
        // Recovery Transaction Detail against Client and Payment Date is greater than Death Date
        List<MwRcvryDtl> rcvryDtlList = mwRcvryRepository.findMwRcvryTrxByPymtRefAndPymtDtAfter(
                String.valueOf(clntSeq), incidentDate);

        // Advance Recovries
        advRecs.addAll(rcvryDtlList);
        // End by Zohaib Asim

        // advance: Payment Schedule Detail
        advance.forEach(psd -> {
            // Commented by Zohaib Asim - Dated 23-02-2021
            //advRecs.addAll(mwRcvryRepository.findAllByPaySchedDtlSeqAndCrntRecFlg(psd.getPaySchedDtlSeq(), true));

            PaymentScheduleHeader ph = paymentScheduleHeaderRespository.findOneByPaySchedHdrSeqAndCrntRecFlg(psd.getPaySchedHdrSeq(),
                    true);
            if (ph != null) {
                LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(ph.getLoanAppSeq(), true);
                if (app != null) {
                    if (app.getLoanAppSts().longValue() == comSts.getRefCdSeq().longValue()) {
                        DisbursementVoucherHeader hdr = disbursementVoucherHeaderRepository
                                .findOneByLoanAppSeqAndCrntRecFlgAndDsbmtVchrTyp(app.getLoanAppSeq(), true, 0);

                        app.setLastUpdBy("wRA-" + currUser);
                        app.setLastUpdDt(currIns);
                        app.setLoanAppSts(actSts.getRefCdSeq());
                        app.setLoanAppStsDt((hdr == null) ? Instant.now() : hdr.getDsbmtDt());
                        loanApplicationRepository.save(app);
                    }
                }
            }
        });

        // advRecs: Recovry Detail
        advRecs.forEach(ar -> {
            ar.setDelFlg(true);
            ar.setLastUpdBy(currUser);
            ar.setLastUpdDt(currIns);
            ar.setCrntRecFlg(false);
            exeRecv.add(ar);
        });

        Map<Long, Long> counting = advRecs.stream()// .sorted( Comparator.comparingLong( MwRcvryDtl::getRcvryTrxSeq ) )
                .collect(Collectors.groupingBy(MwRcvryDtl::getRcvryTrxSeq, Collectors.summingLong(MwRcvryDtl::getPymtAmt)));

        Map<Long, Long> sorted = counting.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        // Recovery Detail
        advRecs.forEach(r -> {
            r.setEffEndDt(Instant.now());
            r.setLastUpdBy(SecurityContextHolder.getContext().getAuthentication().getName());
            r.setLastUpdDt(Instant.now());
            r.setDelFlg(true);
            r.setCrntRecFlg(false);
        });
        mwRcvryRepository.save(advRecs);

        List<MwRcvryTrx> trxs = new ArrayList();
        sorted.forEach((a, b) -> {
            if (b > 0) {
                Map m = mwRcvryTrxRepository.getGlAcctNumByRcvryTrxSeq(a);
                MwRcvryTrx extrx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(a, true);

                extrx.setEffEndDt(currIns);
                extrx.setLastUpdBy(currUser);
                extrx.setLastUpdDt(currIns);
                extrx.setDelFlg(true);
                extrx.setCrntRecFlg(false);
                mwRcvryTrxRepository.save(extrx);

                long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
                MwRcvryTrx trx = new MwRcvryTrx();
                trx.setChngRsnCmnt(extrx.getChngRsnCmnt());
                trx.setChngRsnKey(extrx.getChngRsnKey());
                trx.setCrntRecFlg(true);
                trx.setCrtdBy(currUser);
                trx.setCrtdDt(currIns);
                trx.setDelFlg(false);
                trx.setEffStartDt(currIns);
                trx.setInstrNum(extrx.getInstrNum());
                trx.setPostFlg(extrx.getPostFlg());
                trx.setPrntRcvryRef(extrx.getRcvryTrxSeq());
                trx.setPymtAmt(extrx.getPymtAmt());
                trx.setPymtDt(Date.from(currIns));
                trx.setPymtModKey(extrx.getPymtModKey());
                trx.setPymtRef(extrx.getPymtRef());
                trx.setPymtStsKey(extrx.getPymtStsKey());
                trx.setRcvryTrxSeq(trxSeq);
                trx.setRcvryTypSeq(extrx.getRcvryTypSeq());
                trx.setLastUpdBy(currUser);
                trx.setLastUpdDt(currIns);
                // added by yousaf , dated: 19-07-2022 deathProcess
                log.error("ParentLoanAppSeq ==> extrx.getPrntLoanAppSeq()" + extrx.getPrntLoanAppSeq());
                trx.setPrntLoanAppSeq(extrx.getPrntLoanAppSeq());
                trxs.add(trx);

                long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                MwRcvryDtl mwRcvry = new MwRcvryDtl();
                mwRcvry.setRcvryChrgSeq(seq);
                mwRcvry.setRcvryTrxSeq(trxSeq);
                mwRcvry.setChrgTypKey(exesstyp.getTypSeq());
                mwRcvry.setPaySchedDtlSeq(-1L);
                mwRcvry.setPymtAmt(b);
                mwRcvry.setCrntRecFlg(true);
                mwRcvry.setCrtdBy(currUser);
                mwRcvry.setCrtdDt(currIns);
                mwRcvry.setDelFlg(false);
                mwRcvry.setEffStartDt(currIns);
                mwRcvry.setLastUpdBy(currUser);
                mwRcvry.setLastUpdDt(currIns);
                exeRecv.add(mwRcvry);

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = format.format(extrx.getPymtDt());
                //Modified by Areeba
                if (incidentTyp == 0) { // Client Death
                    Long jvhSeq = vchrPostingService.genericJV(trxSeq, exesstyp.getTypStr(),
                            "Recovery of " + dateString + " created in excess recovery due to Client death", exesstyp.getGlAcctNum(),
                            m.get("gl_acct_num").toString(), b, currUser, branch.getBrnchSeq(), Instant.now());
                } else if (incidentTyp == 1) { // Client Disability
                    Long jvhSeq = vchrPostingService.genericJV(trxSeq, exesstyp.getTypStr(),
                            "Recovery of " + dateString + " created in excess recovery due to Client disability", exesstyp.getGlAcctNum(),
                            m.get("gl_acct_num").toString(), b, currUser, branch.getBrnchSeq(), Instant.now());
                }
            }
        });
        mwRcvryTrxRepository.save(trxs);
        mwRcvryRepository.save(exeRecv);


        // Modified by Zohaib Asim - Dated 28-09-2021
        // Partial and Due Status issue
        advance.forEach(psd -> {
            // Get detail for PSD Due/Partial status
            List<MwRcvryTrx> rcvryTrxList = mwRcvryTrxRepository.getPartialTransDetailByPsdSeq(psd.getPaySchedDtlSeq());
            // Get Sum of Total Paid Amount
            Long ttlDueAmt = psd.getPpalAmtDue() + psd.getTotChrgDue();
            Long ttlPaidRcvryAmt = 0L;
            // Traverse
            for (MwRcvryTrx rt : rcvryTrxList) {
                ttlPaidRcvryAmt += rt.getPymtAmt();
            }

            // In Case Multiple Transactions Found; Parital Payment has been made
            if (rcvryTrxList.size() > 1 ||
                    (ttlDueAmt > ttlPaidRcvryAmt && ttlPaidRcvryAmt > 0)) {
                // Partial Status
                psd.setPymtStsKey(partialSts.getRefCdSeq());
                psd.setLastUpdBy(currUser);
                psd.setLastUpdDt(currIns);
            } else {
                // Due Status
                psd.setPymtStsKey(dueSts.getRefCdSeq());
                psd.setLastUpdBy(currUser);
                psd.setLastUpdDt(currIns);
            }
        });
        // Save PSD
        paymentScheduleDetailRepository.save(advance);

        // Partial Payment Reversal JV Issue
        vchrPostingService.reverseAdvanceRecovery(rcvryDtlList, RECOVERY, currUser);
        // End by Zohaib Asim
    }
    //

    // Reverse Back The Excess Amount To Recovery in case of Death Revert
    // Modified by Areeba
    @Transactional
    public void reverseExcessToRecovery(long clntSeq, String currUser, Integer incidentTyp) {
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(clntSeq, true);
        Instant currIns = Instant.now();
        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true); //JV

        List<LoanApplication> apps = loanApplicationRepository.findMaxLoanCyclAssocLoansForClient(clntSeq);
        MwRefCdVal activeSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        List<LoanApplication> appsUpdate = new ArrayList();
        apps.forEach(app -> {
            app.setLastUpdBy("DR-" + currUser);
            app.setLastUpdDt(currIns);
            app.setLoanAppSts(activeSts.getRefCdSeq());
            app.setLoanAppStsDt(currIns);
            appsUpdate.add(app);
        });
        loanApplicationRepository.save(appsUpdate);

        // Added by Zohaib Asim - Dated 18-02-2021
        // getExcessRecoveryToReverse() -> Parameter Updated
        List<MwRcvryDtl> excessRecv;
        if (incidentTyp == 1) {
            excessRecv = mwRcvryRepository.getXSRcvryToRvrseForDsblty(String.valueOf(clntSeq));
        } else {
            //for death
            excessRecv = mwRcvryRepository.getXSRcvryToRvrse(String.valueOf(clntSeq));
        }
        //excessRecv = mwRcvryRepository.getXSRcvryToRvrse(String.valueOf(clntSeq));

        List<Long> trxSeqs = new ArrayList();
        if (excessRecv.size() > 0) {
            List<MwRcvryTrx> trxs = new ArrayList();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            excessRecv.forEach(r -> {
                r.setEffEndDt(Instant.now());
                r.setLastUpdBy(currUser);
                r.setLastUpdDt(Instant.now());
                r.setDelFlg(true);
                r.setCrntRecFlg(false);
                mwRcvryRepository.save(r);

                MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(r.getRcvryTrxSeq(), true);
                trxs.add(trx);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = format.format(trx == null ? new Date() : trx.getPymtDt());
                String desc = new String("Reversal of Excess Recovery dated ").concat(dateString).concat(" ")
                        .concat(clnt.getFrstNm()).concat(" ").concat((clnt.getLastNm() == null ? "" : clnt.getLastNm()));
                vchrPostingService.reverseVchrPosting(r.getRcvryTrxSeq(), exesstyp.getTypStr(), currUser, desc);
            });
            mwRcvryRepository.save(excessRecv);

            //
            excessRecv.forEach(r -> {
                MwRcvryTrx trx = null;
                for (MwRcvryTrx t : trxs) {
                    if (t.getRcvryTrxSeq().longValue() == r.getRcvryTrxSeq().longValue()) {
                        trx = t;
                    }
                }
                if (trx == null) {
                    trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(r.getRcvryTrxSeq(), true);
                    trx.setDelFlg(true);
                    trx.setLastUpdBy(currUser);
                    trx.setLastUpdDt(currIns);
                    trx.setCrntRecFlg(false);
                    trxs.add(trx);
                }
                trx.setDelFlg(true);
                trx.setCrntRecFlg(false);
                trx.setEffEndDt(Instant.now());
                trx.setLastUpdBy(currUser);
                trx.setLastUpdDt(Instant.now());
                MwRcvryTrx prntTrx = mwRcvryTrxRepository.findTop1ByRcvryTrxSeqAndCrntRecFlgFalse(trx.getPrntRcvryRef());

                MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(clnt.getClntSeq());
                AppRcvryDTO appRcvryDTO = new AppRcvryDTO();
                appRcvryDTO.brnchSeq = brnch.getBrnchSeq();
                appRcvryDTO.clientId = clnt.getClntId();
                if (prntTrx.getInstrNum() != prntTrx.getRcvryTrxSeq().toString())
                    appRcvryDTO.instr = prntTrx.getInstrNum();
                appRcvryDTO.post = true;
                appRcvryDTO.pymtAmt = prntTrx.getPymtAmt();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                appRcvryDTO.pymtDt = df.format(prntTrx.getPymtDt());
                appRcvryDTO.rcvryTypsSeq = prntTrx.getRcvryTypSeq();
                appRcvryDTO.user = currUser;
                //ADDED BY YOUSAF DATED : 19-07-2022 MULTIPLE LOAN DTH REVERSAL
                appRcvryDTO.prntLoanApp = prntTrx.getPrntLoanAppSeq();
                Map map = applyRecoveryPaymentDth(appRcvryDTO);
                // Long seq = adjustRecoveryPaymentAccount( clnt.getClntId(), r.getPymtAmt(), trx.getRcvryTypSeq(), trx.getInstrNum(),
                // currUser, formatter.format( prntTrx.getPymtDt() ), true, prntTrx );
            });
            mwRcvryTrxRepository.save(trxs);
        }

        //
        Query qry = em.createNativeQuery("SELECT FN_DEFFERED_REVERSAL_CHRGES(" + clntSeq + ",'" +
                currUser + "') FROM DUAL");
        String fnResp = qry.getSingleResult().toString();
        if (!fnResp.contains("1")) {
            return;
        }
    }

    /*  Modified by Zohaib Asim - Dated 20/01/2021
        Parameter added: EXP_SEQ
        MwRcvryTrx converted to list for multiple execution
        REASON: IN CASE MULTIPLE RECOVERY TRANSACTION EXIST FOR SPECIFIC USER
     */
    public void reverseCashFunralCharges(long clntSeq, long expSeq, String currUser) {
        Instant currIns = Instant.now();
        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0750", 4, 0, true);

        boolean rcvryTrxSizeFlg = false;
        // IN CASE MULTIPLE RECOVERY TRANSACTION EXIST FOR SPECIFIC USER
        String pExpSeq = "EXP_SEQ%" + expSeq + "%";
        List<MwRcvryTrx> trxArr = mwRcvryTrxRepository.findByRcvryTypSeqAndPymtRefAndCrntRecFlg(pExpSeq, exesstyp.getTypSeq(), clntSeq);
        // MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTypSeqAndPymtRefAndCrntRecFlg( exesstyp.getTypSeq(), clntSeq, true );

        if (trxArr != null && trxArr.size() > 0) {
            rcvryTrxSizeFlg = true;
        } else {
            trxArr = mwRcvryTrxRepository.findByRcvryTypSeqAndPymtRefAndCrntRecFlg(exesstyp.getTypSeq(), clntSeq, true);
            rcvryTrxSizeFlg = trxArr != null && trxArr.size() > 0 ? true : false;
        }

        if (rcvryTrxSizeFlg) {
            trxArr.forEach(trx -> {
                if (trx != null) {

                    List<MwRcvryDtl> funRecv = mwRcvryRepository.findAllByRcvryTrxSeqAndCrntRecFlg(trx.getRcvryTrxSeq(), true);

                    List<Long> ids = new ArrayList();

                    if (trx != null) {
                        funRecv.forEach(r -> {
                            r.setCrntRecFlg(false);
                            r.setLastUpdBy(currUser);
                            r.setDelFlg(true);
                            r.setLastUpdDt(currIns);
                            ids.add(r.getPaySchedDtlSeq());

                        });

                        trx.setDelFlg(true);
                        trx.setLastUpdBy(currUser);
                        trx.setLastUpdDt(currIns);
                        trx.setCrntRecFlg(false);

                        MwRefCdVal dueSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0945"); //DUE

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(new Date());
                        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(trx.getPymtRef(), true);
                        String desc = new String("Reversal of Funeral Charges dated ").concat(dateString).concat(" ")
                                .concat(clnt.getFrstNm()).concat(" ").concat((clnt.getLastNm() == null ? "" : clnt.getLastNm()));
                        vchrPostingService.reverseVchrPosting(trx.getRcvryTrxSeq(), RECOVERY, currUser, desc);

                        List<PaymentScheduleDetail> paymentScheduleDetails = paymentScheduleDetailRepository
                                .findOneByPaySchedDtlSeqInAndCrntRecFlg(ids, true);
                        paymentScheduleDetails.forEach(d -> {
                            d.setPymtStsKey(dueSts.getRefCdSeq());
                        });
                        paymentScheduleDetailRepository.save(paymentScheduleDetails);
                        mwRcvryRepository.save(funRecv);
                        mwRcvryTrxRepository.save(trx);
                    }
                }
            });
        }
    }

    public List<?> bulkPosting(String user, String currUser) {
        Query q = em.createNativeQuery("select DISTINCT la.loan_app_seq,la.prd_seq,trx.rcvry_trx_seq,trx.pymt_ref\r\n"
                        + "from mw_rcvry_dtl dtl \r\n"
                        + "join mw_rcvry_trx trx on trx.rcvry_trx_seq = dtl.rcvry_trx_seq and nvl(trx.post_flg,0)!=1 and to_date(trx.crtd_dt)=to_date(sysdate)\r\n"
                        + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq=dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg=1\r\n"
                        + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
                        + "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\r\n"
                        + "join mw_acl acl on acl.port_seq = la.port_seq and acl.user_id =:userId \r\n" + "where dtl.crnt_rec_flg =1 \r\n" + " ")
                .setParameter("userId", user);
        List<Object[]> unpostedTrx = q.getResultList();
        vchrPostingService.bulkPosting(user, currUser, unpostedTrx);

        return unpostedTrx;
    }

    @Transactional
    public boolean recoveryPosting(long id, String currUser) {
        boolean posted = isRecoveryPosted(id);
        if (posted) {
            return false;
        }
        MwRcvryTrx trx = mwRcvryTrxRepository.findOneByRcvryTrxSeqAndCrntRecFlg(id, true);
        Instant currIns = Instant.now();
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(trx.getPymtRef());
        List<MwRcvryDtl> rcvrys = mwRcvryRepository.findAllByRcvryTrxSeqAndCrntRecFlg(id, true);
        Types rectyp = typesRepository.findOneByTypSeqAndCrntRecFlg(trx.getRcvryTypSeq(), true);
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(trx.getPymtRef(), true);
        if (rcvrys.get(0).getPaySchedDtlSeq() == null) {
            Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true);

            Long jvhSeq = vchrPostingService.genericJV(trx.getRcvryTrxSeq(), exesstyp.getTypStr(), exesstyp.getTypStr(),
                    exesstyp.getGlAcctNum(), rectyp.getGlAcctNum(), rcvrys.get(0).getPymtAmt().longValue(), currUser, brnch.getBrnchSeq(),
                    Instant.now());
            return true;
        }
        List prds = new ArrayList();
        List<LoanApplication> apps = new ArrayList();
        List<Object[]> pymts = mwRcvryRepository.getDuaAndPymtTotalAmts(clnt.getClntSeq());
        pymts.forEach(obj -> {
            prds.add(new BigDecimal(obj[3].toString()).longValue());
            if (new BigDecimal(obj[1].toString()).longValue() == new BigDecimal(obj[2].toString()).longValue()) {
                LoanApplication loan = loanApplicationRepository
                        .findByLoanAppSeqAndCrntRecFlg(new BigDecimal(obj[0].toString()).longValue(), true);
                if (loan != null)
                    apps.add(loan);
            }

        });

        List<MwPrd> mwPrds = mwPrdRepository.findAllByPrdSeqInAndCrntRecFlg(prds, true);

        String joined = mwPrds.stream().map(MwPrd::getPrdCmnt).collect(Collectors.joining(", "));

        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.CHARG_ORDER).setParameter("clntSeq", clnt.getClntSeq());
        List<Object[]> chrgOrder = q.getResultList();
        MwJvHdr jvh = vchrPostingService.jvPosting(id, RECOVERY,
                joined + " Recovery received from Client " + clnt.getFrstNm() + " " + (clnt.getLastNm() == null ? "" : clnt.getLastNm())
                        + " through " + rectyp.getTypStr(),
                currUser, chrgOrder, rcvrys, trx.getRcvryTypSeq(), trx.getPymtDt(), brnch.getBrnchSeq());
        trx.setPostFlg(1L);
        mwRcvryTrxRepository.save(trx);

        List<LoanApplication> appsUpdate = new ArrayList();
        MwRefCdVal comSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);
        apps.forEach(napp -> {
            napp.setLastUpdBy("w-" + currUser);
            napp.setLastUpdDt(currIns);
            napp.setLoanAppSts(comSts.getRefCdSeq());
            napp.setLoanAppStsDt(currIns);
            appsUpdate.add(napp);

        });

        if (appsUpdate != null && appsUpdate.size() > 0) {
            loanApplicationRepository.save(appsUpdate);
        }

        return true;
    }

    @Async
    public void recoveryMultiplePosting(String trxs, String currUser) {
        Pattern pattern = Pattern.compile(",");
        List<Long> list = pattern.splitAsStream(trxs).map(Long::valueOf).collect(Collectors.toList());

        list.forEach(e -> {
            recoveryPosting(e, currUser);
        });

    }

    @Async("taskExecutor")
    public void postAdcs(String user) {
        long seq = SequenceFinder.findNextVal(Sequences.JOB_SEQ);
        BackJob job = new BackJob();
        job.setJobSeq(seq);
        job.setJobDscr(RECOVERY);
        job.setSts(1);
        job.setStrtDt(Instant.now());
        backJobRepository.save(job);
        job.setTotRec(0);
        int accRec = 0;
        List<MwRcvryLoadStg> allUnposted = mwRcvryLoadStgRepository.findAllByTrxStsKey(true);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        allUnposted.forEach(r -> {
            String opnDt = "";
            opnDt = new SimpleDateFormat("dd/MM/yyyy").format(r.getTrxDt());
            long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);

            try {
                MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
                mwRcvryTrx.setRcvryTrxSeq(trxSeq);
                Long clntSeq = adjustRecoveryPaymentAccount(r.getClntId(), r.getAmt(), r.getAgentId(), r.getTrxId(), user, opnDt, true,
                        mwRcvryTrx);
                if (clntSeq != null) {
                    r.setTrxStsKey(true);
                    job.setTotRec(job.getTotRec() + 1);
                }

            } catch (Exception e) {
                r.setTrxStsKey(false);
                r.setCmnt("Recovery not posted");
            }
        });

        job.setEndDt(Instant.now());
        job.setSts(0);
        job.setRjRec(allUnposted.size() - job.getTotRec());
        job.setTotRec(allUnposted.size());

        mwRcvryLoadStgRepository.save(allUnposted);
        backJobRepository.save(job);
    }

    /*    @Async
    public void uploadAdcRecoveryFile( String fileNm, String user ) {
        List< AppRcvryDTO > rcvryList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader( new FileReader( fileNm ) );

            // read file line by line
            String line = null;
            Scanner scanner = null;
            int index = 0;
            boolean firstLineRead = false;
            while ( ( line = reader.readLine() ) != null ) {
                if ( !firstLineRead ) {
                    firstLineRead = true;
                    continue;
                }
                AppRcvryDTO rcvry = new AppRcvryDTO();
                scanner = new Scanner( line );
                scanner.useDelimiter( "," );
                while ( scanner.hasNext() ) {
                    String data = scanner.next();
                    if ( data != null && !data.isEmpty() ) {
                        if ( index == 0 ) {
                            rcvry.instr = data.toString();
                        } else if ( index == 1 )
                            rcvry.clientId = data.toString();
                        else if ( index == 2 )
                            rcvry.pymtAmt = Long.parseLong( data );
                        else if ( index == 3 )
                            rcvry.pymtDt = data;
                        else if ( index == 4 )
                            rcvry.rcvryTypsSeq = Long.parseLong( data );
                        else
                            System.out.println( "invalid data::" + data );
                        index++;
                    }

                }
                if ( rcvry.clientId != null ) {
                    rcvry.user = user;
                    rcvry.post = true;
                    rcvryList.add( rcvry );
                }
                index = 0;
            }
            reader.close();
            scanner.close();
            if ( rcvryList.size() >= 0 ) {
                rcvryList.forEach( r -> {
                    applyRecoveryPayment( r );
                } );
                File file = new File( fileNm );
                file.setWritable( true );
                Instant.now().toString();
                File newFile = new File( file.getParent() + file.separator + "ADC_FILE_UPLOADED_" + Instant.now().toString() + ".csv" );
                if ( file.renameTo( newFile ) ) {
                    System.out.println( "File rename success" );
                    newFile.delete();
                } else {
                    System.out.println( "File rename failed" );
                }
            }

        } catch ( NumberFormatException | IOException e ) {

            e.printStackTrace();
        }

    }*/

    public Long recoveryAdjustment(String clientId, String currUser, String token) {

        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);
        long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(clnt.getClntSeq());
        List<Object[]> dueInstallments = mwRcvryRepository.getDuaInstallment(clnt.getClntSeq());

        //Added by Areeba
        int incidentTyp;
        MwDsbltyRpt rpt = mwDsbltyRptRepository.findByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);
        if (rpt != null) {
            incidentTyp = 1;
        } else {
            incidentTyp = 0;
        }
        //Ended by Areeba

        //Edited by Areeba - 22-04-2022
        if (dueInstallments == null || dueInstallments.size() == 0) {
            return null;
        }
        //Ended by Areeba

        // Zohaib Asim - Dated 04-11-2021 - Sanction List Phase 2
        Query qry = em.createNativeQuery("SELECT FN_FIND_CLNT_TAGGED( 'AML', " + clientId +
                ", null ) FROM DUAL");
        String fnResp = qry.getSingleResult().toString();
        if (fnResp.contains("SUCCESS")) {
            return -1L;
        }
        // End


        Types typ = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0020", 4, 0, true);// LOAN ADJ TYP

        List<MwRcvryDtl> rcvryToSave = new ArrayList();
        Instant currIns = Instant.now();
        MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
        mwRcvryTrx.setRcvryTrxSeq(trxSeq);
        mwRcvryTrx.setInstrNum(null);
        mwRcvryTrx.setPymtDt(new Date());
        mwRcvryTrx.setRcvryTypSeq(typ.getTypSeq());
        mwRcvryTrx.setPymtStsKey(0L);
        mwRcvryTrx.setPostFlg(1L);
        mwRcvryTrx.setEffStartDt(currIns);
        mwRcvryTrx.setEffEndDt(currIns);
        mwRcvryTrx.setCrtdBy(currUser);
        mwRcvryTrx.setCrtdDt(currIns);
        mwRcvryTrx.setDelFlg(false);
        mwRcvryTrx.setLastUpdBy(currUser);
        mwRcvryTrx.setLastUpdDt(currIns);
        mwRcvryTrx.setCrntRecFlg(true);
        mwRcvryTrx.setPymtRef(clnt.getClntSeq());

        /*  Long prdSeq = ( dueInstallments.get( 0 )[ 0 ] == null ) ? 0L
                : new BigDecimal( dueInstallments.get( 0 )[ 0 ].toString() ).longValue();
        Long loanAppSeq = ( dueInstallments.get( 0 )[ 2 ] == null ) ? 0L
                : new BigDecimal( dueInstallments.get( 0 )[ 2 ].toString() ).longValue();*/
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.CHARG_ORDER).setParameter("clntSeq", clnt.getClntSeq());// CHARGE ADJ
        // ORDER
        List<Object[]> chrgOrder = q.getResultList();

        long totalPayaable = 0L;
        List<PaymentScheduleDetail> isntallments = new ArrayList<PaymentScheduleDetail>();

        //Edited by Areeba
        MwRefCdVal sts;
        if (incidentTyp == 1)
            sts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0951");// DISABILITY Adjustment
        else
            sts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0949");// DEATH Adjustment


        for (Object[] obj : dueInstallments) {

            Long loanAppSeq = new BigDecimal(obj[2].toString()).longValue();
            long ppalDueAmt = ((obj[5] == null) ? 0L : new BigDecimal(obj[5].toString()).longValue());
            long servChrgAmt = ((obj[6] == null) ? 0L : new BigDecimal(obj[6].toString()).longValue());
            long chrgs = ((obj[7] == null) ? 0L : new BigDecimal(obj[7].toString()).longValue());


            Long dueAmt = ppalDueAmt + servChrgAmt;
            Long paidAmt = (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue();
            if (dueAmt.longValue() > paidAmt.longValue()) {
                totalPayaable = totalPayaable + dueAmt + paidAmt - chrgs;
                List<MwRcvryDtl> rcvryToAdd = new ArrayList();
                Long paySchedDtlSeq = (obj[3] == null) ? 0L : new BigDecimal(obj[3].toString()).longValue();

                Query serChrgQury = em.createNativeQuery("select t.TYP_SEQ \r\n" + "from mw_loan_app_chrg_stngs acs\r\n"
                        + "join mw_typs t on t.TYP_SEQ=acs.TYP_SEQ and t.CRNT_REC_FLG=1 and t.TYP_ID='0017'\r\n"
                        + "where loan_app_seq=:loanAppSeq").setParameter("loanAppSeq", loanAppSeq); // service chrge

                Object servChrg = serChrgQury.getSingleResult();

                Long ppalChrgSeq = -1L;
                Long servChrgSeq = (servChrg == null) ? 0L : new BigDecimal(servChrg.toString()).longValue();/// 17

                PaymentScheduleDetail paymentScheduleDetail = paymentScheduleDetailRepository
                        .findOneByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
                paymentScheduleDetail.setPymtStsKey(sts.getRefCdSeq());
                paymentScheduleDetail.setLastUpdBy(currUser);
                paymentScheduleDetail.setLastUpdDt(currIns);
                isntallments.add(paymentScheduleDetail);

                List<MwRcvryDtl> recoverd = mwRcvryRepository.findAllByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
                MwRcvryDtl ppalAmt = recoverd.stream()
                        .filter(recovery -> paySchedDtlSeq.longValue() == recovery.getPaySchedDtlSeq().longValue()
                                && ppalChrgSeq.longValue() == recovery.getChrgTypKey().longValue())
                        .findAny().orElse(null); // PPAL

                Long ppalAmtRcv = ppalAmt != null && ppalAmt.getPymtAmt() != null ? ppalAmt.getPymtAmt() : (long) 0;
                if ((ppalDueAmt - ppalAmtRcv) > 0) {

                    long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                    MwRcvryDtl ppalRevry = new MwRcvryDtl();
                    ppalRevry.setPymtAmt(ppalDueAmt - ppalAmtRcv);
                    ppalRevry.setChrgTypKey(ppalChrgSeq);
                    ppalRevry.setPaySchedDtlSeq(paySchedDtlSeq);
                    ppalRevry.setRcvryChrgSeq(seq);
                    ppalRevry.setRcvryTrxSeq(trxSeq);
                    ppalRevry.setCrntRecFlg(true);
                    ppalRevry.setCrtdBy(currUser);
                    ppalRevry.setCrtdDt(currIns);
                    ppalRevry.setDelFlg(false);
                    ppalRevry.setEffStartDt(currIns);
                    ppalRevry.setLastUpdBy(currUser);
                    ppalRevry.setLastUpdDt(currIns);
                    rcvryToSave.add(ppalRevry);
                }

                MwRcvryDtl chrgsAmt = recoverd.stream().filter(recovery -> paySchedDtlSeq == recovery.getPaySchedDtlSeq().longValue()
                        && servChrgSeq.longValue() == recovery.getChrgTypKey().longValue()).findAny().orElse(null);
                Long chrgsAmtRcv = chrgsAmt != null && chrgsAmt.getPymtAmt() != null ? chrgsAmt.getPymtAmt() : (long) 0;// service chrg
                if ((servChrgAmt - chrgsAmtRcv) > 0) {
                    long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                    MwRcvryDtl chrgsRevry = new MwRcvryDtl();
                    chrgsRevry.setPymtAmt(servChrgAmt - chrgsAmtRcv);
                    chrgsRevry.setChrgTypKey(servChrgSeq);
                    chrgsRevry.setPaySchedDtlSeq(paySchedDtlSeq);
                    chrgsRevry.setRcvryChrgSeq(seq);
                    chrgsRevry.setRcvryTrxSeq(trxSeq);
                    chrgsRevry.setCrntRecFlg(true);
                    chrgsRevry.setCrtdBy(currUser);
                    chrgsRevry.setCrtdDt(currIns);
                    chrgsRevry.setDelFlg(false);
                    chrgsRevry.setEffStartDt(currIns);
                    chrgsRevry.setLastUpdBy(currUser);
                    chrgsRevry.setLastUpdDt(currIns);
                    rcvryToSave.add(chrgsRevry);
                }

            }
        }
        mwRcvryTrx.setPymtAmt(totalPayaable);
        MwRefCdVal clntSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);
        MwRefCdVal activeSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        List<LoanApplication> apps = loanApplicationRepository.findAllByClntSeqAndLoanAppStsAndCrntRecFlg(clnt.getClntSeq(),
                activeSts.getRefCdSeq(), true);

        List<LoanApplication> appsUpdate = new ArrayList();

        apps.forEach(app -> {
            app.setLastUpdBy("w-" + currUser);
            app.setLastUpdDt(currIns);
            app.setLoanAppSts(clntSts.getRefCdSeq());
            app.setLoanAppStsDt(currIns);
        });

        boolean flag = recoveryComponent.saveRecoveryPayment(mwRcvryTrx, rcvryToSave, appsUpdate, isntallments);
        if (flag) {
            //Edited by Areeba
            if (incidentTyp == 0) {
                MwJvHdr jvh = vchrPostingService.genrateRecoveryJv(trxSeq, RECOVERY,
                        "Loan is adjusted against death case " + clnt.getFrstNm() + " " + (clnt.getLastNm() == null ? "" : clnt.getLastNm()),
                        typ.getGlAcctNum(), currUser, chrgOrder, rcvryToSave, new Date(), brnch.getBrnchSeq());
            } else {
                MwJvHdr jvh = vchrPostingService.genrateRecoveryJv(trxSeq, RECOVERY,
                        "Loan is adjusted against disability case " + clnt.getFrstNm() + " " + (clnt.getLastNm() == null ? "" : clnt.getLastNm()),
                        typ.getGlAcctNum(), currUser, chrgOrder, rcvryToSave, new Date(), brnch.getBrnchSeq());
            }

        }

        long tagCnic = clnt.getCnicNum();

        //Edited by Areeba
        if (incidentTyp == 0) {
            MwDthRpt dth = mwDthRptRepository.findTopByClntSeqAndCrntRecFlgOrderByDtOfDthDesc(clnt.getClntSeq(), true);
            if (dth.getClntNomFlg() == 1L) {
                // Added by Zohaib Asim - Dated 22-02-2022
                // KSK Loan dont have Relative Information
                long loanAppSeq = 0;
                for (LoanApplication obj : apps) {
                    if (obj.getPrdSeq() != 29)
                        loanAppSeq = obj.getLoanAppSeq();
                }
                MwClntRel clntRel = mwClntRelRepository.findOneByLoanAppSeqAndRelTypFlgAndCrntRecFlg(loanAppSeq, 1L, true);
                // End
                tagCnic = clntRel.getCnicNum();
            }
        } else {
            MwDsbltyRpt dsblty = mwDsbltyRptRepository.findTopByClntSeqAndCrntRecFlgOrderByDtOfDsbltyDesc(clnt.getClntSeq(), true);
            if (dsblty.getClntNomFlg() == 1L) {
                // KSK Loan dont have Relative Information
                long loanAppSeq = 0;
                for (LoanApplication obj : apps) {
                    if (obj.getPrdSeq() != 29)
                        loanAppSeq = obj.getLoanAppSeq();
                }
                MwClntRel clntRel = mwClntRelRepository.findOneByLoanAppSeqAndRelTypFlgAndCrntRecFlg(loanAppSeq, 1L, true);
                tagCnic = clntRel.getCnicNum();
            }
        }

        loanApplicationRepository.save(apps);
        setupServiceClient.addClntTagList(tagCnic, apps.get(0).getLoanAppSeq(), token);

        return flag == false ? null : clnt.getClntSeq();

    }

    public long addExcessRecovery(AppRcvryDTO appRcvryDTO) {
        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0005", 2, 0, true);
        long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
        Instant currIns = Instant.now();
        List<MwRcvryDtl> rcvryToSave = new ArrayList();
        MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
        mwRcvryTrx.setRcvryTrxSeq(trxSeq);
        mwRcvryTrx.setInstrNum(null);
        mwRcvryTrx.setPymtDt(new Date());
        mwRcvryTrx.setPymtAmt(appRcvryDTO.pymtAmt);
        mwRcvryTrx.setRcvryTypSeq(appRcvryDTO.rcvryTypsSeq);
        mwRcvryTrx.setPymtStsKey(0L);
        mwRcvryTrx.setPostFlg(1L);
        mwRcvryTrx.setEffStartDt(currIns);
        mwRcvryTrx.setEffEndDt(currIns);
        mwRcvryTrx.setCrtdBy(appRcvryDTO.user);
        mwRcvryTrx.setCrtdDt(currIns);
        mwRcvryTrx.setDelFlg(false);
        mwRcvryTrx.setLastUpdBy(appRcvryDTO.user);
        mwRcvryTrx.setLastUpdDt(currIns);
        mwRcvryTrx.setCrntRecFlg(true);
        mwRcvryTrx.setPymtRef(Long.parseLong(appRcvryDTO.clientId));
        mwRcvryTrx.setPrntLoanAppSeq(appRcvryDTO.prntLoanApp);

        long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
        MwRcvryDtl mwRcvry = new MwRcvryDtl();
        mwRcvry.setRcvryChrgSeq(seq);
        mwRcvry.setRcvryTrxSeq(trxSeq);
        mwRcvry.setChrgTypKey(exesstyp.getTypSeq());
        mwRcvry.setPaySchedDtlSeq(null);
        mwRcvry.setPymtAmt(appRcvryDTO.pymtAmt);
        mwRcvry.setCrntRecFlg(true);
        mwRcvry.setCrtdBy(appRcvryDTO.user);
        mwRcvry.setCrtdDt(currIns);
        mwRcvry.setDelFlg(false);
        mwRcvry.setEffStartDt(currIns);
        mwRcvry.setLastUpdBy(appRcvryDTO.user);
        mwRcvry.setLastUpdDt(currIns);

        recoveryComponent.saveRecoveryPayment(mwRcvryTrx, mwRcvry);
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(Long.parseLong(appRcvryDTO.clientId));
        Types rectyp = typesRepository.findOneByTypSeqAndCrntRecFlg(appRcvryDTO.rcvryTypsSeq, true);
        Long jvhSeq = vchrPostingService.genericJV(trxSeq, exesstyp.getTypStr(), exesstyp.getTypStr(), exesstyp.getGlAcctNum(),
                rectyp.getGlAcctNum(), appRcvryDTO.pymtAmt, appRcvryDTO.user, brnch.getBrnchSeq(), Instant.now());
        return Long.parseLong(appRcvryDTO.clientId);

    }

    public Long calculatePayOffAmount(Long clntSeq) {

        Query dueQury = em.createNativeQuery(
                        "select la.prd_seq,psd.pymt_sched_hdr_seq,la.loan_app_seq,psd.pymt_sched_dtl_seq,psd.inst_num,psd.due_dt,psd.ppal_amt_due,psd.tot_chrg_due,\r\n"
                                + "(\r\n" + "select scal.ref_cd\r\n" + "from mw_loan_app_chrg_stngs slacs \r\n"
                                + "join mw_typs styp on styp.typ_seq=slacs.typ_seq and styp.crnt_rec_flg=1 and styp.typ_id='0017'\r\n"
                                + "join mw_ref_cd_val scal on scal.ref_cd_seq=slacs.calc_flg and scal.crnt_rec_flg=1 and scal.del_flg=0\r\n"
                                + "where slacs.loan_app_seq = la.loan_app_seq) scal_flg\r\n" + ",psc.amt as amt,psc.chrg_typs_seq,\r\n"
                                + "(select calc_flg\r\n" + "from mw_clnt_hlth_insr chi \r\n"
                                + "join mw_ref_cd_val scal on scal.ref_cd_seq=chi.calc_flg and scal.crnt_rec_flg=1 and scal.del_flg=0 \r\n"
                                + "where chi.loan_app_seq=la.loan_app_seq and psc.chrg_typs_seq=-2 and chi.crnt_rec_flg=1\r\n" + "union \r\n"
                                + "select calc_flg\r\n" + "from mw_loan_app_chrg_stngs lacs \r\n"
                                + "join mw_ref_cd_val scal on scal.ref_cd_seq=lacs.calc_flg and scal.crnt_rec_flg=1 and scal.del_flg=0\r\n"
                                + "where lacs.loan_app_seq = la.loan_app_seq  and lacs.typ_seq=psc.chrg_typs_seq ) calc_flg,\r\n"
                                + "rd.pymt_amt as reco,rt.pymt_dt \r\n" + "from mw_loan_app la  \r\n"
                                + "join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1  \r\n"
                                + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
                                + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and val.del_flg=0 and (val.ref_cd = '0005' or val.ref_cd ='1245')  \r\n"
                                + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
                                + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq=psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
                                + "left outer join mw_rcvry_dtl rd on rd.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rd.crnt_rec_flg=1\r\n"
                                + "left outer join mw_rcvry_trx rt on rt.rcvry_trx_seq=rd.rcvry_trx_seq and rt.crnt_rec_flg=1\r\n"
                                + "where la.clnt_seq=:clntSeq and la.crnt_rec_flg=1 \r\n" + "order by psd.due_dt,la.prd_seq")
                .setParameter("clntSeq", clntSeq);

        List<Object[]> dueInstallments = dueQury.getResultList();

        dueInstallments.forEach(due -> {

        });

        if (dueInstallments == null || dueInstallments.size() == 0) {

            return null;
        }
        return null;

    }

    public Long payOffLoan(String clientId, String currUser, String token) {
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(Long.parseLong(clientId), true);
        long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(clnt.getClntSeq());

        List<Object[]> dueInstallments = mwRcvryRepository.getDuaInstallment(clnt.getClntSeq());
        if (dueInstallments == null || dueInstallments.size() == 0) {
            return null;
        }

        Types typ = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0020", 4, 0, true);

        List<MwRcvryDtl> rcvryToSave = new ArrayList();
        Instant currIns = Instant.now();
        MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
        mwRcvryTrx.setRcvryTrxSeq(trxSeq);
        mwRcvryTrx.setInstrNum(null);
        mwRcvryTrx.setPymtDt(new Date());
        mwRcvryTrx.setRcvryTypSeq(typ.getTypSeq());
        mwRcvryTrx.setPymtStsKey(0L);
        mwRcvryTrx.setPostFlg(1L);
        mwRcvryTrx.setEffStartDt(currIns);
        mwRcvryTrx.setEffEndDt(currIns);
        mwRcvryTrx.setCrtdBy(currUser);
        mwRcvryTrx.setCrtdDt(currIns);
        mwRcvryTrx.setDelFlg(false);
        mwRcvryTrx.setLastUpdBy(currUser);
        mwRcvryTrx.setLastUpdDt(currIns);
        mwRcvryTrx.setCrntRecFlg(true);
        mwRcvryTrx.setPymtRef(clnt.getClntSeq());

        /*  Long prdSeq = ( dueInstallments.get( 0 )[ 0 ] == null ) ? 0L
                : new BigDecimal( dueInstallments.get( 0 )[ 0 ].toString() ).longValue();
        Long loanAppSeq = ( dueInstallments.get( 0 )[ 2 ] == null ) ? 0L
                : new BigDecimal( dueInstallments.get( 0 )[ 2 ].toString() ).longValue();*/
        Query q = em.createNativeQuery(com.idev4.rds.util.Queries.CHARG_ORDER).setParameter("clntSeq", clnt.getClntSeq());
        List<Object[]> chrgOrder = q.getResultList();

        long totalPayaable = 0L;
        List<PaymentScheduleDetail> isntallments = new ArrayList<PaymentScheduleDetail>();

        MwRefCdVal sts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0949");

        for (Object[] obj : dueInstallments) {

            Long loanAppSeq = new BigDecimal(obj[2].toString()).longValue();
            long ppalDueAmt = ((obj[5] == null) ? 0L : new BigDecimal(obj[5].toString()).longValue());
            long servChrgAmt = ((obj[6] == null) ? 0L : new BigDecimal(obj[6].toString()).longValue());
            long chrgs = ((obj[7] == null) ? 0L : new BigDecimal(obj[7].toString()).longValue());

            Long dueAmt = ppalDueAmt + servChrgAmt;
            Long paidAmt = (obj[8] == null) ? 0L : new BigDecimal(obj[8].toString()).longValue();
            if (dueAmt.longValue() > paidAmt.longValue()) {
                totalPayaable = totalPayaable + dueAmt - paidAmt - chrgs;
                List<MwRcvryDtl> rcvryToAdd = new ArrayList();
                Long paySchedDtlSeq = (obj[3] == null) ? 0L : new BigDecimal(obj[3].toString()).longValue();

                Query serChrgQury = em.createNativeQuery("select t.TYP_SEQ \r\n" + "from mw_loan_app_chrg_stngs acs\r\n"
                        + "join mw_typs t on t.TYP_SEQ=acs.TYP_SEQ and t.CRNT_REC_FLG=1 and t.TYP_ID='0017'\r\n"
                        + "where loan_app_seq=:loanAppSeq").setParameter("loanAppSeq", loanAppSeq);

                Object servChrg = serChrgQury.getSingleResult();

                Long ppalChrgSeq = -1L;
                Long servChrgSeq = (servChrg == null) ? 0L : new BigDecimal(servChrg.toString()).longValue();

                PaymentScheduleDetail paymentScheduleDetail = paymentScheduleDetailRepository
                        .findOneByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
                paymentScheduleDetail.setPymtStsKey(sts.getRefCdSeq());
                isntallments.add(paymentScheduleDetail);

                List<MwRcvryDtl> recoverd = mwRcvryRepository.findAllByPaySchedDtlSeqAndCrntRecFlg(paySchedDtlSeq, true);
                MwRcvryDtl ppalAmt = recoverd.stream()
                        .filter(recovery -> paySchedDtlSeq.longValue() == recovery.getPaySchedDtlSeq().longValue()
                                && ppalChrgSeq.longValue() == recovery.getChrgTypKey().longValue())
                        .findAny().orElse(null);

                Long ppalAmtRcv = ppalAmt != null && ppalAmt.getPymtAmt() != null ? ppalAmt.getPymtAmt() : (long) 0;
                if ((ppalDueAmt - ppalAmtRcv) > 0) {

                    long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                    MwRcvryDtl ppalRevry = new MwRcvryDtl();
                    ppalRevry.setPymtAmt(ppalDueAmt - ppalAmtRcv);
                    ppalRevry.setChrgTypKey(ppalChrgSeq);
                    ppalRevry.setPaySchedDtlSeq(paySchedDtlSeq);
                    ppalRevry.setRcvryChrgSeq(seq);
                    ppalRevry.setRcvryTrxSeq(trxSeq);
                    ppalRevry.setCrntRecFlg(true);
                    ppalRevry.setCrtdBy(currUser);
                    ppalRevry.setCrtdDt(currIns);
                    ppalRevry.setDelFlg(false);
                    ppalRevry.setEffStartDt(currIns);
                    ppalRevry.setLastUpdBy(currUser);
                    ppalRevry.setLastUpdDt(currIns);
                    rcvryToSave.add(ppalRevry);
                }

                MwRcvryDtl chrgsAmt = recoverd.stream().filter(recovery -> paySchedDtlSeq == recovery.getPaySchedDtlSeq().longValue()
                        && servChrgSeq.longValue() == recovery.getChrgTypKey().longValue()).findAny().orElse(null);
                Long chrgsAmtRcv = chrgsAmt != null && chrgsAmt.getPymtAmt() != null ? chrgsAmt.getPymtAmt() : (long) 0;
                if ((servChrgAmt - chrgsAmtRcv) > 0) {
                    long seq = SequenceFinder.findNextVal(Sequences.RCVRY_CHRG_SEQ);
                    MwRcvryDtl chrgsRevry = new MwRcvryDtl();
                    chrgsRevry.setPymtAmt(servChrgAmt - chrgsAmtRcv);
                    chrgsRevry.setChrgTypKey(servChrgSeq);
                    chrgsRevry.setPaySchedDtlSeq(paySchedDtlSeq);
                    chrgsRevry.setRcvryChrgSeq(seq);
                    chrgsRevry.setRcvryTrxSeq(trxSeq);
                    chrgsRevry.setCrntRecFlg(true);
                    chrgsRevry.setCrtdBy(currUser);
                    chrgsRevry.setCrtdDt(currIns);
                    chrgsRevry.setDelFlg(false);
                    chrgsRevry.setEffStartDt(currIns);
                    chrgsRevry.setLastUpdBy(currUser);
                    chrgsRevry.setLastUpdDt(currIns);
                    rcvryToSave.add(chrgsRevry);
                }

            }
        }
        mwRcvryTrx.setPymtAmt(totalPayaable);
        MwRefCdVal clntSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, COMPLETED_STATUS);
        MwRefCdVal activeSts = mwRefCdValRepository.findRefCdByGrpAndVal(APPLICATIONS_STATUS, ACTIVE_STATUS);
        List<LoanApplication> apps = loanApplicationRepository.findAllByClntSeqAndLoanAppStsAndCrntRecFlg(clnt.getClntSeq(),
                activeSts.getRefCdSeq(), true);

        List<LoanApplication> appsUpdate = new ArrayList();

        apps.forEach(napp -> {
            napp.setLastUpdBy("w-" + currUser);
            napp.setLastUpdDt(currIns);
            napp.setLoanAppSts(clntSts.getRefCdSeq());
            napp.setLoanAppStsDt(currIns);
            appsUpdate.add(napp);
        });

        boolean flag = recoveryComponent.saveRecoveryPayment(mwRcvryTrx, rcvryToSave, appsUpdate, isntallments);
        if (flag) {
            MwJvHdr jvh = vchrPostingService.genrateRecoveryJv(trxSeq, RECOVERY,
                    "Loan is adjusted against death case " + clnt.getFrstNm() + " " + (clnt.getLastNm() == null ? "" : clnt.getLastNm()),
                    typ.getGlAcctNum(), currUser, chrgOrder, rcvryToSave, new Date(), brnch.getBrnchSeq());
        }

        MwDthRpt dth = mwDthRptRepository.findTopByClntSeqAndCrntRecFlgOrderByDtOfDthDesc(clnt.getClntSeq(), true);

        long tagCnic = clnt.getCnicNum();
        if (dth.getClntNomFlg() == 1L) {
            // Added by Zohaib Asim - Dated 22-02-2022
            // KSK Loan dont have Relative Information
            long loanAppSeq = 0;
            for (LoanApplication obj : apps) {
                if (obj.getPrdSeq() != 29)
                    loanAppSeq = obj.getLoanAppSeq();
            }
            // MwClntRel clntRel = mwClntRelRepository.findOneByLoanAppSeqAndRelTypFlgAndCrntRecFlg(apps.get(0).getLoanAppSeq(), 1L, true);
            MwClntRel clntRel = mwClntRelRepository.findOneByLoanAppSeqAndRelTypFlgAndCrntRecFlg(loanAppSeq, 1L, true);
            // End
            tagCnic = clntRel.getCnicNum();
        }

        setupServiceClient.addClntTagList(tagCnic, apps.get(0).getLoanAppSeq(), token);

        return flag == false ? null : clnt.getClntSeq();

    }

    /* public boolean revertAdjustedRecovery( String clientId, String currUser ) {
        Clients clnt = clientRepository.findOneByClntIdAndCrntRecFlg( clientId, true );

        Types typ = typesRepository.findOneByTypIdAndTypCtgryKeyAndCrntRecFlg( "0020", 4, true );

        MwRcvryTrx recv = mwRcvryTrxRepository.findOneByRcvryTypSeqANDPymtRefAndCrntRecFlg( typ.getTypSeq(), clnt.getClntSeq(), true );
        AjRcvryDTO dto = new AjRcvryDTO();
        dto.adjPymtDt = new Date();
        dto.chngRsnKey = recv.getRcvryTrxSeq();
        dto.post = recv.getPostFlg().longValue() == 1L ? true : false;
        dto.trxId = recv.getRcvryTrxSeq();
        dto.rcvryTypsSeq = recv.getRcvryTypSeq();
        dto.pymtAmt = 0L;

        Long flag = reversePayment( dto, currUser );

        return true;
    }
    */

    @Transactional
    public Long anmlLoanAdjust(Long loanAppSeq, Long amt, String user, Long anmlRgstrSeq) {
        LoanApplication app = loanApplicationRepository.findByLoanAppSeqAndCrntRecFlg(loanAppSeq, true);
        Long trxSeq = SequenceFinder.findNextVal(Sequences.RCVRY_TRX_SEQ);
        Types type = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0454", 2, 0, true);
        MwRcvryTrx mwRcvryTrx = new MwRcvryTrx();
        mwRcvryTrx.setRcvryTrxSeq(trxSeq);
        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(app.getClntSeq(), true);
        MwBrnch brnch = mwBrnchRepository.findOneByClntSeq(app.getClntSeq());
        AppRcvryDTO appRcvryDTO = new AppRcvryDTO();
        appRcvryDTO.brnchSeq = brnch.getBrnchSeq();
        appRcvryDTO.clientId = clnt.getClntId();
        appRcvryDTO.post = true;
        appRcvryDTO.pymtAmt = amt;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        appRcvryDTO.pymtDt = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        appRcvryDTO.rcvryTypsSeq = type.getTypSeq();
        appRcvryDTO.user = user;
        Map map = applyRecoveryPayment(appRcvryDTO);
        MwRcvryTrx trx = mwRcvryTrxRepository.findTop1ByPymtRef("" + clnt.getClntSeq());
        if (trx != null && trx.getPymtAmt().longValue() == amt.longValue()) {
            trx.setPrntRcvryRef(anmlRgstrSeq);
            mwRcvryTrxRepository.save(trx);
        }
        return clnt.getClntSeq();

    }

    // public List getWriteOffClntsForBrnch( Long brnchSeq ) {
    // String query = "select clnt_seq, clnt_nm,\r\n" + " LISTAGG(PRD_GRP_SEQ,',') within group (order by clnt_seq) PRD_GRP_SEQ,\r\n"
    // + " sum(outsd_ppal_amt)+sum(nvl(wc.outsd_srvc_chrg_amt,0))+\r\n"
    // + " nvl(sum((select sum(chrg_amt) from mw_wrt_of_oth_chrg oc \r\n"
    // + " where crnt_rec_flg=1 and oc.wrt_of_clnt_seq=wc.wrt_of_clnt_seq)),0\r\n" + " ) total_due,\r\n"
    // + " sum((select sum(dtl.pymt_amt) \r\n" + " from mw_wrt_of_rcvry_trx trx\r\n"
    // + " join mw_wrt_of_rcvry_dtl dtl on dtl.wrt_of_rcvry_trx_seq=trx.wrt_of_rcvry_trx_seq and dtl.crnt_rec_flg=1\r\n"
    // + " where trx.crnt_rec_flg=1 and dtl.loan_app_seq=wc.loan_app_seq)\r\n" + " ) tot_paid,\r\n"
    // + " max(LOAN_APP_STS)\r\n" + "from mw_wrt_of_clnt wc \r\n" + "where wc.BRNCH_SEQ=:brnch_seq\r\n"
    // + "group by clnt_nm,wc.clnt_seq";
    //
    // Query q = em.createNativeQuery( query ).setParameter( "brnch_seq", brnchSeq );
    //
    // List< Object[] > results = q.getResultList();
    // List< RecoveryListingDTO > recoveryListingDTOs = new ArrayList< RecoveryListingDTO >();
    // if ( results != null && results.size() != 0 ) {
    // results.forEach( obj -> {
    // RecoveryListingDTO recoveryListingDTO = new RecoveryListingDTO();
    // recoveryListingDTO.clntSeq = obj[ 0 ].toString();
    //
    // recoveryListingDTO.frstNm = obj[ 1 ] == null ? "" : obj[ 1 ].toString();
    //
    // recoveryListingDTO.prd = obj[ 2 ] == null ? "" : obj[ 2 ].toString();
    //
    // recoveryListingDTO.totalDue = obj[ 3 ] == null ? 0 : new BigDecimal( obj[ 3 ].toString() ).longValue();
    //
    // recoveryListingDTO.totalRecv = obj[ 4 ] == null ? 0 : new BigDecimal( obj[ 4 ].toString() ).longValue();
    //
    // recoveryListingDTO.status = obj[ 5 ] == null ? "" : obj[ 5 ].toString();
    // recoveryListingDTOs.add( recoveryListingDTO );
    // } );
    // }
    // return recoveryListingDTOs;
    // }

    public Map<String, Object> getWriteOffClntsForBrnch(Long brnchSeq, Integer pageIndex, Integer pageSize, String filter,
                                                        Boolean isCount) {
        String writeOffClntQuery = "select clnt_seq, clnt_nm,     \r\n"
                + "       LISTAGG(PRD.PRD_CMNT,',') within group (order by clnt_seq) PRD_GRP_SEQ,\r\n"
                + "       sum(outsd_ppal_amt)+sum(nvl(wc.outsd_srvc_chrg_amt,0))+nvl(sum((select sum(chrg_amt) from mw_wrt_of_oth_chrg oc where crnt_rec_flg=1 and oc.wrt_of_clnt_seq=wc.wrt_of_clnt_seq)),0) total_due,\r\n"
                + "       max((select sum(pymt_amt) from mw_wrt_of_rcvry_trx trx where trx.crnt_rec_flg=1 and trx.clnt_seq=wc.clnt_seq)) tot_paid,\r\n"
                + "       LOAN_APP_STS \r\n" + "from mw_wrt_of_clnt wc JOIN MW_PRD PRD ON PRD.PRD_SEQ = wc.PRD_GRP_SEQ AND PRD.CRNT_REC_FLG = 1 where wc.BRNCH_SEQ=:brnch_seq\r\n" + "??"
                + "group by clnt_nm,wc.clnt_seq,LOAN_APP_STS";

        String writeOffClntCount = "select count(distinct wc.clnt_seq)  from mw_wrt_of_clnt wc where wc.BRNCH_SEQ=:brnch_seq";

        if (filter != null && filter.length() > 0) {
            String search = " and (lower(clnt_seq) like '%?%') ".replace("?", filter.toLowerCase());
            writeOffClntQuery = writeOffClntQuery.replace("??", search);
            writeOffClntCount += search;
        } else {
            writeOffClntQuery = writeOffClntQuery.replace("??", " ");
        }

        List<Object[]> results = em.createNativeQuery(writeOffClntQuery).setParameter("brnch_seq", brnchSeq)
                .setFirstResult((pageIndex) * pageSize).setMaxResults(pageSize).getResultList();

        List<RecoveryListingDTO> recoveryListingDTOs = new ArrayList<RecoveryListingDTO>();
        if (results != null && results.size() != 0) {
            results.forEach(obj -> {
                RecoveryListingDTO recoveryListingDTO = new RecoveryListingDTO();

                recoveryListingDTO.clntSeq = obj[0].toString();
                recoveryListingDTO.frstNm = obj[1] == null ? "" : obj[1].toString();
                recoveryListingDTO.prd = obj[2] == null ? "" : obj[2].toString();
                recoveryListingDTO.totalDue = obj[3] == null ? 0 : new BigDecimal(obj[3].toString()).longValue();
                recoveryListingDTO.totalRecv = obj[4] == null ? 0 : new BigDecimal(obj[4].toString()).longValue();
                recoveryListingDTO.status = obj[5] == null ? "" : obj[5].toString();

                recoveryListingDTOs.add(recoveryListingDTO);
            });
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("clnts", recoveryListingDTOs);

        Long totalResultCount = 0L;
        if (isCount.booleanValue()) {
            totalResultCount = new BigDecimal(
                    em.createNativeQuery(writeOffClntCount).setParameter("brnch_seq", brnchSeq).getSingleResult().toString())
                    .longValue();
        }
        resp.put("count", totalResultCount);

        return resp;
    }

    public List getWriteOffRcvryTrxForClnt(Long clntSeq) {
        List resp = new ArrayList<>();
        List<MwWrtOfRcvryTrx> trxs = mwWrtOfRcvryTrxRepository.findAllByClntSeqAndCrntRecFlgOrderByCrtdDtAsc(clntSeq, true);
        for (MwWrtOfRcvryTrx trx : trxs) {
            Map m = new HashMap();
            m.put("trx", trx);
            String query = "select dtl.WRT_OF_RCVRY_DTL_SEQ, dtl.PYMT_AMT, dtl.chrg_typ_key, t.typ_str, dtl.LOAN_APP_SEQ  \r\n"
                    + "                from MW_WRT_OF_RCVRY_DTL dtl \r\n"
                    + "                left outer join mw_typs t on T.typ_seq=dtl.chrg_typ_key and t.crnt_rec_flg=1\r\n"
                    + "                where WRT_OF_RCVRY_TRX_SEQ=:trxSeq";

            Query q = em.createNativeQuery(query).setParameter("trxSeq", trx.getWrtOfRcvryTrxSeq());
            List<Object[]> results = q.getResultList();
            List dtlList = new ArrayList<>();
            results.forEach(obj -> {
                Map dtlMap = new HashMap();
                dtlMap.put("dtl_seq", obj[0] == null ? "" : obj[0].toString());
                dtlMap.put("amt", obj[1] == null ? "" : obj[1].toString());
                dtlMap.put("typ_seq", obj[2] == null ? "" : obj[2].toString());
                dtlMap.put("typ_str", obj[3] == null ? "" : obj[3].toString());
                dtlMap.put("loan_app_seq", obj[4] == null ? "" : obj[4].toString());
                dtlList.add(dtlMap);
            });
            m.put("dtl", dtlList);
            resp.add(m);
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> applyWrtOffRecoveryPayment(AppRcvryDTO appRcvryDTO) {
        // EntityManager em2 = emf.createEntityManager();
        // em.getTransaction().begin();

        Map<String, Object> resp = new HashMap<>();

        // define the stored procedure
        StoredProcedureQuery query = em.createStoredProcedureQuery("WO_RECOVERY");
        query.registerStoredProcedureParameter("P_CLNT_SEQ", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_RCVRY_TYP_SEQ", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_AMT", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_USER", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_INSTR_NUM", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_BRNCH_SEQ", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_RETURN_TRX_SEQ", String.class, ParameterMode.OUT);

        // set input parameter
        // query.setParameter( "x", 1.23d );
        // query.setParameter( "y", 4d );
        query.setParameter("P_CLNT_SEQ", Long.valueOf(appRcvryDTO.clientId));
        query.setParameter("P_RCVRY_TYP_SEQ", appRcvryDTO.rcvryTypId);
        query.setParameter("P_AMT", appRcvryDTO.pymtAmt);
        query.setParameter("P_USER", appRcvryDTO.user);
        query.setParameter("P_INSTR_NUM", appRcvryDTO.instr);
        query.setParameter("P_BRNCH_SEQ", appRcvryDTO.brnchSeq);

        // call the stored procedure and get the result

        long returnTrxSeq = 0;
        try {
            query.execute();
            returnTrxSeq = Long.parseLong(query.getOutputParameterValue("P_RETURN_TRX_SEQ").toString());
            log.info("returnTrxSeq " + returnTrxSeq);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }

        resp.put("returnTrxSeq", returnTrxSeq);

        if (returnTrxSeq != 0) {
            resp.put("success", "Amount Posted");
        } else {
            resp.put("warning", "Amount Posted");
        }
        log.info("Executing WO_RECOVERY for Client : " + appRcvryDTO.clientId + "  Amount : " + appRcvryDTO.pymtAmt);

        // em.getTransaction().commit();
        // em.close();

        return resp;
    }

    @Transactional
    public boolean wrtOffRcvryReversal(long trxSeq, long loanAppSeq, String user) {
        MwJvHdr jvHdr = mwJvHdrRepository.findTop1ByEntySeqAndEntyTypOrderByJvHdrSeqDesc(trxSeq, "WO Recovery");

        // define the stored procedure
        StoredProcedureQuery query = em.createStoredProcedureQuery("PROC_WRT_RECOVERY");
        query.registerStoredProcedureParameter("P_LOAN_APP_SEQ", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_JV_HDR_SEQ", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_USER", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_ALERT_TEXT", String.class, ParameterMode.OUT);

        // set input parameter
        query.setParameter("P_LOAN_APP_SEQ", loanAppSeq);
        query.setParameter("P_JV_HDR_SEQ", jvHdr.getJvHdrSeq().longValue());
        query.setParameter("P_USER", user);

        // call the stored procedure and get the result
        String returnMessage = "";
        try {
            query.execute();
            returnMessage = query.getOutputParameterValue("P_ALERT_TEXT").toString();
            log.info(returnMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        log.info("Executing WO_RECOVERY for Loan Application Sequence : " + loanAppSeq + "  TrxSeq : " + trxSeq + " User " + user);

        return true;
    }

    //Added by Areeba
    public void reverseDisabilityReceivable(long clntSeq, long expSeq, String currUser) {
        Instant currIns = Instant.now();
        Types exesstyp = typesRepository.findOneByTypIdAndTypCtgryKeyAndBrnchSeqAndCrntRecFlg("0001", 4, 0, true);

        boolean rcvryTrxSizeFlg = false;
        // IN CASE MULTIPLE RECOVERY TRANSACTION EXIST FOR SPECIFIC USER
        String pExpSeq = "EXP_SEQ%" + expSeq + "%";
        List<MwRcvryTrx> trxArr = mwRcvryTrxRepository.findByRcvryTypSeqAndPymtRefAndCrntRecFlg(pExpSeq, exesstyp.getTypSeq(), clntSeq);

        if (trxArr != null && trxArr.size() > 0) {
            rcvryTrxSizeFlg = true;
        } else {
            trxArr = mwRcvryTrxRepository.findByRcvryTypSeqAndPymtRefAndCrntRecFlg(exesstyp.getTypSeq(), clntSeq, true);
            rcvryTrxSizeFlg = trxArr != null && trxArr.size() > 0 ? true : false;
        }

        if (rcvryTrxSizeFlg) {
            trxArr.forEach(trx -> {
                if (trx != null) {

                    List<MwRcvryDtl> funRecv = mwRcvryRepository.findAllByRcvryTrxSeqAndCrntRecFlg(trx.getRcvryTrxSeq(), true);

                    List<Long> ids = new ArrayList();

                    if (trx != null) {
                        funRecv.forEach(r -> {
                            r.setCrntRecFlg(false);
                            r.setLastUpdBy(currUser);
                            r.setDelFlg(true);
                            r.setLastUpdDt(currIns);
                            ids.add(r.getPaySchedDtlSeq());

                        });

                        trx.setDelFlg(true);
                        trx.setLastUpdBy(currUser);
                        trx.setLastUpdDt(currIns);
                        trx.setCrntRecFlg(false);

                        MwRefCdVal dueSts = mwRefCdValRepository.findRefCdByGrpAndVal("0179", "0945"); //DUE

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(new Date());
                        Clients clnt = clientRepository.findOneByClntSeqAndCrntRecFlg(trx.getPymtRef(), true);
                        String desc = new String("Reversal of Disability Receivable dated ").concat(dateString).concat(" ")
                                .concat(clnt.getFrstNm()).concat(" ").concat((clnt.getLastNm() == null ? "" : clnt.getLastNm()));
                        vchrPostingService.reverseVchrPosting(trx.getRcvryTrxSeq(), RECOVERY, currUser, desc);

                        List<PaymentScheduleDetail> paymentScheduleDetails = paymentScheduleDetailRepository
                                .findOneByPaySchedDtlSeqInAndCrntRecFlg(ids, true);
                        paymentScheduleDetails.forEach(d -> {
                            d.setPymtStsKey(dueSts.getRefCdSeq());
                        });
                        paymentScheduleDetailRepository.save(paymentScheduleDetails);
                        mwRcvryRepository.save(funRecv);
                        mwRcvryTrxRepository.save(trx);
                    }
                }
            });
        }
    }

    public MwRcvryTrx getOneRecoveryForDisability(Long clntSeq) {
        return mwRcvryTrxRepository.findTop1ByPymtRefAndRcvryTypSeq(clntSeq, "0001");
    }

    public MwRcvryTrx getOneReversedRecoveryForDisability(Long clntSeq) {
        return mwRcvryTrxRepository.findTop1ByPymtRefAndRcvryTypSeq(clntSeq, "0020");
    }
    //Ended by Areeba
}
