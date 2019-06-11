package spiper.test.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spier.common.bean.net.task.TaskRequestBeans;
import com.spier.common.bean.net.task.TaskRequestBeans.Response;
import com.spier.common.http.AbsSafeHttp;

public class SafeHttpRequestTask extends AbsSafeHttp<TaskRequestBeans.Response> {

	private Gson mGson;
	
	public SafeHttpRequestTask(String channelId, String rkPub) throws IllegalArgumentException {
		super("http://192.168.1.36:8080/SDKServer/tasks/request", channelId, rkPub);
		
		mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.disableHtmlEscaping().create();
	}

	private TaskRequestBeans.Request mRequestObj;
	
	public void setRequestObj(TaskRequestBeans.Request requestData) {
		mRequestObj = requestData;
	}
	
	@Override
	public String serializeRequestObj() {
		if(null == mRequestObj) {
			return null;
		}
		
		String res = null;
		try {
			res = mGson.toJson(mRequestObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public Response deserializeResponseObj(String dataStr) {
		Response res = null;
		try {
			res = mGson.fromJson(dataStr, TaskRequestBeans.Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
