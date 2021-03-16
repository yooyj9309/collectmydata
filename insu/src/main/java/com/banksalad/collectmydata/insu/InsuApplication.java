package com.banksalad.collectmydata.insu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.insu"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.insu"
    }
)
public class InsuApplication {

  public static void main(String[] args) {
    SpringApplication.run(InsuApplication.class, args);
  }

}
