<h1>Hbase + phoenix 二级索引 大数据量复杂查询解决方案</h1>

  Hbase 是基于hdfs存储的列式数据库,个人对于hbase的理解 就是大数据量的数据仓库 它适用于在线分析处理（OLAP）适合大宽表. 虽然hbase有快速查询rowkey的有点
但是索引却只建立在rowkey上  也就是说只有通过rowkey检索才可以使用索引 如果在复杂查询场景内 每次的全表扫描的性能开销肯定是不可忽略的.所以出现了在hbase之上
开发二级索引的需求, 目前solr+hbase的方案也是做的比较完善,目前比较火爆的es+hbase 结合的案例也很好.

目前增强hbase索引能力的一些方案
https://mp.weixin.qq.com/s?__biz=MzU5OTQ1MDEzMA==&mid=2247484665&idx=1&sn=0f5ef7df76ffe533f30a83a3d271b4f7&scene=21#wechat_redirect

<h1>phoenix 搭建流程 CDH集成方式</h1>
1.进入Cloudera官网下载Phoenix的Parcel：

`http://archive.cloudera.com/cloudera-labs/phoenix/parcels/latest/`

2.将Parcel 放入cm 仓库进入 进入CM主页点击《主机 -> Parcel》进入Parcel管理页面

3.在Parcel管理页面点击《配置》进入Parcel配置  检查新Parcel

4. 依次点击《下载 -> 分配 -> 激活》完成Parcel布置

5.HBase配置更新  重启相关服务

<h1>phoenix 集成springboot使用</h1>

phoenix 是通过jdbc的方式连接  通过网上一些文章来看  好像druid对于phoenix 好像不太兼容 会报错,有可能是版本问题 具体没有亲自尝试,使用了HikariCP连接池

第一步创建springboot maven项目  加入pom依赖

```scala
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.hejz</groupId>
    <artifactId>springBoot_hbase_phoenix_mybatis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>phoenix-mybatis</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <mysql.version>5.1.32</mysql.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>com.google.guava</groupId>
                    <artifactId>guava</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>16.0</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--凤凰hbase插件-->
        <dependency>
            <groupId>org.apache.phoenix</groupId>
            <artifactId>phoenix-core</artifactId>
            <version>4.7.0-HBase-1.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--分页-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.5</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <!--<dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.0.18</version>
        </dependency>-->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>


</project>

```

接下来是application的配置文件  这里需要注意的是  因为通过连接池需要创建不同数据源  比如mysql和 phoenix 两个数据源

`application.properties`

```properties
mybatis.config-location=mybatis-config.xml
datasource.maxPoolSize=20
datasource.minIdle=2
datasource.validationTimeout=300000
datasource.idleTimeout=600000
datasource.connectionTestQuery=select 1+1
mybatis.mapperLocations=

#phoenix 数据源1
datasource.driverClassName = org.apache.phoenix.jdbc.PhoenixDriver
datasource.jdbcUrl = jdbc:phoenix:anser01,anser02,anser03:2181
datasource.username = root
datasource.password = root

#mysql  数据源2
spring.datasource.test2.driverClassName = com.mysql.jdbc.Driver
spring.datasource.test2.jdbcUrl = jdbc:mysql://****:3306/bigdata?useUnicode=true&characterEncoding=UTF-8
spring.datasource.test2.username = root
spring.datasource.test2.password = ***
```

接下来是配置config datasource

HikariDataSourceFactory
```java
package com.wxstc.bigdata.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源
 */
@Configuration
public class HikariDataSourceFactory extends UnpooledDataSourceFactory {

  public HikariDataSourceFactory() {
    this.dataSource = new HikariDataSource();
  }
}

```
mysql 数据源 需要注意的是 @Primary @Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常 
DataSource1Config

```java
package com.wxstc.bigdata.config;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.wxstc.bigdata.mqmapper", sqlSessionTemplateRef  = "test1SqlSessionTemplate")
public class DataSource1Config {

    @Bean(name = "test1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.test2")
    @Primary
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "test1SqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("test1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/test1/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "test1TransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("test1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "test1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}

```

phoenix 数据源

PhoenixDataSourceConfig

```java
package com.wxstc.bigdata.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;


@Configuration
@MapperScan(basePackages = PhoenixDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "PhoenixSqlSessionFactory")
public class PhoenixDataSourceConfig {
    static final String PACKAGE = "com.wxstc.bigdata.phoenixdao.**";

    @Bean(name = "PhoenixDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.test1")
    public DataSource phoenixDataSource() throws IOException {
        ResourceLoader loader = new DefaultResourceLoader();
        InputStream inputStream = loader.getResource("classpath:application.properties")
                .getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);
        Set<Object> keys = properties.keySet();
        Properties dsProperties = new Properties();

        for (Object key : keys) {
            if (key.toString().startsWith("datasource")) {
                dsProperties.put(key.toString().replace("datasource.", ""), properties.get(key));
            }
        }
        HikariDataSourceFactory factory = new HikariDataSourceFactory();
        factory.setProperties(dsProperties);
        inputStream.close();
        return factory.getDataSource();
    }

    @Bean(name = "PhoenixSqlSessionFactory")
    public SqlSessionFactory phoenixSqlSessionFactory(
            @Qualifier("PhoenixDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        ResourceLoader loader = new DefaultResourceLoader();
        String resource = "classpath:mybatis-config.xml";
        factoryBean.setConfigLocation(loader.getResource(resource));
        factoryBean.setSqlSessionFactoryBuilder(new SqlSessionFactoryBuilder());
        return factoryBean.getObject();
    }

}

```

其他的mapper  entity  正常一样配置即可