package com.sinogale.dksdkdemo.controller;

import com.sinogale.dksdkdemo.dto.request.*;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.service.DataWorksService;
import com.sinogale.dksdkdemo.service.DataWorksLogService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;

@Api(tags = "DataWorks 脚本管理")
@ApiResponses({
        @io.swagger.annotations.ApiResponse(code = 400, message = "请求参数、日期格式或 JSON 错误"),
        @io.swagger.annotations.ApiResponse(code = 404, message = "未找到完全匹配的节点"),
        @io.swagger.annotations.ApiResponse(code = 409, message = "文件归属、发布包环境冲突或节点名称不唯一"),
        @io.swagger.annotations.ApiResponse(code = 422, message = "节点搜索超过分页上限"),
        @io.swagger.annotations.ApiResponse(code = 502, message = "DataWorks 调用失败或返回异常数据"),
        @io.swagger.annotations.ApiResponse(code = 503, message = "DataWorks 未配置或请求中断"),
        @io.swagger.annotations.ApiResponse(code = 504, message = "调用超时；写操作请先核对云端结果")
})
@RestController
@RequestMapping("/api/dataworks")
public class DataWorksController {
    private final DataWorksService service;
    private final DataWorksLogService logs;
    public DataWorksController(DataWorksService service, DataWorksLogService logs) {
        this.service = service; this.logs = logs;
    }
    @PostMapping("/files/create")
    @ApiOperation(value = "创建脚本", notes = "仅创建文件，返回 fileId；不会自动提交、发布或运行。")
    public ApiResponse<CreateScriptResponse> create(
            @ApiParam(value = "脚本内容及调度配置", required = true) @Valid @RequestBody CreateScriptRequest request) {
        return ApiResponse.ok(service.create(request));
    }
    @PostMapping("/files/submit")
    @ApiOperation(value = "提交脚本", notes = "返回提交包 deploymentId，SUBMIT_ACCEPTED 表示已受理，尚不代表提交完成。")
    public ApiResponse<SubmitScriptResponse> submit(
            @ApiParam(value = "文件 ID 及提交说明", required = true) @Valid @RequestBody SubmitScriptRequest request) {
        return ApiResponse.ok(service.submit(request));
    }
    @PostMapping("/files/deploy")
    @ApiOperation(value = "检查状态并发布脚本", notes = "checkOnly=true 时只查询发布包；"
            + "实际发布要求开发环境提交包 status=1、checkingStatus 为空且包含该 fileId。"
            + "WAITING 表示稍后重查；PUBLISH_ACCEPTED 后使用新的 publishDeploymentId，"
            + "设置 checkOnly=true 查询最终状态，不要反复发布。")
    public ApiResponse<DeployScriptResponse> deploy(
            @ApiParam(value = "提交包 ID、文件 ID 或只读查询标志", required = true) @Valid @RequestBody DeployScriptRequest request) {
        return ApiResponse.ok(service.deploy(request));
    }
    @PostMapping("/logs/query")
    @ApiOperation(value = "查询运行日志", notes = "taskId、fileName、instanceId 三选一。"
            + "按任务查询必须提供 bizdate；按实例查询可选 runNumber。按名称查询仅接受唯一精确匹配。"
            + "hasMore=true 时用 nextPageNumber 续读；complete=false 表示部分日志读取失败。")
    public ApiResponse<QueryLogsResponse> logs(
            @ApiParam(value = "查询目标、业务日期及分页设置", required = true) @Valid @RequestBody QueryLogsRequest request) {
        return ApiResponse.ok(logs.query(request));
    }
}
