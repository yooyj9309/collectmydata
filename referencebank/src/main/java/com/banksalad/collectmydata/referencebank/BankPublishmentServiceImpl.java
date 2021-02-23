package com.banksalad.collectmydata.referencebank;

import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;

import org.springframework.stereotype.Service;

@Service
public class BankPublishmentServiceImpl implements BankPublishmentService {

  @Override
  public SyncFinanceBankResponse requestPublishment(long banksaladUserId, String organizationId,
      BankApiResponse bankApiResponse) {

    // TODO : implement
    return SyncFinanceBankResponse.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }
}
