package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.connect.common.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.service.ExecutionService;
import com.banksalad.collectmydata.connect.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.ExternalIssueTokenRequest;
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
    // TODO : refresh Token
    return ExternalTokenResponse.builder().build();
  }

  @Override
  public void revokeToken(Organization organization, String accessToken) {
    // TODO : revoke Token
  }

  private OrganizationClientEntity getOrganizationClientEntity(Organization organization) {
    return organizationClientRepository
        .findByOrganizationId(organization.getOrganizationId())
        .orElseThrow(NotFoundOrganizationException::new);
  }

  private ExecutionContext buildExecutionContext(Organization organization) {
    return ExecutionContext.builder()
        .organizationId(organization.getOrganizationId())
        .organizationHost(organization.getDomain())
        .build();
  }
}
