package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.service.ExecutionService;
import com.banksalad.collectmydata.connect.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.ExternalIssueTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.ExternalRefreshTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.ExternalRevokeTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalTokenServiceImpl implements ExternalTokenService {

  private final OrganizationClientRepository organizationClientRepository;
  private final ExecutionService executionService;

  @Value("${banksalad.oauth-callback-url}")
  private String redirectUrl;

  @Override
  public ExternalTokenResponse issueToken(Organization organization, String authorizationCode) {
    OrganizationClientEntity organizationClientEntity = getOrganizationClientEntity(organization);

    ExternalIssueTokenRequest request = ExternalIssueTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .code(authorizationCode)
        .clientId(organizationClientEntity.getClientId())
        .clientSecret(organizationClientEntity.getClientSecret())
        .redirectUri(redirectUrl)
        .build();

    ExecutionRequest<ExternalIssueTokenRequest> executionRequest = ExecutionUtil.executionRequestAssembler(request);
    ExecutionContext executionContext = buildExecutionContext(organization);

    ExternalTokenResponse response = executionService
        .execute(executionContext, Executions.oauth_issue_token, executionRequest);
    return response;
  }

  @Override
  public ExternalTokenResponse refreshToken(Organization organization, String refreshToken) {
    OrganizationClientEntity organizationClientEntity = getOrganizationClientEntity(organization);

    ExternalRefreshTokenRequest request = ExternalRefreshTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .refreshToken(refreshToken)
        .clientId(organizationClientEntity.getClientId())
        .clientSecret(organizationClientEntity.getClientSecret())
        .build();

    ExecutionRequest<ExternalRefreshTokenRequest> executionRequest = ExecutionUtil.executionRequestAssembler(request);
    ExecutionContext executionContext = buildExecutionContext(organization);

    ExternalTokenResponse response = executionService
        .execute(executionContext, Executions.oauth_refresh_token, executionRequest);
    return response;
  }

  @Override
  public void revokeToken(Organization organization, String accessToken) {
    OrganizationClientEntity organizationClientEntity = getOrganizationClientEntity(organization);

    ExternalRevokeTokenRequest request = ExternalRevokeTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .token(accessToken)
        .clientId(organizationClientEntity.getClientId())
        .clientSecret(organizationClientEntity.getClientSecret())
        .build();

    ExecutionRequest<ExternalRevokeTokenRequest> executionRequest = ExecutionUtil.executionRequestAssembler(request);
    ExecutionContext executionContext = buildExecutionContext(organization);

    executionService.execute(executionContext, Executions.oauth_revoke_token, executionRequest);
  }

  private OrganizationClientEntity getOrganizationClientEntity(Organization organization) {
    return organizationClientRepository
        .findByOrganizationId(organization.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));
  }

  private ExecutionContext buildExecutionContext(Organization organization) {
    return ExecutionContext.builder()
        .organizationId(organization.getOrganizationId())
        .organizationHost(organization.getDomain())
        .build();
  }
}
