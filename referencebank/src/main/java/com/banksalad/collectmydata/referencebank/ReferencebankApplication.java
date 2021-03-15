package com.banksalad.collectmydata.referencebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.referencebank"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.referencebank"
    }
)
public class ReferencebankApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReferencebankApplication.class, args);
  }

}
