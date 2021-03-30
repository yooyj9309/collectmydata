package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface CardSummaryRepository extends JpaRepository<CardSummaryEntity, Long> {

  Optional<CardSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndCardId(long banksaladUserId,
      String organizationId, String cardId);

  List<CardSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(long banksaladUserId,
      String organizationId);
}
