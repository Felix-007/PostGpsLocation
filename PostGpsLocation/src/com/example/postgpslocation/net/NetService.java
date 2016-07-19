package com.example.postgpslocation.net;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.postgpslocation.staticClass.Flag;
import com.example.postgpslocation.util.AppUtil;
import com.example.postgpslocation.util.PublicUtil;

import android.content.Context;
import android.util.Log;

public class NetService {

	final static String TAG = "NetService";

	private Context appcontext;
	private NetHelper netHelper = null;

	public NetService(Context context) {
		appcontext = context;
		netHelper = new NetHelper(SVR_URL);
	}

	public String token = "notoken";

	public final String SVR_URL = Flag.SVR_URL;
//	public final String SVR_URL = "http://211.156.198.50/";
	//public final String SVR_URL = "http://10.10.5.14:8080/cpgps/";
	//public final String SVR_URL = "http://61.187.64.220:81/cpgps/";
	//public final String SVR_URL = "http://61.187.64.217:9002/post/";
	//public final String SVR_URL = "http://10.10.5.12:8080/cpgps/";

	public final String SVR_PIC_URL = SVR_URL + "pic/";

	final String PROXY_DEBUG = "mobi_debug";
	final String PROXY_LOGIN = "mobi_login";
	final String PROXY_CLIENT = "mobi_client";

	final String METHOD_LOGIN_DO_LOGIN = "doLogin";
	final String METHOD_LOGIN_DO_LOGOUT = "doLogout";
	final String METHOD_LOGIN_CHK_VERSION = "chkVersion";
	final String METHOD_LOGIN_CHK_DATE = "chkDate";
	
	
	//add By LF 2016年4月12日09:22:50
	final String METHOD_CLIENT_GET_POSTROADTASK4MOBILEPHONE="getPostRoadTask4MobilePhone";
	
		
	/**
	 * 登陆
	 * 
	 * @param c_jgdm
	 *            机构代码
	 * @param c_yhdm
	 *            用户代码
	 * @param c_yhmm
	 *            用户密码
	 */
	public Map<String, Object> doLogin(String c_jgdm, String c_yhdm,
			String c_yhmm) {
		Log.i(TAG, "调用接口，iface:" + PROXY_LOGIN + "!" + METHOD_LOGIN_DO_LOGIN
				+ ",c_jgdm:" + c_jgdm + ",c_yhdm:" + c_yhdm + ",c_yhmm:"
				+ c_yhmm);
		Map<String, Object> rmap;
		try {
			JSONObject o = new JSONObject();
			o.put("c_jgdm", c_jgdm);
			o.put("c_yhdm", c_yhdm);
			o.put("c_yhmm", c_yhmm);
//			o.put("c_yhdm", "jnzxst");
//			o.put("c_yhmm", "123456");
			
			rmap = netHelper.req(PROXY_LOGIN, METHOD_LOGIN_DO_LOGIN,
					o.toString(), null);
//			rmap = new HashMap<String, Object>();
//			rmap.put("result", 1);
//			rmap.put("msg", "登陆成功");
//			rmap.put("data", "");
		} catch (JSONException e) {
			e.printStackTrace();
			rmap = new HashMap<String, Object>();
			rmap.put("result", -3);
			rmap.put("msg", "接口参数解析异常");
			Log.e(TAG, "接口参数解析异常，iface:" + PROXY_LOGIN + "!"
					+ METHOD_LOGIN_DO_LOGIN + ",c_jgdm:" + c_jgdm + ",c_yhdm:"
					+ c_yhdm + ",c_yhmm:" + c_yhmm);
		}
		return rmap;
	}

	/**
	 * 登出
	 */
	public Map<String, Object> doLogout() {
		Log.i(TAG, "调用接口，iface:" + PROXY_LOGIN + "!" + METHOD_LOGIN_DO_LOGOUT);
		return netHelper.req(PROXY_LOGIN, METHOD_LOGIN_DO_LOGOUT, null, null);
	}

	/**
	 * 检查版本
	 */
	public Map<String, Object> chkVersion() {
		String v = AppUtil.getVersionName(appcontext);
		Log.i(TAG, "调用接口，iface:" + PROXY_LOGIN + "!" + METHOD_LOGIN_CHK_VERSION
				+ ",v:" + v);
		Map<String, Object> rmap;
		try {
			JSONObject o = new JSONObject();
			o.put("v", v);
			rmap = netHelper.req(PROXY_LOGIN, METHOD_LOGIN_CHK_VERSION,
					o.toString(), null);
		} catch (JSONException e) {
			e.printStackTrace();
			rmap = new HashMap<String, Object>();
			rmap.put("result", -3);
			rmap.put("msg", "接口参数解析异常");
			Log.e(TAG, "接口参数解析异常，iface:" + PROXY_LOGIN + "!"
					+ METHOD_LOGIN_CHK_VERSION + ",v:" + v);
		}
		return rmap;
	}

	/**
	 * 检查时间
	 */
	public Map<String, Object> chkDate() {
		String date = PublicUtil.getDateForYYYYMMDDHHMISS(new Date());
		Log.i(TAG, "调用接口，iface:" + PROXY_LOGIN + "!" + METHOD_LOGIN_CHK_VERSION
				+ ",date:" + date);
		return netHelper.req(PROXY_LOGIN, METHOD_LOGIN_CHK_VERSION, null, null);
	}

	
	
	public Map<String, Object> getPostRoadTask4MobilePhone(String devID) {
		Log.i(TAG, "调用接口，iface:" + PROXY_CLIENT + "!"
				+ METHOD_CLIENT_GET_POSTROADTASK4MOBILEPHONE);
		Map<String, Object> rmap;
		JSONObject o = new JSONObject();
		try {
			o.put("devID", devID);
			rmap = netHelper.req(PROXY_LOGIN, METHOD_CLIENT_GET_POSTROADTASK4MOBILEPHONE,
					o.toString(), null);
		} catch (JSONException e) {
			e.printStackTrace();
			rmap = new HashMap<String, Object>();
			rmap.put("result", -3);
			rmap.put("msg", "接口参数解析异常");
			Log.e(TAG, "接口参数解析异常，iface:" + PROXY_CLIENT + "!"
					+ METHOD_CLIENT_GET_POSTROADTASK4MOBILEPHONE + ",type:" + devID);
		}
		return rmap;
	}
}
