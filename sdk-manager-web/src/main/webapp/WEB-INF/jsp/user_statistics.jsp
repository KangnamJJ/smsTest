<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>新增/留存页面</title>
	
</head>

<body>
<%
	String chan = (String) request.getAttribute("ch");
	String time = (String) request.getAttribute("time");
	Integer allActivedUsers = (Integer) request.getAttribute("allActived");
	String nxtAlive = (String) request.getAttribute("nxtAlive");
	String twoDaysAlive = (String) request.getAttribute("twoAlive");
	String threeDaysAlive = (String) request.getAttribute("threeAlive");
	String fiveDaysAlive = (String) request.getAttribute("fiveAlive");
	String weekAlive = (String) request.getAttribute("weekAlive");
	String halfMonthAlive = (String) request.getAttribute("halfMonthAlive");
	String monthAlive = (String) request.getAttribute("monthAlive");
%>
	${msg }
	<!-- form -->
	<form action="statistics" method="post" enctype="multipart/form-data">
		<!-- 推广时间 -->
		<p>
			推广时间：<input type="text" name="campaign_time" value="">  渠道号：<input type="text" name="chan_no" value="">
			<input type="submit" value="开始查询" />
		</p>
		
		<!-- 查询结果 -->
		<table border="1">
			<!-- 表头 -->
			<tr>
				<th>渠道号</th>
				<th>推广时间</th>
				<th>总新增量</th>
				<th>次日留存率</th>
				<th>2日留存率</th>
				<th>3日留存率</th>
				<th>5日留存率</th>
				<th>周留存率</th>
				<th>半月留存率</th>
				<th>月留存率</th>
			</tr>
			<!-- 表体 -->
			<tr>
				<td><%=chan %></td>
				<td><%=time %></td>
				<td><%=allActivedUsers %></td>
				<td><%=nxtAlive %></td>
				<td><%=twoDaysAlive %></td>
				<td><%=threeDaysAlive %></td>
				<td><%=fiveDaysAlive %></td>
				<td><%=weekAlive %></td>
				<td><%=halfMonthAlive %></td>
				<td><%=monthAlive %></td>
			</tr>
		</table>
	</form>
</body>

</html>