/**
 *
 */
package ser.ds.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HttpUtil
{
	
	public static HttpClient httpClient = new DefaultHttpClient();
	public static final String BASE_URL =
			"http://202.200.112.91:3333/UserService/";
	/**
	 *
	 * @param url 发送请求的URL
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String getRequest( final String url)
		throws Exception
	{
		FutureTask<String> task = new FutureTask<String>(
		new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				// 创建HttpGet对象。
				HttpGet get = new HttpGet(url);
				// 发送GET请求
				HttpResponse httpResponse = httpClient.execute(get);
				// 如果服务器成功地返回响应
				if (httpResponse.getStatusLine()
					.getStatusCode() == 200)
				{
					// 获取服务器响应字符串
					String result = EntityUtils
						.toString(httpResponse.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}

	/**
	 * @param url 发送请求的URL
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(final String url, final Map<String ,String> rawParams)throws Exception
	{
		FutureTask<String> task = new FutureTask<String>(
		new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				HttpPost post = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for(String key : rawParams.keySet())
				{
					params.add(new BasicNameValuePair(key, rawParams.get(key)));
				}
				post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
				HttpResponse httpResponse = httpClient.execute(post);
				if (httpResponse.getStatusLine().getStatusCode() == 200)
				{
					String result = EntityUtils.toString(httpResponse.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}
	public static JSONObject query(Map<String, String> userObject, String actionName) throws Exception {
		String url = HttpUtil.BASE_URL + actionName;
		JSONObject result = new JSONObject();
		result = (JSONObject) JSON.parse(HttpUtil.postRequest(url, userObject));
		Util.NetState = 1;
		return result;
	}
	
}
