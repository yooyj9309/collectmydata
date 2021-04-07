package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;

import java.util.Optional;

public interface ConsentRepository extends JpaRepository<ConsentEntity, Long> {

  Optional<ConsentEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
