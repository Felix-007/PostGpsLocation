package com.example.postgpslocation.util;

import java.io.File;
import java.io.IOException;

import com.example.postgpslocation.MyApplication;


import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

public class AppUtil {

	/**
	 * 获取软件版本号
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取软件版本名
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取联网状态
	 */
	public static boolean isNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获得文件对象
	 */
	public static File getFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	/**
	 * 获取存储空间 剩,总,使用百分比
	 */
	public static String getStorageSpace() {
		String result = "0,0,0";

		// 取得SDCard当前的状态
		String sDcString = android.os.Environment.getExternalStorageState();
		if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			// // 取得sdcard文件路径
			// File pathFile = android.os.Environment
			// .getExternalStorageDirectory();
			// android.os.StatFs statfs = new
			// android.os.StatFs(pathFile.getPath());
			// // 获取SDCard上BLOCK总数
			// long nTotalBlocks = statfs.getBlockCount();
			// // 获取SDCard上每个block的SIZE
			// long nBlocSize = statfs.getBlockSize();
			// // 获取可供程序使用的Block的数量
			// long nAvailaBlock = statfs.getAvailableBlocks();
			// // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
			// // long nFreeBlock = statfs.getFreeBlocks();
			// // 计算SDCard 总容量大小MB
			// long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
			// // 计算 SDCard 剩余大小MB
			// long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
			// int nPercent = (int) (nSDFreeSize / (float) nSDTotalSize * 100);
			// result = nSDFreeSize + "," + nSDTotalSize + "," + nPercent;

			long SysAvailableSize = MemorySpaceCheck.getSDAvailableSize() / 1024 / 1024;
			long SysTotalSize = MemorySpaceCheck.getSDTotalSize() / 1024 / 1024;
			int nPercent = (int) ((SysTotalSize - SysAvailableSize)
					/ (float) SysTotalSize * 100);
			result = SysAvailableSize + "," + SysTotalSize + "," + nPercent;
		} else {
			long SysAvailableSize = MemorySpaceCheck.getSysAvailableSize() / 1024 / 1024;
			long SysTotalSize = MemorySpaceCheck.getSysTotalSize() / 1024 / 1024;
			int nPercent = (int) ((SysTotalSize - SysAvailableSize)
					/ (float) SysTotalSize * 100);
			result = SysAvailableSize + "," + SysTotalSize + "," + nPercent;
		}
		return result;
	}

	/**
	 * 存储空间管理
	 */
	static class MemorySpaceCheck {

		/**
		 * 计算剩余空间
		 */
		private static long getAvailableSize(String path) {
			StatFs fileStats = new StatFs(path);
			fileStats.restat(path);
			return (long) fileStats.getAvailableBlocks()
					* fileStats.getBlockSize(); // 注意与fileStats.getFreeBlocks()的区别
		}

		/**
		 * 计算总空间
		 */
		private static long getTotalSize(String path) {
			StatFs fileStats = new StatFs(path);
			fileStats.restat(path);
			return (long) fileStats.getBlockCount() * fileStats.getBlockSize();
		}

		/**
		 * 计算SD卡的剩余空间
		 */
		public static long getSDAvailableSize() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				return getAvailableSize(Environment
						.getExternalStorageDirectory().toString());
			}

			return 0;
		}

		/**
		 * 计算系统的剩余空间
		 */
		public static long getSysAvailableSize() {
			// context.getFilesDir().getAbsolutePath();
			return getAvailableSize("/data");
		}

		/**
		 * 获取SD卡的总空间
		 */
		public static long getSDTotalSize() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				return getTotalSize(Environment.getExternalStorageDirectory()
						.toString());
			}

			return 0;
		}

		/**
		 * 获取系统可读写的总空间
		 */
		public static long getSysTotalSize() {
			return getTotalSize("/data");
		}

		/**
		 * 是否有足够的空间
		 * 
		 * @param filePath
		 *            文件路径，不是目录的路径
		 */
		public static boolean hasEnoughMemory(String filePath) {
			File file = new File(filePath);
			long length = file.length();
			if (filePath.startsWith("/sdcard")
					|| filePath.startsWith("/mnt/sdcard")) {
				return getSDAvailableSize() > length;
			} else {
				return getSysAvailableSize() > length;
			}

		}
	}

	/**
	 * 输出私有文件
	 */
	public static boolean outputPrivateFile(File oFile) {
		if (!oFile.exists()) {
			System.out.println("源文件不存在");
			return false;
		}
		try {
			String oFilePath = oFile.getAbsolutePath();
			oFilePath = oFilePath.substring(oFilePath.lastIndexOf("/") + 1);
			File destDir = new File(MyApplication.getInstance().MainDirPath
					+ "/test/");
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File destFile = new File(destDir.getAbsoluteFile() + "/"
					+ oFilePath);
			if (!destFile.exists()) {
				destFile.createNewFile();
			}
			PublicUtil.copyFile(oFile.getAbsolutePath(),
					destFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
