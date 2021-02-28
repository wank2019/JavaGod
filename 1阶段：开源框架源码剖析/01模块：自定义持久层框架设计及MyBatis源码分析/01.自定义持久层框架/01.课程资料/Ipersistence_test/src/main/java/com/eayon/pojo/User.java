package com.eayon.pojo;

/**
 * @author zhengtai.li
 * @ClassName User
 * @Description //TODO
 * @Copyright 2021 © kuwo.cn
 * @date 2021/2/27 14:46
 * @Version: 1.0
 */
public class User {
    private Integer id;
    private String username;

    public User(Integer id, String username) {
        this.id = id;
        this.username = username;
    }
    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
