package com.spier.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.CountryInfo;
import com.spier.common.bean.db.OperatorInfo;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.Base64Util;
import com.spier.common.utils.DateTimeUtil;
import com.spier.common.utils.FileUtils;
import com.spier.common.utils.MessageDigestUtil;
import com.spier.service.ICountriesInfoService;
import com.spier.service.IOperatorsInfoService;
import com.spier.service.task.IScriptInfoService;

/**
 * 脚本操作类
 * @author GHB
 * @version 1.0
 * @date 2019.1.6
 */
@Controller
public class ScriptsOperations {

	private static final String M_PAGE_SCRIPTS_HOME = "scriptshome";
	private static final String M_PAGE_SCRIPT_ADD = "scriptadd";
	
	@Reference	
	private IScriptInfoService mScriptsInfoService;
	@Reference	
	private ICountriesInfoService mCountriesService;
	@Reference	
	private IOperatorsInfoService mOperatorsService;
	@Reference	
	private ICountriesInfoService mCountryService;
	
	@RequestMapping(value = "scripts/home", method = RequestMethod.GET)
	public String onScriptsHomeLoaded(Model model) {
		List<ScriptInfo> scripts = mScriptsInfoService.getAllInfo();
		if(null == scripts || scripts.isEmpty()) {
			model.addAttribute("msg", "无脚本，请点击添加按钮添加脚本");
			return M_PAGE_SCRIPTS_HOME;
		}
		
		model.addAttribute("scripts", scripts);
		pushNetEnvs2Page(model);
		
		return M_PAGE_SCRIPTS_HOME;
	}
	
	private static final String M_SCRIPTS_DIR_PATH = GlobalConfig.M_FILES_BASE_DIR + File.separator + "script_files";
//	private static final String M_SCRIPTS_DIR_PATH = "c:\\sdk_files\\script_files";
	
	@RequestMapping(value = "scripts/add", method = {RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onScriptAddLoaded(Model model, HttpServletRequest request) {
		// 检查当前系统是否有存放文件的目录，如果没有则创建
		if(!FileUtils.directoryExists(M_SCRIPTS_DIR_PATH)) {
			if(!FileUtils.createDirectory(M_SCRIPTS_DIR_PATH, false)) {
				model.addAttribute("msg", "创建文件保存目录失败！");
				return M_PAGE_SCRIPT_ADD;
			}
		}
		
		// 为页面提供信息
		pushInfos2Page(model);
		
		boolean isCommit = false;
		boolean isNewLoad = false;
		boolean isEdit = false;
		
		/**************************** 分析是什么操作   *********************************/
		String action = request.getParameter("action");
		if(StringUtils.equalsIgnoreCase(action, "load")) {
			isNewLoad = true;
		} else if(StringUtils.equalsIgnoreCase(action, "edit")) {
			isEdit = true;
		} else if(StringUtils.equalsIgnoreCase(action, "commit")) {
			isCommit = true;
		}
		
		/**************************** 页面载入   *********************************/
		if(isNewLoad) {
			return M_PAGE_SCRIPT_ADD;
		}
		
		/**************************** 编辑操作   *********************************/
		if(isEdit) {
			
			String indStr = request.getParameter("ind");
			int ind = -1;
			try {
				ind = Integer.parseInt(indStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				model.addAttribute("msg", MessageFormat.format("索引号【{0}】无法转换成数字，编辑失败", indStr));
				return M_PAGE_SCRIPT_ADD;
			}
			
			ScriptInfo info = mScriptsInfoService.findScriptInfoByInd(ind);
			if(null == info) {
				model.addAttribute("msg", MessageFormat.format("未查到索引号为【{0}】的脚本信息，请录入", ind));
				return M_PAGE_SCRIPT_ADD;
			}
			
			model.addAttribute("scriptInfo", info);
			pushInfos2Page(model);
			return M_PAGE_SCRIPT_ADD;
		}
		
		/**************************** 提交操作   *********************************/
		if(isCommit) {
			Logger.getAnonymousLogger().log(Level.INFO, "脚本编辑提交。");
			
			String scriptId = request.getParameter("scriptId");
			String scriptDesc = request.getParameter("scriptDesc");
			String vCode = request.getParameter("verCode");
			String envStr = request.getParameter("net_env");
			String indStr = request.getParameter("index");
			String countryAbb = request.getParameter("country");
			String opName = request.getParameter("operators");
			if(!StringUtils.isEmpty(opName)) {
				opName = opName.replaceAll("_", " ");
			}
			String shortCode = request.getParameter("short_code");
			
			int versionCode = 0;
			try {
				versionCode = Integer.parseInt(vCode);
			} catch (NumberFormatException e) {
				String info = MessageFormat.format("[{0}]无法转换成整形的版本号，无法保存脚本！", versionCode);
				Logger.getAnonymousLogger().log(Level.SEVERE, info);
				return M_PAGE_SCRIPT_ADD;
			}
			
			if(StringUtils.isEmpty(scriptId)) {
				String info = "检查到请求中无脚本id，认定为载入页面操作";
				Logger.getAnonymousLogger().log(Level.INFO, info);
				return M_PAGE_SCRIPT_ADD;
			}
			
			int env = -1;
			try {
				env = Integer.parseInt(envStr);
			} catch (NumberFormatException e) {
				String info = MessageFormat.format("[{0}]无法转换成整形的网络环境，无法保存脚本！", envStr);
				Logger.getAnonymousLogger().log(Level.SEVERE, info);
				return M_PAGE_SCRIPT_ADD;
			}
			
			int ind = -1;
			try {
				ind = Integer.parseInt(indStr);
			} catch(NumberFormatException e) {
				String info = MessageFormat.format("[{0}]无法转换成整形的序列号，无法保存脚本！", indStr);
				Logger.getAnonymousLogger().log(Level.SEVERE, info);
				return M_PAGE_SCRIPT_ADD;
			}
			
			// 检查数据的有效性
			if(StringUtils.isEmpty(scriptId)) {
				model.addAttribute("msg", "scriptId为空，无法保存！");
				return M_PAGE_SCRIPT_ADD;
			}
			
			if(versionCode <= 0) {
				model.addAttribute("msg", "versionCode不合法，无法保存！");
				return M_PAGE_SCRIPT_ADD;
			}
			
			if(!GlobalConfig.isNetworkEnvValid(env)) {
				model.addAttribute("msg", "脚本执行网络环境不合法，无法保存！");
				return M_PAGE_SCRIPT_ADD;
			}
			
			if(!isCountryAbbValid(countryAbb)) {
				model.addAttribute("msg", "国家简称无效，无法保存！");
				return M_PAGE_SCRIPT_ADD;
			}
			
			if(!isOperatorNameValid(countryAbb, opName)) {
				model.addAttribute("msg", "运营商名称无效，无法保存！");
				return M_PAGE_SCRIPT_ADD;
			}
			
			//将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
	                request.getSession().getServletContext());
			//检查form中是否有enctype="multipart/form-data"
			if(multipartResolver.isMultipart(request)) {
				//将request变成多部分request
	            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
	            //获取multiRequest 中所有的文件名
	            Iterator<String> iter = multiRequest.getFileNames();
	            while(iter.hasNext()) {
	            	//一次遍历所有文件
	                MultipartFile file = multiRequest.getFile(iter.next().toString());
	                if(null == file) {
	                	Logger.getAnonymousLogger().log(Level.WARNING, "null file!");
	                	continue;
	                }
	                
	                // 在文件末尾加日期和时间，起到备份旧数据，保存新数据的目的
	                String fileName = file.getOriginalFilename();
	                if (fileName.isEmpty()) {
						model.addAttribute("msg", "脚本文件不能为空");
						return M_PAGE_SCRIPT_ADD;
					}
	                String saveFileName = fileName.substring(0, fileName.lastIndexOf("."));
	                saveFileName += DateTimeUtil.getCurrentDateTimeStr("yyyyMMddHHmmss");
	                saveFileName += fileName.substring(fileName.lastIndexOf("."));
	                String savePath = M_SCRIPTS_DIR_PATH + File.separator + saveFileName;
	                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("原有文件名：{0}", fileName));
	                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("保存文件名：{0}", saveFileName));
	                
	                // 接收数据
	        		boolean receivedSucceed = false;
	                try {
						file.transferTo(new File(savePath));
						receivedSucceed = true;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	                if(!receivedSucceed) {
	                	Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】接受失败！", fileName));
	                	continue;
	                }
	                
	                // 计算哈希值
	                byte[] hashCodeBytes = MessageDigestUtil.getFileDigestSHA1(savePath);
	                if(null == hashCodeBytes || hashCodeBytes.length == 0) {
	                	Logger.getAnonymousLogger().log(Level.SEVERE, 
	                			MessageFormat.format("文件【{0}】计算哈希值失败，无法保存！", savePath));
	                	FileUtils.deleteFile(savePath);
	                	continue;
	                }
	                
	                // 组织文件信息
	                ScriptInfo info = new ScriptInfo();
	                info.setFilePath(savePath);
	                info.setHash(new String(Base64Util.bas64Encode(hashCodeBytes)));
	                info.setNetEnv(env);
	                info.setScriptDescription(scriptDesc);
	                info.setScriptId(scriptId);
	                info.setVersionCode(versionCode);
	                info.setCountryAbb(countryAbb);
	                info.setOperator(opName);
	                info.setShortCode(shortCode);
	                
	                // 检查数据库，不存在的情况下才能插入
	                ScriptInfo exist = null;
	                if(ind > 0) {
	                	exist = mScriptsInfoService.findScriptInfoByInd(ind);
	                } else {
	                	List<ScriptInfo> exists = mScriptsInfoService.findScriptInfosByScriptId(scriptId);
	                	if(null != exists && !exists.isEmpty()) {
	                		exist = exists.get(0);
	                	}
	                }
	                
	                if(null != exist) {
	                	String log = MessageFormat.format("脚本【{0}】已存在，走更新流程", scriptId);
	                	Logger.getAnonymousLogger().log(Level.SEVERE, log);
	                	if(!mScriptsInfoService.updateScriptInfoByInd(ind, info)) {
	                		model.addAttribute("msg", "脚本更新失败！");
	                	}
	                } else {
	                	if(!mScriptsInfoService.insertScriptInfo(info)) {
	                    	model.addAttribute("msg", MessageFormat.format("脚本【{0}】新增失败！", scriptId));
	                    	FileUtils.deleteFile(savePath);
	                    	continue;
	                    }
	                }
	                
	                String msg = MessageFormat.format("文件【{0}】上传成功！", fileName);
	                model.addAttribute("msg", msg);
	                model.addAttribute("scriptInfo", info);
	                Logger.getAnonymousLogger().log(Level.INFO, msg);
	            }
			} else {
				Logger.getAnonymousLogger().log(Level.WARNING, "not multipart, no process.");
				model.addAttribute("msg", "非MultiPart格式，不予处理！");
			}
		}
		
		return M_PAGE_SCRIPT_ADD;
	}
	
	@RequestMapping(value = "scripts/delete", method = {RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onScriptDeleteLoaded(Model model, HttpServletRequest request) {
		String indStr = request.getParameter("ind");
		int ind = -1;
		try {
			ind = Integer.parseInt(indStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			model.addAttribute("msg", MessageFormat.format("[{0}]无法解析成整形序列号，无法删除！", indStr));
			return M_PAGE_SCRIPTS_HOME;
		}
		
		mScriptsInfoService.deleteRecordByInd(ind);
		
		model.addAttribute("msg", "文件删除成功！");
		
		return M_PAGE_SCRIPTS_HOME;
	}
	
	@RequestMapping(value="scripts/download", method={RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<byte[]> onScriptDownloading(@RequestParam("sid") String id) {
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("要查询的脚本id是：{0}", id));
		
		// 根据Id查到文件的路径
		List<ScriptInfo> infoList = mScriptsInfoService.findScriptInfosByScriptId(id);
		if(null == infoList || infoList.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("未找到id为【{0}】的脚本信息！", id));
			return null;
		}
		
		ScriptInfo scriptInfo = infoList.get(0);
		String localPath = scriptInfo.getFilePath();
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("查询到文件路径：{0}", localPath));
		if(StringUtils.isEmpty(localPath)) {
			return null;
		}
		
		if(!FileUtils.fileExists(localPath)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】不存在，无法下载！", localPath));
			return null;
		}
		
		// 组装响应实体
		ResponseEntity<byte[]> res = null;
		InputStream is = null;
		try {
			is = new FileInputStream(localPath);
			byte[] body = new byte[is.available()];
			is.read(body);
			
			HttpHeaders headers = new HttpHeaders();
			// 设置文件类型
			headers.add("Content-Disposition", 
					MessageFormat.format("attchement;filename={0}", FileUtils.getFileNameFromFileAbPath(localPath)));
			//设置Http状态码
			HttpStatus statusCode = HttpStatus.OK;
			
			res = new ResponseEntity<byte[]>(body, headers, statusCode);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	private void pushInfos2Page(Model model) {
		pushCountries2Page(model);
		pushOperators2Page(model);
		pushNetEnvs2Page(model);
	}
	
	private void pushCountries2Page(Model model) {
		List<CountryInfo> countries = mCountriesService.getAll();
		model.addAttribute("countries", countries);
	}
	
	private void pushOperators2Page(Model model) {
		List<OperatorInfo> ops = mOperatorsService.getAll();
		model.addAttribute("ops", ops);
	}
	
	private void pushNetEnvs2Page(Model model) {
		Map<Integer, String> values = new HashMap<Integer, String>();
		values.put(GlobalConfig.M_NET_ENV_ALL, "全    部");
		values.put(GlobalConfig.M_NET_ENV_MOBILE, "手机流量");
		values.put(GlobalConfig.M_NET_ENV_WIFI, "WiFi流量");
		model.addAttribute("netenv", values);
	}
	
	private boolean isCountryAbbValid(String abb) {
		return mCountryService.checkCountryExistsByAbb(abb);
	}
	
	private boolean isOperatorNameValid(String abb, String op) {
		return mOperatorsService.checkOperatorExistsByCountryAbbAndOpName(abb, op);
	}
}
