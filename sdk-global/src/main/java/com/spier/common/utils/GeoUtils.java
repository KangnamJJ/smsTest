package com.spier.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GeoUtils {

    private static List<Long> IP_NUM_LIST = Lists.newArrayList();
    private static Map<Long, String> IP_COUNTY_MAPPING = Maps.newHashMap();

    private static StringBuilder carriers = new StringBuilder();
    static {
        try(InputStream in = GeoUtils.class.getClassLoader().getResourceAsStream("file/ipCountry.csv");
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr)) {
            String line = null;
            while(null != (line = br.readLine())) {
                String[] item = line.split(",");
                Long ipNum = Long.valueOf(item[0]);
                IP_NUM_LIST.add(ipNum);
                IP_COUNTY_MAPPING.put(ipNum, item[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse local ip-country file !");
        }

        try(InputStream ins = GeoUtils.class.getClassLoader().getResourceAsStream("file/carrier.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins))) {
            String line = null;
            while(null != (line = reader.readLine())) {
                carriers.append(line);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse local ip-country file !");
        }
    }

    public static String getAllCarriers() {
        return carriers.toString();
    }

    /**
     * <p> IP地址解析国家二字码
     * @param ip
     * @return eg. CN, US
     */
    public static String parseCountryCode(String ip) {
        if(Strings.isNullOrEmpty(ip)) return null;
        int start = 0;
        int end = IP_NUM_LIST.size();
        long ipNum = parseIpNum(ip);
        if(ipNum < 0) return null;
        for(;;) {
            int mid = (start + end) / 2;
            long current = IP_NUM_LIST.get(mid);
            if(ipNum == current) {
                return IP_COUNTY_MAPPING.get(ipNum);
            } else if(ipNum < current) {
                end = mid;
            } else {
                start = mid;
            }

            if(start +1 == end) return IP_COUNTY_MAPPING.get(IP_NUM_LIST.get(start));
        }
    }

    /**
     * <p> 解析IPv4地址，按二进制移位，转化为正整数
     * @param ip
     * @return if fail, -1 will be returned.
     */
    public static long parseIpNum(String ip) {
        if(15 >= ip.length()) {
            String[] item = ip.split("\\.");
            try {
                return Long.parseLong(item[0])* (1 << 24)
                        + Integer.parseInt(item[1]) * (1 << 16)
                        + Integer.parseInt(item[2]) * (1 << 8)
                        + Integer.parseInt(item[3]);
            } catch (Exception e) {
            }
        }
        return -1;
    }

    /**
     * <p> 解析IPv4地址，按二进制移位，转化为正整数
     * @param ip
     * @return if fail, -1 will be returned.
     */
    public static String parseIpNum2Ip(Long ipNum) {
        if(null != ipNum) {
            long ip1 = ipNum / (1 << 24);
            long ip2 = (ipNum % (1 << 24)) / (1 << 16);
            long ip3 = (ipNum % (1 << 16)) / (1 << 8);
            long ip4 = ipNum % (1 << 8);

            return ip1 + "." + ip2 + "." + ip3 + "." + ip4;
        }
        return null;
    }

    /**
     * parse ip addr to geo info using http://ip-api.com
     *
     * <p>
     * The system will automatically ban any IP addresses doing over 150 requests per minute. If your IP was banned, use this form to remove the ban.
     * <br> To unban, visit http://ip-api.com/docs/unban
     * @param ip
     * @return
     */
    private static int requestTimes = 0;
    private static long stamp = 0;
    private static int MAX_PERIOD_REQ_TIMES = 130;
    public synchronized static GeoInfo parse(String ip) {
        GeoInfo info = null;
        if(! Strings.isNullOrEmpty(ip)) {
            requestTimes++;
            if(requestTimes >= MAX_PERIOD_REQ_TIMES) {
                long left = 60000 - (System.currentTimeMillis() - stamp) + 30000;
                if(left > 0) {
                    try { Thread.sleep(left);
                    } catch (InterruptedException e) {}
                }
                stamp = System.currentTimeMillis();
                requestTimes = 0;
            }
            String json = HttpUtils.get("http://ip-api.com/json/"+ ip +"?fields=520191", 60000, 60000);
            if(! Strings.isNullOrEmpty(json)) {
                info = JSONUtils.parse(json, GeoInfo.class);
                if(null != info && !"success".equalsIgnoreCase(info.getStatus())) info = null;
            }
        }
        return info;
    }

    //	{
//		accuracy: 100,
//		as: "AS24203 PT Excelcomindo Pratama (Network Access Provider)",
//		city: "Central Jakarta",
//		country: "Indonesia",
//		countryCode: "ID",
//		isp: "PT Excelcomindo Pratama",
//		lat: -6.18649,
//		lon: 106.834,
//		mobile: true,
//		org: "PT Excelcomindo Pratama",
//		proxy: false,
//		query: "112.215.36.143",
//		region: "",
//		regionName: "Special Capital Region of Jakarta",
//		status: "success",
//		timezone: "Asia/Jakarta",
//		zip: ""
//	}
    public static class GeoInfo {
        private String status;
        private String isp;
        private String org;
        private String country;
        private String countryCode;
        private String city;
        private String as;
        private String region;
        private String proxy;
        private String mobile;
        private BigDecimal lat;
        private BigDecimal lon;
        private String timezone;
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getIsp() {
            return isp;
        }
        public void setIsp(String isp) {
            this.isp = isp;
        }
        public String getOrg() {
            return org;
        }
        public void setOrg(String org) {
            this.org = org;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
        public String getCountryCode() {
            return countryCode;
        }
        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public String getAs() {
            return as;
        }
        public void setAs(String as) {
            this.as = as;
        }
        public String getRegion() {
            return region;
        }
        public void setRegion(String region) {
            this.region = region;
        }
        public String getProxy() {
            return proxy;
        }
        public void setProxy(String proxy) {
            this.proxy = proxy;
        }
        public String getMobile() {
            return mobile;
        }
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public BigDecimal getLat() {
            return lat;
        }
        public void setLat(BigDecimal lat) {
            this.lat = lat;
        }
        public BigDecimal getLon() {
            return lon;
        }
        public void setLon(BigDecimal lon) {
            this.lon = lon;
        }
        public String getTimezone() {
            return timezone;
        }
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }

    public static void main(String[] args) {
//		{
//			long start = System.nanoTime();
//			System.out.println(parseCountryCode("5.2.238.255"));
//			System.out.println("-- cost " + (System.nanoTime() - start) + " ms. ");
//		}
//		{
//			long start = System.nanoTime();
//			System.out.println(parseCountryCode("5.2.239.0"));
//			System.out.println("-- cost " + (System.nanoTime() - start) + " ms. ");
//		}
//		{
//			long start = System.nanoTime();
//			System.out.println(parseCountryCode("5.1.69.255"));
//			System.out.println("-- cost " + (System.nanoTime() - start) + " ms. ");
//		}
//		{
//			long start = System.nanoTime();
//			System.out.println(parseCountryCode("5.1.69.255"));
//			System.out.println("-- cost " + (System.nanoTime() - start) + " ms. ");
//		}
//		System.out.println(System.currentTimeMillis());
//		System.out.println(parseIpNum("35.165.245.88"));
//		System.out.println(parse("35.164.235.105"));
//		System.out.println(parseCountryCode("47.89.39.193"));
    }
}
