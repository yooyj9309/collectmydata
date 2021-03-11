package com.banksalad.collectmydata.insu.insurance.service;

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
import com.banksalad.collectmydata.insu.collect.Apis;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.mapper.InsurancePaymentHistoryMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsurancePaymentMapper;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.common.service.ExecutionResponseValidateService;
import com.banksalad.collectmydata.insu.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsurancePaymentServiceImpl implements InsurancePaymentService {

  private final InsurancePaymentRepository insurancePaymentRepository;
  private final InsurancePaymentHistoryRepository insurancePaymentHistoryRepository;

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;
  private final ExecutionResponseValidateService executionResponseValidateService;
  private final InsuranceSummaryRepository insuranceSummaryRepository;

  private final InsurancePaymentMapper insurancePaymentMapper = Mappers.getMapper(InsurancePaymentMapper.class);
  private final InsurancePaymentHistoryMapper insurancePaymentHistoryMapper = Mappers
      .getMapper(InsurancePaymentHistoryMapper.class);

  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<InsurancePayment> listInsurancePayments(ExecutionContext executionContext, Organization organization,
      List<InsuranceSummary> insuranceSummaries) {
    List<InsurancePayment> insurancePayments = new ArrayList<>();

    boolean isExceptionOccurred = FALSE;
    for (InsuranceSummary insuranceSummary : insuranceSummaries) {
      try {
        GetInsurancePaymentResponse insurancePaymentResponse = getInsurancePaymentResponse(executionContext,
            organization.getOrganizationCode(), insuranceSummary.getInsuNum(),
            insuranceSummary.getPaymentSearchTimestamp());

        InsurancePaymentEntity insurancePaymentEntity = saveInsurancePaymentWithHistory(executionContext,
            insuranceSummary, insurancePaymentResponse);
        insurancePayments.add(insurancePaymentMapper.toInsurancePaymentFrom(insurancePaymentEntity));
        updateSearchTimestamp(executionContext, insuranceSummary, insurancePaymentResponse);
      } catch (Exception e) {
        isExceptionOccurred = TRUE;
        log.error("Failed to save insurance payment", e);
      }
    }

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.insurance_get_payment.getId(),
        executionContext.getSyncStartedAt(),
        null,
        executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred));

    return insurancePayments;
  }

  private InsurancePaymentEntity saveInsurancePaymentWithHistory(ExecutionContext executionContext,
      InsuranceSummary insuranceSummary, GetInsurancePaymentResponse insurancePaymentResponse) {

    InsurancePaymentEntity insurancePaymentEntity = insurancePaymentMapper
        .toInsurancePaymentEntityFrom(insurancePaymentResponse);
    insurancePaymentEntity.setSyncedAt(executionContext.getSyncStartedAt());
    insurancePaymentEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    insurancePaymentEntity.setOrganizationId(executionContext.getOrganizationId());
    insurancePaymentEntity.setInsuNum(insuranceSummary.getInsuNum());

    InsurancePaymentEntity existingInsurancePaymentEntity = insurancePaymentRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElse(InsurancePaymentEntity.builder().build());

    if (existingInsurancePaymentEntity.getId() != null) {
      insurancePaymentEntity.setId(existingInsurancePaymentEntity.getId());
    }

    if (!ObjectComparator.isSame(insurancePaymentEntity, existingInsurancePaymentEntity,
        "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")) {
      insurancePaymentRepository.save(insurancePaymentEntity);
      insurancePaymentHistoryRepository
          .save(insurancePaymentHistoryMapper.toInsurancePaymentHistoryEntityFrom(insurancePaymentEntity));
    }

    return insurancePaymentEntity;
  }

  private void updateSearchTimestamp(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      GetInsurancePaymentResponse insurancePaymentResponse) {
    InsuranceSummaryEntity insuranceSummaryEntity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            insuranceSummary.getInsuNum())
        .orElseThrow(EntityNotFoundException::new);

    insuranceSummaryEntity.setPaymentSearchTimestamp(insurancePaymentResponse.getSearchTimestamp());
    insuranceSummaryRepository.save(insuranceSummaryEntity);
  }

  private GetInsurancePaymentResponse getInsurancePaymentResponse(ExecutionContext executionContext,
      String organizationCode, String insuNum, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetInsurancePaymentRequest request = GetInsurancePaymentRequest.builder()
        .orgCode(organizationCode)
        .insuNum(insuNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetInsurancePaymentRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetInsurancePaymentResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_payment, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("Execution status is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("Response is null");
    }

    return executionResponse.getResponse();
  }
}
