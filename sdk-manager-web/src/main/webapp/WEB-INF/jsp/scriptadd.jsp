<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.spier.common.bean.db.OperatorInfo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.spier.common.bean.db.CountryInfo"%>
<%@page import="java.util.List"%>
<%@page import="com.spier.common.bean.db.ScriptInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>脚本文件新增页面</title>

	<script type="text/javascript">
		function load() {
			var actualEnv = window.document.getElementById("net-env-value").innerText;

			Element netEnvSel = window.document.getElementById("select_net_env");
			for(var i = 0; i < netEnvSel.options.length; i++) {
				if(netEnvSel.options[i].value == actualEnv) {
					netEnvSel.options[i].selected = true;
				} else {
					netEnvSel.options[i].selected = false;
				}
			}
		}
	</script>
</head>

<body onload="load();">
	<p>${msg}</p>
	<%
	List<CountryInfo> countries = (List<CountryInfo>) request.getAttribute("countries");
	if(null == countries) {
		countries = new ArrayList<CountryInfo>();
	}

	List<OperatorInfo> ops = (List<OperatorInfo>) request.getAttribute("ops");
	if(null == ops) {
		ops = new ArrayList<OperatorInfo>();
	}

	Map<Integer, String> netEnvs = (Map<Integer, String>) request.getAttribute("netenv");
	if(null == netEnvs) {
		netEnvs = new HashMap<Integer, String>();
	}
	%>
	<form action="add?action=commit" method="post" enctype="multipart/form-data">
		<!-- 选择文件按钮 -->
		<input type="file" name="uploadFile" value="选择文件">

		<!-- 文件信息 -->
		<table>
			<!-- 表头 -->
			<tr>
				<th>脚本索引号</th>
				<th>脚本唯一标识</th>
				<th>脚本描述</th>
				<th>脚本版本号</th>
				<th>国家简称</th>
				<th>运营商</th>
				<th>短号</th>
				<th>所需网络环境</th>
				<th>文件哈希</th>
			</tr>

			<tr>
				<%
					int ind = -1;
					String sid = "请填写脚本唯一标识";
					String desc = "请填写脚本描述";
					int vcode = -1;
					int env = -1;
					String hash = "上传后显示文件哈希";
					String countryAbb = "请选择国家简称";
					String opTxt = "请选择运营商";

					ScriptInfo info = (ScriptInfo) request.getAttribute("scriptInfo");
					if(null != info) {
						ind = info.getIndex();
						sid = info.getScriptId();
						desc = info.getScriptDescription();
						vcode = info.getVersionCode();
						env = info.getNetEnv();
						hash = info.getHash();
						countryAbb = info.getCountryAbb();
						opTxt = info.getOperator();
					}
				%>
				<td><input type="text" name="index" value="<%=ind %>" readonly="readonly"/></td>
				<td><input type="text" name="scriptId" value="<%=sid %>" /></td>
				<td><input type="text" name="scriptDesc" value="<%=desc %>" /></td>
				<td><input type="text" name="verCode" value="<%=vcode %>" /></td>

				<!-- 国家简称 -->
				<td>
					<select id="select_country" name="country">
					<%
					for(CountryInfo country : countries) {
						if(null == country) {
							continue;
						}

						boolean selected = false;
						if(StringUtils.equals(countryAbb, country.getAbbrevation())) {
							selected = true;
						}

						String countryTxt = country.getAbbrevation() + " - " + country.getChiness();

						if(selected) {
							%>
							<option value="<%=country.getAbbrevation() %>" selected="selected"><%=countryTxt %></option>
							<%
						} else {
							%>
							<option value="<%=country.getAbbrevation() %>"><%=countryTxt %></option>
							<%
						}
					}
					%>
					</select>
				</td>

				<!-- 运营商 -->
				<td>
					<select id="select_op" name="operators">
					<%
					for(OperatorInfo op : ops) {
						if(null == op) {
							continue;
						}

						boolean selected = false;
						if(StringUtils.equals(opTxt, op.getOperatorName())) {
							selected = true;
						}

						String opName = op.getCountryAbb() + " - " + op.getOperatorName();

						if(selected) {
							%>
							<option value="<%=op.getOperatorName() %>" selected="selected"><%=opName %></option>
							<%
						} else {
							%>
							<option value="<%=op.getOperatorName() %>"><%=opName %></option>
							<%
						}
					}
					%>
					</select>
				</td>
				<!-- 短号 -->
				<td><input type="text" name="short_code" /></td>
				<td>
					<select id="select_net_env" name="net_env">
					<%
						for(Entry<Integer, String> entry : netEnvs.entrySet()) {
							boolean selected = false;
							if(env == entry.getKey()) {
								selected = true;
							}

							if(selected) {
								%>
								<option value="<%=entry.getKey() %>" selected="selected"><%=entry.getValue() %></option>
								<%
							} else {
								%>
								<option value="<%=entry.getKey() %>"><%=entry.getValue() %></option>
								<%
							}
						}
					%>
					</select>
				</td>
				<td><input type="text" name="fileHash" value="<%=hash %>" readonly="readonly"/></td>
			</tr>
		</table>
		
		<!-- 上传按钮 -->
		<input type="submit" value="提交脚本" />
	</form>
</body>
</html>