package com.banksalad.collectmydata.card;


import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.card.accountInfo.CardAccountInfoPublishmentHelper;
import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.GetCardBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListPaymentsRequest;
import com.banksalad.collectmydata.card.card.dto.ListPointsRequest;
import com.banksalad.collectmydata.card.card.dto.ListRevolvingsRequest;
import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.card.userbase.LoanLongTermPublishmentHelper;
import com.banksalad.collectmydata.card.card.userbase.LoanShortTermPublishmentHelper;
import com.banksalad.collectmydata.card.card.userbase.LoanSummaryPublishmentHelper;
import com.banksalad.collectmydata.card.card.userbase.PaymentPublishmentHelper;
import com.banksalad.collectmydata.card.card.userbase.PointPublishmentHelper;
import com.banksalad.collectmydata.card.card.userbase.RevolvingPublishmentHelper;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryRequest;
import com.banksalad.collectmydata.card.loan.dto.ListLoanLongTermsRequest;
import com.banksalad.collectmydata.card.loan.dto.ListLoanShortTermsRequest;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.card.summary.CardSummaryPublishmentHelper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardApiServiceImpl implements CardApiService {

  private final FinanceMessageService financeMessageService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;

  // Summary 6.3.1
  private final SummaryService<ListCardSummariesRequest, CardSummary> accountSummaryService;
  private final SummaryRequestHelper<ListCardSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<CardSummary> summaryResponseHelper;
  private final CardSummaryPublishmentHelper cardSummaryPublishmentHelper;

  // Card Basic 6.3.2
  private final AccountInfoService<CardSummary, GetCardBasicRequest, CardBasic> accountInfoService;
  private final AccountInfoRequestHelper<GetCardBasicRequest, CardSummary> accountInfoRequestHelper;
  private final AccountInfoResponseHelper<CardSummary, CardBasic> accountInfoResponseHelper;
  private final CardAccountInfoPublishmentHelper cardAccountInfoPublishmentHelper;

  // Point 6.3.3
  private final UserBaseService<ListPointsRequest, List<Point>> pointUserBaseService;
  private final UserBaseRequestHelper<ListPointsRequest> pointRequestHelper;
  private final UserBaseResponseHelper<List<Point>> pointResponseHelper;
  private final PointPublishmentHelper pointPublishmentHelper;

  // Payment 6.3.6
  private final UserBaseService<ListPaymentsRequest, List<Payment>> paymentUserBaseService;
  private final UserBaseRequestHelper<ListPaymentsRequest> paymentRequestHelper;
  private final UserBaseResponseHelper<List<Payment>> paymentResponseHelper;
  private final PaymentPublishmentHelper paymentPublishmentHelper;

  // LoanSummary 6.3.9
  private final UserBaseService<GetLoanSummaryRequest, LoanSummary> loanSummaryUserBaseService;
  private final UserBaseRequestHelper<GetLoanSummaryRequest> loanSummaryRequestHelper;
  private final UserBaseResponseHelper<LoanSummary> loanSummaryResponseHelper;
  private final LoanSummaryPublishmentHelper loanSummaryPublishmentHelper;

  // Revolving 6.3.10
  private final UserBaseService<ListRevolvingsRequest, List<Revolving>> revolvingUserBaseService;
  private final UserBaseRequestHelper<ListRevolvingsRequest> revolvingRequestHelper;
  private final UserBaseResponseHelper<List<Revolving>> revolvingResponseHelper;
  private final RevolvingPublishmentHelper revolvingPublishmentHelper;

  // LoanShortTerm 6.3.11
  private final UserBaseService<ListLoanShortTermsRequest, List<LoanShortTerm>> loanShortTermUserBaseService;
  private final UserBaseRequestHelper<ListLoanShortTermsRequest> loanShortTermsRequestHelper;
  private final UserBaseResponseHelper<List<LoanShortTerm>> loanShortTermResponseHelper;
  private final LoanShortTermPublishmentHelper loanShortTermPublishmentHelper;

  // LoanLongTerm 6.3.12
  private final UserBaseService<ListLoanLongTermsRequest, List<LoanLongTerm>> loanLongTermUserBaseService;
  private final UserBaseRequestHelper<ListLoanLongTermsRequest> loanLongTermsRequestHelper;
  private final UserBaseResponseHelper<List<LoanLongTerm>> loanLongTermResponseHelper;
  private final LoanLongTermPublishmentHelper loanLongTermPublishmentHelper;

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
        .requestedBy(String.valueOf(banksaladUserId))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_card_summaries,
        summaryRequestHelper, summaryResponseHelper, cardSummaryPublishmentHelper);

    // TODO (hyujun) : summary 외에 나머지 api 추가.
    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(() -> accountInfoService.listAccountInfos(executionContext, Executions.finance_card_basic,
                accountInfoRequestHelper, accountInfoResponseHelper, cardAccountInfoPublishmentHelper)
            ).handle(this::handleException),

        CompletableFuture
            .runAsync(() -> pointUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_card_point, pointRequestHelper,
                    pointResponseHelper, pointPublishmentHelper)).handle(this::handleException),

        CompletableFuture
            .runAsync(() -> paymentUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_card_payment, paymentRequestHelper,
                    paymentResponseHelper, paymentPublishmentHelper)).handle(this::handleException),

        CompletableFuture.runAsync(() -> loanSummaryUserBaseService
            .getUserBaseInfo(executionContext, Executions.finance_loan_summary, loanSummaryRequestHelper,
                loanSummaryResponseHelper, loanSummaryPublishmentHelper)),

        // FIXME : 6.3.9에서 true일 경우만 아래 3개 api 실행해야함
        CompletableFuture.runAsync(() -> revolvingUserBaseService
            .getUserBaseInfo(executionContext, Executions.finance_loan_revolvings, revolvingRequestHelper,
                revolvingResponseHelper, revolvingPublishmentHelper)),

        CompletableFuture.runAsync(() -> loanShortTermUserBaseService
            .getUserBaseInfo(executionContext, Executions.finance_loan_short_terms, loanShortTermsRequestHelper,
                loanShortTermResponseHelper, loanShortTermPublishmentHelper)),

        CompletableFuture.runAsync(() -> loanLongTermUserBaseService
            .getUserBaseInfo(executionContext, Executions.finance_loan_long_terms, loanLongTermsRequestHelper,
                loanLongTermResponseHelper, loanLongTermPublishmentHelper))
    ).join();

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

  private Object handleException(Void v, Throwable t) {
    if (t != null) {
      log.error(t.getMessage());
    }
    return v;
  }
}
