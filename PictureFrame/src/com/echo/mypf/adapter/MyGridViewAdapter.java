package com.echo.mypf.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.echo.mypf.R;
import com.echo.mypf.utils.FileUtils;

public class MyGridViewAdapter extends BaseAdapter {
	
	private String tag = "MyGridViewAdapter";
	private Context mContext;
	private String pathType;
	private ArrayList<String> imgPathList = new ArrayList<String>();
//	private ViewHolder viewHolder;

	public MyGridViewAdapter(Context mContext, String pathType) {
		this.mContext = mContext;
		this.pathType = pathType;
		imgPathList = FileUtils.getImageFilePath(mContext, pathType);
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
//		viewHolder = null;
		RelativeLayout rl = null;
		ImageView iv = null;
		if (convertView != null) {
//			viewHolder = (ViewHolder) convertView.getTag();
			rl = (RelativeLayout) convertView;
			iv = (ImageView) rl.findViewById(R.id.grid_item_view);
		} else {
			rl = (RelativeLayout) LayoutInflater.from(mContext).inflate((R.layout.gridview_item), null);
			iv = (ImageView) rl.findViewById(R.id.grid_item_view);
//			viewHolder = new ViewHolder();
//			viewHolder.image = (ImageView) convertView.findViewById(R.id.grid_item_view);
//			convertView.setTag(viewHolder);
		}
		FileUtils.ImgLoader("File:/" + imgPathList.get(position), iv);
		rl.setTag(position);
//		FileUtils.ImgLoader("File:/" + imgPathList.get(position), viewHolder.image);
		return rl;
	}
	
//	class ViewHolder{
//
//		public ImageView image;
//		
//	}


}
