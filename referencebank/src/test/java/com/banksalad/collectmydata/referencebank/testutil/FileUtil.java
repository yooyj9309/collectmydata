package com.banksalad.collectmydata.referencebank.testutil;

import org.springframework.util.ResourceUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Deprecated
public class FileUtil {

  public static String readText(String fileInClassPath) {
    try {
      File file = ResourceUtils.getFile(fileInClassPath);
      return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Fail to read file", e);
    }
  }
}
