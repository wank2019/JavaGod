package com.eayon.dao;

import com.eayon.io.Resources;
import com.eayon.pojo.User;
import com.eayon.sqlSession.SqlSession;
import com.eayon.sqlSession.SqlSessionFactoryBuilder;
import com.eayon.sqlSession.SqlsessionFactory;
import java.io.InputStream;
import java.util.List;

/**
 * UserDao
 */
public class UserDaoImpl implements UserDao {
    @Override
    public List<User> findAll() throws Exception {
        //获取核心配置文件字节流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //创建session工厂
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlsessionFactory sqlsessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
        //通过session工厂创建session
        SqlSession sqlSession = sqlsessionFactory.openSession();

        //参数一：statementId  (statementId.id)
        List<User> users = sqlSession.selectList("user.selectList");
        return users;
    }

    @Override
    public User findByCondition(User user) throws Exception {
        //获取核心配置文件字节流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //创建session工厂
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlsessionFactory sqlsessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
        //通过session工厂创建session
        SqlSession sqlSession = sqlsessionFactory.openSession();
        //通过session操作数据库查询
        //参数一：statementId  (statementId.id)   参数二：查询参数
        User user2 = sqlSession.selectOne("user.selectOne",user);
        return user2;
    }
}
