<div align=center>
<img width="200px;" src="http://mars-framework.com/img/logo-black.png"/>
</div>

<br/>

<div align=center>

<img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
<img src="https://img.shields.io/badge/jdk-11+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>

</div>
<br/>

<div align=center>
基于AIO的网络编程包
</div>


## 项目简介

Magician 是一个基于AIO的网络编程包，支持http，websocket等协议【暂时只支持http】

## 安装步骤

### 一、导入依赖

```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>最新版</version>
</dependency>

<!-- 这个是日志包，支持任意可以跟slf4j桥接的包 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```
### 二、创建Handler
```java
public class DemoHandler implements MagicianHandler {

    @Override
    public void request(MagicianRequest magicianRequest) {
        // 响应数据
        magicianRequest.getResponse()
                .setResponseHeader("content-type", "application/json;charset=UTF-8")
                .sendText(200, "ok");
    }
}
```

### 三、创建服务
```java
Magician.builder().bind(8080, 100)
                    .threadPool(传入一个线程池)
                    .httpHandler("/", new DemoHandler())
                    .start();
```