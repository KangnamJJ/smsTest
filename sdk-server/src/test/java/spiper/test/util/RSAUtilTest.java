package spiper.test.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import com.spier.common.utils.RSAUtils;

public class RSAUtilTest {

	@Test
	public void testRSAGen() {
		List<String> pair = RSAUtils.generateRSAKeyPairStr();
		Assert.isTrue(pair != null, "列表不为null");
		Assert.isTrue(pair.size() == 2, "列表子项数量是否正确");
		for(String key : pair) {
			Assert.isTrue(!StringUtils.isEmpty(key), "key不应该为空");
			Logger.getAnonymousLogger().log(Level.INFO, key);
		}
	}
}
