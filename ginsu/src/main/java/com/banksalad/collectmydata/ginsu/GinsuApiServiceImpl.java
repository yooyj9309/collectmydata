package com.banksalad.collectmydata.ginsu;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import com.banksalad.collectmydata.ginsu.collect.Executions;
import com.banksalad.collectmydata.ginsu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.ginsu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListInsuranceSummariesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GinsuApiServiceImpl implements GinsuApiService {

  private final FinanceMessageService financeMessageService;
  private final CollectmydataConnectClientService connectClientService;

  private final SummaryService<ListInsuranceSummariesRequest, InsuranceSummary> insuranceSummaryService;
  private final SummaryRequestHelper<ListInsuranceSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<InsuranceSummary> summaryResponseHelper;

  private final AccountInfoService<InsuranceSummary, GetInsuranceBasicRequest, InsuranceBasic> insuranceBasicApiService;

  private final AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> insuranceBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> insuranceBasicInfoResponseHelper;

  private final TransactionApiService<InsuranceSummary, ListInsuranceTransactionsRequest, InsuranceTransaction> insuranceTransactionApiService;
  private final TransactionRequestHelper<InsuranceSummary, ListInsuranceTransactionsRequest> insuranceTransactionRequestHelper;
  private final TransactionResponseHelper<InsuranceSummary, InsuranceTransaction> insuranceTransactionResponseHelper;

  @Override
  public void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    OauthToken oauthToken = connectClientService.getAccessToken(banksaladUserId, organizationId);
    Organization organization = connectClientService.getOrganization(organizationId);

    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(oauthToken.getConsentId())
        .syncRequestId(syncRequestId)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(banksaladUserId)
        .organizationId(organization.getOrganizationId())
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .requestedBy(String.valueOf(banksaladUserId))
        .build();

    insuranceSummaryService.listAccountSummaries(
        executionContext,
        Executions.finance_ginsu_summaries,
        summaryRequestHelper,
        summaryResponseHelper
    );

    CompletableFuture.allOf(
        CompletableFuture.runAsync(
            () -> insuranceBasicApiService.listAccountInfos(executionContext, Executions.finance_ginsu_insurance_basic,
                insuranceBasicInfoRequestHelper, insuranceBasicInfoResponseHelper)),

        CompletableFuture.runAsync(
            () -> insuranceTransactionApiService.listTransactions(
                executionContext,
                Executions.finance_ginsu_insurance_transaction,
                insuranceTransactionRequestHelper,
                insuranceTransactionResponseHelper)
        )
    ).join();
  }

  // TODO jaeseong: 스케쥴 request api 구현
}
