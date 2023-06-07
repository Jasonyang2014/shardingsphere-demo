## sharding sphere mysql demo

### jdbc模式
sharding sphere的入口类在驱动类`org.apache.shardingsphere.driver.ShardingSphereDriver`。
被动处理，获取连接的时候才会触发程序运行。

进而通过`org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory`加载yaml配置
```java
class YamlShardingSphereDataSourceFactory {
    //创建datasource
    public static DataSource createDataSource(final byte[] yamlBytes) throws SQLException, IOException {
        YamlRootConfiguration rootConfig = YamlEngine.unmarshal(yamlBytes, YamlRootConfiguration.class);
        //将配置转换成对象，将多个数据源放入
        return createDataSource(DATA_SOURCE_SWAPPER.swapToDataSources(rootConfig.getDataSources()), rootConfig);
    }

    private static DataSource createDataSource(final Map<String, DataSource> dataSourceMap, final YamlRootConfiguration rootConfig) throws SQLException {
        ModeConfiguration modeConfig = null == rootConfig.getMode() ? null : new YamlModeConfigurationSwapper().swapToObject(rootConfig.getMode());
        Collection<RuleConfiguration> ruleConfigs = SWAPPER_ENGINE.swapToRuleConfigurations(rootConfig.getRules());
        return ShardingSphereDataSourceFactory.createDataSource(rootConfig.getDatabaseName(), modeConfig, dataSourceMap, ruleConfigs, rootConfig.getProps());
    }
}

class ShardingSphereDataSourceFactory{
    //创建ShardingSphereDataSource
    public static DataSource createDataSource(final String databaseName, final ModeConfiguration modeConfig,
                                              final Map<String, DataSource> dataSourceMap, final Collection<RuleConfiguration> configs, final Properties props) throws SQLException {
        return new ShardingSphereDataSource(getDatabaseName(databaseName), modeConfig, dataSourceMap, null == configs ? new LinkedList<>() : configs, props);
    }
}
```
创建`ShardingSphereDataSource`并缓存。
```java
class ShardingSphereDataSource{

    public ShardingSphereDataSource(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                    final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        this.databaseName = databaseName;
        //创建上下文管理
        contextManager = createContextManager(databaseName, modeConfig, dataSourceMap, ruleConfigs, null == props ? new Properties() : props);
        jdbcContext = new JDBCContext(contextManager.getDataSourceMap(databaseName));
    }


    private ContextManager createContextManager(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                                final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        //加载Meta实例builder
        InstanceMetaData instanceMetaData = TypedSPILoader.getService(InstanceMetaDataBuilder.class, "JDBC").build(-1);
        Collection<RuleConfiguration> globalRuleConfigs = ruleConfigs.stream().filter(each -> each instanceof GlobalRuleConfiguration).collect(Collectors.toList());
        Collection<RuleConfiguration> databaseRuleConfigs = new LinkedList<>(ruleConfigs);
        databaseRuleConfigs.removeAll(globalRuleConfigs);
        ContextManagerBuilderParameter param = new ContextManagerBuilderParameter(modeConfig, Collections.singletonMap(databaseName,
                new DataSourceProvidedDatabaseConfiguration(dataSourceMap, databaseRuleConfigs)), globalRuleConfigs, props, Collections.emptyList(), instanceMetaData, false);
        //根据spi加载ContextManagerBuilder的实现类
        return TypedSPILoader.getService(ContextManagerBuilder.class, null == modeConfig ? null : modeConfig.getType()).build(param);
    }
}
```
根据不同的模式加载不同的`ContextManager`
- **Standalone** `org.apache.shardingsphere.mode.manager.standalone.StandaloneContextManagerBuilder`
- **Cluster** `org.apache.shardingsphere.mode.manager.cluster.ClusterContextManagerBuilder`

事务管理器加载通过`org.apache.shardingsphere.transaction.ShardingSphereTransactionManagerEngine`来进行。
`Seata`的事务管理类`org.apache.shardingsphere.transaction.base.seata.at.SeataATShardingSphereTransactionManager`

```java
class ShardingSphereTransactionManagerEngine{
    //加载事务管理器
    private void loadTransactionManager() {
        //通过SPI进行加载，如果引入了seata包，则会加载seata的事务管理器
        //org.apache.shardingsphere.transaction.base.seata.at.SeataATShardingSphereTransactionManager
        for (ShardingSphereTransactionManager each : ShardingSphereServiceLoader.getServiceInstances(ShardingSphereTransactionManager.class)) {
            if (transactionManagers.containsKey(each.getTransactionType())) {
                log.warn("Find more than one {} transaction manager implementation class, use `{}` now",
                        each.getTransactionType(), transactionManagers.get(each.getTransactionType()).getClass().getName());
                continue;
            }
            transactionManagers.put(each.getTransactionType(), each);
        }
    }
}

```


```java
class StandaloneContextManagerBuilder {

    @Override
    public ContextManager build(final ContextManagerBuilderParameter param) throws SQLException {
        PersistRepositoryConfiguration repositoryConfig = param.getModeConfiguration().getRepository();
        StandalonePersistRepository repository = TypedSPILoader.getService(
                StandalonePersistRepository.class, null == repositoryConfig ? null : repositoryConfig.getType(), null == repositoryConfig ? new Properties() : repositoryConfig.getProps());
        MetaDataPersistService persistService = new MetaDataPersistService(repository);
        persistConfigurations(persistService, param);
        InstanceContext instanceContext = buildInstanceContext(param);
        new ProcessStandaloneSubscriber(instanceContext.getEventBusContext());
        //此处创建一个
        MetaDataContexts metaDataContexts = MetaDataContextsFactory.create(persistService, param, instanceContext);
        ContextManager result = new ContextManager(metaDataContexts, instanceContext);
        setContextManagerAware(result);
        return result;
    }

}
class MetaDataContextsFactory{
    
    public static MetaDataContexts create(final MetaDataPersistService persistService, final ContextManagerBuilderParameter param,
                                          final InstanceContext instanceContext, final Map<String, StorageNodeDataSource> storageNodes) throws SQLException {
        Collection<String> databaseNames = instanceContext.getInstance().getMetaData() instanceof JDBCInstanceMetaData
                ? param.getDatabaseConfigs().keySet()
                : persistService.getDatabaseMetaDataService().loadAllDatabaseNames();
        Map<String, DatabaseConfiguration> effectiveDatabaseConfigs = createEffectiveDatabaseConfigurations(databaseNames, param.getDatabaseConfigs(), persistService);
        checkDataSourceStates(effectiveDatabaseConfigs, storageNodes, param.isForce());
        //加载全局规则
        Collection<RuleConfiguration> globalRuleConfigs = persistService.getGlobalRuleService().load();
        ConfigurationProperties props = new ConfigurationProperties(persistService.getPropsService().load());
        // TODO Distinguish load calls ExternalMetaDataFactory or InternalMetaDataFactory
        Map<String, ShardingSphereDatabase> databases = ExternalMetaDataFactory.create(effectiveDatabaseConfigs, props, instanceContext);
        databases.putAll(reloadDatabases(databases, persistService));
        //获取全局的规则meta数据
        //GlobalRulesBuilder.buildRules(globalRuleConfigs, databases, props) 加载了transaction
        ShardingSphereRuleMetaData globalMetaData = new ShardingSphereRuleMetaData(GlobalRulesBuilder.buildRules(globalRuleConfigs, databases, props));
        return new MetaDataContexts(persistService, new ShardingSphereMetaData(databases, globalMetaData, props));
    }
}

class GlobalRulesBuilder{
    
    public static Collection<ShardingSphereRule> buildRules(final Collection<RuleConfiguration> globalRuleConfigs,
    final Map<String, ShardingSphereDatabase> databases, final ConfigurationProperties props) {
        Collection<ShardingSphereRule> result = new LinkedList<>();
        for (Entry<RuleConfiguration, GlobalRuleBuilder> entry : getRuleBuilderMap(globalRuleConfigs).entrySet()) {
            result.add(entry.getValue().build(entry.getKey(), databases, props));
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    private static Map<RuleConfiguration, GlobalRuleBuilder> getRuleBuilderMap(final Collection<RuleConfiguration> globalRuleConfigs) {
        Map<RuleConfiguration, GlobalRuleBuilder> result = new LinkedHashMap<>();
        result.putAll(OrderedSPILoader.getServices(GlobalRuleBuilder.class, globalRuleConfigs));
        //如果未配置相关的属性，会使用默认的的builder
        //默认的全局事务为LOCAL
        //org.apache.shardingsphere.transaction.rule.builder.DefaultTransactionRuleConfigurationBuilder
        result.putAll(getMissedDefaultRuleBuilderMap(result));
        return result;
    }
}

//如果配置了全局事务，使用定义的事务。如果未定义，使用默认的的builder
//getMissedDefaultRuleBuilderMap
class TransactionRuleBuilder{
    @Override
    public TransactionRule build(final TransactionRuleConfiguration ruleConfig, final Map<String, ShardingSphereDatabase> databases, final ConfigurationProperties props) {
        //创建事务规则
        return new TransactionRule(ruleConfig, databases);
    }
}

class TransactionRule{
    
    public TransactionRule(final TransactionRuleConfiguration ruleConfig, final Map<String, ShardingSphereDatabase> databases) {
        configuration = ruleConfig;
        //如果不配置，默认为LOCAL
        defaultType = TransactionType.valueOf(ruleConfig.getDefaultType().toUpperCase());
        providerType = ruleConfig.getProviderType();
        props = ruleConfig.getProps();
        this.databases = new ConcurrentHashMap<>(databases);
        //创建事务引擎
        resource = createTransactionManagerEngine(this.databases);
    }

    private synchronized ShardingSphereTransactionManagerEngine createTransactionManagerEngine(final Map<String, ShardingSphereDatabase> databases) {
        if (databases.isEmpty()) {
            return new ShardingSphereTransactionManagerEngine();
        }
        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>(databases.size(), 1);
        Map<String, DatabaseType> databaseTypes = new LinkedHashMap<>(databases.size(), 1);
        for (Entry<String, ShardingSphereDatabase> entry : databases.entrySet()) {
            ShardingSphereDatabase database = entry.getValue();
            database.getResourceMetaData().getDataSources().forEach((key, value) -> dataSourceMap.put(database.getName() + "." + key, value));
            database.getResourceMetaData().getStorageTypes().forEach((key, value) -> databaseTypes.put(database.getName() + "." + key, value));
        }
        if (dataSourceMap.isEmpty()) {
            return new ShardingSphereTransactionManagerEngine();
        }
        //创建事务引擎
        ShardingSphereTransactionManagerEngine result = new ShardingSphereTransactionManagerEngine();
        result.init(databaseTypes, dataSourceMap, providerType);
        return result;
    }
}

class ShardingSphereTransactionManagerEngine{
    public ShardingSphereTransactionManagerEngine() {
        //load manager
        loadTransactionManager();
    }

    private void loadTransactionManager() {
        //此处加载ShardingSphereTransactionManager的实例
        for (ShardingSphereTransactionManager each : ShardingSphereServiceLoader.getServiceInstances(ShardingSphereTransactionManager.class)) {
            if (transactionManagers.containsKey(each.getTransactionType())) {
                log.warn("Find more than one {} transaction manager implementation class, use `{}` now",
                        each.getTransactionType(), transactionManagers.get(each.getTransactionType()).getClass().getName());
                continue;
            }
            transactionManagers.put(each.getTransactionType(), each);
        }
    }
}
//org.apache.shardingsphere.transaction.base.seata.at.SeataATShardingSphereTransactionManager
class SeataATShardingSphereTransactionManager{
    
    public SeataATShardingSphereTransactionManager() {
        FileConfiguration config = new FileConfiguration("seata.conf");
        enableSeataAT = config.getBoolean("sharding.transaction.seata.at.enable", true);
        applicationId = config.getConfig("client.application.id");
        transactionServiceGroup = config.getConfig("client.transaction.service.group", "default");
        globalTXTimeout = config.getInt("sharding.transaction.seata.tx.timeout", 60);
    }

    @Override
    public void init(final Map<String, DatabaseType> databaseTypes, final Map<String, DataSource> dataSources, final String providerType) {
        if (enableSeataAT) {
            initSeataRPCClient();
            dataSources.forEach((key, value) -> dataSourceMap.put(key, new DataSourceProxy(value)));
        }
    }

    /**
     * 如果项目未配置 registry.conf, 在初始化的时候会默认使用 seata-config-core-1.5.2.jar!registry.conf
     * SEATA-SERVER 的配置相关信息
     */
    private void initSeataRPCClient() {
        ShardingSpherePreconditions.checkNotNull(applicationId, () -> new SeataATConfigurationException("Please config application id within seata.conf file"));
        //初始化TM
        TMClient.init(applicationId, transactionServiceGroup);
        //初始化RM
        RMClient.init(applicationId, transactionServiceGroup);
    }

    /**
     * 开启事务，向seata-server 注册分支
     * @param timeout
     */
    @Override
    @SneakyThrows(TransactionException.class)
    public void begin(final int timeout) {
        ShardingSpherePreconditions.checkState(timeout >= 0, TransactionTimeoutException::new);
        checkSeataATEnabled();
        GlobalTransaction globalTransaction = GlobalTransactionContext.getCurrentOrCreate();
        globalTransaction.begin(timeout * 1000);
        SeataTransactionHolder.set(globalTransaction);
    }

    /**
     * 提交事务，通知seata-server分支
     * @param rollbackOnly
     */
    @Override
    @SneakyThrows(TransactionException.class)
    public void commit(final boolean rollbackOnly) {
        checkSeataATEnabled();
        try {
            SeataTransactionHolder.get().commit();
        } finally {
            SeataTransactionHolder.clear();
            RootContext.unbind();
            SeataXIDContext.remove();
        }
    }

    /**
     * 回滚本地分支，通知seata-server分支
     */
    @Override
    @SneakyThrows(TransactionException.class)
    public void rollback() {
        checkSeataATEnabled();
        try {
            SeataTransactionHolder.get().rollback();
        } finally {
            SeataTransactionHolder.clear();
            RootContext.unbind();
            SeataXIDContext.remove();
        }
    }
}
```

最终`Connection`由`org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection`代理。
连接管理类`org.apache.shardingsphere.driver.jdbc.core.connection.ConnectionManager`。
```java
class ConnectionManager {

    public ConnectionManager(final String databaseName, final ContextManager contextManager) {
        dataSourceMap.putAll(contextManager.getDataSourceMap(databaseName));
        dataSourceMap.putAll(getTrafficDataSourceMap(databaseName, contextManager));
        physicalDataSourceMap.putAll(contextManager.getDataSourceMap(databaseName));
        //创建事务管理器
        connectionTransaction = createConnectionTransaction(databaseName, contextManager);
        connectionContext = new ConnectionContext(this::getDataSourceNamesOfCachedConnections);
    }


    private ConnectionTransaction createConnectionTransaction(final String databaseName, final ContextManager contextManager) {
        TransactionType type = TransactionTypeHolder.get();
        //获取配置的规则
        TransactionRule rule = contextManager.getMetaDataContexts().getMetaData().getGlobalRuleMetaData().getSingleRule(TransactionRule.class);
        return null == type ? new ConnectionTransaction(databaseName, rule) : new ConnectionTransaction(databaseName, type, rule);
    }

}
```
获取`connection`的时候，绑定相应的事务管理器。

`sharding sphere`使用多处使用 **SPI** 加载。
类`org.apache.shardingsphere.infra.util.spi.ShardingSphereServiceLoader` 加载服务。
- `@SingletonSPI`注解标记的类

seata事务的处理，在`org.apache.shardingsphere.infra.executor.sql.execute.engine.driver.jdbc.JDBCExecutorCallback`添加钩子方法，绑定seata的全局事务TXID

```java
class JDBCExecutorCallback{
    private T execute(final JDBCExecutionUnit jdbcExecutionUnit, final boolean isTrunkThread) throws SQLException {
        SQLExecutorExceptionHandler.setExceptionThrown(isExceptionThrown);
        DatabaseType storageType = storageTypes.get(jdbcExecutionUnit.getExecutionUnit().getDataSourceName());
        DataSourceMetaData dataSourceMetaData = getDataSourceMetaData(jdbcExecutionUnit.getStorageResource().getConnection().getMetaData(), storageType);
        //加载厂商的方法
        SQLExecutionHook sqlExecutionHook = new SPISQLExecutionHook();
        try {
            SQLUnit sqlUnit = jdbcExecutionUnit.getExecutionUnit().getSqlUnit();
            //sql执行前
            sqlExecutionHook.start(jdbcExecutionUnit.getExecutionUnit().getDataSourceName(), sqlUnit.getSql(), sqlUnit.getParameters(), dataSourceMetaData, isTrunkThread);
            T result = executeSQL(sqlUnit.getSql(), jdbcExecutionUnit.getStorageResource(), jdbcExecutionUnit.getConnectionMode(), storageType);
            //sql执行后
            sqlExecutionHook.finishSuccess();
            finishReport(jdbcExecutionUnit);
            return result;
        } catch (final SQLException ex) {
            if (!storageType.equals(protocolType)) {
                Optional<T> saneResult = getSaneResult(sqlStatement, ex);
                if (saneResult.isPresent()) {
                    return isTrunkThread ? saneResult.get() : null;
                }
            }
            sqlExecutionHook.finishFailure(ex);
            SQLExecutorExceptionHandler.handleException(ex);
            return null;
        }
    }
}

class SQLExecutionHook{
    //加载SPI
    // org.apache.shardingsphere.transaction.base.seata.at.SeataTransactionalSQLExecutionHook
    private final Collection<SQLExecutionHook> sqlExecutionHooks = ShardingSphereServiceLoader.getServiceInstances(SQLExecutionHook.class);
}
```

使用事务，需要开启`@Transaction`，使用seata的事务注解`@globalTransaction`不会生效。