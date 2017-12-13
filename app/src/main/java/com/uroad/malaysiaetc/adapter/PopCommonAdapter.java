package com.uroad.malaysiaetc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uroad.malaysiaetc.R;

import java.util.List;


public class PopCommonAdapter extends BaseAdapter {

	Context mContext;
	List<String> mylist;

	public PopCommonAdapter(Context mCon, List<String> datas) {
		mContext = mCon;
		mylist = datas;
	}

	@Override
	public int getCount() {
		return mylist.size();
	}

	@Override
	public Object getItem(int position) {
		return mylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.view_item_pop_common, null);
		TextView tvContent = (TextView) convertView
				.findViewById(R.id.tvContent);
		tvContent.setText(mylist.get(position));
		return convertView;
	}

}
