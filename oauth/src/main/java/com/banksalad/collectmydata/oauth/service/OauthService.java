package com.banksalad.collectmydata.oauth.service;


import java.util.Map;

public interface OauthService {
    public String keepUserInfo(Long userId, String organizationCode, Map<String,String> headers);
    public void getUserInfo(String state); // 작업하면서 return값 변경 예정

    public String getRedirectUrl(String state, String organizationCode);
    public void approve(String state, String organizationCode); // 작업하면서 return값 변경 예정
}
