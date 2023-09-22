package com.zisheng;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class SpringBootTest01ApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(SpringBootTest01ApplicationTests.class);
    @Test
    public void test_create_file()
    {
        File file = new File("D:/sh");
        boolean mkdir = file.mkdir();
        if(mkdir) log.info("文件创建成功！！！");
        else log.info("文件创建失败");
    }
}
