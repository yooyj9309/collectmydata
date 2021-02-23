package com.banksalad.collectmydata.referencebank.common.db.entity;

import com.banksalad.collectmydata.referencebank.common.db.converter.ApiLogEncryptConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
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

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String requestHeaderEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String requestBodyEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedRequestHeaderEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedRequestBodyEncrypted;

  private String resultCode;

  private String resultMessage;

  private String responseCode;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String responseHeaderEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String responseBodyEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedResponseHeaderEncrypted;

  @Convert(converter = ApiLogEncryptConverter.class)
  @Column(columnDefinition = "MEDIUMTEXT")
  private String transformedResponseBodyEncrypted;

  @Column(nullable = false)
  private LocalDateTime requestDatetime;

  private LocalDateTime responseDatetime;
}
