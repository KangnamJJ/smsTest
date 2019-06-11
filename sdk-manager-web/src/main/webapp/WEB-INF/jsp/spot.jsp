<%@page import="com.spier.entity.SpotInfo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>客户端埋点页面</title>
	
	<script type="text/javascript">
		function requestSpots(crt, step) {
			// var crt = button.value;
			location.href='?crtPage=' + crt + "&step=" + step;
		}
	</script>
</head>

<body>
<%
Map<Integer, String> types = (Map<Integer, String>) request.getAttribute("types");
if(null == types) {
	types = new HashMap<Integer, String>();
}

List<SpotInfo> list = (List<SpotInfo>) request.getAttribute("spots");
if(null == list) {
	list = new ArrayList<SpotInfo>();
}

int crtPage = (Integer) request.getAttribute("crtPage");
int totalPages = (Integer) request.getAttribute("totalPages");
%>
	<p>${msg}</p>
	<p>
		<!-- 上一页按钮 -->
		<button id="pre_page" value=<%=crtPage%> onclick="requestSpots(this.value, -1)">上一页</button>
		<!-- 页数、页码 -->
		第 <%=crtPage %> 页    共 <%=totalPages %> 页
		<!-- 下一页按钮 -->
		<button id="next_page" value=<%=crtPage%> onclick="requestSpots(this.value, 1)">下一页</button>
	</p>
	<p>
		<form action="spot" method="get">
			<input type="hidden" value="1" id="status" name="status">
			渠道号：<input type="text" id="channelNo" name="channelNo">
			开始时间：<input type="text" id="startTime" name="startTime">
			结束时间：<input type="text" id="endTime" name="endTime">
			用户ID：<input type="text" id="userId" name="userId">
			应用名称：<input type="text" id="appName" name="appName">
			<button type="submit" value="提交">提交</button>
		</form>
</form>
	</p>
	<!-- 当前文件展示 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>渠道号</th>
			<th>应用名称</th>
			<th>用户ID</th>
			<th>会话ID</th>
			<th>埋点类型</th>
			<th>埋点标识</th>
			<th>埋点信息</th>
			<th>修改时间</th>
		</tr>
		
		<!-- 表内容 -->
		<%
		for(int i = 0; i < list.size(); i++) {
			SpotInfo spot = list.get(i);
			if(null == spot) {
				continue;
			}
			
			String typeStr = types.get(spot.getType());
			if(null == typeStr) {
				typeStr = "未知类型";
			}
			%>
			
			<tr>
				<!-- 渠道号 -->
				<td><%=spot.getChanNo() %></td>
				<!-- 应用名称 -->
				<td><%=spot.getAppName() %></td>
				<!-- 用户ID -->
				<td><%=spot.getUID() %>
				<!-- 会话ID -->
				<td><%=spot.getFlowId() %></td>
				<!-- 埋点类型 -->
				<td><%=typeStr %></td>
				<!-- 埋点标识 -->
				<td><%=spot.getTag() %></td>
				<!-- 埋点信息-->
				<td><input type="text" value="<%=spot.getInfo() %>" readonly="readonly"/></td>
				<!-- 修改时间-->
				<td><%=spot.getChangeTime() %></td>
			</tr>
		<% 
		}
		%>
	</table>
</body>
</html>