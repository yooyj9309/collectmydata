package com.banksalad.collectmydata.schedule.sync.poll;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledSyncKafkaTemplate implements ScheduledSyncTemplate {

  private final KafkaTemplate<Integer, String> template;
  private static final String TOPIC = "test-topic";

  @Override
  public void sync(ScheduledSync scheduledSync) {
    template
        .send(TOPIC, "test-message")
        .addCallback(new ListenableFutureCallback<>() {
          @Override
          public void onSuccess(SendResult<Integer, String> result) {
            log.info("Produce Success, Result : {}", result);
          }

          @Override
          public void onFailure(Throwable ex) {
            log.error("Produce Fail, Exception : {}", ex.getMessage());
          }
        });
  }
}
