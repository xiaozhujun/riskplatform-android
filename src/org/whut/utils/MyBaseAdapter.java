package org.whut.utils;

import java.util.HashMap;
import java.util.List;

import org.whut.data.CollectionData;
import org.whut.platform.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {

	  private List<CollectionData> mlist;  
	    // 用来控制CheckBox的选中状况  
	    private static HashMap<Integer, Boolean> isSelected;  
	    // 上下文  
	    private Context mcontext;  
	    // 用来导入布局  
	    private LayoutInflater inflater = null; 
	    ViewHolder holder=null;
	   public static String[] address;
	    @SuppressLint("UseSparseArrays")
		public MyBaseAdapter(List<CollectionData> list,Context context)
	    {
	    	mcontext=context;
	    	mlist=list;
	    	inflater=LayoutInflater.from(context);
	    	isSelected=new HashMap<Integer, Boolean>();
	    	init();
	    }
	    public void refresh(List<CollectionData> list){
	    	mlist=list;
	    	notifyDataSetChanged();
	    }
	    public void init()
	    {
	    	 for (int i = 0; i < mlist.size(); i++) {  
	             getIsSelected().put(i, false);  
	         }  
	    }
	@Override
	public int getCount() {
		
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
         
         if (convertView==null) {
			holder=new ViewHolder();
		
			convertView=inflater.inflate(R.layout.collection, null);
			
			holder.unitAddress=(TextView)convertView.findViewById(R.id.tv_collection_unitAddress);
			holder.equipmentVariety=(TextView)convertView.findViewById(R.id.tv_collection_equipmentVariety);
			holder.riskValue=(TextView)convertView.findViewById(R.id.tv_collection_riskValue);
			holder.userPoint=(TextView)convertView.findViewById(R.id.tv_collection_userPoint);
			holder.checkBox=(CheckBox)convertView.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder)convertView.getTag();
		}
        
         holder.unitAddress.setText(mlist.get(position).getunitAddress());
         holder.equipmentVariety.setText(mlist.get(position).getequipmentVariety());
         holder.riskValue.setText(mlist.get(position).getriskValue());
         holder.userPoint.setText(mlist.get(position).getuserPoint());
         holder.checkBox.setChecked(getIsSelected().get(position));
		 //holder.checkBox.setVisibility(View.INVISIBLE);
		return convertView;
	}
	public static HashMap<Integer, Boolean> getIsSelected() {  
        return isSelected;  
    }  
  
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {  
        MyBaseAdapter.isSelected = isSelected;  
    }  
	public static class ViewHolder {
		public TextView id;
        public TextView unitAddress;
		public TextView equipmentVariety;
		public TextView riskValue;
		public TextView userPoint;  
        public CheckBox checkBox;  
    }  

}
