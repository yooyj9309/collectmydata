package com.banksalad.collectmydata.insu;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.dto.SyncFinanceInsuResponse;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InsuPublishmentServiceImpl implements InsuPublishmentService {

  @Override
  public SyncFinanceInsuResponse requestPublishment(long banksaladUserId, String organizationId,
      InsuApiResponse insuApiResponse) {

    // TODO : implement
    return SyncFinanceInsuResponse.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }
}
