package com.banksalad.collectmydata.collect.common.service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

@Slf4j
@Service
public class RedisPubSubServiceImpl implements RedisPubSubService {

  private static final int SUBSCRIBE_TIMEOUT_SEC = 60 * 5;
  private static final String COLLECT_SUBSCRIBE_KEY_POSTFIX = "collect";

  private final ObjectMapper objectMapper;
  private final RedisClient redisClient;

  public RedisPubSubServiceImpl(
      ObjectMapper objectMapper,
      RedisClient redisClient
  ) {
    this.objectMapper = objectMapper;
    this.redisClient = redisClient;
  }

  @Override
  public <T> Mono<T> subscribeSyncResponse(long banksaladUserId, String syncRequestId, Class<T> clazz) {

    final RedisPubSubReactiveCommands<String, String> commands = redisClient.connectPubSub().reactive();

    String subscribeKey = generateSubscribeKey.apply(String.valueOf(banksaladUserId), syncRequestId);
    log.info("collect pubsub subscribe: {}", subscribeKey);

    return commands.subscribe(subscribeKey)
        .thenMany(
            commands.observeChannels()
                .map(message -> mapPayloadToSyncResponseMessage(message.getMessage(), clazz))
        )
        .take(Duration.ofSeconds(SUBSCRIBE_TIMEOUT_SEC))
        .next()
        .doFinally(signalType -> {
          commands.unsubscribe(subscribeKey).subscribe();
          log.debug("Collect sync done: {}", subscribeKey);
        });
  }

  @Override
  public void publishSyncCompleted(long banksaladUserId, String syncRequestId, String message) {

    try {
      final RedisPubSubReactiveCommands<String, String> commands = redisClient.connectPubSub().reactive();
      String subscribeKey = generateSubscribeKey.apply(String.valueOf(banksaladUserId), syncRequestId);

      log.info("Collect subscribe: {}", subscribeKey);

      /* publish event */
      log.info("collect pubsub publish: {}", subscribeKey);
      commands.publish(subscribeKey, message).toFuture().get();

    } catch (InterruptedException | ExecutionException e) {
      throw new CollectRuntimeException("Fail to publish SyncCompleted", e);
    }
  }

  private final BiFunction<String, String, String> generateSubscribeKey = (banksalasUserId, syncRequestId) ->
      COLLECT_SUBSCRIBE_KEY_POSTFIX + ":" + banksalasUserId + ":" + syncRequestId;

  private <T> T mapPayloadToSyncResponseMessage(String payload, Class<T> clazz) {
    try {

      return objectMapper.readValue(payload, clazz);

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize SyncResponseMessage, payload={}", payload, e);
      throw new CollectRuntimeException("Fail to deserialize SyncResponseMessage", e);
    }
  }
}
