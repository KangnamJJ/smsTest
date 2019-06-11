package com.spier.common.utils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Objects {

	public static Double getNumber(Object in)
	{
		return Double.parseDouble(in.toString());
	}
	public static boolean isNotEmpty(Object inObj)
	{
		return !isEmpty(inObj);
	}
	public static boolean isNotNull(Object ori)
	{
		return !isNull(ori);
	}
	public static boolean isNull(Object ori)
	{
		return ori==null;
	}
	public static boolean isOrTrue(Boolean[] in){//或关系
		boolean retn=false;
		if(in==null || in.length<=0){
			return retn;
		}
		for(Boolean o:in){
			if(o){
				retn= true;
				break;
			}
		}
		return retn;
	}
	public static boolean isTrue(Boolean[] in){//与关系
		boolean retn=true;
		if(in==null || in.length<=0){
			return !retn;
		}
		for(Boolean o:in){
			if(!o){
				retn= false;
				break;
			}
		}
		return retn;
	}
	public static boolean isBatchAndEmpty(Object... in){
		boolean retn=false;
		if(in==null || in.length<=0){
			return !retn;
		}
		for(Object o:in){
			if(isEmpty(o)){
				retn= true;
				break;
			}
		}
		return retn;
	}
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object inObj)
	{
		if(inObj==null)
		{
			return true;
		}
		if(inObj instanceof Collection)
		{
			return ((Collection)inObj).isEmpty();
		}
		else if(inObj instanceof Map)
		{
			return ((Map)inObj).isEmpty();
		}
		else if(inObj instanceof List)
		{
			return ((List)inObj).isEmpty();
		}
		else if(inObj instanceof String)
		{
			return "".equals((String)inObj);
		}
		else if(inObj.getClass().isArray())
		{
			return Array.getLength(inObj)==0;
		}
		return false;
	}
	public static int compareTo(Double ori,Double dest)
	{
		BigDecimal dori = new BigDecimal(ori); 
		BigDecimal ddest = new BigDecimal(dest); 
		return dori.compareTo(ddest);
	}
	private Objects(){
		
	}
	
	/**
     * 编码指定的参数值
     *
     * @param value 待编码参数值
     * @return 编码后的参数值
     */
    public static String encode(Object value) throws UnsupportedEncodingException {
        if(value == null){
            return null;
        }

        String result = URLEncoder.encode(value.toString(), "UTF-8");
        // URLEncoder.encode()方法会将空格转换为+，导致IOS端无法正常解析
        return result.replaceAll("\\+", "%20");
    }
    
    /**
     * 解码指定的参数值
     *
     * @param value 待解码参数值
     * @return 解码后的参数值
     */
    public static String decode(Object value) throws UnsupportedEncodingException {
        if(value == null){
            return null;
        }

        return URLDecoder.decode(value.toString(), "UTF-8");
    }
		
}
