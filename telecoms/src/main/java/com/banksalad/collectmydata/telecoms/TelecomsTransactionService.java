package com.banksalad.collectmydata.telecoms;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.dto.TelecomsSummary;
import com.banksalad.collectmydata.telecoms.dto.TelecomsBill;
import com.banksalad.collectmydata.telecoms.dto.TelecomsPaidTransaction;
import com.banksalad.collectmydata.telecoms.dto.TelecomsTransaction;

import java.util.List;

public interface TelecomsTransactionService {
  List<TelecomsTransaction> listTelecomsTransactions(ExecutionContext executionContext, String organizationCode, List<TelecomsSummary> telecomsSummaries);

  List<TelecomsPaidTransaction> listTelecomsPaidTransactions(ExecutionContext executionContext, String organizationCode, List<TelecomsSummary> telecomsSummaries);

}
