package com.example.postgpslocation.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.postgpslocation.MyApplication;

import android.util.Log;


public class NetHelper {

	final static String TAG = "NetHelper";

	public String SVR_URL;

	private HttpClient httpClient;

	public NetHelper(String svr_url) {
		SVR_URL = svr_url;
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, false);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https", PlainSocketFactory
				.getSocketFactory(), 433));
		ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		httpClient = new DefaultHttpClient(conMgr, params);
		httpClient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		// 请求超时1分钟
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		// 读取超时1分钟
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);
	}

	/**
	 * 获得Net操作类
	 * 
	 * @param proxy
	 *            类型
	 * @param method
	 *            方法
	 * @param token
	 *            会话令牌
	 * @return 操作类
	 */
	public HttpPost getHttpPost(String proxy, String method, String token) {
		StringBuilder url = new StringBuilder(SVR_URL);
		url.append(proxy);
		url.append("!");
		url.append(method);
		url.append(".action");
		HttpPost httpPost = new HttpPost(url.toString());
		Log.i("getHttpPost", url.toString());
		// 添加http头信息
		// httpPost.addHeader(HTTP.CONTENT_TYPE,
		// "application/x-www-form-urlencoded"); // application/json
		// //application/x-www-form-urlencoded
		// //multipart/form-data
		httpPost.addHeader(HTTP.USER_AGENT, "android");
		httpPost.addHeader(HTTP.CHARSET_PARAM, HTTP.UTF_8);
		// httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		// httpPost.addHeader("Accept-Language", "zh-cn");
		httpPost.addHeader("Accept-Encoding", "gzip, deflate");
		// 认证token
		httpPost.addHeader("authorization", token);
		return httpPost;
	}

	/**
	 * 发起请求
	 * 
	 * @param proxy
	 *            类型
	 * @param method
	 *            方法
	 * @param jsonparam
	 *            json参数
	 * @return [result] -3:客户端致命错误 -2:客户端异常 -1:未登陆鉴权 1:成功 2:失败 3:服务端异常<br/>
	 *         [msg] 提示信息<br/>
	 *         [data] 返回数据
	 */
	public Map<String, Object> req(String proxy, String method, String reqjson,
			File file) {
		Map<String, Object> rmap = new HashMap<String, Object>();
		try {
			// 新建HttpPost对象
//			HttpPost request = getHttpPost(proxy, method,
//					MyApplication.getInstance().Token);
			HttpPost request = getHttpPost(proxy, method,
					MyApplication.getInstance().Token);
			Log.i("NetHelper", "Tokne"+MyApplication.getInstance().Token);			
			MultipartEntity mpEntity = new MultipartEntity();
			// 文件流参数
			if (file != null) {
				ContentBody cbFileData = new FileBody(file);
				mpEntity.addPart("upfile", cbFileData);
			}
			// json参数
			if (reqjson != null) {
				ContentBody cbStringData = new StringBody(reqjson,
						Charset.forName(HTTP.UTF_8));
				mpEntity.addPart("jdata", cbStringData);
			}
			request.setEntity(mpEntity);
			// 获取HttpResponse实例
			HttpResponse httpResp = httpClient.execute(request);
			// 判断是够请求成功
			if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = httpResp.getEntity().getContent();
				Header contentEncoding = httpResp
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(new BufferedInputStream(is));
				}
				// 获取返回的数据
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				String respjson = sb.toString();

				// 获取返回的数据
				JSONObject o = new JSONObject(respjson);
				rmap.put("result", o.get("result"));
				rmap.put("msg", o.get("msg"));
				if (o.has("data")) {
					rmap.put("data", o.get("data"));
				}
			} else {
				rmap.put("result", -2);
				rmap.put("msg", "无法访问服务");
				Log.e(TAG, "调用失败，无法访问服务");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			rmap.put("result", -3);
			rmap.put("msg", "接口调用超时");
			Log.e(TAG, "接口调用超时");
		} catch (IOException e) {
			e.printStackTrace();
			rmap.put("result", -3);
			rmap.put("msg", "接口调用失败");
			Log.e(TAG, "接口调用失败");
		} catch (JSONException e) {
			e.printStackTrace();
			rmap.put("result", -3);
			rmap.put("msg", "接口返回解析失败");
			Log.e(TAG, "接口返回解析失败");
		}
		return rmap;
	}
}
