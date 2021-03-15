package com.banksalad.collectmydata.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.dto.TelecomsSummary;

import java.util.List;

public interface TelecomsSummaryService {
  List<TelecomsSummary> listTelecomsSummaries(ExecutionContext executionContext, String organizationCode);
}
