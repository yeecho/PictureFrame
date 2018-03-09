package com.echo.mypf.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.echo.mypf.R;
import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.utils.FileUtils;
//弃用
public class PhotoRotateActivity extends CustomActivity {
	
	private String tag = "PhotoRotaeActivity";
	private String pathType;
	private int position;
	private ImageView iv;
	private String imgPath;
	private Bitmap bitmap;
	private float scale = 90;
	private Matrix matrix;
	private MyRotateHandler mHandler = new MyRotateHandler();

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initImage();
	}

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_photo_rotate);
		iv = (ImageView) findViewById(R.id.photo_rotate_iv);
	}

	private void initData() {
		pathType = getIntent().getStringExtra(Echo.PATH_TYPE);
		position = getIntent().getIntExtra(Echo.POSITION, 0);
		imgPath = FileUtils.getImageFilePath(this, pathType).get(position);
		bitmap = BitmapFactory.decodeFile(imgPath);
		matrix = new Matrix();
		matrix.postRotate(scale);
	}

	private void initImage() {
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		iv.setImageBitmap(bitmap);
	}

	public Bitmap rotate(Bitmap bitmap, float scale) {
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getKeyCode() == Echo.KEY_ROTATE) {
			mHandler.sendEmptyMessage(0);
		} else if (event.getKeyCode() == Echo.KEY_BACK) {
			Intent intent = new Intent(PhotoRotateActivity.this,
					PhotoActivity.class);
			intent.putExtra(Echo.PATH_TYPE, pathType);
			intent.putExtra(Echo.POSITION, position);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("HandlerLeak")
	private class MyRotateHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			initImage();
			super.handleMessage(msg);
		}
		
	}
}
