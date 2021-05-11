package com.banksalad.collectmydata.card.card.bill;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionRequestHelper;
import lombok.RequiredArgsConstructor;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;

@Component
@RequiredArgsConstructor
public class BillDetailRequestHelper implements BillTransactionRequestHelper<ListBillDetailRequest, BillBasic> {

  @Override
  public int getChargeMonth(BillBasic billBasic) {
    return billBasic.getChargeMonth();
  }

  @Override
  public String getSeqno(BillBasic billBasic) {
    return billBasic.getSeqno();
  }

  @Override
  public ListBillDetailRequest make(ExecutionContext executionContext, BillBasic billBasic, String nextPage) {
    return ListBillDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .seqno(billBasic.getSeqno())
        .chargeMonth(billBasic.getChargeMonth())
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
