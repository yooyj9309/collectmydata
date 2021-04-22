package com.banksalad.collectmydata.bank.publishment.deposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import com.google.protobuf.StringValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("수신계좌 publish service 테스트")
@Transactional
class DepositAccountPublishServiceImplTest {

  @Autowired
  private DepositAccountPublishService depositAccountPublishService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountBasicRepository depositAccountBasicRepository;

  @Autowired
  private DepositAccountDetailRepository depositAccountDetailRepository;

  @Autowired
  private DepositAccountTransactionRepository depositAccountTransactionRepository;

  @MockBean
  private ConnectClientService connectClientService;

  private final String[] ENTITY_IGNORE_FIELD = {"id", "banksaladUserId", "organizationId", "syncedAt", "createdBy",
      "updatedBy", "basicResponseCode", "detailResponseCode", "transactionResponseCode", "transactionYearMonth",
      "uniqueTransNo"};

  @Test
  @DisplayName("수신계좌 기본정보 정상 조회 후 응답 성공")
  void getDepositAccountBasicResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    depositAccountBasicRepository.save(getDepositAccountBasicEntity());
    when(connectClientService.getOrganizationByOrganizationObjectid(any())).thenReturn(
        GetOrganizationResponse.newBuilder()
            .setSector("sector")
            .setIndustry("industry")
            .setOrganizationId(ORGANIZATION_ID)
            .setOrganizationCode(ORGANIZATION_CODE)
            .setDomain(ORGANIZATION_HOST)
            .build());

    // when
    List<DepositAccountBasicResponse> depositAccountBasicResponses = depositAccountPublishService
        .getDepositAccountBasicResponses(getListBankDepositAccountBasicsRequest());

    // then
    assertThat(depositAccountBasicResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountBasicRepository.findAll());
  }

  @Test
  @DisplayName("수신계좌 추가정보 정상 조회 후 응답 성공")
  void getDepositAccountDetailResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    depositAccountDetailRepository.save(getDepositAccountDetailEntity());
    when(connectClientService.getOrganizationByOrganizationObjectid(any())).thenReturn(
        GetOrganizationResponse.newBuilder()
            .setSector("sector")
            .setIndustry("industry")
            .setOrganizationId(ORGANIZATION_ID)
            .setOrganizationCode(ORGANIZATION_CODE)
            .setDomain(ORGANIZATION_HOST)
            .build());

    // when
    List<DepositAccountDetailResponse> depositAccountDetailResponses = depositAccountPublishService
        .getDepositAccountDetailResponses(getListBankDepositAccountDetailsRequest());

    // then
    assertThat(depositAccountDetailResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountDetailRepository.findAll());
  }

  @Test
  @DisplayName("수신계좌 거래내역 정상 조회 후 응답 성공")
  void getDepositAccountTransactionResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    depositAccountTransactionRepository.save(getDepositAccountTransactionEntity());
    when(connectClientService.getOrganizationByOrganizationObjectid(any())).thenReturn(
        GetOrganizationResponse.newBuilder()
            .setSector("sector")
            .setIndustry("industry")
            .setOrganizationId(ORGANIZATION_ID)
            .setOrganizationCode(ORGANIZATION_CODE)
            .setDomain(ORGANIZATION_HOST)
            .build());

    // when
    List<DepositAccountTransactionResponse> depositAccountTransactionResponses = depositAccountPublishService
        .getDepositAccountTransactionResponses(getListBankDepositAccountTransactionsRequest());

    // then
    assertThat(depositAccountTransactionResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountTransactionRepository.findAll());
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

  private DepositAccountBasicEntity getDepositAccountBasicEntity() {
    return DepositAccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .currencyCode("KRW")
        .savingMethod("savingMethod")
        .holderName("김뱅샐")
        .issueDate("20200101")
        .expDate(null)
        .commitAmt(null)
        .monthlyPaidInAmt(null)
        .build();
  }

  private DepositAccountDetailEntity getDepositAccountDetailEntity() {
    return DepositAccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .currencyCode("KRW")
        .balanceAmt(BigDecimal.valueOf(10000.10))
        .withdrawableAmt(BigDecimal.valueOf(2000.20))
        .offeredRate(BigDecimal.valueOf(0.3))
        .lastPaidInCnt(1)
        .build();
  }

  private DepositAccountTransactionEntity getDepositAccountTransactionEntity() {
    return DepositAccountTransactionEntity.builder()
        .transactionYearMonth(20200101)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .currencyCode("KRW")
        .uniqueTransNo("uniqueHash")
        .transDtime("20210101000000")
        .transNo(null)
        .transType("02")
        .transClass("출금")
        .transAmt(BigDecimal.valueOf(1000))
        .balanceAmt(BigDecimal.valueOf(3000))
        .paidInCnt(1)
        .build();
  }

  private ListBankDepositAccountBasicsRequest getListBankDepositAccountBasicsRequest() {
    return ListBankDepositAccountBasicsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .build();
  }

  private ListBankDepositAccountDetailsRequest getListBankDepositAccountDetailsRequest() {
    return ListBankDepositAccountDetailsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .build();
  }

  private ListBankDepositAccountTransactionsRequest getListBankDepositAccountTransactionsRequest() {
    return ListBankDepositAccountTransactionsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .setAccountNum("1234567890")
        .setSeqno(StringValue.newBuilder().setValue("01").build())
        .setCreatedAfterMs(0)
        .setLimit(500)
        .build();
  }
}
