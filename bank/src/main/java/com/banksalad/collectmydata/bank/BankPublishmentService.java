package com.banksalad.collectmydata.bank;

import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;

public interface BankPublishmentService {

  SyncFinanceBankResponse requestPublishment(long banksaladUserId, String organizationId,
      BankApiResponse bankApiResponse);
}
