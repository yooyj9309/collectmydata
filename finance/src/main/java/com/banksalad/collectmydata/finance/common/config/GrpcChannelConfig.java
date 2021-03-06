package com.banksalad.collectmydata.finance.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc;
import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc.CollectmydataconnectBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcChannelConfig {

  @Value("${uri.collectmydataconnect}")
  private String connectUri = null;

  @Value("${uri.cipher}")
  private String cipherUri = null;

  private static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public CollectmydataconnectBlockingStub collectmydataconnectBlockingStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forTarget(connectUri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return CollectmydataconnectGrpc.newBlockingStub(connectChannel);
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
