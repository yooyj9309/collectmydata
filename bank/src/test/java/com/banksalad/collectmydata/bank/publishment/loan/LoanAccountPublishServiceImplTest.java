package com.banksalad.collectmydata.bank.publishment.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionInterestRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionRepository;
import com.banksalad.collectmydata.bank.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountTransactionsRequest;
import com.google.protobuf.StringValue;
import org.assertj.core.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("대출상품계좌 publish service 테스트")
@Transactional
class LoanAccountPublishServiceImplTest {

  @Autowired
  private LoanAccountPublishService loanAccountPublishService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private LoanAccountBasicRepository loanAccountBasicRepository;

  @Autowired
  private LoanAccountDetailRepository loanAccountDetailRepository;

  @Autowired
  private LoanAccountTransactionRepository loanAccountTransactionRepository;

  @Autowired
  private LoanAccountTransactionInterestRepository loanAccountTransactionInterestRepository;

  private final String[] ENTITY_IGNORE_FIELD = {"id", "banksaladUserId", "organizationId", "syncedAt", "createdBy",
      "updatedBy", "basicResponseCode", "detailResponseCode", "transactionResponseCode", "transactionYearMonth",
      "uniqueTransNo"};

  @Test
  @DisplayName("대출상품계좌 기본정보 정상 조회 후 응답 성공")
  void getLoanAccountBasicResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    loanAccountBasicRepository.save(getLoanAccountBasicEntity());

    // when
    List<LoanAccountBasicResponse> loanAccountBasicResponses = loanAccountPublishService
        .getLoanAccountBasicResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(loanAccountBasicResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(loanAccountBasicRepository.findAll());
  }

  @Test
  @DisplayName("대출상품계좌 추가정보 정상 조회 후 응답 성공")
  void getLoanAccountDetailResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    loanAccountDetailRepository.save(getLoanAccountDetailEntity());

    // when
    List<LoanAccountDetailResponse> loanAccountDetailResponses = loanAccountPublishService
        .getLoanAccountDetailResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(loanAccountDetailResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(loanAccountDetailRepository.findAll());
  }

  @Test
  @DisplayName("대출상품계좌 거래내역 정상 조회 후 응답 성공")
  void getLoanAccountTransactionResponses_success() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    loanAccountTransactionRepository.save(getLoanAccountTransactionEntity());
    loanAccountTransactionInterestRepository.save(getLoanAccountTransactionInterestEntity());

    ListBankLoanAccountTransactionsRequest request = getListBankLoanAccountTransactionsRequest();

    // when
    List<LoanAccountTransactionResponse> loanAccountTransactionResponses = loanAccountPublishService
        .getLoanAccountTransactionResponse(BANKSALAD_USER_ID, ORGANIZATION_ID,
            request.getAccountNum(),
            request.getSeqno().getValue(),
            LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC),
            Long.valueOf(request.getLimit()).intValue());

    // then
    assertThat(loanAccountTransactionResponses).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields("loanAccountTransactionInterests")
        .isEqualTo(loanAccountTransactionRepository.findAll());
    assertThat(loanAccountTransactionResponses.get(0).getLoanAccountTransactionInterests()).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(loanAccountTransactionInterestRepository.findAll());
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

  private LoanAccountBasicEntity getLoanAccountBasicEntity() {
    return LoanAccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .holderName("김뱅샐")
        .issueDate("20200101")
        .expDate(null)
        .lastOfferedRate(BigDecimal.valueOf(5.10))
        .repayDate(null)
        .repayMethod("03")
        .repayOrgCode(null)
        .repayAccountNum("1234567")
        .build();
  }

  private LoanAccountDetailEntity getLoanAccountDetailEntity() {
    return LoanAccountDetailEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .balanceAmt(BigDecimal.valueOf(1000.10))
        .loanPrincipal(BigDecimal.valueOf(3000.30))
        .nextRepayDate("20201231")
        .build();
  }

  private LoanAccountTransactionEntity getLoanAccountTransactionEntity() {
    return LoanAccountTransactionEntity.builder()
        .transactionYearMonth(20200101)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .seqno("01")
        .uniqueTransNo("uniqueTransNo")
        .transDtime("20200101000000")
        .transNo(null)
        .transType("01")
        .transAmt(BigDecimal.valueOf(1000.10))
        .balanceAmt(BigDecimal.valueOf(1000.10))
        .principalAmt(BigDecimal.valueOf(1000.10))
        .intAmt(BigDecimal.valueOf(1000.10))
        .build();
  }

  private LoanAccountTransactionInterestEntity getLoanAccountTransactionInterestEntity() {
    return LoanAccountTransactionInterestEntity.builder()
        .transactionYearMonth(20200101)
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234567890")
        .uniqueTransNo("uniqueTransNo")
        .intNo(0)
        .intStartDate("20200101")
        .intEndDate("20201231")
        .intRate(BigDecimal.valueOf(1.100))
        .intType("01")
        .build();
  }

  private ListBankLoanAccountBasicsRequest getListBankLoanAccountBasicsRequest() {
    return ListBankLoanAccountBasicsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .build();
  }

  private ListBankLoanAccountDetailsRequest getListBankLoanAccountDetailsRequest() {
    return ListBankLoanAccountDetailsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .build();
  }

  private ListBankLoanAccountTransactionsRequest getListBankLoanAccountTransactionsRequest() {
    return ListBankLoanAccountTransactionsRequest.newBuilder()
        .setBanksaladUserId("1")
        .setOrganizationObjectid("objectid")
        .setAccountNum("1234567890")
        .setSeqno(StringValue.newBuilder().setValue("01").build())
        .setCreatedAfterMs(0)
        .setLimit(500)
        .build();
  }
}
