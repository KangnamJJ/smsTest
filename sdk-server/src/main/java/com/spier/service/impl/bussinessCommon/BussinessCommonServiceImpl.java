package com.spier.service.impl.bussinessCommon;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.service.BussinessCommonService;
import com.spier.service.ICountriesInfoService;
import com.spier.service.IOperatorsInfoService;
import com.spier.service.task.IScriptInfoService;

@Service
public class BussinessCommonServiceImpl implements BussinessCommonService{
	@Autowired
	private ICountriesInfoService mCountriesInfoService;
	
	@Autowired
	private IOperatorsInfoService mOperatorServices;
	
	@Autowired
	private IScriptInfoService mScriptInfoService;
	
	private static final String M_SUFFIX_OP_NAME_MY = "com.my";
	private static final String M_SUFFIX_OP_NAME_TH = "co.th";

	@Override
	public String getCountryAbb(String str) {
		if(StringUtils.isEmpty(str)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "str为空，无法查询国家简称！");
			return str;
		}
		
		String res = str;
		
		// 如果字符串是纯数字，可能是出自于mcc
		if(StringUtils.isNumeric(str)) {
			res = getCountryAbbByMCCText(str);
			if(!StringUtils.equals(str, res)) {
				return res;
			}
		}
		
		// 从国家名称中查询
		res = mCountriesInfoService.getCountryAbbByCountry(str);
		if(StringUtils.isEmpty(res)) {
			Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format("国家名【{0}】没有对应的简称！", str));
			res = str;
		}
		
		return res;
	}

	@Override
	public String getOperatorNameByCountryAbbAndOpText(String abb, String opTxt) {
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "国家简称为空，无法查询运营商名称！");
			return null;
		}
		
		String res = opTxt;
		
		// 如果txt是纯数字，可能是从imsi中取出
		if(StringUtils.isNumeric(opTxt)) {
			res = getOperatorByAbbAndMNCText(abb, opTxt);
			if(StringUtils.isEmpty(res)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"根据国家【{0}】和运营商【{1}】未查出对应的运营商名称！", abb, opTxt));
			}
			
			return res;
		}

		// 马来
		if(StringUtils.equals(abb, "MY")) {
			if(StringUtils.contains(opTxt, "u.com.my")) {
				res = "U Mobile";
			} else if(StringUtils.contains(opTxt, M_SUFFIX_OP_NAME_MY)) {	// 如果以com.my结尾的，则取最前面的一个字段并忽略大小写作比较
				res = getOperatorByNameAndSuffix(abb, opTxt);
				if(StringUtils.isEmpty(res)) {
					Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
							"根据国家【{0}】和运营商【{1}】未查出对应的运营商名称！", abb, opTxt));
				}
			}

			return res;
		}

		// 泰国
		if(StringUtils.equals(abb, "TH")) {
			if(StringUtils.contains(opTxt, M_SUFFIX_OP_NAME_TH)) {
				res = getOperatorByNameAndSuffix(abb, opTxt);
				if(StringUtils.isEmpty(res)) {
					Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
							"根据国家【{0}】和运营商【{1}】未查出对应的运营商名称！", abb, opTxt));
				}
			}

			return res;
		}
		
		// 没有其他逻辑了
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
				"opTxt【{0}】不是数字，直接返回！", opTxt));
		return res;
	}

	@Override
	public Map<String, ScriptInfo> getScriptsByTasks(List<TaskInfo> tasks) {
		if(null == tasks || tasks.isEmpty()) {
			return null;
		}
		
		Map<String, ScriptInfo> res = new HashMap<String, ScriptInfo>();
		
		List<ScriptInfo> allScripts = mScriptInfoService.getAllInfo();
		for(TaskInfo task : tasks) {
			if(null == task) {
				continue;
			}
			
			for(ScriptInfo script : allScripts) {
				if(null == script) {
					continue;
				}
				
				if(StringUtils.equals(script.getScriptId(), task.getScriptId())) {
					res.put(task.getScriptId(), script);
					break;
				}
			}
		}
		
		return res;
	}
	
	
	private String getCountryAbbByMCCText(String txt) {
		if(StringUtils.isEmpty(txt)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "文本为空，不是MCC！");
			return txt;
		}
		
		if(txt.length() < 3) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文本【{0}】长度不够，不是MCC", txt));
			return txt;
		}
		
		String res = mOperatorServices.getCountryAbbByMCC(txt.substring(0, 3));
		if(StringUtils.isEmpty(res)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("根据文本【{0}】未查到相应的国家简称！", txt));
			res = txt;
		}
		
		return res;
	}
	
	private String getOperatorByNameAndSuffix(String abb, String opTxt) {
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("abb为空，无法查询运营商【{0}】对应的名称！", opTxt));
			return null;
		}
		
		if(StringUtils.isEmpty(opTxt)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "opTxt为空，无法查询运营商名称！");
			return null;
		}
		
		// 截取最前面一段
		String[] ss = opTxt.split("\\.");
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
				"运营商字符串【{0}】经过切割后提取的字符串：【{1}】", opTxt, ss[0]));
		
		String res = opTxt;
		
		// truecorp.co.th切割出来之后是truecorp，而数据库中是TrueMove，这样就无法判别。
		// 这里对truecorp做简单处理
		if(StringUtils.equals(ss[0], "truecorp")) {
			ss[0] = ss[0].replace("corp", "");
		}
		
		List<String> list = mOperatorServices.getOperatorNameByAbbAndOpTxt(abb, ss[0]);
		if(list.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("根据文本【{0}】未找到相应的运营商名称！", opTxt));
		} else {
			res = list.get(0);
		}
		
		return res;
	}
	
	private String getOperatorByAbbAndMNCText(String abb, String txt) {
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"abb为空，无法查询【{0}】对应的运营商名称！", txt));
			return null;
		}
		
		if(StringUtils.isEmpty(txt)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "文本为空，无法查询运营商名称！");
			return null;
		}
		
		if(txt.length() < 2) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文本【{0}】长度不够，不是MNC！", txt));
			return txt;
		}
		
		String res = mOperatorServices.getOperatorNameByAbbMNC(abb, txt.substring(0, 2));
		if(StringUtils.isEmpty(res)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("根据文本【{0}】未找到相应的运营商名称！", txt));
			res = txt;
		}
		
		return res;
	}
}
