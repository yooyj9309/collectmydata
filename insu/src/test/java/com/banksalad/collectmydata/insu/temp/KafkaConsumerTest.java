package com.banksalad.collectmydata.insu.temp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.kafka.test.utils.KafkaTestUtils.*;

@EmbeddedKafka
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("KafkaConsumer Test")
class KafkaConsumerTest {

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @MockBean
  private InsuService insuService;

  private static final String TOPIC = "collect-mydata-insurance";

  private KafkaTemplate<Integer, String> kafkaTemplate;

  @BeforeEach
  void setUp() throws InterruptedException {
    this.kafkaTemplate = getKafkaTemplate();
    Thread.sleep(2000);
  }

  @Test
  @DisplayName("Kafka Message Consume Test")
  void givenTestMessage_whenProduced_thenConsumed() {
    // Given
    String message = "test-message";

    // When
    kafkaTemplate.send(TOPIC, message);

    // Then
    await()
        .atMost(5, SECONDS)
        .untilAsserted(() -> then(insuService).should(times(1)).sync(message));
  }

  private KafkaTemplate<Integer, String> getKafkaTemplate() {
    Map<String, Object> produceProps = producerProps(embeddedKafka);
    ProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(produceProps);
    KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
    kafkaTemplate.setDefaultTopic(TOPIC);

    return kafkaTemplate;
  }
}
