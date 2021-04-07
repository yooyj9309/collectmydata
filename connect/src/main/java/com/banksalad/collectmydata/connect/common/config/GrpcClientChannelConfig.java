package com.banksalad.collectmydata.connect.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientChannelConfig {

  @Value("${banksalad.collectSchedule.uri}")
  private String collectScheduleConnectUri;

  private static final String CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin";

  @Bean
  public CollectscheduleBlockingStub collectScheduleBlockingStub() {
    ManagedChannel connectChannel = ManagedChannelBuilder.forTarget(collectScheduleConnectUri)
        .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
        .usePlaintext()
        .build();
    return CollectscheduleGrpc.newBlockingStub(connectChannel);
  }
}
