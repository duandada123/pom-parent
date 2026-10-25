# dksdk

> 当前提供纯 Service 调用入口 `DataWorksScriptService`，不再提供 Controller 或 Swagger 页面。
> Java 8、无公司 parent、普通 JAR；调用方式和测试入口以 [Service 使用说明](docs/Service使用说明.md) 为准。
> 以下早期 HTTP 接口、启动打包及 SDK 版本说明仅作历史参考，不适用于当前交付方式。

> 当前代码使用公司 SDK `com.aliyun.dataworks:dataworks_public_20240902:4.2.5`。
> 本次局部修正见 [优化说明](docs/优化说明.md)，保留原方法、接口参数和全量分页逻辑。下文是早期 demo 文档，部分内容与当前代码有差异。

Java 8 / Spring Boot 2.1.14，使用指定依赖 `com.aliyun:alibabacloud-dataworks_public20240518:9.0.11`。
实际目录为 `D:\work\ph\pom-parent\dksdk`，父 POM 路径为 `../pom.xml`。
下面示例中的项目、目录、文件和实例 ID 都是占位示例，需替换成实际值。

## Java 8 与 Lombok

使用 JDK 8 编译和运行，Maven source/target 保持 1.8。
Lombok 版本继承父 POM 管理的 1.18.12，编译期生效，最终可执行 JAR 排除 Lombok。
DTO 和配置类使用 @Getter/@Setter；不可变响应 ApiResponse 和异常类只使用 @Getter。
boolean 属性由 Lombok 生成 isXxx()，JSON 字段名及 Swagger 文档保持不变。
含业务逻辑的校验方法、客户端初始化方法仍保留。

内网构建需在 Maven 仓库中准备上述依赖；使用 IDEA 时需启用注解处理。

## SDK 版本与参考代码的区别

参考 Blink 程序使用 `com.aliyun.dataworks_20240902.Client`；本项目使用
`com.aliyun.sdk.service.dataworks_public20240518.AsyncClient`，均使用强类型请求，不采用反射执行任意 action。

| 原调用思路 | 本 SDK 实际调用 |
| --- | --- |
| CreateFile | CreateFile |
| SubmitFile | SubmitFile |
| GetDeployment | GetDeploymentPackage |
| DeployFile | DeployFile |
| ListNodes | ListNodes |
| ListInstances | ListTaskInstances |
| GetInstanceLog | GetTaskInstanceLog |

ListNodes 返回的 `id` 是数据开发节点 ID（String），`taskId` 是调度任务 ID（Long）。
查询实例使用后者，不把 `nodes.get(0).getId()` 当作任务 ID。

## 配置与启动

在运行环境设置：

- `ALIBABA_CLOUD_ACCESS_KEY_ID`、`ALIBABA_CLOUD_ACCESS_KEY_SECRET`：凭据。
- `ALIBABA_CLOUD_SECURITY_TOKEN`：使用临时凭据时设置。
- `DATAWORKS_REGION_ID`：默认 cn-hangzhou；私有化环境按实际区域填写。
- `DATAWORKS_ENDPOINT`：可选，自定义 endpoint 主机名，不带协议/路径；默认由 SDK 解析。
- `DATAWORKS_PROJECT_ID`：默认项目 ID；也可以在每次请求传 projectId。
- `SERVER_PORT`：默认 8080。

```powershell
cd D:\work\ph\pom-parent\dksdk
mvn test
mvn package -DskipTests
java -jar target\dksdk-1.0.0-SNAPSHOT.jar
```

客户端延迟初始化，缺少凭据也能启动应用；首次调用返回 503 DATAWORKS_NOT_CONFIGURED。
SDK 单例复用并在关闭应用时释放资源；HTTPS，连接超时 5 秒、单次调用等待上限默认 20 秒。
SDK 自动重试关闭，创建/提交/发布超时可能已经在云端执行，不要直接重复提交。
未复制参考文件里的账号、密钥、固定节点名，也未实际请求该私有化服务。
测试通过代表本地流程验证通过，私有化端点是否支持 2024-05-18 版本仍需在目标环境联调确认。

## Swagger 接口文档

已引入 springfox-swagger2 和 springfox-swagger-ui，版本统一继承父 POM 的 2.9.2。
启动应用后访问：

- Swagger UI：http://localhost:8080/swagger-ui.html
- Swagger JSON：http://localhost:8080/v2/api-docs

如果修改 SERVER_PORT，请相应替换端口。文档无需配置 DataWorks 凭据即可查看。
Controller 使用 @Api、@ApiOperation、@ApiParam、@ApiResponses；
请求/响应对象使用 @ApiModel、@ApiModelProperty，包含中文字段说明、示例、必填及枚举值。
条件必填及三选一约束见字段和接口说明，业务校验辅助属性不会显示在文档中。

docs/swagger.json 是本次从运行中的 /v2/api-docs 导出的 Swagger 2.0 文档，
可导入 Postman/Apifox；已去掉临时验证端口的 host，导入后设置实际服务地址。
后续调整注解会自动更新在线文档，静态 JSON 需要重新导出。

## 四个接口

均为 POST，Content-Type: application/json。请求与响应对象位于 dto 和 dto/response。
接口类为 controller/DataWorksController.java，文件编排与日志查询分别在 service 下，
SDK 调用集中在 sdk/DataWorksGateway.java，客户端与外部配置位于 config 下。

| 接口 | 必要输入 | 核心输出 |
| --- | --- | --- |
| /api/dataworks/files/create | fileName、fileType、fileFolderPath、content | fileId |
| /api/dataworks/files/submit | fileId | deploymentId（提交包） |
| /api/dataworks/files/deploy | deploymentId；实际发布还需 fileId | state、status、checkingStatus、publishDeploymentId |
| /api/dataworks/logs/query | taskId / fileName / instanceId 三选一 | logs、totalCount、hasMore、nextPageNumber |

projectId 可使用全局默认值。直接按 instanceId 查询日志不需要 projectId、bizdate。
未知 JSON 字段、无效 ID、日期及分页参数会返回 HTTP 400，避免拼错参数后查询范围意外扩大。
顶层 success 仅表示本次接口是否正常处理；提交/发布是否真正完成须查看 data 内的状态。

### 1. 创建脚本

```json
{
  "projectId": 10000,
  "fileName": "python_demo",
  "fileType": 1221,
  "fileFolderPath": "业务流程/demo/MaxCompute",
  "content": "print('hello DataWorks')",
  "fileDescription": "SDK 示例脚本",
  "paraValue": "bizdate=$bizdate",
  "cronExpress": "00 30 05 * * ?",
  "cycleType": "DAY",
  "autoRerunTimes": 0,
  "autoRerunIntervalMillis": 0
}
```

fileType 根据工作空间支持的节点类型选择，1221 沿用参考中的 Python 示例。
可选字段还有 connectionName、inputList、outputList、owner、resourceGroupId、
resourceGroupIdentifier、createFolderIfNotExists（默认 false）、autoParsing。
输入/输出依赖、数据源及调度资源组需要按实际任务配置。
返回 data.fileId。接口只创建文件，不自动提交、发布或运行。

参考 OdpsSqlPrecheck 包含针对稽查业务的分区字段及 SQL 黑名单。本项目支持原始 SQL/Python 内容，
未自动套用这些业务规则；如果接入特定稽查流程，可在 create 服务调用 SDK 之前增加领域校验。

### 2. 提交脚本

```json
{"projectId":10000,"fileId":1000001,"comment":"提交示例"}
```

返回 data.deploymentId，state=SUBMIT_ACCEPTED；表示提交请求已受理。
保留发布前检查，不设置 skipAllDeployFileExtensions=true。

### 3. 查看提交状态并发布

```json
{"projectId":10000,"fileId":1000001,"deploymentId":3000001,"comment":"发布示例"}
```

deploymentId 必须来自该文件的提交请求。服务先查询提交包：

- WAITING：提交/检查未完成；可稍后再调用本接口。
- SUBMIT_FAILED：提交失败；根据返回 requestId/deploymentId 排查。
- CHECK_FAILED：发布前检查失败。
- PUBLISH_ACCEPTED：发布已受理，返回新的 publishDeploymentId；尚不代表发布完成。

只有提交包目标环境为开发环境、status=1、checkingStatus 为空，
且包内文件明细包含此 fileId，才执行 DeployFile。
FileId 与 NodeId 二选一即可，因此这个发布接口只使用 fileId，无需先查节点。
收到 PUBLISH_ACCEPTED 后不要继续用原提交包反复调用发布接口；改为以下只读查询：

```json
{"projectId":10000,"deploymentId":4000001,"checkOnly":true}
```

将 4000001 换成 publishDeploymentId。返回 state=STATUS_ONLY：
status=0 就绪，1 成功，2 失败；toEnvironment=1 开发环境，2 生产环境。
checkOnly=true 也可以查询提交包；不会调用 DeployFile。

本示例没有持久化幂等记录或跨进程锁。调用方应保存 fileId、提交 deploymentId、发布 publishDeploymentId，
避免并发重复发布，也不要使用旧提交包发布后来修改的文件；生产化可增加持久化请求去重与版本约束。
发布本身不等于运行；日志仅在调度、冒烟测试或手动运行产生实例后才存在。

### 4. 查看日志

最快路径（已有实例 ID）：

```json
{"instanceId":9000001,"runNumber":1}
```

runNumber 可选，不传由云端返回该实例最近一次运行的日志。

已有调度任务 ID，直接查实例并读取日志：

```json
{
  "projectId":10000,
  "taskId":2000001,
  "bizdate":"2026-10-21",
  "projectEnv":"Prod",
  "pageNumber":1,
  "pageSize":20,
  "maxPages":1
}
```

兼容旧代码中调度节点的叫法：请求 `nodeId` 是 `taskId` 的别名，
仅适用于已有的**调度任务 ID**，不能填写新版 ListNodes 的数据开发节点 id。

只有文件名时：

```json
{
  "projectId":10000,
  "fileName":"python_demo",
  "bizdate":"2026-10-21",
  "projectEnv":"Prod",
  "pageNumber":1,
  "pageSize":20,
  "maxPages":2
}
```

先分页 ListNodes，过滤名称完全相等的节点；发现多个同名节点返回 409 AMBIGUOUS_NODE。
可传 containerId、scene 缩小范围，或改用 taskId。最多检查配置中的 max-node-pages 页，
到上限仍不能确认唯一性时明确报错；不选择不确定的第一个节点。
节点匹配后从 taskId 查询实例；若尚无 taskId，返回 NODE_HAS_NO_TASK。

bizdate 必填（按 instanceId 查询除外），按 dataworks.bizdate-zone（默认 Asia/Shanghai）
转换成当天零点的毫秒时间戳；不会隐式查询所有历史记录。
Prod/Dev 及旧写法 PROD/DEV 均接受。实例按 Id Desc 查询。
pageSize=1..100，maxPages=1..5，默认只读一页；需要批量时增加 maxPages。
第一页不会为了统计总数再请求一次。同一 instanceId 的不同 runNumber 分别保留。

返回：

```json
{
  "success":true,
  "code":"OK",
  "message":"请求处理成功，业务状态请查看 data",
  "requestId":null,
  "data":{
    "taskId":2000001,
    "nodeId":null,
    "totalCount":30,
    "pagesFetched":1,
    "nextPageNumber":2,
    "hasMore":true,
    "complete":true,
    "logs":[{
      "instanceId":9000001,
      "taskId":2000001,
      "taskName":"python_demo",
      "runNumber":1,
      "status":"Success",
      "bizdate":1792512000000,
      "startedTime":null,
      "finishedTime":null,
      "logContent":"...",
      "logSuccess":true,
      "errorCode":null,
      "errorMessage":null,
      "requestId":"aliyun-request-id"
    }]
  }
}
```

hasMore=true 时，使用相同筛选条件及 pageSize，下一次传 nextPageNumber。
complete 表示本次返回的日志是否全部读取成功，不表示已读完所有页。
聚合中某条日志失败会保留该条的 errorCode/requestId，complete=false，其他日志照常返回；
可根据失败条目的 instanceId/runNumber 重查。实例列表或节点查询失败则整个请求报错。
聚合时间预算为 60 秒，在每次下游请求前检查，最多再加一次正在进行的 SDK 调用耗时。
超过预算返回 504，请减少每页条数或直接查指定实例。
这是在线分页，云端实例列表变化时可能需要重查，不保证跨请求的快照一致性。

## 验证范围

单元测试使用 SDK 真实模型与 mock client/gateway，覆盖发布状态、文件归属、检查中/检查失败、
分页续读、同名节点、taskId 映射、重跑日志、部分失败、超时取消及中断。
MVC 测试覆盖请求校验和 JSON 绑定；启动测试验证 Spring 配置可装配。
凭据构建测试只构造和关闭 SDK 客户端，不发送请求。不会自动创建、提交或发布任何云端文件。

### Spring Boot 真实环境测试

`DksdkDemoApplicationTests` 使用 `@SpringBootTest(webEnvironment = NONE)` 启动完整 Spring 容器，
自动注入 `DataWorksController`，可直接运行创建、提交、状态查询、发布和日志查询方法。
默认执行 `mvn test` 时，所有真实云端调用都会跳过；必须显式开启，防止误操作云端资源。

先配置凭据和项目：

```powershell
$env:ALIBABA_CLOUD_ACCESS_KEY_ID = "实际 AccessKey ID"
$env:ALIBABA_CLOUD_ACCESS_KEY_SECRET = "实际 AccessKey Secret"
$env:DATAWORKS_PROJECT_ID = "10000"
```

只读查询发布包状态：

```powershell
mvn '-Dtest=DksdkDemoApplicationTests#deploymentStatusTest' `
  '-Ddataworks.live-test.enabled=true' `
  '-Ddataworks.live-test.deployment-id=3000001' test
```

创建脚本等写操作需要额外开启 `allow-writes`：

```powershell
mvn '-Dtest=DksdkDemoApplicationTests#createScriptTest' `
  '-Ddataworks.live-test.enabled=true' `
  '-Ddataworks.live-test.allow-writes=true' test
```

提交和发布用例还需传入 `dataworks.live-test.file-id`、
`dataworks.live-test.deployment-id`。日志查询优先使用 `dataworks.live-test.instance-id`；
没有实例 ID 时传 `dataworks.live-test.task-id` 和 `dataworks.live-test.bizdate=yyyy-MM-dd`。

## 官方接口说明

- [GetDeploymentPackage](https://help.aliyun.com/zh/dataworks/developer-reference/api-dataworks-public-2024-05-18-getdeploymentpackage)
- [DeployFile](https://help.aliyun.com/zh/dataworks/developer-reference/api-dataworks-public-2024-05-18-deployfile)
- [ListNodes](https://help.aliyun.com/zh/dataworks/developer-reference/api-dataworks-public-2024-05-18-listnodes)
- [ListTaskInstances](https://help.aliyun.com/zh/dataworks/developer-reference/api-dataworks-public-2024-05-18-listtaskinstances)
- [GetTaskInstanceLog](https://help.aliyun.com/zh/dataworks/developer-reference/api-dataworks-public-2024-05-18-gettaskinstancelog)
