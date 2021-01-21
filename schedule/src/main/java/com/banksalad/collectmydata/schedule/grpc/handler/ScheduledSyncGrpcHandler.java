package com.banksalad.collectmydata.schedule.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import com.banksalad.collectmydata.schedule.sync.schedule.ScheduledSyncScheduler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledSyncGrpcHandler extends CollectScheduleGrpc.CollectscheduleImplBase implements
    ScheduledSyncHandler {

  private final ScheduledSyncScheduler scheduledSyncScheduler;

  public void registerScheduledSync(RegisterScheduledSyncRequest request,
      StreamObserver<RegisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncRequest scheduledSyncRequest = ScheduledSyncRequest.builder()
          .banksaladUserId()
          .orgCode()
          .organizationId()
          .build();
      scheduledSyncScheduler.register(scheduledSyncRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e);
    }
  }

  public void unregisterScheduledSync(UnregisterScheduledSyncRequest request,
      StreamObserver<UnregisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncRequest scheduledSyncRequest = ScheduledSyncRequest.builder()
          .banksaladUserId()
          .orgCode()
          .organizationId()
          .build();
      scheduledSyncScheduler.unregister(scheduledSyncRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e);
    }
  }
}
