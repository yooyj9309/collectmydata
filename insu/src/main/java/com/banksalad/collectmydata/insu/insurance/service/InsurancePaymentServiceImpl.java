package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class InsurancePaymentServiceImpl implements InsurancePaymentService {

  @Override
  public List<InsurancePayment> listInsurancePayments(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }
}
