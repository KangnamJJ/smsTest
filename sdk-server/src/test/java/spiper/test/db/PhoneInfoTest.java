package spiper.test.db;

import java.text.MessageFormat;
import java.util.List;
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
import com.spier.common.bean.db.PhoneInfo;
import com.spier.common.utils.RandomUtil;
import com.spier.service.IPhoneInfoService;

/**
 * 手机信息数据操作测试类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class PhoneInfoTest{

	@Reference
	private IPhoneInfoService mService;
	
	@Test
	public void testAllOperations() {
		for(int i = 0; i < 10; i++) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("进行第【{0}】轮测试……", i));
			
			// 测试入库
			PhoneInfo rawInfo = new PhoneInfo();
			rawInfo.setBrand("Huawei");
			rawInfo.setModel("HW G521");
			rawInfo.setFingerPrint("asdfsdfdsfsdf");
			rawInfo.setIMEI("sfsfdfimfd993r84");
			rawInfo.setIMSI("24247297492347");
			rawInfo.setMac("00:ab:3e:af:4a:55");
			rawInfo.setApiLevel(25);
			rawInfo.setSerialNo("17G5nE");
			rawInfo.setScreenHeight(854);
			rawInfo.setScreenWidth(480);
			rawInfo.setUserAgent("asfasdfsdfsdf safsdfuisdf8asfd 87-sdf");
			rawInfo.generateIdentifyStr();
			
			boolean succeed = mService.addRecord(rawInfo);
			Assert.isTrue(succeed, "检查入库是否成功");
			
			// 测试更新
			PhoneInfo modifiedInfo = PhoneInfo.clone(rawInfo);
			Assert.isTrue(rawInfo.equals(modifiedInfo), "数据克隆是否成功");
			modifiedInfo.setApiLevel(30);
			modifiedInfo.setBrand("Xiao Mi");
			modifiedInfo.generateIdentifyStr();
			succeed = mService.updateRecordByInd(modifiedInfo.getId(), modifiedInfo);
			Assert.isTrue(succeed, "检查更新操作是否成功");
			PhoneInfo modActual = mService.findRecordByInd(modifiedInfo.getId());
			Assert.isTrue(modifiedInfo.equals(modActual), "更新后的数据是否与期望相同");
			Assert.isTrue(!modifiedInfo.equals(rawInfo), "更新后的数据是否与原始数据不同");
			
			// 测试更新国家和运营商
			modifiedInfo.setCountryAbb("MY");
			modifiedInfo.setOperator("Celcom");
			mService.updateCountryAndOp(modifiedInfo.getIdentifyStr(), 
					modifiedInfo.getCountryAbb(), modifiedInfo.getOperator());
			PhoneInfo infoInDB = mService.findRecordByIdentifyStr(modifiedInfo.getIdentifyStr());
			Assert.isTrue(infoInDB != null, "更新国家和运营商后取到的数据应该不为null");
			Assert.isTrue(modifiedInfo.equals(infoInDB), "更新国家和运营的数据应该正确！");
			// 测试给不存在的记录更新国家和运营商
			PhoneInfo notExists = PhoneInfo.clone(modifiedInfo);
			notExists.setIdentifyStr("abdcdafsfoo343479");
			mService.updateCountryAndOp(notExists.getIdentifyStr(), 
					notExists.getCountryAbb(), notExists.getOperator());
			infoInDB = mService.findRecordByIdentifyStr(notExists.getIdentifyStr());
			Assert.isTrue(infoInDB == null, "更新国家和运营商给不存在的记录后取到的数据应该为null");
			
			// 测试删除
			succeed = mService.deleteRecordByInd(modActual.getId());
			Assert.isTrue(succeed, "删除操作是否成功");
			PhoneInfo deleteActual = mService.findRecordByInd(modActual.getId());
			Assert.isTrue(deleteActual == null, "检查数据库中是否已经没有此条数据");
		}
		
	}
	
	@Test
	public void testPhoneInfoMapper() {
		// 生成5个手机信息单元，3个mac重复的，2个无重复的，并插入数据库
		PhoneInfo[] infos = new PhoneInfo[5];
		
		for(int i = 0; i < 3; i++) {
			PhoneInfo info = new PhoneInfo();
			info.setApiLevel(i + 10);
			info.setBrand(RandomUtil.getRandomStr(12));
			info.setFingerPrint(RandomUtil.getRandomStr(12));
			info.setIMEI(RandomUtil.getRandomStr(12));
			info.setIMSI(RandomUtil.getRandomStr(12));
			info.setMac("534535:34234");
			info.setSerialNo(RandomUtil.getRandomStr(12));
			info.setAndroidId(RandomUtil.getRandomStr(12));
			info.generateIdentifyStr();
			
			infos[i] = info;
		}
		
		for(int i = 3; i < 5; i++) {
			PhoneInfo info = new PhoneInfo();
			info.setApiLevel(i + 10);
			info.setBrand(RandomUtil.getRandomStr(12));
			info.setFingerPrint(RandomUtil.getRandomStr(12));
			info.setIMEI(null);
			info.setIMSI(RandomUtil.getRandomStr(12));
			info.setMac(RandomUtil.getRandomStr(12));
			info.setSerialNo(RandomUtil.getRandomStr(12));
			info.setAndroidId(RandomUtil.getRandomStr(12));
			info.generateIdentifyStr();
			
			infos[i] = info;
		}
		
		// 写入数据库
		for(int i = 0; i < infos.length; i++) {
			mService.addRecord(infos[i]);
		}
		
		// 测试查询重复的，是否会返回期望中的列表:findRecordsByMAC
		List<PhoneInfo> list = mService.findRecordsByMAC(infos[0].getMac());
		Assert.isTrue(list != null, "根据mac查询的list不为null");
		Assert.isTrue(list.size() == 3, MessageFormat.format("根据mac查询列表长度：期望【{0}】，实际【{1}】", 3, list.size()));
		
		// 测试查询不重复的findRecordsByIMEI
		for(int i = 0; i < infos.length; i++) {
			list = mService.findRecordsByIMEI(infos[0].getIMEI());
			Assert.isTrue(list != null, "根据mac查询的list不为null");
			Assert.isTrue(list.size() == 1, MessageFormat.format("根据mac查询列表长度：期望【{0}】，实际【{1}】", 1, list.size()));
		}
		
		// 测试查询不重复的androidId
		for(int i = 0; i < infos.length; i++) {
			list = mService.findRecordsByAndroidId(infos[i].getAndroidId());
			Assert.isTrue(list != null, "根据mac查询的list不为null");
			Assert.isTrue(list.size() == 1, MessageFormat.format("根据mac查询列表长度：期望【{0}】，实际【{1}】", 1, list.size()));
		}
		
		for(int i = 3; i < 5; i++) {
			list = mService.findRecordsByKWs(infos[i].getIMEI(), infos[i].getMac(), infos[i].getSerialNo(), infos[i].getAndroidId());
			Assert.isTrue(list != null, "根据全部查询的list不为null");
			Assert.isTrue(list.size() == 1, MessageFormat.format("根据全部查询列表长度：期望【{0}】，实际【{1}】", 1, list.size()));
		}
		
		// 查询不存在
		list = mService.findRecordsByAndroidId("asdfsdf");
		Assert.isTrue(list != null, "根据androidId查询的list不为null");
		Assert.isTrue(list.isEmpty(), "根据androidId查询的list应该为空");
		
		// 查询字段为空
		list = mService.findRecordsByAndroidId("");
		Assert.isTrue(list != null, "查询字段为空不为null");
		Assert.isTrue(list.isEmpty(), "查询字段为空应该为空");
		
		// 查询字段为null
		list = mService.findRecordsByAndroidId(null);
		Assert.isTrue(list != null, "查询字段为null不为null");
		Assert.isTrue(list.isEmpty(), "查询字段为null应该为空");
		
		
		// 最后删除
		for(int i = 0; i < infos.length; i++) {
			mService.deleteRecordByInd(infos[i].getId());
		}
	}
}
