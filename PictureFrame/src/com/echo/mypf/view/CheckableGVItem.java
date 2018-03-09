package com.echo.mypf.view;

import com.echo.mypf.R;
import com.echo.mypf.utils.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CheckableGVItem extends RelativeLayout implements Checkable{

	private ImageView ivBackground,ivSelected;
	private boolean mChecked;

	public CheckableGVItem(Context context) {
		this(context,null);
	}

	public CheckableGVItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CheckableGVItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.gridview_item_edit, this);
		ivBackground = (ImageView) findViewById(R.id.iv_gridview_item_edit);
		ivSelected = (ImageView) findViewById(R.id.iv_selected);
	}

	public void setImage(String string) {
		FileUtils.ImgLoader("File:/" + string, ivBackground);
	}

	@SuppressLint("NewApi")
	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
		ivSelected.setVisibility(checked ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

}
