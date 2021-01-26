package com.banksalad.collectmydata.oauth.common.config.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.banksalad.idl.apis.v1.auth.AuthGrpc;
import com.github.banksalad.idl.apis.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
@Profile({"local", "test"})
public class LocalGrpcClientChannelConfig {

  // connect 연동
  @Value("${connect-server.host}")
  private String connectHost;

  @Value("${connect-server.port}")
  private int connectPort;

  @Value("${auth-server.host}")
  private String authHost;

  @Value("${auth-server.port}")
  private int authPort;

  @Bean
  public AuthBlockingStub authBlockingStub() {
    ManagedChannel authChannel = ManagedChannelBuilder.forAddress(authHost, authPort)
        .usePlaintext()
        .build();
    return AuthGrpc.newBlockingStub(authChannel);
  }

  @Bean
  public ConnectmydataBlockingStub connectmydataBlockingStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forAddress(connectHost, connectPort)
        .usePlaintext()
        .build();
    return ConnectmydataGrpc.newBlockingStub(connectChannel);
  }
}
