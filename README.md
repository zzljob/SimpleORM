SimpleORM
=========

一个针对于Android的简单的ORM框架

why?
-------------
	因为在Android开发的时候经常对于数据库就是存放一些数据的
	很多情况下可能就是存放一些缓存数据或者是常量数据
	一般来讲不会像服务端那样的复杂的数据存储
	所以我觉得对于Android开发来讲我们是需要一个简洁而又方便的ORM
	于是我就将我日常开发中的一些对SQLite数据库的操作工具收集起来作为一个简洁的ORM框架

what？
--------------
	1、通过EntityRepository将实体类SQLite的表结构对应
	2、通过继承EntityManager类对所有实体可进行增删查改
	3、整个框架没有使用反射，文件操作等技术，速度跟自己写SQL速度相仿
	4、代码不多，足够简洁
	5、文档都在代码里
	6、想对字段加密，自己实现加密方式

libs support ?
----------------
	1、操作日志使用Log4j，依赖包已经放到libs目录
	




