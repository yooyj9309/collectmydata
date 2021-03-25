package com.banksalad.collectmydata.telecom;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.dto.SyncFinanceTelecomResponse;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TelecomPublishmentServiceImpl implements TelecomPublishmentService {

  @Override
  public SyncFinanceTelecomResponse requestPublishment(long banksaladUserId, String organizationId,
      TelecomApiResponse telecomApiResponse) {

    // TODO : implement
    return SyncFinanceTelecomResponse.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }
}
