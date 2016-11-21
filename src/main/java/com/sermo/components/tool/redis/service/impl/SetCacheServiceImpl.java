package com.sermo.components.tool.redis.service.impl;

import com.sermo.components.tool.redis.service.SetCacheService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author sermo
 * @version 2016/11/15.
 */
@Service
public class SetCacheServiceImpl implements SetCacheService{

    @Resource
    private StringRedisTemplate template;

    @Override
    public void add(final String key, final String... values) {
        template.opsForSet().add(key, values);
    }

    @Override
    public boolean isMember(String key, String value) {
        return template.opsForSet().isMember(key, value);
    }

    @Override
    public void remove(String key, String... values) {
        template.opsForSet().remove(key, values);
    }

    @Override
    public Set<String> sMembers(String key) {
        return template.opsForSet().members(key);
    }
}
