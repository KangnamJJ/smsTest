package spiper.test.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.CPICampaignInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.service.job.ICPICampaignInfoService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class CPICampaignInfoTest {

	@Reference
	private ICPICampaignInfoService mService;
	
	@Test
	public void testAll() {
		CPICampaignInfo info1 = new CPICampaignInfo();
		info1.setCampaign("asfdsf");
		info1.setDesc("safsfdsf-sdfasd f sdfsd f");
		info1.setIconIds("sfsdf,sadfsdfd,sdfsdf");
		info1.setMaterialIds("sdfsd,sfsdfsdfsdf,erwe,r,w");
		info1.setOfferIds("242jk234,234234j2432,234234d,sfd");
		info1.setPkgName("asfsd.sfsdfsdf.sdfsdf");
		
		CPICampaignInfo info2 = new CPICampaignInfo();
		info2.setCampaign("asfdsf");
		info2.setDesc("safsfdsf-sdfasd f sdfsd f");
		info2.setIconIds("sfsdf,sadfsdfd,sdfsdf");
		info2.setMaterialIds("sdfsd,sfsdfsdfsdf,erwe,r,w");
		info2.setOfferIds("1123,234234j2432,234234d,sfd");
		info2.setPkgName("sf.sfsdfsdsff.sdfs33df");
		
		List<CPICampaignInfo> list = new ArrayList<CPICampaignInfo>();
		list.add(info1);
		list.add(info2);
		
		boolean succeed = mService.addOrUpdateCampaignsBatch(list);
		Assert.isTrue(succeed, "纯插入应该成功");
		
		// 第二次插入
		CPICampaignInfo info3 = new CPICampaignInfo();
		info3.setCampaign("asfdsf");
		info3.setDesc("safsfdsf-sdfasd f sdfsd f");
		info3.setIconIds("sfsdf,sadfsdfd,sdfsdf");
		info3.setMaterialIds("sdfsd,sfsdfsdfsdf,erwe,r,w");
		info3.setOfferIds("1314,234234j2432,234234d,sfd");
		info3.setPkgName("vdff.htty.cddsd");
		info2.setIconIds("14234kj2jk34");
		List<CPICampaignInfo> newList = new ArrayList<CPICampaignInfo>();
		newList.add(info2);
		newList.add(info3);
		succeed = mService.addOrUpdateCampaignsBatch(newList);
		Assert.isTrue(succeed, "更新插入应该成功");
		// 检查数据正确性
		newList.add(info1);
		
		List<CPICampaignInfo> listInDB = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(listInDB, newList, true), "更新插入后数据应该是正确的");
		
		succeed = clearAll(newList);
		Assert.isTrue(succeed, "清理是否成功");
	}
	
	private boolean clearAll(List<CPICampaignInfo> list) {
		List<String> ids = new ArrayList<String>();
		for(CPICampaignInfo info : list) {
			ids.add(info.getOfferIds());
		}
		
		return mService.deleteCampaignsByIdBatch(ids);
	}
}
