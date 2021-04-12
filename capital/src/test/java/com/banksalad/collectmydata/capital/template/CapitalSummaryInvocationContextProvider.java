package com.banksalad.collectmydata.capital.template;

import com.banksalad.collectmydata.capital.testcase.CapitalSummaryTestCaseGenerator;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CapitalSummaryInvocationContextProvider implements TestTemplateInvocationContextProvider {

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    return CapitalSummaryTestCaseGenerator.get().stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(TestCase testCase) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return testCase.getDisplayName();
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new DefaultParameterResolver(testCase));
      }
    };
  }
}
