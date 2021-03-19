package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailRequest;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IrpAccountDetailInfoRequestHelper implements
    AccountInfoRequestHelper<IrpAccountDetailRequest, IrpAccountSummary> {

  private static final int PAGING_MAXIMUM_LIMIT = 500;

  private final IrpAccountSummaryService irpAccountSummaryService;

  @Override
  public List<IrpAccountSummary> listSummaries(ExecutionContext executionContext) {

    return irpAccountSummaryService
        .listConsentedAccountSummaries(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());
  }

  @Override
  public IrpAccountDetailRequest make(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary) {

    return IrpAccountDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode()) // TODO:
        .accountNum(irpAccountSummary.getAccountNum())
        .seqno(irpAccountSummary.getSeqno())
        .searchTimestamp(irpAccountSummary.getDetailSearchTimestamp())
        .limit(PAGING_MAXIMUM_LIMIT)
        .build();
  }
}
