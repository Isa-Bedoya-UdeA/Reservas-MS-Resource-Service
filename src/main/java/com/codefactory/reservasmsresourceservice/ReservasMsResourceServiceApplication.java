package com.codefactory.reservasmsresourceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReservasMsResourceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasMsResourceServiceApplication.class, args);
    }

}
