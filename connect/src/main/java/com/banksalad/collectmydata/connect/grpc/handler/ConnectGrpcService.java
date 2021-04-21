package com.banksalad.collectmydata.connect.grpc.handler;

import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.connect.common.service.ValidatorService;
import com.banksalad.collectmydata.connect.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.banksalad.collectmydata.connect.grpc.validator.GetAccessTokenRequestValidator;
import com.banksalad.collectmydata.connect.grpc.validator.GetOrganizationRequestValidator;
import com.banksalad.collectmydata.connect.grpc.validator.IssueTokenRequestValidator;
import com.banksalad.collectmydata.connect.grpc.validator.RefreshTokenRequestValidator;
import com.banksalad.collectmydata.connect.grpc.validator.RevokeAllTokensRequestValidator;
import com.banksalad.collectmydata.connect.grpc.validator.RevokeTokenRequestValidator;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.organization.dto.OrganizationProtoResponse;
import com.banksalad.collectmydata.connect.organization.service.OrganizationService;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.dto.OauthTokenProtoResponse;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
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
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("healthCheck error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void issueToken(IssueTokenRequest request, StreamObserver<IssueTokenResponse> responseObserver) {
    try {
      IssueTokenRequestValidator validator = IssueTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.issueToken(request);
      OauthTokenProtoResponse oauthTokenProtoResponse = OauthTokenProtoResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(oauthTokenProtoResponse.toIssueTokenResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("issueToken error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void getAccessToken(GetAccessTokenRequest request, StreamObserver<GetAccessTokenResponse> responseObserver) {
    try {
      GetAccessTokenRequestValidator validator = GetAccessTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.getAccessToken(request);
      OauthTokenProtoResponse oauthTokenProtoResponse = OauthTokenProtoResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(oauthTokenProtoResponse.toGetAccessTokenResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("getAccessToken error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
    try {
      RefreshTokenRequestValidator validator = RefreshTokenRequestValidator.of(request);
      validatorService.validate(validator);

      OauthToken oauthToken = oauthTokenService.refreshToken(request);
      OauthTokenProtoResponse oauthTokenProtoResponse = OauthTokenProtoResponse.builder()
          .oauthToken(oauthToken)
          .build();

      responseObserver.onNext(oauthTokenProtoResponse.toRefreshTokenResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("refreshToken error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
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
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("revokeToken error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request,
      StreamObserver<RevokeAllTokensResponse> responseObserver) {
    try {
      RevokeAllTokensRequestValidator validator = RevokeAllTokensRequestValidator.of(request);
      validatorService.validate(validator);

      oauthTokenService.revokeAllTokens(request);

      responseObserver.onNext(RevokeAllTokensResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("revokeAllTokens error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void getOrganizationByOrganizationObjectid(GetOrganizationByOrganizationObjectidRequest request,
      StreamObserver<GetOrganizationResponse> responseObserver) {
    try {
      GetOrganizationRequestValidator validator = GetOrganizationRequestValidator.of(request);
      validatorService.validate(validator);

      Organization organization = organizationService.getOrganization(request);
      OrganizationProtoResponse organizationProtoResponse = OrganizationProtoResponse.builder()
          .organization(organization)
          .build();

      responseObserver.onNext(organizationProtoResponse.toGetOrganizationProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("getOrganizationByOrganizationObjectid error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void getOrganizationByOrganizationId(GetOrganizationByOrganizationIdRequest request,
      StreamObserver<GetOrganizationResponse> responseObserver) {
    try {
      GetOrganizationRequestValidator validator = GetOrganizationRequestValidator.of(request);
      validatorService.validate(validator);

      Organization organization = organizationService.getOrganization(request);
      OrganizationProtoResponse organizationProtoResponse = OrganizationProtoResponse.builder()
          .organization(organization)
          .build();

      responseObserver.onNext(organizationProtoResponse.toGetOrganizationProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("getOrganizationByOrganizationId error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }
}
