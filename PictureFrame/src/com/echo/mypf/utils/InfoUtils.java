package com.echo.mypf.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;

@SuppressLint("NewApi")
public class InfoUtils {
	public static String getStorageTotalSize(Context context, String path) { 
        StatFs stat = new StatFs(path);  
        long blockSize = stat.getBlockSizeLong();  
        long totalBlocks = stat.getBlockCountLong();  
        return Formatter.formatFileSize(context, blockSize * totalBlocks);  
    }  
  
    public static String getStorageAvailableSize(Context context, String path) {  
        StatFs stat = new StatFs(path);  
        long blockSize = stat.getBlockSizeLong();  
        long availableBlocks = stat.getAvailableBlocksLong();  
        return Formatter.formatFileSize(context, blockSize * availableBlocks);  
    }  
}
