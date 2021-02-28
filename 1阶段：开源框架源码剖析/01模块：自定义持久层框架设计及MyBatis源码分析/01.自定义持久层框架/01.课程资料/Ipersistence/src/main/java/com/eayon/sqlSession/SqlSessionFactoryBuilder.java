package com.eayon.sqlSession;


import com.eayon.config.XMLConfigBuilder;
import com.eayon.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * 构建者类：用于创建SqlSessionFactory
 */
public class SqlSessionFactoryBuilder {

    public SqlsessionFactory build(InputStream is) throws PropertyVetoException, DocumentException {
        //第一：使用dom4j解析配置文件，将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(is);

        //第二：创建SqlsessionFactory工厂
        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return sqlSessionFactory;
    }
}
