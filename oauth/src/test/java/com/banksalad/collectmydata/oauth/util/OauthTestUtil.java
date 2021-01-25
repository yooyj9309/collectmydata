package com.banksalad.collectmydata.oauth.util;

import com.banksalad.collectmydata.oauth.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.dto.OauthPageRequest;
import com.banksalad.collectmydata.oauth.dto.Organization;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;

public class OauthTestUtil {

  public static final int userId = 1;
  public static final String organizationCode = "000";
  public static final String os = "android";
  public static final String organizationId = "shinhancard";
  public static final String organizationObjectId = "objectId";


  public static UserAuthInfo generateUserAuthInfo() {
    return UserAuthInfo.builder()
        .banksaladUserId(userId)
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

  public static OauthPageRequest generateOauthPageRequest() {
    OauthPageRequest request = new OauthPageRequest();
    request.setOrganizationObjectId(organizationObjectId);
    return request;
  }
}
