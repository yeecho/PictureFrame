package com.echo.mypf.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

public class ImageUtils {
	private static String tag = "ImageUtils_Log";

	public static Bitmap resizeBitmap(String imgPath, int sWidth, int sHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap mBitmap = BitmapFactory.decodeFile(imgPath, options);
		int bmpWidth = mBitmap.getWidth();
		int bmpHeight = mBitmap.getHeight();
		float scaleWidth = (float) sWidth / bmpWidth;
		float scaleHeight = (float) sHeight / bmpHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, false);
		mBitmap.recycle();
		return resizeBitmap;
	}

	public static Bitmap fitScreen(Bitmap bitmap) {
		if (bitmap==null) {
			return null;
		}
		int mWidth = 1280;
		int mHeight = 800;
		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
//		Log.e(tag, "picture w:"+bWidth);
//		Log.e(tag, "picture H:"+bHeight);
		float scaleW = (float) mWidth/bWidth;
		float scaleH = (float) mHeight/bHeight;
//		Log.e(tag, "scaleW:"+scaleW);
//		Log.e(tag, "scaleH:"+scaleH);
		Matrix matrix = new Matrix();
		if(scaleW < scaleH){
			matrix.postScale(scaleW, scaleW);
		}else{
			matrix.postScale(scaleH, scaleH);
		}
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bWidth,
				bHeight, matrix, true);
//		Log.e(tag, "bmp w:"+resizeBitmap.getWidth());
//		Log.e(tag, "bmp H:"+resizeBitmap.getHeight());
		return resizeBitmap;
	}
	
	public static Bitmap fillScreen(Bitmap bitmap) {
		if (bitmap==null) {
			return null;
		}
		int mWidth = 1280;
		int mHeight = 800;
		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
		Log.e(tag, "picture w:"+bWidth);
		Log.e(tag, "picture H:"+bHeight);
		float scaleW = (float) mWidth/bWidth;
		float scaleH = (float) mHeight/bHeight;
		Log.e(tag, "scaleW:"+scaleW);
		Log.e(tag, "scaleH:"+scaleH);
		Matrix matrix = new Matrix();
		if(scaleW < scaleH){
			matrix.postScale(scaleH, scaleH);
		}else{
			matrix.postScale(scaleW, scaleW);
		}
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bWidth,
				bHeight, matrix, true);
		Log.e(tag, "bmp w:"+resizeBitmap.getWidth());
		Log.e(tag, "bmp H:"+resizeBitmap.getHeight());
		return resizeBitmap;
	}

	public static Bitmap getGalleryMain(Bitmap bitmap) {
		if (bitmap==null) {
			return null;
		}
		int mWidth = 950;
		int mHeight = 700;
		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
		Log.e(tag, "picture w:"+bWidth);
		Log.e(tag, "picture H:"+bHeight);
		float scaleW = (float) mWidth/bWidth;
		float scaleH = (float) mHeight/bHeight;
		Log.e(tag, "scaleW:"+scaleW);
		Log.e(tag, "scaleH:"+scaleH);
		Matrix matrix = new Matrix();
		if(scaleW < scaleH){
			matrix.postScale(scaleW, scaleW);
		}else{
			matrix.postScale(scaleH, scaleH);
		}
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bWidth,
				bHeight, matrix, true);
		Log.e(tag, "bmp w:"+resizeBitmap.getWidth());
		Log.e(tag, "bmp H:"+resizeBitmap.getHeight());
		return resizeBitmap;
	}

	public static Bitmap getGalleryRest(Bitmap bitmap) {
		// TODO Auto-generated method stub
		if (bitmap==null) {
			return null;
		}
		int mWidth = 300;
		int mHeight = 210;
		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
		Log.e(tag, "picture w:"+bWidth);
		Log.e(tag, "picture H:"+bHeight);
		float scaleW = (float) mWidth/bWidth;
		float scaleH = (float) mHeight/bHeight;
		Log.e(tag, "scaleW:"+scaleW);
		Log.e(tag, "scaleH:"+scaleH);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleW, scaleH);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bWidth,
				bHeight, matrix, true);
		Log.e(tag, "bmp w:"+resizeBitmap.getWidth());
		Log.e(tag, "bmp H:"+resizeBitmap.getHeight());
		return resizeBitmap;
	}

	

}
