package com.example.postgpslocation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class CustomKeyPointInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public CustomKeyPointInfo(double latitude, double longitude, String name) {
		super();
		this.wd = latitude;
		this.jd = longitude;
		this.zdmc = name;
	}
	public CustomKeyPointInfo(double latitude, double longitude, String name,String xh) {
		super();
		this.wd = latitude;
		this.jd = longitude;
		this.zdmc = name;
		this.xh = xh;
	}
	
	public CustomKeyPointInfo(double latitude, double longitude) {
		super();
		this.wd = latitude;
		this.jd = longitude;
	}



	public static List<CustomKeyPointInfo> infos = new ArrayList<CustomKeyPointInfo>();  
	  
	    static  
	    {  
	        infos.add(new CustomKeyPointInfo(30.23448, 112.916875,"中国邮政(长沙市邮政局)"));  
	        infos.add(new CustomKeyPointInfo(30.229977, 112.91665, "长沙市邮政管理局"));  
//	        infos.add(new CustomKeyPointInfo(30.23448, 112.916875,"关键点3"));  
//	        infos.add(new CustomKeyPointInfo(30.229977, 112.91665, "关键点4"));  
//	        infos.add(new CustomKeyPointInfo(30.23448, 112.916875,"关键点5"));  
//	        infos.add(new CustomKeyPointInfo(30.229977, 112.91665, "关键点6"));  
	    } 
	
	/** 
     * 纬度
     */  
    private double wd;  
    /** 
     * 经度
     */  
    private double jd;  
    /** 
     * 商家名称 
     */  
    private String zdmc;
    /**
     * 顺序
     */
    private String xh;
	public double getWd() {
		return wd;
	}
	public void setWd(double wd) {
		this.wd = wd;
	}
	public double getJd() {
		return jd;
	}
	public void setJd(double jd) {
		this.jd = jd;
	}
	public String getZdmc() {
		return zdmc;
	}
	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}
	public String getXh() {
		return xh;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
    
    
	
    
    
}
