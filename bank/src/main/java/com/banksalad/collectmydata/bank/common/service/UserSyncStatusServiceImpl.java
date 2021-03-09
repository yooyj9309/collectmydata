package com.banksalad.collectmydata.bank.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.UserSyncStatus;
import com.banksalad.collectmydata.common.util.DateUtil;
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
  public UserSyncStatus getUserSyncStatus(long banksaladUserId, String organizationId, String apiId) {
    // TODO jayden-lee db 조회로 변경 예정
    return UserSyncStatus.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .apiId(apiId)
        .searchTimestamp(0L)
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  @Override
  public Mono<List<UserSyncStatus>> getUserSyncStatus(long banksaladUserId) {

    return Mono.just(
        List.of(UserSyncStatus.builder()
            .banksaladUserId(banksaladUserId)
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
