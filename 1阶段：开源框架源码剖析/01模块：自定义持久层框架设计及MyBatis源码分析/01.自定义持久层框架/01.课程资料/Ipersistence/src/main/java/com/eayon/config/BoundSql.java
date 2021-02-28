package com.eayon.config;

import com.eayon.util.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 转换并赋值后的SQL语句
 */
public class BoundSql {

    private String sql;//解析后的sql
    private List<ParameterMapping> parameterMappingList = new ArrayList<>();//#{}里面解析出来的参数名称

    public BoundSql(String sql, List<ParameterMapping> parameterMappingList) {
        this.sql = sql;
        this.parameterMappingList = parameterMappingList;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }
}
