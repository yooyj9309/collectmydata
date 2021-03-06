package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
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

      String uniqueTransNo = generateUniqueTransNo(irpAccountTransaction);
      int transactionYearMonth = generateTransactionYearMonth(irpAccountTransaction);

      irpAccountTransactionRepository
          .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNo(
              transactionYearMonth, executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(),
              irpAccountSummary.getSeqno(), uniqueTransNo)
          .ifPresentOrElse(irpAccountTransactionEntity -> {
              },
              () -> {
                IrpAccountTransactionEntity irpAccountTransactionEntity = irpAccountTransactionMapper
                    .dtoToEntity(irpAccountTransaction);
                irpAccountTransactionEntity
                    .setTransactionYearMonth(transactionYearMonth);
                irpAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
                irpAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
                irpAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
                irpAccountTransactionEntity.setAccountNum(irpAccountSummary.getAccountNum());
                irpAccountTransactionEntity.setSeqno(irpAccountSummary.getSeqno());
                irpAccountTransactionEntity.setUniqueTransNo(uniqueTransNo);

                irpAccountTransactionEntity.setCreatedBy(executionContext.getRequestedBy());
                irpAccountTransactionEntity.setUpdatedBy(executionContext.getRequestedBy());
                irpAccountTransactionEntity.setConsentId(executionContext.getConsentId());
                irpAccountTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());

                irpAccountTransactionRepository.save(irpAccountTransactionEntity);
              });
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

  private String generateUniqueTransNo(IrpAccountTransaction irpAccountTransaction) {

//  TODO: ???????????? ?????? ??????????????? ?????? ??????(?)
    String transDtime = irpAccountTransaction.getTransDtime();  // ????????????
    String transType = irpAccountTransaction.getTransType();    // ????????????(01:??????, 02:??????)
    String transAmt = irpAccountTransaction.getTransAmt().toString(); // ????????????

    return HashUtil.hashCat(transDtime, transType, transAmt);
  }

  private int generateTransactionYearMonth(IrpAccountTransaction irpAccountTransaction) {

    String transDtime = irpAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.parseInt(yearMonthString);
  }
}
