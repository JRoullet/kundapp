package jroullet.mswebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsWebappApplication.class, args);
    }

}
