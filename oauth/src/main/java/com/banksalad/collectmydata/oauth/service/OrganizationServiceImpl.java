package com.banksalad.collectmydata.oauth.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

  @PostConstruct
  public void init() {
    // connect에서 가져온 값 저장.
  }

  @Override
  public String getOrganizationByObjectId(String organizationObjectId) {
    return null;
  }
}
