package com.banksalad.collectmydata.connect.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;
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

  /**
   * TODO
   * 아직 idl package 논의로 connect-mydata IDL 정의가 되지않아 기존에 존재하는 connect IDL로 우선 개발합니다.
   * 추후, connect-mydata IDL 정의 후 세부사항 업데이트 예정입니다.
   *
   * - request 값에 대한 validation 로직 추가
   */

  @Override
  public void issueToken(IssueTokenRequest request, StreamObserver<IssueTokenResponse> responseObserver) {
    OauthToken oauthToken = oauthTokenService.issueToken(request);
    IssueTokenResponse response = IssueTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .setRefreshToken(oauthToken.getRefreshToken())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getAccessToken(GetAccessTokenRequest request, StreamObserver<GetAccessTokenResponse> responseObserver) {
    // 토큰 조회
  }

  @Override
  public void revokeToken(RevokeTokenRequest request, StreamObserver<RevokeTokenResponse> responseObserver) {
    // 토큰 폐기
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request, StreamObserver<RevokeAllTokensResponse> responseObserver) {
    // 모든 토큰 폐기
  }

  @Override
  public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
    // 토큰 갱신
  }
}
