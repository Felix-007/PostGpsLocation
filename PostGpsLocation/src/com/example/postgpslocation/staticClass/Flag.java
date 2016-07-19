package com.example.postgpslocation.staticClass;

public class Flag {
		public static boolean socketSwith=false;
		public static boolean buttonSwith=false; //false显示开关是关闭的
		public static boolean isloc=false;
		
		//socket连接地址和端口
		public static final String SERVERIP = "211.156.198.45";
		public static final int SERVERPORT = 7881;
		//邮路下载的地址
		public final static String SVR_URL = "http://10.10.5.98/zgyz/";
		//登录 解绑
		public static final String HTTPCustomer_phoneLogin = "http://10.10.5.98/zgyz/mobi_login!phoneLogin.action";
		public static final String HTTPCustomer_unbindDevice = "http://10.10.5.98/zgyz/mobi_login!unbindDevice.action";
		
}
