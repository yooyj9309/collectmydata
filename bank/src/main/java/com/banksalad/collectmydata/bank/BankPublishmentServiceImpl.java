package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;

@Service
public class BankPublishmentServiceImpl implements BankPublishmentService {

  @Override
  public SyncFinanceBankResponse requestPublishment(long banksaladUserId, String organizationId,
      BankApiResponse bankApiResponse) {

    // TODO jayden-lee bankApiResponse를 idl에 정의한 proto message로 convert 하는 로직 추가

    return SyncFinanceBankResponse.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }
}
