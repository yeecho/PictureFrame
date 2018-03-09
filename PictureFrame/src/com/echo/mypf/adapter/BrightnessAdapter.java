package com.echo.mypf.adapter;

import com.echo.mypf.R;
import com.echo.mypf.custom.Echo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BrightnessAdapter extends BaseAdapter{

	private String tag = "BrightnessAdapter_log";
	private Context context;

	public BrightnessAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public View getItem(int position) {
		
		return getView(position, null, null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(R.layout.window_brightness_item, null);
		}else{
			view = convertView;
		}
		if (position == 7) {
			view.requestFocus();
		}
		return view;
	}

}
