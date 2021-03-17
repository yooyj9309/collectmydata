package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IrpAccountBasicInfoRequestHelper implements
    AccountInfoRequestHelper<IrpAccountBasicRequest, IrpAccountSummary> {

  private final IrpAccountSummaryService irpAccountSummaryService;

  @Override
  public List<IrpAccountSummary> listSummaries(ExecutionContext executionContext) {

    return irpAccountSummaryService
        .listConsentedAccountSummaries(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());
  }

  @Override
  public IrpAccountBasicRequest make(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary) {

    return IrpAccountBasicRequest.builder()
        .orgCode("020")  // TODO:
        .searchTimestamp(irpAccountSummary.getBasicSearchTimestamp())
        .accountNum(irpAccountSummary.getAccountNum())
        .seqno(irpAccountSummary.getSeqno())
        .build();
  }
}
