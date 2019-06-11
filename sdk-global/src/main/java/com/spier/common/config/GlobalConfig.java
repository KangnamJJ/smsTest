package com.spier.common.config;

/**
 * 全局配置
 * @author GHB
 * @version 1.3.8<br>
 * <br>
 * @changes：<br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.11<br>
 * 版本号：1.0.6<br>
 * 修改内容：增加了激活留存统计<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.11<br>
 * 修改内容：修改派发任务机制，一个用户一天只做一个成功的任务；
 * 一个任务一天最多失败三次，达到上限不再派发此任务；
 * 任务失败后30分钟内不再派发任务。<br>
 * 版本号：1.0.7<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.15<br>
 * 修改内容：<br>
 * 1.增加非WiFi网络情况下取任务记录国家和运营商信息。
 * 2.增加Referrer广告归因的记录、转化发送的操作。都未测试。<br>
 * 版本号：1.0.8<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.16<br>
 * 修改内容：<br>
 * 1.广告归因转化下发已测试通过，目前支持HexMobile的广告归因数据下发。<br>
 * 2.去掉了一些日志<br>
 * 版本号：1.1.0<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.25<br>
 * 修改内容：<br>
 * 1.修复了请求任务时，如果没有匹配任务，无返回内容的bug<br>
 * 2.为任务创建、任务编辑添加了结算类型字段。<br>
 * 版本号：1.1.1<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.27<br>
 * 修改内容：<br>
 * 增加了运营商名称**.com.my的模糊查询功能。<br>
 * 版本号：1.1.2
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.3.27<br>
 * 修改内容：<br>
 * 1.脚本信息表中增加列sc，表示短码。
 * 2.请求任务时，如果已经做过相同短码的任务，则不再派发。<br>
 * 版本号：1.2.0<br>
 * <br>
 * 修改人：Timo<br>
 * 修改时间：2019.4.8<br>
 * 修改内容：<br>
 * 1.新增任务定时更新任务信息功能
 * 2.修复脚本上传不了,bug，原因是由于运营商名称带有空格，导致匹配不上无法查询
 * 3.修复用户手机环境无法获取任务的Bug，原因是代码逻辑问题出错
 * 版本号：1.2.1<br>
 * <br>
 * 修改人：Timo<br>
 * 修改时间：2019.4.9<br>
 * 修改内容：<br>
 * 1.新增定时任务删除埋点和tomcat日志
 * 2.新增设置tomcat最大连接数问题
 * 3.修复获取运营商bug
 * 4：新增埋点搜索功能
 * 版本号：1.2.2<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.11<br>
 * 修改内容：<br>
 * 1.将数据库配置改回默认情况，避免多人协作带来的问题
 * 版本号：1.2.3<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.12<br>
 * 修改内容：<br>
 * 1.由于运营商数据中出现了多条重复，因此数据库查询是出现了异常，导致用户注册失败。此版本修复了此问题。
 * 版本号：1.2.4<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.12<br>
 * 修改内容：<br>
 * 1.修改了几个bug
 * 版本号：1.3.0<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.16<br>
 * 修改内容：<br>
 * 1.修复了清理任务的一个bug。当目录下没有文件时，会抛出空指针异常
 * 版本号：1.3.1<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.16<br>
 * 修改内容：<br>
 * 取任务逻辑增加了一天内3次失败限制
 * 版本号：1.3.2<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.17<br>
 * 修改内容：<br>
 * 1.修复了取任务中限定用户整个生命周期为3个失败上限的bug，改为每个任务50个失败上限<br>
 * 2.修复了取任务时，truecorp无法找到运营商的bug。<br>
 * 版本号：1.3.3<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.17<br>
 * 修改内容：<br>
 * 1.修复了TaskInfo中的空引用异常
 * 2.修复了埋点信息超长的异常。
 * 版本号：1.3.4<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.17<br>
 * 修改内容：<br>
 * 1.修复了获取测试任务时，脚本network类型是ALL，请求数据不是时不给任务的bug。
 * 版本号：1.3.5<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.18<br>
 * 修改内容：<br>
 * 1.修改用户信息服务和相关dao层，保留国家和运营商数据，避免更新而丢失。<br>
 * 2.修改用户信息服务和dao层，增加忠实用户一栏，记录用户曾经打开过权限开关的操作。<br>
 * 版本号：1.3.6<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.19<br>
 * 修改内容：<br>
 * 去掉了任务上报时，任务状态的检测，避免由客户端定义的状态值在1-2-3之外的时候，无法保存任务结果的bug。<br>
 * 版本号：1.3.7<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.19<br>
 * 修改内容：<br>
 * 修复了一天最多3次失败限制的bug，将比较时间时的after改为before。<br>
 * 版本号：1.3.8<br>
 * <br>
 */

public class GlobalConfig {

	public static final String M_VERSION = "1.3.8";
	
	/**
	 * 文件根目录
	 */
	//public static final String M_FILES_BASE_DIR = "c:\\sdk_files";
	public static final String M_FILES_BASE_DIR = "/usr/local/sdk_files";
	/**
	 * 网络环境：数据连接
	 */
	public static final int M_NET_ENV_MOBILE = 0;
	
	/**
	 * 网络环境：WiFi
	 */
	public static final int M_NET_ENV_WIFI = 1;
	/**
	 * 网络环境：所有的
	 */
	public static final int M_NET_ENV_ALL = 101;
	
	/**
	 * 网络状态类型是否有效
	 * @param env
	 * @return
	 */
	public static boolean isNetworkEnvValid(int env) {
		return (env == M_NET_ENV_MOBILE)
				|| (env == M_NET_ENV_WIFI)
				|| (env == M_NET_ENV_ALL);
	}
	
	/**
	 * 任务状态：正在运行
	 */
	public static final int M_TASK_STATE_RUNNING = 1;
	
	/**
	 * 任务状态：暂停运行
	 */
	public static final int M_TASK_STATE_PAUSE = 0;
	
	/**
	 * 任务状态是否合法
	 * @param state
	 * @return
	 */
	public static boolean isTaskStateValid(int state) {
		return (state == M_TASK_STATE_RUNNING)
				|| (state == M_TASK_STATE_PAUSE);
	}
	
	/**
	 * 正常信息埋点
	 */
	public static final int M_SPOT_TYPE_NORMAL 	= 1;
	
	/**
	 * 错误信息埋点
	 */
	public static final int M_SPOT_TYPE_ERR		= 2;
	
	/**
	 * 测试任务
	 */
	public static final int M_TASK_TYPE_TEST	= 1;
	
	/**
	 * 线上任务
	 */
	public static final int M_TASK_TYPE_ONLINE	= 0;
	
	/**
	 * 任务是否是测试任务
	 * @param type
	 * @return
	 */
	public static boolean isTaskForTest(int type) {
		return type == M_TASK_TYPE_TEST;
	}
	
	public static final String M_PAYOUT_TYPE_CPA = "CPA";
	public static final String M_PAYOUT_TYPE_CPI = "CPI";
	public static final String M_PAYOUT_TYPE_CPS = "CPS";
	public static final String M_PAYOUT_TYPE_CPC = "CPC";
	public static final String M_PAYOUT_TYPE_CPM = "CPM";
	public static final String M_PAYOUT_TYPE_CPE = "CPE";
	public static final String M_PAYOUT_TYPE_SMARTLINK = "SmartLink";
	
	/**
	 * 任务执行结果——成功：{@value}
	 */
	public static final int M_TASK_EXEC_RESULT_SUCCEED = 1;
	
	/**
	 * 任务执行结果——失败：{@value}
	 */
	public static final int M_TASK_EXEC_RESULT_FAILED = 2;
	
	/**
	 * 任务是否执行成功
	 * @param rst
	 * @return
	 */
	public static boolean isTaskExecSucceed(int rst) {
		return rst == M_TASK_EXEC_RESULT_SUCCEED;
	}
	
	/**
	 * 任务执行结果是否有效
	 * @param rst
	 * @return
	 */
	public static boolean isTaskExecResultValid(int rst) {
		return (rst == M_TASK_EXEC_RESULT_FAILED || rst == M_TASK_EXEC_RESULT_SUCCEED);
	}
	
	/**
	 * 单个任务状态——成功：{@value}
	 */
	public static final int M_TASK_RUNNING_STATE_SUCCEED = 1;
	/**
	 * 单个任务状态——失败：{@value}
	 */
	public static final int M_TASK_RUNNING_STATE_FAILED = 2;
	/**
	 * 单个任务状态——正在执行：{@value}
	 */
	public static final int M_TASK_RUNNING_STATE_RUNNING = 3;
	/**
	 * 单个任务状态——上报无效数据：{@value}
	 */
	public static final int M_TASK_RUNNING_STATE_INVALID_REPORT = 4;
	
	/**
	 * 任务运行状态是否有效
	 * @param state
	 * @return
	 */
	public static boolean isTaskRunningStateValid(int state) {
		boolean res = false;
		
		switch(state) {
			case M_TASK_RUNNING_STATE_SUCCEED:
			case M_TASK_RUNNING_STATE_FAILED:
			case M_TASK_RUNNING_STATE_RUNNING:
			case M_TASK_RUNNING_STATE_INVALID_REPORT:
				res = true;
			break;
		}
		
		return res;
	}
	
	/**
	 * 任务请求状态——成功
	 */
	public static final int M_TASK_REQUEST_STATUS_SUCCEED = 0;
}
