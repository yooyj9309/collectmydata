package com.banksalad.collectmydata.oauth.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.grpc.client.ConnectClient;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  private final ConnectClient connectClient;

  @Override
  public Organization getOrganizationByOrganizationGuid(String organizationGuid) {
    // connect Client에 조회하기전에 한번에 다가져오거나 캐시하는게 어떨까
    return organizationAssembler(connectClient.getOrganization(organizationGuid), organizationGuid);
  }

  @Override
  public void issueToken(UserEntity userEntity, String authorizationCode) {
    connectClient.issueToken(userEntity.getBanksaladUserId(), userEntity.getOrganizationId(), authorizationCode);
  }

  private Organization organizationAssembler(GetOrganizationResponse response, String organizationGuid) {
    return Organization.builder()
        .mydataSector(MydataSector.getSector(response.getSector()))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .organizationHost(response.getDomain())
        .industry(Industry.getIndustry(response.getIndustry()))
        .organizationGuid(organizationGuid)
        .build();
  }

}
