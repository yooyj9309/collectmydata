package com.banksalad.collectmydata.collect.common.service;

import reactor.core.publisher.Mono;

public interface RedisPubSubService {

  <T> Mono<T> subscribeSyncResponse(long banksaladUserId, String syncRequestId, Class<T> clazz);

  void publishSyncCompleted(long banksaladUserId, String syncRequestId, String message);
}
