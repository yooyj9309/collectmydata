package com.banksalad.collectmydata.connect.collect;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.apilog.IdGenerator;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {

  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
