package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.schedule.ScheduledSyncServiceImpl;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
class ScheduledSyncServiceImplTest {

  @Mock
  private ScheduledSyncRepository scheduledSyncRepository;

  @Mock
  private ScheduledSyncMessageService scheduledSyncMessageService;

  @InjectMocks
  private ScheduledSyncServiceImpl scheduledSyncService;

  @Test
  void givenScheduledSync_whenSyncBasic_thenProduce() {
    // Given
    ScheduledSyncEntity scheduledSyncEntity01 = getScheduledSyncEntityWith(1L);
    ScheduledSyncEntity scheduledSyncEntity02 = getScheduledSyncEntityWith(2L);
    given(scheduledSyncRepository.findAll())
        .willReturn(asList(scheduledSyncEntity01, scheduledSyncEntity02));

    // When
    scheduledSyncService.syncBasic();

    // Then
    then(scheduledSyncMessageService).should(times(2)).produceSyncRequest(any(), any());
  }

  private ScheduledSyncEntity getScheduledSyncEntityWith(Long id) {
    return ScheduledSyncEntity.builder()
        .id(id)
        .banksaladUserId(123324L)
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .build();
  }
}
