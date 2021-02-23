package com.banksalad.collectmydata.referencebank;

import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;

public interface BankPublishmentService {

  SyncFinanceBankResponse requestPublishment(long banksaladUserId, String organizationId, BankApiResponse bankApiResponse);
}
