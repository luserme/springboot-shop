# springboot-shop

#### 项目介绍
**毕业设计:基于ssj商城的设计与实现**
基于springboot + spring + spring data jpa 的商城项目

#### 功能模块

- 用户管理模块
- 分类管理模块
- 商品模块
- 购物车模块
- 收货地址模块
- 支付模块
- 订单模块

#### 软件架构

- [v1.0:单体架构](https://gitee.com/luserme/springboot-shop/tree/v1.0/)
- [v2.0:nginx+tomcat+redis分布式集群 (ing)](https://gitee.com/luserme/springboot-shop/tree/develop/)
- v3.0:基于spring cloud微服务 (后续)

#### 使用技术(v1.0)

###### 框架
- springboot
- spring
- spring data jpa

###### java插件
- lombok
- guava
- joda-time
- Jackson

###### 数据库
- mysql

###### 工具
- vsftpd

###### 支付对接
- 支付宝当面付

###### 系统环境
- manjaro linux 17.1

###### 开发工具
- idea 2017.2.6

###### 容器及服务
- tomcat
- nginx
- vsftpd

#### 使用说明

1. IDE中添加lombok插件,否则无法识别会报错
2. 修改数据库连接池
3. 修改ftp服务器信息
4. 使用nginx搭建图片服务器
5. 支付宝配置信息可以先调通官方沙箱demo,修改zfbinfo.properties中信息

#### 感谢
感谢geely老师的教程,学习过程中收益匪浅,贴一下geely老师的教程地址

1. [Java大牛 带你从0到上线开发企业级电商项目](http://coding.imooc.com/class/96.html)
2. [Java企业级电商项目架构演进之路 Tomcat集群与Redis分布式](http://coding.imooc.com/class/162.html)
