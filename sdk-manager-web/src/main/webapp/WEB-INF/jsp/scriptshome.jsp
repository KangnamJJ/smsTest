<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.spier.common.bean.db.ScriptInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>脚本管理页面</title>
	
	<script type="text/javascript">
		function onAddClicked() {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			window.open('add?action=load', '添加文件', 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function onEditClicked(button) {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var recordInd = button.value;
			var url = 'add?ind=' + recordInd + '&action=edit';
			window.open(url, "编辑脚本", 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
		}
		
		function onDeleteClicked(button) {
			var recordInd = button.value;
			
			// 弹出窗口确认后再删除
			var conf = confirm("确定要删除文件索引号为" + recordInd + "的记录吗？");
			if(conf) {
				var url = "delete?ind=" + recordInd;
				
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
Map<Integer, String> netEnvs = (Map<Integer, String>) request.getAttribute("netenv");
if(null == netEnvs) {
	netEnvs = new HashMap<Integer, String>();
}
%>
	<p>${msg}</p>
	<!-- 新增脚本按钮 -->
	<button onclick="onAddClicked()">新增文件</button>
	<!-- 当前文件展示 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>序号</th>
			<th>脚本id</th>
			<th>脚本描述</th>
			<th>文件版本</th>
			<th>文件哈希</th>
			<th>国家</th>
			<th>运营商</th>
			<th>短号</th>
			<th>网络环境</th>
			<th>文件路径</th>
			<th>操作</th>
		</tr>
		
		<!-- 表内容 -->
		<%
		List<ScriptInfo> list = (List<ScriptInfo>) request.getAttribute("scripts");
		if(null != list) {
			for(int i = 0; i < list.size(); i++) {
				String snId = "sn_" + i;
				ScriptInfo script = list.get(i);
				if(null == script) {
					continue;
				}
				%>
				
				<tr>
					<!-- 序列号 -->
					<td><%=script.getIndex() %></td>
					<!-- 脚本id -->
					<td><%=script.getScriptId() %></td>
					<!-- 脚本描述 -->
					<td><%=script.getScriptDescription() %></td>
					<!-- 文件版本 -->
					<td><%=script.getVersionCode() %>
					<!-- 文件哈希 -->
					<td><%=script.getHash() %></td>
					<!-- 国家简称 -->
					<td><%=script.getCountryAbb() %></td>
					<!-- 运营商 -->
					<td><%=script.getOperator() %></td>
					<!-- 短号 -->
					<td><%=script.getShortCode() %></td>
					<!-- 网络环境 -->
					<td><%=netEnvs.get(script.getNetEnv()) %></td>
					<!-- 文件路径 -->
					<td><%=script.getFilePath() %></td>
					<!-- 操作按钮 -->
					<td>
						<button value=<%=script.getIndex() %> onclick="onEditClicked(this)">编辑</button>
						<button value=<%=script.getIndex() %> onclick="onDeleteClicked(this)">删除</button>
					</td>
				</tr>
			<% 
			}
		}
		%>
	</table>
</body>

</html>