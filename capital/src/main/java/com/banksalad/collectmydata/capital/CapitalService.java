package com.banksalad.collectmydata.capital;

public interface CapitalService {

  void sync(long banksaladUserId, String organizationId);
}
