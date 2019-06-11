package spiper.test.net;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.SDKServerStart;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.service.saferequest.ISafeRequestParserService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class SafeRequestParserServiceTest  {

	@Reference
	private ISafeRequestParserService mRequestParserService;
	
	@Test
	public void testAll() {
		testGenResponseStr();
	}
	
	private static final String M_CHAN_ID = "20190113194237";
	private void testGenResponseStr() {
		TestResponse response = new TestResponse();
		response.ab = "ab";
		response.bc = "hey";
		
		// 有responseObj的，错误码是非正确的
		int errorCode = 123;
		AnalyseResult<Object, TestResponse> respRes = 
				 mRequestParserService.generateResponseStr(errorCode, M_CHAN_ID, response);
		Assert.isTrue(respRes != null, "生成的响应结果不为null");
		Assert.isTrue(respRes.mErrCode == errorCode, "errorcode相等");
		Assert.isTrue(respRes.mIsSucceed, "respRes.mIsSucceed");
		Assert.isTrue(StringUtils.equals(M_CHAN_ID, respRes.mChanId), 
				MessageFormat.format("期望【{0}】，实际【{1}】", M_CHAN_ID, respRes.mChanId));
		Assert.isTrue(!StringUtils.isEmpty(respRes.mResult), MessageFormat.format("res中有响应数据【{0}】", respRes.mResult));
		Assert.isTrue(StringUtils.contains(respRes.mResult, "\"c\":"), MessageFormat.format("期望res【{0}】中包含c部分", respRes.mResult));
		
		// 有responseObj的，错误码是正确的
		errorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		respRes = 
				 mRequestParserService.generateResponseStr(errorCode, M_CHAN_ID, response);
		Assert.isTrue(respRes != null, "生成的响应结果不为null");
		Assert.isTrue(respRes.mErrCode == errorCode, "errorcode相等");
		Assert.isTrue(respRes.mIsSucceed, "respRes.mIsSucceed");
		Assert.isTrue(StringUtils.equals(M_CHAN_ID, respRes.mChanId), 
				MessageFormat.format("期望【{0}】，实际【{1}】", M_CHAN_ID, respRes.mChanId));
		Assert.isTrue(!StringUtils.isEmpty(respRes.mResult), MessageFormat.format("res中有响应数据【{0}】", respRes.mResult));
		Assert.isTrue(StringUtils.contains(respRes.mResult, "\"c\":"), 
				MessageFormat.format("期望res【{0}】中包含c部分", respRes.mResult));
		
		// 无responseObj的，错误码是非正确的
		errorCode = 123;
		respRes = 
				 mRequestParserService.generateResponseStr(errorCode, M_CHAN_ID, null);
		Assert.isTrue(respRes != null, "生成的响应结果不为null");
		Assert.isTrue(respRes.mErrCode == errorCode, "errorcode相等");
		Assert.isTrue(respRes.mIsSucceed, "respRes.mIsSucceed");
		Assert.isTrue(StringUtils.equals(M_CHAN_ID, respRes.mChanId), 
				MessageFormat.format("期望【{0}】，实际【{1}】", M_CHAN_ID, respRes.mChanId));
		Assert.isTrue(!StringUtils.isEmpty(respRes.mResult), MessageFormat.format("res中有响应数据【{0}】", respRes.mResult));
		Assert.isTrue(!StringUtils.contains(respRes.mResult, "\"c\":"), 
				MessageFormat.format("期望res【{0}】中不包含c部分", respRes.mResult));
		
		// 无responseObj的，错误码是正确的
		errorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		respRes = 
				 mRequestParserService.generateResponseStr(errorCode, M_CHAN_ID, null);
		Assert.isTrue(respRes != null, "生成的响应结果不为null");
		Assert.isTrue(respRes.mErrCode == errorCode, "errorcode相等");
		Assert.isTrue(respRes.mIsSucceed, "respRes.mIsSucceed");
		Assert.isTrue(StringUtils.equals(M_CHAN_ID, respRes.mChanId), 
				MessageFormat.format("期望【{0}】，实际【{1}】", M_CHAN_ID, respRes.mChanId));
		Assert.isTrue(!StringUtils.isEmpty(respRes.mResult), MessageFormat.format("res中有响应数据【{0}】", respRes.mResult));
		Assert.isTrue(!StringUtils.contains(respRes.mResult, "\"c\":"), 
				MessageFormat.format("期望res【{0}】中不包含c部分", respRes.mResult));
	}
	
	private static class TestRequest {
		@Expose
		@SerializedName("a")
		public int a;
		
		@Expose
		@SerializedName("b")
		public String b;
	}
	
	private static class TestResponse {
		@Expose
		@SerializedName("a")
		public String ab;
		
		@Expose
		@SerializedName("b")
		public String bc;
	}
}
