package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.collect.Apis;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuranceBasicHistoryMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuranceBasicMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuranceContractHistoryMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuranceContractMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuredHistoryMapper;
import com.banksalad.collectmydata.insu.common.db.mapper.InsuredMapper;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

  private final CollectExecutor collectExecutor;
  private final InsuranceSummaryService insuranceSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final InsuranceBasicRepository insuranceBasicRepository;
  private final InsuranceBasicHistoryRepository insuranceBasicHistoryRepository;
  private final InsuredRepository insuredRepository;
  private final InsuredHistoryRepository insuredHistoryRepository;
  private final InsuranceContractRepository insuranceContractRepository;
  private final InsuranceContractHistoryRepository insuranceContractHistoryRepository;

  @Qualifier("async-thread")
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final InsuranceBasicMapper insuranceBasicMapper = Mappers.getMapper(InsuranceBasicMapper.class);
  private final InsuranceBasicHistoryMapper insuranceBasicHistoryMapper = Mappers
      .getMapper(InsuranceBasicHistoryMapper.class);
  private final InsuredMapper insuredMapper = Mappers.getMapper(InsuredMapper.class);
  private final InsuredHistoryMapper insuredHistoryMapper = Mappers.getMapper(InsuredHistoryMapper.class);
  private final InsuranceContractMapper insuranceContractMapper = Mappers.getMapper(InsuranceContractMapper.class);
  private final InsuranceContractHistoryMapper insuranceContractHistoryMapper = Mappers
      .getMapper(InsuranceContractHistoryMapper.class);

  private static final String[] CONTRACT_EXCLUDE_FIELD = {"insuredNo", "insuNum"};
  private static final String[] INSURANCE_RES_EXCLUDE_EQUALS_FIELD = {"rspCode", "rspMsg", "searchTimestamp"};

  private static final String AUTHORIZATION = "Authorization";
  private final long DEFAULT_SEARCH_TIMESTAMP = 0L;

  @Override
  public List<InsuranceBasic> listInsuranceBasics(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {

    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);

    List<InsuranceBasic> insuranceBasics = insuranceSummaries.stream()
        .map(insuranceSummary -> CompletableFuture
            .supplyAsync(() ->
                progressInsuranceBasic(executionContext, organizationCode, insuranceSummary), threadPoolTaskExecutor
            ).exceptionally(e -> {
              log.error("6.5.2 insuranceBasic exception {}", e.getMessage());
              isExceptionOccurred.set(true);
              return null;
            })
        ).map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

//    userSyncStatusService
//        .updateUserSyncStatus(
//            executionContext.getBanksaladUserId(),
//            executionContext.getOrganizationId(),
//            Apis.insurance_get_basic.getId(),
//            executionContext.getSyncStartedAt(),
//            null,
//            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
//        );

    return insuranceBasics;
  }

  @Override
  public List<InsuranceContract> listInsuranceContracts(ExecutionContext executionContext, String organizationCode,
      List<InsuranceBasic> insuranceBasics) {

    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);

    List<InsuranceContract> insuranceContracts = insuranceBasics.stream()
        .map(insuranceBasic -> CompletableFuture
            .supplyAsync(() ->
                progressInsuranceContract(executionContext, organizationCode, insuranceBasic), threadPoolTaskExecutor
            ).exceptionally(e -> {
              log.error("6.7.2 insuranceBasic exception {}", e.getMessage());
              isExceptionOccurred.set(true);
              return null;
            })
        ).map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

//    userSyncStatusService
//        .updateUserSyncStatus(
//            executionContext.getBanksaladUserId(),
//            executionContext.getOrganizationId(),
//            Apis.insurance_get_contract.getId(),
//            executionContext.getSyncStartedAt(),
//            null,
//            executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
//        );

    return insuranceContracts;
  }

  private InsuranceBasic progressInsuranceBasic(ExecutionContext executionContext, String orgCode,
      InsuranceSummary insuranceSummary) {

    GetInsuranceBasicResponse insuranceBasicResponse = getInsuranceBasicResponse(executionContext, orgCode,
        insuranceSummary.getInsuNum(), insuranceSummary.getBasicSearchTimestamp());

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String insuNum = insuranceSummary.getInsuNum();

    InsuranceBasicEntity entity = insuranceBasicRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNum(
        banksaladUserId,
        organizationId,
        insuNum
    ).orElse(InsuranceBasicEntity.builder().build());

    GetInsuranceBasicResponse entityDto = insuranceBasicMapper.entityToResponseDto(entity);

    if (!ObjectComparator.isSame(entityDto, insuranceBasicResponse, INSURANCE_RES_EXCLUDE_EQUALS_FIELD)) {
      insuranceBasicMapper.merge(insuranceBasicResponse, entity);

      entity.setSyncedAt(executionContext.getSyncStartedAt());
      entity.setBanksaladUserId(banksaladUserId);
      entity.setOrganizationId(organizationId);
      entity.setInsuNum(insuNum);
      // make history
      InsuranceBasicHistoryEntity historyEntity = insuranceBasicHistoryMapper.toHistoryEntity(entity);

      // 보험기본 및 history save;
      insuranceBasicRepository.save(entity);
      insuranceBasicHistoryRepository.save(historyEntity);

      // 피보험자 목록 비교 및 저장.
      if (insuranceBasicResponse.getInsuredList() != null) {
        for (Insured insured : insuranceBasicResponse.getInsuredList()) {
          InsuredEntity insuredEntity = insuredRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
              banksaladUserId, organizationId, insuNum, insured.getInsuredNo()
          ).orElse(InsuredEntity.builder().build());

          Insured insuredEntityDto = insuredMapper.entityToDto(insuredEntity);
          if (!ObjectComparator.isSame(insuredEntityDto, insured)) {
            insuredMapper.merge(insured, insuredEntity);

            insuredEntity.setSyncedAt(executionContext.getSyncStartedAt());
            insuredEntity.setBanksaladUserId(banksaladUserId);
            insuredEntity.setOrganizationId(organizationId);
            insuredEntity.setInsuNum(insuNum);

            InsuredHistoryEntity insuredHistoryEntity = insuredHistoryMapper.toHistoryEntity(insuredEntity);
            insuredRepository.save(insuredEntity);
            insuredHistoryRepository.save(insuredHistoryEntity);
          }
        }
      }

      insuranceSummaryService.updateBasicSearchTimestampAndResponseCode(banksaladUserId, organizationId, insuNum,
          insuranceBasicResponse.getSearchTimestamp(), insuranceBasicResponse.getRspCode());
    }

    InsuranceBasic insuranceBasic = insuranceBasicMapper.responseDtoToDto(insuranceBasicResponse);
    insuranceBasic.setInsuNum(insuNum);

    return insuranceBasicMapper.responseDtoToDto(insuranceBasicResponse);
  }

  private List<InsuranceContract> progressInsuranceContract(ExecutionContext executionContext, String orgCode,
      InsuranceBasic insuranceBasic) {
    List<InsuranceContract> responseInsuranceContracts = new ArrayList<>();
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String insuNum = insuranceBasic.getInsuNum();

    for (Insured insured : insuranceBasic.getInsuredList()) {
      String insuredNo = insured.getInsuredNo();

      long searchTimestamp = insuredRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
          banksaladUserId, organizationId, insuranceBasic.getInsuNum(), insured.getInsuredNo()
      ).map(InsuredEntity::getContractSearchTimestamp).orElse(DEFAULT_SEARCH_TIMESTAMP);

      GetInsuranceContractResponse insuranceContractResponse = getInsuranceContractResponse(executionContext, orgCode,
          insuranceBasic.getInsuNum(), insured.getInsuredNo(), searchTimestamp);

      List<InsuranceContract> insuranceContracts = insuranceContractResponse.getContractList();

      List<InsuranceContract> existingInsuranceContracts = insuranceContractRepository
          .findAllByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum)
          .stream()
          .map(insuranceContractMapper::entityToDto)
          .collect(Collectors.toList());

      if (!ObjectComparator.isSameListIgnoreOrder(insuranceContracts, existingInsuranceContracts)) {

        // Delete All
        insuranceContractRepository.deleteAllByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
            banksaladUserId, organizationId, insuNum, insuredNo
        );
        insuranceContractRepository.flush();

        for (int idx = 0; idx < insuranceContracts.size(); idx++) {
          InsuranceContract insuranceContract = insuranceContracts.get(idx);
          insuranceContract.setInsuNum(insuNum);
          insuranceContract.setInsuredNo(insuredNo);

          InsuranceContractEntity insuranceContractEntity = insuranceContractMapper.dtoToEntity(insuranceContract);
          insuranceContractEntity.setSyncedAt(executionContext.getSyncStartedAt());
          insuranceContractEntity.setBanksaladUserId(banksaladUserId);
          insuranceContractEntity.setOrganizationId(organizationId);
          insuranceContractEntity.setContractNo(idx);

          insuranceContractRepository.save(insuranceContractEntity);
          insuranceContractHistoryRepository
              .save(insuranceContractHistoryMapper.toHistoryEntity(insuranceContractEntity));
        }
      }
      // response Add
      responseInsuranceContracts.addAll(insuranceContracts);

      // search timestamp 업데이트 -> 피보험자 테이블
      InsuredEntity insuredEntity = insuredRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
              banksaladUserId,
              organizationId,
              insuNum,
              insuredNo
          ).orElseThrow(() -> new CollectRuntimeException("No data InsuredEntity"));

      insuredEntity.setContractSearchTimestamp(insuranceContractResponse.getSearchTimestamp());
      insuredEntity.setContractSearchResponseCode(insuranceContractResponse.getRspCode());
      insuredRepository.save(insuredEntity);
    }
    return responseInsuranceContracts;
  }

  private GetInsuranceBasicResponse getInsuranceBasicResponse(ExecutionContext executionContext, String orgCode, String
      insuNum, long searchTimestamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetInsuranceBasicRequest request = GetInsuranceBasicRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetInsuranceBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetInsuranceBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }

  // 피보험자순번(insuredNo) 을 받는 명세에서는 String인데 Request에서는 int로 되어있는상태. 그러나 N(2)로 되어있어서 String이 되지않을까 추측합니다.
  private GetInsuranceContractResponse getInsuranceContractResponse(ExecutionContext executionContext, String orgCode,
      String insuNum, String insuredNo, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetInsuranceContractRequest request = GetInsuranceContractRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .insuredNo(insuredNo)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetInsuranceContractRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetInsuranceContractResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_contract, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
