package com.example.postgpslocation.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.postgpslocation.activity.LocationApplication;
import com.example.postgpslocation.eventBus.eventBusForMsg;
import com.example.postgpslocation.staticClass.Flag;

import de.greenrobot.event.EventBus;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SocketService extends Service {
//	public static final String SERVERIP = "192.168.191.1"; // your computer IP
//	public static final String SERVERIP = "211.156.198.45"; // your computer IP
//															// address should be
//															// written here
//	public static final int SERVERPORT = 7881;
	PrintWriter out;
	Socket socket;
	InetAddress serverAddr;

	//定位服务
	private LocationService locationService;
	private LocationClientOption option;
	//读取本地存储数据
	private SharedPreferences sp;
	private	Editor editor;
	private Map<String,Object> resultmap;
	private int Time = 0;
	private int success=0;
	private String szImei = " ";
	private String fwqzt=" ";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("I am in Ibinder onBind method");
		return myBinder;
	}

	private  IBinder myBinder = new LocalBinder();

	// TCPClient mTcpClient = new TCPClient();

	public class LocalBinder extends Binder {
		public SocketService getService() {
			System.out.println("I am in Localbinder ");
			return SocketService.this;

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("I am in on create");
		
		sp=getSharedPreferences("userInfo", 0);
		String tel=sp.getString("spTelep","");
		String Imei =sp.getString("spImei", "");
		String timeHz =sp.getString("timeHz", "");
	    Boolean Islog =sp.getBoolean("spIslog", false);
	    editor=sp.edit();
		
		locationService = ((LocationApplication) getApplication()).locationService;
		option= new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");
		option.setScanSpan(Integer.parseInt(sp.getString("timeHz", ""))*1000);
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
		option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
		option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死   
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.SetIgnoreCacheException(false);
		locationService.setLocationOption(option);
		locationService.registerListener(mListener);
		
	
		
	}
	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location
					&& location.getLocType() != BDLocation.TypeServerError) {
				
				//定位次数
				Time = Time + 1;
				
				
				SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    formatter.format(curDate);    
				
				StringBuffer sb = new StringBuffer(256);
				resultmap=new HashMap<String,Object>();
//				resultmap.put("time", location.getTime());
				resultmap.put("time", str);
				resultmap.put("code", location.getLocType());
				resultmap.put("latitude", location.getLatitude());
				resultmap.put("longitude", location.getLongitude());
				resultmap.put("imei", szImei);
				resultmap.put("speed", location.getSpeed());
				resultmap.put("direction", location.getDirection());
				resultmap.put("counttime", Time);
				
				
				
				
				sb.append("时间:");
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
				sb.append(str);
				// sb.append(SystemClock.elapsedRealtime());
				sb.append("\nerror code:");
				sb.append(location.getLocType());
				sb.append("\n纬度 : ");
				sb.append(location.getLatitude());
				sb.append("\n经度 : ");
				sb.append(location.getLongitude());
				sb.append("\n定位精度 : ");
				sb.append(location.getRadius());
				sb.append("\n国家编码 : ");
				sb.append(location.getCountryCode());
				sb.append("\n国家 : ");
				sb.append(location.getCountry());
				sb.append("\n城市编码 : ");
				sb.append(location.getCityCode());
				sb.append("\n城市 : ");
				sb.append(location.getCity());
				sb.append("\n区/县 : ");
				sb.append(location.getDistrict());
				sb.append("\n街道 : ");
				sb.append(location.getStreet());
				sb.append("\n地址信息 : ");
				sb.append(location.getAddrStr());
				sb.append("\n位置语义化信息: ");
				sb.append(location.getLocationDescribe());
				sb.append("\n方向(not all devices have value): ");
				sb.append(location.getDirection());
				
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
					sb.append("\n速度 : ");
					sb.append(location.getSpeed());// 单位：km/h
					sb.append("\ngps锁定用的卫星数 : ");
					sb.append(location.getSatelliteNumber());
					sb.append("\n高地 : ");
					sb.append(location.getAltitude());// 单位：米
					sb.append("\n定位描述 : ");
					sb.append("gps定位成功");
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
					sb.append("\n运行商 : ");
					sb.append(location.getOperators());
					sb.append("\n定位描述  : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\n定位描述  : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\n定位描述  : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\n定位描述  : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\n定位描述  : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				
				sb.append("\n定位次数:" + Time);
				sb.append("\nIMEI:" + szImei);
				
//	使用eventbus发送数据到界面		
//				logMsg(sb.toString(),resultmap);
			
				
				Log.i("定位次数", "定位次数Time"+Time);
				//*手机唯一标示，手机号码，经度，纬度，时间，速度，方向#
				String ddMMyyHHmmss=GMT8TOGMT0(str);
				DecimalFormat df = new DecimalFormat("#.0000");  
				StringBuffer tosb= new StringBuffer(256);
				tosb.append("*");
				tosb.append("XY");	//制造商 XY 写死XY
				tosb.append(",");
				tosb.append(sp.getString("spTelep",""));	//车载机序列号 写的设备序列号（手机号）
				tosb.append(",");
				tosb.append("V1");	//确认表示符 写死V1
				tosb.append(",");
				tosb.append(ddMMyyHHmmss.substring(6, ddMMyyHHmmss.length()));	//hhmmss
				tosb.append(",");
				tosb.append("A");	//数据有效位（A/V），A表示GPS数据是有效定位数据  写死A
				tosb.append(",");
				tosb.append(df.format(location.getLatitude()*100));     //28.229572*100
				tosb.append(",");
				tosb.append("N");	//纬度表示 N表示北纬  写死
				tosb.append(",");
				tosb.append(df.format(location.getLongitude()*100));	//
				tosb.append(",");
				tosb.append("E");	//东经 
				tosb.append(",");
				tosb.append(location.getSpeed()<0?"":location.getSpeed());//速度 可以为空 表示0
				tosb.append(",");
				tosb.append(location.getDirection()<0?"":location.getDirection()); //方向
				tosb.append(",");
				tosb.append("0");	//总里程数  写死0
				tosb.append(",");
				tosb.append(ddMMyyHHmmss.substring(0, 6));  	//DDMMYY
				tosb.append(",");
				tosb.append("BFF7DFFF"); 	//vehicle_status：车辆状态 写死FFFFFFFF
				tosb.append("#");
				tosb.append("<BFF7DFFF,30,16,257,8562,8527,0>");
//				<BFF7DFFF,30,16,257,8562,8527,0>
				
				
				Log.i("socketservice", "Time定位次数："+Time);
				if(isconnect()){
					sendMessage(tosb.toString());
					success=success+1;
					resultmap.put("success", success);
					Log.i("socketservice", "发送成功次数："+success);
					EventBus.getDefault().post(new eventBusForMsg("正常", resultmap));
				}else{
//					success=success+1;
					resultmap.put("success", "未知");
					Log.i("socketservice", "发送失败次数");
					EventBus.getDefault().post(new eventBusForMsg("连接异常", resultmap));
					socket = null;
					Runnable connect = new connectSocket();
					new Thread(connect).start();
				}
								
			}
		}
	};

	public void IsBoundable() {
		Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
		
	}
	
	public boolean isconnect(){
		
		if(socket!=null){
			 if (!socket.isClosed()) {
              	Log.i("socket", "[if] socket连接isClosed");
                  if (socket.isConnected()) {
                  	Log.i("socket", "[if] socket连接isConnected");
                      if (!socket.isInputShutdown()) {
                    	  try{  
                  				socket.sendUrgentData(0xFF);  
                  				return true;
                  			}catch(IOException e){  
                  				return false;
                  			}                      	 
                      }
                  }
			 }
		}
		return false;
		
	}

	public void sendMessage(String message) {
		if (out != null && !out.checkError()) {
			System.out.println("in sendMessage" + message);
			out.println(message);
			out.flush();
			           
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		System.out.println("I am in on start");
		// Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
		Runnable connect = new connectSocket();
		new Thread(connect).start();
		//启动定位
		locationService.start();
		return START_STICKY;
	}

	class connectSocket implements Runnable {

		@Override
		public void run() {

			try {
				// here you must put your computer's IP address.
				serverAddr = InetAddress.getByName(Flag.SERVERIP);
				Log.e("TCP Client", "C: Connecting...");
				// create a socket to make the connection with the server

				socket = new Socket(serverAddr, Flag.SERVERPORT);

				try {

					// send the message to the server
					out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);

					Log.e("TCP Client", "C: Sent.");

					Log.e("TCP Client", "C: Done.");

				} catch (Exception e) {
					socket=null;
					Log.e("TCP", "S: Error", e);

				}
			} catch (Exception e) {
				socket=null;
				Log.e("TCP", "C: Error", e);

			}

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationService.unregisterListener(mListener); 
		locationService.stop();
		try {
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = null;
	}
	
	
	private String GMT8TOGMT0(String strDate){
//		String strDate = "2016-04-18 16:41:54";  
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