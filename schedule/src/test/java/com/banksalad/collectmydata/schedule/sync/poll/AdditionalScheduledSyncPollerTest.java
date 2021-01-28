package com.banksalad.collectmydata.schedule.sync.poll;

import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
class AdditionalScheduledSyncPollerTest {

  @Mock
  private ScheduledSyncRepository scheduledSyncRepository;

  @Mock
  private ScheduledSyncKafkaTemplate scheduledSyncKafkaTemplate;

  @InjectMocks
  private AdditionalScheduledSyncPoller additionalScheduledSyncPoller;

  // TODO : - 문제 : ShortScheduledSyncPoller 의 cron schedule time 을 조작해야, 다음 테스트 코드가 성공
  //        - 해결 : 조작하지 않아도 테스트 코드가 성공하도록 수정 필요
  @Test
  @Disabled
  public void whenWaitThirtySeconds_thenScheduledIsCalledAtLeastOneTimes() {
    await()
        .atMost(30, SECONDS)
        .untilAsserted(() -> then(additionalScheduledSyncPoller).should(times(1)).poll());
  }

  @Test
  public void givenScheduledSync_whenPolledFromDB_thenCalledKafkaTemplateSync() {
    // Given
    ScheduledSync scheduledSync01 = getScheduledSyncWith(1L);
    ScheduledSync scheduledSync02 = getScheduledSyncWith(2L);
    given(scheduledSyncRepository.findAllByIsDeletedEquals(FALSE)).willReturn(asList(scheduledSync01, scheduledSync02));

    // When
    additionalScheduledSyncPoller.poll();

    // Then
    then(scheduledSyncKafkaTemplate).should().sync(scheduledSync01);
    then(scheduledSyncKafkaTemplate).should().sync(scheduledSync02);
  }

  private ScheduledSync getScheduledSyncWith(Long id) {
    return ScheduledSync.builder()
        .scheduledSyncId(id)
        .banksaladUserId("123324")
        .sector("finance")
        .industry("card")
        .organizationId("shinhancard")
        .isDeleted(FALSE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
