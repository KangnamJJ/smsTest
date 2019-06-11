package spiper.test.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.service.channel.IChannelInfoService;

/**
 * 渠道信息测试
 * @author GHB
 * @version 1.0
 * @date 2018.12.31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class ChanInfoTest {
	@Reference
	private IChannelInfoService mService;
	
	@Test
	public void testChanInfoOperations() {
		// 插入数据
		ChannelInfo info1 = new ChannelInfo();
		info1.setChanDesc("asdfdscvv你好");
		info1.setChannelNo("cvdsf1324");
		info1.setRsaPrivKB64Str("vsdfs2342fsfsdf-=sxc");
		info1.setRsaPubKB64Str("sdffxcvxsd00234");
		boolean succeed = mService.insertChanInfo(info1);
		Assert.isTrue(succeed, "首次插入正常");
		
		// 根据渠道号查询信息
		ChannelInfo info2 = mService.getChanInfoByChanNo(info1.getChannelNo());
		Assert.isTrue(CompareUtil.objsEqual(info1, info2));
		
		// 根据索引号查询
		info2 = mService.getChanInfoByIndex(info1.getIndex());
		Assert.isTrue(CompareUtil.objsEqual(info1, info2));
		
		// 获取rsapriv
		String K = mService.getRSAPrivKB64StrByChanNo(info1.getChannelNo());
		Assert.isTrue(info2.getRsaPrivKeyB64Str().equals(K), "取出的私钥应该正确");
		
		// 删除
		mService.deleteByIndex(info1.getIndex());
	}
}
