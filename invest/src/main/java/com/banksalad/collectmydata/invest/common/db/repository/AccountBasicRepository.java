package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;

import java.util.List;
import java.util.Optional;

public interface AccountBasicRepository extends JpaRepository<AccountBasicEntity, Long> {

  Optional<AccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId, String organizationId,
      String accountNum);

  List<AccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
