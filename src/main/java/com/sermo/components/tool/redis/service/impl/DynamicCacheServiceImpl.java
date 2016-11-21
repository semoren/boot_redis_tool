package com.sermo.components.tool.redis.service.impl;

import com.sermo.components.biz.exception.BaseException;
import com.sermo.components.biz.util.ExceptionUtil;
import com.sermo.components.tool.redis.annotation.CacheEntity;
import com.sermo.components.tool.redis.service.DynamicCacheService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author sermo
 * @version 2016/11/16.
 */
@Service
public class DynamicCacheServiceImpl implements DynamicCacheService{

    private static Logger logger = LoggerFactory.getLogger(DynamicCacheServiceImpl.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> void set(Class<T> type, T value, long expire) {
        try {
            redisTemplate.opsForValue().set(getCacheKey(type, value), value, expire, TimeUnit.SECONDS);
        } catch (BaseException e) {
            logger.error("Dynamic cache service set error!", e);
        }
    }

    @Override
    public <T> T get(Class<T> type, Serializable id) {
        try {
            return (T) redisTemplate.opsForValue().get(getCachePrefix(type) + id);
        } catch (BaseException e) {
            logger.error("Dynamic cache service get error!", e);
        }
        return null;
    }

    @Override
    public void del(Class<?> type, Serializable id) {
        try {
            redisTemplate.delete(getCachePrefix(type) + id);
        } catch (BaseException e) {
            logger.error("Dynamic cache service del error!", e);
        }
    }

    private <T> Object invokeMethod(Class<T> type, T value, String method) throws BaseException {
        try {
            return type.getMethod(method).invoke(value);
        } catch (Exception e) {
            throw ExceptionUtil.exception(503, "invoke object[#0] method[#1] error!", type.getName(), method);
        }
    }

    private String getCachePrefix(Class<?> type) throws BaseException {
        return getCacheEntity(type).key();
    }

    private <T> String getCacheKey(Class<T> type, T value) throws BaseException {
        CacheEntity entity = getCacheEntity(type);
        return entity.key() + invokeMethod(type, value, entity.primary());
    }

    private CacheEntity getCacheEntity(Class<?> type) throws BaseException {
        if (!type.isAnnotationPresent(CacheEntity.class)) {
            throw ExceptionUtil.exception(503, "Class[#0] not annotation CacheEntity!", type.getName());
        }
        return type.getAnnotation(CacheEntity.class);
    }
}
