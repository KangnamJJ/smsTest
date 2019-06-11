package com.spier.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.spier.common.utils.AOCAnalyseUtil;
import com.spier.common.utils.JavaScriptUtil;

/**
 * 辅助操作接口类
 * @author GHB
 * @version 1.0
 * @date 2019.1.28
 */
@Controller
public class AssistantOperations {
	
	private static final String M_JSP_NAME_EMPTY_PAGE = "empty";

	@RequestMapping(value = "/assistant/js", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String onJavaScriptAssitantRequest(HttpServletRequest request) {
		String content = request.getParameter("content");
		if(StringUtils.isEmpty(content)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "请求中content为空，无法协助处理js脚本！");
			return "";
		}
		
		String baseUrl = MessageFormat.format("{0}://{1}:{2}{3}", 
				request.getScheme(), request.getServerName(), Integer.toString(request.getServerPort()), 
				request.getContextPath());
		String emptyPagePath = baseUrl + "/assistant/empty";
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("emptyURL: {0}", emptyPagePath));
		
		return JavaScriptUtil.executeSpJsObject(emptyPagePath, content);
	}
	
	@RequestMapping(value = "/assistant/empty", method = {RequestMethod.GET, RequestMethod.POST})
	public String onEmptyPageLoaded(Model model) {
		return M_JSP_NAME_EMPTY_PAGE;
	}
	
	@RequestMapping(value = "/assistant/aoc_analyse", method = {RequestMethod.POST})
	@ResponseBody
	public String onAOCAnalyseRequest(HttpServletRequest request) {
		String content = request.getParameter("content");
		if(StringUtils.isEmpty(content)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "请求中content为空，无法协助解析AOC！");
			return "";
		}
		
		String decodeContent = null;
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(StringUtils.isEmpty(decodeContent)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"从原始内容【{0}】中未解码出内容，无法协助解析AOC！", 
					decodeContent));
			return "";
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("要解析的AOC内容：【{0}】", decodeContent));
		
		List<String> aocs = AOCAnalyseUtil.analyseAOCContent(decodeContent);
		String res = new Gson().toJson(aocs);
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("解析出的AOC：【{0}】", res));
		
		try {
			res = URLEncoder.encode(res, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			res = "";
		}
		
		return res;
	}
}
