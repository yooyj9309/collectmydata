package com.banksalad.collectmydata.capital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.capital"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance.common.db",
        "com.banksalad.collectmydata.capital"
    }
)
public class CapitalApplication {

  public static void main(String[] args) {
    SpringApplication.run(CapitalApplication.class, args);
  }

}
