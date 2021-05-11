package com.banksalad.collectmydata.connect.grpc.handler;

import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
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
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinanceProtoResponse;
import com.banksalad.collectmydata.connect.publishment.organization.service.OrganizationPublishService;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.dto.OauthTokenProtoResponse;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListConnectedFinanceOrganizationsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListConnectedFinanceOrganizationsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListFinanceOrganizationsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListFinanceOrganizationsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RefreshTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeAllTokensResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeTokenResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class ConnectGrpcService extends CollectmydataconnectGrpc.CollectmydataconnectImplBase {

  private final OauthTokenService oauthTokenService;
  private final OrganizationService organizationService;
  private final ValidatorService validatorService;
  private final OrganizationPublishService organizationPublishService;

  @Override
  public void issueToken(IssueTokenRequest request, StreamObserver<IssueTokenResponse> responseObserver) {
    try {
      IssueTokenRequestValidator validator = IssueTokenRequestValidator.of(request);
      validatorService.validate(validator);

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

      LoggingMdcUtil.set(Sector.FINANCE.name(), null, banksaladUserId, request.getOrganizationId(), null);
      log.info("Collectmydata-connect ConnectGrpcService.issueToken()");

      OauthToken oauthToken = oauthTokenService
          .issueToken(banksaladUserId, request.getOrganizationId(), request.getAuthorizationCode());
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

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void getAccessToken(GetAccessTokenRequest request, StreamObserver<GetAccessTokenResponse> responseObserver) {
    try {
      GetAccessTokenRequestValidator validator = GetAccessTokenRequestValidator.of(request);
      validatorService.validate(validator);

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

      LoggingMdcUtil.set(Sector.FINANCE.name(), null, banksaladUserId, request.getOrganizationId(), null);
      log.info("Collectmydata-connect ConnectGrpcService.getAccessToken()");

      OauthToken oauthToken = oauthTokenService.getAccessToken(banksaladUserId, request.getOrganizationId());
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

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
    try {
      RefreshTokenRequestValidator validator = RefreshTokenRequestValidator.of(request);
      validatorService.validate(validator);

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

      LoggingMdcUtil.set(Sector.FINANCE.name(), null, banksaladUserId, request.getOrganizationId(), null);
      log.info("Collectmydata-connect ConnectGrpcService.refreshToken()");

      OauthToken oauthToken = oauthTokenService.refreshToken(banksaladUserId, request.getOrganizationId());
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

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void revokeToken(RevokeTokenRequest request, StreamObserver<RevokeTokenResponse> responseObserver) {
    try {
      RevokeTokenRequestValidator validator = RevokeTokenRequestValidator.of(request);
      validatorService.validate(validator);

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

      LoggingMdcUtil.set(Sector.FINANCE.name(), null, banksaladUserId, request.getOrganizationId(), null);
      log.info("Collectmydata-connect ConnectGrpcService.revokeToken()");

      oauthTokenService.revokeToken(banksaladUserId, request.getOrganizationId());

      responseObserver.onNext(RevokeTokenResponse.getDefaultInstance());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("revokeToken error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request,
      StreamObserver<RevokeAllTokensResponse> responseObserver) {
    try {
      RevokeAllTokensRequestValidator validator = RevokeAllTokensRequestValidator.of(request);
      validatorService.validate(validator);

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

      LoggingMdcUtil.set(Sector.FINANCE.name(), null, banksaladUserId, null, null);
      log.info("Collectmydata-connect ConnectGrpcService.revokeAllTokens()");

      oauthTokenService.revokeAllTokens(banksaladUserId);

      responseObserver.onNext(RevokeAllTokensResponse.getDefaultInstance());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("revokeAllTokens error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void getOrganizationByOrganizationGuid(GetOrganizationByOrganizationGuidRequest request,
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
      log.error("getOrganizationByOrganizationGuid error message,{}", e.getMessage(), e);
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

  @Override
  public void listFinanceOrganizations(ListFinanceOrganizationsRequest request,
      StreamObserver<ListFinanceOrganizationsResponse> responseObserver) {

    try {
      // Todo: to validate request

      List<OrganizationForFinance> organizationForFinances = organizationPublishService.listFinanceOrganizations();
      OrganizationForFinanceProtoResponse organizationForFinanceProtoResponse = OrganizationForFinanceProtoResponse
          .builder()
          .organizationForFinances(organizationForFinances)
          .build();
      responseObserver.onNext(organizationForFinanceProtoResponse.toListFinanceOrganizationsProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listFinanceOrganizations error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void listConnectedFinanceOrganizations(ListConnectedFinanceOrganizationsRequest request,
      StreamObserver<ListConnectedFinanceOrganizationsResponse> responseObserver) {

    try {
      // Todo: to validate request

      final long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      List<OrganizationForFinance> organizationForFinances = organizationPublishService
          .listConnectedFinanceOrganizations(banksaladUserId);
      OrganizationForFinanceProtoResponse organizationForFinanceProtoResponse = OrganizationForFinanceProtoResponse
          .builder()
          .organizationForFinances(organizationForFinances)
          .build();
      responseObserver.onNext(organizationForFinanceProtoResponse.toListConnectedFinanceOrganizationsProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listFinanceOrganizations error message,{}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }
}
