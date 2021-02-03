package com.banksalad.collectmydata.connect.common.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenResponse;

public class Executions {

  public static final Execution support_get_access_token =
      Execution.create()
          .exchange(Apis.support_get_access_token)
          .as(FinanceOrganizationTokenResponse.class)
          .build();

  public static final Execution support_get_organization_info =
      Execution.create()
          .exchange(Apis.support_get_organization_info)
          .as(FinanceOrganizationResponse.class)
          .build();

  public static final Execution support_get_organization_service_info =
      Execution.create()
          .exchange(Apis.support_get_organization_service_info)
          .as(FinanceOrganizationServiceResponse.class)
          .build();
}
