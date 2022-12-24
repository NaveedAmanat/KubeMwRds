package com.idev4.rds.repository;

import com.idev4.rds.domain.MwLoanAppChrgStngs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MwLoanAppChrgStngsRepository extends JpaRepository<MwLoanAppChrgStngs, Long> {

    @Query(value = "select chi.CLNT_HLTH_INSR_SEQ prd_chrg_seq,to_number(inst.num_of_inst_sgrt),'' sgrt_inst,0 rndng_flg,1 chrg_calc_typ_key,0 START_LMT,null END_LMT,\r\n"
            + "            case when mnth_flg=0 then hip.ANL_PREM_AMT else hip.ANL_PREM_AMT*to_number(inst.num_of_inst_sgrt) end chrg_val,\r\n"
            + "            0 adjust_rounding_flg,hip.PLAN_NM typ_str, -2 typ_seq,hip.gl_acct_num gl_acct_num,'-2' typ_id,hip.DFRD_ACCT_NUM \r\n"
            + "            from MW_CLNT_HLTH_INSR chi  \r\n"
            + "            join MW_HLTH_INSR_PLAN hip on hip.HLTH_INSR_PLAN_SEQ = chi.HLTH_INSR_PLAN_SEQ and hip.CRNT_REC_FLG=1\r\n"
            + "            join  mw_loan_app_chrg_stngs stng on stng.loan_app_seq=chi.loan_app_seq\r\n"
            + "            join (select distinct prd_seq,vl.ref_cd_dscr num_of_inst_sgrt\r\n"
            + "                    from mw_prd_loan_trm t\r\n"
            + "                    join mw_ref_cd_val vl on vl.ref_cd_seq=t.trm_key and vl.crnt_rec_flg=1\r\n"
            + "                    where t.crnt_rec_flg=1\r\n" + "                 ) inst on inst.prd_seq=stng.prd_seq\r\n"
            + "            where chi.LOAN_APP_SEQ=:loanAppSeq \r\n" + "            and chi.CRNT_REC_FLG=1 AND HIP.PLAN_ID!='1223' \r\n"
            + "            union  \r\n"
            + "            select lacs.prd_chrg_seq,num_of_inst_sgrt,sgrt_inst,rndng_flg,chrg_calc_typ_key,slb.START_LMT,slb.END_LMT,slb.VAL chrg_val,adjust_rounding_flg,t.typ_str,t.typ_SEQ,t.gl_acct_num gl_acct_num,t.TYP_ID,t.DFRD_ACCT_NUM\r\n"
            + "            from mw_loan_app_chrg_stngs lacs  \r\n"
            + "            join mw_typs t on lacs.typ_seq = t.typ_seq and t.del_flg=0 and t.crnt_rec_flg=1  \r\n"
            + "            join mw_prd_chrg pc on lacs.prd_seq=pc.prd_seq and lacs.typ_seq=pc.chrg_typ_seq and nvl(pc.upfront_flg,0)!=1 and pc.crnt_rec_flg=1  \r\n"
            + "            left outer join mw_prd_chrg_slb slb on slb.PRD_CHRG_SEQ = pc.PRD_CHRG_SEQ and slb.CRNT_REC_FLG = 1  \r\n"
            + "            where lacs.prd_seq=:prdSeq and lacs.loan_app_seq=:loanAppSeq", nativeQuery = true)
    List<Object[]> getLoanAppChrgStngsByRddSeqAndLoanAppSeq(@Param("prdSeq") long prdSeq, @Param("loanAppSeq") long loanAppSeq);

    @Query(value = "select st.* from mw_loan_app_chrg_stngs st\r\n"
            + "                                join mw_typs typ on typ.typ_seq = st.typ_seq and typ.crnt_rec_flg=1 and typ.typ_id='0017'\r\n"
            + "                                where st.loan_app_seq=:loanAppSeq", nativeQuery = true)
    MwLoanAppChrgStngs getSrvChrgStng(@Param("loanAppSeq") long loanAppSeq);

}
