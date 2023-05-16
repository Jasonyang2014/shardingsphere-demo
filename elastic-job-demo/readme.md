### elastic job
使用的时候记得清除zookeeper的数据，因为没有清除数据，被分片弄了很久时间

[管理平台](https://github.com/apache/shardingsphere-elasticjob-ui)可以查看任务执行情况
- 添加zookeeper节点信息
- 添加任务的数据库信息
    
    这个特别坑，默认的界面不支持mysql，需要手动添加驱动到项目ext-lib。elastic-job文档及其粗糙，根本没有啥介绍。在GitHub项目页面才有一些说明。
    另外，application.properties 里面的
**dynamic.datasource.allowed-driver-classes={'org.h2.Driver','org.postgresql.Driver','com.mysql.jc.jdbc.Driver'}** 
  必须要添加mysql驱动，否则无法添加数据源
- **RDBTracingListenerConfiguration**是实现elasticjob.tracing.type=RDB的关键
