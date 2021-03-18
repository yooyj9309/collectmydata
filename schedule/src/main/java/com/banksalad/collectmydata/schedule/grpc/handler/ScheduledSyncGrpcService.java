package com.banksalad.collectmydata.schedule.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.HealthCheckResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleImplBase;
import io.grpc.stub.StreamObserver;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

// TODO : - How to validate request parameters
//        - RegisterScheduledSyncRequest, UnregisterScheduledSyncRequest
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncGrpcService extends CollectscheduleImplBase {

  private final ScheduledSyncRepository scheduledSyncRepository;

  public void registerScheduledSync(RegisterScheduledSyncRequest request,
      StreamObserver<RegisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncEntity scheduledSyncEntity = ScheduledSyncEntity.builder()
          .banksaladUserId(Long.valueOf(request.getBanksaladUserId()))
          .sector(request.getSector())
          .industry(request.getIndustry())
          .organizationId(request.getOrganizationId())
          .isDeleted(FALSE)
          .build();

      scheduledSyncRepository.save(scheduledSyncEntity);

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
      ScheduledSyncEntity scheduledSyncEntity = scheduledSyncRepository
          .findByBanksaladUserIdAndSectorAndIndustryAndOrganizationIdAndIsDeleted(
              request.getBanksaladUserId(), request.getSector(),
              request.getIndustry(), request.getOrganizationId(), FALSE)
          .orElseThrow(EntityNotFoundException::new);
      scheduledSyncEntity.setIsDeleted(TRUE);

      scheduledSyncRepository.save(scheduledSyncEntity);

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
