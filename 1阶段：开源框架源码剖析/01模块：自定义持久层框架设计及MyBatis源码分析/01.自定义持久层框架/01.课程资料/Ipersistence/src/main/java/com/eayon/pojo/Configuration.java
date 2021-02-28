package com.eayon.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 核心配置类：存放数据库基本信息，也就是客户端提供的sqlMapConfig.xml配置文件的内容
 */
public class Configuration {

    //数据源配置信息：使用dom4j解析sqlMapConfig.xml时会解析到dataSource这个标签中的属性值并创建成数据源对象赋值给本类的dataSource属性
    private DataSource dataSource;

    //在sqlMapConfig.xml中会存放*mapper.xml的全路径并加载
    //那一个*mapper.xml可能会解析出来多个MappedStatement(SQL配置信息)，所以在Configuration我们使用Map来进行多个存储
    //Map集合中的Key：statementId（namespace.id），通过statementId可以定位到每一条SQL
    //Map集合中的Value：每条SQL的配置类MappedStatement
    Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
