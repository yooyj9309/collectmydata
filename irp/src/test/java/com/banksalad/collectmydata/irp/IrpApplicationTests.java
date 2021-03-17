package com.banksalad.collectmydata.irp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;

@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
class IrpApplicationTests {

  @Test
  void contextLoads() {
  }

}
