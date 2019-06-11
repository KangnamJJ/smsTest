<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="com.spier.common.bean.db.OperatorInfo"%>
<%@page import="com.spier.common.bean.db.ScriptInfo"%>
<%@page import="com.spier.common.bean.db.CountryInfo"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="com.spier.common.bean.db.task.TaskInfo"%>
<%@page import="com.spier.common.bean.db.ChannelInfo"%>
<%@page import="java.util.List"%>
<%@page import="java.util.logging.Level"%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>任务新增页面</title>
	
	<script type="text/javascript">
		function load() {
			// 设置渠道
			Element hiddenP = window.document.getElementById("task-chan");
			var selection = hiddenP.innerText;
			Element taskChanObj = window.document.getElementById("select_chan_no");
			for(var i = 0; i < taskChanObj.options.length; i++) {
				if(taskChanObj.options[i].value == selection) {
					taskChanObj.options[i].selected = true;
				} else {
					taskChanObj.options[i].selected = false;
				}
			}
			
			// 设置任务状态
			Element hiddenP = window.document.getElementById("task-state");
			var selection = hiddenP.innerText;
			//console.log("任务状态：" + selection);
			Element taskStateObj = window.document.getElementById("select_task_state");
			for(var i = 0; i < taskStateObj.options.length; i++) {
				if(taskStateObj.options[i].value == selection) {
					taskStateObj.options[i].selected = true;
				} else {
					taskStateObj.options[i].selected = false;
				}
			}
			
			// 设置网络环境
			hiddenP = window.document.getElementById("net-env");
			selection = hiddenP.innerText;
			Element netEnvObj = window.document.getElementById("select_net_env");
			for(var i = 0; i < netEnvObj.options.length; i++) {
				if(netEnvObj.options[i].value == selection) {
					netEnvObj.options[i].selected = true;
				} else {
					netEnvObj.options[i].selected = false;
				}
			}
			
			// 设置国家
			hiddenP = window.document.getElementById("task-country");
			selection = hiddenP.innerText;
			Element countries = window.document.getElementById("select_task_country");
			for(var i = 0; i < countries.options.length; i++) {
				if(countries.options[i].value == selection) {
					countries.options[i].selected = true;
				} else {
					countries.options[i].selected = false;
				}
			}
			
			// 设置运营商
			hiddenP = window.document.getElementById("task-op");
			selection = hiddenP.innerText;
			/*
			Element ops = window.document.getElementById("select_task_op");
			for(var i = 0; i < countries.options.length; i++) {
				if(ops.options[i].value == selection) {
					ops.options[i].selected = true;
				} else {
					ops.options[i].selected = false;
				}
			} */
			// 从数据中找出有那些运营商
			var opList = getOperatorsByCountryAbb(selection);
			createOptions("select_task_op", opList);
		}
		
		function onCountryChanged(countrySelectObj) {
			document.write("进来了！");
			
			Element countriesSelections = document.getElementById("select_task_country");
			Element opsSelections = document.getElementById("select_task_op");
			// 清除运营商的选项
			opsSelections.options.length = 0;
			
			
		}
		
		function saveOperators(ops) {
			alert("进入js了！");
		}
		
		function getOperatorsByCountryAbb(abb) {
			var res = [];
			
			Element elem = document.getElementById("data_op_countres_count");
			var countriesCount = elem.innerText;
			
			for(var i = 0; i < countriesCount; i++) {
				var selectId = "select_country_" + i;
				elem = document.getElementById(selectId);
				if(abb != elem.name) {
					continue;
				}
				
				for(var j = 0; j < elem.options.length; j++) {
					res[j] = elem.options[j].innerText;
				}
				
				break;
			}
			
			return res;
		}
		
		function createOptions(selectObjId, optionsValueArray) {
			Element selectObj = document.getElementById(selectObjId);
			
			// 清空select
			selectObj.options.length = 0;
			
			// 添加
			for(var i = 0; i < optionsValueArray.length; i++) {
				var objOption = document.createElement("OPTION");
				objOption.text = optionsValueArray[i];
				objOption.value = optionsValueArray[i];
				selectObj.options.add(objOption);
			}
		}
	</script>
</head>

<body onload="load();">

<p>${msg}</p>

<!-- 存国家与运营商的映射数据 -->
<%
Map<String, List<OperatorInfo>> operatorsData = (Map<String, List<OperatorInfo>>) request.getAttribute("operators");
if(null == operatorsData || operatorsData.isEmpty()) {
	Logger.getAnonymousLogger().log(Level.SEVERE, "运营商数据为空，无法生成备份数据！");
	return;
}
int op_countries_count = operatorsData.size();
%>

<!-- 记录运营商所在国家的数量 -->
<p id="data_op_countres_count" hidden="hidden"><%=op_countries_count %></p>

<%
int count = 0;
for(Entry<String, List<OperatorInfo>> entry : operatorsData.entrySet()) {
	String country = entry.getKey();
	List<OperatorInfo> ops = entry.getValue();
	if(StringUtils.isEmpty(country)) {
		continue;
	}
	if(null == ops || ops.isEmpty()) {
		continue;
	}
	
	String selectId = "select_country_" + count;
	%>
	
	<select id=<%=selectId %> name=<%=country %> hidden="hidden">
	<%
	for(OperatorInfo op : ops) {
		if(null == op) {
			continue;
		}
		%>
		<option hidden="hidden"><%=op.getOperatorName() %></option>
	<%
	}
	%>
	</select>
	<%
	count++;
}
%>

<%
	List<String> payoutTypes = (List<String>) request.getAttribute("payoutTypes");
	if(null == payoutTypes) {
		payoutTypes = new ArrayList<String>();
	}
%>

<form action="edit?action=commit" method="post">
	<!-- 文件信息 -->
	<table>
		<!-- 表头 -->
		<tr>
			<th>序列号</th>
			<th>任务id</th>
			<th>任务描述</th>
			<th>结算类型</th>
			<th>是否测试</th>
			<th>任务状态</th>
			<th>任务总数</th>
			<th>任务完成数</th>
			<th>脚本ID</th>
		</tr>
		
		<!-- 表体 -->
		<%
			int ind = -1;
			String taskId = "请填写任务ID";
			String taskDesc = "请填写任务描述";
			int taskState = 0;
			String country = "请选择国家";
			String op = "请填写运营商";
			int netEnv = -1;
			String totalCount = "请填写总任务数";
			String finishedCount = "0";
			String scriptId = "请选择脚本";
			int isTest = 0;
			
			String script = (String) request.getAttribute("script");
			if(null != script && !script.isEmpty()) {
				scriptId = script;
			}
			
			TaskInfo info = (TaskInfo) request.getAttribute("task");
			if(info != null) {
				ind = info.getInd();
				taskId = info.getTaskId();
				taskDesc = info.getTaskDesc();
				taskState = info.getTaskState();
				totalCount = Integer.toString(info.getTaskTotalCount());
				finishedCount = Integer.toString(info.getTaskFinishedCount());
				scriptId = info.getScriptId();
				isTest = info.isTaskForTest();
			}
			
			List<CountryInfo> countriesInfo = (List<CountryInfo>) request.getAttribute("countries");
			List<ScriptInfo> scriptsInfo = (List<ScriptInfo>) request.getAttribute("scripts");
			Map<String, List<OperatorInfo>> operators = (Map<String, List<OperatorInfo>>) request.getAttribute("operators");
			if(null == operators || operators.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.INFO, "运营商为空！");
			}
			Map<Integer, String> testSelections = (Map<Integer, String>) request.getAttribute("testSlections");
			if(null == testSelections) {
				testSelections = new HashMap<Integer, String>();
			}
		%>
		<tr>
			<td><input type="text" name="ind" readonly="readonly" value=<%=ind%>></td>
			<td><input type="text" name="task_id" value=<%=taskId%>></td>
			<!-- 任务描述 -->
			<td><input type="text" name="task_desc" value="<%=taskDesc%>"></td>
			<!-- 结算类型 -->
			<td>
				<select id="select_pay_type" name="pay_type">
				<%
					for(String type : payoutTypes) {
						if(type == null) {
							continue;
						}
						
						// 新增
						if(info == null) {
							%>
							<option value=<%=type %>><%=type %></option>
							<%
							continue;
						}
						
						// 编辑
						if(type.equals(info.getTaskType())) {
							%>
							<option value=<%=type %> selected="selected"><%=type %></option>
							<%
						} else {
							%>
							<option value=<%=type %>><%=type %></option>
							<%
						}
					}
				%>
				</select>
			</td>
			<!-- 是否测试任务 -->
			<td>
				<select id="select_task_test" name="task_test">
					<%
					for(Entry<Integer, String> entry : testSelections.entrySet()) {
						if(isTest == entry.getKey()) {
							%>
							<option value=<%=entry.getKey() %> selected="selected"><%=entry.getValue() %></option>
							<%
						} else {
							%>
							<option value=<%=entry.getKey() %>><%=entry.getValue() %></option>
							<%
						}
					}
					%>
				</select>
			</td>
			<!-- 任务状态 -->
			<td>
				<p id="task-state" hidden="hidden"><%=taskState %></p>
				<select id="select_task_state" name="task_state">
					<option value="0">暂停</option>
					<option value="1">运行</option>
				</select>
			</td>
			<!-- 任务总数 -->
			<td><input type="text" name="task_total_count" value=<%=totalCount %>></td>
			<!-- 已完成任务数 -->
			<td><input type="text" name="task_finished_count" value=<%=finishedCount %> readonly="readonly"></td>
			<!-- 脚本ID -->
			<td>
				<p id="task-script-id" hidden="hidden"><%=scriptId %></p>
				<select id="select_task_script_id" name="script_id">
					<option value=<%=scriptId %>><%=scriptId %></option>
					<%
					if(null != scriptsInfo && !scriptsInfo.isEmpty()) {
						for(ScriptInfo scriptInfo : scriptsInfo) {
							if(null == scriptInfo) {
								continue;
							}
							%>
							<option value=<%=scriptInfo.getScriptId() %>><%=scriptInfo.getScriptId() %></option>
							<%
						}
					}
					%>
				</select>
			</td>
		</tr>
	</table>
	<!-- 提交按钮 -->
	<input type="submit" value="提交" />
</form>


</body>

</html>