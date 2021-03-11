package com.banksalad.collectmydata.invest.service;

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
import com.banksalad.collectmydata.invest.collect.Apis;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.dto.AccountSummary;
import com.banksalad.collectmydata.invest.common.dto.AccountSummaryRequest;
import com.banksalad.collectmydata.invest.common.dto.ListAccountSummariesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final UserSyncStatusService userSyncStatusService;
  private final CollectExecutor collectExecutor;
  private final AccountSummaryRepository accountSummaryRepository;
  private final ExecutionResponseValidateService executionResponseValidateService;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public List<AccountSummary> listAccountSummaries(ExecutionContext executionContext, Organization organization) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    long searchTimestamp = userSyncStatusService
        .getSearchTimestamp(banksaladUserId, organizationId, Apis.finance_invest_accounts);

    ListAccountSummariesResponse listAccountSummariesResponse = listAccountSummariesResponse(executionContext,
        organization.getOrganizationCode(), searchTimestamp);

    saveAccountSummaries(executionContext, listAccountSummariesResponse);

    userSyncStatusService.updateUserSyncStatus(banksaladUserId, organizationId, Apis.finance_invest_accounts.getId(),
        executionContext.getSyncStartedAt(), listAccountSummariesResponse.getSearchTimestamp(),
        executionResponseValidateService.isAllResponseResultSuccess(executionContext, false));

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsent(banksaladUserId, organizationId, true);

    List<AccountSummary> accountSummaries = accountSummaryEntities.stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    return accountSummaries;
  }

  private ListAccountSummariesResponse listAccountSummariesResponse(ExecutionContext executionContext, String orgCode, long searchTimeStamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, executionContext.getAccessToken());
    AccountSummaryRequest accountSummaryRequest = AccountSummaryRequest.builder()
        .searchTimestamp(searchTimeStamp)
        .orgCode(orgCode)
        .build();

    ExecutionRequest<AccountSummaryRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, accountSummaryRequest);

    ExecutionResponse<ListAccountSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.finance_invest_accounts, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution status code is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }

  private void saveAccountSummaries(ExecutionContext executionContext,
      ListAccountSummariesResponse listAccountSummariesResponse) {

    for (AccountSummary accountSummary : listAccountSummariesResponse.getAccountSummaries()) {
      AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), accountSummary.getAccountNum())
          .orElse(AccountSummaryEntity.builder().build());

      accountSummaryMapper.merge(accountSummary, accountSummaryEntity);
      accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
      accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());

      accountSummaryRepository.save(accountSummaryEntity);
    }
  }
}
