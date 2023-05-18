### xxl job

- 配置好参数，启动服务
- 使用 **@XxlJob** 注解，自动注册到executor
- 实现 **IJobHandler** 接口，手动注册到executor

#### 执行
执行器接收到任务时，会根据logId，获取相应的jobThread **JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());**
若果获取不到相应的thread，会重新注册一个。
主要的方法入口在 **ExecutorBizImpl#run(TriggerParam triggerParam)**

jobThread内部队列使用的是 **LinkedBlockingQueue**，队列长度为默认的 **0x7fffffff**

#### 初始化

**XxlJobExecutor#start** 入口方法。**EmbedServer** 使用netty构建服务容器。
再使用**ExecutorRegistryThread#start** 注册到调度中心
