package com.eayon.sqlSession;

/**
 * SqlsessionFactory工厂
 */
public interface SqlsessionFactory {

    //生产session
    public SqlSession openSession();
}
