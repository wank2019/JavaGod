package com.eayon.sqlSession;

import com.eayon.pojo.Configuration;
import com.eayon.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * 封装CRUD方法，其实就是对jdbc代码的进一步封装
 */
public interface Executor {

    /**
     * 查询
     *
     * @param configuration   核心配置类
     * @param mappedStatement SQL配置信息
     * @param params          可变参数
     * @param <E>
     * @return
     */
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException, Exception;
}
