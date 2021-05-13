package com.banksalad.collectmydata.connect.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.banksalad.idl.apis.v1.auth.AuthGrpc;
import com.github.banksalad.idl.apis.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientChannelConfig {

  @Value("${banksalad.collectSchedule.uri}")
  private String collectScheduleConnectUri;

  @Value("${banksalad.auth.uri}")
  private String authUri;

  private static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public CollectscheduleBlockingStub collectScheduleBlockingStub() {
    return CollectscheduleGrpc.newBlockingStub(generateManagedChannel(collectScheduleConnectUri));
  }

  @Bean
  public AuthBlockingStub authBlockingStub() {
    return AuthGrpc.newBlockingStub(generateManagedChannel(authUri));
  }

  private ManagedChannel generateManagedChannel(String uri) {
    return ManagedChannelBuilder.forTarget(uri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
  }
}
