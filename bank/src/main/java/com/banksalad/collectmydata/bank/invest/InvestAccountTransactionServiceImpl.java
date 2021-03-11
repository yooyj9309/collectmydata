package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountTransactionMapper;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateRange;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.util.DateUtil.utcLocalDateTimeToKstLocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestAccountTransactionServiceImpl implements InvestAccountTransactionService {

  private static final String[] EXCLUDE_FIELDS = {"syncedAt", "createdAt", "updatedAt", "createdBy", "updatedBy"};

  private static final int MINUS_YEAR = 1; // TODO yooyj9309 최초 조회 시 5년으로 설정 할지 논의 필요 (동적으로 변경하도록 작성?)
  private static final int INTERVAL_MONTH = 3; // TODO yooyj9309 거래내역 요청 시 월별 간격 논의 필요 (동적으로 변경하도록 작성?)

  private final ExternalApiService externalApiService;
  private final AccountSummaryService accountSummaryService;
  private final InvestAccountTransactionRepository investAccountTransactionRepository;
  private final InvestAccountTransactionMapper investAccountTransactionMapper = Mappers
      .getMapper(InvestAccountTransactionMapper.class);

  @Override
  public List<InvestAccountTransaction> listInvestAccountTransactions(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    Organization organization = getOrganization(executionContext);

    boolean isExceptionOccurred = false;
    List<InvestAccountTransaction> investAccountTransactions = new ArrayList<>();

    for (AccountSummary accountSummary : accountSummaries) {
      LocalDateTime transactionSyncedAt = Optional.ofNullable(accountSummary.getTransactionSyncedAt())
          .orElse(executionContext.getSyncStartedAt().minusYears(MINUS_YEAR).plusDays(1L));

      LocalDate startDate = utcLocalDateTimeToKstLocalDateTime(transactionSyncedAt).toLocalDate();
      LocalDate endDate = utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt()).toLocalDate();

      //call investAccountTransactionsResponse by dateRange

      for (DateRange dateRange : DateUtil.splitDate(startDate, endDate, INTERVAL_MONTH)) {
        ListInvestAccountTransactionsResponse response;
        try {
          response = externalApiService
              .listInvestAccountTransactions(executionContext, accountSummary, organization, dateRange);
        } catch (Exception e) {
          log.error("Failed to send invest account transaction", e);
          isExceptionOccurred = true;
          continue;
        }
        investAccountTransactions.addAll(response.getInvestAccountTransactions());
      }

      //save investAccountTransactions
      for (InvestAccountTransaction investAccountTransaction : investAccountTransactions) {
        try {
          saveInvestAccountTransaction(executionContext, accountSummary, investAccountTransaction);
        } catch (Exception e) {
          log.error("Failed to save invest account transaction", e);
          isExceptionOccurred = true;
        }
      }

      if (!isExceptionOccurred) {
        accountSummaryService.updateTransactionSyncedAt(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary,
            executionContext.getSyncStartedAt());
      }
    }

    if (isExceptionOccurred) {
      log.info("Don't update BA07 API UserSyncStatus");
    } else {
      log.info("Update BA07 API UserSyncStatus");
    }

    return investAccountTransactions;
  }

  private void saveInvestAccountTransaction(ExecutionContext executionContext, AccountSummary accountSummary,
      InvestAccountTransaction investAccountTransaction) {
    InvestAccountTransactionEntity investAccountTransactionEntity = investAccountTransactionMapper
        .dtoToEntity(investAccountTransaction);
    investAccountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(investAccountTransaction));
    investAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
    investAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    investAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
    investAccountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
    investAccountTransactionEntity.setSeqno(accountSummary.getSeqno());
    investAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(investAccountTransaction));

    InvestAccountTransactionEntity existingInvestAccountTransactionEntity = investAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
            investAccountTransactionEntity.getBanksaladUserId(),
            investAccountTransactionEntity.getOrganizationId(),
            investAccountTransactionEntity.getAccountNum(),
            investAccountTransactionEntity.getSeqno(),
            investAccountTransactionEntity.getUniqueTransNo(),
            investAccountTransactionEntity.getTransactionYearMonth()
        );

    if (existingInvestAccountTransactionEntity != null) {
      investAccountTransactionEntity.setId(existingInvestAccountTransactionEntity.getId());
    }

    if (!ObjectComparator
        .isSame(investAccountTransactionEntity, existingInvestAccountTransactionEntity, EXCLUDE_FIELDS)) {
      investAccountTransactionRepository.save(investAccountTransactionEntity);
    }
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020")
        .build();
  }

  // TODO: TransNo랑 YearMonth 만드는 method Util로 빼거나 common에서 관리하도록 수정할 예정
  private int generateTransactionYearMonth(InvestAccountTransaction investAccountTransaction) {
    String transDtime = investAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.parseInt(yearMonthString);
  }

  private String generateUniqueTransNo(InvestAccountTransaction investAccountTransaction) {
    String transDtime = investAccountTransaction.getTransDtime();
    String transAmtString = investAccountTransaction.getTransAmt().toString();
    String balanceAmtString = investAccountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transAmtString, balanceAmtString);
  }
}
