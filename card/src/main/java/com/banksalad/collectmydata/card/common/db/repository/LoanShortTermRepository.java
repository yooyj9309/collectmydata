package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;

import java.util.List;

public interface LoanShortTermRepository extends JpaRepository<LoanShortTermEntity, Long> {

  List<LoanShortTermEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  void deleteByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
