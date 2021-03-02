package com.banksalad.collectmydata.insu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class InsuranceSummaryServiceImpl implements InsuranceSummaryService {

  @Override
  public List<InsuranceSummary> listInsuranceSummaries(ExecutionContext executionContext, String organizationCode) {
    return null;
  }
}
