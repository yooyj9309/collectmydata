package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.OrganizationInfoEntity;

public interface OrganizationInfoRepository extends JpaRepository<OrganizationInfoEntity, Long> {

}
