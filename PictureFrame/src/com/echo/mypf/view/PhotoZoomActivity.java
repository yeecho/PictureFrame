package com.echo.mypf.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.echo.mypf.R;
import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.utils.FileUtils;
import com.echo.mypf.utils.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoZoomActivity extends CustomActivity {

	private String tag = "PhotoZoomActivity";
	private ImageView iv;
	private Context context;
	private String pathType;
	private int position;
	private int type;
	private Bitmap bitmap;
	private float scale = (float) 1.25;
	private int zoomCount = 0;
	private String pathName;
	private Bitmap resizeBmp;
	private int saveDegree = 90;
	private MyZoomHandler mHandler = new MyZoomHandler();

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		if (type == 0) {
			mHandler.sendEmptyMessage(0);
			saveDegree = 0;
		}else{
			mHandler.sendEmptyMessage(1);
			saveDegree = 90;
		}
	}

	private void initData() {
		context = this;
		pathType = getIntent().getStringExtra(Echo.PATH_TYPE);
		position = getIntent().getIntExtra(Echo.POSITION, 0);
		type = getIntent().getIntExtra(Echo.ZOOM_ROTATE, 0);
		pathName = FileUtils.getImageFilePath(context, pathType).get(position);
		bitmap = ImageUtils.fitScreen(BitmapFactory.decodeFile(pathName));
		resizeBmp = bitmap;
	}

	private void initView() {
		setContentView(R.layout.activity_photo_zoom);
		iv = (ImageView) findViewById(R.id.zoom_iv);
	}

	private void zoom() {
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		// float scaleWidth = (float) 1.0;
		// float scaleHeight = (float) 1.0;
		// scaleWidth = (float)(scaleWidth*scale);
		// scaleHeight = (float)(scaleHeight*scale);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		if (zoomCount>=5) {
			bitmap = resizeBmp;
			zoomCount=0;
		}else{
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight,
					matrix, true);
		}
		iv.setImageBitmap(bitmap);
	}
	private void rotate() {
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		// float scaleWidth = (float) 1.0;
		// float scaleHeight = (float) 1.0;
		// scaleWidth = (float)(scaleWidth*scale);
		// scaleHeight = (float)(scaleHeight*scale);
		Matrix matrix = new Matrix();
//		matrix.postScale(scale, scale);
		matrix.postRotate(90);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		resizeBmp = Bitmap.createBitmap(resizeBmp, 0, 0, resizeBmp.getWidth(), resizeBmp.getHeight(),
				matrix, true);
		iv.setImageBitmap(bitmap);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == Echo.KEY_ZOOM ) {
			zoomCount++;
			mHandler.sendEmptyMessage(0);
		} else if (keyCode == Echo.KEY_ROTATE) {
			saveDegree += 90;
			mHandler.sendEmptyMessage(1);
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			bitmap.recycle();
			resizeBmp.recycle();
			Intent intent = new Intent(context, PhotoActivity.class);
			intent.putExtra(Echo.PATH_TYPE, pathType);
			intent.putExtra(Echo.POSITION, position);
			startActivity(intent);
			finish();
		} else if (keyCode == Echo.KEY_ENTER) {
			Log.e(tag, "degree:"+saveDegree);
			saveImage();
			ImageLoader.getInstance().clearDiskCache();
			ImageLoader.getInstance().clearMemoryCache();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void saveImage() {
		Bitmap savaBmp = BitmapFactory.decodeFile(pathName);
		int bmpWidth = savaBmp.getWidth();
		int bmpHeight = savaBmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postRotate(saveDegree);
		savaBmp = Bitmap.createBitmap(savaBmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		try {
			FileUtils.saveImage(savaBmp, pathName);
			Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
			Log.e(tag, "bmpwidth:"+bmpWidth+"bmpHeight:"+bmpHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	private class MyZoomHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what==0) {
				zoom();
			}else{
				rotate();
			}
			super.handleMessage(msg);
		}

	}
}
