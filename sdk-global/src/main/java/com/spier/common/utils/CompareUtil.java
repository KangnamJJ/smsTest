package com.spier.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 比较工具类
 * @author GHB
 * @version 1.0
 * @date 2018.12.31
 */
public class CompareUtil {

	/**
	 * 比较两个对象是否相等
	 * @param obj1 
	 * @param obj2
	 * @return
	 */
	public static <T extends Object> boolean objsEqual(T obj1, T obj2) {
		if(null == obj1) {
			return null == obj2;
		}
		
		if(null == obj2) {
			return null == obj1;
		}
		
		return obj1.equals(obj2);
	}
	
	/**
	 * 检查两个数组是否相等
	 * @param array1 数组1
	 * @param array2 数组2
	 * @param ignoreSequence 是否忽略顺序
	 * @return 是否相等
	 */
	public static <E> boolean checkArraysEquals(E[] array1, E[] array2, boolean ignoreSequence) {
		if(null == array1) {
			return null == array2;
		}
		
		if(null == array2) {
			return null == array1;
		}
		
		List<E> list1 = Arrays.asList(array1);
		List<E> list2 = Arrays.asList(array2);
		
		return areListsEqual(list1, list2, ignoreSequence);
	}
	
	/**
	 * 比较两个列表是否相等
	 * @param list1
	 * @param list2
	 * @param ignoreSequence 是否忽略列表中各元素的顺序
	 * @return
	 */
	public static <E> boolean areListsEqual(List<E> list1, List<E> list2, boolean ignoreSequence) {
		boolean res = true;
		
		if(null == list1) {
			return null == list2;
		}
		
		if(null == list2) {
			return null == list1;
		}
		
		if(list1.size() != list2.size()) {
			return false;
		}
		
		List<E> copiedList1 = new ArrayList<E>(list1);
		List<E> copiedList2 = new ArrayList<E>(list2);
		
		// 忽略顺序的
		if(ignoreSequence) {
			Iterator<E> it1 = copiedList1.iterator();
			while(it1.hasNext()) {
				E itemInList1 = it1.next();
				boolean found = false;
				
				Iterator<E> it2 = copiedList2.iterator();
				while(it2.hasNext()) {
					E itemInList2 = it2.next();
					
					if(itemInList1 == null) {
						if(itemInList2 == null) {
							found = true;
							break;
						}
					} else {
						if(itemInList1.equals(itemInList2)) {
							found = true;
							break;
						}
					}
				}
				
				if(found) {
					it2.remove();
				} else {
					res = false;
					break;
				}
			}
		} else {
			for(int posInList1 = 0; posInList1 < copiedList1.size(); posInList1++) {
				E itemInList1 = copiedList1.get(posInList1);
				E itemInList2 = copiedList2.get(posInList1);
				
				if(!itemInList1.equals(itemInList2)) {
					res = false;
					break;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 比较两个map中的内容是否相同，忽略子项出现的位置
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static <K, V> boolean areMapsEqual(Map<K, V> map1, Map<K, V> map2) {
		if(null == map1) {
			return map2 == null;
		}
		
		if(map2 == null) {
			return map1 == null;
		}
		
		if(map1.size() != map2.size()) {
			return false;
		}
		
		
		boolean res = true;
		
		Map<K, V> copiedMap1 = new HashMap<K, V>(map1);
		Map<K, V> copiedMap2 = new HashMap<K, V>(map2);
		
		Iterator<Entry<K, V>> itInMap1 = copiedMap1.entrySet().iterator();
		while(itInMap1.hasNext()) {
			Entry<K, V> entryInMap1 = itInMap1.next();
			
			boolean found = false;
			Iterator<Entry<K, V>> itInMap2 = copiedMap2.entrySet().iterator();
			while(itInMap2.hasNext()) {
				Entry<K, V> entryInMap2 = itInMap2.next();
				
				if(null == entryInMap1) {
					if(null == entryInMap2) {
						found = true;
						break;
					}
				} else {
					if(entryInMap1.equals(entryInMap2)) {
						found = true;
						break;
					}
				}
				
			}
			
			if(found) {
				itInMap2.remove();
			} else {
				res = false;
				break;
			}
		}
		
		return res;
	}
}
