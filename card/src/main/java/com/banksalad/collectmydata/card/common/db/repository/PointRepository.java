package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.PointEntity;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

  Optional<PointEntity> findByBanksaladUserIdAndOrganizationIdAndPointName(long banksaladUserId, String organizationId,
      String pointName);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from PointEntity p where p.banksaladUserId = ?1 and p.organizationId = ?2")
  void deleteAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  List<PointEntity> findAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
