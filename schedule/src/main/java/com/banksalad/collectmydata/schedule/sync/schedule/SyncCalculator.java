package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.SyncEntity;
import com.banksalad.collectmydata.schedule.sync.dto.SyncRequest;

@Component
public class SyncCalculator {

  public SyncEntity calculate(SyncRequest syncRequest) {
    return new SyncEntity();
  }
}
