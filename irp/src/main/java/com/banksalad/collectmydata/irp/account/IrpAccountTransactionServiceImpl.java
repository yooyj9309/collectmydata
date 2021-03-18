package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrpAccountTransactionServiceImpl implements
    IrpAccountTransactionService {

  private final TransactionApiService<IrpAccountSummary, IrpAccountTransactionRequest, IrpAccountTransaction> irpAccountTransactionApiService;

  private final TransactionRequestHelper<IrpAccountSummary, IrpAccountTransactionRequest> irpAccountTransactionRequestHelper;

  private final TransactionResponseHelper<IrpAccountSummary, IrpAccountTransaction> irpAccountTransactionResponseHelper;

  @Override
  public List<IrpAccountTransaction> listTransactions(ExecutionContext executionContext) {

    return irpAccountTransactionApiService
        .listTransactions(executionContext, Executions.irp_get_transactions, irpAccountTransactionRequestHelper,
            irpAccountTransactionResponseHelper);
  }
}
