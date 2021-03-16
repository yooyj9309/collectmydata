package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.OrganizationUserEntity;

import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, Long> {

  Optional<OrganizationUserEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
