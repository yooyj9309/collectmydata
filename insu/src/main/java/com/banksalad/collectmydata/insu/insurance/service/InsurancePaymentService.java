package com.banksalad.collectmydata.insu.insurance.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;

import java.util.List;

public interface InsurancePaymentService {

  List<InsurancePayment> listInsurancePayments(ExecutionContext executionContext, Organization organization,
      List<InsuranceSummary> insuranceSummaries);
}
