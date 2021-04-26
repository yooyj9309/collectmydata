package com.banksalad.collectmydata.irp.func;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("TestTemplate test")
public class TestTemplateExample {

  @TestTemplate
  @ExtendWith(MyTestTemplateInvocationContextProvider.class)
  void testTemplate(String parameter) {
    assertEquals(3, parameter.length());
  }
}
