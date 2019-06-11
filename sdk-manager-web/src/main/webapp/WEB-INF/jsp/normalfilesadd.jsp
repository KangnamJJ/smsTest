<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>普通文件新增页面</title>
	</head>
	
	<body>
		<form action="upload" method="post" enctype="multipart/form-data">
			<!-- 选择文件按钮 -->
			<input type="file" name="uploadFile" value="选择文件">
			
			<!-- 文件信息 -->
			<table>
				<!-- 表头 -->
				<tr>
					<th>文件唯一标识</th>
					<th>文件描述</th>
					<th>文件版本号</th>
					<th>文件哈希值</th>
					<th>文件下载地址</th>
				</tr>
				
				<tr>
					<td>
						<input type="text" name="fileId" value="请填写文件唯一标识" />
					</td>
					<td>
						<input type="text" name="fileDesc" value="请填写文件描述" />
					</td>
					<td>
						<input type="text" name="fileVCode" value="请填写文件版本号" />
					</td>
					<td>
						<input type="text" name="fileHash" value="无需填写，计算获得" />
					</td>
					<td>
						<input type="text" name="fileUrl" value="无需填写，计算获得" />
					</td>
				</tr>
			</table>
			
			<!-- 上传按钮 -->
			<input type="submit" value="上传文件" />
		</form>
		
		上传结果：${msg}
	</body>
</html>