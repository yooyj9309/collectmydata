package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;

import java.util.Optional;

public interface OrganizationClientRepository extends JpaRepository<OrganizationClientEntity, Long> {

  Optional<OrganizationClientEntity> findByOrganizationId(String organizationId);
}
