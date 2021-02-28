package com.eayon.dao;

import com.eayon.pojo.User;

import java.util.List;

/**
 * UserDao
 */
public interface UserDao {

    //查询所有用户
    public List<User> findAll() throws Exception;

    //根据条件查询
    public User findByCondition(User user) throws Exception;
}
