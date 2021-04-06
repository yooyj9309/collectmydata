package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import com.banksalad.collectmydata.schedule.ScheduledSyncServiceImpl;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@EmbeddedKafka
@SpringBootTest
@ActiveProfiles("test")
class ScheduledSyncServicePagingTest {

  @Autowired
  private ScheduledSyncRepository scheduledSyncRepository;

  @Autowired
  private ScheduledSyncServiceImpl scheduledSyncService;

  @Test
  void givenSavedScheduledSyncRecords_whenSyncBasic_thenGetDataThreeTimes() {
    // Given
    for (int i = 1; i <= 201; i++) {
      scheduledSyncRepository.save(getScheduledSyncEntityWith((long) i));
    }

    // When
    scheduledSyncService.syncBasic();

    // Then
    // selected records id : 1 - 100
    // selected records id : 101 - 200
    // selected records id : 201
  }

  private ScheduledSyncEntity getScheduledSyncEntityWith(Long id) {
    return ScheduledSyncEntity.builder()
        .id(id)
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(123324L)
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .consentId("11")
        .cycle("cycle")
        .endDate("20210401")
        .build();
  }
}
