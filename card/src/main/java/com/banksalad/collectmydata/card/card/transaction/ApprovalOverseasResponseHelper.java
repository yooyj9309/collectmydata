package com.banksalad.collectmydata.card.card.transaction;

import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.card.dto.ListApprovalOverseasResponse;
import com.banksalad.collectmydata.card.common.db.entity.ApprovalOverseasEntity;
import com.banksalad.collectmydata.card.common.db.repository.ApprovalOverseasRepository;
import com.banksalad.collectmydata.card.common.mapper.ApprovalOverseasMapper;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class ApprovalOverseasResponseHelper implements TransactionResponseHelper<CardSummary, ApprovalOverseas> {

  private final CardSummaryService cardSummaryService;
  private final ApprovalOverseasRepository approvalOverseasRepository;

  private final ApprovalOverseasMapper approvalOverseasMapper = Mappers.getMapper(ApprovalOverseasMapper.class);

  @Override
  public List<ApprovalOverseas> getTransactionsFromResponse(TransactionResponse transactionResponse) {

    return ((ListApprovalOverseasResponse) transactionResponse).getApprovedList();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, CardSummary cardSummary,
      List<ApprovalOverseas> approvalOverseass) {

    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String cardId = cardSummary.getCardId();

    approvalOverseass.forEach(approvalOverseas -> {
      ApprovalOverseasEntity approvalOverseasEntity = approvalOverseasMapper.dtoToEntity(approvalOverseas);
      if (approvalOverseas.getCurrencyCode() == null) {
        approvalOverseasEntity.setCurrencyCode(CURRENCY_KRW);
      }
      approvalOverseasEntity
          .setApprovalYearMonth(NumberUtils.toInt(StringUtils.left(approvalOverseas.getApprovedDtime(), 6)));
      approvalOverseasEntity.setSyncedAt(syncedAt);
      approvalOverseasEntity.setBanksaladUserId(banksaladUserId);
      approvalOverseasEntity.setOrganizationId(organizationId);
      approvalOverseasEntity.setCardId(cardId);

      ApprovalOverseasEntity existingApprovalOverseasEntity = approvalOverseasRepository
          .findByApprovalYearMonthAndBanksaladUserIdAndOrganizationIdAndCardIdAndApprovedNumAndStatus(
              approvalOverseasEntity.getApprovalYearMonth(),
              approvalOverseasEntity.getBanksaladUserId(),
              approvalOverseasEntity.getOrganizationId(),
              approvalOverseasEntity.getCardId(),
              approvalOverseasEntity.getApprovedNum(),
              approvalOverseasEntity.getStatus()
          )
          .map(foundApprovalOverseasEntity -> {
            approvalOverseasEntity.setId(foundApprovalOverseasEntity.getId());
            return foundApprovalOverseasEntity;
          })
          .orElseGet(() -> ApprovalOverseasEntity.builder().build());

      if (!ObjectComparator.isSame(approvalOverseasEntity, existingApprovalOverseasEntity, ENTITY_EXCLUDE_FIELD)) {
        approvalOverseasRepository.save(approvalOverseasEntity);
      }
    });
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, CardSummary cardSummary,
      LocalDateTime syncStartedAt) {
    cardSummaryService
        .updateApprovalOverseasTransactionSyncedAt(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), cardSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, CardSummary cardSummary, String responseCode) {
    cardSummaryService
        .updateApprovalOverseasTransactionResponseCode(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), cardSummary, responseCode);
  }
}
