package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;

import java.util.List;

public interface AccountProductRepository extends JpaRepository<AccountProductEntity, Long> {

  List<AccountProductEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId,
      String organizationId, String accountNum);

  void deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId, String organizationId,
      String accountNum);
}
