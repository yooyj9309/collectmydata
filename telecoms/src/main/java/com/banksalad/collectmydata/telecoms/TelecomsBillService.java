package com.banksalad.collectmydata.telecoms;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.dto.TelecomsSummary;
import com.banksalad.collectmydata.telecoms.dto.TelecomsBill;

import java.util.List;

public interface TelecomsBillService {
  List<TelecomsBill> listTelecomsBills(ExecutionContext executionContext, String organizationCode);
}
