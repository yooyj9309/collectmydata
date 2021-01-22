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
@Profile(value = {"staging", "production"})
public class GrpcClientChannelConfig {

  // connect 연동
  @Value("${connect-server.host}")
  private String connectHost;

  @Value("${connect-server.port}")
  private int connectPort;

  @Value("${auth-server.url}")
  private String authUrl;

  public static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public AuthBlockingStub authBlockingStub() {
    ManagedChannel accountbookChannel = ManagedChannelBuilder.forTarget(authUrl)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return AuthGrpc.newBlockingStub(accountbookChannel);
  }

  @Bean
  public ConnectGrpc.ConnectBlockingStub connectStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forAddress(connectHost, connectPort)
        .usePlaintext()
        .build();
    return ConnectGrpc.newBlockingStub(connectChannel);
  }
}
