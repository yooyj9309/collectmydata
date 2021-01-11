package com.banksalad.collectmydata.common.crypto;

import com.banksalad.collectmydata.common.exception.CipherException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesGcmEncrypt {

  private static final String ALGORITHM_AES = "AES";
  private static final String ALGORITHM_AES_GCM_NOPADDING = "AES/GCM/NoPadding";
  public static final int GCM_TAG_LENGTH = 16;

  private AesGcmEncrypt() {
  }

  public static String encryptStringBase64(String secret, String iv, String payload) {
    return Base64Util.encode(
        encrypt(Base64Util.decode(secret), Base64Util.decode(iv), payload.getBytes(StandardCharsets.UTF_8)));
  }

  public static String decryptStringBase64(String secret, String iv, String encrypted) {
    return new String(decrypt(Base64Util.decode(secret), Base64Util.decode(iv), Base64Util.decode(encrypted)));
  }

  public static byte[] encrypt(byte[] secret, byte[] iv, byte[] payload) {
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM_AES_GCM_NOPADDING);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, ALGORITHM_AES),
          new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));

      return cipher.doFinal(payload);

    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CipherException("encrypt fail", e);
    }
  }

  public static byte[] decrypt(byte[] secret, byte[] iv, byte[] encrypted) {
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM_AES_GCM_NOPADDING);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret, ALGORITHM_AES),
          new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));

      return cipher.doFinal(encrypted);

    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CipherException("decrypt fail", e);
    }
  }
}
