package com.example.postgpslocation.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * google转成Baidu经纬度
 * @author jueyue
 *
 */
public class GoogleToBaidu {
	
	private static Map<String, String> googleToBaidu(String x,String y){
		String url = String
				.format("http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x=%s&y=%s", x,y);
		URL myURL = null;
		URLConnection httpsConn = null;
		try {
			myURL = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,String> map = new HashMap<String, String>();
		InputStream is =null;
		InputStreamReader insr = null;
		BufferedReader br = null;
		try {
			if(myURL!=null)
				httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				is = httpsConn.getInputStream();
				insr = new InputStreamReader(is, "UTF-8");
				br = new BufferedReader(insr);
				String data = null;
				while ((data = br.readLine()) != null) {
					Pattern p = Pattern.compile("[\\w[^\\[\\],{}]]+");
					Matcher m = p.matcher(data.replaceAll("'|\"", ""));
					String[] _strs = null;
					while (m.find()) {
						_strs = m.group().split(":");
						if (_strs.length == 2) {
							map.put(_strs[0], _strs[1].trim());
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(insr!=null)
					insr.close();
				if(is!=null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
//	public static Map<String, String> getLonAndLat(String x,String y){
//		Map<String,String> map = googleToBaidu(x,y);
//		System.out.println(map);
//		if(map!=null&&map.get("error").equalsIgnoreCase("0")){
//			map.put("x", Base64.decode(map.get("x")));
//			map.put("y", Base64.decode(map.get("y")));
//		}
//		return map;
//	}
//	
	private static  double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	public static Map<String,Double> Convert_GCJ02_To_BD09( double lat, double lng)
	{
	double x = lng, y = lat;
	double z =Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
	double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
	lng = z * Math.cos(theta) + 0.0065;
	lat = z * Math.sin(theta) + 0.006;
	Map<String,Double> map =new HashMap<String, Double>();
	map.put("lat",lng);
	map.put("lng",lat);
	return map;
	}
	public static Map<String,Double> Convert_BD09_To_GCJ02( double lat,  double lng)
	{
	double x = lng - 0.0065, y = lat - 0.006;
	double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
	double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
	lng = z * Math.cos(theta);
	lat = z * Math.sin(theta);
	Map<String,Double> map =new HashMap<String, Double>();
	map.put("lat",lng);
	map.put("lng",lat);
	return map;
	}
	
}
