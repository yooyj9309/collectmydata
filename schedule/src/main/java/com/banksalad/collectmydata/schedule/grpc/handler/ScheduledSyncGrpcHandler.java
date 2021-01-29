package com.banksalad.collectmydata.schedule.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import com.banksalad.collectmydata.schedule.sync.service.ScheduledSyncService;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleImplBase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO : - How to validate request parameters
//        - RegisterScheduledSyncRequest, UnregisterScheduledSyncRequest
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncGrpcHandler extends CollectscheduleImplBase implements ScheduledSyncHandler {

  private final ScheduledSyncService scheduledSyncService;

  public void registerScheduledSync(RegisterScheduledSyncRequest request,
      StreamObserver<RegisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncRequest scheduledSyncRequest = ScheduledSyncRequest.of(request);
      scheduledSyncService.register(scheduledSyncRequest);

      RegisterScheduledSyncResponse response = RegisterScheduledSyncResponse.newBuilder().build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Register Scheduled Sync Error: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }

  public void unregisterScheduledSync(UnregisterScheduledSyncRequest request,
      StreamObserver<UnregisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncRequest scheduledSyncRequest = ScheduledSyncRequest.of(request);
      scheduledSyncService.unregister(scheduledSyncRequest);

      UnregisterScheduledSyncResponse response = UnregisterScheduledSyncResponse.newBuilder().build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Unregister Scheduled Sync Error: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }

  @Override
  public void healthCheck(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    try {
      HealthCheckResponse response = HealthCheckResponse.newBuilder().build();
      responseObserver.onCompleted();
      responseObserver.onNext(response);
    } catch (Exception e) {
      log.error("Health Check Error: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }
}
