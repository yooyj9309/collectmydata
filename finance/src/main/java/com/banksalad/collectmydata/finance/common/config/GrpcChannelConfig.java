package com.banksalad.collectmydata.finance.common.config;

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
public class GrpcChannelConfig {

  @Value("${spring.profiles.active}")
  private String profile;

  private static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";
  private static final String CIPHER_TARGET = "dns:///cipher-headless:18081";
  private static final String COLLECTMYDATA_CONNECT_TARGET = "dns:///collectmydataconnect-headless:18081";

  private static final String LOCAL_CIPHER_TARGET = "localhost:9999";
  private static final String LOCAL_COLLECTMYDATA_CONNECT_TARGET = "localhost:19081";

  @Bean
  public ConnectmydataBlockingStub connectmydataBlockingStub() {
    String target = isLocalProfiles(profile) ? LOCAL_COLLECTMYDATA_CONNECT_TARGET : COLLECTMYDATA_CONNECT_TARGET;
    ManagedChannel connectChannel = ManagedChannelBuilder.forTarget(target)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return ConnectmydataGrpc.newBlockingStub(connectChannel);
  }

  @Bean
  public CipherBlockingStub cipherBlockingStub() {
    String target = isLocalProfiles(profile) ? LOCAL_CIPHER_TARGET : CIPHER_TARGET;
    ManagedChannel cipherChannel = ManagedChannelBuilder.forTarget(target)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return CipherGrpc.newBlockingStub(cipherChannel);
  }

  private boolean isLocalProfiles(String profile) {
    return profile.equals("local") || profile.equals("test");
  }
}
