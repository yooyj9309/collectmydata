package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;

import java.util.Optional;

public interface DepositAccountBasicRepository extends JpaRepository<DepositAccountBasicEntity, Long> {

  Optional<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
