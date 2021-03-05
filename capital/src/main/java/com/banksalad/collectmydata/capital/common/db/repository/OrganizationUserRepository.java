package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.OrganizationUserEntity;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, Long> {

  Boolean existsByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
