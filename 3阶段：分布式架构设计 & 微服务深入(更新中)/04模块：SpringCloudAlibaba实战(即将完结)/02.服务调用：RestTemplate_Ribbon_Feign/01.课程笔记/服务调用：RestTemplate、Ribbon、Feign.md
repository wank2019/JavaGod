# 服务调用：RestTemplate、Ribbon、Feign

## 说在前面

>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)

## 目录
- [服务调用：RestTemplate、Ribbon、Feign](#服务调用resttemplateribbonfeign)
  - [说在前面](#说在前面)
  - [目录](#目录)
  - [一. 提供者及消费者准备](#一-提供者及消费者准备)
    - [1.1 准备两个提供者](#11-准备两个提供者)
      - [1.1.1 创建两个提供者:nacos-provider及naocs-provider2](#111-创建两个提供者nacos-provider及naocs-provider2)
      - [1.1.2 创建pom](#112-创建pom)
      - [1.1.3 创建application.yaml](#113-创建applicationyaml)
      - [1.1.5 创建对外访问接口](#115-创建对外访问接口)
      - [1.1.6 启动提供者注册到Nacos](#116-启动提供者注册到nacos)
    - [1.2 准备一个消费者](#12-准备一个消费者)
      - [1.2.1 创建一个消费者](#121-创建一个消费者)
      - [1.2.2 创建pom](#122-创建pom)
      - [1.2.2 创建application.yaml](#122-创建applicationyaml)
      - [1.2.3 开启服务注册发现](#123-开启服务注册发现)
      - [1.2.4 启动消费者注册到Nacos](#124-启动消费者注册到nacos)
  - [二. RestTemplate](#二-resttemplate)
    - [2.1 RestTemplate请求模板类解读](#21-resttemplate请求模板类解读)
    - [2.2 RestTemplate的配置](#22-resttemplate的配置)
    - [2.3 RestTemplate的GET请求](#23-resttemplate的get请求)
      - [2.3.1 getForEntity() 方式](#231-getforentity-方式)
      - [2.3.2 getForObject() 方式](#232-getforobject-方式)
    - [2.4 RestTemplate的POST请求](#24-resttemplate的post请求)
    - [2.5 RestTemplate的PUT请求](#25-resttemplate的put请求)
    - [2.6 RestTemplate的DELETE请求](#26-resttemplate的delete请求)
  - [三. Ribbon](#三-ribbon)
    - [3.1 Ribbon组件解读](#31-ribbon组件解读)
    - [3.2 客户端负载均衡 和 服务端负载均衡](#32-客户端负载均衡-和-服务端负载均衡)
    - [3.3 Ribbon实现服务调用](#33-ribbon实现服务调用)
    - [3.4 Ribbon负载均衡策略](#34-ribbon负载均衡策略)
      - [3.4.1 更改Ribbon默认负载均衡策略](#341-更改ribbon默认负载均衡策略)
      - [3.4.2 自定义负载均衡策略](#342-自定义负载均衡策略)
      - [3.4.3 个性化负载均衡配置](#343-个性化负载均衡配置)
      - [3.4.4 Nacos权重负载均衡](#344-nacos权重负载均衡)
      - [3.4.5 Nacos不能跨namespace调用](#345-nacos不能跨namespace调用)
    - [3.5 Ribbon饥饿加载配置](#35-ribbon饥饿加载配置)
  - [四. Feign](#四-feign)
    - [4.1 简介](#41-简介)
    - [4.2 使用Feign实现消费者](#42-使用feign实现消费者)
      - [4.2.1 添加Feign依赖](#421-添加feign依赖)
      - [4.2.2 开启Feign注解](#422-开启feign注解)
      - [4.2.3 声明服务](#423-声明服务)
      - [4.2.4 创建服务降级处理类](#424-创建服务降级处理类)
      - [4.2.5 Feign配置类（实例化服务降级处理类）](#425-feign配置类实例化服务降级处理类)
      - [4.2.6 调用测试](#426-调用测试)
    - [4.3 Feign配置超时时间](#43-feign配置超时时间)
      - [4.3.1 全局配置](#431-全局配置)
      - [4.3.2 指定服务配置](#432-指定服务配置)

## 一. 提供者及消费者准备

### 1.1 准备两个提供者 

#### 1.1.1 创建两个提供者:nacos-provider及naocs-provider2 

![image-20210609170042374](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609170042374.png)

#### 1.1.2 创建pom

``nacos-provider``及``naocs-provider2``的pom文件相同，所以下面只举例了``nacos-provider``的pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!-- parent标签只能单继承一个SpringBoot 但是我们还需要继承SpringCloud-alibaba 所以我们把这里注释 通过最下面的dependencyManagement继承 -->
    <!--<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.5</version>
    </parent>-->

    <groupId>com.eayon</groupId>
    <artifactId>nacos-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>nacos-provider</name>
    <description>Demo project for Spring Boot</description>


    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <spring-boot.version>2.3.0.RELEASE</spring-boot.version><!--指定SpringBoot版本-->
        <spring-cloud-alibaba.version>2.2.1.RELEASE</spring-cloud-alibaba.version><!--指定spring-cloud-alibaba版本-->
    </properties>

    <dependencies>
        <!--SpringCloud Alibaba-Nacos-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>


    <!-- 继承SpringBoot及SpringCloud-Alibaba依赖 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>


    <build>
        <plugins>
            <!--Maven编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!--SpringBoot编译插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```



#### 1.1.3 创建application.yaml 

``nacos-provider``及``naocs-provider2``的``application.yaml``文件大致相同，所以下面只举例了``nacos-provider``的``application.yaml``文件

**注意**：需要修改端口号、服务命不用改 保持一致

```xml
server:
  port: 18082

spring:
  application:
    name: nacos-provider
  cloud:
    nacos:
      discovery:
        #Nacos服务地址，集群模式有多个的话用逗号隔开
        server-addr: 192.168.159.100:8848
        #注册到哪个命名空间下：填写命名空间ID
        namespace: 2af21206-a502-46e8-bd05-41548e8b1cd5
        #注册到哪个组下
        group: DEV_GROUP
      #Nacos服务用户名和密码
      username: nacos
      password: nacos
```



**1.1.4 开启服务注册与发现功能**

在``nacos-provider``及``nacos-provider2``的主启动类上开启服务注册与发现注解 ``@EnableDiscoveryClient``

```java
@SpringBootApplication
@EnableDiscoveryClient//开启服务注册与发现功能
public class NacosProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication.class, args);
    }

}
```



#### 1.1.5 创建对外访问接口

所涉及的User实体类自行定义即可

以下只演示了``nacos-provider``的对外接口，``nacos-provider2``的相同，只需要修改以下返回值和``nacos-provider``进行区别即可。

```java
@RestController
public class EchoController {

    /*          GET请求              */
    @GetMapping(value = "/getData")
    public String getData() {
        return "【提供者1】测试数据";
    }

    @GetMapping(value = "/getData2")
    public String getData2(@RequestParam String something) {
        return "【提供者1】输入的内容：" + something;
    }

    @GetMapping(value = "/getData3")
    public Object getData3(User user){
        return user;
    }

    /*         POST请求              */
    @PostMapping(value = "/add1")
    public String add1() {
        return "【提供者1】添加成功";
    }
    
    @PostMapping(value = "/add2")
    public String add2(@RequestParam String something) {
        return "【提供者1】添加成功：" + something;
    }
    
    @PostMapping(value = "/add3")
    public User add3(@RequestBody User user){
        return user;
    }

    /*         PUT请求              */
    @PutMapping(value = "/update1")
    public String update1() {
        return "【提供者1】修改成功";
    }
    
    @PutMapping(value = "/update2")
    public String update2(@RequestParam String something) {
        return "【提供者1】修改成功：" + something;
    }
    
    @PutMapping(value = "/update3")
    public User update3(@RequestBody User user){
        return user;
    }
    
    /*         DELETE请求              */
    @DeleteMapping(value = "/delete1")
    public String delete1() {
        return "【提供者1】删除成功";
    }
    
    @DeleteMapping(value = "/delete2")
    public String delete2(@RequestParam String something) {
        return "【提供者1】删除成功：" + something;
    }
    
    @DeleteMapping(value = "/delete3")
    public User delete3(@RequestBody User user){
        return user;
    }
}
```



#### 1.1.6 启动提供者注册到Nacos

![image-20210609170350616](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609170350616.png)

### 1.2 准备一个消费者

#### 1.2.1 创建一个消费者

![image-20210609170430696](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609170430696.png)

#### 1.2.2 创建pom

这里没有添加Ribbon或者Feign的依赖，后面具体使用到的时候再去添加，这里只进行基础的消费者配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!-- parent标签只能单继承一个SpringBoot 但是我们还需要继承SpringCloud-alibaba 所以我们把这里注释 通过最下面的dependencyManagement继承 -->
    <!--<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.5</version>
    </parent>-->

    <groupId>com.eayon</groupId>
    <artifactId>nacos-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>nacos-consumer</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <spring-boot.version>2.3.0.RELEASE</spring-boot.version><!--指定SpringBoot版本-->
        <spring-cloud-alibaba.version>2.2.1.RELEASE</spring-cloud-alibaba.version><!--指定spring-cloud-alibaba版本-->
        <spring-cloud.version>Hoxton.SR3</spring-cloud.version><!--指定spring-cloud版本-->
    </properties>

    <dependencies>
        <!--SpringCloud Alibaba-Nacos-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>


    <!-- 继承SpringBoot及SpringCloud-Alibaba依赖 和 SpringCloud依赖 -->
    <dependencyManagement>
        <dependencies>
            <!--SpringCloud-Alibaba-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--SpringBoot-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--SpringCloud-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>


    <build>
        <plugins>
            <!--Maven编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!--SpringBoot编译插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```



#### 1.2.2 创建application.yaml

```yaml
server:
  port: 18083

spring:
  application:
    name: nacos-consumer
  cloud:
    nacos:
      discovery:
        #Nacos服务地址，集群模式有多个的话用逗号隔开
        server-addr: 192.168.159.100:8848
        #注册到哪个命名空间下：填写命名空间ID
        namespace: 2af21206-a502-46e8-bd05-41548e8b1cd5
        #注册到哪个组下
        group: DEV_GROUP
      #Nacos服务用户名和密码
      username: nacos
      password: nacos
```



#### 1.2.3 开启服务注册发现

这里没有添加开启Feign的注解或者实例化RestTemplate的方法，等后面使用到的时候会进行添加，这里只进行基础的消费者配置

```java
@EnableDiscoveryClient//开启服务注册与发现
@SpringBootApplication
public class NacosConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```



#### 1.2.4 启动消费者注册到Nacos

![image-20210609170605064](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609170605064.png)

消费者的调用代码又下面各章节演示，这里只准备基础的提供者及消费者服务

## 二. RestTemplate

### 2.1 RestTemplate请求模板类解读

当我们服务消费者去调用服务提供者提供的服务的时候，使用了一个极其方便的对象叫``RestTemplate``，我们通常使用RestTemplate中最简单的一个功能``getForObject`` 发起了一个get请求去调用服务端的数据，同时，我们还通过配置``@LoadBalanced``注解开启客户端负载均衡，RestTemplate的功能非常强大， 接下来我们就来详细的看一下RestTemplate中几种常见请求方法的使用，在日常操作中，基于Rest的方式通常是四种情况，它们分别是：

``GET 请求 --查询数据``

``POST 请求 –添加数据``

``PUT 请求 – 修改数据``

``DELETE 请求 –删除数据``

### 2.2 RestTemplate的配置

在主启动类中加载RestTemplate

```java
@EnableDiscoveryClient//开启服务注册与发现
@SpringBootApplication
public class NacosConsumerApplication {

    /**
     * 如果使用restTemplate调用的话需要在这去加载
     * 如果你并不需要使用RestTemplate去进行调用 则无需加载
     * 如果使用loadBalancerClient手动进行负载均衡调用的话需要将@LoadBalanced注解去除掉，否则调用失败
     * 如果加上@LoadBalanced注解，那么使用restTemplate调用会自动进行负载均衡
     * 
     * @return
     */
    @Bean
    @LoadBalanced//负载均衡的去调用
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

springCloud-Alibaba默认已经集成了Ribbon，所以我们只需要在实例化RestTemplate方法上加上``@LoadBalanced``注解就可以通过Ribbon这个客户端负载均衡器去Nacos中拉取服务端列表然后进行负载均衡调用。

### 2.3 RestTemplate的GET请求

#### 2.3.1 getForEntity() 方式

```java
@RestController
public class RestTempalteController {

    @Autowired
    private RestTemplate restTemplate;


    /**
     * GET请求 - getForEntity方式
     *
     * @return
     */
    @GetMapping(value = "/getData1")
    public Object getData1() {
        //无参请求
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://nacos-provider/getData", String.class);
        System.out.println(responseEntity.getStatusCodeValue());//请求结果状态
        System.out.println(responseEntity.getBody());//请求结果值

        //有参请求 - 数组传参（通过集合中的顺序和URL中的参数下标对应）
        String[] paramArray = {"GET有参请求-数组传参"};
        ResponseEntity<String> responseEntity2 = restTemplate.getForEntity("http://nacos-provider/getData2?something={0}", String.class, paramArray);
        System.out.println(responseEntity2.getStatusCodeValue());//请求结果状态
        System.out.println(responseEntity2.getBody());//请求结果值

        //有参请求 - Map传参（通过Map中的Key与URL中的参数名称对应）
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("name","GET有参请求 - Map传参");
        paramMap.put("age",25);
        ResponseEntity<User> responseEntity3 = restTemplate.getForEntity("http://nacos-provider/getData3?name={name}&age={age}", User.class, paramMap);
        System.out.println(responseEntity3.getStatusCodeValue());//请求结果状态
        System.out.println(responseEntity3.getBody());//请求结果值

        return responseEntity3.getBody();
    }
}
```

该方法返回一个 ``ResponseEntity``对象，ResponseEntity是 Spring 对 HTTP 请求响应的封装，包括了几个重要的元素，比如``响应码、contentType、 contentLength、响应消息体``等；

**getForEntity方法第一个参数**

要调用的服务的地址，即服务提供者提供的``http://nacos-provider/getData``接口地址，注意这里是通过服务名调用，会根据Naocs中该服务下的所有实例进行负载均衡调用

**getForEntity方法第二个参数**

String.class 表示希望返回的 body 类型是 String 类型，如果希望返回一个对象，也是可以的，比如User对象；

**请求参数**

并非实体参数一定要用Map传参，只是演示而已，不管是实体参数还是单个参数都可以使用数组或Map传参

分为：数组、Map两种方式

数组：通过集合中的元素顺序和URL中的参数下标对应

Map：通过Map中的Key与URL中的参数名称对应

#### 2.3.2 getForObject() 方式

```java
@RestController
public class RestTempalteController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * GET请求 - getForObject方式
     *
     * @return
     */
    @GetMapping(value = "/getData2")
    public Object getData2() {
        //无参请求
        String s = restTemplate.getForObject("http://nacos-provider/getData", String.class);
        System.out.println(s);//请求结果值

        //有参请求 - 数组传参（通过集合中的顺序和URL中的参数下标对应）
        String[] paramArray = {"GET有参请求-数组传参"};
        String s2 = restTemplate.getForObject("http://nacos-provider/getData2?something={0}", String.class, paramArray);
        System.out.println(s2);//请求结果值

        //有参请求 - Map传参（通过Map中的Key与URL中的参数名称对应）
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("name","GET有参请求 - Map传参");
        paramMap.put("age",25);
        User user = restTemplate.getForObject("http://nacos-provider/getData3?name={name}&age={age}", User.class, paramMap);
        System.out.println(JSONObject.toJSONString(user));//请求结果值

        return user;
    }
}
```

**请求参数**

并非实体参数一定要用Map传参，只是演示而已，不管是实体参数还是单个参数都可以使用数组或Map传参



``getForObject()`` ，可以将http的响应体body信息转化成指定的对象 如User、String，方便我们的代码开发；

当你不需要返回响应中的其他信息，只需要body体信息的时候，可以使用这个更方便；如果希望返回一个对象，也是可以的，比如User对象；

### 2.4 RestTemplate的POST请求

```java
@RestController
public class RestTempalteController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * POST请求
     *
     * @return
     */
    @GetMapping(value = "/add")
    public Object add() {
        /**
         * postForEntity方式
         */
        //无参请求
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://nacos-provider/add1", null, String.class);
        System.out.println(responseEntity.getStatusCodeValue());
        System.out.println(responseEntity.getBody());//请求结果值
    
        //有参请求 - 数组传参（通过集合中的顺序和URL中的参数下标对应）
        String[] paramArray = {"POST有参请求-数组传参"};
        ResponseEntity<String> responseEntity2 = restTemplate.postForEntity("http://nacos-provider/add2?something={0}", null, String.class, paramArray);
        System.out.println(responseEntity2.getStatusCodeValue());
        System.out.println(responseEntity2.getBody());//请求结果值
    
        //有参请求 - 请求体传参
        User user = new User();
        user.setName("POST有参请求 - 请求体Body传参");
        user.setAge(25);
        ResponseEntity<User> responseEntity3 = restTemplate.postForEntity("http://nacos-provider/add3", user, User.class);
        //如果参数中除了有实体使用请求体Body传参之外还有 普通单个参数需要传递的话，还是以在第四个参数位置加上Map或者数组传递  如下
        //ResponseEntity<User> responseEntity3 = restTemplate.postForEntity("http://nacos-provider/add3", user, User.class, paramArray);
        System.out.println(responseEntity3.getStatusCodeValue());
        System.out.println(responseEntity3.getBody());//请求结果值
    
        /**
         * postForEntity方式
         */
        //无参请求
        String s = restTemplate.postForObject("http://nacos-provider/add1", null, String.class);
        System.out.println(s);//请求结果值
    
        //有参请求 - 数组传参（通过集合中的顺序和URL中的参数下标对应）
        String s2 = restTemplate.postForObject("http://nacos-provider/add2?something={0}", null, String.class, paramArray);
        System.out.println(s2);//请求结果值
    
        //有参请求 - 请求体传参
        User u = restTemplate.postForObject("http://nacos-provider/add3", user, User.class);
        //如果参数中除了有实体使用请求体Body传参之外还有 普通单个参数需要传递的话，还是以在第四个参数位置加上Map或者数组传递  如下
        //ResponseEntity<User> responseEntity3 = restTemplate.postForEntity("http://nacos-provider/add3", user, User.class, paramArray);
        System.out.println(JSONObject.toJSONString(u));//请求结果值
    
        /**
         * 传递JSON
         */
        String userJson = "{\"name\":\"POST有参请求 - 请求体Body传参JSON\",\"age\":25}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(userJson, headers);
        User user1 = restTemplate.postForObject("http://nacos-provider/add3", entity, User.class);
        System.out.println(JSONObject.toJSONString(user1));
    
        return user1;
    }
}
```

**getForEntity方法第一个参数**

要调用的服务的地址，即服务提供者提供的``http://nacos-provider/add``接口地址，注意这里是通过服务名调用，会根据Naocs中该服务下的所有实例进行负载均衡调用

**getForEntity方法第二个参数**

请求体中的参数，也就是通过``@requestBody``注解的参数。如果需要传递实体，那么就通过该参数进行传递，如User；或者使用MultiValueMap进行封装每一个属性，用普通的Map是传递不过去的

**getForEntity方法第三个参数**

String.class 表示希望返回的 body 类型是 String 类型，如果希望返回一个对象，也是可以的，比如User对象；

**getForEntity方法第四个参数**

普通请求参数，也就是@RequestParam注解的参数，如果除了请求体中有实体参数，还有单个普通参数的话可以在这里通过Map或者数组的方式传递

### 2.5 RestTemplate的PUT请求

```java
@RestController
public class RestTempalteController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * PUT请求
     *
     * @return
     */
    @GetMapping(value = "/update")
    public Object update() {
        //无参
        restTemplate.put("http://nacos-provider/update1", null);
        
        //有参-普通参数
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("something", "PUT请求-有参-普通参数");
        restTemplate.delete("http://nacos-provider/update2?something={something}", paramMap);

        //有参-请求体
        User user = new User();
        user.setName("PUT请求-实体传参");
        user.setAge(25);
        restTemplate.put("http://nacos-provider/update3", user);

        return "success";
    }

```

put方法没有返回值

### 2.6 RestTemplate的DELETE请求

```java
@RestController
public class RestTempalteController {

    @Autowired
    private RestTemplate restTemplate;


    /**
     * DELETE请求
     *
     * @return
     */
    @GetMapping(value = "/delete")
    public Object delete() {
        //无参
        restTemplate.delete("http://nacos-provider/delete1");

        //有参-普通参数
        Map<String, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put("something", "DELETE请求-有参-普通参数");
        restTemplate.delete("http://nacos-provider/delete2?something={something}", paramMap);

        return "success";
    }

}
```



## 三. Ribbon

### 3.1 Ribbon组件解读

Ribbon是什么？Ribbon是基于Netflix Ribbon实现的一套**客户端**负载均衡器；主要功能是提供客户端的**软件负载均衡**算法，**它会从nacos中获取一个可用的服务端列表，通过心跳检测来剔除故障的服务端节点以保证清单中都是可以正常访问的服务端节点；当客户端发送请求，则Ribbon负载均衡器按某种算法（比如轮询、权重、随机等）从维护的可用服务端清单中取出一台服务端的地址，然后进行请求**；

Ribbon非常简单，可以说就是一个jar包，这个jar包实现了负载均衡算法，Spring Cloud Alibaba底层对Ribbon做了二次封装，可以让我们使用 RestTemplate的服务请求，自动转换成客户端负载均衡的服务调用；Ribbon支持多种负载均衡算法，还支持自定义的负载均衡算法；

我们通常说的负载均衡是指将一个请求均匀地分摊到不同的节点单元上执行，负载均衡分为硬件负载均衡和软件负载均衡：

**硬件负载均衡**：比如 F5、深信服、Array 等；

**软件负载均衡：**比如 Nginx、LVS、HAProxy 等；（是由一个服务器实现的）

### 3.2 客户端负载均衡 和 服务端负载均衡

从另一个角度来观看负载均衡的话，负载均衡又分为两种方式：**服务端负载均衡、客户端负载均衡**

![image-20210609173853317](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609173853317.png)

![image-20210609173903518](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609173903518.png)

### 3.3 Ribbon实现服务调用

**首先加入Ribbon依赖**

```xml
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

其实可以不用添加Ribbon依赖，因为我们在引入spring-cloud-starter-alibaba-nacos-discovery 这个依赖的时候 它底层已经引入了ribbon

![image-20210609173951605](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609173951605.png)

**要使用Ribbon，只需要一个注解 @LoadBalanced**

在RestTemplate上面加入``@LoadBalanced``注解，这样就可以实现RestTemplate在调用时通过Ribbon去Nacos中拉取服务端列表，然后负载均衡的选择可用节点进行调用。

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate(){
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
}
```



### 3.4 Ribbon负载均衡策略

Ribbon的负载均衡策略是由 IRule 接口定义, 该接口由如下实现：

在jar包：``com.netflix.ribbon#ribbon-loadbalancer``中；

![image-20210609174035455](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174035455.png)

#### 3.4.1 更改Ribbon默认负载均衡策略

默认是ZoneAvoidanceRule策略，想要更改默认策略，在消费者主启动类中把对应的负载均衡接口实现类作为一个Bean配置一下就行了，即像下面的 **iRule()** 方法：

```java
@EnableDiscoveryClient//开启服务注册与发现
@SpringBootApplication
public class NacosConsumerApplication {

    /**
     * 如果使用restTemplate调用的话需要在这去加载
     * 如果你并不需要使用RestTemplate去进行调用 则无需加载
     * 如果使用loadBalancerClient手动进行负载均衡调用的话需要将@LoadBalanced注解去除掉，否则调用失败
     *
     * @return
     */
    @Bean
    @LoadBalanced//负载均衡的去调用
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * 更改Ribbon负载均衡策略，默认是ZoneAvoidanceRule策略
     * @return
     */
    @Bean
    public IRule iRule(){
        return new WeightedResponseTimeRule();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

**已有的负载均衡策略有**：

| **负载均衡实现**           | **策略**                                                     |
| -------------------------- | ------------------------------------------------------------ |
| RandomRule                 | 随机                                                         |
| RoundRobinRule             | 轮询                                                         |
| AvailabilityFilteringRule  | 先过滤掉由于多次访问故障的服务，以及并发连接数超过阈值的服务，然后对剩下的服务按照轮询策略进行访问； |
| WeightedResponseTimeRule   | 根据平均响应时间计算所有服务的权重，响应时间越快服务权重就越大被选中的概率即越高，如果服务刚启动时统计信息不足，则使用RoundRobinRule策略，待统计信息足够会切换到该WeightedResponseTimeRule策略； |
| RetryRule                  | 先按照RoundRobinRule策略分发，如果分发到的服务不能访问，则在指定时间内进行重试，然后分发其他可用的服务； |
| BestAvailableRule          | 先过滤掉由于多次访问故障的服务，然后选择一个并发量最小的服务； |
| ZoneAvoidanceRule （默认） | 综合判断服务节点所在区域的性能和服务节点的可用性，来决定选择哪个服务； |

#### 3.4.2 自定义负载均衡策略

主要需要去继承 AbstractLoadBalancerRule 这个抽象类，该类去实现了IRule接口。

继承该抽象类的好处在于比直接实现IRule接口少实现几个方法。

```java
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Random;

/**
 * @author zhengtai.li
 * @ClassName MyRibbonRule
 * @Description 自定义负载均衡策略
 * @Copyright 2021 © kuwo.cn
 * @date 2021/5/18 17:36
 * @Version: 1.0
 */
public class MyRibbonRule extends AbstractLoadBalancerRule {

    /**
     * 用于初始化一些配置信息  可以不用去具体实现内容
     *
     * @param iClientConfig
     */
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }


    /**
     * 自定义一个随机选择服务节点的负载均衡策略
     *
     * @param o
     * @return
     */
    @Override
    public Server choose(Object o) {
        System.out.println("自定义负载均衡策略.......");
        ILoadBalancer lb = getLoadBalancer();
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            //获取可用服务
            List<Server> upList = lb.getReachableServers();
            //获取所有服务
            List<Server> allList = lb.getAllServers();
            //总服务数
            int serverCount = allList.size();
            if (serverCount == 0) {
                return null;
            }

            //从可用服务中随机获取一个
            int index = new Random().nextInt(serverCount);
            server = upList.get(index);

            //如果获取到的服务是空则继续循环获取可用服务
            if (server == null) {
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            server = null;
            Thread.yield();
        }

        return server;
    }
}
```

**配置类的方式 - 更改Ribbon负载均衡策略为我们刚刚自定义的策略**

该方式是消费者调用所有提供者时使用的负载均衡策略，是消费者全局负载均衡配置

```java
@EnableDiscoveryClient//开启服务注册与发现
@SpringBootApplication
public class NacosConsumerApplication {

    /**
     * 如果使用restTemplate调用的话需要在这去加载
     * 如果你并不需要使用RestTemplate去进行调用 则无需加载
     * 如果使用loadBalancerClient手动进行负载均衡调用的话需要将@LoadBalanced注解去除掉，否则调用失败
     *
     * @return
     */
    @Bean
    @LoadBalanced//负载均衡的去调用
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * 更改Ribbon负载均衡策略，默认是ZoneAvoidanceRule策略
     * @return
     */
    @Bean
    public IRule iRule(){
        //更改Ribbon负载均衡策略为我们刚刚自定义的策略
        return new MyRibbonRule();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```



#### 3.4.3 个性化负载均衡配置

如上通过主启动类或者配置类实例化IRule加载自定义负载均衡策略的方式是消费者全局配置。

如果我们只想针对某个或多个提供者配置自定义负载均衡策略的话可以使用如下配置文件的方式去加载。

**配置文件的方式 - 更改Ribbon负载均衡策略为我们刚刚自定义的策略**

该方式是指定消费者调用某提供者时使用的负载均衡策略，并非消费者全局负载均衡配置

```xml
#通过配置文件 更改Ribbon负载均衡策略
nacos-provider: #这里的nacos-provider为提供者的服务名称 即注册到Nacos的serviceId
  ribbon:
    NFLoadBalancerRuleClassName: com.eayon.ribbon.MyRibbonRule #这里为自定义的负载均衡类，也可以是自带的负载均衡的实现类
```

> 格式：
>
> <serviceName>: #为哪个服务指定负载均衡策略
>
>  	   ribbon:
>	
>  		NFLoadBalancerClassName: #ILoadBalancer该接口实现类
>	
>  		NFLoadBalancerRuleClassName: #IRule该接口实现类
>	
>  		NFLoadBalancerPingClassName: #Iping该接口实现类
>	
>  		NIWSServerListClassName: #ServerList该接口实现类
>	
>  		NIWSServerListFilterClassName: #ServiceListFilter该接口实现类

![image-20210609174232862](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174232862.png)

**注意：**

- 使用配置文件的方式就需要注释掉主启动类或放在自定义配置类中实例化负载均衡策略的IRule()方法
- 如上配置的nacos-provider并不是固定配置，而是该消费者给某提供者专门配置了一个负载均衡策略。所以nacos-provider就是提供者注册到Nacos的serviceId。当然我们也可以在配置文件中给多个提供者同时去配置不同的负载均衡策略

![image-20210609174251367](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174251367.png)

#### 3.4.4 Nacos权重负载均衡

我们可以直接在管理后台对某提供者集群的各个节点进行权重的修改   ``权重取值范围：0~1   你也可以写0.5或者0.6这样的小数，只要区间在0~1``

**权重为 0 不会进行调用**

设置权重为0可以实现服务优雅下线，即先调整权重，在下线。避免实例在下线过程中被调用。

在线上调试时，在调试期间将权重设置为0，避免业务访问造成的干扰，调整完成后恢复权重。

![image-20210609174315640](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174315640.png)

那么我们只需要将默认的负载均衡策略更换成 NacosRule 策略即可。该策略已经实现了基于权重的负载均衡

![image-20210609174336594](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174336594.png)

#### 3.4.5 Nacos不能跨namespace调用

消费者和提供者的命名空间必须相同才能调用，不能跨命名空间进行调用，命名空间可以实现服务的完全隔离；

举例：

​	如果消费者通过 ``spring.cloud.nacos.discovery.namespace=b91111e4-8a21-4c12-9a3f-cf40d93a8319`` 设置了命名空间为dev

​	提供者nacos-provider设置命名空间为``test``

​	提供者nacos-provider设置命名空间为``dev``

​	那么消费者永远不会调用到命名空间为test的提供者，他只能调用与自己相同命名空间下的服务



### 3.5 Ribbon饥饿加载配置

Ribbon默认时使用懒加载的，什么意思呢？就是说当程序启动的时候不会去Nacos去拉取注册列表。而是等接收到请求要发起调用的时候才会去拉取，那么就导致一开始的请求响应时间会比较久。

那么我们就可以将默认的懒加载修改为饥饿加载，让程序在启动的时候就去Nacos拉取注册表，达到数据快速响应的目的。

**只需在消费者的配置文件中加入如下配置即可**

![image-20210609174436488](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174436488.png)

**注意**：

​	如果需要饥饿加载多个服务的话，只需要在clients后面添加多个服务名称即可，并且用逗号隔开



## 四. Feign

### 4.1 简介

Feign 是 Netflix 公司开发的一个``声明式的REST调用客户端``；（调用远程的restful风格的http接口的一个组件）

我们平时大部分时间都说Feign，实际上这个项目的全称是叫**OpenFeign**；

**Http远程调用组件其实很多，比如**：

- HttpURLConnection（JDK） java.net.*
- HttpClient（apache）
- RestTemplate（Spring）
- OkHttp（android）
- Feign（Netflix）

Spring Cloud Feign对Ribbon负载均衡进行了简化（``Feign默认集成了Ribbon``），在其基础上进行了进一步的封装，在配置上大大简化了开发工作，它是一种声明式的调用方式，它的使用方法是定义一个接口，然后在接口上添加注解，就可以通过``Feign底层封装的默认调用工具httpClient``结和Ribbon进行负载均衡调用。

### 4.2 使用Feign实现消费者

#### 4.2.1 添加Feign依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 4.2.2 开启Feign注解

在项目入口类上添加``@EnableFeignClients``注解表示开启Spring Cloud Feign的支持功能

```java
@EnableDiscoveryClient//开启服务注册与发现
@SpringBootApplication
@EnableFeignClients//开启Feign
public class NacosConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

#### 4.2.3 声明服务

定义一个EchoService接口，通过``@FeignClient`` 注解来指定调用哪个服务提供者，该接口中所定义的方法尽量与提供者保持一致。

```java
@FeignClient(name = "nacos-provider",//提供者在Nacos的serviceId
        fallback = EchoServiceFallback.class,//该接口的服务降级处理类
        configuration = FeignConfiguration.class)//Feign配置类 主要用于实例化各种服务降级处理类
public interface EchoService {

    @GetMapping("/getData2")
    String getData2(@RequestParam("something") String something);

}
```

![image-20210609174645002](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174645002.png)

#### 4.2.4 创建服务降级处理类

```java
public class EchoServiceFallback implements EchoService{

    @Override
    public String getData2(String something) {
        return "测试：降级处理";
    }

}
```

![image-20210609174723667](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174723667.png)

#### 4.2.5 Feign配置类（实例化服务降级处理类）

```java
public class FeignConfiguration {

    /**
     * 主要实例化某Service的降级处理类
     *
     * @return
     */
    @Bean
    public EchoServiceFallback echoServiceFallback() {
        return new EchoServiceFallback();
    }
}
```

![image-20210609174758080](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174758080.png)

#### 4.2.6 调用测试

```java
@RestController
public class FeignController {

    @Autowired
    private EchoService echoService;

    @GetMapping("/getData2")
    public String getData2(){
        String s = echoService.getData2("测试Feign调用");
        System.out.println(s);
        return s;
    }
}
```



### 4.3 Feign配置超时时间

#### 4.3.1 全局配置

对所有提供者服务都生效

![image-20210609174849411](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174916948.png)

#### 4.3.2 指定服务配置

下边代码中使用的 ``feign.client.config.nacos-provider``，意思是该配置只针对名为``nacos-provider`` 的服务有效，可以根据实际的需要替换成你自己的服务名

![image-20210609174916948](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609174916948.png)