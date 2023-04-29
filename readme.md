## sharding sphere mysql demo

在配置规则的时候，文档出现了一些问题。在`dataSources`后面，配置了读写，还需要根据`YamlReadwriteSplittingDataSourceRuleConfiguration`内的属性配置好策略，否则会出错误
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
但是在[issue](https://github.com/apache/shardingsphere/issues/25002)列表看到有取消策略的提示，在下个版本(5.4.0)可用
