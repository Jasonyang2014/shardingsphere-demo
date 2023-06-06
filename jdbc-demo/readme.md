### Seata

使用seata全局事务(at模式)，非常简单。只需要在入口service方法是使用`@GlobalTransactional`即可。

seata集成springboot，
类`io.seata.spring.annotation.GlobalTransactionScanner`实现`InitializingBean`，在完成实例化后，执行
```java
class GlobalTransactionScanner extends AbstractAutoProxyCreator
        implements ConfigurationChangeListener, InitializingBean, ApplicationContextAware, DisposableBean{

    @Override
    public void afterPropertiesSet() {
        if (disableGlobalTransaction) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Global transaction is disabled.");
            }
            ConfigurationCache.addConfigListener(ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
                    (ConfigurationChangeListener)this);
            return;
        }
        //cas
        if (initialized.compareAndSet(false, true)) {
            //初始化客户端
            initClient();
        }
    }

    private void initClient() {
        //init TM
        TMClient.init(applicationId, txServiceGroup, accessKey, secretKey);
        //init RM
        RMClient.init(applicationId, txServiceGroup);
        //注册关闭钩子
        registerSpringShutdownHook();
    }
}
```