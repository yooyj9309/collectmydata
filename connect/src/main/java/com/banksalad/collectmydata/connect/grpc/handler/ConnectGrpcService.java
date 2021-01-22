package com.banksalad.collectmydata.connect.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.dto.TokenResponse;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;
import com.banksalad.collectmydata.connect.token.service.ValidatorService;
import com.banksalad.collectmydata.connect.token.validator.GetAccessTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.IssueTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RefreshTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeAllTokensRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeTokenRequestValidator;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectGrpc;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RefreshTokenResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeAllTokensResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeTokenResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectGrpcService extends ConnectGrpc.ConnectImplBase {

  private final OauthTokenService oauthTokenService;
  private final ValidatorService validatorService;

  /**
   * TODO
   * 아직 idl package 논의로 connect-mydata IDL 정의가 되지않아 기존에 존재하는 connect IDL로 우선 개발합니다.
   * 추후, connect-mydata IDL 정의 후 세부사항 업데이트 예정입니다.
   */

  @Override
  public void issueToken(IssueTokenRequest request, StreamObserver<IssueTokenResponse> responseObserver) {
    IssueTokenRequestValidator validator = IssueTokenRequestValidator.of(request);
    validatorService.validate(validator);

    OauthToken oauthToken = oauthTokenService.issueToken(request);
    TokenResponse tokenResponse = TokenResponse.builder()
        .oauthToken(oauthToken)
        .build();

    responseObserver.onNext(tokenResponse.toIssueTokenResponseProto());
    responseObserver.onCompleted();
  }

  @Override
  public void getAccessToken(GetAccessTokenRequest request, StreamObserver<GetAccessTokenResponse> responseObserver) {
    GetAccessTokenRequestValidator validator = GetAccessTokenRequestValidator.of(request);
    validatorService.validate(validator);

    OauthToken oauthToken = oauthTokenService.getAccessToken(request);
    TokenResponse tokenResponse = TokenResponse.builder()
        .oauthToken(oauthToken)
        .build();

    responseObserver.onNext(tokenResponse.toGetAccessTokenResponseProto());
    responseObserver.onCompleted();
  }

  @Override
  public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
    RefreshTokenRequestValidator validator = RefreshTokenRequestValidator.of(request);
    validatorService.validate(validator);

    OauthToken oauthToken = oauthTokenService.refreshToken(request);
    TokenResponse tokenResponse = TokenResponse.builder()
        .oauthToken(oauthToken)
        .build();

    responseObserver.onNext(tokenResponse.toRefreshTokenResponseProto());
    responseObserver.onCompleted();
  }

  @Override
  public void revokeToken(RevokeTokenRequest request, StreamObserver<RevokeTokenResponse> responseObserver) {
    RevokeTokenRequestValidator validator = RevokeTokenRequestValidator.of(request);
    validatorService.validate(validator);

    oauthTokenService.revokeToken(request);

    responseObserver.onNext(RevokeTokenResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request, StreamObserver<RevokeAllTokensResponse> responseObserver) {
    RevokeAllTokensRequestValidator validator = RevokeAllTokensRequestValidator.of(request);
    validatorService.validate(validator);

    oauthTokenService.revokeAllTokens(request);

    responseObserver.onNext(RevokeAllTokensResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
