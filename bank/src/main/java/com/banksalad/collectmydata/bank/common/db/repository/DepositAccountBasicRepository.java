package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;

import java.util.List;
import java.util.Optional;

public interface DepositAccountBasicRepository extends JpaRepository<DepositAccountBasicEntity, Long> {

  Optional<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(long banksaladUserId,
      String organizationId, String accountNum, String seqno, String currencyCode);

  // TODO : 6.2.2 수신계좌 기본정보 조회 시 account_num & seqno 조합으로 unique key를 만들 수 있는지 확인
  List<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
