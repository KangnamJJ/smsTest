package spiper.test.db;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.SimInfo;
import com.spier.service.ISimInfoService;

/**
 * 手机卡信息测试类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class SimInfoTest {

	@Reference
	private ISimInfoService mService;
	
	@Test
	public void testSimInfoOperations() {
		for(int i = 0; i < 100; i++) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("进行第【{0}】轮测试……", i));
			
			// 新增
			SimInfo infoCreated = new SimInfo();
			infoCreated.setCountry("China");
			infoCreated.setNumber("18922234351");
			infoCreated.setOperator("China Mobile");
			
			boolean succeed = mService.addSimInfoRecord(infoCreated);
			Assert.isTrue(succeed, "新增数据成功");
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("新增id：{0}", infoCreated.getId()));
			
			// 查询比较
			SimInfo actual = mService.findSimInfoRecordByInd(infoCreated.getId());
			Assert.notNull(actual, "查询得到的信息不为null");
			
			Assert.isTrue(infoCreated.equals(actual), "新增和查询得到的数据是完全相同的");
			
			// 更改
			actual.setCountry("Japan");
			actual.setOperator("ooxx");
			succeed = mService.updateInfoByInd(actual.getId(), actual);
			Assert.isTrue(succeed, "更新操作是否成功");
			
			SimInfo modified = mService.findSimInfoRecordByInd(actual.getId());
			Assert.isTrue(actual.equals(modified), "更新数据是否一致");
			Assert.isTrue(!modified.equals(infoCreated), "与原来数据是否不一致");
			
			// 删除
			mService.deleteSimInfoRecord(actual.getId());
			actual = mService.findSimInfoRecordByInd(actual.getId());
			Assert.isNull(actual, "确认记录被删除");
		}
		
	}
}
