package com.banksalad.collectmydata.oauth.common.repository;

import com.banksalad.collectmydata.oauth.common.db.UserEntity;

import java.util.Optional;

public interface UserRedisRepository {

  public Optional<UserEntity> getUserInfo(String key);

  public Boolean setUserInfo(String key, UserEntity value);
}
