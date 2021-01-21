package com.banksalad.collectmydata.schedule.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.schedule.sync.dto.SyncRequest;
import com.banksalad.collectmydata.schedule.sync.schedule.SyncScheduler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SyncGrpcHandler extends CollectScheduleGrpc.CollectscheduleImplBase implements SyncHandler {

  private final SyncScheduler syncScheduler;

  public void registerScheduledSync(RegisterScheduledSyncRequest request,
      StreamObserver<RegisterScheduledSyncResponse> responseObserver) {
    try {
      SyncRequest syncRequest = SyncRequest.builder()
          .banksaladUserId()
          .orgCode()
          .organizationId()
          .requestId()
          .build();

      syncScheduler.register(syncRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e);
    }
  }

  public void unregisterScheduledSync(UnregisterScheduledSyncRequest request,
      StreamObserver<UnregisterScheduledSyncResponse> responseObserver) {
    try {
      SyncRequest syncRequest = SyncRequest.builder()
          .banksaladUserId()
          .orgCode()
          .organizationId()
          .requestId()
          .build();

      syncScheduler.unregister(syncRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e);
    }
  }
}
