package com.banksalad.collectmydata.invest.common.db.entity;

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
public class ApiLogEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  @Column(name = "request_header_encrypted", columnDefinition = "MEDIUMTEXT")
  private String requestHeader;

  @Column(name = "request_body_encrypted", columnDefinition = "MEDIUMTEXT")
  private String requestBody;

  @Column(name = "transformed_request_header_encrypted", columnDefinition = "MEDIUMTEXT")
  private String transformedRequestHeader;

  @Column(name = "transformed_request_body_encrypted", columnDefinition = "MEDIUMTEXT")
  private String transformedRequestBody;

  private String resultCode;

  private String resultMessage;

  private String responseCode;

  @Column(name = "response_header_encrypted", columnDefinition = "MEDIUMTEXT")
  private String responseHeader;

  @Column(name = "response_body_encrypted", columnDefinition = "MEDIUMTEXT")
  private String responseBody;

  @Column(name = "transformed_response_header_encrypted", columnDefinition = "MEDIUMTEXT")
  private String transformedResponseHeader;

  @Column(name = "transformed_response_body_encrypted", columnDefinition = "MEDIUMTEXT")
  private String transformedResponseBody;

  @Column(nullable = false)
  private LocalDateTime requestDtime;

  private LocalDateTime responseDtime;

  private Long elapsedTime;
}
