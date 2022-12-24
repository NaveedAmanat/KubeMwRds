package com.idev4.rds.service;

import com.idev4.rds.domain.LoanApplication;
import com.idev4.rds.domain.MwRcvryDtl;
import com.idev4.rds.domain.MwRcvryTrx;
import com.idev4.rds.domain.PaymentScheduleDetail;
import com.idev4.rds.repository.LoanApplicationRepository;
import com.idev4.rds.repository.MwRcvryDtlRepository;
import com.idev4.rds.repository.MwRcvryTrxRepository;
import com.idev4.rds.repository.PaymentScheduleDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class RecoveryComponent {

    @Autowired
    MwRcvryDtlRepository mwRcvryRepository;

    @Autowired
    MwRcvryTrxRepository mwRcvryTrxRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    PaymentScheduleDetailRepository paymentScheduleDetailRepository;

    @Transactional
    public Boolean saveRecoveryPayment(MwRcvryTrx mwRcvryTrx, List<MwRcvryDtl> rcvryToSave,
                                       List<PaymentScheduleDetail> isntallments) {
        mwRcvryTrxRepository.save(mwRcvryTrx);
        mwRcvryRepository.save(rcvryToSave);
        paymentScheduleDetailRepository.save(isntallments);
        return true;
    }

    @Transactional
    public Boolean saveRecoveryPayment(MwRcvryTrx mwRcvryTrx, List<MwRcvryDtl> rcvryToSave, List<LoanApplication> app,
                                       List<PaymentScheduleDetail> isntallments) {
        mwRcvryTrxRepository.save(mwRcvryTrx);
        mwRcvryRepository.save(rcvryToSave);
        loanApplicationRepository.save(app);
        paymentScheduleDetailRepository.save(isntallments);
        return true;
    }

    @Transactional
    public Boolean saveRecoveryPayment(MwRcvryTrx mwRcvryTrx, MwRcvryDtl rcvryToSave) {
        mwRcvryTrxRepository.save(mwRcvryTrx);
        mwRcvryRepository.save(rcvryToSave);
        return true;
    }

    @Transactional
    public Boolean reverseRecoveryPayment(MwRcvryTrx mwRcvryTrx, List<MwRcvryDtl> rcvryToSave) {
        rcvryToSave.forEach(r -> {
            r.setEffEndDt(Instant.now());
            r.setLastUpdBy(SecurityContextHolder.getContext().getAuthentication().getName());
            r.setLastUpdDt(Instant.now());
            r.setDelFlg(true);
            r.setCrntRecFlg(false);
        });
        mwRcvryRepository.save(rcvryToSave);
        mwRcvryTrx.setEffEndDt(Instant.now());
        mwRcvryTrx.setLastUpdBy(SecurityContextHolder.getContext().getAuthentication().getName());
        mwRcvryTrx.setLastUpdDt(Instant.now());
        mwRcvryTrx.setDelFlg(true);
        mwRcvryTrx.setCrntRecFlg(false);
        mwRcvryTrxRepository.save(mwRcvryTrx);
        return true;
    }
}
