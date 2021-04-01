package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;

import java.util.Optional;

public interface OrganizationOauthTokenRepository extends JpaRepository<OrganizationOauthTokenEntity, Long> {

  Optional<OrganizationOauthTokenEntity> findBySecretType(String secretType);
}
