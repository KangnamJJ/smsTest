package spiper.test.db;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import com.spier.common.bean.db.SimInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.common.utils.DateTimeUtil;
import com.spier.service.IPhoneInfoService;
import com.spier.service.ISimInfoService;
import com.spier.service.IUserInfoService;

/**
 * 用户信息的测试类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class UserInfoTest {

	@Reference
	private IUserInfoService mUserService;
	
	@Reference
	private ISimInfoService mSimService;
	
	@Reference
	private IPhoneInfoService mPhoneService;
	
	@Test
	public void testUserInfo() {
		for(int i = 0; i < 100; i++) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("进行第【{0}】轮测试……", i));
			
			// 测试情况1：无手机信息，无sim卡信息，只有用户信息，且用户信息缺名称
			UserInfo userInfo = new UserInfo();
			userInfo.setState(UserInfo.M_STATE_UNREGSITER);
			userInfo.setSessionEstablishTime(DateTimeUtil.getCurrentDateTimeStr("yyyy-MM-dd HH:mm:ss"));
			userInfo.setSessionId("4324244234");
			userInfo.setAppName("testapp");
			userInfo.setChannelNo("4242342werwer");
			boolean succeed = mUserService.addRecord(userInfo, null, null);
			Assert.isTrue(!succeed, "检查添加应该不成功");
			
			// 测试情况2：无手机信息，无sim卡信息，只有用户信息，用户信息完整
			userInfo.setName("Bush");
			succeed = mUserService.addRecord(userInfo, null, null);
			Assert.isTrue(succeed, "检查添加应该成功");
			// 检查存入的数据是否和当前数据相同
			UserInfo actInfo = mUserService.findRecordByInd(userInfo.getId());
			Assert.isTrue(actInfo != null, "查询得到的结果不应该为null");
			Assert.isTrue(CompareUtil.objsEqual(userInfo,actInfo),"数据正确");
			
			// 测试情况3：无手机信息，有sim卡信息，用户信息缺名称
			SimInfo simInfo = new SimInfo();
			simInfo.setCountry("China");
			simInfo.setNumber("18411111111");
			simInfo.setOperator("oh yeah!");
			userInfo.setName(null);
			succeed = mUserService.addRecord(userInfo, null, simInfo);
			Assert.isTrue(!succeed, "用户信息应该添加失败");
			SimInfo actSimInfo = mSimService.findSimInfoRecordByInd(simInfo.getId());
			Assert.isTrue(actSimInfo != null, "查询得到的数据不应该为null");
			Assert.isTrue(CompareUtil.objsEqual(simInfo, actSimInfo), "手机卡信息入库应该正确");
			
			// 测试情况4：无手机信息，有sim卡信息，用户信息完整
			userInfo.setName("Bush");
			succeed = mUserService.addRecord(userInfo, null, simInfo);
			Assert.isTrue(succeed, "新增用户信息成功");
			
			// 测试情况5：有手机信息，无sim卡信息，用户信息完整
			PhoneInfo phoneInfo = new PhoneInfo();
			phoneInfo.setApiLevel(22);
			phoneInfo.setBrand("abc");
			phoneInfo.setFingerPrint("asfds9 345845-sfd s");
			phoneInfo.setIMEI("sfasfdsf34234");
			phoneInfo.setIMSI("sfasfsdwerwe");
			phoneInfo.setMac("ab:cd:ef:00:12:34");
			phoneInfo.setModel("sdfd");
			phoneInfo.setScreenHeight(300);
			phoneInfo.setScreenWidth(434);
			phoneInfo.setSerialNo("981ns");
			phoneInfo.setUserAgent("sdfdf");
			// 无身份值，手机信息应该插入不成功
			succeed = mUserService.addRecord(userInfo, phoneInfo, null);
			Assert.isTrue(succeed, "用户信息插入成功");
			Assert.isTrue(phoneInfo.getId() <= 0, "手机信息插入不成功应该是这样");
			phoneInfo.generateIdentifyStr();
			succeed = mUserService.addRecord(userInfo, phoneInfo, null);
			Assert.isTrue(succeed, "用户信息插入成功");
			Assert.isTrue(phoneInfo.getId() > 0, "手机信息插入成功");
			PhoneInfo actPhoneInfo = mPhoneService.findRecordByInd(phoneInfo.getId());
			Assert.isTrue(actPhoneInfo != null, "手机信息查找成功");
			Assert.isTrue(CompareUtil.objsEqual(phoneInfo, actPhoneInfo));
			
			// 测试更新
			userInfo.setState(2);
			succeed = mUserService.updateInfoRecordByInd(userInfo.getId(), userInfo);
			Assert.isTrue(succeed, "用户信息更新成功");
			
			// 测试更新权限
			List<String> privs = new ArrayList<String>();
			privs.add("124234234");
			privs.add("smss");
			mUserService.updateUserPrivileges(userInfo.getName(), privs);
			List<UserInfo> listInDB = mUserService.findRecordsByUserName(userInfo.getName());
			UserInfo infoInDb = listInDB.get(0);
			Assert.isTrue(infoInDb != null, "数据库中查出的数据不为null");
			Assert.isTrue(infoInDb.getPrivileges().contains("smss"), "数据库中查出的数据包含smss");
			Assert.isTrue(infoInDb.getIsLoyalty() == 1, "应该是忠实用户");
			
			// 测试删除
			mUserService.deleteInfoRecordByInd(userInfo.getId());
			UserInfo existInfo = mUserService.findRecordByInd(userInfo.getId());
			Assert.isTrue(existInfo == null, "删除的就查不到了");
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("第【{0}】轮测试完成！", i));
		}
		
	}
}
