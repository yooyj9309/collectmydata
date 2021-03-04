package com.banksalad.collectmydata.common.crypto;

import com.banksalad.collectmydata.common.exception.CipherException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.StringJoiner;

public class HashUtil {

  private static final String ALG = "SHA3-256";
  private static MessageDigest messageDigest;

  static {
    try {
      messageDigest = MessageDigest.getInstance(ALG);
    } catch (NoSuchAlgorithmException e) {
      throw new CipherException("No digest algorithm found", e);
    }
  }

  public static String hashCat(List<String> strings){
    StringJoiner stringJoiner = new StringJoiner(":");
    for(String s:strings){
      stringJoiner.add(s);
    }
    return digest(stringJoiner.toString());
  }

  public static String digest(String s){
    final byte[] bytes=messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(bytes);
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
