package com.example.postgpslocation.adapter;

import java.util.List;
import java.util.Map;

import com.example.postgpslocation.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author fan
 * @describe 定位界面的listview自定义适配器
 */
public class ViewTextAdapter extends BaseAdapter{
	    LayoutInflater mInflater;  
	    List<Map<String, Object>> list;  
	  
	    public ViewTextAdapter(Context context, List<Map<String, Object>> list) {  
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
	  
	        return list.size();  
	    }  
	  
	    @Override  
	    public Object getItem(int position) {  
	  
	        return null;  
	    }  
	  
	    @Override  
	    public long getItemId(int position) {  
	  
	        return 0;  
	    }  
	  
	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	        ViewHolder viewHolder = null;  
	        if (convertView == null) {  
	            viewHolder = new ViewHolder();  
	            convertView = mInflater.inflate(R.layout.locationlistview, null);  
	            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);  
	            viewHolder.title = (TextView) convertView.findViewById(R.id.title);  
	            viewHolder.info = (TextView) convertView.findViewById(R.id.info);  
	            convertView.setTag(viewHolder);  
	        } else {  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }  
	        viewHolder.img.setBackgroundResource(Integer.parseInt(list.get(position).get("img").toString()));
	        viewHolder.title.setText(list.get(position).get("title").toString());
	        viewHolder.info.setText(list.get(position).get("info").toString());  
	        if(5==position){
	        	  viewHolder.info.setTextColor(Color.parseColor("#FF0000"));
	        }else{
	        	 viewHolder.info.setTextColor(Color.parseColor("#000000"));
	        }
//	        if(list.get(position).get("title").toString().equals(other))
	        return convertView;  
	    }  
	  

}
