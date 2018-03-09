package com.echo.mypf.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.mypf.R;
import com.echo.mypf.adapter.MyGridViewAdapter;
import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.utils.FileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GridViewActivity extends CustomActivity implements OnItemClickListener,OnItemSelectedListener,OnKeyListener{

	private String tag = "GridViewActivity";
	private SharedPreferences sharedPreferences;
	private boolean photo_info;
	private Context mContext;
	private String pathType;
	private MyGridViewAdapter myAdapter;
	private GridView gv;
	private TextView tvImageInfo;
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private Handler handler = new MyHandler();
	private ExifInterface exif;
	private int focusId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
	}
	
	@Override
	protected void onStart() {
		if (FileUtils.getImageFilePath(mContext, pathType).isEmpty()) {
			Toast.makeText(mContext, "No images found", Toast.LENGTH_SHORT).show();
		}
//		ImageLoader.getInstance().clearDiskCache();
//		ImageLoader.getInstance().clearMemoryCache();
		super.onStart();
	}

	@Override
	protected void onRestart() {
		refreshGridview();
		super.onRestart();
	}

	private void refreshGridview() {
		initView();
		initListener();
		initData();
	}

	@Override
	protected void onResume() {
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	private void initData() {
		sharedPreferences = getSharedPreferences(Echo.SHARED_PREFERENCES_NAME, 0);
		photo_info = sharedPreferences.getBoolean(Echo.PHOTO_INFO_SP, false);
		mContext = this.getApplicationContext();
		pathType = this.getIntent().getStringExtra(Echo.PATH_TYPE);
		myAdapter = new MyGridViewAdapter(mContext, pathType);
		gv.setAdapter(myAdapter);
		filter = new IntentFilter();
		filter.addAction(Echo.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Echo.ACTION_MEDIA_SCANNER_FINISHED);
		filter.addDataScheme("file");
		receiver = new MyBroadcastReceiver();
	}

	private void initListener() {
		gv.setOnItemClickListener(this);
		gv.setOnItemSelectedListener(this);
		gv.setOnKeyListener(this);
	}

	private void initView() {
		setContentView(R.layout.activity_gridview);
		gv = (GridView) findViewById(R.id.gv);
		tvImageInfo = (TextView) findViewById(R.id.tv_imageinfo);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra(Echo.PATH_TYPE, pathType);
		intent.putExtra(Echo.POSITION, position);
		intent.setClass(mContext, PhotoActivity.class);
		startActivity(intent);
	}

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (focusId>0) {
					gv.setSelection(focusId-1);
				}else{
					gv.setSelection(myAdapter.getCount()-1);
				}
				break;
			case 1:
				if (focusId<myAdapter.getCount()-1) {
					gv.setSelection(focusId+1);
				}else{
					gv.setSelection(0);
				}
				break;
			case 2:
				Bundle b = msg.getData();
				String name = b.getString("name");
				String date = b.getString("date");
				String length = b.getString("length");
				String width = b.getString("width");
				String orientation = b.getString("orientation");
				tvImageInfo.setText(name+"      "+date+"        "+length+"*"+width);
				break;

			default:
				break;
			}
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(tag, "receiver:"+intent.toString());
			myAdapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(GridViewActivity.this, PhotoActivity.class);
		String mPathType = sharedPreferences.getString(Echo.PATH_TYPE_SP, Echo.PATH_FLASH);
		int mPosition = sharedPreferences.getInt(Echo.POSITION_SP, 0);
		intent.putExtra(Echo.PATH_TYPE, mPathType);
		intent.putExtra(Echo.POSITION, mPosition);
		switch (event.getKeyCode()) {
		case Echo.KEY_SLIDESHOW:
			startActivity(intent);
			break;
		case Echo.KEY_MENU:
			intent.putExtra("SOC", 1);
			startActivity(intent);
			break;
		case Echo.KEY_CALL_TIME:
			intent.putExtra("SOC", 3);
			startActivity(intent);
			break;
		case Echo.KEY_SELECT:
			Intent intentSelect = new Intent(GridViewActivity.this, GridViewEditActivity.class);
			intentSelect.putExtra(Echo.PATH_TYPE, pathType);
			startActivity(intentSelect);
			break;
		case Echo.KEY_EDIT:
			Intent intentEdit = new Intent(GridViewActivity.this, GridViewEditActivity.class);
			intentEdit.putExtra(Echo.PATH_TYPE, pathType);
			startActivity(intentEdit);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		focusId = position;
		if (photo_info) {
			initImageInfo(position);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void initImageInfo(int position) {
		String imgPath = myAdapter.getItem(position);
		try {
			exif = new ExifInterface(imgPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String name = imgPath.substring(imgPath.lastIndexOf("/")+1);
		String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
		String length,width,orientation;
		
		if (date == null) {//picture
			File file = new File(imgPath);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.format(file.lastModified());
			length = ""+BitmapFactory.decodeFile(imgPath).getWidth();
			width = ""+BitmapFactory.decodeFile(imgPath).getHeight();
//			orientation = "land";
		}else{//photo
			length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
			width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
			orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
		}
		Bundle b = new Bundle();
		b.putString("name", name);
		b.putString("date", date);
		b.putString("length", length);
		b.putString("width", width);
//		b.putString("orientation", orientation);
		Message msg = new Message();
		msg.setData(b);
		msg.what = 2;
		handler.sendMessage(msg);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == Echo.KEY_LEFT) {
				if (focusId%4==0) {
					handler.sendEmptyMessage(0);
					Log.e("FocuseId", ""+focusId);
				}
			}
			if (keyCode == Echo.KEY_RIGHT) {
				if (focusId==myAdapter.getCount()-1||focusId%4==3) {
					handler.sendEmptyMessage(1);
					Log.e("FocuseId", ""+focusId);
				}
			}
		}
		return false;
	}
}
