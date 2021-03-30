package com.banksalad.collectmydata.connect.common.mapper;

import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OauthTokenMapper {

  @Mapping(target = "accessTokenExpiresIn", source = "expiresIn")
  OauthTokenEntity dtoToEntity(GetOauthTokenResponse getOauthTokenResponse, @MappingTarget OauthTokenEntity oauthTokenEntity);
}
