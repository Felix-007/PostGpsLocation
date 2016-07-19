package com.example.postgpslocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.example.postgpslocation.net.NetService;

@SuppressLint("NewApi")
public class MyApplication extends Application {

	// private boolean DEVELOPER_MODE = true;

	private static MyApplication mInstance = null;
	
	public static boolean mDownDataTheadFlag = true;		//下载数据线程开关
	public static int mDownDataIntervalTime = 60*1000;		//一分钟
	public JSONObject mJsonO_todayAlarmCount;	//{1终到晚点、2发车晚点、3超速、4偏离、5失步},如{zdwd:1,fcwd:2,cs:3,pl:4,sb:5}
	
	public boolean key_bKeyRight = true;
	// Map服务
	public BMapManager mBMapManager = null;
	// DB服务
//	public DBService mDBService = null;
	// Net服务
	public NetService mNetService = null;
	// Pic服务
//	public PicService mPicService = null;
	// Misc服务
//	public MiscService mMiscService = null;
	// Log服务
//	public LogService mLogService = null;
	
	// dp与像素比例
	public float density;
	// 预报小时数设置
	public String forecastTime;
	// 软件提醒设置
	public boolean isRemind;
	//本局/本省
	public String orgTypeStr = "";
	// 主目录
	public String MainDirPath = null;

	public final String key_mapKey = "WuAtTiIf6tdLypLriz3dNpwZ";	//ZHG
//	public final String key_mapKey = "GemouVNGNt4Tpbnun1O3IO2b";
//	public final String key_mapKey = "WuAtTiIf6tdLypLriz3dNpwZ";	//HYH
//	public final String key_mapKey = "zH1XD4diA1OMOC7HjfTRW4Mp";	//LF
	

	// 在线登陆口令，离线登陆则为null
	public String Token = null;
	
	//报警(老)
	public List<Map<String, Object>> localAlarmData = new ArrayList<Map<String, Object>>();
//	public AutoUpdateArrayAdapter localAlarmAdapter;
	public int noReadAlarmCount = 0;
	
	//我的关注
	public List<Map<String, Object>> localAttentionData = new ArrayList<Map<String, Object>>();
//	public AutoUpdateArrayAdapter localAttentionAdapter;
	public int noReadAttentionCount = 0;

	@Override
	public void onCreate() {
		// if (DEVELOPER_MODE) {
		// StrictMode();
		// }
		super.onCreate();
		mInstance = this;

//		initMapService(this);// 初始化Map
//		initDBService(this);// 初始化存储
		initNetService(this);// 初始化网络
//		initPicService(this);// 初始化照片
//		initMiscService(this);// 初始化音效
		// initPolyword(this);//初始化多音字库
//		initLogService(MainDirPath);// 初始化日志

		density = this.getResources().getDisplayMetrics().density;
//		forecastTime = mDBService.getForecastTime();
//		isRemind = mDBService.getIsRemind();
//		Intent serviceIntent = new Intent("com.copote.postgps.frame.ServiceCenter");
		Bundle bundle = new Bundle();
        bundle.putInt("style", 1);
//        serviceIntent.putExtras(bundle);
//		startService(serviceIntent);
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}
//		if (mDBService != null) {
//			mDBService.destroy();
//			mDBService = null;
//		}
//		if (mMiscService != null) {
//			mMiscService.destroy();
//			mMiscService = null;
//		}
		super.onTerminate();
	}

	/**
	 * Map服务
	 */
//	private void initMapService(Context context) {
//		if (mBMapManager == null) {
//			mBMapManager = new BMapManager(context);
//		}
//		if (!mBMapManager.init(key_mapKey, new MyGeneralListener())) {
//			Toast.makeText(MyApplication.getInstance().getApplicationContext(),
//					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
//		}
//	}

	/**
	 * DB服务
	 */
//	private void initDBService(Context context) {
//		if (mDBService == null) {
//			mDBService = new DBService(context);
//		}
//	}

	/**
	 * Net服务
	 */
	private void initNetService(Context context) {
		if (mNetService == null) {
			mNetService = new NetService(context);
		}
	}

	/**
	 * 照片服务
	 */
//	private void initPicService(Context context) {
//		if (mPicService == null) {
//			mPicService = new PicService(context);
//			MainDirPath = mPicService.getMainDirPath();
//		}
//	}
//
//	/**
//	 * 音效服务
//	 */
//	private void initMiscService(Context context) {
//		if (mMiscService == null) {
//			mMiscService = new MiscService(context);
//		}
//	}
//
//	/**
//	 * 多音字词库
//	 */
//	private void initPolyword(Context context) {
//		PublicUtil.InitPolyword(context);
//	}
//
//	/**
//	 * 日志服务
//	 */
//	private void initLogService(String MainDirPath) {
//		if (mLogService == null) {
//			mLogService = new LogService(MainDirPath);
//		}
//	}

	public static MyApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
//	public static class MyGeneralListener implements MKGeneralListener {
//		
		
//		@Override
//		public void onGetNetworkState(int iError) {
//			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
//				Toast.makeText(
//						MyApplication.getInstance().getApplicationContext(),
//						"您的网络出错啦！", Toast.LENGTH_LONG).show();
//			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
//				Toast.makeText(
//						MyApplication.getInstance().getApplicationContext(),
//						"输入正确的检索条件！", Toast.LENGTH_LONG).show();
//			}
//			// ...
//		}

//		@Override
//		public void onGetPermissionState(int iError) {
//			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
//				// 授权Key错误：
//				Toast.makeText(
//						MyApplication.getInstance().getApplicationContext(),
//						"请使用正确的授权Key！", Toast.LENGTH_LONG).show();
//				MyApplication.getInstance().key_bKeyRight = false;
//			}
//		}
//	}
}