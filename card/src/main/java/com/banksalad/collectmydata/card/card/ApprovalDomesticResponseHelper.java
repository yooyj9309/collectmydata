package com.banksalad.collectmydata.card.card;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticResponse;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalDomesticRepository;
import com.banksalad.collectmydata.card.common.mapper.ApprovalDomesticMapper;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class ApprovalDomesticResponseHelper implements TransactionResponseHelper<CardSummary, ApprovalDomestic> {

  private final CardSummaryService cardSummaryService;
  private final ApprovalDomesticRepository approvalDomesticRepository;

  private final ApprovalDomesticMapper approvalDomesticMapper = Mappers.getMapper(ApprovalDomesticMapper.class);

  @Override
  public List<ApprovalDomestic> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    ListApprovalDomesticResponse response = (ListApprovalDomesticResponse) transactionResponse;
    return response.getApprovalDomestics();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, CardSummary cardSummary,
      List<ApprovalDomestic> approvalDomestics) {
    approvalDomestics.forEach(approvalDomestic -> {
      ApprovalDomesticEntity approvalDomesticEntity = approvalDomesticMapper.dtoToEntity(approvalDomestic);
      if (approvalDomestic.getTotalInstallCnt() < 1) {
        approvalDomesticEntity.setTotalInstallCnt(null);
      }
      approvalDomesticEntity
          .setApprovalYearMonth(NumberUtils.toInt(StringUtils.left(approvalDomestic.getApprovedDtime(), 6)));
      approvalDomesticEntity.setSyncedAt(executionContext.getSyncStartedAt());
      approvalDomesticEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      approvalDomesticEntity.setOrganizationId(executionContext.getOrganizationId());
      approvalDomesticEntity.setCardId(cardSummary.getCardId());

      ApprovalDomesticEntity existingApprovalDomesticEntity = approvalDomesticRepository
          .findByApprovalYearMonthAndBanksaladUserIdAndOrganizationIdAndCardIdAndApprovedNum(
              approvalDomesticEntity.getApprovalYearMonth(),
              approvalDomesticEntity.getBanksaladUserId(),
              approvalDomesticEntity.getOrganizationId(),
              approvalDomesticEntity.getCardId(),
              approvalDomesticEntity.getApprovedNum()
          )
          .map(foundApprovalDomesticEntity -> {
            approvalDomesticEntity.setId(foundApprovalDomesticEntity.getId());
            return foundApprovalDomesticEntity;
          })
          .orElseGet(() -> ApprovalDomesticEntity.builder().build());

      if (!ObjectComparator.isSame(approvalDomesticEntity, existingApprovalDomesticEntity, ENTITY_EXCLUDE_FIELD)) {
        approvalDomesticRepository.save(approvalDomesticEntity);
      }
    });
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, CardSummary cardSummary,
      LocalDateTime syncStartedAt) {
    cardSummaryService
        .updateApprovalDomesticTransactionSyncedAt(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), cardSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, CardSummary cardSummary, String responseCode) {
    cardSummaryService
        .updateApprovalDomesticTransactionResponseCode(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), cardSummary, responseCode);
  }
}
