package com.banksalad.collectmydata.insu.template.provider;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.template.testcase.CarInsuranceTestCaseGenerator;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST1;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_ST2;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_NO_ACCOUNT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.CAR_INSU_TYPE;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.CAR_NUMBER;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.CAR_PROD_NAME;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceTestConstants.INSU_STATUS;

public class CarInsuranceInvocationContextProvider implements TestTemplateInvocationContextProvider {

  private static final Execution exeuciton = Executions.insurance_get_car;

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
        .insuNum("existingInsuNum")
        .consent(true)
        .prodName(CAR_PROD_NAME)
        .insuType(CAR_INSU_TYPE)
        .insuStatus(INSU_STATUS)
        .carSearchTimestamp(OLD_ST1)
        .carResponseCode(RSP_CODE_SUCCESS)
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    InsuranceSummaryEntity newParent = InsuranceSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("newInsuNum")
        .consent(true)
        .prodName(CAR_PROD_NAME)
        .insuType(CAR_INSU_TYPE)
        .insuStatus(INSU_STATUS)
        .carSearchTimestamp(null)
        .carResponseCode(RSP_CODE_SUCCESS)
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    Map<String, InsuranceSummaryEntity> parentMap = Map.of(
        "existingParent", existingParent,
        "failedExistingParent", existingParent.toBuilder().carResponseCode(RSP_CODE_NO_ACCOUNT).build(),
        "updatedExistingParent", existingParent.toBuilder().carSearchTimestamp(NEW_ST1).build(),
        "newParent", newParent,
        "updatedNewParent", newParent.toBuilder().carSearchTimestamp(OLD_ST2).build()
    );

    CarInsuranceEntity existingMain = CarInsuranceEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("existingInsuNum")
        .carNumber(CAR_NUMBER)
        .carInsuType("02")
        .carName("그랜져 IG")
        .startDate("20200101")
        .endDate("20210101")
        .contractAge("21세")
        .contractDriver("가족한정")
        .ownDmgCoverage(true)
        .selfPayRate("01")
        .selfPayAmt(bigDecimalOf(200000, 3))
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    CarInsuranceEntity newMain = CarInsuranceEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("newInsuNum")
        .carNumber(CAR_NUMBER)
        .carInsuType("04")
        .carName("그랜져 IG")
        .startDate("20200601")
        .endDate("20210601")
        .contractAge("21세")
        .contractDriver("본인")
        .ownDmgCoverage(false)
        .selfPayRate("02")
        .selfPayAmt(bigDecimalOf(30000, 3))
        .syncRequestId(SYNC_REQUEST_ID)
        .consentId(CONSENT_ID)
        .build();
    Map<String, CarInsuranceEntity> mainMap = Map.of(
        "existingMain", existingMain,
        "updatedExistingMain",
        existingMain.toBuilder().syncedAt(NEW_SYNCED_AT).selfPayAmt(bigDecimalOf(299999, 3)).build(),
        "newMain", newMain.toBuilder().syncedAt(NEW_SYNCED_AT).build()
    );

    CarInsuranceTestCaseGenerator<Object, InsuranceSummaryEntity, CarInsuranceEntity, Object> generator =
        new CarInsuranceTestCaseGenerator<>(exeuciton, null, parentMap, mainMap, null);

    return generator.generate().stream()
//        .peek(o -> cprint("Generated testCase",o))
        .map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(
      TestCase<Object, InsuranceSummaryEntity, CarInsuranceEntity, Object> testCase) {

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
