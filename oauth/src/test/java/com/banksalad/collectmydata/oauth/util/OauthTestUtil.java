package com.banksalad.collectmydata.oauth.util;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;

public class OauthTestUtil {

  public static final long banksaladUserId = 1L;
  public static final String organizationCode = "000";
  public static final String os = "android";
  public static final String organizationId = "shinhancard";
  public static final String organizationObjectId = "objectId";
  public static final String state = "state";


  public static UserAuthInfo generateUserAuthInfo() {
    return UserAuthInfo.builder()
        .banksaladUserId(banksaladUserId)
        .os(os)
        .build();
  }

  public static Organization generateOrganization(MydataSector mydataSector) {
    return Organization.builder()
        .organizationId(OauthTestUtil.organizationId)
        .organizationCode(OauthTestUtil.organizationCode)
        .mydataSector(mydataSector)
        .organizationHost("https://testdomain.com/testapi")
        .build();
  }

  public static GetOrganizationResponse getOrganizationResponseAssembler(MydataSector mydataSector) {
    return GetOrganizationResponse.newBuilder()
        .setOrganizationId(organizationId)
        .setOrganizationCode(organizationCode)
        .setSector(mydataSector.name())
        .setDomain("https://testdomain.com/testapi")
        .build();
  }

  public static OauthPageRequest generateOauthPageRequest() {
    OauthPageRequest request = new OauthPageRequest();
    request.setOrganizationObjectId(organizationObjectId);
    return request;
  }

  public static UserEntity userEntityAssembler() {
    return UserEntity.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organizationCode)
        .os(os)
        .build();
  }
}
