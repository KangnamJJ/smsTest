package spiper.test;

import java.util.HashMap;

import com.spier.common.utils.GeoUtils;
import com.spier.common.utils.HttpUtils;

public class Test {
    public static void main(String[] args) throws Exception{
        // ip 测试
        String ip = "49.230.27.233";
        String s = GeoUtils.parseCountryCode(ip);
        String country = "ID";
        System.out.println(s);

        // 接口测试
        String json = HttpUtils.post("http://localhost:8080/sdkserver/user/register", new HashMap<String, String>() {
        }, new HashMap<String, String>());
        System.out.println(json);

        // 修改数据库最大连接数设置
        /*Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.184.129:3306/sdk_server", "root", "123456");
        Statement stmt = connection.createStatement();
        String sql = "set global max_connections=1000";
        stmt.execute(sql);
        stmt.close();
        connection.close();*/
    }
}
