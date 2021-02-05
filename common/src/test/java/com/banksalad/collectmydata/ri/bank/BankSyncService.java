package com.banksalad.collectmydata.ri.bank;

import com.banksalad.collectmydata.common.exception.CollectException;

public interface BankSyncService {

  void sync(long banksaladUserId, String organizationId) throws CollectException;

}
