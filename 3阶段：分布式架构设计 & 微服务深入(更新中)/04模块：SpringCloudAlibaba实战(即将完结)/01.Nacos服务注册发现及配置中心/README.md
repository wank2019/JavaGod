# Nacos服务注册与发现及配置中心

## 说在前面
>**本章相关代码及笔记地址：**[**飞机票🚀**](https://github.com/EayonLee/JavaGod/tree/main/1%E9%98%B6%E6%AE%B5%EF%BC%9A%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90/01%E6%A8%A1%E5%9D%97%EF%BC%9A%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%8F%8AMyBatis%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/01.%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%81%E4%B9%85%E5%B1%82%E6%A1%86%E6%9E%B6)
>
>🌍Github：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://github.com/EayonLee/JavaGod)<br>
>🪐CSDN：[🚀Java超神之路：【🍔Java全生态技术学习笔记，一起超神吧🍔】](https://blog.csdn.net/qq_20492277/article/details/114269863)

## 本章所需安装包下载地址
链接：https://pan.baidu.com/s/1ncltc-c4u5_jXAmaTOTJMA
提取码：ra0l

## 目录
- [Nacos服务注册与发现及配置中心](#nacos服务注册与发现及配置中心)
  - [说在前面](#说在前面)
  - [目录](#目录)
  - [一. 什么是SpringCloud Alibaba?](#一-什么是springcloud-alibaba)
    - [1.1 SpringCloud Alibaba](#11-springcloud-alibaba)
    - [1.2 SpringCloud Alibaba 版本对应](#12-springcloud-alibaba-版本对应)
    - [1.3 什么是Nacos？](#13-什么是nacos)
  - [二. Nacos运行环境部署](#二-nacos运行环境部署)
    - [2.1 下载Nacos二进制包](#21-下载nacos二进制包)
    - [2.2 部署 & 启动](#22-部署--启动)
    - [2.3 访问测试](#23-访问测试)
  - [三. Nacos 服务注册与发现 及 服务调用](#三-nacos-服务注册与发现-及-服务调用)
    - [3.1 实现服务提供者](#31-实现服务提供者)
      - [3.1.2 提供者Pom文件](#312-提供者pom文件)
      - [3.1.3 提供者application.yaml核心配置文件](#313-提供者applicationyaml核心配置文件)
      - [3.1.4 提供者开启服务注册与发现功能](#314-提供者开启服务注册与发现功能)
      - [3.1.5 提供者编写一个测试接口](#315-提供者编写一个测试接口)
      - [3.1.6 提供者整体目录结构](#316-提供者整体目录结构)
      - [3.1.7 启动提供者注册到Nacos](#317-启动提供者注册到nacos)
    - [3.2 实现服务消费者 及 多方式的服务调用](#32-实现服务消费者-及-多方式的服务调用)
      - [3.2.1 消费者Pom文件](#321-消费者pom文件)
      - [3.2.2 消费者application.yaml核心配置文件](#322-消费者applicationyaml核心配置文件)
      - [3.2.3 消费者开启服务注册与发现功能 / Feign负载均衡调用](#323-消费者开启服务注册与发现功能--feign负载均衡调用)
      - [3.2.3 消费者的LoadBalancerClient和RestTemplate结和负载均衡访问方式](#323-消费者的loadbalancerclient和resttemplate结和负载均衡访问方式)
      - [3.2.4 消费者的RestTemplate负载均衡访问方式](#324-消费者的resttemplate负载均衡访问方式)
      - [3.2.5 消费者的Feign访问方式 （常用）](#325-消费者的feign访问方式-常用)
    - [3.3 Nacos客户端信息缓存](#33-nacos客户端信息缓存)
  - [四. Nacos Config 配置中心](#四-nacos-config-配置中心)
    - [4.1 读取外部Nacos Config配置中心的配置](#41-读取外部nacos-config配置中心的配置)
      - [4.1.1 创建测试服务的pom文件](#411-创建测试服务的pom文件)
      - [4.1.2 创建bootstrap.properties配置文件](#412-创建bootstrapproperties配置文件)
      - [4.2.3 启动类添加注册发现注解](#423-启动类添加注册发现注解)
      - [4.2.4 在Nacos增加配置](#424-在nacos增加配置)
      - [4.2.5 编写测试类读取外部配置](#425-编写测试类读取外部配置)
    - [4.2 基于DataId为yaml扩展名的配置方式](#42-基于dataid为yaml扩展名的配置方式)
    - [4.3 基于profile粒度的多环境配置](#43-基于profile粒度的多环境配置)
      - [4.3.1 激活多环境](#431-激活多环境)
      - [4.3.2 配置Nacos中的配置](#432-配置nacos中的配置)
      - [4.3.3 测试](#433-测试)
    - [4.4 Nacos中的namespace和group](#44-nacos中的namespace和group)
      - [4.4.1 创建namespace](#441-创建namespace)
      - [4.4.2 服务注册发现：创建group及测试](#442-服务注册发现创建group及测试)
      - [4.4.3 配置：创建group及测试](#443-配置创建group及测试)
  - [五. Nacos更换数据源](#五-nacos更换数据源)
    - [5.1 安装MySql数据库](#51-安装mysql数据库)
    - [5.3 修改Nacos数据源为MySql](#53-修改nacos数据源为mysql)
  - [六. Nacos集群部署](#六-nacos集群部署)
    - [6.1 二进制集群部署](#61-二进制集群部署)
      - [6.1.1 将原有100节点的Nacos服务停止](#611-将原有100节点的nacos服务停止)
      - [6.1.2 将100节点的Nacos目录下的垃圾文件删除](#612-将100节点的nacos目录下的垃圾文件删除)
      - [6.1.3 将100节点的Nacos目录发送到101、102节点](#613-将100节点的nacos目录发送到101102节点)
      - [6.1.4 配置100、101、102节点Nacos的cluster.conf.example集群文件](#614-配置100101102节点nacos的clusterconfexample集群文件)
      - [6.1.5 三台Nacos节点的数据源一定要是相同的MySql地址及数据库](#615-三台nacos节点的数据源一定要是相同的mysql地址及数据库)
      - [6.1.6 集群启动](#616-集群启动)
      - [6.1.7 集群环境测试](#617-集群环境测试)
    - [6.2 Nacos集群统一入口Nginx](#62-nacos集群统一入口nginx)
      - [6.2.1 安装软件包](#621-安装软件包)
      - [6.2.2 Nginx配置](#622-nginx配置)
    - [6.3 K8s部署Nacos集群](#63-k8s部署nacos集群)
      - [6.3.1 创建Nacos数据库](#631-创建nacos数据库)
      - [6.3.2 部署](#632-部署)
      - [6.3.3 服务注册](#633-服务注册)



## 一. 什么是SpringCloud Alibaba?

### 1.1 SpringCloud Alibaba

Spring Cloud Alibaba是Spring Cloud下的一个子项目，Spring Cloud Alibaba为分布式应用程序开发提供了一站式解决方案，它包含开发分布式应用程序所需的所有组件，使您可以轻松地使用Spring Cloud开发应用程序，使用Spring Cloud Alibaba，您只需要添加一些注解和少量配置即可将Spring Cloud应用程序连接到Alibaba的分布式解决方案，并使用Alibaba中间件构建分布式应用程序系统；

Spring Cloud Alibaba 是阿里巴巴开源中间件跟 Spring Cloud 体系的融合:

![image-20210609153205389](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153205389.png)

**主要特性**：

**1、流量控制和服务降级**：使用阿里巴巴Sentinel进行流量控制，断路和系统自适应保护；

**2、服务注册和发现**：实例可以在Alibaba Nacos上注册，客户可以使用Spring管理的bean发现实例，通过Spring Cloud Netflix支持Ribbon客户端负载均衡器；

**3、分布式配置**：使用阿里巴巴Nacos作为数据存储；

**4、事件驱动**：构建与Spring Cloud Stream RocketMQ Binder连接的高度可扩展的事件驱动微服务；

**5、消息总线**：使用Spring Cloud Bus RocketMQ链接分布式系统的节点；

**6、分布式事务**：支持高性能且易于使用的Seata分布式事务解决方案；

**7、Dubbo RPC**：通过Apache Dubbo RPC扩展Spring Cloud服务到服务调用的通信协议；



### 1.2 SpringCloud Alibaba 版本对应

目前最新版本：Spring Cloud Alibaba 2.2.1 

![image-20210609153413607](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153413607.png)

Spring Cloud Alibaba 2.1.0 RELEASE对应Spring Cloud Greenwich版本

Spring Cloud Alibaba 2.2.0 RELEASE对应Spring Cloud Hoxton.RELEASE版本

Spring Cloud Alibaba 2.2.1 RELEASE对应Spring Cloud Hoxton.SR3版本



### 1.3 什么是Nacos？

Nacos是阿里巴巴2018年7月推出来的一个开源项目，是一个更易于构建云原生应用的动态服务注册与发现、配置管理和服务管理平台；（Nacos：纳科斯）

Nacos致力于快速实现动态服务注册与发现、服务配置、服务元数据及流量管理；

Nacos 属于Spring cloud alibaba下的一个组件；

Nacos 约等于 spring cloud eureka（注册中心）+ spring cloud config（配置中心）

Nacos官网：https://nacos.io/

**Nacos功能特性**

- 服务发现与健康检查
- 动态配置管理
- 动态DNS服务
- 服务和元数据管理（管理平台的⻆度，nacos也有⼀个ui⻚⾯，可以看到注册的服务及其实例信息 （元数据信息）等），动态的服务权重调整、动态服务优雅下线，都可以去



## 二. Nacos运行环境部署

本章最后有二进制集群部署和K8s集群部署教程

### 2.1 下载Nacos二进制包

下载地址：https://github.com/alibaba/nacos/releases

### 2.2 部署 & 启动

```shell
#解压下载下来的nacos最新的二进制压缩包；
cd /opt
tar -zxvf nacos-server-1.3.1.tar.gz
cd /opt/nacos/bin
#启动nacos server
./startup.sh -m standalone
#注：单机环境必须带-m standalone参数启动，否则无法启动，不带参数启动的是集群环境；
#查看启动日志：
cat /opt/nacos/logs/start.out
```

> Nacos是依赖Java环境，所以确保服务器安装了Java环境才能启动成功

### 2.3 访问测试

```shell
http://192.168.159.100:8848/nacos
#默认用户名密码：nacos/nacos
```

![image-20210609153612886](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153612886.png)



## 三. Nacos 服务注册与发现 及 服务调用

![image-20210609153648835](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153648835.png)

和Dubbo类似，微服务开发是controller调用controller，调用者是服务消费者，被调用者是服务提供者，服务消费者和服务提供者是相对概念，服务消费者也可以被另一个服务调用，那么此时的服务消费者也是一个服务提供者；

在实际开发中，我们会把所有服务都注册到nacos注册中心上，由nacos去维护和管理我们的所有服务；

通过添加一个starter依赖：``spring-cloud-starter-alibaba-nacos-discovery`` 它通过自动配置、注解以及Spring Boot 编程模型与Nacos无缝集成，实现服务注册与发现，nacos经过双十一考验，可以在生产环境中作为大规模分布式系统的服务注册中心；

### 3.1 实现服务提供者

![image-20210609153728714](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153728714.png)

#### 3.1.2 提供者Pom文件

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
        <!--SpringCloud Alibaba-Nacos 服务注册发现-->
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

#### 3.1.3 提供者application.yaml核心配置文件

```xml
server:
  port: 18082

spring:
  application:
    name: nacos-provider
  cloud:
    nacos:
      discovery:
        #Nacos服务部署地址 ，如果是Nacos集群 多个IP之间使用逗号分割：192.168.159.100:8848,192.168.159.101:8848,192.168.159.102:8848
        server-addr: 192.168.159.100:8848
      #Nacos服务账号密码
      username: nacos
      password: nacos
```

#### 3.1.4 提供者开启服务注册与发现功能

在启动类上加入`` @EnableDiscoveryClient ``注解开启服务注册与发现功能

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient//开启服务注册与发现功能
public class NacosProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication.class, args);
    }

}
```

#### 3.1.5 提供者编写一个测试接口

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName EchoController
 * @Description //TODO
 * @date 2021/5/12 16:26
 * @Version: 1.0
 */
@RestController
public class EchoController {

    @GetMapping(value = "/getData")
    public Object getData(){
        return "测试数据";
    }
}
```

#### 3.1.6 提供者整体目录结构

![image-20210609153905323](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153905323.png)

#### 3.1.7 启动提供者注册到Nacos

将提供者项目启动，然后在Nacos的管理平台查看是否注册成功

![image-20210609153926255](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609153926255.png)



### 3.2 实现服务消费者 及 多方式的服务调用

#### 3.2.1 消费者Pom文件

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
        <!--SpringCloud Alibaba-Nacos 服务注册发现-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!--Ribbon 客户端负载均衡调用 这里为了测试所以Ribbon和Feign都引用了  一般这俩你选择一个使用即可-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
        <!--openfeign 客户端负载均衡调用-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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

#### 3.2.2 消费者application.yaml核心配置文件 

```xml
server:
  port: 18083

spring:
  application:
    name: nacos-consumer
  cloud:
    nacos:
      discovery:
        #Nacos服务部署地址 ，如果是Nacos集群 多个IP之间使用逗号分割：192.168.159.100:8848,192.168.159.101:8848,192.168.159.102:8848
        server-addr: 192.168.159.100:8848
      #Nacos服务账号密码
      username: nacos
      password: nacos
```

#### 3.2.3 消费者开启服务注册与发现功能 / Feign负载均衡调用 

在启动类上加入`` @EnableDiscoveryClient ``注解开启服务注册与发现功能（必须）

在启动类上加入 ``@EnableFeignClients`` 注解开启Feign（如果你没有使用Feign进行调用，则无需开启）

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient//开启服务注册与发现
@EnableFeignClients //开启feign
@SpringBootApplication
public class NacosConsumerApplication {

    /**
     * 如果使用restTemplate调用的话需要在这去加载
     * 如果你并不需要使用RestTemplate去进行调用 则无需加载
     *
     * @return
     */
    @LoadBalanced//负载均衡的去调用
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

#### 3.2.3 消费者的LoadBalancerClient和RestTemplate结和负载均衡访问方式 

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhengtai.li
 * @ClassName TestController
 * @Description //TODO
 * @Copyright 2021 © kuwo.cn
 * @date 2021/5/12 17:02
 * @Version: 1.0
 */
@RestController
public class TestController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;//负载均衡客户端

    @Autowired
    private RestTemplate restTemplate;//restTemplate方式调用


    /**
     * 使用LoadBalancerClient和RestTemplate结和的方式来负载均衡访问
     *
     * @return
     */
    @GetMapping(value = "/echo1")
    public Object echo1() {
        //通过负载均衡客户端loadBalancerClient选择一个服务：nacos-provider为提供者注册到Nacos的serviceid
        //loadBalancerClient会根据serviceid去Nacos根据loadBalancerClient自己的负载均衡策略进行选择一个服务进行返回
        //如果使用loadBalancerClient手动进行负载均衡调用的话需要将消费者的主启动类中restTemplate方法上的@LoadBalanced注解去除掉，否则调用失败
        ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-provider");
        //通过如上获取到的服务 我们可以获取到该服务的IP和端口
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        //拼接请求地址：通过提供这注册到Nacos的serviceid和端口进行调用
        String url = String.format("http://%s:%s/getData", host, port);
        System.out.println("请求地址为：" + url);
        //使用restTemplate进行调用
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }
}
```

**注意**：

使用该方式调用需要将消费者的主启动类中restTemplate方法上的``@LoadBalanced``注解去除掉，否则调用失败

![image-20210609154140626](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154140626.png)

#### 3.2.4 消费者的RestTemplate负载均衡访问方式 

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhengtai.li
 * @ClassName TestController
 * @Description //TODO
 * @Copyright 2021 © kuwo.cn
 * @date 2021/5/12 17:02
 * @Version: 1.0
 */
@RestController
public class TestController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;//负载均衡客户端

    @Autowired
    private RestTemplate restTemplate;//restTemplate方式调用


    /**
     * 使用RestTemplate结和的方式来负载均衡访问
     *
     * @return
     */
    @GetMapping(value = "/echo2")
    public Object echo2() {
        //如果使用restTemplate自动进行负载均衡调用的话需要在消费者主启动类中restTemplate方法上加上@LoadBalanced注解，否则调用失败
        //restTemplate会通过注册到Nacos的serviceid自动进行负载均衡选择一个服务调用
        String result = restTemplate.getForObject("http://nacos-provider/getData", String.class);
        return result;
    }

}
```

**注意**：

使用该方式调用需要在消费者主启动类中restTemplate方法上加上@``LoadBalanced``注解，否则调用失败

![image-20210609154254895](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154254895.png)



#### 3.2.5 消费者的Feign访问方式 （常用） 

**在消费者端定义需要调用提供者的Service**

![image-20210609154330279](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154330279.png)

service中的方法需要和提供者中的保持一致

那么如图所示还需要创建该Service对应的服务降级处理类和Feign配置类

``如果你的某个Service不需要服务降级处理，在这里可以不需要配置fallback选项``

``如果你一个服务降级处理类都没有，那么这里也可以不需要配置configuration选项，因为该选项的Feign配置类就是用来加载服务降级处理类的``



**在消费者端定义调用Service的服务降级处理类**

``如果你的某个Service不需要服务降级处理，在这里可以不需要创建服务降级处理类``

![image-20210609154424825](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154424825.png)

**在消费者端定义Feign配置类**

``如果你一个服务降级处理类都没有，那么该Feign配置类也可以不需要创建``

![image-20210609154443763](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154443763.png)

**在消费者端进行调用测试**

```java
import com.eayon.service.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengtai.li
 * @ClassName TestController
 * @Description //TODO
 * @Copyright 2021 © kuwo.cn
 * @date 2021/5/12 17:02
 * @Version: 1.0
 */
@RestController
public class TestController {

    @Autowired
    private EchoService echoService;//feign的声明式调用


    /**
     * 使用Feign进行调用
     *
     * @return
     */
    @GetMapping(value = "/echo3")
    public String echo3() {
        String data = echoService.echo();
        return data;
    }
}
```



### 3.3 Nacos客户端信息缓存 

**当Nacos宕机后，消费者是否还可以成功调用提供者？**

当消费者调用过某提供者服务后 Nacos才宕机的话，消费者是还可以继续调用提供者的。因为消费者是先去到本地缓存中查询提供者的调用信息，如果查询不到才去Nacos拉取提供者信息。

但是在Nacos宕机前已经调用过提供者了，这也代表着消费者本地已经存在了该提供者的调用信息，所以就不再依赖Nacos。

后面会通过分析SpringCloud Alibaba的源码去详细分析，其实就是保存在了一个Map里面然后缓存在了内存



## 四. Nacos Config 配置中心 

Nacos提供用于``存储配置``和``其他元数据``功能，为分布式系统中的外部化配置提供服务器端和客户端支持，使用Spring Cloud Alibaba Nacos Config就可以在Nacos Server集中管理Spring Cloud应用的外部属性配置；

Spring Cloud Alibaba Nacos Config是在启动的bootstrap阶段，将配置加载到Spring环境中；

Spring Cloud Alibaba Nacos Config使用DataId和GROUP确定一个配置；

**类似的产品**：Apollo、SpringCloud Config...



### 4.1 读取外部Nacos Config配置中心的配置 

#### 4.1.1 创建测试服务的pom文件 

这里创建的测试服务：nacos-config 只是我们用来测试读取外部配置中心Nacos config的一个服务，并非Nacos Config配置中心本身。

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
        <relativePath/>
    </parent>-->

    <groupId>com.eayon</groupId>
    <artifactId>nacos-config</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>nacos-config</name>
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
        <!-- Nacos服务注册发现 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!--Nacos Config配置中心-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
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



#### 4.1.2 创建bootstrap.properties配置文件 

![image-20210609154641111](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154641111.png)

**为什么创建bootstrap.properties？**

因为该服务启动时肯定是需要先去连接Nacos的，如果连接Nacos的配置在application.properties中则会和一些自定义业务配置同时加载，那么业务配置和Nacos连接配置的优先级无法控制，所以将Nacos连接配置放在bootstrap.properties中。

``bootstrap.properties在SpringBoot项目启动时优先于application.properties加载。``

```xml
#端口
server.port=18084

#服务名称
spring.application.name=nacos-config

#nacos的用户名和密码
spring.cloud.nacos.username=nacos
spring.cloud.nacos.password=nacos

#Nacos注册发现连接地址，如果是集群模式有多个的话就用逗号隔开
spring.cloud.nacos.discovery.server-addr=192.168.159.100:8848

#nacos配置中心的连接地址，如果是集群模式有多个的话就用逗号隔开
spring.cloud.nacos.config.server-addr=192.168.159.100:8848
```

**注意**：

当你使用域名的方式来访问Nacos时，

spring.cloud.nacos.config.server-addr 配置的方式为 ``域名:port``，例如 Nacos的域名为nacos.power.com，监听的端口为80，

则 spring.cloud.nacos.config.server-addr=nacos.power.com:80,**注意80 端口不能省略**；

#### 4.2.3 启动类添加注册发现注解 

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient//服务注册于发现
@SpringBootApplication
public class NacosConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfigApplication.class, args);
    }

}
```

#### 4.2.4 在Nacos增加配置 

![image-20210609154758203](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154758203.png)

![image-20210609154809279](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154809279.png)

DataId默认使用`spring.application.name`配置跟文件扩展名结合(配置格式默认使用.properties)，GROUP不配置默认使用``DEFAULT_GROUP``；

#### 4.2.5 编写测试类读取外部配置 

![image-20210609154856697](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154856697.png)



### 4.2 基于DataId为yaml扩展名的配置方式 

Nacos Config 除了默认支持.properties格式以外，也支持yaml格式，这个时候只需要完成以下两步：

**1、在应用的bootstrap.properties配置文件中地声明DataId文件扩展名；bootstrap.properties文件配置如下：**

```xml
spring.cloud.nacos.config.file-extension=yaml
```

![image-20210609154934606](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609154934606.png)

**2、在Nacos的web管控台新增一个DataId为yaml扩展名的配置，如下所示：**

```txt
Data ID:       nacos-config2
Group  :       DEFAULT_GROUP
配置格式:        YAML
配置内容:        user.name: 张三丰
                user.age: 35
```

![image-20210609155002654](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155002654.png)



**3、修改应用的服务名称，和Nacos中新的配置的DataId保持一致**

![image-20210609155021760](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155021760.png)



**4、测试读取到的数据是否时新配置的**

![image-20210609155037434](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155037434.png)



### 4.3 基于profile粒度的多环境配置 

``spring-cloud-starter-alibaba-nacos-config`` 在加载配置的时候，不仅仅加载了以 dataid 为 ``${spring.application.name}.${file-extension:properties}`` 为的基础配置，还加载了dataid为 ``${spring.application.name}-${profile}.${file-extension:properties} ``的基础配置；

在日常开发中如果遇到多套环境下的不同配置，可以通过Spring提供的 ``${spring.profiles.active} ``配置项来激活使用某个配置文件；

#### 4.3.1 激活多环境 

```xml
#激活多种环境 ：dev、pro、test
# 比如这里配置dev，那么他会去Nacos Config中查询DataId=${spring.application.name} + "-" + ${spring.profiles.active}的配置  nacos-config2-dev.yaml
spring.profiles.active=dev
```

#### 4.3.2 配置Nacos中的配置 

![image-20210609155256595](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155256595.png)

> 如果激活了多环境的话，并且是YAML的格式，那么DataId后面一定要加.yaml后缀，否则加载不到该配置

#### 4.3.3 测试 

![image-20210609155312137](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155312137.png)

### 4.4 Nacos中的namespace和group 

无论是服务的注册发现还是配置文件都有namespace和group的隔离

如上使用中我们并没有特定去配置namespace和group，因为它是走的默认配置

``默认namespace：public``

``默认group：DEFAULT_GROUP``

那么我们可以将配置进行分命名空间和分组进行管理

#### 4.4.1 创建namespace 

``命名空间是公用的，创建了之后再服务列表和配置列表都会存在该命名空间``

![image-20210609155344838](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155344838.png)

#### 4.4.2 服务注册发现：创建group及测试 

**创建Group**

```xml
#Nacos注册发现的命名空间配置  值为命名空间ID
spring.cloud.nacos.discovery.namespace=c7146cfc-a16d-4dd8-a005-ee680364d398
#Nacos注册发现的分组 如果该组不存在直接在此处进行定义即可
spring.cloud.nacos.discovery.group=DEV_GROUP
```

![image-20210609155524498](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155524498.png)

**查看**

![image-20210609155542612](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155542612.png)

#### 4.4.3 配置：创建group及测试 

**直接修改Group名称即可，他会自动创建**

![image-20210609155600309](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155600309.png)

**应用的bootstrap.properties文件中增加配置文件所在group及namespace配置**

```xml
#Nacos Config中配置文件所在的group
spring.cloud.nacos.config.group=DEV_GROUP
#Nacos Config中配置文件所在的namespace，值为命名空间ID
spring.cloud.nacos.config.namespace=c7146cfc-a16d-4dd8-a005-ee680364d398
```

![image-20210609155627684](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155627684.png)

**启动应用测试是否可以读取配置**

![image-20210609155644759](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155644759.png)



## 五. Nacos更换数据源 

Nacos默认情况下是采用apache derby内嵌数据库进行数据存储，在单机模式时可以使用nacos嵌入式数据库实现数据存储，但是derby数据库不方便观察数据存储的基本情况，从nacos 0.7版本开始增加了支持mysql数据源能力；

### 5.1 安装MySql数据库 

http://www.zzvips.com/article/124798.html

**5.2 初始化数据库**

初始化MySQL数据库，数据库初始化文件： ``nacos-mysql.sql``，该文件可以在Nacos程序包下的 conf目录下获得

![image-20210609155726866](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155726866.png)

**连接数据库**

![image-20210609155740973](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155740973.png)

![image-20210609155801198](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155801198.png)

**往该数据库中执行SQL脚本**

![image-20210609155821036](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155821036.png)

![image-20210609155829212](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155829212.png)

### 5.3 修改Nacos数据源为MySql 

修改 ``conf/application.properties``文件，增加支持MySQL数据源配置，添加（目前只支持mysql）数据源的url、用户名和密码；

```shell
#修改文件
vim /opt/nacos/conf/application.properties
```

![image-20210609155912920](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609155912920.png)

**删除垃圾文件**

```shell
rm -rf /opt/nacos/data /opt/nacos/logs /opt/nacos/bin/derby.log /opt/nacos/bin/logs /opt/nacos/bin/work
```

**重启Nacos**

```shell
#关闭Nacos 然后等几秒再启动
/opt/nacos/bin/shutdown.sh
#单机启动Nacos的话一定要加-m standalone
/opt/nacos/bin/startup.sh -m standalone
```

**访问Nacos**

我们会发现之前的配置都没了，因为数据库已经换掉了

![image-20210609160018288](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160018288.png)

**测试数据库存储**

新建一个配置

![image-20210609160043054](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160043054.png)

**查看数据库是否成功存储**

![image-20210609160109891](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160109891.png)

至此说明数据库替换成功

## 六. Nacos集群部署 

### 6.1 二进制集群部署 

#### 6.1.1 将原有100节点的Nacos服务停止 

```shell
/opt/nacos/bin/shutdown.sh
```

#### 6.1.2 将100节点的Nacos目录下的垃圾文件删除

```shell
rm -rf /opt/nacos/data /opt/nacos/logs /opt/nacos/bin/derby.log /opt/nacos/bin/logs /opt/nacos/bin/work
```

#### 6.1.3 将100节点的Nacos目录发送到101、102节点

```shell
scp -r /opt/nacos/ root@192.168.159.101:/opt
scp -r /opt/nacos/ root@192.168.159.102:/opt
```

#### 6.1.4 配置100、101、102节点Nacos的cluster.conf.example集群文件

```shell
#将cluster.conf.example文件名修改为cluster.conf
mv /opt/nacos/conf/cluster.conf.example /opt/nacos/conf/cluster.conf
#修改配置文件
vim /opt/nacos/conf/cluster.conf
```

三个节点配置文件中的IP相同，就是Nacos集群个节点的IP

![image-20210609160250301](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160250301.png)

#### 6.1.5 三台Nacos节点的数据源一定要是相同的MySql地址及数据库

101、102节点的Nacos数据库连接地址需要从127.0.0.1改成192.168.159.100

![image-20210609160328511](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160328511.png)

生产环境数据库需要使用主备模式，这里就不去做高可用数据库了

#### 6.1.6 集群启动 

```shell
#三台节点挨个启动 启动命令相同
/opt/nacos/bin/startup.sh

#查看启动是否成功
jps

#查看启动日志
tail /opt/nacos/logs/nacos.log -f
```

#### 6.1.7 集群环境测试 

分别测试三个节点的Nacos管理平台是否正常，并且查看配置是否一致，一致说明连接的MySql数据库没问题

![image-20210609160413952](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160413952.png)

**修改服务的Nacos连接地址**

![image-20210609160428978](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160428978.png)

**测试读取配置**

![image-20210609160444295](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160444295.png)

Nacos集群节点有三种角色状态：``leader、follower、candidate``；

当leader宕机，会从剩下的follower中投票选举出一个新的leader，选举算法是基于*Raft算法**实现；*

*经测试，发现有一点与三个角色不符，部署3个nacos节点，其中宕机2台，只剩下一个节点，此节点将变为*candidate角色，但是此时该nacos集群仍然可以注册服务，订阅服务，（按照正确的理论应该是：如果nacos集群中没有leader角色的节点就不能注册服务，因为leader角色处理事务性请求），这比较匪夷所思，有待研究

### 6.2 Nacos集群统一入口Nginx 

#### 6.2.1 安装软件包 

```shell
#yum install epel-release -y
yum install nginx -y
yum  install keepalived -y

#彻底卸载nginx
yum  --purge autoremove nginx
```

#### 6.2.2 Nginx配置 

```shell
cat > /etc/nginx/nginx.conf << "EOF"
worker_processes  1; 
 
events { 
    worker_connections  1024; 
} 
 
http { 
    include       mime.types; 
    default_type  application/octet-stream; 
 
    sendfile        on; 
 
    keepalive_timeout  65; 
 
    #nacos集群负载均衡 
    upstream nacos-cluster { 
        server 192.168.159.100:8848; 
        server 192.168.159.101:8848; 
        server 192.168.159.102:8848; 
    } 
 
    server { 
        listen       80; 
        server_name  localhost; 
 
        location / { 
            #root   html; 
            #index  index.html index.htm; 
            proxy_pass http://nacos-cluster; 
        } 
 
        error_page   500 502 503 504  /50x.html; 
        location = /50x.html { 
            root   html; 
        } 
    } 
 
} 
EOF
```

**启动Nginx**

```shell
systemctl daemon-reload
systemctl start nginx
systemctl enable nginx

#查看日志
tail /var/log/nginx/access.log -f
```

**最后只需要将项目中所有连接Nacos地址的地方改为Nginx地址即可  如:192.168.159.201:80   最后一定要加80**



### 6.3 K8s部署Nacos集群 

相关博客：https://blog.csdn.net/fsjwin/article/details/110503029

#### 6.3.1 创建Nacos数据库 

先新建一个数据库：``nacos-config``

（可以随便取名，但需要与Nacos.yaml文件中的数据库配置项相同）

**创建Nacos配置表**

```shell
/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(20) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
	`username` varchar(50) NOT NULL PRIMARY KEY,
	`password` varchar(500) NOT NULL,
	`enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
	`username` varchar(50) NOT NULL,
	`role` varchar(50) NOT NULL,
	UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');

```

![image-20210609160805683](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160805683.png)

#### 6.3.2 部署 

**Nacos.yaml**

如下nacos资源清单兼容最新2.0.1及以下所有版本

```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: nacos-headless
  labels:
    app: nacos-headless
spec:
  type: ClusterIP
  clusterIP: None
  ports:
    - port: 8848
      name: server
      targetPort: 8848
    - port: 9848
      name: client-rpc
      targetPort: 9848
    - port: 9849
      name: raft-rpc
      targetPort: 9849
      ## 兼容1.4.x版本的选举端口
    - port: 7848
      name: old-raft-rpc
      targetPort: 7848
  selector:
    app: nacos
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: nacos-cm
  namespace: default
data:
  mysql.host: "10.0.74.21"
  mysql.db.name: "k8s-nacos"
  mysql.port: "25037"
  mysql.user: "editor_write"
  mysql.password: "w876b@93"
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nacos
  namespace: default
spec:
  serviceName: nacos-headless
  replicas: 3
  template:
    metadata:
      labels:
        app: nacos
      annotations:
        pod.alpha.kubernetes.io/initialized: "true"
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                  - key: "app"
                    operator: In
                    values:
                      - nacos-headless
              topologyKey: "kubernetes.io/hostname"
      containers:
        - name: k8snacos
          imagePullPolicy: Always
          image: nacos/nacos-server:latest
          ports:
            - containerPort: 8848
              name: client
            - containerPort: 9848
              name: client-rpc
            - containerPort: 9849
              name: raft-rpc
            - containerPort: 7848
              name: old-raft-rpc
          env:
            - name: NACOS_REPLICAS
              value: "3"
            - name: MYSQL_SERVICE_HOST
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.host
            - name: MYSQL_SERVICE_DB_NAME
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.db.name
            - name: MYSQL_SERVICE_PORT
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.port
            - name: MYSQL_SERVICE_USER
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.user
            - name: MYSQL_SERVICE_PASSWORD
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.password
            - name: MODE
              value: "cluster"
            - name: NACOS_SERVER_PORT
              value: "8848"
            - name: PREFER_HOST_MODE
              value: "hostname"
            - name: NACOS_SERVERS
              value: "nacos-0.nacos-headless.default.svc.cluster.local:8848 nacos-1.nacos-headless.default.svc.cluster.local:8848 nacos-2.nacos-headless.default.svc.cluster.local:8848"
  selector:
    matchLabels:
      app: nacos
---
# ------------------- App Service ------------------- #
#apiVersion: v1
#kind: Service
#metadata:
#  name: nacos-service
#  namespace: default
#  annotations:
#    nginx.ingress.kubernetes.io/affinity: "true"
#    nginx.ingress.kubernetes.io/session-cookie-name: backend
#    nginx.ingress.kubernetes.io/load-balancer-method: drr
#spec:
#  type: NodePort
#  ports:
#  - protocol: TCP
#    nodePort: 30003
#    port: 80
#    targetPort: 8848
#  selector:
#    app: nacos
#---
# ------------------- App Ingress ------------------- #
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nacos-web
  namespace: default
spec:        
  rules:
    - host: k8snacos.kwrd.mobi
      http:
        paths:
          - backend:
              service:
                name: nacos-headless
                port:
                  number: 8848
            path: /
            pathType: Prefix
```

**参数说明**

**Service模块**

![image-20210609160853294](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160853294.png)

如图所示，对于nacos服务暴露了多个端口，由于我们部署的是最新的2.0.1版本，我这里还兼容了老版本的端口，对于新版本2.0.1，还需要暴露9848和9849 RPC端口。

**数据库配置**

![image-20210609160913551](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160913551.png)

这里通过CinfigMap来为Nacos服务提供数据库连接配置。

**StatefulSet**

![image-20210609160934945](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160934945.png)

如上框选信息分别为：Naocs负载数，Naocs容器端口，Nacos负载数环境变量（与前面对应）

![image-20210609160954353](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609160954353.png)

我们有三个Nacos负载，所以这里的NACOS_SERVERS的值也需要配置三个。

**注意**：``nacos-0.nacos-headless.default.svc.cluster.local:8848`` 中的default代表的是这个Pod所在的名空间名称，如果没有指定命名空间则写``default``即可

**Ingress**

![image-20210609161022361](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609161022361.png)

Nacos服务的Ingress配置。

域名为：``k8snacos.kwrd.mobi``

服务端口：``8848``



**应用nacos.yaml**

```shell
kubectl apply -f nacos.yaml
```

**访问WEB**

http://k8snacos.kwrd.mobi/nacos/

![image-20210609161054499](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609161054499.png)

#### 6.3.3 服务注册 

application.yaml配置文件

![image-20210609161112941](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609161112941.png)

Naocs的连接地址如果是域名的话一定要加上端口，否则会连接失败

![image-20210609161131272](https://cdn.jsdelivr.net/gh/EayonLee/IMG-Cloud@master/data/image-20210609161131272.png)

如果上述注册不成功可以看下这个博客的方法试一下：https://blog.csdn.net/fsjwin/article/details/110217895