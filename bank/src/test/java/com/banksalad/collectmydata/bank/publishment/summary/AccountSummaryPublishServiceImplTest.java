package com.banksalad.collectmydata.bank.publishment.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("계좌목록 publish service 테스트")
@Transactional
class AccountSummaryPublishServiceImplTest {

  @Autowired
  private AccountSummaryPublishService accountSummaryPublishService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private final String[] ENTITY_IGNORE_FIELD = {"id", "banksaladUserId", "organizationId", "syncedAt", "createdBy", "updatedBy"};

  @Test
  @DisplayName("계좌목록 정상 조회 후 응답 성공")
  void getAccountSummaryResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());

    // when
    List<AccountSummaryResponse> accountSummaryResponses = accountSummaryPublishService
        .getAccountSummaryResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(accountSummaryResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(accountSummaryRepository.findAll());
  }

  private AccountSummaryEntity getAccountSummaryEntity() {
    return AccountSummaryEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .consent(TRUE)
        .seqno("01")
        .foreignDeposit(FALSE)
        .prodName("abc")
        .accountType("0000")
        .accountStatus("01")
        .build();
  }
}
