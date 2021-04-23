package com.banksalad.collectmydata.collect.grpc.client;

import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatabankSyncedRequest;

public interface FinanceClientService {

  void notifyCollectmydatabankSynced(NotifyCollectmydatabankSyncedRequest request);
}
