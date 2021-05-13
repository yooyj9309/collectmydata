package com.banksalad.collectmydata.card;


import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.card.transaction.ApprovalDomesticPublishmentHelper;
import com.banksalad.collectmydata.card.card.transaction.ApprovalOverseasPublishmentHelper;
import com.banksalad.collectmydata.card.card.accountInfo.CardAccountInfoPublishmentHelper;
import com.banksalad.collectmydata.card.card.bill.BillBasicPublishmentHelper;
import com.banksalad.collectmydata.card.card.bill.BillDetailPublishmentHelper;
import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.GetCardBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticRequest;
import com.banksalad.collectmydata.card.card.dto.ListApprovalOverseasRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
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
import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanSummaryRepository;
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
import com.banksalad.collectmydata.finance.api.bill.BillRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.BillService;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
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

  // billBasic 6.3.4
  private final BillService<ListBillBasicRequest, BillBasic, ListBillDetailRequest, BillDetail> billService;
  private final BillRequestHelper<ListBillBasicRequest> billBasicRequestHelper;
  private final BillResponseHelper<BillBasic> billBasicResponseHelper;
  private final BillBasicPublishmentHelper billBasicPublishmentHelper;

  // billDetail 6.3.5
  private final BillTransactionRequestHelper<ListBillDetailRequest, BillBasic> billDetailRequestHelper;
  private final BillTransactionResponseHelper<BillBasic, BillDetail> billDetailResponseHelper;
  private final BillDetailPublishmentHelper billDetailPublishmentHelper;

  // Payment 6.3.6
  private final UserBaseService<ListPaymentsRequest, List<Payment>> paymentUserBaseService;
  private final UserBaseRequestHelper<ListPaymentsRequest> paymentRequestHelper;
  private final UserBaseResponseHelper<List<Payment>> paymentResponseHelper;
  private final PaymentPublishmentHelper paymentPublishmentHelper;

  // ApprovalDomestic 6.3.7
  private final TransactionApiService<CardSummary, ListApprovalDomesticRequest, ApprovalDomestic> approvalDomesticService;
  private final TransactionRequestHelper<CardSummary, ListApprovalDomesticRequest> approvalDomesticRequestHelper;
  private final TransactionResponseHelper<CardSummary, ApprovalDomestic> approvalDomesticResponseHelper;
  private final ApprovalDomesticPublishmentHelper approvalDomesticPublishmentHelper;

  // ApprovalOverseas 6.3.8
  private final TransactionApiService<CardSummary, ListApprovalOverseasRequest, ApprovalOverseas> approvalOverseasService;
  private final TransactionRequestHelper<CardSummary, ListApprovalOverseasRequest> approvalOverseasRequestHelper;
  private final TransactionResponseHelper<CardSummary, ApprovalOverseas> approvalOverseasResponseHelper;
  private final ApprovalOverseasPublishmentHelper approvalOverseasPublishmentHelper;

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

  // LoanSummary 6.3.9 Repository
  private final LoanSummaryRepository loanSummaryRepository;

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

    /* 6.3.1 */
    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_card_summaries,
        summaryRequestHelper, summaryResponseHelper, cardSummaryPublishmentHelper);

    /** 6.3.4
     *
     *  6.3.5는 6.3.4가 있어야 가져올 수 있으니 비동기 요청 전 먼저 받아온다.
     *  @author hyunjun
     */
    List<BillBasic> billBasics = billService
        .listBills(executionContext, Executions.finance_card_bills, billBasicRequestHelper, billBasicResponseHelper,
            billBasicPublishmentHelper);

    /** 6.3.9
     *
     *  6.3.9를 요청하고 존재여부에 따라 6.3.10 / 6.3.11 / 6.3.12 요청 여부 결정.
     *  @author hyunjun
     */
    loanSummaryUserBaseService
        .getUserBaseInfo(executionContext, Executions.finance_loan_summary, loanSummaryRequestHelper,
            loanSummaryResponseHelper, loanSummaryPublishmentHelper);

    /* search loanSummary */
    LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).orElse(
            LoanSummaryEntity.builder().loanRevolving(false).loanShortTerm(false).loanLongTerm(false).build());

    CompletableFuture.allOf(
        /* 6.3.2 */
        CompletableFuture
            .runAsync(() -> accountInfoService.listAccountInfos(executionContext, Executions.finance_card_basic,
                accountInfoRequestHelper, accountInfoResponseHelper, cardAccountInfoPublishmentHelper)
            ).handle(this::handleException),

        /* 6.3.3 */
        CompletableFuture
            .runAsync(() -> pointUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_card_point, pointRequestHelper,
                    pointResponseHelper, pointPublishmentHelper)).handle(this::handleException),

        /* 6.3.5 */
        CompletableFuture.runAsync(() -> billService
            .listBillDetails(executionContext, Executions.finance_card_bills_detail, billBasics,
                billDetailRequestHelper,
                billDetailResponseHelper, billDetailPublishmentHelper)).handle(this::handleException),

        /* 6.3.6 */
        CompletableFuture
            .runAsync(() -> paymentUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_card_payment, paymentRequestHelper,
                    paymentResponseHelper, paymentPublishmentHelper)).handle(this::handleException),

        /* 6.3.7 */
        CompletableFuture.runAsync(() -> approvalDomesticService
            .listTransactions(executionContext, Executions.finance_card_approval_domestic,
                approvalDomesticRequestHelper, approvalDomesticResponseHelper, approvalDomesticPublishmentHelper))
            .handle(this::handleException),

        /* 6.3.8 */
        CompletableFuture.runAsync(
            () -> approvalOverseasService.listTransactions(executionContext, Executions.finance_card_approval_overseas,
                approvalOverseasRequestHelper, approvalOverseasResponseHelper, approvalOverseasPublishmentHelper))
            .handle(this::handleException),

        /** 6.3.10 / 6.3.11 / 6.3.12
         *
         *  6.3.9의 값들이 true인 경우만 실행
         *  @author : hyunjun
         */
        CompletableFuture.runAsync(() -> {
          if (loanSummaryEntity.getLoanRevolving()) {
            revolvingUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_loan_revolvings, revolvingRequestHelper,
                    revolvingResponseHelper, revolvingPublishmentHelper);
          }
        }).handle(this::handleException),

        CompletableFuture.runAsync(() -> {
          if (loanSummaryEntity.getLoanShortTerm()) {
            loanShortTermUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_loan_short_terms, loanShortTermsRequestHelper,
                    loanShortTermResponseHelper, loanShortTermPublishmentHelper);
          }
        }).handle(this::handleException),

        CompletableFuture.runAsync(() -> {
          if (loanSummaryEntity.getLoanLongTerm()) {
            loanLongTermUserBaseService
                .getUserBaseInfo(executionContext, Executions.finance_loan_long_terms, loanLongTermsRequestHelper,
                    loanLongTermResponseHelper, loanLongTermPublishmentHelper);
          }
        }).handle(this::handleException)

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
