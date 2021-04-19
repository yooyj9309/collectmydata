package com.banksalad.collectmydata.ginsu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.ginsu.collect.Executions;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.template.testcase.InsuranceBasicTestCaseGenerator;
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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class InsuranceBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution execution = Executions.finance_ginsu_insurance_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    InsuranceSummaryEntity existingParent = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("1111111111")
        .consent(true)
        .prodName("상품명1")
        .insuType("20")
        .insuStatus("01")
        .basicSearchTimestamp(OLD_ST1)
        .build();

    InsuranceSummaryEntity newParent = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("2222222222")
        .consent(true)
        .prodName("상품명2")
        .insuType("20")
        .insuStatus("01")
        .basicSearchTimestamp(0L)
        .build();

    Map<String, InsuranceSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().basicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().basicSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().basicSearchTimestamp(OLD_ST2).build()
    );

    InsuranceBasicEntity existingMain = InsuranceBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("1111111111")
        .issueDate("20210101")
        .expDate("20300201")
        .faceAmt(BigDecimal.valueOf(11111.111))
        .payDue("02")
        .payAmt(BigDecimal.valueOf(11111.111))
        .build();

    InsuranceBasicEntity newMain = InsuranceBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("2222222222")
        .issueDate("20210101")
        .expDate("20300201")
        .faceAmt(BigDecimal.valueOf(22222.222))
        .payDue("02")
        .payAmt(BigDecimal.valueOf(22222.222))
        .build();

    Map<String, InsuranceBasicEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain", existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).faceAmt(BigDecimal.valueOf(22222.222)).build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuredEntity existingChild1 = InsuredEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("1111111111")
        .insuredNo((short) 1)
        .insuredName("피보험자1")
        .build();

    InsuredEntity existingChild2 = InsuredEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("1111111111")
        .insuredNo((short) 2)
        .insuredName("피보험자2")
        .build();

    InsuredEntity newChild1 = InsuredEntity.builder()
        .syncedAt(NEW_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("2222222222")
        .insuredNo((short) 1)
        .insuredName("피보험자3")
        .build();

    InsuredEntity newChild2 = InsuredEntity.builder()
        .syncedAt(NEW_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("2222222222")
        .insuredNo((short) 2)
        .insuredName("피보험자4")
        .build();

    Map<String, InsuredEntity> childMap = Map.of(
        "existingChild1", existingChild1,
        "existingChild2", existingChild2,
        "newChild1", newChild1.toBuilder().syncedAt(NEW_SYNCED_AT).build(),
        "newChild2", newChild2.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    InsuranceBasicTestCaseGenerator<Object, InsuranceSummaryEntity, InsuranceBasicEntity, InsuredEntity> generator =
        new InsuranceBasicTestCaseGenerator<>(execution, null, parentMap, mainMap, childMap);

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
