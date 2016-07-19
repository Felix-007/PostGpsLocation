package com.example.postgpslocation.activity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.example.postgpslocation.CustomDialog;
import com.example.postgpslocation.R;
import com.example.postgpslocation.R.id;
import com.example.postgpslocation.R.layout;
import com.example.postgpslocation.R.menu;
import com.example.postgpslocation.activity.RegisterActivity.AT;
import com.example.postgpslocation.activity.RoutePoint.DemoRoutePlanListener;
import com.example.postgpslocation.activity.RoutePoint.MyLocationListenner;
import com.example.postgpslocation.adapter.ViewTextAdapter;
import com.example.postgpslocation.adapter.ZDListViewAdapter;
import com.example.postgpslocation.bean.CustomKeyPointInfo;
import com.example.postgpslocation.eventBus.eventBusForMsg;
import com.example.postgpslocation.net.NetHandler;
import com.example.postgpslocation.net.NetThread;
import com.example.postgpslocation.service.SocketService;
import com.example.postgpslocation.staticClass.Flag;
import com.example.postgpslocation.util.GoogleToBaidu;
import com.example.postgpslocation.view.ScrollUpdateListView;
import com.example.postgpslocation.view.ScrollUpdateListView.OnRefreshListener;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	public ListView FunctionList;
	protected AlertDialog.Builder confirmDialog;
	public Button dolocation;
	public Button routePoint;
	private SharedPreferences sp;
	private Editor editor;
	private TextView welcome;
	private TextView dwtime;
	private TextView routeInfo;

	private ScrollUpdateListView zdListView;
	private ZDListViewAdapter zdadapter;

	//商户点
	List<CustomKeyPointInfo> zxlist=new ArrayList<CustomKeyPointInfo>();
	//邮路list
	List<CustomKeyPointInfo> yldlist=new ArrayList<CustomKeyPointInfo>();
	
	//百度导航
	public static List<Activity> activityList = new LinkedList<Activity>();
	private String mSDCardPath = null;
	private String authinfo = null;
	private static final String APP_FOLDER_NAME = "PostGpsLocation";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";
//	public MyLocationListenner myListener = new MyLocationListenner();
	public MyLocationData locData;
//	LocationClient mLocClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		setContentView(R.layout.activity_main);
		
		//保持屏幕常亮cpu运行
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// FunctionList = (ListView) findViewById(R.id.functionList);
		// FunctionList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.menubutton, getData()));
		// 基础定位按钮
		dolocation = (Button) findViewById(R.id.dolocation);
		dolocation.setBackgroundResource(R.drawable.selector_jcdwgn);
//		// 邮路关键点
//		routePoint = (Button) findViewById(R.id.routePoint);
//		routePoint.setBackgroundResource(R.drawable.selector_jcdwgn);
		
		//邮路信息标题
		routeInfo=(TextView)findViewById(R.id.routeInfo);		
		

		sp = getSharedPreferences("userInfo", 0);
		welcome = (TextView) findViewById(R.id.welcome);
		welcome.setText("欢迎用户：" + sp.getString("spTelep", ""));
		dwtime = (TextView) findViewById(R.id.dwtime);
		dwtime.setText("定位频率：" + sp.getString("timeHz", "") + " s  " + "车牌号："
				+ sp.getString("cph", ""));
		confirmDialog = new AlertDialog.Builder(this);
		confirmDialog.setCancelable(false);
		
		//站点ListView
//		zdListView=(ListView)findViewById(R.id.ZDListView);
		zdListView = (ScrollUpdateListView) findViewById(R.id.ZDListView);
		zdListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				routeInfo.setText("邮路信息:加载中…");
				reqData();
			}
		});
		
		zdadapter = new ZDListViewAdapter(this,zxlist);//得到一个MyAdapter对象 
//		zdadapter = new ZDListViewAdapter(this,CustomKeyPointInfo.infos);//得到一个MyAdapter对象 
		zdListView.setAdapter(zdadapter);
		zdListView.setOnItemClickListener(zdlistListener);
		
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true); // 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();
	
		reqData();
		 
		 //初始化导航引擎
		if (initDirs()) {
			if (true) {
				initNavi();		
				}
		}
		//
		startService(new Intent(MainActivity.this, SocketService.class));
		doBindService();
		 
		
		//注册eventbus
		EventBus.getDefault().register(MainActivity.this);
	}
	
	@Subscribe
	public void onEventMainThread(eventBusForMsg event) {
//		Toast.makeText(MainActivity.this, "我也在接受", Toast.LENGTH_LONG).show();
		if("正常".equals( event.getmMsg())){
			Toast.makeText(MainActivity.this, "定位上传成功", Toast.LENGTH_LONG).show();
			
		}else{
			Toast.makeText(MainActivity.this, event.getmMsg(), Toast.LENGTH_LONG).show();  
		}
			Map<String,Object> resultmap=event.getResultmap();
//			resultmap.get("latitude");
//			resultmap.get("longitude");
			//设置当前未知 导航用
			locData = new MyLocationData.Builder()			
			.direction(100).latitude((Double) resultmap.get("latitude"))
			.longitude((Double)resultmap.get("longitude")).build();
	}
	
//	public class MyLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			// map view 销毁后不在处理新接收的位置
//			if (location == null ) {
//				return;
//			}
//			locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//		}
//
//	
//	}
	
	//zdlistView的点击监听
	OnItemClickListener zdlistListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			if(zxlist.get(arg2-1).getJd()==-1||zxlist.get(arg2-1).getWd()==-1){
				Toast.makeText(MainActivity.this, "没有数据无法启动导航……"+arg2, Toast.LENGTH_SHORT).show();
			}else{			
				if (BaiduNaviManager.isNaviInited()) {
					Toast.makeText(MainActivity.this, "启动导航……"+arg2, Toast.LENGTH_SHORT).show();
	//				routeplanToNavi(CoordinateType.BD09LL);
	//				BNRoutePlanNode sNode = new BNRoutePlanNode(zxlist.get(arg2).getJd(), 4846474, "百度大厦", null, coType);
				
					BNRoutePlanNode sNode = new BNRoutePlanNode(locData.longitude,locData.latitude, "我的位置", null, CoordinateType.BD09LL);
					BNRoutePlanNode eNode = new BNRoutePlanNode(zxlist.get(arg2-1).getJd(), zxlist.get(arg2-1).getWd(), zxlist.get(arg2-1).getZdmc(), null, CoordinateType.BD09LL);
				
					routeplanToNavi(CoordinateType.BD09LL, sNode, eNode);
				}
			}
		}
	};
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		dolocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						LocationActivity.class);
				intent.putExtra("from", "0");
//				mLocClient.stop();
				startActivity(intent);
				
			}
		});


	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		mLocClient.stop();
		super.onDestroy();
		EventBus.getDefault().unregister(MainActivity.this);
		stopService(new Intent(MainActivity.this, SocketService.class));
		doUnbindService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			showConfirmDialog("解除当前绑定？(解绑需要重新注册绑定)", null, "确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							new Thread() {
								public void run() {
									System.out.println("想要解除绑定啊！！！！！！！！");
									new UnbundlingAT().execute(sp.getString(
											"spTelep", ""));
								}
							}.start();
						}
					}, null, null, "取消", null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		data.add("基础定位功能");
		// data.add("自定义参数定位");
		// data.add("function3");

		return data;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showConfirmDialog("是否注销登录？", null, "注销",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							new Thread() {
								public void run() {
									editor = sp.edit();
									editor.putString("spTelep", "");
									editor.putString("spImei", "");
									editor.putBoolean("spIslog", false);
									editor.putString("timeHz", "");
									editor.putString("cph", "");
									editor.commit();
									System.out.println(sp.getBoolean("spIslog",
											false) + "注销后啊啊啊！！！！！！！！");
									app.Token = null;
									MainActivity.this.finish();
								}
							}.start();
						}
					}, null, null, "取消", null);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showConfirmDialog(String title, String message,
			String positive, DialogInterface.OnClickListener positiveListener,
			String neutral, DialogInterface.OnClickListener neutralListener,
			String negative, DialogInterface.OnClickListener negativeListener) {
		confirmDialog.setTitle(title);
		if (message != null) {
			confirmDialog.setMessage(message);
		}
		if (positive != null) {
			confirmDialog.setPositiveButton(positive, positiveListener);
		}
		if (neutral != null) {
			confirmDialog.setNeutralButton(neutral, neutralListener);
		}
		if (negative != null) {
			confirmDialog.setNegativeButton(negative, negativeListener);
		}
		confirmDialog.show();
	}

//	public static final String HTTPCustomer = "http://10.10.5.98/zgyz/mobi_login!unbindDevice.action";

	@SuppressWarnings("rawtypes")
	class UnbundlingAT extends AsyncTask {

		String result = "";

		@Override
		protected void onPreExecute() {
			// 加载progressDialog
		}

		@Override
		protected Object doInBackground(Object... params_obj) {
			System.out.println(params_obj[0] + "-------------");

			// 请求数据
			HttpPost httpRequest = new HttpPost(Flag.HTTPCustomer_unbindDevice);
			// 创建参数
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("sbxlh", params_obj[0].toString()));
			try {
				// 对提交数据进行编码
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);
				// 获取响应服务器的数据
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 利用字节数组流和包装的绑定数据
					byte[] data = new byte[2048];
					// 先把从服务端来的数据转化成字节数组
					data = EntityUtils.toByteArray((HttpEntity) httpResponse
							.getEntity());
					// 再创建字节数组输入流对象
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					// 绑定字节流和数据包装流
					DataInputStream dis = new DataInputStream(bais);
					// 将字节数组中的数据还原成原来的各种数据类型，代码如下：
					result = new String(dis.readUTF());
					Log.i("服务器返回信息:", result);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			// result ="{\"result\":1,\"msg\":\"解绑成功\"}";
			// result ="{\"result\":1,\"msg\":\"成功\"}";
			JSONObject jsonObj;
			String type = "", msg = "";
			try {
				jsonObj = new JSONObject(result.toString());
				type = jsonObj.getString("result");
				msg = jsonObj.getString("msg");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (type.equals("1")) {
				app.Token = null;
				MainActivity.this.finish();
				editor = sp.edit();
				editor.putString("spTelep", "");
				editor.putString("spImei", "");
				editor.putBoolean("spIslog", false);
				editor.putString("timeHz", "");
				editor.putString("cph", "");
				editor.commit();
				System.out.println(msg);
			}else{
				Toast.makeText(MainActivity.this, "解除绑定失败", Toast.LENGTH_SHORT).show();
			}

		}
		
	}
		
		
		
		/**
		 * 获取后台商户数据和路径数据 显示成为listview
		 */
		
		private NetHandler mHandler = new NetHandler(this) {
			protected void call_comm(Message msg) {
				if (msg.what == 300) {
					showDialog(CALL_DATA_DISPOSE);
					Log.d("下载关键点", "调用下载数据线程开始！");
					
				}
				if (msg.what == 301) {
					
					Bundle b = msg.getData();
					int result = b.getInt("result");
					if (result == 1) {
						String ss=b.getString("data");
						
						if("{}".equals(b.getString("data"))||b.getString("data")==null){
							zxlist.clear();
							Toast.makeText(MainActivity.this, "该线路没有邮路+"+b.getString("msg"),
									Toast.LENGTH_LONG).show();
							zxlist.add(new CustomKeyPointInfo(-1,-1,"该线路没有邮路"));
							routeInfo.setText("邮路信息:");
						}else{
						
						zxlist.clear();
//						yldlist=null;
						
						Log.i("关键点", msg.getData().getString("data"));
						try {
							JSONObject o = new JSONObject(String.valueOf(b
									.getString("data")));
							// 显示到地图
							
							Map map = new HashMap();
							for (Iterator iter = o.keys(); iter.hasNext();) {
								String key = (String) iter.next();
								map.put(key, o.get(key));
							}
							Log.i("关键点", map.get("zxlist").toString());
							Log.i("关键点", map.get("yldlist").toString());
							//取出了zxlist yldlist的字符串
							
							JSONArray zxlistary = new JSONArray(String.valueOf(map.get("zxlist").toString()));
							for(int i=0;i<zxlistary.length();i++){
								 JSONObject jsonObject = zxlistary.getJSONObject(i);
								 
								 Log.i("关键点zxlist", jsonObject.getString("jd"));
								 Log.i("关键点zxlist",jsonObject.getString("wd"));
								 Object jd=jsonObject.get("jd");
								 Object wd=jsonObject.get("wd");
								 if(jd==null||wd==null||jd.toString().equals("null")||wd.toString().equals("null")||jd.toString().equals("")||wd.toString().equals("")){
									 Log.i("空的JD或者WD", "空的JD或者WD");
								 }else{
									 //google  GCJ02转位百度的BD09
									 Map<String,Double> mao= GoogleToBaidu.Convert_GCJ02_To_BD09(jsonObject.getDouble("wd"),jsonObject.getDouble("jd"));
//									 CustomKeyPointInfo cki=new CustomKeyPointInfo(mao.get("lat"), mao.get("lng"), jsonObject.getString("zdmc"),jsonObject.getString("xh"));
									 CustomKeyPointInfo cki=new CustomKeyPointInfo(Double.valueOf(String.format("%.10f",mao.get("lng"))),Double.valueOf(String.format("%.10f",mao.get("lat"))), jsonObject.getString("zdmc"),jsonObject.getString("xh"));
									
									 zxlist.add(cki);
								 }
							}
							
							JSONArray yldlistary = new JSONArray(String.valueOf(map.get("yldlist").toString()));
							for(int i=0;i<yldlistary.length();i++){
								 JSONObject jsonObject = yldlistary.getJSONObject(i);
								 
								 Log.i("关键点yldlist", jsonObject.getString("jd"));
								 Log.i("关键点yldlist",jsonObject.getString("wd"));
								 Object jd=jsonObject.get("jd");
								 Object wd=jsonObject.get("wd");
								 if(jd==null||wd==null||jd.toString().equals("null")||wd.toString().equals("null")||jd.toString().equals("")||wd.toString().equals("")){
									 Log.i("空的JD或者WD", "空的JD或者WD");
								 }else{
									 Map<String,Double> mao= GoogleToBaidu.Convert_GCJ02_To_BD09(jsonObject.getDouble("wd"),jsonObject.getDouble("jd"));
									 CustomKeyPointInfo cki=new CustomKeyPointInfo(Double.valueOf(String.format("%.10f",mao.get("lng"))),Double.valueOf(String.format("%.10f",mao.get("lat"))));
//									 CustomKeyPointInfo cki=new CustomKeyPointInfo(jsonObject.getDouble("wd"), jsonObject.getDouble("jd"));
									 yldlist.add(cki);
								 }
							}
							
							zdadapter.notifyDataSetChanged();
//							addCustomElementsDemo(zxlist);
//							addCustomyldlist(yldlist);
							routeInfo.setText("邮路信息:"+map.get("ylmc").toString());
						
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					} else {
						
						zxlist.clear();
//						yldlist=new ArrayList<CustomKeyPointInfo>();
						
						Log.i("关键点", msg.getData().getString("msg"));
						Toast.makeText(MainActivity.this, "获取数据失败+"+b.getString("msg"),
								Toast.LENGTH_LONG).show();
						zxlist.add(new CustomKeyPointInfo(-1,-1,"获取邮路数据失败"));
						zxlist.addAll(CustomKeyPointInfo.infos);
						zdadapter.notifyDataSetChanged();
						routeInfo.setText("邮路信息:获取失败。");
					}
					
					zdListView.onRefreshComplete();
					removeDialog(CALL_DATA_DISPOSE);
				}
			};
		};

		public void reqData() {
			new NetThread(null) {
				public void run() {
					// 登陆接口
					mHandler.sendEmptyMessage(NetHandler.CALL_CLIENT_getPostRoadTask4MobilePhone);

					Map<String, Object> m = app.mNetService
							.getPostRoadTask4MobilePhone(sp.getString("spTelep", ""));
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putInt("result",
							Integer.valueOf(m.get("result").toString()));
					data.putString("msg", String.valueOf(m.get("msg")));
					data.putString("data", String.valueOf(m.get("data")));
					msg.setData(data);
					msg.what = NetHandler.BACK_CLIENT_getPostRoadTask4MobilePhone;
					mHandler.sendMessage(msg);

				}
			}.start();
		}
		
		/**
		 * 百度地图
		 */
		private boolean initDirs() {
			mSDCardPath = getSdcardDir();
			if (mSDCardPath == null) {
				return false;
			}
			File f = new File(mSDCardPath, APP_FOLDER_NAME);
			if (!f.exists()) {
				try {
					f.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}


		/**
		 * 内部TTS播报状态回传handler
		 */
		private Handler ttsHandler = new Handler() {
		    public void handleMessage(Message msg) {
		        int type = msg.what;
		        switch (type) {
		            case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
		                 showToastMsg("Handler : TTS play start");
		                break;
		            }
		            case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
		                 showToastMsg("Handler : TTS play end");
		                break;
		            }
		            default :
		                break;
		        }
		    }
		};
		
		/**
		 * 内部TTS播报状态回调接口
		 */
		private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {
	        
	        @Override
	        public void playEnd() {
//	            showToastMsg("TTSPlayStateListener : TTS play end");
	        }
	        
	        @Override
	        public void playStart() {
//	            showToastMsg("TTSPlayStateListener : TTS play start");
	        }
	    };
		
		public void showToastMsg(final String msg) {
		    MainActivity.this.runOnUiThread(new Runnable() {

	            @Override
	            public void run() {
	                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	            }
	        });
		}
		
		private void initNavi() {	
		
			BNOuterTTSPlayerCallback ttsCallback = null;
			
			BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
				@Override
				public void onAuthResult(int status, String msg) {
					if (0 == status) {
						authinfo = "key校验成功!";
					} else {
						authinfo = "key校验失败, " + msg;
					}
					MainActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(MainActivity.this, authinfo, Toast.LENGTH_LONG).show();
						}
					});
				}

				public void initSuccess() {
					Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
					initSetting();
				}

				public void initStart() {
					Toast.makeText(MainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
				}

				public void initFailed() {
					Toast.makeText(MainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
				}


			},  null, ttsHandler, null);

		}

		private String getSdcardDir() {
			if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				return Environment.getExternalStorageDirectory().toString();
			}
			return null;
		}

		private void routeplanToNavi(CoordinateType coType,BNRoutePlanNode sNode,BNRoutePlanNode eNode) {
			switch (coType) {
			case BD09LL: {
//				sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
//				sNode = new BNRoutePlanNode(112.915066, 30.229795, "我的未知", null, coType);
//				eNode = new BNRoutePlanNode(112.91665,30.229977, "百度大厦", null, coType);
//				eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
				break;
			}
			default:
				;
			}
				if (sNode != null && eNode != null) {
					List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
					list.add(sNode);
					//可以添加三个途经点哦
//					list.add(new BNRoutePlanNode(12938110, 4835927, "北京门", null, coType));
					list.add(eNode);
					BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
				}
		}

		public class DemoRoutePlanListener implements RoutePlanListener {

			private BNRoutePlanNode mBNRoutePlanNode = null;

			public DemoRoutePlanListener(BNRoutePlanNode node) {
				mBNRoutePlanNode = node;
			}

			@Override
			public void onJumpToNavigator() {
				/*
				 * 设置途径点以及resetEndNode会回调该接口
				 */
			 
				for (Activity ac : activityList) {
				   
					if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
					 
						return;
					}
				}
				Intent intent = new Intent(MainActivity.this, BNDemoGuideActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
//				bundle.putParcelableArrayList(key, value)
				intent.putExtras(bundle);
				startActivity(intent);
				
			}

			@Override
			public void onRoutePlanFailed() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
			}
			
		}
			private void initSetting(){
			    BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
			    BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
			    BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);	    
		        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
			}

			private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

				@Override
				public void stopTTS() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "stopTTS");
				}

				@Override
				public void resumeTTS() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "resumeTTS");
				}

				@Override
				public void releaseTTSPlayer() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "releaseTTSPlayer");
				}

				@Override
				public int playTTSText(String speech, int bPreempt) {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

					return 1;
				}

				@Override
				public void phoneHangUp() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "phoneHangUp");
				}

				@Override
				public void phoneCalling() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "phoneCalling");
				}

				@Override
				public void pauseTTS() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "pauseTTS");
				}

				@Override
				public void initTTSPlayer() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "initTTSPlayer");
				}

				@Override
				public int getTTSState() {
					// TODO Auto-generated method stub
					Log.e("test_TTS", "getTTSState");
					return 1;
				}
			};
			
			
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
				bindService(new Intent(MainActivity.this, SocketService.class),
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

}
