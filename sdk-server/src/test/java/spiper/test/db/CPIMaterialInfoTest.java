package spiper.test.db;

import java.util.ArrayList;
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
import com.spier.common.bean.db.CPIMaterialInfo;
import com.spier.service.job.ICPIMaterialInfoService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class CPIMaterialInfoTest {

	@Reference
	private ICPIMaterialInfoService mService;
	
	@Test
	public void testAll() {
		CPIMaterialInfo info1 = new CPIMaterialInfo();
		info1.setPixel("vasfsfsdf");
		info1.setUrl("sdfsd234234lj");
		CPIMaterialInfo info2 = new CPIMaterialInfo();
		info2.setPixel("bbg4g4gr");
		info2.setUrl("98sdfsdf89");
		List<CPIMaterialInfo> rawList = new ArrayList<CPIMaterialInfo>();
		rawList.add(info1);
		rawList.add(info2);
		boolean succeed = mService.addOrUpdateBatch(rawList);
		Assert.isTrue(succeed, "第一次添加应该成功");
		
		
		
		// 清理
		succeed = clearAll(rawList);
		Assert.isTrue(succeed, "清理工作应该成功");
	}
	
	private boolean clearAll(List<CPIMaterialInfo> list) {
		List<CPIMaterialInfo> listInDB = mService.getAll();
		
		List<Integer> ids = new ArrayList<Integer>();
		for(CPIMaterialInfo info : listInDB) {
			for(CPIMaterialInfo item : list) {
				if(StringUtils.equals(info.getPixel(), item.getPixel())
						&& StringUtils.equals(info.getUrl(), item.getUrl())) {
					ids.add(info.getIndex());
					break;
				}
			}
		}
		
		return mService.deleteByIdBatch(ids);
	}
}
