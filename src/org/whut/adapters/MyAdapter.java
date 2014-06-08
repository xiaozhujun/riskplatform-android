package org.whut.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.platform.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{

	private List<Map<String,String>> list;
	private static HashMap<Integer,Boolean> isSelected;
	private LayoutInflater inflater = null; 
	private int visibility;
	private int visibility_for_image[];
	
	public void setVisibility(int visibility){
		this.visibility = visibility;
	}

	@SuppressLint("UseSparseArrays")
	public MyAdapter(List<Map<String,String>> list,Context context){
		this.list = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();	
		visibility = View.INVISIBLE;
		visibility_for_image = new int[list.size()];
		for(int i=0;i<list.size();i++){
			if(Integer.parseInt(list.get(i).get("riskValue").split("：")[1])>7){
				visibility_for_image[i] = View.VISIBLE;
			}else{
				visibility_for_image[i] = View.GONE;
			}
		}
		initData();
	}

	private void initData(){
		for(int i=0;i<list.size();i++){
			getIsSelected().put(i,false);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.common_listview_collection, null);
			holder.equipmentVariety = (TextView) convertView.findViewById(R.id.content_name);
			holder.riskAccessment = (ImageView) convertView.findViewById(R.id.groupon_img);
			holder.ratingbar = (RatingBar) convertView.findViewById(R.id.content_rating);
			holder.riskValue = (TextView) convertView.findViewById(R.id.content_info_text);
			holder.usePoint = (TextView)convertView.findViewById(R.id.content_price_detail);
			holder.unitAddress = (TextView) convertView.findViewById(R.id.content_reason);
			holder.cb = (CheckBox) convertView.findViewById(R.id.distance);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();	
		}
		holder.equipmentVariety.setText(list.get(position).get("equipmentVariety"));
		
		switch(Integer.parseInt(list.get(position).get("riskValue").split("：")[1])){
		case 0:
			holder.ratingbar.setRating(0);
			break;
		case 1:
			holder.ratingbar.setRating((float) 0.5);
			break;
		case 2:
			holder.ratingbar.setRating(1);
			break;
		case 3:
			holder.ratingbar.setRating((float) 1.5);
			break;
		case 4:
			holder.ratingbar.setRating(2);
			break;
		case 5:	
			holder.ratingbar.setRating((float) 2.5);
			break;
		case 6:	
			holder.ratingbar.setRating(3);
			break;
		case 7:
			holder.ratingbar.setRating((float) 3.5);
			break;
		case 8:
			holder.ratingbar.setRating(4);
			break;
		case 9:
			holder.ratingbar.setRating((float) 4.5);
			break;
		case 10:
			holder.ratingbar.setRating(5);
			break;
		}
		
		final int pos = position;
	
		holder.riskAccessment.setVisibility(visibility_for_image[pos]);

		
		holder.cb.setVisibility(visibility);
		
		
		
		/*此处一定要注意，先设置监听，再设置checkbox的状态！不然ListView下滑时会
		*造成已选中的checkbox的状态丢失，上方图片设置同理！
		*
		*
		*/
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				getIsSelected().put(position, isChecked);
			}
		});
		
		holder.cb.setChecked(getIsSelected().get(position));
		
		holder.riskValue.setText(list.get(position).get("riskValue"));
		holder.usePoint.setText(list.get(position).get("usePoint"));
		holder.unitAddress.setText(list.get(position).get("unitAddress"));
		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {  
		return isSelected;  
	}
	
	public static void getIsSelected(HashMap<Integer,Boolean> isSelected){
		MyAdapter.isSelected = isSelected;
	}
	
	public static class ViewHolder {  
		TextView equipmentVariety; 
		ImageView riskAccessment;
		RatingBar ratingbar;
		TextView riskValue;
		TextView usePoint;
		CheckBox cb;  
		TextView unitAddress;
	} 

}
