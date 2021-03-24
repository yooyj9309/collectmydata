package com.banksalad.collectmydata.connect.common.db.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {

  @EqualsExclude
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @EqualsExclude
  private String createdBy;

  @EqualsExclude
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @EqualsExclude
  private String updatedBy;
}
