package com.banksalad.collectmydata.invest.publishment.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.summary.dto.AccountSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("계좌 목록 Publish 서비스 테스트")
class AccountSummaryPublishServiceTest {

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountSummaryPublishService accountSummaryPublishService;

  @Test
  @DisplayName("계좌 목록 조회 성공 테스트")
  void getAccountSummaryResponsesTest() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());

    // when
    List<AccountSummaryResponse> accountSummaryResponses = accountSummaryPublishService
        .getAccountSummaryResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(accountSummaryResponses).usingRecursiveComparison().isEqualTo(accountSummaryRepository.findAll());
  }

  private AccountSummaryEntity getAccountSummaryEntity() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .build();
  }
}
