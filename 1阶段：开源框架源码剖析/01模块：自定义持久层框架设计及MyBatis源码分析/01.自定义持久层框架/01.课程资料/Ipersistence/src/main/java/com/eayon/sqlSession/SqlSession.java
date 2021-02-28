package com.eayon.sqlSession;

import java.util.List;

/**
 * SqlSession
 */
public interface SqlSession {

    /**
     * 查询所有
     *
     * @param statementId statementId（namespace.id），通过statementId可以定位到每一条SQL
     * @param params      可变参数
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 根据条件查询单个
     *
     * @param statementId statementId（namespace.id），通过statementId可以定位到每一条SQL
     * @param params      可变参数
     * @return
     */
    public <T> T selectOne(String statementId, Object... params) throws Exception;

}
