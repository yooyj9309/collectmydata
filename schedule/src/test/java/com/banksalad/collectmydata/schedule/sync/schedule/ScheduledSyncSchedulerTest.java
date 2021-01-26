package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("ScheduledSyncScheduler Test")
class ScheduledSyncSchedulerTest {

  @Autowired
  private ScheduledSyncScheduler scheduledSyncScheduler;

  @Autowired
  private ScheduledSyncRepository scheduledSyncRepository;

  @Test
  @DisplayName("Test for Sync Target Saving in DB")
  void givenScheduledSyncRequest_whenRegisterScheduledSyncRequest_thenSavedInDatabase() {
    // Given
    ScheduledSyncRequest scheduledSyncRequest = getScheduledSyncRequest();

    // When
    scheduledSyncScheduler.register(scheduledSyncRequest);

    // Then
    ScheduledSync savedScheduledSync = scheduledSyncRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
    assertNotNull(savedScheduledSync);
    assertEquals(1, savedScheduledSync.getScheduledSyncId());
    assertEquals(FALSE, savedScheduledSync.getIsDeleted());
    assertEquals(scheduledSyncRequest.getBanksaladUserId(), savedScheduledSync.getBanksaladUserId());
    assertEquals(scheduledSyncRequest.getSector(), savedScheduledSync.getSector());
    assertEquals(scheduledSyncRequest.getIndustry(), savedScheduledSync.getIndustry());
    assertEquals(scheduledSyncRequest.getOrganizationId(), savedScheduledSync.getOrganizationId());
    assertNotNull(savedScheduledSync.getCreatedAt());
    assertNotNull(savedScheduledSync.getUpdatedAt());
  }

  private ScheduledSyncRequest getScheduledSyncRequest() {
    return ScheduledSyncRequest.builder()
        .banksaladUserId("1234")
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .build();
  }
}
