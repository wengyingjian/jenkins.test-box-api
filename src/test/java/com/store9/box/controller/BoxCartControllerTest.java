/*
 * Copyright 2015 © 59store.com. BoxControllerTest.java
 */
package com.store9.box.controller;

import com.store59.box.BoxApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by shanren on 15/7/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BoxApplication.class)
@WebIntegrationTest({ "server.port=0", "management.port=0" })
public class BoxCartControllerTest {

    RestTemplate template = new TestRestTemplate();

    @Test
    public void testRequest() throws Exception {
        // System.out.println(template.postForEntity("http://localhost:8080/box/token/new?device_id=1&device_type=1&site_id=1&uid=5947", null,
        // String.class,
        // new String[] {}));
        System.out.println(template.postForEntity(
                "http://localhost:8080/box/cart/update?box_id=1&rid=10&quantity=1&token=c877cfc7982b0b0090bc26eed852ebcb", null, String.class,
                new String[] {}).getBody());
    }

}
