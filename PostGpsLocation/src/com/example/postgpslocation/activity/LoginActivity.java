package com.example.postgpslocation.activity;

import com.example.postgpslocation.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public EditText telephonenum;
	public EditText password;
	private String szImei = "";
	public Button login, register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
//		username = (EditText) findViewById(R.id.username);
		telephonenum = (EditText) findViewById(R.id.telephonenum);
		password = (EditText) findViewById(R.id.password);
	
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		// 设置监听
		setListener();
	}

	private void setListener() {
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = telephonenum.getText().toString();
				String pass = password.getText().toString();
				if (!("".equals(name.trim())||"".equals(pass.trim()))) {					
					Log.i("TAG", name + "," + pass);
					// UserService userService=new
					// UserService(LoginActivity.this);
					// boolean flag=userService.login(name, pass);
					boolean flag = true;
					if (flag) {
						Log.i("TAG", "登录成功");
						Toast.makeText(LoginActivity.this, "登录成功",
								Toast.LENGTH_LONG).show();
						Bundle b = new Bundle();
						b.putString("name", name);
						Intent i = new Intent();
						i.setClass(LoginActivity.this, MainActivity.class);
						i.putExtras(b);
						startActivity(i);
					} else {
						Log.i("TAG", "登录失败");
						Toast.makeText(LoginActivity.this, "登录失败",
								Toast.LENGTH_LONG).show();

					}
				}else{
					Toast.makeText(LoginActivity.this, "手机号和密码不能为空",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

}
