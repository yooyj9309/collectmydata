package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface LoanSummaryRepository extends JpaRepository<LoanSummaryEntity, Long> {

  Optional<LoanSummaryEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from LoanSummaryEntity ls where ls.banksaladUserId = ?1 and ls.organizationId = ?2")
  void deleteAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  List<LoanSummaryEntity> findAllByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
