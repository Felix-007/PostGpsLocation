package com.example.postgpslocation.activity;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.TextView;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.example.postgpslocation.R;
import com.example.postgpslocation.bean.CustomKeyPointInfo;
import com.example.postgpslocation.net.NetHandler;
import com.example.postgpslocation.net.NetThread;
import com.example.postgpslocation.util.GoogleToBaidu;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出tips
 */
public class RoutePoint extends BaseActivity {
	
	private final static String ACCESS_KEY = "zH1XD4diA1OMOC7HjfTRW4Mp";
	private boolean mIsEngineInitSuccess = false;
	public String spTelep;

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker = null;
	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	// OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true; // 是否首次定位
	public ProgressDialog progressDialog;
	private Spinner end_et;	//起始点 和终点
	private  TextView ylmc;
	
	private Button start_nav_btn; //导航button

	// 客户图片
	BitmapDescriptor shop = BitmapDescriptorFactory
			.fromResource(R.drawable.shop);

	private SharedPreferences sp;
	// DrivingRouteLine
	// 线路规划
	// private POI searchModel;
	
	
//	private BNaviPoint mStartPoint = new BNaviPoint(109.69618, 27.42964, "湖南湘邮科技股份有限公司", BNaviPoint.CoordinateType.GCJ02);
//	private BNaviPoint mEndPoint = new BNaviPoint(109.684022, 27.362847, "王府井", BNaviPoint.CoordinateType.GCJ02);
	private BNRoutePlanNode mStartPoint;
	private BNRoutePlanNode mEndPoint ;
	private List<BNRoutePlanNode> mViaPoints = new ArrayList<BNRoutePlanNode>();	//设置途经点，最多设置三个
	String authinfo = null;
	private String mSDCardPath = null;
	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
	public static List<Activity> activityList = new LinkedList<Activity>();
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		setContentView(R.layout.activity_routepoint);
		
		requestLocButton = (Button) findViewById(R.id.button1);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		//起点 终点 导航按钮右路名称
//		start_et=(Spinner) findViewById(R.id.start_et);
		ylmc=(TextView) findViewById(R.id.ylmc);
		
		end_et=(Spinner) findViewById(R.id.end_et);
		start_nav_btn=(Button) findViewById(R.id.start_nav_btn);
		
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));

					break;
				default:
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		sp = getSharedPreferences("userInfo", 0);
		spTelep = sp.getString("spTelep", "");
		// 获取邮路和关键点信息
		progressDialog = new ProgressDialog(this);
		Log.i("设备序列号", spTelep);

		reqData();
		
		
		//测试的两个点
//		addCustomElementsDemo(CustomKeyPointInfo.infos);
		
//		findViewById(R.id.start_nav_btn).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                if (mViaPoints.size() == 0) {
//                    launchNavigator();
//                } else {
//                    launchNavigatorViaPoints();
//                }
//            }
//        });
		findViewById(R.id.start_nav_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (BaiduNaviManager.isNaviInited()) {
					routeplanToNavi(CoordinateType.BD09_MC);
				}
			}
		});

		
		
		//初始化导航引擎
		if (initDirs()) {
			if (true) {
				initNavi();		
			}
		}
		
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(15.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory
						.newMapStatus(builder.build()));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

	
	}

	// 添加关键点
	public void addCustomElementsDemo(List<CustomKeyPointInfo> infos ) {
		Marker marker = null;
		LatLng llDot = null;
		OverlayOptions o3 = null;
//		 infos = CustomKeyPointInfo.infos;
		for (CustomKeyPointInfo info : infos) {

			llDot = new LatLng(info.getWd(), info.getJd());
			OverlayOptions ooDot = new DotOptions().center(llDot).radius(10)
					.color(0xFFFF0000);
			mBaiduMap.addOverlay(ooDot);
			// 图标
			o3 = new MarkerOptions().position(llDot).icon(shop).zIndex(0);
			marker = (Marker) (mBaiduMap.addOverlay(o3));
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// 获得marker中的数据
				CustomKeyPointInfo info = (CustomKeyPointInfo) marker
						.getExtraInfo().get("info");

				InfoWindow mInfoWindow = null;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.pointdialogue);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getZdmc());
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				Log.v("point", "点击了关键点" + info.getZdmc());
				// 显示信息小框
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(location), llInfo, -47,
						new OnInfoWindowClickListener() {
							@Override
							public void onInfoWindowClick() {
								mBaiduMap.hideInfoWindow();
							}
						});

				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});

	}
	
//	yldlist  邮路轨迹添加到地图
	public void addCustomyldlist(List<CustomKeyPointInfo> infos ) {
		Marker marker = null;
		LatLng llDot = null;
		OverlayOptions o3 = null;
		List<LatLng> points = new ArrayList<LatLng>();
		for (CustomKeyPointInfo info : infos) {
			llDot = new LatLng(info.getWd(), info.getJd());
//			OverlayOptions ooDot = new DotOptions().center(llDot).radius(10)
//					.color(0xFF0000FF);
//			mBaiduMap.addOverlay(ooDot);
			points.add(llDot);
		}
	
		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.points(points);
		polylineOptions.color(0xFF000000);
		polylineOptions.width(4);// 折线线宽
		mBaiduMap.addOverlay(polylineOptions);
		
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

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
					Log.i("关键点", msg.getData().getString("data"));
					try {
						JSONObject o = new JSONObject(String.valueOf(b
								.getString("data")));
						// 显示到地图
						//邮路名称显示
						ylmc.setText("奔往未来的邮路");
						
						//商户点
						List<CustomKeyPointInfo> zxlist=new ArrayList<CustomKeyPointInfo>();
						//邮路list
						List<CustomKeyPointInfo> yldlist=new ArrayList<CustomKeyPointInfo>();
						
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
//								 CustomKeyPointInfo cki=new CustomKeyPointInfo(mao.get("lat"), mao.get("lng"), jsonObject.getString("zdmc"),jsonObject.getString("xh"));
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
//								 CustomKeyPointInfo cki=new CustomKeyPointInfo(jsonObject.getDouble("wd"), jsonObject.getDouble("jd"));
								 yldlist.add(cki);
							 }
						}
						
						addCustomElementsDemo(zxlist);
						addCustomyldlist(yldlist);
						//end_et 显示数据
						ArrayList<String> end_etlist=new ArrayList<String>();
						
						for(int i=0;i<zxlist.size();i++){
						end_etlist.add(zxlist.get(i).getXh()+zxlist.get(i).getZdmc());
						}
//						String[] aaa =  { "a","b"};       
//						new ArrayAdapter<String>(RoutePoint.this, android.R.layout.simple_spinner_item, end_etlist);
//						new ArrayAdapter<String>(RoutePoint.this, android.R.layout.simple_spinner_item, aaa);
//						end_et.setAdapter(new ArrayAdapter<T>(this, android.R.layout.simple_spinner_item, aaa);)
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					Log.i("关键点", msg.getData().getString("msg"));
					Toast.makeText(RoutePoint.this, b.getString("msg"),
							Toast.LENGTH_LONG).show();
				}
			}
		};
	};

	public void reqData() {
		new NetThread(null) {
			public void run() {
				// 登陆接口
				mHandler.sendEmptyMessage(NetHandler.CALL_CLIENT_getPostRoadTask4MobilePhone);

				Map<String, Object> m = app.mNetService
						.getPostRoadTask4MobilePhone(spTelep);
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
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }
        
        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
	
	public void showToastMsg(final String msg) {
	    RoutePoint.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(RoutePoint.this, msg, Toast.LENGTH_SHORT).show();
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
				RoutePoint.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(RoutePoint.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			public void initSuccess() {
				Toast.makeText(RoutePoint.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
				initSetting();
			}

			public void initStart() {
				Toast.makeText(RoutePoint.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
			}

			public void initFailed() {
				Toast.makeText(RoutePoint.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
			}


		},  null, ttsHandler, null);

	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private void routeplanToNavi(CoordinateType coType) {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;
		switch (coType) {
			case BD09_MC: {
				sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
				eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
				break;
			}
			default:
				;
			}
			if (sNode != null && eNode != null) {
				List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
				list.add(sNode);
				list.add(new BNRoutePlanNode(12938110, 4835927, "北京门", null, coType));
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
			Intent intent = new Intent(RoutePoint.this, BNDemoGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
			
		}

		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			Toast.makeText(RoutePoint.this, "算路失败", Toast.LENGTH_SHORT).show();
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
}
