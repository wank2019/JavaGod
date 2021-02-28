package com.eayon.test;


import com.eayon.dao.UserDaoImpl;
import com.eayon.io.Resources;
import com.eayon.pojo.User;
import com.eayon.sqlSession.SqlSession;
import com.eayon.sqlSession.SqlSessionFactoryBuilder;
import com.eayon.sqlSession.SqlsessionFactory;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * 测试类
 */
public class MyTest {


    @Test
    public void test1() throws Exception {
        //获取核心配置文件字节流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //创建session工厂
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlsessionFactory sqlsessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
        //通过session工厂创建session
        SqlSession sqlSession = sqlsessionFactory.openSession();
        //通过session操作数据库查询
        User user = new User();
        user.setId(1);
        user.setUsername("张三");
        //参数一：statementId  (statementId.id)   参数二：查询参数
        List<User> users = sqlSession.selectList("user.selectOne", user);
        for (User user1 : users) {
            System.out.println(user1);
        }
    }

    @Test
    public void test2() throws Exception {
        UserDaoImpl userDao = new UserDaoImpl();
        List<User> users = userDao.findAll();
        System.out.println(users);
    }

}
