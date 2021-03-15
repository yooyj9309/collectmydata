package com.banksalad.collectmydata.referencebank.common.db.repository;

import com.banksalad.collectmydata.referencebank.common.db.entity.OrganizationUserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, Long> {

  Optional<OrganizationUserEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
