package com.banksalad.collectmydata.common.crypto;

import java.util.Base64;

public class Base64Util {

  private Base64Util() {
  }


  public static String encode(byte[] src) {
    return Base64.getEncoder().withoutPadding().encodeToString(src);
  }

  public static byte[] decode(String src) {
    return Base64.getDecoder().decode(src);
  }
}
