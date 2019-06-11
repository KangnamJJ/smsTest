package com.spier.controller.test;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spier.common.utils.AnalyseResult;
import com.spier.service.saferequest.ISafeRequestParserService;

/**
 * 此类用于回声测试
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 */
@Controller
public class Echo {

//	private static final String M_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDmtrv9rnej7PX/HNO6h4AOOh1BTRhusGmdAxaiDftJyqciNDL+n57pUtPlgn/VRJ5j3CjUYong1Og2brCCqkuPHSPc/tu6jOkOHyMFQSEAvEj6A+7o4DBVhZfxAjXP662Km3kOJoWUGKpWezoB5O1d0LR5B/RL1XzXBkPuWeeUBwIDAQAB";
//	private static final String M_PRIV_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOa2u/2ud6Ps9f8c07qHgA46HUFNGG6waZ0DFqIN+0nKpyI0Mv6fnulS0+WCf9VEnmPcKNRiieDU6DZusIKqS48dI9z+27qM6Q4fIwVBIQC8SPoD7ujgMFWFl/ECNc/rrYqbeQ4mhZQYqlZ7OgHk7V3QtHkH9EvVfNcGQ+5Z55QHAgMBAAECgYEA492nXq5qS+VGMWlvVNNvjp2nN0/Fyc1DAmiCehwFDKSy3f1gQEu20AuWbR+u0hyrs0VGrj2EoCKoFtrMSsGXTSoIBvVyZQP9qkSKGn1IuZjISz1XZ+kbED1LQHYs/icnMHU2etR9oBUS0EPCSAgbyhVmZvteCg+8OepeAduiSEECQQD2M8fp4hnGHquZxcMENPNTwIykv7tQEwv4axYIQb8sagk8VJpz/Eq2fvScsFY3UGN535PsfcPZyeugBk0E29CNAkEA7+UpoFgorVdhRzzMwr85CLgf5QLsoqzlePGB52QFyINbSoyEG1us2WjWgBT/pDuMo2YS8l/S4lSP7vU4kLwD4wJAf0AaNr9fFPBrPypg9n6ruYEO8GQM24FMsZQfqhrZRYp0xZLw1bvzncmWgpn9pc9N6wLtANGr9ZgQygAXtMgsTQJAWznQHJOp1FE4QvjR/PTu4WvzZQk5p5sMIHPmrHGS6W9aVf0dd7HjB0Ig39Acbv3eO3zjk5vw+/SgT4SdIBz/dQJAWAQ9qJt84/BUf/37XiWky+XedOqNhURWukpxp7XUIgkyBeKMZH6eOneRA4XpPKsS8PyMOB2ZRqJr9xNQOY5lug==";
	
	@Autowired
	private ISafeRequestParserService mParserService;
	
	/**
	 * 回声测试接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/echo", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String doEcho(HttpServletRequest request) {
		
		// 解析请求数据，因此接口约定请求参数和响应参数就是一个字符串，因此泛型都填写String
		AnalyseResult<String, String> res = mParserService.deserializeRequestFirstLayer(request, String.class, true);
		
		// 生成响应数据，无论处理是否成功，都要回送数据。
		if(!res.mIsSucceed) {
			res = mParserService.generateResponseStr(res.mErrCode, res.mChanId, null);
		} else {
			Logger.getAnonymousLogger().log(Level.INFO, "request analysed, now generate response ...");
			// 直接传请求中的字符串
			String responseObj = res.mRequestObj;
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("解析后的请求数据明文是：{0}", responseObj));
			res = mParserService.generateResponseStr(res.mErrCode, res.mChanId, responseObj);
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("响应数据：{0}", res.mResult));
		
		return res.mResult;
	}
}
