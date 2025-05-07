package jroullet.msidentity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class MsIdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsIdentityApplication.class, args);
    }

}
