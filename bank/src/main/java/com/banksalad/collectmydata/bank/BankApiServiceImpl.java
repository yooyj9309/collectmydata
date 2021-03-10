package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.deposit.DepositAccountTransactionService;
import com.banksalad.collectmydata.bank.deposit.DepositAccountService;
import com.banksalad.collectmydata.bank.invest.InvestAccountService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private static final String DEPOSIT_ACCOUNT_TYPE_CODE = "DEPOSIT";
  private static final String LOAN_ACCOUNT_TYPE_CODE = "LOAN";
  private static final String INVEST_ACCOUNT_TYPE_CODE = "INVEST";

  private final AccountSummaryService accountSummaryService;
  private final DepositAccountService depositAccountService;
  private final DepositAccountTransactionService depositAccountTransactionService;
  private final InvestAccountService investAccountService;

  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<AccountSummary> accountSummaries = accountSummaryService.listAccountSummaries(executionContext);

    // TODO jayden-lee 마이너스 통장은 별도의 리스트에 담아야 함 (기본정보: 대출, 추가정보: 수신, 거래내역: 수신)
    List<AccountSummary> depositAccountSummaries = new ArrayList<>();
    List<AccountSummary> investAccountSummaries = new ArrayList<>();
    List<AccountSummary> loanAccountSummaries = new ArrayList<>();

    for (AccountSummary accountSummary : accountSummaries) {
      String accountType = accountSummary.getAccountType();
      if (StringUtils.isEmpty(accountType)) {
        log.error("Unavailable account type: {}", accountType);
        continue;
      }

      String accountTypeCode = getAccountTypeCode(accountSummary.getAccountType());

      if (DEPOSIT_ACCOUNT_TYPE_CODE.equals(accountTypeCode)) {
        depositAccountSummaries.add(accountSummary);

      } else if (INVEST_ACCOUNT_TYPE_CODE.equals(accountType)) {
        investAccountSummaries.add(accountSummary);

      } else if (LOAN_ACCOUNT_TYPE_CODE.equals(accountType)) {
        loanAccountSummaries.add(accountSummary);
      }
    }

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    // TODO jayden-lee 투자, 대출, IRP 서비스 추가
    CompletableFuture.allOf(
        CompletableFuture.supplyAsync(
            () -> depositAccountService.listDepositAccountBasics(executionContext, depositAccountSummaries))
            .thenAccept(depositAccountBasics -> bankApiResponseAtomicReference.get()
                .setDepositAccountBasics(depositAccountBasics)),

        CompletableFuture.supplyAsync(
            () -> depositAccountService.listDepositAccountDetails(executionContext, depositAccountSummaries))
            .thenAccept(depositAccountDetails -> bankApiResponseAtomicReference.get()
                .setDepositAccountDetails(depositAccountDetails)),

        CompletableFuture.supplyAsync(
            () -> depositAccountTransactionService
                .listDepositAccountTransactions(executionContext, depositAccountSummaries))
            .thenAccept(depositAccountTransactions -> bankApiResponseAtomicReference.get()
                .setDepositAccountTransactions(depositAccountTransactions)),

        CompletableFuture.supplyAsync(
            () -> investAccountService.listInvestAccountBasics(executionContext, investAccountSummaries))
            .thenAccept(investAccountBasics -> bankApiResponseAtomicReference.get()
                .setInvestAccountBasics(investAccountBasics)),

        CompletableFuture.supplyAsync(
            () -> investAccountService.listInvestAccountDetails(executionContext, investAccountSummaries))
            .thenAccept(investAccountDetails -> bankApiResponseAtomicReference.get()
                .setInvestAccountDetails(investAccountDetails))
    ).join();

    return bankApiResponseAtomicReference.get();
  }

  // TODO jayden-lee Enum 클래스 생성하고 변경할 예정
  // int accountTypeCode = Integer.valueOf(accountType);
  // accountTypeCode % 1000, 공통된 규칙은 4자리에서 첫번째 자리에 따라 계좌 유형이 달라짐.
  private String getAccountTypeCode(String accountType) {
    switch (accountType) {
      case "1001":
      case "1002":
      case "1003":
      case "1999":
        return DEPOSIT_ACCOUNT_TYPE_CODE;

      case "2001":
      case "2002":
      case "2003":
      case "2004":
      case "2999":
        return INVEST_ACCOUNT_TYPE_CODE;

      case "3001":
      case "3150":
      case "3170":
      case "3200":
      case "3210":
      case "3220":
      case "3230":
      case "3240":
      case "3245":
      case "3250":
      case "3260":
      case "3270":
      case "3271":
      case "3290":
      case "3400":
      case "3500":
      case "3510":
      case "3590":
      case "3700":
      case "3710":
      case "3999":
        return LOAN_ACCOUNT_TYPE_CODE;

      default:
        return "";
    }
  }
}
