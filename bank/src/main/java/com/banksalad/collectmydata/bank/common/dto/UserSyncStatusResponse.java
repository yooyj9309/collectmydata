package com.banksalad.collectmydata.bank.common.dto;

import com.github.banksalad.idl.daas.v1.collect.bank.BankProto;
import com.google.protobuf.Int64Value;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class UserSyncStatusResponse {

  private final List<UserSyncStatus> userSyncStatuses;

  public BankProto.GetSyncStatusResponse toSyncStatusResponseProto() {
    List<BankProto.LegacyUserAPISyncStatus> syncStatusResponse = userSyncStatuses.stream()
        .map(userSyncStatus ->
            BankProto.LegacyUserAPISyncStatus
                .newBuilder()
                .setUserId((int) userSyncStatus.getBanksaladUserId())
                .setCompanyId(userSyncStatus.getOrganizationId())   // TODO : convert to organizationObjectId
                .setCompanyType(userSyncStatus.getOrganizationCompanyType())
                .setSyncedAt(Int64Value.newBuilder().setValue(userSyncStatus.getLastCheckAt()).build())
                .build())
        .collect(Collectors.toList());

    return BankProto.GetSyncStatusResponse
        .newBuilder()
        .addAllData(syncStatusResponse)
        .build();
  }
}
