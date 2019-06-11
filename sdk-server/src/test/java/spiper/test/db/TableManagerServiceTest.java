package spiper.test.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.service.table.ITableManagerService;

/**
 * 表管理服务测试类
 * @author GHB
 * @version 1.0
 * @date 2019.1.11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class TableManagerServiceTest  {

	@Reference
	private ITableManagerService mService;
	
	private static final String M_TAB_NAME_1 = "test_tab_1";
	private static final String M_SQL_TAB1_CREATE = "_id bigint(20) NOT NULL AUTO_INCREMENT, " +
          "abb varchar(255) NOT NULL, " +
          "chiness varchar(255) NOT NULL, " +
          "english varchar(255) NOT NULL, " +
          "PRIMARY KEY (_id)";
	
	@Test
	public void testAll() {
		boolean succeed = false;
		
		if(mService.checkTableExists(M_TAB_NAME_1)) {
			succeed = mService.deleteTable(M_TAB_NAME_1);
			Assert.isTrue(succeed, "清理表1是否成功");
		}
		
		// 在没有表测试创建表
		succeed = mService.createTable(M_TAB_NAME_1, M_SQL_TAB1_CREATE);
		Assert.isTrue(succeed);
		// 在有表的情况下测试创建表
		succeed = mService.createTable(M_TAB_NAME_1, M_SQL_TAB1_CREATE);
		Assert.isTrue(succeed, "重复创建");
		
		// 清空表
		succeed = mService.clearTable(M_TAB_NAME_1);
		Assert.isTrue(succeed, "清空表成功");
		
		// 最后清理现场
		succeed = mService.deleteTable(M_TAB_NAME_1);
		Assert.isTrue(succeed);
	}
}
