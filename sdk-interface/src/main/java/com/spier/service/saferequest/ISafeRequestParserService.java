package com.spier.service.saferequest;

import javax.servlet.http.HttpServletRequest;

import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;

/**
 * 安全通信接口解析服务接口
 * @author GHB
 * @version 1.0
 * @date 2018.12.31
 */
public interface ISafeRequestParserService {

	/**
	 * 解析请求，获取请求的数据，结果存储在返回值中
	 * @param request
	 * @param requestObjClazz 要解析的请求对象的类型
	 * @param requestHasParam 此请求是否有参数
	 * @return 操作结果，内含错误码，不为null
	 */
	public <RequestType, ResponseType> AnalyseResult<RequestType, ResponseType> deserializeRequestFirstLayer(
			HttpServletRequest request, Class<?> requestObjClazz, boolean requestHasParam);
	
	/**
	 * 生成最终的响应数据，此数据就是直接发往客户端的数据
	 * @param errorCode 之前操作的错误码。如果错误码不是{@link ErrorCodes#M_ERR_CODE_SUCCEED}, 则不理会第二个参数，直接发送错误码给客户端
	 * @param chanId 客户端请求的渠道号
	 * @param responseObj 响应数据，为null代表响应中无数据
	 * @return 操作结果，不为null
	 */
	public <RequestType, ResponseType> AnalyseResult<RequestType, ResponseType> generateResponseStr(
			int errorCode, String chanId, ResponseType responseObj);
}
