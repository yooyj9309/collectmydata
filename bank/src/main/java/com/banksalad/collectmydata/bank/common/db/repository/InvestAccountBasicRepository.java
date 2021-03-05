package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;

import java.util.List;

public interface InvestAccountBasicRepository extends JpaRepository<InvestAccountBasicEntity, Long> {

  InvestAccountBasicEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);

  List<InvestAccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
