package spiper.test.net;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.spier.SDKServerStart;
import com.spier.common.bean.net.CPIOfferBeans;
import com.spier.common.bean.net.CPIOfferBeans.Request;
import com.spier.common.http.HttpCPIOfferRequest;
import com.spier.common.http.HttpCtx;
import com.spier.common.http.HttpMode;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class CPIOfferRequestTest {

	@Test
	public void testAll() {
		Request data = new Request();
		data.mToken = "3c5375f07a37ae81170c8bbb25c43d65";
		data.mAffiliateId = 27;
		
		HttpCPIOfferRequest request = new HttpCPIOfferRequest(data);
		HttpCtx ctx = HttpCtx.getDefault();
		ctx.setHttpMode(HttpMode.Get);
		
		request.access(ctx);
		Assert.isTrue(request.isSucceed(), "请求的HTTP层是否成功");
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("请求响应：{0}", request.getHtml()));
		
		CPIOfferBeans.Response response = CPIOfferBeans.Response.fromJsonStr(request.getHtml());
		Assert.isTrue(response != null, "反序列化后的数据不应该为null");
	}
}
