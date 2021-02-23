package com.banksalad.collectmydata.collect.grpc.handler;

import com.banksalad.collectmydata.collect.sync.CollectSyncService;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.DeleteAllSyncStatusRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.DeleteAllSyncStatusResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.DeleteSyncStatusRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.DeleteSyncStatusResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.GetSyncStatusRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataProto.GetSyncStatusResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceBankProto.SyncFinanceBankRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceBankProto.SyncFinanceBankResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCapitalProto.SyncFinanceCapitalRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCapitalProto.SyncFinanceCapitalResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCardProto.SyncFinanceCardRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCardProto.SyncFinanceCardResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceEfinProto.SyncFinanceEfinRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceEfinProto.SyncFinanceEfinResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInsuProto.SyncFinanceInsuRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInsuProto.SyncFinanceInsuResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInvestProto.SyncFinanceInvestRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInvestProto.SyncFinanceInvestResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CollectmydataCollectGrpcService extends CollectmydataGrpc.CollectmydataImplBase {

  private final CollectSyncService collectSyncService;

  public CollectmydataCollectGrpcService(CollectSyncService collectSyncService) {
    this.collectSyncService = collectSyncService;
  }

  @Override
  public void syncFinanceBank(SyncFinanceBankRequest request, StreamObserver<SyncFinanceBankResponse> responseObserver) {

//    collectSyncService.syncFinanceBank(request);
//    responseObserver.onCompleted();
  }

  @Override
  public void syncFinanceCard(SyncFinanceCardRequest request, StreamObserver<SyncFinanceCardResponse> responseObserver) {

  }

  @Override
  public void syncFinanceInvest(SyncFinanceInvestRequest request, StreamObserver<SyncFinanceInvestResponse> responseObserver) {

  }

  @Override
  public void syncFinanceInsu(SyncFinanceInsuRequest request, StreamObserver<SyncFinanceInsuResponse> responseObserver) {

  }

  @Override
  public void syncFinanceEfin(SyncFinanceEfinRequest request, StreamObserver<SyncFinanceEfinResponse> responseObserver) {

  }

  @Override
  public void syncFinanceCapital(SyncFinanceCapitalRequest request, StreamObserver<SyncFinanceCapitalResponse> responseObserver) {
    super.syncFinanceCapital(request, responseObserver);
  }

  @Override
  public void getSyncStatus(GetSyncStatusRequest request, StreamObserver<GetSyncStatusResponse> responseObserver) {
    super.getSyncStatus(request, responseObserver);
  }

  @Override
  public void deleteSyncStatus(DeleteSyncStatusRequest request, StreamObserver<DeleteSyncStatusResponse> responseObserver) {

  }

  @Override
  public void deleteAllSyncStatus(DeleteAllSyncStatusRequest request,
      StreamObserver<DeleteAllSyncStatusResponse> responseObserver) {

  }
}
