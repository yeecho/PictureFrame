package com.echo.mypf;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.utils.FileUtils;
import com.echo.mypf.utils.LanguageUtils;
import com.echo.mypf.view.GridViewActivity;
import com.echo.mypf.view.PhotoActivity;

public class MainActivity extends CustomActivity implements OnClickListener{
	
	private String tag = "MainActivity";
	private SharedPreferences sp;
	private Editor editor;
	private String pathType;
	private int position;
	private String language;
	private int focusPosition;
	private RelativeLayout rl;
	private TextView tvSd;
	private TextView tvUsb;
	private TextView tvMemory;
	private TextView tvSettings;
	private TextView tvCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
	}

	@Override
	protected void onStop() {
		saveFocus();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		saveFocus();
		super.onDestroy();
	}

	private void saveFocus() {
		if (rl.findFocus()==null) {
			return;
		}
		int pos = 0;
		switch (rl.findFocus().getId()) {
		case R.id.sd_tv_main:
			pos = 0;
			break;
		case R.id.usb_tv_main:
			pos = 1;
			break;
		case R.id.memory_tv_main:
			pos = 2;
			break;
		case R.id.settings_tv_main:
			pos = 3;
			break;
		case R.id.calendar_tv_main:
			pos = 4;
			break;
		default:
			pos = 0;
			break;
		}
		editor.putInt(Echo.MAIN_FOCUS_POSITION, pos);
		editor.commit();
	}

	private void initData() {
		FileUtils.getImageFilePath(this, Echo.PATH_FLASH);
		sp = getSharedPreferences(Echo.SHARED_PREFERENCES_NAME, 0);
		language = sp.getString(Echo.LANGUAGE_SP, "en");
		LanguageUtils.setLanguage(getApplicationContext(), language);
		editor = sp.edit();
		focusPosition = sp.getInt(Echo.MAIN_FOCUS_POSITION, 0);
		setFocus(focusPosition);
	}

	private void setFocus(int focusPosition) {
		switch (focusPosition) {
		case 0:
			tvSd.requestFocus();
			break;
		case 1:
			tvUsb.requestFocus();
			break;
		case 2:
			tvMemory.requestFocus();
			break;
		case 3:
			tvSettings.requestFocus();
			break;
		case 4:
			tvCalendar.requestFocus();
			break;

		default:
			break;
		}
	}

	private void initListener() {
		tvSd.setOnClickListener(this);
		tvUsb.setOnClickListener(this);
		tvMemory.setOnClickListener(this);
		tvSettings.setOnClickListener(this);
		tvCalendar.setOnClickListener(this);
	}

	private void initView() {
		setContentView(R.layout.activity_main);
		rl = (RelativeLayout) findViewById(R.id.main_rl);
		tvSd = (TextView) findViewById(R.id.sd_tv_main);
		tvUsb = (TextView) findViewById(R.id.usb_tv_main);
		tvMemory = (TextView) findViewById(R.id.memory_tv_main);
		tvSettings = (TextView) findViewById(R.id.settings_tv_main);
		tvCalendar = (TextView) findViewById(R.id.calendar_tv_main);
	}

	@Override
	public void onClick(View v) {
		Intent itGrid = new Intent();
		itGrid.setClass(MainActivity.this, GridViewActivity.class);
		pathType = sp.getString(Echo.PATH_TYPE_SP, Echo.PATH_FLASH);
		position = sp.getInt(Echo.POSITION_SP, 0);
		switch (v.getId()) {
		case R.id.sd_tv_main:
			itGrid.putExtra(Echo.PATH_TYPE, Echo.PATH_SD);
			startActivity(itGrid);
			break;
		case R.id.usb_tv_main:
			Log.e(tag, "usb");
			itGrid.putExtra(Echo.PATH_TYPE, Echo.PATH_USB);
			startActivity(itGrid);
			break;
		case R.id.memory_tv_main:
			Log.e(tag, "flash");
			itGrid.putExtra(Echo.PATH_TYPE, Echo.PATH_FLASH);
			startActivity(itGrid);
			break;
		case R.id.settings_tv_main:
			Intent intentSettings = new Intent(MainActivity.this, PhotoActivity.class);
			intentSettings.putExtra(Echo.PATH_TYPE, pathType);
			intentSettings.putExtra(Echo.POSITION, position);
			intentSettings.putExtra("SOC", 1);
			startActivity(intentSettings);
			break;
		case R.id.calendar_tv_main:
			Intent intentCalendar = new Intent(MainActivity.this, PhotoActivity.class);
			intentCalendar.putExtra(Echo.PATH_TYPE, pathType);
			intentCalendar.putExtra(Echo.POSITION, position);
			intentCalendar.putExtra("SOC", 2);
			startActivity(intentCalendar);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		pathType = sp.getString(Echo.PATH_TYPE_SP, Echo.PATH_FLASH);
		position = sp.getInt(Echo.POSITION_SP, 0);
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), PhotoActivity.class);
		intent.putExtra(Echo.PATH_TYPE, pathType);
		intent.putExtra(Echo.POSITION, position);
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
		case Echo.KEY_BACK:
//			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}


}
