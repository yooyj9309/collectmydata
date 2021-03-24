package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.ChargeEntity;

public interface ChargeRepository extends JpaRepository<ChargeEntity, Long> {

}
