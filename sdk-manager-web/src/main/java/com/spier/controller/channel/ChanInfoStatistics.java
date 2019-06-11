package com.spier.controller.channel;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.common.utils.RSAUtils;
import com.spier.service.channel.IChannelInfoService;

/**
 * 用于收集渠道信息的Controller
 * @author GHB
 * @version 1.0
 * @date 2018.12.21
 */
@Controller
public class ChanInfoStatistics {

	@Reference
	private IChannelInfoService mChanInfoService;
	
	private static final String M_PAGE_JSP_NAME_HOME = "channelshome";
	private static final String M_PAGE_JSP_NAME_EDIT = "channeledit";
	
	@RequestMapping("/channels")
	public String onHomeLoaded(Model model) {
		List<ChannelInfo> chanInfos = mChanInfoService.getAll();
		model.addAttribute("channels", chanInfos);
		
		setTaskSwitchValue2Page(model);
		
		return M_PAGE_JSP_NAME_HOME;
	}
	
	private void setTaskSwitchValue2Page(Model model) {
		Map<Integer, String> mTaskSwitch = new HashMap<Integer, String>();
		mTaskSwitch.put(ChannelInfo.M_TASK_SWITCH_ON, "开");
		mTaskSwitch.put(ChannelInfo.M_TASK_SWITCH_OFF, "关");
		model.addAttribute("switch-value", mTaskSwitch);
	}
	
	@RequestMapping(value="edit", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onEditLoaded(Model model, HttpServletRequest request) {
		String res = M_PAGE_JSP_NAME_EDIT;
		
		String action = request.getParameter("action");
		if(StringUtils.equals(action, "add")) {
			res = procChannelAdd(model, request);
		} else if(StringUtils.equals(action, "insert")) {
			res = procChannelInsert(model, request);
		} else if(StringUtils.equals(action, "delete")) {
			res = procChannelDelete(model, request);
		} else if(StringUtils.equals(action, "edit")) {
			res = procChannelEdit(model, request);
		}
		
		return res;
	}
	
	private String procChannelAdd(Model model, HttpServletRequest request) {
		Logger.getAnonymousLogger().log(Level.INFO, "创建渠道数据进入");
		
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String chNo = df.format(new Date());
		
		List<String> keyPair = RSAUtils.generateRSAKeyPairStr();
		if(keyPair.isEmpty()) {
			model.addAttribute("msg", "RSA秘钥对生成失败，请刷新页面重新生成！");
			return "createchinfo";
		}
		
		model.addAttribute("ch_no", chNo);
		model.addAttribute("pub_k", keyPair.get(0));
		model.addAttribute("priv_k", keyPair.get(1));
		model.addAttribute("switch", ChannelInfo.M_TASK_SWITCH_OFF);
		model.addAttribute("su_max_tasks", 2);
		
		setTaskSwitchValue2Page(model);
		
		return M_PAGE_JSP_NAME_EDIT;
	}
	
	private String procChannelInsert(Model model, HttpServletRequest request) {
		Logger.getAnonymousLogger().log(Level.INFO, "进入新增渠道");
		
		String chNo = request.getParameter("ch_no");
		String chDesc = request.getParameter("ch_desc");
		String pubK = request.getParameter("rsapub");
		String privK = request.getParameter("rsapriv");
		String switchVal = request.getParameter("task_switch");
		String maxTasks = request.getParameter("max_tasks");
		String postback = request.getParameter("post_back");
		
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("ch_no", chNo);
		attrs.put("ch_desc", chDesc);
		attrs.put("rsapub", pubK);
		attrs.put("rsapriv", privK);
		attrs.put("su_max_tasks", maxTasks);
		attrs.put("postback", postback);
		
		model.addAllAttributes(attrs);
		try {
			model.addAttribute("switch", Integer.parseInt(switchVal));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTaskSwitchValue2Page(model);
		
		// 检查数据库有没有记录，有的话就更新，没有就插入
		boolean succeed = false;
		ChannelInfo exists = mChanInfoService.getChanInfoByChanNo(chNo);
		if(exists != null) {
			exists.setChanDesc(chDesc);
			exists.setChannelNo(chNo);
			exists.setRsaPubKB64Str(pubK);
			exists.setRsaPrivKB64Str(privK);
			exists.setChanTaskSwitchState(switchVal);
			exists.setSingelUserMaxTasksStr(maxTasks);
			exists.setPostback(postback);
			succeed = mChanInfoService.updateChanInfoByChanNo(chNo, exists);
		} else {
			// 插入数据库
			ChannelInfo info = new ChannelInfo();
			info.setChannelNo(chNo);
			info.setChanDesc(chDesc);
			info.setRsaPubKB64Str(pubK);
			info.setRsaPrivKB64Str(privK);
			info.setChanTaskSwitchState(switchVal);
			info.setSingelUserMaxTasksStr(maxTasks);
			info.setPostback(postback);
			succeed = mChanInfoService.insertChanInfo(info);
		}
		
		model.addAttribute("msg", MessageFormat.format("渠道新增结果【{0}】", succeed ? "成功" : "失败"));
		
		return M_PAGE_JSP_NAME_EDIT;
	}
	
	private String procChannelDelete(Model model, HttpServletRequest request) {
		Logger.getAnonymousLogger().log(Level.INFO, "进入渠道删除");
		
		String chanNo = request.getParameter("chNo");
		boolean succeed = mChanInfoService.deleteByChanNo(chanNo);
		
		model.addAttribute("msg", MessageFormat.format("渠道删除结果：【{0}】", succeed ? "成功" : "失败"));
		
		return M_PAGE_JSP_NAME_EDIT;
	}
	
	private String procChannelEdit(Model model, HttpServletRequest request) {
		Logger.getAnonymousLogger().log(Level.INFO, "进入渠道编辑");
		
		String chanNo = request.getParameter("chNo");
		ChannelInfo info = mChanInfoService.getChanInfoByChanNo(chanNo);
		if(null == info) {
			model.addAttribute("msg", MessageFormat.format("没有渠道id为【{0}】的记录", chanNo));
			return M_PAGE_JSP_NAME_EDIT;
		}
		
		model.addAttribute("ch_no", info.getChannelNo());
		model.addAttribute("ch_desc", info.getChanDesc());
		model.addAttribute("pub_k", info.getRsaPubKeyB64Str());
		model.addAttribute("priv_k", info.getRsaPrivKeyB64Str());
		model.addAttribute("switch", info.getChanTaskSwitchState());
		model.addAttribute("su_max_tasks", info.getSingleUserMaxTasks());
		model.addAttribute("postback", info.getPostback());
		
		setTaskSwitchValue2Page(model);
		
		return M_PAGE_JSP_NAME_EDIT;
	}
	
	@RequestMapping(value = "/chan/getkey", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String onRSAPubkeyRequest(HttpServletRequest request) {
		String cid = request.getParameter("cid");
		if(StringUtils.isEmpty(cid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "参数中没有cid，无法查找RSA公钥！");
			return "";
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("获取密钥，渠道号：{0}", cid));
		
		ChannelInfo info = mChanInfoService.getChanInfoByChanNo(cid);
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"渠道号【{0}】没有对应的渠道信息,无法查找RSA公钥！", cid));
			return "";
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("是否取到密钥：{0}", 
				!StringUtils.isEmpty(info.getRsaPubKeyB64Str())));
		
		return info.getRsaPubKeyB64Str();
	}
}
