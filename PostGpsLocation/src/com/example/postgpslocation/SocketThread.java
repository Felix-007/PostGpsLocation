package com.example.postgpslocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SocketThread extends Thread{
	public Handler mHandler;    
	public Socket socket;
	private BufferedReader in = null;
    private PrintWriter out = null;
    public int content ;
    private boolean threadFlag=true;
    
    public void stopThread(){
    	threadFlag = false;
	}

	public SocketThread(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		Looper.prepare();   
		 try {
//			 socket = new Socket("10.10.5.98", 7005);
			 socket = new Socket("192.168.191.1", 1989);
			 in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
			 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
	            while (true) {
	                if (!socket.isClosed()) {
	                	Log.i("socket", "[if] socket连接isClosed");
	                    if (socket.isConnected()) {
	                    	Log.i("socket", "[if] socket连接isConnected");
	                        if (!socket.isInputShutdown()) {
	                        	Log.i("socket", "[if] socket连接isInputShutdown");
	                            if ((content=in.read())+""!= "") {
	                            	String abc=in.readLine();
	                            	System.out.println(abc);
	                            	Log.i("socket", "[if] socket连接：获得数据 上传成功");
	                            	Log.i("socket", "content"+content);
	                                Message msg=new Message();
	                                msg.obj=true;
	                                msg.what=777;
	                                mHandler.sendMessage(msg);
	                            } else {
	                            	Log.i("socket", "[else] socket连接：获得数据 上传失败");
	                            	Message msg=new Message();
	                            	msg.what=777;
	                            	msg.obj=false;
	                                mHandler.sendMessage(msg);
	                            }
	                        }else{
	                        	Log.i("socket", "[else] socket连接isInputShutdown");
	                        	Message msg=new Message();
                            	msg.what=777;
                            	msg.obj=false;
                                mHandler.sendMessage(msg);
	                        }
	                    }else{
	                    	Log.i("socket", "[else] socket连接NOT isConnected");
	                    	Message msg=new Message();
                        	msg.what=777;
                        	msg.obj=false;
                            mHandler.sendMessage(msg);
	                    }
	                }else{
	                	Log.i("socket", "[else] socket连接isClosed");
	                	Message msg=new Message();
                    	msg.what=777;
                    	msg.obj=false;
                        mHandler.sendMessage(msg);
                       
	                }
	            }
	        } catch (Exception e) {
	        	Log.i("socket", "[else] Exception线程里面异常");
	        	Message msg=new Message();
	        	 msg.what=777;
	        	 msg.obj=false;
                 mHandler.sendMessage(msg);
	            e.printStackTrace();
	          
	        }
		
//		 Looper.loop();    
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public PrintWriter getOut() {
		return out;
	}
	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}

	public Handler getmHandler() {
		return mHandler;
	}
	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	

}
