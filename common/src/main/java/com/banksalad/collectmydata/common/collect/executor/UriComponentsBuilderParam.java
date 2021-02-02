package com.banksalad.collectmydata.common.collect.executor;

import java.util.HashMap;

class UriComponentsBuilderParam extends HashMap<String, String> {

  @Override
  public String get(Object key) {

    if (!super.containsKey(key)) {
      super.put(key.toString(), "");
    }

    return super.getOrDefault(key, "");
  }

  @Override
  public boolean containsKey(Object key) {
    return true;
  }
}
