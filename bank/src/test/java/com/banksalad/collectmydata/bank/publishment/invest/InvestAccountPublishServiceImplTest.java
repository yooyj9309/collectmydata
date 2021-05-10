package com.banksalad.collectmydata.bank.publishment.invest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountTransactionsRequest;
import com.google.protobuf.StringValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("투자상품계좌 publish service 테스트")
@Transactional
class InvestAccountPublishServiceImplTest {

  @Autowired
  private InvestAccountPublishService investAccountPublishService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private InvestAccountBasicRepository investAccountBasicRepository;

  @Autowired
  private InvestAccountDetailRepository investAccountDetailRepository;

  @Autowired
  private InvestAccountTransactionRepository investAccountTransactionRepository;

  private final String[] ENTITY_IGNORE_FIELD = {"id", "banksaladUserId", "organizationId", "syncedAt", "createdBy",
      "updatedBy", "basicResponseCode", "detailResponseCode", "transactionResponseCode", "transactionYearMonth",
      "uniqueTransNo"};

  @Test
  @DisplayName("투자상품계좌 기본정보 정상 조회 후 응답 성공")
  void getInvestAccountBasicResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    investAccountBasicRepository.save(getInvestAccountBasicEntity());

    // when
    List<InvestAccountBasicResponse> investAccountBasicResponses = investAccountPublishService
        .getInvestAccountBasicResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(investAccountBasicResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(investAccountBasicRepository.findAll());
  }

  @Test
  @DisplayName("투자상품계좌 추가정보 정상 조회 후 응답 성공")
  void getInvestAccountDetailResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    investAccountDetailRepository.save(getInvestAccountDetailEntity());

    // when
    List<InvestAccountDetailResponse> investAccountDetailResponses = investAccountPublishService
        .getInvestAccountDetailResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(investAccountDetailResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(investAccountDetailRepository.findAll());
  }

  @Test
  @DisplayName("투자상품계좌 거래내역 정상 조회 후 응답 성공")
  void getInvestAccountTransactionResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    investAccountTransactionRepository.save(getInvestAccountTransactionEntity());

    ListBankInvestAccountTransactionsRequest request = getListBankInvestAccountTransactionsRequest();
    LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);

    // when
    List<InvestAccountTransactionResponse> investAccountTransactionResponses = investAccountPublishService
        .getInvestAccountTransactionResponses(BANKSALAD_USER_ID, ORGANIZATION_ID, request.getAccountNum(),
            request.getSeqno().getValue(), createdAt, Long.valueOf(request.getLimit()).intValue());

    // then
    assertThat(investAccountTransactionResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(investAccountTransactionRepository.findAll());
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
        .basicResponseCode("00000")
        .detailResponseCode("00000")
        .transactionResponseCode("00000")
        .build();
  }

  private InvestAccountBasicEntity getInvestAccountBasicEntity() {
    return InvestAccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .standardFundCode("standardFundCode")
        .paidInType("02")
        .issueDate("20200101")
        .expDate(null)
        .consentId("consent_id1")
        .build();
  }

  private InvestAccountDetailEntity getInvestAccountDetailEntity() {
    return InvestAccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .currencyCode("KRW")
        .balanceAmt(BigDecimal.valueOf(10000.10))
        .evalAmt(BigDecimal.valueOf(20000.20))
        .invPrincipal(BigDecimal.valueOf(5000.50))
        .fundNum(BigDecimal.valueOf(3.0))
        .consentId("consent_id1")
        .build();
  }

  private InvestAccountTransactionEntity getInvestAccountTransactionEntity() {
    return InvestAccountTransactionEntity.builder()
        .transactionYearMonth(20210101)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .uniqueTransNo("uniqueHash")
        .currencyCode("KRW")
        .transDtime("20210101000000")
        .transNo(null)
        .transType("02")
        .baseAmt(BigDecimal.valueOf(1000.10))
        .transFundNum(BigDecimal.valueOf(3.0))
        .transAmt(BigDecimal.valueOf(2000.20))
        .balanceAmt(BigDecimal.valueOf(10000.10))
        .consentId("consent_id1")
        .build();
  }

  private ListBankInvestAccountTransactionsRequest getListBankInvestAccountTransactionsRequest() {
    return ListBankInvestAccountTransactionsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationGuid("organizationGuid")
        .setAccountNum("1234567890")
        .setSeqno(StringValue.newBuilder().setValue("01").build())
        .setCreatedAfterMs(0)
        .setLimit(500)
        .build();
  }
}
