package spiper.test.db;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.service.task.IScriptInfoService;

/**
 * 脚本信息测试类
 * @author GHB
 * @version 1.0
 * @date 2019.1.7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class ScriptInfoTest {

	@Reference
	private IScriptInfoService mScriptInfoService;
	
	@Test
	public void testAll() {
		// 插入一个
		ScriptInfo info1 = new ScriptInfo();
		info1.setCountryAbb("sdafsdf");
		info1.setFilePath("sfsdfsdf");
		info1.setHash("sdfsfsdf=");
		info1.setNetEnv(1);
		info1.setOperator("sfsdfcsdfsd");
		info1.setScriptDescription("fsdfninihao你好sf");
		info1.setScriptId("24234234234");
		info1.setVersionCode(2);
		info1.setShortCode("95588");
		boolean succeed = mScriptInfoService.insertScriptInfo(info1);
		Assert.isTrue(succeed, "第一次插入成功");
		
		// 修改后更新
		info1.setFilePath("2r23423jk23k4j");
		succeed = mScriptInfoService.updateScriptInfoByScriptId(info1.getScriptId(), info1);
		Assert.isTrue(succeed, "更新成功");
		
		// 取出数据比较
		ScriptInfo info2 = mScriptInfoService.findScriptInfoByInd(info1.getIndex());
		Assert.isTrue(info2 != null, "成功取出");
		Assert.isTrue(CompareUtil.objsEqual(info1, info2), "取出数据正确");
		
		// 复杂取数据
		List<String> ids = mScriptInfoService.getScriptIdsByCountryAbbAndOp(info1.getCountryAbb(), info1.getOperator());
		Assert.isTrue(ids != null);
		Assert.isTrue(ids.size() == 1, "应该查询到");
		Assert.isTrue(StringUtils.equals(ids.get(0), info1.getScriptId()), "id应该相同");
		
		ids = mScriptInfoService.getScriptIdsByKeyConds(info1.getCountryAbb(), info1.getOperator(), info1.getNetEnv());
		Assert.isTrue(ids != null);
		Assert.isTrue(ids.size() == 1, "应该查询到");
		Assert.isTrue(StringUtils.equals(ids.get(0), info1.getScriptId()), "id应该相同");
		
		mScriptInfoService.deleteRecordByInd(info1.getIndex());
	}
}
