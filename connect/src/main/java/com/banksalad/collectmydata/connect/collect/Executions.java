package com.banksalad.collectmydata.connect.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;

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

  public static final Execution oauth_issue_token =
      Execution.create()
          .exchange(Apis.oauth_issue_token)
          .as(ExternalTokenResponse.class)
          .build();

  public static final Execution oauth_refresh_token =
      Execution.create()
          .exchange(Apis.oauth_refresh_token)
          .as(ExternalTokenResponse.class)
          .build();

  public static final Execution oauth_revoke_token =
      Execution.create()
          .exchange(Apis.oauth_revoke_token)
          .as(ExternalTokenResponse.class)
          .build();
}
