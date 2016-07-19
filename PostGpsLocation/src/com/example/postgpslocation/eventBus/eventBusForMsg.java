package com.example.postgpslocation.eventBus;

import java.util.Map;

public class eventBusForMsg {
	private String mMsg;
	
	private  Map<String,Object> resultmap;
	
	

	public eventBusForMsg(String mMsg, Map<String, Object> resultmap) {
		super();
		this.mMsg = mMsg;
		this.resultmap = resultmap;
	}
	
	

	public eventBusForMsg(String msg) {
		// TODO Auto-generated constructor stub
		mMsg = msg;
	}


	public String getmMsg() {
		return mMsg;
	}



	public Map<String, Object> getResultmap() {
		return resultmap;
	}
	
	
	
}
