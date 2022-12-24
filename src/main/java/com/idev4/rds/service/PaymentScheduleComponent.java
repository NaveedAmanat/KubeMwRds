package com.idev4.rds.service;

import com.idev4.rds.domain.PaymentScheduleChargers;
import com.idev4.rds.domain.PaymentScheduleDetail;
import com.idev4.rds.domain.PaymentScheduleHeader;
import com.idev4.rds.repository.PaymentScheduleChargersRepository;
import com.idev4.rds.repository.PaymentScheduleDetailRepository;
import com.idev4.rds.repository.PaymentScheduleHeaderRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class PaymentScheduleComponent {

    @Autowired
    PaymentScheduleHeaderRespository paymentScheduleHeaderRespository;

    @Autowired
    PaymentScheduleChargersRepository paymentScheduleChargersRepository;

    @Autowired
    PaymentScheduleDetailRepository paymentScheduleDetailRepository;

    @Transactional
    public Boolean savePaymentScheduleService(boolean reGenFlag, PaymentScheduleHeader paymentScheduleHeader,
                                              List<PaymentScheduleDetail> pplInts, List<PaymentScheduleChargers> chargesInsts, List<PaymentScheduleDetail> exPplInts,
                                              List<PaymentScheduleChargers> exChargesInsts) {
        if (reGenFlag) {
            paymentScheduleChargersRepository.deleteInBatch(exChargesInsts);
            paymentScheduleDetailRepository.deleteInBatch(exPplInts);
        }
        paymentScheduleHeaderRespository.save(paymentScheduleHeader);
        paymentScheduleDetailRepository.save(pplInts);
        paymentScheduleChargersRepository.save(chargesInsts);
        return true;
    }
}
