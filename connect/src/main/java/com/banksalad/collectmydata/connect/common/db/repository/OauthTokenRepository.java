package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;

import java.util.Optional;

public interface OauthTokenRepository extends JpaRepository<OauthTokenEntity, Long> {

  Optional<OauthTokenEntity> findByBanksaladUserIdAndOrganizationIdAndIsExpired(Long banksaladUserId,
      String organizationId, boolean isExpired);
}
