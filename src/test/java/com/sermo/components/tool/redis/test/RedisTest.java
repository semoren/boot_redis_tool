package com.sermo.components.tool.redis.test;

import com.sermo.components.tool.redis.Application;
import com.sermo.components.tool.redis.service.SetCacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author sermo
 * @version 2016/11/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class RedisTest {

    @Resource
    private SetCacheService setCacheService;
    @Test
    public void test() throws Exception{
//        template.opsForValue().set("a","b");
        setCacheService.add("aset","b","c","d");

    }
}
