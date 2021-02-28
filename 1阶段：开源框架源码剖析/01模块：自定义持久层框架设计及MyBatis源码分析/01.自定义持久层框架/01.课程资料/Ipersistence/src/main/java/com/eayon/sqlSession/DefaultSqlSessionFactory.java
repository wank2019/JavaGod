package com.eayon.sqlSession;

import com.eayon.pojo.Configuration;

/**
 * SqlSessionFactory工厂
 */
public class DefaultSqlSessionFactory implements SqlsessionFactory {

    //声明核心配置类成员变量
    private Configuration configuration;

    //在使用有参构造new的时候会将传递的configuration进行赋值
    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }


    /**
     * 生产SqlSession
     * @return
     */
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
