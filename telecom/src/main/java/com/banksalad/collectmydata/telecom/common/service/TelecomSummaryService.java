package com.banksalad.collectmydata.telecom.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.telecom.common.dto.TelecomSummary;

import java.util.List;

public interface TelecomSummaryService {

  List<TelecomSummary> listTelecomSummaries(ExecutionContext executionContext, String organizationCode);
}
