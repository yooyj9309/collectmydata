package com.banksalad.collectmydata.connect.common.db.repository;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationOauthTokenRepository extends JpaRepository<OrganizationOauthTokenEntity, Long> {

  Optional<OrganizationOauthTokenEntity> findByOrganizationId(String organizationId);
}
