package com.banksalad.collectmydata.connect.support.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.connect.common.collect.Apis;
import com.banksalad.collectmydata.connect.common.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.SyncApiStatusEntity;
import com.banksalad.collectmydata.connect.common.db.entity.mapper.ConnectOrganizationMapper;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.SyncApiStatusRepository;
import com.banksalad.collectmydata.connect.common.service.ExecutionService;
import com.banksalad.collectmydata.connect.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationRequest;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenRequest;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenResponse;
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
  public static final String AUTHORIZATION = "Authorization";
  public static final String BANKSALAD_ORAGNIZATION_ID = "banksalad";

  private final ExecutionService executionService;
  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final SyncApiStatusRepository syncApiStatusRepository;
  private final OrganizationClientRepository organizationClientRepository;
  private final OrganizationOauthTokenRepository organizationOauthTokenRepository;
  private final ConnectOrganizationMapper mapper = Mappers.getMapper(ConnectOrganizationMapper.class);

  public void syncAllOrganizationInfo() {
    syncOrganizationInfo();
    syncOrganizationServiceInfo();
  }

  @Override
  public void syncOrganizationInfo() {
    ExecutionContext executionContext = executionContextAssembler();

    String accessToken = getAccessToken(executionContext);
    Long timestamp = getTimeStamp(Apis.support_get_organization_info); // 7.1.2 timestamp 조회 fixme

    Map<String, String> headers = Map.of(AUTHORIZATION, accessToken);
    FinanceOrganizationRequest request = FinanceOrganizationRequest.builder().searchTimestamp(timestamp).build();
    // 7.1.2 기관 정보 조회 및 적재

    ExecutionRequest<FinanceOrganizationRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    FinanceOrganizationResponse financeOrganizationResponse = (FinanceOrganizationResponse) executionService.execute(
        executionContext,
        Executions.support_get_organization_info,
        executionRequest
    );

    // db 조회
    for (FinanceOrganizationInfo orgInfo : financeOrganizationResponse.getOrgList()) {
      ConnectOrganizationEntity entity = connectOrganizationRepository.findByOrganizationCode(orgInfo.getOrgCode())
          .orElse(ConnectOrganizationEntity.builder().build());

      mapper.merge(orgInfo, entity);
      connectOrganizationRepository.save(entity);
    }
  }

  @Override
  public void syncOrganizationServiceInfo() {
    ExecutionContext executionContext = executionContextAssembler();
    String accessToken = getAccessToken(executionContext);

    Long timestamp = getTimeStamp(Apis.support_get_organization_service_info); // 7.1.3 timestamp 조회 fixme
    // request 생성 fixme
    // 7.1.3 기관 서비스 정보 조회 및 적재
    FinanceOrganizationServiceResponse financeOrganizationServiceResponse = null;

  }
  
  public String getAccessToken(ExecutionContext executionContext) {
    // banksalad 기관 clientId, clientSecret 조회
    OrganizationClientEntity organizationClientEntity = organizationClientRepository
        .findByOrganizationId(BANKSALAD_ORAGNIZATION_ID)
        .orElseThrow(RuntimeException::new); // fixme

    // accessToken db 조회
    OrganizationOauthTokenEntity tokenEntity = organizationOauthTokenRepository
        .findByOrganizationId(BANKSALAD_ORAGNIZATION_ID)
        .orElse(null);

    // accessToken 유효기간 검증 ,조회 및 db저장
    if (tokenEntity == null || tokenEntity.isAccessTokenExpired()) {

      FinanceOrganizationTokenRequest request = FinanceOrganizationTokenRequest.builder()
          .clientId(organizationClientEntity.getClientId())
          .clientSecret(organizationClientEntity.getClientSecret())
          .build();

      ExecutionRequest<FinanceOrganizationTokenRequest> executionRequest = ExecutionUtil
          .executionRequestAssembler(request);

      // 7.1.1 기관 정보 조회 및 적재
      FinanceOrganizationTokenResponse response = (FinanceOrganizationTokenResponse) executionService.execute(
          executionContext,
          Executions.support_get_access_token,
          executionRequest
      );

      //TODO token_type, scope 고정값 검증 및 에러 처리

      // expiresAt 계산
      LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(response.getExpiresIn()).minusDays(1);

      // db 적재
      tokenEntity = organizationOauthTokenRepository.save(
          OrganizationOauthTokenEntity.builder()
              .organizationId(BANKSALAD_ORAGNIZATION_ID)
              .accessToken(response.getAccessToken())
              .accessTokenExpiresAt(expiresAt)
              .accessTokenExpiresIn(response.getExpiresIn())
              .tokenType(response.getTokenType())
              .scope(response.getScope())
              .build()
      );
    }

    return tokenEntity.getAccessToken();
  }

  private Long getTimeStamp(Api api) {
    //DB 조회
    SyncApiStatusEntity entity = syncApiStatusRepository.findByApiId(api.getId())
        .orElse(SyncApiStatusEntity.builder().build());
    return Optional.ofNullable(entity.getOriginalSyncedAt()).orElse(0L);
  }

  private ExecutionContext executionContextAssembler() {
    return ExecutionContext.builder()
        .organizationHost(financePortalDomain)
        .build();
  }
}
