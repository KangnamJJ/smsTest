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
import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.service.job.ICPIOfferService;

/**
 * CPI Info DB测试
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class CPIInfoTest {

	@Reference
	private ICPIOfferService mService;
	
//	@Test
	public void testInsertIgnoreExists() {
		// 纯新增操作
		CPIOfferInfo info1 = new CPIOfferInfo();
		info1.mOfferId = 1002;
		info1.mAllowNetworkTypes = "WiFi";
		info1.mCap = 100;
		info1.mCategories = "abc,123";
		info1.mCountries = "CN,US";
		info1.mCurrency = "USD";
		info1.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info1.mForbiddenNetworkTypes = "Mobile";
		info1.mIncent = "0";
		info1.mMarket = "GP";
		info1.mNeedDeviceInfo = "No";
		info1.mOfferName = "测试";
		info1.mPayout = 2.3;
		info1.mPayoutType = "CPI";
		info1.mPlatforms = "IOS";
		info1.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info1.mSubAffBlacklist = "bdc,adfc";
		info1.mTrackLink = "asfsdc.asdfsd.";
		info1.mVersion = "3.2.0.a";
		
		CPIOfferInfo info2 = new CPIOfferInfo();
		info2.mOfferId = 1003;
		info2.mAllowNetworkTypes = "WiFi";
		info2.mCap = 100;
		info2.mCategories = "abc,123";
		info2.mCountries = "CN,US";
		info2.mCurrency = "USD";
		info2.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info2.mForbiddenNetworkTypes = "Mobile";
		info2.mIncent = "0";
		info2.mMarket = "GP";
		info2.mNeedDeviceInfo = "No";
		info2.mOfferName = "测试";
		info2.mPayout = 2.3;
		info2.mPayoutType = "CPI";
		info2.mPlatforms = "IOS";
		info2.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info2.mSubAffBlacklist = "bdc,adfc";
		info2.mTrackLink = "asfsdc.asdfsd.";
		info2.mVersion = "3.2.0.a";
		List<CPIOfferInfo> offersList = new ArrayList<CPIOfferInfo>();
		offersList.add(info1);
		offersList.add(info2);
		boolean succeed = mService.saveCPIOffers(offersList);
		Assert.isTrue(succeed, "纯添加应该成功");
		// 取出所有的对比
		List<CPIOfferInfo> listInDB = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(listInDB, offersList, true), "第一次插入数据应该正确，取出应该正确");
		
		// 有一个存在的和一个新的
		CPIOfferInfo info3 = new CPIOfferInfo();
		info3.mOfferId = 1004;
		info3.mAllowNetworkTypes = "WiFi";
		info3.mCap = 100;
		info3.mCategories = "abc,123";
		info3.mCountries = "CN,US";
		info3.mCurrency = "USD";
		info3.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info3.mForbiddenNetworkTypes = "Mobile";
		info3.mIncent = "0";
		info3.mMarket = "GP";
		info3.mNeedDeviceInfo = "No";
		info3.mOfferName = "测试";
		info3.mPayout = 2.3;
		info3.mPayoutType = "CPI";
		info3.mPlatforms = "IOS";
		info3.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info3.mSubAffBlacklist = "bdc,adfc";
		info3.mTrackLink = "asfsdc.asdfsd.";
		info3.mVersion = "3.2.0.a";
		
		offersList.remove(0);
		offersList.get(0).mCap = 555;
		offersList.add(info3);
		succeed = mService.saveCPIOffers(offersList);
		Assert.isTrue(succeed, "第二次插入操作应该成功");
		
		offersList.clear();
		offersList.addAll(listInDB);
		offersList.add(info3);
		listInDB = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(listInDB, offersList, true), "第二次插入数据应该正确，取出应该正确");
		
		// 清理
		for(CPIOfferInfo info : offersList) {
			mService.deleteById(info.mOfferId);
		}
		
	}
	
	@Test
	public void testInsertOrUpdate() {
		// 纯新增操作
		CPIOfferInfo info1 = new CPIOfferInfo();
		info1.mOfferId = 1002;
		info1.mAllowNetworkTypes = "WiFi";
		info1.mCap = 100;
		info1.mCategories = "abc,123";
		info1.mCountries = "CN,US";
		info1.mCurrency = "USD";
		info1.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info1.mForbiddenNetworkTypes = "Mobile";
		info1.mIncent = "0";
		info1.mMarket = "GP";
		info1.mNeedDeviceInfo = "No";
		info1.mOfferName = "测试";
		info1.mPayout = 2.3;
		info1.mPayoutType = "CPI";
		info1.mPlatforms = "IOS";
		info1.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info1.mSubAffBlacklist = "bdc,adfc";
		info1.mTrackLink = "asfsdc.asdfsd.";
		info1.mVersion = "3.2.0.a";
		
		CPIOfferInfo info2 = new CPIOfferInfo();
		info2.mOfferId = 1003;
		info2.mAllowNetworkTypes = "WiFi";
		info2.mCap = 100;
		info2.mCategories = "abc,123";
		info2.mCountries = "CN,US";
		info2.mCurrency = "USD";
		info2.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info2.mForbiddenNetworkTypes = "Mobile";
		info2.mIncent = "0";
		info2.mMarket = "GP";
		info2.mNeedDeviceInfo = "No";
		info2.mOfferName = "测试";
		info2.mPayout = 2.3;
		info2.mPayoutType = "CPI";
		info2.mPlatforms = "IOS";
		info2.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info2.mSubAffBlacklist = "bdc,adfc";
		info2.mTrackLink = "asfsdc.asdfsd.";
		info2.mVersion = "3.2.0.a";
		List<CPIOfferInfo> offersList = new ArrayList<CPIOfferInfo>();
		offersList.add(info1);
		offersList.add(info2);
		boolean succeed = mService.saveCPIOffers(offersList);
		Assert.isTrue(succeed, "纯添加应该成功");
		// 取出所有的对比
		List<CPIOfferInfo> listInDB = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(listInDB, offersList, true), "第一次插入数据应该正确，取出应该正确");
		
		// 有一个存在的和一个新的
		CPIOfferInfo info3 = new CPIOfferInfo();
		info3.mOfferId = 1004;
		info3.mAllowNetworkTypes = "WiFi";
		info3.mCap = 100;
		info3.mCategories = "abc,123";
		info3.mCountries = "CN,US";
		info3.mCurrency = "USD";
		info3.mDesc = "asdsdc asdfsdf  你好，世界！123";
		info3.mForbiddenNetworkTypes = "Mobile";
		info3.mIncent = "0";
		info3.mMarket = "GP";
		info3.mNeedDeviceInfo = "No";
		info3.mOfferName = "测试";
		info3.mPayout = 2.3;
		info3.mPayoutType = "CPI";
		info3.mPlatforms = "IOS";
		info3.mPreviewLink = "vadfsdfsdfds/asdfsd://sfsf";
		info3.mSubAffBlacklist = "bdc,adfc";
		info3.mTrackLink = "asfsdc.asdfsd.";
		info3.mVersion = "3.2.0.a";
		
		offersList.clear();
		info2.mCap = 555;
		offersList.add(info2);
		offersList.add(info3);
		succeed = mService.saveCPIOffers(offersList);
		Assert.isTrue(succeed, "第二次插入操作应该成功");
		
		offersList.clear();
		offersList.add(info2);
		offersList.add(info3);
		listInDB = mService.getAll();
		Assert.isTrue(!CompareUtil.areListsEqual(listInDB, offersList, true), "第二次插入数据应该正确，取出应该正确");
		
		// 清理
		List<Integer> ids = new ArrayList<Integer>();
		for(CPIOfferInfo offer : listInDB) {
			ids.add(offer.mOfferId);
		}
		mService.batchDeleteByIds(ids);
	}
}
