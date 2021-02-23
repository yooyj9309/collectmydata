package com.banksalad.collectmydata.collect.sync;

import com.banksalad.collectmydata.collect.sync.dto.SyncFinanceBankRequest;
import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;

import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCapitalProto.SyncFinanceCapitalRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCardProto.SyncFinanceCardRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceEfinProto.SyncFinanceEfinRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInsuProto.SyncFinanceInsuRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInvestProto.SyncFinanceInvestRequest;
import reactor.core.publisher.Mono;

public interface CollectSyncService {

  Mono<SyncFinanceBankResponse> syncFinanceBank(Mono<SyncFinanceBankRequest> request);

  void syncFinanceCard(SyncFinanceCardRequest request);

  void syncFinanceInvest(SyncFinanceInvestRequest request);

  void syncFinanceInsu(SyncFinanceInsuRequest request);

  void syncFinanceEfin(SyncFinanceEfinRequest request);

  void syncFinanceCapital(SyncFinanceCapitalRequest request);

}
