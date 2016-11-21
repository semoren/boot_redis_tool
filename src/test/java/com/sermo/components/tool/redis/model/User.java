package com.sermo.components.tool.redis.model;

import com.sermo.components.tool.redis.annotation.CacheEntity;

import java.io.Serializable;

/**
 * @author sermo
 * @version 2016/11/16.
 */
@CacheEntity(key = "sermo.redis.ceshi.", primary = "getId")
public class User implements Serializable{

    private String id;

    private int age;

    public User(String id, int age) {
        this.id = id;
        this.age = age;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", age=" + age +
                '}';
    }
}
