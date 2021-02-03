package com.banksalad.collectmydata.schedule.common.db.entity;

import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@Setter
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

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public static ScheduledSync of(ScheduledSyncRequest request) {
    return ScheduledSync.builder()
        .banksaladUserId(request.getBanksaladUserId())
        .sector(request.getSector())
        .industry(request.getIndustry())
        .organizationId(request.getOrganizationId())
        .isDeleted(FALSE)
        .build();
  }

  public void disable() {
    this.isDeleted = TRUE;
  }
}
