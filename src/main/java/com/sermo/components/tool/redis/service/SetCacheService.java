package com.sermo.components.tool.redis.service;

import java.util.Set;

/**
 * @author sermo
 * @version 2016年11月15日
 */
public interface SetCacheService {

    public void add(String key, String...values);

    public boolean isMember(String key, String value);

    public void remove(String key, String...values);

    public Set<String> sMembers(String key);
}
