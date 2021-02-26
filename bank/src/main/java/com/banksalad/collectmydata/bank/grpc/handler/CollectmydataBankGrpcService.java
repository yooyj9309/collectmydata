package com.banksalad.collectmydata.bank.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.UserSyncStatus;
import com.banksalad.collectmydata.bank.common.dto.UserSyncStatusResponse;
import com.banksalad.collectmydata.bank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.common.logging.CollectLogbackJsonLayout;
import com.github.banksalad.idl.daas.v1.collect.bank.BankGrpc;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.DeleteAllSyncStatusRequest;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.DeleteAllSyncStatusResponse;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.DeleteSyncStatusRequest;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.DeleteSyncStatusResponse;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.GetSyncStatusRequest;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.GetSyncStatusResponse;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.HealthCheckRequest;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.HealthCheckResponse;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.SyncUserRequest;
import com.github.banksalad.idl.daas.v1.collect.bank.BankProto.SyncUserResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class CollectmydataBankGrpcService extends BankGrpc.BankImplBase {

  private final UserSyncStatusService userSyncStatusService;

  public CollectmydataBankGrpcService(
      UserSyncStatusService userSyncStatusService
  ) {
    this.userSyncStatusService = userSyncStatusService;
  }

  @Override
  public void healthCheck(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    Mono.just(HealthCheckResponse.newBuilder().build())
        .subscribe(
            response -> responseObserver.onNext(response),
            cause -> {
              log.error("health check error: {}", cause.getMessage(), cause);
              responseObserver.onError(cause);
            },
            () -> responseObserver.onCompleted()
        );
  }

  @Override
  public void syncUser(SyncUserRequest request, StreamObserver<SyncUserResponse> responseObserver) {
    // TODO : implement
  }

  @Override
  public void getSyncStatus(GetSyncStatusRequest request, StreamObserver<GetSyncStatusResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getUserId());
      String organizationId = "organizationId";

      MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
      MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);

      UserSyncStatus userSyncStatus = userSyncStatusService
          .getUserSyncStatus(banksaladUserId, "organizationId", "api_id");

      UserSyncStatusResponse userSyncStatusResponse = UserSyncStatusResponse.builder()
          .userSyncStatuses(List.of(userSyncStatus))
          .build();

      responseObserver.onNext(userSyncStatusResponse.toSyncStatusResponseProto());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e);
    } finally {
      MDC.clear();
    }
  }

  @Override
  public void deleteSyncStatus(DeleteSyncStatusRequest request,
      StreamObserver<DeleteSyncStatusResponse> responseObserver) {

    // TODO : implement
  }

  @Override
  public void deleteAllSyncStatus(DeleteAllSyncStatusRequest request,
      StreamObserver<DeleteAllSyncStatusResponse> responseObserver) {

    // TODO : implement
  }
}
