# 块链Api接口DEOM说明

接口提基于`Spring Boot` 开发

用于与`WeBASE`交互

[https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Front/interface.html?highlight=%2Ftrans%2Fhandle#id401](接口官方文档)

## 配置文件

Spring Boot 配置文件路径在 `src/main/resources/application.properties`

按需配置相应参数

```properties
#TomCat 默认端口：
#--为防止与前端的端口冲突，避免使用默认8080
server.port=8081
# WeBASE交易接口
url=http://master:5002/WeBASE-Front/trans/handle
# 合约名称
name=Trace
# 合约地址
address=0x......
# 合约ABI
abi=......
# 测试用户地址
adminAddress=0x......
distributorAddress=0x......
producerAddress=0x......
retailerAddress=0x......
```

## 启动类

Spring 默认启动类，无需修改 路径`src/main/java/com/example/demo/ApiDeomApplication.java`

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiDeomApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDeomApplication.class, args);
	}

}
```

## Http类

这个类是我自己写的，用于发送HTTP请求

类只简单实现了一个POST方法，足够用于向WeBASE交易接口发送POST请求用了

路径`src/main/java/com/example/demo/HttpRequest.java`

详见内部文档注释

## API类

用于对外开放交易接口，实现数据上链于查询

路径`src/main/java/com/example/demo/Api.java`

具体实现可以看文档注释



### 提供了以下接口

1. http://localhost:8081/newFood

   请求方式：**POST**

   用于给新生产的食物录入数据

   形参:
   	name – 产品名
   	traceNumber – 溯源码
   	operatorName – 厂商名
   	qualit – 品质

<hr />

2. http://localhost:8081/distributeFood

    请求方式：**POST**

    用于给分销商拿到商品后录入数据

    形参:
	traceNumber – 溯源码
	operatorName – 分销商名称
	quality – 品质

<hr />

3. http://localhost:8081/retailFood

   请求方式：**POST**

   用于给零售商拿到商品后录入数据

   形参:

   ​	traceNumber – 溯源码
   ​	operatorName – 分销商名称

   ​	quality – 品质

<hr />

4. http://localhost:8081/queryFoodTraceInfo

   请求方式：**GET**

   用于查询某个溯源码对应的商品的消息

   形参:
       traceNumber – 溯源码

<hr />

5. http://localhost:8081/queryFoodTraceExists

   返回溯源码是否存在

   请求方式：**GET**

   无参
