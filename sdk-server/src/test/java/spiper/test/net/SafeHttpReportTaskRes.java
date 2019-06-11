package spiper.test.net;

import com.spier.common.bean.net.task.TaskReportBeans;
import com.spier.common.bean.net.task.TaskReportBeans.Response;
import com.spier.common.http.AbsSafeHttp;

public class SafeHttpReportTaskRes extends AbsSafeHttp<TaskReportBeans.Response> {

	public SafeHttpReportTaskRes(String channelId, String rkPub) throws IllegalArgumentException {
		super("http://192.168.1.36:8080/SDKServer/tasks/report", channelId, rkPub);
		// TODO Auto-generated constructor stub
	}

	private TaskReportBeans.Request mRequestObj;
	
	public void setRequestObj(TaskReportBeans.Request obj) {
		mRequestObj = obj;
	}
	
	@Override
	public String serializeRequestObj() {
		String res = null;
		try {
			res = getGson().toJson(mRequestObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public Response deserializeResponseObj(String dataStr) {
		Response res = null;
		try {
			res = getGson().fromJson(dataStr, Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
