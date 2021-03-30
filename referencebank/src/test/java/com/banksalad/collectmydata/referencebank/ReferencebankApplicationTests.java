package com.banksalad.collectmydata.referencebank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionRequest;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.referencebank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.referencebank.summary.dto.ListAccountSummariesRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

@SpringBootTest
class ReferencebankApplicationTests {

  @Autowired
  private AccountInfoService<IrpAccountSummary, IrpAccountBasicRequest, IrpAccountBasic> irpAccountBasicInfoService;

  @Autowired
  private AccountInfoRequestHelper<IrpAccountBasicRequest, IrpAccountSummary> irpAccountBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<IrpAccountSummary, IrpAccountBasic> irpAccountBasicInfoResponseHelper;

  @Autowired
  private AccountInfoService<IrpAccountSummary, IrpAccountDetailRequest, List<IrpAccountDetail>> irpAccountDetailInfoService;

  @Autowired
  private AccountInfoRequestHelper<IrpAccountDetailRequest, IrpAccountSummary> irpAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<IrpAccountSummary, List<IrpAccountDetail>> irpAccountDetailInfoResponseHelper;

  @Autowired
  private SummaryService<ListIrpAccountSummariesRequest, IrpAccountSummary> irpAccountSummaryService;

  @Autowired
  private SummaryRequestHelper<ListIrpAccountSummariesRequest> irpAccountSummariesRequestHelper;

  @Autowired
  private SummaryResponseHelper<IrpAccountSummary> irpAccountSummaryResponseHelper;

  @Autowired
  private TransactionApiService<IrpAccountSummary, IrpAccountTransactionRequest, IrpAccountTransaction> irpAccountTransactionApiService;

  @Autowired
  private TransactionRequestHelper<IrpAccountSummary, IrpAccountTransactionRequest> irpAccountTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<IrpAccountSummary, IrpAccountTransaction> irpAccountTransactionResponseHelper;

  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  @Autowired
  private UserSyncStatusService userSyncStatusService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> accountInfoRequestHelper;

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicApiService;

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;

  @Autowired
  private TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositTransactionApiService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> bankSummaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> bankSummaryResponseHelper;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountInfoBasicResponseHelper;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  @Autowired
  private TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;

  @Test
  void contextLoads() {
  }

}
