package com.banksalad.collectmydata.irp.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;
import com.banksalad.collectmydata.irp.template.testcase.IrpAccountTransactionTestCaseGenerator;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_INVALID_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ZERO_USS_ST;

public class IrpAccountTransactionInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.irp_get_transactions;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    IrpAccountSummaryEntity parent1 = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .accountStatus("01")
        .basicSearchTimestamp(ZERO_USS_ST)
        .detailSearchTimestamp(ZERO_USS_ST)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("상품명1")
        .seqno("a123")
        .build();

    Map<String, IrpAccountSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "invalidAccountFreshParent1", parent1.toBuilder().transactionResponseCode(RSP_CODE_INVALID_ACCOUNT).build(),
        "updatedFreshParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "failedTooManyQuotaParent1",
        parent1.toBuilder().transactionSyncedAt(OLD_SYNCED_AT).transactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().transactionSyncedAt(NEW_SYNCED_AT).build()
    );

    IrpAccountTransactionEntity main1 = IrpAccountTransactionEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .transDtime("20210402102000")
        .transAmt(new BigDecimal(10000))
        .transType("01").build();

    main1.setTransactionYearMonth(Integer.valueOf(main1.getTransDtime().substring(0, 6)));
    main1.setUniqueTransNo(
        HashUtil.hashCat(main1.getTransDtime(), main1.getTransType(), main1.getTransAmt().toString()));

    IrpAccountTransactionEntity main2 = IrpAccountTransactionEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .transDtime("20210402202000")
        .transAmt(new BigDecimal(20000))
        .transType("02").build();
    main2.setTransactionYearMonth(Integer.valueOf(main2.getTransDtime().substring(0, 6)));
    main2.setUniqueTransNo(
        HashUtil.hashCat(main2.getTransDtime(), main2.getTransType(), main2.getTransAmt().toString()));

    IrpAccountTransactionEntity main3 = IrpAccountTransactionEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .transDtime("20210403102000")
        .transAmt(new BigDecimal(10000))
        .transType("02").build();
    main3.setTransactionYearMonth(Integer.valueOf(main3.getTransDtime().substring(0, 6)));
    main3.setUniqueTransNo(
        HashUtil.hashCat(main3.getTransDtime(), main3.getTransType(), main3.getTransAmt().toString()));

    Map<String, IrpAccountTransactionEntity> mainMap = Map.of(
        "main1", main1,
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    IrpAccountTransactionTestCaseGenerator<Object, IrpAccountSummaryEntity, IrpAccountTransactionEntity, Object> generator =
        new IrpAccountTransactionTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, IrpAccountSummaryEntity, IrpAccountTransactionEntity, Object> testCase) {

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
