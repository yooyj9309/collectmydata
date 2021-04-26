package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.irp.api.AccountInfoRequestPaginationHelper;
import com.banksalad.collectmydata.irp.api.AccountInfoResponsePaginationHelper;
import com.banksalad.collectmydata.irp.api.AccountInfoServicePagination;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrpAccountServiceImpl implements IrpAccountService {

  private final AccountInfoService<IrpAccountSummary, IrpAccountBasicRequest, IrpAccountBasic> irpAccountBasicInfoService;

  private final AccountInfoRequestHelper<IrpAccountBasicRequest, IrpAccountSummary> irpAccountBasicInfoRequestHelper;

  private final AccountInfoResponseHelper<IrpAccountSummary, IrpAccountBasic> irpAccountBasicInfoResponseHelper;

  private final AccountInfoServicePagination<IrpAccountSummary, IrpAccountDetailRequest, List<IrpAccountDetail>> irpAccountDetailInfoService;

  private final AccountInfoRequestPaginationHelper<IrpAccountDetailRequest, IrpAccountSummary> irpAccountDetailInfoRequestHelper;

  private final AccountInfoResponsePaginationHelper<IrpAccountDetailRequest, IrpAccountSummary, List<IrpAccountDetail>> irpAccountDetailInfoResponseHelper;

  @Override
  public void getIrpAccountBasics(ExecutionContext executionContext) {
    irpAccountBasicInfoService
        .listAccountInfos(executionContext, Executions.irp_get_basic, irpAccountBasicInfoRequestHelper,
            irpAccountBasicInfoResponseHelper);
  }

  @Override
  public void listIrpAccountDetails(ExecutionContext executionContext) {
    irpAccountDetailInfoService
        .listAccountInfos(executionContext, Executions.irp_get_detail, irpAccountDetailInfoRequestHelper,
            irpAccountDetailInfoResponseHelper);
  }
}
