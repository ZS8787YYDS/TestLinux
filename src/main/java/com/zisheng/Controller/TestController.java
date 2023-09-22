package com.zisheng.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/hello")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    @GetMapping
    public String testHello()
    {
        log.info("明天计算机二级考试");
        return "饿死了 吃饭吃饭！！！";
    }

}
