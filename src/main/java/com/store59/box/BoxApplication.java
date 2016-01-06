package com.store59.box;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.caucho.hessian.client.HessianProxyFactory;
import com.store59.box.remoting.UserService;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.store59")
@PropertySource("classpath:boxapi.properties")
public class BoxApplication extends SpringBootServletInitializer {
    @Value("${userservice.url}")
    private String userServiceUrl;

    public static void main(String[] args) {
        SpringApplication.run(BoxApplication.class, args);
    }

    @Bean
    public UserService userServiceRemoting() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = userServiceUrl + "/user";
        return (UserService) hessianProxyFactoryBean.create(UserService.class, url);
    }

}
