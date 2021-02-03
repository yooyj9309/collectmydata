package com.banksalad.collectmydata.schedule.sync.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.banksalad.collectmydata.schedule.common.enums.SyncType.ADDITIONAL;
import static java.lang.Boolean.FALSE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ScheduledSyncKafkaTemplate Test")
class ScheduledSyncKafkaTemplateTest {

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Autowired
  private ScheduledSyncKafkaTemplate scheduledSyncKafkaTemplate;

  private BlockingQueue<ConsumerRecord<String, String>> records;

  private KafkaMessageListenerContainer<String, String> container;

  @BeforeEach
  void setUp() {
    records = new LinkedBlockingQueue<>();
    container = getKafkaMessageListenerContainer();
    container.setupMessageListener((MessageListener<String, String>) records::add);
    container.start();

    ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
  }

  @AfterEach
  void tearDown() {
    container.stop();
  }

  @Test
  @DisplayName("Kafka Produce Test")
  void givenScheduledSync_whenProduceToKafka_thenSavedToTopic() throws InterruptedException {
    // Given
    ScheduledSync scheduledSync = getScheduledSync();
    String topic = "collect-mydata-card";
    String message = "{\"banksaladUserId\":\"123324\",\"sector\":\"finance\",\"industry\":\"card\",\"organizationId\":\"shinhancard\",\"syncType\":\"ADDITIONAL\"}";

    // When
    scheduledSyncKafkaTemplate.sync(scheduledSync, ADDITIONAL);

    // Then
    ConsumerRecord<String, String> record = records.poll(2, SECONDS);
    assertNotNull(record);
    assertEquals(topic, record.topic());
    assertEquals(message, record.value());
  }

  private ScheduledSync getScheduledSync() {
    return ScheduledSync.builder()
        .scheduledSyncId(1L)
        .banksaladUserId("123324")
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .isDeleted(FALSE)
        .build();
  }

  private KafkaMessageListenerContainer<String, String> getKafkaMessageListenerContainer() {
    Map<String, Object> consumerConfigs = new HashMap<>(
        KafkaTestUtils.consumerProps("consumer", "false", embeddedKafka));

    DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
        consumerConfigs, new StringDeserializer(), new StringDeserializer());

    ContainerProperties containerProperties = new ContainerProperties("collect-mydata-card");

    return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
  }
}
