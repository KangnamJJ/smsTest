<%@page import="java.util.Map"%>
<%@page import="com.spier.common.bean.db.ChannelInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>渠道信息</title>
	
	<script type="text/javascript">
		function popDialogAddChannel() {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			window.open('edit?action=add', '新增渠道', 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function popDialogDeleteChannel(button) {
			var chNo = button.value;
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var conf = confirm("确定要删除渠道【" + chNo + "】吗？");
			if(conf == true) {
				// 执行删除操作
				var url = 'edit?action=delete&chNo=' + chNo;
				window.open(url, '删除文件',
						"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
			}
		}
		
		function popDialogEditChannel(button) {
			var chNo = button.value;
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var url = 'edit?action=edit&chNo=' + chNo;
			window.open(url, '删除文件',
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function returnToHome() {
			location.href=''
		}
	</script>
</head>

<body>
	<!-- 首页 -->
	<p><button onclick="returnToHome()">首页</button></p>
	<!-- 新增文件按钮 -->
	<p><button onclick="popDialogAddChannel()">新增渠道</button></p>
	
	<!-- 已有渠道信息 -->
	<table border="1">
		<tr>
			<th>渠道号</th>
			<th>渠道名称（手动填写）</th>
			<th>任务开关</th>
			<th>单用户任务上限</th>
			<th>postback</th>
			<th>RSA公钥（Base64）</th>
			<th>RSA私钥（Base64）</th>
			<th>操作</th>
		</tr>
		
		<%
		Map<Integer, String> switch_value = (Map<Integer, String>) request.getAttribute("switch-value");
		List<ChannelInfo> channels = (List<ChannelInfo>) request.getAttribute("channels");
		if(null != channels) {
			for(ChannelInfo info : channels) {
				if(null == info) {
					continue;
				}
				
				String switchText = switch_value.get(info.getChanTaskSwitchState());
				if(null == switchText) {
					switchText = "关";
				}
				%>
				<tr>
					<td><%=info.getChannelNo() %></td>
					<td><%=info.getChanDesc() %></td>
					<td><%=switchText %></td>
					<td><%=info.getSingleUserMaxTasks() %></td>
					<td><%=info.getPostback() %></td>
					<td><input type="text" value=<%=info.getRsaPubKeyB64Str() %> readonly="readonly"></td>
					<td><input type="text" value=<%=info.getRsaPrivKeyB64Str() %> readonly="readonly"></td>
					<td>
						<button value=<%=info.getChannelNo() %> onclick="popDialogEditChannel(this)">编辑</button>
						<button value=<%=info.getChannelNo() %> onclick="popDialogDeleteChannel(this)">删除</button>
					</td>
				</tr>
				<%
			}
		}
		%>
	</table>
	
	
	<p>${msg}</p>
</body>

</html>