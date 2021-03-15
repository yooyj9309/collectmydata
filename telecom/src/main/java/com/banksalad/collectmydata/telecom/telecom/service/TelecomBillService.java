package com.banksalad.collectmydata.telecom.telecom.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;

import java.util.List;

public interface TelecomBillService {

  List<TelecomBill> listTelecomBills(ExecutionContext executionContext, String organizationCode);
}
