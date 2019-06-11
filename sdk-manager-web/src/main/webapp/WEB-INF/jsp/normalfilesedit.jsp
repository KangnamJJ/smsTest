<%@page import="com.spier.common.bean.db.file.NormalFileInfo"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>普通文件编辑页面</title>
	</head>
	
	<body>
		<p>${msg}</p>
		<form action="edit_commit" method="post" enctype="multipart/form-data">
			<p><input name="file_path" type="file" value="选择文件" /></p>
			
			<table border="1">
				<!-- 表头 -->
				<tr>
					<th>序号</th>
					<th>文件id</th>
					<th>文件描述</th>
					<th>文件版本</th>
					<th>文件哈希</th>
				</tr>
				
				<!-- 表体 -->
				<%
				NormalFileInfo info = (NormalFileInfo) request.getAttribute("info");
				if(null != info) {
				%>
					<tr>
						<td>
							<input name="file_ind" type="text" readonly="readonly" value="<%=info.getSn()%>" />
						</td>
						<td>
							<input name="file_id" type="text" value="<%=info.getId()%>" />
						</td>
						<td>
							<input name="file_desc" type="text" value="<%=info.getFileDesc()%>" />
						</td>
						<td>
							<input name="file_ver" type="text" value="<%=info.getFileVerCode()%>" />
						</td>
						<td>
							<input name="file_hash" type="text" value="<%=info.getFileHashCode()%>" />
						</td>
					</tr>
				<%
				}
			 	%>
			</table>
			
			<p><input name="submit" type="submit" /></p>
		</form>
	</body>
	
</html>