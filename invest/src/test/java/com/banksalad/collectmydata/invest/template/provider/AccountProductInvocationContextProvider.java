package com.banksalad.collectmydata.invest.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.template.testcase.AccountProductTestCaseGenerator;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class AccountProductInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_invest_account_products;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    AccountSummaryEntity parent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .build();

    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "existingParent", parent.toBuilder().productSearchTimestamp(OLD_ST1).build(),
        "failedExistingParent", parent.toBuilder().productResponseCode(RSP_CODE_NO_ACCOUNT).productSearchTimestamp(OLD_ST1).build(),
        "updatedExistingParent", parent.toBuilder().productSearchTimestamp(NEW_ST1).build(),
        "newParent", parent.toBuilder().productSearchTimestamp(0L).build(),
        "updatedNewParent", parent.toBuilder().productSearchTimestamp(OLD_ST1).build()
    );

    AccountProductEntity main1 = AccountProductEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .prodNo((short) 1)
        .prodCode("005930")
        .prodType("401")
        .prodTypeDetail("국내주식")
        .prodName("삼성전자")
        .purchaseAmt(new BigDecimal( "10000.000"))
        .holdingNum(100L)
        .availForSaleNum(100L)
        .evalAmt(new BigDecimal("20000.000"))
        .issueDate("20210101")
        .paidInAmt(new BigDecimal("30000.000"))
        .withdrawalAmt(new BigDecimal("40000.000"))
        .lastPaidInDate("20210201")
        .rcvAmt(new BigDecimal("50000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountProductEntity main2 = AccountProductEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .prodNo((short) 2)
        .prodCode("AAPL")
        .prodType("402")
        .prodTypeDetail("해외주식")
        .prodName("애플")
        .purchaseAmt(new BigDecimal( "111.111"))
        .holdingNum(100L)
        .availForSaleNum(100L)
        .evalAmt(new BigDecimal("222.222"))
        .issueDate("20210101")
        .paidInAmt(new BigDecimal("333.333"))
        .withdrawalAmt(new BigDecimal("444.444"))
        .lastPaidInDate("20210201")
        .rcvAmt(new BigDecimal("555.555"))
        .currencyCode("USD")
        .build();

    Map<String, AccountProductEntity> mainMap = Map.of(
        "main1", main1,
        "main2", main2,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).holdingNum(200L).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    AccountProductTestCaseGenerator<Object, AccountSummaryEntity, AccountProductEntity, Object> generator =
        new AccountProductTestCaseGenerator<>(execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(TestCase testCase) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return testCase.getDisplayName();
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new ParameterResolver() {
          @Override
          public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
              throws ParameterResolutionException {
            return parameterContext.getParameter().getType().equals(TestCase.class);
          }

          @Override
          public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
              throws ParameterResolutionException {
            return testCase;
          }
        });
      }
    };
  }
}
