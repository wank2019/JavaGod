package com.eayon.sqlSession;

import com.eayon.config.BoundSql;
import com.eayon.pojo.Configuration;
import com.eayon.pojo.MappedStatement;
import com.eayon.util.GenericTokenParser;
import com.eayon.util.ParameterMapping;
import com.eayon.util.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装CRUD方法，其实就是对jdbc代码的进一步封装
 */
public class SimpleExecutor implements Executor {

    /**
     * 查询
     *
     * @param configuration   核心配置类
     * @param mappedStatement SQL配置信息
     * @param params          可变参数
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // 1. 注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        //2.获取本次操作的sql语句
        String sql = mappedStatement.getSql();
        //比如获取到的SQL格式：select * from user where id = #{id} and username = #{username} jdbc无法识别，所以我们需要转换
        //比如转为select * from user where id = ? and username = ? 这样的格式，并且还需要对#{}里面的值进行解析存储
        //转换SQL
        BoundSql boundSql = getBoundSql(sql);

        // 3.获取预处理对象：preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());

        //4.设置参数
        //获取查询参数类型的全路径  如：com.eayon.pojo.User
        String paramterType = mappedStatement.getParamterType();
        //获取到查询参数类型的Class字节码对象   如：class com.eayon.pojo.User
        Class<?> paramtertypeClass = getClassType(paramterType);

        //获取原始SQL中#{}里面设置的参数名称集合  如：id，username
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        //遍历参数名称集合，
        for (int i = 0; i < parameterMappingList.size(); i++) {
            //循环取出原始SQL中#{}里面设置的参数名称
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //使用反射，根据参数名获取实体对象的属性值， 再根据传递的参数进行赋值
            //通过Class获取到某一个属性对象
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //获取该属性对象的值
            Object o = declaredField.get(params[0]);
            //设置参数
            preparedStatement.setObject(i+1,o);
        }

        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        //获取返回结果的实体全路径
        String resultType = mappedStatement.getResultType();
        //获取返回结果实体全路径的Class，主要用于反射封装结果集时使用
        Class<?> resultTypeClass = getClassType(resultType);

        //返回结果集
        ArrayList<Object> objects = new ArrayList<>();

        // 6. 封装返回结果集
        while (resultSet.next()){
            //获取返回结果实体对象
            Object o =resultTypeClass.newInstance();
            //metaData.getColumnCount()  查询结果集中的总列数  循环获取每列数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //获取数据库中的字段名称
                String columnName = metaData.getColumnName(i);
                //通过字段名称去查询结果集中取出字段值
                Object value = resultSet.getObject(columnName);
                //PropertyDescriptor：可以通过有参构造获取该字段在返回值实体中的get、set方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                //然后我们就可以获取到这个返回值实体的set方法
                Method writeMethod = propertyDescriptor.getWriteMethod();
                //把该字段值set到实体里面即可
                writeMethod.invoke(o,value);
            }
            //封装到返回结果集
            objects.add(o);
        }

        //返回查询结果
        return (List<E>) objects;
    }


    /**
     * 通过查询参数的全路径获取到Class
     *
     * @param paramterType 参数的全路径
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if (paramterType != null) {
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
        return null;
    }

    /**
     * 完成对SQL语句的解析：
     * 1、将#{}使用?进行代替
     * 2、解析出#{}里面的值进行存储
     *
     * @param sql
     * @return
     */
    public BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        //解析并封装好的SQL进行返回
        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
