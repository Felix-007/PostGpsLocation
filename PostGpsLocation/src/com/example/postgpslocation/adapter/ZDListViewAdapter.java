package com.example.postgpslocation.adapter;

import java.util.List;
import java.util.Map;

import com.example.postgpslocation.R;
import com.example.postgpslocation.adapter.ViewTextAdapter.ViewHolder;
import com.example.postgpslocation.bean.CustomKeyPointInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZDListViewAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	List<CustomKeyPointInfo> list;

	public ZDListViewAdapter(Context context, List<CustomKeyPointInfo> list) {
		super();
		this.mInflater = LayoutInflater.from(context);
		this.list = list;

	}
	
	  // 内部类优化作用  
    public final class ViewHolder {  
        public ImageView img;  
        public TextView title;  
        public TextView info;  
  
    }  

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();  
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewHolder = null;  
		 if (convertView == null) {  
	            viewHolder = new ViewHolder();  
	            convertView = mInflater.inflate(R.layout.zdlistview, null);  
	            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);  
	            viewHolder.title = (TextView) convertView.findViewById(R.id.title);  
	            viewHolder.info = (TextView) convertView.findViewById(R.id.info);  
	            convertView.setTag(viewHolder);  
	        } else {  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }  
//	        viewHolder.img.setBackgroundResource(Integer.parseInt(list.get(position).getXh().toString()));
	        viewHolder.title.setText(list.get(position).getZdmc().toString());
//	        viewHolder.info.setText(list.get(position).getJd()+"E"+list.get(position).getWd()+"N");  
	        return convertView;  
	}

}
