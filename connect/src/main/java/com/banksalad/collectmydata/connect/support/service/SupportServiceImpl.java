package com.banksalad.collectmydata.connect.support.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.collect.Apis;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.ApiSyncStatusEntity;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationHistoryEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ApiSyncStatusRepository;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationHistoryRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceClientIpRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceRepository;
import com.banksalad.collectmydata.connect.common.dto.ErrorResponse;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.enums.SecretType;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.mapper.OrganizationHistoryMapper;
import com.banksalad.collectmydata.connect.common.mapper.OrganizationMapper;
import com.banksalad.collectmydata.connect.common.mapper.ServiceMapper;
import com.banksalad.collectmydata.connect.common.meters.ConnectMeterRegistry;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationRequest;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenRequest;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

  @Value("${organization.finance-portal-domain}")
  private String financePortalDomain;

  private static final String FINANCE_REQUEST_GRANT_TYPE = "Bearer";
  private static final String FINANCE_RESPONSE_TOKEN_TYPE = "Bearer";
  private static final String FINANCE_SCOPE = "manage";
  private static final String BANKSALAD_ORGANIZATION_ID = "banksalad";

  private final CollectExecutor collectExecutor;
  private final ConnectMeterRegistry connectMeterRegistry;
  private final OrganizationRepository organizationRepository;
  private final OrganizationHistoryRepository organizationHistoryRepository;
  private final ApiSyncStatusRepository apiSyncStatusRepository;
  private final BanksaladClientSecretRepository banksaladClientSecretRepository;
  private final OrganizationOauthTokenRepository organizationOauthTokenRepository;
  private final ServiceRepository serviceRepository;
  private final ServiceClientIpRepository serviceClientIpRepository;

  private final OrganizationMapper organizationMapper = Mappers.getMapper(OrganizationMapper.class);
  private final OrganizationHistoryMapper organizationHistoryMapper = Mappers
      .getMapper(OrganizationHistoryMapper.class);
  private final ServiceMapper serviceMapper = Mappers.getMapper(ServiceMapper.class);

  public void syncAllOrganizationInfo() {
    syncOrganizationInfo();
    syncOrganizationServiceInfo();
  }

  @Override
  public void syncOrganizationInfo() {
    LocalDateTime now = LocalDateTime.now();
    String accessToken = getAccessToken(SecretType.FINANCE);
    Long timestamp = getTimeStamp(Apis.support_get_organization_info);

    Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, accessToken);
    FinanceOrganizationRequest request = FinanceOrganizationRequest.builder().searchTimestamp(timestamp).build();

    ExecutionRequest<FinanceOrganizationRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    // 7.1.2 기관 정보 조회 및 적재
    FinanceOrganizationResponse financeOrganizationResponse = execute(
        Executions.support_get_organization_info, executionRequest);

    // db 조회
    for (FinanceOrganizationInfo orgInfo : financeOrganizationResponse.getOrgList()) {
      OrganizationEntity entity = organizationRepository.findByOrgCode(orgInfo.getOrgCode())
          .orElse(OrganizationEntity.builder().build());

      organizationMapper.mergeDtoToEntity(orgInfo, entity);
      entity.setSyncedAt(now);

      organizationRepository.save(entity);
      organizationHistoryRepository
          .save(organizationHistoryMapper.toHistoryEntity(entity, OrganizationHistoryEntity.builder().build()));
    }

    apiSyncStatusRepository.save(
        ApiSyncStatusEntity.builder()
            .apiId(Executions.support_get_organization_info.getApi().getId())
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(financeOrganizationResponse.getSearchTimestamp())
            .build()
    );
  }

  @Override
  @Transactional
  public void syncOrganizationServiceInfo() {
    String accessToken = getAccessToken(SecretType.FINANCE);
    Long timestamp = getTimeStamp(Apis.support_get_organization_service_info); // 7.1.3 timestamp 조회
    // 7.1.3 기관 서비스 정보 조회 및 적재
    Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, accessToken);
    FinanceOrganizationRequest request = FinanceOrganizationRequest.builder()
        .searchTimestamp(timestamp)
        .build();

    ExecutionRequest<FinanceOrganizationRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    // 7.1.3 기관 서비스 조회
    FinanceOrganizationServiceResponse financeOrganizationServiceResponse = execute(
        Executions.support_get_organization_service_info,
        executionRequest);

    // db 적재
    // 기관 리스트 순회
    // 추후 업데이트시 히스토리 이력이 필요하다면 객체비교로직 추가.
    for (FinanceOrganizationInfo orgInfo : financeOrganizationServiceResponse.getOrgList()) {
      String orgCode = orgInfo.getOrgCode();

      OrganizationEntity organizationEntity = organizationRepository
          .findByOrgCode(orgCode)
          .orElse(null);

      String organizationId = null;
      if (organizationEntity != null) {
        organizationId = organizationEntity.getOrganizationId();
      }

      // service 순회
      for (FinanceOrganizationServiceInfo service : orgInfo.getServiceList()) {
        // service db insert;
        ServiceEntity serviceEntity = serviceRepository.findByOrgCodeAndClientId(orgCode, service.getClientId())
            .orElse(ServiceEntity.builder().build());

        serviceEntity.setOrganizationId(organizationId);
        serviceEntity.setOrgCode(orgCode);
        serviceMapper.mergeDtoToEntity(service, serviceEntity);
        serviceEntity = serviceRepository.save(serviceEntity);

        Long serviceId = serviceEntity.getId();
        String serviceName = serviceEntity.getServiceName();

        // clientIp 전체 리스트 제거 및 insert
        serviceClientIpRepository.deleteAllByOrgCodeAndServiceId(orgCode, serviceEntity.getId());
        // serviceIp 순회
        for (FinanceOrganizationServiceIp serviceIp : service.getClientIpList()) {
          ServiceClientIpEntity serviceClientIpEntity = ServiceClientIpEntity.builder()
              .serviceId(serviceId)
              .organizationId(organizationId)
              .orgCode(orgCode)
              .serviceName(serviceName)
              .clientIp(serviceIp.getClientIp())
              .build();

          serviceClientIpRepository.save(serviceClientIpEntity);
        }
      }
    }

    apiSyncStatusRepository.save(
        ApiSyncStatusEntity.builder()
            .apiId(Executions.support_get_organization_service_info.getApi().getId())
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(financeOrganizationServiceResponse.getSearchTimestamp())
            .build()
    );
  }

  public String getAccessToken(SecretType secretType) {
    // accessToken db 조회
    OrganizationOauthTokenEntity tokenEntity = organizationOauthTokenRepository
        .findBySecretType(secretType.name())
        .orElse(null);

    // accessToken 유효기간 검증 ,조회 및 db저장,
    if (tokenEntity == null || tokenEntity.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
      // banksalad 기관 clientId, clientSecret 조회
      BanksaladClientSecretEntity banksaladClientSecretEntity = banksaladClientSecretRepository
          .findBySecretType(secretType.name())
          .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_CLIENT_ID));

      FinanceOrganizationTokenRequest request = FinanceOrganizationTokenRequest.builder()
          .grantType(FINANCE_REQUEST_GRANT_TYPE)
          .clientId(banksaladClientSecretEntity.getClientId())
          .clientSecret(banksaladClientSecretEntity.getClientSecret())
          .scope(FINANCE_SCOPE)
          .build();

      Map<String, String> header = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
      ExecutionRequest<FinanceOrganizationTokenRequest> executionRequest = ExecutionUtil
          .assembleExecutionRequest(header, request);

      // 7.1.1 토큰정보 조회
      FinanceOrganizationTokenResponse response = execute(Executions.support_get_access_token,
          executionRequest);

      if (!response.getTokenType().equals(FINANCE_RESPONSE_TOKEN_TYPE) ||
          !response.getScope().equals(FINANCE_SCOPE)) {
        throw new ConnectException(ConnectErrorType.INVALID_TOKEN_RESPONSE);
      }

      // expiresAt 계산
      LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(response.getExpiresIn()).minusDays(1);

      // db 적재
      tokenEntity = organizationOauthTokenRepository.save(
          OrganizationOauthTokenEntity.builder()
              .id(Optional.ofNullable(tokenEntity).map(OrganizationOauthTokenEntity::getId).orElse(null))
              .secretType(secretType.name())
              .accessToken(response.getAccessToken())
              .accessTokenExpiresAt(expiresAt)
              .accessTokenExpiresIn(response.getExpiresIn())
              .tokenType(response.getTokenType())
              .scope(response.getScope())
              .build()
      );
    }

    return new StringBuilder().append("Bearer ").append(tokenEntity.getAccessToken()).toString();
  }

  private <T, R> R execute(Execution execution, ExecutionRequest<T> executionRequest) {
    // 추후 도메인이 늘어나는경우, 해당부분 수정 필요.
    ExecutionContext executionContext = ExecutionContext.builder()
        .organizationId(BANKSALAD_ORGANIZATION_ID)
        .organizationHost(financePortalDomain)
        .build();

    ExecutionResponse<R> executionResponse = collectExecutor.execute(executionContext, execution, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      ErrorResponse errorResponse = ((FinanceOrganizationTokenResponse) executionResponse.getResponse())
          .getErrorResponse();

      connectMeterRegistry.incrementTokenErrorCount(executionContext.getOrganizationId(),
          TokenErrorType.getValidatedError(errorResponse.getError()));

      throw new ConnectException(ConnectErrorType.INVALID_API_RESPONSE);
    }
    return executionResponse.getResponse();
  }

  private Long getTimeStamp(Api api) {
    //DB 조회
    ApiSyncStatusEntity entity = apiSyncStatusRepository.findByApiId(api.getId())
        .orElse(ApiSyncStatusEntity.builder().build());
    return Optional.ofNullable(entity.getSearchTimestamp()).orElse(0L);
  }

}
