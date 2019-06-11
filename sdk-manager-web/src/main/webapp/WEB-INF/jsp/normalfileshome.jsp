<%@page import="com.spier.common.bean.db.file.NormalFileInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>普通文件管理页面</title>
	
	<script language="javascript">
		// 弹出窗口增加文件
		function popDialogAddFile() {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			window.open('add', '添加文件', 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function popDialogEditFile(obj) {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var idObjId = "id_" + obj.value;
			var fileId = document.getElementById(idObjId).getAttribute("value");
			var url = 'edit?id=' + fileId + '&action=edit';
			window.open(url, '编辑文件', 
					"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
		}
		
		function popDialogDeleteFile(obj) {
			var iWidth=800;
			var iHeight=600;
			var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
			
			var idObjId = "id_" + obj.value;
			var fileId = document.getElementById(idObjId).getAttribute("value");
			
			// 弹出窗口确认后再删除
			var conf = confirm("确定要删除文件Id为" + fileId + "的记录吗？");
			if(conf == true) {
				// 执行删除操作
				var url = 'edit?id=' + fileId + '&action=delete';
				window.open(url, '删除文件',
						"height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft + 'toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
			}
		}
	</script>
</head>

<body>
	<p>${msg}</p>
	<!-- 新增文件按钮 -->
	<p><button onclick="popDialogAddFile()">新增文件</button></p>
	
	<!-- 当前文件展示 -->
	<table border="1">
		<!-- 表头 -->
		<tr>
			<th>序号</th>
			<th>文件id</th>
			<th>文件描述</th>
			<th>文件版本</th>
			<th>文件哈希</th>
			<th>操作</th>
		</tr>
		
		<!-- 表内容 -->
		<%
			List<NormalFileInfo> list = (List<NormalFileInfo>) request.getAttribute("records");
			if(null != list) {
				for(int i = 0; i < list.size(); i++) {
					NormalFileInfo info = list.get(i);
					
					String snId = "sn_" + i;
					String idId = "id_" + i;
					String descId = "desc_" + i;
					String vcId = "vc_" + i;
					String hcId = "hc_" + i;
					String btValue = Integer.toString(i);
					%>
					<tr>
						<td id=<%=snId%>>
							<%=info.getSn()%>
						</td>
		                <td id=<%=idId%> value="<%=info.getId()%>" >
		                	<%=info.getId()%>
		                </td>
		                <td id=<%=descId%>>
		                	<%=info.getFileDesc()%>
		                </td>
		                <td id=<%=vcId%>>
		                	<%=info.getFileVerCode()%>
		                </td>
		                <td id=<%=hcId%>>
		                	<%=info.getFileHashCode()%>
		                </td>
		                <td>
		                	<button value=<%=btValue%> onclick="popDialogEditFile(this)">编辑</button>
		                	<button value=<%=btValue%> onclick="popDialogDeleteFile(this)">删除</button>
		                </td>
                	</tr>
                	<%
				}
			}
		%>
	</table>
</body>

</html>