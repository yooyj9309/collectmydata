package com.banksalad.collectmydata.schedule.common.db.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ScheduledSync {

  @Id
  private Long scheduledSyncId;
}
