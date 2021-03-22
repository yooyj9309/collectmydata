package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.banksalad.collectmydata.schedule.ScheduledSyncServiceImpl;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

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

  @Mock
  Page<ScheduledSyncEntity> scheduledSyncEntityPage01;

  @Mock
  Page<ScheduledSyncEntity> scheduledSyncEntityPage02;

  @InjectMocks
  private ScheduledSyncServiceImpl scheduledSyncService;

  @Test
  void givenScheduledSync_whenSyncBasic_thenProduce() {
    // Given
    ScheduledSyncEntity scheduledSyncEntity01 = getScheduledSyncEntityWith(1L);
    ScheduledSyncEntity scheduledSyncEntity02 = getScheduledSyncEntityWith(2L);

    given(scheduledSyncRepository.findAll(PageRequest.of(0, 100))).willReturn(scheduledSyncEntityPage01);
    given(scheduledSyncEntityPage01.getContent()).willReturn(asList(scheduledSyncEntity01, scheduledSyncEntity02));
    given(scheduledSyncRepository.findAll(PageRequest.of(1, 100))).willReturn(scheduledSyncEntityPage02);
    given(scheduledSyncEntityPage02.getContent()).willReturn(Collections.emptyList());

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
