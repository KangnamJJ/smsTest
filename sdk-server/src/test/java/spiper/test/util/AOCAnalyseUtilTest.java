package spiper.test.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.util.Assert;

import com.spier.common.utils.AOCAnalyseUtil;
import com.spier.common.utils.CompareUtil;

public class AOCAnalyseUtilTest {

	@Test
	public void testAOCAnalyse() {
		String raw = "[{\"address\":\"6 ​ข้อ​ความ​ใหม่\",\"content\":\"AIS  You OTP to subscribe content service is 3152. Please enter OTP to assure your intention to subscribe the service.---4187219  ขอบคุณที่ใช้บริการ Birthday Horo(5บ/ขค.) สอบถามโทร 02-965-7087 ยกเลิกกด *137 โทรออก\",\"date\":1555060319519,\"hasRead\":false,\"type\":1}]";
		List<String> acturally = AOCAnalyseUtil.analyseAOCContent(raw);
		List<String> expect = new ArrayList<String>();
		expect.add("3152");
		expect.add("1555060319519");
		
		boolean res = CompareUtil.areListsEqual(expect, acturally, true);
		Assert.isTrue(res, "解析到的内容应该与期望值相符");
		
		raw = "[{\"address\":\"AIS\",\"content\":\"รหัสสมัครบริการเสริมของคุณคือ 6518 กรุณาใส่รหัสยืนยันการสมัครด้วยตนเอง\",\"date\":1555143383614,\"hasRead\":false,\"type\":1}]";
		acturally = AOCAnalyseUtil.analyseAOCContent(raw);
		expect = new ArrayList<String>();
		expect.add("6518");
		expect.add("1555143383614");
		res = CompareUtil.areListsEqual(expect, acturally, true);
		Assert.isTrue(res, "解析到的内容应该与期望值相符");
		
		raw = "[{\"address\":\"AIS\",\"content\":\"รหัสสมัครบริการเสริมของคุณคือ 1625 กรุณาใส่รหัสยืนยันการสมัครด้วยตนเอง\",\"date\":1555145540700,\"hasRead\":false,\"type\":1}]";
		acturally = AOCAnalyseUtil.analyseAOCContent(raw);
		expect = new ArrayList<String>();
		expect.add("1625");
		expect.add("1555145540700");
		res = CompareUtil.areListsEqual(expect, acturally, true);
		Assert.isTrue(res, "解析到的内容应该与期望值相符");
		
		raw = "รหัสสมัครบริการเสริมของคุณคือ 2996 กรุณาใส่รหัสยืนยันการสมัครด้วยตนเอง รหัสสมัครบริการเสริมของคุณคือ 6666 กรุณาใส่รหัสยืนยันการสมัครด้วยตนเอง รหัสสมัครบริการเสริมของคุณคือ 7777 กรุณาใส่รหัสยืนยันการสมัครด้วยตนเอง";
		acturally = AOCAnalyseUtil.analyseAOCContent(raw);
		expect = new ArrayList<String>();
		expect.add("2996");
		expect.add("6666");
		expect.add("7777");
		res = CompareUtil.areListsEqual(expect, acturally, true);
		Assert.isTrue(res, "解析到的内容应该与期望值相符");
	}
}
