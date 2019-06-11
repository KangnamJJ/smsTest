/**
 * Copyright: Guangzhou Junbo Network Tech. Co., Ltd.
 * Author:          
 * Date: // 作者、版本及完成日期
 * Description:    // 用于详细说明此程序文件完成的主要功能，与其他模块
                  // 或函数的接口，输出值、取值范围、含义及参数间的控
                  // 制、顺序、独立或依赖等关系
 * Others:         // 其它内容的说明
 *
 */
package com.spier.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * 反射工具。未进行严格的测试，仅可用于逆向工程的开发、测试。<br>
 * 修改记录：<br>
 * 修改人：GHB<br>
 * 修改时间：2016.7.24<br>
 * 修改内容：增加获取类的实例的方法<br>
 * 版本号：1.1<br>
 * 修改人：GHB<br>
 * 修改时间：2016.12.11<br>
 * 修改内容：从冗余类中下沉了一些方法<br>
 * 版本号：1.2<br>
 * 
 * @author GHB
 * @date 2016.3.11
 * @version 1.2
 */
public class ReflectUtils {

	// type获取name的前缀
	private static final String TYPE_NAME_PREFIX = "class ";

	/**
	 * 通过type获取类名
	 * 
	 * @param type
	 * @return 可能为null
	 */
	public static String getClassName(Type type) {
		if (type == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument is null, cannot get classname from type");
			return null;
		}

		String className = type.toString();
		if (className.startsWith(TYPE_NAME_PREFIX)) {
			className = className.substring(TYPE_NAME_PREFIX.length());
		}

		return className;
	}

	/**
	 * 通过type获取类
	 * @param type
	 * @return 可能为null
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(Type type) throws ClassNotFoundException {
		String className = getClassName(type);
		if (StringUtils.isEmpty(className)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no class name from type, cannot get class.");
			return null;
		}
		
		return Class.forName(className);
	}
	
	/**
	 * 获取某个对象的泛型类型，此方法功能不正常！！！
	 * @param object 对象
	 * @return 可能为null-错误发生；空数组-传入的对象不是泛型类型
	 */
	public static Type[] getParameterizedTypes(Object object) {
		if(null == object) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument is null, cannot get parameterized type.");
			return null;
		}
		
	    Type superclassType = object.getClass().getGenericSuperclass();
	    if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
	        return null;
	    }
	    
	    return ((ParameterizedType)superclassType).getActualTypeArguments();
	}
	
	/**
	 * 获取某个对象的泛型类型的类
	 * @param object 对象
	 * @return 可能为null-错误发生或对象类型不是泛型
	 */
	public static Class<?> getParameterizedClass(Object object) {
		Type[] parameterizedTypes = getParameterizedTypes(object);
		if(null == parameterizedTypes || parameterizedTypes.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument 'object' is null or not an instance of parameterized type, "
					+ "cannot get class.");
			return null;
		}
		
		Class<?> res = null;
		try {
			res = (Class<?>)getClass(parameterizedTypes[0]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	/**
	 * 通过类路径找到类
	 * 
	 * @param className
	 *            类完整路径
	 * @param cl
	 *            classloader，可以为null。如果不为null，则先从cl中查找类再从当前classloader查；
	 *            否则只从当前classloader查找
	 * @return 类，或null
	 */
	public static Class<?> getClassByName(String className, ClassLoader cl) {
		Class<?> res = null;

		if (null == className || className.trim().isEmpty()) {
			return res;
		}

		try {
			if (null != cl) {
				res = Class.forName(className, true, cl);
			}

			if (null == res) {
				res = Class.forName(className);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 只从当前classloader查找需要的类
	 * 
	 * @param className
	 *            类的全路径
	 * @return 对应的类或null
	 */
	public static Class<?> getClassByName(String className) {
		return getClassByName(className, null);
	}

	public static List<String> getDeclaredFieldNames(Class<?> clazz) {
		List<String> res = new ArrayList<String>();

		if (null == clazz) {
			throw new IllegalArgumentException("clazz is null");
		}

		Field[] fields = clazz.getDeclaredFields();
		if (null == fields || fields.length == 0) {
			return res;
		}

		for (Field f : fields) {
			if (null == f) {
				continue;
			}

			res.add(f.getName());
		}

		return res;
	}

	/**
	 * 获得某个类的实例
	 * 
	 * @param clazz
	 *            类
	 * @param args
	 *            参数，可以为null
	 * @param argTypes
	 *            参数类型，可以为null
	 * @return 类实例或null
	 */
	public static Object getInstanceofClass(Class<?> clazz, Object[] args,
			Class<?>[] argTypes) {
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument clazz is null, no instance returned.");
			return null;
		}

		Constructor<?> constructor = null;
		Object res = null;
		try {
			if (null == argTypes) {
				constructor = clazz.getDeclaredConstructor(new Class<?>[] {});
			} else {
				constructor = clazz.getDeclaredConstructor(argTypes);
			}

			boolean isAccessibilityModified = false;
			if(!constructor.isAccessible()) {
				constructor.setAccessible(true);
				isAccessibilityModified = true;
			}
			
			if (null == args) {
				res = constructor.newInstance(new Object[] {});
			} else {
				res = constructor.newInstance(args);
			}
			
			if(isAccessibilityModified) {
				constructor.setAccessible(false);
			}

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 获取某个类的实例
	 * 
	 * @param classPath
	 *            类路径
	 * @param args
	 *            构造方法的参数，可以为null
	 * @param argTypes
	 *            构造方法参数类型类型，可以为null
	 * @return 类实例或null
	 */
	public static Object getInstanceofClass(String classPath, Object[] args,
			Class<?>[] argTypes) {

		Class<?> clazz = getClassByName(classPath);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no sensitive class generated by the classPath[{0}], null returned.", 
					classPath));
			return null;
		}

		return getInstanceofClass(clazz, args, argTypes);
	}
	
	

	/**
	 * 获取类声明的所有属性名称，包括公有、保护、私有
	 * 
	 * @param classloader
	 *            可以为null
	 * @param className
	 *            类的全路径
	 * @return 属性名称集合，不为null
	 */
	public static List<String> getDeclaredFieldNames(ClassLoader classloader,
			String className) {
		List<String> res = new ArrayList<String>();

		Class<?> clazz = getClassByName(className, classloader);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "did not find class [{0}], field to exec getDeclaredFieldNames");
			return res;
		}

		return getDeclaredFieldNames(clazz);
	}

	/**
	 * 获取某个类声明的公有属性名称。
	 * 
	 * @param clazz
	 *            类
	 * @return 属性名称的列表，不为null
	 * @throws IllegalArgumentException
	 *             参数为null
	 */
	public static List<String> getFieldNames(Class<?> clazz) {
		List<String> res = new ArrayList<String>();

		if (null == clazz) {
			throw new IllegalArgumentException("clazz is null");
		}

		Field[] fields = clazz.getFields();
		if (null == fields || fields.length == 0) {
			return res;
		}

		for (Field f : fields) {
			if (null == f) {
				continue;
			}

			res.add(f.getName());
		}

		return res;
	}

	/**
	 * 获取某个类声明的公有属性。
	 * 
	 * @param className
	 *            类的全路径
	 * @return 属性名称的列表，不为null
	 */
	public static List<String> getFieldNames(ClassLoader classloader,
			String className) {
		List<String> res = new ArrayList<String>();

		if (null == className || className.trim().isEmpty()) {
			return res;
		}

		Class<?> clazz = getClassByName(className, classloader);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"did not find the class [{0}] in classloader [{1}], failed to exec getFieldNames", 
					className, classloader));
			return res;
		}

		res = getFieldNames(clazz);

		return res;
	}
	
	/**
	 * 获取一个对象的属性，包括静态属性、父类的属性，也包括private和protected的
	 * @param obj 对象
	 * @param fieldName 属性名称
	 * @return 可能为null - 参数不对，或没找到
	 * @since 1.2
	 */
	public static Field getField(Object obj, String fieldName) {
		if(null == obj) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument obj is null, cannot get field from a null-object.");
			return null;
		}
		
		if(StringUtils.isEmpty(fieldName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument fieldName is null, cannot get field of nothing.");
			return null;
		}
		
		return getField(obj.getClass(), fieldName);
	}
	
	/**
	 * 获取一个类声明的所有属性，包括静态属性，也包括本类和基类的所有属性
	 * @param clazz
	 * @param fieldName 属性名称
	 * @return 可能为null
	 * @since 1.2
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		if(null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument clazz is null, cannot get static field");
			return null;
		}
		
		if(StringUtils.isEmpty(fieldName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"argument fieldName is null, cannot get static field of class[{0}].", clazz.getName()));
			return null;
		}
		
		Field res = null;
		for(Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
			try {
				if(c == null) {
					continue;
				}
				
				res = c.getDeclaredField(fieldName);
				if(res != null) {
					break;
				}
			} catch (NoSuchFieldException e) {
				// 因为是查找，这里无需打印
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	/**
	 * 获取某个对象的某个属性的值，支持private属性
	 * 
	 * @param obj 对象
	 * @param fieldName 属性名称
	 * @return 属性对应的值，可能为null
	 * @since 1.0
	 */
	public static Object getFieldValue(Object obj, String fieldName) {
		Field field = getField(obj, fieldName);
		if(null == field) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no field found by the name[{0}], failed to get field value.", fieldName));
			return null;
		}
		
		boolean accessible = field.isAccessible();
		
		field.setAccessible(true);
		
		Object res = null;
		try {
			res = field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		field.setAccessible(accessible);
		
		return res;
	}
	
	/**
	 * 获取一个类的静态属性的值，此属性可以是本类或是父类的公有、保护、私有属性
	 * @param clazz 类
	 * @param fieldName 属性名称
	 * @return 可能为null
	 * @since 1.2
	 */
	public static Object getFieldValue(Class<?> clazz, String fieldName) {
		Field field = getField(clazz, fieldName);
		if(null == field) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("no field found named [{0}].", fieldName));
			return null;
		}
		
		boolean accessible = field.isAccessible();
		
		field.setAccessible(true);
		
		Object res = null;
		try {
			res = field.get(null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		field.setAccessible(accessible);
		
		return res;
	}

	/**
	 * 为某个对象的属性设置值，支持private对象
	 * 
	 * @param obj 要设置属性的对象
	 * @param fieldName 属性名称
	 * @param fieldValue 要设置的值
	 * @return 操作是否成功。
	 */
	public static boolean setFieldValue(Object obj, String fieldName, Object fieldValue) {
		Field field = getField(obj, fieldName);
		if(null == field) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no field found by the name[{0}], failed to set field value.", fieldName));
			return false;
		}
		
		boolean accessible = field.isAccessible();
		
		field.setAccessible(true);
		boolean res = false;
		
		try {
			field.set(obj, fieldValue);
			res = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		field.setAccessible(accessible);
		
		return res;
	}
	
	/**
	 * 设置某个类的静态属性
	 * @param clazz 类
	 * @param fieldName 属性名称
	 * @param fieldValue 属性值
	 * @return 操作是否成功
	 * @since 1.2
	 */
	public static boolean setFieldValue(Class<?> clazz, String fieldName, Object fieldValue) {
		if(null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "clazz is null, cannot set field value.");
			return false;
		}
		
		if(StringUtils.isEmpty(fieldName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "fieldName is empty, cannot set field value.");
			return false;
		}
		
		Field field = getField(clazz, fieldName);
		if(null == field) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no field found by the name[{0}], failed to set field value.", fieldName));
			return false;
		}
		
		boolean accessible = field.isAccessible();
		
		field.setAccessible(true);
		boolean res = false;
		
		try {
			field.set(null, fieldValue);
			res = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		field.setAccessible(accessible);
		
		return res;
	}

	/**
	 * 调用某个对象的某个方法，支持访问private方法
	 * 
	 * @param obj
	 * @param methodName  方法名称
	 * @param args 参数
	 * @param paramTypes 参数对应的类型
	 * @return 方法调用后的返回值，可能为null
	 */
	public static Object callMethod(Object obj, String methodName,
			Object[] args, Class<?>... paramTypes) {
		if (null == obj || null == methodName || methodName.trim().isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"arg1 is null or arg2 [{0}] is illegal, failed to exec callMethod", methodName));
			return null;
		}

		Class<? extends Object> clazz = obj.getClass();
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"can not get class from obj [{0}], failed to exec callMethod", obj));
			return null;
		}

		Object res = null;
		try {
			Method method = clazz.getDeclaredMethod(methodName, paramTypes);
			if (null == method) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"did not find method [{0}] from class [{1}], which is the class of Object [{2}]", 
						methodName, clazz.getName(), obj));
				return res;
			}

			if (method.isAccessible()) {
				res = method.invoke(obj, args);
			} else {
				method.setAccessible(true);
				res = method.invoke(obj, args);
				method.setAccessible(false);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 调用类的静态方法
	 * 
	 * @param classPath
	 *            类的全路径
	 * @param methodName
	 *            要调用的方法名称
	 * @param args
	 *            参数列表
	 * @param paramTypes
	 *            参数类型列表
	 * @return 返回值，可能为null
	 */
	public static Object callStaticMethod(String classPath, String methodName,
			Object[] args, Class<?>... paramTypes) {
		Class<?> clazz = getClassByName(classPath);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no clazz found by the class path[{0}], cannot invoke static method[{1}]", 
					classPath, methodName));
			return null;
		}

		return callStaticMethod(clazz, methodName, args, paramTypes);
	}

	/**
	 * 调用类的静态方法
	 * 
	 * @param clazz
	 *            类
	 * @param methodName
	 *            方法名
	 * @param args
	 *            参数
	 * @param paramTypes
	 *            参数类型
	 * @return 方法的返回值，可能为null
	 */
	public static Object callStaticMethod(Class<?> clazz, String methodName,
			Object[] args, Class<?>... paramTypes) {
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"clazz is null, failed to call the static method {0}", 
					methodName));
			return null;
		}

		if (null == methodName || methodName.trim().isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"method is illegal, failed to call the static method {0}.{1}", 
					clazz.getName(), methodName));
			return null;
		}

		Object res = null;
		try {
			Method m = clazz.getDeclaredMethod(methodName, paramTypes);
			if (m == null) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"there's no method named [{0}] under class [{1}]", 
						clazz.getName(), methodName));
				return null;
			}
			
			if (m.isAccessible()) {
				res = m.invoke(null, args);
			} else {
				m.setAccessible(true);
				res = m.invoke(null, args);
				m.setAccessible(false);
			}

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 调用某个类的静态方法，私有方法也可以调用。
	 * 
	 * @param classloader
	 * @param classPath
	 *            类的完整路径
	 * @param methodName
	 *            方法名
	 * @param args
	 *            参数
	 * @param paramTypes
	 *            参数类型
	 * @return 调用后返回的结果，有可能为null
	 */
	public static Object callStaticMethod(ClassLoader classloader,
			String classPath, String methodName, Object[] args,
			Class<?>... paramTypes) {
		Class<?> clazz = getClassByName(classPath, classloader);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"did not find class [{0}] in classloader [{1}], failed to call the static method [{2}].", 
					classPath, classloader.toString(), methodName));
			return null;
		}

		return callStaticMethod(clazz, methodName, args, paramTypes);
	}

	/**
	 * 返回所有已声明的方法名称
	 * 
	 * @param className
	 *            类全路径
	 * @return 类的方法名称，不可能为null
	 */
	public static List<String> getMethodNames(String className) {
		List<String> res = new ArrayList<String>();

		Class<?> clazz = getClassByName(className);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"can not find the class named [{0}], failed to exec getMethodNames", 
					className));
			return res;
		}

		Method[] methods = clazz.getDeclaredMethods();
		if (null == methods || methods.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no declared method found, failed to exec getMethodNames");
			return res;
		}

		for (Method m : methods) {
			if (null == m) {
				continue;
			}

			res.add(m.getName());
		}

		return res;
	}

	/**
	 * 获得一个类的所有方法对象
	 * 
	 * @param className
	 * @return 不为null
	 * @throws SecurityException
	 *             - If a security manager, s, is present and any of the
	 *             following conditions is met: <br>
	 *             • the caller's class loader is not the same as the class
	 *             loader of this class and invocation of s.checkPermission
	 *             method with RuntimePermission("accessDeclaredMembers") denies
	 *             access to the declared methods within this class<br>
	 *             • the caller's class loader is not the same as or an ancestor
	 *             of the class loader for the current class and invocation of
	 *             s.checkPackageAccess() denies access to the package of this
	 *             class
	 */
	public static Method[] getAllMethods(String className) {
		Method[] res = new Method[] {};

		Class<?> clazz = getClassByName(className);
		if (null == clazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no class named [{0}] found, failed to exec getAllMethods", 
					className));
			return res;
		}

		return clazz.getDeclaredMethods();
	}
}
