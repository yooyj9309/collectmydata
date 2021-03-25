package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.common.dto.SyncFinanceTelecomResponse;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;

public interface TelecomPublishmentService {

  SyncFinanceTelecomResponse requestPublishment(long banksaladUserId, String organizationId,
      TelecomApiResponse telecomApiResponse);
}
