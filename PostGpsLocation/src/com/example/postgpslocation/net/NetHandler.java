package com.example.postgpslocation.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public abstract class NetHandler extends Handler {

	final static String TAG = "NetHandler";

	public static final int DEFAULT = -1;
	public static final int CALL_TOAST_SHOW = 0;
	public static final int CALL_DATA_DISPOSE = 99;
	public static final int BACK_DATA_DISPOSE = 100;
	// public static final int CALL_DEBUG_UP_LOG = 111;
	// public static final int BACK_DEBUG_UP_LOG = 112;

	public static final int CALL_LOGIN_DO_LOGIN = 1;
	public static final int BACK_LOGIN_DO_LOGIN = 2;
	public static final int CALL_LOGIN_DO_LOGOUT = 3;
	public static final int BACK_LOGIN_DO_LOGOUT = 4;
	// public static final int CALL_LOGIN_DO_EDITPWD = 5;
	// public static final int BACK_LOGIN_DO_EDITPWD = 6;
	public static final int CALL_LOGIN_CHK_VERSION = 7;
	public static final int BACK_LOGIN_CHK_VERSION = 8;
	public static final int CALL_LOGIN_CHK_DATE = 9;
	public static final int BACK_LOGIN_CHK_DATE = 10;
	
	private Context context;

	/**
	 * 罗帆
	 */
	
	public static final int CALL_CLIENT_getPostRoadTask4MobilePhone=300;
	public static final int BACK_CLIENT_getPostRoadTask4MobilePhone=301;
	
	public NetHandler(Context context) {
		this.context = context;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		Bundle b = msg.getData();
		switch (msg.what) {
		case CALL_TOAST_SHOW:
			call_TOAST_SHOW(b);
			break;
		case CALL_DATA_DISPOSE:
			call_DATA_DISPOSE(b);
			break;
		case BACK_DATA_DISPOSE:
			back_DATA_DISPOSE(b);
			break;
		// case CALL_DEBUG_UP_LOG:
		// call_DEBUG_UP_LOG(b);
		// break;
		// case BACK_DEBUG_UP_LOG:
		// back_DEBUG_UP_LOG(b);
		// break;
		case CALL_LOGIN_DO_LOGIN:
			call_LOGIN_DO_LOGIN(b);
			break;
		case BACK_LOGIN_DO_LOGIN:
			back_LOGIN_DO_LOGIN(b);
			break;
		case CALL_LOGIN_DO_LOGOUT:
			call_LOGIN_DO_LOGOUT(b);
			break;
		case BACK_LOGIN_DO_LOGOUT:
			back_LOGIN_DO_LOGOUT(b);
			break;
		// case CALL_LOGIN_DO_EDITPWD:
		// call_LOGIN_DO_EDITPWD(b);
		// break;
		// case BACK_LOGIN_DO_EDITPWD:
		// back_LOGIN_DO_EDITPWD(b);
		// break;
		case CALL_LOGIN_CHK_VERSION:
			call_LOGIN_CHK_VERSION(b);
			break;
		case BACK_LOGIN_CHK_VERSION:
			back_LOGIN_CHK_VERSION(b);
			break;
		case CALL_LOGIN_CHK_DATE:
			call_LOGIN_CHK_DATE(b);
			break;
		case BACK_LOGIN_CHK_DATE:
			back_LOGIN_CHK_DATE(b);
			break;
		case CALL_CLIENT_getPostRoadTask4MobilePhone:
			CALL_CLIENT_getPostRoadTask4MobilePhone(b);
			break;
		case BACK_CLIENT_getPostRoadTask4MobilePhone:
			BACK_CLIENT_getPostRoadTask4MobilePhone(b);
			break;	
		default:
			break;
		}
		call_comm(msg);
	}

	protected void call_comm(Message msg) {
		Log.w(TAG, "空方法");
	}

	public void showSubThreadToast(String msg) {
		Message message = new Message();
		message.what = NetHandler.CALL_TOAST_SHOW;
		Bundle data = new Bundle();
		data.putString("msg", msg);
		message.setData(data);
		this.sendMessage(message);
	}

	protected void call_TOAST_SHOW(Bundle b) {
		Toast.makeText(context, (String) b.get("msg"), Toast.LENGTH_LONG)
				.show();
	}

	protected void call_DATA_DISPOSE(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void back_DATA_DISPOSE(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void call_LOGIN_DO_LOGIN(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void back_LOGIN_DO_LOGIN(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void call_LOGIN_DO_LOGOUT(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void back_LOGIN_DO_LOGOUT(Bundle b) {
		Log.w(TAG, "空方法");
	}

	// protected void call_LOGIN_DO_EDITPWD(Bundle b) {
	// Log.w(TAG, "空方法");
	// }
	//
	// protected void back_LOGIN_DO_EDITPWD(Bundle b) {
	// Log.w(TAG, "空方法");
	// }

	protected void call_LOGIN_CHK_VERSION(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void back_LOGIN_CHK_VERSION(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void call_LOGIN_CHK_DATE(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void back_LOGIN_CHK_DATE(Bundle b) {
		Log.w(TAG, "空方法");
	}

	protected void CALL_CLIENT_getPostRoadTask4MobilePhone(Bundle b) {
		Log.w(TAG, "空方法");
	}
	protected void BACK_CLIENT_getPostRoadTask4MobilePhone(Bundle b) {
		Log.w(TAG, "空方法");
	}
}
