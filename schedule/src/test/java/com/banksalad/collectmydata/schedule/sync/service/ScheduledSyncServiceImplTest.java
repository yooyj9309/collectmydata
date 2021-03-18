package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.schedule.ScheduledSyncService;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.banksalad.collectmydata.schedule.common.enums.SyncType.ADDITIONAL;
import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@Disabled
class ScheduledSyncServiceImplTest {

  @Mock
  private ScheduledSyncRepository scheduledSyncRepository;

  @Mock
  private ScheduledSyncMessageServiceImpl scheduledSyncMessageService;

  @InjectMocks
  private ScheduledSyncService scheduledSyncService;

  @Test
  void givenScheduledSync_whenPolledFromDB_thenCalledKafkaTemplateSync() {
    // Given
    ScheduledSyncEntity scheduledSyncEntity01 = getScheduledSyncEntityWith(1L);
    ScheduledSyncEntity scheduledSyncEntity02 = getScheduledSyncEntityWith(2L);
    given(scheduledSyncRepository.findAllByIsDeletedEquals(FALSE))
        .willReturn(asList(scheduledSyncEntity01, scheduledSyncEntity02));

    // When
    scheduledSyncService.syncAdditional();

    // Then
    then(scheduledSyncMessageService).should().produce(scheduledSyncEntity01, ADDITIONAL);
    then(scheduledSyncMessageService).should().produce(scheduledSyncEntity02, ADDITIONAL);
  }

  private ScheduledSyncEntity getScheduledSyncEntityWith(Long id) {
    return ScheduledSyncEntity.builder()
        .scheduledSyncId(id)
        .banksaladUserId(123324L)
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .isDeleted(FALSE)
        .build();
  }
}
