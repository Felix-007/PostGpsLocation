package com.example.postgpslocation.activity;

import java.util.Map;

import com.example.postgpslocation.MyApplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class BaseActivity extends Activity {

//	@Override  
//	public boolean onCreateOptionsMenu(Menu menu) {  
//	  // TODO Auto-generated method stub   
//		menu.add(0, 0, 1, "截屏并分享至微信");  
//		menu.add(0, 1, 2, "截屏并分享至QQ");  
//	  return super.onCreateOptionsMenu(menu);  
//	  }  
//	 @Override  
//	 public boolean onOptionsItemSelected(MenuItem item) {  
//	   // TODO Auto-generated method stub   
//	   switch (item.getItemId()) {  
//	   		case 1:  
//	    	  shot(); 
//	    	  Toast.makeText(getApplicationContext(), "截屏并分享至QQ,APP已经提交给微信官网审核，待官网开通给出AppKey方可使用", Toast.LENGTH_SHORT).show();
//	    	  break; 
//	   		case 0:  
//	   			if(true)
//	   			{
//	   				IWXAPI wxApi= WXAPIFactory.createWXAPI(this, "wx9325c10dff3a1caa");
//	   				wxApi.registerApp("wx9325c10dff3a1caa");  
//	   				WXWebpageObject webpage = new WXWebpageObject();  
//		   		    webpage.webpageUrl = "这里填写链接url";  
//		   		    WXMediaMessage msg = new WXMediaMessage(webpage);  
//		   		    msg.title = "这里填写标题";  
//		   		    msg.description = "这里填写内容";  
//		   		    //这里替换一张自己工程里的图片资源  
//		   		    Bitmap thumb =  shot();  
//		   		    msg.setThumbImage(thumb);  
//		   		      
//		   		    SendMessageToWX.Req req = new SendMessageToWX.Req();  
//		   		    req.transaction = String.valueOf(System.currentTimeMillis());  
//		   		    req.message = msg;  
//		   		    req.scene = 0==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
//		   		    wxApi.sendReq(req);  
//	   			}
//	   			break;
//	    	  
//	     } 
//	   return super.onOptionsItemSelected(item);  
//	} 
	 
	 private String buildTransaction(final String type) {
			return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
		}
	private Bitmap shot() {  
        View view = getWindow().getDecorView();  
        Display display = this.getWindowManager().getDefaultDisplay();  
        view.layout(0, 0, display.getWidth(), display.getHeight());  
        view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap   
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());  
        return bmp;  
    } 
	protected ProgressDialog progressDialog = null;
	protected AlertDialog.Builder confirmDialog = null;
	protected MyApplication app = null;
	protected Toast toast = null;
	protected Handler switchActivityHandler;
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
//		ImageButton btnBack = ((ImageButton) this.findViewById(R.id.btnBack));
//		if(btnBack!=null)
//		{
//			btnBack.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					BaseActivity.this.finish();
//				}
//			});
//		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progressDialog = new ProgressDialog(this);
		// progressDialog.setTitle("Indeterminate");
		// progressDialog.setMessage("正在登陆...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 旋转
		// progressDialog.setIndeterminate(true);// 设置明确刻度
		progressDialog.setCancelable(false);

		confirmDialog = new AlertDialog.Builder(this);
		confirmDialog.setCancelable(false);

		toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);

		app = (MyApplication) getApplication();

		switchActivityHandler = new Handler();
		
		
	}

	/**
	 * 选择提示框
	 */
	protected void showConfirmDialog(String title, String message,
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

	/**
	 * 子线程Toast 注意：会终止当前子线程<不推荐>
	 */
	// protected void showSubThreadToast(Context context, String msg) {
	// Looper.prepare();
	// Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	// Looper.loop();// 进入loop中的循环，查看消息队列
	// }

	protected void showCenterToast(String msg) {
		toast.setText(msg);
		toast.show();
	}

	/**
	 * 切换界面+效果
	 */
	protected void startSwitchActivity(Activity from, Class<?> to,
			Map<String, String> param, boolean isFinish, int time) {
		switchActivityHandler.postDelayed(new SwitchActivityThread(from, to,
				param, isFinish, false, 0), time);
	}

	/**
	 * 切换界面+效果
	 */
	protected void startSwitchActivityForResult(Activity from, Class<?> to,
			Map<String, String> param, boolean isFinish, int requestCode,
			int time) {
		switchActivityHandler.postDelayed(new SwitchActivityThread(from, to,
				param, isFinish, true, requestCode), time);
	}

	private class SwitchActivityThread implements Runnable {
		Activity from;
		Class<?> to;
		boolean isFinish;
		boolean isResult;
		int requestCode;
		Map<String, String> param;

		public SwitchActivityThread(Activity from, Class<?> to,
				Map<String, String> param, boolean isFinish, boolean isResult,
				int requestCode) {
			this.from = from;
			this.to = to;
			this.param = param;
			this.isFinish = isFinish;
			this.isResult = isResult;
			this.requestCode = requestCode;
		}

		public void run() {
			Intent intent = new Intent(from, to);
			if (param != null) {
				for (String k : param.keySet()) {
					intent.putExtra(k, param.get(k));
				}
			}

			if (isResult) {
				from.startActivityForResult(intent, requestCode);
			} else {
				from.startActivity(intent);
			}

			// 实现淡入浅出的效果
			from.overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			// // 由左向右滑入的效果
			// from.overridePendingTransition(android.R.anim.slide_in_left,
			// android.R.anim.slide_out_right);
			// // 实现zoommin 和 zoomout,即类似iphone的进入和退出时的效果
			// from.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

			if (isFinish) {
				from.finish();
			}
		}
	}
}
