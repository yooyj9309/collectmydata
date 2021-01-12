package com.banksalad.collectmydata.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class OauthServiceImpl implements OauthService{
    @Override
    public String keepUserInfo(Long userId, String organizationCode, Map<String, String> headers) {
        return null;
    }

    @Override
    public void getUserInfo(String state) {

    }

    @Override
    public String getRedirectUrl(String state, String organizationCode) {
        // redirect 만드는 로직 추가
        return null;
    }

    @Override
    public void approve(String state, String organizationCode) {
        // 유저 정보를 통해 connect에 토급발급요청
        // 에러가 있는 경우 throw and logging
    }
}
