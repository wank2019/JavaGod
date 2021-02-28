package com.eayon.sqlSession;

import com.eayon.io.Resources;
import com.eayon.pojo.Configuration;
import com.eayon.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

/**
 * SqlSession实现类
 */
public class DefaultSqlSession implements SqlSession {
    //声明核心配置类成员变量
    private Configuration configuration;

    //在使用有参构造new的时候会将传递的configuration进行赋值
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 查询所有
     *
     * @param statementId statementId（namespace.id），通过statementId可以定位到每一条SQL
     * @param params      可变参数
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        //创建SimpleExecutor
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        //通过核心配置文件获取所有的MappedStatement集合
        Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
        //通过本次查询参数中的statementId，从Map集合中找出本次查询的SQL配置（SQL语句，返回值类型，参数类型等等）
        MappedStatement mappedStatement = mappedStatementMap.get(statementId);
        //调用SimpleExecutor中对jdbc封装好的query方法进行查询
        List<E> list = simpleExecutor.query(configuration, mappedStatement, params);
        return list;
    }

    /**
     * 根据条件查询单个
     *
     * @param statementId statementId（namespace.id），通过statementId可以定位到每一条SQL
     * @param params      可变参数
     * @return
     */
    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或返回结果过多");
        }
    }

}
