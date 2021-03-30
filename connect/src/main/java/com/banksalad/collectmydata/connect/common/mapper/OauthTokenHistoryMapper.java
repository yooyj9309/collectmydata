package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OauthTokenHistoryMapper {

  @Mapping(target = "id", ignore = true)
  OauthTokenHistoryEntity toHistoryEntity(OauthTokenEntity oauthTokenEntity);
}
