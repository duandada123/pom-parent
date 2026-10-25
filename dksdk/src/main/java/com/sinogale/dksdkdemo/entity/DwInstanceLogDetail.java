package com.sinogale.dksdkdemo.entity;

import lombok.*;
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
public class DwInstanceLogDetail {

	/**
	 * 实例任务开始运行的时间
	 */
	private String beginRunningTime;

	/**
	 * 实例任务开始等待资源的时间
	 */
	private String beginWaitResTime;

	/**
	 * 实例任务开始等待调度的时间
	 */
	private String beginWaitTimeTime;

	/**
	 * 业务日期。通常为该任务运行时间的前一天
	 */
	private String bizdate;

	/**
	 * 实例任务的定时时间
	 */
	private String cycTime;

	/**
	 * 工作流的ID
	 */
	private Long dagId;

	/**
	 * 工作流的类型
	 */
	private String dagType;

	/**
	 * DQC类型
	 */
	private Integer dqcType;

	/**
	 * 信息
	 */
	private String errorMessage;

	/**
	 * 实例任务运行的结束时间
	 */
	private String finishTime;

	/**
	 * 实例的ID
	 */
	private Long instanceId;

	/**
	 * 节点的ID
	 */
	private Long nodeId;

	/**
	 * 节点的名称
	 */
	private String nodeName;

	/**
	 * 参数信息
	 */
	private String paramValues;

	/**
	 * 任务的状态
	 */
	private String status;


	private String logContent;

}
