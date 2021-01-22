package com.banksalad.collectmydata.oauth.common.config.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.banksalad.idl.apis.external.v1.auth.AuthGrpc;
import com.github.banksalad.idl.apis.external.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
@Profile("local")
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
  public ConnectGrpc.ConnectBlockingStub connectStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forAddress(connectHost, connectPort)
        .usePlaintext()
        .build();
    return ConnectGrpc.newBlockingStub(connectChannel);
  }
}
