package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountTransactionRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionResponse;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountTransactionMapper;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IrpAccountTransactionResponseHelper implements
    TransactionResponseHelper<IrpAccountSummary, IrpAccountTransaction> {

  private final IrpAccountSummaryService irpAccountSummaryService;

  private final IrpAccountTransactionRepository irpAccountTransactionRepository;

  private final IrpAccountTransactionMapper irpAccountTransactionMapper = Mappers
      .getMapper(IrpAccountTransactionMapper.class);

  @Override
  public List<IrpAccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((IrpAccountTransactionResponse) transactionResponse).getIrpAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      List<IrpAccountTransaction> irpAccountTransactions) {

    for (IrpAccountTransaction irpAccountTransaction : irpAccountTransactions) {

      IrpAccountTransactionEntity irpAccountTransactionEntity = irpAccountTransactionMapper
          .dtoToEntity(irpAccountTransaction);
      irpAccountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(irpAccountTransaction));
      irpAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      irpAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      irpAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      irpAccountTransactionEntity.setAccountNum(irpAccountSummary.getAccountNum());
      irpAccountTransactionEntity.setSeqno(irpAccountSummary.getSeqno());
      irpAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(irpAccountTransactionEntity));

      // TODO : on-demand, scheduler
      irpAccountTransactionEntity.setCreatedBy(Optional.ofNullable(irpAccountTransactionEntity.getCreatedBy())
          .orElseGet(() -> String.valueOf(executionContext.getBanksaladUserId())));
      irpAccountTransactionEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      irpAccountTransactionEntity.setConsentId(executionContext.getConsentId());
      irpAccountTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());

      IrpAccountTransactionEntity existingAccountTransactionEntity = irpAccountTransactionRepository
          .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNo(
              irpAccountTransactionEntity.getTransactionYearMonth(), irpAccountTransactionEntity.getBanksaladUserId(),
              irpAccountTransactionEntity.getOrganizationId(), irpAccountTransactionEntity.getAccountNum(),
              irpAccountTransactionEntity.getSeqno(), irpAccountTransactionEntity.getUniqueTransNo())
          .orElse(null);

      if (existingAccountTransactionEntity != null) {
        irpAccountTransactionEntity.setId(existingAccountTransactionEntity.getId());
      }

      if (!ObjectComparator.isSame(irpAccountTransactionEntity, existingAccountTransactionEntity,
          FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
        irpAccountTransactionRepository.save(irpAccountTransactionEntity);
      }
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      LocalDateTime syncStartedAt) {

    irpAccountSummaryService.updateTransactionSyncedAt(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), irpAccountSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary,
      String responseCode) {

    irpAccountSummaryService.updateTransactionResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), irpAccountSummary, responseCode);
  }

  private String generateUniqueTransNo(IrpAccountTransactionEntity irpAccountTransactionEntity) {

//  TODO: 거래내역 아래 필드만으로 구분 불가(?)
    String transDtime = irpAccountTransactionEntity.getTransDtime();  // 거래일시
    String transType = irpAccountTransactionEntity.getTransType();    // 거래유형(01:입금, 02:지급)
    String transAmt = irpAccountTransactionEntity.getTransAmt().toString(); // 거래금액

    return HashUtil.hashCat(transDtime, transType, transAmt);
  }

  private int generateTransactionYearMonth(IrpAccountTransaction irpAccountTransaction) {

    String transDtime = irpAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.parseInt(yearMonthString);
  }
}
