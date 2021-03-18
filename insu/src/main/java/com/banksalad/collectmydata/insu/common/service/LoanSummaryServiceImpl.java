package com.banksalad.collectmydata.insu.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.collect.Apis;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanSummaryServiceImpl implements LoanSummaryService {

  private final UserSyncStatusService userSyncStatusService;
  private final CollectExecutor collectExecutor;
  private final LoanSummaryRepository loanSummaryRepository;
  private static final String AUTHORIZATION = "Authorization";

  private static final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);

  @Override
  public List<LoanSummary> listLoanSummaries(ExecutionContext executionContext, String organizationCode) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    long searchTimestamp = userSyncStatusService.getSearchTimestamp(
        banksaladUserId,
        organizationCode,
        Apis.insurance_get_loan_summaries
    );

    ListLoanSummariesResponse loanSummariesResponse = listLoanSummariesResponse(executionContext,
        organizationCode, searchTimestamp);

    // db 적재
    for (LoanSummary loanSummary : loanSummariesResponse.getLoanList()) {
      LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
              banksaladUserId, organizationId, loanSummary.getAccountNum()
          ).orElse(LoanSummaryEntity.builder().build());

      // merge
      loanSummaryMapper.merge(loanSummary, loanSummaryEntity);

      // save (insert, update)
      loanSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      loanSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
      loanSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
      loanSummaryRepository.save(loanSummaryEntity);
    }

//    userSyncStatusService
//        .updateUserSyncStatus(
//            banksaladUserId,
//            organizationId,
//            Apis.insurance_get_loan_summaries.getId(),
//            executionContext.getSyncStartedAt(),
//            loanSummariesResponse.getSearchTimestamp(),
//            executionResponseValidateService.isAllResponseResultSuccess(executionContext, false)
//        );

    List<LoanSummaryEntity> loanSummaryEntities = loanSummaryRepository
        .findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId);

    List<LoanSummary> responseLoanSummaries = loanSummaryEntities.stream()
        .map(loanSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    return responseLoanSummaries;
  }

  @Override
  public void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setBasicSearchTimestamp(searchTimestamp);
    entity.setBasicResponseCode(rspCode);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateDetailSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setDetailSearchTimestamp(searchTimestamp);
    entity.setDetailResponseCode(rspCode);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setTransactionSyncedAt(transactionSyncedAt);
    loanSummaryRepository.save(entity);
  }

  private LoanSummaryEntity getLoanSummaryEntity(long banksaladUserId, String organizationId, String accountNum) {
    return loanSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(
        banksaladUserId,
        organizationId,
        accountNum
    ).orElseThrow(() -> new CollectRuntimeException("No data LoanSummaryEntity"));
  }

  private ListLoanSummariesResponse listLoanSummariesResponse(ExecutionContext executionContext,
      String organizationCode, long searchTimestamp) {

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListLoanSummariesRequest request = ListLoanSummariesRequest.builder()
        .orgCode(organizationCode)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<ListLoanSummariesRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<ListLoanSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_summaries, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
