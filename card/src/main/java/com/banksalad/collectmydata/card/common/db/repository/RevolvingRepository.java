package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;

import java.util.List;

public interface RevolvingRepository extends JpaRepository<RevolvingEntity, Long> {

  List<RevolvingEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from RevolvingEntity r where r.banksaladUserId = ?1 and r.organizationId = ?2 and r.revolvingMonth = ?3")
  void deleteAllByBanksaladUserIdAndOrganizationIdAndRevolvingMonthInQuery(long banksaladUserId, String organizationId, int revolvingMonth);
}
