package com.banksalad.collectmydata.bank.common.dto;

import com.github.banksalad.idl.daas.v1.collect.bank.BankProto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserSyncStatusResponse {

  private final List<UserSyncStatus> userSyncStatuses;

  public BankProto.GetSyncStatusResponse toSyncStatusResponseProto() {
    return BankProto.GetSyncStatusResponse
        .newBuilder()
        .build();
  }
}
