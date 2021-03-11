package com.banksalad.collectmydata.insu.loan.service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.mapper.LoanDetailHistoryMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.LoanDetailMapper;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.common.service.ExecutionResponseValidateService;
import com.banksalad.collectmydata.insu.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private static final String AUTHORIZATION = "Authorization";
  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;
  private final ExecutionResponseValidateService executionResponseValidateService;
  private final LoanSummaryRepository loanSummaryRepository;
  private final LoanDetailRepository loanDetailRepository;
  private final LoanDetailHistoryRepository loanDetailHistoryRepository;
  private final LoanDetailMapper loanDetailMapper = Mappers.getMapper(LoanDetailMapper.class);
  private final LoanDetailHistoryMapper loanDetailHistoryMapper = Mappers.getMapper(LoanDetailHistoryMapper.class);

  @Override
  public List<LoanBasic> listLoanBasics(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  @Override
  public List<LoanDetail> listLoanDetails(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    // FIXME: reference 나오면 수정
    final ExecutionContext context = executionContext.withExecutionRequestId(UUID.randomUUID().toString());
    final Execution execution = Executions.insurance_get_loan_detail;
    List<LoanDetail> loanDetails = new ArrayList<>(loanSummaries.size());
    AtomicBoolean exceptionOccurred = new AtomicBoolean(false);

    // Non-parallel version:
    for (LoanSummary loanSummary : loanSummaries) {
      try {
        loanDetails.add(processLoanDetail(execution, context, organizationCode, loanSummary));
      } catch (CollectmydataRuntimeException e) {
        log.error(e.getMessage(), e);
        exceptionOccurred.set(true);
      }
    }

    userSyncStatusService.updateUserSyncStatus(
        context.getBanksaladUserId(),
        context.getOrganizationId(),
        execution.getApi().getId(),
        context.getSyncStartedAt(),
        null,
        executionResponseValidateService.isAllResponseResultSuccess(context, exceptionOccurred.get())
    );
    return loanDetails;
  }

  private GetLoanBasicResponse getLoanBasicResponse(ExecutionContext executionContext, String organizationCode,
      String accountNum, long searchTimestamp) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetLoanBasicRequest request = GetLoanBasicRequest.builder()
        .orgCode(organizationCode)
        .accountNum(accountNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetLoanBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetLoanBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }

  @Transactional
  protected LoanDetail processLoanDetail(Execution execution, ExecutionContext executionContext,
      String organizationCode, LoanSummary loanSummary) {
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String accountNum = loanSummary.getAccountNum();
    try {
      LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId, accountNum)
          .orElseThrow(NoResultException::new);
      LoanDetailEntity loanDetailEntity = loanDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId, accountNum)
          .orElse(LoanDetailEntity.builder().build());
      // Call a Mydata API
      GetLoanDetailResponse getLoanDetailResponse = getLoanDetailResponse(
          execution, executionContext, organizationCode, accountNum, loanSummaryEntity.getDetailSearchTimestamp());
      LoanDetail loanDetail = getLoanDetailResponse.getLoanDetail();
      // Compare the API result with the DB record
      // If both are equal or the API result is new
      if (!ObjectComparator.isSame(loanDetail, loanDetailMapper.toDto(loanDetailEntity))) {
        // Update the loan_detail record
        loanDetailEntity = loanDetailMapper.toEntity(loanDetail);
        loanDetailEntity.setSyncedAt(syncedAt);
        loanDetailEntity.setBanksaladUserId(banksaladUserId);
        loanDetailEntity.setOrganizationId(organizationId);
        loanDetailRepository.save(loanDetailEntity);
        loanDetailHistoryRepository.save(loanDetailHistoryMapper.toEntity(loanDetailEntity));
        // Update the loan_summary record
        loanSummaryEntity.setDetailSearchTimestamp(getLoanDetailResponse.getSearchTimestamp());
        loanSummaryRepository.save(loanSummaryEntity);
      }
      return loanDetail;
    } catch (NoResultException e) {
      throw new CollectmydataRuntimeException(String.format(
          "No record in loan_summary: banksaladUserId=%s, organizationId=%s, accountNum=%s",
          banksaladUserId, organizationId, accountNum), e);
    } catch (DataAccessException e) {
      throw new CollectmydataRuntimeException("JPA operation failed", e);
    } catch (CollectRuntimeException e) {
      throw new CollectmydataRuntimeException(e.getMessage(), e);
    } catch (NullPointerException e) {
      throw new CollectmydataRuntimeException("Mydata API Response is null", e);
    } catch (RuntimeException e) {
      throw new CollectmydataRuntimeException("Unknown runtime exception", e);
    }
  }

  private GetLoanDetailResponse getLoanDetailResponse(Execution execution, ExecutionContext executionContext,
      String organizationCode, String accountNum, long searchTimestamp) {
    // Make a GetLoanDetailRequest.
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetLoanDetailRequest request = GetLoanDetailRequest.builder()
        .orgCode(organizationCode)
        .accountNum(accountNum)
        .searchTimestamp(searchTimestamp)
        .build();
    ExecutionRequest<GetLoanDetailRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(headers, request);

    // Post a Mydata API and get the response.
    ExecutionResponse<GetLoanDetailResponse> executionResponse = collectExecutor
        .execute(executionContext, execution, executionRequest);
    GetLoanDetailResponse getLoanDetailResponse = executionResponse.getResponse();
    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      // FIXME: Used a temporal exception message format
      throw new CollectRuntimeException(String.format(
          "Mydata API %s was not succeeded: rspCode=%s, rspMsg=%s",
          execution.getApi().getId(), getLoanDetailResponse.getRspCode(), getLoanDetailResponse.getRspMsg()));
    }
    // Set account_number for the next logics to process conveniently.
    getLoanDetailResponse.getLoanDetail().setAccountNum(accountNum);
    return getLoanDetailResponse;
  }
}
