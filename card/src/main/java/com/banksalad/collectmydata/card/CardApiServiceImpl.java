package com.banksalad.collectmydata.card;


import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.summary.CardSummaryPublishmentHelper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardApiServiceImpl implements CardApiService {

  private final FinanceMessageService financeMessageService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;

  // SUMMARY
  private final SummaryService<ListCardSummariesRequest, CardSummary> accountSummaryService;
  private final SummaryRequestHelper<ListCardSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<CardSummary> summaryResponseHelper;
  private final CardSummaryPublishmentHelper cardSummaryPublishmentHelper;

  @Override
  public void requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    OauthToken oauthToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);

    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(oauthToken.getConsentId())
        .syncRequestId(syncRequestId)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_card_summaries,
        summaryRequestHelper, summaryResponseHelper, cardSummaryPublishmentHelper);

    // TODO (hyujun) : summary 외에 나머지 api 추가.

    /* produce syncCompleted */
    financeMessageService.produceSyncCompleted(
        MessageTopic.cardSyncCompleted,
        SyncCompletedMessage.builder()
            .banksaladUserId(executionContext.getBanksaladUserId())
            .organizationId(executionContext.getOrganizationId())
            .syncRequestId(executionContext.getSyncRequestId())
            .syncRequestType(syncRequestType)
            .build());
  }
}
