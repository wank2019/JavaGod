# MyBatis基础及高级应用

## 说在前面

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/1%E9%98%B6%E6%AE%B5%EF%BC%9A%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01%E6%A8%A1%E5%9D%97%EF%BC%9A%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%8F%8AMyBatis%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/02.MyBatis%E5%9F%BA%E7%A1%80%E5%9B%9E%E9%A1%BE%E5%8F%8A%E9%AB%98%E7%BA%A7%E5%BA%94%E7%94%A8)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)



## 目录
- [MyBatis基础及高级应用](#mybatis基础及高级应用)
  - [说在前面](#说在前面)
  - [目录](#目录)
  - [一、Mybatis相关概念](#一mybatis相关概念)
    - [1.1 对象/关系数据库映射（ORM）](#11-对象关系数据库映射orm)
    - [1.2 MyBatis简介](#12-mybatis简介)
    - [1.3 为什么要用MyBatis](#13-为什么要用mybatis)
  - [二、Mybatis基本应用](#二mybatis基本应用)
    - [2.1 快速入门](#21-快速入门)
      - [2.1.1 开发步骤](#211-开发步骤)
      - [2.1.2 环境搭建](#212-环境搭建)
      - [2.1.3 编写测试代码](#213-编写测试代码)
    - [2.2 MyBatis的Dao层实现](#22-mybatis的dao层实现)
      - [2.2.1 传统开发方式](#221-传统开发方式)
      - [2.2.3 代理开发方式](#223-代理开发方式)
    - [2.3 Mybatis相应API介绍](#23-mybatis相应api介绍)
      - [2.3.1 SqlSession工厂构建器：SqlSessionFactoryBuilder](#231-sqlsession工厂构建器sqlsessionfactorybuilder)
      - [2.3.2 SqlSession工厂对象：SqlSessionFactory](#232-sqlsession工厂对象sqlsessionfactory)
      - [2. 3.3 SqlSession会话对象](#2-33-sqlsession会话对象)
  - [三、MyBatis配置文件深入](#三mybatis配置文件深入)
    - [3.1 *mapper.xml映射文件](#31-mapperxml映射文件)
    - [3.2 sqlMapConfig.xml核心配置文件层级关系](#32-sqlmapconfigxml核心配置文件层级关系)
    - [3.3 sqlMapConfig.xml中常用配置解析](#33-sqlmapconfigxml中常用配置解析)
      - [3.3.1 environments标签](#331-environments标签)
      - [3.3.2 mapper标签](#332-mapper标签)
      - [3.3.3 properties标签](#333-properties标签)
      - [3.3.4 typeAliases标签](#334-typealiases标签)
  - [四、动态SQL及SQL片段](#四动态sql及sql片段)
    - [4.1 if语句](#41-if语句)
    - [4.2 where语句](#42-where语句)
    - [4.3 foreach语句](#43-foreach语句)
    - [4.3 SQL片段抽取](#43-sql片段抽取)
  - [五、MyBatis复杂映射开发](#五mybatis复杂映射开发)
    - [5.1 一对一查询](#51-一对一查询)
      - [5.1.1 一对一查询的模型及需求](#511-一对一查询的模型及需求)
      - [5.1.2 一对一查询的SQL语句](#512-一对一查询的sql语句)
      - [5.1.3 一对一查询的具体实现](#513-一对一查询的具体实现)
    - [5.2 一对多查询](#52-一对多查询)
      - [5.2.1 一对多查询的模型及需求](#521-一对多查询的模型及需求)
      - [5.2.2 一对多查询的语句](#522-一对多查询的语句)
      - [5.2.3 一对多查询的具体实现](#523-一对多查询的具体实现)
    - [5.3 多对多查询](#53-多对多查询)
      - [5.3.1 多对多查询的模型及需求](#531-多对多查询的模型及需求)
      - [5.3.2 多对多查询的语句](#532-多对多查询的语句)
      - [5.3.3 多对多查询的具体实现](#533-多对多查询的具体实现)
  - [六、MyBatis注解开发](#六mybatis注解开发)
    - [6.1 常用注解](#61-常用注解)
    - [6.2 注解方式进行CRUD](#62-注解方式进行crud)
  - [七、MyBatis缓存](#七mybatis缓存)
    - [7.1 缓存相关概念](#71-缓存相关概念)
    - [7.2 一级缓存验证](#72-一级缓存验证)
      - [7.2.1 验证一](#721-验证一)
      - [7.2.2 验证二](#722-验证二)
    - [7.3 一级缓存原理探究与源码分析](#73-一级缓存原理探究与源码分析)
    - [7.4 二级缓存验证](#74-二级缓存验证)
      - [7.4.1 验证](#741-验证)
      - [7.4.2 二级缓存的其他配置：useCache和flushCache](#742-二级缓存的其他配置usecache和flushcache)
        - [7.4.2.1 userCache禁用二级缓存](#7421-usercache禁用二级缓存)
        - [7.4.2.2 flushCache刷新二级缓存**](#7422-flushcache刷新二级缓存)
    - [7.5 二级缓存原理探究与源码分析](#75-二级缓存原理探究与源码分析)
    - [7.6 Redis实现二级缓存（MyBatis分布式缓存）](#76-redis实现二级缓存mybatis分布式缓存)
      - [7.6.1 概述](#761-概述)
      - [7.6.2 实现](#762-实现)
  - [八、MyBatis插件](#八mybatis插件)
    - [8.1 插件简介](#81-插件简介)
    - [8.2 MyBatis插件介绍](#82-mybatis插件介绍)
    - [8.3 MyBatis插件原理](#83-mybatis插件原理)
    - [8.4 自定义插件](#84-自定义插件)
    - [8.5 插件源码分析](#85-插件源码分析)



## 一、Mybatis相关概念

### 1.1 对象/关系数据库映射（ORM）

ORM全称Object/Relation Mapping：表示**对象-关系映射**的缩写

ORM完成面向对象的编程语言到关系数据库的映射。当ORM框架完成映射后，程序员既可以利用面向对象程序设计语言的简单易用性，又可以利用关系数据库的技术优势。ORM把关系数据库包装成面向对象的模型。ORM框架是面向对象设计语言与关系数据库发展不同步时的中间解决方案。采用ORM框架后，应用程序不再直接访问底层数据库，而是以面向对象的放松来操作持久化对象，而ORM框架则将这些面向对象的操作转换成底层SQL操作。ORM框架实现的效果：把对持久化对象的保存、修改、删除 等操作，转换为对数据库的操作

### 1.2 MyBatis简介

MyBatis是一款优秀的基于ORM的半自动轻量级持久层框架，它支持定制化SQL、存储过程以及高级映射。MyBatis避免了几乎所有的JDBC代码和手动设置参数以及获取结果集。MyBatis可以使用简单的XML或注解来配置和映射原生类型、接口和Java的POJO （Plain Old Java Objects,普通老式Java对 象）为数据库中的记录。

### 1.3 为什么要用MyBatis

* MyBatis是一个半自动化的持久化层框架。

* jdbc编程—当我们使用jdbc持久化的时候，sql语句被硬编码到java代码中。这样耦合度太高。代码不易于维护。在实际项目开发中会经常添加sql或者修改sql，这样我们就只能到java代码中去修改。

* Hibernate和JPA；长难复杂SQL，对于Hibernate而言处理也不容易；内部自动生产的SQL，不容易做特殊优化。；基于全映射的全自动框架，javaBean存在大量字段时无法只映射部分字段。导致数据库性能下降。

* 对开发人员而言，核心sql还是需要自己优化；sql和java编码分开，功能边界清晰，一个专注业务、一个专注数据；可以使用简单的XML或注解用于配置和原始映射，将接口和Java的POJO映射成数据库中的记录。成为业务代码+底层数据库的媒介

**1.4 MyBatis优势**

Mybatis是一个半自动化的持久层框架，对开发人员开说，核心sql还是需要自己进行优化，sql和java编码进行分离，功能边界清晰，一个专注业务，一个专注数据。

**分析图示如下：**

![img](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/clipboard.png)



## 二、Mybatis基本应用

### 2.1 快速入门

MyBatis官网地址：[http://www.mybatis.org/mybatis-3/ ](http://www.mybatis.org/mybatis-3/)

#### 2.1.1 开发步骤

- 添加MyBatis的坐标
- 创建user数据表
- 编写User实体类 

- 编写映射文件UserMapper.xml
- 编写核心文件SqlMapConfig.xml
- 编写测试类

#### 2.1.2 环境搭建

- **第一步：创建快速开始工程，项目名：mybatis_quickStarter**

- **第二步：导入MyBatis的坐标和其他相关坐标**

  ```xml
  <properties>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
      <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
      <java.version>1.8</java.version>
      <maven.compiler.source>1.8</maven.compiler.source>
      <maven.compiler.target>1.8</maven.compiler.target>
  </properties>
  
  
  
  <!--引入依赖-->
  <dependencies>
      <!--mybatis坐标-->
      <dependency>
          <groupId>org.mybatis</groupId>
          <artifactId>mybatis</artifactId>
          <version>3.4.5</version>
      </dependency>
      <!--mysql驱动坐标-->
      <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>5.1.6</version>
          <scope>runtime</scope>
      </dependency>
      <!--单元测试坐标-->
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.12</version>
      </dependency>
      <!--日志-->
      <dependency>
          <groupId>log4j</groupId>
          <artifactId>log4j</artifactId>
          <version>1.2.12</version>
      </dependency>
  </dependencies>
  ```

- **第三步：创建User数据库表**

![image-20210313184301666](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313184301666.png)

- **第四步：创建User实体**

  在``com.eayon.pojo``下创建

  ```java
  public class User {
      private Integer id;
      private String username;
      //省略get set方法
  }
  ```

- **第五步：编写UserMapper.xml映射配置文件**

  在``resources/mapper``目录下创建

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC " //mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  <mapper namespace="userMapper">
      <!--namespace：命名空间 与id组成statementId用于定位sql
          resultType：表明返回值类型
          parameterType：表明参数类型
      -->
      <select id="findAll" resultType="com.eayon.pojo.User">
          SELECT id,username FROM user
      </select>
  </mapper>
  ```

- **第六步：编写SqlMapConfig.xml核心配置文件**

  在``resources``目录下创建

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  
      <!--
          environments：是配置多个jdbc运行环境
        default：表示使用的默认环境
      -->
      <environments default="dev">
          <!--
           environment：标签用来配置一个环境
           id：是环境的标识
         -->
          <environment id="dev">
              <!--
                  当前事务交由JDBC进行管理
                  transactionManager：配置使用什么样类型的数据库事务管理
                  type="JDBC"：表示启用事务，有commit和rollback操作，常用
                  type="MANAGED"：表示不直接控制事务。交给容器处理，几乎不用。
            -->
              <transactionManager type="JDBC"/>
              <!--
              dataSource标签配置连接池
              type="POOLED"：表示启用数据库连接池
              type="UNPOOLED"：表示不启用数据库连接池
            -->
              <dataSource type="POOLED">
                  <!-- 连接数据库的驱动类 -->
                  <property name="driver" value="com.mysql.jdbc.Driver" />
                  <!-- 数据库访问地址 -->
                  <property name="url" value="jdbc:mysql:///eayon_mybatis" />
                  <!-- 数据库用户名 -->
                  <property name="username" value="root" />
                  <!-- 数据库密码 -->
                  <property name="password" value="1234" />
              </dataSource>
          </environment>
      </environments>
  
      <!--引入mapper映射配置文件-->
      <mappers>
          <mapper resource="mapper/UserMapper.xml"></mapper>
      </mappers>
  
  </configuration>
  ```



#### 2.1.3 编写测试代码

在``com.eayon.test``下创建MyTest测试类并编写测试方法test1（这是最原始最传统的MyBatis使用方式，和我们之前自定义持久层框架时客户端使用的方式一样）

```java
package com.eayon.test;

import com.eayon.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * 测试类
 */
public class MyTest {

    /**
     * 快速开始
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        // 读取mybatis的核心配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 通过SqlSessionFactoryBuilder创建一个SqlSessionFactory对象
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        // 创建一个sqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            //第一个参数：statementId，用来定位你要执行UserMapper.xml中的哪个SQL语句（spacename+id）
            //第二个参数：是你要执行sql的对应参数（可不传）
            User user = sqlSession.selectOne("userMapper.findAll");
            System.out.println(user);
        } finally {
            sqlSession.close();
        }
    }
}
```



### 2.2 MyBatis的Dao层实现

#### 2.2.1 传统开发方式

- **第一步：编写UserDao接口**

  在``com.eayon.dao``下创建

  ```java
  package com.eayon.dao;
  
  import com.eayon.pojo.User;
  import java.io.IOException;
  import java.util.List;
  
  public interface UserDao {
      List<User> findAll() throws IOException;
  }
  ```

- **第二步：编写实现类UserDaoImpl**

  在``com.eayon.dao``下创建

  ```java
  package com.eayon.dao;
  
  
  import com.eayon.pojo.User;
  import org.apache.ibatis.io.Resources;
  import org.apache.ibatis.session.SqlSession;
  import org.apache.ibatis.session.SqlSessionFactory;
  import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  
  import java.io.IOException;
  import java.io.InputStream;
  import java.util.List;
  
  public class UserDaoImpl implements UserDao{
  
      @Override
      public List<User> findAll() throws IOException {
          InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
          SqlSessionFactory sqlSessionFactory = new
                  SqlSessionFactoryBuilder().build(resourceAsStream);
          SqlSession sqlSession = sqlSessionFactory.openSession();
          List<User> userList = sqlSession.selectList("userMapper.findAll");
          sqlSession.close();
          return userList;
      }
  }
  ```

- **第三步：测试传统方式**

  在``com.eayon.test.MyTest``类中测试以下方法

  ```java
  /**
   * 测试Dao层实现的传统开发方式
   */
  @Test
  public void testTraditionDao() throws IOException {
      UserDao userDao = new UserDaoImpl();
      List<User> all = userDao.findAll();
      System.out.println(all);
  }
  ```

- **总结**

  我们发现使用传统开发方式还需要编写Dao层接口的实现类非常的复杂，我们能不能不写实现类，只写接口呢？那就看下面的代理开发方式。

#### 2.2.3 代理开发方式

**代理开发方式介绍：**

- 采用Mybatis的代理开发方式实现 DAO层的开发，这种方式是我们后面进入**企业的主流**。
- Mapper接口开发方法只需要程序员编写Mapper接口（相当于Dao接口），由Mybatis框架根据接口定义创建接口的动态代理对象，代理对象的方法体同上边Dao接口实现类方法。

**Mapper接口开发需要遵循以下规范：**

- Mapper.xml文件中的namespace与mapper接口的全限定名相同
- Mapper接口方法名和Mapper.xml中定义的每个statement的id相同
- Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql的parameterType的类型相同
- Mapper接口方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同

![image-20210313174206414](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174206414.png)

**实现：**

- **第一步：编写UserMapper接口**

  在``com.eayon.mapper``下创建，但是不需要创建UserMapper接口的实现类了

  ```java
  package com.eayon.mapper;
  
  import com.eayon.pojo.User;
  
  public interface UserMapper {
      User findById(Integer id);
  }
  ```

- **第二步：修改UserMapper.xml**

  因为根据Mapper接口开发规范，所以我们要修改UserMapper.xml中的``namespace``以及``statement``的``id``

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  <!--namespace：需要和UserMapper的全路径保持相同 这样才能映射找到 -->
  <mapper namespace="com.eayon.mapper.UserMapper">
      <!--
          namespace：命名空间 与id组成statementId用于定位sql
          id：该条sql语句在当前UserMapper.xml中的唯一标识 与namespace组成statementId用于定位sql
          resultType：表明返回值类型
          parameterType：表明参数类型
      -->
  
      <!--id：需要和UserMapper中的对应方法名保持一致-->
      <select id="findById" parameterType="integer" resultType="com.eayon.pojo.User">
          SELECT id,username FROM User WHERE id = #{id}
      </select>
  
  </mapper>
  ```

- **第三步：测试代理开发方式**

  在``com.eayon.test.MyTest``类中测试以下方法

  ```java
  /**
   * 测试代理开发方式
   */
  @Test
  public void testProxyDao() throws IOException {
      // 读取mybatis的核心配置文件
      InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
      // 通过SqlSessionFactoryBuilder创建一个SqlSessionFactory对象
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
      // 创建一个sqlSession对象
      SqlSession sqlSession = sqlSessionFactory.openSession();
      try {
          //获得MyBatis框架生成的UserMapper接口的代理对象
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
          User user = userMapper.findById(1);
          System.out.println(user);
      } finally {
          sqlSession.close();
      }
  }
  ```

- **总结**

  通过对``*mapper.xml``映射配置文件的编写规范，可以通过代理的方式省去手动创建mapper接口实现类，更加便捷，也是企业中常用方式。

  

### 2.3 Mybatis相应API介绍

#### 2.3.1 SqlSession工厂构建器：SqlSessionFactoryBuilder

**常用API**：SqlSessionFactory  build(InputStream inputStream)

**作用**：通过加载SqlMapConfig.xml核心配置文件文件的输入流形式构建一个SqlSessionFactory对象

```java
// Resources工具类，它会读取SqlMapConfig.xml核心配置文件加载陈字节流
InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
// SqlSessionFactoryBuilder是一个sqlSession工厂构建器
SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
//解析配置文件并创建sqlSessionFactory工厂
SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
```

#### 2.3.2 SqlSession工厂对象：SqlSessionFactory

​	SqlSessionFactory有多个方法创建SqlSession实例，常用的API有如下两种

![image-20210313174506408](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174506408.png)

​	sqlSession会默认开启一个事务，但是该事务不会自动提交。

​	想要sqlSession自动提交事务则在创建sqlSession的时候通过 ``sqlSessionFactory.openSession(true)``方法创建sqlSession

#### 2. 3.3 SqlSession会话对象

SqlSession中你会看到所有执行语句、提交或回滚事务和获取映射器实例的方法。

**主要的内置执行语句的方法有：**

```java
//查询单个
<T> T selectOne(String statement, Object parameter)
//查询所有
<E> List<E> selectList(String statement, Object parameter)
//插入
int insert(String statement, Object parameter)
//修改
int update(String statement, Object parameter)
//删除
int delete(String statement, Object parameter)
```

**主要的事务方法有：**

sqlSession会默认开启一个事务，但是该事务不会自动提交，所以在前面我们通过了``sqlSession.commit()``去手动提交了事务

想要自动提交则使用``sqlSessionFactory.openSession(true)``方法创建sqlSession

```java
//提交事务
void commit()
//回滚事务
void rollback()
```



## 三、MyBatis配置文件深入

### 3.1 *mapper.xml映射文件

![image-20210313174711673](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174711673.png)

### 3.2 sqlMapConfig.xml核心配置文件层级关系

![image-20210313174732221](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174732221.png)

### 3.3 sqlMapConfig.xml中常用配置解析

#### 3.3.1 environments标签

数据库环境的配置，支持多环境配置

![image-20210313174807797](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174807797.png)

**其中，事务管理器（transactionManager）类型有两种：**

- JDBC：这个配置就是直接使用了JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务作用域。（常用）
- MANAGED：这个配置几乎没做什么。它从来不提交或回滚一个连接，而是让容器来管理事务的整个生 命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接，然而一些容器并不希望这样，因 此需要将 closeConnection 属性设置为 false 来阻止它默认的关闭行为。

**其中，数据源（dataSource）类型有三种：**

- UNPOOLED：这个数据源的实现是每次被请求时打开和关闭连接。没有用到连接池
- POOLED：这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来。（常用）
- JNDI：这个数据源的实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置 数据源，然后放置一个 JNDI 上下文的引用。

#### 3.3.2 mapper标签

该标签的作用是加载*mapper.xml映射配置文件的，加载方式有如下几种：

```xml
•使用相对于类路径的资源引用，例如：
<mapper resource="org/mybatis/builder/AuthorMapper.xml"/>

•使用完全限定资源定位符（URL），例如：
<mapper url="file:///var/mappers/AuthorMapper.xml"/>

•使用映射器接口实现类的完全限定类名，例如：
<mapper class="org.mybatis.builder.AuthorMapper"/>

•将包内的映射器接口实现全部注册为映射器，例如：（常用）
<package name="org.mybatis.builder"/>
```

#### 3.3.3 properties标签

实际开发中，习惯将数据库的配置信息单独抽取成一个properties文件，该标签将可以加载额外配置的properties文件

![image-20210313174931786](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313174931786.png)

- **创建jdbc.properties数据库配置文件**

  在resources下创建jdbc.properties，等同于将数据库配置从sqlMapConfig.xml中抽取出来，并在sqlMapConfig.xml中加载jdbc.properties数据库配置

  ```xml
  jdbc.driver=com.mysql.jdbc.Driver
  jdbc.url=jdbc:mysql:///eayon_mybatis
  jdbc.username=root
  jdbc.password=1234
  ```

- **在sqlMapConfig.xml中加载jdbc.properties数据库配置**

  在sqlMapConfig.xml中通过properties标签加载外部的jdbc.properties数据库配置，并修改``environment`` 标签中写死的数据库配置信息

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  
      <!--
          properties：加载外部配置文件
          properties标签必须放在configuration标签下的第一个，否则报错
          引入数据库配置文件
          resources：jdbc.properties文件的全路径
      -->
      <properties resource="jdbc.properties"></properties>
  
      <!--
          environments：是配置多个jdbc运行环境
        default：表示使用的默认环境
      -->
      <environments default="dev">
          <!--
           environment：标签用来配置一个环境
           id：是环境的标识
         -->
          <environment id="dev">
              <!--
                  当前事务交由JDBC进行管理
              transactionManager：配置使用什么样类型的数据库事务管理
              type="JDBC"：表示启用事务，有commit和rollback操作，常用
              type="MANAGED"：表示不直接控制事务。交给容器处理，几乎不用。
            -->
              <transactionManager type="JDBC"/>
              <!--
              dataSource标签配置连接池
              type="POOLED"：表示启用数据库连接池
              type="UNPOOLED"：表示不启用数据库连接池
            -->
              <dataSource type="POOLED">
                  <!-- 连接数据库的驱动类 -->
                  <property name="driver" value="${jdbc.driver}" />
                  <!-- 数据库访问地址 -->
                  <property name="url" value="${jdbc.url}" />
                  <!-- 数据库用户名 -->
                  <property name="username" value="${jdbc.username}" />
                  <!-- 数据库密码 -->
                  <property name="password" value="${jdbc.password}" />
              </dataSource>
          </environment>
      </environments>
  
      <!--引入mapper映射配置文件-->
      <mappers>
          <mapper resource="mapper/UserMapper.xml"></mapper>
      </mappers>
  
  </configuration>
  ```

#### 3.3.4 typeAliases标签

可以为*mapper.xml配置文件中的java类型的全限定名取一个名别

- **比如原来\*mapper.xml中User全限定名称如下**

![image-20210313175109229](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175109229.png)

- **我们就可以在sqlMapConfig.xml中配置typeAliases标签。为com.eayon.pojo.User定义别名为user**

![image-20210313175133670](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175133670.png)

- **修改原来\*mapper.xml中User的全限定名为别名**

![image-20210313175149477](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175149477.png)

​	上面是我们自定义的别名，MyBatis已经为我们定义好了一些常用的类型别名，可以直接使用

![image-20210313175207567](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175207567.png)



## 四、动态SQL及SQL片段

Mybatis 的映射文件中，前面我们的 SQL 都是比较简单的，有些时候业务逻辑复杂时，我们的 SQL是动 态变化的，此时在前面的学习中我们的 SQL 就不能满足要求了。

### 4.1 if语句

我们根据实体类的不同取值，使用不同的 SQL语句来进行查询。比如在 id如果不为空时可以根据id查 询，如果username 不同空时还要加入用户名作为条件。这种情况在我们的多条件组合查询中经常会碰到。

- **第一步：编写多条件查询方法**

  在UserMapper中新增如下方法

  ```java
  //多条件查询：演示if
  public List<User> findByCondition(User user);
  ```

- **第二步：UserMapper.xml中新增多条件查询语句**

  ```xml
  <!--多条件查询 用于演示：if-->
  <select id="findByCondition" resultType="com.eayon.pojo.User">
      SELECT id,username FROM user WHERE
      <if test="id != null">
          id = #{id}
      </if>
      <if test="username != null">
          username = #{username}
      </if>
  </select>
  ```

- **第三步：测试**

  ```java
  /**
   * 测试动态SQL语句-- if
   * @throws IOException
   */
  @Test
  public void test2() throws IOException {
      // 读取mybatis的核心配置文件
      InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
      // 通过SqlSessionFactoryBuilder创建一个SqlSessionFactory对象
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
      // 创建一个sqlSession对象
      SqlSession sqlSession = sqlSessionFactory.openSession();
      try {
          //获得MyBatis框架生成的UserMapper接口的代理对象
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
          User user = new User();
          user.setId(1);
          List<User> users = userMapper.findByCondition(user);
          System.out.println(users);
      } finally {
          sqlSession.close();
      }
  }
  ```

### 4.2 where语句

where语句，可以帮我们在多个动态语句中，有效的去掉前面的多余的and 或 or 之类的多余关键字

- **第一步：UserMapper.xml配置文件**

  ```xml
  <!--多条件查询 用于演示：if - where -->
  <select id="findByCondition" resultType="com.eayon.pojo.User">
      SELECT id,username FROM user
      <where>
          <if test="id != null">
              id = #{id}
          </if>
          <if test="username != null">
              username = #{username}
          </if>
      </where>
  </select>
  ```

- **第二步：测试**

  ```java
  /**
   * 测试动态SQL语句-- if - where
   * @throws IOException
   */
  @Test
  public void test2() throws IOException {
      // 读取mybatis的核心配置文件
      InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
      // 通过SqlSessionFactoryBuilder创建一个SqlSessionFactory对象
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
      // 创建一个sqlSession对象
      SqlSession sqlSession = sqlSessionFactory.openSession();
      try {
          //获得MyBatis框架生成的UserMapper接口的代理对象
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
          User user = new User();
          user.setId(1);
          List<User> users = userMapper.findByCondition(user);
          System.out.println(users);
      } finally {
          sqlSession.close();
      }
  }
  ```

### 4.3 foreach语句

foreach语句，可以遍历输出一个集合的数据

- **第一步：编写多值查询方法**

  在UserMapper中新增如下方法

  ```java
  //多值查询：演示foreach
  public List<User> findByIds(List<Integer> ids);
  ```

- **第二步：UserMapper.xml中新值查询语句**

  ```xml
  <!--多值查询：演示foreach-->
  <select id="findByIds" resultType="com.eayon.pojo.User">
      SELECT id,username FROM user
      <where>
          id IN
          /*
          collection：表示遍历的集合
          open：表示遍历输出之前的内容
          close：表示遍历输出后的内容
          separator：每遍历一个元素中间进行间隔的内容
          item：当前正在遍历的元素名称
          */
          <foreach collection="list" open="(" separator="," close=")" item="item_id">
              #{item_id}
          </foreach>
      </where>
  </select>
  ```

- **第三步：测试**

  ```java
  /**
   * 测试动态SQL语句-- foreach
   * @throws IOException
   */
  @Test
  public void test3() throws IOException {
      // 读取mybatis的核心配置文件
      InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
      // 通过SqlSessionFactoryBuilder创建一个SqlSessionFactory对象
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
      // 创建一个sqlSession对象
      SqlSession sqlSession = sqlSessionFactory.openSession();
      try {
          //获得MyBatis框架生成的UserMapper接口的代理对象
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
          List<Integer> ids = new ArrayList();
          ids.add(1);
          ids.add(2);
          List<User> users = userMapper.findByIds(ids);
          System.out.println(users);
      } finally {
          sqlSession.close();
      }
  }
  ```



### 4.3 SQL片段抽取

我们会发现上面我们在UserMapper.xml中写的SQ语句中有很多重复部分，每次都需要去写重复SQL很复杂，那么我们就可以进行抽取

- **第一步：在UserMapper.xml中编写抽取出来的SQL片段**

  ```xml
  <!--SQL片段抽取-->
  <sql id="baseSql">
      SELECT id,username FROM user
  </sql>
  ```

- **第二步：修改原来那些SQL语句，使用SQL片段进行替换**

  我随便拿了一个进行举例

  ```xml
  <select id="findById" parameterType="integer" resultType="user">
      <include refid="baseSql"></include> WHERE id = #{id}
  </select>
  ```

  

## 五、MyBatis复杂映射开发

### 5.1 一对一查询

#### 5.1.1 一对一查询的模型及需求

​	**用户表和订单表的关系为**：一个用户有多个订单，一个订单只从属于一个用户 

​	**一对一查询的需求**：查询一个订单，与此同时查询出该订单所属的用户信息。

![image-20210313175727113](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175727113.png)

#### 5.1.2 一对一查询的SQL语句

```sql
//对应的sql语句
select * from orders o,user u where o.uid=u.id.id
```

​	**查询的结果如下：**

​	![image-20210313175804708](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313175804708.png)

#### 5.1.3 一对一查询的具体实现

- **第一步：新建Order(订单表)及User(用户表)**

  ```sql
  SET NAMES utf8mb4;
  SET FOREIGN_KEY_CHECKS = 0;
  
  -- ----------------------------
  -- Table structure for orders
  -- ----------------------------
  DROP TABLE IF EXISTS `orders`;
  CREATE TABLE `orders`  (
    `id` int(11) NOT NULL,
    `uid` int(11) DEFAULT NULL,
    `order_time` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci DEFAULT NULL,
    `total` double DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = Compact;
  
  -- ----------------------------
  -- Records of orders
  -- ----------------------------
  INSERT INTO `orders` VALUES (1, 1, '2019-12-12', 3000);
  INSERT INTO `orders` VALUES (2, 1, '2019-12-12', 4000);
  INSERT INTO `orders` VALUES (3, 2, '2019-12-12', 5000);
  
  -- ----------------------------
  -- Table structure for user
  -- ----------------------------
  DROP TABLE IF EXISTS `user`;
  CREATE TABLE `user`  (
    `id` int(11) NOT NULL,
    `username` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = Compact;
  
  -- ----------------------------
  -- Records of user
  -- ----------------------------
  INSERT INTO `user` VALUES (1, 'tom');
  INSERT INTO `user` VALUES (2, 'lucy');
  
  SET FOREIGN_KEY_CHECKS = 1;
  ```

- **第二步：新建测试工程，项目名称：mybatis_multiable**

- **第三步：导入MyBatis坐标及其他相关**

  ```xml
  <!--引入依赖-->
  <dependencies>
      <!--mybatis坐标-->
      <dependency>
          <groupId>org.mybatis</groupId>
          <artifactId>mybatis</artifactId>
          <version>3.4.5</version>
      </dependency>
      <!--mysql驱动坐标-->
      <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>5.1.6</version>
          <scope>runtime</scope>
      </dependency>
      <!--单元测试坐标-->
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.12</version>
      </dependency>
      <!--日志-->
      <dependency>
          <groupId>log4j</groupId>
          <artifactId>log4j</artifactId>
          <version>1.2.12</version>
      </dependency>
  </dependencies>
  ```

- **第四步：创建User及Order实体**

  在``com.eayon.pojo``下创建

  ```java
  package com.eayon.pojo;
  
  
  public class User {
      private Integer id;
      private String username;
  }
  ```

  ```java
  package com.eayon.pojo;
  
  public class Orders {
      private Integer id;
      private String orderTime;
      private Double total;
      private Integer uid;
      
      //该订单属于哪个用户，不存在于表字段
      private User user;
  }
  ```

- **第五步：创建OrdersMapper接口**

  在``com.eayon.mapper``下创建

  ```java
  xxxxxxxxxx package com.eayon.mapper;import com.eayon.pojo.Orders;import java.util.List;public interface OrdersMapper {    //查询订单的同时并查询该订单所属用户信息    List<Orders> findOrderAndUser();}	java
  ```

- **第六步：创建OrdersMapper.xml映射配置文件**

  在resources下的``com.eayon.mapper``下创建

  由于Order实体中包含了User实体属性，而且我们的需求是：查询订单及相关用户信息，所以按照原来的resultType标签属性就无法进行查询结果的封装了，所以这里使用了resultMap自定义实体属性与表字段映射关系的配置

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
  <mapper namespace="com.eayon.mapper.OrdersMapper">
  
      <!-- type ：当前resultMap返回值类型-->
      <resultMap id="orderMap" type="com.eayon.pojo.Orders">
          <!--
              property：实体中属性
              column：数据库中字段
              jdbcType:数据类型
          -->
          <result property="id" column="id" jdbcType="INTEGER"/>
          <result property="orderTime" column="order_time" jdbcType="VARCHAR"/>
          <result property="total" column="total" jdbcType="DOUBLE"/>
          <result property="uid" column="uid" jdbcType="INTEGER"/>
  
          <!--配置User实体的映射-->
          <association property="user" javaType="com.eayon.pojo.User">
              <result property="id" column="uid"/>
              <result property="username" column="id"/>
          </association>
      </resultMap>
  
      <!--查询订单的同时并查询该订单所属用户信息-->
      <!-- resultMap：手动配置实体属性与表字段的映射关系-->
      <select id="findOrderAndUser" resultMap="orderMap">
          SELECT * FROM orders o,user u WHERE o.uid = u.id
      </select>
  
  </mapper>
  ```

- **第七步：创建jdbc.properties数据库配置文件**

  在resources下创建jdbc.properties

  ```properties
  jdbc.driver=com.mysql.jdbc.Driver
  jdbc.url=jdbc:mysql:///eayon_mybatis
  jdbc.username=root
  jdbc.password=1234
  ```

- **第八步：创建sqlMapConfig.xml核心配置文件**

  在resources下创建

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  
      <!--
          properties：加载外部配置文件
          properties标签必须放在configuration标签下的第一个，否则报错
          引入数据库配置文件
          resources：jdbc.properties文件的全路径
      -->
      <properties resource="jdbc.properties"></properties>
  
      <!--
          environments：是配置多个jdbc运行环境
        default：表示使用的默认环境
      -->
      <environments default="dev">
          <!--
               environment：标签用来配置一个环境
               id：是环境的标识
          -->
          <environment id="dev">
              <!--
                  当前事务交由JDBC进行管理
                  transactionManager：配置使用什么样类型的数据库事务管理
                  type="JDBC"：表示启用事务，有commit和rollback操作，常用
                  type="MANAGED"：表示不直接控制事务。交给容器处理，几乎不用。
            -->
              <transactionManager type="JDBC"/>
              <!--
              dataSource标签配置连接池
              type="POOLED"：表示启用数据库连接池
              type="UNPOOLED"：表示不启用数据库连接池
            -->
              <dataSource type="POOLED">
                  <!-- 连接数据库的驱动类 -->
                  <property name="driver" value="${jdbc.driver}" />
                  <!-- 数据库访问地址 -->
                  <property name="url" value="${jdbc.url}" />
                  <!-- 数据库用户名 -->
                  <property name="username" value="${jdbc.username}" />
                  <!-- 数据库密码 -->
                  <property name="password" value="${jdbc.password}" />
              </dataSource>
          </environment>
      </environments>
  
      <!--引入mapper映射配置文件-->
      <mappers>
          <!--加载该目录下所有的mapper映射文件-->
          <package name="com.eayon.mapper"/>
          <!--<mapper resource="com/eayon/mapper/OrdersMapper.xml"></mapper>-->
      </mappers>
  
  </configuration>
  ```

  **注意**：这里引入Mapper映射文件的时候我们没有使用原来的``<mapper resource = ""/>``的方式引入，因为如果有多个Mapper文件的时候我们都要去写一个标签去引入就非常的复杂。所以我们这里使用了``<package name=""/>``的方式去加载某包下的所有mapper文件。但是需要注意的是，mapper文件须和mapper接口同包同名，除此之外，maven默认不会将xml文件进行加载，所以我们需要在pom中加入以下代码让他去主动加载

  ```xml
  <build>
      <resources>
          <resource>
              <directory>src/main/java</directory>
              <includes>
                  <include>**/*.xml</include>
              </includes>
          </resource>
      </resources>
  </build>
  ```

- **第九步：测试一对一复杂查询**

  在``com.eayon.test``下创建

  ```java
  package com.eayon.test;
  
  
  import com.eayon.mapper.OrdersMapper;
  import com.eayon.pojo.Orders;
  import org.apache.ibatis.io.Resources;
  import org.apache.ibatis.session.SqlSession;
  import org.apache.ibatis.session.SqlSessionFactory;
  import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  import org.junit.Test;
  
  import java.io.IOException;
  import java.io.InputStream;
  import java.util.List;
  
  
  public class MyTest {
  
      /**
       * 测试一对一查询
       */
      @Test
      public void test1() throws IOException {
          InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
          SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
          SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
          SqlSession sqlSession = sqlSessionFactory.openSession();
  
          OrdersMapper ordersMapper = sqlSession.getMapper(OrdersMapper.class);
          List<Orders> orders = ordersMapper .findOrderAndUser();
          for (Orders order : orders) {
              System.out.println(order);
          }
      }
  }
  ```

### 5.2 一对多查询

#### 5.2.1 一对多查询的模型及需求

​	**用户表和订单表的关系**：一个用户有多个订单，一个订单只属于一个用户

​	**一对多查询的需求**：查询所有用户，与此同时查询出每个用户具体的订单信息。

​	![image-20210313180300360](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313180300360.png)

#### 5.2.2 一对多查询的语句

```sql
//对应的sql语句
select u.*,o.id oid,o.order_time orderTime,o.total,o.uid from user u left join orders o on u.id=o.uid
```

​	**查询的结果如下：**

​	![image-20210313180339773](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313180339773.png)

#### 5.2.3 一对多查询的具体实现

- **第一步：修改User实体**

  在User实体中使用集合表示具有的订单信息

  ```java
  package com.eayon.pojo;
  
  
  import java.util.List;
  
  public class User {
      private Integer id;
      private String username;
  
      //当前用户具备那些订单
      private List<Orders> ordersList;
  }
  ```

- **第二步：创建UserMapper接口**

  ```java
  package com.eayon.mapper;
  
  
  import com.eayon.pojo.User;
  
  import java.util.List;
  
  public interface UserMapper {
  
      //查询所有用户信息，并查出每个用户关联的订单信息
      List<User> findAll();
  }
  ```

- **第三步：创建UserMapper.xml映射文件**

  在resources下的``com.eayon.mapper``目录创建

  由于User实体包含了``List<Orders>``集合，而且我们需求是：查询所有用户及具有的订单信息，所以按照原来的resultType标签属性就无法进行查询结果的封装了，所以这里使用了resultMap自定义实体属性与表字段映射关系的配置

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
  <mapper namespace="com.eayon.mapper.UserMapper">
  
      <!-- type ：当前resultMap返回值类型-->
      <resultMap id="userMap" type="com.eayon.pojo.User">
          <id property="id" column="id"/>
          <result property="username" column="username"/>
  
          <!--
              property：实体中属性
              column：数据库中字段
              ofType：集合中元素实体的全路径
          -->
          <collection property="ordersList" ofType="com.eayon.pojo.Orders">
              <id property="id" column="oid"/>
              <result property="uid" column="uid"/>
              <result property="orderTime" column="orderTime"/>
              <result property="total" column="total"/>
          </collection>
      </resultMap>
  
      <!--查询所有用户信息，并查出每个用户关联的订单信息-->
      <select id="findAll" resultMap="userMap">
          select u.*,o.id oid,o.order_time orderTime,o.total,o.uid from user u left join orders o on u.id=o.uid
      </select>
  </mapper>
  ```

- **第四步：测试一对多复杂查询**

  ```java
  /**
   * 测试一对多查询
   */
  @Test
  public void test2() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession();
  
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<User> userList = userMapper.findAll();
      for (User user : userList) {
          System.out.println(user);
      }
  }
  ```

### 5.3 多对多查询

#### 5.3.1 多对多查询的模型及需求

​	**用户表和角色表的关系**：一个用户有多个角色，一个角色被多个用户使用

​	**多对多查询的需求**：查询用户的同时查询处该用户的所有角色	

![image-20210313184739773](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313184739773.png)

#### 5.3.2 多对多查询的语句

```sql
//对应的sql语句
select u.*,r.id rid,r.role_name from user u left join user_role ur on u.id=ur.user_id inner join role r on ur.role_id=r.id
```

​	**查询的结果如下：**

![image-20210313184821069](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313184821069.png)

#### 5.3.3 多对多查询的具体实现

- **第一步：创建数据库表**

  ```sql
  DROP TABLE IF EXISTS `role`;
  CREATE TABLE `role`  (
    `id` int(11) NOT NULL,
    `role_name` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = Compact;
  
  -- ----------------------------
  -- Records of role
  -- ----------------------------
  INSERT INTO `role` VALUES (1, 'CEO');
  INSERT INTO `role` VALUES (2, 'CFO');
  INSERT INTO `role` VALUES (3, 'COO');
  
  SET FOREIGN_KEY_CHECKS = 1;
  
  
  DROP TABLE IF EXISTS `user_role`;
  CREATE TABLE `user_role`  (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) DEFAULT NULL,
    `role_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = Compact;
  
  -- ----------------------------
  -- Records of user_role
  -- ----------------------------
  INSERT INTO `user_role` VALUES (1, 2, 1);
  INSERT INTO `user_role` VALUES (2, 2, 2);
  INSERT INTO `user_role` VALUES (3, 1, 2);
  INSERT INTO `user_role` VALUES (4, 1, 3);
  
  SET FOREIGN_KEY_CHECKS = 1;
  
  ```

- **第二步：创建Role实体**

  在``com.eayon.pojo``下

  ```java
  package com.eayon.pojo;
  
  
  public class Role {
      private Integer id;
      private String roleName;
  }
  ```

- **第三步：修改User实体**

  实体中添加用户具备的角色集合属性

  ```java
  package com.eayon.pojo;
  
  
  import java.util.List;
  
  public class User {
      private Integer id;
      private String username;
  
      //当前用户具备那些订单
      private List<Orders> ordersList;
      
      //代表当前用户具备那些角色
      private List<Role> roleList;
  }
  ```

- **第四步：在UserMapper接口中添加多对多查询方法**

  ```java
  //查询用户的同时查询处该用户的所有角色
  List<User> findAllUserAndRole();
  ```

- **第五步：配置UserMapper.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
  <mapper namespace="com.eayon.mapper.UserMapper">
      
      //此处省略部分与本部分无关代码
      
      <resultMap id="userRoleMap" type="com.eayon.pojo.User">
          <id property="id" column="id"/>
          <result property="username" column="username"/>
  
          <!--
              property：实体中属性
              column：数据库中字段
              ofType：集合中元素实体的全路径
          -->
          <collection property="roleList" ofType="com.eayon.pojo.Role">
              <id property="id" column="rid"/>
              <result property="roleName" column="role_name"/>
          </collection>
      </resultMap>
  
      <!--查询用户的同时查询处该用户的所有角色-->
      <select id="findAllUserAndRole" resultMap="userRoleMap">
          select u.*,r.id rid,r.role_name from user u left join user_role ur on u.id=ur.user_id inner join role r on ur.role_id=r.id
      </select>
  </mapper>
  ```

- **第六步：测试多对多查询**

  ```java
  /**
   * 测试多对多查询
   */
  @Test
  public void test3() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession();
  
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<User> users = userMapper.findAllUserAndRole();
      for (User user : users) {
          System.out.println(user);
      }
  }
  ```

  

## 六、MyBatis注解开发

### 6.1 常用注解

- @Insert：实现新增 

- @Update：实现更新
- @Delete：实现删除 
- @Select：实现查询 
- @Result：实现结果集封装 
- @Results：可以与@Result 一起使用，封装多个结果集 
- @One：实现一对一结果集封装 
- @Many：实现一对多结果集封装

### 6.2 注解方式进行CRUD

​	我们完成简单的user表的增删改查的操作

- **第一步：编写UserMapper接口中的CRUD方法**

  ```java
  package com.eayon.mapper;
  
  
  import com.eayon.pojo.User;
  import org.apache.ibatis.annotations.Delete;
  import org.apache.ibatis.annotations.Insert;
  import org.apache.ibatis.annotations.Select;
  import org.apache.ibatis.annotations.Update;
  
  import java.util.List;
  
  public interface UserMapper {
  
      //注解方式：添加用户
      @Insert("insert into user values(#{id},#{username})")
      void addUser(User user);
  
      //注解方式：修改用户
      @Update("update user set username = #{username} where id = #{id}")
      void updateUser(User user);
  
      //注解方式：查询用户
      @Select("select * from user")
      List<User> findUsers();
  
      //注解方式：删除用户
      @Delete("delete from user where id = #{id}")
      void delUser(Integer id);
  }
  ```

- **第二步：测试使用注解方式进行CRUD**

  ```java
  /**
   * 测试注解方式进行CRUD
   */
  @Test
  public void test4() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession(true);//自动提交事务
  
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      //增加
      //userMapper.addUser(new User("张三"));
  
      //修改
      userMapper.updateUser(new User(4,"李四"));
  
      //查询
      /*List<User> users = userMapper.findUsers();
      for (User user : users) {
          System.out.println(user);
      }*/
  
      //删除
      userMapper.delUser(4);
  }
  ```



## 七、MyBatis缓存

### 7.1 缓存相关概念

缓存就是内存中的数据，常常来对数据库查询结果的保存，使用缓存，我们可以避免频繁的与数据库进行交互，进而提高响应速度。

**MyBatis也提供了对缓存的支持，分为一级缓存和二级缓存，可以通过下图来理解：**

![image-20210313180956287](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313180956287.png)

* 一级缓存是sqlSession级别的缓存，在操作数据库时需要构造sqlSession对象，在对象中有一个数据结构（HashMap）用于存储缓存数据。不同的sqlSession之间的缓存数据区域（HashMap）是互相不影响的。

* 二级缓存是mapper级别的缓存，多个sqlSession去操作同一个Mapper的sql语句，多个sqlSession可以共用二级缓存，二级缓存是跨sqlSession的。

* 一级缓存默认开启，二级缓存需要手动开启



### 7.2 一级缓存验证

#### 7.2.1 验证一

​	我们在一个sqlSession中，对User表根据id进行两次查询，看看控制台打印sql语句的情况

- **为了能打印sql语句，我们配置以下log4j**

  在``mybatis_multiable``工程的resources下创建log4j.properties文件并添加如下内容

  ```properties
  ### direct log messages to stdout ###
  log4j.appender.stdout=org.apache.log4j.ConsoleAppender
  log4j.appender.stdout.Target=System.out
  log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
  log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
  
  ### direct messages to file mylog.log ###
  log4j.appender.file=org.apache.log4j.FileAppender
  log4j.appender.file.File=d:/mylog.log
  log4j.appender.file.layout=org.apache.log4j.PatternLayout
  log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
  
  ### set log levels - for more verbose logging change 'info' to 'debug' ###
  
  log4j.rootLogger=debug, stdout
  ```

- **在UserMapper接口中编写用于测试的方法**

  ```java
  //测试一级缓存
  @Select("select * from user where id = #{id}")
  User findUserById(Integer id);
  ```

- **创建缓存测试类，并编写测试方法**

  在``mybatis_multiable``工程的``com.eayon.test``下创建

  ```java
  package com.eayon.test;
  
  import com.eayon.mapper.UserMapper;
  import com.eayon.pojo.User;
  import org.apache.ibatis.io.Resources;
  import org.apache.ibatis.session.SqlSession;
  import org.apache.ibatis.session.SqlSessionFactory;
  import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  import org.junit.Test;
  
  import java.io.IOException;
  import java.io.InputStream;
  
  public class CacheTest {
  
      /**
       * 测试一级缓存
       */
      @Test
      public void firstLevelCache() throws IOException {
          InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
          SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
          SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
          SqlSession sqlSession = sqlSessionFactory.openSession(true);//自动提交事务
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
  
          //第一次查询：测试结果第一次查询打印了sql语句，并将查询出来的结果放进了缓存
          User user = userMapper.findUserById(1);
          System.out.println(user);
  
          //第二次查询：由于使用的使用一个sqlSession，所以他并没有走数据库，没有打印sql，走的是一级缓存
          User user2 = userMapper.findUserById(1);
          System.out.println(user2);
          
          //判断他俩是否为同一个对象：结果为true
          System.out.println(user == user2);
      }
  }
  ```

- **控制台打印情况**

  ![image-20210313181157690](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181157690.png)

- **一张图说清一级缓存**

  ![image-20210313181215092](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181215092.png)

#### 7.2.2 验证二

我们在一个sqlSession中，对User表根据id进行两次查询，但是在两次查询中间对查询的这条数据进行更新

- **编写测试类**

  ```java
  /**
   * 测试一级缓存2
   */
  @Test
  public void firstLevelCache2() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
      SqlSession sqlSession = sqlSessionFactory.openSession(true);//自动提交事务
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
  
      //第一次查询：测试结果第一次查询打印了sql语句，并将查询出来的结果放进了缓存
      User user = userMapper.findUserById(1);
      System.out.println(user);
  
      //对查询的数据进行更新：打印了更新语句，并且将缓存中的该条数据删除
      userMapper.updateUser(new User(1,"李四"));
  
      //第二次查询：答应了sql语句。在缓存中没有找到该数据，所以走了数据库，并将查询结果房近缓存
      User user2 = userMapper.findUserById(1);
      System.out.println(user2);
  
      //判断他俩是否为同一个对象：结果为true
      System.out.println(user == user2);
  }
  ```

- **控制台打印情况**

  ![image-20210313181257489](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181257489.png)

**7.2.3 总结**

- 第一次发起查询用户id为1的用户信息，先去找缓存中是否有id为1的用户信息，如果没有，从数据库查询用户信息。得到用户信息，将用户信息存储到一级缓存中。 

- 如果中间sqlSession去执行commit操作（执行插入、更新、删除），则会清空SqlSession中的一级缓存，这样做的目的为了让缓存中存储的是最新的信息，避免脏读。 

- 第二次发起查询用户id为1的用户信息，先去找缓存中是否有id为1的用户信息，缓存中有，直接从缓存中获取用户信息

  ![image-20210313181314652](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181314652.png)



### 7.3 一级缓存原理探究与源码分析

一级缓存到底是什么？一级缓存什么时候被创建、一级缓存的工作流程是怎样的？相信你现在应该会有 这几个疑问，那么我们本节就来研究一下一级缓存的本质

大家可以这样想，上面我们一直提到一级缓存，那么提到一级缓存就绕不开SqlSession,所以索性我们就**直接从SqlSession，看看有没有创建缓存或者与缓存有关的属性或者方法**

![image-20210313181356758](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181356758.png)

找了一圈发现上述所有方法中，好像只有``clearCache()``和缓存沾点关系，那么就直接从这个方法入手吧，分析源码时，我们要看它(此类)是谁，它的父类和子类分别又是谁，对如上关系了解了，你才会对这个类有更深的认识，一直往下跟方法，你可能会得到如下这个流程图

![image-20210313181420823](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181420823.png)

再深入分析，流程走到 ``Perpetualcache 中的 clear() ``方法之后，会调用其``cache.clear()``方法，那么这个 ``cache`` 是什么东西呢？点进去发现，cache其实就是``private Map cache = new HashMap()``；也就是一个Map，所以说``cache.clear() ``其实相当于 ``map.clear()``，也就是说，缓存其实就是本地存放的一个map对象，每一个SqISession都会存放一个map对象的引用

**那么这个cache是何 时创建的呢？**

你觉得最有可能创建缓存的地方是哪里呢？我觉得是Executor，为什么这么认为？因为Executor是执行器，用来执行SQL请求，而且清除缓存的方法也在Executor中执行，所以很可能缓存的创建也很有可能在Executor中，看了一圈发现Executor中有一个`` createCacheKey()``方法，这个方法很像是创建缓存的方法啊，跟进去看看，你发现 ``createCacheKey()`` 方法是由BaseExecutor执行的，``createCacheKey()`` 方法源码如下

```java
public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
  if (closed) {
    throw new ExecutorException("Executor was closed.");
  }
  
  //cacheKey 是由：statementId、分页参数、要执行的sql语句组成
  CacheKey cacheKey = new CacheKey();
  
  //其实就是statementId(namespace.id)
  cacheKey.update(ms.getId());
  
  //其实就是设置分页参数  offset就是0
  cacheKey.update(rowBounds.getOffset());
  
  //其实就是设置分页参数  limit就是Integer.MAXVALUE
  cacheKey.update(rowBounds.getLimit());
  
  //具体要执行的sql语句
  cacheKey.update(boundSql.getSql());
  
  List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
  TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
  // mimic DefaultParameterHandler logic
  for (ParameterMapping parameterMapping : parameterMappings) {
    if (parameterMapping.getMode() != ParameterMode.OUT) {
      Object value;
      String propertyName = parameterMapping.getProperty();
      if (boundSql.hasAdditionalParameter(propertyName)) {
        value = boundSql.getAdditionalParameter(propertyName);
      } else if (parameterObject == null) {
        value = null;
      } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
        value = parameterObject;
      } else {
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        value = metaObject.getValue(propertyName);
      }
      
      //这里其实就是设置sql中的参数而已
      cacheKey.update(value);
    }
  }
  
  //configuration是mybatis核心配置类，也就是我们sqlMapConfig.xml配置文件   通过核心配置类获取我们的mybatis环境配置
  if (configuration.getEnvironment() != null) {
    // configuration.getEnvironment().getId()：获取到的id也就是sqlMapConfig.xml配置文件中设置mybatis环境的environments标签中的id
    cacheKey.update(configuration.getEnvironment().getId());
  }
  return cacheKey;
}
```

通过上面的代码我们会发现，创建cacheKey（缓存key）会经过一系列的update方法，udate方法由一个CacheKey这个对象来执行的，这个 **update 方法里面其实就是通过updateList这个集合来把五个值存进去**，对照上面的代码和下面的图示，你应该能理解这五个值都是什么了

![image-20210313181639721](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181639721.png)

这里需要注意一下最后一个值，``configuration.getEnvironment().getId()``这是什么，这其实就是 定义在 sqlMapConfig.xml中的标签，见如下。

```xml
<environments default="dev">
    <environment id="dev">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <!-- 连接数据库的驱动类 -->
            <property name="driver" value="${jdbc.driver}" />
            <!-- 数据库访问地址 -->
            <property name="url" value="${jdbc.url}" />
            <!-- 数据库用户名 -->
            <property name="username" value="${jdbc.username}" />
            <!-- 数据库密码 -->
            <property name="password" value="${jdbc.password}" />
        </dataSource>
    </environment>
</environments>
```

那么我们回归正题，**那么创建完缓存之后该用在何处呢？**总不会凭空创建一个缓存不使用吧？绝对不会的，经过我对一级缓存的探究之后，我们发现一级缓存更多是用于查询操作，毕竟一级缓存也叫做查询缓存吧，所以缓存的创建肯定在查询方法里面。我们跟踪到 实现类BaseExcetor下的``query()``方法如下：

```java
@Override
 public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
   //从MappedStatement中获取到要执行的sql，你要是问MappedStatement是什么请回头看看自定义持久层框架篇
   BoundSql boundSql = ms.getBoundSql(parameter);
   //通过MappedStatement、参数、分页信息、执行sql来创建缓存key，也就是要存入到一级缓存HashMap中的key值
   CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
   //调用了下面的query重载方法
   return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
}
```

```java
 @SuppressWarnings("unchecked")
 @Override
 public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
   ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
   if (closed) {
     throw new ExecutorException("Executor was closed.");
   }
   if (queryStack == 0 && ms.isFlushCacheRequired()) {
     clearLocalCache();
   }
   List<E> list;
   try {
     queryStack++;
     
     //根据刚刚传递过来的缓存key去一级缓存中查询
     list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
     
     //如果查询到了数据
     if (list != null) {
       //查到了那就封装一下返回
       handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
       
     //如果一级缓存中没有数据
     } else {
       //没查到呢就进入下面的方法取查询数据库
       list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
     }
   } finally {
     queryStack--;
   }
   if (queryStack == 0) {
     for (DeferredLoad deferredLoad : deferredLoads) {
       deferredLoad.load();
     }
     // issue #601
     deferredLoads.clear();
     if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
       // issue #482
       clearLocalCache();
     }
   }
   return list;
 }
```

```java
//如果一级缓存中没有数据则会走此方法去查询数据库

private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  List<E> list;
  localCache.putObject(key, EXECUTION_PLACEHOLDER);
  try {
    list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
  } finally {
    localCache.removeObject(key);
  }
  
  //将缓存key和查询出来的结果存入到一级缓存HashMap
  localCache.putObject(key, list);
  if (ms.getStatementType() == StatementType.CALLABLE) {
    localOutputParameterCache.putObject(key, parameter);
  }
  return list;
}
```

在queryFromDatabase中，会对localcache进行写入。**localcache对象的putObject()方法其实就是给HashMap去存放缓存数据**

```java
private Map<Object, Object> cache = new HashMap<Object, Object>();

@Override
public void putObject(Object key, Object value) {
  cache.put(key, value);
}
```



### 7.4 二级缓存验证

二级缓存的原理和一级缓存原理一样，第一次查询，会将数据放入缓存中，然后第二次查询则会直接去缓存中取。但是**一级缓存是基于sqlSession的，而二级缓存是基于mapper文件的namespace的，也就是说多个sqlSession可以共享一个mapper中的二级缓存区域**，并且如果两个mapper的namespace相 同，即使是两个mapper,那么这两个mapper中执行sql查询到的数据也将存在相同的二级缓存区域中

![image-20210313181844775](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313181844775.png)

#### 7.4.1 验证

- **第一步：核心配置文件开启二级缓存**

  和一级缓存默认开启不一样，二级缓存需要我们手动开启

  在``mybatis_multiable``工程的下的核心配置文件sqlMapConfig.xml文件中加入如下代码:

  ```xml
  <!--开启二级缓存-->
  <settings>
      <setting name = "cacheEnabled" value="true"/>
  </settings>
  ```

- **第二步：Mapper映射文件中开启缓存**

  在UserMapper.xml中加入如下代码

  ```xml
  <cache></cache>
  ```

  **注意：**

  我们说二级缓存是Mapper级别的，那哪个Mapper需要开启缓存就在哪个Mapper文件下添加如上配置即可。

  当然这都是基于Mapper.xml映射文件开发是这样配置的，如果你是基于MyBatis注解开发就没有用到Mapper映射文件的话，只需要在对应的Mapper接口上加入 ``@CacheNamespace`` 注解也可以开启

- **第三步：User实体实现序列化**

  开启了二级缓存后，还需要将要缓存的pojo实现``Serializable``接口，因为二级缓存数据存储介质多种多样，不一定只存在内存中，有可能存在硬盘中，如果我们要再取这个缓存的话，就需要反序列化了。所以mybatis中的pojo都去实现``Serializable``接口

  ![image-20210313182044602](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182044602.png)

- **第四步：测试**

  在``com.eayon.test.CacheTest``加入如下测试方法

  ```java
  /**
   * 测试二级缓存
   */
  @Test
  public void secondLevelCache() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
  
      //构建三个session
      SqlSession sqlSession1 = sqlSessionFactory.openSession(true);
      SqlSession sqlSession2 = sqlSessionFactory.openSession(true);
  
      //通过三个不同的session获取三个UserMapper对象
      UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
      UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
  
      //使用不同的session（跨session）去进行查询
      User user1 = mapper1.findById(1);//注意这里使用的是Mapper映射文件查询，而非注解
      sqlSession1.close();//清空一级缓存
      User user2 = mapper2.findById(1);
  
      //判断跨session查询的user是否为同一个  结果：false
      //一级缓存是将查询出来的对象进行缓存，而二级缓存并没有缓存整个对象
      //而是将对象中的数据进行缓存，为我们重新创建了一个新的对象并且将缓存中的数据进行重新赋值
      System.out.println(user1 == user2);
  
  }
  ```

  **注意：**

  ​	1、由于我们是在Mapper映射文件中使用``<cache>``标签开启二级缓存，所以使用的查询操作一定是基于Mapper映射文件的方法，如果使用注解的查询方法则二级缓存不会生效。

  ​	2、同理如果我们是通过在Mapper接口上加入``@CacheNamespace``注解开启Mapper二级缓存，一定是使用基于注解查询的方法，如果使用基于Mapper映射文件的方法则二级缓存不会生效。

- **第五步：控制台打印结果分析**

  ![image-20210313182145938](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182145938.png)

#### 7.4.2 二级缓存的其他配置：useCache和flushCache

##### 7.4.2.1 userCache禁用二级缓存

- **基于Mpper映射文件开发时使用userCache关闭二级缓存**

  **userCache是用来设置是否禁用二级缓存**的，在statement中设置``useCache=false``**可以禁用当前select语句的二级缓存**，即每次查询都会发出sql去查询，**默认情况是true**,即该sql使用二级缓存

  ```xml
  <!--
      useCache="false" 禁用二级缓存
  -->
  <select id="findById" resultType="com.eayon.pojo.User" useCache="false">
      select * from user where id = #{id}
  </select>
  ```

- **基于MyBatis注解开发时使用userCache关闭二级缓存**

  只需要在需要关闭二级缓存的Mapper接口里的方法上加上如下注解即可，**注意：只对使用MyBatis注解的方法才有效**

  ```java
  @Options(useCache = false)//禁用二级缓存 默认开启true
  @Select("select * from user where id = #{id}")
  User findUserById(Integer id);
  ```



##### 7.4.2.2 flushCache刷新二级缓存**

我们知道在同一个Mapper中，默认只有``insert、update, delete``操作数据后才会需要刷新缓存，如果不执行刷新缓存会出现脏读。但其实我们也可以关闭在这些操作执行后刷新二级缓存，当然我们一般不会关闭，默认开启

- **基于Mpper映射文件开发时使用flushCacje关闭刷新二级缓存**

  设置statement配置中的``flushCache="true”``属性，默认情况下为true,即刷新缓存，如果改成false则不会刷新。所以会出现脏读的情况。

  下面我在``select``方法上关闭了刷新二级缓存，其实在更新操作方法上加入测试更好

  ```xml
  <!--
      useCache="false" 禁用二级缓存
      flushCache="false" 关闭刷新二级缓存
  -->
  <select id="findById" resultType="com.eayon.pojo.User" useCache="false" flushCache="false">
      select * from user where id = #{id}
  </select>
  ```



### 7.5 二级缓存原理探究与源码分析

实现二级缓存的时候我们首先在核心配置文件sqlMapConfig.xml加入了开启二级缓存的配置，然后在mapper.xml映射文件中也加入了``<cache>``标签开启该mapper的二级缓存，并且我们发现这个``<cache>``标签是空的，其实该标签默认有一个type属性属性值为``PerpetualCache``类的全限定名，如下所示：

```xml
<cache type="org.apache.ibatis.cache.impl.PerpetualCache"></cache>
```

**PerpetualCache类是mybatis默认实现缓存功能的类。我们如果不写type默认就是使用这个类的缓存。**

![image-20210313182506561](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182506561.png)

并且我们发现，这个PerpetualCache类还是实现了Cache接口，那么如果我们需要自定义二级缓存的话同样实现Cache接口也可以进行定义

那么**二级缓存的流程原理**到底是什么呢？根据之前分析一级缓存的经验，我们还是先进入Executor执行器中的``query``方法去看以下

通过下面的源码我们可以看出，首先去创建缓存key，然后去下一个``query``方法

```java
@Override
 public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
   BoundSql boundSql = ms.getBoundSql(parameter);
   //创建缓存key
   CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
   return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
}
```

**该query方法先用key去缓存中查询，查询不到则自然走queryFromDatabase方法去数据库查询**

```java
@Override
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
  if (closed) {
    throw new ExecutorException("Executor was closed.");
  }
  if (queryStack == 0 && ms.isFlushCacheRequired()) {
    clearLocalCache();
  }
  List<E> list;
  try {
    queryStack++;
    //去二级缓存功能类PerpetualCache的HashMap中查询缓存
    list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
    if (list != null) {
      handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
      
    //如果缓存中未查询到结果就从数据库查询
    } else {
      list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }
  } finally {
    queryStack--;
  }
  if (queryStack == 0) {
    for (DeferredLoad deferredLoad : deferredLoads) {
      deferredLoad.load();
    }
    // issue #601
    deferredLoads.clear();
    if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
      // issue #482
      clearLocalCache();
    }
  }
  return list;
}
```

**将数据库查询出来的结果放入PerpetualCache类中的HashMap缓存中**

```java
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  List<E> list;
  localCache.putObject(key, EXECUTION_PLACEHOLDER);
  try {
    list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
  } finally {
    localCache.removeObject(key);
  }
  //将数据库查询出来的结果放入PerpetualCache类中的HashMap缓存中
  localCache.putObject(key, list);
  if (ms.getStatementType() == StatementType.CALLABLE) {
    localOutputParameterCache.putObject(key, parameter);
  }
  return list;
}
```



### 7.6 Redis实现二级缓存（MyBatis分布式缓存）

#### 7.6.1 概述

上面我们介绍mybatis自带的二级缓存，但是这个缓存是单服务器工作，无法实现分布式缓存。 那么什么是分布式缓存呢？假设现在有两个服务器1和2,用户访问的时候访问了1服务器，查询后的缓存就会放在1服务器上PerpetualCache类中的HashMap缓存中，假设现在有个用户访问的是2服务器，那么他在2服务器上就无法获取刚刚那个缓存，如下图所示：

![image-20210313182635931](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182635931.png)

为了解决这个问题，就得找一个分布式的缓存，专门用来存储缓存数据的，这样不同的服务器要缓存数 据都往它那里存，取缓存数据也从它那里取，如下图所示：

![image-20210313182654132](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182654132.png)

如上图所示，在几个不同的服务器之间，我们使用第三方缓存，将缓存都放在这个第三方框架中, 然后无论有多少台服务器，我们都能从缓存中获取数据。 这里我们介绍mybatis与redis的整合。 刚刚提到过，mybatis提供了一个cache接口，如果要实现自己的缓存逻辑，实现cache接口开发即可。 mybatis本身默认实现了一个``PerpetualCache``，但是这个缓存的实现无法实现分布式缓存，所以我们要自己来实现。

#### 7.6.2 实现

- **第一步：引入pom**

  mybatis提供了一个针对cache接口的redis实现类RedisCache，该类存在``mybatis-redis``包中

  ```xml
  <!--mybatis提供针对cache缓存接口的redis实现类所在包-->
  <dependency>
      <groupId>org.mybatis.caches</groupId>
      <artifactId>mybatis-redis</artifactId>
      <version>1.0.0-beta2</version>
  </dependency>
  ```

- **第二步：配置mapper.xml映射文件**

  在映射文件中开启二级缓存，并指定二级缓存功能实现类

  ```xml
  <!--使用redisCache作为二级缓存的功能实现类-->
  <cache type="org.mybatis.caches.redis.RedisCache"></cache>
  ```

- **第三步：创建redis配置文件**

  在resource下创建``redis.properties``配置文件，内容如下

  redis环境请自行搭建

  ```properties
  redis.host=localhost
  redis.port=6379
  redis.connectionTimeout=5000
  redis.password=1234
  redis.database=0
  ```

- **第四步：测试**

  ```java
  /**
   * 测试Redis实现二级缓存
   */
  @Test
  public void redisCache() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
  
      //构建三个session
      SqlSession sqlSession1 = sqlSessionFactory.openSession(true);
      SqlSession sqlSession2 = sqlSessionFactory.openSession(true);
  
      //通过三个不同的session获取三个UserMapper对象
      UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
      UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
  
      //使用不同的session（跨session）去进行查询
      User user1 = mapper1.findById(1);//注意这里使用的是Mapper映射文件查询，而非注解
      sqlSession1.close();//清空一级缓存
      User user2 = mapper2.findById(1);
  
      //判断跨session查询的user是否为同一个  结果：false
      //一级缓存是将查询出来的对象进行缓存，而二级缓存并没有缓存整个对象
      //而是将对象中的数据进行缓存，为我们重新创建了一个新的对象并且将缓存中的数据进行重新赋值
      System.out.println(user1 == user2);
  
  }
  ```

- **第五步：控制台打印结果**

  ![image-20210313182856741](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182856741.png)

- **第六步：查询redis中是否存在缓存**

  ![image-20210313182915783](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313182915783.png)



## 八、MyBatis插件

### 8.1 插件简介

一般情况下，开源框架都会提供插件或其他形式的拓展点，供开发者自行拓展。这样的好处是显而易见 的，一是增加了框架的灵活性。二是开发者可以结合实际需求，对框架进行拓展，使其能够更好的工作。以MyBatis为例，我们可基于MyBatis插件机制实现分页、分表，监控等功能。由于插件和业务无关，业务也无法感知插件的存在。因此可以无感植入插件，在无形中增强功能

### 8.2 MyBatis插件介绍

Mybatis作为一个应用广泛的优秀的ORM开源框架，这个框架具有强大的灵活性，在**四大组件 (Executor、StatementHandler、ParameterHandler、ResultSetHandler)**处提供了简单易用的插件扩展机制。Mybatis对持久层的操作就是借助于四大核心对象。MyBatis支持用插件对四大核心对象进行拦截，对mybatis来说插件就是拦截器，用来增强四大组件中某一个的功能，增强功能本质上是借助于底层的动态代理实现的，换句话说，MyBatis中的四大对象都是代理对象

![image-20210313183044716](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313183044716.png)

**MyBatis四大组件介绍  及所允许拦截的方法如下：**

- **执行器Executor ：**负责增删改查的行为(``update、query、commit、rollback``等方法方法可被插件拦截)；
- **SQL语法构建器StatementHandler ：**主要借助它完成sql的预编译(``prepare、parameterize、batch、updates query``等方法方法可被插件拦截)； 
- **参数处理器ParameterHandler：** 来处理参数(``getParameterObject、setParameters``方法方法可被插件拦截)； 
- **结果集处理器ResultSetHandler ：**来处理返回结果集 (``handleResultSets、handleOutputParameters``方法可被插件拦截)；



### 8.3 MyBatis插件原理

**在四大对象创建的时候** 

* 每个创建出来的对象不是直接返回的，而是interceptorChain.pluginAll(parameterHandler); 

* 获取到所有的Interceptor (拦截器)(插件需要实现的接口)；调用 interceptor.plugin(target);返 回 target 包装后的对象 

* 插件机制，我们可以使用插件为目标对象创建一个代理对象；AOP (面向切面)我们的插件可以为四大对象创建出代理对象，代理对象就可以拦截到四大对象的每一个执行； 

**举例说明**

​	插件具体是如何拦截并附加额外的功能的呢？以**ParameterHandler**来说

​	当我们要去设置参数的时候会去创建**ParameterHandler**对象，（四大对象的创建方法都在Configuration核心配置类中）

​	那我们发现他其实并没有直接将创建的parameterHandler 对象返回，而是通过``interceptorChain.pluginAll``生成代理对象

```java
public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    //创建parameterHandler 原生对象
  ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
  //将原生对象parameterHandler 交给interceptorChain.pluginAll方法进行处理
  parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
  return parameterHandler;
}
```

​	``pluginAll``方法遍历所有的拦截器，来调用每一个拦截器的``plugin``方法将这个原生对象进行重重增强代理最后将代理对象返回。

​	这样我们的插件就可以针对该**ParameterHandler**代理对象执行的前后进行方法增强实现特殊功能

​	``interceptor.plugin(target)``中的target就可以理解为mybatis 中的四大对象

```java
public class InterceptorChain {

//interceptorChain保存了所有的拦截器(interceptors)，是mybatis初始化的时候创建的
  private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

  public Object pluginAll(Object target) {
      //遍历所有的拦截器
    for (Interceptor interceptor : interceptors) {
        //调用每一个拦截器的plugin方法将这个原生对象进行处理并返回该原生对象的代理对象
      target = interceptor.plugin(target);
    }
    return target;
  }
............

}
```

​	``plugin``方法其实就MyBatis下的一个拦截器``Interceptor``接口

​	如果你想自定义一个插件，那我们自定义的插件只需要实现该接口即可

```java
public interface Interceptor {
  Object plugin(Object target);
}
```



### 8.4 自定义插件

- **自定义一个插件类**

  在``mybatis_multiable``工程的``com.eayon.plugin``报下创建MyPlugin自定义插件类，并实现MyBatis 的``Interceptor``拦截接口

  ```java
  package com.eayon.plugin;
  
  import org.apache.ibatis.executor.statement.StatementHandler;
  import org.apache.ibatis.plugin.*;
  
  import java.sql.Connection;
  import java.util.Properties;
  
  /**
   * 自定义插件
   */
  @Intercepts({//注意看这个大花括号，也就这说这里可以定义多个@Signature对多个组件对象拦截，都用这个拦截器
          @Signature(type = StatementHandler.class,//你需要拦截MyBatis四大组件中的哪一个
                  method = "prepare",//拦截该组件对象中的哪个方法
                  args = {Connection.class, Integer.class}//有可能该组件对象中你想拦截的方法有重载，所以再通过参数进行精准定位
          )
  })
  public class MyPlugin implements Interceptor {
  
      /**
       * 第一：拦截方法 只要被拦截的目标对象的目标方法被执行时 每次都会执行当前这个方法
       */
      @Override
      public Object intercept(Invocation invocation) throws Throwable {
          //大家可以在目标方法执行时进行一些分页 监控等操作进行增强
  
          //模拟增强
          System.out.println("对方法进行了增强");
  
          return invocation.proceed();//执行目标方法
      }
  
      /**
       * 第二：主要为了把当前的拦截器生成代理对象存到拦截器链中（InterceptorChain类中的List<Interceptor> interceptors = new ArrayList<Interceptor>() 这个集合）
       */
      @Override
      public Object plugin(Object target) {
          //target：被拦截的目标对象
          Object wrap = Plugin.wrap(target, this);//生成代理对象
          return wrap;
      }
  
     /**
       * 该方法第一个执行
       * 获取配置文件中的参数
       * 该配置文件指核心配置文件sqlMapConfig.xml中<plugins>标签引入本插件时设置的参数
       */
      @Override
      public void setProperties(Properties properties) {
          //获取到的配置文件中的参数
          System.out.println("获取到的配置文件中的参数" + properties);
      }
  }
  ```

- **在核心配置文件sqlMapConfig,xml中配置我们自定义的插件**

  ```java
  @Test
  public void test1() throws IOException {
      InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
      SqlSession sqlSession1 = sqlSessionFactory.openSession(true);
      UserMapper mapper = sqlSession1.getMapper(UserMapper.class);
      User user = mapper.findById(1);
      System.out.println(user);
  }
  ```

**流程解释：**

当代码执行到  ``sqlSessionFactoryBuilder.build(resourceAsStream); ``的时候会去创建 ``StatementHandler`` 组件对象，那么他既然创建就会通过``interceptorChain.pluginAll(StatementHandler) ``方法去遍历所有的拦截器，但遍历到我们自定义的插件拦截器的时候就会通过`` interceptor.plugin(target) ``方法首先进入我们插件的``setProperties``方法去设置参数。然后在进入插件的``plugin``方法对目标对象生成代理对象，然后进入插件的``intercept``方法对代理对象的目标方法进行增强。



### 8.5 插件源码分析

通过上述流程解释发现我们自定义的插件类中三个方法的执行顺序：``setProperties -> plugin -> intercept``

第一个``setProperties``没什么好说的，就是获取配置参数。

当执行到第二个``plugin``方法的时候使用了 ``Object wrap = Plugin.wrap(target, this); ``来生成代理对象，我们来看一下``Plugin``类

![image-20210313183635186](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210313183635186.png)

我们首先发现Plugin实现了``InvocationHandler``接口 **JDK动态代理**啊，那么就算在我们自定义框架的``plguin``方法中调用了``Plugin.wrap``方法也会被Plugin中的``invoke``方法拦截（invoke方法会拦截所有的方法调用）。那么我们来找一下有没有``invoke``方法

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  try {
    //method.getDeclaringClass() ： 获取调用了当前Plugin类中方法的调用者方法的所属类Class（也就是我们的自定义插件类）
    //从signatureMap中获取该方法所属类Class(我们的自定义插件类)中注解配置的拦截方法列表
    Set<Method> methods = signatureMap.get(method.getDeclaringClass());
    //判断有没有方法列表 并且你这个方法列表中是否包含拦截的方法
    if (methods != null && methods.contains(method)) {
        //interceptor对象：其实就是我们的自定义插件类
        //interceptor方法：就是我们自定义插件类中的intercept方法
        //总结：执行自定义插件类中的增强逻辑
      return interceptor.intercept(new Invocation(target, method, args));
    }
    return method.invoke(target, args);
  } catch (Exception e) {
    throw ExceptionUtil.unwrapThrowable(e);
  }
}
```

``invoke``方法的代码比较少，逻辑不难理解。首先,``invoke``方法会检测被拦截方法是否配置在插件的 ``@Signature``注解中，若是，则执行插件的增强逻辑，否则执行被拦截方法。



**多说一嘴**：

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/1阶段：开源框架源码剖析/01模块：自定义持久层框架设计及MyBatis源码分析)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)