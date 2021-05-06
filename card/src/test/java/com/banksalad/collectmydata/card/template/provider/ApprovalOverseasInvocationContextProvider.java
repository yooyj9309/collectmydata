package com.banksalad.collectmydata.card.template.provider;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalOverseasEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.template.testcase.ApprovalOverseasTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;

public class ApprovalOverseasInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.finance_card_approval_overseas;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    CardSummaryEntity parent1 = CardSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("card001")
        .cardNum("123456******456")
        .consent(true)
        .cardName("하나카드01")
        .cardMember(1)
        .searchTimestamp(OLD_ST1)
        .approvalOverseasTransactionSyncedAt(null)
        .approvalOverseasTransactionResponseCode(RSP_CODE_SUCCESS)
        .build();
    Map<String, CardSummaryEntity> parentMap = Map.of(
        "freshParent1", parent1,
        "failedFreshParent1", parent1.toBuilder().approvalOverseasTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        "updatedFreshParent1", parent1.toBuilder().approvalOverseasTransactionSyncedAt(NEW_SYNCED_AT).build(),
        "existingParent1", parent1.toBuilder().approvalOverseasTransactionSyncedAt(OLD_SYNCED_AT).build(),
        "failedExistingParent1",
        parent1.toBuilder().approvalOverseasTransactionSyncedAt(OLD_SYNCED_AT)
            .approvalOverseasTransactionResponseCode(RSP_CODE_OVER_QUOTA).build(),
        /* transaction은 summary의 syncedAt을 변경하지 않는다. updatedFreshParent1==updatedExistingParent1 */
        "updatedExistingParent1", parent1.toBuilder().approvalOverseasTransactionSyncedAt(NEW_SYNCED_AT).build()
    );

    ApprovalOverseasEntity main1 = ApprovalOverseasEntity.builder()
        .approvalYearMonth(202103)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("card001")
        .approvedNum("001")
        .status("01")
        .payType("01")
        .approvedDtime("20210301091000")
        .merchantName("StartBucks")
        .approvedAmt(bigDecimalOf(5000, 3))
        .countryCode("US")
        .currencyCode("USD")
        .build();
    ApprovalOverseasEntity main2 = ApprovalOverseasEntity.builder()
        .approvalYearMonth(202103)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("card001")
        .approvedNum("002")
        .status("02")
        .payType("01")
        .approvedDtime("20210302101000")
        .cancelDtime("20210302102000")
        .merchantName("GigaCoffee")
        .approvedAmt(bigDecimalOf(15000, 3))
        .countryCode("KR")
        .currencyCode("KRW")
        .krwAmt(bigDecimalOf(15000,3))
        .build();
    ApprovalOverseasEntity main3 = ApprovalOverseasEntity.builder()
        .approvalYearMonth(202103)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("card001")
        .approvedNum("003")
        .status("02")
        .payType("01")
        .approvedDtime("20210302103000")
        .merchantName("YangYang")
        .approvedAmt(bigDecimalOf(3000, 3))
        .countryCode("KR")
        .currencyCode("KRW")
        .krwAmt(bigDecimalOf(3000,3))
        .build();
    Map<String, ApprovalOverseasEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).approvedAmt(bigDecimalOf(5,3)).build(),
        "newMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain2", main2.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newMain3", main3.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    ApprovalOverseasTestCaseGenerator<Object, CardSummaryEntity, ApprovalOverseasEntity, Object> generator =
        new ApprovalOverseasTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, CardSummaryEntity, ApprovalOverseasEntity, Object> testCase) {

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
