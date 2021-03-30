package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;

import java.util.Optional;

public interface ApprovalDomesticRepository extends JpaRepository<ApprovalDomesticEntity, Long> {

  Optional<ApprovalDomesticEntity> findByApprovalYearMonthAndBanksaladUserIdAndOrganizationIdAndCardIdAndApprovedNum(
      Integer approvalYearMonth, Long banksaladUserId, String organizationId, String cardId, String approvedNum);
}
