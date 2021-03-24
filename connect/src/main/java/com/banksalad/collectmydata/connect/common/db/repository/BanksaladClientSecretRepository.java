package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;

import java.util.Optional;

public interface BanksaladClientSecretRepository extends JpaRepository<BanksaladClientSecretEntity, Long> {

  Optional<BanksaladClientSecretEntity> findBySecretType(String secretType);
}
