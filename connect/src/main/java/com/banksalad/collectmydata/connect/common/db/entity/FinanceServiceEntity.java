package com.banksalad.collectmydata.connect.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "service")
public class FinanceServiceEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long serviceId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String serviceName;

  @Column(nullable = false)
  private String clientId;

  private String clientSecret;

  private String redirectUri;
}
