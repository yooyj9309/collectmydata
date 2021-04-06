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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncGrpcService extends CollectscheduleImplBase {

  private final ScheduledSyncRepository scheduledSyncRepository;

  public void registerScheduledSync(RegisterScheduledSyncRequest request,
      StreamObserver<RegisterScheduledSyncResponse> responseObserver) {
    try {
      ScheduledSyncEntity scheduledSyncEntity = ScheduledSyncEntity.builder()
          .syncedAt(LocalDateTime.now())
          .banksaladUserId(Long.valueOf(request.getBanksaladUserId()))
          .sector(request.getSector())
          .industry(request.getIndustry())
          .organizationId(request.getOrganizationId())
          .consentId(request.getConsentId())
          .cycle(request.getCycle())
          .endDate(request.getEndDate())
          .build();

      scheduledSyncRepository.save(scheduledSyncEntity);

      responseObserver.onNext(RegisterScheduledSyncResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Register Scheduled Sync Error: {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  public void unregisterScheduledSync(UnregisterScheduledSyncRequest request,
      StreamObserver<UnregisterScheduledSyncResponse> responseObserver) {
    try {
      scheduledSyncRepository
          .deleteByBanksaladUserIdAndSectorAndIndustryAndOrganizationIdAndConsentId(
              Long.valueOf(request.getBanksaladUserId()), request.getSector(),
              request.getIndustry(), request.getOrganizationId(), request.getConsentId());

      responseObserver.onNext(UnregisterScheduledSyncResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Unregister Scheduled Sync Error: {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void healthCheck(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    try {
      responseObserver.onNext(HealthCheckResponse.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Health Check Error: {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }
}
