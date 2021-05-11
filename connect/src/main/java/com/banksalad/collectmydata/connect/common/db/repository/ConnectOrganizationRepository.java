package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;

import java.util.Optional;

public interface ConnectOrganizationRepository extends JpaRepository<ConnectOrganizationEntity, Long> {

  Optional<ConnectOrganizationEntity> findByOrganizationId(String organizationId);

  Optional<ConnectOrganizationEntity> findByOrganizationGuid(String organizationGuid);

  Optional<ConnectOrganizationEntity> findByOrgCode(String orgCode);
}
