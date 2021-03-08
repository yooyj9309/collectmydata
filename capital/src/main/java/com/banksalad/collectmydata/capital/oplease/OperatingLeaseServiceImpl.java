package com.banksalad.collectmydata.capital.oplease;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Apis;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.OperatingLeaseHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.OperatingLeaseMapper;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.common.service.ExecutionResponseValidateService;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatingLeaseServiceImpl implements OperatingLeaseService {

  private final AccountSummaryService accountSummaryService;
  private final ExternalApiService externalApiService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExecutionResponseValidateService executionResponseValidateService;
  private final OperatingLeaseRepository operatingLeaseRepository;
  private final OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final OperatingLeaseMapper operatingLeaseMapper = Mappers.getMapper(OperatingLeaseMapper.class);
  private final OperatingLeaseHistoryMapper operatingLeaseHistoryMapper = Mappers
      .getMapper(OperatingLeaseHistoryMapper.class);


  private static final String[] LEASE_RES_EXCLUDE_EQUALS_FIELD = {"rspCode", "rspMsg", "searchTimestamp"};

  @Override
  public List<OperatingLease> listOperatingLeases(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);

    List<OperatingLease> operatingLeases = accountSummaries.stream()
        .map(account -> CompletableFuture
            .supplyAsync(
                () -> operatingLeaseProcess(executionContext, organization, account, banksaladUserId, organizationId),
                threadPoolTaskExecutor
            ).exceptionally(e -> {
              log.error("6.7.5 listOperatingLeases exception {}", e.getMessage());
              isExceptionOccurred.set(true);
              return null;
            }))
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    // userSyncStatus table update
    userSyncStatusService
        .updateUserSyncStatus(
            banksaladUserId,
            organizationId,
            Apis.capital_get_operating_lease_basic.getId(),
            executionContext.getSyncStartedAt(),
            null,
            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
        );

    return operatingLeases;
  }

  public OperatingLease operatingLeaseProcess(ExecutionContext context, Organization organization,
      AccountSummary accountSummary,
      long banksaladUserId, String organizationId) {
    OperatingLeaseBasicResponse response = externalApiService
        .getOperatingLeaseBasic(context, organization, accountSummary);

    OperatingLeaseEntity entity = operatingLeaseRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId,
            organizationId,
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(OperatingLeaseEntity.builder().build());

    OperatingLeaseBasicResponse entityDto = operatingLeaseMapper
        .entityToOperatingLeaseBasicResponse(entity);

    if (!ObjectComparator.isSame(entityDto, response, LEASE_RES_EXCLUDE_EQUALS_FIELD)) {
      // merge
      operatingLeaseMapper.merge(context, accountSummary, response, entity);

      // make history
      OperatingLeaseHistoryEntity historyEntity = operatingLeaseHistoryMapper
          .toOperatingLeaseHistoryEntity(entity);

      // 운용리스 및 history save;
      operatingLeaseRepository.save(entity);
      operatingLeaseHistoryRepository.save(historyEntity);
    }

    // accountList timestamp update
    accountSummary.setOperatingLeaseBasicSearchTimestamp(response.getSearchTimestamp());
    accountSummaryService.updateSearchTimestamp(banksaladUserId, organizationId, accountSummary);

    return operatingLeaseMapper.operatingLeaseAssembler(response, accountSummary);
  }

  @Override
  public List<OperatingLeaseTransaction> listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {

    return null;
  }
}
