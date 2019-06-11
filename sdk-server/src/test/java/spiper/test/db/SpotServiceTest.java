package spiper.test.db;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.CompareUtil;
import com.spier.entity.SpotInfo;
import com.spier.service.spot.ISpotInfoService;

/**
 * 埋点信息服务测试类
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class SpotServiceTest {

	@Reference
	private ISpotInfoService mSpotInfoService;
	
	@Test
	public void testAll() {
		// 测试添加
		SpotInfo info1 = new SpotInfo();
		info1.setAppName("234234");
		info1.setChanNo("asdfsdf");
		info1.setFlowId("bgerterg");
		info1.setInfo("24908sfsdjof");
		info1.setTag("asdfsdfsdfsd");
		info1.setType(GlobalConfig.M_SPOT_TYPE_ERR);
		info1.setUID("2234llkjlj");
		boolean succeed = mSpotInfoService.addOrUpdateRecord(info1);
		Assert.isTrue(succeed, "添加成功");
		
		// 测试更新
		info1.setInfo("55678678-");
		succeed = mSpotInfoService.addOrUpdateRecord(info1);
		Assert.isTrue(succeed, "更新成功");
		SpotInfo info2 = mSpotInfoService.getSpotByKeyConds(info1.getUID(), info1.getChanNo(), info1.getFlowId());
		Assert.isTrue(info2 != null, "查出的记录不为null");
		Assert.isTrue(!CompareUtil.objsEqual(info1, info2), "更新成功");
		
		// 更改会话id插入，应该有2条
		info2.setFlowId("0900900");
		succeed = mSpotInfoService.addOrUpdateRecord(info2);
		Assert.isTrue(succeed);
		List<SpotInfo> spots = mSpotInfoService.getSpotsByUser(info1.getUID(), info1.getChanNo());
		Assert.isTrue(spots.size() == 2, "应该有两条记录");
		
		mSpotInfoService.deleteByInd(info1.getIndex());
		mSpotInfoService.deleteByInd(info2.getIndex());
	}
}
