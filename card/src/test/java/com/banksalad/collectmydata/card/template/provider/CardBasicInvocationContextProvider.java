package com.banksalad.collectmydata.card.template.provider;

import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.template.testcase.CardBasicTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_USS_ST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;

public class CardBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_card_basic;

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
        .cardId("AN11111111")
        .cardNum("123456*****11111")
        .consent(true)
        .cardName("??????????????????1")
        .cardMember(1)
        .searchTimestamp(OLD_USS_ST) // 100
        .responseCode("00000")
        .build();

//    CardSummaryEntity parent2 = CardSummaryEntity.builder()
//        .syncedAt(OLD_SYNCED_AT)
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .cardId("AN22222222")
//        .cardNum("123456*****11111")
//        .consent(true)
//        .cardName("??????????????????2")
//        .cardMember(1)
//        .searchTimestamp(OLD_USS_ST) // 100
//        .responseCode("00000")
//        .build();

    Map<String, CardSummaryEntity> parentMap = Map.of(
        "parent1", parent1,
        "updatedParent1", parent1.toBuilder().syncedAt(NEW_SYNCED_AT).searchTimestamp(NEW_USS_ST).build()
    );

    CardEntity main1 = CardEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .cardId("AN11111111")
        .cardType("01") // ??????
        .transPayable(true)
        .cashCard(true)
        .linkedBankCode("0000000000")
        .cardBrand("I01")
        .annualFee(BigDecimal.valueOf(10000000))
        .issueDate("20210210")
        .build();

    Map<String, CardEntity> mainMap = Map.of(
        "main1", main1,
        "updatedMain1", main1.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    CardBasicTestCaseGenerator<Object, CardSummaryEntity, CardEntity, Object> generator = new CardBasicTestCaseGenerator<>(
        execution, null, parentMap, mainMap, null);

    return generator.generate().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, CardSummaryEntity, CardEntity, Object> testCase
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
