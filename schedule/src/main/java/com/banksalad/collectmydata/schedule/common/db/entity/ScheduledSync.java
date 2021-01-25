package com.banksalad.collectmydata.schedule.common.db.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSync {

  @Id
  private Long scheduledSyncId;

  private String banksaladUserId;

  private String sector;

  private String industry;

  private String organizationId;
}
