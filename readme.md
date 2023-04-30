## sharding sphere mysql demo

5.3.2版本规则配置
```yaml
rules:
- !READWRITE_SPLITTING
  dataSources:
    readwrite_ds:
      staticStrategy:
        writeDataSourceName: master
        readDataSourceNames:
          - slave
      loadBalancerName: random
  loadBalancers:
    random:
      type: RANDOM
```
在[issue](https://github.com/apache/shardingsphere/issues/25002)列表看到有取消策略的提示，在下个版本(5.4.0)可用
