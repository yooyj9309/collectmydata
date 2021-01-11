package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.UserSyncStatus;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  @Override
  public void updateUserSyncStatus(Long banksaladUserId, String organizationId, String transactionId,
      LocalDateTime lastSyncedAt, boolean isAllResponseResultOk) {

  }

  @Override
  public Mono<UserSyncStatus> getUserSyncStatus(long banksaladUserId, String organizationId) {

    return Mono.just(
        UserSyncStatus.builder()
            .banksaladUserId(banksaladUserId)
            .organizationCompanyType("bank")
            .organizationId(organizationId)
            .build());
  }

  @Override
  public Mono<List<UserSyncStatus>> getUserSyncStatus(long banksaladUserId) {

    return Mono.just(
        List.of(UserSyncStatus.builder()
            .banksaladUserId(banksaladUserId)
            .organizationCompanyType("bank")
            .organizationId("organizationId")
            .build()));
  }

  @Override
  public void disconnectUserSyncStatus(long banksaladUserId) {

  }

  @Override
  public void disconnectUserSyncStatus(long banksaladUserId, String organizationId) {

  }
}
