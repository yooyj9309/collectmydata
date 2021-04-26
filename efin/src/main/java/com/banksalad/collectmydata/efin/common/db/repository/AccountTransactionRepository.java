package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountTransactionEntity;

import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

  Optional<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobNameAndTransNumAndTransDtime(
      Long banksaladUserId, String organizationId, String subKey, String fobName, String transNum, String transDtime);
}
