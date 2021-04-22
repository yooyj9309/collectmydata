package com.banksalad.collectmydata.capital.template.provider;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicEntity;
import com.banksalad.collectmydata.capital.template.testcase.OperatingLeaseBasicTestCaseGenerator;
import com.banksalad.collectmydata.common.collect.execution.Execution;
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

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;

public class OperatingLeaseBasicInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.capital_get_operating_lease_basic;

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    AccountSummaryEntity existingParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("existingAccountNum")
        .isConsent(true)
        .seqno("1")
        .prodName("상품명1")
        .accountType("3710")
        .accountStatus("01")
        .operatingLeaseBasicSearchTimestamp(OLD_ST1)
        .build();
    AccountSummaryEntity newParent = AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("newAccountNum")
        .isConsent(true)
        .seqno("2")
        .prodName("상품명2")
        .accountType("3710")
        .accountStatus("03")
        .operatingLeaseBasicSearchTimestamp(null)
        .build();
    Map<String, AccountSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().operatingLeaseBasicResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().operatingLeaseBasicSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().operatingLeaseBasicSearchTimestamp(OLD_ST2).build()
    );

    OperatingLeaseBasicEntity existingMain = OperatingLeaseBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("existingAccountNum")
        .seqno("1")
        .holderName("김뱅셀")
        .issueDate("20210210")
        .expDate("20221231")
        .repayDate("03")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .nextRepayDate("20210310")
        .build();
    OperatingLeaseBasicEntity newMain = OperatingLeaseBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("newAccountNum")
        .seqno("2")
        .holderName("김뱅셀")
        .issueDate("20210310")
        .expDate("20221231")
        .repayDate("03")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .nextRepayDate("20210410")
        .build();
    Map<String, OperatingLeaseBasicEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain", existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).expDate("20351231").build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    OperatingLeaseBasicTestCaseGenerator<Object, AccountSummaryEntity, OperatingLeaseBasicEntity, Object> generator =
        new OperatingLeaseBasicTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, AccountSummaryEntity, OperatingLeaseBasicEntity, Object> testCase) {

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
