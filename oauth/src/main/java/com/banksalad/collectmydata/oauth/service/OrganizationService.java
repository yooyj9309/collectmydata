package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.dto.Organization;

public interface OrganizationService {

  Organization getOrganizationByOrganizationGuid(String organizationGuid);

  void issueToken(UserEntity userEntity, String authorizationCode);
}
