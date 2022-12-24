package com.idev4.rds.repository;

import com.idev4.rds.domain.PaymentScheduleHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface PaymentScheduleHeaderRespository extends JpaRepository<PaymentScheduleHeader, Long> {

    @Query(value = "select psh.pymt_sched_hdr_seq, psh.pymt_sched_id, psh.frst_inst_dt, psh.loan_app_seq,psh.sched_sts_key,psd.pymt_sched_dtl_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due,sum(psc.amt) amt\r\n"
            + "from mw_pymt_sched_hdr psh\r\n"
            + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
            + "where psh.loan_app_seq=? and psh.crnt_rec_flg=1\r\n"
            + "group by psh.pymt_sched_hdr_seq, psh.pymt_sched_id, psh.frst_inst_dt, psh.loan_app_seq,psh.sched_sts_key,psd.pymt_sched_dtl_seq, psd.inst_num, psd.due_dt, psd.ppal_amt_due, psd.tot_chrg_due\r\n"
            + "order by psd.inst_num", nativeQuery = true)
    List<Object[]> getPaymentScheduleHeaderByLoanAppSeq(long paySchedChrgSeq);

    public PaymentScheduleHeader findOneByLoanAppSeqAndCrntRecFlg(long loanAppSeq, boolean flg);

    public Integer countByLoanAppSeq(long loanAppSeq);

    public PaymentScheduleHeader findOneByPaySchedHdrSeqAndCrntRecFlg(long hdrSeq, boolean flg);
}
