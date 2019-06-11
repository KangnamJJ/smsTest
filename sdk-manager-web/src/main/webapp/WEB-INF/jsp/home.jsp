<%@page import="com.spier.common.bean.db.ScriptInfo"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.spier.common.bean.db.task.TaskInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>SDK管理后台</title>
	
	<script type="text/javascript">
		function navigateToChannels() {
			location.href='channels';
		}
		
		function navigateToScripts() {
			location.href='scripts/home';
		}
		
		function navigateToNormalFiles() {
			location.href='files/home';
		}
		
		function navigateToTasks() {
			location.href='tasks/home';
		}
		
		function navigateToSpot() {
			location.href='spot';
		}
		
		function navigateToSyncTask() {
			location.href = 'synctask';
		}
		
		function navigateToActive() {
			location.href = 'statistics'
		}
	</script>
</head>
<body>
	<%
	List<TaskInfo> tasks = (List<TaskInfo>) request.getAttribute("tasks");
	if(null == tasks) {
		tasks = new ArrayList<TaskInfo>();
	}
	
	Map<Integer, String> taskStateValues = (Map<Integer, String>) request.getAttribute("statesValue");
	if(null == taskStateValues) {
		taskStateValues = new HashMap<Integer, String>();
	}
	
	Map<Integer, String> netEnvValues = (Map<Integer, String>) request.getAttribute("netEnvValue");
	if(null == netEnvValues) {
		netEnvValues = new HashMap<Integer, String>();
	}
	
	Map<String, ScriptInfo> scripts = (Map<String, ScriptInfo>) request.getAttribute("scripts");
	if(null == scripts) {
		scripts = new HashMap<String, ScriptInfo>();
	}
	Map<Integer, String> testTaskSels = (Map<Integer, String>) request.getAttribute("testtask");
	if(null == testTaskSels) {
		testTaskSels = new HashMap<Integer, String>();
	}
	List<String> payoutTypes = (List<String>) request.getAttribute("payouttypes");
	if(null == payoutTypes) {
		payoutTypes = new ArrayList<String>();
	}
	%>
	<p>${msg }</p>
	<button id="bt_channels" onclick="navigateToChannels()">渠道管理</button>
	<button id="bt_scripts" onclick="navigateToScripts()">脚本管理</button>
	<button id="bt_normal_files" onclick="navigateToNormalFiles()">文件管理</button>
	<button id="bt_normal_files" onclick="navigateToTasks()">任务管理</button>
	<button id="bt_normal_files" onclick="navigateToSpot()">客户端埋点</button>
	<button id="bt_sync_task_edit" onclick="navigateToSyncTask()">任务同步设置</button>
	<button id="bt_active" onclick="navigateToActive()">激活留存</button>
	
	<p></p>
	
	<!-- 任务列表 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>序号</th>
			<th>任务ID</th>
			<th>结算类型</th>
			<th>任务描述</th>
			<th>是否测试</th>
			<th>任务状态</th>
			<th>国家</th>
			<th>运营商</th>
			<th>网络状态</th>
			<th>脚本ID</th>
			<th>任务总数</th>
			<th>已完成</th>
			<th>创建时间</th>
		</tr>
		
		<!-- 表内容 -->
		<%
		for(TaskInfo task : tasks) {
			if(null == task) {
				continue;
			}
			
			String stateStr = taskStateValues.get(task.getTaskState());
			if(null == stateStr) {
				stateStr = "未定义";
			}
			
			String netEnvStr = "";
			ScriptInfo script = scripts.get(task.getScriptId());
			if(null != script) {
				netEnvStr = netEnvValues.get(script.getNetEnv());
			}
			
			String country = "";
			if(null != script) {
				country = script.getCountryAbb();
			}
			
			String op = "";
			if(null != script) {
				op = script.getOperator();
			}
			
			String testTask = testTaskSels.get(task.isTaskForTest());
			if(null == testTask) {
				testTask = "";
			}
			%>
			<tr>
				<!-- 索引号 -->
				<td><%=task.getInd() %></td>
				<!-- 任务ID -->
				<td><%=task.getTaskId() %></td>
				<!-- 任务类型 -->
				<td><%=task.getTaskType() %>
				<!-- 任务描述 -->
				<td><%=task.getTaskDesc() %></td>
				<!-- 是否测试任务 -->
				<td><%=testTask %></td>
				<!-- 任务状态 -->
				<td><%=stateStr %></td>
				<!-- 国家 -->
				<td><%=country %></td>
				<!-- 运营商 -->
				<td><%=op %></td>
				<!-- 网络状态 -->
				<td><%=netEnvStr %></td>
				<!-- 脚本ID -->
				<td><%=task.getScriptId() %></td>
				<!-- 任务总数 -->
				<td><%=task.getTaskTotalCount() %></td>
				<!-- 完成数 -->
				<td><%=task.getTaskFinishedCount() %></td>
				<!-- 创建时间 -->
				<td><%=task.getCreateTime() %></td>
			</tr>
			<%
		}
		%>
	</table>
	
</body>
</html>