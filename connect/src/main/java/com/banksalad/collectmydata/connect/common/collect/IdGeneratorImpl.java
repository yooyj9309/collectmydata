package com.banksalad.collectmydata.connect.common.collect;

import com.banksalad.collectmydata.common.collect.apilog.IdGenerator;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {

  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
