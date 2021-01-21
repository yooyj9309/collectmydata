package com.banksalad.collectmydata.schedule.sync.poll;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.SyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.SyncRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SyncPoller {

  private final SyncRepository syncRepository;
  private final SyncTemplate syncKafkaTemplate;

  @Scheduled
  public void poll() {
    List<SyncEntity> syncs = syncRepository.findAll();
    for (SyncEntity syncTarget : syncs) {
      syncKafkaTemplate.sync(syncTarget);
    }
  }
}
