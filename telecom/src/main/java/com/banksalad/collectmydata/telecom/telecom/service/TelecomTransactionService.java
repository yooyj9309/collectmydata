package com.banksalad.collectmydata.telecom.telecom.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.telecom.common.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;

import java.util.List;

public interface TelecomTransactionService {

  List<TelecomTransaction> listTelecomTransactions(ExecutionContext executionContext, String organizationCode,
      List<TelecomSummary> telecomsSummaries);

  List<TelecomPaidTransaction> listTelecomPaidTransactions(ExecutionContext executionContext, String organizationCode,
      List<TelecomSummary> telecomsSummaries);

}
