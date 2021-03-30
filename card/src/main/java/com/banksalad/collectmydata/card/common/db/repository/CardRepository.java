package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.CardEntity;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

  Optional<CardEntity> findByBanksaladUserIdAndOrganizationIdAndCardId(long banksaladUserId, String organizationId,
      String cardId);
}
