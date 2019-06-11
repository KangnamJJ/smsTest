package com.spier.common.config;

/**
 * 错误码定义类，所有的错误码都在此定义
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 */
public class ErrorCodes {

	/**
	 * 错误码——刚初始化，还未解析：{@value}
	 */
	public static final int M_ERR_CODE_NOT_ANALYSE 			= 0x00000002;
	/**
	 * 错误码——成功：{@value}
	 */
	public static final int M_ERR_CODE_SUCCEED 				= 0x00000001;
	
	/**
	 * 错误码——请求中数据部分为空：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_DATA_EMPTY 	= 0x00000003;
	/**
	 * 错误码——请求数据base64解码失败：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_DATA_B64_DEC = 0x00000004;
	/**
	 * 错误码——请求数据AES解密失败：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_DATA_AES_DEC = 0x00000005;
	/*
	 * 错误码——请求数据字符串不是UTF-8编码：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_DATA_NOT_UTF8 = 0x00000006;
	/**
	 * 错误码——请求中所有的数据均缺失：{@value}
	 */
	public static final int M_ERR_CODE_NO_OUTLAYER_REQUEST	= 0x00000007;
	/**
	 * 错误码——最外层请求数据反序列化失败：{@value}
	 */
	public static final int M_ERR_CODE_DESERIALIZE_OUTLAYER = 0x00000008;
	/**
	 * 错误码——请求中无渠道号数据：{@value}
	 */
	public static final int M_ERR_CODE_NO_CHANNEL_ID		= 0x00000009;
	/**
	 * 错误码——替换回车换行符失败：{@value}
	 */
	public static final int M_ERR_CODE_REPLACE_N_R			= 0x00000010;
	
	/**
	 * 错误码——响应数据为空，无法加密：{@value}
	 */
	public static final int M_ERR_CODE_RESP_DATA_EMPTY 		= 0x00000015;
	/**
	 * 错误码——响应数据base64编码失败：{@value}
	 */
	public static final int M_ERR_CODE_RESP_DATA_B64		= 0x00000021;
	/**
	 * 错误码——响应数据utf-8编码失败：{@value}
	 */
	public static final int M_ERR_CODE_RESP_DATA_UTF8		= 0x00000022;
	/**
	 * 错误码——响应数据序列化失败：{@value}
	 */
	public static final int M_ERR_CODE_RESP_DATA_SERIALIZE	= 0x00000023;
	
	/*
	 * 错误码——aeskey为空：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_AES_EMPTY 	= 0x00000100;
	/**
	 * 错误码——aeskey解码失败：{@value}
	 */
	public static final int M_ERR_CODE_REQUEST_AES_DEC_B64 	= 0x00000101;
	
	/**
	 * 错误码——用rsa解密aes秘钥失败：{@value}
	 */
	public static final int M_ERR_CODE_AES_KEY_DEC_CLEAR	= 0x00000102;
	
	
	
	/**
	 * 错误码——根据渠道号未查到对应的rsa私钥：{@value}
	 */
	public static final int M_ERR_CODE_RSA_PRIV_MPTY 		= 0x00000200;
	/**
	 * 错误码——RSA私钥加密失败：{@value}
	 */
	public static final int M_ERR_CODE_RSA_PRIV_ENC			= 0x00000201;
	
	/* ============================ 业务相关的错误码如下 ============================ */
	
	/**
	 * app名称无效：{@value}
	 */
	public static final int M_ERR_CODE_APP_NAME_INVALID		= 0x01000000;
	
	/**
	 * 注册时无用户手机信息：{@value}
	 */
	public static final int M_ERR_CODE_REG_NO_PHONE_INFO	= 0x01000001;
	
	/**
	 * 注册时用户手机信息中重要的信息缺失：{@value}
	 */
	public static final int M_ERR_CODE_REG_PHONE_INFO_INV	= 0x01000002;
	/**
	 * 转换数据库使用的手机信息单元时发生错误：{@value}
	 */
	public static final int M_ERR_CODE_REG_DB_PH_INFO_FAIL	= 0x01000003;
	/**
	 * 注册时发现手机数据存在于多条记录中，且多条记录的关键信息并不完全相同，怀疑为刷量
	 */
	public static final int M_ERR_CODE_REG_DB_PH_INFO_DUP	= 0x01000004;
	
	/**
	 * 注册时用户转化数据库使用的用户信息单元失败:{@value}
	 */
	public static final int M_ERR_CODE_REG_USR_GEN_FAIL 	= 0x01000005;
	/**
	 * 注册时，用户信息入库失败：{@value}
	 */
	public static final int M_ERR_CODE_REG_INS_FAIL			= 0x01000006;
	/**
	 * 更新时，用户信息入库失败：{@value}
	 */
	public static final int M_ERR_CODE_REG_UPD_FAIL			= 0x01000007;
	/**
	 * 用户登录时用户状态更新失败：{@value}
	 */
	public static final int M_ERR_CODE_LOGIN_STATE_SAVE_FAIL	= 0x01000008;
	/**
	 * 用户未注册：{@value}
	 */
	public static final int M_ERR_CODE_USER_UNREGISTER		= 0x01000100;
	
	/**
	 * 用户未登陆：{@value}
	 */
	public static final int M_ERR_CODE_USER_UNLOGIN			= 0x01000101;
	/**
	 * 用户名或应用名缺失：{@value}
	 */
	public static final int M_ERR_CODE_UN_AN_LACK			= 0x01000102;
	/**
	 * 会话id缺失：{@value}
	 */
	public static final int M_ERR_CODE_SID_LACK				= 0x01000103;
	/**
	 * 文件id缺失：{@value}
	 */
	public static final int M_ERR_CODE_FID_LACK				= 0x01000104;
	/**
	 * 渠道号缺失：{@value}
	 */
	public static final int M_ERR_CODE_CHNO_LACK			= 0x01000105;
	/**
	 * 会话已超时：{@value}
	 */
	public static final int M_ERR_CODE_SESSION_TIMEOUT		= 0x01000106;
	/**
	 * 查无此文件：{@value}
	 */
	public static final int M_ERR_CODE_FILE_NOT_FOUND		= 0x01000107;
	
	/**
	 * 渠道号有误，查询不到渠道信息
	 */
	public static final int M_ERR_CODE_WRONG_CHAN_ID		= 0x01000108;
	
	/**
	 * 渠道任务关闭
	 */
	public static final int M_ERR_CODE_CHAN_SWITCH_OFF		= 0x01000109;
	
	/**
	 * 国家名称无效
	 */
	public static final int M_ERR_CODE_COUNTRY_INVALID		= 0x0100010A;
	
	/**
	 * 运营商名称无效
	 */
	public static final int M_ERR_CODE_OP_NAME_INVALID		= 0x0100010B;
	
	/**
	 * 用户任务数已经达到上限
	 */
	public static final int M_ERR_CODE_USER_MAX_TASKS		= 0x0100010C;
	
	/**
	 * 无合适的任务
	 */
	public static final int M_ERR_CODE_NO_SUITABLE_TASK		= 0x0100010D;
	
	/**
	 * 生成任务失败
	 */
	public static final int M_ERR_CODE_GEN_TASK				= 0x0100010E;
	
	/**
	 * 更改用户任务状态失败
	 */
	public static final int M_ERR_CODE_CHANGE_TASK_STATE	= 0x0100010F;
	
	/**
	 * 任务flowid无效
	 */
	public static final int M_ERR_CODE_INVALID_FLOW_ID		= 0x01000110;
	
	/**
	 * 用户数据不完整
	 */
	public static final int M_ERR_CODE_INCOMP_USER_ID		= 0x01000111;
	
	/**
	 * 新增或更新埋点信息失败
	 */
	public static final int M_ERR_CODE_RECORD_SPOT			= 0x01000112;
	
	/**
	 * 手机信息上报中缺失手机信息
	 */
	public static final int M_ERR_CODE_REPORT_LACK_PHONE	= 0x01000113;
	
	/**
	 * 无referrer数据
	 */
	public static final int M_ERR_CODE_NO_REFERRER 			= 0x01000114;

	/**
	 * 注册时，ip解析错误
	 */
	public static final int M_ERR_CODE_IP_PARSER_FAIL			= 0x01000006;

	/**
	 * 获取错误码对应的错误信息
	 * @param code
	 * @return 不为null
	 */
	public static String getErrorMsgByErrorCode(int code) {
		String res = "未知错误";
		
		switch(code) {
			case M_ERR_CODE_AES_KEY_DEC_CLEAR:
				res = "用RSA密钥解密AES-Key失败";
			break;
			
			case M_ERR_CODE_APP_NAME_INVALID:
				res = "APP名称无效";
			break;
			
			case M_ERR_CODE_CHAN_SWITCH_OFF:
				res = "本渠道任务处于关闭状态";
			break;
			
			case M_ERR_CODE_CHANGE_TASK_STATE:
				res = "更改用户任务状态时发生错误";
			break;
			
			case M_ERR_CODE_CHNO_LACK:
				res = "传输的数据中缺失渠道号";
			break;
			
			case M_ERR_CODE_COUNTRY_INVALID:
				res = "国家名称无效";
			break;
			
			case M_ERR_CODE_DESERIALIZE_OUTLAYER:
				res = "最外层请求数据反序列化失败";
			break;
			
			case M_ERR_CODE_FID_LACK:
				res = "文件ID缺失";
			break;
			
			case M_ERR_CODE_FILE_NOT_FOUND:
				res = "服务器未找到文件";
			break;
			
			case M_ERR_CODE_GEN_TASK:
				res = "服务器生成任务失败";
			break;
			
			case M_ERR_CODE_INCOMP_USER_ID:
				res = "用户身份数据不完整，用ID、渠道号、会话ID";
			break;
			
			case M_ERR_CODE_INVALID_FLOW_ID:
				res = "任务会话ID无效";
			break;
			
			case M_ERR_CODE_LOGIN_STATE_SAVE_FAIL:
				res = "用户登录时用户状态更新失败";
			break;
			
			case M_ERR_CODE_NO_CHANNEL_ID:
				res = "请求中缺少渠道数据";
			break;
			
			case M_ERR_CODE_NO_OUTLAYER_REQUEST:
				res = "请求中所有的数据均缺失";
			break;
		}
		
		return res;
	}
}
