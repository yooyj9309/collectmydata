package com.banksalad.collectmydata.insu.loan;

import com.banksalad.collectmydata.insu.loan.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class LoanServiceTest {
  @Autowired
  LoanService loanService;

  @Test
  @DisplayName("6.5.10 (1) 대출상품 추가정보 조회: 정상 케이스")
  public void listInsuranceBasics_success() {
    // TODO: Test here
  }
}
