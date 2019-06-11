package com.spier.common.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.http.IKVSerializer;
import com.spier.common.http.XZAnnotations;

/**
 * 属性-值序列化工具类
 * @author GHB
 * @version 1.0
 */
public class KVSerializerUtils {

	/**
	 * 获取对象与值的关系表
	 * @param attr 属性名称
	 * @param obj 对象值
	 * @return 可能为null
	 */
	public static Map<String, String> getObjectValueString(String attr, Object obj) {
		if(null == obj) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument is null, cannot get k-v map.");
			return null;
		}
		
		if(StringUtils.isEmpty(attr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "attr is null or empty, cannot get k-v map.");
			return null;
		}
		
		String res = null;
		Class<?> clazz = obj.getClass();
		// 内建类型
		if(clazz.equals(Character.class) || clazz.equals(char.class)) {
			res = Character.toString((Character) obj);
		} else if(clazz.equals(Integer.class) || clazz.equals(int.class)) {
			res = Integer.toString((Integer) obj);
		} else if(clazz.equals(Short.class) || clazz.equals(short.class)) {
			res = Short.toString((Short) obj);
		} else if(clazz.equals(Long.class) || clazz.equals(long.class)) {
			res = Long.toString((Long) obj);
		} else if(clazz.equals(Float.class) || clazz.equals(float.class)) {
			res = Float.toString((Float) obj);
		} else if(clazz.equals(Double.class) || clazz.equals(double.class)) {
			res = Double.toString((Double) obj);
		} else if(clazz.equals(String.class)) {
			res = (String) obj;
		} else if(clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
			res = Boolean.toString((Boolean) obj);
		} 
		
		if(res != null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(attr, res);
			return map;
		}
		
		// list
		if(obj instanceof List) {
			// 未定义
			Logger.getAnonymousLogger().log(Level.WARNING, "the transformation of list type of data is undefined.");
		}
		// map
		else if(obj instanceof Map) {
			// 未定义
			Logger.getAnonymousLogger().log(Level.WARNING, "the transformation of list map of data is undefined.");
		}
		// 实现了IKVSerializer接口的类型
		else if(obj instanceof IKVSerializer) {
			IKVSerializer serializer = (IKVSerializer) obj;
			return serializer.serilize();
		}
		
		Logger.getAnonymousLogger().log(Level.SEVERE, "type incorrect, null returned.");
		
		return null;
	}
	
	/**
	 * 将kv表展开成Http请求参数
	 * @param data kv表
	 * @param enableUrlEncode 开启url Encode
	 * @param charset 字符集
	 * @return 可能为null
	 */
	public static String extendsKVMap2HttpParams(Map<String, String> data, boolean enableUrlEncode, String charset) {
		if(null == data || data.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument is null, no http param generated.");
			return null;
		}
		
		String format = "{0}={1}";
		StringBuffer buffer = new StringBuffer();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		
		while(it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			
			if(StringUtils.isEmpty(key) || null == value) {
				continue;
			}
			
			String str;
			try {
				str = enableUrlEncode ? 
						MessageFormat.format(format, key, URLEncoder.encode(value, charset)) :
							MessageFormat.format(format, key, value);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				continue;
			}
			
			buffer.append(str);
			
			if(it.hasNext()) {
				buffer.append("&");
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * 获取一个对象中，使用了某个注解标注的属性名称-值对。如果参数annotationClass，则功能同{@link #getMemberValuePairs(Object)}
	 * @param obj 对象
	 * @param annotationClass 注解类
	 * @return 可能为null
	 */
	public static Map<String, Object> getMemberValuePairsByAnnotation(Object obj, 
			Class<? extends XZAnnotations.KVSerialize> annotationClass) {
		if(null == annotationClass) {
			return getMemberValuePairs(obj);
		}
		
		Map<String, Object> res = null;
		
		if(null == obj) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "输入参数为null，无法获得属性-值");
			return res;
		}
		
		Field[] fields = getAllDeclaredFields(obj, true);
		if(null == fields || fields.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未获得属性，无法获得属性-值！");
			return res;
		}
		
		res = new HashMap<String, Object>();
		for(Field f : fields) {
			if(null == f) {
				continue;
			}
			
			if(!f.isAnnotationPresent(annotationClass)) {
				continue;
			}
			
			// 取注解的值
			String annotationValue = null;
			XZAnnotations.KVSerialize ann = f.getAnnotation(annotationClass);
			if(null != ann) {
				annotationValue = ann.value();
			}
			
			try {
				// 取名称
				String name = f.getName();
				if("this$0".equals(name)) {
					continue;
				}
				
				// 设置注解的值
				if(!StringUtils.isEmpty(annotationValue)) {
					name = annotationValue;
				}
				
				// 取属性的值
				boolean accessible = f.isAccessible();
				if(!accessible) {
					f.setAccessible(true);
				}
				
				Object val = f.get(obj);
				
				f.setAccessible(accessible);
				
				res.put(name, val);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	/**
	 * 获得某个对象的所有属性-值对，包括公有属性和受保护属性
	 * @param obj 对象
	 * @return 属性-值对表；null-有错误发生
	 */
	public static Map<String, Object> getMemberValuePairs(Object obj) {
		Map<String, Object> res = null;
		
		if(null == obj) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "输入参数为null，无法获得属性-值");
			return res;
		}
		
		Field[] fields = getAllDeclaredFields(obj, true);
		if(null == fields || fields.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未获得属性，无法获得属性-值！");
			return res;
		}
		
		res = new HashMap<String, Object>();
		for(Field f : fields) {
			if(null == f) {
				continue;
			}
			
			try {
				String name = f.getName();
				if("this$0".equals(name)) {
					continue;
				}
				
				boolean accessible = f.isAccessible();
				if(!accessible) {
					f.setAccessible(true);
				}
				
				Object val = f.get(obj);
				
				f.setAccessible(accessible);
				
				res.put(name, val);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	/**
	 * 通过属性名称将值设置给属性
	 * @param o 要设置的对象
	 * @param memberName 属性名称
	 * @param value 值
	 * @return 操作是否成功
	 */
	public static boolean setValueToMember(Object o, String memberName, Object value) {
		if(StringUtils.isEmpty(memberName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "member name is empty, cannot set value");
			return false;
		}
		
		if(null == o) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "object is null, cannot set value");
			return false;
		}
		
		Field[] fields = getAllDeclaredFields(o, true);
		if(null == fields || fields.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未获得属性，无法设置属性值！");
			return false;
		}
		
		boolean res = false;
		for(Field f : fields) {
			if(null == f) {
				continue;
			}
			
			String name = f.getName();
			if("this$0".equals(name)) {
				continue;
			}
			
			if(!StringUtils.equals(name, memberName)) {
				continue;
			}
			
			boolean accessible = f.isAccessible();
			if(!accessible) {
				f.setAccessible(true);
			}
			
			try {
				f.set(o, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			f.setAccessible(accessible);
			
			res = true;
			break;
		}
		
		return res;
	}
	
	/**
	 * 从一个对象中取出所有属性，包括公有的和受保护的。
	 * @param obj
	 * @param includeInherited 是否包含从父类继承的属性
	 * @return 所有属性；null - obj为null
	 */
	public static Field[] getAllDeclaredFields(Object obj, boolean includeInherited) {
		if(null == obj) {
			return null;
		}
		
		Class<?> clazz = obj.getClass();
		Field[] tmp = clazz.getDeclaredFields();
		if(!includeInherited) {
			return tmp;
		}
		
		List<Field> res = new ArrayList<Field>();
		for(; clazz != Object.class; clazz = clazz.getSuperclass()) {
			res.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		
		return res.toArray(new Field[res.size()]);
	}
}
