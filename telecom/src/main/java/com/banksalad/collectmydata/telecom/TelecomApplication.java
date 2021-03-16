package com.banksalad.collectmydata.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.banksalad.collectmydata.telecom",
    "com.banksalad.collectmydata.finance"})
@EnableJpaRepositories(basePackages = {
    "com.banksalad.collectmydata.telecom",
    "com.banksalad.collectmydata.finance"})
public class TelecomApplication {

  public static void main(String[] args) {
    SpringApplication.run(TelecomApplication.class, args);
  }
}
