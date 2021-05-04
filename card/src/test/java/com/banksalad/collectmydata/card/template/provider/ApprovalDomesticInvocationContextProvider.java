package com.banksalad.collectmydata.card.template.provider;

import org.springframework.format.datetime.DateFormatter;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.template.testcase.ApprovalDomesticTestCaseGenerator;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_OVER_QUOTA;

public class ApprovalDomesticInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_card_approval_domestic;

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
        .approvalOverseasTransactionResponseCode(null)
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

    ApprovalDomesticEntity main1 = ApprovalDomesticEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("AN11111111")
        .approvedNum("12345678")
        .status("01")
        .payType("01")
        .approvedDtime(
            LocalDateTime.parse("20210401121212", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString())
        .merchantName("홍콩반점")
        .approvedAmt(NumberUtil.bigDecimalOf(10000, 3))
        .totalInstallCnt(2)
        .build();
    Map<String, ApprovalDomesticEntity> mainMap = Map.of(
        "main1", main1
    );

    ApprovalDomesticTestCaseGenerator<Object, CardSummaryEntity, ApprovalDomesticEntity, Object> generator = new ApprovalDomesticTestCaseGenerator(
        execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, CardSummaryEntity, ApprovalDomesticEntity, Object> testCase
  ) {
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
