package com.banksalad.collectmydata.insu.temp;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

  private final InsuService insuService;
  private static final String TOPIC = "collect-mydata-insurance";

  @KafkaListener(topics = TOPIC)
  public void consume(String data, Acknowledgment acknowledgment) {
    log.info("Consumed Data : {}", data);

    insuService.sync(data); // TODO : Insurance sync logic will be added

    acknowledgment.acknowledge();
  }
}
