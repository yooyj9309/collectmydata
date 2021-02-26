package com.banksalad.collectmydata.bank.common.db.entity;

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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "api_log")
public class ApiLogEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long apiLogId;

  private String syncRequestId;

  private String executionRequestId;

  private String apiRequestId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String apiId;

  @Column(nullable = false)
  private String organizationApiId;

  @Column(nullable = false)
  private String requestUrl;

  @Column(nullable = false)
  private String httpMethod;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String requestHeaderEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String requestBodyEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedRequestHeaderEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedRequestBodyEncrypted;

  private String resultCode;

  private String resultMessage;

  private String responseCode;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String responseHeaderEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String responseBodyEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedResponseHeaderEncrypted;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedResponseBodyEncrypted;

  @Column(nullable = false)
  private LocalDateTime requestDatetime;

  private LocalDateTime responseDatetime;
}
