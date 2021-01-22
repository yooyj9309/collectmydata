package com.banksalad.collectmydata.oauth.common.repository;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;
import com.banksalad.collectmydata.oauth.common.redis.OauthRedisTemplate;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class UserRedisRepositoryImpl implements UserRedisRepository {

  private final OauthRedisTemplate oauthRedisTemplate;

  private static final Long DAYS = 7L;
  private static final TimeUnit TIME_UNIT = TimeUnit.DAYS;

  public UserRedisRepositoryImpl(OauthRedisTemplate oauthRedisTemplate) {
    this.oauthRedisTemplate = oauthRedisTemplate;
  }

  @Override
  public Optional<UserEntity> getUserInfo(String key) {
    return Optional.of((UserEntity) oauthRedisTemplate.get(key));
  }

  @Override
  public Boolean setUserInfo(String key, UserEntity value) {
    return oauthRedisTemplate.setIfAbsent(key, value, DAYS, TIME_UNIT);
  }
}
