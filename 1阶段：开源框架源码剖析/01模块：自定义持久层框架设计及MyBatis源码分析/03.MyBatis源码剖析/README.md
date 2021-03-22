# MyBatis架构及源码剖析

## 说在前面

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/1%E9%98%B6%E6%AE%B5%EF%BC%9A%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01%E6%A8%A1%E5%9D%97%EF%BC%9A%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%8F%8AMyBatis%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/03.MyBatis%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)



## 目录
- [MyBatis架构及源码剖析](#mybatis架构及源码剖析)
  - [说在前面](#说在前面)
  - [目录](#目录)
  - [一、架构原理](#一架构原理)
    - [1.1 架构设计](#11-架构设计)
    - [1.2 主要组件及其相互关系](#12-主要组件及其相互关系)
    - [1.3 大概的总体流程](#13-大概的总体流程)
  - [二、MyBatis源码剖析](#二mybatis源码剖析)
    - [2.1 传统开发方式源码剖析](#21-传统开发方式源码剖析)
      - [2.1.1 初始化 - 源码剖析](#211-初始化---源码剖析)
      - [2.1.2 执行SQL流程 - 源码剖析](#212-执行sql流程---源码剖析)
      - [2.1.3 Executor - 源码剖析](#213-executor---源码剖析)
      - [2.1.4 StatementHandler - 源码剖析](#214-statementhandler---源码剖析)
      - [2.1.5 总结](#215-总结)
    - [2.2 Mapper代理开发方式源码剖析](#22-mapper代理开发方式源码剖析)
      - [2.2.1 如何加载Mapper接口](#221-如何加载mapper接口)
      - [2.2.2 getMapper() - 源码剖析](#222-getmapper---源码剖析)
      - [2.2.3 invoke() - 源码剖析](#223-invoke---源码剖析)
    - [2.3 二级缓存源码剖析](#23-二级缓存源码剖析)
      - [2.3.1 启用二级缓存 & 发现问题](#231-启用二级缓存--发现问题)
      - [2.3.2 <cache/>标签的解析](#232-cache标签的解析)
      - [2.3.3 二级缓存执行流程 - 源码剖析](#233-二级缓存执行流程---源码剖析)



## 一、架构原理

### 1.1 架构设计

![image-20210321181341758](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210321181341758.png)

**我们把Mybatis的功能架构分为三层**：

* **API接口层**：提供给外部使用的接口 API，开发人员通过这些本地API来操纵数据库。接口层一接收到调用请求就会调用数据处理层来完成具体的数据处理。 

  **MyBatis和数据库的交互有两种方式** ：

* 1. 使用传统的MyBatis提供的API 基于StatementId操作
  2. 使用Mapper接口代理的方式

* **数据处理层**：负责具体的SQL查找、SQL解析、SQL执行和执行结果映射处理等。它主要的目的是根据调用的请求完成一次数据库操作。

* **基础支撑层**：负责最基础的功能支撑，包括连接管理、事务管理、配置加载和缓存处理，这些都是 共 用的东西，将他们抽取出来作为最基础的组件。为上层的数据处理层提供最基础的支撑



### 1.2 主要组件及其相互关系

| **组件**         | **描述**                                                     |
| ---------------- | ------------------------------------------------------------ |
| SqlSession       | 作为MyBatis工作的主要顶层API，表示和数据库交互的会话，完成必要数据看增删改查功能 |
| Executor         | MyBatis执行器，是MyBatis调度的核心，负责SQL语句的生成和查询缓存的维护 |
| StatementHandler | 封装了JDBC Statement操作，负责对JDBC Statement的操作，如设置参数或将Statement结果集转换为List集合 |
| ParameterHandler | 负责对用户传递的参数转换成JDBC Statement所需要的参数         |
| ResultSetHandler | 负责将JDBC返回的ResultSet结果集对象转换成List类型的集合      |
| TypeHandler      | 负责java数据类型和jdbc数据类型之间的映射和转换               |
| MappedStatement  | MappedStatement维护了一条 <select \| update \| delete \| insert>节点的封装 |
| SqlSource        | 负责根据用户传递的parameterObject，动态的生成SQL语句，将信息封装到BoundSql对象中并返回 |
| BoundSql         | 表示动态生成的SQL语句以及相应的参数信息                      |

![image-20210321181724168](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210321181724168.png)



### 1.3 大概的总体流程

* 通过Resuources.getResourceAsStream()方法将核心配置文件读取成字节流

* 通过SqlSessionFactoryBuilder.builder()方法 根据核心配置文件字节流创建出来SqlSessionFactory。在创建SqlSessionFactory的过程中去解析核心配置文件以及核心配置文件中引入的Mapper映射文件到Configuration中的MappedStatements属性中
  * MappedStatements就是一个HashMap，将Mapper映射文件的namespace以及select/update等标签的id作为key，select/update等标签解析对应的MappedStatement对象作为value存入
* 通过SqlSessionFactory.openSession()方法创建SqlSession
* 通过SqlSession对数据库进行CRUD操作
  * SqlSession执行查询操作实际是委托给Executor执行器，Executor执行器会去创建cachKey并去缓存中查询，有则返回，无则去数据库查询
  * 去数据库查询的操作 Executor则委托给StatementHandler去执行
  * 但在StatementHandler去执行之前，Executor会去创建JDBC的prepareStatement预编译对象，并将SQL参数中的一些？占位符委托给StatementHandler进行处理，StatementHandler委托给ParameterHandler，ParameterHandler进行一系列读取判断后委托给TypeHandler。
  * Executor将prepareStatement预编译对象创建完成后则会继续执行SqlSession委托的CURD操作，通过对数据库操作完成后，将查询结果委托给ResultSetHandler进行处理并返回。



## 二、MyBatis源码剖析

MyBatis源码地址在课程资料中：[mybatis-3-master](https://github.com/EayonLee/JavaGod/tree/main/1%E9%98%B6%E6%AE%B5%EF%BC%9A%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01%E6%A8%A1%E5%9D%97%EF%BC%9A%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%8F%8AMyBatis%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/03.MyBatis%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01.%E8%AF%BE%E7%A8%8B%E8%B5%84%E6%96%99)

### 2.1 传统开发方式源码剖析

#### 2.1.1 初始化 - 源码剖析

首先我们来看看传统方式是如何初始化的

```java
/**
 * 传统方式
 */
public void test1() {
    // 1. 读取配置文件，读成字节输入流，注意：现在还没解析
    InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

    //这一行代码正是初始化工作的开始。
    // 2. 解析配置文件，封装Configuration对象   创建DefaultSqlSessionFactory对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
}
```

第一步首先是通过MyBatis提供的工具类``Resources.getResourceAsStream()``方法去将核心配置文件加载成字节流，加载过程也非常简单，就是通过类加载器的``getResourceAsStream()``而已，这里不做过多解释。

第二步才是重点，首先``new``了一个``SqlSessionFactoryBuilder``对象，通过``SqlSessionFactoryBuilder.build()`` **方法将核心配置文件字节流进行解析并创建SqlSessionFactory**

那我们就来具体看看这个``build()``方法到底做了些什么，先看源码

```java
public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
        // 创建 XMLConfigBuilder, XMLConfigBuilder是专门解析sqlMapConfig.xml核心配置文件的类
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
        // 执行 XML 解析
        // 创建 DefaultSqlSessionFactory 对象
        return build(parser.parse());
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
        ErrorContext.instance().reset();
        try {
            inputStream.close();
        } catch (IOException e) {
            // Intentionally ignore. Prefer previous error.
        }
    }
}

/**
 * 创建 DefaultSqlSessionFactory 对象
 *
 * @param config Configuration 对象
 * @return DefaultSqlSessionFactory 对象
 */
public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config); //构建者设计模式
}
```

关于上面第一个``build()``方法中的**XMLConfigBuilder对象是专门用来解析sqlMapConfig.xml核心配置文件的**，通过它的``parse()``方法将sqlMapConfig.xml字节输入流解析成**Configuration核心配置类对象**。

然后第一个``build()``方法返回了``build(parser.parse()) ``，那也就是将解析出来的Configuration核心配置类传递给下面第二个``buil()``方法，将核心配置类通过有参构造创建出一个``DefaultSqlSessionFactory``对象并返回。

那由此我们能看出XMLConfigBuilder对象中的``parse()``方法是**解析配置文件的关键**，我们来看下源码

```java
public Configuration parse() {
    // 若已解析，抛出 BuilderException 异常
    if (parsed) {
        throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    // 标记已解析
    parsed = true;
    ///parser是XPathParser解析器对象，读取节点内数据，<configuration>是MyBatis配置文件中的顶层标签
    // 解析 XML configuration 节点
    parseConfiguration(parser.evalNode("/configuration"));
    return configuration;
}
```

很简单的代码，先判断这个Configuration核心配置有没有解析过，没解析过就去通过``parseConfiguration(parser.evalNode("/configuration"))``方法去具体解析

``parser.evalNode("/configuration") ``**是什么意思呢？**

首先parser就是我们创建XMLConfigBuilder时传递的sqlMapConfig.xml核心配置文件字节流。``parser.evalNode("/configuration")`` 自然就是获取**核心配置文件中的configuration这个根节点**

我们继续看一下``parseConfiguration()`` 对核心配置文件的根节点做了什么

```java
private void parseConfiguration(XNode root) {
    try {
        //issue #117 read properties first
        // 解析 <properties /> 标签
        propertiesElement(root.evalNode("properties"));
        // 解析 <settings /> 标签
        Properties settings = settingsAsProperties(root.evalNode("settings"));
        // 加载自定义的 VFS 实现类
        loadCustomVfs(settings);
        // 解析 <typeAliases /> 标签
        typeAliasesElement(root.evalNode("typeAliases"));
        // 解析 <plugins /> 标签
        pluginElement(root.evalNode("plugins"));
        // 解析 <objectFactory /> 标签
        objectFactoryElement(root.evalNode("objectFactory"));
        // 解析 <objectWrapperFactory /> 标签
        objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
        // 解析 <reflectorFactory /> 标签
        reflectorFactoryElement(root.evalNode("reflectorFactory"));
        // 赋值 <settings /> 到 Configuration 属性
        settingsElement(settings);
        // read it after objectFactory and objectWrapperFactory issue #631
        // 解析 <environments /> 标签
        environmentsElement(root.evalNode("environments"));
        // 解析 <databaseIdProvider /> 标签
        databaseIdProviderElement(root.evalNode("databaseIdProvider"));
        // 解析 <typeHandlers /> 标签
        typeHandlerElement(root.evalNode("typeHandlers"));
        // 重要：解析 <mappers /> 标签
        mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
        throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
}
```

由上述代码不难看出，就是通过根节点(sqlMapConfig.xml中的标签)获取下面的每一个标签并解析标签中的属性，那具体是如何解析的，我们拿 ``propertiesElement(root.evalNode("properties"))`` 来举例

```java
/**
 * 1. 解析 <properties /> 标签，成 Properties 对象。
 * 2. 覆盖 configuration 中的 Properties 对象到上面的结果。
 * 3. 设置结果到 parser 和 configuration 中
 *
 * @param context 节点
 * @throws Exception 解析发生异常
 */
private void propertiesElement(XNode context) throws Exception {
    if (context != null) {
        // 读取子标签们，为 Properties 对象
        Properties defaults = context.getChildrenAsProperties();
        // 读取 resource 和 url 属性
        String resource = context.getStringAttribute("resource");
        String url = context.getStringAttribute("url");
        if (resource != null && url != null) { // resource 和 url 都存在的情况下，抛出 BuilderException 异常
            throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
        }
        // 读取本地 Properties 配置文件到 defaults 中。
        if (resource != null) {
            defaults.putAll(Resources.getResourceAsProperties(resource));
            // 读取远程 Properties 配置文件到 defaults 中。
        } else if (url != null) {
            defaults.putAll(Resources.getUrlAsProperties(url));
        }
        // 覆盖 configuration 中的 Properties 对象到 defaults 中。
        Properties vars = configuration.getVariables();
        if (vars != null) {
            defaults.putAll(vars);
        }
        // 设置 defaults 到 parser 和 configuration 中。
        parser.setVariables(defaults);
        configuration.setVariables(defaults);
    }
}
```

可以看出其实就是将sqlMapConfig.xml核心配置文件中的配置解析出来并**封装给Configuration核心配置类**。

**这时我们会想到核心配置文件中其实还配置了众多Mapper映射文件的地址，那岂不是映射文件也要解析吗？**

那我们来看看 ``mapperElement(root.evalNode("mappers"))``的源码

```java
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
        // 遍历子节点
        for (XNode child : parent.getChildren()) {
            // 如果是 package 标签，则扫描该包
            if ("package".equals(child.getName())) {
                // 获得包名
                String mapperPackage = child.getStringAttribute("name");
                // 添加到 configuration 中
                configuration.addMappers(mapperPackage);
            // 如果是 mapper 标签，
            } else {
                // 获得 resource、url、class 属性
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");
                // 使用相对于类路径的资源引用
                if (resource != null && url == null && mapperClass == null) {
                    ErrorContext.instance().resource(resource);
                    // 获得 resource 的 InputStream 对象
                    InputStream inputStream = Resources.getResourceAsStream(resource);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, 
                                                                                            configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用完全限定资源定位符（URL）
                } else if (resource == null && url != null && mapperClass == null) {
                    ErrorContext.instance().resource(url);
                    // 获得 url 的 InputStream 对象
                    InputStream inputStream = Resources.getUrlAsStream(url);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, 
                                                                                            configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用映射器接口实现类的完全限定类名
                } else if (resource == null && url == null && mapperClass != null) {
                    // 获得 Mapper 接口
                    Class<?> mapperInterface = Resources.classForName(mapperClass);
                    // 添加到 configuration 中
                    configuration.addMapper(mapperInterface);
                } else {
                    throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                }
            }
        }
    }
}
```

由上述代码可以看出，就是去判断核心配置文件中通过何种方式去加载的Mapper映射文件（package标签或mapper标签），然后获取标签中的信息进行解析。

那么由最开始自定义持久层框架的时候我们知道，**Mapper映射文件解析后是放在了Configuration核心配置类中的一个mappedStatements属性中存储的**。那我们直接来说说这个``mappedStatements``是什么。

**介绍一下mappedStatements**：

这个mappedStatements属性其实就是Configuration核心配置类中定义的一个HashMap。看下面代码

```java
/**
 * MappedStatement 映射
 *
 * KEY：`${namespace}.${id}`
 */
protected final Map<String, MappedStatement> mappedStatements = new StrictMap<>("Mapped Statements collection");
```

**MappedStatement与Mapper映射文件中的一个个 select/update/insert/delete 节点相对应**。换句话说当解析一个Mapper映射文件的时候，如果里面有5个select标签，则会在Configuration中的``mappedStatements``属性中存储5个元素，那么存储的key就是mapper映射文件的``namespace + select标签上的id值``，value则为``将这个select标签解析成的MappedStatement对象``。



#### 2.1.2 执行SQL流程 - 源码剖析

**回顾写法**

```java
/**
 * 传统方式
 * @throws IOException
 */
public void test1() throws IOException {
  // 1. 读取配置文件，读成字节输入流，注意：现在还没解析
  InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

  //这一行代码正是初始化工作的开始。
  // 2. 解析配置文件，封装Configuration对象   创建DefaultSqlSessionFactory对象
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

  // 3. 生产了DefaultSqlsession实例对象   设置了事务不自动提交  完成了executor对象的创建
  SqlSession sqlSession = sqlSessionFactory.openSession();

  // 4.(1)根据statementid来从Configuration中map集合中获取到了指定的MappedStatement对象
     //(2)将查询任务委派了executor执行器
  List<Object> objects = sqlSession.selectList("namespace.id");

  // 5.释放资源
  sqlSession.close();
}
```



**介绍一下SqlSession**：

SqlSession是一个接口，它有两个实现类：``DefaultSqlSession (默认)``和`` SqlSessionManager (弃用，不做介绍) ``SqlSession是MyBatis中用于和数据库交互的**顶层类**

SqlSession中的两个最重要的参数，``configuration核心配置类``，``Executor执行器``（Executor是在openSession的时候创建的）

```java
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;
    
    //省略其他......
}
```



**介绍一下Executor**：

Executor也是一个接口，他有三个常用的实现类： 

- BatchExecutor (重用语句并执行批量更新) 
- ReuseExecutor (重用预处理语句 prepared statements) 
- **SimpleExecutor (普通的执行器，默认) **



**创建SqlSession**：

当MyBatis初始化完毕后，我们首先会创建``SqlSession``，才能通过``SqlSession``去操作数据库。

```java
//生产了DefaultSqlsession实例对象   设置了事务不自动提交  完成了executor对象的创建
SqlSession sqlSession = sqlSessionFactory.openSession();
// 4.(1)根据statementid来从Configuration中map集合中获取到了指定的MappedStatement对象
   //(2)将查询任务委派了executor执行器
List<Object> objects = sqlSession.selectList("com.eayon.mapper.UserMapper.getUserByName"); //namespace + "." + id
```

上述代码首先通过``sqlSessionFactory.openSession()``方法**开启SqlSession**，并**创建Exceutor对象**那么看一下``openSession()``的源码

```java
//1. 进入openSession方法
@Override
public SqlSession openSession() {
    //第一个参数：configuration.getDefaultExecutorType()得到的就是默认的SimpleExecutor
    //第二个参数：数据库隔离级别
    //第三个参数：是否自动提交事务 true自动提交事务  false手动提交事务
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
}

 //2. 进入openSessionFromDataSource。
 //ExecutorType 为Executor的类型，TransactionIsolationLevel为事务隔离级别，autoCommit是否开启事务
//openSession的多个重载方法可以指定获得的SeqSession的Executor类型和事务的处理
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
        // 获得 Environment（数据库运行环境） 对象
        final Environment environment = configuration.getEnvironment();
        // 创建 Transaction（事务） 对象
        final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
        tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
        // 创建 Executor 对象
        final Executor executor = configuration.newExecutor(tx, execType);
        // 创建 DefaultSqlSession 对象
        return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
        // 如果发生异常，则关闭 Transaction 对象
        closeTransaction(tx); // may have fetched a connection so lets call close()
        throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```

``openSessionFromDataSource()``方法主要去**创建了Executor对象**以及通过**new DefaultSqlSession(核心配置类, Executor执行器, 是否开启事务)创建了SqlSession并返回**。



**SqlSession中的API**：

```java
List<Object> objects = sqlSession.selectList("com.eayon.mapper.UserMapper.getUserByName");
```

我们通过上述代码可看出，SqlSession中好似封装了对数据库操作的``CRUD``方法，只需要传递 ``namespace.id``组成**statementId**作为key去到Configuration的mappedStatements属性中去获取到value，**value也就是Mapper映射文件中该statementId所对应的MappedStatement对象**。

那么我们就来看一下sqlSession中这个``selectList()``方法

```java
//第一个参数：namespace.id 组成的statementId
//第二个参数：CRUD参数
//第三个参数：分页对象
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
        //namespace.id作为statementId去到Configuration中的MappedStatements这个HashMap中获取到MappedStatement对象
        MappedStatement ms = configuration.getMappedStatement(statement);
        // SqlSession将查询操作委托给Executor执行器
        return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```

上述代码不难看出，具体的查询操作交给了Executor执行器。



#### 2.1.3 Executor - 源码剖析

通过上面的``executor.query()`` 我们不难看出，其实具体的查询操作是通过Executor来执行的。

```java
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    //根据传入的参数动态获得SQL语句，最后返回BoundSql对象
    BoundSql boundSql = ms.getBoundSql(parameter);
    //通过MappedStatement、查询参数、分页参数、SQL 作为参数去创建缓存key
    CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
    // 查询
    return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
}

//query重载方法
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler
                                                            , CacheKey key, BoundSql boundSql) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
    // 已经关闭，则抛出 ExecutorException 异常
    if (closed) {
        throw new ExecutorException("Executor was closed.");
    }
    // 清空本地缓存，如果 queryStack 为零，并且要求清空本地缓存。
    if (queryStack == 0 && ms.isFlushCacheRequired()) {
        clearLocalCache();
    }
    List<E> list;
    try {
        // queryStack + 1
        queryStack++;
        // 从一级缓存中，获取查询结果
        list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
        // 获取到，则进行处理
        if (list != null) {
            handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
        // 缓存获取不到，则从数据库中查询
        } else {
            list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
        }
    } finally {
        // queryStack - 1
        queryStack--;
    }
    if (queryStack == 0) {
        // 执行延迟加载
        for (DeferredLoad deferredLoad : deferredLoads) {
            deferredLoad.load();
        }
        // issue #601
        // 清空 deferredLoads
        deferredLoads.clear();
        // 如果缓存级别是 LocalCacheScope.STATEMENT ，则进行清理
        if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
            // issue #482
            clearLocalCache();
        }
    }
    return list;
}

// 缓存中未查询到数据则从数据库中读取操作
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, 
                                        ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    // 在缓存中，添加占位对象。此处的占位符，和延迟加载有关，可见 `DeferredLoad#canLoad()` 方法
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
        // 从数据库执行读操作
        list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
        // 从缓存中，移除占位对象
        localCache.removeObject(key);
    }
    // 添加到缓存中
    localCache.putObject(key, list);
    // 暂时忽略，存储过程相关
    if (ms.getStatementType() == StatementType.CALLABLE) {
        localOutputParameterCache.putObject(key, parameter);
    }
    return list;
}

public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, 
                                                                                    BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
        Configuration configuration = ms.getConfiguration();
        // 传入参数创建StatementHanlder对象来执行查询
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
        // 创建jdbc中的预编译对象（数据库连接...）
        stmt = prepareStatement(handler, ms.getStatementLog());
        // 执行 StatementHandler  ，进行读操作
        return handler.query(stmt, resultHandler);
    } finally {
        // 关闭 StatementHandler 对象
        closeStatement(stmt);
    }
}
```

Executor中的``query()``方法，先去通过**MappedStatement、查询参数、分页参数、SQL**作为参数去创建**Cachekey**，通过Cachekey查询缓存，查询到了就返回。查询不到就通过``queryFromDatabase()``方法从数据库读取。

而``queryFromDatabase()``又调用了``doQuery()``，在``doQuery()``中首先是创建出了**StatementHandler对象**，并且又创建了JDBC的预编译对象 **prepareStatement**，最后**Executor将对数据库的查询操作继续委派给了StatementHandler对象**。

从上面的代码我们可以看出，**Executor的功能和作用是**：

- 根据传递的参数，完成SQL语句的动态解析，生成BoundSql对象，供StatementHandler使用；
- 为查询创建缓存，以提高性能
- 创建JDBC的Statement连接对象，传递给StatementHandler对象，返回List查询结果。



#### 2.1.4 StatementHandler - 源码剖析

StatementHandler对象主要完成两个工作： 

- 我们使用的SQL语句字符串会包 含若干个``？``占位符，我们其后再对占位符进行设值。所以通过``prepareStatement()``创建prepareStatement预编译对象的同时，通过 ``parameterize(statement)``方法对SQL进行参数设置； 
- StatementHandler 通过``query(prepareStatement,resultHandler)``方法来完成执行PrepareStatement，和将prepareStatement对象返回的resultSet封装成List；

 

我们先看第一个，在Executor将查询操作委派给StatementHandler之前还有一步操作，就是``prepareStatement(handler, ms.getStatementLog())`` 创建JDBC预编译对象**prepareStatement**

```java
// 创建JDBC的预编译对象statement
private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    // 获得 数据库连接Connection 对象
    Connection connection = getConnection(statementLog);
    // 创建 Statement 或 PrepareStatement 对象
    stmt = handler.prepare(connection, transaction.getTimeout());
    // 设置 SQL 上的参数，例如 PrepareStatement 对象上的占位符
    handler.parameterize(stmt);
    return stmt;
}
```

而在``prepareStatement()``方法创建JDBC预编译对象后会通过``handler.parameterize(stmt)``对PrepareStatement进行SQL参数设置

```java
public void parameterize(Statement statement) throws SQLException {
    //使用ParameterHandler对象来完成对Statement的设值
    parameterHandler.setParameters((PreparedStatement) statement);
}

public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    // 遍历 ParameterMapping 数组     boundSql中存储带有？占位符的SQL语句，以及#{}中的参数名称
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
        for (int i = 0; i < parameterMappings.size(); i++) {
            // 获得 ParameterMapping 对象
            ParameterMapping parameterMapping = parameterMappings.get(i);
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                // 获得值
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                // 获得 typeHandler
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = configuration.getJdbcTypeForNull();
                }
                // 设置 ? 占位符的参数
                try {
                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                } catch (TypeException | SQLException e) {
                    throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                }
            }
        }
    }
}
```

通过上述代码可看出，**StatementHandler**通过``parameterize(prepareStatement)``方法委托给了 **ParameterHandler**的``setParameters(prepareStatement)``方法去设置prepareStatement

ParameterHandler的``setParameters(Statement)``方法负责根据我们输入的参数，对statement对象的 ? 占位符处进行赋值。

但是，ParameterHandler的``setParameters()``方法中，还创建了**TypeHandler**，并通过``typeHandler.setParameter(ps, i + 1, value, jdbcType)`` 设置 ``? ``占位符的参数



**回头继续看Executor中的doQuery方法**：

Executor中的``doQuery()``方法将查询操作委派给了statementHandler接口的实现类PreparedStatementHandler的``query()``方法

```java
public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    // 执行查询
    ps.execute();
    // 处理返回结果
    return resultSetHandler.handleResultSets(ps);
}
```

对于返回结果集的处理是交由**ResultSetHandler**的``handleResultSets()``方法进行处理的

```java
public List<Object> handleResultSets(Statement stmt) throws SQLException {
    this.stmt = stmt;
    ErrorContext.instance().activity("handling results").object(mappedStatement.getId());

    // 多 ResultSet 的结果集合，每个 ResultSet 对应一个 Object 对象。而实际上，每个 Object 是 List<Object> 对象。
    // 在不考虑存储过程的多 ResultSet 的情况，普通的查询，实际就一个 ResultSet ，也就是说，multipleResults 最多就一个元素。
    final List<Object> multipleResults = new ArrayList<>();

    int resultSetCount = 0;
    // 获得首个 ResultSet 对象，并封装成 ResultSetWrapper 对象
    ResultSetWrapper rsw = getFirstResultSet(stmt);

    // 获得 ResultMap 数组
    // 在不考虑存储过程的多 ResultSet 的情况，普通的查询，实际就一个 ResultSet ，也就是说，resultMaps 就一个元素。
    List<ResultMap> resultMaps = mappedStatement.getResultMaps();
    int resultMapCount = resultMaps.size();
    validateResultMapsCount(rsw, resultMapCount); // 校验
    while (rsw != null && resultMapCount > resultSetCount) {
        // 获得 ResultMap 对象
        ResultMap resultMap = resultMaps.get(resultSetCount);
        // 处理 ResultSet ，将结果添加到 multipleResults 中
        handleResultSet(rsw, resultMap, multipleResults, null);
        // 获得下一个 ResultSet 对象，并封装成 ResultSetWrapper 对象
        rsw = getNextResultSet(stmt);
        // 清理
        cleanUpAfterHandlingResultSet();
        // resultSetCount ++
        resultSetCount++;
    }

    // 因为 `mappedStatement.resultSets` 只在存储过程中使用，本系列暂时不考虑，忽略即可
    String[] resultSets = mappedStatement.getResultSets();
    if (resultSets != null) {
        while (rsw != null && resultSetCount < resultSets.length) {
            ResultMapping parentMapping = nextResultMaps.get(resultSets[resultSetCount]);
            if (parentMapping != null) {
                String nestedResultMapId = parentMapping.getNestedResultMapId();
                ResultMap resultMap = configuration.getResultMap(nestedResultMapId);
                handleResultSet(rsw, resultMap, null, parentMapping);
            }
            rsw = getNextResultSet(stmt);
            cleanUpAfterHandlingResultSet();
            resultSetCount++;
        }
    }

    // 如果是 multipleResults 单元素，则取首元素返回
    return collapseSingleResultList(multipleResults);
}
```



#### 2.1.5 总结

- 通过``Resuources.getResourceAsStream()``方法将核心配置文件读取成字节流

- 通过``SqlSessionFactoryBuilder.builder()``方法 根据核心配置文件字节流创建出来SqlSessionFactory。在创建SqlSessionFactory的过程中去解析核心配置文件以及核心配置文件中引入的Mapper映射文件到Configuration中的MappedStatements属性中

- - MappedStatements属性就是一个HashMap，将Mapper映射文件的``namespace``以及``select/update``等标签的id作为key，``select/update``等标签解析对应的MappedStatement对象作为value存入

- 通过``SqlSessionFactory.openSession()``方法创建SqlSession

- 通过SqlSession对数据库进行CRUD操作

- - SqlSession执行查询操作实际是委托给Executor执行器，Executor执行器会去创建cacheKey并去缓存中查询，有则返回，无则去数据库查询
  - 去数据库查询的操作 Executor则委托给StatementHandler去执行
  - 但在StatementHandler去执行之前，Executor会去创建JDBC的prepareStatement预编译对象，并将SQL参数中的一些？占位符委托给StatementHandler进行处理，StatementHandler委托给ParameterHandler，ParameterHandler进行一系列读取判断后委托给TypeHandler。
  - Executor将prepareStatement预编译对象创建完成后则会继续执行SqlSession委托的CURD操作，通过对数据库操作完成后，将查询结果委托给ResultSetHandler进行处理并返回。



### 2.2 Mapper代理开发方式源码剖析

**回顾下写法**：

```java
/**
 * mapper代理方式
 */
public void test2() throws IOException {
  InputStream inputStream = Resources.getResourceAsStream("sqlMapConfig.xml");
  SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
  SqlSession sqlSession = factory.openSession();

  //使用JDK动态代理对mapper接口产生代理对象
  IUserMapper mapper = sqlSession.getMapper(IUserMapper.class);

  //代理对象调用接口中的任意方法，执行的都是动态代理中的invoke方法
  List<Object> allUser = mapper.findAllUser();
}
```



#### 2.2.1 如何加载Mapper接口

分析源码之前我们先来想一个问题，通常Mapper接口我们都没有定义他的实现类并重写方法，为什么还可以用呢？答案就是JDK动态代理。那么这个Mapper接口的动态代理是什么时候生成的呢？

首先我们要知道Mapper接口是如何被扫描加载的。首先我们都会在核心配置文件中引入Mapper吧，如下

```java
<mappers>
    <package name="com.eayon.mapper"/>
</mappers>
```

那么加载mappers标签的的时机也就是 ``SqlSessionFactoryBuilder.builder()`` 创建sqlSessionFactory的时候去解析的，

在``builder()``方法中会去解析核心配置文件，解析到核心配置文件中的mappers标签时会进入如下代码

```java
//解析核心配置文件中的Mapper标签
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
        // 遍历子节点
        for (XNode child : parent.getChildren()) {
            // 如果是 package 标签，则扫描该包
            if ("package".equals(child.getName())) {
                // 获得包名
                String mapperPackage = child.getStringAttribute("name");
                // 添加到 configuration 中
                configuration.addMappers(mapperPackage);
            // 如果是 mapper 标签，
            } else {
                // 获得 resource、url、class 属性
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");
                // 使用相对于类路径的资源引用
                if (resource != null && url == null && mapperClass == null) {
                    ErrorContext.instance().resource(resource);
                    // 获得 resource 的 InputStream 对象
                    InputStream inputStream = Resources.getResourceAsStream(resource);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用完全限定资源定位符（URL）
                } else if (resource == null && url != null && mapperClass == null) {
                    ErrorContext.instance().resource(url);
                    // 获得 url 的 InputStream 对象
                    InputStream inputStream = Resources.getUrlAsStream(url);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用映射器接口实现类的完全限定类名
                } else if (resource == null && url == null && mapperClass != null) {
                    // 获得 Mapper 接口
                    Class<?> mapperInterface = Resources.classForName(mapperClass);
                    // 添加到 configuration 中
                    configuration.addMapper(mapperInterface);
                } else {
                    throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                }
            }
        }
    }
}
```

由上述代码我们可看出，他首先会根据你在配置文件中引入Mapper的方式去分别加载，比如通过package标签或者mapper标签。

比如我们通过package标签引入，那么首先获取到我们name属性中配置的Mapper所在包名。

然后调用``configuration.addMappers(mapperPackage)``方法。

```java
//Configuration类中的addMapper方法
public void addMappers(String packageName) {
    // 扫描该包下所有的 Mapper 接口，并添加到 mapperRegistry 中
    mapperRegistry.addMappers(packageName);
}

//MapperRegistry类中的addMapper方法
public void addMappers(String packageName) {
    addMappers(packageName, Object.class);
}

//MapperRegistry类中的addMapper重载方法
public void addMappers(String packageName, Class<?> superType) {
    //创建解析器工具类
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // 扫描指定包下的指定类 也就是扫描com.eayon.mapper包下的所有Mapper接口
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    //并获取这些Mapper的Class集合
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    // 遍历，添加到 knownMappers 中
    for (Class<?> mapperClass : mapperSet) {
        //调用下面的addMapper
        addMapper(mapperClass);
    }
}

//MapperRegistry类中的addMapper重载方法
public <T> void addMapper(Class<T> type) {
    // 判断，必须是接口。
    if (type.isInterface()) {
        // 已经添加过，则抛出 BindingException 异常
        if (hasMapper(type)) {
            throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
        }
        boolean loadCompleted = false;
        try {
            // 添加到 knownMappers 中
            knownMappers.put(type, new MapperProxyFactory<>(type));
            // 解析 Mapper 的注解配置
            MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
            parser.parse();
            // 标记加载完成
            loadCompleted = true;
        } finally {
            // 若加载未完成，从 knownMappers 中移除
            if (!loadCompleted) {
                knownMappers.remove(type);
            }
        }
    }
}
```

我们发现Configuration将加载Mapper接口委托给了**MapperRegistry**类来处理

MapperRegistry类的``addMappers()``方法通过ResolverUtil工具类扫描该包路径下所有的Mapper接口，并将每一个加载到的Mapper接口创建成MapperProxyFactory 这个Mapper代理工厂，并将该代理工厂添加到knownMappers中。

这个knownMappers其实就是MapperRegistry类中维护的一个HashMap 专门用来存放加载出来的MapperProxyFactory 。

**将Mapper接口的Class作为Key，通过Mapper接口Class创建的MapperProxyFactory作为Value存入**。

```java
//存放MapperProxyFactory
private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
```



#### 2.2.2 getMapper() - 源码剖析

进入 ``sqlSession.getMapper(UserMapper.class)``中

```java
//DefaultSqlSession类中的getMapper方法
public <T> T getMapper(Class<T> type) {
    return configuration.getMapper(type, this);
}

//Configuration类中的getMapper方法
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    return mapperRegistry.getMapper(type, sqlSession);
}

//MapperRegistry类中的getMapper方法
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    // 通过Mapper接口的Class从knownMappers中获得 MapperProxyFactory 对象
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    // 不存在，则抛出 BindingException 异常
    if (mapperProxyFactory == null) {
        throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    // 存在则通过动态代理工厂生成实例。
    try {
        return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
        throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
}

//MapperProxyFactory类中的newInstance方法
 public T newInstance(SqlSession sqlSession) {
     // 创建了JDK动态代理的invocationHandler接口的实现类mapperProxy
     final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
     // 调用了重载方法
     return newInstance(mapperProxy);
 }
 
 //重载方法
 protected T newInstance(MapperProxy<T> mapperProxy) {
     //JDK动态代理
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
}
```



#### 2.2.3 invoke() - 源码剖析

现在Mapper接口如何加载，加载后如何存储，存储后如何获取我们都已经翻阅了源码

那么当我们获取到Mapper接口的代理对象之后，就会进行CRUD方法的操作，我们知道调用代理对象的任何方法都会进入``invoke``方法，也就是执行MapperProxy中的``invoke``方法

```java
public class MapperProxy<T> implements InvocationHandler, Serializable {
    
    //SqlSession 对象
    private final SqlSession sqlSession;
    
    //Mapper 接口
    private final Class<T> mapperInterface;

    private final Map<Method, MapperMethod> methodCache;

    // 构造，传入了SqlSession，说明每个session中的代理对象的不同的！
    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // 如果是 Object 定义的方法，直接调用
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);

            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
        // 获得 MapperMethod 对象
        final MapperMethod mapperMethod = cachedMapperMethod(method);
        // 重点在这：MapperMethod最终调用了执行的方法
        return mapperMethod.execute(sqlSession, args);
    }
}
```

上述的``invoke``方法中最重要的其实是：``mapperMethod.execute(sqlSession, args)``，它才是最终调用执行的方法，进入``execute``方法

```java
//MapperMethod类中的execute方法
public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    //判断mapper中的方法类型，最终调用的还是SqlSession中的方法
    switch (command.getType()) {
        case INSERT: { //增加操作
            // 转换参数
            Object param = method.convertArgsToSqlCommandParam(args);
            // 执行 INSERT 操作
            // 转换 rowCount
            result = rowCountResult(sqlSession.insert(command.getName(), param));
            break;
        }
        case UPDATE: { //修改操作
            // 转换参数
            Object param = method.convertArgsToSqlCommandParam(args);
            // 转换 rowCount
            result = rowCountResult(sqlSession.update(command.getName(), param));
            break;
        }
        case DELETE: { //删除操作
            // 转换参数
            Object param = method.convertArgsToSqlCommandParam(args);
            // 转换 rowCount
            result = rowCountResult(sqlSession.delete(command.getName(), param));
            break;
        }
        case SELECT: //查询操作
            // 无返回，并且有 ResultHandler 方法参数，则将查询的结果，提交给 ResultHandler 进行处理
            if (method.returnsVoid() && method.hasResultHandler()) {
                executeWithResultHandler(sqlSession, args);
                result = null;
            // 执行查询，返回列表
            } else if (method.returnsMany()) {
                result = executeForMany(sqlSession, args);
            // 执行查询，返回 Map
            } else if (method.returnsMap()) {
                result = executeForMap(sqlSession, args);
            // 执行查询，返回 Cursor
            } else if (method.returnsCursor()) {
                result = executeForCursor(sqlSession, args);
            // 执行查询，返回单个对象
            } else {
                // 转换参数
                Object param = method.convertArgsToSqlCommandParam(args);
                // 查询单条  参数分别为statementId以及操作参数
                result = sqlSession.selectOne(command.getName(), param);
                if (method.returnsOptional() &&
                        (result == null || !method.getReturnType().equals(result.getClass()))) {
                    result = Optional.ofNullable(result);
                }
            }
            break;
        case FLUSH:
            result = sqlSession.flushStatements();
            break;
        default:
            throw new BindingException("Unknown execution method for: " + command.getName());
    }
    // 返回结果为 null ，并且返回类型为基本类型，则抛出 BindingException 异常
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
        throw new BindingException("Mapper method '" + command.getName()
                + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    // 返回结果
    return result;
}
```

我们不难发现在``mapperMethod.execute()``方法中，它是先去判断mapper中的方法类型，根据不同的方法类型去分别调用sqlSession中的CRUD方法，那么最后这操作还是通过sqlSession来完成的。



### 2.3 二级缓存源码剖析

**注意**：二级缓存是构建在一级缓存之上的，比如在收到查询请求的时候，首先MyBatis默认开启了一级缓存，但是我们又开启了二级缓存。这时MyBatis首先会查询二级缓存，若二级缓存未命中，再去查询一级缓存，一级缓存没有，再查询数据库。

与一级缓存不同，二级缓存和具体的namespace绑定，一个Mapper中有一个Cache对象，相同Mapper中的MappedStatement共用一个Cache，一级缓存则是和SqlSession绑定。

#### 2.3.1 启用二级缓存 & 发现问题

**在sqlMapConfig.xml中开启全局二级缓存配置**：

```xml
<!--开启二级缓存-->
<settings>
    <setting name = "cacheEnabled" value="true"/>
</settings>
```

**在需要使用二级缓存的Mapper映射配置文件中配置<cache>标签**：

```xml
<cache/>
```

**在具体CRUD标签上配置 useCache = true:**

```xml
<!--useCache="false" 禁用二级缓存-->
<select id="findById" resultType="com.eayon.pojo.User" useCache="true">
    select * from user where id = #{id}
</select>
```

**测试**：

```java
public void secondLevelCache() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);

    //构建2个session
    SqlSession sqlSession1 = sqlSessionFactory.openSession(true);
    SqlSession sqlSession2 = sqlSessionFactory.openSession(true);

    //通过2个不同的session获取2个UserMapper对象
    UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
    UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

    //使用不同的session（跨session）去进行查询
    User user1 = mapper1.findById(1);
    System.out.println(user1);
    
    User user2 = mapper2.findById(1);
    System.out.println(user2);
}
```

**发现问题**：

![image-20210322151455173](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210322151455173.png)

第一次未命中二级缓存走了数据库我们可以理解，可是第一次从数据库查询出来的结果应该存入二级缓存了，那为什么第二次还是未命中呢？

其实是因为我们在第一次查询结束后没有将``sqlSession.commit()``进行事务提交，如下

```java
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
    User user1 = mapper1.findById(1);
    System.out.println(user1);

    //事务提交
    sqlSession1.commit();

    User user2 = mapper2.findById(1);
    System.out.println(user2);
}
```

**再次测试**：

![image-20210322151639873](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210322151639873.png)

是不是觉得一头雾水？为什么将``sqlSession.commit()``一下就可以了呢？

**那让我们带着问题来看MyBatis源码**：

​	问题1：在Mapper映射文件加入的``<cache/>``标签，它是如何解析的？

​	问题2：为什么同时开启一级和二级缓存的时候，他会按照 二级缓存 -> 一级缓存 -> 数据库 的顺序去查询？

​	问题3：为什么``sqlSession.commit()``就可以将结果存储到二级缓存？



#### 2.3.2 <cache/>标签的解析

那么我们先来分析第一个问题，为什么需要在Mapper映射文件加入``<cache/>``标签，它是如何解析并创建Cache对象的？

首先``<cache/>``标签我们是配置在Mapper映射配置文件中的，那么我们想要了解``<cahce/>``标签是如何解析的，只需要找到Mapper映射文件解析的地方就可以了。

根据之前MyBatis源码剖析，Mapper映射文件的解析是在``SqlSessionFactoryBuilder.builder()``时候通过这个``builder()``方法去解析的。``builder()``方法中通过``XMLConfigBuilder.parse()``方法来解析核心配置文件。

在解析到核心配置文件中的Mapper标签的时候通过 ``mapperElement(root.evalNode("mappers"))`` 解析Mapper映射文件

```java
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
        // 遍历子节点
        for (XNode child : parent.getChildren()) {
            // 如果是 package 标签，则扫描该包
            if ("package".equals(child.getName())) {
                // 获得包名
                String mapperPackage = child.getStringAttribute("name");
                // 添加到 configuration 中
                configuration.addMappers(mapperPackage);
            // 如果是 mapper 标签，
            } else {
                // 获得 resource、url、class 属性
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");
                // 使用相对于类路径的资源引用
                if (resource != null && url == null && mapperClass == null) {
                    ErrorContext.instance().resource(resource);
                    // 获得 resource 的 InputStream 对象
                    InputStream inputStream = Resources.getResourceAsStream(resource);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用完全限定资源定位符（URL）
                } else if (resource == null && url != null && mapperClass == null) {
                    ErrorContext.instance().resource(url);
                    // 获得 url 的 InputStream 对象
                    InputStream inputStream = Resources.getUrlAsStream(url);
                    // 创建 XMLMapperBuilder 对象
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                    // 执行解析
                    mapperParser.parse();
                // 使用映射器接口实现类的完全限定类名
                } else if (resource == null && url == null && mapperClass != null) {
                    // 获得 Mapper 接口
                    Class<?> mapperInterface = Resources.classForName(mapperClass);
                    // 添加到 configuration 中
                    configuration.addMapper(mapperInterface);
                } else {
                    throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                }
            }
        }
    }
}
```

上述代码中我们可以发现，其实它是通过``XMLMapperBuilder对象的parse()``方法来具体进行解析Mapper映射文件的，我们来看源码

```java
public void parse() {
    // 判断当前 Mapper 是否已经加载过
    if (!configuration.isResourceLoaded(resource)) {
        // 解析 `<mapper />` 根节点
        configurationElement(parser.evalNode("/mapper"));
        // 标记该 Mapper 已经加载过
        configuration.addLoadedResource(resource);
        // 绑定 Mapper
        bindMapperForNamespace();
    }

    // 解析待定的 <resultMap /> 节点
    parsePendingResultMaps();
    // 解析待定的 <cache-ref /> 节点
    parsePendingCacheRefs();
    // 解析待定的 SQL 语句的节点
    parsePendingStatements();
}
```

通过查看Mapper映射文件我们发现，``<cache/>``标签我们是配置在``<mapper>``根标签下的。那么我们直接看解析``<mapper>``根标签的源码  ``configurationElement(parser.evalNode("/mapper"))`` 即可

```java
// 解析 `<mapper />` 节点
private void configurationElement(XNode context) {
    try {
        // 获得 namespace 属性
        String namespace = context.getStringAttribute("namespace");
        if (namespace == null || namespace.equals("")) {
            throw new BuilderException("Mapper's namespace cannot be empty");
        }
        // 设置 namespace 属性
        builderAssistant.setCurrentNamespace(namespace);
        // 解析 <cache-ref /> 节点
        cacheRefElement(context.evalNode("cache-ref"));
        // 解析 <cache /> 节点
        cacheElement(context.evalNode("cache"));
        // 已废弃！老式风格的参数映射。内联参数是首选,这个元素可能在将来被移除，这里不会记录。
        parameterMapElement(context.evalNodes("/mapper/parameterMap"));
        // 解析 <resultMap /> 节点们
        resultMapElements(context.evalNodes("/mapper/resultMap"));
        // 解析 <sql /> 节点们
        sqlElement(context.evalNodes("/mapper/sql"));
        // 解析 <select /> <insert /> <update /> <delete /> 节点们
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    } catch (Exception e) {
        throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
    }
}
```

从上述代码我们就发现了解析``<cache>``标签的源码  ``cacheElement(context.evalNode("cache"))``，如下

```java
// 解析 <cache /> 标签
private void cacheElement(XNode context) throws Exception {
    if (context != null) {
        // 获得负责存储的 Cache 实现类，如redisCache....
        String type = context.getStringAttribute("type", "PERPETUAL");
        Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
        // 获得负责过期的 Cache 实现类
        String eviction = context.getStringAttribute("eviction", "LRU");
        Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
        // 获得 flushInterval、size、readWrite、blocking 属性
        Long flushInterval = context.getLongAttribute("flushInterval");
        Integer size = context.getIntAttribute("size");
        boolean readWrite = !context.getBooleanAttribute("readOnly", false);
        boolean blocking = context.getBooleanAttribute("blocking", false);
        // 获得 Properties 属性
        Properties props = context.getChildrenAsProperties();
        // 创建 Cache 对象
        builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
    }
}
```

上述代码的前面都是去解析``<cache/>``标签中的属性而已，最终是通过最后一行代码 ``builderAssistant.useNewCache() ``方法去创建Cache对象。

```java
/**
 * 创建 Cache 对象
 *
 * @param typeClass 负责存储的 Cache 实现类
 * @param evictionClass 负责过期的 Cache 实现类
 * @param flushInterval 清空缓存的频率。0 代表不清空
 * @param size 缓存容器大小
 * @param readWrite 是否序列化
 * @param blocking 是否阻塞
 * @param props Properties 对象
 * @return Cache 对象
 */
public Cache useNewCache(Class<? extends Cache> typeClass,
                         Class<? extends Cache> evictionClass,
                         Long flushInterval,
                         Integer size,
                         boolean readWrite,
                         boolean blocking,
                         Properties props) {
    // 通过上一步解析<cache/>标签中的属性 来构建一个Cache对象
    Cache cache = new CacheBuilder(currentNamespace)
            .implementation(valueOrDefault(typeClass, PerpetualCache.class))
            .addDecorator(valueOrDefault(evictionClass, LruCache.class))
            .clearInterval(flushInterval)
            .size(size)
            .readWrite(readWrite)
            .blocking(blocking)
            .properties(props)
            .build();
    // 添加到 configuration 的 caches 中
    configuration.addCache(cache);
    // 赋值给 currentCache
    currentCache = cache;
    return cache;
}
```

如上代码，将创建出来的Cache对象保存到核心配置文件的cache属性中，这个cache属性就是一个HashMap。并在本类 MapperBuilderAssistant 的成员变量 currentCache保存一份，currentCache属性类型是Cache对象



**那么我们再来想一个问题，为什么同一个Mapper下的MappedStatement共用一个Cache**

明明每一个Mapper中的``<cache/>``标签解析出来的Cache对象是存到了Configuration类中的cache属性，和MapperBuilderAssistant 类中的成员变量currentCache。为什么同一个Mapper下的MappedStatement共用一个Cache呢？MappedStatement中是否也会保存一个Cache呢？

想要解决这个问题，我们就要找到在哪里解析Mapper文件中的select、update...``标签并生成成一个MappedStatement对象存放在Configuration核心配置类中的mappedStatements属性中。

所以再来看看解析Mapper映射文件中mapper标签的地方

```java
// 解析 `<mapper />` 节点
private void configurationElement(XNode context) {
    try {
        // 获得 namespace 属性
        String namespace = context.getStringAttribute("namespace");
        if (namespace == null || namespace.equals("")) {
            throw new BuilderException("Mapper's namespace cannot be empty");
        }
        // 设置 namespace 属性
        builderAssistant.setCurrentNamespace(namespace);
        // 解析 <cache-ref /> 节点
        cacheRefElement(context.evalNode("cache-ref"));
        // 解析 <cache /> 节点
        cacheElement(context.evalNode("cache"));
        // 已废弃！老式风格的参数映射。内联参数是首选,这个元素可能在将来被移除，这里不会记录。
        parameterMapElement(context.evalNodes("/mapper/parameterMap"));
        // 解析 <resultMap /> 节点们
        resultMapElements(context.evalNodes("/mapper/resultMap"));
        // 解析 <sql /> 节点们
        sqlElement(context.evalNodes("/mapper/sql"));
        // 解析 <select /> <insert /> <update /> <delete /> 节点们
        buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    } catch (Exception e) {
        throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
    }
}
```

我们发现最后一句代码 ``buildStatementFromContext(context.evalNodes("select|insert|update|delete"))`` 解析``select .... ``标签

```java
// 解析 <select /> <insert /> <update /> <delete /> 节点们
private void buildStatementFromContext(List<XNode> list) {
    if (configuration.getDatabaseId() != null) {
        buildStatementFromContext(list, configuration.getDatabaseId());
    }
    buildStatementFromContext(list, null);
    // 上面两块代码，可以简写成 buildStatementFromContext(list, configuration.getDatabaseId());
}

private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
    //遍历 <select /> <insert /> <update /> <delete /> 节点们
    for (XNode context : list) {
        // 创建 XMLStatementBuilder 对象，执行解析
        final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
        try {
            statementParser.parseStatementNode();
        } catch (IncompleteElementException e) {
            // 解析失败，添加到 configuration 中
            configuration.addIncompleteStatement(statementParser);
        }
    }
}

/**
 * 执行解析
 */
public void parseStatementNode() {
    
    //省略一大推解析select、update等标签中的其他属性
    
    // 创建 MappedStatement 对象
    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
            fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
            resultSetTypeEnum, flushCache, useCache, resultOrdered,
            keyGenerator, keyProperty, keyColumn, databaseId, langDriver, resultSets);
}

// 构建 MappedStatement 对象
public MappedStatement addMappedStatement(
       
    //省略一大堆解析代码

    // 创建 MappedStatement.Builder 对象
    MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
            .resource(resource)
            .fetchSize(fetchSize)
            .timeout(timeout)
            .statementType(statementType)
            .keyGenerator(keyGenerator)
            .keyProperty(keyProperty)
            .keyColumn(keyColumn)
            .databaseId(databaseId)
            .lang(lang)
            .resultOrdered(resultOrdered)
            .resultSets(resultSets)
            .resultMaps(getStatementResultMaps(resultMap, resultType, id)) // 获得 ResultMap 集合
            .resultSetType(resultSetType)
            .flushCacheRequired(valueOrDefault(flushCache, !isSelect))
            .useCache(valueOrDefault(useCache, isSelect))
            .cache(currentCache);//在创建每一个select等标签对应的MappedStatement对象时会将他们所在Mapper的Cache对象进行赋值绑定

    // 创建 MappedStatement 对象
    MappedStatement statement = statementBuilder.build();
    // 添加到 configuration 中
    configuration.addMappedStatement(statement);
    return statement;
}
```

通过上述代码我们不难看出。每一个Mapper映射文件下的``select``等标签都会被解析成一个个的MappedStatement对象存储在Configuration中。但是在构建每一个MappedStatement对象的时候会将该MappedStatement所属Mapper的``currentCache``也就是Cache对象进行赋值绑定。所以我们说**同一个Mapper下的MappedStatement时共用同一个二级缓存的，二级缓存也是Mapper级别的**。



#### 2.3.3 二级缓存执行流程 - 源码剖析

很简单，我们还是用如下代码举例：

```java
public void test1() throws IOException {
  InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
  SqlSession sqlSession = sqlSessionFactory.openSession();

  List<Object> objects = sqlSession.selectList("namespace.id");
  sqlSession.close();
}
```

那首先我们肯定是去看看``selectList()``方法做了什么吧

```java
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
        // 获得 MappedStatement 对象
        MappedStatement ms = configuration.getMappedStatement(statement);
        // 执行查询
        return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```

通过上述代码我们来到了DefaultSqlSession中的``selectList``方法，不难看出啊 ， 委托给了**Executor执行器**。

但是通过前几章的学习我们跟踪``executor.query()``方法的时候都是进入的 **BaseExecutor**实现类 ，而现在我们要进入**CachingExecutor**实现类

![image-20210322153308416](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210322153308416.png)

**欸？**为什么呢？原来我们跟踪源码都是进入到Executor的啊？

这是因为，如果我们**在核心配置文件中配置了如下代码开启二级缓存，那么MyBatis就不会默认进入BaseExecutor实现类的query方法，而是选择CachingExecutor**

```xml
<!--开启二级缓存-->
<settings>
    <setting name = "cacheEnabled" value="true"/>
</settings>
```

进入**CachingExecutor**中的 ``query()``

```java
//CachingExecutor中的query()
public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    // 获得 BoundSql 对象
    BoundSql boundSql = ms.getBoundSql(parameterObject);
    // 创建 CacheKey 对象
    CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
    // 查询
    return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}

public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql)
        throws SQLException {
    //从MappedStatement中获取Cache对象
    //注意：这里的Cache是前面解析Mapper中<cache/>标签时创建的并保存在MappedStatement中和Configuration中
    //所以说每一个MappedStatement都有一个Cache对象，同一个Mapper下的MappedStatement中的Cache对象是相同的
    Cache cache = ms.getCache();
    
    //如果没有cache对象  其实就是Mapper映射文件中没有配置<cache/>标签
    if (cache != null) {
        // 判断是否需要刷新缓存，其实就是判断我们的select、update这些标签上面有没有配置 flushCache = "true" ，当然 默认为false不刷新 ，否在每次到这一步都会清空二级缓存
        flushCacheIfRequired(ms);
        //判断select、update这些标签上面的userCache = "true" 是否为true 也就是该MappedStatement的操作是否使用缓存
        if (ms.isUseCache() && resultHandler == null) {
            // 暂时忽略，存储过程相关
            ensureNoOutParams(ms, boundSql);
            // 从二级缓存中，获取结果
            List<E> list = (List<E>) tcm.getObject(cache, key);
            if (list == null) {
                // delegate其实就是BaseExecutor
                // 如果在本类CachingExecutor中的二级缓存没有查询到，就走BaseExecutor中的query方法，去查一级缓存，一级缓存还没有就走数据库
                //那我们也就可以理解CachingExecutor好像就是对二级缓存的实现
                list = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
                // 缓存结果到二级缓存中
                tcm.putObject(cache, key, list); // issue #578 and #116
            }
            // 如果存在，则直接返回结果
            return list;
        }
    }
    // 没有在Mapper映射文件中配置<cache/>标签  则直接从数据库中查询
    return delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

我们通过上述代码可发现，在**CachingExecutor**中其实就是对二级缓存的实现。首先他会通过 ``tcm.getObject(cache, key) ``从二级缓存中获取，如果二级缓存中没有则会通过 ``delegate.query()`` 走BaseExecutor的query方法，此时的delegate变量就代表着BaseExecutor

那我们知道，**BaseExecutor的qeury方法主要是先去查询一级缓存，一级缓存有则返回，没有则查询数据库**，然后将结果保存到一级缓存，并返回结果。

此时的**CachingExecutor接收到BaseExecutor返回的结果后会通过``tcm.putObject()``方法保存到二级缓存**。

*但是他真的保存到二级缓存了吗？*     如果真的保存到了二级缓存，就不会出现前面二级缓存未命中的情况了。

而且这个**tcm**变量到底是什么，为什么不直接保存到**Cache**对象当中呢，而保存在了**tcm**当中

所以此时的重点就是``tcm.putObject()``，但是分析tcm是个什么东西之前，我们先来知道一件事情。

注意：

​	二级缓存是从MappedStatement中获取的对吧？那么MappedStatement是从哪来的呢？是从Configuration中的全局变量MappedStatements属性中获取到的。那么也就说明可以被多个CachingExecutor获取到，这样就会出现**线程安全**的问题。除此之外，若不加以控制，多个事务共用同一个缓存实例，会导致脏读的问题。至于脏读的问题，就需要借助其他类来处理，也就是这个tcm，下面来分析一下

**TransactionalCacheManager（这个就是上面提到的tcm）**

```java
/**
 * {@link TransactionalCache} 管理器
 *
 * @author Clinton Begin
 */
public class TransactionalCacheManager {

     //Cache 和 TransactionalCache 的映射，使用Cache会有脏读等问题，那么就通过映射获取到对应的TransactionalCache 对它进行操作就好了
    private final Map<Cache, TransactionalCache> transactionalCaches = new HashMap<>();

    /**
     * 获得缓存中，指定 Cache + K 的值。
     *
     * @param cache Cache 对象
     * @param key 键
     * @return 值
     */
    public Object getObject(Cache cache, CacheKey key) {
        // 首先，获得 Cache 对应的 TransactionalCache 对象
        // 然后从 TransactionalCache 对象中，获得 key 对应的值
        return getTransactionalCache(cache).getObject(key);
    }

    /**
     * 添加 Cache + KV ，到缓存中
     *
     * @param cache Cache 对象
     * @param key 键
     * @param value 值
     */
    public void putObject(Cache cache, CacheKey key, Object value) {
        // 首先，获得 Cache 对应的 TransactionalCache 对象
        // 然后，添加 KV 到 TransactionalCache 对象中
        getTransactionalCache(cache).putObject(key, value);
    }

    /**
     * 获得 Cache 对应的 TransactionalCache 对象
     *
     * @param cache Cache 对象
     * @return TransactionalCache 对象
     */
    private TransactionalCache getTransactionalCache(Cache cache) {
        return transactionalCaches.computeIfAbsent(cache, TransactionalCache::new);
    }
}
```

CachingExecutor将从BaseExecutor查询出来的结果通过``tcm.putObject()``到TransactionalCacheManager类中的定义的transactionalCaches属性中的TransactionalCache对象

举例：

​	1、如上面类中的putObject方法，首先通过``getTransactionalCache()``方法获取到你这个MappedStatement中的Cache在transactionalCaches中对应的TransactionalCache对象

​	2、调用TransactionalCache对象的``putObject()``方法。

那我们就来看看TransactionalCache是什么

**TransactionalCache**

```java
public class TransactionalCache implements Cache {

    private static final Log log = LogFactory.getLog(TransactionalCache.class);

    /**
     * 委托的 Cache 对象。
     *
     * 实际上，就是二级缓存 MappedStatement中获取的 Cache 对象。
     */
    private final Cache delegate;

    private boolean clearOnCommit;

     //在事务被提交前，所有从数据库中查询的结果会将缓存在此集合中
    private final Map<Object, Object> entriesToAddOnCommit;

    //在事务被提交前，但缓存未命中时，CacheKey会将被存储在此集合中
    private final Set<Object> entriesMissedInCache;

    @Override
    public void putObject(Object key, Object object) {
        // 暂存 KV 到 entriesToAddOnCommit 中，key则是CacheKey，  object则是查询出来的结果
        entriesToAddOnCommit.put(key, object);
    }

    @Override
    public Object getObject(Object key) {
        // issue #116
        // 从 delegate 中获取 key 对应的 value
        Object object = delegate.getObject(key);
        // 如果不存在，则添加到 entriesMissedInCache 中
        if (object == null) {
            entriesMissedInCache.add(key);
        }
        // issue #146
        // 如果 clearOnCommit 为 true ，表示处于持续清空状态，则返回 null
        if (clearOnCommit) {
            return null;
        // 返回 value
        } else {
            return object;
        }
    }


    public void commit() {
        // 如果 clearOnCommit 为 true ，则清空 delegate 缓存
        if (clearOnCommit) {
            delegate.clear();
        }
        // 将 entriesToAddOnCommit、entriesMissedInCache 刷入 delegate 中
        flushPendingEntries();
        // 重置
        reset();
    }
    
    /**
     * 将 entriesToAddOnCommit、entriesMissedInCache 刷入 delegate 中
     */
    private void flushPendingEntries() {
        // 将 entriesToAddOnCommit 刷入 delegate 中
        for (Map.Entry<Object, Object> entry : entriesToAddOnCommit.entrySet()) {
            delegate.putObject(entry.getKey(), entry.getValue());
        }
        // 将 entriesMissedInCache 刷入 delegate 中
        for (Object entry : entriesMissedInCache) {
            if (!entriesToAddOnCommit.containsKey(entry)) {
                delegate.putObject(entry, null);
            }
        }
    }
}
```

**解读：**

从上述代码的``putObject()``方法来看，它是将查询出来的结果存到了**entriesToAddOnCommit**这个HashMap中，而这个属性的含义为：**在事务被提交前，所有从数据库中查询的结果会将缓存在此集合中**

那我们再来看看``getObject()``方法，它是通过``Object object = delegate.getObject(key)``从真正的二级缓存Cache中通过cacheKey获取到缓存数据。

那么前后对比，存入是存到entriesToAddOnCommit这个HashMap中，获取是通过真正的二级缓存Cache获取，所以我们之前没有通过``sqlSession.commit()``进行事务提交是肯定没有办法从二级缓存中命中的。

那么我们再来看看上述的``commit()``方法，他去调用了``flushPendingEntries()``方法，该方法将entriesToAddOnCommit这个HashMap中事务未提交之前缓存的数据全部循环刷到了真正的二级缓存cache中，所以我们通过``sqlSession.commit()``操作之后就可以从二级缓存命中。



**多说一嘴**：

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/1%E9%98%B6%E6%AE%B5%EF%BC%9A%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01%E6%A8%A1%E5%9D%97%EF%BC%9A%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%8F%8AMyBatis%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/03.MyBatis%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)































