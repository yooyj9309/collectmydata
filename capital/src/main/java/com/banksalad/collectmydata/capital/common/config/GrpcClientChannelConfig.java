package com.banksalad.collectmydata.capital.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc;
import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientChannelConfig {

  @Value("${banksalad.cipher.uri}")
  private String cipherUri;

  @Value("${banksalad.collectmydata-connect.uri}")
  private String collectmydataConnectUri;

  private static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public ConnectmydataBlockingStub connectmydataBlockingStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forTarget(collectmydataConnectUri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return ConnectmydataGrpc.newBlockingStub(connectChannel);
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
