package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;

import java.util.List;
import java.util.Optional;

public interface InvestAccountDetailRepository extends JpaRepository<InvestAccountDetailEntity, Long> {

  Optional<InvestAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String currencyCode);

  // TODO : 6.2.6 투자상품 계좌 추가정보 조회 시 account_num & seqno 조합으로 unique key를 만들 수 있는지 확인
  Optional<InvestAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
