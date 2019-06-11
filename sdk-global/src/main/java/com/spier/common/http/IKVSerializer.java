package com.spier.common.http;

import java.util.Map;


/**
 * 对象属性-值序列化接口
 * @author GHB
 * @version 1.0
 */
public interface IKVSerializer {

	/**
	 * 序列化，将属性-值序列化成字符串
	 * @return 不为null
	 */
	public Map<String, String> serilize();
}
