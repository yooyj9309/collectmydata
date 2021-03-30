package com.banksalad.collectmydata.insu;

import com.banksalad.collectmydata.common.dto.SyncFinanceInsuResponse;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;

public interface InsuPublishmentService {

  SyncFinanceInsuResponse requestPublishment(long banksaladUserId, String organizationId,
      InsuApiResponse insuApiResponse);
}
