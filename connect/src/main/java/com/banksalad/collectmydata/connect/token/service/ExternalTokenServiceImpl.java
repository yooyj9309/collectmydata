package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.connect.common.dto.ErrorResponse;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.meters.ConnectMeterRegistry;
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
  private final CollectExecutor collectExecutor;
  private final ConnectMeterRegistry connectMeterRegistry;

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

    return execute(executionContext, Executions.oauth_issue_token, executionRequest);
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

    return execute(executionContext, Executions.oauth_refresh_token, executionRequest);
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

    execute(executionContext, Executions.oauth_revoke_token, executionRequest);
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

  private <T, R> R execute(ExecutionContext executionContext, Execution execution,
      ExecutionRequest<T> executionRequest) {

    ExecutionResponse<R> executionResponse = collectExecutor.execute(executionContext, execution, executionRequest);

    // TODO : Throw 부분 개선후 적용 - logging, execution monitoring,throw
    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      ErrorResponse errorResponse = (ErrorResponse) executionResponse.getResponse();
      connectMeterRegistry.incrementTokenErrorCount(executionContext.getOrganizationId(),
          TokenErrorType.getValidatedError(errorResponse.getError()));

      throw new CollectRuntimeException(errorResponse.getError());
    }
    return executionResponse.getResponse();
  }
}
