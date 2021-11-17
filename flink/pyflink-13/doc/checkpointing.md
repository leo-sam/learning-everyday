# **Checkpointing [#](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/#checkpointing)**

Flink 中的每个方法或算子都能够是**有状态的**（阅读 [working with state](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/state.html) 了解更多）。 状态化的方法在处理单个 元素/事件 的时候存储数据，让状态成为使各个类型的算子更加精细的重要部分。 为了让状态容错，Flink 需要为状态添加 **checkpoint（检查点）**。Checkpoint 使得 Flink 能够恢复状态和在流中的位置，从而向应用提供和无故障执行时一样的语义。

[容错文档](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/learn-flink/fault_tolerance/) 中介绍了 Flink 流计算容错机制内部的技术原理。

# **前提条件 [#](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/#%e5%89%8d%e6%8f%90%e6%9d%a1%e4%bb%b6)**

Flink 的 checkpoint 机制会和持久化存储进行交互，读写流与状态。一般需要：

- 一个能够回放一段时间内数据的持久化数据源，例如持久化消息队列（例如 Apache Kafka、RabbitMQ、 Amazon Kinesis、 Google PubSub 等）或文件系统（例如 HDFS、 S3、 GFS、 NFS、 Ceph 等）。
- 存放状态的持久化存储，通常为分布式文件系统（比如 HDFS、 S3、 GFS、 NFS、 Ceph 等）。

# **开启与配置 Checkpoint [#](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/#%e5%bc%80%e5%90%af%e4%b8%8e%e9%85%8d%e7%bd%ae-checkpoint)**

默认情况下 checkpoint 是禁用的。通过调用 `StreamExecutionEnvironment` 的 `enableCheckpointing(n)` 来启用 checkpoint，里面的 *n* 是进行 checkpoint 的间隔，单位毫秒。

Checkpoint 其他的属性包括：

- *精确一次（exactly-once）对比至少一次（at-least-once）*：你可以选择向 `enableCheckpointing(long interval, CheckpointingMode mode)` 方法中传入一个模式来选择使用两种保证等级中的哪一种。 对于大多数应用来说，精确一次是较好的选择。至少一次可能与某些延迟超低（始终只有几毫秒）的应用的关联较大。
- *checkpoint 超时*：如果 checkpoint 执行的时间超过了该配置的阈值，还在进行中的 checkpoint 操作就会被抛弃。
- *checkpoints 之间的最小时间*：该属性定义在 checkpoint 之间需要多久的时间，以确保流应用在 checkpoint 之间有足够的进展。如果值设置为了 *5000*， 无论 checkpoint 持续时间与间隔是多久，在前一个 checkpoint 完成时的至少五秒后会才开始下一个 checkpoint。

  往往使用“checkpoints 之间的最小时间”来配置应用会比 checkpoint 间隔容易很多，因为“checkpoints 之间的最小时间”在 checkpoint 的执行时间超过平均值时不会受到影响（例如如果目标的存储系统忽然变得很慢）。

  注意这个值也意味着并发 checkpoint 的数目是*一*。

- *checkpoint 可容忍连续失败次数*：该属性定义可容忍多少次连续的 checkpoint 失败。超过这个阈值之后会触发作业错误 fail over。 默认次数为“0”，这意味着不容忍 checkpoint 失败，作业将在第一次 checkpoint 失败时fail over。
- *并发 checkpoint 的数目*: 默认情况下，在上一个 checkpoint 未完成（失败或者成功）的情况下，系统不会触发另一个 checkpoint。这确保了拓扑不会在 checkpoint 上花费太多时间，从而影响正常的处理流程。 不过允许多个 checkpoint 并行进行是可行的，对于有确定的处理延迟（例如某方法所调用比较耗时的外部服务），但是仍然想进行频繁的 checkpoint 去最小化故障后重跑的 pipelines 来说，是有意义的。

  该选项不能和 “checkpoints 间的最小时间"同时使用。

- *externalized checkpoints*: 你可以配置周期存储 checkpoint 到外部系统中。Externalized checkpoints 将他们的元数据写到持久化存储上并且在 job 失败的时候*不会*被自动删除。 这种方式下，如果你的 job 失败，你将会有一个现有的 checkpoint 去恢复。更多的细节请看 [Externalized checkpoints 的部署文档](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/ops/state/checkpoints/#externalized-checkpoints)。
- *优先从 checkpoint 恢复（prefer checkpoint for recovery）*：该属性确定 job 是否在最新的 checkpoint 回退，即使有更近的 savepoint 可用，这可以潜在地减少恢复时间（checkpoint 恢复比 savepoint 恢复更快）。

```python
env = StreamExecutionEnvironment.get_execution_environment()

# 每 1000ms 开始一次 checkpoint
env.enable_checkpointing(1000)

# 高级选项：

# 设置模式为精确一次 (这是默认值)
env.get_checkpoint_config().set_checkpointing_mode(CheckpointingMode.EXACTLY_ONCE)

# 确认 checkpoints 之间的时间会进行 500 ms
env.get_checkpoint_config().set_min_pause_between_checkpoints(500)

# Checkpoint 必须在一分钟内完成，否则就会被抛弃
env.get_checkpoint_config().set_checkpoint_timeout(60000)

# 允许两个连续的 checkpoint 错误
env.get_checkpoint_config().set_tolerable_checkpoint_failure_number(2)

# 同一时间只允许一个 checkpoint 进行
env.get_checkpoint_config().set_max_concurrent_checkpoints(1)

# 允许在有更近 savepoint 时回退到 checkpoint
env.get_checkpoint_config().set_prefer_checkpoint_for_recovery(True)

# 使用 externalized checkpoints，这样 checkpoint 在作业取消后仍就会被保留
env.get_checkpoint_config().enable_externalized_checkpoints(
    ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    
# 开启实验性的 unaligned checkpoints
env.get_checkpoint_config().enable_unaligned_checkpoints()
```
# **选择一个 State Backend [#](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/#%e9%80%89%e6%8b%a9%e4%b8%80%e4%b8%aa-state-backend)**

Flink 的 [checkpointing 机制](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/learn-flink/fault_tolerance/) 会将 timer 以及 stateful 的 operator 进行快照，然后存储下来， 包括连接器（connectors），窗口（windows）以及任何用户[自定义的状态](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/state.html)。 Checkpoint 存储在哪里取决于所配置的 **State Backend**（比如 JobManager memory、 file system、 database）。

默认情况下，状态是保持在 TaskManagers 的内存中，checkpoint 保存在 JobManager 的内存中。为了合适地持久化大体量状态， Flink 支持各种各样的途径去存储 checkpoint 状态到其他的 state backends 上。通过 `StreamExecutionEnvironment.setStateBackend(…)` 来配置所选的 state backends。

阅读 [state backends](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/ops/state/state_backends/) 来查看在 job 范围和集群范围上可用的 state backends 与选项的更多细节。

# **迭代作业中的状态和 checkpoint [#](https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/dev/datastream/fault-tolerance/checkpointing/#%e8%bf%ad%e4%bb%a3%e4%bd%9c%e4%b8%9a%e4%b8%ad%e7%9a%84%e7%8a%b6%e6%80%81%e5%92%8c-checkpoint)**

Flink 现在为没有迭代（iterations）的作业提供一致性的处理保证。在迭代作业上开启 checkpoint 会导致异常。为了在迭代程序中强制进行 checkpoint，用户需要在开启 checkpoint 时设置一个特殊的标志： `env.enableCheckpointing(interval, CheckpointingMode.EXACTLY_ONCE, force = true)`。

请注意在环形边上游走的记录（以及与之相关的状态变化）在故障时会丢失。