package com.banksalad.collectmydata.collect.grpc.handler;

import com.banksalad.collectmydata.collect.sync.CollectSyncService;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectmydataCollectGrpcService extends CollectmydataGrpc.CollectmydataImplBase {

  private final CollectSyncService collectSyncService;

  
}
