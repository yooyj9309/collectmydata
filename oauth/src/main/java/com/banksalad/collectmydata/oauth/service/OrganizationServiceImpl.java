package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.grpc.client.ConnectClient;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  private final ConnectClient connectClient;

  @PostConstruct
  public void init() {
    // connect에서 가져온 값 저장.
    // 해당부분을 사용하지않는다면 해당부분은 제거해도 무방.
  }

  @Override
  public Organization getOrganizationByObjectId(String organizationObjectId) {
    // connect Client에 조회하기전에 한번에 다가져오거나 캐시하는게 어떨까
    return organizationAssembler(connectClient.getOrganization(organizationObjectId), organizationObjectId);
  }

  @Override
  public void issueToken(UserEntity userEntity, String authorizationCode) {
    connectClient.issueToken(userEntity.getBanksaladUserId(), userEntity.getOrganizationId(), authorizationCode);
  }

  private Organization organizationAssembler(GetOrganizationResponse response, String organizationObjectId) {
    return Organization.builder()
        .mydataSector(MydataSector.getSector(response.getSector()))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .organizationHost(response.getDomain())
        .industry(response.getIndustry())
        .organizationObjectId(organizationObjectId)
        .build();
  }

}
