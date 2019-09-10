package edu.uta.cse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * API gateway via Zuul: https://github.com/Netflix/zuul
 */

@SpringBootApplication
@EnableZuulProxy
public class ZuulApplication {
    public static void main(String args[]) {
        SpringApplication.run(ZuulApplication.class, args);
    }

}
