package com.sinogale.dksdkdemo.entity;

import lombok.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;

/**
 * Description:实例运行日志表
 * @author sgcsc_hansiyu
 * Date 2026-03-11
 */

@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "DwInstanceLogDetail", description = "实例运行日志明细")
public class DwInstanceLogDetail {

	/**
	 * 实例任务开始运行的时间
	 */
	@ApiModelProperty(value = "实例开始运行时间，格式 yyyy-MM-dd HH:mm:ss；无时间时为空字符串")
	private String beginRunningTime;

	/**
	 * 实例任务开始等待资源的时间
	 */
	@ApiModelProperty(value = "实例开始等待资源时间，格式 yyyy-MM-dd HH:mm:ss；无时间时为空字符串")
	private String beginWaitResTime;

	/**
	 * 实例任务开始等待调度的时间
	 */
	@ApiModelProperty(value = "实例开始等待调度时间，格式 yyyy-MM-dd HH:mm:ss；无时间时为空字符串")
	private String beginWaitTimeTime;

	/**
	 * 业务日期。通常为该任务运行时间的前一天
	 */
	@ApiModelProperty(value = "实例业务日期，格式 yyyyMMdd")
	private String bizdate;

	/**
	 * 实例任务的定时时间
	 */
	@ApiModelProperty(value = "实例计划调度时间，格式 yyyy-MM-dd HH:mm:ss")
	private String cycTime;

	/**
	 * 工作流的ID
	 */
	@ApiModelProperty(value = "实例所属工作流 ID")
	private Long dagId;

	/**
	 * 工作流的类型
	 */
	@ApiModelProperty(value = "工作流类型；当前日志聚合逻辑未填充此字段")
	private String dagType;

	/**
	 * DQC类型
	 */
	@ApiModelProperty(value = "数据质量检查类型编码，按上游原值返回")
	private Integer dqcType;

	/**
	 * 信息
	 */
	@ApiModelProperty(value = "实例执行失败时的错误信息")
	private String errorMessage;

	/**
	 * 实例任务运行的结束时间
	 */
	@ApiModelProperty(value = "实例运行结束时间，格式 yyyy-MM-dd HH:mm:ss；未结束时为空字符串")
	private String finishTime;

	/**
	 * 实例的ID
	 */
	@ApiModelProperty(value = "任务实例 ID")
	private Long instanceId;

	/**
	 * 节点的ID
	 */
	@ApiModelProperty(value = "调度节点 ID")
	private Long nodeId;

	/**
	 * 节点的名称
	 */
	@ApiModelProperty(value = "调度节点名称")
	private String nodeName;

	/**
	 * 参数信息
	 */
	@ApiModelProperty(value = "实例运行参数")
	private String paramValues;

	/**
	 * 任务的状态
	 */
	@ApiModelProperty(value = "实例运行状态，按上游原值返回")
	private String status;


	@ApiModelProperty(value = "实例运行日志文本；尚未产生或未获取到时可能为空")
	private String logContent;

}
