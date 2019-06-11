<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.spier.common.bean.db.ScriptInfo"%>
<%@page import="java.util.Map"%>
<%@page import="com.spier.common.bean.db.task.TaskInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>任务管理页面</title>
	
	<script type="text/javascript">
		function onAddClicked() {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			window.open('edit?action=add', '添加文件', 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function onEditClicked(button) {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var recordInd = button.value;
			var url = 'edit?ind=' + recordInd + '&action=edit';
			window.open(url, "编辑脚本", 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
		}
		
		function onDeleteClicked(button) {
			var recordInd = button.value;
			
			// 弹出窗口确认后再删除
			var conf = confirm("确定要删除文件索引号为" + recordInd + "的记录吗？");
			if(conf) {
				var url = "edit?action=delete&ind=" + recordInd;
				
				var iWidth=800;
				var iHeight=600;
				var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
				var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
				
				window.open(url, '删除文件',
						"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
			}
		}
	</script>
</head>

<body>
<%
List<TaskInfo> taskList = (List<TaskInfo>) request.getAttribute("taskList");
if(null == taskList) {
	taskList = new ArrayList<TaskInfo>();
}
Map<Integer, String> taskStates = (Map<Integer, String>) request.getAttribute("taskStates");
if(null == taskStates) {
	taskStates = new HashMap<Integer, String>();
}
Map<Integer, String> netStates = (Map<Integer, String>) request.getAttribute("netStates");
if(null == netStates) {
	netStates = new HashMap<Integer, String>();
}
Map<String, ScriptInfo> scripts = (Map<String, ScriptInfo>) request.getAttribute("scripts");
if(null == scripts) {
	scripts = new HashMap<String, ScriptInfo>();
}
Map<Integer, String> testSelections = (Map<Integer, String>) request.getAttribute("testSlections");
if(null == testSelections) {
	testSelections = new HashMap<Integer, String>();
}
%>
	<p>${msg}</p>
	<!-- 新增任务按钮 -->
	<button onclick="onAddClicked()">新增任务</button>
	<!-- 当前文件展示 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>序列号</th>
			<th>任务ID</th>
			<th>结算类型</th>
			<th>是否测试</th>
			<th>任务描述</th>
			<th>任务状态</th>
			<th>国家</th>
			<th>运营商</th>
			<th>网络环境</th>
			<th>任务总数</th>
			<th>任务完成数</th>
			<th>脚本ID</th>
			<th>操作</th>
		</tr>
		
		<!-- 表内容 -->
		<%
		if(null != taskList) {
			for(int i = 0; i < taskList.size(); i++) {
				String snId = "sn_" + i;
				TaskInfo task = taskList.get(i);
				if(null == task) {
					continue;
				}
				
				String scriptId = task.getScriptId();
				String taskState = taskStates.get(task.getTaskState());
				
				String networkState = "";
				ScriptInfo script = scripts.get(scriptId);
				if(null != script) {
					networkState = netStates.get(script.getNetEnv());
				}
				
				String testSels = "";
				if(null != testSelections) {
					testSels = testSelections.get(task.isTaskForTest());
					if(null == testSels) {
						testSels = "";
					}
				}
				
				String country = "";
				if(null != script) {
					country = script.getCountryAbb();
				}
				String opName = "";
				if(null != script) {
					opName = script.getOperator();
				}
				%>
				
				<tr>
					<!-- 序列号 -->
					<td><%=task.getInd() %></td>
					<!-- 任务id -->
					<td><%=task.getTaskId() %></td>
					<!-- 任务类型 -->
					<td><%=task.getTaskType() %></td>
					<!-- 是否是测试渠道 -->
					<td><%=testSels %></td>
					<!-- 任务描述 -->
					<td><%=task.getTaskDesc() %></td>
					<!-- 任务状态 -->
					<td><%=taskState == null ? "" : taskState %>
					<!-- 国家 -->
					<td><%=country %></td>
					<!-- 运营商 -->
					<td><%=opName %></td>
					<!-- 网络环境-->
					<td><%=networkState == null ? "" : networkState %></td>
					<!-- 任务总数 -->
					<td><%=task.getTaskTotalCount() %></td>
					<!-- 任务完成数 -->
					<td><%=task.getTaskFinishedCount() %></td>
					<!-- 脚本ID -->
					<td><%=scriptId == null ? "" : scriptId %></td>
					<!-- 操作按钮 -->
					<td>
						<button value=<%=task.getInd() %> onclick="onEditClicked(this)">编辑</button>
						<button value=<%=task.getInd() %> onclick="onDeleteClicked(this)">删除</button>
					</td>
				</tr>
			<% 
			}
		}
		%>
	</table>
</body>

</html>