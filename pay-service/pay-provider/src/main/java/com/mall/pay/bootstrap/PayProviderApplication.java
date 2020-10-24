package com.mall.pay.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan(basePackages ={"com"})
@MapperScan(basePackages = "com.mall.pay.dal")
@SpringBootApplication
public class PayProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayProviderApplication.class,args);
    }
}
