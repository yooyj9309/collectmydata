package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.PointEntity;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

  Optional<PointEntity> findByBanksaladUserIdAndOrganizationIdAndPointName(long banksaladUserId, String organizationId,
      String pointName);
}
