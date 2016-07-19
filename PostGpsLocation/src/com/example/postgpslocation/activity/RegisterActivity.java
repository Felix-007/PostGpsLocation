package com.example.postgpslocation.activity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.example.postgpslocation.CustomDialog;
import com.example.postgpslocation.MyApplication;
import com.example.postgpslocation.R;
import com.example.postgpslocation.staticClass.Flag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	public Button register;
	public EditText telephonenum,cphnum;
	private String szImei = " ";
	public ProgressDialog progressDialog;
	
	private SharedPreferences sp;
	private	Editor editor;
	
	protected MyApplication app = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		telephonenum = (EditText) findViewById(R.id.telephonenumRegister);
		cphnum = (EditText) findViewById(R.id.cphnum);
		register = (Button) findViewById(R.id.registerRegister);
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		szImei = TelephonyMgr.getDeviceId();
		//本地存储
		sp = getSharedPreferences("userInfo", 0);
		String spTelep=sp.getString("spTelep", "");
	    String spImei =sp.getString("spImei", "");
	    String timeHz =sp.getString("timeHz", "");
	    String C_CPH=sp.getString("cph", "");
	    Boolean spIslog =sp.getBoolean("spIslog", false);
	    System.out.println(spTelep+"刚刚开始-！！！！！！");
	    System.out.println(spImei);
	    System.out.println(spIslog);
	    editor=sp.edit();
	    
	    telephonenum.setText(spTelep);
	    cphnum.setText(C_CPH);
	    
	    app = (MyApplication) getApplication();
	    
	    //绑定监听
		setListener();
		progressDialog = new ProgressDialog(this);
		
		  if(spIslog){
		    	new AT().execute(spTelep, spImei,C_CPH);
		    }
	}

	private void setListener() {
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String telep = telephonenum.getText().toString();
				String cph = cphnum.getText().toString();
				
				if (!("".equals(telep.trim()) || "".equals(szImei.trim())||"".equals(cph.trim()))) {
					new AT().execute(telep, szImei,cph);
					
					editor.putString("spTelep", telep);
					editor.putString("spImei", szImei);
					editor.putString("cph", cph);
					editor.commit();
				} else {
					Toast.makeText(RegisterActivity.this, "手机号,车牌号不能为空",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}
//	public static final String HTTPCustomer = "http://10.10.5.98/zgyz/mobi_login!phoneLogin.action";

	@SuppressWarnings("rawtypes")
	class AT extends AsyncTask {

		String result = "";

		@Override
		protected void onPreExecute() {
			// 加载progressDialog
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected Object doInBackground(Object... params_obj) {
			System.out.println(params_obj[0] + "-------------");
			System.out.println(params_obj[1]);
			System.out.println(params_obj[2]);

			// 请求数据
			HttpPost httpRequest = new HttpPost(Flag.HTTPCustomer_phoneLogin);
			// 创建参数
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("sbxlh", params_obj[0].toString()));
			params.add(new BasicNameValuePair("imei", params_obj[1].toString()));
			params.add(new BasicNameValuePair("c_cph", params_obj[2].toString()));
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
//					while((str=DataInputStream.readUTF( fi )) != null )
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
			// 取消进度条
			progressDialog.cancel();
			result ="{\"result\":1,\"msg\":\"成功\",\"time\":\"10\",\"token\":\"token\"}";
			Log.i("register+result", result.toString());
			if("".equals(result.toString())||result==null){
				//显示失败对话框
				 CustomDialog dialog=new CustomDialog(RegisterActivity.this,R.style.loginDialog,R.layout.logindialog);
		         dialog.show();
				Toast.makeText(RegisterActivity.this,
						"失败,请检查网络链接." + result.toString(), Toast.LENGTH_LONG)
						.show();
				return;
			}
			JSONObject jsonObj ;
			String type="",msg="",time="",token="";
			try {
				jsonObj=new JSONObject(result.toString());
				type=jsonObj.getString("result");
				msg=jsonObj.getString("msg");
				time=jsonObj.getString("time");
				token=jsonObj.getString("token");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (type.equals("1")) {
				editor.putBoolean("spIslog", true);
				editor.putString("timeHz", time);
				//还有其他数据再存入
				editor.commit();
				
				app.Token = String.valueOf(token);
				
				//绑定成功,登录成功
				Log.i("TAG", msg);
				Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG)
						.show();
				
				Intent i = new Intent();
				i.setClass(RegisterActivity.this, MainActivity.class);
				startActivity(i);
			} else if(type.equals("2")){
				//设备已经被别的手机绑定
				//显示失败对话框
				 CustomDialog dialog=new CustomDialog(RegisterActivity.this,R.style.loginDialog,R.layout.logindialog);
				 dialog.show();
				Log.i("TAG", msg);
				Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG)
				.show();
			}
			
		}

	}

}
