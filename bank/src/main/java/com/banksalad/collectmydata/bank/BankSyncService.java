package com.banksalad.collectmydata.bank;

import com.banksalad.collectmydata.common.exception.CollectException;

public interface BankSyncService {

  void sync(long banksaladUserId, String organizationId) throws CollectException;

}
