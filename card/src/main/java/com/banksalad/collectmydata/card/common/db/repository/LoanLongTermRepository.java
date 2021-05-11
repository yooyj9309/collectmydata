package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;

import java.util.List;
import java.util.Optional;

public interface LoanLongTermRepository extends JpaRepository<LoanLongTermEntity, Long> {

  List<LoanLongTermEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  Optional<LoanLongTermEntity> findByBanksaladUserIdAndOrganizationIdAndLoanDtimeAndLoanCnt(long banksaladUserId,
      String organizationId, String loanDtime, Integer loanCnt);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from LoanLongTermEntity llt where llt.banksaladUserId = ?1 and llt.organizationId = ?2")
  void deleteAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
