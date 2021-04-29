package com.banksalad.collectmydata.mock.irp.service;

import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasicSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetail;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetailSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummarySearch;

import java.util.List;

public interface IrpService {

  List<IrpAccountSummary> getIrpAccountSummaryList(IrpAccountSummarySearch irpAccountBasicSearch);

  IrpAccountBasic getIrpAccountBasic(IrpAccountBasicSearch irpAccountBasicSearch);

  int getIrpAccountDetailCount(IrpAccountDetailSearch irpAccountDetailSearch);

  List<IrpAccountDetail> getIrpAccountDetailList(IrpAccountDetailSearch irpAccountDetailSearch);
}
