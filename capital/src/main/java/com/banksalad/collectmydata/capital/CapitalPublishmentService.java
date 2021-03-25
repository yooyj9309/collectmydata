package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.common.dto.SyncFinanceCapitalResponse;

public interface CapitalPublishmentService {

  SyncFinanceCapitalResponse requestPublishment(long banksaladUserId, String organizationId,
      CapitalApiResponse capitalApiResponse);
}
