package com.example.postgpslocation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class PublicUtil {

	/* 界面处理 */
	/**
	 * 根据文字重新设置TextView的宽度
	 */
	public static void resetTextViewWidth(TextView view, String content) {
		LayoutParams lp = view.getLayoutParams();
		lp.width = ((int) view.getPaint().measureText(content));
		view.setLayoutParams(lp);
	}

	/* 时间处理 */
	private static SimpleDateFormat sdf_yyyymmddhhmiss = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.CHINA);
	private static SimpleDateFormat sdf_yyyymmddhhmi = new SimpleDateFormat(
			"yyyyMMddHHmm", Locale.CHINA);
	private static SimpleDateFormat sdf_yyyymm = new SimpleDateFormat(
			"yyyyMMdd", Locale.CHINA);
	private static SimpleDateFormat sdf_yyyy_mm_dd_hh_mi_ss = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private static SimpleDateFormat sdf_yyyy_mm_dd_hh_mi = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINA);
	private static SimpleDateFormat sdf_yyyy_mm_dd_hh_mi_ss2 = new SimpleDateFormat(
			"yyyy_MM_dd HH_mm_ss", Locale.CHINA);
	private static SimpleDateFormat sdf_hhmi = new SimpleDateFormat("HHmm",
			Locale.CHINA);
	private static SimpleDateFormat sdf_dd_hh_mi = new SimpleDateFormat("dd日HH点mm分",
			Locale.CHINA);
	private static SimpleDateFormat sdf_hh_mi = new SimpleDateFormat("HH点mm分",
			Locale.CHINA);
	public static String getDateForYYYYMMDD(Date date)
	{
		return sdf_yyyymm.format(date);
	}
	/**
	 * 返回yyyymmddhhmiss
	 */
	public static String getDateForYYYYMMDDHHMISS(Date date) {
		return sdf_yyyymmddhhmiss.format(date);
	}

	/**
	 * 返回yyyy-mm-dd hh:mi:ss
	 */
	public static String GetDateForYYYY_MM_DD_HH_MI_SS(Date date) {
		return sdf_yyyy_mm_dd_hh_mi_ss.format(date);
	}
	
	/**
	 * 返回yyyy-mm-dd hh:mi
	 */
	public static String GetDateForYYYY_MM_DD_HH_MI(Date date) {
		return sdf_yyyy_mm_dd_hh_mi.format(date);
	}

	/**
	 * 返回yyyy_mm_dd hh_mi_ss
	 * 
	 * @param date
	 * @return
	 */
	public static String GetDateForYYYY_MM_DD_HH_MI_SS2(Date date) {
		return sdf_yyyy_mm_dd_hh_mi_ss2.format(date);
	}

	/**
	 * 从yyyymmddhhmiss转为yyyy-mm-dd hh:mi:ss
	 */
	public static String GetDateForYYYY_MM_DD_HH_MI_SSFromYYYYMMDDHHMISSStr(
			String dateStr) {
		try {
			if (dateStr.length() == 14) {
				return sdf_yyyy_mm_dd_hh_mi_ss.format(sdf_yyyymmddhhmiss
						.parse(dateStr));
			} else {
				return dateStr;
			}
		} catch (Exception e) {
			return dateStr;
		}
	}

	/**
	 * 从yyyymmddhhmiss转为hh:mi
	 */
	public static String GetDateForHHMMFromYYYYMMDDHHMISSStr(String dateStr) {
		try {
			if (dateStr.length() == 14) {
				return sdf_hhmi.format(sdf_yyyymmddhhmiss.parse(dateStr));
			} else {
				return dateStr;
			}
		} catch (Exception e) {
			return dateStr;
		}
	}
	
	/**
	 * 从yyyymmddhhmiss转为hh点mi分
	 */
	public static String GetDateForHH_MMFromYYYYMMDDHHMISSStr(String dateStr) {
		try {
			if (dateStr.length() == 14) {
				return sdf_hh_mi.format(sdf_yyyymmddhhmiss.parse(dateStr));
			} else {
				return dateStr;
			}
		} catch (Exception e) {
			return dateStr;
		}
	}
	
	/**
	 * 从yyyymmddhhmiss转为dd日hh点mi分
	 */
	public static String GetDateForDD_HH_MMFromYYYYMMDDHHMISSStr(String dateStr) {
		try {
			if (dateStr.length() == 14) {
				return sdf_dd_hh_mi.format(sdf_yyyymmddhhmiss.parse(dateStr));
			} else if(dateStr.length() == 12){
				return sdf_dd_hh_mi.format(sdf_yyyymmddhhmi.parse(dateStr));
			} else {
				return dateStr;
			}
		} catch (Exception e) {
			return dateStr;
		}
	}

	/* 字符串处理 */

	/**
	 * 获得数据集里的指定属性，不存在或为空则返回空字符串
	 */
	public static String GetValidDataStr(JSONObject o, String fieldName,
			String defaultValue) {
		String value = defaultValue;
		try {
			if (o.has(fieldName)) {
				value = o.getString(fieldName);
				if (value == null || "null".equals(value) || "".equals(value)) {
					value = defaultValue;
				} else {
					value = value.trim();
				}
			}
		} catch (JSONException e) {
			value = defaultValue;
		}
		return value;
	}

	/**
	 * 数据用逗号合并
	 */
	public static String GetJoinArray(Object[] arr) {
		StringBuffer sb = new StringBuffer();
		String s = ",";
		for (Object o : arr) {
			sb.append(String.valueOf(o));
			sb.append(s);
		}
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 集合用逗号合并
	 */
	public static String GetJoinList(List<String> list) {
		StringBuffer sb = new StringBuffer();
		String ss = ",";
		for (String s : list) {
			sb.append(s);
			sb.append(ss);
		}
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 半角转换为全角
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 去除特殊字符或将所有中文标号替换为英文标号
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	// 多音字词库
	private static Map<String, List<String>> pinyinMap = new HashMap<String, List<String>>();

	/**
	 * 初始化多音字词库
	 */
	public static void InitPolyword(Context context) {
		BufferedReader br = null;
		try {
			// 读取多音字词库;
			br = new BufferedReader(new InputStreamReader(context.getAssets()
					.open("polyword/polyword.txt")));

			String s = null;

			while ((s = br.readLine()) != null) {

				if (s != null) {
					String[] arr = s.split("#");
					String pinyin = arr[0];
					String chinese = arr[1];

					if (chinese != null) {
						String[] strs = chinese.split(" ");
						List<String> list = Arrays.asList(strs);
						pinyinMap.put(pinyin, list);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	/**
//	 * 将汉字转换为全拼
//	 */
//	public static String GetPinYin(String str) {
//		char[] t1 = null;
//		t1 = str.toCharArray();
//		// System.out.println(t1.length);
//		String[] t2 = new String[t1.length];
//		// System.out.println(t2.length);
//		// 设置汉字拼音输出的格式
//		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
//		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
//		String t4 = "";
//		int t0 = t1.length;
//		try {
//			for (int i = 0; i < t0; i++) {
//				// 判断能否为汉字字符
//				// System.out.println(t1[i]);
//				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
//					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
//					if (t2.length > 1) {
//						t4 += GetPolyword(str, t2, i);
//					} else {
//						t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
//					}
//				} else {
//					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
//					t4 += Character.toString(t1[i]);
//				}
//			}
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
//			e.printStackTrace();
//		}
//		return t4;
//	}
//
//	/**
//	 * 提取每个汉字的首字母
//	 */
//	public static String GetPinYinHeadChar(String str) {
//		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
//		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
//		String convert = "";
//		try {
//			for (int j = 0; j < str.length(); j++) {
//				char word = str.charAt(j);
//				// 提取汉字的首字母
//				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(
//						word, t3);
//				if (pinyinArray != null) {
//					if (pinyinArray.length > 1) {
//						convert += GetPolyword(str, pinyinArray, j).charAt(0);
//					} else {
//						convert += pinyinArray[0].charAt(0);
//					}
//				} else {
//					convert += word;
//				}
//			}
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
//			e.printStackTrace();
//		}
//		return convert;
//	}
//
//	/**
//	 * 提取字符串首字符拼音首字母
//	 */
//	public static String GetFirstPinYinHeadChar(String str) {
//		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
//		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
//		String convert = "";
//		try {
//			if (str.length() != 0) {
//				char word = str.charAt(0);
//				// 提取汉字的首字母
//				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(
//						word, t3);
//				if (pinyinArray != null) {
//					if (pinyinArray.length > 1) {
//						convert += GetPolyword(str, pinyinArray, 0).charAt(0);
//					} else {
//						convert += pinyinArray[0].charAt(0);
//					}
//				} else {
//					convert += word;
//				}
//			}
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
//			e.printStackTrace();
//		}
//		return convert;
//	}

	/**
	 * 识别多音字词
	 */
	public static String GetPolyword(String str, String[] pinyinArray, int j) {
		for (String py : pinyinArray) {
			List<String> ploywordList = pinyinMap.get(py);
			String s;
			if (j + 3 <= str.length()) { // 后向匹配2个汉字 大西洋
				s = str.substring(j, j + 3);
				if (ploywordList != null && (ploywordList.contains(s))) {
					// System.out.println("last 2 > " + py);
					return py;
				}
			}

			if (j + 2 <= str.length()) { // 后向匹配 1个汉字 大西
				s = str.substring(j, j + 2);
				if (ploywordList != null && (ploywordList.contains(s))) {
					// System.out.println("last 1 > " + py);
					return py;
				}
			}

			if ((j - 2 >= 0) && (j + 1 <= str.length())) { // 前向匹配2个汉字 龙固大
				s = str.substring(j - 2, j + 1);
				if (ploywordList != null && (ploywordList.contains(s))) {
					// System.out.println("before 2 < " + py);
					return py;
				}
			}

			if ((j - 1 >= 0) && (j + 1 <= str.length())) { // 前向匹配1个汉字 固大
				s = str.substring(j - 1, j + 1);
				if (ploywordList != null && (ploywordList.contains(s))) {
					// System.out.println("before 1 < " + py);
					return py;
				}
			}

			if ((j - 1 >= 0) && (j + 2 <= str.length())) { // 前向1个，后向1个 固大西
				s = str.substring(j - 1, j + 2);
				if (ploywordList != null && (ploywordList.contains(s))) {
					// System.out.println("before last 1 <> " + py);
					return py;
				}
			}
		}
		return pinyinArray[0];
	}

	/**
	 * 将字符串转换成ASCII码
	 */
	public static String GetCnASCII(String str) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = str.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			// System.out.println(Integer.toHexString(bGBK[i] & 0xff));
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}

	/* 数字处理 */
	private static DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * 是否数字
	 */
	public static boolean IsNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 字节转K/M/G
	 */
	public static String Bit2Amount(int bit) {
		int k = (bit / 1024);
		if (k != 0) {
			int m = bit / 1024 / 1024;
			if (m != 0) {
				int g = bit / 1024 / 1024 / 1024;
				if (g != 0) {
					return df.format(((double) bit) / 1024 / 1024 / 1024) + "G";
				} else {
					return df.format(((double) bit) / 1024 / 1024) + "M";
				}
			} else {
				return df.format(((double) bit) / 1024) + "K";
			}
		} else {
			return String.valueOf(bit);
		}
	}

	/**
	 * 复制文件
	 */
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		int byteread = 0;
		InputStream inStream = new FileInputStream(oldPath);
		FileOutputStream fs = new FileOutputStream(newPath);
		byte[] buffer = new byte[1024 * 5];
		while ((byteread = inStream.read(buffer)) != -1) {
			fs.write(buffer, 0, byteread);
		}
		fs.flush();
		fs.close();
		inStream.close();
	}

	/**
	 * 复制文件夹
	 */
	public static void copyFolder(String oldPath, String newPath)
			throws IOException {
		(new File(newPath)).mkdirs();
		File a = new File(oldPath);
		String[] file = a.list();
		File temp = null;
		for (int i = 0; i < file.length; i++) {
			if (oldPath.endsWith(File.separator)) {
				temp = new File(oldPath + file[i]);
			} else {
				temp = new File(oldPath + File.separator + file[i]);
			}

			if (temp.isFile()) {
				FileInputStream input = new FileInputStream(temp);
				FileOutputStream output = new FileOutputStream(newPath + "/"
						+ (temp.getName()).toString());
				byte[] b = new byte[1024 * 5];
				int len = 0;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
			}
			if (temp.isDirectory()) {// 如果是子文件夹
				copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
			}
		}
	}
}
