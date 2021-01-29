package com.banksalad.collectmydata.connect.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.organization.dto.OrganizationResponse;
import com.banksalad.collectmydata.connect.organization.service.OrganizationService;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.dto.TokenResponse;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;
import com.banksalad.collectmydata.connect.common.service.ValidatorService;
import com.banksalad.collectmydata.connect.token.validator.GetAccessTokenRequestValidator;
import com.banksalad.collectmydata.connect.organization.validator.GetOrganizationRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.IssueTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RefreshTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeAllTokensRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeTokenRequestValidator;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.HealthCheckRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.HealthCheckResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import static com.banksalad.collectmydata.common.exception.ExceptionHandler.*;

@Service
@RequiredArgsConstructor
public class ConnectGrpcService extends ConnectmydataGrpc.ConnectmydataImplBase {

  private final OauthTokenService oauthTokenService;
  private final OrganizationService organizationService;
  private final ValidatorService validatorService;

  @Override
  public void healthCheck(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    try {
      responseObserver.onNext(HealthCheckResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void issueToken(IssueTokenRequest request, StreamObserver<IssueTokenResponse> responseObserver) {
    try {
      IssueTokenRequestValidator validator = IssueTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.issueToken(request);
      TokenResponse tokenResponse = TokenResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(tokenResponse.toIssueTokenResponseProto());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void getAccessToken(GetAccessTokenRequest request, StreamObserver<GetAccessTokenResponse> responseObserver) {
    try {
      GetAccessTokenRequestValidator validator = GetAccessTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.getAccessToken(request);
      TokenResponse tokenResponse = TokenResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(tokenResponse.toGetAccessTokenResponseProto());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
    try {
      RefreshTokenRequestValidator validator = RefreshTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.refreshToken(request);
      TokenResponse tokenResponse = TokenResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(tokenResponse.toRefreshTokenResponseProto());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void revokeToken(RevokeTokenRequest request, StreamObserver<RevokeTokenResponse> responseObserver) {
    try {
      RevokeTokenRequestValidator validator = RevokeTokenRequestValidator.of(request);
      validatorService.validate(validator);

      oauthTokenService.revokeToken(request);

      responseObserver.onNext(RevokeTokenResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request, StreamObserver<RevokeAllTokensResponse> responseObserver) {
    try {
      RevokeAllTokensRequestValidator validator = RevokeAllTokensRequestValidator.of(request);
      validatorService.validate(validator);

      oauthTokenService.revokeAllTokens(request);

      responseObserver.onNext(RevokeAllTokensResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }

  @Override
  public void getOrganization(GetOrganizationRequest request, StreamObserver<GetOrganizationResponse> responseObserver) {
    try {
      GetOrganizationRequestValidator validator = GetOrganizationRequestValidator.of(request);
      validatorService.validate(validator);

      Organization organization = organizationService.getOrganization(request);
      OrganizationResponse organizationResponse = OrganizationResponse.builder()
          .organization(organization)
          .build();

      responseObserver.onNext(organizationResponse.toGetOrganizationProto());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(handle(e));
    }
  }
}
