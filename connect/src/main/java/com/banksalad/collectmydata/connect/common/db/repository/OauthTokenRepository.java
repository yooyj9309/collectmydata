package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;

import java.util.List;
import java.util.Optional;

public interface OauthTokenRepository extends JpaRepository<OauthTokenEntity, Long> {

  Optional<OauthTokenEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId,
      String organizationId);

  List<OauthTokenEntity> findAllByBanksaladUserId(Long banksaladUserId);
}
