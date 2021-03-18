package com.banksalad.collectmydata.schedule.common.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static javax.persistence.GenerationType.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "scheduled_sync")
public class ScheduledSyncEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long scheduledSyncId;

  private Long banksaladUserId;

  private String sector;

  private String industry;

  private String organizationId;

  private Boolean isDeleted;
}
