package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;

import java.util.Optional;

public interface LoanAccountDetailRepository extends JpaRepository<LoanAccountDetailEntity, Long> {

  Optional<LoanAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String seqNo);

}
