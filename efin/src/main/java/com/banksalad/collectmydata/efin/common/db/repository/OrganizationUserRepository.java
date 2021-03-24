package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.OrganizationUserEntity;

import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, Long> {

  Optional<OrganizationUserEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

}
