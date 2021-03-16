package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.OauthTokenService;
import com.banksalad.collectmydata.finance.common.service.OrganizationService;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryRequestHelper;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryResponseHelper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelecomApiServiceImple implements TelecomApiService {

  private final OrganizationService organizationService;
  private final OauthTokenService oauthTokenService;

  private final TelecomSummaryRequestHelper telecomSummaryRequestHelper;
  private final TelecomSummaryResponseHelper telecomSummaryResponseHelper;

  private final SummaryService<ListTelecomSummariesRequest, TelecomSummary> telecomSummaryService;

  @Override
  public TelecomApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    // TODO: Use a real Grpc client implementation
    //  OauthToken oauthToken = oauthTokenService.getOauthToken(banksaladUserId, organizationId);
    OauthToken oauthToken = OauthToken.builder()
        .accessToken("xxx.yyy.zzz")
        .build();
    // TODO: Use a real Grpc client implementation
    //  Organization organization = organizationService.getOrganizationById(organizationId);
    Organization organization = Organization.builder()
        .organizationCode("020")
        .hostUrl("http://localhost:9090")
        .build();

    // Make an execution context
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .build();

    // 6.9.1: 통신 계약 목록 조회
    telecomSummaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);

    // TODO: 6.9.2 - 6.9.4

    return null;
  }
}
