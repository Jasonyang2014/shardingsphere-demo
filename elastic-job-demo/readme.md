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
- **ShardingListenerManager** 实现分片机制
- 实现error handler，需要在配置文件配置**job-error-handler-type: EMAIL**
  
    [配置页面](https://shardingsphere.apache.org/elasticjob/current/cn/user-manual/elasticjob-lite/configuration/built-in-strategy/error-handler/)
    根据不同的方案去配置，默认为LOG。这个配置文件，文档没有说明该怎么配置，需要自己去看源码。邮件的相关配置得放到props这个属性里面。
    另外这个邮件发送没有做缓冲设置，导致同样的错误会被发送多次。最好是可以做一个errorMsg去重，同一类型的错误在一段时间内只发送一次。
    简便的办法是可以做一个hash、md5、crc32之类的特征值做一个缓存去重
  
  ```
    elastic-job:
    regCenter:
      serverLists: 127.0.0.1:2181
      namespace: elastic-job-lite
    jobs:
      simpleJob:
        job-error-handler-type: EMAIL
        job-sharding-strategy-type: AVG_ALLOCATION
        elastic-job-class: com.example.elastic.job.demo.jobs.MyJob
        cron: 0/10 * * * * ?
        time-zone: GMT+08:00
        sharding-total-count: 3
        sharding-item-parameters: 0=FEMALE,1=MALE,2=UNKNOW
        job-parameter: 0,1,2
        props:
          email.host: smtp.126.com
          email.port: 465
          email.username: tianrenshui1988@126.com
          email.password: xxxx
          email.from: tianrenshui1988@126.com
          email.to: 305744830@qq.com
          email.subject: "elastic job email test"
          email.debug: true
          email.useSsl: true
          email.ssl.trust: smtp.126.com
      ```
