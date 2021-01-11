package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.UserSyncStatus;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSyncStatusService {

  void updateUserSyncStatus(Long banksaladUserId, String organizationId, String transactionId,
      LocalDateTime lastSyncedAt, boolean isAllResponseResultOk);

  Mono<UserSyncStatus> getUserSyncStatus(long banksaladUserId, String organizationId);

  Mono<List<UserSyncStatus>> getUserSyncStatus(long banksaladUserId);

  void disconnectUserSyncStatus(long banksaladUserId);

  void disconnectUserSyncStatus(long banksaladUserId, String organizationId);
}
