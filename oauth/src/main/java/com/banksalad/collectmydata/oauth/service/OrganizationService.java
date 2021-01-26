package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.dto.Organization;

public interface OrganizationService {

  public Organization getOrganizationByObjectId(String organizationObjectId);

  public void issueToken(UserEntity userEntity, String authorizationCode);
}
