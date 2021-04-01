package com.banksalad.collectmydata.card.common.db.repository;

import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevolvingRepository extends JpaRepository<RevolvingEntity, Long> {

  List<RevolvingEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  void deleteByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
