package com.spier.common.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此类定义了自定义注解
 * @author GHB
 * @version 1.0
 */
public class XZAnnotations {

	/**
	 * KV序列化标记，使用此注解标记的属性将被KV序列化，否则忽略。
	 * @author GHB
	 * @version 1.0
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface KVSerialize {
	    String value() default "";
	}
}
