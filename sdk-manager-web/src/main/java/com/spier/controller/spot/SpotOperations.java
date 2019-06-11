package com.spier.controller.spot;

import java.text.MessageFormat;
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
import com.spier.common.bean.ReturnEntity;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.SpotReportBeans;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.common.utils.BussinessCommonUtil;
import com.spier.common.utils.DateTimeUtil;
import com.spier.entity.SpotInfo;
import com.spier.service.IUserInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;
import com.spier.service.spot.ISpotInfoService;

/**
 * 埋点处理
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
@Controller
public class SpotOperations {

	private static final String M_JSP_PAGE_HOME = "spot";
	private static final int M_PAGE_SIZE = 50;
	
	@Reference
	private ISpotInfoService mSpotInfoService;
	
	
	@RequestMapping(value = "spot", method = RequestMethod.GET)
	public String onSpotHomeLoaded(Model model, HttpServletRequest request) {
		String status = request.getParameter("status");
		int total;
		Map<String, Object> args = null;
		List<SpotInfo> res = null;
		if (StringUtils.isEmpty(status)) {
			// 计算页数
			total = mSpotInfoService.getSpotsAmount();
		} else {
			String channelNo = request.getParameter("channelNo");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String userId = request.getParameter("userId");
			String appName = request.getParameter("appName");
			args = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(startTime)) {
				args.put("startTime", DateTimeUtil.getTimestampFromStr(startTime));
			}
			if (StringUtils.isNotEmpty(endTime)) {
				args.put("endTime", DateTimeUtil.getTimestampFromStr(endTime));
			}
			if (StringUtils.isNotEmpty(channelNo)) {
				args.put("channelNo", channelNo);
			}
			if (StringUtils.isNotEmpty(userId)) {
				args.put("userId", userId);
			}
			if (StringUtils.isNotEmpty(appName)) {
				args.put("appName", appName);
			}
			// 计算页数
			res = mSpotInfoService.getSpotsByCondition(args);
			total = res.size();
		}
		int pageCnt = total / M_PAGE_SIZE;
		if(total % M_PAGE_SIZE != 0) {
			pageCnt += 1;
		}
		
		// 当前页
		int pageInd = 0;
		String startStr = request.getParameter("crtPage");
		if(!StringUtils.isEmpty(startStr)) {
			try {
				pageInd = Integer.parseInt(startStr);
				// 页面上显示的页码是从1开始的
				if(pageInd > 0) {
					pageInd--;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 步进值
		int step = 0;
		String stepStr = request.getParameter("step");
		if(!StringUtils.isEmpty(stepStr)) {
			try {
				step = Integer.parseInt(stepStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 下一页
		int nextPageInd = pageInd + step;
		if(nextPageInd < 0 || nextPageInd >= pageCnt) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"下一页页码【{0}】不正确！总页数为【{1}】", nextPageInd, pageCnt));
			nextPageInd -= step;
		}

		// 从起始处查询n条
		if (StringUtils.isEmpty(status)) {
			res = mSpotInfoService.getSpotsLimit(nextPageInd * M_PAGE_SIZE, M_PAGE_SIZE);
		}
		model.addAttribute("spots", res);

		// 设置spot类型
		model.addAttribute("types", BussinessCommonUtil.getInstance().getSpotTypeSelections());
		
		// 当前页页码
		model.addAttribute("crtPage", nextPageInd + 1);
		// 页数
		model.addAttribute("totalPages", pageCnt);
		
		return M_JSP_PAGE_HOME;
	}
	
}
