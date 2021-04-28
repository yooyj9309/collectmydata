package com.banksalad.collectmydata.collect.grpc.handler;

import com.banksalad.collectmydata.collect.common.service.CollectMessageService;
import com.banksalad.collectmydata.collect.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.collect.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;

import org.springframework.util.StringUtils;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.HealthCheckRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.HealthCheckResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.SyncCollectmydatabankRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.SyncCollectmydatabankResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.SyncCollectmydatacardRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.SyncCollectmydatacardResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.SyncCollectmydatainvestRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.SyncCollectmydatainvestResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.UUID;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class CollectmydataCollectGrpcService extends CollectmydataGrpc.CollectmydataImplBase {

  private final ConnectClientService connectClientService;
  private final CollectMessageService collectMessageService;

  @Override
  public void healthCheck(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    try {
      HealthCheckResponse response = HealthCheckResponse.newBuilder().build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("health check error: {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void syncCollectmydatabank(SyncCollectmydatabankRequest request,
      StreamObserver<SyncCollectmydatabankResponse> responseObserver) {

    try {
      GetOrganizationResponse getOrganizationResponse = connectClientService
          .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid());

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = getOrganizationResponse.getOrganizationId();
      String syncRequestId =
          StringUtils.hasLength(request.getSyncRequestId()) ? request.getSyncRequestId() : UUID.randomUUID().toString();

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.BANK.name(), banksaladUserId, organizationId, syncRequestId);

      log.info("CollectmydataCollectGrpcService.syncCollectmydatabank");

      collectMessageService.produceBankSyncRequested(SyncRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .syncRequestType(SyncRequestType.ONDEMAND)
          .build());

      responseObserver.onNext(SyncCollectmydatabankResponse.newBuilder().build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("syncCollectmydatabank error: {}", e.getMessage(), e);
      responseObserver.onError(e);

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void syncCollectmydatacard(SyncCollectmydatacardRequest request,
      StreamObserver<SyncCollectmydatacardResponse> responseObserver) {

    try {
      GetOrganizationResponse getOrganizationResponse = connectClientService
          .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid());

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = getOrganizationResponse.getOrganizationId();
      String syncRequestId =
          StringUtils.hasLength(request.getSyncRequestId()) ? request.getSyncRequestId() : UUID.randomUUID().toString();

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.CARD.name(), banksaladUserId, organizationId, syncRequestId);

      collectMessageService.produceCardSyncRequested(SyncRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .syncRequestType(SyncRequestType.ONDEMAND)
          .build());

    } catch (Exception e) {
      log.error("syncCollectmydatacard error : {}", e.getMessage(), e);
      responseObserver.onError(e);

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  @Override
  public void syncCollectmydatainvest(SyncCollectmydatainvestRequest request,
      StreamObserver<SyncCollectmydatainvestResponse> responseObserver) {

    try {
      GetOrganizationResponse getOrganizationResponse = connectClientService
          .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid());

      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = getOrganizationResponse.getOrganizationId();
      String syncRequestId =
          StringUtils.hasLength(request.getSyncRequestId()) ? request.getSyncRequestId() : UUID.randomUUID().toString();

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INVEST.name(), banksaladUserId, organizationId, syncRequestId);

      collectMessageService.produceInvestSyncRequested(SyncRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .syncRequestType(SyncRequestType.ONDEMAND)
          .build());

      responseObserver.onNext(SyncCollectmydatainvestResponse.newBuilder().build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("syncCollectmydatainvest error: {}", e.getMessage(), e);
      responseObserver.onError(e);

    } finally {
      LoggingMdcUtil.clear();
    }
  }
}
