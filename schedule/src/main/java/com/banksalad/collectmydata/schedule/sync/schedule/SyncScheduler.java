package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.SyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.SyncRepository;
import com.banksalad.collectmydata.schedule.sync.dto.SyncRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SyncScheduler {

  private final SyncCalculator syncCalculator;
  private final SyncRepository syncRepository;

  public void register(SyncRequest syncRequest) {
    SyncEntity sync = syncCalculator.calculate(syncRequest);
    syncRepository.save(sync);
  }

  public void unregister(SyncRequest syncRequest) {
    syncRepository.deleteById(Long.valueOf(syncRequest.getRequestId()));
  }
}
