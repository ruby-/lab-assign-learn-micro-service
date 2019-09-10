package edu.uta.cse.microservice.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UserThriftServiceApplication {

    public static void main(String args[]) {
//        SpringApplication.run(UserThriftServiceApplication.class, args);
        new SpringApplicationBuilder()
                .sources(UserThriftServiceApplication.class)
                .web(false)
                .run(args);
    }

}
