package com.banksalad.collectmydata.capital;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.common.dto.SyncFinanceCapitalResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CapitalPublishmentServiceImpl implements CapitalPublishmentService {

  @Override
  public SyncFinanceCapitalResponse requestPublishment(long banksaladUserId, String organizationId,
      CapitalApiResponse capitalApiResponse) {
    // TODO
    return null;
  }
}
