package com.example.postgpslocation.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.postgpslocation.R;
import com.example.postgpslocation.SocketThread;
import com.example.postgpslocation.adapter.ViewTextAdapter;
import com.example.postgpslocation.eventBus.eventBusForMsg;
import com.example.postgpslocation.service.LocationService;
import com.example.postgpslocation.service.SocketService;
import com.example.postgpslocation.staticClass.Flag;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity {
//	private LocationService locationService;
	private ListView ResultListView;
	private int Time = 0;
	private String szImei = " ";
	private String fwqzt=" ";
	
	//读取本地存储数据
	private SharedPreferences sp;
	private	Editor editor;
	
	//根据后台时间来设置定位时间
	private LocationClientOption option;

	public ProgressDialog progressDialog;
	
	private  ViewTextAdapter mAdapter ;
	
	private Map<String,Object> resultmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("LocationActivity", "oncreate");
		super.onCreate(savedInstanceState);
		progressDialog = new ProgressDialog(this);
	
		setContentView(R.layout.location);
		ResultListView=(ListView)findViewById(R.id.ResultListView);
		// 获取The IMEI: 仅仅只对Android手机有效:
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		szImei = TelephonyMgr.getDeviceId();
		
		//保持屏幕常亮cpu运行
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		sp=getSharedPreferences("userInfo", 0);
		String tel=sp.getString("spTelep","");
		String Imei =sp.getString("spImei", "");
		String timeHz =sp.getString("timeHz", "");
	    Boolean Islog =sp.getBoolean("spIslog", false);
	    editor=sp.edit();
	    
	    System.out.println(tel+"电话号码");
	    System.out.println(Imei);
	    System.out.println(Islog);
	    System.out.println("定位频率"+timeHz);
	    
	    resultmap=new HashMap<String,Object>();
		resultmap.put("time", "未知");
		resultmap.put("code", 0);
		resultmap.put("latitude", "未知");
		resultmap.put("longitude", "未知");
		resultmap.put("imei", "未知");
		resultmap.put("speed", "未知");
		resultmap.put("direction", "未知");
		resultmap.put("counttime", "未知");
		resultmap.put("success", "未知");
		logMsg("未知", resultmap);
		
		/**
		 * 接受service发过来的数据 使用eventbus
		 */
		//注册eventbus
		EventBus.getDefault().register(LocationActivity.this);
		
	
	}
	

	@Override
	protected void onStart() {
		
		Log.i("LocationActivity", "onstart");
		// TODO Auto-generated method stub
		super.onStart();
		

//		startbutton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				 if(!Flag.buttonSwith){					 
////					locationService.start();
//					startService(new Intent(LocationActivity.this, SocketService.class));
//					doBindService();
//					startbutton.setText(getString(R.string.stoplocation));
//					startbutton.setTextColor(Color.parseColor("#ff6d36"));
//					//设置按下按钮后改变按钮样式
//					startbutton.setBackgroundResource(R.drawable.selector_tzdw);
//					Toast.makeText(getApplicationContext(), "开始定位",
//							Toast.LENGTH_SHORT).show();
//					Flag.buttonSwith=true;
//					Flag.isloc=true;
//					}else{
////					locationService.stop();
//					stopService(new Intent(LocationActivity.this, SocketService.class));
//					doUnbindService();
//					Flag.buttonSwith=false;
//					Flag.isloc=false;
//					startbutton.setText(getString(R.string.startlocation));
//					startbutton.setTextColor(Color.parseColor("#f4ba00"));
//					//设置按下按钮后改变按钮样式
//					startbutton.setBackgroundResource(R.drawable.selector_ksdw);
//					Toast.makeText(getApplicationContext(), "停止定位",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
	}


	
	//接受service发过来的数据显示
	@Subscribe
	public void onEventMainThread(eventBusForMsg event) {
		// TODO Auto-generated method stub
		if("正常".equals( event.getmMsg())){
			fwqzt="正常";
//			Toast.makeText(LocationActivity.this, "定位上传成功", Toast.LENGTH_LONG).show();
			logMsg(event.getmMsg(),event.getResultmap());
		}else{
			fwqzt="连接异常";
//			Toast.makeText(LocationActivity.this, event.getmMsg(), Toast.LENGTH_LONG).show();  
			logMsg(event.getmMsg(),event.getResultmap());
		}
	
	}
	
	// 显示到LocationResult TextView中
	public void logMsg(String str,Map<String,Object> map) {
		try {

//			if (LocationResult != null)
//				LocationResult.setText(str);
			if(ResultListView!=null){
			 List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			 Map<String, Object> mapforlist = new HashMap<String, Object>();
			 if(Integer.parseInt(map.get("code").toString())== BDLocation.TypeGpsLocation){
			 		//GPS定位结果
				 mapforlist.put("img", R.drawable.dwfs);
				 mapforlist.put("title", "定位描叙");
				 mapforlist.put("info", "GPS定位");
				 list.add(mapforlist);
				 mapforlist= new HashMap<String, Object>();
				 mapforlist.put("img", R.drawable.sd);
				 mapforlist.put("title", "速度");
				 mapforlist.put("info", map.get("speed"));
				 list.add(mapforlist);
			 	}else if (Integer.parseInt(map.get("code").toString()) == BDLocation.TypeNetWorkLocation) {// 网络定位结果
			 		 mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "网络定位结果");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "仅GPS定位有值");
					 list.add(mapforlist);
			 	} else if (Integer.parseInt(map.get("code").toString()) == BDLocation.TypeOffLineLocation) {// 离线定位结果
//					sb.append("\n定位描述  : ");
//					sb.append("离线定位成功，离线定位结果也是有效的");
			 		 mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "离线定位");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "仅GPS定位有值");
					 list.add(mapforlist);
				} else if (Integer.parseInt(map.get("code").toString()) == BDLocation.TypeServerError) {
//					sb.append("\n定位描述  : ");
//					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
					 mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "服务端网络定位失败");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "仅GPS定位有值");
					 list.add(mapforlist);
				} else if (Integer.parseInt(map.get("code").toString())== BDLocation.TypeNetWorkException) {
//					sb.append("\n定位描述  : ");
//					sb.append("网络不同导致定位失败，请检查网络是否通畅");
					mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "网络不同导致定位失败，请检查网络是否通畅");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "仅GPS定位有值");
					 list.add(mapforlist);
				} else if (Integer.parseInt(map.get("code").toString()) == BDLocation.TypeCriteriaException) {
//					sb.append("\n定位描述  : ");
//					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
					mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "无法获取有效定位依据导致定位失败，请重启手机");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "仅GPS定位有值");
					 list.add(mapforlist);
				}else if (Integer.parseInt(map.get("code").toString())  == 0) {
					//没有开始定位哦，第一次进入界面显示listview
					mapforlist.put("img", R.drawable.dwfs);
					 mapforlist.put("title", "定位描叙");
					 mapforlist.put("info", "未知");
					 list.add(mapforlist);
					 mapforlist= new HashMap<String, Object>();
					 mapforlist.put("img", R.drawable.sd);
					 mapforlist.put("title", "速度");
					 mapforlist.put("info", "未知");
					 list.add(mapforlist);
				}
			 
			 mapforlist= new HashMap<String, Object>();
			 mapforlist.put("img", R.drawable.jd);
			 mapforlist.put("title", "经度");
			 mapforlist.put("info", map.get("longitude"));
			 list.add(mapforlist);
			 mapforlist= new HashMap<String, Object>();
			 mapforlist.put("img", R.drawable.wd);
			 mapforlist.put("title", "纬度");
			 mapforlist.put("info", map.get("latitude"));
			 list.add(mapforlist);
			 mapforlist= new HashMap<String, Object>();
			 if(null==map.get("time")||"".equals(map.get("time"))){
				 map.put("time", "未知");
			 }
			 mapforlist.put("img", R.drawable.sj);
			 mapforlist.put("title", "时间");
			 mapforlist.put("info", map.get("time"));
			 list.add(mapforlist);
			 mapforlist= new HashMap<String, Object>();
			 mapforlist.put("img", R.drawable.fwlj);
			 mapforlist.put("title", "服务器连接状态");
			 mapforlist.put("info", fwqzt);
			 list.add(mapforlist);
			 //数据上传成功次数
			 mapforlist= new HashMap<String, Object>();
			 mapforlist.put("img", R.drawable.sccg);
			 mapforlist.put("title", "本次定位数据上次成功次数");
			 mapforlist.put("info",map.get("success"));
			 list.add(mapforlist);
			 
			 mapforlist= new HashMap<String, Object>();
			 mapforlist.put("img", R.drawable.drljsbcs);
			 mapforlist.put("title", "本次定位次数");
			 mapforlist.put("info", map.get("counttime"));
			 list.add(mapforlist);
			
			 mAdapter = new ViewTextAdapter(this,list);//得到一个MyAdapter对象 
			 ResultListView.setAdapter(mAdapter);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(LocationActivity.this);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			 moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	/*
	 * socketservice 的bind方法
	 */
	private SocketService mBoundService;
	private boolean mIsBound;

	private ServiceConnection mConnection = new ServiceConnection() {
		// EDITED PART
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBoundService = ((SocketService.LocalBinder) service).getService();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBoundService = null;
		}

	};

	private void doBindService() {
		bindService(new Intent(LocationActivity.this, SocketService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		if (mBoundService != null) {
			mBoundService.IsBoundable();
		}
	}

	private void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	
	private String GMT8TOGMT0(String strDate){
//			String strDate = "2016-04-18 16:41:54";  
			String fromTimeZone = "GMT+8";  
			String toTimeZone = "GMT+0";  				  
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			format.setTimeZone(TimeZone.getTimeZone(fromTimeZone));  
			Date date;
			try {
				date = format.parse(strDate);
				format=new SimpleDateFormat("ddMMyyHHmmss");
			format.setTimeZone(TimeZone.getTimeZone(toTimeZone));  
			strDate = format.format(date);  
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(strDate);
			return strDate;
	}
}
