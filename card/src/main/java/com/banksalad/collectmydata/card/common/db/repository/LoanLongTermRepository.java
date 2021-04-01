package com.banksalad.collectmydata.card.common.db.repository;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanLongTermRepository extends JpaRepository<LoanLongTermEntity, Long> {

  List<LoanLongTermEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  @Transactional
  @Modifying
  @Query("delete from LoanLongTermEntity llt where llt.banksaladUserId = ?1 and llt.organizationId = ?2")
  void deleteByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
