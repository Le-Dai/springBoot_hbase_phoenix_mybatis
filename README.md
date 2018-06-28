# springBoot_hbase_phoenix_mybatis
spring boot 使用mybatis连接phoenix使用hbase

##当前版本：

    phoenix：4.13.1-HBase-1.2 （与hbase服务器phoenix版本一致）
    spring boot:2.0.2.RELEASE

##注意：无法与swagger2集成：swagger2的guava包不能低于18.0

##使用注意事项：

1.（必要设置）配置本地HADOOP_HOME环境变量，在windows10时需要重启服务器
    
2.（必要设置）配置hosthome
```
C:\Windows\System32\drivers\etc下的hosts里面添加与zookeeper一致的hosthome设置,添加：
     10.122.22.8   blsdh014
     10.122.22.9   blsdh017
     10.122.22.10  blsdh018
```                 
3.解决包冲突
```
compile('org.apache.phoenix:phoenix-core') {
            exclude(module: 'slf4j-log4j12')
            exclude(module: 'log4j')
            exclude(module: 'jsp-2.1')
        }
compile('org.springframework.boot:spring-boot-starter') {
         exclude(module: 'guava')
     }
     compile  ('com.google.guava:guava:16.0')
```         
**注意guava包不能超过16.0,否则phoenix不能够使用，同时要解决去除phoenix的log4j依赖包时日志错误文件:** 
    
    org.apache.log4j下的文件加入Level    
 
##配置jdbc连接
``` 
spring:
    datasource:
        url: jdbc:phoenix:10.122.22.8,10.122.22.9,10.122.22.10:2181
        driver-class-name: org.apache.phoenix.jdbc.PhoenixDriver
```  
**url连接多个zookeeper地址，端口默认为2181**

##目前无法解决的问题:
    在第一连接初始化phoenix连接时会报出错误，但并非异常信息（不会做为异常信息处理），由于连接池使用单例模式，之后就不会再报异常。
    
    
##使用说明：

####支持的数据类型：
    http://phoenix.apache.org/language/datatypes.html
    
####语法：
    http://phoenix.apache.org/language/index.html
####子查询
    http://phoenix.apache.org/subqueries.html
