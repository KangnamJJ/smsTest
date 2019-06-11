package com.spier.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.ReturnEntity;
import com.spier.common.bean.db.file.NormalFileInfo;
import com.spier.common.bean.net.file.NormalFileInfoQueryBeans;
import com.spier.common.config.ErrorCodes;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.AnalyseResult;
import com.spier.common.utils.Base64Util;
import com.spier.common.utils.DateTimeUtil;
import com.spier.common.utils.FileUtils;
import com.spier.common.utils.MessageDigestUtil;
import com.spier.service.file.INormalFileService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;

/**
 * 普通文件操作的controller
 * @author GHB
 * @version 1.0
 * @date 2018.12.28
 */
@Controller
public class NormalFileOperations {

	@Reference	
	private INormalFileService mFileService;
	@Autowired	
	private ISafeRequestParserService mParserService;
	@Reference	
	private ISessionManageService mSessionManageService;
	
	private static final String M_NORMAL_FILES_HOME_JSP_NAME = "normalfileshome";
	private static final String M_NORMAL_FILES_ADD_JSP_NAME = "normalfilesadd";
	private static final String M_NORMAL_FILES_EDIT_JSP_NAME = "normalfilesedit";
	private static final String M_NORMAL_FILES_DIR = GlobalConfig.M_FILES_BASE_DIR + File.separator + "normal_files";
//	private static final String M_NORMAL_FILES_DIR = "c:\\sdk_files\\normal_files";
	
	@RequestMapping(value="/files/home", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onNormalFilesHomeLoaded(Model modle) {
		List<NormalFileInfo> infoLst = mFileService.getAllFilesInfo();
		if(null == infoLst || infoLst.isEmpty()) {
			modle.addAttribute("msg", "无文件记录，请点击新增按钮添加文件");
			return M_NORMAL_FILES_HOME_JSP_NAME;
		}
		
		modle.addAttribute("records", infoLst);
		modle.addAttribute("msg", MessageFormat.format("共有【{0}】条文件记录", infoLst.size()));
		
		return M_NORMAL_FILES_HOME_JSP_NAME;
	}
	
	@RequestMapping(value="/files/add", method={RequestMethod.GET, RequestMethod.POST})
	public String onFileAddingLoaded(Model model) {
		return M_NORMAL_FILES_ADD_JSP_NAME;
	}
	
	@RequestMapping(value="/files/upload", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String uploadFile(Model model, HttpServletRequest request) {
		// 检查当前系统是否有存放文件的目录，如果没有则创建
		if(!FileUtils.directoryExists(M_NORMAL_FILES_DIR)) {
			if(!FileUtils.createDirectory(M_NORMAL_FILES_DIR, false)) {
				model.addAttribute("msg", "创建文件保存目录失败！");
				return M_NORMAL_FILES_ADD_JSP_NAME;
			}
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
                String saveFileName = fileName;
                if(fileName.contains(".")) {
                	saveFileName = fileName.substring(0, fileName.lastIndexOf("."));
                }
                saveFileName += DateTimeUtil.getCurrentDateTimeStr("yyyyMMddHHmmss");
                if(fileName.contains(".")) {
                	saveFileName += ".";
                	saveFileName += fileName.substring(fileName.lastIndexOf("."));
                }
                String savePath = M_NORMAL_FILES_DIR + File.separator + saveFileName;
                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("原有文件名：{0}", fileName));
                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("保存文件名：{0}", saveFileName));
                // 组装url：不用组装，这个字段暂留。下载的url采用统一接口，post方式。文件id在post的参数中提供。
                
                
                // 组织文件信息
                NormalFileInfo info = new NormalFileInfo();
        		// 取文件Id
        		info.setId(request.getParameter("fileId"));
        		info.setFileDesc(request.getParameter("fileDesc"));
        		int vcode = -1;
        		try {
        			vcode = Integer.parseInt(request.getParameter("fileVCode"));
        		} catch (NumberFormatException e) {
        			e.printStackTrace();
        			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("版本号异常，文件【{0}】接收失败！", fileName));
        			continue;
        		}
        		info.setFileVerCode(vcode);
        		
        		// 接收数据
        		boolean receivedSucceed = false;
                try {
					file.transferTo(new File(savePath));
					receivedSucceed = true;
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if(!receivedSucceed) {
                	Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】接受失败！", fileName));
                	continue;
                }
                info.setFilePath(savePath);
                
                // 计算哈希值
                byte[] hashCodeBytes = MessageDigestUtil.getFileDigestSHA1(savePath);
                if(null == hashCodeBytes || hashCodeBytes.length == 0) {
                	Logger.getAnonymousLogger().log(Level.SEVERE, 
                			MessageFormat.format("文件【{0}】计算哈希值失败，无法保存！", fileName));
                	FileUtils.deleteFile(savePath);
                	continue;
                }
                info.setFileHashCode(new String(Base64Util.bas64Encode(hashCodeBytes)));
                
                // 检查文件信息是否有效
        		if(!info.isValidForFileSaving()) {
        			FileUtils.deleteFile(savePath);
        			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】信息缺失，无法保存！", fileName));
        			continue;
        		}
        		
        		// 检查数据库，不存在的情况下才能插入
        		if(mFileService.checkFileRecordExistsById(info.getId())) {
        			model.addAttribute("msg", MessageFormat.format("文件【{0}】存在，无法新增！", info.getId()));
        			continue;
        		}
        		
        		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("文件【{0}】不存在，执行插入操作", info.getId()));
    			// 插入操作
                mFileService.insertNormalFileInfo(info);
        		
                String msg = MessageFormat.format("文件【{0}】上传成功！", fileName);
                model.addAttribute("msg", msg);
                Logger.getAnonymousLogger().log(Level.INFO, msg);
            }
		} else {
			Logger.getAnonymousLogger().log(Level.WARNING, "not multipart, no process.");
			model.addAttribute("msg", "非MultiPart格式，不予处理！");
		}
		
		return M_NORMAL_FILES_ADD_JSP_NAME;
	}
	
	/**
	 * 普通文件信息查询接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="files/info_query", method={RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	@ResponseBody
	public String onNormalFileInfoQueryLoaded(HttpServletRequest request) {
		AnalyseResult<NormalFileInfoQueryBeans.RequestData, NormalFileInfoQueryBeans.ResponseData> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, NormalFileInfoQueryBeans.RequestData.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查会话是否以已经过期
		ReturnEntity retRst = mSessionManageService.checkSessionEstablished(
				requestData.mRequestObj.getUserName(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查文件id是否合法
		if(StringUtils.isEmpty(requestData.mRequestObj.getFileId())) {
			AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_FID_LACK, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询文件信息
		NormalFileInfo fileInfo = mFileService.findFileRecordById(requestData.mRequestObj.getFileId());
		if(null == fileInfo) {
			AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_FILE_NOT_FOUND, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 组织响应数据
		NormalFileInfoQueryBeans.ResponseData responseData = new NormalFileInfoQueryBeans.ResponseData();
		responseData.setHash(fileInfo.getFileHashCode());
		responseData.setVer(fileInfo.getFileVerCode());
		
		// 构造响应字符串
		AnalyseResult<Object, NormalFileInfoQueryBeans.ResponseData> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseData);
		
		return response.mResult;
	}
	
	/**
	 * 文件下载的处理接口。这个方法要实现为安全下载方法，即解析数据时使用安全解密，返回响应数据时只是传数据即可。
	 * @param id 文件id
	 * @return 数据流
	 */
	@RequestMapping(value="files/download", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public ResponseEntity<byte[]> downloadNormalFile(@RequestParam("id") String id) {
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("要查询的文件id是：{0}", id));
		// 根据Id查到文件的路径
		String localPath = mFileService.findFilePathById(id);
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
	
	@RequestMapping(value="files/edit", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onFileEditingLoaded(Model model, HttpServletRequest request) {
		String id = request.getParameter("id");
		if(StringUtils.isEmpty(id)) {
			model.addAttribute("msg", "无id，无法编辑文件！");
			return M_NORMAL_FILES_EDIT_JSP_NAME;
		}
		
		String action = request.getParameter("action");
		
		// 执行edit操作
		if(!StringUtils.isEmpty(action) && action.equalsIgnoreCase("edit")) {
			NormalFileInfo info = mFileService.findFileRecordById(id);
			if(null == info) {
				model.addAttribute("msg", MessageFormat.format("未找到id为【{0}】的记录，无法编辑！", id));
				return M_NORMAL_FILES_EDIT_JSP_NAME;
			}
			
			// 填充页面内容
			model.addAttribute("info", info);
		}
		// 执行删除操作
		else if(!StringUtils.isEmpty(action) && action.equalsIgnoreCase("delete")) {
			// 首先查出记录，找到文件并删除
			NormalFileInfo info = mFileService.findFileRecordById(id);
			if(null == info) {
				model.addAttribute("msg", MessageFormat.format("未找到id为【{0}】的文件记录，无需删除。", id));
			} else {
				// 删除文件
				if(!FileUtils.deleteFile(info.getFilePath())) {
					model.addAttribute("msg", MessageFormat.format("id为【{0}】的文件删除失败，记录删除终止！", id));
				} else {
					mFileService.deleteRecordById(id);
					model.addAttribute("msg", MessageFormat.format("id为【{0}】的文件和记录删除成功！", id));
				}
			}
		} else {
			model.addAttribute("msg", MessageFormat.format("操作【{0}】有误！", action));
		}
		
		return M_NORMAL_FILES_EDIT_JSP_NAME;
	}
	
	@RequestMapping(value="files/edit_commit", method={RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String editFileCommit(Model model, HttpServletRequest request) {
		String recordInd = request.getParameter("file_ind");
		if(StringUtils.isEmpty(recordInd)) {
			model.addAttribute("msg", "ind为空，无法上传文件");
			return M_NORMAL_FILES_EDIT_JSP_NAME;
		}
		
		int ind = -1;
		try {
			ind = Integer.parseInt(recordInd);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if(ind < 0) {
			model.addAttribute("msg", "ind解析失败，无法上传文件");
			return M_NORMAL_FILES_EDIT_JSP_NAME;
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
                if(StringUtils.isEmpty(fileName)) {
                	model.addAttribute("msg", "是否未选择文件？请选择文件再提交！");
                	continue;
                }
                
                String saveFileName = fileName.substring(0, fileName.lastIndexOf("."));
                saveFileName += DateTimeUtil.getCurrentDateTimeStr("yyyyMMddHHmmss");
                saveFileName += fileName.substring(fileName.lastIndexOf("."));
                String savePath = M_NORMAL_FILES_DIR + File.separator + saveFileName;
                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("原有文件名：{0}", fileName));
                Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("保存文件名：{0}", saveFileName));
                
                // 组织文件信息
                NormalFileInfo info = new NormalFileInfo();
        		// 取文件Id
        		info.setId(request.getParameter("file_id"));
        		info.setFileDesc(request.getParameter("file_desc"));
        		int vcode = -1;
        		try {
        			vcode = Integer.parseInt(request.getParameter("file_ver"));
        		} catch (NumberFormatException e) {
        			e.printStackTrace();
        			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("版本号异常，文件【{0}】接收失败！", fileName));
        			continue;
        		}
        		info.setFileVerCode(vcode);
        		
        		// 接收数据
        		boolean receivedSucceed = false;
                try {
    				file.transferTo(new File(savePath));
    				receivedSucceed = true;
    			} catch (IllegalStateException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                if(!receivedSucceed) {
                	Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】接受失败！", fileName));
                	continue;
                }
                info.setFilePath(savePath);
                
                // 计算哈希值
                byte[] hashCodeBytes = MessageDigestUtil.getFileDigestSHA1(savePath);
                if(null == hashCodeBytes || hashCodeBytes.length == 0) {
                	Logger.getAnonymousLogger().log(Level.SEVERE, 
                			MessageFormat.format("文件【{0}】计算哈希值失败，无法保存！", fileName));
                	FileUtils.deleteFile(savePath);
                	continue;
                }
                info.setFileHashCode(new String(Base64Util.bas64Encode(hashCodeBytes)));
                
                // 检查文件信息是否有效
        		if(!info.isValidForFileSaving()) {
        			FileUtils.deleteFile(savePath);
        			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("文件【{0}】信息缺失，无法保存！", fileName));
        			continue;
        		}
        		
        		// 检查数据库，如果存在则更新，否则提示错误
        		if(!mFileService.checkFileRecordExistsByInd(ind)) {
        			model.addAttribute("msg", MessageFormat.format("文件【{0}】不存在，无法编辑！", info.getId()));
        			continue;
        		}
        		
        		// 更新操作
    			mFileService.updateNormalFileInfoByInd(ind, info);
        		model.addAttribute("info", info);
            }
		}
		
		model.addAttribute("msg", "文件编辑成功！");
		
		return M_NORMAL_FILES_EDIT_JSP_NAME;
	}
	
//	/**
//	 * 收到请求文件信息的安全访问，需要身份验证
//	 * @param request 请求
//	 * @return 响应数据
//	 */
//	@RequestMapping(value="files/query_info", method=RequestMethod.POST)
//	@ResponseBody
//	public String onQueryFileInfoSave(HttpServletRequest request) {
//		
//	}
}
