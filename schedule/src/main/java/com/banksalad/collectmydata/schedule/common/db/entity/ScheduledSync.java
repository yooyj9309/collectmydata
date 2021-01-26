package com.banksalad.collectmydata.schedule.common.db.entity;

import com.banksalad.collectmydata.schedule.common.enums.SyncType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSync {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long scheduledSyncId;

  private String banksaladUserId;

  private String sector;

  private String industry;

  private String organizationId;

  private Boolean isDeleted;

  @Transient
  private SyncType syncType;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
