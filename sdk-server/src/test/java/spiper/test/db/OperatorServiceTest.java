package spiper.test.db;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.OperatorInfo;
import com.spier.service.IOperatorsInfoService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class OperatorServiceTest  {

	@Reference
	private IOperatorsInfoService mService;
	
	@Test
	public void testAll() {
		OperatorInfo info = new OperatorInfo();
		info.setCountryAbb("MY");
		info.setOperatorName("DiGi");
		
		String opTxt = "digi.com.my";
		String[] ss = opTxt.split("\\.");
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("切割后的运营商：【{0}】", ss[0]));
		
		List<String> list = mService.getOperatorNameByAbbAndOpTxt(info.getCountryAbb(), ss[0]);
		String res = list.get(0);
		Assert.isTrue(!StringUtils.isEmpty(res), "应该查询出结果");
		Assert.isTrue(StringUtils.equals(info.getOperatorName(), res), MessageFormat.format(
				"查到的结果[{0}]应该与设置的结果[{1}]相同", res, info.getOperatorName()));
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("最终获取到的运营商：【{0}】", info.getOperatorName()));
	}
}
