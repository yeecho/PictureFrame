package com.echo.mypf.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.mypf.R;
import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.utils.FileUtils;

public class GridViewEditActivity extends CustomActivity implements
		OnItemClickListener, OnClickListener,OnItemSelectedListener {
	private String tag = "GridViewEditActivity_LOG";
	private SharedPreferences sp;
	private Editor editor;
	private boolean photo_info;
	private GridView gvEdit;
	private TextView tvEdit;
	private TextView tvImageInfo;
	private Context context;
	private String pathType;
	private ArrayList<String> imgPathList = new ArrayList<String>();
	private CheckableGVAdapter adapter;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> selectedMap = new HashMap<Integer, Boolean>();
	private MyHandler mHandler = new MyHandler();
	private boolean selectFlag;
	private AlertDialog dialog;
	private ExifInterface exif;
	private IntentFilter filter;
	private MyBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
	}

	@Override
	protected void onStart() {
		registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	private void initView() {
		setContentView(R.layout.activity_gridview_edit);
		gvEdit = (GridView) findViewById(R.id.gv_edit);
		tvEdit = (TextView) findViewById(R.id.tv_edit);
		tvImageInfo = (TextView) findViewById(R.id.tv_edit_imageinfo);
	}

	private void initListener() {
		gvEdit.setOnItemClickListener(this);
		gvEdit.setOnItemSelectedListener(this);
	}
	
	private void initData() {
		context = this;
		sp = getSharedPreferences(Echo.SHARED_PREFERENCES_NAME, 0);
		photo_info = sp.getBoolean(Echo.PHOTO_INFO_SP, false);
		editor = sp.edit();
		pathType = getIntent().getStringExtra(Echo.PATH_TYPE);
		imgPathList = FileUtils.getImageFilePath(context, pathType);
		adapter = new CheckableGVAdapter(context);
		gvEdit.setAdapter(adapter);
		gvEdit.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		
		filter = new IntentFilter();
		filter.addAction(Echo.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Echo.ACTION_MEDIA_SCANNER_FINISHED);
		filter.addDataScheme("file");
		receiver = new MyBroadcastReceiver();
	}

	class CheckableGVAdapter extends BaseAdapter {
		private Context mContext;

		public CheckableGVAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return imgPathList.size();
		}

		@Override
		public String getItem(int position) {
			return imgPathList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public void notifyDataSetChanged() {
			imgPathList = FileUtils.getImageFilePath(mContext, pathType);
			super.notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckableGVItem checkableItem;
			if (convertView == null) {
				checkableItem = new CheckableGVItem(mContext);
				checkableItem.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			} else {
				checkableItem = (CheckableGVItem) convertView;
			}
			checkableItem.setImage(imgPathList.get(position));
			checkableItem.setChecked(selectedMap.get(position) == null ? false
					: selectedMap.get(position));
			return checkableItem;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		boolean checked;
		if (selectedMap.get(position) == null) {
			checked = true;
		} else {
			checked = !selectedMap.get(position);
		}
		selectedMap.put(position, checked);
		mHandler.sendEmptyMessage(0);
	}

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tvEdit.setText(getResources().getString(
						R.string.tv_edit_selected_counts)
						+ " " + gvEdit.getCheckedItemCount());
				break;
			case 1:
//				initView();
//				initData();
				break;
			case 2:
				Bundle b = msg.getData();
				String name = b.getString("name");
				String date = b.getString("date");
				String length = b.getString("length");
				String width = b.getString("width");
				String make = b.getString("make");
				String model = b.getString("model");
				tvImageInfo.setText(name+"      "+date+"        "+length+"*"+width);
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case Echo.KEY_SELECT:
			selectFlag = !selectFlag;
			if (selectFlag) {
				selectAll();
			} else {
				selectNo();
			}
			break;
		case Echo.KEY_EDIT:
			showMenu();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showMenu() {
		Builder builder = new Builder(context);
		LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.dialog_edit_options, null);
		builder.setView(ll);
		dialog = builder.show();
		TextView tvCopy = (TextView) ll.findViewById(R.id.tv_edit_dialog_copy);
		TextView tvCut = (TextView) ll.findViewById(R.id.tv_edit_dialog_cut);
		TextView tvPaste = (TextView) ll
				.findViewById(R.id.tv_edit_dialog_paste);
		TextView tvDelete = (TextView) ll
				.findViewById(R.id.tv_edit_dialog_delete);
		tvCopy.setOnClickListener(this);
		tvCut.setOnClickListener(this);
		tvPaste.setOnClickListener(this);
		tvDelete.setOnClickListener(this);
	}

	private void selectAll() {
		for (int i = 0; i < gvEdit.getCount(); i++) {
			gvEdit.setItemChecked(i, true);
			selectedMap.put(i, true);
		}
		mHandler.sendEmptyMessage(0);
	}

	private void selectNo() {
		for (int i = 0; i < gvEdit.getCount(); i++) {
			gvEdit.setItemChecked(i, false);
		}
		selectedMap.clear();
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public void onClick(View v) {
		HashSet<String> selectedSet = new HashSet<String>();
		for (int i = 0; i < imgPathList.size(); i++) {
			Boolean b = (selectedMap.get(i) == null) ? false
					: (boolean) selectedMap.get(i);
			if (b) {
				selectedSet.add(imgPathList.get(i));
			}
		}
		switch (v.getId()) {
		case R.id.tv_edit_dialog_copy:
			Log.e(tag, "set:"+selectedSet.toString());
			editor.remove(Echo.PASTE_IMAGES);
			editor.putStringSet(Echo.PASTE_IMAGES, selectedSet);
			editor.putInt(Echo.PASTE_TYPE, 0);
			editor.commit();
			break;
		case R.id.tv_edit_dialog_cut:
			Log.e(tag, "set:"+selectedSet.toString());
			editor.remove(Echo.PASTE_IMAGES);
			editor.putStringSet(Echo.PASTE_IMAGES, selectedSet);
			editor.putInt(Echo.PASTE_TYPE, 1);
			editor.commit();
			break;
		case R.id.tv_edit_dialog_paste:
			int pasteType = sp.getInt(Echo.PASTE_TYPE, 0);
			ArrayList<String> images = new ArrayList<String>(sp.getStringSet(Echo.PASTE_IMAGES, null));
			if (images.size()==0) {
				Toast.makeText(context, "no images to paste!", Toast.LENGTH_SHORT).show();
			}else{
				if (pasteType == 0) {
					imagePaste(images);
				}else if(pasteType == 1){
					if(imagePaste(images)){
						imageDelete(images);
					}
					editor.remove(Echo.PASTE_IMAGES);
					editor.commit();
				}
//				refreshGridview();
			}
			
			break;
		case R.id.tv_edit_dialog_delete:
			imageDelete(new ArrayList<String>(selectedSet));
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
		dialog.dismiss();
//		selectedMap.clear();
//		selectedSet.clear();
	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	private boolean imagePaste(ArrayList<String> selectedPaths) {
		String basePath = Echo.FLASH_PASTE_PATH;
		if (pathType.equals(Echo.PATH_SD)) {
			basePath = Echo.SD_PASTE_PATH;
		}else if (pathType.equals(Echo.PATH_USB)) {
			basePath = Echo.USB_PASTE_PATH;
		}
		if (!new File(basePath).exists()) {
			new File(basePath).mkdir();
		}
		for (int i = 0; i < selectedPaths.size(); i++) {
			try {
				String newPath = basePath + getCurrentDate() + "_yuanye_00" + i + ".jpg";
				FileUtils.copyImage(selectedPaths.get(i), newPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Log.e("basePath", basePath);
//		Uri uri = Uri.fromFile(new File(basePath));
//		intent.setData(uri);
		Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, 
				Uri.parse("file://" + basePath));
		sendBroadcast(intent);
		return true;
	}
	
	private void imageDelete(ArrayList<String> images) {
		FileUtils.deleteImages(context, images);
	}

	private void refreshGridview() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 5000);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (photo_info) {
			initImageInfo(position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	@SuppressLint("SimpleDateFormat")
	private void initImageInfo(int position) {
		String imgPath = adapter.getItem(position);
		try {
			exif = new ExifInterface(imgPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String name = imgPath.substring(imgPath.lastIndexOf("/")+1);
		String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
		String length,width,make,model;
		
		if (date == null) {//picture
			File file = new File(imgPath);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.format(file.lastModified());
			length = ""+BitmapFactory.decodeFile(imgPath).getWidth();
			width = ""+BitmapFactory.decodeFile(imgPath).getHeight();
		}else{//photo
			length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
			width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
		}
		Bundle b = new Bundle();
		b.putString("name", name);
		b.putString("date", date);
		b.putString("length", length);
		b.putString("width", width);
		Message msg = new Message();
		msg.setData(b);
		msg.what = 2;
		mHandler.sendMessage(msg);
	}

	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			adapter.notifyDataSetChanged();
		}
		
	}
}
