package com.banksalad.collectmydata.connect.support.service;

public interface SupportService {

  // 일괄 처리
  public void syncAllOrganizationInfo();

  // 7.1.2 기관 업데이트
  public void syncOrganizationInfo();

  //7.1.3 기관 서비스 업데이트
  public void syncOrganizationServiceInfo();
}
