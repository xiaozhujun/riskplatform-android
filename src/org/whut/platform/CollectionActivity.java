package org.whut.platform;

import java.util.ArrayList;
import java.util.List;
import org.whut.data.CollectionData;
import org.whut.data.dataDao;
import org.whut.utils.MyBaseAdapter;
import org.whut.utils.MyBaseAdapter.ViewHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CollectionActivity extends Activity {

	private List<CollectionData> list;
	private ListView listView;
	private Button delete, cancelAll, choose;
	private String[] address;
	private TextView edit;
	private LinearLayout layout;
	private MyBaseAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		listView = (ListView) findViewById(R.id.lv_collection);
		delete = (Button) findViewById(R.id.btnDelAll);
		cancelAll = (Button) findViewById(R.id.btnCancelAll);
		choose = (Button) findViewById(R.id.btnSelAll);
		edit = (TextView) findViewById(R.id.btn_fav_edit);
		layout = (LinearLayout) findViewById(R.id.fav_bat_del);
		
	  list=new ArrayList<CollectionData>();

        initData();
	    address=new String[list.size()];
	
		adapter=new MyBaseAdapter(list, CollectionActivity.this);
		adapter.refresh(list);
		listView.setAdapter(adapter);
		
		if (edit.getText().toString().equals("编辑")) {
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ListView lv = (ListView) parent;
					 int i=(Integer) lv.getItemAtPosition(position);
						Intent intent = new Intent(CollectionActivity.this,InfoActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("unitAddress",list.get(i).getunitAddress() );
						intent.putExtras(bundle);
						startActivity(intent);
					
				}
			});
		}
			 edit.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (edit.getText().toString().equals("编辑")) {
							edit.setText("完成");
							layout.setVisibility(View.VISIBLE);
							for (int i = 0; i < listView.getChildCount(); i++) {
								View view=listView.getChildAt(i);
								ViewHolder holder=(ViewHolder) view.getTag();
								holder.checkBox.setVisibility(View.VISIBLE);
							}
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
								
									 ViewHolder holder=(ViewHolder)view.getTag(); 
									  holder.checkBox.toggle();
									  //MyBaseAdapter.getIsSelected().put(position, holder.checkBox.isSelected());
									 
									  if (holder.checkBox.isChecked()){
										  
											address[position]=holder.unitAddress.getText().toString();
											
									  }else {
										address[position]=" ";
									}
								}
							});
						}else {
							edit.setText("编辑");
							layout.setVisibility(View.INVISIBLE);
							for (int i = 0; i < listView.getChildCount(); i++) {
								View view=listView.getChildAt(i);
								ViewHolder holder=(ViewHolder) view.getTag();
								holder.checkBox.setVisibility(View.INVISIBLE);
							}
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									
									
									ListView lv = (ListView) parent;
									 int i=(Integer) lv.getItemAtPosition(position);
										Intent intent = new Intent(CollectionActivity.this,InfoActivity.class);
										Bundle bundle=new Bundle();
										bundle.putString("unitAddress",list.get(i).getunitAddress() );
										intent.putExtras(bundle);
										startActivity(intent);
								}
							});
						}
						
					}
				});
		

		cancelAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					if (MyBaseAdapter.getIsSelected().get(i)) {
						
						MyBaseAdapter.getIsSelected().put(i, false);
						
					}	
				}
				adapter.notifyDataSetChanged();

			}
		});
		choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					MyBaseAdapter.getIsSelected().put(i, true);
					int position=(int) listView.getItemIdAtPosition(i);
					address[i]=list.get(position).getunitAddress();
					
				}
				adapter.notifyDataSetChanged();

			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dataDao dd = new dataDao(CollectionActivity.this);				
				 dd.Delete(address);	
				initData();
				//MyBaseAdapter adapter=new MyBaseAdapter(list,CollectionActivity.this);
				adapter.refresh(list);
			
				listView.setAdapter(adapter);
				edit.setText("编辑");
				layout.setVisibility(View.INVISIBLE);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ListView lv = (ListView) parent;
						 int i=(Integer) lv.getItemAtPosition(position);
							Intent intent = new Intent(CollectionActivity.this,InfoActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("unitAddress",list.get(i).getunitAddress() );
							intent.putExtras(bundle);
							startActivity(intent);
						
					}
				});
			}
		});

	}
	private void initData()
	{
		dataDao dd = new dataDao(CollectionActivity.this);
		 list = dd.get();
	}
	
		
	}

