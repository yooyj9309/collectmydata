package com.banksalad.collectmydata.efin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.efin"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.efin"
    }
)
public class EfinApplication {

  public static void main(String[] args) {
    SpringApplication.run(EfinApplication.class, args);
  }

}
