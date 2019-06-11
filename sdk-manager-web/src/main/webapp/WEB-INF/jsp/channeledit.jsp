<%@page import="java.util.logging.Level"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>新增渠道</title>
	
	<script type="text/javascript">
		function load() {
			logTxt("Hello");
			
			// 初始化任务开关
			Element elem = document.getElementById("switch_value");
			var value = elem.innerText;
			logTxt(value);
			elem = document.getElementById("select_switch");
			for(var i = 0; i < elem.options.length; i++) {
				if(elem.options[i].value == value) {
					elem.options[i].selected = true;
				} else {
					elem.options[i].selected = false;
				}
			}
			
			logTxt("初始化完成！");
		}
		
		function logTxt(txt) {
			window.alert(txt);
			Element elem = document.getElementById("debug");
			elem.innerText = txt;
		}
	</script>
</head>

<body onload="load()">
	<p id="debug"></p>
	
	<%
	Map<Integer, String> task_switch_values = (Map<Integer, String>) request.getAttribute("switch-value");
	Integer switchTxt = (Integer) request.getAttribute("switch");
	Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("页面收到开关状态：{0}", switchTxt));
	%>
	<form action="edit?action=insert" method="post">
		<table border="1">
			<tr>
				<th>渠道号</th>
				<th>渠道名称（手动填写）</th>
				<th>任务开关（要重新选）</th>
				<th>单用户任务上限</th>
				<th>postback</th>
				<th>RSA公钥（Base64）</th>
				<th>RSA私钥（Base64）</th>
			</tr>
			<tr>
				<!-- 渠道号 -->
				<td>
					<input type="text" name="ch_no" value="${ch_no}" readonly="readonly" />
				</td>
				<!-- 渠道名称 -->
				<td>
					<input type="text" name="ch_desc" value="${ch_desc}"/>
				</td>
				<!-- 任务开关 -->
				<td>
					<p id="switch_value" hidden="hidden"><%=switchTxt %></p>
					<select id="select_switch" name="task_switch">
					<%
						if(null != task_switch_values) {
							for(Entry<Integer, String> entry : task_switch_values.entrySet()) {
								%>
								<option value=<%=entry.getKey() %>><%=entry.getValue() %></option>
								<%
							}
						}
					%>
					</select>
				</td>
				<!-- 单用户任务上限 -->
				<td><input type="text" name="max_tasks" value="${su_max_tasks}"/></td>
				<!-- postback -->
				<td><input type="text" name="post_back" value="${postback }" /></td>
				<!-- 公钥 -->
				<td>
					<input type="text" name="rsapub" value="${pub_k}" readonly="readonly" />
				</td>
				<!-- 私钥 -->
				<td>
					<input type="text" name="rsapriv" value="${priv_k}" readonly="readonly" />
				</td>
			</tr>
		</table>
		<!-- 提交按钮 -->
		<input type="submit" value="提交" />
	</form>

	<p>${msg }</p>
</body>

</html>