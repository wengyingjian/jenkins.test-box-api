/*
 * Copyright 2015 © 59store.com.
 *
 * BoxControllerTest.java
 *
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
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class BoxControllerTest {

    RestTemplate template = new TestRestTemplate();

    @Test
    public void testRequest() throws Exception {
        System.out.println(template.getForEntity("http://localhost/box/hello?dormId=1", String.class).getBody());
    }


}
