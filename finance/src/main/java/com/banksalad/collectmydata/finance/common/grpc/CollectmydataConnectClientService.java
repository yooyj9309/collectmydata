package com.banksalad.collectmydata.finance.common.grpc;

import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;

public interface CollectmydataConnectClientService {

  Organization getOrganization(String organizationId);

  Organization getOrganizationByOrganizationGuid(String organizationGuId);

  OauthToken getAccessToken(long banksaladUserId, String organizationId);
}
