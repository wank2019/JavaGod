package com.eayon.pojo;

/**
 * 映射配置类：存放SQL语句、参数类型、返回值类型，也就是*mapper.xml配置文件中每一个持久化操作标签的内容，如某一个select标签
 * 一个*mapper.xml配置文件可能会解析出来多个MappedStatement
 */
public class MappedStatement {

    //id标识
    private String id;
    //返回值类型
    private String resultType;
    //参数值类型
    private String paramterType;
    //sql语句
    private String sql;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamterType() {
        return paramterType;
    }

    public void setParamterType(String paramterType) {
        this.paramterType = paramterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
