package com.spier.controller.test;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spier.common.utils.RSAUtils;

/**
 * 生成rsa秘钥对的测试页面
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 */
@Controller
public class RSAKeyGenerator {

	/**
	 * 生成RSA秘钥对
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/rsagen", method=RequestMethod.GET)
	public String generateRSAPair(Model model) {
		List<String> pair = RSAUtils.generateRSAKeyPairStr();
		
		String msg = MessageFormat.format("公钥：{0}<br>私钥：{1}", pair.get(0), pair.get(1));
		
		model.addAttribute("msg", msg);
		
		return "rsagen";
	}
}
