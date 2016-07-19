package com.example.postgpslocation.net;

import java.util.Map;

public abstract class NetThread extends Thread {
	public Map<String,Object> map;
	
	public NetThread(Map<String,Object> map) {
		this.map=map;
	}
	
	@Override
	public abstract void run();
}
