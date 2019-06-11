package com.spier.common.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class JSONUtils {

    public static enum SerializeFormater {
        /** TIMESTAMP format as 'yyyy-MM-dd HH:mm:ss' */
        DEFAULT_DATE(SerializerFeature.WriteDateUseDateFormat);

        private SerializerFeature seria;
        private SerializeFormater(SerializerFeature seria) {
            this.seria = seria;
        }
    }

    /**
     * json to bean
     */
    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * json to bean
     */
    public static <T> T parseObject(String json, TypeReference<T> ref) {
        return JSON.parseObject(json, ref);
    }

    /**
     * bean to json
     */
    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 定制化格式
     * @param obj
     * @param sf
     * @return
     */
    public static String toJSONString(Object obj, SerializeFormater sf) {
        return JSON.toJSONString(obj, sf.seria);
    }

    /**
     * json to bean list
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

//	@Deprecated
//	public static String toJSONString(Map map) {
//		return JSON.toJSONString(map);
//	}
//
//	@Deprecated
//	public static String toJSONString4Map(Map map) {
//		return JSON.toJSONString(map);
//	}
}

