package com.banksalad.collectmydata.irp.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionResponse;

public class Executions {

  public static final Execution irp_get_accounts =
      Execution.create()
          .exchange(Apis.irp_get_accounts)
          .as(IrpAccountSummariesResponse.class)
          .build();

  public static final Execution irp_get_basic =
      Execution.create()
          .exchange(Apis.irp_get_basic)
          .as(IrpAccountBasicResponse.class)
          .build();

  public static final Execution irp_get_detail =
      Execution.create()
          .exchange(Apis.irp_get_detail)
          .as(IrpAccountDetailsResponse.class)
          .build();

  public static final Execution irp_get_transactions =
      Execution.create()
          .exchange(Apis.irp_get_transactions)
          .as(IrpAccountTransactionResponse.class)
          .build();
}
