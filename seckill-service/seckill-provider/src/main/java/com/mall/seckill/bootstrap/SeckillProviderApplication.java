package com.mall.seckill.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan(basePackages ={"com.mall.seckill"})
@MapperScan(basePackages = "com.mall.seckill.dal")
@SpringBootApplication
public class SeckillProviderApplication {

    public static void main(String[] args) {

        SpringApplication.run(SeckillProviderApplication.class, args);

    }

}
