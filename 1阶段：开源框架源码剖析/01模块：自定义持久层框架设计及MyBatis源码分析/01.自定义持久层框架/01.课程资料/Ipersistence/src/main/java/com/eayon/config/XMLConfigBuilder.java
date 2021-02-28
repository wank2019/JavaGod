package com.eayon.config;

import com.eayon.io.Resources;
import com.eayon.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 使用dom4j解析sqlMapConfig.xml的字节流封装到Configuration核心配置类
 */
public class XMLConfigBuilder {

    //声明核心配置类成员变量
    private Configuration configuration;

    //在new XMLConfigBuilder的时候就会通过无参构造给成员变量configuration进行赋值
    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 该方法使用dom4j将sqlMapConfig.xml在内存中的字节流封装到Configuration核心配置类并返回。
     *
     * @param is 核心配置文件qlMapConfig.xml的字节流
     * @return
     */
    public Configuration parseConfig(InputStream is) throws DocumentException, PropertyVetoException {

        /**
         * 第一步：通过dom4j解析核心配置文件封装到Configuration配置类
         */
        //通过dom4j读取核心配置文件字节流生成Dom
        Document document = new SAXReader().read(is);
        //拿到根元素，也就是sqlMapConfig.xml文件中的configuration标签
        Element configElement = document.getRootElement();
        //查找根元素下所有的property标签元素
        List<Element> propertyElements = configElement.selectNodes("//property");
        //通过Properties存储数据源信息name以及value值
        Properties dataSources = new Properties();
        //获取每一个property元素的name以及value值
        for (Element propertyElement : propertyElements) {
            String name = propertyElement.attributeValue("name");
            String value = propertyElement.attributeValue("value");
            //将name、value对应的存入Properties
            dataSources.setProperty(name,value);
        }

        //创建数据库连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        //设置数据源信息
        comboPooledDataSource.setDriverClass(dataSources.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(dataSources.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(dataSources.getProperty("username"));
        comboPooledDataSource.setPassword(dataSources.getProperty("password"));

        //将数据源封装到Configuration对象
        configuration.setDataSource(comboPooledDataSource);

        /**
         * 第二步：通过sqlMapConfig.xml中配置的*mapper.xml的全路径来解析*mapper.xml并封装到MappedStatement配置类
         *        再将MappedStatement赋值给Configuration的mappedStatementMap属性
         */
        //mapper.xml解析：拿到mapper.xml路径、通过dom4j解析
        List<Element> mapperElements = configElement.selectNodes("//mapper");
        //sqlMapConfig.xml中可能会配置多个*mapper.xml的地址，所以这里我们会获取到多个地址
        for (Element mapperElement : mapperElements) {
            String mapperPath = mapperElement.attributeValue("resource");
            //获取*mapper.xml的字节输入流
            InputStream mapperStream = Resources.getResourceAsStream(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(mapperStream);
        }

        //返回核心配置类
        return configuration;
    }
}
