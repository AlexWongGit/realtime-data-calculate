package com.alex;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringCloudApplication
@EnableScheduling
@EnableAspectJAutoProxy
@MapperScan("cn.com.ztesa.iot.serv.mapper")
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.alex"})
public class RealtimeDataCalculatApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtimeDataCalculatApplication.class, args);
    }

}
