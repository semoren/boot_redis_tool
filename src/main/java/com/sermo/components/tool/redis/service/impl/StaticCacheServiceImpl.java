package com.sermo.components.tool.redis.service.impl;

import com.sermo.components.biz.exception.BaseException;
import com.sermo.components.biz.util.BaseUtil;
import com.sermo.components.biz.util.ExceptionUtil;
import com.sermo.components.tool.redis.annotation.CacheEntity;
import com.sermo.components.tool.redis.service.StaticCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BuilderFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @author sermo
 * @version 2016/11/15.
 */
@Service
public class StaticCacheServiceImpl implements StaticCacheService{

    private static Logger logger = LoggerFactory.getLogger(StaticCacheServiceImpl.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> void set(Class<T> type, T value) {
        try {
            CacheEntity entity = getCacheEntity(type);
            Object field = invokeMethod(type, value, entity.primary());
            if (field != null) {
                redisTemplate.opsForHash().put(entity.key(), field, value);
            }
        } catch (BaseException e) {
            logger.error("Static cache service set error!", e);
        }
    }

    @Override
    public <T> void set(Class<T> type, Collection<T> values) {
        try {
            CacheEntity entity = getCacheEntity(type);
            Map<String, Object> map = new HashMap<>();
            for (T value : values) {
                Object field = invokeMethod(type, value, entity.primary());
                map.put(BaseUtil.STRING.parse(field), value);
            }
            redisTemplate.opsForHash().putAll(entity.key(), map);
        } catch (BaseException e) {
            logger.error("Static cache service set error!", e);
        }
    }

    @Override
    public <T> T get(Class<T> type, Serializable id) {
        try {
            return (T) redisTemplate.opsForHash().get(getCacheEntityKey(type), id);
        } catch (BaseException e) {
            logger.error("Static cache service get error!", e);
        }
        return null;
    }

    @Override
    public <T extends Comparable<T>> List<T> list(Class<T> type) {
        try {
            List list = redisTemplate.opsForHash().values(getCacheEntityKey(type));
            Collections.sort(list);
            return list;
        } catch (BaseException e) {
            logger.error("Static cache service list error!", e);
        }
        return null;
    }

    @Override
    public <T extends Comparable<T>> List<T> list(Class<T> type, Serializable... ids) {
        try {
            List param = new ArrayList(ids.length);
            Collections.addAll(param, ids);
            List list = redisTemplate.opsForHash().multiGet(getCacheEntityKey(type), param);
            Collections.sort(list);
            return list;
        } catch (BaseException e) {
            logger.error("Static cache service list error", e);
        }
        return null;
    }

    @Override
    public void del(Class<?> type, Serializable... ids) {
        try {
            redisTemplate.opsForHash().delete(getCacheEntityKey(type), ids);
        } catch (BaseException e) {
            logger.error("Static cache service del error!", e);
        }
    }

    @Override
    public void clear(Class<?> type) {
        try {
            redisTemplate.opsForHash().delete(getCacheEntityKey(type));
        } catch (BaseException e) {
            logger.error("Static cache srvice clear error!", e);
        }
    }

    @Override
    public List<String> hmget(String key, String... codes) {
        List param = new ArrayList(codes.length);
        Collections.sort(param);
        List list = redisTemplate.opsForHash().multiGet(key, param);
        return BuilderFactory.STRING_LIST.build(list);
    }

    private CacheEntity getCacheEntity(Class<?> type) throws BaseException {
        if (!type.isAnnotationPresent(CacheEntity.class)) {
            throw ExceptionUtil.exception(503, "Class[#0] not annotation CacheEntity!", type);
        }
        return type.getAnnotation(CacheEntity.class);
    }

    private String getCacheEntityKey(Class<?> type) throws BaseException {
        return getCacheEntity(type).key();
    }

    private <T> Object invokeMethod(Class<T> type, T value, String method) throws BaseException {
        try {
            return type.getMethod(method).invoke(value);
        } catch (Exception e) {
            throw ExceptionUtil.exception(503, "invoke object[#0] method[#1] error!", type.getName(), method);
        }
    }
}
