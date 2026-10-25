package com.sinogale.dksdkdemo;

import com.sinogale.dksdkdemo.service.DataWorksService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DksdkDemoApplicationTests {
    @Autowired private DataWorksService service;

    @Test
    public void contextLoads() {
        assertNotNull(service);
    }
}
