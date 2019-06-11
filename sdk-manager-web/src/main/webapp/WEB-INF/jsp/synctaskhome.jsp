<%@page import="com.spier.common.config.GlobalConfig"%>
<%@page import="com.spier.common.utils.BussinessCommonUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.spier.common.bean.db.task.SyncTaskInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>同步任务管理</title>
	
	<script type="text/javascript">
		function switchTaskState(offerId) {
			location.href='?action=switcher&offerid=' + offerId;
		}
	</script>
</head>

<body>
<%
	List<SyncTaskInfo> tasks = (List<SyncTaskInfo>) request.getAttribute("tasks");
	if(null == tasks) {
		tasks = new ArrayList<SyncTaskInfo>();
	}
%>
	<p>
		<button>预留</button>
		<button>预留</button>
		<button>预留</button>
	</p>
	
	<!-- 任务列表 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>State</th>
			<th>Campaign</th>
			<th>Package</th>
			<th>Campaign-Desc</th>
			<th>Offer-ID</th>
			<th>Offer-Name</th>
			<th>Offer-Desc</th>
			<th>Country</th>
			<th>S-Market</th>
			<th>Payout</th>
			<th>Payout-Type</th>
			<th>Cap</th>
			<th>Finished</th>
			<th>Switch</th>
		</tr>
		
		<!-- 表内容 -->
		<%
			for(SyncTaskInfo task : tasks) {
				if(null == task) {
					continue;
				}
				
				%>
				
				<tr>
					<!-- 任务开关 -->
					
					<td><%=BussinessCommonUtil.getInstance().getTaskStateString(task.getSwitch()) %></td>
					<!-- campaign -->
					<td><%=task.getCampaign() %></td>
					<!-- package -->
					<td><%=task.getPkgName() %></td>
					<!-- campaign-description -->
					<td><%=task.getCampaignDesc() %></td>
					<!-- offer-id -->
					<td><%=task.getOfferId() %></td>
					<!-- offer-name -->
					<td><%=task.getOfferName() %></td>
					<!-- offer-desc -->
					<td><%=task.getDesc() %></td>
					<!-- country -->
					<td><%=task.getCountries() %></td>
					<!-- source-market -->
					<td><%=task.getMarket() %></td>
					<!-- payout -->
					<td><%=task.getPayout() %></td>
					<!-- payout-type -->
					<td><%=task.getPayoutType() %></td>
					<!-- CAP -->
					<td><%=task.getCap() %></td>
					<!-- finished -->
					<td><%=task.getFinishedCount() %></td>
					<!-- switch -->
					<%
						String switcherTxt = "";
						if(task.getSwitch() == GlobalConfig.M_TASK_STATE_PAUSE) {
							switcherTxt = "启动";
						} else {
							switcherTxt = "暂停";
						}
					%>
					<td><button value=<%=task.getOfferId() %> onclick="switchTaskState(this.value)"><%=switcherTxt %></button></td>
				</tr>
				
				<%
			}
		%>
		
	</table>
</body>
</html>