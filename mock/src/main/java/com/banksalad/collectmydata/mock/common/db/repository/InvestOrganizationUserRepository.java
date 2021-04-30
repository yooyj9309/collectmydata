package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestOrganizationUserEntity;

import java.util.Optional;

public interface InvestOrganizationUserRepository extends JpaRepository<InvestOrganizationUserEntity, Long> {


  Optional<InvestOrganizationUserEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId,
      String organizationId);
}
