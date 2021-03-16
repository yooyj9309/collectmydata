package com.banksalad.collectmydata.invest.account;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicResponse;
import com.banksalad.collectmydata.invest.collect.Apis;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountBasicHistoryMapper;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountBasicMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.dto.AccountSummary;
import com.banksalad.collectmydata.invest.service.AccountSummaryService;
import com.banksalad.collectmydata.invest.service.ExecutionResponseValidateService;
import com.banksalad.collectmydata.invest.service.UserSyncStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final CollectExecutor collectExecutor;
  private final AccountSummaryService accountSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExecutionResponseValidateService executionResponseValidateService;
  private final AccountBasicRepository accountBasicRepository;
  private final AccountBasicHistoryRepository accountBasicHistoryRepository;

  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);
  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers.getMapper(AccountBasicHistoryMapper.class);

  @Override
  public List<AccountBasic> listAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {

    boolean isExceptionOccurred = false;
    for (AccountSummary accountSummary : accountSummaries) {
      GetAccountBasicResponse accountBasicResponse = getAccountBasicResponse(executionContext,
          organization.getOrganizationCode(), accountSummary.getAccountNum(),
          accountSummary.getBasicSearchTimestamp());

      AccountBasic accountBasic = accountBasicResponse.getAccountBasic();

      try {
        saveAccountBasic(executionContext, accountSummary, accountBasic);

        accountSummaryService
            .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
                accountSummary.getAccountNum(), accountBasic.getSearchTimestamp());

      } catch (Exception e) {
        isExceptionOccurred = true;
        log.error("Failed to save account basic", e);
      }
    }

    userSyncStatusService
        .updateUserSyncStatus(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            Apis.finance_invest_account_basic.getId(), executionContext.getSyncStartedAt(), null,
            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred));

    List<AccountBasicEntity> accountBasicEntities = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    return accountBasicEntities.stream()
        .map(accountBasicMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private GetAccountBasicResponse getAccountBasicResponse(ExecutionContext executionContext,
      String orgCode, String accountNum, long searchTimestamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, executionContext.getAccessToken());

    GetAccountBasicRequest getAccountBasicRequest = GetAccountBasicRequest.builder()
        .orgCode(orgCode)
        .accountNum(accountNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetAccountBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, getAccountBasicRequest);

    ExecutionResponse<GetAccountBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.finance_invest_account_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution status code is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }

  private void saveAccountBasic(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountBasic accountBasic) {

    AccountBasicEntity accountBasicEntity = accountBasicMapper.dtoToEntity(accountBasic);
    accountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    accountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountBasicEntity.setAccountNum(accountSummary.getAccountNum());

    AccountBasicEntity existingAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum())
        .orElse(AccountBasicEntity.builder().build());

    if (existingAccountBasicEntity.getId() != null) {
      accountBasicEntity.setId(existingAccountBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(accountBasicEntity, existingAccountBasicEntity, "syncedAt")) {
      accountBasicRepository.save(accountBasicEntity);
      accountBasicHistoryRepository.save(accountBasicHistoryMapper.toHistoryEntity(accountBasicEntity));
    }
  }
}
