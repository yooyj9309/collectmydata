package com.banksalad.collectmydata.collect.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc;
import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.user.UserGrpc;
import com.github.banksalad.idl.apis.v1.user.UserGrpc.UserBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Configuration
public class GrpcClientChannelConfig {

  @Value("${connect-server.host}")
  private String connectHost;

  @Value("${connect-server.port}")
  private int connectPort;

  @Value("${uri.user}")
  private String userUri = null;

  @Value("${uri.cipher}")
  private String cipherUri = null;

  public static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public ConnectmydataBlockingStub connectmydataBlockingStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forAddress(connectHost, connectPort)
        .usePlaintext()
        .build();
    return ConnectmydataGrpc.newBlockingStub(connectChannel);
  }

  @Bean
  public UserBlockingStub userBlockingStub() {
    ManagedChannel userChannel = ManagedChannelBuilder.forTarget(userUri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return UserGrpc.newBlockingStub(userChannel);
  }

  @Bean
  public CipherBlockingStub cipherBlockingStub() {
    ManagedChannel cipherChannel = ManagedChannelBuilder.forTarget(cipherUri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return CipherGrpc.newBlockingStub(cipherChannel);
  }
}
