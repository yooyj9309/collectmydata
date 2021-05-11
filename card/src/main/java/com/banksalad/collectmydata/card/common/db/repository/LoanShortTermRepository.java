package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;

import java.util.List;
import java.util.Optional;

public interface LoanShortTermRepository extends JpaRepository<LoanShortTermEntity, Long> {

  List<LoanShortTermEntity> findAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  Optional<LoanShortTermEntity> findByBanksaladUserIdAndOrganizationIdAndLoanDtime(long banksaladUserId, String organizationId, String loanDtime);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from LoanShortTermEntity lst where lst.banksaladUserId = ?1 and lst.organizationId = ?2")
  void deleteAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
